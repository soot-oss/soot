package soot.jimple.toolkit.callgraph;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Qidan He
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

import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.PhaseOptions;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.callgraph.Edge;
import soot.testing.framework.AbstractTestingFramework;


@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class VtaCallGraphBaseClassTest {
    private static final String PACKAGE = "soot.jimple.toolkit.callgraph";
    private static final String FOO = String.format("<%s.BaseClassFooCallsBar: void foo()>", PACKAGE);
    private static final String BAR = String.format("<%s.BaseClassFooCallsBar: void bar()>", PACKAGE);
    private static final String MAIN = String.format("<%s.EntryPointCallsSubClassFoo: void main()>", PACKAGE);
    private static final String CLASSES_OR_PACKAGE_TO_ANALYZE = String.format("%s.*", PACKAGE);
    private static final String TARGET_METHOD_SIGNATURE = MAIN;

    private static class TestingFramework extends AbstractTestingFramework {
        private final boolean vta;

        private TestingFramework(final boolean vta) {
            this.vta = vta;
        }

        @Override
        protected void setupSoot() {
            super.setupSoot();
            PhaseOptions.v().setPhaseOption("cg.spark", String.format("vta:%s", vta));
        }

        private void run() {
            prepareTarget(TARGET_METHOD_SIGNATURE, CLASSES_OR_PACKAGE_TO_ANALYZE);
        }
    }

    private Unit callSite(final SootMethod caller, final SootMethod callee) {
        final String calleeSignature = callee.getSignature();
        return caller.getActiveBody().getUnits().stream()
                .filter(unit -> unit.toString().contains(calleeSignature))
                .findFirst()
                .get();
    }

    private List<SootMethod> callTargets(final Unit unit) {
        final Iterable<Edge> iterable = () -> Scene.v().getCallGraph().edgesOutOf(unit);
        return StreamSupport.stream(iterable.spliterator(), false)
                .map(Edge::tgt)
                .collect(Collectors.toList());
    }

    @Test
    public void callTargetsWithoutVta() {
        new TestingFramework(false).run();
        final SootMethod main = Scene.v().getMethod(MAIN);
        final SootMethod foo = Scene.v().getMethod(FOO);
        final SootMethod bar = Scene.v().getMethod(BAR);
        assertEquals("expected target foo", Collections.singletonList(foo), callTargets(callSite(main, foo)));
        assertEquals("expected target bar", Collections.singletonList(bar), callTargets(callSite(foo, bar)));
    }

    @Test
    public void callTargetsWithVta() {
        new TestingFramework(true).run();
        final SootMethod main = Scene.v().getMethod(MAIN);
        final SootMethod foo = Scene.v().getMethod(FOO);
        final SootMethod bar = Scene.v().getMethod(BAR);
        assertEquals("expected target foo", Collections.singletonList(foo), callTargets(callSite(main, foo)));
        assertEquals("expected target bar", Collections.singletonList(bar), callTargets(callSite(foo, bar)));
    }
}
