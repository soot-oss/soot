package soot.asm.backend;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Mustafa Senoglu
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import soot.G;
import soot.Main;
import soot.ModulePathSourceLocator;
import soot.Scene;

/**
 * Regression test for <a href="https://github.com/soot-oss/soot/issues/1917">issue #1917</a>: calling an interface's
 * static method must produce an {@code InterfaceMethodref} (not a {@code Methodref}) constant pool entry, otherwise
 * the JVM throws {@code IncompatibleClassChangeError} at runtime. The {@code invokeinterface} variant is covered as
 * well.
 */
public class InterfaceStaticCallTest {

  private static final String TARGET_CLASS = "soot.asm.backend.targets.InterfaceStaticCall";
  private static final String INTERFACE_NAME = "soot/asm/backend/targets/InterfaceWithStaticMethod";

  @Test
  public void interfaceMethodCallsUseInterfaceMethodref() throws IOException {
    runSoot();

    File out = new File("sootOutput/" + TARGET_CLASS.replace('.', '/') + ".class");
    assertTrue("Soot output file not found: " + out.getAbsolutePath(), out.exists());

    List<String[]> methodrefs = new ArrayList<>();
    List<String[]> interfaceMethodrefs = new ArrayList<>();
    collectMethodrefs(out, methodrefs, interfaceMethodrefs);

    assertTrue("Expected an InterfaceMethodref for the interface static method 'answer', found: " + interfaceMethodrefs,
        contains(interfaceMethodrefs, INTERFACE_NAME, "answer"));
    assertTrue("Expected an InterfaceMethodref for the default method 'twice', found: " + interfaceMethodrefs,
        contains(interfaceMethodrefs, INTERFACE_NAME, "twice"));
    assertFalse("Must not emit a Methodref for the interface-declared method 'answer', found: " + methodrefs,
        contains(methodrefs, INTERFACE_NAME, "answer"));
  }

  private static boolean contains(List<String[]> refs, String owner, String name) {
    for (String[] ref : refs) {
      if (ref[0].equals(owner) && ref[1].equals(name)) {
        return true;
      }
    }
    return false;
  }

  private void runSoot() {
    G.reset();
    String rtJar = "";
    if (Scene.isJavaGEQ9(System.getProperty("java.version"))) {
      rtJar = ModulePathSourceLocator.DUMMY_CLASSPATH_JDK9_FS;
    } else {
      rtJar = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
    }
    String classpath = new File("./test-classes-asm").getAbsolutePath() + File.pathSeparator + rtJar;
    Main.main(new String[] { "-cp", classpath, "-src-prec", "only-class", "-output-format", "class",
        "-allow-phantom-refs", "-java-version", "default", "-no-derive-java-version", TARGET_CLASS,
        "-no-writeout-body-releasing" });
  }

  /**
   * Parses the constant pool of a class file, collecting {@code [owner, name]} pairs of {@code Methodref} and
   * {@code InterfaceMethodref} entries.
   */
  private static void collectMethodrefs(File classFile, List<String[]> methodrefs, List<String[]> interfaceMethodrefs)
      throws IOException {
    try (InputStream in = new FileInputStream(classFile); DataInputStream data = new DataInputStream(in)) {
      if (data.readInt() != 0xCAFEBABE) {
        fail("Not a valid class file: " + classFile);
      }
      data.readUnsignedShort(); // minor_version
      data.readUnsignedShort(); // major_version
      int cpCount = data.readUnsignedShort();
      int[] tags = new int[cpCount];
      int[] first = new int[cpCount];
      int[] second = new int[cpCount];
      String[] utf8 = new String[cpCount];
      for (int i = 1; i < cpCount; i++) {
        int tag = data.readUnsignedByte();
        tags[i] = tag;
        switch (tag) {
          case 1: // Utf8
            utf8[i] = data.readUTF();
            break;
          case 3: // Integer
          case 4: // Float
            data.readInt();
            break;
          case 5: // Long
          case 6: // Double
            data.readLong();
            i++;
            break;
          case 7: // Class
          case 8: // String
          case 16: // MethodType
          case 19: // Module
          case 20: // Package
            first[i] = data.readUnsignedShort();
            break;
          case 9: // Fieldref
          case 10: // Methodref
          case 11: // InterfaceMethodref
          case 12: // NameAndType
          case 17: // Dynamic
          case 18: // InvokeDynamic
            first[i] = data.readUnsignedShort();
            second[i] = data.readUnsignedShort();
            break;
          case 15: // MethodHandle
            first[i] = data.readUnsignedByte();
            second[i] = data.readUnsignedShort();
            break;
          default:
            fail("Unexpected constant pool tag: " + tag);
        }
      }
      for (int i = 1; i < cpCount; i++) {
        if (tags[i] != 10 && tags[i] != 11) {
          continue;
        }
        int classIndex = first[i];
        int nameAndTypeIndex = second[i];
        if (tags[classIndex] != 7 || tags[nameAndTypeIndex] != 12) {
          fail("Malformed method reference entry " + i);
        }
        String owner = utf8[first[classIndex]];
        String name = utf8[first[nameAndTypeIndex]];
        if (owner == null || name == null) {
          fail("Could not resolve constant pool entry " + i);
        }
        (tags[i] == 11 ? interfaceMethodrefs : methodrefs).add(new String[] { owner, name });
      }
    }
  }
}
