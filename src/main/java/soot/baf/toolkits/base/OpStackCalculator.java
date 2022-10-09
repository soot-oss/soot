package soot.baf.toolkits.base;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2021 Raja Vallee-Rai and others
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.IntegerType;
import soot.LongType;
import soot.PatchingChain;
import soot.RefLikeType;
import soot.RefType;
import soot.SootMethod;
import soot.StmtAddressType;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.baf.AddInst;
import soot.baf.AndInst;
import soot.baf.ArrayLengthInst;
import soot.baf.ArrayReadInst;
import soot.baf.ArrayWriteInst;
import soot.baf.BafBody;
import soot.baf.CmpInst;
import soot.baf.CmpgInst;
import soot.baf.CmplInst;
import soot.baf.DivInst;
import soot.baf.Dup1Inst;
import soot.baf.Dup1_x1Inst;
import soot.baf.Dup1_x2Inst;
import soot.baf.Dup2Inst;
import soot.baf.Dup2_x1Inst;
import soot.baf.Dup2_x2Inst;
import soot.baf.DynamicInvokeInst;
import soot.baf.EnterMonitorInst;
import soot.baf.ExitMonitorInst;
import soot.baf.FieldGetInst;
import soot.baf.FieldPutInst;
import soot.baf.GotoInst;
import soot.baf.IdentityInst;
import soot.baf.IfCmpEqInst;
import soot.baf.IfCmpGeInst;
import soot.baf.IfCmpGtInst;
import soot.baf.IfCmpLeInst;
import soot.baf.IfCmpLtInst;
import soot.baf.IfCmpNeInst;
import soot.baf.IfEqInst;
import soot.baf.IfGeInst;
import soot.baf.IfGtInst;
import soot.baf.IfLeInst;
import soot.baf.IfLtInst;
import soot.baf.IfNeInst;
import soot.baf.IfNonNullInst;
import soot.baf.IfNullInst;
import soot.baf.IncInst;
import soot.baf.Inst;
import soot.baf.InstSwitch;
import soot.baf.InstanceCastInst;
import soot.baf.InstanceOfInst;
import soot.baf.InterfaceInvokeInst;
import soot.baf.JSRInst;
import soot.baf.LoadInst;
import soot.baf.LookupSwitchInst;
import soot.baf.MethodArgInst;
import soot.baf.MulInst;
import soot.baf.NegInst;
import soot.baf.NewArrayInst;
import soot.baf.NewInst;
import soot.baf.NewMultiArrayInst;
import soot.baf.NopInst;
import soot.baf.OpTypeArgInst;
import soot.baf.OrInst;
import soot.baf.PopInst;
import soot.baf.PrimitiveCastInst;
import soot.baf.PushInst;
import soot.baf.RemInst;
import soot.baf.ReturnInst;
import soot.baf.ReturnVoidInst;
import soot.baf.ShlInst;
import soot.baf.ShrInst;
import soot.baf.SpecialInvokeInst;
import soot.baf.StaticGetInst;
import soot.baf.StaticInvokeInst;
import soot.baf.StaticPutInst;
import soot.baf.StoreInst;
import soot.baf.SubInst;
import soot.baf.SwapInst;
import soot.baf.TableSwitchInst;
import soot.baf.TargetArgInst;
import soot.baf.ThrowInst;
import soot.baf.UshrInst;
import soot.baf.VirtualInvokeInst;
import soot.baf.XorInst;
import soot.baf.internal.AbstractOpTypeInst;
import soot.toolkits.graph.BriefUnitGraph;
import soot.util.Chain;

/**
 * Emulates the operation of the JVM stack to compute the expected types of the objects on the stack at each {@link Unit} in
 * a {@link BafBody}.
 * 
 * @author Michael Batchelder
 * @author Timothy Hoffman
 */
public class OpStackCalculator {
  private static final Logger logger = LoggerFactory.getLogger(OpStackCalculator.class);

  private static class StackEffectSwitch implements InstSwitch {

    public boolean shouldThrow = true;
    // Types popped from the stack by the instruction
    public Type[] remove_types = null;
    // Types pushed to the stack by the instruction
    public Type[] add_types = null;

    private static RefLikeType arrayRefType() {
      // RefType replaces the arraytype
      return RefType.v();
    }

    @Override
    public void caseReturnInst(ReturnInst i) {
      remove_types = new Type[] { i.getOpType() };
      add_types = null;
    }

    @Override
    public void caseReturnVoidInst(ReturnVoidInst i) {
      remove_types = null;
      add_types = null;
    }

    @Override
    public void caseNopInst(NopInst i) {
      remove_types = null;
      add_types = null;
    }

    @Override
    public void caseGotoInst(GotoInst i) {
      remove_types = null;
      add_types = null;
    }

    @Override
    public void caseJSRInst(JSRInst i) {
      remove_types = null;
      add_types = new Type[] { StmtAddressType.v() };
    }

    @Override
    public void casePushInst(PushInst i) {
      remove_types = null;
      add_types = new Type[] { i.getConstant().getType() };
    }

    @Override
    public void casePopInst(PopInst i) {
      remove_types = new Type[] { ((soot.baf.internal.BPopInst) i).getType() };
      add_types = null;
    }

    @Override
    public void caseIdentityInst(IdentityInst i) {
      remove_types = null;
      add_types = null;
    }

    @Override
    public void caseStoreInst(StoreInst i) {
      remove_types = new Type[] { ((AbstractOpTypeInst) i).getOpType() };
      add_types = null;
    }

    @Override
    public void caseLoadInst(LoadInst i) {
      remove_types = null;
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseArrayWriteInst(ArrayWriteInst i) {
      remove_types = new Type[] { arrayRefType(), IntType.v(), i.getOpType() };
      add_types = null;
    }

    @Override
    public void caseArrayReadInst(ArrayReadInst i) {
      remove_types = new Type[] { arrayRefType(), IntType.v() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseIfNullInst(IfNullInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = null;
    }

    @Override
    public void caseIfNonNullInst(IfNonNullInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = null;
    }

    @Override
    public void caseIfEqInst(IfEqInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    @Override
    public void caseIfNeInst(IfNeInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    @Override
    public void caseIfGtInst(IfGtInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    @Override
    public void caseIfGeInst(IfGeInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    @Override
    public void caseIfLtInst(IfLtInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    @Override
    public void caseIfLeInst(IfLeInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    @Override
    public void caseIfCmpEqInst(IfCmpEqInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    @Override
    public void caseIfCmpNeInst(IfCmpNeInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    @Override
    public void caseIfCmpGtInst(IfCmpGtInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    @Override
    public void caseIfCmpGeInst(IfCmpGeInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    @Override
    public void caseIfCmpLtInst(IfCmpLtInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    @Override
    public void caseIfCmpLeInst(IfCmpLeInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    @Override
    public void caseStaticGetInst(StaticGetInst i) {
      remove_types = null;
      add_types = new Type[] { i.getField().getType() };
    }

    @Override
    public void caseStaticPutInst(StaticPutInst i) {
      remove_types = new Type[] { i.getField().getType() };
      add_types = null;
    }

    @Override
    public void caseFieldGetInst(FieldGetInst i) {
      remove_types = new Type[] { i.getField().getDeclaringClass().getType() };
      add_types = new Type[] { i.getField().getType() };
    }

    @Override
    public void caseFieldPutInst(FieldPutInst i) {
      remove_types = new Type[] { i.getField().getDeclaringClass().getType(), i.getField().getType() };
      add_types = null;
    }

    @Override
    public void caseInstanceCastInst(InstanceCastInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = new Type[] { i.getCastType() };
    }

    @Override
    public void caseInstanceOfInst(InstanceOfInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = new Type[] { IntType.v() };
    }

    @Override
    public void casePrimitiveCastInst(PrimitiveCastInst i) {
      remove_types = new Type[] { i.getFromType() };
      add_types = new Type[] { i.getToType() };
    }

    private void staticinvoke(MethodArgInst i) {
      SootMethod m = i.getMethod();

      final int len = m.getParameterCount();
      remove_types = new Type[len];
      System.arraycopy(m.getParameterTypes().toArray(), 0, remove_types, 0, len);

      Type retTy = m.getReturnType();
      add_types = (retTy instanceof VoidType) ? null : new Type[] { retTy };
    }

    private void instanceinvoke(MethodArgInst i) {
      SootMethod m = i.getMethod();

      final int len = m.getParameterCount();
      remove_types = new Type[len + 1];
      remove_types[0] = RefType.v("java.lang.Object");
      System.arraycopy(m.getParameterTypes().toArray(), 0, remove_types, 1, len);

      Type retTy = m.getReturnType();
      add_types = (retTy instanceof VoidType) ? null : new Type[] { retTy };
    }

    @Override
    public void caseDynamicInvokeInst(DynamicInvokeInst i) {
      staticinvoke(i);
    }

    @Override
    public void caseStaticInvokeInst(StaticInvokeInst i) {
      staticinvoke(i);
    }

    @Override
    public void caseVirtualInvokeInst(VirtualInvokeInst i) {
      instanceinvoke(i);
    }

    @Override
    public void caseInterfaceInvokeInst(InterfaceInvokeInst i) {
      instanceinvoke(i);
    }

    @Override
    public void caseSpecialInvokeInst(SpecialInvokeInst i) {
      instanceinvoke(i);
    }

    @Override
    public void caseThrowInst(ThrowInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Throwable") };
      add_types = null;
    }

    @Override
    public void caseAddInst(AddInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    private void bitOps(OpTypeArgInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseAndInst(AndInst i) {
      bitOps(i);
    }

    @Override
    public void caseOrInst(OrInst i) {
      bitOps(i);
    }

    @Override
    public void caseXorInst(XorInst i) {
      bitOps(i);
    }

    @Override
    public void caseArrayLengthInst(ArrayLengthInst i) {
      remove_types = new Type[] { arrayRefType() };
      add_types = new Type[] { IntType.v() };
    }

    @Override
    public void caseCmpInst(CmpInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { IntType.v() };
    }

    @Override
    public void caseCmpgInst(CmpgInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { IntType.v() };
    }

    @Override
    public void caseCmplInst(CmplInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { IntType.v() };
    }

    @Override
    public void caseDivInst(DivInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseIncInst(IncInst i) {
      remove_types = null;
      add_types = null;
    }

    @Override
    public void caseMulInst(MulInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseRemInst(RemInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseSubInst(SubInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseShlInst(ShlInst i) {
      remove_types = new Type[] { i.getOpType(), IntType.v() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseShrInst(ShrInst i) {
      remove_types = new Type[] { i.getOpType(), IntType.v() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseUshrInst(UshrInst i) {
      remove_types = new Type[] { i.getOpType(), IntType.v() };
      add_types = new Type[] { i.getOpType() };
    }

    @Override
    public void caseNewInst(NewInst i) {
      remove_types = null;
      add_types = new Type[] { i.getBaseType() };
    }

    @Override
    public void caseNegInst(NegInst i) {
      remove_types = null;
      add_types = null;
    }

    @Override
    public void caseSwapInst(SwapInst i) {
      remove_types = new Type[] { i.getFromType(), i.getToType() };
      add_types = new Type[] { i.getToType(), i.getFromType() };
    }

    @Override
    public void caseDup1Inst(Dup1Inst i) {
      remove_types = new Type[] { i.getOp1Type() };
      add_types = new Type[] { i.getOp1Type(), i.getOp1Type() };
    }

    @Override
    public void caseDup2Inst(Dup2Inst i) {
      if (!(i.getOp1Type() instanceof DoubleType || i.getOp1Type() instanceof LongType)) {
        add_types = new Type[] { i.getOp2Type(), i.getOp1Type() };
        remove_types = null;
      } else {
        add_types = new Type[] { i.getOp1Type() };
        remove_types = null;
      }
    }

    @Override
    public void caseDup1_x1Inst(Dup1_x1Inst i) {
      remove_types = new Type[] { i.getUnder1Type(), i.getOp1Type() };
      add_types = new Type[] { i.getOp1Type(), i.getUnder1Type(), i.getOp1Type() };
    }

    @Override
    public void caseDup1_x2Inst(Dup1_x2Inst i) {
      Type u1 = i.getUnder1Type();
      if (u1 instanceof DoubleType || u1 instanceof LongType) {
        remove_types = new Type[] { u1, i.getOp1Type() };
        add_types = new Type[] { i.getOp1Type(), u1, i.getOp1Type() };
      } else {
        remove_types = new Type[] { i.getUnder2Type(), u1, i.getOp1Type() };
        add_types = new Type[] { i.getOp1Type(), i.getUnder2Type(), u1, i.getOp1Type() };
      }
    }

    @Override
    public void caseDup2_x1Inst(Dup2_x1Inst i) {
      Type ot = i.getOp1Type();
      if (ot instanceof DoubleType || ot instanceof LongType) {
        remove_types = new Type[] { i.getUnder1Type(), ot };
        add_types = new Type[] { ot, i.getUnder1Type(), ot };
      } else {
        remove_types = new Type[] { i.getUnder1Type(), i.getOp2Type(), ot };
        add_types = new Type[] { i.getOp2Type(), ot, i.getUnder1Type(), i.getOp2Type(), ot };
      }
    }

    @Override
    public void caseDup2_x2Inst(Dup2_x2Inst i) {
      Type u1 = i.getUnder1Type();
      Type o1 = i.getOp1Type();
      if (u1 instanceof DoubleType || u1 instanceof LongType) {
        if (o1 instanceof DoubleType || o1 instanceof LongType) {
          remove_types = new Type[] { u1, o1 };
          add_types = new Type[] { o1, u1, o1 };
        } else {
          remove_types = new Type[] { u1, i.getOp2Type(), o1 };
          add_types = new Type[] { i.getOp2Type(), o1, u1, i.getOp2Type(), o1 };
        }
      } else if (o1 instanceof DoubleType || o1 instanceof LongType) {
        remove_types = new Type[] { i.getUnder2Type(), u1, o1 };
        add_types = new Type[] { o1, i.getUnder2Type(), u1, o1 };
      } else {
        remove_types = new Type[] { i.getUnder2Type(), u1, i.getOp2Type(), o1 };
        add_types = new Type[] { i.getOp2Type(), o1, i.getUnder2Type(), u1, i.getOp2Type(), o1 };
      }
    }

    @Override
    public void caseNewArrayInst(NewArrayInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = new Type[] { arrayRefType() };
    }

    @Override
    public void caseNewMultiArrayInst(NewMultiArrayInst i) {
      int size = i.getDimensionCount();
      remove_types = new Type[size];
      Arrays.fill(remove_types, 0, size, IntType.v());

      add_types = new Type[] { arrayRefType() };
    }

    @Override
    public void caseLookupSwitchInst(LookupSwitchInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    @Override
    public void caseTableSwitchInst(TableSwitchInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    @Override
    public void caseEnterMonitorInst(EnterMonitorInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = null;
    }

    @Override
    public void caseExitMonitorInst(ExitMonitorInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = null;
    }
  }

  /**
   * @param b
   * @return {@link Map} of each {@link Unit} to the VM operand stack types prior to executing the {@link Unit}
   */
  public static Map<Unit, Stack<Type>> calculateStacks(BafBody b) {
    Map<Unit, Stack<Type>> results = new IdentityHashMap<>();

    final StackEffectSwitch sw = new StackEffectSwitch();
    final BriefUnitGraph bug = new BriefUnitGraph(b);
    for (Unit h : bug.getHeads()) {
      RefType handlerExc = isHandlerUnit(b.getTraps(), h);
      Stack<Type> stack = results.get(h);
      if (stack == null) {
        stack = new Stack<>();
        if (handlerExc != null) {
          stack.push(handlerExc);
        }
        results.put(h, stack);
        List<Unit> worklist = new ArrayList<>();
        worklist.add(h);
        while (!worklist.isEmpty()) {
          Inst inst = (Inst) worklist.remove(0);

          inst.apply(sw);

          stack = updateStack(sw, results.get(inst));

          for (Unit next : bug.getSuccsOf(inst)) {
            Stack<Type> nxtStck = results.get(next);
            if (nxtStck != null) {
              if (nxtStck.size() != stack.size()) {
                printStack(sw, b.getUnits(), results);
                throw new java.lang.RuntimeException(
                    "Problem with stack height at: " + next + "\n\rHas Stack " + nxtStck + " but is expecting " + stack);
              }
              continue;
            }

            results.put(next, stack);
            worklist.add(next);
          }
        }
      } else {
        if (stack.size() != (handlerExc != null ? 1 : 0)) {
          throw new java.lang.RuntimeException("Problem with stack height - head expects 0 (or 1 if exception handler)");
        }
      }
    }

    return results;
  }

  private static Stack<Type> updateStack(StackEffectSwitch sw, Stack<Type> st) {
    @SuppressWarnings("unchecked")
    Stack<Type> clone = (Stack<Type>) st.clone();

    final Type[] remove_types = sw.remove_types;
    if (remove_types != null) {
      if (remove_types.length > clone.size()) {
        StringBuilder exc = new StringBuilder();
        exc.append("Expecting values on stack: ");
        for (Type element : remove_types) {
          String type = element.toString();
          if (type.trim().isEmpty()) {
            type = element instanceof RefLikeType ? "L" : "U";
          }
          exc.append(type).append("  ");
        }
        exc.append("\n\tbut only found: ");
        for (Type element : clone) {
          String type = element.toString();
          if (type.trim().isEmpty()) {
            type = element instanceof RefLikeType ? "L" : "U";
          }
          exc.append(type).append("  ");
        }

        if (sw.shouldThrow) {
          throw new RuntimeException(exc.toString());
        } else {
          logger.debug(exc.toString());
        }
      }
      for (int i = remove_types.length - 1; i >= 0; i--) {
        try {
          Type t = clone.pop();
          assert (typesAreCompatible(t, remove_types[i])); // ensure consistency
        } catch (Exception exc) {
          return null;
        }
      }
    }

    if (sw.add_types != null) {
      for (Type element : sw.add_types) {
        clone.push(element);
      }
    }

    return clone;
  }

  private static boolean typesAreCompatible(Type t1, Type t2) {
    if ((t1 == t2) || (t1 instanceof RefLikeType && t2 instanceof RefLikeType)) {
      return true;
    }
    if ((t1 instanceof IntegerType && t2 instanceof IntegerType) || (t1 instanceof LongType && t2 instanceof LongType)) {
      return true;
    }
    if (t1 instanceof DoubleType && t2 instanceof DoubleType) {
      return true;
    }
    if (t1 instanceof FloatType && t2 instanceof FloatType) {
      return true;
    }
    return false;
  }

  private static RefType isHandlerUnit(Chain<Trap> traps, Unit h) {
    for (Trap t : traps) {
      if (t.getHandlerUnit() == h) {
        return t.getException().getType();
      }
    }
    return null;
  }

  private static void printStack(StackEffectSwitch sw, PatchingChain<Unit> units, Map<Unit, Stack<Type>> stacks) {
    try {
      sw.shouldThrow = false;

      Map<Unit, Integer> indexes = new HashMap<>();
      {
        int count = 0;
        for (Iterator<Unit> it = units.snapshotIterator(); it.hasNext();) {
          indexes.put(it.next(), count++);
        }
      }
      for (Iterator<Unit> it = units.snapshotIterator(); it.hasNext();) {
        Unit unit = it.next();
        StringBuilder s = new StringBuilder();

        try {
          s.append(indexes.get(unit)).append(' ').append(unit).append("  ");
        } catch (Exception e) {
          logger.debug("Error in OpStackCalculator trying to find index of unit");
        }

        if (unit instanceof TargetArgInst) {
          s.append(indexes.get(((TargetArgInst) unit).getTarget()));
        } else if (unit instanceof TableSwitchInst) {
          TableSwitchInst tswi = (TableSwitchInst) unit;
          s.append("\r\tdefault: ").append(tswi.getDefaultTarget());
          s.append("  ").append(indexes.get(tswi.getDefaultTarget()));
          int index = 0;
          for (int x = tswi.getLowIndex(), e = tswi.getHighIndex(); x <= e; x++) {
            s.append("\r\t ").append(x).append(": ").append(tswi.getTarget(index));
            s.append("  ").append(indexes.get(tswi.getTarget(index++)));
          }
        }
        s.append("   [");
        Stack<Type> stack = stacks.get(unit);
        if (stack != null) {
          unit.apply(sw);
          stack = updateStack(sw, stack);
          if (stack == null) {
            printUnits(units, " StackTypeHeightCalc failed");
            return;
          }
          for (int i = 0, e = stack.size(); i < e; i++) {
            s.append(printType(stack.get(i)));
          }
        } else {
          s.append("***missing***");
        }
        s.append(']');
        System.out.println(s);
      }
    } finally {
      sw.shouldThrow = true;
    }
  }

  private static String printType(Type t) {
    if (t instanceof IntegerType) {
      return "I";
    } else if (t instanceof FloatType) {
      return "F";
    } else if (t instanceof DoubleType) {
      return "D";
    } else if (t instanceof LongType) {
      return "J";
    } else if (t instanceof RefLikeType) {
      return "L" + t.toString();
    } else {
      return "U(" + t.getClass().toString() + ")";
    }
  }

  private static void printUnits(PatchingChain<Unit> u, String msg) {
    System.out.println("\r\r***********  " + msg);

    HashMap<Unit, Integer> numbers = new HashMap<>();
    {
      int i = 0;
      for (Iterator<Unit> it = u.snapshotIterator(); it.hasNext();) {
        numbers.put(it.next(), i++);
      }
    }
    for (Iterator<Unit> udit = u.snapshotIterator(); udit.hasNext();) {
      final Unit unit = udit.next();
      final Integer numb = numbers.get(unit);
      if (unit instanceof TargetArgInst) {
        TargetArgInst ti = (TargetArgInst) unit;
        if (ti.getTarget() == null) {
          System.out.println(unit + " null null null null null null null null null");
          continue;
        }
        System.out.println(numb + " " + unit + "   #" + numbers.get(ti.getTarget()));
        continue;
      } else if (unit instanceof TableSwitchInst) {
        TableSwitchInst tswi = (TableSwitchInst) unit;
        System.out.println(numb + " SWITCH:");
        System.out.println("\tdefault: " + tswi.getDefaultTarget() + "  " + numbers.get(tswi.getDefaultTarget()));
        int idx = 0;
        for (int x = tswi.getLowIndex(), e = tswi.getHighIndex(); x <= e; x++) {
          System.out.println("\t " + x + ": " + tswi.getTarget(idx) + "  " + numbers.get(tswi.getTarget(idx++)));
        }
        continue;
      }
      System.out.println(numb + " " + unit);
    }
  }

  private OpStackCalculator() {
  }
}
