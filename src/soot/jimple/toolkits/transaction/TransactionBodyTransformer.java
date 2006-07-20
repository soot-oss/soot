package soot.jimple.toolkits.transaction;

import java.util.*;
import soot.*;
import soot.util.Chain;
import soot.jimple.Stmt;
import soot.jimple.Jimple;
import soot.jimple.IdentityStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.toolkits.scalar.*;

public class TransactionBodyTransformer extends BodyTransformer
{
    private static TransactionBodyTransformer instance = new TransactionBodyTransformer();
    private TransactionBodyTransformer() {}

    public static TransactionBodyTransformer v() { return instance; }

    private FlowSet fs;
    private int maxLockObjs;
    
    public void setDetails(FlowSet fs, int maxLockObjs)
    {
    	this.fs = fs;
    	this.maxLockObjs = maxLockObjs; 
    }
    
    public static boolean[] addedGlobalLockObj = null;
	private static int throwableNum = 0; // doesn't matter if not reinitialized to 0
    
    protected void internalTransform(Body b, String phase, Map opts)
	{
		// 
		SootMethod thisMethod = b.getMethod();
    	Chain units = b.getUnits();
		Iterator unitIt = units.iterator();
		Unit firstUnit = (Unit) units.getFirst();
		Unit lastUnit = (Unit) units.getLast();
		// skip firstUnit past all the identity refs at the beginning
		while(firstUnit instanceof IdentityStmt)
		{
			firstUnit = (Unit) units.getSuccOf(firstUnit);
		}
		// skip lastUnit to before the final return statement
		if(lastUnit instanceof RetStmt || lastUnit instanceof ReturnStmt || lastUnit instanceof ReturnVoidStmt)
			lastUnit = (Unit) units.getPredOf(lastUnit);
		
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
        if (!Scene.v().getMainClass().declaresMethod("void main(java.lang.String[])"))
            throw new RuntimeException("couldn't find main() in mainClass");
        
        // Add all global lock objects to the main class if not yet added.
        // Get references to them if they do already exist.
  		for(int i = 1; i < maxLockObjs; i++)
   		{
   			if (!addedGlobalLockObj[i])
            {
            	// Add globalLockObj field
            	globalLockObj[i] = new SootField("globalLockObj" + i, RefType.v("java.lang.Object"), 
                                      Modifier.STATIC | Modifier.PUBLIC);
            	Scene.v().getMainClass().addField(globalLockObj[i]);

            	addedGlobalLockObj[i] = true;
            }
            else
            {
            	globalLockObj[i] = Scene.v().getMainClass().getFieldByName("globalLockObj" + i);
            }
   		}
   		
   		// If the current method is the main method, for each global lock object,
   		// add a local lock object and assign it a new object.  Copy the new 
   		// local lock object into the global lock object for use by other fns.
        if(thisMethod.getSubSignature().equals("void main(java.lang.String[])"))
        {
    		for(int i = 1; i < maxLockObjs; i++)
    		{
				// add local lock obj
    			addedLocalLockObj[i] = true;
				b.getLocals().add(lockObj[i]);
	            
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
		
		// Iterate through all of the transactions in the current method
		Iterator fsIt = fs.iterator();
		while(fsIt.hasNext())
		{
			Transaction tn = (Transaction) fsIt.next();
			
			// Print information about this transaction for debugging
//			G.v().out.print("Transaction #" + tn.IDNum + "  ");
//			G.v().out.print("Location: " + b.getMethod().getDeclaringClass().toString() + ":" + b.getMethod().toString() + ":  ");
//			G.v().out.println("Begin: " + tn.begin.toString() + "  ");
//			G.v().out.print("End  : " + tn.ends.toString() + " \n");
//			G.v().out.println("Size : " + tn.units.size());
//			if(tn.read.size() < 100)
//				G.v().out.print("Read :\n" + tn.read.toString());
//			else
//				G.v().out.println("Read : " + tn.read.size() + "  ");
//			G.v().out.println("R/Ws : " + (tn.read.size() + tn.write.size()) + "  ");
//			if(tn.write.size() < 100)
//				G.v().out.print("Write:\n" + tn.write.toString());
//			else
//				G.v().out.println("Write: " + tn.write.size() + "  ");
//			Iterator tnedgeit = tn.edges.iterator();
//		    G.v().out.println("Edges: " + tn.edges.size());
//				G.v().out.print("Edges: ");
//			while(tnedgeit.hasNext())
//				G.v().out.print(((Transaction)tnedgeit.next()).IDNum + " ");
//			G.v().out.println("\nGroup: " + tn.setNumber + "\n");

			// Print output for GraphViz package
			Iterator tnedgeit = tn.edges.iterator();
			G.v().out.println("[transaction] " + tn.name + "n" + tn.IDNum + " [name=\"" + "Tn" + tn.IDNum + " " + b.getMethod().toString() + "\"];");
			while(tnedgeit.hasNext())
			{
				DataDependency edge = (DataDependency) tnedgeit.next();
				Transaction tnedge = edge.other;
				G.v().out.println("[transaction] " + tn.name + "n" + tn.IDNum + " -- " + tnedge.name + "n" + tnedge.IDNum + " [color=" + (edge.size > 1 ? (edge.size > 20 ? "black" : "blue") : "black") + " style=" + (edge.size > 20 ? "dashed" : "solid") + "];");
			}
			

			// If this method does not yet have a reference to the lock object
			// needed for this transaction, then create one.
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
			
			// Add synchronization code
			// For transactions from synchronized methods, use synchronizeSingleEntrySingleExitBlock()
			// to add all necessary code (including ugly exception handling)
			// For transactions from synchronized blocks, simply replace the
			// monitorenter/monitorexit statements with new ones
			if(tn.wholeMethod)
			{
				thisMethod.setModifiers( thisMethod.getModifiers() & ~ (Modifier.SYNCHRONIZED) ); // remove synchronized modifier for this method
				synchronizeSingleEntrySingleExitBlock(b, (Stmt) firstUnit, (Stmt) lastUnit, lockObj[tn.setNumber]);
			}
			else
			{
				units.insertBefore(Jimple.v().newEnterMonitorStmt(lockObj[tn.setNumber]), tn.begin);
				units.remove(tn.begin);
				Iterator endsIt = tn.ends.iterator();
				while(endsIt.hasNext())
				{
					Stmt sEnd = (Stmt) endsIt.next();
						units.insertBefore(Jimple.v().newExitMonitorStmt(
							lockObj[tn.setNumber]), sEnd);
						units.remove(sEnd);
				}
			}
			
			// Replace calls to notify() with calls to notifyAll()
			Iterator notifysIt = tn.notifys.iterator();
			while(notifysIt.hasNext())
			{
				Stmt sNotify = (Stmt) notifysIt.next();
				sNotify.getInvokeExpr().setMethodRef(
					sNotify.getInvokeExpr().getMethodRef().declaringClass().getMethod("void notifyAll()").makeRef());
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
		units.insertAfter(Jimple.v().newExitMonitorStmt(lockObj), end);
		end = (Stmt) units.getSuccOf(end);
		Unit label1Unit = (Unit) Jimple.v().newGotoStmt((Stmt) units.getSuccOf(end));
		units.insertAfter(label1Unit, end);
		end = (Stmt) units.getSuccOf(end);
		
		// add exceptional flow and labels
		Unit label2Unit = (Unit) Jimple.v().newIdentityStmt(throwableLocal, Jimple.v().newCaughtExceptionRef());
		units.insertAfter(label2Unit, end);
		end = (Stmt) units.getSuccOf(end);
		Unit label3Unit = (Unit) Jimple.v().newExitMonitorStmt(lockObj);
		units.insertAfter(label3Unit, end);
		end = (Stmt) units.getSuccOf(end);
		Unit label4Unit = (Unit) Jimple.v().newThrowStmt(throwableLocal);
		units.insertAfter(label4Unit, end);
		end = (Stmt) units.getSuccOf(end);
		
		// <existing return statement>
		
		// add exception routing table
		SootClass throwableClass = Scene.v().loadClassAndSupport("java.lang.Throwable");
		b.getTraps().addLast(Jimple.v().newTrap(throwableClass, label0Unit, label1Unit, label2Unit));
		b.getTraps().addLast(Jimple.v().newTrap(throwableClass, label3Unit, label4Unit, label2Unit));
	}
}
