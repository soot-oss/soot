package soot.dava.toolkits.base.misc;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;

public class ThrowFinder
{
    public ThrowFinder( Singletons.Global g ) {}
    public static ThrowFinder v() { return G.v().ThrowFinder(); }

    private HashSet registeredMethods;
    private HashMap protectionSet;

    public void find()
    {
	G.v().out.print( "Verifying exception handling.. ");

	registeredMethods = new HashSet();
	protectionSet = new HashMap();

        CallGraph cg = Scene.v().getCallGraph();

	IterableSet worklist = new IterableSet();

	G.v().out.print( "\b. ");
	G.v().out.flush();


	// Get all the methods, and find protection for every statement.
	Iterator classIt = Scene.v().getApplicationClasses().iterator();
	while (classIt.hasNext()) {
	    Iterator methodIt = ((SootClass) classIt.next()).methodIterator();
	    while (methodIt.hasNext()) {
		SootMethod m = (SootMethod) methodIt.next();

		register_AreasOfProtection( m);
		worklist.add( m);
	    }
	}


	// Build the subClass and superClass mappings.
	HashMap 
	    subClassSet = new HashMap(),
	    superClassSet = new HashMap();

	HashSet applicationClasses = new HashSet();
	applicationClasses.addAll( Scene.v().getApplicationClasses());

	classIt = Scene.v().getApplicationClasses().iterator();
	while (classIt.hasNext()) {
	    SootClass c = (SootClass) classIt.next();
	    
	    IterableSet superClasses =  (IterableSet) superClassSet.get( c);
	    if (superClasses == null) {
		superClasses = new IterableSet();
		superClassSet.put( c, superClasses);
	    }

	    IterableSet subClasses = (IterableSet) subClassSet.get( c);
	    if (subClasses == null) {
		subClasses = new IterableSet();
		subClassSet.put( c, subClasses);
	    }

	    if (c.hasSuperclass()) {
		SootClass superClass = c.getSuperclass();

		IterableSet superClassSubClasses = (IterableSet) subClassSet.get( superClass);
		if (superClassSubClasses == null) {
		    superClassSubClasses = new IterableSet();
		    subClassSet.put( superClass, superClassSubClasses);
		}
		superClassSubClasses.add( c);
		superClasses.add( superClass);
	    }

	    Iterator interfaceIt = c.getInterfaces().iterator();
	    while (interfaceIt.hasNext()) {
		SootClass interfaceClass = (SootClass) interfaceIt.next();

		IterableSet interfaceClassSubClasses = (IterableSet) subClassSet.get( interfaceClass);
		if (interfaceClassSubClasses == null) {
		    interfaceClassSubClasses = new IterableSet();
		    subClassSet.put( interfaceClass, interfaceClassSubClasses);
		}
		interfaceClassSubClasses.add( c);
		superClasses.add( interfaceClass);
	    }
	}
	
	// Build the subMethod and superMethod mappings.
	HashMap agreementMethodSet = new HashMap();

	// Get exceptions from throw statements and add them to the exceptions that the method throws.
	Iterator worklistIt = worklist.iterator();
	while (worklistIt.hasNext()) {
	    SootMethod m = (SootMethod) worklistIt.next();

	    if (m.isAbstract() == false) {

		List exceptionList = m.getExceptions();
		IterableSet exceptionSet = new IterableSet( exceptionList);
		boolean changed = false;
		
		Iterator it = m.retrieveActiveBody().getUnits().iterator();
		while (it.hasNext()) {
		    Unit u = (Unit) it.next();
		    HashSet handled = (HashSet) protectionSet.get(u);
		    
		    if (u instanceof ThrowStmt) {
			Type t = ((ThrowStmt) u).getOp().getType();
			
			if (t instanceof RefType) {
			    SootClass c = ((RefType) t).getSootClass();
			    
			    if ((handled_Exception( handled, c) == false) && (exceptionSet.contains( c) == false)) {
				exceptionSet.add( c);
				changed = true;
			    }
			}
		    }
		}

		it = cg.targetsOf(m);
		while (it.hasNext()) {
                    Edge e = (Edge) it.next();
		    Stmt callSite = e.srcStmt();
                    if( callSite == null ) continue;
		    HashSet handled = (HashSet) protectionSet.get( callSite);

                    SootMethod target = e.tgt();

                    Iterator exceptionIt = target.getExceptions().iterator();
                    while (exceptionIt.hasNext()) {
                        SootClass exception = (SootClass) exceptionIt.next();

                        if ((handled_Exception( handled, exception) == false) && (exceptionSet.contains( exception) == false)) {
                            exceptionSet.add( exception);
                            changed = true;
                        }
                    }
		}
		
		if (changed) {
		    exceptionList.clear();
		    exceptionList.addAll( exceptionSet);
		}
	    }

	    // While we're at it, put the superMethods and the subMethods in the agreementMethodSet.
	    find_OtherMethods( m, agreementMethodSet, subClassSet, applicationClasses);
	    find_OtherMethods( m, agreementMethodSet, superClassSet, applicationClasses);
	}

	// Perform worklist algorithm to propegate the throws information.
	while (worklist.isEmpty() == false) {

	    SootMethod m = (SootMethod) worklist.getFirst();
	    worklist.removeFirst();

	    IterableSet agreementMethods = (IterableSet) agreementMethodSet.get( m);
	    if (agreementMethods != null) {
		Iterator amit = agreementMethods.iterator();
		while (amit.hasNext()) {
		    SootMethod otherMethod = (SootMethod) amit.next();

		    List otherExceptionsList = otherMethod.getExceptions();
		    IterableSet otherExceptionSet = new IterableSet( otherExceptionsList);
		    boolean changed = false;		    

		    Iterator exceptionIt = m.getExceptions().iterator();
		    while (exceptionIt.hasNext()) {
			SootClass exception = (SootClass) exceptionIt.next();

			if (otherExceptionSet.contains( exception) == false) {
			    otherExceptionSet.add( exception);
			    changed = true;
			}
		    }

		    if (changed) {
			otherExceptionsList.clear();
			otherExceptionsList.addAll( otherExceptionSet);

			if (worklist.contains( otherMethod) == false)
			    worklist.addLast( otherMethod);
		    }
		}
	    }

            Iterator it = cg.targetsOf(m);
            while (it.hasNext()) {
                Edge e = (Edge) it.next();
		Stmt callingSite = e.srcStmt();
                if( callingSite == null ) continue;
		SootMethod callingMethod = e.src();
		List exceptionList = callingMethod.getExceptions();
		IterableSet exceptionSet = new IterableSet( exceptionList);
		HashSet handled = (HashSet) protectionSet.get( callingSite);
		boolean changed = false;

		Iterator exceptionIt = m.getExceptions().iterator();
		while (exceptionIt.hasNext()) {
		    SootClass exception = (SootClass) exceptionIt.next();

		    if ((handled_Exception( handled, exception) == false) && (exceptionSet.contains( exception) == false)) {
			exceptionSet.add( exception);
			changed = true;
		    }
		}
		
		if (changed) {
		    exceptionList.clear();
		    exceptionList.addAll( exceptionSet);

		    if (worklist.contains( callingMethod) == false)
			worklist.addLast( callingMethod);
		}
	    }
	}

	G.v().out.println();
	G.v().out.flush();
    }


    private void find_OtherMethods( SootMethod startingMethod, HashMap methodMapping, HashMap classMapping, HashSet applicationClasses)
    {
	IterableSet worklist = (IterableSet) ((IterableSet) classMapping.get( startingMethod.getDeclaringClass())).clone();

	HashSet touchSet = new HashSet();
	touchSet.addAll( worklist);

	String signature = startingMethod.getSubSignature();

	while (worklist.isEmpty() == false) {
	    SootClass currentClass = (SootClass) worklist.getFirst();
	    worklist.removeFirst();

	    if (applicationClasses.contains( currentClass) == false)
		continue;

	    if (currentClass.declaresMethod( signature)) {
		IterableSet otherMethods = (IterableSet) methodMapping.get( startingMethod);
		if (otherMethods == null) {
		    otherMethods = new IterableSet();
		    methodMapping.put( startingMethod, otherMethods);
		}

		otherMethods.add( currentClass.getMethod( signature));
	    }

	    else {
		IterableSet otherClasses = (IterableSet) classMapping.get( currentClass);
		if (otherClasses != null) {
		    Iterator ocit = otherClasses.iterator();
		    while (ocit.hasNext()) {
			SootClass otherClass = (SootClass) ocit.next();

			if (touchSet.contains( otherClass) == false) {
			    worklist.addLast( otherClass);
			    touchSet.add( otherClass);
			}
		    }
		}
	    }
	}
    }

    private void register_AreasOfProtection( SootMethod m)
    {
	if (registeredMethods.contains( m))
	    return;

	registeredMethods.add( m);

	if (m.hasActiveBody() == false)
	    return;

	Body b = (Body) m.getActiveBody();
	Chain stmts = b.getUnits();

	Iterator trapIt = b.getTraps().iterator();
	while (trapIt.hasNext()) {
	    Trap t = (Trap) trapIt.next();
	    SootClass exception = t.getException();

	    Iterator sit = stmts.iterator( t.getBeginUnit(), stmts.getPredOf( t.getEndUnit()));
	    while (sit.hasNext()) {
		Stmt s = (Stmt) sit.next();

		HashSet handled = null;
		if ((handled = (HashSet) protectionSet.get( s)) == null) {
		    handled = new HashSet();
		    protectionSet.put( s, handled);
		}
		
		if (handled.contains( exception) == false)
		    handled.add( exception);
	    }
	}
    }

    private boolean handled_Exception( HashSet handledExceptions, SootClass c)
    {
	SootClass thrownException = c;

	if (is_HandledByRuntime( thrownException))
	    return true;

	if (handledExceptions == null)
	    return false;

	while (true) {
	    if (handledExceptions.contains( thrownException))
		return true;
	    
	    if (thrownException.hasSuperclass() == false)
		return false;

	    thrownException = thrownException.getSuperclass();
	}
    }

    private boolean is_HandledByRuntime( SootClass c)
    {
	SootClass 
	    thrownException = c,
	    runtimeException = Scene.v().getSootClass( "java.lang.RuntimeException"),
	    error = Scene.v().getSootClass( "java.lang.Error");
	
	while (true) {
	    if ((thrownException == runtimeException) || (thrownException == error))
		return true;
	    
	    if (thrownException.hasSuperclass() == false)
		return false;

	    thrownException = thrownException.getSuperclass();
	}
    }
}
