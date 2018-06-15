package soot.jimple.toolkits.typing.fast;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2008 Ben Bellamy
 *
 * All rights reserved.
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

import java.util.Iterator;

import soot.ArrayType;
import soot.BooleanType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.NullType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootMethodRef;
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
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.Constant;
import soot.jimple.DivExpr;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.EqExpr;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.GeExpr;
import soot.jimple.GotoStmt;
import soot.jimple.GtExpr;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.LtExpr;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NopStmt;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.RemExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.Stmt;
import soot.jimple.SubExpr;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UshrExpr;
import soot.jimple.XorExpr;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

/**
 * This checks all uses against the rules in Jimple, except some uses are not checked where the bytecode verifier guarantees
 * use validity.
 *
 * @author Ben Bellamy
 */
public class UseChecker extends AbstractStmtSwitch {
  private JimpleBody jb;

  private Typing tg;
  private IUseVisitor uv;

  private LocalDefs defs = null;
  private LocalUses uses = null;

  public UseChecker(JimpleBody jb) {
    this.jb = jb;
  }

  public void check(Typing tg, IUseVisitor uv) {
    this.tg = tg;
    this.uv = uv;
    if (this.tg == null) {
      throw new RuntimeException("null typing passed to useChecker");
    }

    for (Iterator<Unit> i = this.jb.getUnits().snapshotIterator(); i.hasNext();) {
      if (uv.finish()) {
        return;
      }
      i.next().apply(this);
    }
  }

  private void handleInvokeExpr(InvokeExpr ie, Stmt stmt) {
    SootMethodRef m = ie.getMethodRef();

    if (ie instanceof InstanceInvokeExpr) {
      InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
      iie.setBase(this.uv.visit(iie.getBase(), m.declaringClass().getType(), stmt));
    }

    for (int i = 0; i < ie.getArgCount(); i++) {
      ie.setArg(i, this.uv.visit(ie.getArg(i), m.parameterType(i), stmt));
    }
  }

  private void handleBinopExpr(BinopExpr be, Stmt stmt, Type tlhs) {
    Value opl = be.getOp1(), opr = be.getOp2();
    Type tl = AugEvalFunction.eval_(this.tg, opl, stmt, this.jb), tr = AugEvalFunction.eval_(this.tg, opr, stmt, this.jb);

    if (be instanceof AddExpr || be instanceof SubExpr || be instanceof MulExpr || be instanceof DivExpr
        || be instanceof RemExpr || be instanceof GeExpr || be instanceof GtExpr || be instanceof LeExpr
        || be instanceof LtExpr || be instanceof ShlExpr || be instanceof ShrExpr || be instanceof UshrExpr) {
      if (tlhs instanceof IntegerType) {
        be.setOp1(this.uv.visit(opl, IntType.v(), stmt));
        be.setOp2(this.uv.visit(opr, IntType.v(), stmt));
      }
    } else if (be instanceof CmpExpr || be instanceof CmpgExpr || be instanceof CmplExpr) {
      // No checks in the original assigner
    } else if (be instanceof AndExpr || be instanceof OrExpr || be instanceof XorExpr) {
      be.setOp1(this.uv.visit(opl, tlhs, stmt));
      be.setOp2(this.uv.visit(opr, tlhs, stmt));
    } else if (be instanceof EqExpr || be instanceof NeExpr) {
      if (tl instanceof BooleanType && tr instanceof BooleanType) {
      } else if (tl instanceof Integer1Type || tr instanceof Integer1Type) {
      } else if (tl instanceof IntegerType) {
        be.setOp1(this.uv.visit(opl, IntType.v(), stmt));
        be.setOp2(this.uv.visit(opr, IntType.v(), stmt));
      }
    }
  }

  private void handleArrayRef(ArrayRef ar, Stmt stmt) {
    ar.setIndex(this.uv.visit(ar.getIndex(), IntType.v(), stmt));
  }

  private void handleInstanceFieldRef(InstanceFieldRef ifr, Stmt stmt) {
    ifr.setBase(this.uv.visit(ifr.getBase(), ifr.getFieldRef().declaringClass().getType(), stmt));
  }

  public void caseBreakpointStmt(BreakpointStmt stmt) {
  }

  public void caseInvokeStmt(InvokeStmt stmt) {
    this.handleInvokeExpr(stmt.getInvokeExpr(), stmt);
  }

  public void caseAssignStmt(AssignStmt stmt) {
    Value lhs = stmt.getLeftOp();
    Value rhs = stmt.getRightOp();
    Type tlhs = null;

    if (lhs instanceof Local) {
      tlhs = this.tg.get((Local) lhs);
    } else if (lhs instanceof ArrayRef) {
      ArrayRef aref = (ArrayRef) lhs;
      Local base = (Local) aref.getBase();

      // Try to force Type integrity. The left side must agree on the
      // element type of the right side array reference.
      ArrayType at = null;
      Type tgType = this.tg.get(base);
      if (tgType instanceof ArrayType) {
        at = (ArrayType) tgType;
      } else {
        // If the right-hand side is a primitive and the left-side type
        // is java.lang.Object
        if (tgType == Scene.v().getObjectType() && rhs instanceof Local) {
          Type rhsType = this.tg.get((Local) rhs);
          if (rhsType instanceof PrimType) {
            if (defs == null) {
              defs = LocalDefs.Factory.newLocalDefs(jb);
              uses = LocalUses.Factory.newLocalUses(jb, defs);
            }

            // Check the original type of the array from the alloc site
            for (Unit defU : defs.getDefsOfAt(base, stmt)) {
              if (defU instanceof AssignStmt) {
                AssignStmt defUas = (AssignStmt) defU;
                if (defUas.getRightOp() instanceof NewArrayExpr) {
                  at = (ArrayType) defUas.getRightOp().getType();
                  break;
                }
              }
            }
          }
        }

        if (at == null) {
          at = tgType.makeArrayType();
        }
      }
      tlhs = ((ArrayType) at).getElementType();

      this.handleArrayRef(aref, stmt);

      aref.setBase((Local) this.uv.visit(aref.getBase(), at, stmt));
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
      stmt.setLeftOp(this.uv.visit(lhs, tlhs, stmt));
    } else if (lhs instanceof FieldRef) {
      tlhs = ((FieldRef) lhs).getFieldRef().type();
      if (lhs instanceof InstanceFieldRef) {
        this.handleInstanceFieldRef((InstanceFieldRef) lhs, stmt);
      }
    }

    // They may have been changed above
    lhs = stmt.getLeftOp();
    rhs = stmt.getRightOp();

    if (rhs instanceof Local) {
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof ArrayRef) {
      ArrayRef aref = (ArrayRef) rhs;
      Local base = (Local) aref.getBase();

      // try to force Type integrity
      ArrayType at = null;
      Type et = null;
      if (this.tg.get(base) instanceof ArrayType) {
        at = (ArrayType) this.tg.get(base);
      } else {
        Type bt = this.tg.get(base);
        // At the very least, the the type for this array should be whatever its
        // base type is
        et = bt;

        // If we have a type of java.lang.Object and access it like an object,
        // this could lead to any kind of object, so we have to look at the uses.
        // For some fixed type T, we assume that we can fix the array to T[].
        if (bt instanceof RefType || bt instanceof NullType) {
          RefType rt = bt instanceof NullType ? null : (RefType) bt;
          if (rt == null || rt.getSootClass().getName().equals("java.lang.Object")
              || rt.getSootClass().getName().equals("java.io.Serializable")
              || rt.getSootClass().getName().equals("java.lang.Cloneable")) {
            if (defs == null) {
              defs = LocalDefs.Factory.newLocalDefs(jb);
              uses = LocalUses.Factory.newLocalUses(jb, defs);
            }

            outer: for (UnitValueBoxPair usePair : uses.getUsesOf(stmt)) {
              Stmt useStmt = (Stmt) usePair.getUnit();
              // Is the array element used in an invocation for which we have a type
              // from the callee's signature=
              if (useStmt.containsInvokeExpr()) {
                for (int i = 0; i < useStmt.getInvokeExpr().getArgCount(); i++) {
                  if (useStmt.getInvokeExpr().getArg(i) == usePair.getValueBox().getValue()) {
                    et = useStmt.getInvokeExpr().getMethod().getParameterType(i);
                    at = et.makeArrayType();
                    break outer;
                  }
                }
              }
              // If we have a comparison, we look at the other value. Using the type
              // of the value is at least closer to the truth than java.lang.Object
              // if the other value is a primitive.
              else if (useStmt instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt) useStmt;
                if (ifStmt.getCondition() instanceof EqExpr) {
                  EqExpr expr = (EqExpr) ifStmt.getCondition();
                  final Value other;
                  if (expr.getOp1() == usePair.getValueBox().getValue()) {
                    other = expr.getOp2();
                  } else {
                    other = expr.getOp1();
                  }

                  Type newEt = getTargetType(other);
                  if (newEt != null) {
                    et = newEt;
                  }
                }
              } else if (useStmt instanceof AssignStmt) {
                // For binary expressions, we can look for type information in the
                // other operands
                AssignStmt useAssignStmt = (AssignStmt) useStmt;
                if (useAssignStmt.getRightOp() instanceof BinopExpr) {
                  BinopExpr binOp = (BinopExpr) useAssignStmt.getRightOp();
                  final Value other;
                  if (binOp.getOp1() == usePair.getValueBox().getValue()) {
                    other = binOp.getOp2();
                  } else {
                    other = binOp.getOp1();
                  }

                  Type newEt = getTargetType(other);
                  if (newEt != null) {
                    et = newEt;
                  }
                }
              } else if (useStmt instanceof ReturnStmt) {
                et = jb.getMethod().getReturnType();
              }
            }
          }
        }

        if (at == null) {
          at = et.makeArrayType();
        }
      }
      Type trhs = ((ArrayType) at).getElementType();

      this.handleArrayRef(aref, stmt);

      aref.setBase((Local) this.uv.visit(aref.getBase(), at, stmt));
      stmt.setRightOp(this.uv.visit(rhs, trhs, stmt));
    } else if (rhs instanceof InstanceFieldRef) {
      this.handleInstanceFieldRef((InstanceFieldRef) rhs, stmt);
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof BinopExpr) {
      this.handleBinopExpr((BinopExpr) rhs, stmt, tlhs);
    } else if (rhs instanceof InvokeExpr) {
      this.handleInvokeExpr((InvokeExpr) rhs, stmt);
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof CastExpr) {
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof InstanceOfExpr) {
      InstanceOfExpr ioe = (InstanceOfExpr) rhs;
      ioe.setOp(this.uv.visit(ioe.getOp(), RefType.v("java.lang.Object"), stmt));
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof NewArrayExpr) {
      NewArrayExpr nae = (NewArrayExpr) rhs;
      nae.setSize(this.uv.visit(nae.getSize(), IntType.v(), stmt));
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof NewMultiArrayExpr) {
      NewMultiArrayExpr nmae = (NewMultiArrayExpr) rhs;
      for (int i = 0; i < nmae.getSizeCount(); i++) {
        nmae.setSize(i, this.uv.visit(nmae.getSize(i), IntType.v(), stmt));
      }
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof LengthExpr) {
      stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof NegExpr) {
      ((NegExpr) rhs).setOp(this.uv.visit(((NegExpr) rhs).getOp(), tlhs, stmt));
    } else if (rhs instanceof Constant) {
      if (!(rhs instanceof NullConstant)) {
        stmt.setRightOp(this.uv.visit(rhs, tlhs, stmt));
      }
    }
  }

  private Type getTargetType(final Value other) {
    if (other instanceof Constant) {
      if (other.getType() != NullType.v()) {
        return other.getType();
      }
    } else if (other instanceof Local) {
      Type tgTp = tg.get((Local) other);
      if (tgTp instanceof PrimType) {
        return tgTp;
      }
    }
    return null;
  }

  public void caseIdentityStmt(IdentityStmt stmt) {
  }

  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
    stmt.setOp(this.uv.visit(stmt.getOp(), RefType.v("java.lang.Object"), stmt));
  }

  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
    stmt.setOp(this.uv.visit(stmt.getOp(), RefType.v("java.lang.Object"), stmt));
  }

  public void caseGotoStmt(GotoStmt stmt) {
  }

  public void caseIfStmt(IfStmt stmt) {
    this.handleBinopExpr((BinopExpr) stmt.getCondition(), stmt, BooleanType.v());
  }

  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    stmt.setKey(this.uv.visit(stmt.getKey(), IntType.v(), stmt));
  }

  public void caseNopStmt(NopStmt stmt) {
  }

  public void caseReturnStmt(ReturnStmt stmt) {
    stmt.setOp(this.uv.visit(stmt.getOp(), this.jb.getMethod().getReturnType(), stmt));
  }

  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
  }

  public void caseTableSwitchStmt(TableSwitchStmt stmt) {
    stmt.setKey(this.uv.visit(stmt.getKey(), IntType.v(), stmt));
  }

  public void caseThrowStmt(ThrowStmt stmt) {
    stmt.setOp(this.uv.visit(stmt.getOp(), RefType.v("java.lang.Throwable"), stmt));
  }

  public void defaultCase(Stmt stmt) {
    throw new RuntimeException("Unhandled stgtement type: " + stmt.getClass());
  }
}
