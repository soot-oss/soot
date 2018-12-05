package soot.asm.backend;

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

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import soot.G;
import soot.Main;

/**
 * Test for the correct determination of the required Java version
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class MinimalJavaVersionTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testMinimalVersionAnnotation() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Enforced Java version 1.3 too low to support required features (1.5 required)");
		runSoot("soot.asm.backend.targets.AnnotatedClass", "1.3");

	}

	@Test
	public void testSufficientUserVersion() {
		try {
			runSoot("soot.asm.backend.targets.AnnotatedClass", "1.7");
			return;
		} catch (RuntimeException e) {
			fail("Version 1.7 should be sufficient for features of pkg.AnnotatedClass!");
		}
	}

	/**
	 * Returns the folder that is to be added to the class path for running soot
	 * 
	 * @return The location of the folder containing the test files
	 */
	protected String getClassPathFolder() {
		File f = new File("./target/test-classes");
		return f.getAbsolutePath();
	}

	protected void runSoot(String className, String version) {
		G.reset();
		// Location of the rt.jar
		String rtJar = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";

		// Run Soot and print output to .asm-files.
		Main.v().run(new String[] { "-cp", getClassPathFolder() + File.pathSeparator + rtJar, "-src-prec", "only-class",
				"-output-format", "asm", "-allow-phantom-refs", "-java-version", version, className });
	}

}
