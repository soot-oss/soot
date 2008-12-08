package soot.jimple.toolkits.thread.synchronization;

import java.util.*;

import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.jimple.internal.JNopStmt;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.thread.ThreadLocalObjectsAnalysis;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;


/**
 * @author Richard L. Halpert
 * Finds Synchronized Regions and creates a set of CriticalSection objects from them.
 */
public class SynchronizedRegionFinder extends ForwardFlowAnalysis<Unit, FlowSet>
{
    FlowSet emptySet = new ArraySparseSet();

    Map unitToGenerateSet;

    Body body;
    Chain units;
	SootMethod method;
	ExceptionalUnitGraph egraph;
	LocalUses slu;
	CriticalSectionAwareSideEffectAnalysis tasea;
//	SideEffectAnalysis sea;
	
	List<Object> prepUnits;

    CriticalSection methodTn;
	
	public boolean optionPrintDebug = false;
	public boolean optionOpenNesting = true;
	
    SynchronizedRegionFinder(UnitGraph graph, Body b, boolean optionPrintDebug, boolean optionOpenNesting, ThreadLocalObjectsAnalysis tlo)
	{
		super(graph);

		this.optionPrintDebug = optionPrintDebug;
		this.optionOpenNesting = optionOpenNesting;

		body = b;
		units = b.getUnits();
		method = body.getMethod();

		if(graph instanceof ExceptionalUnitGraph)
			egraph = (ExceptionalUnitGraph) graph;
		else
			egraph = new ExceptionalUnitGraph(b);
				
		slu = new SimpleLocalUses(egraph, new SmartLocalDefs(egraph, new SimpleLiveLocals(egraph)));
		
		if( G.v().Union_factory == null ) {
		    G.v().Union_factory = new UnionFactory() {
			public Union newUnion() { return FullObjectSet.v(); }
		    };
		}
		
    	tasea = new CriticalSectionAwareSideEffectAnalysis(Scene.v().getPointsToAnalysis(), 
    				Scene.v().getCallGraph(), null, tlo);
    	    				
    	prepUnits = new ArrayList<Object>();
    	
		methodTn = null;
		if(method.isSynchronized())
		{
			// Entire method is transactional
			methodTn = new CriticalSection(true, method, 1);
			methodTn.beginning = ((JimpleBody) body).getFirstNonIdentityStmt();
		}
        doAnalysis();
		if(method.isSynchronized() && methodTn != null)
		{
			for(Iterator tailIt = graph.getTails().iterator(); tailIt.hasNext(); )
			{
				Stmt tail = (Stmt) tailIt.next();
				methodTn.earlyEnds.add(new Pair(tail, null)); // has no exitmonitor stmt yet
			}
		}
	}
    	
    /**
     * All INs are initialized to the empty set.
     **/
    protected FlowSet newInitialFlow()
    {
		FlowSet ret = emptySet.clone();
		if(method.isSynchronized() && methodTn != null)
		{
			ret.add(new SynchronizedRegionFlowPair(methodTn, true));
		}
        return ret;
    }

    /**
     * IN(Start) is the empty set
     **/
    protected FlowSet entryInitialFlow()
    {
		FlowSet ret = emptySet.clone();
		if(method.isSynchronized() && methodTn != null)
		{
			ret.add(new SynchronizedRegionFlowPair(methodTn, true));
		}
        return ret;
    }

    /**
     * OUT is the same as (IN minus killSet) plus the genSet.
     **/
    protected void flowThrough(FlowSet in, Unit unit, FlowSet out)
    {
		Stmt stmt = (Stmt) unit;

       	copy(in, out);
       	
        // Determine if this statement is a preparatory statement for an
        // upcoming transactional region. Such a statement would be a definition 
        // which contains no invoke statement, and which corresponds only to 
        // EnterMonitorStmt and ExitMonitorStmt uses.  In this case, the read
        // set of this statement should not be considered part of the read set
        // of any containing transaction
        if(unit instanceof AssignStmt)
        {
	        boolean isPrep = true;
        	Iterator uses = slu.getUsesOf((Unit) unit).iterator();
        	if(!uses.hasNext())
        		isPrep = false;
        	while(uses.hasNext())
        	{
        		UnitValueBoxPair use = (UnitValueBoxPair) uses.next();
        		Unit useStmt = use.getUnit();
        		if( !(useStmt instanceof EnterMonitorStmt) && !(useStmt instanceof ExitMonitorStmt) )
        		{
        			isPrep = false;
        			break;
        		}
        	}
        	if(isPrep)
        	{
        		prepUnits.add(unit);
        		if(optionPrintDebug)
        		{
        			G.v().out.println("prep: " + unit.toString());
        		}
        		return;
        	}
        }
                
        // Determine if this statement is the start of a transaction
        boolean addSelf = (unit instanceof EnterMonitorStmt);
        
		// Determine the level of transaction nesting of this statement
		int nestLevel = 0;
        Iterator outIt0 = out.iterator();
        while(outIt0.hasNext())
        {
            SynchronizedRegionFlowPair srfp = (SynchronizedRegionFlowPair) outIt0.next();
            if(srfp.tn.nestLevel > nestLevel && srfp.inside == true)
            	nestLevel = srfp.tn.nestLevel;
        }

		// Process this unit's effect on each txn
		RWSet stmtRead = null;
		RWSet stmtWrite = null;
        Iterator outIt = out.iterator();
        boolean printed = false;
        while(outIt.hasNext())
        {
            SynchronizedRegionFlowPair srfp = (SynchronizedRegionFlowPair) outIt.next();
            CriticalSection tn = srfp.tn;
            
            // Check if we are revisting the start of this existing transaction
            if(tn.entermonitor == stmt)
            {
            	srfp.inside = true;
            	addSelf = false; // this transaction already exists...
            }
            
            // if this is the immediately enclosing transaction
        	if(srfp.inside == true && (tn.nestLevel == nestLevel || optionOpenNesting == false))
        	{
        		printed = true; // for debugging purposes, indicated that we'll print a debug output for this statement
        		
            	// Add this unit to the current transactional region
            	if(!tn.units.contains(unit))
	            	tn.units.add(unit);
        		
        		// Check what kind of statement this is
        		// If it contains an invoke, save it for later processing as part of this transaction
        		// If it is a monitorexit, mark that it's the end of the transaction
        		// Otherwise, add it's read/write sets to the transaction's read/write sets
            	if(stmt.containsInvokeExpr())
            	{
            		// Note if this unit is a call to wait() or notify()/notifyAll()
            		String InvokeSig = stmt.getInvokeExpr().getMethod().getSubSignature();
            		if((InvokeSig.equals("void notify()") || InvokeSig.equals("void notifyAll()")) && tn.nestLevel == nestLevel) // only applies to outermost txn
            		{
				        if(!tn.notifys.contains(unit))
		            		tn.notifys.add(unit);
	            		if(optionPrintDebug)
	            			G.v().out.print("{x,x} ");
	            	}
	            	else if((InvokeSig.equals("void wait()") || InvokeSig.equals("void wait(long)") || InvokeSig.equals("void wait(long,int)")) && tn.nestLevel == nestLevel) // only applies to outermost txn
            		{
				        if(!tn.waits.contains(unit))
		            		tn.waits.add(unit);
	            		if(optionPrintDebug)
	            			G.v().out.print("{x,x} ");
	            	}
	            	
	            	if(!tn.invokes.contains(unit))
	            	{
	            		// Mark this unit for later read/write set calculation (must be deferred until all tns have been found)
		            	tn.invokes.add(unit);
		            	
		            	// Debug Output
	            		if(optionPrintDebug)
	            		{
							stmtRead = tasea.readSet(tn.method, stmt, tn, new HashSet());
							stmtWrite = tasea.writeSet(tn.method, stmt, tn, new HashSet());

			           		G.v().out.print("{");
				           	if(stmtRead != null)
				           	{
					           	G.v().out.print( ( (stmtRead.getGlobals()  != null ? stmtRead.getGlobals().size()  : 0)   + 
					           					   (stmtRead.getFields()   != null ? stmtRead.getFields().size()   : 0) ) );
					        }
					        else
					        	G.v().out.print( "0" );
					        G.v().out.print(",");
					        if(stmtWrite != null)
					        {
					           	G.v().out.print( ( (stmtWrite.getGlobals() != null ? stmtWrite.getGlobals().size() : 0)   + 
				           						   (stmtWrite.getFields()  != null ? stmtWrite.getFields().size()  : 0) ) );
				        	}
				        	else
				        		G.v().out.print( "0" );
				        	G.v().out.print("} ");
						}
		            }
            	}
            	else if(unit instanceof ExitMonitorStmt && tn.nestLevel == nestLevel) // only applies to outermost txn
            	{
            		// Mark this as end of this tn
            		srfp.inside = false;
            		
            		// Check if this is an early end or fallthrough end
            		Stmt nextUnit = stmt;
            		do
            		{
            			nextUnit = (Stmt) units.getSuccOf(nextUnit);
            		} while (nextUnit instanceof JNopStmt);
            		if( nextUnit instanceof ReturnStmt ||
            			nextUnit instanceof ReturnVoidStmt ||
            			nextUnit instanceof ExitMonitorStmt )
            		{
            			tn.earlyEnds.add(new Pair(nextUnit, stmt)); // <early end stmt, exitmonitor stmt>
            		}
            		else if( nextUnit instanceof GotoStmt )
            		{
            			tn.end = new Pair(nextUnit, stmt); // <end stmt, exitmonitor stmt>
            			tn.after = (Stmt) ((GotoStmt) nextUnit).getTarget();
            		}
            		else if( nextUnit instanceof ThrowStmt )
            		{
            			tn.exceptionalEnd = new Pair(nextUnit, stmt);
            		}
            		else
            			throw new RuntimeException("Unknown bytecode pattern: exitmonitor not followed by return, exitmonitor, goto, or throw");

	            	if(optionPrintDebug)
            			G.v().out.print("[0,0] ");
            	}
				else
            	{
            		// Add this unit's read and write sets to this transactional region
            		HashSet uses = new HashSet();
	               	stmtRead = tasea.readSet( method, stmt, tn, uses );
		           	stmtWrite = tasea.writeSet( method, stmt, tn, uses );

    		   		tn.read.union(stmtRead);
        			tn.write.union(stmtWrite);
        					           	
		           	// Debug Output
            		if(optionPrintDebug)
			        {
			           	G.v().out.print("[");
			           	if(stmtRead != null)
			           	{
				           	G.v().out.print( ( (stmtRead.getGlobals()  != null ? stmtRead.getGlobals().size()  : 0)   + 
				           					   (stmtRead.getFields()   != null ? stmtRead.getFields().size()   : 0) ) );
				        }
				        else
				        	G.v().out.print( "0" );
				        G.v().out.print(",");
				        if(stmtWrite != null)
				        {
				           	G.v().out.print( ( (stmtWrite.getGlobals() != null ? stmtWrite.getGlobals().size() : 0)   + 
			           						   (stmtWrite.getFields()  != null ? stmtWrite.getFields().size()  : 0) ) );
			        	}
			        	else
			        		G.v().out.print( "0" );
			        	G.v().out.print("] ");
					}
        		}
			}
        }
        
		// DEBUG output
	    if(optionPrintDebug)
		{
			if(!printed)
			{
	        	G.v().out.print("[0,0] ");
			}
			G.v().out.println(unit.toString());
			
			// If this unit is an invoke statement calling a library function and the R/W sets are huge, print out the targets
			if(stmt.containsInvokeExpr() && 
				stmt.getInvokeExpr().getMethod().getDeclaringClass().toString().startsWith("java.") &&
				stmtRead != null && stmtWrite != null)
				{
					if(stmtRead.size() < 25 && stmtWrite.size() < 25)
					{
						G.v().out.println("        Read/Write Set for LibInvoke:");
						G.v().out.println("Read Set:(" + stmtRead.size() + ")" + stmtRead.toString().replaceAll("\n", "\n        "));
						G.v().out.println("Write Set:(" + stmtWrite.size() + ")" + stmtWrite.toString().replaceAll("\n", "\n        "));
					}
				}
		}
		
		// If this statement was a monitorenter, and no transaction object yet exists for it,
		// create one.
        if(addSelf)
        {
        	CriticalSection newTn = new CriticalSection(false, method, nestLevel + 1);
			newTn.entermonitor = stmt;
			newTn.beginning = (Stmt) units.getSuccOf(stmt);
			if(stmt instanceof EnterMonitorStmt)
				newTn.origLock = ((EnterMonitorStmt) stmt).getOp();
				
        	if(optionPrintDebug)
        		G.v().out.println("Transaction found in method: " + newTn.method.toString());
			out.add(new SynchronizedRegionFlowPair(newTn, true));
			
			// This is a really stupid way to find out which prep applies to this txn.
			Iterator<Object> prepUnitsIt = prepUnits.iterator();
			while(prepUnitsIt.hasNext())
			{
				Unit prepUnit = (Unit) prepUnitsIt.next();
				
				Iterator uses = slu.getUsesOf(prepUnit).iterator();
	        	while(uses.hasNext())
	        	{
	        		UnitValueBoxPair use = (UnitValueBoxPair) uses.next();
	        		if(use.getUnit() == (Unit) unit)
	        		{// if this transaction's monitorenter statement is one of the uses of this preparatory unit
	        			newTn.prepStmt = (Stmt) prepUnit;
	        		}
	        	}

			}
		}
    }

    /**
     * union
     **/
    protected void merge(FlowSet inSet1, FlowSet inSet2, FlowSet outSet)
    {
		inSet1.union(inSet2, outSet);
    }

    protected void copy(FlowSet sourceSet, FlowSet destSet)
    {
		destSet.clear();

		Iterator it = sourceSet.iterator();
		while(it.hasNext())
		{
			SynchronizedRegionFlowPair tfp = (SynchronizedRegionFlowPair) it.next();
			destSet.add(tfp.clone());
		}
    }
}
