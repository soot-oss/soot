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
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

/**
 * Checks if the locals and the operand stack will contain the correct types for each instruction in the {@link BafBody}.
 * 
 * NOTE: This validator assumes that each local will hold a single {@link Type} throughout the method. However, that is
 * actually a stronger requirement than necessary for bytecode. In fact, after running the
 * {@link soot.toolkits.scalar.LocalPacker} pass, there will likely be cases that are reported by this validator which are
 * actually safe for bytecode.
 *
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

  private static final class BitArray implements Cloneable {

    // number of bits required to store each value
    // NOTE: Although 3 is sufficient, using a power of 2 gives a reasonable
    // speedup for only a small trade-off in terms of memory usage.
    public static final int BITS_PER_VAL = 4;
    // number of values to store at each array index
    public static final int VALS_PER_IDX = Integer.SIZE / BITS_PER_VAL;
    // rightmost 'BITS_PER_VAL' bits will be '1'
    public static final int VAL_MASK = 0xFFFFFFFF >>> (Integer.SIZE - BITS_PER_VAL);

    private final int[] data;

    public BitArray(int numValues) {
      assert (numValues >= 0);
      this.data = new int[numValues / VALS_PER_IDX + (numValues % VALS_PER_IDX == 0 ? 0 : 1)];
    }

    private BitArray(int[] otherData) {
      int count = otherData.length;
      int[] temp = new int[count];
      System.arraycopy(otherData, 0, temp, 0, count);
      this.data = temp;
    }

    public int get(int index) {
      final int arrIdx = index / VALS_PER_IDX;
      final int bitShift = (index % VALS_PER_IDX) * BITS_PER_VAL;
      // Shift (unsigned) the relevant value to the right-most bits and then mask.
      return (this.data[arrIdx] >>> bitShift) & VAL_MASK;
    }

    public void set(int index, int value) {
      if ((value & VAL_MASK) != value) {
        throw new IllegalArgumentException(value + " does not fit in " + BITS_PER_VAL + " bits!");
      }

      final int arrIdx = index / VALS_PER_IDX;
      final int bitShift = (index % VALS_PER_IDX) * BITS_PER_VAL;

      // First, use the inverse of 'VAL_MASK' to set position 'i' to all zeros and then set the new value.
      this.data[arrIdx] = (this.data[arrIdx] & Integer.rotateLeft(~VAL_MASK, bitShift)) | (value << bitShift);
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
      if (obj == null || BitArray.class != obj.getClass()) {
        return false;
      }
      final BitArray other = (BitArray) obj;
      return Arrays.equals(this.data, other.data);
    }

    @Override
    public BitArray clone() {
      return new BitArray(this.data);
    }

    public void copyTo(BitArray dest) {
      if (this != dest) {
        assert (this.data.length == dest.data.length);
        System.arraycopy(this.data, 0, dest.data, 0, this.data.length);
      }
    }
  }

  private static final class VMStateAnalysis extends ForwardFlowAnalysis<Unit, BitArray> {

    protected static final Type TYPE_UNK = UnknownType.v();
    protected static final Type TYPE_REF = RefType.v();
    protected static final Type TYPE_INT = IntType.v();
    protected static final Type TYPE_DUB = DoubleType.v();
    protected static final Type TYPE_FLT = FloatType.v();
    protected static final Type TYPE_LNG = LongType.v();
    protected static final Type TYPE_ERR = ErroneousType.v();

    // NOTE: UnknownType is 0 so that a new BitArray is trivially all UnknownType
    protected static final int TYPE_UNK_BITS = 0b000;
    protected static final int TYPE_ERR_BITS = 0b111;

    /**
     * Convert all reference-like types to the canonical reference instance and all subclasses of {@link IntegerType} to
     * {@link IntType} but preserve other types.
     *
     * @param t
     *
     * @return
     */
    protected static Type canonicalize(Type t) {
      return (t instanceof RefLikeType || t instanceof NullType) ? TYPE_REF : (t instanceof IntegerType) ? TYPE_INT : t;
    }

    /**
     * Performs {@link #canonicalize(Type)} and converts the canonical {@link Type} to its 3-bit representation.
     *
     * @param type
     *
     * @return
     */
    protected static int typeToBits(Type type) {
      if (type == TYPE_UNK) {
        return TYPE_UNK_BITS;
      } else if (type == TYPE_INT || type instanceof IntegerType) {
        return 0b010;
      } else if (type == TYPE_REF || type instanceof RefLikeType || type instanceof NullType) {
        return 0b001;
      } else if (type == TYPE_DUB) {
        return 0b011;
      } else if (type == TYPE_FLT) {
        return 0b100;
      } else if (type == TYPE_LNG) {
        return 0b101;
      } else if (type == TYPE_ERR) {
        return TYPE_ERR_BITS;
      } else {
        throw new IllegalArgumentException(Objects.toString(type));
      }
    }

    /**
     * Convert the given 3-bit representation (right-most bits of the argument) to its canonical {@link Type}.
     * 
     * @param bits
     * 
     * @return
     */
    protected static Type bitsToType(int bits) {
      switch (bits) {
        case TYPE_UNK_BITS:
          return TYPE_UNK;
        case 0b001:
          return TYPE_REF;
        case 0b010:
          return TYPE_INT;
        case 0b011:
          return TYPE_DUB;
        case 0b100:
          return TYPE_FLT;
        case 0b101:
          return TYPE_LNG;
        case TYPE_ERR_BITS:
          return TYPE_ERR;
        default:
          throw new IllegalArgumentException(Integer.toString(bits));
      }
    }

    //
    protected final List<ValidationException> exceptions;
    // Map each Unit to the operand stack prior to executing the Unit
    protected final Map<Unit, Stack<Type>> opStacks;
    // Map each Local to array index for the Local->Type arrays
    protected final Map<Local, Integer> varToIdx;
    //
    protected final BitArray initFlow;

    public VMStateAnalysis(BafBody body, List<ValidationException> exceptions) {
      super(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body, PedanticThrowAnalysis.v(), false));
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
        this.initFlow = new BitArray(varNum);
      }

      doAnalysis();
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
    protected void flowThrough(final BitArray in, final Unit u, final BitArray out) {
      assert (u instanceof Inst);

      // Initialize the output Local types from input local Types
      copy(in, out);

      // Update Locals based on the current instruction
      if (u instanceof IdentityInst) {
        IdentityInst i = (IdentityInst) u;
        assert (i.getLeftOp() instanceof Local);
        assert (i.getRightOp() instanceof IdentityRef);
        // Type of the LHS Local is updated to the RHS type
        int x = indexOf((Local) i.getLeftOp());
        out.set(x, merge(out.get(x), typeToBits(i.getRightOp().getType()), u));
      } else if (u instanceof StoreInst) {
        StoreInst i = (StoreInst) u;
        // Type of the Local is updated to the Type from the top of the stack
        int x = indexOf(i.getLocal());
        out.set(x, merge(out.get(x), typeToBits(peekStackAt(u)), u));
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected BitArray newInitialFlow() {
      return initFlow.clone();
    }

    @Override
    protected boolean omissible(Unit u) {
      return !(u instanceof IdentityInst) && !(u instanceof StoreInst);
    }

    @Override
    protected void copy(BitArray in, BitArray out) {
      in.copyTo(out);
    }

    @Override
    protected void merge(BitArray in1, BitArray in2, BitArray out) {
      merge(null, in1, in2, out);
    }

    @Override
    protected void merge(Unit successor, BitArray in1, BitArray in2, BitArray out) {
      if (in1.equals(in2)) {
        copy(in1, out);
      } else {
        for (int i = 0, e = this.varToIdx.size(); i < e; i++) {
          out.set(i, merge(in1.get(i), in2.get(i), successor));
        }
      }
    }

    private int merge(final int in1, final int in2, final Unit u) {
      // If they are identical, the output type is the same.
      if (in1 == in2) {
        return in1;
      }

      // If either type is unknown (i.e. uninitialized), return the other.
      if (in1 == TYPE_UNK_BITS) {
        return in2;
      } else if (in2 == TYPE_UNK_BITS) {
        return in1;
      }

      // If either type is erroneous, the output type is too.
      if (in1 == TYPE_ERR_BITS || in2 == TYPE_ERR_BITS) {
        return TYPE_ERR_BITS;
      }

      // If there is a mismatch, return erroneous type.
      exceptions.add(new ValidationException(u,
          "Ambiguous type: '" + toString(bitsToType(in1)) + "' vs '" + toString(bitsToType(in2)) + "'"));
      return TYPE_ERR_BITS;
    }

    public InstSwitch createVerifier() {
      return new InstSwitch() {

        private void checkType(Inst i, Type expect, Type actual) {
          Type canonExpect = canonicalize(expect);
          Type canonActual = canonicalize(actual);
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
          checkType(i, i.getOpType(), bitsToType(getFlowBefore(i).get(indexOf(i.getLocal()))));
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
          checkType(i, TYPE_INT, bitsToType(getFlowBefore(i).get(indexOf(i.getLocal()))));
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
