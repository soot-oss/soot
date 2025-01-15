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

import heros.solver.Pair;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.BooleanType;
import soot.G;
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

  private final JimpleBody jb;

  private ITyping tg;
  private IUseVisitor uv;

  private static final Logger logger = LoggerFactory.getLogger(UseChecker.class);

  public static class UseCheckerCache {
    private LocalDefs defs = null;
    private LocalUses uses = null;

    private final Type objectType = Scene.v().getObjectType();
    private final JimpleBody body;

    public UseCheckerCache(JimpleBody body) {
      this.body = body;
    }

    public Type getObjectType() {
      return objectType;
    }

    public LocalDefs getDefs() {
      LocalDefs d = defs;
      if (d == null) {
        d = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(body);
        defs = d;
      }
      return d;
    }

    public LocalUses getUses() {
      LocalUses u = uses;
      if (u != null) {
        return u;
      }
      u = LocalUses.Factory.newLocalUses(body, getDefs());
      uses = u;
      return u;
    }
  }

  private UseCheckerCache cache;

  public UseChecker(JimpleBody jb) {
    this.jb = jb;
    cache = new UseCheckerCache(jb);
  }

  public UseChecker(JimpleBody jb, UseCheckerCache cache) {
    this.jb = jb;
    this.cache = cache;
  }

  public void check(ITyping tg, IUseVisitor uv) {
    if (tg == null) {
      throw new RuntimeException("null typing passed to useChecker");
    }

    this.tg = tg;
    this.uv = uv;
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
      iie.setBase(this.uv.visit(iie.getBase(), m.getDeclaringClass().getType(), stmt));
    }

    for (int i = 0, e = ie.getArgCount(); i < e; i++) {
      ie.setArg(i, this.uv.visit(ie.getArg(i), m.getParameterType(i), stmt));
    }
  }

  private void handleBinopExpr(BinopExpr be, Stmt stmt, Type tlhs) {
    Value opl = be.getOp1(), opr = be.getOp2();
    Type tl = AugEvalFunction.eval_(this.tg, opl, stmt, this.jb);
    Type tr = AugEvalFunction.eval_(this.tg, opr, stmt, this.jb);

    if (be instanceof AddExpr || be instanceof SubExpr || be instanceof MulExpr || be instanceof DivExpr
        || be instanceof RemExpr || be instanceof GeExpr || be instanceof GtExpr || be instanceof LeExpr
        || be instanceof LtExpr || be instanceof ShlExpr || be instanceof ShrExpr || be instanceof UshrExpr) {
      if (tlhs instanceof IntegerType) {
        be.setOp1(this.uv.visit(opl, IntType.v(), stmt, true));
        be.setOp2(this.uv.visit(opr, IntType.v(), stmt, true));
      }
    } else if (be instanceof CmpExpr || be instanceof CmpgExpr || be instanceof CmplExpr) {
      // No checks in the original assigner
    } else if (be instanceof AndExpr || be instanceof OrExpr || be instanceof XorExpr) {
      be.setOp1(this.uv.visit(opl, tlhs, stmt, true));
      be.setOp2(this.uv.visit(opr, tlhs, stmt, true));
    } else if (be instanceof EqExpr || be instanceof NeExpr) {
      if (tl instanceof BooleanType && tr instanceof BooleanType) {
      } else if (tl instanceof Integer1Type || tr instanceof Integer1Type) {
      } else if (tl instanceof IntegerType) {
        be.setOp1(this.uv.visit(opl, IntType.v(), stmt, true));
        be.setOp2(this.uv.visit(opr, IntType.v(), stmt, true));
      }
    }
  }

  private void handleArrayRef(ArrayRef ar, Stmt stmt) {
    ar.setIndex(this.uv.visit(ar.getIndex(), IntType.v(), stmt));
  }

  private void handleInstanceFieldRef(InstanceFieldRef ifr, Stmt stmt) {
    ifr.setBase(this.uv.visit(ifr.getBase(), ifr.getFieldRef().declaringClass().getType(), stmt));
  }

  @Override
  public void caseBreakpointStmt(BreakpointStmt stmt) {
  }

  @Override
  public void caseInvokeStmt(InvokeStmt stmt) {
    this.handleInvokeExpr(stmt.getInvokeExpr(), stmt);
  }

  @Override
  public void caseAssignStmt(AssignStmt stmt) {
    Value lhs = stmt.getLeftOp();
    Value rhs = stmt.getRightOp();
    Type tlhs = null;
    LocalDefs defs = cache.defs;
    LocalUses uses = cache.uses;
    final IUseVisitor uv = this.uv;

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
        if (rhs instanceof Local) {
          Type rhsType = this.tg.get((Local) rhs);
          if ((tgType == cache.getObjectType() && rhsType instanceof PrimType) || tgType instanceof WeakObjectType) {
            if (defs == null) {
              defs = cache.getDefs();
              uses = cache.getUses();
            }

            // Check the original type of the array from the alloc site
            boolean hasDefs = false;
            for (Unit defU : defs.getDefsOfAt(base, stmt)) {
              if (defU instanceof AssignStmt) {
                AssignStmt defUas = (AssignStmt) defU;
                if (defUas.getRightOp() instanceof NewArrayExpr) {
                  at = (ArrayType) defUas.getRightOp().getType();
                  hasDefs = true;
                  break;
                }
              }
            }

            if (!hasDefs) {
              at = ArrayType.v(rhsType, 1);
            }
          }
        }

        if (at == null) {
          at = tgType.makeArrayType();
        }
      }
      tlhs = ((ArrayType) at).getElementType();

      this.handleArrayRef(aref, stmt);

      aref.setBase((Local) uv.visit(aref.getBase(), at, stmt));
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
      stmt.setLeftOp(uv.visit(lhs, tlhs, stmt));
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
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof ArrayRef) {
      ArrayRef aref = (ArrayRef) rhs;
      Local base = (Local) aref.getBase();

      // try to force Type integrity
      final ArrayType at;
      final Type bt = this.tg.get(base);
      if (bt instanceof ArrayType) {
        at = (ArrayType) bt;
      } else {
        Type et = null;

        // If we have a type of java.lang.Object and access it like an object,
        // this could lead to any kind of object, so we have to look at the uses.
        // For some fixed type T, we assume that we can fix the array to T[].
        if (bt instanceof RefType || bt instanceof NullType) {
          String btName = bt instanceof NullType ? null : ((RefType) bt).getSootClass().getName();
          if (btName == null || cache.getObjectType().toString().equals(btName) || "java.io.Serializable".equals(btName)
              || "java.lang.Cloneable".equals(btName)) {
            if (defs == null) {
              defs = cache.getDefs();
              uses = cache.getUses();
            }
            // First, we check the definitions. If we can see the definitions and know the array type
            // that way, we are safe.
            ArrayDeque<Pair<Unit, Local>> worklist = new ArrayDeque<Pair<Unit, Local>>();
            Set<Pair<Unit, Local>> seen = new HashSet<>();
            worklist.add(new Pair<>(stmt, (Local) ((ArrayRef) rhs).getBase()));
            while (!worklist.isEmpty()) {
              Pair<Unit, Local> r = worklist.removeFirst();
              if (!seen.add(r)) {
                // Make sure we only process each entry once
                continue;
              }

              List<Unit> d = defs.getDefsOfAt(r.getO2(), r.getO1());
              if (d.isEmpty()) {
                // In this case, probably we are asking for some variable which got casted. Since the local defs and uses are
                // cached
                // they might not reflect this.
                defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(jb);
                uses = LocalUses.Factory.newLocalUses(jb, defs);
                d = defs.getDefsOfAt(r.getO2(), r.getO1());
              }
              for (Unit u : d) {
                if (u instanceof AssignStmt) {
                  AssignStmt assign = (AssignStmt) u;
                  Value rop = assign.getRightOp();
                  if (rop instanceof NewArrayExpr) {
                    et = merge(stmt, et, ((NewArrayExpr) assign.getRightOp()).getBaseType());
                  } else if (rop instanceof Local) {
                    worklist.add(new Pair<>(u, (Local) rop));
                  } else if (rop instanceof CastExpr) {
                    worklist.add(new Pair<>(u, (Local) ((CastExpr) rop).getOp()));
                  }
                }
              }
            }

            // Take a look at uses if the definitions didn't give any intel.
            if (et == null) {
              OUTER: for (UnitValueBoxPair usePair : uses.getUsesOf(stmt)) {
                Stmt useStmt = (Stmt) usePair.getUnit();
                // Is the array element used in an invocation for which we have a type
                // from the callee's signature=
                if (useStmt.containsInvokeExpr()) {
                  InvokeExpr invokeExpr = useStmt.getInvokeExpr();
                  for (int i = 0, e = invokeExpr.getArgCount(); i < e; i++) {
                    if (invokeExpr.getArg(i) == usePair.getValueBox().getValue()) {
                      et = merge(stmt, et, invokeExpr.getMethod().getParameterType(i));
                      break OUTER;
                    }
                  }
                } else if (useStmt instanceof IfStmt) {
                  // If we have a comparison, we look at the other value. Using
                  // the type of the value is at least closer to the truth than
                  // java.lang.Object if the other value is a primitive.
                  Value condition = ((IfStmt) useStmt).getCondition();
                  if (condition instanceof EqExpr) {
                    EqExpr expr = (EqExpr) condition;
                    final Value other;
                    if (expr.getOp1() == usePair.getValueBox().getValue()) {
                      other = expr.getOp2();
                    } else {
                      other = expr.getOp1();
                    }

                    Type newEt = getTargetType(other);
                    if (newEt != null) {
                      et = merge(stmt, et, newEt);
                    }
                  }
                } else if (useStmt instanceof AssignStmt) {
                  // For binary expressions, we can look for type information
                  // in the other operands.
                  AssignStmt useAssignStmt = (AssignStmt) useStmt;
                  Value rop = useAssignStmt.getRightOp();
                  if (rop instanceof BinopExpr) {
                    BinopExpr binOp = (BinopExpr) rop;
                    final Value other;
                    if (binOp.getOp1() == usePair.getValueBox().getValue()) {
                      other = binOp.getOp2();
                    } else {
                      other = binOp.getOp1();
                    }

                    Type newEt = getTargetType(other);
                    if (newEt != null) {
                      et = merge(stmt, et, newEt);
                    }
                  } else if (rop instanceof CastExpr) {
                    et = merge(stmt, et, ((CastExpr) rop).getCastType());
                  }
                } else if (useStmt instanceof ReturnStmt) {
                  et = merge(stmt, et, jb.getMethod().getReturnType());
                }
              }
            }
          }
        }

        if (et == null) {
          // At the very least, the the type for this array should be whatever its
          // base type is
          et = bt;
          if (logger.isDebugEnabled()) {
            //This can happen in rare cases in Android for int/float and long/double arrays
            logger.debug(
                "Could not find any indication on the array type of " + stmt + " in " + jb.getMethod().getSignature(),
                ", assuming its base type is " + bt);
          }
        }

        at = et.makeArrayType();
      }
      Type trhs = ((ArrayType) at).getElementType();

      this.handleArrayRef(aref, stmt);

      aref.setBase((Local) uv.visit(aref.getBase(), at, stmt));
      stmt.setRightOp(uv.visit(rhs, trhs, stmt));
    } else if (rhs instanceof InstanceFieldRef) {
      this.handleInstanceFieldRef((InstanceFieldRef) rhs, stmt);
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof BinopExpr) {
      this.handleBinopExpr((BinopExpr) rhs, stmt, tlhs);
    } else if (rhs instanceof InvokeExpr) {
      this.handleInvokeExpr((InvokeExpr) rhs, stmt);
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof CastExpr) {
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof InstanceOfExpr) {
      InstanceOfExpr ioe = (InstanceOfExpr) rhs;
      ioe.setOp(uv.visit(ioe.getOp(), Scene.v().getObjectType(), stmt));
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof NewArrayExpr) {
      NewArrayExpr nae = (NewArrayExpr) rhs;
      nae.setSize(uv.visit(nae.getSize(), IntType.v(), stmt));
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof NewMultiArrayExpr) {
      NewMultiArrayExpr nmae = (NewMultiArrayExpr) rhs;
      for (int i = 0, e = nmae.getSizeCount(); i < e; i++) {
        nmae.setSize(i, uv.visit(nmae.getSize(i), IntType.v(), stmt));
      }
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof LengthExpr) {
      stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
    } else if (rhs instanceof NegExpr) {
      ((NegExpr) rhs).setOp(uv.visit(((NegExpr) rhs).getOp(), tlhs, stmt));
    } else if (rhs instanceof Constant) {
      if (!(rhs instanceof NullConstant)) {
        stmt.setRightOp(uv.visit(rhs, tlhs, stmt));
      }
    }
  }

  protected Type merge(Stmt stmt, Type previousType, Type newType) {
    if (previousType == null) {
      return newType;
    }
    if (newType == previousType) {
      return previousType;
    }
    Type choose;
    // we choose the wider one. Note that this probably still results in code which cannot be executed!
    if (TypeUtils.getValueBitSize(previousType) > TypeUtils.getValueBitSize(newType)) {
      choose = previousType;
    } else {
      choose = newType;
    }
    logger.warn("Conflicting array types at " + stmt + " in " + jb.getMethod().getSignature(),
        ", its base type may be " + previousType + " or " + newType + ". Choosing " + choose + ".");
    return newType;
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

  @Override
  public void caseIdentityStmt(IdentityStmt stmt) {
  }

  @Override
  public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
    stmt.setOp(this.uv.visit(stmt.getOp(), RefType.v("java.lang.Object"), stmt));
  }

  @Override
  public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
    stmt.setOp(this.uv.visit(stmt.getOp(), RefType.v("java.lang.Object"), stmt));
  }

  @Override
  public void caseGotoStmt(GotoStmt stmt) {
  }

  @Override
  public void caseIfStmt(IfStmt stmt) {
    this.handleBinopExpr((BinopExpr) stmt.getCondition(), stmt, BooleanType.v());
  }

  @Override
  public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
    stmt.setKey(this.uv.visit(stmt.getKey(), IntType.v(), stmt));
  }

  @Override
  public void caseNopStmt(NopStmt stmt) {
  }

  @Override
  public void caseReturnStmt(ReturnStmt stmt) {
    stmt.setOp(this.uv.visit(stmt.getOp(), this.jb.getMethod().getReturnType(), stmt));
  }

  @Override
  public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
  }

  @Override
  public void caseTableSwitchStmt(TableSwitchStmt stmt) {
    stmt.setKey(this.uv.visit(stmt.getKey(), IntType.v(), stmt));
  }

  @Override
  public void caseThrowStmt(ThrowStmt stmt) {
    stmt.setOp(this.uv.visit(stmt.getOp(), Scene.v().getBaseExceptionType(), stmt));
  }

  @Override
  public void defaultCase(Object stmt) {
    throw new RuntimeException("Unhandled type: " + stmt.getClass());
  }
}
