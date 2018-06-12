package soot.asm;

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

import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.D2F;
import static org.objectweb.asm.Opcodes.D2I;
import static org.objectweb.asm.Opcodes.D2L;
import static org.objectweb.asm.Opcodes.DADD;
import static org.objectweb.asm.Opcodes.DALOAD;
import static org.objectweb.asm.Opcodes.DASTORE;
import static org.objectweb.asm.Opcodes.DCMPG;
import static org.objectweb.asm.Opcodes.DCMPL;
import static org.objectweb.asm.Opcodes.DCONST_0;
import static org.objectweb.asm.Opcodes.DCONST_1;
import static org.objectweb.asm.Opcodes.DDIV;
import static org.objectweb.asm.Opcodes.DLOAD;
import static org.objectweb.asm.Opcodes.DMUL;
import static org.objectweb.asm.Opcodes.DNEG;
import static org.objectweb.asm.Opcodes.DREM;
import static org.objectweb.asm.Opcodes.DRETURN;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.DSUB;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2;
import static org.objectweb.asm.Opcodes.DUP2_X1;
import static org.objectweb.asm.Opcodes.DUP2_X2;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.DUP_X2;
import static org.objectweb.asm.Opcodes.F2D;
import static org.objectweb.asm.Opcodes.F2I;
import static org.objectweb.asm.Opcodes.F2L;
import static org.objectweb.asm.Opcodes.FCMPG;
import static org.objectweb.asm.Opcodes.FCMPL;
import static org.objectweb.asm.Opcodes.FCONST_0;
import static org.objectweb.asm.Opcodes.FCONST_2;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.I2B;
import static org.objectweb.asm.Opcodes.I2C;
import static org.objectweb.asm.Opcodes.I2D;
import static org.objectweb.asm.Opcodes.I2F;
import static org.objectweb.asm.Opcodes.I2L;
import static org.objectweb.asm.Opcodes.I2S;
import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.IALOAD;
import static org.objectweb.asm.Opcodes.IAND;
import static org.objectweb.asm.Opcodes.IASTORE;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.IDIV;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFGE;
import static org.objectweb.asm.Opcodes.IFGT;
import static org.objectweb.asm.Opcodes.IFLE;
import static org.objectweb.asm.Opcodes.IFLT;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNONNULL;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.IF_ACMPEQ;
import static org.objectweb.asm.Opcodes.IF_ACMPNE;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.IF_ICMPGT;
import static org.objectweb.asm.Opcodes.IF_ICMPLE;
import static org.objectweb.asm.Opcodes.IF_ICMPLT;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IMUL;
import static org.objectweb.asm.Opcodes.INEG;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.IOR;
import static org.objectweb.asm.Opcodes.IREM;
import static org.objectweb.asm.Opcodes.IRETURN;
import static org.objectweb.asm.Opcodes.ISHL;
import static org.objectweb.asm.Opcodes.ISHR;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.ISUB;
import static org.objectweb.asm.Opcodes.IUSHR;
import static org.objectweb.asm.Opcodes.IXOR;
import static org.objectweb.asm.Opcodes.JSR;
import static org.objectweb.asm.Opcodes.L2D;
import static org.objectweb.asm.Opcodes.L2F;
import static org.objectweb.asm.Opcodes.L2I;
import static org.objectweb.asm.Opcodes.LADD;
import static org.objectweb.asm.Opcodes.LALOAD;
import static org.objectweb.asm.Opcodes.LAND;
import static org.objectweb.asm.Opcodes.LASTORE;
import static org.objectweb.asm.Opcodes.LCMP;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LCONST_1;
import static org.objectweb.asm.Opcodes.LDIV;
import static org.objectweb.asm.Opcodes.LLOAD;
import static org.objectweb.asm.Opcodes.LMUL;
import static org.objectweb.asm.Opcodes.LNEG;
import static org.objectweb.asm.Opcodes.LOR;
import static org.objectweb.asm.Opcodes.LREM;
import static org.objectweb.asm.Opcodes.LRETURN;
import static org.objectweb.asm.Opcodes.LSHL;
import static org.objectweb.asm.Opcodes.LSHR;
import static org.objectweb.asm.Opcodes.LSTORE;
import static org.objectweb.asm.Opcodes.LSUB;
import static org.objectweb.asm.Opcodes.LUSHR;
import static org.objectweb.asm.Opcodes.LXOR;
import static org.objectweb.asm.Opcodes.MONITORENTER;
import static org.objectweb.asm.Opcodes.MONITOREXIT;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.NOP;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.POP2;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.RET;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SALOAD;
import static org.objectweb.asm.Opcodes.SASTORE;
import static org.objectweb.asm.Opcodes.SIPUSH;
import static org.objectweb.asm.Opcodes.SWAP;
import static org.objectweb.asm.Opcodes.T_BOOLEAN;
import static org.objectweb.asm.Opcodes.T_BYTE;
import static org.objectweb.asm.Opcodes.T_CHAR;
import static org.objectweb.asm.Opcodes.T_DOUBLE;
import static org.objectweb.asm.Opcodes.T_FLOAT;
import static org.objectweb.asm.Opcodes.T_INT;
import static org.objectweb.asm.Opcodes.T_LONG;
import static org.objectweb.asm.Opcodes.T_SHORT;
import static org.objectweb.asm.tree.AbstractInsnNode.FIELD_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.FRAME;
import static org.objectweb.asm.tree.AbstractInsnNode.IINC_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.INT_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.INVOKE_DYNAMIC_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.JUMP_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.LABEL;
import static org.objectweb.asm.tree.AbstractInsnNode.LDC_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.LINE;
import static org.objectweb.asm.tree.AbstractInsnNode.LOOKUPSWITCH_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.METHOD_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.MULTIANEWARRAY_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.TABLESWITCH_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.TYPE_INSN;
import static org.objectweb.asm.tree.AbstractInsnNode.VAR_INSN;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.MethodSource;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.coffi.Util;
import soot.jimple.AddExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LongConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MethodHandle;
import soot.jimple.MonitorStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.ReturnStmt;
import soot.jimple.StringConstant;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;
import soot.util.Chain;

/**
 * Generates Jimple bodies from bytecode.
 *
 * @author Aaloan Miftah
 */
final class AsmMethodSource implements MethodSource {

  private static final Operand DWORD_DUMMY = new Operand(null, null);

  /* -state fields- */
  private int nextLocal;
  private Map<Integer, Local> locals;
  private Multimap<LabelNode, UnitBox> labels;
  private Map<AbstractInsnNode, Unit> units;
  private ArrayList<Operand> stack;
  private Map<AbstractInsnNode, StackFrame> frames;
  private Multimap<LabelNode, UnitBox> trapHandlers;
  private JimpleBody body;
  private int lastLineNumber = -1;
  /* -const fields- */
  private final int maxLocals;
  private final InsnList instructions;
  private final List<LocalVariableNode> localVars;
  private final List<TryCatchBlockNode> tryCatchBlocks;

  private final Set<LabelNode> inlineExceptionLabels = new HashSet<LabelNode>();
  private final Map<LabelNode, Unit> inlineExceptionHandlers = new HashMap<LabelNode, Unit>();

  private final CastAndReturnInliner castAndReturnInliner = new CastAndReturnInliner();

  AsmMethodSource(int maxLocals, InsnList insns, List<LocalVariableNode> localVars, List<TryCatchBlockNode> tryCatchBlocks) {
    this.maxLocals = maxLocals;
    this.instructions = insns;
    this.localVars = localVars;
    this.tryCatchBlocks = tryCatchBlocks;
  }

  private StackFrame getFrame(AbstractInsnNode insn) {
    StackFrame frame = frames.get(insn);
    if (frame == null) {
      frame = new StackFrame(this);
      frames.put(insn, frame);
    }
    return frame;
  }

  private Local getLocal(int idx) {
    if (idx >= maxLocals) {
      throw new IllegalArgumentException("Invalid local index: " + idx);
    }
    Integer i = idx;
    Local l = locals.get(i);
    if (l == null) {
      String name;
      if (localVars != null) {
        name = null;
        for (LocalVariableNode lvn : localVars) {
          if (lvn.index == idx) {
            name = lvn.name;
            break;
          }
        }
        /* normally for try-catch blocks */
        if (name == null) {
          name = "l" + idx;
        }
      } else {
        name = "l" + idx;
      }
      l = Jimple.v().newLocal(name, UnknownType.v());
      locals.put(i, l);
    }
    return l;
  }

  private void push(Operand opr) {
    stack.add(opr);
  }

  private void pushDual(Operand opr) {
    stack.add(DWORD_DUMMY);
    stack.add(opr);
  }

  private Operand peek() {
    return stack.get(stack.size() - 1);
  }

  private void push(Type t, Operand opr) {
    if (AsmUtil.isDWord(t)) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  private Operand pop() {
    if (stack.isEmpty()) {
      throw new RuntimeException("Stack underrun");
    }
    return stack.remove(stack.size() - 1);
  }

  private Operand popDual() {
    Operand o = pop();
    Operand o2 = pop();
    if (o2 != DWORD_DUMMY && o2 != o) {
      throw new AssertionError("Not dummy operand, " + o2.value + " -- " + o.value);
    }
    return o;
  }

  private Operand pop(Type t) {
    return AsmUtil.isDWord(t) ? popDual() : pop();
  }

  private Operand popLocal(Operand o) {
    Value v = o.value;
    Local l = o.stack;
    if (l == null && !(v instanceof Local)) {
      l = o.stack = newStackLocal();
      setUnit(o.insn, Jimple.v().newAssignStmt(l, v));
      o.updateBoxes();
    }
    return o;
  }

  private Operand popImmediate(Operand o) {
    Value v = o.value;
    Local l = o.stack;
    if (l == null && !(v instanceof Local) && !(v instanceof Constant)) {
      l = o.stack = newStackLocal();
      setUnit(o.insn, Jimple.v().newAssignStmt(l, v));
      o.updateBoxes();
    }
    return o;
  }

  private Operand popStackConst(Operand o) {
    Value v = o.value;
    Local l = o.stack;
    if (l == null && !(v instanceof Constant)) {
      l = o.stack = newStackLocal();
      setUnit(o.insn, Jimple.v().newAssignStmt(l, v));
      o.updateBoxes();
    }
    return o;
  }

  private Operand popLocal() {
    return popLocal(pop());
  }

  private Operand popLocalDual() {
    return popLocal(popDual());
  }

  @SuppressWarnings("unused")
  private Operand popLocal(Type t) {
    return AsmUtil.isDWord(t) ? popLocalDual() : popLocal();
  }

  private Operand popImmediate() {
    return popImmediate(pop());
  }

  private Operand popImmediateDual() {
    return popImmediate(popDual());
  }

  private Operand popImmediate(Type t) {
    return AsmUtil.isDWord(t) ? popImmediateDual() : popImmediate();
  }

  private Operand popStackConst() {
    return popStackConst(pop());
  }

  private Operand popStackConstDual() {
    return popStackConst(popDual());
  }

  @SuppressWarnings("unused")
  private Operand popStackConst(Type t) {
    return AsmUtil.isDWord(t) ? popStackConstDual() : popStackConst();
  }

  void setUnit(AbstractInsnNode insn, Unit u) {
    if (Options.v().keep_line_number() && lastLineNumber >= 0) {
      Tag lineTag = u.getTag("LineNumberTag");
      if (lineTag == null) {
        lineTag = new LineNumberTag(lastLineNumber);
        u.addTag(lineTag);
      } else if (((LineNumberTag) lineTag).getLineNumber() != lastLineNumber) {
        throw new RuntimeException("Line tag mismatch");
      }
    }

    Unit o = units.put(insn, u);
    if (o != null) {
      throw new AssertionError(insn.getOpcode() + " already has a unit, " + o);
    }
  }

  void mergeUnits(AbstractInsnNode insn, Unit u) {
    Unit prev = units.put(insn, u);
    if (prev != null) {
      Unit merged = new UnitContainer(prev, u);
      units.put(insn, merged);
    }
  }

  Local newStackLocal() {
    Integer idx = nextLocal++;
    Local l = Jimple.v().newLocal("$stack" + idx, UnknownType.v());
    locals.put(idx, l);
    return l;
  }

  @SuppressWarnings("unchecked")
  <A extends Unit> A getUnit(AbstractInsnNode insn) {
    return (A) units.get(insn);
  }

  private void assignReadOps(Local l) {
    if (stack.isEmpty()) {
      return;
    }
    for (Operand opr : stack) {
      if (opr == DWORD_DUMMY || opr.stack != null || (l == null && opr.value instanceof Local)) {
        continue;
      }
      if (l != null && !opr.value.equivTo(l)) {
        List<ValueBox> uses = opr.value.getUseBoxes();
        boolean noref = true;
        for (ValueBox use : uses) {
          Value val = use.getValue();
          if (val.equivTo(l)) {
            noref = false;
            break;
          }
        }
        if (noref) {
          continue;
        }
      }
      int op = opr.insn.getOpcode();
      if (l == null && op != GETFIELD && op != GETSTATIC && (op < IALOAD && op > SALOAD)) {
        continue;
      }
      Local stack = newStackLocal();
      opr.stack = stack;
      AssignStmt as = Jimple.v().newAssignStmt(stack, opr.value);
      opr.updateBoxes();
      setUnit(opr.insn, as);
    }
  }

  private void convertGetFieldInsn(FieldInsnNode insn) {
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    Type type;
    if (out == null) {
      SootClass declClass = Scene.v().getSootClass(AsmUtil.toQualifiedName(insn.owner));
      type = AsmUtil.toJimpleType(insn.desc);
      Value val;
      SootFieldRef ref;
      if (insn.getOpcode() == GETSTATIC) {
        ref = Scene.v().makeFieldRef(declClass, insn.name, type, true);
        val = Jimple.v().newStaticFieldRef(ref);
      } else {
        Operand base = popLocal();
        ref = Scene.v().makeFieldRef(declClass, insn.name, type, false);
        InstanceFieldRef ifr = Jimple.v().newInstanceFieldRef(base.stackOrValue(), ref);
        val = ifr;
        base.addBox(ifr.getBaseBox());
        frame.in(base);
        frame.boxes(ifr.getBaseBox());
      }
      opr = new Operand(insn, val);
      frame.out(opr);
    } else {
      opr = out[0];
      type = opr.<FieldRef>value().getFieldRef().type();
      if (insn.getOpcode() == GETFIELD) {
        frame.mergeIn(pop());
      }
    }
    push(type, opr);
  }

  private void convertPutFieldInsn(FieldInsnNode insn) {
    boolean instance = insn.getOpcode() == PUTFIELD;
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr, rvalue;
    Type type;
    if (out == null) {
      SootClass declClass = Scene.v().getSootClass(AsmUtil.toQualifiedName(insn.owner));
      type = AsmUtil.toJimpleType(insn.desc);
      Value val;
      SootFieldRef ref;
      rvalue = popImmediate(type);
      if (!instance) {
        ref = Scene.v().makeFieldRef(declClass, insn.name, type, true);
        val = Jimple.v().newStaticFieldRef(ref);
        frame.in(rvalue);
      } else {
        Operand base = popLocal();
        ref = Scene.v().makeFieldRef(declClass, insn.name, type, false);
        InstanceFieldRef ifr = Jimple.v().newInstanceFieldRef(base.stackOrValue(), ref);
        val = ifr;
        base.addBox(ifr.getBaseBox());
        frame.in(rvalue, base);
      }
      opr = new Operand(insn, val);
      frame.out(opr);
      AssignStmt as = Jimple.v().newAssignStmt(val, rvalue.stackOrValue());
      rvalue.addBox(as.getRightOpBox());
      if (!instance) {
        frame.boxes(as.getRightOpBox());
      } else {
        frame.boxes(as.getRightOpBox(), ((InstanceFieldRef) val).getBaseBox());
      }
      setUnit(insn, as);
    } else {
      opr = out[0];
      type = opr.<FieldRef>value().getFieldRef().type();
      rvalue = pop(type);
      if (!instance) {
        /* PUTSTATIC only needs one operand on the stack, the rvalue */
        frame.mergeIn(rvalue);
      } else {
        /* PUTFIELD has a rvalue and a base */
        frame.mergeIn(rvalue, pop());
      }
    }
    /*
     * in case any static field or array is read from, and the static constructor or the field this instruction writes to,
     * modifies that field, write out any previous read from field/array
     */
    assignReadOps(null);
  }

  private void convertFieldInsn(FieldInsnNode insn) {
    int op = insn.getOpcode();
    if (op == GETSTATIC || op == GETFIELD) {
      convertGetFieldInsn(insn);
    } else {
      convertPutFieldInsn(insn);
    }
  }

  private void convertIincInsn(IincInsnNode insn) {
    Local local = getLocal(insn.var);
    assignReadOps(local);
    if (!units.containsKey(insn)) {
      AddExpr add = Jimple.v().newAddExpr(local, IntConstant.v(insn.incr));
      setUnit(insn, Jimple.v().newAssignStmt(local, add));
    }
  }

  private void convertConstInsn(InsnNode insn) {
    int op = insn.getOpcode();
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      Value v;
      if (op == ACONST_NULL) {
        v = NullConstant.v();
      } else if (op >= ICONST_M1 && op <= ICONST_5) {
        v = IntConstant.v(op - ICONST_0);
      } else if (op == LCONST_0 || op == LCONST_1) {
        v = LongConstant.v(op - LCONST_0);
      } else if (op >= FCONST_0 && op <= FCONST_2) {
        v = FloatConstant.v(op - FCONST_0);
      } else if (op == DCONST_0 || op == DCONST_1) {
        v = DoubleConstant.v(op - DCONST_0);
      } else {
        throw new AssertionError("Unknown constant opcode: " + op);
      }
      opr = new Operand(insn, v);
      frame.out(opr);
    } else {
      opr = out[0];
    }
    if (op == LCONST_0 || op == LCONST_1 || op == DCONST_0 || op == DCONST_1) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  private void convertArrayLoadInsn(InsnNode insn) {
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      Operand indx = popImmediate();
      Operand base = popImmediate();
      ArrayRef ar = Jimple.v().newArrayRef(base.stackOrValue(), indx.stackOrValue());
      indx.addBox(ar.getIndexBox());
      base.addBox(ar.getBaseBox());
      opr = new Operand(insn, ar);
      frame.in(indx, base);
      frame.boxes(ar.getIndexBox(), ar.getBaseBox());
      frame.out(opr);
    } else {
      opr = out[0];
      frame.mergeIn(pop(), pop());
    }
    int op = insn.getOpcode();
    if (op == DALOAD || op == LALOAD) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  private void convertArrayStoreInsn(InsnNode insn) {
    int op = insn.getOpcode();
    boolean dword = op == LASTORE || op == DASTORE;
    StackFrame frame = getFrame(insn);
    if (!units.containsKey(insn)) {
      Operand valu = dword ? popImmediateDual() : popImmediate();
      Operand indx = popImmediate();
      Operand base = popLocal();
      ArrayRef ar = Jimple.v().newArrayRef(base.stackOrValue(), indx.stackOrValue());
      indx.addBox(ar.getIndexBox());
      base.addBox(ar.getBaseBox());
      AssignStmt as = Jimple.v().newAssignStmt(ar, valu.stackOrValue());
      valu.addBox(as.getRightOpBox());
      frame.in(valu, indx, base);
      frame.boxes(as.getRightOpBox(), ar.getIndexBox(), ar.getBaseBox());
      setUnit(insn, as);
    } else {
      frame.mergeIn(dword ? popDual() : pop(), pop(), pop());
    }
  }

  /*
   * Following version is more complex, using stack frames as opposed to simply swapping
   */
  /*
   * StackFrame frame = getFrame(insn); Operand[] out = frame.out(); Operand dup, dup2 = null, dupd, dupd2 = null; if (out ==
   * null) { dupd = popImmediate(); dup = new Operand(insn, dupd.stackOrValue()); if (dword) { dupd2 = peek(); if (dupd2 ==
   * DWORD_DUMMY) { pop(); dupd2 = dupd; } else { dupd2 = popImmediate(); } dup2 = new Operand(insn, dupd2.stackOrValue());
   * frame.out(dup, dup2); frame.in(dupd, dupd2); } else { frame.out(dup); frame.in(dupd); } } else { dupd = pop(); dup =
   * out[0]; if (dword) { dupd2 = pop(); if (dupd2 == DWORD_DUMMY) dupd2 = dupd; dup2 = out[1]; frame.mergeIn(dupd, dupd2); }
   * else { frame.mergeIn(dupd); } }
   */

  private void convertDupInsn(InsnNode insn) {
    int op = insn.getOpcode();

    // Get the top stack value which we need in either case
    Operand dupd = popImmediate();
    Operand dupd2 = null;

    // Some instructions allow operands that take two registers
    boolean dword = op == DUP2 || op == DUP2_X1 || op == DUP2_X2;
    if (dword) {
      if (peek() == DWORD_DUMMY) {
        pop();
        dupd2 = dupd;
      } else {
        dupd2 = popImmediate();
      }
    }

    if (op == DUP) {
      // val -> val, val
      push(dupd);
      push(dupd);
    } else if (op == DUP_X1) {
      // val2, val1 -> val1, val2, val1
      // value1, value2 must not be of type double or long
      Operand o2 = popImmediate();
      push(dupd);
      push(o2);
      push(dupd);
    } else if (op == DUP_X2) {
      // value3, value2, value1 -> value1, value3, value2, value1
      Operand o2 = popImmediate();
      Operand o3 = peek() == DWORD_DUMMY ? pop() : popImmediate();
      push(dupd);
      push(o3);
      push(o2);
      push(dupd);
    } else if (op == DUP2) {
      // value2, value1 -> value2, value1, value2, value1
      push(dupd2);
      push(dupd);
      push(dupd2);
      push(dupd);
    } else if (op == DUP2_X1) {
      // value3, value2, value1 -> value2, value1, value3, value2, value1
      // Attention: value2 may be
      Operand o2 = popImmediate();
      push(dupd2);
      push(dupd);
      push(o2);
      push(dupd2);
      push(dupd);
    } else if (op == DUP2_X2) {
      // (value4, value3), (value2, value1) -> (value2, value1), (value4, value3), (value2, value1)
      Operand o2 = popImmediate();
      Operand o2h = peek() == DWORD_DUMMY ? pop() : popImmediate();
      push(dupd2);
      push(dupd);
      push(o2h);
      push(o2);
      push(dupd2);
      push(dupd);
    }
  }

  private void convertBinopInsn(InsnNode insn) {
    int op = insn.getOpcode();
    boolean dword = op == DADD || op == LADD || op == DSUB || op == LSUB || op == DMUL || op == LMUL || op == DDIV
        || op == LDIV || op == DREM || op == LREM || op == LSHL || op == LSHR || op == LUSHR || op == LAND || op == LOR
        || op == LXOR || op == LCMP || op == DCMPL || op == DCMPG;
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      Operand op2 = (dword && op != LSHL && op != LSHR && op != LUSHR) ? popImmediateDual() : popImmediate();
      Operand op1 = dword ? popImmediateDual() : popImmediate();
      Value v1 = op1.stackOrValue();
      Value v2 = op2.stackOrValue();
      BinopExpr binop;
      if (op >= IADD && op <= DADD) {
        binop = Jimple.v().newAddExpr(v1, v2);
      } else if (op >= ISUB && op <= DSUB) {
        binop = Jimple.v().newSubExpr(v1, v2);
      } else if (op >= IMUL && op <= DMUL) {
        binop = Jimple.v().newMulExpr(v1, v2);
      } else if (op >= IDIV && op <= DDIV) {
        binop = Jimple.v().newDivExpr(v1, v2);
      } else if (op >= IREM && op <= DREM) {
        binop = Jimple.v().newRemExpr(v1, v2);
      } else if (op >= ISHL && op <= LSHL) {
        binop = Jimple.v().newShlExpr(v1, v2);
      } else if (op >= ISHR && op <= LSHR) {
        binop = Jimple.v().newShrExpr(v1, v2);
      } else if (op >= IUSHR && op <= LUSHR) {
        binop = Jimple.v().newUshrExpr(v1, v2);
      } else if (op >= IAND && op <= LAND) {
        binop = Jimple.v().newAndExpr(v1, v2);
      } else if (op >= IOR && op <= LOR) {
        binop = Jimple.v().newOrExpr(v1, v2);
      } else if (op >= IXOR && op <= LXOR) {
        binop = Jimple.v().newXorExpr(v1, v2);
      } else if (op == LCMP) {
        binop = Jimple.v().newCmpExpr(v1, v2);
      } else if (op == FCMPL || op == DCMPL) {
        binop = Jimple.v().newCmplExpr(v1, v2);
      } else if (op == FCMPG || op == DCMPG) {
        binop = Jimple.v().newCmpgExpr(v1, v2);
      } else {
        throw new AssertionError("Unknown binop: " + op);
      }
      op1.addBox(binop.getOp1Box());
      op2.addBox(binop.getOp2Box());
      opr = new Operand(insn, binop);
      frame.in(op2, op1);
      frame.boxes(binop.getOp2Box(), binop.getOp1Box());
      frame.out(opr);
    } else {
      opr = out[0];
      if (dword) {
        if (op != LSHL && op != LSHR && op != LUSHR) {
          frame.mergeIn(popDual(), popDual());
        } else {
          frame.mergeIn(pop(), popDual());
        }
      } else {
        frame.mergeIn(pop(), pop());
      }
    }
    if (dword && (op < LCMP || op > DCMPG)) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  private void convertUnopInsn(InsnNode insn) {
    int op = insn.getOpcode();
    boolean dword = op == LNEG || op == DNEG;
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      Operand op1 = dword ? popImmediateDual() : popImmediate();
      Value v1 = op1.stackOrValue();
      UnopExpr unop;
      if (op >= INEG && op <= DNEG) {
        unop = Jimple.v().newNegExpr(v1);
      } else if (op == ARRAYLENGTH) {
        unop = Jimple.v().newLengthExpr(v1);
      } else {
        throw new AssertionError("Unknown unop: " + op);
      }
      op1.addBox(unop.getOpBox());
      opr = new Operand(insn, unop);
      frame.in(op1);
      frame.boxes(unop.getOpBox());
      frame.out(opr);
    } else {
      opr = out[0];
      frame.mergeIn(dword ? popDual() : pop());
    }
    if (dword) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  private void convertPrimCastInsn(InsnNode insn) {
    int op = insn.getOpcode();
    boolean tod = op == I2L || op == I2D || op == F2L || op == F2D || op == D2L || op == L2D;
    boolean fromd = op == D2L || op == L2D || op == D2I || op == L2I || op == D2F || op == L2F;
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      Type totype;
      if (op == I2L || op == F2L || op == D2L) {
        totype = LongType.v();
      } else if (op == L2I || op == F2I || op == D2I) {
        totype = IntType.v();
      } else if (op == I2F || op == L2F || op == D2F) {
        totype = FloatType.v();
      } else if (op == I2D || op == L2D || op == F2D) {
        totype = DoubleType.v();
      } else if (op == I2B) {
        totype = ByteType.v();
      } else if (op == I2S) {
        totype = ShortType.v();
      } else if (op == I2C) {
        totype = CharType.v();
      } else {
        throw new AssertionError("Unknonw prim cast op: " + op);
      }
      Operand val = fromd ? popImmediateDual() : popImmediate();
      CastExpr cast = Jimple.v().newCastExpr(val.stackOrValue(), totype);
      opr = new Operand(insn, cast);
      val.addBox(cast.getOpBox());
      frame.in(val);
      frame.boxes(cast.getOpBox());
      frame.out(opr);
    } else {
      opr = out[0];
      frame.mergeIn(fromd ? popDual() : pop());
    }
    if (tod) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  private void convertReturnInsn(InsnNode insn) {
    int op = insn.getOpcode();
    boolean dword = op == LRETURN || op == DRETURN;
    StackFrame frame = getFrame(insn);
    if (!units.containsKey(insn)) {
      Operand val = dword ? popImmediateDual() : popImmediate();
      ReturnStmt ret = Jimple.v().newReturnStmt(val.stackOrValue());
      val.addBox(ret.getOpBox());
      frame.in(val);
      frame.boxes(ret.getOpBox());
      setUnit(insn, ret);
    } else {
      frame.mergeIn(dword ? popDual() : pop());
    }
  }

  private void convertInsn(InsnNode insn) {
    int op = insn.getOpcode();
    if (op == NOP) {
      /*
       * We can ignore NOP instructions, but for completeness, we handle them
       */
      if (!units.containsKey(insn)) {
        units.put(insn, Jimple.v().newNopStmt());
      }
    } else if (op >= ACONST_NULL && op <= DCONST_1) {
      convertConstInsn(insn);
    } else if (op >= IALOAD && op <= SALOAD) {
      convertArrayLoadInsn(insn);
    } else if (op >= IASTORE && op <= SASTORE) {
      convertArrayStoreInsn(insn);
    } else if (op == POP) {
      popImmediate();
    } else if (op == POP2) {
      popImmediate();
      if (peek() == DWORD_DUMMY) {
        pop();
      } else {
        popImmediate();
      }
    } else if (op >= DUP && op <= DUP2_X2) {
      convertDupInsn((InsnNode) insn);
    } else if (op == SWAP) {
      Operand o1 = popImmediate();
      Operand o2 = popImmediate();
      push(o1);
      push(o2);
    } else if ((op >= IADD && op <= DREM) || (op >= ISHL && op <= LXOR) || (op >= LCMP && op <= DCMPG)) {
      convertBinopInsn((InsnNode) insn);
    } else if ((op >= INEG && op <= DNEG) || op == ARRAYLENGTH) {
      convertUnopInsn(insn);
    } else if (op >= I2L && op <= I2S) {
      convertPrimCastInsn(insn);
    } else if (op >= IRETURN && op <= ARETURN) {
      convertReturnInsn(insn);
    } else if (op == RETURN) {
      if (!units.containsKey(insn)) {
        setUnit(insn, Jimple.v().newReturnVoidStmt());
      }
    } else if (op == ATHROW) {
      StackFrame frame = getFrame(insn);
      Operand opr;
      if (!units.containsKey(insn)) {
        opr = popImmediate();
        ThrowStmt ts = Jimple.v().newThrowStmt(opr.stackOrValue());
        opr.addBox(ts.getOpBox());
        frame.in(opr);
        frame.out(opr);
        frame.boxes(ts.getOpBox());
        setUnit(insn, ts);
      } else {
        opr = pop();
        frame.mergeIn(opr);
      }
      push(opr);
    } else if (op == MONITORENTER || op == MONITOREXIT) {
      StackFrame frame = getFrame(insn);
      if (!units.containsKey(insn)) {
        Operand opr = popStackConst();
        MonitorStmt ts = op == MONITORENTER ? Jimple.v().newEnterMonitorStmt(opr.stackOrValue())
            : Jimple.v().newExitMonitorStmt(opr.stackOrValue());
        opr.addBox(ts.getOpBox());
        frame.in(opr);
        frame.boxes(ts.getOpBox());
        setUnit(insn, ts);
      } else {
        frame.mergeIn(pop());
      }
    } else {
      throw new AssertionError("Unknown insn op: " + op);
    }
  }

  private void convertIntInsn(IntInsnNode insn) {
    int op = insn.getOpcode();
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      Value v;
      if (op == BIPUSH || op == SIPUSH) {
        v = IntConstant.v(insn.operand);
      } else {
        Type type;
        switch (insn.operand) {
          case T_BOOLEAN:
            type = BooleanType.v();
            break;
          case T_CHAR:
            type = CharType.v();
            break;
          case T_FLOAT:
            type = FloatType.v();
            break;
          case T_DOUBLE:
            type = DoubleType.v();
            break;
          case T_BYTE:
            type = ByteType.v();
            break;
          case T_SHORT:
            type = ShortType.v();
            break;
          case T_INT:
            type = IntType.v();
            break;
          case T_LONG:
            type = LongType.v();
            break;
          default:
            throw new AssertionError("Unknown NEWARRAY type!");
        }
        Operand size = popImmediate();
        NewArrayExpr anew = Jimple.v().newNewArrayExpr(type, size.stackOrValue());
        size.addBox(anew.getSizeBox());
        frame.in(size);
        frame.boxes(anew.getSizeBox());
        v = anew;
      }
      opr = new Operand(insn, v);
      frame.out(opr);
    } else {
      opr = out[0];
      if (op == NEWARRAY) {
        frame.mergeIn(pop());
      }
    }
    push(opr);
  }

  private void convertJumpInsn(JumpInsnNode insn) {
    int op = insn.getOpcode();
    if (op == GOTO) {
      if (!units.containsKey(insn)) {
        UnitBox box = Jimple.v().newStmtBox(null);
        labels.put(insn.label, box);
        setUnit(insn, Jimple.v().newGotoStmt(box));
      }
      return;
    }
    /* must be ifX insn */
    StackFrame frame = getFrame(insn);
    if (!units.containsKey(insn)) {
      Operand val = popImmediate();
      Value v = val.stackOrValue();
      ConditionExpr cond;
      if (op >= IF_ICMPEQ && op <= IF_ACMPNE) {
        Operand val1 = popImmediate();
        Value v1 = val1.stackOrValue();
        if (op == IF_ICMPEQ) {
          cond = Jimple.v().newEqExpr(v1, v);
        } else if (op == IF_ICMPNE) {
          cond = Jimple.v().newNeExpr(v1, v);
        } else if (op == IF_ICMPLT) {
          cond = Jimple.v().newLtExpr(v1, v);
        } else if (op == IF_ICMPGE) {
          cond = Jimple.v().newGeExpr(v1, v);
        } else if (op == IF_ICMPGT) {
          cond = Jimple.v().newGtExpr(v1, v);
        } else if (op == IF_ICMPLE) {
          cond = Jimple.v().newLeExpr(v1, v);
        } else if (op == IF_ACMPEQ) {
          cond = Jimple.v().newEqExpr(v1, v);
        } else if (op == IF_ACMPNE) {
          cond = Jimple.v().newNeExpr(v1, v);
        } else {
          throw new AssertionError("Unknown if op: " + op);
        }
        val1.addBox(cond.getOp1Box());
        val.addBox(cond.getOp2Box());
        frame.boxes(cond.getOp2Box(), cond.getOp1Box());
        frame.in(val, val1);
      } else {
        if (op == IFEQ) {
          cond = Jimple.v().newEqExpr(v, IntConstant.v(0));
        } else if (op == IFNE) {
          cond = Jimple.v().newNeExpr(v, IntConstant.v(0));
        } else if (op == IFLT) {
          cond = Jimple.v().newLtExpr(v, IntConstant.v(0));
        } else if (op == IFGE) {
          cond = Jimple.v().newGeExpr(v, IntConstant.v(0));
        } else if (op == IFGT) {
          cond = Jimple.v().newGtExpr(v, IntConstant.v(0));
        } else if (op == IFLE) {
          cond = Jimple.v().newLeExpr(v, IntConstant.v(0));
        } else if (op == IFNULL) {
          cond = Jimple.v().newEqExpr(v, NullConstant.v());
        } else if (op == IFNONNULL) {
          cond = Jimple.v().newNeExpr(v, NullConstant.v());
        } else {
          throw new AssertionError("Unknown if op: " + op);
        }
        val.addBox(cond.getOp1Box());
        frame.boxes(cond.getOp1Box());
        frame.in(val);
      }
      UnitBox box = Jimple.v().newStmtBox(null);
      labels.put(insn.label, box);
      setUnit(insn, Jimple.v().newIfStmt(cond, box));
    } else {
      if (op >= IF_ICMPEQ && op <= IF_ACMPNE) {
        frame.mergeIn(pop(), pop());
      } else {
        frame.mergeIn(pop());
      }
    }
  }

  private void convertLdcInsn(LdcInsnNode insn) {
    Object val = insn.cst;
    boolean dword = val instanceof Long || val instanceof Double;
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      Value v = toSootValue(val);
      opr = new Operand(insn, v);
      frame.out(opr);
    } else {
      opr = out[0];
    }
    if (dword) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  private Value toSootValue(Object val) throws AssertionError {
    Value v;
    if (val instanceof Integer) {
      v = IntConstant.v((Integer) val);
    } else if (val instanceof Float) {
      v = FloatConstant.v((Float) val);
    } else if (val instanceof Long) {
      v = LongConstant.v((Long) val);
    } else if (val instanceof Double) {
      v = DoubleConstant.v((Double) val);
    } else if (val instanceof String) {
      v = StringConstant.v(val.toString());
    } else if (val instanceof org.objectweb.asm.Type) {
      v = ClassConstant.v(((org.objectweb.asm.Type) val).getDescriptor());
    } else if (val instanceof Handle) {
      v = MethodHandle.v(toSootMethodRef((Handle) val), ((Handle) val).getTag());
    } else {
      throw new AssertionError("Unknown constant type: " + val.getClass());
    }
    return v;
  }

  private void convertLookupSwitchInsn(LookupSwitchInsnNode insn) {
    StackFrame frame = getFrame(insn);
    if (units.containsKey(insn)) {
      frame.mergeIn(pop());
      return;
    }
    Operand key = popImmediate();
    UnitBox dflt = Jimple.v().newStmtBox(null);

    List<UnitBox> targets = new ArrayList<UnitBox>(insn.labels.size());
    labels.put(insn.dflt, dflt);
    for (LabelNode ln : insn.labels) {
      UnitBox box = Jimple.v().newStmtBox(null);
      targets.add(box);
      labels.put(ln, box);
    }

    List<IntConstant> keys = new ArrayList<IntConstant>(insn.keys.size());
    for (Integer i : insn.keys) {
      keys.add(IntConstant.v(i));
    }

    LookupSwitchStmt lss = Jimple.v().newLookupSwitchStmt(key.stackOrValue(), keys, targets, dflt);
    key.addBox(lss.getKeyBox());
    frame.in(key);
    frame.boxes(lss.getKeyBox());
    setUnit(insn, lss);
  }

  private void convertMethodInsn(MethodInsnNode insn) {
    int op = insn.getOpcode();
    boolean instance = op != INVOKESTATIC;
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    Type returnType;
    if (out == null) {
      String clsName = AsmUtil.toQualifiedName(insn.owner);
      if (clsName.charAt(0) == '[') {
        clsName = "java.lang.Object";
      }
      SootClass cls = Scene.v().getSootClass(clsName);
      List<Type> sigTypes = AsmUtil.toJimpleDesc(insn.desc);
      returnType = sigTypes.remove(sigTypes.size() - 1);
      SootMethodRef ref = Scene.v().makeMethodRef(cls, insn.name, sigTypes, returnType, !instance);
      int nrArgs = sigTypes.size();
      final Operand[] args;
      List<Value> argList = Collections.emptyList();
      if (!instance) {
        args = nrArgs == 0 ? null : new Operand[nrArgs];
        if (args != null) {
          argList = new ArrayList<Value>(nrArgs);
        }
      } else {
        args = new Operand[nrArgs + 1];
        if (nrArgs != 0) {
          argList = new ArrayList<Value>(nrArgs);
        }
      }
      while (nrArgs-- != 0) {
        args[nrArgs] = popImmediate(sigTypes.get(nrArgs));
        argList.add(args[nrArgs].stackOrValue());
      }
      if (argList.size() > 1) {
        Collections.reverse(argList);
      }
      if (instance) {
        args[args.length - 1] = popLocal();
      }
      ValueBox[] boxes = args == null ? null : new ValueBox[args.length];
      InvokeExpr invoke;
      if (!instance) {
        invoke = Jimple.v().newStaticInvokeExpr(ref, argList);
      } else {
        Local base = (Local) args[args.length - 1].stackOrValue();
        InstanceInvokeExpr iinvoke;
        if (op == INVOKESPECIAL) {
          iinvoke = Jimple.v().newSpecialInvokeExpr(base, ref, argList);
        } else if (op == INVOKEVIRTUAL) {
          iinvoke = Jimple.v().newVirtualInvokeExpr(base, ref, argList);
        } else if (op == INVOKEINTERFACE) {
          iinvoke = Jimple.v().newInterfaceInvokeExpr(base, ref, argList);
        } else {
          throw new AssertionError("Unknown invoke op:" + op);
        }
        boxes[boxes.length - 1] = iinvoke.getBaseBox();
        args[args.length - 1].addBox(boxes[boxes.length - 1]);
        invoke = iinvoke;
      }
      if (boxes != null) {
        for (int i = 0; i != sigTypes.size(); i++) {
          boxes[i] = invoke.getArgBox(i);
          args[i].addBox(boxes[i]);
        }
        frame.boxes(boxes);
        frame.in(args);
      }
      opr = new Operand(insn, invoke);
      frame.out(opr);
    } else {
      opr = out[0];
      InvokeExpr expr = (InvokeExpr) opr.value;
      List<Type> types = expr.getMethodRef().parameterTypes();
      Operand[] oprs;
      int nrArgs = types.size();
      if (expr.getMethodRef().isStatic()) {
        oprs = nrArgs == 0 ? null : new Operand[nrArgs];
      } else {
        oprs = new Operand[nrArgs + 1];
      }
      if (oprs != null) {
        while (nrArgs-- != 0) {
          oprs[nrArgs] = pop(types.get(nrArgs));
        }
        if (!expr.getMethodRef().isStatic()) {
          oprs[oprs.length - 1] = pop();
        }
        frame.mergeIn(oprs);
        nrArgs = types.size();
      }
      returnType = expr.getMethodRef().returnType();
    }
    if (AsmUtil.isDWord(returnType)) {
      pushDual(opr);
    } else if (!(returnType instanceof VoidType)) {
      push(opr);
    } else if (!units.containsKey(insn)) {
      setUnit(insn, Jimple.v().newInvokeStmt(opr.value));
    }
    /*
     * assign all read ops in case the method modifies any of the fields
     */
    assignReadOps(null);
  }

  private void convertInvokeDynamicInsn(InvokeDynamicInsnNode insn) {
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    Type returnType;
    if (out == null) {
      // convert info on bootstrap method
      SootMethodRef bsmMethodRef = toSootMethodRef(insn.bsm);
      List<Value> bsmMethodArgs = new ArrayList<Value>(insn.bsmArgs.length);
      for (Object bsmArg : insn.bsmArgs) {
        bsmMethodArgs.add(toSootValue(bsmArg));
      }

      // create ref to actual method
      SootClass bclass = Scene.v().getSootClass(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME);

      // Generate parameters & returnType & parameterTypes
      Type[] types = Util.v().jimpleTypesOfFieldOrMethodDescriptor(insn.desc);
      int nrArgs = types.length - 1;
      List<Type> parameterTypes = new ArrayList<Type>(nrArgs);
      List<Value> methodArgs = new ArrayList<Value>(nrArgs);

      Operand[] args = new Operand[nrArgs];
      ValueBox[] boxes = new ValueBox[nrArgs];

      // Beware: Call stack is FIFO, Jimple is linear

      while (nrArgs-- != 0) {
        parameterTypes.add(types[nrArgs]);
        args[nrArgs] = popImmediate(types[nrArgs]);
        methodArgs.add(args[nrArgs].stackOrValue());
      }
      if (methodArgs.size() > 1) {
        Collections.reverse(methodArgs); // Call stack is FIFO, Jimple is linear
        Collections.reverse(parameterTypes);
      }
      returnType = types[types.length - 1];

      // we always model invokeDynamic method refs as static method references
      // of methods on the type SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME
      SootMethodRef methodRef = Scene.v().makeMethodRef(bclass, insn.name, parameterTypes, returnType, true);

      DynamicInvokeExpr indy
          = Jimple.v().newDynamicInvokeExpr(bsmMethodRef, bsmMethodArgs, methodRef, insn.bsm.getTag(), methodArgs);
      if (boxes != null) {
        for (int i = 0; i < types.length - 1; i++) {
          boxes[i] = indy.getArgBox(i);
          args[i].addBox(boxes[i]);
        }

        frame.boxes(boxes);
        frame.in(args);
      }
      opr = new Operand(insn, indy);
      frame.out(opr);
    } else {
      opr = out[0];
      InvokeExpr expr = (InvokeExpr) opr.value;
      List<Type> types = expr.getMethodRef().parameterTypes();
      Operand[] oprs;
      int nrArgs = types.size();
      if (expr.getMethodRef().isStatic()) {
        oprs = nrArgs == 0 ? null : new Operand[nrArgs];
      } else {
        oprs = new Operand[nrArgs + 1];
      }
      if (oprs != null) {
        while (nrArgs-- != 0) {
          oprs[nrArgs] = pop(types.get(nrArgs));
        }
        if (!expr.getMethodRef().isStatic()) {
          oprs[oprs.length - 1] = pop();
        }
        frame.mergeIn(oprs);
        nrArgs = types.size();
      }
      returnType = expr.getMethodRef().returnType();
    }
    if (AsmUtil.isDWord(returnType)) {
      pushDual(opr);
    } else if (!(returnType instanceof VoidType)) {
      push(opr);
    } else if (!units.containsKey(insn)) {
      setUnit(insn, Jimple.v().newInvokeStmt(opr.value));
    }
    /*
     * assign all read ops in case the method modifies any of the fields
     */
    assignReadOps(null);
  }

  private SootMethodRef toSootMethodRef(Handle methodHandle) {
    String bsmClsName = AsmUtil.toQualifiedName(methodHandle.getOwner());
    SootClass bsmCls = Scene.v().getSootClass(bsmClsName);
    List<Type> bsmSigTypes = AsmUtil.toJimpleDesc(methodHandle.getDesc());
    Type returnType = bsmSigTypes.remove(bsmSigTypes.size() - 1);
    return Scene.v().makeMethodRef(bsmCls, methodHandle.getName(), bsmSigTypes, returnType, true /* always static */);
  }

  private void convertMultiANewArrayInsn(MultiANewArrayInsnNode insn) {
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      ArrayType t = (ArrayType) AsmUtil.toJimpleType(insn.desc);
      int dims = insn.dims;
      Operand[] sizes = new Operand[dims];
      Value[] sizeVals = new Value[dims];
      ValueBox[] boxes = new ValueBox[dims];
      while (dims-- != 0) {
        sizes[dims] = popImmediate();
        sizeVals[dims] = sizes[dims].stackOrValue();
      }
      NewMultiArrayExpr nm = Jimple.v().newNewMultiArrayExpr(t, Arrays.asList(sizeVals));
      for (int i = 0; i != boxes.length; i++) {
        ValueBox vb = nm.getSizeBox(i);
        sizes[i].addBox(vb);
        boxes[i] = vb;
      }
      frame.boxes(boxes);
      frame.in(sizes);
      opr = new Operand(insn, nm);
      frame.out(opr);
    } else {
      opr = out[0];
      int dims = insn.dims;
      Operand[] sizes = new Operand[dims];
      while (dims-- != 0) {
        sizes[dims] = pop();
      }
      frame.mergeIn(sizes);
    }
    push(opr);
  }

  private void convertTableSwitchInsn(TableSwitchInsnNode insn) {
    StackFrame frame = getFrame(insn);
    if (units.containsKey(insn)) {
      frame.mergeIn(pop());
      return;
    }
    Operand key = popImmediate();
    UnitBox dflt = Jimple.v().newStmtBox(null);
    List<UnitBox> targets = new ArrayList<UnitBox>(insn.labels.size());
    labels.put(insn.dflt, dflt);
    for (LabelNode ln : insn.labels) {
      UnitBox box = Jimple.v().newStmtBox(null);
      targets.add(box);
      labels.put(ln, box);
    }
    TableSwitchStmt tss = Jimple.v().newTableSwitchStmt(key.stackOrValue(), insn.min, insn.max, targets, dflt);
    key.addBox(tss.getKeyBox());
    frame.in(key);
    frame.boxes(tss.getKeyBox());
    setUnit(insn, tss);
  }

  private void convertTypeInsn(TypeInsnNode insn) {
    int op = insn.getOpcode();
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      Type t = AsmUtil.toJimpleRefType(insn.desc);
      Value val;
      if (op == NEW) {
        val = Jimple.v().newNewExpr((RefType) t);
      } else {
        Operand op1 = popImmediate();
        Value v1 = op1.stackOrValue();
        ValueBox vb;
        if (op == ANEWARRAY) {
          NewArrayExpr expr = Jimple.v().newNewArrayExpr(t, v1);
          vb = expr.getSizeBox();
          val = expr;
        } else if (op == CHECKCAST) {
          CastExpr expr = Jimple.v().newCastExpr(v1, t);
          vb = expr.getOpBox();
          val = expr;
        } else if (op == INSTANCEOF) {
          InstanceOfExpr expr = Jimple.v().newInstanceOfExpr(v1, t);
          vb = expr.getOpBox();
          val = expr;
        } else {
          throw new AssertionError("Unknown type op: " + op);
        }
        op1.addBox(vb);
        frame.in(op1);
        frame.boxes(vb);
      }
      opr = new Operand(insn, val);
      frame.out(opr);
    } else {
      opr = out[0];
      if (op != NEW) {
        frame.mergeIn(pop());
      }
    }
    push(opr);
  }

  private void convertVarLoadInsn(VarInsnNode insn) {
    int op = insn.getOpcode();
    boolean dword = op == LLOAD || op == DLOAD;
    StackFrame frame = getFrame(insn);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      opr = new Operand(insn, getLocal(insn.var));
      frame.out(opr);
    } else {
      opr = out[0];
    }
    if (dword) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  private void convertVarStoreInsn(VarInsnNode insn) {
    int op = insn.getOpcode();
    boolean dword = op == LSTORE || op == DSTORE;
    StackFrame frame = getFrame(insn);
    Operand opr = dword ? popDual() : pop();
    Local local = getLocal(insn.var);
    if (!units.containsKey(insn)) {
      DefinitionStmt as = Jimple.v().newAssignStmt(local, opr.stackOrValue());
      opr.addBox(as.getRightOpBox());
      frame.boxes(as.getRightOpBox());
      frame.in(opr);
      setUnit(insn, as);
    } else {
      frame.mergeIn(opr);
    }
    assignReadOps(local);
  }

  private void convertVarInsn(VarInsnNode insn) {
    int op = insn.getOpcode();
    if (op >= ILOAD && op <= ALOAD) {
      convertVarLoadInsn(insn);
    } else if (op >= ISTORE && op <= ASTORE) {
      convertVarStoreInsn(insn);
    } else if (op == RET) {
      /* we handle it, even thought it should be removed */
      if (!units.containsKey(insn)) {
        setUnit(insn, Jimple.v().newRetStmt(getLocal(insn.var)));
      }
    } else {
      throw new AssertionError("Unknown var op: " + op);
    }
  }

  private void convertLabel(LabelNode ln) {
    if (!trapHandlers.containsKey(ln)) {
      return;
    }

    // We create a nop statement as a placeholder so that we can jump
    // somewhere from the real exception handler in case this is inline
    // code
    if (inlineExceptionLabels.contains(ln)) {
      if (!units.containsKey(ln)) {
        NopStmt nop = Jimple.v().newNopStmt();
        setUnit(ln, nop);
      }
      return;
    }

    StackFrame frame = getFrame(ln);
    Operand[] out = frame.out();
    Operand opr;
    if (out == null) {
      CaughtExceptionRef ref = Jimple.v().newCaughtExceptionRef();
      Local stack = newStackLocal();
      DefinitionStmt as = Jimple.v().newIdentityStmt(stack, ref);
      opr = new Operand(ln, ref);
      opr.stack = stack;
      frame.out(opr);
      setUnit(ln, as);
    } else {
      opr = out[0];
    }
    push(opr);
  }

  private void convertLine(LineNumberNode ln) {
    lastLineNumber = ln.line;
  }

  /* Conversion */

  private final class Edge {
    /* edge endpoint */
    final AbstractInsnNode insn;
    /* previous stacks at edge */
    final LinkedList<Operand[]> prevStacks;
    /* current stack at edge */
    ArrayList<Operand> stack;

    Edge(AbstractInsnNode insn, ArrayList<Operand> stack) {
      this.insn = insn;
      this.prevStacks = new LinkedList<Operand[]>();
      this.stack = stack;
    }

    Edge(AbstractInsnNode insn) {
      this(insn, new ArrayList<Operand>(AsmMethodSource.this.stack));
    }
  }

  private Table<AbstractInsnNode, AbstractInsnNode, Edge> edges;
  private ArrayDeque<Edge> conversionWorklist;

  private void addEdges(AbstractInsnNode cur, AbstractInsnNode tgt1, List<LabelNode> tgts) {
    int lastIdx = tgts == null ? -1 : tgts.size() - 1;
    Operand[] stackss = (new ArrayList<Operand>(stack)).toArray(new Operand[stack.size()]);
    AbstractInsnNode tgt = tgt1;
    int i = 0;
    tgt_loop: do {
      Edge edge = edges.get(cur, tgt);
      if (edge == null) {
        edge = new Edge(tgt);
        edge.prevStacks.add(stackss);
        edges.put(cur, tgt, edge);
        conversionWorklist.add(edge);
        continue;
      }
      if (edge.stack != null) {
        ArrayList<Operand> stackTemp = edge.stack;
        if (stackTemp.size() != stackss.length) {
          throw new AssertionError("Multiple un-equal stacks!");
        }
        for (int j = 0; j != stackss.length; j++) {
          if (!stackTemp.get(j).equivTo(stackss[j])) {
            throw new AssertionError("Multiple un-equal stacks!");
          }
        }
        continue;
      }
      for (Operand[] ps : edge.prevStacks) {
        if (Arrays.equals(ps, stackss)) {
          continue tgt_loop;
        }
      }
      edge.stack = new ArrayList<Operand>(stack);
      edge.prevStacks.add(stackss);
      conversionWorklist.add(edge);
    } while (i <= lastIdx && (tgt = tgts.get(i++)) != null);
  }

  private void convert() {
    ArrayDeque<Edge> worklist = new ArrayDeque<Edge>();
    for (LabelNode ln : trapHandlers.keySet()) {
      if (checkInlineExceptionHandler(ln)) {
        handleInlineExceptionHandler(ln, worklist);
      } else {
        worklist.add(new Edge(ln, new ArrayList<Operand>()));
      }
    }
    worklist.add(new Edge(instructions.getFirst(), new ArrayList<Operand>()));
    conversionWorklist = worklist;
    edges = HashBasedTable.create(1, 1);

    do {
      Edge edge = worklist.pollLast();
      AbstractInsnNode insn = edge.insn;
      stack = edge.stack;
      edge.stack = null;
      do {
        int type = insn.getType();
        if (type == FIELD_INSN) {
          convertFieldInsn((FieldInsnNode) insn);
        } else if (type == IINC_INSN) {
          convertIincInsn((IincInsnNode) insn);
        } else if (type == INSN) {
          convertInsn((InsnNode) insn);
          int op = insn.getOpcode();
          if ((op >= IRETURN && op <= RETURN) || op == ATHROW) {
            break;
          }
        } else if (type == INT_INSN) {
          convertIntInsn((IntInsnNode) insn);
        } else if (type == LDC_INSN) {
          convertLdcInsn((LdcInsnNode) insn);
        } else if (type == JUMP_INSN) {
          JumpInsnNode jmp = (JumpInsnNode) insn;
          convertJumpInsn(jmp);
          int op = jmp.getOpcode();
          if (op == JSR) {
            throw new UnsupportedOperationException("JSR!");
          }
          if (op != GOTO) {
            /* ifX opcode, i.e. two successors */
            AbstractInsnNode next = insn.getNext();
            addEdges(insn, next, Collections.singletonList(jmp.label));
          } else {
            addEdges(insn, jmp.label, null);
          }
          break;
        } else if (type == LOOKUPSWITCH_INSN) {
          LookupSwitchInsnNode swtch = (LookupSwitchInsnNode) insn;
          convertLookupSwitchInsn(swtch);
          LabelNode dflt = swtch.dflt;
          addEdges(insn, dflt, swtch.labels);
          break;
        } else if (type == METHOD_INSN) {
          convertMethodInsn((MethodInsnNode) insn);
        } else if (type == INVOKE_DYNAMIC_INSN) {
          convertInvokeDynamicInsn((InvokeDynamicInsnNode) insn);
        } else if (type == MULTIANEWARRAY_INSN) {
          convertMultiANewArrayInsn((MultiANewArrayInsnNode) insn);
        } else if (type == TABLESWITCH_INSN) {
          TableSwitchInsnNode swtch = (TableSwitchInsnNode) insn;
          convertTableSwitchInsn(swtch);
          LabelNode dflt = swtch.dflt;
          addEdges(insn, dflt, swtch.labels);
        } else if (type == TYPE_INSN) {
          convertTypeInsn((TypeInsnNode) insn);
        } else if (type == VAR_INSN) {
          if (insn.getOpcode() == RET) {
            throw new UnsupportedOperationException("RET!");
          }
          convertVarInsn((VarInsnNode) insn);
        } else if (type == LABEL) {
          convertLabel((LabelNode) insn);
        } else if (type == LINE) {
          convertLine((LineNumberNode) insn);
        } else if (type == FRAME) {
          // we can ignore it
        } else {
          throw new RuntimeException("Unknown instruction type: " + type);
        }
      } while ((insn = insn.getNext()) != null);
    } while (!worklist.isEmpty());
    conversionWorklist = null;
    edges = null;
  }

  private void handleInlineExceptionHandler(LabelNode ln, ArrayDeque<Edge> worklist) {
    // Catch the exception
    CaughtExceptionRef ref = Jimple.v().newCaughtExceptionRef();
    Local local = newStackLocal();
    DefinitionStmt as = Jimple.v().newIdentityStmt(local, ref);

    Operand opr = new Operand(ln, ref);
    opr.stack = local;

    ArrayList<Operand> stack = new ArrayList<Operand>();
    stack.add(opr);
    worklist.add(new Edge(ln, stack));

    // Save the statements
    inlineExceptionHandlers.put(ln, as);
  }

  private boolean checkInlineExceptionHandler(LabelNode ln) {
    // If this label is reachable through an exception and through normal
    // code, we have to split the exceptional case (with the exception on
    // the stack) from the normal fall-through case without anything on the
    // stack.
    for (Iterator<AbstractInsnNode> it = instructions.iterator(); it.hasNext();) {
      AbstractInsnNode node = it.next();
      if (node instanceof JumpInsnNode) {
        if (((JumpInsnNode) node).label == ln) {
          inlineExceptionLabels.add(ln);
          return true;
        }
      } else if (node instanceof LookupSwitchInsnNode) {
        if (((LookupSwitchInsnNode) node).labels.contains(ln)) {
          inlineExceptionLabels.add(ln);
          return true;
        }
      } else if (node instanceof TableSwitchInsnNode) {
        if (((TableSwitchInsnNode) node).labels.contains(ln)) {
          inlineExceptionLabels.add(ln);
          return true;
        }
      }
    }
    return false;
  }

  private void emitLocals() {
    JimpleBody jb = body;
    SootMethod m = jb.getMethod();
    Collection<Local> jbl = jb.getLocals();
    Collection<Unit> jbu = jb.getUnits();
    int iloc = 0;
    if (!m.isStatic()) {
      Local l = getLocal(iloc++);
      jbu.add(Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef(m.getDeclaringClass().getType())));
    }
    int nrp = 0;
    for (Object ot : m.getParameterTypes()) {
      Type t = (Type) ot;
      Local l = getLocal(iloc);
      jbu.add(Jimple.v().newIdentityStmt(l, Jimple.v().newParameterRef(t, nrp++)));
      if (AsmUtil.isDWord(t)) {
        iloc += 2;
      } else {
        iloc++;
      }
    }
    for (Local l : locals.values()) {
      jbl.add(l);
    }
  }

  private void emitTraps() {
    Chain<Trap> traps = body.getTraps();
    SootClass throwable = Scene.v().getSootClass("java.lang.Throwable");
    Map<LabelNode, Iterator<UnitBox>> handlers = new HashMap<LabelNode, Iterator<UnitBox>>(tryCatchBlocks.size());
    for (TryCatchBlockNode tc : tryCatchBlocks) {
      UnitBox start = Jimple.v().newStmtBox(null);
      UnitBox end = Jimple.v().newStmtBox(null);
      Iterator<UnitBox> hitr = handlers.get(tc.handler);
      if (hitr == null) {
        hitr = trapHandlers.get(tc.handler).iterator();
        handlers.put(tc.handler, hitr);
      }
      UnitBox handler = hitr.next();
      SootClass cls = tc.type == null ? throwable : Scene.v().getSootClass(AsmUtil.toQualifiedName(tc.type));
      Trap trap = Jimple.v().newTrap(cls, start, end, handler);
      traps.add(trap);
      labels.put(tc.start, start);
      labels.put(tc.end, end);
    }
  }

  private void emitUnits(Unit u) {
    if (u instanceof UnitContainer) {
      for (Unit uu : ((UnitContainer) u).units) {
        emitUnits(uu);
      }
    } else {
      body.getUnits().add(u);
    }
  }

  private void emitUnits() {
    AbstractInsnNode insn = instructions.getFirst();
    ArrayDeque<LabelNode> labls = new ArrayDeque<LabelNode>();

    while (insn != null) {
      // Save the label to assign it to the next real unit
      if (insn instanceof LabelNode) {
        labls.add((LabelNode) insn);
      }

      // Get the unit associated with the current instruction
      Unit u = units.get(insn);
      if (u == null) {
        insn = insn.getNext();
        continue;
      }

      emitUnits(u);

      // If this is an exception handler, register the starting unit for it
      {
        IdentityStmt caughtEx = null;
        if (u instanceof IdentityStmt) {
          caughtEx = (IdentityStmt) u;
        } else if (u instanceof UnitContainer) {
          caughtEx = getIdentityRefFromContrainer((UnitContainer) u);
        }

        if (insn instanceof LabelNode && caughtEx != null && caughtEx.getRightOp() instanceof CaughtExceptionRef) {
          // We directly place this label
          Collection<UnitBox> traps = trapHandlers.get((LabelNode) insn);
          for (UnitBox ub : traps) {
            ub.setUnit(caughtEx);
          }
        }
      }

      // Register this unit for all targets of the labels ending up at it
      while (!labls.isEmpty()) {
        LabelNode ln = labls.poll();
        Collection<UnitBox> boxes = labels.get(ln);
        if (boxes != null) {
          for (UnitBox box : boxes) {
            box.setUnit(u instanceof UnitContainer ? ((UnitContainer) u).getFirstUnit() : u);
          }
        }
      }
      insn = insn.getNext();
    }

    // Emit the inline exception handlers
    for (LabelNode ln : this.inlineExceptionHandlers.keySet()) {
      Unit handler = this.inlineExceptionHandlers.get(ln);
      emitUnits(handler);

      Collection<UnitBox> traps = trapHandlers.get(ln);
      for (UnitBox ub : traps) {
        ub.setUnit(handler);
      }

      // We need to jump to the original implementation
      Unit targetUnit = units.get(ln);
      GotoStmt gotoImpl = Jimple.v().newGotoStmt(targetUnit);
      body.getUnits().add(gotoImpl);
    }

    /* set remaining labels & boxes to last unit of chain */
    if (labls.isEmpty()) {
      return;
    }
    Unit end = Jimple.v().newNopStmt();
    body.getUnits().add(end);
    while (!labls.isEmpty()) {
      LabelNode ln = labls.poll();
      Collection<UnitBox> boxes = labels.get(ln);
      if (boxes != null) {
        for (UnitBox box : boxes) {
          box.setUnit(end);
        }
      }
    }
  }

  private IdentityStmt getIdentityRefFromContrainer(UnitContainer u) {
    for (Unit uu : ((UnitContainer) u).units) {
      if (uu instanceof IdentityStmt) {
        return (IdentityStmt) uu;
      } else if (uu instanceof UnitContainer) {
        return getIdentityRefFromContrainer((UnitContainer) uu);
      }
    }
    return null;
  }

  @Override
  public Body getBody(SootMethod m, String phaseName) {
    if (!m.isConcrete()) {
      return null;
    }
    JimpleBody jb = Jimple.v().newBody(m);
    /* initialize */
    int nrInsn = instructions.size();
    nextLocal = maxLocals;
    locals = new HashMap<Integer, Local>(maxLocals + (maxLocals / 2));
    labels = ArrayListMultimap.create(4, 1);
    units = new HashMap<AbstractInsnNode, Unit>(nrInsn);
    frames = new HashMap<AbstractInsnNode, StackFrame>(nrInsn);
    trapHandlers = ArrayListMultimap.create(tryCatchBlocks.size(), 1);
    body = jb;
    /* retrieve all trap handlers */
    for (TryCatchBlockNode tc : tryCatchBlocks) {
      trapHandlers.put(tc.handler, Jimple.v().newStmtBox(null));
    }
    /* convert instructions */
    try {
      convert();
    } catch (Throwable t) {
      throw new RuntimeException("Failed to convert " + m, t);
    }

    /* build body (add units, locals, traps, etc.) */
    emitLocals();
    emitTraps();
    emitUnits();

    /* clean up */
    locals = null;
    labels = null;
    units = null;
    stack = null;
    frames = null;
    body = null;

    // Make sure to inline patterns of the form to enable proper variable
    // splitting and type assignment:
    // a = new A();
    // goto l0;
    // l0:
    // b = (B) a;
    // return b;
    castAndReturnInliner.transform(jb);

    try {
      PackManager.v().getPack("jb").apply(jb);
    } catch (Throwable t) {
      throw new RuntimeException("Failed to apply jb to " + m, t);
    }

    return jb;
  }
}
