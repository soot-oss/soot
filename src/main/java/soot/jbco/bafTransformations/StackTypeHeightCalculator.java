package soot.jbco.bafTransformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
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
 * @author Michael Batchelder
 * 
 *         Created on 3-May-2006
 */
public class StackTypeHeightCalculator {
  private static final Logger logger = LoggerFactory.getLogger(StackTypeHeightCalculator.class);

  protected class StackEffectSwitch implements InstSwitch {

    public boolean shouldThrow = true;
    public Map<Local, Local> bafToJLocals = null;
    public Type remove_types[] = null;
    public Type add_types[] = null;

    public void caseReturnInst(ReturnInst i) {
      remove_types = new Type[] { i.getOpType() };
      add_types = null;
    }

    public void caseReturnVoidInst(ReturnVoidInst i) {
      remove_types = null;
      add_types = null;
    }

    public void caseNopInst(NopInst i) {
      remove_types = null;
      add_types = null;
    }

    public void caseGotoInst(GotoInst i) {
      remove_types = null;
      add_types = null;
    }

    public void caseJSRInst(JSRInst i) {
      remove_types = null;
      // add_types=new Type[]{RefType.v()};
      add_types = new Type[] { StmtAddressType.v() };
    }

    public void casePushInst(PushInst i) {
      remove_types = null;
      add_types = new Type[] { i.getConstant().getType() };
    }

    public void casePopInst(PopInst i) {
      remove_types = new Type[] { ((soot.baf.internal.BPopInst) i).getType() };
      add_types = null;
    }

    public void caseIdentityInst(IdentityInst i) {
      remove_types = null;
      add_types = null;
    }

    public void caseStoreInst(StoreInst i) {
      remove_types = new Type[] { ((AbstractOpTypeInst) i).getOpType() };
      add_types = null;
    }

    public void caseLoadInst(LoadInst i) {
      remove_types = null;
      add_types = null;
      if (bafToJLocals != null) {
        Local jl = (Local) bafToJLocals.get(i.getLocal());
        if (jl != null) {
          add_types = new Type[] { jl.getType() };
        }
      }

      if (add_types == null) {
        add_types = new Type[] { i.getOpType() };
      }
    }

    public void caseArrayWriteInst(ArrayWriteInst i) {
      // RefType replaces the arraytype
      remove_types = new Type[] { RefType.v(), IntType.v(), i.getOpType() };
      add_types = null;
    }

    public void caseArrayReadInst(ArrayReadInst i) {
      remove_types = new Type[] { RefType.v(), IntType.v() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseIfNullInst(IfNullInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = null;
    }

    public void caseIfNonNullInst(IfNonNullInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = null;
    }

    public void caseIfEqInst(IfEqInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    public void caseIfNeInst(IfNeInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    public void caseIfGtInst(IfGtInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    public void caseIfGeInst(IfGeInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    public void caseIfLtInst(IfLtInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    public void caseIfLeInst(IfLeInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    public void caseIfCmpEqInst(IfCmpEqInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    public void caseIfCmpNeInst(IfCmpNeInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    public void caseIfCmpGtInst(IfCmpGtInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    public void caseIfCmpGeInst(IfCmpGeInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    public void caseIfCmpLtInst(IfCmpLtInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    public void caseIfCmpLeInst(IfCmpLeInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = null;
    }

    public void caseStaticGetInst(StaticGetInst i) {
      remove_types = null;
      add_types = new Type[] { i.getField().getType() };
    }

    public void caseStaticPutInst(StaticPutInst i) {
      remove_types = new Type[] { i.getField().getType() };
      add_types = null;
    }

    public void caseFieldGetInst(FieldGetInst i) {
      remove_types = new Type[] { i.getField().getDeclaringClass().getType() };
      add_types = new Type[] { i.getField().getType() };
    }

    public void caseFieldPutInst(FieldPutInst i) {
      remove_types = new Type[] { i.getField().getDeclaringClass().getType(), i.getField().getType() };
      add_types = null;
    }

    public void caseInstanceCastInst(InstanceCastInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = new Type[] { i.getCastType() };
    }

    public void caseInstanceOfInst(InstanceOfInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = new Type[] { IntType.v() };
    }

    public void casePrimitiveCastInst(PrimitiveCastInst i) {
      remove_types = new Type[] { i.getFromType() };
      add_types = new Type[] { i.getToType() };
    }

    public void caseDynamicInvokeInst(DynamicInvokeInst i) {
      SootMethod m = i.getMethod();
      Object args[] = m.getParameterTypes().toArray();
      remove_types = new Type[args.length];
      for (int ii = 0; ii < args.length; ii++) {
        remove_types[ii] = (Type) args[ii];
      }

      if (m.getReturnType() instanceof VoidType) {
        add_types = null;
      } else {
        add_types = new Type[] { m.getReturnType() };
      }
    }

    public void caseStaticInvokeInst(StaticInvokeInst i) {
      SootMethod m = i.getMethod();
      Object args[] = m.getParameterTypes().toArray();
      remove_types = new Type[args.length];
      for (int ii = 0; ii < args.length; ii++) {
        remove_types[ii] = (Type) args[ii];
      }

      if (m.getReturnType() instanceof VoidType) {
        add_types = null;
      } else {
        add_types = new Type[] { m.getReturnType() };
      }
    }

    private void instanceinvoke(MethodArgInst i) {
      SootMethod m = i.getMethod();

      int length = m.getParameterCount();
      remove_types = new Type[length + 1];
      remove_types[0] = RefType.v();
      System.arraycopy(m.getParameterTypes().toArray(), 0, remove_types, 1, length);

      if (m.getReturnType() instanceof VoidType) {
        add_types = null;
      } else {
        add_types = new Type[] { m.getReturnType() };
      }
    }

    public void caseVirtualInvokeInst(VirtualInvokeInst i) {
      instanceinvoke(i);
    }

    public void caseInterfaceInvokeInst(InterfaceInvokeInst i) {
      instanceinvoke(i);
    }

    public void caseSpecialInvokeInst(SpecialInvokeInst i) {
      instanceinvoke(i);
    }

    public void caseThrowInst(ThrowInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Throwable") };
      add_types = null;
    }

    public void caseAddInst(AddInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    private void bitOps(OpTypeArgInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseAndInst(AndInst i) {
      bitOps(i);
    }

    public void caseOrInst(OrInst i) {
      bitOps(i);
    }

    public void caseXorInst(XorInst i) {
      bitOps(i);
    }

    public void caseArrayLengthInst(ArrayLengthInst i) {
      remove_types = new Type[] { RefType.v() };
      add_types = new Type[] { IntType.v() };
    }

    public void caseCmpInst(CmpInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { IntType.v() };
    }

    public void caseCmpgInst(CmpgInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { IntType.v() };
    }

    public void caseCmplInst(CmplInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { IntType.v() };
    }

    public void caseDivInst(DivInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseIncInst(IncInst i) {
      remove_types = null;
      add_types = null;
    }

    public void caseMulInst(MulInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseRemInst(RemInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseSubInst(SubInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseShlInst(ShlInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseShrInst(ShrInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseUshrInst(UshrInst i) {
      remove_types = new Type[] { i.getOpType(), i.getOpType() };
      add_types = new Type[] { i.getOpType() };
    }

    public void caseNewInst(NewInst i) {
      remove_types = null;
      add_types = new Type[] { i.getBaseType() };
    }

    public void caseNegInst(NegInst i) {
      remove_types = null;
      add_types = null;
    }

    public void caseSwapInst(SwapInst i) {
      remove_types = new Type[] { i.getFromType(), i.getToType() };
      add_types = new Type[] { i.getToType(), i.getFromType() };
    }

    public void caseDup1Inst(Dup1Inst i) {
      remove_types = new Type[] { i.getOp1Type() };
      add_types = new Type[] { i.getOp1Type(), i.getOp1Type() };
    }

    public void caseDup2Inst(Dup2Inst i) {
      if (!(i.getOp1Type() instanceof DoubleType || i.getOp1Type() instanceof LongType)) {
        add_types = new Type[] { i.getOp2Type(), i.getOp1Type() };
        remove_types = null;
      } else {
        add_types = new Type[] { i.getOp1Type() };
        remove_types = null;
      }
    }

    public void caseDup1_x1Inst(Dup1_x1Inst i) {
      remove_types = new Type[] { i.getUnder1Type(), i.getOp1Type() };
      add_types = new Type[] { i.getOp1Type(), i.getUnder1Type(), i.getOp1Type() };
    }

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

    public void caseNewArrayInst(NewArrayInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = new Type[] { RefType.v() };
    }

    public void caseNewMultiArrayInst(NewMultiArrayInst i) {
      remove_types = new Type[i.getDimensionCount()];
      for (int ii = 0; ii < remove_types.length; ii++) {
        remove_types[ii] = IntType.v();
      }
      add_types = new Type[] { RefType.v() };
    }

    public void caseLookupSwitchInst(LookupSwitchInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    public void caseTableSwitchInst(TableSwitchInst i) {
      remove_types = new Type[] { IntType.v() };
      add_types = null;
    }

    public void caseEnterMonitorInst(EnterMonitorInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = null;
    }

    public void caseExitMonitorInst(ExitMonitorInst i) {
      remove_types = new Type[] { RefType.v("java.lang.Object") };
      add_types = null;
    }
  }

  public static StackEffectSwitch sw = new StackTypeHeightCalculator().new StackEffectSwitch();
  public static BriefUnitGraph bug = null;

  public static Map<Unit, Stack<Type>> calculateStackHeights(Body b, Map<Local, Local> b2JLocs) {
    sw.bafToJLocals = b2JLocs;
    return calculateStackHeights(b, true);
  }

  public static Map<Unit, Stack<Type>> calculateStackHeights(Body b) {
    sw.bafToJLocals = null;
    return calculateStackHeights(b, false);
  }

  public static Map<Unit, Stack<Type>> calculateStackHeights(Body b, boolean jimpleLocals) {
    if (!(b instanceof BafBody)) {
      throw new java.lang.RuntimeException("Expecting Baf Body");
      // System.out.println("\n"+b.getMethod().getName());
    }

    Map<Unit, Stack<Type>> results = new HashMap<Unit, Stack<Type>>();
    bug = new BriefUnitGraph(b);
    List<Unit> heads = bug.getHeads();
    for (int i = 0; i < heads.size(); i++) {
      Unit h = heads.get(i);
      RefType handlerExc = isHandlerUnit(b.getTraps(), h);
      Stack<Type> stack = (Stack<Type>) results.get(h);
      if (stack != null) {
        if (stack.size() != (handlerExc != null ? 1 : 0)) {
          throw new java.lang.RuntimeException("Problem with stack height - head expects ZERO or one if handler");
        }
        continue;
      }

      List<Unit> worklist = new ArrayList<Unit>();
      stack = new Stack<Type>();
      if (handlerExc != null) {
        stack.push(handlerExc);
      }
      results.put(h, stack);
      worklist.add(h);
      while (!worklist.isEmpty()) {
        Inst inst = (Inst) worklist.remove(0);

        inst.apply(sw);

        stack = updateStack(sw, (Stack<Type>) results.get(inst));
        Iterator<Unit> lit = bug.getSuccsOf(inst).iterator();
        while (lit.hasNext()) {
          Unit next = lit.next();
          Stack<Type> nxtStck = results.get(next);
          if (nxtStck != null) {
            if (nxtStck.size() != stack.size()) {
              printStack(b.getUnits(), results, false);
              throw new java.lang.RuntimeException(
                  "Problem with stack height at: " + next + "\n\rHas Stack " + nxtStck + " but is expecting " + stack);
            }
            continue;
          }

          results.put(next, stack);
          worklist.add(next);
        }
      }
    }

    return results;
  }

  public static Stack<Type> updateStack(Unit u, Stack<Type> st) {
    u.apply(sw);
    return updateStack(sw, st);
  }

  public static Stack<Type> updateStack(StackEffectSwitch sw, Stack<Type> st) {
    @SuppressWarnings("unchecked")
    Stack<Type> clone = (Stack<Type>) st.clone();

    if (sw.remove_types != null) {
      if (sw.remove_types.length > clone.size()) {
        String exc = "Expecting values on stack: ";
        for (Type element : sw.remove_types) {
          String type = element.toString();
          if (type.trim().length() == 0) {
            type = element instanceof RefLikeType ? "L" : "U";
          }

          exc += type + "  ";
        }
        exc += "\n\tbut only found: ";
        for (int i = 0; i < clone.size(); i++) {
          String type = clone.get(i).toString();
          if (type.trim().length() == 0) {
            type = clone.get(i) instanceof RefLikeType ? "L" : "U";
          }

          exc += type + "  ";
        }

        if (sw.shouldThrow) {
          throw new RuntimeException(exc);
        } else {
          logger.debug("" + exc);
        }
      }
      for (int i = sw.remove_types.length - 1; i >= 0; i--) {
        try {
          Type t = clone.pop();

          if (!checkTypes(t, sw.remove_types[i])) {
            // System.out.println("Incompatible types: " + t + " : "+sw.remove_types[i]);
          }
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

  private static boolean checkTypes(Type t1, Type t2) {
    if (t1 == t2) {
      return true;
    }

    if (t1 instanceof RefLikeType && t2 instanceof RefLikeType) {
      return true;
    }

    if (t1 instanceof IntegerType && t2 instanceof IntegerType) {
      return true;
    }

    if (t1 instanceof LongType && t2 instanceof LongType) {
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

  public static void printStack(PatchingChain<Unit> units, Map<Unit, Stack<Type>> stacks, boolean before) {

    int count = 0;
    sw.shouldThrow = false;
    Map<Unit, Integer> indexes = new HashMap<Unit, Integer>();
    Iterator<Unit> it = units.snapshotIterator();
    while (it.hasNext()) {
      indexes.put(it.next(), new Integer(count++));
    }
    it = units.snapshotIterator();
    while (it.hasNext()) {
      String s = "";
      Unit unit = it.next();
      if (unit instanceof TargetArgInst) {
        Object t = ((TargetArgInst) unit).getTarget();
        s = indexes.get(t).toString();
      } else if (unit instanceof TableSwitchInst) {
        TableSwitchInst tswi = (TableSwitchInst) unit;
        s += "\r\tdefault: " + tswi.getDefaultTarget() + "  " + indexes.get(tswi.getDefaultTarget());
        int index = 0;
        for (int x = tswi.getLowIndex(); x <= tswi.getHighIndex(); x++) {
          s += "\r\t " + x + ": " + tswi.getTarget(index) + "  " + indexes.get(tswi.getTarget(index++));
        }
      }
      try {
        s = indexes.get(unit) + " " + unit + "  " + s + "   [";
      } catch (Exception e) {
        logger.debug("Error in StackTypeHeightCalculator trying to find index of unit");
      }
      Stack<Type> stack = stacks.get(unit);
      if (stack != null) {
        if (!before) {
          ((Unit) unit).apply(sw);
          stack = updateStack(sw, stack);
          if (stack == null) {
            soot.jbco.util.Debugger.printUnits(units, " StackTypeHeightCalc failed");
            sw.shouldThrow = true;
            return;
          }
        }
        for (int i = 0; i < stack.size(); i++) {
          s += printType(stack.get(i));
        }
      } else {
        s += "***missing***";
      }
      System.out.println(s + "]");
    }
    sw.shouldThrow = true;
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

      // if (t instanceof RefType && ((RefType)t).getSootClass() != null)
      // return "L(" + ((RefType)t).getSootClass().getName()+")";
      // else
      return "L" + t.toString();
    } else {
      return "U(" + t.getClass().toString() + ")";
    }
  }

  private static RefType isHandlerUnit(Chain<Trap> traps, Unit h) {
    Iterator<Trap> it = traps.iterator();
    while (it.hasNext()) {
      Trap t = (Trap) it.next();
      if (t.getHandlerUnit() == h) {
        return t.getException().getType();
      }
    }
    return null;
  }

  public static Stack<Type> getAfterStack(Body b, Unit u) {
    Stack<Type> stack = calculateStackHeights(b).get(u);
    u.apply(sw);
    return updateStack(sw, stack);
  }

  public static Stack<Type> getAfterStack(Stack<Type> beforeStack, Unit u) {
    u.apply(sw);
    return updateStack(sw, beforeStack);
  }
}
