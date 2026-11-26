package soot.dotnet;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

import java.io.File;
import java.util.Collections;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * Base class for system tests of the {@code soot.dotnet} frontend.
 *
 * <p>
 * The analyzed C# code lives in {@code src/systemTest/dotnet}, is compiled by the Maven build into
 * {@code target/systemTest-dotnet-targets} and is loaded with {@link Options#src_prec_dotnet}. Tests are skipped when the
 * targets or the .NET wrapper were not built (e.g. {@code -DskipDotnet}).
 */
@PowerMockIgnore({ "javax.management.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.dom.*",
    "com.sun.org.apache.xalan.*", "javax.activation.*" })
public abstract class AbstractDotnetTestingFramework extends AbstractTestingFramework {

  protected static final String TARGETS_DIR = new File("target", "systemTest-dotnet-targets").getAbsolutePath();
  protected static final String TARGET_ASSEMBLY_NAME = "Soot.SystemTest.Dotnet.dll";
  protected static final String TARGET_ASSEMBLY = TARGETS_DIR + File.separator + TARGET_ASSEMBLY_NAME;
  protected static final String TEST_NAMESPACE = "Soot.SystemTest.Dotnet.";

  @Before
  public void setUpScene() {
    Assume.assumeTrue("System test targets are missing, build them with `mvn test-compile` (without -DskipDotnet)",
        new File(TARGET_ASSEMBLY).isFile());
    Assume.assumeTrue("The .NET wrapper is not available, build Soot without -DskipDotnet", DotnetNativeHost.isAvailable());

    G.reset();
    Options.v().set_src_prec(Options.src_prec_dotnet);
    Options.v().set_soot_classpath(TARGETS_DIR);
    Options.v().set_process_dir(Collections.singletonList(TARGETS_DIR));
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_resolve_all_dotnet_methods(false);
    Options.v().set_ignore_methodsource_error(true);
    Options.v().set_throw_analysis(Options.throw_analysis_dotnet);
    Options.v().set_validate(true);
    Scene.v().loadNecessaryClasses();
  }

  @After
  public void tearDownScene() {
    G.reset();
  }

  protected static SootClass loadClass(String name) {
    return AbstractTestingFramework.getOrResolveSootClass(Scene.v(), name, SootClass.BODIES);
  }

  protected static SootClass testClass(String simpleName) {
    return loadClass(TEST_NAMESPACE + simpleName);
  }
}
