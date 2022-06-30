package soot.jimple.toolkits.typing.fast;

import java.util.ArrayDeque;

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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.G;
import soot.IntegerType;
import soot.Local;
import soot.LocalGenerator;
import soot.PatchingChain;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NegExpr;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.typing.Util;
import soot.toolkits.scalar.LocalDefs;

/**
 * New Type Resolver by Ben Bellamy (see 'Efficient Local Type Inference' at OOPSLA 08).
 *
 * Ben has tested this code, and verified that it provides a typing that is at least as tight as the original algorithm
 * (tighter in 2914 methods out of 295598) on a number of benchmarks. These are: abc-complete.jar, BlueJ, CSO (Scala code),
 * Gant, Groovy, havoc.jar, Java 3D, jEdit, Java Grande Forum, Jigsaw, Jython, Kawa, rt.jar, Kawa, Scala and tools.jar. The
 * mean execution time improvement is around 10 times, but for the longest methods (abc parser methods and havoc with >9000
 * statements) the improvement is between 200 and 500 times.
 *
 * @author Ben Bellamy
 */
public class TypeResolver {
  protected final JimpleBody jb;

  private final List<DefinitionStmt> assignments;
  private final HashMap<Local, BitSet> depends;
  private final LocalGenerator localGenerator;

  public TypeResolver(JimpleBody jb) {
    this.jb = jb;
    this.assignments = new ArrayList<DefinitionStmt>();
    this.depends = new HashMap<Local, BitSet>(jb.getLocalCount());
    this.localGenerator = Scene.v().createLocalGenerator(jb);
    this.initAssignments();
  }

  private void initAssignments() {
    for (Unit stmt : this.jb.getUnits()) {
      if (stmt instanceof DefinitionStmt) {
        this.initAssignment((DefinitionStmt) stmt);
      }
    }
  }

  private void initAssignment(DefinitionStmt ds) {
    Value lhs = ds.getLeftOp();
    if (lhs instanceof Local || lhs instanceof ArrayRef) {
      int assignmentIdx = this.assignments.size();
      this.assignments.add(ds);

      Value rhs = ds.getRightOp();
      if (rhs instanceof Local) {
        this.addDepend((Local) rhs, assignmentIdx);
      } else if (rhs instanceof BinopExpr) {
        BinopExpr be = (BinopExpr) rhs;
        Value lop = be.getOp1(), rop = be.getOp2();
        if (lop instanceof Local) {
          this.addDepend((Local) lop, assignmentIdx);
        }
        if (rop instanceof Local) {
          this.addDepend((Local) rop, assignmentIdx);
        }
      } else if (rhs instanceof NegExpr) {
        Value op = ((NegExpr) rhs).getOp();
        if (op instanceof Local) {
          this.addDepend((Local) op, assignmentIdx);
        }
      } else if (rhs instanceof CastExpr) {
        Value op = ((CastExpr) rhs).getOp();
        if (op instanceof Local) {
          this.addDepend((Local) op, assignmentIdx);
        }
      } else if (rhs instanceof ArrayRef) {
        this.addDepend((Local) ((ArrayRef) rhs).getBase(), assignmentIdx);
      }
    }
  }

  private void addDepend(Local v, int stmtIndex) {
    BitSet d = this.depends.get(v);
    if (d == null) {
      d = new BitSet();
      this.depends.put(v, d);
    }
    d.set(stmtIndex);
  }

  public void inferTypes() {
    ITypingStrategy typingStrategy = getTypingStrategy();
    AugEvalFunction ef = new AugEvalFunction(this.jb);
    BytecodeHierarchy bh = new BytecodeHierarchy();
    Collection<Typing> sigma = this.applyAssignmentConstraints(typingStrategy.createTyping(this.jb.getLocals()), ef, bh);

    // If there is nothing to type, we can quit
    if (sigma.isEmpty()) {
      return;
    }

    int[] castCount = new int[1];
    Typing tg = this.minCasts(sigma, bh, castCount);
    if (castCount[0] != 0) {
      this.split_new();
      sigma = this.applyAssignmentConstraints(typingStrategy.createTyping(this.jb.getLocals()), ef, bh);
      tg = this.minCasts(sigma, bh, castCount);
    }

    this.insertCasts(tg, bh, false);

    final BottomType bottom = BottomType.v();
    for (Local v : this.jb.getLocals()) {
      Type t = tg.get(v);
      if (t instanceof IntegerType) {
        // t = inttype;
        tg.set(v, bottom);
      }
      v.setType(t);
    }

    tg = this.typePromotion(tg);
    if (tg == null) {
      // Use original soot algorithm for inserting casts
      soot.jimple.toolkits.typing.integer.TypeResolver.resolve(this.jb);
    } else {
      for (Local v : this.jb.getLocals()) {
        Type type = tg.get(v);
        v.setType(type);
      }
    }
  }

  protected ITypingStrategy getTypingStrategy() {
    return DefaultTypingStrategy.INSTANCE;
  }

  public class CastInsertionUseVisitor implements IUseVisitor {
    protected JimpleBody jb;
    protected Typing tg;
    protected IHierarchy h;

    private final boolean countOnly;
    private int count;

    public CastInsertionUseVisitor(boolean countOnly, JimpleBody jb, Typing tg, IHierarchy h) {
      this.jb = jb;
      this.tg = tg;
      this.h = h;

      this.countOnly = countOnly;
      this.count = 0;
    }

    @Override
    public Value visit(Value op, Type useType, Stmt stmt, boolean checkOnly) {
      Type t = AugEvalFunction.eval_(this.tg, op, stmt, this.jb);
      if (useType == t) {
        return op;
      }

      boolean needCast = false;
      if (useType instanceof PrimType && t instanceof PrimType) {
        if (t.isAllowedInFinalCode() && useType.isAllowedInFinalCode()) {
          needCast = true;
        }
      }
      if (!needCast && this.h.ancestor(useType, t)) {
        return op;
      }

      this.count++;

      if (countOnly) {
        return op;
      } else {
        // If we're referencing an array of the base type java.lang.Object,
        // we also need to fix the type of the assignment's target variable.
        if (stmt.containsArrayRef() && stmt.getArrayRef().getBase() == op && stmt instanceof DefinitionStmt) {
          Value leftOp = ((DefinitionStmt) stmt).getLeftOp();
          if (leftOp instanceof Local) {
            Type baseType = tg.get((Local) op);
            if (baseType instanceof RefType && isObjectLikeType((RefType) baseType)) {
              tg.set((Local) leftOp, ((ArrayType) useType).getElementType());
            }
          }
        }

        Local vold;
        if (op instanceof Local) {
          vold = (Local) op;
        } else {
          /*
           * By the time we have countOnly == false, all variables must by typed with concrete Jimple types, and never
           * [0..1], [0..127] or [0..32767].
           */
          vold = localGenerator.generateLocal(t);
          this.tg.set(vold, t);
          this.jb.getUnits().insertBefore(Jimple.v().newAssignStmt(vold, op), Util.findFirstNonIdentityUnit(this.jb, stmt));
        }
        // Cast from the original type to the type that we use in the code
        return createCast(useType, stmt, vold, false);
      }
    }

    private boolean isObjectLikeType(RefType rt) {
      if (rt instanceof WeakObjectType) {
        return true;
      } else {
        final String name = rt.getSootClass().getName();
        return "java.lang.Object".equals(name) || "java.io.Serializable".equals(name) || "java.lang.Cloneable".equals(name);
      }
    }

    /**
     * Creates a cast at stmt of vold to the given type.
     * 
     * @param useType
     *          the new type
     * @param stmt
     *          stmt
     * @param old
     *          the old local
     * @param after
     *          True to insert the cast after the statement, false to insert it before
     * @return the new local
     */
    protected Local createCast(Type useType, Stmt stmt, Local old, boolean after) {
      Local vnew = localGenerator.generateLocal(useType);
      this.tg.set(vnew, useType);
      Jimple jimple = Jimple.v();
      AssignStmt newStmt = jimple.newAssignStmt(vnew, jimple.newCastExpr(old, useType));
      Unit u = Util.findFirstNonIdentityUnit(this.jb, stmt);
      if (after) {
        this.jb.getUnits().insertAfter(newStmt, u);
      } else {
        this.jb.getUnits().insertBefore(newStmt, u);
      }
      return vnew;
    }

    public int getCount() {
      return this.count;
    }

    @Override
    public boolean finish() {
      return false;
    }
  }

  final BooleanType booleanType = BooleanType.v();
  final ByteType byteType = ByteType.v();
  final ShortType shortType = ShortType.v();

  private Typing typePromotion(Typing tg) {
    boolean conversionsPending;
    do {
      AugEvalFunction ef = new AugEvalFunction(this.jb);
      AugHierarchy h = new AugHierarchy();
      UseChecker uc = new UseChecker(this.jb);
      TypePromotionUseVisitor uv = new TypePromotionUseVisitor(jb, tg);
      do {
        Collection<Typing> sigma = this.applyAssignmentConstraints(tg, ef, h);
        if (sigma.isEmpty()) {
          return null;
        }
        tg = sigma.iterator().next();
        uv.typingChanged = false;
        uc.check(tg, uv);
        if (uv.fail) {
          return null;
        }

      } while (uv.typingChanged);

      conversionsPending = false;
      for (Local v : this.jb.getLocals()) {
        Type t = tg.get(v);
        Type r = convert(t);
        if (r != null) {
          tg.set(v, r);
          conversionsPending = true;
        }
      }
    } while (conversionsPending);

    return tg;
  }

  protected Type convert(Type t) {
    if (t instanceof Integer1Type) {
      return booleanType;
    } else if (t instanceof Integer127Type) {
      return byteType;
    } else if (t instanceof Integer32767Type) {
      return shortType;
    } else if (t instanceof WeakObjectType) {
      return RefType.v(((WeakObjectType) t).getClassName());
    } else if (t instanceof ArrayType) {
      ArrayType r = (ArrayType) t;
      Type cv = convert(r.getElementType());
      if (cv != null) {
        return ArrayType.v(cv, r.numDimensions);
      }
    }
    return null;
  }

  private int insertCasts(Typing tg, IHierarchy h, boolean countOnly) {
    UseChecker uc = new UseChecker(this.jb);
    CastInsertionUseVisitor uv = createCastInsertionUseVisitor(tg, h, countOnly);
    uc.check(tg, uv);
    return uv.getCount();
  }

  /**
   * Allows clients to provide an own visitor for cast insertion
   * 
   * @param tg
   *          the typing
   * @param h
   *          the hierarchy
   * @param countOnly
   *          whether to count only (no actual changes)
   * @return the visitor
   */
  protected CastInsertionUseVisitor createCastInsertionUseVisitor(Typing tg, IHierarchy h, boolean countOnly) {
    return new CastInsertionUseVisitor(countOnly, this.jb, tg, h);
  }

  private Typing minCasts(Collection<Typing> sigma, IHierarchy h, int[] count) {
    count[0] = -1;
    Typing r = null;
    for (Typing tg : sigma) {
      int n = this.insertCasts(tg, h, true);
      if (count[0] == -1 || n < count[0]) {
        count[0] = n;
        r = tg;
      }
    }
    return r;
  }

  static class WorklistElement {
    Typing typing;
    BitSet worklist;

    public WorklistElement(Typing tg, BitSet wl) {
      this.typing = tg;
      this.worklist = wl;
    }

    @Override
    public String toString() {
      return "Left in worklist: " + worklist.size() + ", typing: " + typing;
    }
  }

  protected Collection<Typing> applyAssignmentConstraints(Typing tg, IEvalFunction ef, IHierarchy h) {
    final int numAssignments = this.assignments.size();
    if (numAssignments == 0) {
      return Collections.emptyList();
    }

    ArrayDeque<WorklistElement> sigma = createSigmaQueue();
    List<Typing> r = createResultList();

    final ITypingStrategy typingStrategy = getTypingStrategy();

    BitSet wl = new BitSet(numAssignments);
    wl.set(0, numAssignments);
    sigma.add(new WorklistElement(tg, wl));

    Set<Type> throwable = null;

    while (!sigma.isEmpty()) {
      WorklistElement element = sigma.element();
      tg = element.typing;
      wl = element.worklist;
      int defIdx = wl.nextSetBit(0);
      if (defIdx == -1) {
        // worklist is empty
        r.add(tg);
        sigma.remove();
      } else {
        // Get the next definition statement
        wl.clear(defIdx);
        final DefinitionStmt stmt = this.assignments.get(defIdx);

        Value lhs = stmt.getLeftOp();
        Local v = (lhs instanceof Local) ? (Local) lhs : (Local) ((ArrayRef) lhs).getBase();
        Type told = tg.get(v);

        boolean isFirstType = true;
        for (Type t_ : ef.eval(tg, stmt.getRightOp(), stmt)) {
          if (lhs instanceof ArrayRef) {
            /*
             * We only need to consider array references on the LHS of assignments where there is supertyping between array
             * types, which is only for arrays of reference types and multidimensional arrays.
             */
            if (!(t_ instanceof RefType || t_ instanceof ArrayType || t_ instanceof WeakObjectType)) {
              continue;
            }

            t_ = t_.makeArrayType();
          }

          // Special handling for exception objects with phantom types
          final Collection<Type> lcas;
          if (!typesEqual(told, t_) && told instanceof RefType && t_ instanceof RefType
              && (((RefType) told).getSootClass().isPhantom() || ((RefType) t_).getSootClass().isPhantom())
              && (stmt.getRightOp() instanceof CaughtExceptionRef)) {
            if (throwable == null) {
              throwable = Collections.<Type>singleton(RefType.v("java.lang.Throwable"));
            }
            lcas = throwable;
          } else {
            lcas = h.lcas(told, t_, true);
          }

          for (Type t : lcas) {
            if (!typesEqual(t, told)) {
              BitSet dependsV = this.depends.get(v);
              Typing tg_;
              BitSet wl_;
              if (/* (eval.size() == 1 && lcas.size() == 1) || */isFirstType) {
                // The types agree, we have a type we can directly use
                tg_ = tg;
                wl_ = wl;
              } else {
                // The types do not agree, add all supertype candidates
                tg_ = typingStrategy.createTyping(tg);
                wl_ = (BitSet) wl.clone();
                WorklistElement e = new WorklistElement(tg_, wl_);
                sigma.add(e);
              }
              tg_.set(v, t);
              if (dependsV != null) {
                wl_.or(dependsV);
              }

            }
            isFirstType = false;
          }
        } // end for
      }
    }
    typingStrategy.minimize(r, h);
    return r;
  }

  protected ArrayDeque<WorklistElement> createSigmaQueue() {
    return new ArrayDeque<>();
  }

  protected List<Typing> createResultList() {
    return new ArrayList<Typing>();
  }

  // The ArrayType.equals method seems odd in Soot 2.2.5
  public static boolean typesEqual(Type a, Type b) {
    if (a instanceof ArrayType && b instanceof ArrayType) {
      ArrayType a_ = (ArrayType) a, b_ = (ArrayType) b;
      return a_.numDimensions == b_.numDimensions && a_.baseType.equals(b_.baseType);
    } else {
      return a.equals(b);
    }
  }

  /*
   * Taken from the soot.jimple.toolkits.typing.TypeResolver class of Soot version 2.2.5.
   */
  private void split_new() {
    final Jimple jimp = Jimple.v();
    final JimpleBody body = this.jb;
    final LocalDefs defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(body);

    PatchingChain<Unit> units = body.getUnits();
    for (Iterator<Unit> it = units.snapshotIterator(); it.hasNext();) {
      Unit stmt = it.next();
      if (stmt instanceof InvokeStmt) {
        InvokeExpr invokeExpr = ((InvokeStmt) stmt).getInvokeExpr();
        if ((invokeExpr instanceof SpecialInvokeExpr) && ("<init>".equals(invokeExpr.getMethodRef().getName()))) {
          SpecialInvokeExpr special = (SpecialInvokeExpr) invokeExpr;
          for (List<Unit> deflist = defs.getDefsOfAt((Local) special.getBase(), stmt); deflist.size() == 1;) {
            Stmt stmt2 = (Stmt) deflist.get(0);

            if (stmt2 instanceof AssignStmt) {
              AssignStmt assign = (AssignStmt) stmt2;
              Value rightOp = assign.getRightOp();

              if (rightOp instanceof Local) {
                deflist = defs.getDefsOfAt((Local) rightOp, assign);
                continue;
              } else if (rightOp instanceof NewExpr) {
                Local newlocal = localGenerator.generateLocal(assign.getLeftOp().getType());

                special.setBase(newlocal);

                DefinitionStmt assignStmt = jimp.newAssignStmt(assign.getLeftOp(), newlocal);
                units.insertAfter(assignStmt, Util.findLastIdentityUnit(body, assign));

                assign.setLeftOp(newlocal);
                this.initAssignment(assignStmt);
              }
            }
            break;
          } // end for(List<Unit>)
        }
      }
    }
  }
}
