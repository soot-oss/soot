/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dava.toolkits.base.misc;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.jimple.internal.JExitMonitorStmt;
import soot.jimple.toolkits.callgraph.*;

/*
 * Nomair A. Naeem 7th April 2006
 * This class detects and propagates whether the signature of a method should have some throws Exception constructs.
 * 
 * The reason we need to do this is since the JVM does not force all compilers to store throws information
 * as attributes (javac does it ) but other compilers are not forced to do it
 * 
 * Hence if we are coming from javac we dont need to perform this analysis since we already have the information
 * if we are not coming from javac then we need to perform this analysis to say what the checked exceptions are for this method.
 * 
 * Alls good until u try to decompile code like this
 *   try{
 *    synchronized(bla){
 *      bla
 *      bla
 *    } 
 *   }catch(InterruptedException e){
 *       bla
 *   }
 *   
 *   If you create bytecode for this you will notice that because exitmointer has to be invoked if an exception occurs
 *   this is done by catching a Throwable(all possible exceptions) exiting the monitor and rethrowing the exception.
 *   
 *   Now that is alright the problem occurs because InterruptedExceptions will be caught but since we are throwing the 
 *   general Throwable exception this algorithm says that the method should state in its signature that it throws
 *   java.lang,Throwable.
 *   CHANGE LOG: current fix is to hack into the algo find the place where we are about to add the java.lang.Throwable
 *   and if it is near an exit monitor we know dava is going to convert this to a synch and hence not add this exception!!
 * 
 * 
 */
public class ThrowFinder
{
    public ThrowFinder( Singletons.Global g ) {}
    public static ThrowFinder v() { return G.v().soot_dava_toolkits_base_misc_ThrowFinder(); }

    private HashSet registeredMethods;
    private HashMap protectionSet;

    public static boolean DEBUG=false;
    
    public void find()
    {
	G.v().out.print( "Verifying exception handling.. ");

	registeredMethods = new HashSet();
	protectionSet = new HashMap();

        CallGraph cg;
        if( Scene.v().hasCallGraph() ) {
            cg = Scene.v().getCallGraph();
        } 
        else {
            new CallGraphBuilder().build();
            cg = Scene.v().getCallGraph();
            Scene.v().releaseCallGraph();
        }

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

	    if (!m.isAbstract() && !m.isNative() ) {

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
		    			/*
		    			 * Nomair A Naeem 7th April
		    			 * HACK TRYING TO MATCH PATTERN 
		    			 *  label0:
		    			        r3 = r0;
		    			        entermonitor r0;
 		      			     label1:
 		      			      	r1.up();
 		      			      	r0.wait();
 		      			      	exitmonitor r3;
		    			 	label2:
		    			 		goto label6;
							label3:
								$r5 := @caughtexception;
						 	label4:
						 		r4 = $r5;
						 		exitmonitor r3;
							label5:
								throw r4;    HERE IS THE THROW WE JUST DETECTED LOOK and see if the previous unit is an exitmonitor
                      	 	label6:
                      	 		goto label8;
							label7:
								$r6 := @caughtexception;
								r7 = $r6;
							label8:
								r1.down();
								return;
        				 catch java.lang.Throwable from label1 to label2 with label3;
        				 catch java.lang.Throwable from label4 to label5 with label3;
        				 catch java.lang.InterruptedException from label0 to label6 with label7;
		    			 * 
		    			 */
		    			PatchingChain list = m.retrieveActiveBody().getUnits();
		    			Unit pred = (Unit)list.getPredOf(u);
		    			if(! (pred instanceof JExitMonitorStmt)){
		    				exceptionSet.add( c);
			    			changed = true;
			    			if(DEBUG)
			    				System.out.println("Added exception which is explicitly thrown"+c.getName());
		    			}
		    			else{
		    				if(DEBUG)
		    					System.out.println("Found monitor exit"+pred+" hence not adding");
		    			}
		    		}
		    	}
		    }
		}

		it = cg.edgesOutOf(m);
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

	    Iterator it = cg.edgesOutOf(m);
	    while (it.hasNext()) {
	    	Edge e = (Edge) it.next();
	    	Stmt callingSite = e.srcStmt();
	    	if( callingSite == null ) 
	    		continue;
	    	
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
