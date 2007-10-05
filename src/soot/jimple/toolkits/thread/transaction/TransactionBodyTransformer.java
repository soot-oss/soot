package soot.jimple.toolkits.thread.transaction;

import java.util.*;

import soot.*;
import soot.util.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;
import soot.jimple.toolkits.infoflow.*;

public class TransactionBodyTransformer extends BodyTransformer
{
    private static final TransactionBodyTransformer instance = new TransactionBodyTransformer();
    private TransactionBodyTransformer() {}

    public static TransactionBodyTransformer v() { return instance; }
    
    public static boolean[] addedGlobalLockObj = null;
    private static boolean addedGlobalLockDefs = false;
	private static int throwableNum = 0; // doesn't matter if not reinitialized to 0
    
    protected void internalTransform(Body b, String phase, Map opts)
    {
    	throw new RuntimeException("Not Supported");
    }
    
    protected void internalTransform(Body b, FlowSet fs, List<TransactionGroup> groups)
	{
		// 
		JimpleBody j = (JimpleBody) b;
		SootMethod thisMethod = b.getMethod();
    	PatchingChain units = b.getUnits();
		Iterator unitIt = units.iterator();
		Unit firstUnit = j.getFirstNonIdentityStmt();
		Unit lastUnit = (Unit) units.getLast();
		
		// Objects of synchronization, plus book keeping
		Local[] lockObj = new Local[groups.size()];
		boolean[] addedLocalLockObj = new boolean[groups.size()];
		SootField[] globalLockObj = new SootField[groups.size()];
		for(int i = 1; i < groups.size(); i++)
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
  		for(int i = 1; i < groups.size(); i++)
   		{
   			TransactionGroup tnGroup = groups.get(i);
// 			if( useGlobalLock[i - 1] )
			if( !tnGroup.useDynamicLock && !tnGroup.useLocksets )
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
        	
    		for(int i = 1; i < groups.size(); i++)
    		{
    			TransactionGroup tnGroup = groups.get(i);
//    			if( useGlobalLock[i - 1] )
				if( !tnGroup.useDynamicLock && !tnGroup.useLocksets )
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
		Stmt newPrep = null;
		while(fsIt.hasNext())
		{
			Transaction tn = ((TransactionFlowPair) fsIt.next()).tn;
			if(tn.setNumber == -1)
				continue; // this tn should be deleted... for now just skip it!
				
			if(tn.wholeMethod)
			{			
				thisMethod.setModifiers( thisMethod.getModifiers() & ~ (Modifier.SYNCHRONIZED) ); // remove synchronized modifier for this method
			}

			Local clo = null; // depends on type of locking
			LockRegion clr = null; // current lock region
			int lockNum = 0;
			boolean moreLocks = true;
			while(moreLocks)
			{
				// If this method does not yet have a reference to the lock object
				// needed for this transaction, then create one.
				if( tn.group.useDynamicLock )
				{
					Value lock = getLockFor((EquivalentValue) tn.lockObject); // adds local vars and global objects if needed
					if(lock instanceof Ref)
					{
						if(lock instanceof InstanceFieldRef)
						{
							InstanceFieldRef ifr = (InstanceFieldRef) lock;
							if(ifr.getBase() instanceof FakeJimpleLocal)
								lock = reconstruct(b, units, ifr, (tn.entermonitor != null ? tn.entermonitor : tn.beginning), (tn.entermonitor != null));
						}
						if(!b.getLocals().contains(lockObj[tn.setNumber]))
							b.getLocals().add(lockObj[tn.setNumber]);
						
						newPrep = Jimple.v().newAssignStmt(lockObj[tn.setNumber], lock);
						if(tn.wholeMethod)
							units.insertBeforeNoRedirect(newPrep, firstUnit);
						else
							units.insertBefore(newPrep, tn.entermonitor);
						clo = lockObj[tn.setNumber];
					}
					else if(lock instanceof Local)
						clo = (Local) lock;
					else
						throw new RuntimeException("Unknown type of lock (" + lock + "): expected Ref or Local");
					clr = tn;
					moreLocks = false;
				}
				else if( tn.group.useLocksets )
				{
					Value lock = getLockFor((EquivalentValue) tn.lockset.get(lockNum)); // adds local vars and global objects if needed
					if( lock instanceof FieldRef )
					{
						if(lock instanceof InstanceFieldRef)
						{
							InstanceFieldRef ifr = (InstanceFieldRef) lock;
							if(ifr.getBase() instanceof FakeJimpleLocal)
								lock = reconstruct(b, units, ifr, (tn.entermonitor != null ? tn.entermonitor : tn.beginning), (tn.entermonitor != null));
						}
						// add a local variable for this lock
						Local lockLocal = Jimple.v().newLocal("locksetObj" + tempNum, RefType.v("java.lang.Object"));
						tempNum++;
						b.getLocals().add(lockLocal);
					
						// make it refer to the right lock object
						newPrep = Jimple.v().newAssignStmt(lockLocal, lock);
						if(tn.entermonitor != null)
							units.insertBefore(newPrep, tn.entermonitor);
						else
							units.insertBeforeNoRedirect(newPrep, tn.beginning);
							
						// use it as the lock
						clo = lockLocal;
					}
					else if( lock instanceof Local )
						clo = (Local) lock;
					else
						throw new RuntimeException("Unknown type of lock (" + lock + "): expected FieldRef or Local");

					if(lockNum + 1 >= tn.lockset.size())
						moreLocks = false;
					else
						moreLocks = true;

					if( lockNum > 0 )
					{
						LockRegion nlr = new LockRegion();

						nlr.beginning = clr.beginning;
						for (Pair earlyEnd : clr.earlyEnds) {
							Stmt earlyExitmonitor = (Stmt) earlyEnd.getO2();
							nlr.earlyEnds.add(new Pair(earlyExitmonitor, null)); // <early exitmonitor, null>
						}
						nlr.last = clr.last; // last stmt before exception handling
						if(clr.end != null)
						{
							Stmt endExitmonitor = (Stmt) clr.end.getO2();
							nlr.after = endExitmonitor;
						}
						
						clr = nlr;
					}
					else
						clr = tn;
				}
				else // global lock
				{
					if(!addedLocalLockObj[tn.setNumber])
						b.getLocals().add(lockObj[tn.setNumber]);
					addedLocalLockObj[tn.setNumber] = true;
					newPrep = Jimple.v().newAssignStmt(lockObj[tn.setNumber],
									Jimple.v().newStaticFieldRef(globalLockObj[tn.setNumber].makeRef()));
					if(tn.wholeMethod)
						units.insertBeforeNoRedirect(newPrep, firstUnit);
					else
						units.insertBefore(newPrep, tn.entermonitor);
					clo = lockObj[tn.setNumber];
					clr = tn;
					moreLocks = false;
				}
				
				// Add synchronization code
				// For transactions from synchronized methods, use synchronizeSingleEntrySingleExitBlock()
				// to add all necessary code (including ugly exception handling)
				// For transactions from synchronized blocks, simply replace the
				// monitorenter/monitorexit statements with new ones		
				if(true)
				{
					// Remove old prep stmt
					if( clr.prepStmt != null )
					{
//						units.remove(clr.prepStmt); // seems to trigger bugs in code generation?
					}
					
					// Reuse old entermonitor or insert new one, and insert prep
					Stmt newEntermonitor = Jimple.v().newEnterMonitorStmt(clo);
					if( clr.entermonitor != null )
					{
						units.insertBefore(newEntermonitor, clr.entermonitor);
						// redirectTraps(b, clr.entermonitor, newEntermonitor); // EXPERIMENTAL
						units.remove(clr.entermonitor);
						clr.entermonitor = newEntermonitor;

						// units.insertBefore(newEntermonitor, newPrep); // already inserted
						// clr.prepStmt = newPrep;
					}
					else
					{
						units.insertBeforeNoRedirect(newEntermonitor, clr.beginning);
						clr.entermonitor = newEntermonitor;
						
						// units.insertBefore(newEntermonitor, newPrep); // already inserted
						// clr.prepStmt = newPrep;
					}
					
					// For each early end, reuse or insert exitmonitor stmt
					List<Pair> newEarlyEnds = new ArrayList<Pair>();
					for (Pair end : clr.earlyEnds) {
						Stmt earlyEnd = (Stmt) end.getO1();
						Stmt exitmonitor = (Stmt) end.getO2();
						
						Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
						if( exitmonitor != null )
						{
							if(newPrep != null)
							{
								Stmt tmp = (Stmt) newPrep.clone();
								units.insertBefore(tmp, exitmonitor); // seems to avoid code generation bugs?
							}
							units.insertBefore(newExitmonitor, exitmonitor);
							// redirectTraps(b, exitmonitor, newExitmonitor); // EXPERIMENTAL
							units.remove(exitmonitor);
							newEarlyEnds.add(new Pair(earlyEnd, newExitmonitor));
						}
						else
						{
							if(newPrep != null)
							{
								Stmt tmp = (Stmt) newPrep.clone();
								units.insertBefore(tmp, earlyEnd);
							}
							units.insertBefore(newExitmonitor, earlyEnd);
							newEarlyEnds.add(new Pair(earlyEnd, newExitmonitor));
						}
					}
					clr.earlyEnds = newEarlyEnds;
					
					// If fallthrough end, reuse or insert goto and exit
					if( clr.after != null )
					{
						Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
						if( clr.end != null )
						{
							Stmt exitmonitor = (Stmt) clr.end.getO2();

							if(newPrep != null)
							{
								Stmt tmp = (Stmt) newPrep.clone();
								units.insertBefore(tmp, exitmonitor);
							}
							units.insertBefore(newExitmonitor, exitmonitor);
							// redirectTraps(b, exitmonitor, newExitmonitor); // EXPERIMENTAL
							units.remove(exitmonitor);
							clr.end = new Pair(clr.end.getO1(), newExitmonitor);
						}
						else
						{
							if(newPrep != null)
							{
								Stmt tmp = (Stmt) newPrep.clone();
								units.insertBefore(tmp, clr.after);
							}
							units.insertBefore(newExitmonitor, clr.after); // steal jumps to end, send them to monitorexit
							Stmt newGotoStmt = Jimple.v().newGotoStmt(clr.after);
							units.insertBeforeNoRedirect(newGotoStmt, clr.after);
							clr.end = new Pair(newGotoStmt, newExitmonitor);
							clr.last = newGotoStmt;
						}
					}
					
					// If exceptional end, reuse it, else insert it and traps
					Stmt newExitmonitor = Jimple.v().newExitMonitorStmt(clo);
					if( clr.exceptionalEnd != null )
					{
						Stmt exitmonitor = (Stmt) clr.exceptionalEnd.getO2();
						
						if(newPrep != null)
						{
							Stmt tmp = (Stmt) newPrep.clone();
							units.insertBefore(tmp, exitmonitor);
						}
						units.insertBefore(newExitmonitor, exitmonitor);
							
						units.remove(exitmonitor);
						clr.exceptionalEnd = new Pair(clr.exceptionalEnd.getO1(), newExitmonitor);
					}
					else
					{
						// insert after the last end
						Stmt lastEnd = null; // last end stmt (not same as last stmt)
						if( clr.end != null )
						{
							lastEnd = (Stmt) clr.end.getO1();
						}
						else
						{
							for (Pair earlyEnd : clr.earlyEnds) {
								Stmt end = (Stmt) earlyEnd.getO1();
								if( lastEnd == null || (units.contains(lastEnd) && units.contains(end) && units.follows(end, lastEnd)) )
									lastEnd = end;
							}
						}
						if(clr.last == null) // || !units.contains(clr.last))
							clr.last = lastEnd; // last stmt and last end are the same
						if( lastEnd == null )
							throw new RuntimeException("Lock Region has no ends!  Where should we put the exception handling???");

						// Add throwable
						Local throwableLocal = Jimple.v().newLocal("throwableLocal" + (throwableNum++), RefType.v("java.lang.Throwable"));
						b.getLocals().add(throwableLocal);
						// Add stmts
						Stmt newCatch = Jimple.v().newIdentityStmt(throwableLocal, Jimple.v().newCaughtExceptionRef());
						if(clr.last == null)
							throw new RuntimeException("WHY IS clr.last NULL???");
						if(newCatch == null)
							throw new RuntimeException("WHY IS newCatch NULL???");
						units.insertAfter(newCatch, clr.last);
						units.insertAfter(newExitmonitor, newCatch);
						Stmt newThrow = Jimple.v().newThrowStmt(throwableLocal);
						units.insertAfter(newThrow, newExitmonitor);
						// Add traps
						SootClass throwableClass = Scene.v().loadClassAndSupport("java.lang.Throwable");
						b.getTraps().addFirst(Jimple.v().newTrap(throwableClass, newExitmonitor, newThrow, newCatch));
						b.getTraps().addFirst(Jimple.v().newTrap(throwableClass, clr.beginning, lastEnd, newCatch));
						clr.exceptionalEnd = new Pair(newThrow, newExitmonitor);
					}
				}
/*
				else if(tn.wholeMethod)
				{
					thisMethod.setModifiers( thisMethod.getModifiers() & ~ (Modifier.SYNCHRONIZED) ); // remove synchronized modifier for this method
					synchronizeSingleEntrySingleExitBlock(b, (Stmt) firstUnit, (Stmt) lastUnit, (Local) clo);
				}
				else if(lockNum > 0)
				{
					// don't have all the info to do this right yet
//					synchronizeSingleEntrySingleExitBlock(b, (Stmt) tnbodystart, (Stmt) tnbodyend, (Local) clo);
				}
				else
				{
					if(tn.entermonitor == null) 
						G.v().out.println("ERROR: Transaction has no beginning statement: " + tn.method.toString());
						
					// Deal with entermonitor
					Stmt newBegin = Jimple.v().newEnterMonitorStmt(clo);
					units.insertBefore(newBegin, tn.entermonitor);
					redirectTraps(b, tn.entermonitor, newBegin);
					units.remove(tn.entermonitor);
					
					// Deal with exitmonitors
					// early
					Iterator endsIt = tn.earlyEnds.iterator();
					while(endsIt.hasNext())
					{
						Pair end = (Pair) endsIt.next();
						Stmt sEnd = (Stmt) end.getO2();
						Stmt newEnd = Jimple.v().newExitMonitorStmt(clo);
						units.insertBefore(newEnd, sEnd);
						redirectTraps(b, sEnd, newEnd);
						units.remove(sEnd);
					}
					// exceptional
					Stmt sEnd = (Stmt) tn.exceptionalEnd.getO2();
					Stmt newEnd = Jimple.v().newExitMonitorStmt(clo);
					units.insertBefore(newEnd, sEnd);
					redirectTraps(b, sEnd, newEnd);
					units.remove(sEnd);
					// fallthrough
					sEnd = (Stmt) tn.end.getO2();
					newEnd = Jimple.v().newExitMonitorStmt(clo);
					units.insertBefore(newEnd, sEnd);
					redirectTraps(b, sEnd, newEnd);
					units.remove(sEnd);
				}
*/
				
				// Replace calls to notify() with calls to notifyAll()
				// Replace base object with appropriate lockobj
				lockNum++;
			}
			
			// deal with waits and notifys
			{
				Iterator<Object> notifysIt = tn.notifys.iterator();
				while(notifysIt.hasNext())
				{
					Stmt sNotify = (Stmt) notifysIt.next();
					Stmt newNotify = 
						Jimple.v().newInvokeStmt(
	           				Jimple.v().newVirtualInvokeExpr(
	       						clo,
	       						sNotify.getInvokeExpr().getMethodRef().declaringClass().getMethod("void notifyAll()").makeRef(), 
	       						Collections.EMPTY_LIST));
					if(newPrep != null)
					{
						Stmt tmp = (Stmt) newPrep.clone();
		       			units.insertBefore(tmp, sNotify);
			            units.insertBefore(newNotify, tmp);
			        }
			        else
			        	units.insertBefore(newNotify, sNotify);
			        	
					redirectTraps(b, sNotify, newNotify);
					units.remove(sNotify);
				}

				// Replace base object of calls to wait with appropriate lockobj
				Iterator<Object> waitsIt = tn.waits.iterator();
				while(waitsIt.hasNext())
				{
					Stmt sWait = (Stmt) waitsIt.next();
					((InstanceInvokeExpr) sWait.getInvokeExpr()).setBase(clo); // WHAT IF THIS IS THE WRONG LOCK IN A PAIR OF NESTED LOCKS???
					if(newPrep != null)
						units.insertBefore((Stmt) newPrep.clone(), sWait);
	//				Stmt newWait = 
	//					Jimple.v().newInvokeStmt(
	//         				Jimple.v().newVirtualInvokeExpr(
	//       						(Local) clo,
	//       						sWait.getInvokeExpr().getMethodRef().declaringClass().getMethod("void wait()").makeRef(), 
	//       						Collections.EMPTY_LIST));
	//	            units.insertBefore(newWait, sWait);
	//				redirectTraps(b, sWait, newWait);
	//				units.remove(sWait);
				}
			}
		}
	}
	
	static int baseLocalNum = 0;
	
	public InstanceFieldRef reconstruct(Body b, PatchingChain units, InstanceFieldRef lock, Stmt insertBefore, boolean redirect)
	{
		G.v().out.println("Reconstructing " + lock);

		if(!(lock.getBase() instanceof FakeJimpleLocal))
		{
			G.v().out.println("  base is not a FakeJimpleLocal");
			return lock;
		}
		FakeJimpleLocal fakeBase = (FakeJimpleLocal) lock.getBase();
		
		if(!(fakeBase.getInfo() instanceof LocksetAnalysis))
			throw new RuntimeException("InstanceFieldRef cannot be reconstructed due to missing LocksetAnalysis info: " + lock);
		LocksetAnalysis la = (LocksetAnalysis) fakeBase.getInfo();
		
		EquivalentValue baseEqVal = la.baseFor(lock);
		if(baseEqVal == null)
			throw new RuntimeException("InstanceFieldRef cannot be reconstructed due to lost base from Lockset");
		Value base = baseEqVal.getValue();
		Local baseLocal;
		if(base instanceof InstanceFieldRef)
		{
			Value newBase = reconstruct(b, units, (InstanceFieldRef) base, insertBefore, redirect);
			baseLocal = Jimple.v().newLocal("baseLocal" + (baseLocalNum++), newBase.getType());
			b.getLocals().add(baseLocal);
					
			// make it equal to the right value
			Stmt baseAssign = Jimple.v().newAssignStmt(baseLocal, newBase);
			if(redirect == true)
				units.insertBefore(baseAssign, insertBefore);
			else
				units.insertBeforeNoRedirect(baseAssign, insertBefore);
		}
		else if(base instanceof Local)
			baseLocal = (Local) base;
		else
			throw new RuntimeException("InstanceFieldRef cannot be reconstructed because it's base is of an unsupported type" + base.getType() + ": " + base);
		
		InstanceFieldRef newLock = Jimple.v().newInstanceFieldRef(baseLocal, lock.getField().makeRef());
		G.v().out.println("  as " + newLock);
		return newLock;
	}
	
	static int lockNumber = 0;
	static Map<EquivalentValue, StaticFieldRef> lockEqValToLock = new HashMap<EquivalentValue, StaticFieldRef>();
	static public Value getLockFor(EquivalentValue lockEqVal)
	{
		Value lock = lockEqVal.getValue();
		
		if( lock instanceof InstanceFieldRef )
			return lock;
			
		if( lock instanceof ArrayRef ) // it would be better to lock the array ref for each value of the index!
			return ((ArrayRef) lock).getBase();
		
		if( lock instanceof Local )
			return lock;
			
		if( lock instanceof StaticFieldRef || lock instanceof NewStaticLock)
		{
			if( lockEqValToLock.containsKey(lockEqVal) )
				return lockEqValToLock.get(lockEqVal);
			
			SootClass lockClass = null;
			if( lock instanceof StaticFieldRef )
			{
				StaticFieldRef sfrLock = (StaticFieldRef) lock;
				lockClass = sfrLock.getField().getDeclaringClass();
			}
			else if( lock instanceof NewStaticLock )
			{
				DeadlockAvoidanceEdge dae = (DeadlockAvoidanceEdge) lock;
				lockClass = dae.getLockClass();
			}
        	SootMethod clinitMethod = null;
        	JimpleBody clinitBody = null;
        	Stmt firstStmt = null;
			boolean addingNewClinit = !lockClass.declaresMethod("void <clinit>()");
			if(addingNewClinit)
        	{
        		clinitMethod = new SootMethod("<clinit>", new ArrayList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
        		clinitBody = Jimple.v().newBody(clinitMethod);
        		clinitMethod.setActiveBody(clinitBody);
        		lockClass.addMethod(clinitMethod);
        	}
        	else
        	{
				clinitMethod = lockClass.getMethod("void <clinit>()");
				clinitBody = (JimpleBody) clinitMethod.getActiveBody();
				firstStmt = clinitBody.getFirstNonIdentityStmt();
        	}
        	PatchingChain clinitUnits = clinitBody.getUnits();
        	
			Local lockLocal = Jimple.v().newLocal("objectLockLocal" + lockNumber, RefType.v("java.lang.Object"));
			// lockNumber is increased below
			clinitBody.getLocals().add(lockLocal); // TODO: add name conflict avoidance code
		            
            // assign new object to lock obj
            Stmt newStmt = Jimple.v().newAssignStmt(lockLocal, Jimple.v().newNewExpr(RefType.v("java.lang.Object")));
			if(addingNewClinit)
				clinitUnits.add(newStmt);
			else
				clinitUnits.insertBeforeNoRedirect(newStmt, firstStmt);

			// initialize new object
            SootClass objectClass = Scene.v().loadClassAndSupport("java.lang.Object");
            RefType type = RefType.v(objectClass);
            SootMethod initMethod = objectClass.getMethod("void <init>()");
            Stmt initStmt = Jimple.v().newInvokeStmt(
            				Jimple.v().newSpecialInvokeExpr(lockLocal, 
            					initMethod.makeRef(), Collections.EMPTY_LIST));
            if(addingNewClinit)
	            clinitUnits.add(initStmt);
	        else
	        	clinitUnits.insertBeforeNoRedirect(initStmt, firstStmt);
			        
	        // copy new object to global static lock object (for use by other fns)
        	SootField actualLockObject = new SootField("objectLockGlobal" + lockNumber, RefType.v("java.lang.Object"), Modifier.STATIC | Modifier.PUBLIC);
			lockNumber++;
        	lockClass.addField(actualLockObject);

			StaticFieldRef actualLockSfr = Jimple.v().newStaticFieldRef(actualLockObject.makeRef());
	        Stmt assignStmt = Jimple.v().newAssignStmt(actualLockSfr, lockLocal);
        	if(addingNewClinit)
	        	clinitUnits.add(assignStmt);
	        else
	        	clinitUnits.insertBeforeNoRedirect(assignStmt, firstStmt);

    		if(addingNewClinit)
	    		clinitUnits.add(Jimple.v().newReturnVoidStmt());
	    		
	    	lockEqValToLock.put(lockEqVal, actualLockSfr);
	    	return actualLockSfr;
		}

		throw new RuntimeException("Unknown type of lock (" + lock + "): expected FieldRef, ArrayRef, or Local");
	}
	
/*
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
*/

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
