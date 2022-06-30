package soot.baf;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Timothy Hoffman
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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * Test the bytecode version computation in {@link soot.baf.BafASMBackend}.
 * 
 * @author Timothy Hoffman
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class BafASMBackendTest extends AbstractTestingFramework {

  @Override
  protected void setupSoot() {
      final Options opts = Options.v();
      opts.set_derive_java_version(false);
  }

  @Test
  public void testCachingInvalidation() throws Exception {
    final String targetClass = "soot.baf.BafASMBackendTestInput_Default";
    SootClass sc = prepareTarget("<" + targetClass + ": void <init>()>", targetClass).getDeclaringClass();
    Assert.assertNotNull(sc);
    Assert.assertEquals(targetClass, sc.getName());

    // Ensure the class was loaded properly with all bodies
    Assert.assertEquals(6, sc.getMethodCount());
    Assert.assertTrue(sc.getMethods().stream().allMatch(SootMethod::hasActiveBody));

    // Ensure the version will be derived only by BafASMBackend
    // (and not from the input source version).
    Assert.assertEquals(0, Options.v().java_version());

    byte[] bytecode = generateBytecode(sc);

    // Run ASM verifier and ensure the message is empty (i.e. there are no VerifyErrors).
    StringWriter strWriter = new StringWriter();
    CheckClassAdapter.verify(new ClassReader(bytecode), null, false, new PrintWriter(strWriter));
    String verifyMsg = strWriter.toString();
    Assert.assertTrue(verifyMsg, verifyMsg.isEmpty());
  }
}
