package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.NullType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.shimple.PhiExpr;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class ConstantArrayAnalysis extends ForwardFlowAnalysis<Unit, ConstantArrayAnalysis.ArrayState> {
  private class ArrayTypesInternal implements Cloneable {
    BitSet mustAssign;
    BitSet typeState[];
    BitSet sizeState = new BitSet(szSize);

    @Override
    public Object clone() {
      ArrayTypesInternal s;
      try {
        s = (ArrayTypesInternal) super.clone();
        s.sizeState = (BitSet) s.sizeState.clone();
        s.typeState = s.typeState.clone();
        s.mustAssign = (BitSet) s.mustAssign.clone();
        return s;
      } catch (CloneNotSupportedException e) {
        throw new InternalError();
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ArrayTypesInternal)) {
        return false;
      }
      ArrayTypesInternal otherTypes = (ArrayTypesInternal) obj;
      return otherTypes.sizeState.equals(sizeState) && Arrays.equals(typeState, otherTypes.typeState)
          && mustAssign.equals(otherTypes.mustAssign);
    }
  }

  public static class ArrayTypes {
    public Set<Integer> possibleSizes;
    public Set<Type>[] possibleTypes;

    @Override
    public String toString() {
      return "ArrayTypes [possibleSizes=" + possibleSizes + ", possibleTypes=" + Arrays.toString(possibleTypes) + "]";
    }
  }

  public class ArrayState {
    ArrayTypesInternal[] state = new ArrayTypesInternal[size];
    BitSet active = new BitSet(size);

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ArrayState)) {
        return false;
      }
      ArrayState otherState = (ArrayState) obj;
      return otherState.active.equals(active) && Arrays.equals(state, otherState.state);
    }

    public void deepCloneLocalValueSlot(int localRef, int index) {
      this.state[localRef] = (ArrayTypesInternal) this.state[localRef].clone();
      this.state[localRef].typeState[index] = (BitSet) this.state[localRef].typeState[index].clone();
    }
  }

  private Map<Local, Integer> localToInt = new HashMap<Local, Integer>();
  private Map<Type, Integer> typeToInt = new HashMap<Type, Integer>();
  private Map<Integer, Integer> sizeToInt = new HashMap<Integer, Integer>();

  private Map<Integer, Type> rvTypeToInt = new HashMap<Integer, Type>();
  private Map<Integer, Integer> rvSizeToInt = new HashMap<Integer, Integer>();

  private int size;
  private int typeSize;
  private int szSize;

  public ConstantArrayAnalysis(DirectedGraph<Unit> graph, Body b) {
    super(graph);
    for (Local l : b.getLocals()) {
      localToInt.put(l, size++);
    }
    for (Unit u : b.getUnits()) {
      Stmt s = (Stmt) u;
      if (s instanceof DefinitionStmt) {
        Type ty = ((DefinitionStmt) s).getRightOp().getType();
        if (!typeToInt.containsKey(ty)) {
          int key = typeSize++;
          typeToInt.put(ty, key);
          rvTypeToInt.put(key, ty);
        }
        if (((DefinitionStmt) s).getRightOp() instanceof NewArrayExpr) {
          NewArrayExpr nae = (NewArrayExpr) ((DefinitionStmt) s).getRightOp();
          if (nae.getSize() instanceof IntConstant) {
            int sz = ((IntConstant) nae.getSize()).value;
            if (!sizeToInt.containsKey(sz)) {
              int key = szSize++;
              sizeToInt.put(sz, key);
              rvSizeToInt.put(key, sz);
            }
          }
        }
      }
    }
    doAnalysis();
  }

  @Override
  protected void flowThrough(ArrayState in, Unit d, ArrayState out) {
    out.active.clear();
    out.active.or(in.active);
    out.state = Arrays.copyOf(in.state, in.state.length);
    if (d instanceof DefinitionStmt) {
      DefinitionStmt ds = (DefinitionStmt) d;
      Value rhs = ds.getRightOp();
      Value lhs = ds.getLeftOp();
      if (rhs instanceof NewArrayExpr) {
        Local l = (Local) lhs;
        int varRef = localToInt.get(l);
        NewArrayExpr nae = (NewArrayExpr) rhs;
        out.active.set(varRef);
        if (!(nae.getSize() instanceof IntConstant)) {
          out.state[varRef] = null;
        } else {
          int arraySize = ((IntConstant) nae.getSize()).value;
          out.state[varRef] = new ArrayTypesInternal();
          out.state[varRef].sizeState.set(sizeToInt.get(arraySize));
          out.state[varRef].typeState = new BitSet[arraySize];
          out.state[varRef].mustAssign = new BitSet(arraySize);
          for (int i = 0; i < arraySize; i++) {
            out.state[varRef].typeState[i] = new BitSet(typeSize);
          }
        }
      } else if (lhs instanceof Local && lhs.getType() instanceof ArrayType && rhs instanceof NullConstant) {
        int varRef = localToInt.get(lhs);
        out.active.clear(varRef);
        out.state[varRef] = null;
      } else if (lhs instanceof Local && rhs instanceof Local && in.state[localToInt.get(rhs)] != null
          && in.active.get(localToInt.get(rhs))) {
        int lhsRef = localToInt.get(lhs);
        int rhsRef = localToInt.get(rhs);
        out.active.set(lhsRef);
        out.state[lhsRef] = in.state[rhsRef];
        out.state[rhsRef] = null;
      } else if (lhs instanceof Local && rhs instanceof PhiExpr) {
        PhiExpr rPhi = (PhiExpr) rhs;
        int lhsRef = localToInt.get(lhs);
        out.state[lhsRef] = null;
        int i = 0;
        List<Value> phiValues = rPhi.getValues();
        for (; i < phiValues.size(); i++) {
          Value v = phiValues.get(i);
          int argRef = localToInt.get(v);
          if (!in.active.get(argRef)) {
            continue;
          }
          out.active.set(lhsRef);
          // one bottom -> all bottom
          if (in.state[argRef] == null) {
            out.state[lhsRef] = null;
            break;
          }
          if (out.state[lhsRef] == null) {
            out.state[lhsRef] = in.state[argRef];
          } else {
            out.state[lhsRef] = mergeTypeStates(in.state[argRef], out.state[lhsRef]);
          }
          out.state[argRef] = null;
        }
        for (; i < phiValues.size(); i++) {
          int argRef = localToInt.get(phiValues.get(i));
          out.state[argRef] = null;
        }
      } else if (lhs instanceof ArrayRef) {
        ArrayRef ar = (ArrayRef) lhs;
        Value indexVal = ar.getIndex();
        int localRef = localToInt.get(ar.getBase());
        if (!(indexVal instanceof IntConstant)) {
          out.state[localRef] = null;
          out.active.set(localRef);
        } else if (out.state[localRef] != null) {
          Type assignType = rhs.getType();
          int index = ((IntConstant) indexVal).value;
          assert index < out.state[localRef].typeState.length;
          out.deepCloneLocalValueSlot(localRef, index);
          assert out.state[localRef].typeState[index] != null : d;
          out.state[localRef].typeState[index].set(typeToInt.get(assignType));
          out.state[localRef].mustAssign.set(index);
        }
      } else {
        Value leftOp = lhs;
        if (leftOp instanceof Local) {
          Local defLocal = (Local) leftOp;
          int varRef = localToInt.get(defLocal);
          out.active.set(varRef);
          out.state[varRef] = null;
        }
      }
      for (ValueBox b : rhs.getUseBoxes()) {
        if (localToInt.containsKey(b.getValue())) {
          int localRef = localToInt.get(b.getValue());
          out.state[localRef] = null;
          out.active.set(localRef);
        }
      }
      if (localToInt.containsKey(rhs)) {
        int localRef = localToInt.get(rhs);
        out.state[localRef] = null;
        out.active.set(localRef);
      }
    } else {
      for (ValueBox b : d.getUseBoxes()) {
        if (localToInt.containsKey(b.getValue())) {
          int localRef = localToInt.get(b.getValue());
          out.state[localRef] = null;
          out.active.set(localRef);
        }
      }
    }
  }

  @Override
  protected ArrayState newInitialFlow() {
    return new ArrayState();
  }

  @Override
  protected void merge(ArrayState in1, ArrayState in2, ArrayState out) {
    out.active.clear();
    out.active.or(in1.active);
    out.active.or(in2.active);
    BitSet in2_excl = (BitSet) in2.active.clone();
    in2_excl.andNot(in1.active);

    for (int i = in1.active.nextSetBit(0); i >= 0; i = in1.active.nextSetBit(i + 1)) {
      if (in1.state[i] == null) {
        out.state[i] = null;
      } else if (in2.active.get(i)) {
        if (in2.state[i] == null) {
          out.state[i] = null;
        } else {
          out.state[i] = mergeTypeStates(in1.state[i], in2.state[i]);
        }
      } else {
        out.state[i] = in1.state[i];
      }
    }
    for (int i = in2_excl.nextSetBit(0); i >= 0; i = in2_excl.nextSetBit(i + 1)) {
      out.state[i] = in2.state[i];
    }
  }

  private ArrayTypesInternal mergeTypeStates(ArrayTypesInternal a1, ArrayTypesInternal a2) {
    assert a1 != null && a2 != null;
    ArrayTypesInternal toRet = new ArrayTypesInternal();
    toRet.sizeState.or(a1.sizeState);
    toRet.sizeState.or(a2.sizeState);
    int maxSize = Math.max(a1.typeState.length, a2.typeState.length);
    int commonSize = Math.min(a1.typeState.length, a2.typeState.length);
    toRet.mustAssign = new BitSet(maxSize);
    toRet.typeState = new BitSet[maxSize];
    for (int i = 0; i < commonSize; i++) {
      toRet.typeState[i] = new BitSet(typeSize);
      toRet.typeState[i].or(a1.typeState[i]);
      toRet.typeState[i].or(a2.typeState[i]);
      toRet.mustAssign.set(i, a1.mustAssign.get(i) && a2.mustAssign.get(i));
    }
    for (int i = commonSize; i < maxSize; i++) {
      if (a1.typeState.length > i) {
        toRet.typeState[i] = (BitSet) a1.typeState[i].clone();
        toRet.mustAssign.set(i, a1.mustAssign.get(i));
      } else {
        toRet.mustAssign.set(i, a2.mustAssign.get(i));
        toRet.typeState[i] = (BitSet) a2.typeState[i].clone();
      }
    }
    return toRet;
  }

  @Override
  protected void copy(ArrayState source, ArrayState dest) {
    dest.active = source.active;
    dest.state = source.state;
  }

  public boolean isConstantBefore(Stmt s, Local arrayLocal) {
    ArrayState flowResults = getFlowBefore(s);
    int varRef = localToInt.get(arrayLocal);
    return flowResults.active.get(varRef) && flowResults.state[varRef] != null;
  }

  @SuppressWarnings("unchecked")
  public ArrayTypes getArrayTypesBefore(Stmt s, Local arrayLocal) {
    if (!isConstantBefore(s, arrayLocal)) {
      return null;
    }
    ArrayTypes toRet = new ArrayTypes();
    int varRef = localToInt.get(arrayLocal);
    ArrayTypesInternal ati = getFlowBefore(s).state[varRef];
    toRet.possibleSizes = new HashSet<Integer>();
    toRet.possibleTypes = new Set[ati.typeState.length];
    for (int i = ati.sizeState.nextSetBit(0); i >= 0; i = ati.sizeState.nextSetBit(i + 1)) {
      toRet.possibleSizes.add(rvSizeToInt.get(i));
    }
    for (int i = 0; i < toRet.possibleTypes.length; i++) {
      toRet.possibleTypes[i] = new HashSet<Type>();
      for (int j = ati.typeState[i].nextSetBit(0); j >= 0; j = ati.typeState[i].nextSetBit(j + 1)) {
        toRet.possibleTypes[i].add(rvTypeToInt.get(j));
      }
      if (!ati.mustAssign.get(i)) {
        toRet.possibleTypes[i].add(NullType.v());
      }
    }
    return toRet;
  }

}
