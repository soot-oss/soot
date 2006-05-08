package soot.jimple.toolkits.transaction;

import java.util.*;
import soot.*;
import soot.util.Chain;
import soot.jimple.Stmt;
import soot.jimple.Jimple;
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
    
    protected void internalTransform(Body b, String phase, Map opts)
	{
    	Chain units = b.getUnits();
		Unit firstUnit = (Unit) units.iterator().next();
		
		Local[] lockObj = new Local[maxLockObjs]; 			
		boolean[] addedLocalLockObj = new boolean[maxLockObjs];
		SootField[] globalLockObj = new SootField[maxLockObjs];
		for(int i = 1; i < maxLockObjs; i++)
		{
			lockObj[i] = Jimple.v().newLocal("lockObj" + i, RefType.v("java.lang.Object"));
			addedLocalLockObj[i] = false;
			globalLockObj[i] = null;
		}

 		synchronized(this)
        {
            if (!Scene.v().getMainClass().declaresMethod("void main(java.lang.String[])"))
                throw new RuntimeException("couldn't find main() in mainClass");
//            G.v().out.println("Processing: " + b.getMethod().getDeclaringClass().toString() + ":" + b.getMethod().toString());
            
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
        }

        
        if(b.getMethod().getSubSignature().equals("void main(java.lang.String[])"))
        {
    		for(int i = 1; i < maxLockObjs; i++)
    		{
//    			if(addedGlobalLockObj[i])
//    			{
	    			addedLocalLockObj[i] = true;
	            
					b.getLocals().add(lockObj[i]);
					units.insertBefore(
							Jimple.v().newAssignStmt(
									lockObj[i],
									Jimple.v().newNewExpr(RefType.v("java.lang.Object"))),
							(Stmt) firstUnit);
					
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
		        
		        	units.insertBefore(
		        			Jimple.v().newAssignStmt(
		        					Jimple.v().newStaticFieldRef(globalLockObj[i].makeRef()),
		        					lockObj[i]),
		        			(Stmt) firstUnit);
//    			}
    		}
        }
		
//		CallGraph cg = Scene.v().getCallGraph();
		Iterator fsIt = fs.iterator();
		while(fsIt.hasNext())
		{
			Transaction tn = (Transaction) fsIt.next();
			//tn.method = b.getMethod(); // set the transaction's method THIS WAS DONE ALREADY
			
			// Printout
			G.v().out.print("Transaction #" + tn.IDNum + "  ");
//			G.v().out.print("Location: " + b.getMethod().getDeclaringClass().toString() + ":" + b.getMethod().toString() + ":  ");
//			G.v().out.println("Begin: " + tn.begin.toString() + "  ");
//			G.v().out.print("End  : " + tn.ends.toString() + " \n");
//			G.v().out.println("Size : " + tn.units.size());
//			if(tn.read.size() < 100)
//				G.v().out.print("Read :\n" + tn.read.toString());
//			else
//				G.v().out.println("Read : " + tn.read.size() + "  ");
			G.v().out.println("R/Ws : " + (tn.read.size() + tn.write.size()) + "  ");
//			if(tn.write.size() < 100)
//				G.v().out.print("Write:\n" + tn.write.toString());
//			else
//				G.v().out.println("Write: " + tn.write.size() + "  ");
//			Iterator tnedgeit = tn.edges.iterator();
		    G.v().out.println("Edges: " + tn.edges.size());
//				G.v().out.print("Edges: ");
//			while(tnedgeit.hasNext())
//				G.v().out.print(((Transaction)tnedgeit.next()).IDNum + " ");
//			G.v().out.println("\nGroup: " + tn.setNumber + "\n");

//			Iterator tnedgeit = tn.edges.iterator();
//			G.v().out.println("n" + tn.IDNum + " [name=\"" + "Tn" + tn.IDNum + " " + b.getMethod().toString() + "\"];");
//			while(tnedgeit.hasNext())
//				G.v().out.println("n" + tn.IDNum + " -- n" + ((Transaction)tnedgeit.next()).IDNum + ";");
			
			if(!addedLocalLockObj[tn.setNumber])
			{
				addedLocalLockObj[tn.setNumber] = true;
				b.getLocals().add(lockObj[tn.setNumber]);
				units.insertBefore(
						Jimple.v().newAssignStmt(
								lockObj[tn.setNumber],
//								Jimple.v().newStaticFieldRef(Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), // synchronize on "System.out"
								Jimple.v().newStaticFieldRef(globalLockObj[tn.setNumber].makeRef())), // synchronize on a new globally accessible static object
						(Stmt) firstUnit);
			}
			units.insertBefore(Jimple.v().newEnterMonitorStmt(lockObj[tn.setNumber]), tn.begin);
			units.remove(tn.begin);
			Iterator endsIt = tn.ends.iterator();
			while(endsIt.hasNext())
			{
				Stmt sEnd = (Stmt) endsIt.next();
				units.insertBefore(Jimple.v().newExitMonitorStmt(lockObj[tn.setNumber]), sEnd);
				units.remove(sEnd);
			}
		}
	}
}