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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.NullType;
import soot.RefType;
import soot.SootMethodRef;
import soot.TrapManager;
import soot.Type;
import soot.Unit;
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

/**
 * @deprecated use {@link soot.jimple.toolkits.typing.fast.TypeResolver} instead
 */
@Deprecated
class ConstraintCheckerBV extends AbstractStmtSwitch {
  private static final Logger logger = LoggerFactory.getLogger(ConstraintCheckerBV.class);
  private final ClassHierarchy hierarchy;
  private final boolean fix; // if true, fix constraint violations

  private JimpleBody stmtBody;

  public ConstraintCheckerBV(TypeResolverBV resolver, boolean fix) {
    this.fix = fix;

    hierarchy = resolver.hierarchy();
  }

  public void check(Stmt stmt, JimpleBody stmtBody) throws TypeException {
    try {
      this.stmtBody = stmtBody;
      stmt.apply(this);
    } catch (RuntimeTypeException e) {
      StringWriter st = new StringWriter();
      PrintWriter pw = new PrintWriter(st);
      logger.error(e.getMessage(), e);
      pw.close();
      throw new TypeException(st.toString());
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
    if (ie instanceof InterfaceInvokeExpr) {
      InterfaceInvokeExpr invoke = (InterfaceInvokeExpr) ie;

      SootMethodRef method = invoke.getMethodRef();
      Value base = invoke.getBase();

      if (base instanceof Local) {
        Local local = (Local) base;

        if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(method.declaringClass().getType()))) {
          if (fix) {
            invoke.setBase(insertCast(local, method.declaringClass().getType(), invokestmt));
          } else {
            error("Type Error(7): local " + local + " is of incompatible type " + local.getType());
          }
        }
      }

      int count = invoke.getArgCount();

      for (int i = 0; i < count; i++) {
        if (invoke.getArg(i) instanceof Local) {
          Local local = (Local) invoke.getArg(i);

          if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(method.parameterType(i)))) {
            if (fix) {
              invoke.setArg(i, insertCast(local, method.parameterType(i), invokestmt));
            } else {
              error("Type Error(8)");
            }
          }
        }
      }
    } else if (ie instanceof SpecialInvokeExpr) {
      SpecialInvokeExpr invoke = (SpecialInvokeExpr) ie;

      SootMethodRef method = invoke.getMethodRef();
      Value base = invoke.getBase();

      if (base instanceof Local) {
        Local local = (Local) base;

        if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(method.declaringClass().getType()))) {
          if (fix) {
            invoke.setBase(insertCast(local, method.declaringClass().getType(), invokestmt));
          } else {
            error("Type Error(9)");
          }
        }
      }

      int count = invoke.getArgCount();

      for (int i = 0; i < count; i++) {
        if (invoke.getArg(i) instanceof Local) {
          Local local = (Local) invoke.getArg(i);

          if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(method.parameterType(i)))) {
            if (fix) {
              invoke.setArg(i, insertCast(local, method.parameterType(i), invokestmt));
            } else {
              error("Type Error(10)");
            }
          }
        }
      }
    } else if (ie instanceof VirtualInvokeExpr) {
      VirtualInvokeExpr invoke = (VirtualInvokeExpr) ie;

      SootMethodRef method = invoke.getMethodRef();
      Value base = invoke.getBase();

      if (base instanceof Local) {
        Local local = (Local) base;

        if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(method.declaringClass().getType()))) {
          if (fix) {
            invoke.setBase(insertCast(local, method.declaringClass().getType(), invokestmt));
          } else {
            error("Type Error(13)");
          }
        }
      }

      int count = invoke.getArgCount();

      for (int i = 0; i < count; i++) {
        if (invoke.getArg(i) instanceof Local) {
          Local local = (Local) invoke.getArg(i);

          if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(method.parameterType(i)))) {
            if (fix) {
              invoke.setArg(i, insertCast(local, method.parameterType(i), invokestmt));
            } else {
              error("Type Error(14)");
            }
          }
        }
      }
    } else if (ie instanceof StaticInvokeExpr) {
      StaticInvokeExpr invoke = (StaticInvokeExpr) ie;

      SootMethodRef method = invoke.getMethodRef();

      int count = invoke.getArgCount();

      for (int i = 0; i < count; i++) {
        if (invoke.getArg(i) instanceof Local) {
          Local local = (Local) invoke.getArg(i);

          if (!hierarchy.typeNode(local.getType()).hasAncestorOrSelf(hierarchy.typeNode(method.parameterType(i)))) {
            if (fix) {
              invoke.setArg(i, insertCast(local, method.parameterType(i), invokestmt));
            } else {
              error("Type Error(15)");
            }
          }
        }
      }
    } else {
      throw new RuntimeException("Unhandled invoke expression type: " + ie.getClass());
    }
  }

  public void caseBreakpointStmt(BreakpointStmt stmt) {
    // Do nothing
  }

  public void caseInvokeStmt(InvokeStmt stmt) {
    handleInvokeExpr(stmt.getInvokeExpr(), stmt);
  }

  public void caseAssignStmt(AssignStmt stmt) {
    Value l = stmt.getLeftOp();
    Value r = stmt.getRightOp();

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

      TypeNode base = hierarchy.typeNode(((Local) ref.getBase()).getType());

      if (!base.hasAncestorOrSelf(hierarchy.typeNode(ref.getField().getDeclaringClass().getType()))) {
        if (fix) {
          ref.setBase(insertCast((Local) ref.getBase(), ref.getField().getDeclaringClass().getType(), stmt));
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
      TypeNode base = hierarchy.typeNode(((Local) ref.getBase()).getType());

      if (!base.isArray()) {
        error("Type Error(19): " + base + " is not an array type");
      }

      if (base == hierarchy.NULL) {
        return;
      }

      if (!left.hasDescendantOrSelf(base.element())) {
        if (fix) {
          Type lefttype = left.type();
          if (lefttype instanceof ArrayType) {
            ArrayType atype = (ArrayType) lefttype;
            ref.setBase(insertCast((Local) ref.getBase(), ArrayType.v(atype.baseType, atype.numDimensions + 1), stmt));
          } else {
            ref.setBase(insertCast((Local) ref.getBase(), ArrayType.v(lefttype, 1), stmt));
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

      BinopExpr be = (BinopExpr) r;

      Value lv = be.getOp1();
      Value rv = be.getOp2();

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
      TypeNode cast = hierarchy.typeNode(ce.getCastType());
      if (ce.getOp() instanceof Local) {
        TypeNode op = hierarchy.typeNode(((Local) ce.getOp()).getType());

        try {
          // we must be careful not to reject primitive type casts (e.g. int to long)
          if (cast.isClassOrInterface() || op.isClassOrInterface()) {
            cast.lca(op);
          }
        } catch (TypeException e) {
          logger.debug("" + r + "[" + op + "<->" + cast + "]");
          error(e.getMessage());
        }
      }

      if (!left.hasDescendantOrSelf(cast)) {
        error("Type Error(30)");
      }
    } else if (r instanceof InstanceOfExpr) {
      InstanceOfExpr ioe = (InstanceOfExpr) r;
      TypeNode type = hierarchy.typeNode(ioe.getCheckType());
      TypeNode op = hierarchy.typeNode(ioe.getOp().getType());

      try {
        op.lca(type);
      } catch (TypeException e) {
        logger.debug("" + r + "[" + op + "<->" + type + "]");
        error(e.getMessage());
      }

      if (!left.hasDescendantOrSelf(hierarchy.typeNode(IntType.v()))) {
        error("Type Error(31)");
      }
    } else if (r instanceof InvokeExpr) {
      InvokeExpr ie = (InvokeExpr) r;

      handleInvokeExpr(ie, stmt);

      if (!left.hasDescendantOrSelf(hierarchy.typeNode(ie.getMethodRef().returnType()))) {
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

      if (le.getOp() instanceof Local) {
        if (!hierarchy.typeNode(((Local) le.getOp()).getType()).isArray()) {
          error("Type Error(39)");
        }
      }
    } else if (r instanceof NegExpr) {
      NegExpr ne = (NegExpr) r;
      TypeNode right;

      if (ne.getOp() instanceof Local) {
        right = hierarchy.typeNode(((Local) ne.getOp()).getType());
      } else if (ne.getOp() instanceof DoubleConstant) {
        right = hierarchy.typeNode(DoubleType.v());
      } else if (ne.getOp() instanceof FloatConstant) {
        right = hierarchy.typeNode(FloatType.v());
      } else if (ne.getOp() instanceof IntConstant) {
        right = hierarchy.typeNode(IntType.v());
      } else if (ne.getOp() instanceof LongConstant) {
        right = hierarchy.typeNode(LongType.v());
      } else {
        throw new RuntimeException("Unhandled neg expression operand type: " + ne.getOp().getClass());
      }

      if (!left.hasDescendantOrSelf(right)) {
        error("Type Error(40)");
      }
    } else if (r instanceof Local) {
      if (!left.hasDescendantOrSelf(hierarchy.typeNode(((Local) r).getType()))) {
        if (fix) {
          stmt.setRightOp(insertCast((Local) r, left.type(), stmt));
        } else {
          error("Type Error(41)");
        }
      }
    } else if (r instanceof InstanceFieldRef) {
      InstanceFieldRef ref = (InstanceFieldRef) r;

      TypeNode baseType = hierarchy.typeNode(((Local) ref.getBase()).getType());
      if (!baseType.hasAncestorOrSelf(hierarchy.typeNode(ref.getField().getDeclaringClass().getType()))) {
        if (fix) {
          ref.setBase(insertCast((Local) ref.getBase(), ref.getField().getDeclaringClass().getType(), stmt));
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

  public void caseIdentityStmt(IdentityStmt stmt) {
    TypeNode left = hierarchy.typeNode(((Local) stmt.getLeftOp()).getType());

    Value r = stmt.getRightOp();

    if (!(r instanceof CaughtExceptionRef)) {
      TypeNode right = hierarchy.typeNode(r.getType());
      if (!left.hasDescendantOrSelf(right)) {
        error("Type Error(46) [" + left + " <- " + right + "]");
      }
    } else {
      List<RefType> exceptionTypes = TrapManager.getExceptionTypesOf(stmt, stmtBody);
      Iterator<RefType> typeIt = exceptionTypes.iterator();

      while (typeIt.hasNext()) {
        Type t = typeIt.next();

        if (!left.hasDescendantOrSelf(hierarchy.typeNode(t))) {
          error("Type Error(47)");
        }
      }

      if (!left.hasAncestorOrSelf(hierarchy.typeNode(RefType.v("java.lang.Throwable")))) {
        error("Type Error(48)");
      }
    }
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
    if (stmt.getOp() instanceof Local) {
      TypeNode op = hierarchy.typeNode(((Local) stmt.getOp()).getType());

      if (!op.hasAncestorOrSelf(hierarchy.typeNode(RefType.v("java.lang.Object")))) {
        error("Type Error(49)");
      }
    }
  }

  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
    if (stmt.getOp() instanceof Local) {
      TypeNode op = hierarchy.typeNode(((Local) stmt.getOp()).getType());

      if (!op.hasAncestorOrSelf(hierarchy.typeNode(RefType.v("java.lang.Object")))) {
        error("Type Error(49)");
      }
    }
  }

  public void caseGotoStmt(GotoStmt stmt) {
  }

  public void caseIfStmt(IfStmt stmt) {
    ConditionExpr cond = (ConditionExpr) stmt.getCondition();

    BinopExpr expr = cond;
    Value lv = expr.getOp1();
    Value rv = expr.getOp2();

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

  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    Value key = stmt.getKey();

    if (key instanceof Local) {
      if (!hierarchy.typeNode(((Local) key).getType()).hasAncestorOrSelf(hierarchy.typeNode(IntType.v()))) {
        error("Type Error(50)");
      }
    }
  }

  public void caseNopStmt(NopStmt stmt) {
  }

  public void caseReturnStmt(ReturnStmt stmt) {
    if (stmt.getOp() instanceof Local) {
      if (!hierarchy.typeNode(((Local) stmt.getOp()).getType())
          .hasAncestorOrSelf(hierarchy.typeNode(stmtBody.getMethod().getReturnType()))) {
        if (fix) {
          stmt.setOp(insertCast((Local) stmt.getOp(), stmtBody.getMethod().getReturnType(), stmt));
        } else {
          error("Type Error(51)");
        }
      }
    }
  }

  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
  }

  public void caseTableSwitchStmt(TableSwitchStmt stmt) {
    Value key = stmt.getKey();

    if (key instanceof Local) {
      if (!hierarchy.typeNode(((Local) key).getType()).hasAncestorOrSelf(hierarchy.typeNode(IntType.v()))) {
        error("Type Error(52)");
      }
    }
  }

  public void caseThrowStmt(ThrowStmt stmt) {
    if (stmt.getOp() instanceof Local) {
      TypeNode op = hierarchy.typeNode(((Local) stmt.getOp()).getType());

      if (!op.hasAncestorOrSelf(hierarchy.typeNode(RefType.v("java.lang.Throwable")))) {
        if (fix) {
          stmt.setOp(insertCast((Local) stmt.getOp(), RefType.v("java.lang.Throwable"), stmt));
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
    Local newlocal = Jimple.v().newLocal("tmp", type);
    stmtBody.getLocals().add(newlocal);

    Unit u = Util.findFirstNonIdentityUnit(stmtBody, stmt);
    stmtBody.getUnits().insertBefore(Jimple.v().newAssignStmt(newlocal, Jimple.v().newCastExpr(oldlocal, type)), u);
    return newlocal;
  }
}
