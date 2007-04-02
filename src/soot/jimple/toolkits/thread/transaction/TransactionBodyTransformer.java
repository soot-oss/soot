package soot.jimple.toolkits.thread.transaction;

import java.util.*;
import soot.*;
import soot.util.Chain;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.toolkits.scalar.*;

public class TransactionBodyTransformer extends BodyTransformer
{
    private static TransactionBodyTransformer instance = new TransactionBodyTransformer();
    private TransactionBodyTransformer() {}

    public static TransactionBodyTransformer v() { return instance; }

    private FlowSet fs;
    private int maxLockObjs;
    private boolean[] useGlobalLock;
    
    public void setDetails(FlowSet fs, int maxLockObjs, boolean[] useGlobalLock)
    {
    	this.fs = fs;
    	this.maxLockObjs = maxLockObjs;
    	this.useGlobalLock = useGlobalLock;
    }
    
    public static boolean[] addedGlobalLockObj = null;
	private static int throwableNum = 0; // doesn't matter if not reinitialized to 0
    
    protected void internalTransform(Body b, String phase, Map opts)
	{
		// 
		JimpleBody j = (JimpleBody) b;
		SootMethod thisMethod = b.getMethod();
    	Chain units = b.getUnits();
		Iterator unitIt = units.iterator();
		Unit firstUnit = (Unit) j.getFirstNonIdentityStmt();
		Unit lastUnit = (Unit) units.getLast();
		
		// Objects of synchronization, plus book keeping
		Local[] lockObj = new Local[maxLockObjs]; 			
		boolean[] addedLocalLockObj = new boolean[maxLockObjs];
		SootField[] globalLockObj = new SootField[maxLockObjs];
		for(int i = 1; i < maxLockObjs; i++)
		{
			lockObj[i] = Jimple.v().newLocal("lockObj" + i, RefType.v("java.lang.Object"));
			addedLocalLockObj[i] = false;
			globalLockObj[i] = null;
		}
		
		// Make sure a main routine exists.  We will insert some code into it.
//        if (!Scene.v().getMainClass().declaresMethod("void main(java.lang.String[])"))
//            throw new RuntimeException("couldn't find main() in mainClass");
        
        // Add all global lock objects to the main class if not yet added.
        // Get references to them if they do already exist.
  		for(int i = 1; i < maxLockObjs; i++)
   		{
   			if( useGlobalLock[i - 1] )
   			{
	   			if( !addedGlobalLockObj[i] )
	            {
	            	// Add globalLockObj field if possible...
	            	
	       			// Avoid name collision... if it's already there, then just use it!
	            	try
	            	{
	            		globalLockObj[i] = Scene.v().getMainClass().getFieldByName("globalLockObj" + i);
	            		// field already exists
	            	}
	            	catch(RuntimeException re)
	            	{
	            		// field does not yet exist (or, as a pre-existing error, there is more than one field by this name)
		            	globalLockObj[i] = new SootField("globalLockObj" + i, RefType.v("java.lang.Object"), 
		                                      Modifier.STATIC | Modifier.PUBLIC);
		            	Scene.v().getMainClass().addField(globalLockObj[i]);
					}

	            	addedGlobalLockObj[i] = true;
	            }
	            else
	            {
	            	globalLockObj[i] = Scene.v().getMainClass().getFieldByName("globalLockObj" + i);
				}
			}
		}
   		
   		// If the current method is the main method, for each global lock object,
   		// add a local lock object and assign it a new object.  Copy the new 
   		// local lock object into the global lock object for use by other fns.
        if(thisMethod.getSubSignature().equals("void main(java.lang.String[])"))
        {
    		for(int i = 1; i < maxLockObjs; i++)
    		{
    			if( useGlobalLock[i - 1] )
    			{
					// add local lock obj
	    			addedLocalLockObj[i] = true;
					b.getLocals().add(lockObj[i]); // TODO: add name conflict avoidance code
		            
		            // assign new object to lock obj
					units.insertBefore(
						Jimple.v().newAssignStmt(
							lockObj[i],
							Jimple.v().newNewExpr(RefType.v("java.lang.Object"))),
						(Stmt) firstUnit);

					// initialize new object
		            SootClass objectClass = Scene.v().loadClassAndSupport("java.lang.Object");
		            RefType type = RefType.v(objectClass);
		            SootMethod initMethod = objectClass.getMethod("void <init>()");
		            units.insertBefore(
	            		Jimple.v().newInvokeStmt(
	           				Jimple.v().newSpecialInvokeExpr(
	       						lockObj[i],
	       						initMethod.makeRef(), 
	       						Collections.EMPTY_LIST)),
	                	(Stmt) firstUnit);
			        
			        // copy new object to global static lock object (for use by other fns)
		        	units.insertBefore(
	        			Jimple.v().newAssignStmt(
	       					Jimple.v().newStaticFieldRef(globalLockObj[i].makeRef()),
	       					lockObj[i]),
	       				(Stmt) firstUnit);
	       		}
    		}
        }
		
		int tempNum = 1;
		// Iterate through all of the transactions in the current method
		Iterator fsIt = fs.iterator();
		while(fsIt.hasNext())
		{
			Transaction tn = ((TransactionFlowPair) fsIt.next()).tn;
			if(tn.setNumber == -1)
				continue; // this tn should be deleted... for now just skip it!
			
/*			// Print information about this transaction for debugging
			G.v().out.print("Transaction " + tn.name + "  ");
			G.v().out.println("Location: " + b.getMethod().getDeclaringClass().toString() + ":" + b.getMethod().toString() + ":  ");
			G.v().out.println("Begin: " + tn.begin.toString() + "  ");
			G.v().out.print("End  : " + tn.ends.toString() + " \n");
			G.v().out.println("Size : " + tn.units.size());
			if(tn.read.size() < 100)
				G.v().out.print("Read :\n" + tn.read.toString());
			else
				G.v().out.println("Read : " + tn.read.size() + "  ");
//			G.v().out.println("R/Ws : " + (tn.read.size() + tn.write.size()) + "  ");
			if(tn.write.size() < 100)
				G.v().out.print("Write:\n" + tn.write.toString());
			else
				G.v().out.println("Write: " + tn.write.size() + "  ");
			Iterator tnedgeit = tn.edges.iterator();
		    G.v().out.println("Edges: " + tn.edges.size());
//			G.v().out.print("Edges: ");
//			while(tnedgeit.hasNext())
//				G.v().out.print(((Transaction)tnedgeit.next()).name + " ");
			G.v().out.println("\nGroup: " + tn.setNumber + "\n");

/*			// Print output for GraphViz package
			Iterator tnedgeit = tn.edges.iterator();
			G.v().out.println("[transaction] " + tn.name + " [name=\"" + b.getMethod().toString() + "\"];");
			while(tnedgeit.hasNext())
			{
				TransactionDataDependency edge = (TransactionDataDependency) tnedgeit.next();
				Transaction tnedge = edge.other;
				G.v().out.println("[transaction] " + tn.name + " -- " + tnedge.name + " [color=" + (edge.size > 5 ? (edge.size > 50 ? "black" : "blue") : "black") + " style=" + (edge.size > 50 ? "dashed" : "solid") + " exactsize=" + edge.size + "];");
			}
*/			

			// If this method does not yet have a reference to the lock object
			// needed for this transaction, then create one.
			if( useGlobalLock[tn.setNumber - 1] )
			{
				if(!addedLocalLockObj[tn.setNumber])
				{
					addedLocalLockObj[tn.setNumber] = true;
					b.getLocals().add(lockObj[tn.setNumber]);
					units.insertBefore(
							Jimple.v().newAssignStmt(
									lockObj[tn.setNumber],
									Jimple.v().newStaticFieldRef(globalLockObj[tn.setNumber].makeRef())),
							(Stmt) firstUnit);
				}
			}
			else
			{
				if(!addedLocalLockObj[tn.setNumber])
				{
					b.getLocals().add(lockObj[tn.setNumber]);
					addedLocalLockObj[tn.setNumber] = true;
				}
				if(tn.lockObject instanceof Ref)
				{
					if(tn.lockObjectArrayIndex != null)
					{
						Local temp = Jimple.v().newLocal("lockObjTemp" + tempNum, ((Ref) tn.lockObject).getType());
						tempNum++;
						if(!b.getLocals().contains(temp))
							b.getLocals().add(temp);
						units.insertBefore(
								Jimple.v().newAssignStmt(
										temp,
										tn.lockObject),
								(Stmt) tn.begin);
						units.insertBefore(
								Jimple.v().newAssignStmt(
										lockObj[tn.setNumber],
										Jimple.v().newArrayRef(temp, tn.lockObjectArrayIndex)),
								(Stmt) tn.begin);
					}
					else
					{												
						units.insertBefore(
								Jimple.v().newAssignStmt(
										lockObj[tn.setNumber],
										tn.lockObject),
								(Stmt) tn.begin);
					}
				}
			}
			
			// Add synchronization code
			// For transactions from synchronized methods, use synchronizeSingleEntrySingleExitBlock()
			// to add all necessary code (including ugly exception handling)
			// For transactions from synchronized blocks, simply replace the
			// monitorenter/monitorexit statements with new ones
			
			Value currentLockObject;				
			if( useGlobalLock[tn.setNumber - 1] || tn.lockObject instanceof Ref)	
			{
				currentLockObject = lockObj[tn.setNumber];
			}
			else
			{
				currentLockObject = tn.lockObject;
			}

			if(tn.wholeMethod)
			{
				thisMethod.setModifiers( thisMethod.getModifiers() & ~ (Modifier.SYNCHRONIZED) ); // remove synchronized modifier for this method
				synchronizeSingleEntrySingleExitBlock(b, (Stmt) firstUnit, (Stmt) lastUnit, (Local) currentLockObject);
			}
			else
			{
				if(tn.begin == null) 
					G.v().out.println("ERROR: Transaction has no beginning statement: " + tn.method.toString());
					
				Stmt newBegin = Jimple.v().newEnterMonitorStmt(currentLockObject);
				units.insertBefore(newBegin, tn.begin);
				redirectTraps(b, tn.begin, newBegin);
				units.remove(tn.begin);
				
				Iterator endsIt = tn.ends.iterator();
				while(endsIt.hasNext())
				{
					Stmt sEnd = (Stmt) endsIt.next();
					Stmt newEnd = Jimple.v().newExitMonitorStmt(currentLockObject);
					units.insertBefore(newEnd, sEnd);
					redirectTraps(b, sEnd, newEnd);
					units.remove(sEnd);
				}
			}
			
			// Replace calls to notify() with calls to notifyAll()
			// Replace base object with appropriate lockobj
			Iterator notifysIt = tn.notifys.iterator();
			while(notifysIt.hasNext())
			{
				Stmt sNotify = (Stmt) notifysIt.next();
				Stmt newNotify = 
					Jimple.v().newInvokeStmt(
           				Jimple.v().newVirtualInvokeExpr(
       						(Local) currentLockObject,
       						sNotify.getInvokeExpr().getMethodRef().declaringClass().getMethod("void notifyAll()").makeRef(), 
       						Collections.EMPTY_LIST));
	            units.insertBefore(newNotify, sNotify);
				redirectTraps(b, sNotify, newNotify);
				units.remove(sNotify);
			}

			// Replace base object of calls to wait with appropriate lockobj
			Iterator waitsIt = tn.waits.iterator();
			while(waitsIt.hasNext())
			{
				Stmt sWait = (Stmt) waitsIt.next();
				Stmt newWait = 
					Jimple.v().newInvokeStmt(
           				Jimple.v().newVirtualInvokeExpr(
       						(Local) currentLockObject,
       						sWait.getInvokeExpr().getMethodRef().declaringClass().getMethod("void notifyAll()").makeRef(), 
       						Collections.EMPTY_LIST));
	            units.insertBefore(newWait, sWait);
				redirectTraps(b, sWait, newWait);
				units.remove(sWait);
			}
		}
	}
	
	public void synchronizeSingleEntrySingleExitBlock(Body b, Stmt start, Stmt end, Local lockObj)
	{
		Chain units = b.getUnits();
		
		// <existing local defs>
		
		// add a throwable to local vars
		Local throwableLocal = Jimple.v().newLocal("throwableLocal" + (throwableNum++), RefType.v("java.lang.Throwable"));
		b.getLocals().add(throwableLocal);
		
		// <existing identity refs>
		
		// add entermonitor statement and label0
		Unit label0Unit = start;
		units.insertBefore(Jimple.v().newEnterMonitorStmt(lockObj), start);
		
		// <existing code body>	
		
		// add normal flow and labels
		Unit labelExitMonitorStmt = (Unit) Jimple.v().newExitMonitorStmt(lockObj);
		units.insertBefore(labelExitMonitorStmt, end); // steal jumps to end, send them to monitorexit
//		end = (Stmt) units.getSuccOf(end);
		Unit label1Unit = (Unit) Jimple.v().newGotoStmt(end);
		units.insertBefore(label1Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		
		// add exceptional flow and labels
		Unit label2Unit = (Unit) Jimple.v().newIdentityStmt(throwableLocal, Jimple.v().newCaughtExceptionRef());
		units.insertBefore(label2Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		Unit label3Unit = (Unit) Jimple.v().newExitMonitorStmt(lockObj);
		units.insertBefore(label3Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		Unit label4Unit = (Unit) Jimple.v().newThrowStmt(throwableLocal);
		units.insertBefore(label4Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		
		// <existing end statement>
		
		// add exception routing table
		SootClass throwableClass = Scene.v().loadClassAndSupport("java.lang.Throwable");
		b.getTraps().addLast(Jimple.v().newTrap(throwableClass, label0Unit, label1Unit, label2Unit));
		b.getTraps().addLast(Jimple.v().newTrap(throwableClass, label3Unit, label4Unit, label2Unit));

	}
	
	public void redirectTraps(Body b, Unit oldUnit, Unit newUnit)
	{
		Chain traps = b.getTraps();
		Iterator trapsIt = traps.iterator();
		while(trapsIt.hasNext())
		{
			AbstractTrap trap = (AbstractTrap) trapsIt.next();
			if(trap.getHandlerUnit() == oldUnit)
				trap.setHandlerUnit(newUnit);
			if(trap.getBeginUnit() == oldUnit)
				trap.setBeginUnit(newUnit);
			if(trap.getEndUnit() == oldUnit)
				trap.setEndUnit(newUnit);
		}
	}
}
