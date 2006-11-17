package soot.jimple.toolkits.transaction;
// PTFindTransactions - Analysis to locate transactional regions

import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;

public class TransactionAnalysis extends ForwardFlowAnalysis
{
    FlowSet emptySet = new ArraySparseSet();

    Map unitToGenerateSet;

    Body body;
	SootMethod method;
	ExceptionalUnitGraph egraph;
	LocalDefs sld;
	LocalUses slu;
	TransactionAwareSideEffectAnalysis tasea;
	
	List prepUnits;

    Transaction methodTn;
	
	public boolean optionPrintDebug = false;

    TransactionAnalysis(UnitGraph graph, Body b)
	{
		super(graph);

		body = b;
		method = body.getMethod();

		if(graph instanceof ExceptionalUnitGraph)
			egraph = (ExceptionalUnitGraph) graph;
		else
			egraph = new ExceptionalUnitGraph(b);
				
		sld = new SmartLocalDefs(egraph, new SimpleLiveLocals(egraph));
		slu = new SimpleLocalUses(egraph, sld);
		
		if( G.v().Union_factory == null ) {
		    G.v().Union_factory = new UnionFactory() {
			public Union newUnion() { return FullObjectSet.v(); }
		    };
		}
		
    	tasea = new TransactionAwareSideEffectAnalysis(Scene.v().getPointsToAnalysis(), 
    				Scene.v().getCallGraph(), null);
    				
    	prepUnits = new ArrayList();
    	
		methodTn = null;
//		if(method.isSynchronized())
//		{
			// Entire method is transactional
//			methodTn = new Transaction((Stmt) null, true, body.getMethod(), 0);
//		}
        doAnalysis();
//		if(method.isSynchronized() && methodTn != null)
//		{
			// TODO: Check if totally safe
//			methodTn.begin = (Stmt) body.getUnits().iterator().next();
//		}
	}
    	
    /**
     * All INs are initialized to the empty set.
     **/
    protected Object newInitialFlow()
    {
        return emptySet.clone();
    }

    /**
     * IN(Start) is the empty set
     **/
    protected Object entryInitialFlow()
    {
		FlowSet ret = (FlowSet) emptySet.clone();
//		if(method.isSynchronized() && methodTn != null)
//			ret.add(methodTn);
        return ret;
    }

    /**
     * OUT is the same as (IN minus killSet) plus the genSet.
     **/
    protected void flowThrough(Object inValue, Object unit, Object outValue)
    {
        FlowSet
            in = (FlowSet) inValue,
            out = (FlowSet) outValue;

       	copy(in, out);
       	
		// If the flowset has a transaction in it with wholeMethod=true and
		// ends is empty then the current instruction is the last instruction on
		// some path of execution in this method, so ends should be set to unit
/*		if(method.isSynchronized() && in.size() == 1)
		{
			Transaction tn = (Transaction) in.iterator().next();
			if(tn.ends.isEmpty())
				tn.ends.add(unit);
		}
*/

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
            TransactionFlowPair tfp = (TransactionFlowPair) outIt0.next();
            if(tfp.tn.nestLevel > nestLevel && tfp.inside == true)
            	nestLevel = tfp.tn.nestLevel;
        }

		// Process this unit's effect on each txn
        Iterator outIt = out.iterator();
        boolean printed = false;
        while(outIt.hasNext())
        {
            TransactionFlowPair tfp = (TransactionFlowPair) outIt.next();
            Transaction tn = tfp.tn;
            
            // Check if we are revisting the start of this existing transaction
            if(tn.begin == (Stmt) unit)
            {
            	tfp.inside = true;
            	addSelf = false; // this transaction already exists...
            }
            
            // Check if the statement is within this transaction
        	if(tfp.inside == true && tn.nestLevel == nestLevel)
        	{
        		printed = true; // for debugging purposes, indicated that we'll print a debug output for this statement
        		
            	// Add this unit to the current transactional region
            	if(!tn.units.contains(unit))
	            	tn.units.add(unit);
        		
        		// Check what kind of statement this is
        		// If it contains an invoke, save it for later processing as part of this transaction
        		// If it is a monitorexit, mark that it's the end of the transaction
        		// Otherwise, add it's read/write sets to the transaction's read/write sets
            	if(((Stmt) unit).containsInvokeExpr())
            	{
            		// Note if this unit is a call to wait() or notify()/notifyAll()
            		String InvokeSig = ((Stmt)unit).getInvokeExpr().getMethod().getSubSignature();
//            		G.v().out.println(InvokeSig);
            		if(InvokeSig.equals("void notify()") || InvokeSig.equals("void notifyAll()"))
            		{
				        if(!tn.notifys.contains(unit))
		            		tn.notifys.add(unit);
	            		if(optionPrintDebug)
	            			G.v().out.print("{x,x} ");
	            	}
	            	else if(InvokeSig.equals("void wait()"))
            		{
				        if(!tn.waits.contains(unit))
		            		tn.waits.add(unit);
	            		if(optionPrintDebug)
	            			G.v().out.print("{x,x} ");
	            	}
	            	else if(!tn.invokes.contains(unit))
	            	{
	            		// Mark this unit for later read/write set consideration
		            	tn.invokes.add(unit);
		            	
		            	// Debug Output
	            		if(optionPrintDebug)
	            		{

							RWSet stmtRead = null;
							RWSet stmtWrite = null;
							Stmt stmt = (Stmt) unit;

			    			if( tasea.sigReadGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
			    			{
			    				if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
			    				{
			    					G.v().out.println("RGI,");
			                    	Iterator rDefsIt = sld.getDefsOfAt( (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase() , stmt ).iterator();
			                    	while (rDefsIt.hasNext())
			                    	{
			                        	Stmt next = (Stmt) rDefsIt.next();
			                        	G.v().out.println("DEF: " + next);
			                        	if(next instanceof DefinitionStmt)
										{
					    					stmtRead = tasea.approximatedReadSet(tn.method, stmt, ((DefinitionStmt) next).getRightOp() );
//			    							tn.read.union(stmtRead);
			    						}
			    					}
			    				}
			    				else
			    				{
			    					G.v().out.println("RG-,");
				    				stmtRead = tasea.approximatedReadSet(tn.method, stmt, null);
//			    					tn.read.union(stmtRead);
			    				}
			    			}
			    			else if( (tasea.sigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSignature())) ||
								     (tasea.subSigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSubSignature())) )
							{
			    				G.v().out.println("RB-,");
			    				stmtRead = tasea.approximatedReadSet(tn.method, stmt, null);
//								tn.read.union(stmtRead);
							}
							else
							{
			    				G.v().out.println("R--,");
			            		stmtRead = tasea.readSet( tn.method, stmt );
//				        		tn.read.union(stmtRead);
				    		}
    			
			    			if( tasea.sigWriteGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
			    			{
			    				if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
			    				{
			    					G.v().out.println("WGI,");
			                    	Iterator rDefsIt = sld.getDefsOfAt( (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase() , stmt).iterator();
			                    	while (rDefsIt.hasNext())
			                    	{
			                        	Stmt next = (Stmt) rDefsIt.next();
			                        	if(next instanceof DefinitionStmt)
										{
					    					stmtWrite = tasea.approximatedWriteSet(tn.method, stmt, ((DefinitionStmt) next).getRightOp() );
//			    							tn.write.union(stmtWrite);
			    						}
			    					}
			    				}
			    				else
			    				{
			    					G.v().out.println("WG-,");
				    				stmtWrite = tasea.approximatedWriteSet(tn.method, stmt, null);
//			    					tn.write.union(stmtWrite);
			    				}
			    			}
			    			else if( tasea.sigReadGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
			    			{
			    				G.v().out.println("WB-,");
			    				stmtWrite = tasea.approximatedWriteSet(tn.method, stmt, null);
//								tn.write.union(stmtWrite);
			    			}
			    			// add else ifs for every special case (specifically functions that write to args)
			    			else if( (tasea.sigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSignature())) ||
									 (tasea.subSigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSubSignature())) )
							{
			    				G.v().out.println("WB-,");
			    				stmtWrite = tasea.approximatedWriteSet(tn.method, stmt, null);
//								tn.write.union(stmtWrite);
							}
							else
							{
			   					G.v().out.println("W--,");
				            	stmtWrite = tasea.writeSet( tn.method, stmt );
//				        		tn.write.union(stmtWrite);
				    		}

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
            	else if(unit instanceof ExitMonitorStmt)
            	{
            		// Mark this as end of this tn
            		if(!tn.ends.contains(unit))
            			tn.ends.add(unit);
            		tfp.inside = false;
	            	if(optionPrintDebug)
            			G.v().out.print("[0,0] ");
            	}
				else
            	{
            		// Add this unit's read and write sets to this transactional region
	               	RWSet stmtRead = tasea.readSet( method, (Stmt) unit );
		           	RWSet stmtWrite = tasea.writeSet( method, (Stmt) unit );

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
				G.v().out.print("[-,-] ");
			G.v().out.println(unit.toString());
		}
		
		// If this statement was a monitorenter, and no transaction object yet exists for it,
		// create one.
        if(addSelf)
        {
        	Transaction newTn = new Transaction((Stmt) unit, false, method, nestLevel + 1);
        	if(optionPrintDebug)
        		G.v().out.println("Transaction found in method: " + newTn.method.toString());
			out.add(new TransactionFlowPair(newTn, true));
			Iterator prepUnitsIt = prepUnits.iterator();
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
            	
//		if(method.toString().equals("<Passenger: void run()>"))
//		Iterator outIt2 = out.iterator();
//		while(outIt2.hasNext())
//		{
//			TransactionFlowPair tfp = (TransactionFlowPair) outIt2.next();
//			G.v().out.print("<" + tfp + ">");
//		}

    }

    /**
     * union
     **/
    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet
            inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2,
            outSet = (FlowSet) out;

		inSet1.union(inSet2, outSet);
/*		
        Iterator inIt1 = inSet1.iterator();
        while(inIt1.hasNext())
        {
        	TransactionFlowPair tfp1 = (TransactionFlowPair) inIt1.next();
        	outSet.add(tfp1.clone());
        }
        
        Iterator inIt2 = inSet2.iterator();
        while(inIt2.hasNext())
        {
            TransactionFlowPair tfp2 = (TransactionFlowPair) inIt2.next();
            outSet.add(tfp2.clone());
        }
*/
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet
            sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;

//		sourceSet.copy(destSet);
		
		destSet.clear();

		Iterator it = sourceSet.iterator();
		while(it.hasNext())
		{
			TransactionFlowPair tfp = (TransactionFlowPair) it.next();
			destSet.add(tfp.clone());
		}
		
/*		G.v().out.println("Copied array is " + (sourceSet.equals(destSet) ? "equal:" : "inequal:") );
			it = sourceSet.iterator();
			Iterator it2 = destSet.iterator();
			while(it.hasNext() && it2.hasNext())
			{
				TransactionFlowPair tfp1 = (TransactionFlowPair) it.next();
				TransactionFlowPair tfp2 = (TransactionFlowPair) it2.next();
				G.v().out.print("<" + tfp1.toString() + "," + tfp2.toString() + ":" + (tfp1.equals(tfp2) ? "equal" : "inequal") + ">");
			}
			G.v().out.println("");
*/
    }
}
