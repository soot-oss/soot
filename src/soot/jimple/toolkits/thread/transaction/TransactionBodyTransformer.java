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
    private static boolean addedGlobalLockDefs = false;
	private static int throwableNum = 0; // doesn't matter if not reinitialized to 0
    
    protected void internalTransform(Body b, String phase, Map opts)
	{
		// 
		JimpleBody j = (JimpleBody) b;
		SootMethod thisMethod = b.getMethod();
    	PatchingChain units = b.getUnits();
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
   		
   		// If the current method is the clinit method of the main class, for each global lock object,
   		// add a local lock object and assign it a new object.  Copy the new 
   		// local lock object into the global lock object for use by other fns.
        if(!addedGlobalLockDefs)// thisMethod.getSubSignature().equals("void <clinit>()") && thisMethod.getDeclaringClass() == Scene.v().getMainClass())
        {
        	// Either get or add the <clinit> method to the main class
        	SootClass mainClass = Scene.v().getMainClass();
        	SootMethod clinitMethod = null;
        	JimpleBody clinitBody = null;
        	Stmt firstStmt = null;
        	boolean addingNewClinit = !mainClass.declaresMethod("void <clinit>()");
        	if(addingNewClinit)
        	{
        		clinitMethod = new SootMethod("<clinit>", new ArrayList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
        		clinitBody = Jimple.v().newBody(clinitMethod);
        		clinitMethod.setActiveBody(clinitBody);
        		mainClass.addMethod(clinitMethod);
        	}
        	else
        	{
				clinitMethod = mainClass.getMethod("void <clinit>()");
				clinitBody = (JimpleBody) clinitMethod.getActiveBody();
				firstStmt = clinitBody.getFirstNonIdentityStmt();
        	}
        	PatchingChain clinitUnits = clinitBody.getUnits();
        	
    		for(int i = 1; i < maxLockObjs; i++)
    		{
    			if( useGlobalLock[i - 1] )
    			{
					// add local lock obj
//	    			addedLocalLockObj[i] = true;
					clinitBody.getLocals().add(lockObj[i]); // TODO: add name conflict avoidance code
		            
		            // assign new object to lock obj
		            Stmt newStmt = Jimple.v().newAssignStmt(lockObj[i],
							Jimple.v().newNewExpr(RefType.v("java.lang.Object")));
					if(addingNewClinit)
						clinitUnits.add(newStmt);
					else
						clinitUnits.insertBeforeNoRedirect(newStmt, firstStmt);

					// initialize new object
		            SootClass objectClass = Scene.v().loadClassAndSupport("java.lang.Object");
		            RefType type = RefType.v(objectClass);
		            SootMethod initMethod = objectClass.getMethod("void <init>()");
		            Stmt initStmt = Jimple.v().newInvokeStmt(
		            				Jimple.v().newSpecialInvokeExpr(lockObj[i], 
		            					initMethod.makeRef(), Collections.EMPTY_LIST));
		            if(addingNewClinit)
			            clinitUnits.add(initStmt);
			        else
			        	clinitUnits.insertBeforeNoRedirect(initStmt, firstStmt);
			        
			        // copy new object to global static lock object (for use by other fns)
			        Stmt assignStmt = Jimple.v().newAssignStmt(
	       							  Jimple.v().newStaticFieldRef(globalLockObj[i].makeRef()), lockObj[i]);
		        	if(addingNewClinit)
			        	clinitUnits.add(assignStmt);
			        else
			        	clinitUnits.insertBeforeNoRedirect(assignStmt, firstStmt);
	       		}
    		}
    		if(addingNewClinit)
	    		clinitUnits.add(Jimple.v().newReturnVoidStmt());
    		addedGlobalLockDefs = true;
        }
		
		int tempNum = 1;
		// Iterate through all of the transactions in the current method
		Iterator fsIt = fs.iterator();
		while(fsIt.hasNext())
		{
			Transaction tn = ((TransactionFlowPair) fsIt.next()).tn;
			if(tn.setNumber == -1)
				continue; // this tn should be deleted... for now just skip it!

			// If this method does not yet have a reference to the lock object
			// needed for this transaction, then create one.
			Stmt assignLocalLockStmt = null;
			if( useGlobalLock[tn.setNumber - 1] )
			{
				if(!addedLocalLockObj[tn.setNumber])
					b.getLocals().add(lockObj[tn.setNumber]);
				addedLocalLockObj[tn.setNumber] = true;
				assignLocalLockStmt = Jimple.v().newAssignStmt(lockObj[tn.setNumber],
								Jimple.v().newStaticFieldRef(globalLockObj[tn.setNumber].makeRef()));
				if(tn.wholeMethod)
					units.insertBeforeNoRedirect(assignLocalLockStmt, firstUnit);
				else
					units.insertBefore(assignLocalLockStmt, (Stmt) tn.begin);
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
						if(tn.wholeMethod)
						{
							units.insertBeforeNoRedirect(
								Jimple.v().newAssignStmt(
										temp,
										tn.lockObject),
								(Stmt) firstUnit);
							units.insertBeforeNoRedirect(
								Jimple.v().newAssignStmt(
										lockObj[tn.setNumber],
										Jimple.v().newArrayRef(temp, tn.lockObjectArrayIndex)),
								(Stmt) firstUnit);
						}
						else
						{
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
					}
					else
					{	
						assignLocalLockStmt = Jimple.v().newAssignStmt(lockObj[tn.setNumber], tn.lockObject);
						if(tn.wholeMethod)
							units.insertBeforeNoRedirect(assignLocalLockStmt, (Stmt) firstUnit);
						else
							units.insertBefore(assignLocalLockStmt, (Stmt) tn.begin);
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
//				units.insertBefore(assignLocalLockStmt.clone(), newNotify);
			}

			// Replace base object of calls to wait with appropriate lockobj
			Iterator waitsIt = tn.waits.iterator();
			while(waitsIt.hasNext())
			{
				Stmt sWait = (Stmt) waitsIt.next();
				((InstanceInvokeExpr) sWait.getInvokeExpr()).setBase(currentLockObject); // WHAT IF THIS IS THE WRONG LOCK IN A PAIR OF NESTED LOCKS???
//				Stmt newWait = 
//					Jimple.v().newInvokeStmt(
//         				Jimple.v().newVirtualInvokeExpr(
//       						(Local) currentLockObject,
//       						sWait.getInvokeExpr().getMethodRef().declaringClass().getMethod("void wait()").makeRef(), 
//       						Collections.EMPTY_LIST));
//	            units.insertBefore(newWait, sWait);
//				redirectTraps(b, sWait, newWait);
//				units.remove(sWait);
//				units.insertBefore(assignLocalLockStmt.clone(), sWait);
			}
		}
	}
	
	public void synchronizeSingleEntrySingleExitBlock(Body b, Stmt start, Stmt end, Local lockObj)
	{
		PatchingChain units = b.getUnits();
		
		// <existing local defs>
		
		// add a throwable to local vars
		Local throwableLocal = Jimple.v().newLocal("throwableLocal" + (throwableNum++), RefType.v("java.lang.Throwable"));
		b.getLocals().add(throwableLocal);
		
		// <existing identity refs>
		
		// add entermonitor statement and label0
//		Unit label0Unit = start;
		Unit labelEnterMonitorStmt = Jimple.v().newEnterMonitorStmt(lockObj);
		units.insertBeforeNoRedirect(labelEnterMonitorStmt, start); // steal jumps to start, send them to monitorenter
		
		// <existing code body>	check for return statements
		List returnUnits = new ArrayList();
		if(start != end)
		{
			Iterator bodyIt = units.iterator(start, end);
			while(bodyIt.hasNext())
			{
				Stmt bodyStmt = (Stmt) bodyIt.next();
				if(bodyIt.hasNext()) // ignore the end unit
				{
					if( bodyStmt instanceof ReturnStmt ||
						bodyStmt instanceof ReturnVoidStmt)
					{
						returnUnits.add(bodyStmt);
					}
				}
			}
		}
		
		// add normal flow and labels
		Unit labelExitMonitorStmt = (Unit) Jimple.v().newExitMonitorStmt(lockObj);
		units.insertBefore(labelExitMonitorStmt, end); // steal jumps to end, send them to monitorexit
//		end = (Stmt) units.getSuccOf(end);
		Unit label1Unit = (Unit) Jimple.v().newGotoStmt(end);
		units.insertBeforeNoRedirect(label1Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		
		// add exceptional flow and labels
		Unit label2Unit = (Unit) Jimple.v().newIdentityStmt(throwableLocal, Jimple.v().newCaughtExceptionRef());
		units.insertBeforeNoRedirect(label2Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		Unit label3Unit = (Unit) Jimple.v().newExitMonitorStmt(lockObj);
		units.insertBeforeNoRedirect(label3Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		Unit label4Unit = (Unit) Jimple.v().newThrowStmt(throwableLocal);
		units.insertBeforeNoRedirect(label4Unit, end);
//		end = (Stmt) units.getSuccOf(end);
		
		// <existing end statement>

		Iterator returnIt = returnUnits.iterator();
		while(returnIt.hasNext())
		{
			Stmt bodyStmt = (Stmt) returnIt.next();
			units.insertBefore(Jimple.v().newExitMonitorStmt(lockObj), bodyStmt); // TODO: WHAT IF IT'S IN A NESTED TRANSACTION???
//			Stmt placeholder = Jimple.v().newNopStmt();
//			units.insertAfter(Jimple.v().newNopStmt(), label4Unit);
//			bodyStmt.redirectJumpsToThisTo(placeholder);
//			units.insertBefore(Jimple.v().newGotoStmt(placeholder), bodyStmt);
//			units.remove(bodyStmt);
//			units.swapWith(placeholder, bodyStmt);
			
//			units.swapWith
		}
		
		// add exception routing table
		Unit label0Unit = (Unit) units.getSuccOf(labelEnterMonitorStmt);
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
