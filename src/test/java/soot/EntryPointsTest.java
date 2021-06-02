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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

/**
 * Tests the EntryPoints class
 * 
 * @author Linghui Luo
 */
public class EntryPointsTest {

	@Test
	public void testClinitOf() {
		Path cp = Paths.get("src", "test", "resources", "Clinit", "bin");
		G.reset();
		Options.v().set_prepend_classpath(true);
		Options.v().set_process_dir(Collections.singletonList(cp.toFile().getAbsolutePath()));
		Options.v().set_src_prec(Options.src_prec_class);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_ignore_resolving_levels(true);
		Options.v().setPhaseOption("cg.spark", "on");
		Options.v().setPhaseOption("cg.spark", "string-constants:true");
		Options.v().set_whole_program(true);
		Scene.v().loadNecessaryClasses();
		SootMethod mainMethod = Scene.v().getMainMethod();
		Scene.v().setEntryPoints(Collections.singletonList(mainMethod));
		PackManager.v().getPack("cg").apply();
		CallGraph cg = Scene.v().getCallGraph();
		boolean found = false;
		for (Edge edge : cg) {
			if (edge.getSrc().method().getSignature().equals("<soot.Main: void main(java.lang.String[])>")) {
				if (edge.getTgt().method().getSignature().equals("<soot.A: void <clinit>()>")) { // A1 is used in main
					found = true;
					break;
				}
			}
		}
		assertTrue(found);
		SootClass a1 = Scene.v().getSootClassUnsafe("soot.A1");
		SootClass a = Scene.v().getSootClassUnsafe("soot.A");
		assertTrue(a1 != null);
		List<String> clinits1 = new ArrayList<>();
		EntryPoints.v().clinitsOf(a1).forEach(e -> {
			clinits1.add(e.toString());
		});
		List<String> clinits = new ArrayList<>();
		EntryPoints.v().clinitsOf(a).forEach(e -> {
			clinits.add(e.toString());
		});
		assertEquals(clinits1, clinits);
	}
}
