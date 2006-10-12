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
    Transaction methodTn;
	TransactionAwareSideEffectAnalysis tasea;
	ExceptionalUnitGraph egraph;
	public boolean optionPrintDebug = true;

    TransactionAnalysis(UnitGraph graph, Body b)
	{
		super(graph);
		
		if(graph instanceof ExceptionalUnitGraph)
			egraph = (ExceptionalUnitGraph) graph;
		else
			egraph = null;
		
		body = b;
		method = body.getMethod();
		if( G.v().Union_factory == null ) {
		    G.v().Union_factory = new UnionFactory() {
			public Union newUnion() { return FullObjectSet.v(); }
		    };
		}
    	tasea = new TransactionAwareSideEffectAnalysis(Scene.v().getPointsToAnalysis(), 
    				Scene.v().getCallGraph(), null);
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

        // If this instruction is a monitorexit, then 
        //     add a (null,emptylist) to the flowset
        // If there is a (null,anylist) in the flowset, then 
        //     add reads & writes to anylist
        // If there is a (null,anylist) in the flowset and this instruction is 
        //     a monitorenter, change (null, anylist) to (unit, anylist)
        
        boolean addSelf = (unit instanceof EnterMonitorStmt);
        
        List epreds = null;
    	if(egraph != null)
    	{
			epreds = egraph.getExceptionalPredsOf(unit);
    	}

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
            if(tn.begin == (Stmt) unit)
            {
            	tfp.inside = true;
            	addSelf = false;
            }
/*
            if(tfp.inside == true && tn.nestLevel == nestLevel + 1) // catch unreachable monitorexits
            {
            	if(epreds != null && epreds.contains(tn.begin))
            	{
            		boolean hasAllEnds = true;
            		for(int i = 0; i < tn.ends.size(); i++)
            		{
            			if(!epreds.contains(tn.ends.get(i)))
            				hasAllEnds = false;
            		}
            		List usuccs = egraph.getUnexceptionalSuccsOf(unit);
            		if(usuccs.size() > 0 && hasAllEnds)
  					{
  						Unit Monitorexit = null;
// 						G.v().out.print(" ***");
  						for(int i = 0; i < epreds.size(); i++)
  						{
// 							G.v().out.print(" | " + epreds.get(i).toString());
  							if(epreds.get(i) instanceof ExitMonitorStmt)
  								Monitorexit = (Unit) epreds.get(i);
  						}
// 						G.v().out.print(" ***\n");
  						if(Monitorexit != null)
  						{
            				// Mark this as end of this tn
            				if(!tn.ends.contains(Monitorexit))
        		    			tn.ends.add(Monitorexit);
		            		tfp.inside = false;
            			}
  					}	
            	}
            }
*/
        	if(tfp.inside == true && tn.nestLevel == nestLevel)
        	{
        		printed = true;
            	// Add this unit to the current transactional region
            	if(!tn.units.contains(unit))
	            	tn.units.add(unit);
        		
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
//	            	else if(InvokeSig.equals("java.lang.Class class$(java.lang.String)"))
//	            	{
//	            		if(optionPrintDebug)
//	            			G.v().out.print("{x,x} ");
//	            	}
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

							UnitGraph g = new ExceptionalUnitGraph(body);
							LocalDefs sld = new SmartLocalDefs(g, new SimpleLiveLocals(g));


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
			    							tn.read.union(stmtRead);
			    						}
			    					}
			    				}
			    				else
			    				{
			    					G.v().out.println("RG-,");
				    				stmtRead = tasea.approximatedReadSet(tn.method, stmt, null);
			    					tn.read.union(stmtRead);
			    				}
			    			}
			    			else if( (tasea.sigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSignature())) ||
								     (tasea.subSigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSubSignature())) )
							{
			    				G.v().out.println("RB-,");
			    				stmtRead = tasea.approximatedReadSet(tn.method, stmt, null);
								tn.read.union(stmtRead);
							}
							else
							{
			    				G.v().out.println("R--,");
			            		stmtRead = tasea.readSet( tn.method, stmt );
				        		tn.read.union(stmtRead);
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
			    							tn.write.union(stmtWrite);
			    						}
			    					}
			    				}
			    				else
			    				{
			    					G.v().out.println("WG-,");
				    				stmtWrite = tasea.approximatedWriteSet(tn.method, stmt, null);
			    					tn.write.union(stmtWrite);
			    				}
			    			}
			    			else if( tasea.sigReadGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
			    			{
			    				G.v().out.println("WB-,");
			    				stmtWrite = tasea.approximatedWriteSet(tn.method, stmt, null);
								tn.write.union(stmtWrite);
			    			}
			    			// add else ifs for every special case (specifically functions that write to args)
			    			else if( (tasea.sigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSignature())) ||
									 (tasea.subSigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSubSignature())) )
							{
			    				G.v().out.println("WB-,");
			    				stmtWrite = tasea.approximatedWriteSet(tn.method, stmt, null);
								tn.write.union(stmtWrite);
							}
							else
							{
			   					G.v().out.println("W--,");
				            	stmtWrite = tasea.writeSet( tn.method, stmt );
				        		tn.write.union(stmtWrite);
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
            		// Add this unit's read and write sets to the "current" transactional region
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

        if(addSelf)
			out.add(new TransactionFlowPair(new Transaction((Stmt) unit, false, method, nestLevel + 1), true));
            	
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
