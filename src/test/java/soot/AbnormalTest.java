/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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
package soot;

import org.junit.Test;
import soot.options.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 * Tests abnormal cases.
 *
 * @author Linghui Luo
 */

public class AbnormalTest {

    public void setup() {
        Path cp = Paths.get("src", "test", "resources", "AbnormalClass");
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_process_dir(Collections.singletonList(cp.toFile().getAbsolutePath()));
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_drop_bodies_after_load(false);
    }

    @Test
    public void testMethodWithNoInstruction() {
        setup();
        Options.v().set_output_format(Options.output_format_jimple);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_grimp);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_baf);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_dava);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_shimp);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_class);
        runTest();
    }

    public void runTest() {
        Scene.v().loadNecessaryClasses();
        PackManager.v().runBodyPacks();
        // The method test in class E has no opcode instruction at all.
        // Such method can be created by a bytecode editor.
        SootMethod method = Scene.v().getSootClass("E").getMethodByName("test");
        assertTrue(!method.hasActiveBody());
        assertTrue(method.isConcrete());
    }
}