package soot.jimple.toolkit.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

import java.util.Collections;

import static soot.SootClass.BODIES;
import static soot.SootClass.HIERARCHY;

/**
 * This class contains tests for {@link soot.jimple.toolkits.callgraph.OnFlyCallGraphBuilder.TypeBasedReflectionModel}.
 *
 * @author Manuel Benz
 *         created on 01.08.17
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TypeBasedReflectionModelTest {


    /**
     * Checks if the reflection model accepts a string constant as base of a call to Method.invoke.
     */
    @Test
    public void constantBase() {
        genericLocalVsStringConstantTest(true);
    }

    /**
     * Checks if the reflection model accepts a local as base of a call to Method.invoke.
     */
    @Test
    public void localBase() {
        genericLocalVsStringConstantTest(false);
    }

    @Test
    public void staticBase() {
        //TODO
    }

    private void genericLocalVsStringConstantTest(boolean useConstantBase) {
        resetSoot();

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_whole_program(true);
        Options.v().set_no_bodies_for_excluded(true);
        Scene.v().loadBasicClasses();

        SootClass tc = setUpTestClass(useConstantBase);
        Scene.v().addClass(tc);
        tc.setApplicationClass();
        Scene.v().setMainClass(tc);

        Scene.v().forceResolve(tc.getName(), BODIES);
        Scene.v().loadNecessaryClasses();

        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().setPhaseOption("cg", "types-for-invoke:true");
        // this option is necessary to get constant bases working
        Options.v().setPhaseOption("wjpp.cimbt", "enabled:true");
        Options.v().setPhaseOption("wjpp.cimbt","verbose:true");
        PackManager.v().getPack("wjpp").apply();
        PackManager.v().getPack("cg").apply();

        CallGraph callGraph = Scene.v().getCallGraph();

        boolean found = false;
        for (Edge edge : callGraph) {
            if (edge.getSrc().method().getSignature().contains("main") && edge.getTgt().method().getSignature().contains("concat")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue("There should be an edge from main to String.concat after resolution of the target of Method.invoke, but none found", found);
    }

    private void resetSoot() {
        G.reset();
        Scene.v().releaseCallGraph();
        Scene.v().releasePointsToAnalysis();
        Scene.v().releaseReachableMethods();
        G.v().resetSpark();
    }


    private SootClass setUpTestClass(boolean useConstantBase) {
        SootClass cl = new SootClass("soot.test.ReflectiveBase", Modifier.PUBLIC);

        SootMethod m = new SootMethod("main", Collections.singletonList((Type) ArrayType.v(Scene.v().getType("java.lang.String"), 1)),
                VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
        cl.addMethod(m);
        cl.setApplicationClass();
        cl.setResolvingLevel(HIERARCHY);

        JimpleBody b = Jimple.v().newBody(m);

        // add parameter local
        Local arg = Jimple.v().newLocal("l0", ArrayType.v(RefType.v("java.lang.String"), 1));
        b.getLocals().add(arg);
        b.getUnits().add(Jimple.v().newIdentityStmt(arg,
                Jimple.v().newParameterRef(ArrayType.v
                        (RefType.v("java.lang.String"), 1), 0)));

        // get class
        StaticInvokeExpr getClass = Jimple.v().newStaticInvokeExpr(
                Scene.v().getMethod("<java.lang.Class: java.lang.Class forName(java.lang.String)>").makeRef(),
                StringConstant.v("java.lang.String"));
        Local clz = Jimple.v().newLocal("clz", Scene.v().getType("java.lang.Class"));
        b.getLocals().add(clz);
        b.getUnits().add(Jimple.v().newAssignStmt(clz, getClass));

        // get declared method
        VirtualInvokeExpr concat = Jimple.v().newVirtualInvokeExpr(clz,
                Scene.v().getMethod("<java.lang.Class: java.lang.reflect.Method getDeclaredMethod(java.lang.String,java.lang.Class[])>").makeRef(),
                StringConstant.v("concat"), clz);
        Local method = Jimple.v().newLocal("method", Scene.v().getType("java.lang.reflect.Method"));
        b.getLocals().add(method);
        b.getUnits().add(Jimple.v().newAssignStmt(method, concat));

        // prepare base object for reflective call
        String baseString = "Reflective call base ";
        Value base;
        if (useConstantBase)
            base = StringConstant.v(baseString);
        else {
            Local l = Jimple.v().newLocal("base", Scene.v().getType("java.lang.String"));
            base = l;
            b.getLocals().add(l);
            b.getUnits().add(Jimple.v().newAssignStmt(l,
                    StringConstant.v(baseString)));
        }

        // prepare argument for reflective invocation of concat method
        Local l = Jimple.v().newLocal("arg", Scene.v().getType("java.lang.String"));
        b.getLocals().add(l);
        b.getUnits().add(Jimple.v().newAssignStmt(l, StringConstant.v("concat")));

        // prepare arg array
        Local argArray = Jimple.v().newLocal("argArray", ArrayType.v(Scene.v().getType("java.lang.Object"), 1));
        b.getLocals().add(argArray);
        b.getUnits().add(Jimple.v().newAssignStmt(argArray, Jimple.v().newNewArrayExpr(Scene.v().getType("java.lang.Object"), IntConstant.v(1))));
        b.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newArrayRef(argArray, IntConstant.v(0)), l));

        // invoke reflective method
        b.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(method,
                Scene.v().getMethod("<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>").makeRef(),
                base, argArray)));

        b.getUnits().add(Jimple.v().newReturnVoidStmt());

        b.validate();
        m.setActiveBody(b);

        return cl;
    }
}
