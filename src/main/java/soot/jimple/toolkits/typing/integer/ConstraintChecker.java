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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.LocalGenerator;
import soot.NullType;
import soot.Scene;
import soot.ShortType;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.UnitPatchingChain;
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
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UshrExpr;
import soot.jimple.XorExpr;
import soot.jimple.toolkits.typing.Util;

class ConstraintChecker extends AbstractStmtSwitch {
  private static final Logger logger = LoggerFactory.getLogger(ConstraintChecker.class);

  private final TypeResolver resolver;
  private final boolean fix; // if true, fix constraint violations

  private JimpleBody stmtBody;
  private LocalGenerator localGenerator;

  public ConstraintChecker(TypeResolver resolver, boolean fix) {
    this.resolver = resolver;
    this.fix = fix;
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
    final ClassHierarchy classHierarchy = ClassHierarchy.v();

    // Handle the parameters
    SootMethodRef method = ie.getMethodRef();
    for (int i = 0, e = ie.getArgCount(); i < e; i++) {
      Value currArg = ie.getArg(i);
      if (currArg instanceof Local) {
        Local local = (Local) currArg;
        Type localType = local.getType();
        if (localType instanceof IntegerType) {
          Type currParamType = method.getParameterType(i);
          if (!classHierarchy.typeNode(localType).hasAncestor_1(classHierarchy.typeNode(currParamType))) {
            if (fix) {
              ie.setArg(i, insertCast(local, currParamType, invokestmt));
            } else {
              error("Type Error");
            }
          }
        }
      }
    }

    if (ie instanceof DynamicInvokeExpr) {
      DynamicInvokeExpr die = (DynamicInvokeExpr) ie;
      SootMethodRef bootstrapMethod = die.getBootstrapMethodRef();
      for (int i = 0, e = die.getBootstrapArgCount(); i < e; i++) {
        Value currBootstrapArg = die.getBootstrapArg(i);
        if (currBootstrapArg instanceof Local) {
          Local local = (Local) currBootstrapArg;
          Type localType = local.getType();
          if (localType instanceof IntegerType) {
            Type currParamType = bootstrapMethod.getParameterType(i);
            if (!classHierarchy.typeNode(localType).hasAncestor_1(classHierarchy.typeNode(currParamType))) {
              if (fix) {
                die.setArg(i, insertCast(local, currParamType, invokestmt));
              } else {
                error("Type Error");
              }
            }
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
    handleInvokeExpr(stmt.getInvokeExpr(), stmt);
  }

  @Override
  public void caseAssignStmt(AssignStmt stmt) {
    final ClassHierarchy classHierarchy = ClassHierarchy.v();

    final Value l = stmt.getLeftOp();
    final Value r = stmt.getRightOp();

    TypeNode left = null;
    TypeNode right = null;

    // ******** LEFT ********

    if (l instanceof ArrayRef) {
      ArrayRef ref = (ArrayRef) l;
      Type baset = ((Local) ref.getBase()).getType();
      if (baset instanceof ArrayType) {
        ArrayType base = (ArrayType) baset;
        Value index = ref.getIndex();

        if ((base.numDimensions == 1) && (base.baseType instanceof IntegerType)) {
          left = classHierarchy.typeNode(base.baseType);
        }

        if (index instanceof Local) {
          if (!classHierarchy.typeNode(((Local) index).getType()).hasAncestor_1(classHierarchy.INT)) {
            if (fix) {
              ref.setIndex(insertCast((Local) index, IntType.v(), stmt));
            } else {
              error("Type Error(5)");
            }
          }
        }
      }
    } else if (l instanceof Local) {
      Type ty = ((Local) l).getType();
      if (ty instanceof IntegerType) {
        left = classHierarchy.typeNode(ty);
      }
    } else if (l instanceof InstanceFieldRef) {
      Type ty = ((InstanceFieldRef) l).getFieldRef().type();
      if (ty instanceof IntegerType) {
        left = classHierarchy.typeNode(ty);
      }
    } else if (l instanceof StaticFieldRef) {
      Type ty = ((StaticFieldRef) l).getFieldRef().type();
      if (ty instanceof IntegerType) {
        left = classHierarchy.typeNode(ty);
      }
    } else {
      throw new RuntimeException("Unhandled assignment left hand side type: " + l.getClass());
    }

    // ******** RIGHT ********

    if (r instanceof ArrayRef) {
      ArrayRef ref = (ArrayRef) r;
      Type baset = ((Local) ref.getBase()).getType();
      if (!(baset instanceof NullType)) {
        ArrayType base = (ArrayType) baset;
        Value index = ref.getIndex();

        if ((base.numDimensions == 1) && (base.baseType instanceof IntegerType)) {
          right = classHierarchy.typeNode(base.baseType);
        }

        if (index instanceof Local) {
          if (!classHierarchy.typeNode(((Local) index).getType()).hasAncestor_1(classHierarchy.INT)) {
            if (fix) {
              ref.setIndex(insertCast((Local) index, IntType.v(), stmt));
            } else {
              error("Type Error(6)");
            }
          }
        }
      }
    } else if (r instanceof DoubleConstant) {
    } else if (r instanceof FloatConstant) {
    } else if (r instanceof IntConstant) {
      int value = ((IntConstant) r).value;

      if (value < -32768) {
        right = classHierarchy.INT;
      } else if (value < -128) {
        right = classHierarchy.SHORT;
      } else if (value < 0) {
        right = classHierarchy.BYTE;
      } else if (value < 2) {
        right = classHierarchy.R0_1;
      } else if (value < 128) {
        right = classHierarchy.R0_127;
      } else if (value < 32768) {
        right = classHierarchy.R0_32767;
      } else if (value < 65536) {
        right = classHierarchy.CHAR;
      } else {
        right = classHierarchy.INT;
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

      TypeNode lop = null;
      TypeNode rop = null;

      // ******** LEFT ********
      if (lv instanceof Local) {
        if (((Local) lv).getType() instanceof IntegerType) {
          lop = classHierarchy.typeNode(((Local) lv).getType());
        }
      } else if (lv instanceof DoubleConstant) {
      } else if (lv instanceof FloatConstant) {
      } else if (lv instanceof IntConstant) {
        int value = ((IntConstant) lv).value;

        if (value < -32768) {
          lop = classHierarchy.INT;
        } else if (value < -128) {
          lop = classHierarchy.SHORT;
        } else if (value < 0) {
          lop = classHierarchy.BYTE;
        } else if (value < 2) {
          lop = classHierarchy.R0_1;
        } else if (value < 128) {
          lop = classHierarchy.R0_127;
        } else if (value < 32768) {
          lop = classHierarchy.R0_32767;
        } else if (value < 65536) {
          lop = classHierarchy.CHAR;
        } else {
          lop = classHierarchy.INT;
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
        if (((Local) rv).getType() instanceof IntegerType) {
          rop = classHierarchy.typeNode(((Local) rv).getType());
        }
      } else if (rv instanceof DoubleConstant) {
      } else if (rv instanceof FloatConstant) {
      } else if (rv instanceof IntConstant) {
        int value = ((IntConstant) rv).value;

        if (value < -32768) {
          rop = classHierarchy.INT;
        } else if (value < -128) {
          rop = classHierarchy.SHORT;
        } else if (value < 0) {
          rop = classHierarchy.BYTE;
        } else if (value < 2) {
          rop = classHierarchy.R0_1;
        } else if (value < 128) {
          rop = classHierarchy.R0_127;
        } else if (value < 32768) {
          rop = classHierarchy.R0_32767;
        } else if (value < 65536) {
          rop = classHierarchy.CHAR;
        } else {
          rop = classHierarchy.INT;
        }
      } else if (rv instanceof LongConstant) {
      } else if (rv instanceof NullConstant) {
      } else if (rv instanceof StringConstant) {
      } else if (rv instanceof ClassConstant) {
      } else {
        throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
      }

      if ((be instanceof AddExpr) || (be instanceof SubExpr) || (be instanceof MulExpr) || (be instanceof DivExpr)
          || (be instanceof RemExpr)) {
        if (lop != null && rop != null) {
          if (!lop.hasAncestor_1(classHierarchy.INT)) {
            if (fix) {
              be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), IntType.v(), stmt));
            } else {
              error("Type Error(7)");
            }
          }

          if (!rop.hasAncestor_1(classHierarchy.INT)) {
            if (fix) {
              be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), IntType.v(), stmt));
            } else {
              error("Type Error(8)");
            }
          }
        }

        right = classHierarchy.INT;
      } else if ((be instanceof AndExpr) || (be instanceof OrExpr) || (be instanceof XorExpr)) {
        if (lop != null && rop != null) {
          TypeNode lca = lop.lca_1(rop);

          if (lca == classHierarchy.TOP) {
            if (fix) {
              if (!lop.hasAncestor_1(classHierarchy.INT)) {
                be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), getTypeForCast(rop), stmt));
                lca = rop;
              }

              if (!rop.hasAncestor_1(classHierarchy.INT)) {
                be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), getTypeForCast(lop), stmt));
                lca = lop;
              }
            } else {
              error("Type Error(11)");
            }
          }

          right = lca;
        }
      } else if (be instanceof ShlExpr) {
        if (lop != null) {
          if (!lop.hasAncestor_1(classHierarchy.INT)) {
            if (fix) {
              be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), IntType.v(), stmt));
            } else {
              error("Type Error(9)");
            }
          }
        }

        if (!rop.hasAncestor_1(classHierarchy.INT)) {
          if (fix) {
            be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), IntType.v(), stmt));
          } else {
            error("Type Error(10)");
          }
        }

        right = (lop == null) ? null : classHierarchy.INT;
      } else if ((be instanceof ShrExpr) || (be instanceof UshrExpr)) {
        if (lop != null) {
          if (!lop.hasAncestor_1(classHierarchy.INT)) {
            if (fix) {
              be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), ByteType.v(), stmt));
              lop = classHierarchy.BYTE;
            } else {
              error("Type Error(9)");
            }
          }
        }

        if (!rop.hasAncestor_1(classHierarchy.INT)) {
          if (fix) {
            be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), IntType.v(), stmt));
          } else {
            error("Type Error(10)");
          }
        }

        right = lop;
      } else if ((be instanceof CmpExpr) || (be instanceof CmpgExpr) || (be instanceof CmplExpr)) {
        right = classHierarchy.BYTE;
      } else if ((be instanceof EqExpr) || (be instanceof GeExpr) || (be instanceof GtExpr) || (be instanceof LeExpr)
          || (be instanceof LtExpr) || (be instanceof NeExpr)) {
        if (rop != null) {
          TypeNode lca = lop.lca_1(rop);

          if (lca == classHierarchy.TOP) {
            if (fix) {
              if (!lop.hasAncestor_1(classHierarchy.INT)) {
                be.setOp1(insertCast(be.getOp1(), getTypeForCast(lop), getTypeForCast(rop), stmt));
              }

              if (!rop.hasAncestor_1(classHierarchy.INT)) {
                be.setOp2(insertCast(be.getOp2(), getTypeForCast(rop), getTypeForCast(lop), stmt));
              }
            } else {
              error("Type Error(11)");
            }
          }
        }

        right = classHierarchy.BOOLEAN;
      } else {
        throw new RuntimeException("Unhandled binary expression type: " + be.getClass());
      }
    } else if (r instanceof CastExpr) {
      Type ty = ((CastExpr) r).getCastType();
      if (ty instanceof IntegerType) {
        right = classHierarchy.typeNode(ty);
      }
    } else if (r instanceof InstanceOfExpr) {
      right = classHierarchy.BOOLEAN;
    } else if (r instanceof InvokeExpr) {
      InvokeExpr ie = (InvokeExpr) r;

      handleInvokeExpr(ie, stmt);

      Type retTy = ie.getMethodRef().getReturnType();
      if (retTy instanceof IntegerType) {
        right = classHierarchy.typeNode(retTy);
      }
    } else if (r instanceof NewArrayExpr) {
      NewArrayExpr nae = (NewArrayExpr) r;
      Value size = nae.getSize();

      if (size instanceof Local) {
        if (!classHierarchy.typeNode(((Local) size).getType()).hasAncestor_1(classHierarchy.INT)) {
          if (fix) {
            nae.setSize(insertCast((Local) size, IntType.v(), stmt));
          } else {
            error("Type Error(12)");
          }
        }
      }
    } else if (r instanceof NewExpr) {
    } else if (r instanceof NewMultiArrayExpr) {
      NewMultiArrayExpr nmae = (NewMultiArrayExpr) r;

      for (int i = 0; i < nmae.getSizeCount(); i++) {
        Value size = nmae.getSize(i);
        if (size instanceof Local) {
          if (!classHierarchy.typeNode(((Local) size).getType()).hasAncestor_1(classHierarchy.INT)) {
            if (fix) {
              nmae.setSize(i, insertCast((Local) size, IntType.v(), stmt));
            } else {
              error("Type Error(13)");
            }
          }
        }
      }
    } else if (r instanceof LengthExpr) {
      right = classHierarchy.INT;
    } else if (r instanceof NegExpr) {
      NegExpr ne = (NegExpr) r;
      Value op = ne.getOp();
      if (op instanceof Local) {
        Local local = (Local) op;

        if (local.getType() instanceof IntegerType) {
          TypeNode ltype = classHierarchy.typeNode(local.getType());
          if (!ltype.hasAncestor_1(classHierarchy.INT)) {
            if (fix) {
              ne.setOp(insertCast(local, IntType.v(), stmt));
              ltype = classHierarchy.BYTE;
            } else {
              error("Type Error(14)");
            }
          }

          right = (ltype == classHierarchy.CHAR) ? classHierarchy.INT : ltype;
        }
      } else if (op instanceof DoubleConstant) {
      } else if (op instanceof FloatConstant) {
      } else if (op instanceof IntConstant) {
        right = classHierarchy.INT;
      } else if (op instanceof LongConstant) {
      } else {
        throw new RuntimeException("Unhandled neg expression operand type: " + op.getClass());
      }
    } else if (r instanceof Local) {
      Type ty = ((Local) r).getType();
      if (ty instanceof IntegerType) {
        right = classHierarchy.typeNode(ty);
      }
    } else if (r instanceof InstanceFieldRef) {
      Type ty = ((InstanceFieldRef) r).getFieldRef().type();
      if (ty instanceof IntegerType) {
        right = classHierarchy.typeNode(ty);
      }
    } else if (r instanceof StaticFieldRef) {
      Type ty = ((StaticFieldRef) r).getFieldRef().type();
      if (ty instanceof IntegerType) {
        right = classHierarchy.typeNode(ty);
      }
    } else {
      throw new RuntimeException("Unhandled assignment right hand side type: " + r.getClass());
    }

    if (left != null && right != null) {
      if (!right.hasAncestor_1(left)) {
        if (fix) {
          stmt.setRightOp(insertCast(stmt.getRightOp(), getTypeForCast(right), getTypeForCast(left), stmt));
        } else {
          error("Type Error(15)");
        }
      }
    }
  }

  // This method is a local kludge, for avoiding NullPointerExceptions
  // when a R0_1, R0_127, or R0_32767 node is used in a type
  // cast. A more elegant solution would work with the TypeNode
  // type definition itself, but that would require a more thorough
  // knowledge of the typing system than the kludger posesses.
  static Type getTypeForCast(TypeNode node) {
    if (node.type() == null) {
      if (node == ClassHierarchy.v().R0_1) {
        return BooleanType.v();
      } else if (node == ClassHierarchy.v().R0_127) {
        return ByteType.v();
      } else if (node == ClassHierarchy.v().R0_32767) {
        return ShortType.v();
      }
      // Perhaps we should throw an exception here, since I don't think
      // there should be any other cases where node.type() is null.
      // In case that supposition is incorrect, though, we'll just
      // go on to return the null, and let the callers worry about it.
    }
    return node.type();
  }

  @Override
  public void caseIdentityStmt(IdentityStmt stmt) {
    Value l = stmt.getLeftOp();
    Value r = stmt.getRightOp();

    if (l instanceof Local) {
      Type locType = ((Local) l).getType();
      if (locType instanceof IntegerType) {
        TypeNode left = ClassHierarchy.v().typeNode((locType));
        TypeNode right = ClassHierarchy.v().typeNode(r.getType());

        if (!right.hasAncestor_1(left)) {
          if (fix) {
            stmt.setLeftOp(insertCastAfter((Local) l, getTypeForCast(left), getTypeForCast(right), stmt));
          } else {
            error("Type Error(16)");
          }
        }
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
    ConditionExpr cond = (ConditionExpr) stmt.getCondition();

    BinopExpr expr = cond;
    Value lv = expr.getOp1();
    Value rv = expr.getOp2();

    TypeNode lop = null;
    TypeNode rop = null;

    // ******** LEFT ********
    if (lv instanceof Local) {
      Type ty = ((Local) lv).getType();
      if (ty instanceof IntegerType) {
        lop = ClassHierarchy.v().typeNode(ty);
      }
    } else if (lv instanceof DoubleConstant) {
    } else if (lv instanceof FloatConstant) {
    } else if (lv instanceof IntConstant) {
      int value = ((IntConstant) lv).value;

      if (value < -32768) {
        lop = ClassHierarchy.v().INT;
      } else if (value < -128) {
        lop = ClassHierarchy.v().SHORT;
      } else if (value < 0) {
        lop = ClassHierarchy.v().BYTE;
      } else if (value < 2) {
        lop = ClassHierarchy.v().R0_1;
      } else if (value < 128) {
        lop = ClassHierarchy.v().R0_127;
      } else if (value < 32768) {
        lop = ClassHierarchy.v().R0_32767;
      } else if (value < 65536) {
        lop = ClassHierarchy.v().CHAR;
      } else {
        lop = ClassHierarchy.v().INT;
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
      Type ty = ((Local) rv).getType();
      if (ty instanceof IntegerType) {
        rop = ClassHierarchy.v().typeNode(ty);
      }
    } else if (rv instanceof DoubleConstant) {
    } else if (rv instanceof FloatConstant) {
    } else if (rv instanceof IntConstant) {
      int value = ((IntConstant) rv).value;

      if (value < -32768) {
        rop = ClassHierarchy.v().INT;
      } else if (value < -128) {
        rop = ClassHierarchy.v().SHORT;
      } else if (value < 0) {
        rop = ClassHierarchy.v().BYTE;
      } else if (value < 2) {
        rop = ClassHierarchy.v().R0_1;
      } else if (value < 128) {
        rop = ClassHierarchy.v().R0_127;
      } else if (value < 32768) {
        rop = ClassHierarchy.v().R0_32767;
      } else if (value < 65536) {
        rop = ClassHierarchy.v().CHAR;
      } else {
        rop = ClassHierarchy.v().INT;
      }
    } else if (rv instanceof LongConstant) {
    } else if (rv instanceof NullConstant) {
    } else if (rv instanceof StringConstant) {
    } else if (rv instanceof ClassConstant) {
    } else {
      throw new RuntimeException("Unhandled binary expression right operand type: " + rv.getClass());
    }

    if (lop != null && rop != null) {
      if (lop.lca_1(rop) == ClassHierarchy.v().TOP) {
        if (fix) {
          if (!lop.hasAncestor_1(ClassHierarchy.v().INT)) {
            expr.setOp1(insertCast(expr.getOp1(), getTypeForCast(lop), getTypeForCast(rop), stmt));
          }

          if (!rop.hasAncestor_1(ClassHierarchy.v().INT)) {
            expr.setOp2(insertCast(expr.getOp2(), getTypeForCast(rop), getTypeForCast(lop), stmt));
          }
        } else {
          error("Type Error(17)");
        }
      }
    }
  }

  @Override
  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    Value key = stmt.getKey();
    if (key instanceof Local) {
      if (!ClassHierarchy.v().typeNode(((Local) key).getType()).hasAncestor_1(ClassHierarchy.v().INT)) {
        if (fix) {
          stmt.setKey(insertCast((Local) key, IntType.v(), stmt));
        } else {
          error("Type Error(18)");
        }
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
      Type opType = ((Local) op).getType();
      if (opType instanceof IntegerType) {
        Type returnType = stmtBody.getMethod().getReturnType();
        if (!ClassHierarchy.v().typeNode(opType).hasAncestor_1(ClassHierarchy.v().typeNode(returnType))) {
          if (fix) {
            stmt.setOp(insertCast((Local) op, returnType, stmt));
          } else {
            error("Type Error(19)");
          }
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
      Local keyLocal = (Local) key;
      if (!ClassHierarchy.v().typeNode((keyLocal).getType()).hasAncestor_1(ClassHierarchy.v().INT)) {
        if (fix) {
          stmt.setKey(insertCast(keyLocal, IntType.v(), stmt));
        } else {
          error("Type Error(20)");
        }
      }
      resolver.typeVariable(keyLocal).addParent(resolver.INT);
    }
  }

  @Override
  public void caseThrowStmt(ThrowStmt stmt) {
  }

  public void defaultCase(Stmt stmt) {
    throw new RuntimeException("Unhandled statement type: " + stmt.getClass());
  }

  private Local insertCast(Local oldlocal, Type type, Stmt stmt) {
    final Jimple jimp = Jimple.v();
    Local newlocal = localGenerator.generateLocal(type);
    stmtBody.getUnits().insertBefore(jimp.newAssignStmt(newlocal, jimp.newCastExpr(oldlocal, type)),
        Util.findFirstNonIdentityUnit(this.stmtBody, stmt));
    return newlocal;
  }

  private Local insertCastAfter(Local leftlocal, Type lefttype, Type righttype, Stmt stmt) {
    final Jimple jimp = Jimple.v();
    Local newlocal = localGenerator.generateLocal(righttype);
    stmtBody.getUnits().insertAfter(jimp.newAssignStmt(leftlocal, jimp.newCastExpr(newlocal, lefttype)),
        Util.findLastIdentityUnit(this.stmtBody, stmt));
    return newlocal;
  }

  private Local insertCast(Value oldvalue, Type oldtype, Type type, Stmt stmt) {
    final Jimple jimp = Jimple.v();
    Local newlocal1 = localGenerator.generateLocal(oldtype);
    Local newlocal2 = localGenerator.generateLocal(type);

    Unit u = Util.findFirstNonIdentityUnit(this.stmtBody, stmt);
    final UnitPatchingChain units = stmtBody.getUnits();
    units.insertBefore(jimp.newAssignStmt(newlocal1, oldvalue), u);
    units.insertBefore(jimp.newAssignStmt(newlocal2, jimp.newCastExpr(newlocal1, type)), u);
    return newlocal2;
  }
}
