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
import soot.ArrayType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.NullType;
import soot.RefType;
import soot.Scene;
import soot.SootField;
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

class ConstraintCollector extends AbstractStmtSwitch {

  private final TypeResolver resolver;
  private final boolean uses; // if true, include use constraints

  private JimpleBody stmtBody;

  public ConstraintCollector(TypeResolver resolver, boolean uses) {
    this.resolver = resolver;
    this.uses = uses;
  }

  public void collect(Stmt stmt, JimpleBody stmtBody) {
    this.stmtBody = stmtBody;
    stmt.apply(this);
  }

  private void handleInvokeExpr(InvokeExpr ie) {
    if (!uses) {
      return;
    }

    // Handle the parameters
    SootMethodRef method = ie.getMethodRef();
    for (int i = 0; i < ie.getArgCount(); i++) {
      Value arg = ie.getArg(i);
      if (arg instanceof Local) {
        TypeVariable localType = resolver.typeVariable((Local) arg);
        localType.addParent(resolver.typeVariable(method.parameterType(i)));
      }
    }

    if (ie instanceof InterfaceInvokeExpr) {
      InterfaceInvokeExpr invoke = (InterfaceInvokeExpr) ie;
      Value base = invoke.getBase();
      if (base instanceof Local) {
        TypeVariable localType = resolver.typeVariable((Local) base);
        localType.addParent(resolver.typeVariable(method.declaringClass()));
      }
    } else if (ie instanceof SpecialInvokeExpr) {
      SpecialInvokeExpr invoke = (SpecialInvokeExpr) ie;
      Value base = invoke.getBase();
      if (base instanceof Local) {
        TypeVariable localType = resolver.typeVariable((Local) base);
        localType.addParent(resolver.typeVariable(method.declaringClass()));
      }
    } else if (ie instanceof VirtualInvokeExpr) {
      VirtualInvokeExpr invoke = (VirtualInvokeExpr) ie;
      Value base = invoke.getBase();
      if (base instanceof Local) {
        TypeVariable localType = resolver.typeVariable((Local) base);
        localType.addParent(resolver.typeVariable(method.declaringClass()));
      }
    } else if (ie instanceof StaticInvokeExpr) {
      // no base to handle
    } else if (ie instanceof DynamicInvokeExpr) {
      DynamicInvokeExpr invoke = (DynamicInvokeExpr) ie;
      SootMethodRef bootstrapMethod = invoke.getBootstrapMethodRef();
      for (int i = 0; i < invoke.getBootstrapArgCount(); i++) {
        if (invoke.getArg(i) instanceof Local) {
          Local local = (Local) invoke.getBootstrapArg(i);
          TypeVariable localType = resolver.typeVariable(local);
          localType.addParent(resolver.typeVariable(bootstrapMethod.parameterType(i)));
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
    handleInvokeExpr(stmt.getInvokeExpr());
  }

  @Override
  public void caseAssignStmt(AssignStmt stmt) {
    final Value l = stmt.getLeftOp();
    final Value r = stmt.getRightOp();

    TypeVariable left = null;
    TypeVariable right = null;

    // ******** LEFT ********

    if (l instanceof ArrayRef) {
      ArrayRef ref = (ArrayRef) l;
      Value index = ref.getIndex();
      Value base = ref.getBase();

      TypeVariable baseType = resolver.typeVariable((Local) base);
      baseType.makeElement();
      left = baseType.element();

      if (index instanceof Local) {
        if (uses) {
          resolver.typeVariable((Local) index).addParent(resolver.typeVariable(IntType.v()));
        }
      }
    } else if (l instanceof Local) {
      left = resolver.typeVariable((Local) l);
    } else if (l instanceof InstanceFieldRef) {
      InstanceFieldRef ref = (InstanceFieldRef) l;

      if (uses) {
        TypeVariable baseType = resolver.typeVariable((Local) ref.getBase());
        SootField field = ref.getField();
        baseType.addParent(resolver.typeVariable(field.getDeclaringClass()));
        left = resolver.typeVariable(field.getType());
      }
    } else if (l instanceof StaticFieldRef) {
      if (uses) {
        StaticFieldRef ref = (StaticFieldRef) l;
        left = resolver.typeVariable(ref.getField().getType());
      }
    } else {
      throw new RuntimeException("Unhandled assignment left hand side type: " + l.getClass());
    }

    // ******** RIGHT ********

    if (r instanceof ArrayRef) {
      ArrayRef ref = (ArrayRef) r;
      Value index = ref.getIndex();
      Value base = ref.getBase();

      TypeVariable baseType = resolver.typeVariable((Local) base);
      baseType.makeElement();
      right = baseType.element();

      if (index instanceof Local) {
        if (uses) {
          resolver.typeVariable((Local) index).addParent(resolver.typeVariable(IntType.v()));
        }
      }
    } else if (r instanceof DoubleConstant) {
      right = resolver.typeVariable(DoubleType.v());
    } else if (r instanceof FloatConstant) {
      right = resolver.typeVariable(FloatType.v());
    } else if (r instanceof IntConstant) {
      right = resolver.typeVariable(IntType.v());
    } else if (r instanceof LongConstant) {
      right = resolver.typeVariable(LongType.v());
    } else if (r instanceof NullConstant) {
      right = resolver.typeVariable(NullType.v());
    } else if (r instanceof StringConstant) {
      right = resolver.typeVariable(RefType.v("java.lang.String"));
    } else if (r instanceof ClassConstant) {
      right = resolver.typeVariable(RefType.v("java.lang.Class"));
    } else if (r instanceof BinopExpr) {
      // ******** BINOP EXPR ********

      final BinopExpr be = (BinopExpr) r;
      final Value lv = be.getOp1();
      final Value rv = be.getOp2();

      TypeVariable lop;
      TypeVariable rop;

      // ******** LEFT ********
      if (lv instanceof Local) {
        lop = resolver.typeVariable((Local) lv);
      } else if (lv instanceof DoubleConstant) {
        lop = resolver.typeVariable(DoubleType.v());
      } else if (lv instanceof FloatConstant) {
        lop = resolver.typeVariable(FloatType.v());
      } else if (lv instanceof IntConstant) {
        lop = resolver.typeVariable(IntType.v());
      } else if (lv instanceof LongConstant) {
        lop = resolver.typeVariable(LongType.v());
      } else if (lv instanceof NullConstant) {
        lop = resolver.typeVariable(NullType.v());
      } else if (lv instanceof StringConstant) {
        lop = resolver.typeVariable(RefType.v("java.lang.String"));
      } else if (lv instanceof ClassConstant) {
        lop = resolver.typeVariable(RefType.v("java.lang.Class"));
      } else {
        throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
      }

      // ******** RIGHT ********
      if (rv instanceof Local) {
        rop = resolver.typeVariable((Local) rv);
      } else if (rv instanceof DoubleConstant) {
        rop = resolver.typeVariable(DoubleType.v());
      } else if (rv instanceof FloatConstant) {
        rop = resolver.typeVariable(FloatType.v());
      } else if (rv instanceof IntConstant) {
        rop = resolver.typeVariable(IntType.v());
      } else if (rv instanceof LongConstant) {
        rop = resolver.typeVariable(LongType.v());
      } else if (rv instanceof NullConstant) {
        rop = resolver.typeVariable(NullType.v());
      } else if (rv instanceof StringConstant) {
        rop = resolver.typeVariable(RefType.v("java.lang.String"));
      } else if (rv instanceof ClassConstant) {
        rop = resolver.typeVariable(RefType.v("java.lang.Class"));
      } else {
        throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
      }

      if ((be instanceof AddExpr) || (be instanceof SubExpr) || (be instanceof MulExpr) || (be instanceof DivExpr)
          || (be instanceof RemExpr) || (be instanceof AndExpr) || (be instanceof OrExpr) || (be instanceof XorExpr)) {
        if (uses) {
          TypeVariable common = resolver.typeVariable();
          rop.addParent(common);
          lop.addParent(common);
        }

        if (left != null) {
          rop.addParent(left);
          lop.addParent(left);
        }
      } else if ((be instanceof ShlExpr) || (be instanceof ShrExpr) || (be instanceof UshrExpr)) {
        if (uses) {
          rop.addParent(resolver.typeVariable(IntType.v()));
        }

        right = lop;
      } else if ((be instanceof CmpExpr) || (be instanceof CmpgExpr) || (be instanceof CmplExpr) || (be instanceof EqExpr)
          || (be instanceof GeExpr) || (be instanceof GtExpr) || (be instanceof LeExpr) || (be instanceof LtExpr)
          || (be instanceof NeExpr)) {
        if (uses) {
          TypeVariable common = resolver.typeVariable();
          rop.addParent(common);
          lop.addParent(common);
        }

        right = resolver.typeVariable(IntType.v());
      } else {
        throw new RuntimeException("Unhandled binary expression type: " + be.getClass());
      }
    } else if (r instanceof CastExpr) {
      CastExpr ce = (CastExpr) r;
      right = resolver.typeVariable(ce.getCastType());
    } else if (r instanceof InstanceOfExpr) {
      right = resolver.typeVariable(IntType.v());
    } else if (r instanceof InvokeExpr) {
      InvokeExpr ie = (InvokeExpr) r;

      handleInvokeExpr(ie);

      right = resolver.typeVariable(ie.getMethodRef().returnType());
    } else if (r instanceof NewArrayExpr) {
      NewArrayExpr nae = (NewArrayExpr) r;

      Type baseType = nae.getBaseType();
      if (baseType instanceof ArrayType) {
        ArrayType arrTy = (ArrayType) baseType;
        right = resolver.typeVariable(ArrayType.v(arrTy.baseType, arrTy.numDimensions + 1));
      } else {
        right = resolver.typeVariable(ArrayType.v(baseType, 1));
      }

      if (uses) {
        Value size = nae.getSize();
        if (size instanceof Local) {
          TypeVariable var = resolver.typeVariable((Local) size);
          var.addParent(resolver.typeVariable(IntType.v()));
        }
      }
    } else if (r instanceof NewExpr) {
      NewExpr na = (NewExpr) r;
      right = resolver.typeVariable(na.getBaseType());
    } else if (r instanceof NewMultiArrayExpr) {
      NewMultiArrayExpr nmae = (NewMultiArrayExpr) r;

      right = resolver.typeVariable(nmae.getBaseType());

      if (uses) {
        for (int i = 0; i < nmae.getSizeCount(); i++) {
          Value size = nmae.getSize(i);
          if (size instanceof Local) {
            TypeVariable var = resolver.typeVariable((Local) size);
            var.addParent(resolver.typeVariable(IntType.v()));
          }
        }
      }
    } else if (r instanceof LengthExpr) {
      if (uses) {
        Value op = ((LengthExpr) r).getOp();
        if (op instanceof Local) {
          resolver.typeVariable((Local) op).makeElement();
        }
      }
      right = resolver.typeVariable(IntType.v());
    } else if (r instanceof NegExpr) {
      Value op = ((NegExpr) r).getOp();
      if (op instanceof Local) {
        right = resolver.typeVariable((Local) op);
      } else if (op instanceof DoubleConstant) {
        right = resolver.typeVariable(DoubleType.v());
      } else if (op instanceof FloatConstant) {
        right = resolver.typeVariable(FloatType.v());
      } else if (op instanceof IntConstant) {
        right = resolver.typeVariable(IntType.v());
      } else if (op instanceof LongConstant) {
        right = resolver.typeVariable(LongType.v());
      } else {
        throw new RuntimeException("Unhandled neg expression operand type: " + op.getClass());
      }
    } else if (r instanceof Local) {
      right = resolver.typeVariable((Local) r);
    } else if (r instanceof InstanceFieldRef) {
      InstanceFieldRef ref = (InstanceFieldRef) r;

      if (uses) {
        TypeVariable baseType = resolver.typeVariable((Local) ref.getBase());
        baseType.addParent(resolver.typeVariable(ref.getField().getDeclaringClass()));
      }

      right = resolver.typeVariable(ref.getField().getType());
    } else if (r instanceof StaticFieldRef) {
      StaticFieldRef ref = (StaticFieldRef) r;

      right = resolver.typeVariable(ref.getField().getType());
    } else {
      throw new RuntimeException("Unhandled assignment right hand side type: " + r.getClass());
    }

    if (left != null && right != null) {
      right.addParent(left);
    }
  }

  @Override
  public void caseIdentityStmt(IdentityStmt stmt) {
    final Value l = stmt.getLeftOp();
    final Value r = stmt.getRightOp();

    if (l instanceof Local) {
      TypeVariable left = resolver.typeVariable((Local) l);

      if (r instanceof CaughtExceptionRef) {
        for (Type t : TrapManager.getExceptionTypesOf(stmt, stmtBody)) {
          resolver.typeVariable(t).addParent(left);
        }
        if (uses) {
          left.addParent(resolver.typeVariable(Scene.v().getBaseExceptionType()));
        }
      } else {
        TypeVariable right = resolver.typeVariable(r.getType());
        right.addParent(left);
      }
    }
  }

  @Override
  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
    if (uses) {
      Value op = stmt.getOp();
      if (op instanceof Local) {
        TypeVariable var = resolver.typeVariable((Local) op);
        var.addParent(resolver.typeVariable(RefType.v("java.lang.Object")));
      }
    }
  }

  @Override
  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
    if (uses) {
      Value op = stmt.getOp();
      if (op instanceof Local) {
        TypeVariable var = resolver.typeVariable((Local) op);
        var.addParent(resolver.typeVariable(RefType.v("java.lang.Object")));
      }
    }
  }

  @Override
  public void caseGotoStmt(GotoStmt stmt) {
  }

  @Override
  public void caseIfStmt(IfStmt stmt) {
    if (uses) {
      final ConditionExpr expr = (ConditionExpr) stmt.getCondition();
      final Value lv = expr.getOp1();
      final Value rv = expr.getOp2();

      TypeVariable lop;
      TypeVariable rop;

      // ******** LEFT ********
      if (lv instanceof Local) {
        lop = resolver.typeVariable((Local) lv);
      } else if (lv instanceof DoubleConstant) {
        lop = resolver.typeVariable(DoubleType.v());
      } else if (lv instanceof FloatConstant) {
        lop = resolver.typeVariable(FloatType.v());
      } else if (lv instanceof IntConstant) {
        lop = resolver.typeVariable(IntType.v());
      } else if (lv instanceof LongConstant) {
        lop = resolver.typeVariable(LongType.v());
      } else if (lv instanceof NullConstant) {
        lop = resolver.typeVariable(NullType.v());
      } else if (lv instanceof StringConstant) {
        lop = resolver.typeVariable(RefType.v("java.lang.String"));
      } else if (lv instanceof ClassConstant) {
        lop = resolver.typeVariable(RefType.v("java.lang.Class"));
      } else {
        throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
      }

      // ******** RIGHT ********
      if (rv instanceof Local) {
        rop = resolver.typeVariable((Local) rv);
      } else if (rv instanceof DoubleConstant) {
        rop = resolver.typeVariable(DoubleType.v());
      } else if (rv instanceof FloatConstant) {
        rop = resolver.typeVariable(FloatType.v());
      } else if (rv instanceof IntConstant) {
        rop = resolver.typeVariable(IntType.v());
      } else if (rv instanceof LongConstant) {
        rop = resolver.typeVariable(LongType.v());
      } else if (rv instanceof NullConstant) {
        rop = resolver.typeVariable(NullType.v());
      } else if (rv instanceof StringConstant) {
        rop = resolver.typeVariable(RefType.v("java.lang.String"));
      } else if (rv instanceof ClassConstant) {
        rop = resolver.typeVariable(RefType.v("java.lang.Class"));
      } else {
        throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
      }

      TypeVariable common = resolver.typeVariable();
      rop.addParent(common);
      lop.addParent(common);
    }
  }

  @Override
  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    if (uses) {
      Value key = stmt.getKey();
      if (key instanceof Local) {
        TypeVariable var = resolver.typeVariable((Local) key);
        var.addParent(resolver.typeVariable(IntType.v()));
      }
    }
  }

  @Override
  public void caseNopStmt(NopStmt stmt) {
  }

  @Override
  public void caseReturnStmt(ReturnStmt stmt) {
    if (uses) {
      Value op = stmt.getOp();
      if (op instanceof Local) {
        TypeVariable var = resolver.typeVariable((Local) op);
        var.addParent(resolver.typeVariable(stmtBody.getMethod().getReturnType()));
      }
    }
  }

  @Override
  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
  }

  @Override
  public void caseTableSwitchStmt(TableSwitchStmt stmt) {
    if (uses) {
      Value key = stmt.getKey();
      if (key instanceof Local) {
        resolver.typeVariable((Local) key).addParent(resolver.typeVariable(IntType.v()));
      }
    }
  }

  @Override
  public void caseThrowStmt(ThrowStmt stmt) {
    if (uses) {
      Value op = stmt.getOp();
      if (op instanceof Local) {
        TypeVariable var = resolver.typeVariable((Local) op);
        var.addParent(resolver.typeVariable(Scene.v().getBaseExceptionType()));
      }
    }
  }

  public void defaultCase(Stmt stmt) {
    throw new RuntimeException("Unhandled statement type: " + stmt.getClass());
  }
}
