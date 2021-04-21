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
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import soot.Body;
import soot.DoubleType;
import soot.ErroneousType;
import soot.FloatType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.LongType;
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

    VMStateAnalysis a = new VMStateAnalysis((BafBody) body, exceptions);

    // Scan through all Units in the body and make sure the stack types
    // and local types are valid for the semantics of each Unit.
    InstSwitch verif = a.createVerifier();
    for (Unit u : body.getUnits()) {
      u.apply(verif);
    }
  }

  /**
   * Wrapper class for {@code Type[]} that correctly implements equals and hashcode for the ForwardFlowAnalysis.
   */
  private static final class TypeArray implements Cloneable {

    public final Type[] data;

    public TypeArray(int count, Type t) {
      Type[] temp = new Type[count];
      for (int i = 0; i < count; i++) {
        temp[i] = t;
      }
      this.data = temp;
    }
    
    private TypeArray(Type[] otherData) {
      int count = otherData.length;
      Type[] temp = new Type[count];
      System.arraycopy(otherData, 0, temp, 0, count);
      this.data = temp;
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(this.data);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || TypeArray.class != obj.getClass()) {
        return false;
      }
      final TypeArray other = (TypeArray) obj;
      return Arrays.equals(this.data, other.data);
    }

    @Override
    public String toString() {
      return VMStateAnalysis.toString(Stream.of(this.data));
    }

    @Override
    public TypeArray clone() {
      return new TypeArray(this.data);
    }
  }

  private static final class VMStateAnalysis extends ForwardFlowAnalysis<Unit, TypeArray> {

    protected static final Type TYPE_INT = IntType.v();
    protected static final Type TYPE_REF = RefType.v();

    //
    protected final List<ValidationException> exceptions;
    // Map each Unit to the operand stack prior to executing the Unit
    protected final Map<Unit, Stack<Type>> opStacks;
    // Map each Local to array index for the Local->Type arrays
    protected final Map<Local, Integer> varToIdx;
    protected final int numVars;
    //
    protected final TypeArray initFlow;

    public VMStateAnalysis(BafBody body, List<ValidationException> exceptions) {
      super(new ExceptionalUnitGraph(body, PedanticThrowAnalysis.v(), false));
      this.exceptions = exceptions;
      this.opStacks = OpStackCalculator.calculateStacks(body);
      assert (opStacks.keySet().equals(new HashSet<>(body.getUnits())));

      {
        HashMap<Local, Integer> varToIdx = new HashMap<>();
        int varNum = 0;
        for (Local l : body.getLocals()) {
          varToIdx.put(l, varNum++);
        }
        this.varToIdx = varToIdx;
        this.numVars = varNum;
        this.initFlow = new TypeArray(varNum, UnknownType.v());
      }

      doAnalysis();
    }

    protected static String toString(Stream<Type> str) {
      return str.map(t -> toString(t)).collect(Collectors.toList()).toString();
    }

    protected static String toString(Type t) {
      return (t == TYPE_REF) ? "RefType" : t.toString();
    }

    protected int indexOf(Local loc) {
      Integer idx = varToIdx.get(loc);
      assert (idx != null) : "Unrecognized Local: " + loc;
      return idx;
    }

    protected Type peekStackAt(Unit u) {
      try {
        return opStacks.get(u).peek();
      } catch (EmptyStackException ex) {
        exceptions.add(new ValidationException(u, "Stack is empty!"));
        return ErroneousType.v();
      }
    }

    @Override
    protected void flowThrough(final TypeArray in, final Unit u, final TypeArray out) {
      assert (u instanceof Inst);

      // Initialize the output Local types from input local Types
      copy(in, out);

      // Update Locals based on the current instruction
      AbstractInstSwitch<Pair<Local, Type>> sw = new AbstractInstSwitch<Pair<Local, Type>>() {

        @Override
        public void caseIdentityInst(IdentityInst i) {
          assert (i.getLeftOp() instanceof Local);
          assert (i.getRightOp() instanceof IdentityRef);
          // Type of the LHS Local is updated to the RHS type
          int x = indexOf((Local) i.getLeftOp());
          out.data[x] = merge(out.data[x], canonicalizeRefs(i.getRightOp().getType()), u);
        }

        @Override
        public void caseStoreInst(StoreInst i) {
          // Type of the Local is updated to the Type from the top of the stack
          int x = indexOf(i.getLocal());
          out.data[x] = merge(out.data[x], canonicalizeRefs(peekStackAt(u)), u);
        }
      };
      u.apply(sw);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected TypeArray newInitialFlow() {
      return (TypeArray) initFlow.clone();
    }

    @Override
    protected void copy(final TypeArray in, final TypeArray out) {
      assert (in.data.length == this.numVars);
      assert (out.data.length == this.numVars);
      if (in != out) {
        System.arraycopy(in.data, 0, out.data, 0, this.numVars);
      }
    }

    @Override
    protected void merge(final TypeArray in1, final TypeArray in2, final TypeArray out) {
      if (in1 == in2) {
        copy(in1, out);
      } else {
        final Type[] dataIn1 = in1.data;
        final Type[] dataIn2 = in2.data;
        final Type[] dataOut = out.data;
        final int size = this.numVars;
        assert (dataIn1.length == size);
        assert (dataIn2.length == size);
        assert (dataOut.length == size);
        for (int i = 0; i < size; i++) {
          dataOut[i] = merge(dataIn1[i], dataIn2[i], null);
        }
      }
    }

    protected Type merge(final Type in1, final Type in2, final Unit u) {
      // If they are identical, the output type is the same.
      if (in1 == in2) {
        return in1;
      }

      // If either type is unknown (i.e. uninitialized), return the other.
      final Type tUnk = UnknownType.v();
      if (in1 == tUnk) {
        return in2;
      } else if (in2 == tUnk) {
        return in1;
      }

      // If either type is erroneous, the output type is too.
      final Type tErr = ErroneousType.v();
      if (in1 == tErr || in2 == tErr) {
        return tErr;
      }

      // Use ErroneousType if there is a mismatch.
      assert (in1 == TYPE_INT || in1 == TYPE_REF || in1 == DoubleType.v() || in1 == FloatType.v() || in1 == LongType.v());
      assert (in2 == TYPE_INT || in2 == TYPE_REF || in2 == DoubleType.v() || in2 == FloatType.v() || in2 == LongType.v());
      exceptions.add(new ValidationException(u, "Ambiguous type: '" + toString(in1) + "' vs '" + toString(in2) + "'"));
      return tErr;
    }

    /**
     * Convert all reference-like types to the canonical reference instance but preserve other types.
     * 
     * @param t
     * @return
     */
    private Type canonicalizeRefs(Type t) {
      return (t instanceof RefLikeType || t instanceof NullType) ? TYPE_REF : (t instanceof IntegerType) ? TYPE_INT : t;
    }

    public InstSwitch createVerifier() {
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
          checkType(i, expect, peekStackAt(i));
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
          checkType(i, i.getOpType(), getFlowBefore(i).data[indexOf(i.getLocal())]);
        }

        @Override
        public void caseArrayWriteInst(ArrayWriteInst i) {
          // Top of stack contains the value with the Type expected by the
          // instruction, beneath that is the index then array reference.
          checkStack3(i, i.getOpType(), TYPE_INT, TYPE_REF);
        }

        @Override
        public void caseArrayReadInst(ArrayReadInst i) {
          // Top of stack is an index with an array reference beneath.
          checkStack2(i, TYPE_INT, TYPE_REF);
        }

        @Override
        public void caseArrayLengthInst(ArrayLengthInst i) {
          checkStack1(i, TYPE_REF);
        }

        @Override
        public void caseNewArrayInst(NewArrayInst i) {
          checkStack1(i, TYPE_INT);
        }

        @Override
        public void caseNewMultiArrayInst(NewMultiArrayInst i) {
          checkStackN(i, TYPE_INT, i.getDimensionCount());
        }

        @Override
        public void caseIfNullInst(IfNullInst i) {
          checkStack1(i, TYPE_REF);
        }

        @Override
        public void caseIfNonNullInst(IfNonNullInst i) {
          checkStack1(i, TYPE_REF);
        }

        @Override
        public void caseIfEqInst(IfEqInst i) {
          checkStack1(i, TYPE_INT);
        }

        @Override
        public void caseIfNeInst(IfNeInst i) {
          checkStack1(i, TYPE_INT);
        }

        @Override
        public void caseIfGtInst(IfGtInst i) {
          checkStack1(i, TYPE_INT);
        }

        @Override
        public void caseIfGeInst(IfGeInst i) {
          checkStack1(i, TYPE_INT);
        }

        @Override
        public void caseIfLtInst(IfLtInst i) {
          checkStack1(i, TYPE_INT);
        }

        @Override
        public void caseIfLeInst(IfLeInst i) {
          checkStack1(i, TYPE_INT);
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
          checkStack1(i, TYPE_REF);
        }

        @Override
        public void caseFieldPutInst(FieldPutInst i) {
          // Top of stack contains the value with the Type expected by the
          // instruction, beneath that is base object reference.
          checkStack2(i, i.getFieldRef().type(), TYPE_REF);
        }

        @Override
        public void caseInstanceCastInst(InstanceCastInst i) {
          checkStack1(i, TYPE_REF);
        }

        @Override
        public void caseInstanceOfInst(InstanceOfInst i) {
          checkStack1(i, TYPE_REF);
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
            for (ListIterator<Type> it = pTypes.listIterator(numParams); it.hasPrevious();) {
              Type t = it.previous();
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
          checkStackForParams(i, i.getMethodRef().getParameterTypes(), TYPE_REF);
        }

        @Override
        public void caseInterfaceInvokeInst(InterfaceInvokeInst i) {
          // Stack contains the parameters in reverse order then the base object reference.
          checkStackForParams(i, i.getMethodRef().getParameterTypes(), TYPE_REF);
        }

        @Override
        public void caseSpecialInvokeInst(SpecialInvokeInst i) {
          // Stack contains the parameters in reverse order then the base object reference.
          checkStackForParams(i, i.getMethodRef().getParameterTypes(), TYPE_REF);
        }

        @Override
        public void caseThrowInst(ThrowInst i) {
          checkStack1(i, TYPE_REF);
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
          // Top of stack is an integer with the operand value underneath.
          checkStack2(i, TYPE_INT, i.getOpType());
        }

        @Override
        public void caseShrInst(ShrInst i) {
          // Top of stack is an integer with the operand value underneath.
          checkStack2(i, TYPE_INT, i.getOpType());
        }

        @Override
        public void caseUshrInst(UshrInst i) {
          // Top of stack is an integer with the operand value underneath.
          checkStack2(i, TYPE_INT, i.getOpType());
        }

        @Override
        public void caseNegInst(NegInst i) {
          checkStack1(i, i.getOpType());
        }

        @Override
        public void caseIncInst(IncInst i) {
          // The type of the Local must be an integer type.
          checkType(i, TYPE_INT, getFlowBefore(i).data[indexOf(i.getLocal())]);
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
          checkStack1(i, TYPE_INT);
        }

        @Override
        public void caseTableSwitchInst(TableSwitchInst i) {
          checkStack1(i, TYPE_INT);
        }

        @Override
        public void caseEnterMonitorInst(EnterMonitorInst i) {
          checkStack1(i, TYPE_REF);
        }

        @Override
        public void caseExitMonitorInst(ExitMonitorInst i) {
          checkStack1(i, TYPE_REF);
        }
      };
    }
  }
}
