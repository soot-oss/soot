package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2020 Raja Vallee-Rai and others
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
import soot.*;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.internal.JGotoStmt;
import soot.util.Chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EmptySwitchEliminatorTest {

    /**
     * The test is ported from https://github.com/soot-oss/SootUp/blob/develop/sootup.java.bytecode/src/test/java/sootup/java/bytecode/interceptors/EmptySwitchEliminatorTest.java
     */
    @Test
    public void testEmptySwitch() {
        // build test method and body
        SootClass cl = new SootClass("TestClass", Modifier.PUBLIC);
        SootMethod method = new SootMethod("test", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC);
        cl.addMethod(method);
        JimpleBody body = Jimple.v().newBody(method);
        method.setActiveBody(body);

        // build locals
        Local l0 = Jimple.v().newLocal("l0", IntType.v());
        Local l1 = Jimple.v().newLocal("l1", IntType.v());
        Local l2 = Jimple.v().newLocal("l2", IntType.v());
        Chain<Local> locals = body.getLocals();
        locals.add(l0);
        locals.add(l1);
        locals.add(l2);

        // build statements
        Unit startingStmt = Jimple.v().newIdentityStmt(l0, Jimple.v().newThisRef(method.getDeclaringClass().getType()));
        Unit stmt1 = Jimple.v().newAssignStmt(l1, IntConstant.v(3));
        Unit defaultStmt = Jimple.v().newAssignStmt(l2, IntConstant.v(0));
        LookupSwitchStmt sw = Jimple.v().newLookupSwitchStmt(l1, new ArrayList<>(), new ArrayList<>(), defaultStmt);
        Unit ret = Jimple.v().newReturnVoidStmt();

        /*
            l0 := @this: TestClass
            l1 = 3
            lookupswitch(l1) {
                default:
                    goto l2 = 0;
            }
            return
         */
        UnitPatchingChain units = body.getUnits();
        units.add(startingStmt);
        units.add(stmt1);
        units.add(sw);
        units.add(ret);

        // execute transform
        EmptySwitchEliminator.v().internalTransform(body, "testPhase", Collections.emptyMap());

        // check resulting code (switch should be removed)
        /*
            Expected:
                l0 := @this: TestClass
                l1 = 3
                goto [?= l2 = 0]
                return
        */
        Iterator<Unit> it = units.iterator();
        assertEquals(startingStmt, it.next());
        assertEquals(stmt1, it.next());
        Unit stmt3 = it.next();
        assertTrue(stmt3 instanceof JGotoStmt);
        assertEquals(defaultStmt, ((JGotoStmt) stmt3).getTargetBox().getUnit());
        assertEquals(ret, it.next());
    }
}