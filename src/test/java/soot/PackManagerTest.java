package soot;
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

import com.google.common.io.Files;
import org.junit.Test;
import soot.options.Options;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Some tests to disable jb transformers.
 *
 * @author Linghui Luo
 */
public class PackManagerTest {

    public void setup() {
        Path cp = Paths.get("src", "test", "resources", "SimpleClass");
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_process_dir(Collections.singletonList(cp.toFile().getAbsolutePath()));
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_output_format(Options.output_format_jimple);
    }

    @Test
    public void testDisableCopyPropagatorInJBPhase() {
        {
            // default CopyPropagator enabled
            setup();
            Scene.v().loadNecessaryClasses();
            PackManager.v().runBodyPacks();
            SootClass cls = Scene.v().getSootClass("Example");
            SootMethod foo = cls.getMethodByName("foo");
            List<String> actual = bodyAsStrings(foo.getActiveBody());
            List<String> expected = expectedBody("r0 := @this: Example",
                    "virtualinvoke r0.<Example: void bar(int,int)>(0, 2)",
                    "return");
            assertEquals(expected, actual);
        }
        {
            // disable CopyPropagator
            setup();
            Options.v().setPhaseOption("jb.sils", "enabled:false");// this transformer calls a lot of other transformers
            Options.v().setPhaseOption("jb.cp", "enabled:false");
            Scene.v().loadNecessaryClasses();
            PackManager.v().runBodyPacks();
            SootClass cls = Scene.v().getSootClass("Example");
            SootMethod foo = cls.getMethodByName("foo");
            List<String> actual = bodyAsStrings(foo.getActiveBody());
            List<String> expected = expectedBody("r0 := @this: Example",
                    "b0 = 0",
                    "b1 = 2",
                    "virtualinvoke r0.<Example: void bar(int,int)>(b0, b1)",
                    "return");
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testDisableUnusedLocalEliminatorInJBPhase() {
        {
            // default UnusedLocalEliminator enabled
            setup();
            Scene.v().loadNecessaryClasses();
            PackManager.v().runBodyPacks();
            SootClass cls = Scene.v().getSootClass("Example");
            SootMethod bar = cls.getMethodByName("bar");
            List<String> actual = bodyAsStrings(bar.getActiveBody());
            List<String> expected = expectedBody("r1 := @this: Example",
                    "i0 := @parameter0: int",
                    "i1 := @parameter1: int",
                    "i2 = i0 * i1",
                    "$r0 = <java.lang.System: java.io.PrintStream out>",
                    "virtualinvoke $r0.<java.io.PrintStream: void println(int)>(i2)",
                    "return");
        }
        {
            //disable UnusedLocalEliminator
            setup();
            Options.v().setPhaseOption("jb.sils", "enabled:false");// this transformer calls a lot of other transformers
            Options.v().setPhaseOption("jb.cp-ule", "enabled:false");
            Scene.v().loadNecessaryClasses();
            PackManager.v().runBodyPacks();
            SootClass cls = Scene.v().getSootClass("Example");
            SootMethod bar = cls.getMethodByName("bar");
            List<String> actual = bodyAsStrings(bar.getActiveBody());
            List<String> expected = expectedBody("r1 := @this: Example",
                    "i0 := @parameter0: int",
                    "i1 := @parameter1: int",
                    "i2 = i0 * i1",
                    "z0 = 0",
                    "$r0 = <java.lang.System: java.io.PrintStream out>",
                    "virtualinvoke $r0.<java.io.PrintStream: void println(int)>(i2)",
                    "return");
        }
    }

    @Test
    public void testWritingToJar() throws Exception {
        setup();
        File tempDir = Files.createTempDir();
        Options.v().set_output_jar(true);
        Options.v().set_output_dir(tempDir.getAbsolutePath());
        Options.v().set_output_format(Options.output_format_dex);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runBodyPacks();
        PackManager.v().writeOutput();
        assertEquals(1, tempDir.listFiles().length);
        File targetJar = tempDir.listFiles()[0];
        ZipFile jarRead = new ZipFile(targetJar.getAbsolutePath());
        Enumeration<? extends ZipEntry> entries = jarRead.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().toLowerCase().endsWith("manifest.mf")) {
                assertTrue(entry.getSize() > 0);
                return;
            }
        }
        fail("No Manifest entry found in " + targetJar.getAbsolutePath());
    }

    public static List<String> expectedBody(String... jimpleLines) {
        return Stream.of(jimpleLines).collect(Collectors.toList());
    }

    public static List<String> bodyAsStrings(Body body) {
        List<String> units = new ArrayList<>();
        for (Unit u : body.getUnits()) {
            units.add(u.toString());
        }
        return units;
    }
}
