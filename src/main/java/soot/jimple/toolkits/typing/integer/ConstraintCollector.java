package soot.jimple.toolkits.typing.integer;

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
import soot.IntegerType;
import soot.Local;
import soot.NullType;
import soot.SootMethodRef;
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
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UshrExpr;
import soot.jimple.XorExpr;

class ConstraintCollector extends AbstractStmtSwitch {

  private final TypeResolver resolver;
  private final boolean uses; // if true, include use contraints

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
        Local local = (Local) arg;
        if (local.getType() instanceof IntegerType) {
          TypeVariable localType = resolver.typeVariable(local);
          localType.addParent(resolver.typeVariable(method.parameterType(i)));
        }
      }
    }

    if (ie instanceof DynamicInvokeExpr) {
      DynamicInvokeExpr die = (DynamicInvokeExpr) ie;
      SootMethodRef bootstrapMethod = die.getBootstrapMethodRef();
      for (int i = 0; i < die.getBootstrapArgCount(); i++) {
        Value arg = die.getBootstrapArg(i);
        if (arg instanceof Local) {
          Local local = (Local) arg;
          if (local.getType() instanceof IntegerType) {
            TypeVariable localType = resolver.typeVariable(local);
            localType.addParent(resolver.typeVariable(bootstrapMethod.parameterType(i)));
          }
        }
      }
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
      Type baset = ((Local) ref.getBase()).getType();
      if (baset instanceof ArrayType) {
        ArrayType base = (ArrayType) baset;
        Value index = ref.getIndex();
        if (uses) {
          if ((base.numDimensions == 1) && (base.baseType instanceof IntegerType)) {
            left = resolver.typeVariable(base.baseType);
          }
          if (index instanceof Local) {
            resolver.typeVariable((Local) index).addParent(resolver.INT);
          }
        }
      }
    } else if (l instanceof Local) {
      Local loc = (Local) l;
      if (loc.getType() instanceof IntegerType) {
        left = resolver.typeVariable(loc);
      }
    } else if (l instanceof InstanceFieldRef) {
      if (uses) {
        InstanceFieldRef ref = (InstanceFieldRef) l;
        Type fieldType = ref.getFieldRef().type();
        if (fieldType instanceof IntegerType) {
          left = resolver.typeVariable(fieldType);
        }
      }
    } else if (l instanceof StaticFieldRef) {
      if (uses) {
        StaticFieldRef ref = (StaticFieldRef) l;
        Type fieldType = ref.getFieldRef().type();
        if (fieldType instanceof IntegerType) {
          left = resolver.typeVariable(fieldType);
        }
      }
    } else {
      throw new RuntimeException("Unhandled assignment left hand side type: " + l.getClass());
    }

    // ******** RIGHT ********

    if (r instanceof ArrayRef) {
      ArrayRef ref = (ArrayRef) r;
      Type baset = ((Local) ref.getBase()).getType();
      if (!(baset instanceof NullType)) {
        Value index = ref.getIndex();

        // Be careful, dex can do some weird object/array casting
        if (baset instanceof ArrayType) {
          ArrayType base = (ArrayType) baset;

          if ((base.numDimensions == 1) && (base.baseType instanceof IntegerType)) {
            right = resolver.typeVariable(base.baseType);
          }
        } else if (baset instanceof IntegerType) {
          right = resolver.typeVariable(baset);
        }
        if (uses) {
          if (index instanceof Local) {
            resolver.typeVariable((Local) index).addParent(resolver.INT);
          }
        }
      }
    } else if (r instanceof DoubleConstant) {
    } else if (r instanceof FloatConstant) {
    } else if (r instanceof IntConstant) {
      int value = ((IntConstant) r).value;

      if (value < -32768) {
        right = resolver.INT;
      } else if (value < -128) {
        right = resolver.SHORT;
      } else if (value < 0) {
        right = resolver.BYTE;
      } else if (value < 2) {
        right = resolver.R0_1;
      } else if (value < 128) {
        right = resolver.R0_127;
      } else if (value < 32768) {
        right = resolver.R0_32767;
      } else if (value < 65536) {
        right = resolver.CHAR;
      } else {
        right = resolver.INT;
      }
    } else if (r instanceof LongConstant) {
    } else if (r instanceof NullConstant) {
    } else if (r instanceof StringConstant) {
    } else if (r instanceof ClassConstant) {
    } else if (r instanceof BinopExpr) {
      // ******** BINOP EXPR ********

      BinopExpr be = (BinopExpr) r;

      Value lv = be.getOp1();
      Value rv = be.getOp2();

      TypeVariable lop = null;
      TypeVariable rop = null;

      // ******** LEFT ********
      if (lv instanceof Local) {
        Local loc = (Local) lv;
        if (loc.getType() instanceof IntegerType) {
          lop = resolver.typeVariable(loc);
        }
      } else if (lv instanceof DoubleConstant) {
      } else if (lv instanceof FloatConstant) {
      } else if (lv instanceof IntConstant) {
        int value = ((IntConstant) lv).value;

        if (value < -32768) {
          lop = resolver.INT;
        } else if (value < -128) {
          lop = resolver.SHORT;
        } else if (value < 0) {
          lop = resolver.BYTE;
        } else if (value < 2) {
          lop = resolver.R0_1;
        } else if (value < 128) {
          lop = resolver.R0_127;
        } else if (value < 32768) {
          lop = resolver.R0_32767;
        } else if (value < 65536) {
          lop = resolver.CHAR;
        } else {
          lop = resolver.INT;
        }
      } else if (lv instanceof LongConstant) {
      } else if (lv instanceof NullConstant) {
      } else if (lv instanceof StringConstant) {
      } else if (lv instanceof ClassConstant) {
      } else {
        throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
      }

      // ******** RIGHT ********
      if (rv instanceof Local) {
        Local loc = (Local) rv;
        if (loc.getType() instanceof IntegerType) {
          rop = resolver.typeVariable(loc);
        }
      } else if (rv instanceof DoubleConstant) {
      } else if (rv instanceof FloatConstant) {
      } else if (rv instanceof IntConstant) {
        int value = ((IntConstant) rv).value;

        if (value < -32768) {
          rop = resolver.INT;
        } else if (value < -128) {
          rop = resolver.SHORT;
        } else if (value < 0) {
          rop = resolver.BYTE;
        } else if (value < 2) {
          rop = resolver.R0_1;
        } else if (value < 128) {
          rop = resolver.R0_127;
        } else if (value < 32768) {
          rop = resolver.R0_32767;
        } else if (value < 65536) {
          rop = resolver.CHAR;
        } else {
          rop = resolver.INT;
        }
      } else if (rv instanceof LongConstant) {
      } else if (rv instanceof NullConstant) {
      } else if (rv instanceof StringConstant) {
      } else if (rv instanceof ClassConstant) {
      } else {
        throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
      }

      if ((be instanceof AddExpr) || (be instanceof SubExpr) || (be instanceof DivExpr) || (be instanceof RemExpr)
          || (be instanceof MulExpr)) {
        if (lop != null && rop != null) {
          if (uses) {
            if (lop.type() == null) {
              lop.addParent(resolver.INT);
            }

            if (rop.type() == null) {
              rop.addParent(resolver.INT);
            }
          }

          right = resolver.INT;
        }
      } else if ((be instanceof AndExpr) || (be instanceof OrExpr) || (be instanceof XorExpr)) {
        if (lop != null && rop != null) {
          right = resolver.typeVariable();
          rop.addParent(right);
          lop.addParent(right);
        }
      } else if (be instanceof ShlExpr) {
        if (uses) {
          if (lop != null && lop.type() == null) {
            lop.addParent(resolver.INT);
          }
          if (rop.type() == null) {
            rop.addParent(resolver.INT);
          }
        }
        right = (lop == null) ? null : resolver.INT;
      } else if ((be instanceof ShrExpr) || (be instanceof UshrExpr)) {
        if (uses) {
          if (lop != null && lop.type() == null) {
            lop.addParent(resolver.INT);
          }
          if (rop.type() == null) {
            rop.addParent(resolver.INT);
          }
        }
        right = lop;
      } else if ((be instanceof CmpExpr) || (be instanceof CmpgExpr) || (be instanceof CmplExpr)) {
        right = resolver.BYTE;
      } else if ((be instanceof EqExpr) || (be instanceof GeExpr) || (be instanceof GtExpr) || (be instanceof LeExpr)
          || (be instanceof LtExpr) || (be instanceof NeExpr)) {
        if (uses) {
          TypeVariable common = resolver.typeVariable();
          if (rop != null) {
            rop.addParent(common);
          }
          if (lop != null) {
            lop.addParent(common);
          }
        }
        right = resolver.BOOLEAN;
      } else {
        throw new RuntimeException("Unhandled binary expression type: " + be.getClass());
      }
    } else if (r instanceof CastExpr) {
      Type ty = ((CastExpr) r).getCastType();
      if (ty instanceof IntegerType) {
        right = resolver.typeVariable(ty);
      }
    } else if (r instanceof InstanceOfExpr) {
      right = resolver.BOOLEAN;
    } else if (r instanceof InvokeExpr) {
      InvokeExpr ie = (InvokeExpr) r;

      handleInvokeExpr(ie);

      Type returnType = ie.getMethodRef().getReturnType();
      if (returnType instanceof IntegerType) {
        right = resolver.typeVariable(returnType);
      }
    } else if (r instanceof NewArrayExpr) {
      NewArrayExpr nae = (NewArrayExpr) r;

      if (uses) {
        Value size = nae.getSize();
        if (size instanceof Local) {
          TypeVariable var = resolver.typeVariable((Local) size);
          var.addParent(resolver.INT);
        }
      }
    } else if (r instanceof NewExpr) {
    } else if (r instanceof NewMultiArrayExpr) {
      NewMultiArrayExpr nmae = (NewMultiArrayExpr) r;

      if (uses) {
        for (int i = 0; i < nmae.getSizeCount(); i++) {
          Value size = nmae.getSize(i);
          if (size instanceof Local) {
            TypeVariable var = resolver.typeVariable((Local) size);
            var.addParent(resolver.INT);
          }
        }
      }
    } else if (r instanceof LengthExpr) {
      right = resolver.INT;
    } else if (r instanceof NegExpr) {
      NegExpr ne = (NegExpr) r;

      if (ne.getOp() instanceof Local) {
        Local local = (Local) ne.getOp();

        if (local.getType() instanceof IntegerType) {
          if (uses) {
            resolver.typeVariable(local).addParent(resolver.INT);
          }
          right = resolver.typeVariable();
          right.addChild(resolver.BYTE);
          right.addChild(resolver.typeVariable(local));
        }
      } else if (ne.getOp() instanceof DoubleConstant) {
      } else if (ne.getOp() instanceof FloatConstant) {
      } else if (ne.getOp() instanceof IntConstant) {
        int value = ((IntConstant) ne.getOp()).value;

        if (value < -32768) {
          right = resolver.INT;
        } else if (value < -128) {
          right = resolver.SHORT;
        } else if (value < 0) {
          right = resolver.BYTE;
        } else if (value < 2) {
          right = resolver.BYTE;
        } else if (value < 128) {
          right = resolver.BYTE;
        } else if (value < 32768) {
          right = resolver.SHORT;
        } else if (value < 65536) {
          right = resolver.INT;
        } else {
          right = resolver.INT;
        }
      } else if (ne.getOp() instanceof LongConstant) {
      } else {
        throw new RuntimeException("Unhandled neg expression operand type: " + ne.getOp().getClass());
      }
    } else if (r instanceof Local) {
      Local local = (Local) r;
      if (local.getType() instanceof IntegerType) {
        right = resolver.typeVariable(local);
      }
    } else if (r instanceof InstanceFieldRef) {
      Type type = ((InstanceFieldRef) r).getFieldRef().type();
      if (type instanceof IntegerType) {
        right = resolver.typeVariable(type);
      }
    } else if (r instanceof StaticFieldRef) {
      Type type = ((StaticFieldRef) r).getFieldRef().type();
      if (type instanceof IntegerType) {
        right = resolver.typeVariable(type);
      }
    } else {
      throw new RuntimeException("Unhandled assignment right hand side type: " + r.getClass());
    }

    if (left != null && right != null && (left.type() == null || right.type() == null)) {
      right.addParent(left);
    }
  }

  @Override
  public void caseIdentityStmt(IdentityStmt stmt) {
    Value l = stmt.getLeftOp();
    if (l instanceof Local) {
      Local loc = (Local) l;
      if (loc.getType() instanceof IntegerType) {
        TypeVariable left = resolver.typeVariable(loc);
        TypeVariable right = resolver.typeVariable(stmt.getRightOp().getType());
        right.addParent(left);
      }
    }
  }

  @Override
  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
  }

  @Override
  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
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

      TypeVariable lop = null;
      TypeVariable rop = null;

      // ******** LEFT ********
      if (lv instanceof Local) {
        Local loc = (Local) lv;
        if ((loc).getType() instanceof IntegerType) {
          lop = resolver.typeVariable(loc);
        }
      } else if (lv instanceof DoubleConstant) {
      } else if (lv instanceof FloatConstant) {
      } else if (lv instanceof IntConstant) {
        int value = ((IntConstant) lv).value;

        if (value < -32768) {
          lop = resolver.INT;
        } else if (value < -128) {
          lop = resolver.SHORT;
        } else if (value < 0) {
          lop = resolver.BYTE;
        } else if (value < 2) {
          lop = resolver.R0_1;
        } else if (value < 128) {
          lop = resolver.R0_127;
        } else if (value < 32768) {
          lop = resolver.R0_32767;
        } else if (value < 65536) {
          lop = resolver.CHAR;
        } else {
          lop = resolver.INT;
        }
      } else if (lv instanceof LongConstant) {
      } else if (lv instanceof NullConstant) {
      } else if (lv instanceof StringConstant) {
      } else if (lv instanceof ClassConstant) {
      } else {
        throw new RuntimeException("Unhandled binary expression left operand type: " + lv.getClass());
      }

      // ******** RIGHT ********
      if (rv instanceof Local) {
        Local loc = (Local) rv;
        if ((loc).getType() instanceof IntegerType) {
          rop = resolver.typeVariable(loc);
        }
      } else if (rv instanceof DoubleConstant) {
      } else if (rv instanceof FloatConstant) {
      } else if (rv instanceof IntConstant) {
        int value = ((IntConstant) rv).value;

        if (value < -32768) {
          rop = resolver.INT;
        } else if (value < -128) {
          rop = resolver.SHORT;
        } else if (value < 0) {
          rop = resolver.BYTE;
        } else if (value < 2) {
          rop = resolver.R0_1;
        } else if (value < 128) {
          rop = resolver.R0_127;
        } else if (value < 32768) {
          rop = resolver.R0_32767;
        } else if (value < 65536) {
          rop = resolver.CHAR;
        } else {
          rop = resolver.INT;
        }
      } else if (rv instanceof LongConstant) {
      } else if (rv instanceof NullConstant) {
      } else if (rv instanceof StringConstant) {
      } else if (rv instanceof ClassConstant) {
      } else {
        throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
      }

      if (rop != null && lop != null) {
        TypeVariable common = resolver.typeVariable();
        rop.addParent(common);
        lop.addParent(common);
      }
    }
  }

  @Override
  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    if (uses) {
      Value key = stmt.getKey();
      if (key instanceof Local) {
        resolver.typeVariable((Local) key).addParent(resolver.INT);
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
        Local opLocal = (Local) op;
        if (opLocal.getType() instanceof IntegerType) {
          resolver.typeVariable(opLocal).addParent(resolver.typeVariable(stmtBody.getMethod().getReturnType()));
        }
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
        resolver.typeVariable((Local) key).addParent(resolver.INT);
      }
    }
  }

  @Override
  public void caseThrowStmt(ThrowStmt stmt) {
  }

  public void defaultCase(Stmt stmt) {
    throw new RuntimeException("Unhandled statement type: " + stmt.getClass());
  }
}
