package soot.toolkits.exceptions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 John Jorgensen
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

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import heros.solver.IDESolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.FastHierarchy;
import soot.G;
import soot.IntegerType;
import soot.Local;
import soot.LongType;
import soot.NullType;
import soot.PatchingChain;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.Singletons;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.baf.AddInst;
import soot.baf.AndInst;
import soot.baf.ArrayLengthInst;
import soot.baf.ArrayReadInst;
import soot.baf.ArrayWriteInst;
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
import soot.grimp.GrimpValueSwitch;
import soot.grimp.NewInvokeExpr;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.BreakpointStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.EqExpr;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GotoStmt;
import soot.jimple.GtExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.LtExpr;
import soot.jimple.MethodHandle;
import soot.jimple.MethodType;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StmtSwitch;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.shimple.PhiExpr;
import soot.shimple.ShimpleValueSwitch;
import soot.toolkits.exceptions.ThrowableSet.Pair;

/**
 * A {@link ThrowAnalysis} which returns the set of runtime exceptions and errors that might be thrown by the bytecode
 * instructions represented by a unit, as indicated by the Java Virtual Machine specification. I.e. this analysis is based
 * entirely on the &ldquo;opcode&rdquo; of the unit, the types of its arguments, and the values of constant arguments.
 *
 * <p>
 * The <code>mightThrow</code> methods could be declared static. They are left virtual to facilitate testing. For example, to
 * verify that the expressions in a method call are actually being examined, a test case can override the
 * mightThrow(SootMethod) with an implementation which returns the empty set instead of all possible exceptions.
 */
public class UnitThrowAnalysis extends AbstractThrowAnalysis {

  protected final ThrowableSet.Manager mgr = ThrowableSet.Manager.v();

  // Cache the response to mightThrowImplicitly():
  private final ThrowableSet implicitThrowExceptions = ThrowableSet.Manager.v().VM_ERRORS
      .add(ThrowableSet.Manager.v().NULL_POINTER_EXCEPTION).add(ThrowableSet.Manager.v().ILLEGAL_MONITOR_STATE_EXCEPTION);

  /**
   * Constructs a <code>UnitThrowAnalysis</code> for inclusion in Soot's global variable manager, {@link G}.
   *
   * @param g
   *          guarantees that the constructor may only be called from {@link Singletons}.
   */
  public UnitThrowAnalysis(Singletons.Global g) {
    this(false);
  }

  /**
   * A protected constructor for use by unit tests.
   */
  protected UnitThrowAnalysis() {
    this(false);
  }

  /**
   * Returns the single instance of <code>UnitThrowAnalysis</code>.
   *
   * @return Soot's <code>UnitThrowAnalysis</code>.
   */
  public static UnitThrowAnalysis v() {
    return G.v().soot_toolkits_exceptions_UnitThrowAnalysis();
  }

  protected final boolean isInterproc;

  protected UnitThrowAnalysis(boolean isInterproc) {
    this.isInterproc = isInterproc;
  }

  public static UnitThrowAnalysis interproceduralAnalysis = null;

  public static UnitThrowAnalysis interproc() {
    if (interproceduralAnalysis == null) {
      interproceduralAnalysis = new UnitThrowAnalysis(true);
    }
    return interproceduralAnalysis;
  }

  protected ThrowableSet defaultResult() {
    return mgr.VM_ERRORS;
  }

  protected UnitSwitch unitSwitch(SootMethod sm) {
    return new UnitSwitch(sm);
  }

  protected ValueSwitch valueSwitch() {
    return new ValueSwitch();
  }

  @Override
  public ThrowableSet mightThrow(Unit u) {
    return mightThrow(u, null);
  }

  public ThrowableSet mightThrow(Unit u, SootMethod sm) {
    UnitSwitch sw = unitSwitch(sm);
    u.apply(sw);
    return sw.getResult();
  }

  @Override
  public ThrowableSet mightThrowImplicitly(ThrowInst t) {
    return implicitThrowExceptions;
  }

  @Override
  public ThrowableSet mightThrowImplicitly(ThrowStmt t) {
    return implicitThrowExceptions;
  }

  protected ThrowableSet mightThrow(Value v) {
    ValueSwitch sw = valueSwitch();
    v.apply(sw);
    return sw.getResult();
  }

  protected ThrowableSet mightThrow(SootMethodRef m) {
    // The throw analysis is used in the front-ends. Conseqeuently, some
    // methods might not yet be loaded. If this is the case, we make
    // conservative assumptions.
    SootMethod sm = m.tryResolve();
    if (sm != null) {
      return mightThrow(sm);
    } else {
      return mgr.ALL_THROWABLES;
    }
  }

  /**
   * Returns the set of types that might be thrown as a result of calling the specified method.
   *
   * @param sm
   *          method whose exceptions are to be returned.
   *
   * @return a representation of the set of {@link java.lang.Throwable Throwable} types that <code>m</code> might throw.
   */
  protected ThrowableSet mightThrow(SootMethod sm) {
    if (!isInterproc) {
      return ThrowableSet.Manager.v().ALL_THROWABLES;
    }
    return methodToThrowSet.getUnchecked(sm);
  }

  protected final LoadingCache<SootMethod, ThrowableSet> methodToThrowSet
      = IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<SootMethod, ThrowableSet>() {
        @Override
        public ThrowableSet load(SootMethod sm) throws Exception {
          return mightThrow(sm, new HashSet<SootMethod>());
        }
      });

  /**
   * Returns the set of types that might be thrown as a result of calling the specified method.
   *
   * @param sm
   *          method whose exceptions are to be returned.
   * @param doneSet
   *          The set of methods that were already processed
   *
   * @return a representation of the set of {@link java.lang.Throwable Throwable} types that <code>m</code> might throw.
   */
  private ThrowableSet mightThrow(SootMethod sm, Set<SootMethod> doneSet) {
    // Do not run in loops
    if (!doneSet.add(sm)) {
      return ThrowableSet.Manager.v().EMPTY;
    }

    // If we don't have body, we silently ignore the method. This is
    // unsound, but would otherwise always bloat our result set.
    if (!sm.hasActiveBody()) {
      return ThrowableSet.Manager.v().EMPTY;
    }

    // We need a mapping between unit and exception
    final PatchingChain<Unit> units = sm.getActiveBody().getUnits();
    Map<Unit, Collection<Trap>> unitToTraps
        = sm.getActiveBody().getTraps().isEmpty() ? null : new HashMap<Unit, Collection<Trap>>();
    for (Trap t : sm.getActiveBody().getTraps()) {
      for (Iterator<Unit> unitIt = units.iterator(t.getBeginUnit(), units.getPredOf(t.getEndUnit())); unitIt.hasNext();) {
        Unit unit = unitIt.next();

        Collection<Trap> unitsForTrap = unitToTraps.get(unit);
        if (unitsForTrap == null) {
          unitsForTrap = new ArrayList<Trap>();
          unitToTraps.put(unit, unitsForTrap);
        }
        unitsForTrap.add(t);
      }
    }

    ThrowableSet methodSet = ThrowableSet.Manager.v().EMPTY;
    if (sm.hasActiveBody()) {
      Body methodBody = sm.getActiveBody();

      for (Unit u : methodBody.getUnits()) {
        if (u instanceof Stmt) {
          Stmt stmt = (Stmt) u;

          ThrowableSet curStmtSet;
          if (stmt.containsInvokeExpr()) {
            InvokeExpr inv = stmt.getInvokeExpr();
            curStmtSet = mightThrow(inv.getMethod(), doneSet);
          } else {
            curStmtSet = mightThrow(u, sm);
          }

          // The exception might be caught along the way
          if (unitToTraps != null) {
            Collection<Trap> trapsForUnit = unitToTraps.get(stmt);
            if (trapsForUnit != null) {
              for (Trap t : trapsForUnit) {
                Pair p = curStmtSet.whichCatchableAs(t.getException().getType());
                curStmtSet = curStmtSet.remove(p.getCaught());
              }
            }
          }

          methodSet = methodSet.add(curStmtSet);
        }
      }
    }

    return methodSet;
  }

  private static final IntConstant INT_CONSTANT_ZERO = IntConstant.v(0);
  private static final LongConstant LONG_CONSTANT_ZERO = LongConstant.v(0);

  protected class UnitSwitch implements InstSwitch, StmtSwitch {

    // Asynchronous errors are always possible:
    protected ThrowableSet result = defaultResult();
    protected SootMethod sm;

    public UnitSwitch(SootMethod sm) {
      this.sm = sm;
    }

    ThrowableSet getResult() {
      return result;
    }

    @Override
    public void caseReturnVoidInst(ReturnVoidInst i) {
      result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
    }

    @Override
    public void caseReturnInst(ReturnInst i) {
      result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
    }

    @Override
    public void caseNopInst(NopInst i) {
    }

    @Override
    public void caseGotoInst(GotoInst i) {
    }

    @Override
    public void caseJSRInst(JSRInst i) {
    }

    @Override
    public void casePushInst(PushInst i) {
    }

    @Override
    public void casePopInst(PopInst i) {
    }

    @Override
    public void caseIdentityInst(IdentityInst i) {
    }

    @Override
    public void caseStoreInst(StoreInst i) {
    }

    @Override
    public void caseLoadInst(LoadInst i) {
    }

    @Override
    public void caseArrayWriteInst(ArrayWriteInst i) {
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mgr.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
      if (i.getOpType() instanceof RefType) {
        result = result.add(mgr.ARRAY_STORE_EXCEPTION);
      }
    }

    @Override
    public void caseArrayReadInst(ArrayReadInst i) {
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mgr.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
    }

    @Override
    public void caseIfNullInst(IfNullInst i) {
    }

    @Override
    public void caseIfNonNullInst(IfNonNullInst i) {
    }

    @Override
    public void caseIfEqInst(IfEqInst i) {
    }

    @Override
    public void caseIfNeInst(IfNeInst i) {
    }

    @Override
    public void caseIfGtInst(IfGtInst i) {
    }

    @Override
    public void caseIfGeInst(IfGeInst i) {
    }

    @Override
    public void caseIfLtInst(IfLtInst i) {
    }

    @Override
    public void caseIfLeInst(IfLeInst i) {
    }

    @Override
    public void caseIfCmpEqInst(IfCmpEqInst i) {
    }

    @Override
    public void caseIfCmpNeInst(IfCmpNeInst i) {
    }

    @Override
    public void caseIfCmpGtInst(IfCmpGtInst i) {
    }

    @Override
    public void caseIfCmpGeInst(IfCmpGeInst i) {
    }

    @Override
    public void caseIfCmpLtInst(IfCmpLtInst i) {
    }

    @Override
    public void caseIfCmpLeInst(IfCmpLeInst i) {
    }

    @Override
    public void caseStaticGetInst(StaticGetInst i) {
      result = result.add(mgr.INITIALIZATION_ERRORS);
    }

    @Override
    public void caseStaticPutInst(StaticPutInst i) {
      result = result.add(mgr.INITIALIZATION_ERRORS);
    }

    @Override
    public void caseFieldGetInst(FieldGetInst i) {
      result = result.add(mgr.RESOLVE_FIELD_ERRORS);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
    }

    @Override
    public void caseFieldPutInst(FieldPutInst i) {
      result = result.add(mgr.RESOLVE_FIELD_ERRORS);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
    }

    @Override
    public void caseInstanceCastInst(InstanceCastInst i) {
      result = result.add(mgr.RESOLVE_CLASS_ERRORS);
      result = result.add(mgr.CLASS_CAST_EXCEPTION);
    }

    @Override
    public void caseInstanceOfInst(InstanceOfInst i) {
      result = result.add(mgr.RESOLVE_CLASS_ERRORS);
    }

    @Override
    public void casePrimitiveCastInst(PrimitiveCastInst i) {
    }

    @Override
    public void caseDynamicInvokeInst(DynamicInvokeInst i) {
      result = result.add(mgr.RESOLVE_METHOD_ERRORS);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mgr.INITIALIZATION_ERRORS);
      // might throw anything
      result = result.add(ThrowableSet.Manager.v().ALL_THROWABLES);
    }

    @Override
    public void caseStaticInvokeInst(StaticInvokeInst i) {
      result = result.add(mgr.INITIALIZATION_ERRORS);
      result = result.add(mightThrow(i.getMethodRef()));
    }

    @Override
    public void caseVirtualInvokeInst(VirtualInvokeInst i) {
      result = result.add(mgr.RESOLVE_METHOD_ERRORS);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mightThrow(i.getMethodRef()));
    }

    @Override
    public void caseInterfaceInvokeInst(InterfaceInvokeInst i) {
      result = result.add(mgr.RESOLVE_METHOD_ERRORS);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mightThrow(i.getMethodRef()));
    }

    @Override
    public void caseSpecialInvokeInst(SpecialInvokeInst i) {
      result = result.add(mgr.RESOLVE_METHOD_ERRORS);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mightThrow(i.getMethodRef()));
    }

    @Override
    public void caseThrowInst(ThrowInst i) {
      result = mightThrowImplicitly(i);
      result = result.add(mightThrowExplicitly(i));
    }

    @Override
    public void caseAddInst(AddInst i) {
    }

    @Override
    public void caseAndInst(AndInst i) {
    }

    @Override
    public void caseOrInst(OrInst i) {
    }

    @Override
    public void caseXorInst(XorInst i) {
    }

    @Override
    public void caseArrayLengthInst(ArrayLengthInst i) {
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
    }

    @Override
    public void caseCmpInst(CmpInst i) {
    }

    @Override
    public void caseCmpgInst(CmpgInst i) {
    }

    @Override
    public void caseCmplInst(CmplInst i) {
    }

    @Override
    public void caseDivInst(DivInst i) {
      if (i.getOpType() instanceof IntegerType || i.getOpType() == LongType.v()) {
        result = result.add(mgr.ARITHMETIC_EXCEPTION);
      }
    }

    @Override
    public void caseIncInst(IncInst i) {
    }

    @Override
    public void caseMulInst(MulInst i) {
    }

    @Override
    public void caseRemInst(RemInst i) {
      if (i.getOpType() instanceof IntegerType || i.getOpType() == LongType.v()) {
        result = result.add(mgr.ARITHMETIC_EXCEPTION);
      }
    }

    @Override
    public void caseSubInst(SubInst i) {
    }

    @Override
    public void caseShlInst(ShlInst i) {
    }

    @Override
    public void caseShrInst(ShrInst i) {
    }

    @Override
    public void caseUshrInst(UshrInst i) {
    }

    @Override
    public void caseNewInst(NewInst i) {
      result = result.add(mgr.INITIALIZATION_ERRORS);
    }

    @Override
    public void caseNegInst(NegInst i) {
    }

    @Override
    public void caseSwapInst(SwapInst i) {
    }

    @Override
    public void caseDup1Inst(Dup1Inst i) {
    }

    @Override
    public void caseDup2Inst(Dup2Inst i) {
    }

    @Override
    public void caseDup1_x1Inst(Dup1_x1Inst i) {
    }

    @Override
    public void caseDup1_x2Inst(Dup1_x2Inst i) {
    }

    @Override
    public void caseDup2_x1Inst(Dup2_x1Inst i) {
    }

    @Override
    public void caseDup2_x2Inst(Dup2_x2Inst i) {
    }

    @Override
    public void caseNewArrayInst(NewArrayInst i) {
      result = result.add(mgr.RESOLVE_CLASS_ERRORS); // Could be omitted for primitive arrays.
      result = result.add(mgr.NEGATIVE_ARRAY_SIZE_EXCEPTION);
    }

    @Override
    public void caseNewMultiArrayInst(NewMultiArrayInst i) {
      result = result.add(mgr.RESOLVE_CLASS_ERRORS);
      result = result.add(mgr.NEGATIVE_ARRAY_SIZE_EXCEPTION);
    }

    @Override
    public void caseLookupSwitchInst(LookupSwitchInst i) {
    }

    @Override
    public void caseTableSwitchInst(TableSwitchInst i) {
    }

    @Override
    public void caseEnterMonitorInst(EnterMonitorInst i) {
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
    }

    @Override
    public void caseExitMonitorInst(ExitMonitorInst i) {
      result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
    }

    @Override
    public void caseAssignStmt(AssignStmt s) {
      Value lhs = s.getLeftOp();
      if (lhs instanceof ArrayRef && (lhs.getType() instanceof UnknownType || lhs.getType() instanceof RefType)) {
        // This corresponds to an aastore byte code.
        result = result.add(mgr.ARRAY_STORE_EXCEPTION);
      }
      result = result.add(mightThrow(s.getLeftOp()));
      result = result.add(mightThrow(s.getRightOp()));
    }

    @Override
    public void caseBreakpointStmt(BreakpointStmt s) {
    }

    @Override
    public void caseEnterMonitorStmt(EnterMonitorStmt s) {
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mightThrow(s.getOp()));
    }

    @Override
    public void caseExitMonitorStmt(ExitMonitorStmt s) {
      result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mightThrow(s.getOp()));
    }

    @Override
    public void caseGotoStmt(GotoStmt s) {
    }

    @Override
    public void caseIdentityStmt(IdentityStmt s) {
    }
    // Perhaps IdentityStmt shouldn't even return VM_ERRORS,
    // since it corresponds to no bytecode instructions whatsoever.

    @Override
    public void caseIfStmt(IfStmt s) {
      result = result.add(mightThrow(s.getCondition()));
    }

    @Override
    public void caseInvokeStmt(InvokeStmt s) {
      result = result.add(mightThrow(s.getInvokeExpr()));
    }

    @Override
    public void caseLookupSwitchStmt(LookupSwitchStmt s) {
      result = result.add(mightThrow(s.getKey()));
    }

    @Override
    public void caseNopStmt(NopStmt s) {
    }

    @Override
    public void caseRetStmt(RetStmt s) {
      // Soot should never produce any RetStmt, since
      // it implements jsr with gotos.
    }

    @Override
    public void caseReturnStmt(ReturnStmt s) {
      // result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
      // result = result.add(mightThrow(s.getOp()));
    }

    @Override
    public void caseReturnVoidStmt(ReturnVoidStmt s) {
      // result = result.add(mgr.ILLEGAL_MONITOR_STATE_EXCEPTION);
    }

    @Override
    public void caseTableSwitchStmt(TableSwitchStmt s) {
      result = result.add(mightThrow(s.getKey()));
    }

    @Override
    public void caseThrowStmt(ThrowStmt s) {
      result = mightThrowImplicitly(s);
      result = result.add(mightThrowExplicitly(s, sm));
    }

    @Override
    public void defaultCase(Object obj) {
    }
  }

  protected class ValueSwitch implements GrimpValueSwitch, ShimpleValueSwitch {

    // Asynchronous errors are always possible:
    protected ThrowableSet result = defaultResult();

    ThrowableSet getResult() {
      return result;
    }

    // Declared by ConstantSwitch interface:

    @Override
    public void caseDoubleConstant(DoubleConstant c) {
    }

    @Override
    public void caseFloatConstant(FloatConstant c) {
    }

    @Override
    public void caseIntConstant(IntConstant c) {
    }

    @Override
    public void caseLongConstant(LongConstant c) {
    }

    @Override
    public void caseNullConstant(NullConstant c) {
    }

    @Override
    public void caseStringConstant(StringConstant c) {
    }

    @Override
    public void caseClassConstant(ClassConstant c) {
    }

    @Override
    public void caseMethodHandle(MethodHandle handle) {
    }

    @Override
    public void caseMethodType(MethodType type) {
    }

    // Declared by ExprSwitch interface:

    @Override
    public void caseAddExpr(AddExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseAndExpr(AndExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseCmpExpr(CmpExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseCmpgExpr(CmpgExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseCmplExpr(CmplExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseDivExpr(DivExpr expr) {
      caseBinopDivExpr(expr);
    }

    @Override
    public void caseEqExpr(EqExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseNeExpr(NeExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseGeExpr(GeExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseGtExpr(GtExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseLeExpr(LeExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseLtExpr(LtExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseMulExpr(MulExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseOrExpr(OrExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseRemExpr(RemExpr expr) {
      caseBinopDivExpr(expr);
    }

    @Override
    public void caseShlExpr(ShlExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseShrExpr(ShrExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseUshrExpr(UshrExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseSubExpr(SubExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseXorExpr(XorExpr expr) {
      caseBinopExpr(expr);
    }

    @Override
    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr expr) {
      caseInstanceInvokeExpr(expr);
    }

    @Override
    public void caseSpecialInvokeExpr(SpecialInvokeExpr expr) {
      caseInstanceInvokeExpr(expr);
    }

    @Override
    public void caseStaticInvokeExpr(StaticInvokeExpr expr) {
      result = result.add(mgr.INITIALIZATION_ERRORS);
      for (int i = 0; i < expr.getArgCount(); i++) {
        result = result.add(mightThrow(expr.getArg(i)));
      }
      result = result.add(mightThrow(expr.getMethodRef()));
    }

    @Override
    public void caseVirtualInvokeExpr(VirtualInvokeExpr expr) {
      caseInstanceInvokeExpr(expr);
    }

    // INSERTED for invokedynamic UnitThrowAnalysis.java
    @Override
    public void caseDynamicInvokeExpr(DynamicInvokeExpr expr) {
      // caseInstanceInvokeExpr(expr);
    }

    @Override
    public void caseCastExpr(CastExpr expr) {
      result = result.add(mgr.RESOLVE_CLASS_ERRORS);
      Type fromType = expr.getOp().getType();
      Type toType = expr.getCastType();
      if (toType instanceof RefLikeType) {
        // fromType might still be unknown when we are called,
        // but toType will have a value.
        FastHierarchy h = Scene.v().getOrMakeFastHierarchy();
        if (fromType == null || fromType instanceof UnknownType
            || ((!(fromType instanceof NullType)) && (!h.canStoreType(fromType, toType)))) {
          result = result.add(mgr.CLASS_CAST_EXCEPTION);
        }
      }
      result = result.add(mightThrow(expr.getOp()));
    }

    @Override
    public void caseInstanceOfExpr(InstanceOfExpr expr) {
      result = result.add(mgr.RESOLVE_CLASS_ERRORS);
      result = result.add(mightThrow(expr.getOp()));
    }

    @Override
    public void caseNewArrayExpr(NewArrayExpr expr) {
      if (expr.getBaseType() instanceof RefLikeType) {
        result = result.add(mgr.RESOLVE_CLASS_ERRORS);
      }
      Value count = expr.getSize();
      if ((!(count instanceof IntConstant)) || (((IntConstant) count).isLessThan(INT_CONSTANT_ZERO))) {
        result = result.add(mgr.NEGATIVE_ARRAY_SIZE_EXCEPTION);
      }
      result = result.add(mightThrow(count));
    }

    @Override
    public void caseNewMultiArrayExpr(NewMultiArrayExpr expr) {
      result = result.add(mgr.RESOLVE_CLASS_ERRORS);
      for (int i = 0; i < expr.getSizeCount(); i++) {
        Value count = expr.getSize(i);
        if ((!(count instanceof IntConstant)) || (((IntConstant) count).isLessThan(INT_CONSTANT_ZERO))) {
          result = result.add(mgr.NEGATIVE_ARRAY_SIZE_EXCEPTION);
        }
        result = result.add(mightThrow(count));
      }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void caseNewExpr(NewExpr expr) {
      result = result.add(mgr.INITIALIZATION_ERRORS);
      for (ValueBox box : expr.getUseBoxes()) {
        result = result.add(mightThrow(box.getValue()));
      }
    }

    @Override
    public void caseLengthExpr(LengthExpr expr) {
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mightThrow(expr.getOp()));
    }

    @Override
    public void caseNegExpr(NegExpr expr) {
      result = result.add(mightThrow(expr.getOp()));
    }

    // Declared by RefSwitch interface:

    @Override
    public void caseArrayRef(ArrayRef ref) {
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mgr.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
      result = result.add(mightThrow(ref.getBase()));
      result = result.add(mightThrow(ref.getIndex()));
    }

    @Override
    public void caseStaticFieldRef(StaticFieldRef ref) {
      result = result.add(mgr.INITIALIZATION_ERRORS);
    }

    @Override
    public void caseInstanceFieldRef(InstanceFieldRef ref) {
      result = result.add(mgr.RESOLVE_FIELD_ERRORS);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      result = result.add(mightThrow(ref.getBase()));
    }

    @Override
    public void caseParameterRef(ParameterRef v) {
    }

    @Override
    public void caseCaughtExceptionRef(CaughtExceptionRef v) {
    }

    @Override
    public void caseThisRef(ThisRef v) {
    }

    @Override
    public void caseLocal(Local l) {
    }

    @Override
    public void caseNewInvokeExpr(NewInvokeExpr e) {
      caseStaticInvokeExpr(e);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void casePhiExpr(PhiExpr e) {
      for (ValueBox box : e.getUseBoxes()) {
        result = result.add(mightThrow(box.getValue()));
      }
    }

    @Override
    public void defaultCase(Object obj) {
    }

    // The remaining cases are not declared by GrimpValueSwitch,
    // but are used to factor out code common to several cases.

    private void caseBinopExpr(BinopExpr expr) {
      result = result.add(mightThrow(expr.getOp1()));
      result = result.add(mightThrow(expr.getOp2()));
    }

    private void caseBinopDivExpr(BinopExpr expr) {
      // Factors out code common to caseDivExpr and caseRemExpr.
      // The checks against constant divisors would perhaps be
      // better performed in a later pass, post-constant-propagation.
      Value divisor = expr.getOp2();
      Type divisorType = divisor.getType();
      if (divisorType instanceof UnknownType) {
        result = result.add(mgr.ARITHMETIC_EXCEPTION);
      } else if ((divisorType instanceof IntegerType)
          && ((!(divisor instanceof IntConstant)) || (((IntConstant) divisor).equals(INT_CONSTANT_ZERO)))) {
        result = result.add(mgr.ARITHMETIC_EXCEPTION);
      } else if ((divisorType == LongType.v())
          && ((!(divisor instanceof LongConstant)) || (((LongConstant) divisor).equals(LONG_CONSTANT_ZERO)))) {
        result = result.add(mgr.ARITHMETIC_EXCEPTION);
      }
      caseBinopExpr(expr);
    }

    private void caseInstanceInvokeExpr(InstanceInvokeExpr expr) {
      result = result.add(mgr.RESOLVE_METHOD_ERRORS);
      result = result.add(mgr.NULL_POINTER_EXCEPTION);
      for (int i = 0; i < expr.getArgCount(); i++) {
        result = result.add(mightThrow(expr.getArg(i)));
      }
      result = result.add(mightThrow(expr.getBase()));
      result = result.add(mightThrow(expr.getMethodRef()));
    }
  }
}
