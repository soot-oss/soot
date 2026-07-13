package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Soot contributors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;
import soot.tagkit.NestHostTag;
import soot.tagkit.NestMembersTag;

/**
 * Tests that Soot reads and writes the {@code NestHost} and {@code NestMembers} class-file attributes introduced by JEP 181
 * (Nest-Based Access Control).
 *
 * @author Soot contributors
 */
public class NestBasedAccessControlTest {

  private File classDir;

  @Before
  public void setUp() throws IOException {
    classDir = Files.createTempDir();
    // Host class declaring two nest members.
    writeClass("NestHost", generateHostClass());
    // Member class declaring its nest host.
    writeClass("NestHost$Member", generateMemberClass());
  }

  private void writeClass(String internalName, byte[] bytecode) throws IOException {
    File classFile = new File(classDir, internalName + ".class");
    Files.write(bytecode, classFile);
  }

  private byte[] generateHostClass() {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "NestHost", null, Type.getInternalName(Object.class),
        null);
    cw.visitNestMember("NestHost$Member");
    cw.visitNestMember("NestHost$Other");
    cw.visitInnerClass("NestHost$Member", "NestHost", "Member", Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC);
    cw.visitInnerClass("NestHost$Other", "NestHost", "Other", Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC);
    cw.visitEnd();
    return cw.toByteArray();
  }

  private byte[] generateMemberClass() {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cw.visit(Opcodes.V11, Opcodes.ACC_SUPER, "NestHost$Member", null, Type.getInternalName(Object.class), null);
    cw.visitNestHost("NestHost");
    cw.visitInnerClass("NestHost$Member", "NestHost", "Member", Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC);
    cw.visitEnd();
    return cw.toByteArray();
  }

  private SootClass loadClass(String className) {
    G.reset();
    Options.v().set_process_dir(Collections.singletonList(classDir.getAbsolutePath()));
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_output_format(Options.output_format_none);
    Options.v().setPhaseOption("cg", "enabled:false");
    Scene.v().loadNecessaryClasses();
    SootClass sc = Scene.v().forceResolve(className, SootClass.BODIES);
    assertNotNull(sc);
    return sc;
  }

  @Test
  public void nestMembersAttributeIsReadIntoTag() {
    SootClass host = loadClass("NestHost");
    NestMembersTag tag = (NestMembersTag) host.getTag(NestMembersTag.NAME);
    assertNotNull("Expected a NestMembersTag on the nest host class", tag);
    assertTrue(tag.getNestMembers().contains("NestHost$Member"));
    assertTrue(tag.getNestMembers().contains("NestHost$Other"));
    assertEquals(2, tag.getNestMembers().size());
    // A nest host does not declare a NestHost attribute for itself.
    assertNull(host.getTag(NestHostTag.NAME));
  }

  @Test
  public void nestHostAttributeIsReadIntoTag() {
    SootClass member = loadClass("NestHost$Member");
    NestHostTag tag = (NestHostTag) member.getTag(NestHostTag.NAME);
    assertNotNull("Expected a NestHostTag on the nest member class", tag);
    assertEquals("NestHost", tag.getHost());
    // A nest member does not declare NestMembers.
    assertNull(member.getTag(NestMembersTag.NAME));
  }

  @Test
  public void nestAttributesRoundTripThroughBackend() throws IOException {
    // Load both classes and write them back out through the ASM backend.
    G.reset();
    File outDir = Files.createTempDir();
    Options.v().set_process_dir(Collections.singletonList(classDir.getAbsolutePath()));
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_output_dir(outDir.getAbsolutePath());
    Options.v().set_output_format(Options.output_format_class);
    Options.v().setPhaseOption("cg", "enabled:false");
    Scene.v().loadNecessaryClasses();
    Scene.v().forceResolve("NestHost", SootClass.BODIES);
    Scene.v().forceResolve("NestHost$Member", SootClass.BODIES);
    soot.PackManager.v().writeOutput();

    // Re-read the generated host class and verify the NestMembers attribute survived.
    List<String> writtenMembers = readNestMembers(new File(outDir, "NestHost.class"));
    assertTrue(writtenMembers.contains("NestHost$Member"));
    assertTrue(writtenMembers.contains("NestHost$Other"));

    // Re-read the generated member class and verify the NestHost attribute survived.
    String writtenHost = readNestHost(new File(outDir, "NestHost$Member.class"));
    assertEquals("NestHost", writtenHost);
  }

  private List<String> readNestMembers(File classFile) throws IOException {
    final List<String> members = new ArrayList<>();
    ClassReader cr = new ClassReader(Files.toByteArray(classFile));
    cr.accept(new ClassVisitor(Opcodes.ASM9) {
      @Override
      public void visitNestMember(String nestMember) {
        members.add(nestMember);
      }
    }, 0);
    return members;
  }

  private String readNestHost(File classFile) throws IOException {
    final String[] host = new String[1];
    ClassReader cr = new ClassReader(Files.toByteArray(classFile));
    cr.accept(new ClassVisitor(Opcodes.ASM9) {
      @Override
      public void visitNestHost(String nestHost) {
        host[0] = nestHost;
      }
    }, 0);
    return host[0];
  }
}
