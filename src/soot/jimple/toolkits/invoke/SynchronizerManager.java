/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.invoke;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/** Utility methods for dealing with synchronization. */
public class SynchronizerManager
{
    public SynchronizerManager( Singletons.Global g ) {}
    public static SynchronizerManager v() { return G.v().soot_jimple_toolkits_invoke_SynchronizerManager(); }
    /** Maps classes to class$ fields.  Don't trust default. */
    public HashMap<SootClass, SootField> classToClassField = new HashMap<SootClass, SootField>();

    /** Adds code to fetch the static Class object to the given JimpleBody
     * before the target Stmt.
     *
     * Uses our custom classToClassField field to cache the results.
     *
     * The code will look like this:
     *
<pre>
        $r3 = <quack: java.lang.Class class$quack>;
        .if $r3 != .null .goto label2;

        $r3 = .staticinvoke <quack: java.lang.Class class$(java.lang.String)>("quack");
        <quack: java.lang.Class class$quack> = $r3;

     label2:
</pre>
     */
    public Local addStmtsToFetchClassBefore(JimpleBody jb, Stmt target)
    {
        SootClass sc = jb.getMethod().getDeclaringClass();
        SootField classCacher = classToClassField.get(sc);
        if (classCacher == null)
        {
            // Add a unique field named [__]class$name
            String n = "class$"+sc.getName().replace('.', '$');
            while (sc.declaresFieldByName(n))
                n = "_" + n;

            classCacher = new SootField(n, RefType.v("java.lang.Class"), Modifier.STATIC);
            sc.addField(classCacher);
            classToClassField.put(sc, classCacher);
        }

        String lName = "$uniqueClass";

        // Find unique name.  Not strictly necessary unless we parse Jimple code.
        while (true)
        {
            Iterator it = jb.getLocals().iterator();
            boolean oops = false;
            while (it.hasNext())
            {
                Local jbLocal = (Local)it.next();
                if (jbLocal.getName().equals(lName))
                    oops = true;
            }
            if (!oops)
                break;
            lName = "_" + lName;
        }

        Local l = Jimple.v().newLocal(lName, RefType.v("java.lang.Class"));
        jb.getLocals().add(l);
        Chain units = jb.getUnits();
        units.insertBefore(Jimple.v().newAssignStmt(l, 
                                  Jimple.v().newStaticFieldRef(classCacher.makeRef())), 
                           target);

        IfStmt ifStmt;
        units.insertBefore(ifStmt = Jimple.v().newIfStmt
                           (Jimple.v().newNeExpr(l, NullConstant.v()),
                            target), target);

        units.insertBefore(Jimple.v().newAssignStmt
            (l, Jimple.v().newStaticInvokeExpr(getClassFetcherFor(sc).makeRef(),
            Arrays.asList(new Value[] {StringConstant.v(sc.getName())}))),
                           target);
        units.insertBefore(Jimple.v().newAssignStmt
                           (Jimple.v().newStaticFieldRef(classCacher.makeRef()),
                            l), target);

        ifStmt.setTarget(target);
        return l;
    }

    /** Finds a method which calls java.lang.Class.forName(String).
     * Searches for names class$, _class$, __class$, etc. 
     * If no such method is found, creates one and returns it.
     *
     * Uses dumb matching to do search.  Not worth doing symbolic
     * analysis for this! */
    public SootMethod getClassFetcherFor(SootClass c)
    {
        String methodName = "class$";
        for ( ; true; methodName = "_" + methodName)
        {
        	SootMethod m = c.getMethodByNameUnsafe(methodName);
            if (m == null)
                return createClassFetcherFor(c, methodName);
            
            // Check signature.
            if (!m.getSignature().equals
                     ("<"+c.getName().replace('.', '$')+": java.lang.Class "+
                      methodName+"(java.lang.String)>"))
                continue;

            Body b = null;
            b = m.retrieveActiveBody();

            Iterator unitsIt = b.getUnits().iterator();
            
            /* we now look for the following fragment: */
            /*    r0 := @parameter0: java.lang.String;
             *    $r2 = .staticinvoke <java.lang.Class: java.lang.Class forName(java.lang.String)>(r0);
             *    .return $r2; 
             *
             * Ignore the catching code; this is enough. */

            if (!unitsIt.hasNext())
                continue;

            Stmt s = (Stmt)unitsIt.next();
            if (!(s instanceof IdentityStmt))
                continue;

            IdentityStmt is = (IdentityStmt)s;
            Value lo = is.getLeftOp(), ro = is.getRightOp();

            if (!(ro instanceof ParameterRef))
                continue;

            ParameterRef pr = (ParameterRef)ro;
            if (pr.getIndex() != 0)
                continue;

            if (!unitsIt.hasNext())
                continue;

            s = (Stmt)unitsIt.next();
            if (!(s instanceof AssignStmt))
                continue;

            AssignStmt as = (AssignStmt)s;
            Value retVal = as.getLeftOp(), ie = as.getRightOp();

            if (!ie.toString().equals(".staticinvoke <java.lang.Class: java.lang.Class forName(java.lang.String)>("+lo+")"))
                continue;
           
            if (!unitsIt.hasNext())
                continue;

            s = (Stmt)unitsIt.next();
            if (!(s instanceof ReturnStmt))
                continue;

            ReturnStmt rs = (ReturnStmt) s;
            if (!rs.getOp().equivTo(retVal))
                continue;

            /* don't care about rest.  we have sufficient code. */
            /* in particular, it certainly returns Class.forName(arg). */
            return m;
        }
    }

    /** Creates a method which calls java.lang.Class.forName(String). 
     *
     * The method should look like the following:
<pre>
         .static java.lang.Class class$(java.lang.String)
         {
             java.lang.String r0, $r5;
             java.lang.ClassNotFoundException r1, $r3;
             java.lang.Class $r2;
             java.lang.NoClassDefFoundError $r4;

             r0 := @parameter0: java.lang.String;

         label0:
             $r2 = .staticinvoke <java.lang.Class: java.lang.Class forName(java.lang.String)>(r0);
             .return $r2;

         label1:
             $r3 := @caughtexception;
             r1 = $r3;
             $r4 = .new java.lang.NoClassDefFoundError;
             $r5 = .virtualinvoke r1.<java.lang.Throwable: java.lang.String getMessage()>();
             .specialinvoke $r4.<java.lang.NoClassDefFoundError: .void <init>(java.lang.String)>($r5);
             .throw $r4;

             .catch java.lang.ClassNotFoundException .from label0 .to label1 .with label1;
         }
</pre>
    */
    public SootMethod createClassFetcherFor(SootClass c, 
                                                   String methodName)
    {
        // Create the method
            SootMethod method = new SootMethod(methodName, 
                Arrays.asList(new Type[] {RefType.v("java.lang.String")}),
                RefType.v("java.lang.Class"), Modifier.STATIC);
        
           c.addMethod(method);
           
        // Create the method body
        {    
            JimpleBody body = Jimple.v().newBody(method);
            
            method.setActiveBody(body);
            Chain units = body.getUnits();
            Local l_r0, l_r1, l_r2, l_r3, l_r4, l_r5;
            
            // Add some locals
                l_r0 = Jimple.v().newLocal
                    ("r0", RefType.v("java.lang.String"));
                l_r1 = Jimple.v().newLocal
                    ("r1", RefType.v("java.lang.ClassNotFoundException"));
                l_r2 = Jimple.v().newLocal
                    ("$r2", RefType.v("java.lang.Class"));
                l_r3 = Jimple.v().newLocal
                    ("$r3", RefType.v("java.lang.ClassNotFoundException"));
                l_r4 = Jimple.v().newLocal
                    ("$r4", RefType.v("java.lang.NoClassDefFoundError"));
                l_r5 = Jimple.v().newLocal
                    ("$r5", RefType.v("java.lang.String"));

                body.getLocals().add(l_r0);
                body.getLocals().add(l_r1);
                body.getLocals().add(l_r2);
                body.getLocals().add(l_r3);
                body.getLocals().add(l_r4);
                body.getLocals().add(l_r5);

            // add "r0 := @parameter0: java.lang.String"
                units.add(Jimple.v().newIdentityStmt(l_r0, 
                      Jimple.v().newParameterRef
                        (RefType.v("java.lang.String"), 0)));
            
            // add "$r2 = .staticinvoke <java.lang.Class: java.lang.Class forName(java.lang.String)>(r0); 
                AssignStmt asi;
                units.add(asi = Jimple.v().newAssignStmt(l_r2, 
                    Jimple.v().newStaticInvokeExpr(
                          Scene.v().getMethod(
                               "<java.lang.Class: java.lang.Class"+
                               " forName(java.lang.String)>").makeRef(), 
                          Arrays.asList(new Value[] {l_r0}))));

            // insert "return $r2;"
                units.add(Jimple.v().newReturnStmt(l_r2));

            // add "r3 := @caughtexception;"
                Stmt handlerStart;
                units.add(handlerStart = Jimple.v().newIdentityStmt(l_r3, 
                        Jimple.v().newCaughtExceptionRef()));

            // add "r1 = r3;"
                units.add(Jimple.v().newAssignStmt(l_r1, l_r3));
                            
            // add "$r4 = .new java.lang.NoClassDefFoundError;"
                units.add(Jimple.v().newAssignStmt(l_r4,
                    Jimple.v().newNewExpr(RefType.v
                              ("java.lang.NoClassDefFoundError"))));

            // add "$r5 = virtualinvoke r1.<java.lang.Throwable: java.lang.String getMessage()>();"
                units.add(Jimple.v().newAssignStmt(l_r5,
                    Jimple.v().newVirtualInvokeExpr(l_r1,
                          Scene.v().getMethod("<java.lang.Throwable: java.lang.String getMessage()>").makeRef(),
                          new LinkedList())));

            // add .specialinvoke $r4.<java.lang.NoClassDefFoundError: .void <init>(java.lang.String)>($r5);
                units.add(Jimple.v().newInvokeStmt(
                     Jimple.v().newSpecialInvokeExpr(l_r4,
                          Scene.v().getMethod(
                               "<java.lang.NoClassDefFoundError: void"+
                               " <init>(java.lang.String)>").makeRef(), 
                          Arrays.asList(new Value[] {l_r5}))));

            // add .throw $r4;
                units.add(Jimple.v().newThrowStmt(l_r4));

            body.getTraps().add(Jimple.v().newTrap
                  (Scene.v().getSootClass("java.lang.ClassNotFoundException"), 
                    asi, handlerStart, handlerStart));
        }

        return method;
    }

    /** Wraps stmt around a monitor associated with local lock. 
     * When inlining or static method binding, this is the former
     * base of the invoke expression. */
    public void synchronizeStmtOn(Stmt stmt, JimpleBody b, Local lock)
    {
        Chain units = b.getUnits();

//          TrapManager.splitTrapsAgainst(b, stmt, (Stmt)units.getSuccOf(stmt));

        units.insertBefore(Jimple.v().newEnterMonitorStmt(lock), stmt);

        Stmt exitMon = Jimple.v().newExitMonitorStmt(lock);
        units.insertAfter(exitMon, stmt);

        // Ok.  That was the easy part.
        // We also need to modify exception blocks to exit the monitor
        //   (they have conveniently been pre-split)
        // Actually, we don't need to do this.
//          {
//              List traps = TrapManager.getTrapsAt(stmt, b);
//              Iterator trapsIt = traps.iterator();

//              while (trapsIt.hasNext())
//              {
//                  Trap t = (Trap)trapsIt.next();

//                  Stmt s = (Stmt)units.getLast();
//                  Stmt newCaughtRef = (Stmt)t.getHandlerUnit().clone();

//                  List l = new ArrayList();

//                  l.add(newCaughtRef);
//                  l.add(exitMon.clone());
//                  l.add(Jimple.v().newGotoStmt((Stmt)units.getSuccOf((Stmt)t.getHandlerUnit())));

//                  units.insertAfter(l, s);
//                  t.setHandlerUnit(newCaughtRef);
//              }
//          }

        // and also we must add a catch Throwable exception block in the appropriate place.
        {
            Stmt newGoto = Jimple.v().newGotoStmt((Stmt)units.getSuccOf(exitMon));
            units.insertAfter(newGoto, exitMon);

            List<Unit> l = new ArrayList<Unit>();
            Local eRef = Jimple.v().newLocal("__exception", RefType.v("java.lang.Throwable"));
            b.getLocals().add(eRef);
            Stmt handlerStmt = Jimple.v().newIdentityStmt(eRef, Jimple.v().newCaughtExceptionRef());
            l.add(handlerStmt);
            l.add((Stmt) exitMon.clone());
            l.add(Jimple.v().newThrowStmt(eRef));
            units.insertAfter(l, newGoto);

            Trap newTrap = Jimple.v().newTrap(Scene.v().getSootClass("java.lang.Throwable"), 
                                              stmt, (Stmt)units.getSuccOf(stmt),
                                              handlerStmt);
            b.getTraps().addFirst(newTrap);
        }
    }
}
