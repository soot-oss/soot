package soot.baf.toolkits.base;

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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import soot.Body;
import soot.ErroneousType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.NullType;
import soot.RefLikeType;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.baf.AbstractInstSwitch;
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
import soot.baf.MulInst;
import soot.baf.NegInst;
import soot.baf.NewArrayInst;
import soot.baf.NewInst;
import soot.baf.NewMultiArrayInst;
import soot.baf.NopInst;
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
import soot.baf.ThrowInst;
import soot.baf.UshrInst;
import soot.baf.VirtualInvokeInst;
import soot.baf.XorInst;
import soot.jimple.IdentityRef;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.toolkits.scalar.Pair;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * @author Timothy Hoffman
 */
public enum StackTypesValidator implements BodyValidator {
  INSTANCE;
  
  public static StackTypesValidator v() {
    return INSTANCE;
  }

  @Override
  public boolean isBasicValidator() {
    return false;
  }

  @Override
  public void validate(Body body, List<ValidationException> exceptions) {
    assert (body instanceof BafBody);

    VMStateAnalysis a = new VMStateAnalysis((BafBody) body);

    // Scan through all Units in the body and make sure the stack types and
    // local types are valid for the semantics of each Unit.
    InstSwitch verif = a.createVerifier(exceptions);
    for (Unit u : body.getUnits()) {
      u.apply(verif);
    }
  }

  private static final class VMStateAnalysis extends ForwardFlowAnalysis<Unit, Type[]> {

    protected static final RefType TY_REF_CANON = RefType.v();

    // Map each Local to array index for the Local->Type arrays
    protected final Map<Local, Integer> varToIdx;
    protected final int numVars;
    // Map each Unit to the operand stack prior to executing the Unit
    protected final Map<Unit, Stack<Type>> opStacks;

    public VMStateAnalysis(BafBody body) {
      super(new ExceptionalUnitGraph(body, PedanticThrowAnalysis.v(), false));
      {
        HashMap<Local, Integer> varToIdx = new HashMap<>();
        int varNum = 0;
        for (Local l : body.getLocals()) {
          varToIdx.put(l, varNum++);
        }
        this.varToIdx = varToIdx;
        this.numVars = varNum;
      }
      this.opStacks = OpStackCalculator.calculateStacks(body);
      assert (opStacks.keySet().equals(new HashSet<>(body.getUnits())));

      doAnalysis();
    }

    protected static String toString(Stream<Type> str) {
      return str.map(t -> toString(t)).collect(Collectors.toList()).toString();
    }

    protected static String toString(Type t) {
      return (t == TY_REF_CANON) ? "RefType" : t.toString();
    }

    protected int indexOf(Local loc) {
      Integer idx = varToIdx.get(loc);
      assert (idx != null) : "Unrecognized Local: " + loc;
      return idx;
    }

    @Override
    protected void flowThrough(final Type[] in, final Unit d, final Type[] out) {
      assert (d instanceof Inst);

      // Initialize the output Local types from input local Types
      copy(in, out);

      // Update Locals based on the current instruction
      AbstractInstSwitch<Pair<Local, Type>> sw = new AbstractInstSwitch<Pair<Local, Type>>() {

        @Override
        public void caseIdentityInst(IdentityInst i) {
          assert (i.getLeftOp() instanceof Local);
          assert (i.getRightOp() instanceof IdentityRef);
          // Type of the LHS Local is updated to the RHS type
          out[indexOf((Local) i.getLeftOp())] = canonicalizeRefs(i.getRightOp().getType());
        }

        @Override
        public void caseStoreInst(StoreInst i) {
          // Type of the Local is updated to the Type from the top of the stack
          out[indexOf(i.getLocal())] = canonicalizeRefs(opStacks.get(d).peek());
        }
      };
      d.apply(sw);
    }

    @Override
    protected Type[] newInitialFlow() {
      Type[] ret = new Type[this.numVars];
      Arrays.fill(ret, UnknownType.v());
      return ret;
    }

    @Override
    protected void copy(final Type[] source, final Type[] dest) {
      System.arraycopy(source, 0, dest, 0, this.numVars);
    }

    @Override
    protected void merge(final Type[] in1, final Type[] in2, final Type[] out) {
      if (in1 == in2) {
        copy(in1, out);
      } else {
        assert (in1.length == this.numVars);
        assert (in2.length == this.numVars);
        assert (out.length == this.numVars);
        final Type tUnk = UnknownType.v();
        final Type tErr = ErroneousType.v();
        for (int i = 0, e = this.numVars; i < e; i++) {
          Type t1 = in1[i];
          Type t2 = in2[i];
          if (t1 == t2) {
            // If they are identical, the output type is the same.
            out[i] = t1;
          } else if (t1 == tUnk || t2 == tUnk) {
            // If either type is unknown/uninitialized, the output type is too.
            out[i] = tUnk;
          } else if (t1 == tErr || t2 == tErr) {
            // If either type is erroneous, the output type is too.
            out[i] = tErr;
          } else {
            // TODO: Maybe need to use ErroneousType if there is a clash.
            throw new UnsupportedOperationException("Not yet implemented!");
          }
        }
      }
    }

    /**
     * Convert all reference-like types to the canonical reference instance but preserve other types.
     * 
     * @param t
     * @return
     */
    private Type canonicalizeRefs(Type t) {
      return (t instanceof RefLikeType || t instanceof NullType) ? TY_REF_CANON
          : (t instanceof IntegerType) ? IntType.v() : t;
    }

    public InstSwitch createVerifier(final List<ValidationException> exceptions) {
      return new InstSwitch() {

        private void checkType(Inst i, Type expect, Type actual) {
          Type canonExpect = canonicalizeRefs(expect);
          Type canonActual = canonicalizeRefs(actual);
          if (!Objects.equals(canonExpect, canonActual)) {
            exceptions.add(new ValidationException(i, "Expected " + VMStateAnalysis.toString(canonExpect) + " but found "
                + VMStateAnalysis.toString(canonActual)));
          }
        }

        // Top of the stack must be 'expect' Type
        private void checkStack1(Inst i, Type expect) {
          checkType(i, expect, opStacks.get(i).peek());
        }

        // Top 2 on the stack must be 'expect' Type
        private void checkStack2(Inst i, Type expect) {
          checkStackN(i, expect, 2);
        }

        // Top 2 on the stack must be 'expect1' then 'expect2'
        private void checkStack2(Inst i, Type expect1, Type expect2) {
          Stack<Type> stk = opStacks.get(i);
          int idx = stk.size();
          checkType(i, expect1, stk.elementAt(--idx));
          checkType(i, expect2, stk.elementAt(--idx));
        }

        // Top 3 on the stack must be 'expect1' then 'expect2' then 'expect3'
        private void checkStack3(Inst i, Type expect1, Type expect2, Type expect3) {
          Stack<Type> stk = opStacks.get(i);
          int idx = stk.size();
          checkType(i, expect1, stk.elementAt(--idx));
          checkType(i, expect2, stk.elementAt(--idx));
          checkType(i, expect3, stk.elementAt(--idx));
        }

        // Top N on the stack must be 'expect' Type
        private void checkStackN(Inst i, Type expect, int count) {
          Stack<Type> stk = opStacks.get(i);
          int idx = stk.size();
          for (int j = 0; j < count; j++) {
            checkType(i, expect, stk.elementAt(--idx));
          }
        }

        @Override
        public void caseIdentityInst(IdentityInst i) {
          // No stack or Local use
        }

        @Override
        public void caseNopInst(NopInst i) {
          // No stack or Local use
        }

        @Override
        public void caseGotoInst(GotoInst i) {
          // No stack or Local use
        }

        @Override
        public void caseJSRInst(JSRInst i) {
          throw new UnsupportedOperationException("deprecated bytecode");
        }

        @Override
        public void caseReturnVoidInst(ReturnVoidInst i) {
          // No stack or Local use
        }

        @Override
        public void caseReturnInst(ReturnInst i) {
          // The top of the stack must match the type expected by the instruction.
          checkStack1(i, i.getOpType());
        }

        @Override
        public void casePushInst(PushInst i) {
          // No stack or Local use
        }

        @Override
        public void casePopInst(PopInst i) {
          // No stack or Local use
        }

        @Override
        public void caseStoreInst(StoreInst i) {
          // The top of the stack must match the type expected by the instruction.
          checkStack1(i, i.getOpType());
        }

        @Override
        public void caseLoadInst(LoadInst i) {
          // The type of the Local must match the type expected by the instruction.
          checkType(i, i.getOpType(), getFlowBefore(i)[indexOf(i.getLocal())]);
        }

        @Override
        public void caseArrayWriteInst(ArrayWriteInst i) {
          // Top of stack contains the value with the Type expected by the
          // instruction, beneath that is the index then array reference.
          checkStack3(i, i.getOpType(), IntType.v(), TY_REF_CANON);
        }

        @Override
        public void caseArrayReadInst(ArrayReadInst i) {
          // Top of stack is an index with an array reference beneath.
          checkStack2(i, IntType.v(), TY_REF_CANON);
        }

        @Override
        public void caseArrayLengthInst(ArrayLengthInst i) {
          checkStack1(i, TY_REF_CANON);
        }

        @Override
        public void caseNewArrayInst(NewArrayInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseNewMultiArrayInst(NewMultiArrayInst i) {
          checkStackN(i, IntType.v(), i.getDimensionCount());
        }

        @Override
        public void caseIfNullInst(IfNullInst i) {
          checkStack1(i, TY_REF_CANON);
        }

        @Override
        public void caseIfNonNullInst(IfNonNullInst i) {
          checkStack1(i, TY_REF_CANON);
        }

        @Override
        public void caseIfEqInst(IfEqInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseIfNeInst(IfNeInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseIfGtInst(IfGtInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseIfGeInst(IfGeInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseIfLtInst(IfLtInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseIfLeInst(IfLeInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseIfCmpEqInst(IfCmpEqInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseIfCmpNeInst(IfCmpNeInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseIfCmpGtInst(IfCmpGtInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseIfCmpGeInst(IfCmpGeInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseIfCmpLtInst(IfCmpLtInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseIfCmpLeInst(IfCmpLeInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseCmpInst(CmpInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseCmpgInst(CmpgInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseCmplInst(CmplInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseStaticGetInst(StaticGetInst i) {
          // No stack or Local use
        }

        @Override
        public void caseStaticPutInst(StaticPutInst i) {
          checkStack1(i, i.getFieldRef().type());
        }

        @Override
        public void caseFieldGetInst(FieldGetInst i) {
          checkStack1(i, TY_REF_CANON);
        }

        @Override
        public void caseFieldPutInst(FieldPutInst i) {
          // Top of stack contains the value with the Type expected by the
          // instruction, beneath that is base object reference.
          checkStack2(i, i.getFieldRef().type(), TY_REF_CANON);
        }

        @Override
        public void caseInstanceCastInst(InstanceCastInst i) {
          checkStack1(i, TY_REF_CANON);
        }

        @Override
        public void caseInstanceOfInst(InstanceOfInst i) {
          checkStack1(i, TY_REF_CANON);
        }

        @Override
        public void casePrimitiveCastInst(PrimitiveCastInst i) {
          checkStack1(i, i.getFromType());
        }

        // Top of stack should have pTypes in reverse followed by the base
        // type (if not null).
        private void checkStackForParams(Inst i, List<Type> pTypes, Type baseType) {
          Stack<Type> stk = opStacks.get(i);
          int idx = stk.size();
          final int numParams = pTypes.size();
          if (numParams > 0) {
            for (Iterator<Type> it = pTypes.listIterator(numParams - 1); it.hasNext();) {
              Type t = it.next();
              checkType(i, t, stk.elementAt(--idx));
            }
          }
          if (baseType != null) {
            checkType(i, baseType, stk.elementAt(--idx));
          }
        }

        @Override
        public void caseDynamicInvokeInst(DynamicInvokeInst i) {
          // Stack contains the parameters in reverse order.
          checkStackForParams(i, i.getMethodRef().getParameterTypes(), null);
        }

        @Override
        public void caseStaticInvokeInst(StaticInvokeInst i) {
          // Stack contains the parameters in reverse order.
          checkStackForParams(i, i.getMethodRef().getParameterTypes(), null);
        }

        @Override
        public void caseVirtualInvokeInst(VirtualInvokeInst i) {
          // Stack contains the parameters in reverse order then the base object reference.
          checkStackForParams(i, i.getMethodRef().getParameterTypes(), TY_REF_CANON);
        }

        @Override
        public void caseInterfaceInvokeInst(InterfaceInvokeInst i) {
          // Stack contains the parameters in reverse order then the base object reference.
          checkStackForParams(i, i.getMethodRef().getParameterTypes(), TY_REF_CANON);
        }

        @Override
        public void caseSpecialInvokeInst(SpecialInvokeInst i) {
          // Stack contains the parameters in reverse order then the base object reference.
          checkStackForParams(i, i.getMethodRef().getParameterTypes(), TY_REF_CANON);
        }

        @Override
        public void caseThrowInst(ThrowInst i) {
          checkStack1(i, TY_REF_CANON);
        }

        @Override
        public void caseAndInst(AndInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseOrInst(OrInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseXorInst(XorInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseAddInst(AddInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseSubInst(SubInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseMulInst(MulInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseDivInst(DivInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseRemInst(RemInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseShlInst(ShlInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseShrInst(ShrInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseUshrInst(UshrInst i) {
          checkStack2(i, i.getOpType());
        }

        @Override
        public void caseNegInst(NegInst i) {
          checkStack1(i, i.getOpType());
        }

        @Override
        public void caseIncInst(IncInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseNewInst(NewInst i) {
          // No stack or Local use
        }

        @Override
        public void caseSwapInst(SwapInst i) {
          // No stack or Local use (only stack modification)
        }

        @Override
        public void caseDup1Inst(Dup1Inst i) {
          // No stack or Local use (only stack modification)
        }

        @Override
        public void caseDup2Inst(Dup2Inst i) {
          // No stack or Local use (only stack modification)
        }

        @Override
        public void caseDup1_x1Inst(Dup1_x1Inst i) {
          // No stack or Local use (only stack modification)
        }

        @Override
        public void caseDup1_x2Inst(Dup1_x2Inst i) {
          // No stack or Local use (only stack modification)
        }

        @Override
        public void caseDup2_x1Inst(Dup2_x1Inst i) {
          // No stack or Local use (only stack modification)
        }

        @Override
        public void caseDup2_x2Inst(Dup2_x2Inst i) {
          // No stack or Local use (only stack modification)
        }

        @Override
        public void caseLookupSwitchInst(LookupSwitchInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseTableSwitchInst(TableSwitchInst i) {
          checkStack1(i, IntType.v());
        }

        @Override
        public void caseEnterMonitorInst(EnterMonitorInst i) {
          checkStack1(i, TY_REF_CANON);
        }

        @Override
        public void caseExitMonitorInst(ExitMonitorInst i) {
          checkStack1(i, TY_REF_CANON);
        }
      };
    }
  }
}
