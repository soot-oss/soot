package soot.jimple.toolkits.typing;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2000 Etienne Gagnon.  All rights reserved.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LocalGenerator;
import soot.LongType;
import soot.NullType;
import soot.RefType;
import soot.Scene;
import soot.SootMethodRef;
import soot.TrapManager;
import soot.Type;
import soot.Value;
import soot.jimple.AbstractStmtSwitch;
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
import soot.jimple.ConditionExpr;
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
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.RemExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;

class ConstraintChecker extends AbstractStmtSwitch {
  private static final Logger logger = LoggerFactory.getLogger(ConstraintChecker.class);

  private final ClassHierarchy hierarchy;
  private final boolean fix; // if true, fix constraint violations

  private JimpleBody stmtBody;
  private LocalGenerator localGenerator;

  public ConstraintChecker(TypeResolver resolver, boolean fix) {
    this.fix = fix;
    this.hierarchy = resolver.hierarchy();
  }

  public void check(Stmt stmt, JimpleBody stmtBody) throws TypeException {
    try {
      this.stmtBody = stmtBody;
      this.localGenerator = Scene.v().createLocalGenerator(stmtBody);
      stmt.apply(this);
    } catch (RuntimeTypeException e) {
      logger.error(e.getMessage(), e);
      throw new TypeException(e.getMessage(), e);
    }
  }

  @SuppressWarnings("serial")
  private static class RuntimeTypeException extends RuntimeException {
    RuntimeTypeException(String message) {
      super(message);
    }
  }

  static void error(String message) {
    throw new RuntimeTypeException(message);
  }

  private void handleInvokeExpr(InvokeExpr ie, Stmt invokestmt) {
    // Handle the parameters
    SootMethodRef method = ie.getMethodRef();
    for (int i = 0; i < ie.getArgCount(); i++) {
      Value arg = ie.getArg(i);
      if (arg instanceof Local) {
        Local local = (Local) arg;
        Type parameterType = method.getParameterType(i);
        if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(parameterType))) {
          if (fix) {
            ie.setArg(i, insertCast(local, parameterType, invokestmt));
          } else {
            error("Type Error");
          }
        }
      }
    }

    if (ie instanceof InterfaceInvokeExpr) {
      InterfaceInvokeExpr invoke = (InterfaceInvokeExpr) ie;
      Value base = invoke.getBase();
      if (base instanceof Local) {
        Local local = (Local) base;
        RefType classType = method.getDeclaringClass().getType();
        if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(classType))) {
          if (fix) {
            invoke.setBase(insertCast(local, classType, invokestmt));
          } else {
            error("Type Error(7): local " + local + " is of incompatible type " + local.getType());
          }
        }
      }
    } else if (ie instanceof SpecialInvokeExpr) {
      SpecialInvokeExpr invoke = (SpecialInvokeExpr) ie;
      Value base = invoke.getBase();
      if (base instanceof Local) {
        Local local = (Local) base;
        RefType classType = method.getDeclaringClass().getType();
        if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(classType))) {
          if (fix) {
            invoke.setBase(insertCast(local, classType, invokestmt));
          } else {
            error("Type Error(9)");
          }
        }
      }
    } else if (ie instanceof VirtualInvokeExpr) {
      VirtualInvokeExpr invoke = (VirtualInvokeExpr) ie;
      Value base = invoke.getBase();
      if (base instanceof Local) {
        Local local = (Local) base;
        RefType classType = method.getDeclaringClass().getType();
        if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(classType))) {
          if (fix) {
            invoke.setBase(insertCast(local, classType, invokestmt));
          } else {
            error("Type Error(13)");
          }
        }
      }
    } else if (ie instanceof StaticInvokeExpr) {
      // No base to handle
    } else if (ie instanceof DynamicInvokeExpr) {
      DynamicInvokeExpr die = (DynamicInvokeExpr) ie;
      SootMethodRef bootstrapMethod = die.getMethodRef();
      for (int i = 0; i < die.getBootstrapArgCount(); i++) {
        if (die.getBootstrapArg(i) instanceof Local) {
          Local local = (Local) die.getBootstrapArg(i);
          Type parameterType = bootstrapMethod.getParameterType(i);
          if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(parameterType))) {
            if (fix) {
              ie.setArg(i, insertCast(local, parameterType, invokestmt));
            } else {
              error("Type Error");
            }
          }
        }
      }
    } else {
      throw new RuntimeException("Unhandled invoke expression type: " + ie.getClass());
    }
  }

  @Override
  public void caseBreakpointStmt(BreakpointStmt stmt) {
    // Do nothing
  }

  @Override
  public void caseInvokeStmt(InvokeStmt stmt) {
    handleInvokeExpr(stmt.getInvokeExpr(), stmt);
  }

  @Override
  public void caseAssignStmt(AssignStmt stmt) {
    final Value l = stmt.getLeftOp();
    final Value r = stmt.getRightOp();

    TypeNode left = null;

    // ******** LEFT ********

    if (l instanceof ArrayRef) {
      ArrayRef ref = (ArrayRef) l;
      TypeNode base = hierarchy.typeNode(((Local) ref.getBase()).getType());

      if (!base.isArray()) {
        error("Type Error(16)");
      }

      left = base.element();

      Value index = ref.getIndex();
      if (index instanceof Local) {
        if (!hierarchy.typeNode(((Local) index).getType()).hasAncestorOrSelf(hierarchy.typeNode(IntType.v()))) {
          error("Type Error(17)");
        }
      }
    } else if (l instanceof Local) {
      try {
        left = hierarchy.typeNode(((Local) l).getType());
      } catch (InternalTypingException e) {
        logger.debug("untyped local: " + l);
        throw e;
      }
    } else if (l instanceof InstanceFieldRef) {
      InstanceFieldRef ref = (InstanceFieldRef) l;
      Local base = (Local) ref.getBase();
      RefType classTy = ref.getField().getDeclaringClass().getType();
      if (!hierarchy.typeNode(base.getType()).hasAncestorOrSelf(hierarchy.typeNode(classTy))) {
        if (fix) {
          ref.setBase(insertCast(base, classTy, stmt));
        } else {
          error("Type Error(18)");
        }
      }

      left = hierarchy.typeNode(ref.getField().getType());
    } else if (l instanceof StaticFieldRef) {
      StaticFieldRef ref = (StaticFieldRef) l;
      left = hierarchy.typeNode(ref.getField().getType());
    } else {
      throw new RuntimeException("Unhandled assignment left hand side type: " + l.getClass());
    }

    // ******** RIGHT ********

    if (r instanceof ArrayRef) {
      ArrayRef ref = (ArrayRef) r;
      Local base = (Local) ref.getBase();

      TypeNode baseTy = hierarchy.typeNode((base).getType());
      if (!baseTy.isArray()) {
        error("Type Error(19): " + baseTy + " is not an array type");
      }
      if (baseTy == hierarchy.NULL) {
        return;
      }

      if (!left.hasDescendantOrSelf(baseTy.element())) {
        if (fix) {
          Type lefttype = left.type();
          if (lefttype instanceof ArrayType) {
            ArrayType atype = (ArrayType) lefttype;
            ref.setBase(insertCast(base, ArrayType.v(atype.baseType, atype.numDimensions + 1), stmt));
          } else {
            ref.setBase(insertCast(base, ArrayType.v(lefttype, 1), stmt));
          }
        } else {
          error("Type Error(20)");
        }
      }

      Value index = ref.getIndex();
      if (index instanceof Local) {
        if (!hierarchy.typeNode(((Local) index).getType()).hasAncestorOrSelf(hierarchy.typeNode(IntType.v()))) {
          error("Type Error(21)");
        }
      }
    } else if (r instanceof DoubleConstant) {
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(DoubleType.v()))) {
        error("Type Error(22)");
      }
    } else if (r instanceof FloatConstant) {
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(FloatType.v()))) {
        error("Type Error(45)");
      }
    } else if (r instanceof IntConstant) {
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(IntType.v()))) {
        error("Type Error(23)");
      }
    } else if (r instanceof LongConstant) {
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(LongType.v()))) {
        error("Type Error(24)");
      }
    } else if (r instanceof NullConstant) {
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(NullType.v()))) {
        error("Type Error(25)");
      }
    } else if (r instanceof StringConstant) {
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(RefType.v("java.lang.String")))) {
        error("Type Error(26)");
      }
    } else if (r instanceof ClassConstant) {
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(RefType.v("java.lang.Class")))) {
        error("Type Error(27)");
      }
    } else if (r instanceof BinopExpr) {
      // ******** BINOP EXPR ********

      final BinopExpr be = (BinopExpr) r;
      final Value lv = be.getOp1();
      final Value rv = be.getOp2();

      TypeNode lop;
      TypeNode rop;

      // ******** LEFT ********
      if (lv instanceof Local) {
        lop = hierarchy.typeNode(((Local) lv).getType());
      } else if (lv instanceof DoubleConstant) {
        lop = hierarchy.typeNode(DoubleType.v());
      } else if (lv instanceof FloatConstant) {
        lop = hierarchy.typeNode(FloatType.v());
      } else if (lv instanceof IntConstant) {
        lop = hierarchy.typeNode(IntType.v());
      } else if (lv instanceof LongConstant) {
        lop = hierarchy.typeNode(LongType.v());
      } else if (lv instanceof NullConstant) {
        lop = hierarchy.typeNode(NullType.v());
      } else if (lv instanceof StringConstant) {
        lop = hierarchy.typeNode(RefType.v("java.lang.String"));
      } else if (lv instanceof ClassConstant) {
        lop = hierarchy.typeNode(RefType.v("java.lang.Class"));
      } else {
        throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
      }

      // ******** RIGHT ********
      if (rv instanceof Local) {
        rop = hierarchy.typeNode(((Local) rv).getType());
      } else if (rv instanceof DoubleConstant) {
        rop = hierarchy.typeNode(DoubleType.v());
      } else if (rv instanceof FloatConstant) {
        rop = hierarchy.typeNode(FloatType.v());
      } else if (rv instanceof IntConstant) {
        rop = hierarchy.typeNode(IntType.v());
      } else if (rv instanceof LongConstant) {
        rop = hierarchy.typeNode(LongType.v());
      } else if (rv instanceof NullConstant) {
        rop = hierarchy.typeNode(NullType.v());
      } else if (rv instanceof StringConstant) {
        rop = hierarchy.typeNode(RefType.v("java.lang.String"));
      } else if (rv instanceof ClassConstant) {
        rop = hierarchy.typeNode(RefType.v("java.lang.Class"));
      } else {
        throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
      }

      if ((be instanceof AddExpr) || (be instanceof SubExpr) || (be instanceof MulExpr) || (be instanceof DivExpr)
          || (be instanceof RemExpr) || (be instanceof AndExpr) || (be instanceof OrExpr) || (be instanceof XorExpr)) {
        if (!(left.hasDescendantOrSelf(lop) && left.hasDescendantOrSelf(rop))) {
          error("Type Error(27)");
        }
      } else if ((be instanceof ShlExpr) || (be instanceof ShrExpr) || (be instanceof UshrExpr)) {
        if (!(left.hasDescendantOrSelf(lop) && hierarchy.typeNode(IntType.v()).hasAncestorOrSelf(rop))) {
          error("Type Error(28)");
        }
      } else if ((be instanceof CmpExpr) || (be instanceof CmpgExpr) || (be instanceof CmplExpr) || (be instanceof EqExpr)
          || (be instanceof GeExpr) || (be instanceof GtExpr) || (be instanceof LeExpr) || (be instanceof LtExpr)
          || (be instanceof NeExpr)) {
        try {
          lop.lca(rop);
        } catch (TypeException e) {
          error(e.getMessage());
        }

        if (!left.hasDescendantOrSelf(hierarchy.typeNode(IntType.v()))) {
          error("Type Error(29)");
        }
      } else {
        throw new RuntimeException("Unhandled binary expression type: " + be.getClass());
      }
    } else if (r instanceof CastExpr) {
      CastExpr ce = (CastExpr) r;
      TypeNode castTy = hierarchy.typeNode(ce.getCastType());
      Value op = ce.getOp();
      if (op instanceof Local) {
        TypeNode opTy = hierarchy.typeNode(((Local) op).getType());
        try {
          // we must be careful not to reject primitive type casts (e.g. int to long)
          if (castTy.isClassOrInterface() || opTy.isClassOrInterface()) {
            castTy.lca(opTy);
          }
        } catch (TypeException e) {
          logger.debug(r + "[" + opTy + "<->" + castTy + "]");
          error(e.getMessage());
        }
      }

      if (!left.hasDescendantOrSelf(castTy)) {
        error("Type Error(30)");
      }
    } else if (r instanceof InstanceOfExpr) {
      InstanceOfExpr ioe = (InstanceOfExpr) r;
      TypeNode type = hierarchy.typeNode(ioe.getCheckType());
      TypeNode op = hierarchy.typeNode(ioe.getOp().getType());

      try {
        op.lca(type);
      } catch (TypeException e) {
        logger.debug(r + "[" + op + "<->" + type + "]");
        error(e.getMessage());
      }

      if (!left.hasDescendantOrSelf(hierarchy.typeNode(IntType.v()))) {
        error("Type Error(31)");
      }
    } else if (r instanceof InvokeExpr) {
      InvokeExpr ie = (InvokeExpr) r;

      handleInvokeExpr(ie, stmt);

      if (!left.hasDescendantOrSelf(hierarchy.typeNode(ie.getMethodRef().getReturnType()))) {
        error("Type Error(32)");
      }
    } else if (r instanceof NewArrayExpr) {
      NewArrayExpr nae = (NewArrayExpr) r;

      Type baseType = nae.getBaseType();
      TypeNode right;
      if (baseType instanceof ArrayType) {
        right = hierarchy.typeNode(ArrayType.v(((ArrayType) baseType).baseType, ((ArrayType) baseType).numDimensions + 1));
      } else {
        right = hierarchy.typeNode(ArrayType.v(baseType, 1));
      }

      if (!left.hasDescendantOrSelf(right)) {
        error("Type Error(33)");
      }

      Value size = nae.getSize();
      if (size instanceof Local) {
        TypeNode var = hierarchy.typeNode(((Local) size).getType());

        if (!var.hasAncestorOrSelf(hierarchy.typeNode(IntType.v()))) {
          error("Type Error(34)");
        }
      }
    } else if (r instanceof NewExpr) {
      NewExpr ne = (NewExpr) r;

      if (!left.hasDescendantOrSelf(hierarchy.typeNode(ne.getBaseType()))) {
        error("Type Error(35)");
      }
    } else if (r instanceof NewMultiArrayExpr) {
      NewMultiArrayExpr nmae = (NewMultiArrayExpr) r;

      if (!left.hasDescendantOrSelf(hierarchy.typeNode(nmae.getBaseType()))) {
        error("Type Error(36)");
      }

      for (int i = 0; i < nmae.getSizeCount(); i++) {
        Value size = nmae.getSize(i);
        if (size instanceof Local) {
          TypeNode var = hierarchy.typeNode(((Local) size).getType());

          if (!var.hasAncestorOrSelf(hierarchy.typeNode(IntType.v()))) {
            error("Type Error(37)");
          }
        }
      }
    } else if (r instanceof LengthExpr) {
      LengthExpr le = (LengthExpr) r;

      if (!left.hasDescendantOrSelf(hierarchy.typeNode(IntType.v()))) {
        error("Type Error(38)");
      }
      Value op = le.getOp();
      if (op instanceof Local) {
        if (!hierarchy.typeNode(((Local) op).getType()).isArray()) {
          error("Type Error(39)");
        }
      }
    } else if (r instanceof NegExpr) {
      NegExpr ne = (NegExpr) r;
      TypeNode right;
      Value op = ne.getOp();
      if (op instanceof Local) {
        right = hierarchy.typeNode(((Local) op).getType());
      } else if (op instanceof DoubleConstant) {
        right = hierarchy.typeNode(DoubleType.v());
      } else if (op instanceof FloatConstant) {
        right = hierarchy.typeNode(FloatType.v());
      } else if (op instanceof IntConstant) {
        right = hierarchy.typeNode(IntType.v());
      } else if (op instanceof LongConstant) {
        right = hierarchy.typeNode(LongType.v());
      } else {
        throw new RuntimeException("Unhandled neg expression operand type: " + op.getClass());
      }

      if (!left.hasDescendantOrSelf(right)) {
        error("Type Error(40)");
      }
    } else if (r instanceof Local) {
      Local loc = (Local) r;
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(loc.getType()))) {
        if (fix) {
          stmt.setRightOp(insertCast(loc, left.type(), stmt));
        } else {
          error("Type Error(41)");
        }
      }
    } else if (r instanceof InstanceFieldRef) {
      InstanceFieldRef ref = (InstanceFieldRef) r;
      Local base = (Local) ref.getBase();
      RefType classTy = ref.getField().getDeclaringClass().getType();
      if (!hierarchy.typeNode(base.getType()).hasAncestorOrSelf(hierarchy.typeNode(classTy))) {
        if (fix) {
          ref.setBase(insertCast(base, classTy, stmt));
        } else {
          error("Type Error(42)");
        }
      }
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(ref.getField().getType()))) {
        error("Type Error(43)");
      }
    } else if (r instanceof StaticFieldRef) {
      StaticFieldRef ref = (StaticFieldRef) r;
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(ref.getField().getType()))) {
        error("Type Error(44)");
      }
    } else {
      throw new RuntimeException("Unhandled assignment right hand side type: " + r.getClass());
    }
  }

  @Override
  public void caseIdentityStmt(IdentityStmt stmt) {
    TypeNode left = hierarchy.typeNode(((Local) stmt.getLeftOp()).getType());

    Value r = stmt.getRightOp();
    if (r instanceof CaughtExceptionRef) {
      for (Type t : TrapManager.getExceptionTypesOf(stmt, stmtBody)) {
        if (!left.hasDescendantOrSelf(hierarchy.typeNode(t))) {
          error("Type Error(47)");
        }
      }
      if (!left.hasAncestorOrSelf(hierarchy.typeNode(Scene.v().getBaseExceptionType()))) {
        error("Type Error(48)");
      }
    } else {
      TypeNode right = hierarchy.typeNode(r.getType());
      if (!left.hasDescendantOrSelf(right)) {
        error("Type Error(46) [" + left + " <- " + right + "]");
      }
    }
  }

  @Override
  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
    Value op = stmt.getOp();
    if (op instanceof Local) {
      TypeNode opTy = hierarchy.typeNode(((Local) op).getType());
      if (!opTy.hasAncestorOrSelf(hierarchy.typeNode(RefType.v("java.lang.Object")))) {
        error("Type Error(49)");
      }
    }
  }

  @Override
  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
    Value op = stmt.getOp();
    if (op instanceof Local) {
      TypeNode opTy = hierarchy.typeNode(((Local) op).getType());
      if (!opTy.hasAncestorOrSelf(hierarchy.typeNode(RefType.v("java.lang.Object")))) {
        error("Type Error(49)");
      }
    }
  }

  @Override
  public void caseGotoStmt(GotoStmt stmt) {
  }

  @Override
  public void caseIfStmt(IfStmt stmt) {
    final ConditionExpr expr = (ConditionExpr) stmt.getCondition();
    final Value lv = expr.getOp1();
    final Value rv = expr.getOp2();

    TypeNode lop;
    TypeNode rop;

    // ******** LEFT ********
    if (lv instanceof Local) {
      lop = hierarchy.typeNode(((Local) lv).getType());
    } else if (lv instanceof DoubleConstant) {
      lop = hierarchy.typeNode(DoubleType.v());
    } else if (lv instanceof FloatConstant) {
      lop = hierarchy.typeNode(FloatType.v());
    } else if (lv instanceof IntConstant) {
      lop = hierarchy.typeNode(IntType.v());
    } else if (lv instanceof LongConstant) {
      lop = hierarchy.typeNode(LongType.v());
    } else if (lv instanceof NullConstant) {
      lop = hierarchy.typeNode(NullType.v());
    } else if (lv instanceof StringConstant) {
      lop = hierarchy.typeNode(RefType.v("java.lang.String"));
    } else if (lv instanceof ClassConstant) {
      lop = hierarchy.typeNode(RefType.v("java.lang.Class"));
    } else {
      throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
    }

    // ******** RIGHT ********
    if (rv instanceof Local) {
      rop = hierarchy.typeNode(((Local) rv).getType());
    } else if (rv instanceof DoubleConstant) {
      rop = hierarchy.typeNode(DoubleType.v());
    } else if (rv instanceof FloatConstant) {
      rop = hierarchy.typeNode(FloatType.v());
    } else if (rv instanceof IntConstant) {
      rop = hierarchy.typeNode(IntType.v());
    } else if (rv instanceof LongConstant) {
      rop = hierarchy.typeNode(LongType.v());
    } else if (rv instanceof NullConstant) {
      rop = hierarchy.typeNode(NullType.v());
    } else if (rv instanceof StringConstant) {
      rop = hierarchy.typeNode(RefType.v("java.lang.String"));
    } else if (rv instanceof ClassConstant) {
      rop = hierarchy.typeNode(RefType.v("java.lang.Class"));
    } else {
      throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
    }

    try {
      lop.lca(rop);
    } catch (TypeException e) {
      error(e.getMessage());
    }
  }

  @Override
  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    Value key = stmt.getKey();
    if (key instanceof Local) {
      if (!hierarchy.typeNode(((Local) key).getType()).hasAncestorOrSelf(hierarchy.typeNode(IntType.v()))) {
        error("Type Error(50)");
      }
    }
  }

  @Override
  public void caseNopStmt(NopStmt stmt) {
  }

  @Override
  public void caseReturnStmt(ReturnStmt stmt) {
    Value op = stmt.getOp();
    if (op instanceof Local) {
      Local opLocal = (Local) op;
      Type returnType = stmtBody.getMethod().getReturnType();
      if (!hierarchy.typeNode(opLocal.getType()).hasAncestorOrSelf(hierarchy.typeNode(returnType))) {
        if (fix) {
          stmt.setOp(insertCast(opLocal, returnType, stmt));
        } else {
          error("Type Error(51)");
        }
      }
    }
  }

  @Override
  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
  }

  @Override
  public void caseTableSwitchStmt(TableSwitchStmt stmt) {
    Value key = stmt.getKey();
    if (key instanceof Local) {
      if (!hierarchy.typeNode(((Local) key).getType()).hasAncestorOrSelf(hierarchy.typeNode(IntType.v()))) {
        error("Type Error(52)");
      }
    }
  }

  @Override
  public void caseThrowStmt(ThrowStmt stmt) {
    Value op = stmt.getOp();
    if (op instanceof Local) {
      Local opLocal = (Local) op;
      TypeNode opTy = hierarchy.typeNode(opLocal.getType());
      if (!opTy.hasAncestorOrSelf(hierarchy.typeNode(Scene.v().getBaseExceptionType()))) {
        if (fix) {
          stmt.setOp(insertCast(opLocal, Scene.v().getBaseExceptionType(), stmt));
        } else {
          error("Type Error(53)");
        }
      }
    }
  }

  public void defaultCase(Stmt stmt) {
    throw new RuntimeException("Unhandled statement type: " + stmt.getClass());
  }

  private Local insertCast(Local oldlocal, Type type, Stmt stmt) {
    final Jimple jimp = Jimple.v();
    Local newlocal = localGenerator.generateLocal(type);
    stmtBody.getUnits().insertBefore(jimp.newAssignStmt(newlocal, jimp.newCastExpr(oldlocal, type)),
        Util.findFirstNonIdentityUnit(stmtBody, stmt));
    return newlocal;
  }
}
