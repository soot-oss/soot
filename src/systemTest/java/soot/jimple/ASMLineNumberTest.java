package soot.jimple;

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

import com.google.common.collect.ImmutableSet;
import com.google.common.io.ByteSource;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import org.apache.commons.io.ByteBuffers;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Body;
import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.asm.AsmUtil;
import soot.asm.BytecodeOffsetNode;
import soot.options.Options;
import soot.tagkit.BytecodeOffsetTag;
import soot.tagkit.LineNumberTag;
import soot.testing.framework.AbstractTestingFramework;
import soot.util.backend.ASMBackendUtils;

/**
 * This test checks whether the line number agree with ASM. Note that we currently only check invocation statements since
 * they are easy to align between ASM and Jimple code.
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class ASMLineNumberTest extends AbstractTestingFramework {

  private static final boolean DEBUG = false;

  final AtomicInteger checkedMethods = new AtomicInteger();

  private abstract static class LineNumberStmt {
    public int lineNumber;
    public int pc;

    public LineNumberStmt(int lineNumber, int pc) {
      this.lineNumber = lineNumber;
      this.pc = pc;
    }

    public abstract boolean matchesJimpleStmt(Stmt s);

    protected static void appendType(StringBuilder sb, Type p) {
      sb.append(ASMBackendUtils.toTypeDesc(p));
    }

    public void assertMatchesJimpleStmt(Stmt stmt) {
      if (!matchesJimpleStmt(stmt)) {
        throw new AssertionError(stmt + " does not match " + this.toString());
      }
      if (lineNumber == -1) {
        return;
      }

      LineNumberTag lt = (LineNumberTag) stmt.getTag(LineNumberTag.NAME);
      if (lt == null) {
        throw new AssertionError("No line number");
      }
      if (this.lineNumber != lt.getLineNumber()) {
        throw new AssertionError("Wrong line number");
      }
      BytecodeOffsetTag bo = (BytecodeOffsetTag) stmt.getTag(BytecodeOffsetTag.NAME);
      if (bo == null) {
        throw new AssertionError("No bytecode offset");
      }
      if (this.pc != bo.getBytecodeOffset()) {
        throw new AssertionError("Wrong bytecode offset");
      }
    }

  }

  private static class MethodCallStmt extends LineNumberStmt {
    private String desc;
    private String name;

    public MethodCallStmt(int linenumber, int pc, String name, String desc) {
      super(linenumber, pc);
      this.name = name;
      this.desc = desc;
    }

    @Override
    public boolean matchesJimpleStmt(Stmt s) {
      InvokeExpr inv = s.getInvokeExprUnsafe();
      if (inv == null) {
        return false;
      }
      SootMethodRef mr = inv.getMethodRef();
      if (!mr.getName().equals(name)) {
        return false;
      }
      StringBuilder sb = new StringBuilder();
      sb.append("(");
      for (Type p : mr.getParameterTypes()) {
        appendType(sb, p);
      }
      sb.append(')');
      appendType(sb, mr.getReturnType());
      return desc.equals(sb.toString());
    }

    @Override
    public String toString() {
      return "Method call: " + name + " " + desc;
    }

  }

  @Test
  public void testLineNumbers() throws Exception {
    testLineNumbersFromClass(ByteBuffers.class);
  }

  private void testLineNumbersFromClass(Class<?> clz) throws Exception {
    ImmutableSet<ClassInfo> classes = ClassPath.from(ClassLoader.getSystemClassLoader()).getAllClasses();

    File tmp = File.createTempFile("tmp", "class");
    try {
      tmp.delete();
      tmp.mkdirs();

      for (ClassInfo c : classes) {
        String name = c.getName();
        String packageName = c.getPackageName().replace('.', File.separatorChar);
        File pck = new File(tmp, packageName);
        pck.mkdirs();
        String n = getSimpleClassName(name);
        ByteSource bs;
        try {
          bs = c.asByteSource();
        } catch (Exception e) {
          // ignore
          continue;
        }
        try (FileOutputStream fos = new FileOutputStream(new File(pck, n + ".class"))) {
          bs.copyTo(fos);
        }
      }
      testLineNumbers(tmp);
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    } finally {
      FileUtils.deleteQuietly(tmp);
    }
  }

  private static String getSimpleClassName(String name) {
    String n = name;
    int idx = n.lastIndexOf('.');
    if (idx != -1) {
      n = n.substring(idx + 1);
    }
    return n;
  }

  private void testLineNumbers(File classFolder) throws Exception {
    G.reset();

    // unreachable code eliminator can make the comparison more difficult since it removes code
    // this happens often in generated finally blocks.
    Options.v().setPhaseOption("jb.uce", "enabled:false");
    Options.v().set_keep_line_number(true);
    Options.v().set_keep_offset(true);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_process_dir(Arrays.asList(classFolder.getAbsolutePath()));

    Scene.v().loadNecessaryClasses();
    performMatching(classFolder);
    int m = checkedMethods.get();
    if (m < 100) {
      throw new RuntimeException(String.format("Checked only %d methods", m));
    }
    System.out.println(String.format("ASMLineNumberTest: Checked %d methods", m));
  }

  private void performMatching(File classFolder) throws IOException, InterruptedException {
    int poolSize = Runtime.getRuntime().availableProcessors();
    ThreadPoolExecutor exec = new ThreadPoolExecutor(poolSize, poolSize, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    for (SootClass c : new ArrayList<>(Scene.v().getApplicationClasses())) {
      File packageFolder = new File(classFolder, c.getPackageName().replace('.', File.separatorChar));
      File clFile = new File(packageFolder, getSimpleClassName(c.getName()) + ".class");
      if (!clFile.exists()) {
        continue;
      }

      try (InputStream is = new FileInputStream(clFile)) {
        final AtomicInteger currentOffset = new AtomicInteger();
        ClassReader reader = new ClassReader(is) {
          @Override
          protected void readBytecodeInstructionOffset(int bytecodeOffset) {
            super.readBytecodeInstructionOffset(bytecodeOffset);
            currentOffset.set(bytecodeOffset);
          }

        };

        ClassNode classNode = new ClassNode(Opcodes.ASM9) {
          @Override
          public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
              String[] exceptions) {
            MethodNode method = (MethodNode) super.visitMethod(access, name, descriptor, signature, exceptions);
            method.instructions = new InsnList() {

              @Override
              public void add(AbstractInsnNode insnNode) {
                int offset = currentOffset.get();
                super.add(new BytecodeOffsetNode(offset));
                super.add(insnNode);
              }
            };
            return method;
          }
        };

        // Read the class layout into the Tree representation
        reader.accept(classNode, ClassReader.SKIP_FRAMES);
        if (!classNode.name.equals(c.getName().replace('.', '/'))) {
          continue;
        }

        // Iterate over every method found in the class
        for (MethodNode method : classNode.methods) {
          exec.execute(() -> {
            analyzeMethod(classNode.name, method);
          });
        }

      } catch (IOException e) {
        System.err.println("Error reading class file: " + e.getMessage());
      }

    }
    exec.shutdown();
    exec.awaitTermination(1, TimeUnit.DAYS);
  }

  private void analyzeMethod(String className, MethodNode method) {
    SootClass cl = Scene.v().getSootClass(AsmUtil.toQualifiedName(className));
    if (cl.isPhantomClass()) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    List<Type> types = AsmUtil.toJimpleDesc(method.desc, com.google.common.base.Optional.absent());
    sb.append(types.get(types.size() - 1));
    sb.append(" ");
    sb.append(method.name);
    sb.append("(");
    boolean first = true;
    for (int i = 0; i < types.size() - 1; i++) {
      if (first) {
        first = false;
      } else {
        sb.append(',');
      }
      sb.append(types.get(i));
    }
    sb.append(")");
    SootMethod m = cl.getMethodUnsafe(sb.toString());
    ;
    if (m == null || !m.isConcrete()) {
      //Usually no big deal
      return;
    }
    Body body = m.retrieveActiveBody();
    UnitPatchingChain chain = body.getUnits();
    Queue<Unit> allUnits = new ArrayDeque<>(chain);
    int currentLineNumber = -1, currentBytecodeOffset = -1;
    StringBuilder matched = new StringBuilder();

    String dbgAsm;
    if (DEBUG) {
      dbgAsm = methodNodeToString(method);
    }

    for (int i = 0; i < method.instructions.size(); i++) {
      AbstractInsnNode insn = method.instructions.get(i);

      if (insn instanceof LineNumberNode) {
        LineNumberNode lineNode = (LineNumberNode) insn;
        currentLineNumber = lineNode.line;
      }
      if (insn instanceof BytecodeOffsetNode) {
        BytecodeOffsetNode boNode = (BytecodeOffsetNode) insn;
        currentBytecodeOffset = boNode.bytecodeOffset;
      }

      if (insn instanceof MethodInsnNode) {
        MethodInsnNode minsn = (MethodInsnNode) insn;

        MethodCallStmt mc = new MethodCallStmt(currentLineNumber, currentBytecodeOffset, minsn.name, minsn.desc);
        Stmt stmt = getNextStmt(allUnits,
            x -> x.containsInvokeExpr()
                && !x.getInvokeExpr().getMethodRef().getDeclaringClass().getName().equals("soot.dummy.InvokeDynamic")
                && !x.getInvokeExpr().getMethodRef().getName().equals("bootstrap$"));
        mc.assertMatchesJimpleStmt(stmt);

        if (DEBUG) {
          matched.append(stmt.toString() + " <-> " + mc.toString() + " has line number " + currentLineNumber).append("\n");
        }
      }

    }
    checkedMethods.incrementAndGet();
  }

  public static String methodNodeToString(MethodNode methodNode) {
    Textifier textifier = new Textifier();
    TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(textifier);

    methodNode.accept(traceMethodVisitor);
    StringWriter stringWriter = new StringWriter();
    try (PrintWriter printWriter = new PrintWriter(stringWriter)) {

      textifier.print(printWriter);
      IOUtils.closeQuietly(stringWriter);
    }
    return stringWriter.toString();
  }

  private static Stmt getNextStmt(Queue<Unit> queue, Predicate<Stmt> predicateFilter) {
    Queue<Unit> old = queue;
    ArrayDeque d = (ArrayDeque) queue;
    queue = d.clone();
    StringBuilder rest = new StringBuilder();
    while (!queue.isEmpty()) {
      Unit p = queue.poll();
      Stmt s = (Stmt) p;
      if (predicateFilter.test(s)) {
        while (old.size() != queue.size()) {
          old.poll();
        }
        return s;
      }
      if (DEBUG) {
        rest.append(s.toString()).append("\n");
      }
    }
    throw new IllegalArgumentException("Could not find the next statement in " + rest);
  }

}
