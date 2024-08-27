package soot.dotnet.exceptiontoolkits;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

import soot.FastHierarchy;
import soot.G;
import soot.IntegerType;
import soot.Local;
import soot.LongType;
import soot.NullType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.Singletons;
import soot.SootMethod;
import soot.Type;
import soot.UnknownType;
import soot.Value;
import soot.baf.ArrayLengthInst;
import soot.baf.ArrayReadInst;
import soot.baf.ArrayWriteInst;
import soot.baf.DivInst;
import soot.baf.DynamicInvokeInst;
import soot.baf.EnterMonitorInst;
import soot.baf.ExitMonitorInst;
import soot.baf.FieldGetInst;
import soot.baf.FieldPutInst;
import soot.baf.IdentityInst;
import soot.baf.InstanceCastInst;
import soot.baf.InstanceOfInst;
import soot.baf.InterfaceInvokeInst;
import soot.baf.LoadInst;
import soot.baf.LookupSwitchInst;
import soot.baf.NewArrayInst;
import soot.baf.NewInst;
import soot.baf.NewMultiArrayInst;
import soot.baf.PrimitiveCastInst;
import soot.baf.RemInst;
import soot.baf.ReturnInst;
import soot.baf.ReturnVoidInst;
import soot.baf.SpecialInvokeInst;
import soot.baf.StaticGetInst;
import soot.baf.StaticInvokeInst;
import soot.baf.StaticPutInst;
import soot.baf.StoreInst;
import soot.baf.TableSwitchInst;
import soot.baf.ThrowInst;
import soot.baf.VirtualInvokeInst;
import soot.dotnet.types.DotNetBasicTypes;
import soot.grimp.NewInvokeExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.DivExpr;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MethodHandle;
import soot.jimple.MethodType;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JCheckedAddExpr;
import soot.jimple.internal.JCheckedCastExpr;
import soot.jimple.internal.JCheckedMulExpr;
import soot.jimple.internal.JCheckedSubExpr;
import soot.toolkits.exceptions.ThrowableSet;
import soot.toolkits.exceptions.UnitThrowAnalysis;

/**
 * ThrowAnalysis for .NET CIL Instructions Described Exceptions are described in the ECMA-335 specification
 */
public class DotnetThrowAnalysis extends UnitThrowAnalysis {

  /**
   * Constructs a <code>DalvikThrowAnalysis</code> for inclusion in Soot's global variable manager, {@link G}.
   *
   * @param g
   *          guarantees that the constructor may only be called from {@link Singletons}.
   */
  public DotnetThrowAnalysis(Singletons.Global g) {
  }

  /**
   * Returns the single instance of <code>DalvikThrowAnalysis</code>.
   *
   * @return Soot's <code>UnitThrowAnalysis</code>.
   */
  public static DotnetThrowAnalysis v() {
    return G.v().soot_dotnet_exceptiontoolkits_DotnetThrowAnalysis();
  }

  protected DotnetThrowAnalysis(boolean isInterproc) {
    super(isInterproc);
  }

  public DotnetThrowAnalysis(Singletons.Global g, boolean isInterproc) {
    super(isInterproc);
  }

  public static DotnetThrowAnalysis interproceduralAnalysis = null;

  @Override
  protected ThrowableSet defaultResult() {
    return mgr.EMPTY;
  }

  @Override
  protected UnitSwitch unitSwitch(SootMethod sm) {
    return new UnitThrowAnalysis.UnitSwitch(sm) {

      // no throw
      @Override
      public void caseReturnVoidInst(ReturnVoidInst i) {
      }

      // no throw
      @Override
      public void caseReturnInst(ReturnInst i) {

      }

      @Override
      public void caseIdentityInst(IdentityInst i) {
        Value rightOp = i.getRightOp();
        if (rightOp instanceof CaughtExceptionRef) {
          result = result.add(Scene.v().getRefType(i.getLeftOp().getType().toString()));
        }
      }

      @Override
      public void caseStoreInst(StoreInst i) {
      }

      @Override
      public void caseLoadInst(LoadInst i) {
      }

      @Override
      public void caseArrayWriteInst(ArrayWriteInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_INDEXOUTOFRANGEEXCEPTION));
        if (i.getOpType() instanceof RefType) {
          result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_ARRAYTYPEMISMATCHEXCEPTION));
        }
      }

      @Override
      public void caseArrayReadInst(ArrayReadInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_INDEXOUTOFRANGEEXCEPTION));
      }

      /**
       * Load static field
       */
      @Override
      public void caseStaticGetInst(StaticGetInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_FIELDACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGFIELDEXCEPTION));
      }

      /**
       * Store static field
       */
      @Override
      public void caseStaticPutInst(StaticPutInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_FIELDACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGFIELDEXCEPTION));
      }

      @Override
      public void caseFieldGetInst(FieldGetInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_FIELDACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGFIELDEXCEPTION));
      }

      @Override
      public void caseFieldPutInst(FieldPutInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_FIELDACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGFIELDEXCEPTION));
      }

      @Override
      public void caseInstanceCastInst(InstanceCastInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_INVALIDCASTEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_TYPELOADEXCEPTION));
      }

      @Override
      public void caseInstanceOfInst(InstanceOfInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_TYPELOADEXCEPTION));
      }

      @Override
      public void casePrimitiveCastInst(PrimitiveCastInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_TYPELOADEXCEPTION));
        // result = result.add(Scene.v().getRefType(DotnetBasicTypes.SYSTEM_OUTOFMEMORYEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_INVALIDCASTEXCEPTION));
      }

      // does not exist
      @Override
      public void caseDynamicInvokeInst(DynamicInvokeInst i) {
      }

      @Override
      public void caseStaticInvokeInst(StaticInvokeInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_METHODACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGMETHODEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(mightThrow(i.getMethodRef()));
      }

      @Override
      public void caseVirtualInvokeInst(VirtualInvokeInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_METHODACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGMETHODEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(mightThrow(i.getMethodRef()));
      }

      @Override
      public void caseInterfaceInvokeInst(InterfaceInvokeInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_METHODACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGMETHODEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(mightThrow(i.getMethodRef()));
      }

      @Override
      public void caseSpecialInvokeInst(SpecialInvokeInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_METHODACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGMETHODEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(mightThrow(i.getMethodRef()));
      }

      @Override
      public void caseThrowInst(ThrowInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        // all throwables which throw can throw
        result = result.add(mightThrowExplicitly(i));
      }

      @Override
      public void caseArrayLengthInst(ArrayLengthInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
      }

      @Override
      public void caseDivInst(DivInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_ARITHMETICEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_DIVIDEBYZEROEXCEPTION));
      }

      @Override
      public void caseRemInst(RemInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_ARITHMETICEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_DIVIDEBYZEROEXCEPTION));
      }

      @Override
      public void caseNewInst(NewInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_INVALIDOPERATIONEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_METHODACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OUTOFMEMORYEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGMETHODEXCEPTION));
      }

      @Override
      public void caseNewArrayInst(NewArrayInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OUTOFMEMORYEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OVERFLOWEXCEPTION));
      }

      @Override
      public void caseNewMultiArrayInst(NewMultiArrayInst i) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OUTOFMEMORYEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OVERFLOWEXCEPTION));
      }

      @Override
      public void caseLookupSwitchInst(LookupSwitchInst i) {
      }

      @Override
      public void caseTableSwitchInst(TableSwitchInst i) {
      }

      @Override
      public void caseEnterMonitorInst(EnterMonitorInst i) {
      }

      @Override
      public void caseExitMonitorInst(ExitMonitorInst i) {
      }

      @Override
      public void caseAssignStmt(AssignStmt s) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_TYPELOADEXCEPTION));
        // handle all exceptions in expressions
        result = result.add(mightThrow(s.getLeftOp()));
        result = result.add(mightThrow(s.getRightOp()));
      }

      // does not exist
      @Override
      public void caseEnterMonitorStmt(EnterMonitorStmt s) {
      }

      // does not exist
      @Override
      public void caseExitMonitorStmt(ExitMonitorStmt s) {
      }

      @Override
      public void caseGotoStmt(GotoStmt s) {
      }

      @Override
      public void caseIdentityStmt(IdentityStmt s) {
        Value rightOp = s.getRightOp();
        if (rightOp instanceof CaughtExceptionRef) {
          result = result.add(Scene.v().getRefType(s.getLeftOp().getType().toString()));
        }
      }

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
      }

      @Override
      public void caseNopStmt(NopStmt s) {
      }

      @Override
      public void caseRetStmt(RetStmt s) {
      }

      @Override
      public void caseReturnStmt(ReturnStmt s) {
      }

      @Override
      public void caseReturnVoidStmt(ReturnVoidStmt s) {
      }

      @Override
      public void caseTableSwitchStmt(TableSwitchStmt s) {
        result = result.add(mightThrow(s.getKey()));
      }

      @Override
      public void caseThrowStmt(ThrowStmt s) {
        // result = mightThrowImplicitly(s);
        result = result.add(Scene.v().getRefType(s.getOp().getType().toString()));
        result = result.add(mightThrowExplicitly(s, sm));
      }

      @Override
      public void defaultCase(Object obj) {
      }

    };
  }

  @Override
  protected ValueSwitch valueSwitch() {
    return new UnitThrowAnalysis.ValueSwitch() {

      @Override
      public void caseCheckedAddExpr(JCheckedAddExpr v) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OVERFLOWEXCEPTION));
      }

      @Override
      public void caseCheckedMulExpr(JCheckedMulExpr v) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OVERFLOWEXCEPTION));
      }

      @Override
      public void caseCheckedSubExpr(JCheckedSubExpr v) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OVERFLOWEXCEPTION));
      }

      @Override
      public void caseCheckedCastExpr(JCheckedCastExpr v) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OVERFLOWEXCEPTION));
      }

      @Override
      public void caseClassConstant(ClassConstant c) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_TYPELOADEXCEPTION));
        // ignore, only refanyval
        // result = result.add(Scene.v().getRefType(DotnetBasicTypes.SYSTEM_INVALIDCASTEXCEPTION));
      }

      public void caseMethodHandle(MethodHandle handle) {
      }

      public void caseMethodType(MethodType type) {
      }

      // Declared by ExprSwitch interface:

      @Override
      public void caseDivExpr(DivExpr expr) {
        caseBinopDivExpr(expr);
      }

      @Override
      public void caseRemExpr(RemExpr expr) {
        caseBinopDivExpr(expr);
      }

      @Override
      public void caseInterfaceInvokeExpr(InterfaceInvokeExpr expr) {
        caseInstanceInvokeExpr(expr, false);
      }

      @Override
      public void caseSpecialInvokeExpr(SpecialInvokeExpr expr) {
        caseInstanceInvokeExpr(expr, false);
      }

      @Override
      public void caseStaticInvokeExpr(StaticInvokeExpr expr) {
        caseInstanceInvokeExpr(expr, true);
      }

      public void caseVirtualInvokeExpr(VirtualInvokeExpr expr) {
        caseInstanceInvokeExpr(expr, false);
      }

      // does not exist
      @Override
      public void caseDynamicInvokeExpr(DynamicInvokeExpr expr) {
      }

      @Override
      public void caseCastExpr(CastExpr expr) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OUTOFMEMORYEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));

        Type fromType = expr.getOp().getType();
        Type toType = expr.getCastType();
        if (!(fromType instanceof RefLikeType)) {
          result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_INVALIDCASTEXCEPTION));
        }
        if (toType instanceof RefLikeType) {
          // if typeTok cannot be found - see ECMA 335
          FastHierarchy h = Scene.v().getOrMakeFastHierarchy();
          if (fromType == null || fromType instanceof UnknownType
              || ((!(fromType instanceof NullType)) && (!h.canStoreType(fromType, toType)))) {
            result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_TYPELOADEXCEPTION));
          }
        }
        result = result.add(mightThrow(expr.getOp()));
      }

      @Override
      public void caseInstanceOfExpr(InstanceOfExpr expr) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_TYPELOADEXCEPTION));
        result = result.add(mightThrow(expr.getOp()));
      }

      @Override
      public void caseNewArrayExpr(NewArrayExpr expr) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OUTOFMEMORYEXCEPTION));
        Value count = expr.getSize();
        if (!(count instanceof IntConstant) || ((IntConstant) count).lessThan(INT_CONSTANT_ZERO).equals(INT_CONSTANT_ZERO)) {
          result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OVERFLOWEXCEPTION));
        }
        result = result.add(mightThrow(count));
      }

      @Override
      public void caseNewMultiArrayExpr(NewMultiArrayExpr expr) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OUTOFMEMORYEXCEPTION));
        for (int i = 0; i < expr.getSizeCount(); i++) {
          Value count = expr.getSize(i);
          if ((!(count instanceof IntConstant))
              || (((IntConstant) count).lessThan(INT_CONSTANT_ZERO).equals(INT_CONSTANT_ZERO))) {
            result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_OVERFLOWEXCEPTION));
          }
          result = result.add(mightThrow(count));
        }
      }

      @Override
      public void caseNewExpr(NewExpr expr) {
      }

      @Override
      public void caseLengthExpr(LengthExpr expr) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(mightThrow(expr.getOp()));
      }

      // Declared by RefSwitch interface:

      @Override
      public void caseArrayRef(ArrayRef ref) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_INDEXOUTOFRANGEEXCEPTION));
        result = result.add(mightThrow(ref.getBase()));
        result = result.add(mightThrow(ref.getIndex()));
      }

      // static field load
      @Override
      public void caseStaticFieldRef(StaticFieldRef ref) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_FIELDACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGFIELDEXCEPTION));
      }

      // Non-static field
      @Override
      public void caseInstanceFieldRef(InstanceFieldRef ref) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_FIELDACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_INVALIDOPERATIONEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGFIELDEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        result = result.add(mightThrow(ref.getBase()));
      }

      @Override
      public void caseParameterRef(ParameterRef v) {
      }

      @Override
      public void caseCaughtExceptionRef(CaughtExceptionRef v) {
        result = result.add(Scene.v().getRefType(v.getType().toString()));
      }

      @Override
      public void caseLocal(Local l) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_VERIFICATIONEXCEPTION));
      }

      @Override
      public void caseNewInvokeExpr(NewInvokeExpr e) {
        caseStaticInvokeExpr(e);
      }

      @Override
      public void defaultCase(Object obj) {
      }

      // The remaining cases are not declared by GrimpValueSwitch,
      // but are used to factor out code common to several cases.

      private void caseBinopDivExpr(BinopExpr expr) {
        // Factors out code common to caseDivExpr and caseRemExpr.
        // The checks against constant divisors would perhaps be
        // better performed in a later pass, post-constant-propagation.
        Value divisor = expr.getOp2();
        Type divisorType = divisor.getType();
        if ((divisorType instanceof UnknownType)
            || (divisorType instanceof IntegerType
                && (!(divisor instanceof IntConstant) || divisor.equals(INT_CONSTANT_ZERO)))
            || (divisorType instanceof LongType
                && (!(divisor instanceof LongConstant) || divisor.equals(LONG_CONSTANT_ZERO)))
        // floating points never throw an exception
        ) {
          result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_DIVIDEBYZEROEXCEPTION));
        }
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_ARITHMETICEXCEPTION));
        // result = result.add(Scene.v().getRefType(DotnetBasicTypes.SYSTEM_OVERFLOWEXCEPTION)); //Implementation specific
        // (Microsoft)

        result = result.add(mightThrow(expr.getOp1()));
        result = result.add(mightThrow(expr.getOp2()));
      }

      private void caseInstanceInvokeExpr(InvokeExpr expr, boolean staticInvoke) {
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_METHODACCESSEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_MISSINGMETHODEXCEPTION));
        result = result.add(Scene.v().getRefType(DotNetBasicTypes.SYSTEM_NULLREFERENCEEXCEPTION));
        // result = result.add(Scene.v().getRefType(DotnetBasicTypes.SYSTEM_SECURITYEXCEPTION));
        for (int i = 0; i < expr.getArgCount(); i++) {
          result = result.add(mightThrow(expr.getArg(i)));
        }
        if (!staticInvoke) {
          result = result.add(mightThrow(((InstanceInvokeExpr) expr).getBase()));
        }
        result = result.add(mightThrow(expr.getMethodRef()));
      }
    };

  }

  private static final IntConstant INT_CONSTANT_ZERO = IntConstant.v(0);
  private static final LongConstant LONG_CONSTANT_ZERO = LongConstant.v(0);

}
