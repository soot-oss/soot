package soot.toolkits.exceptions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import soot.ArrayType;
import soot.Body;
import soot.G;
import soot.Local;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.Singletons;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.SimpleLocalDefs;

/**
 * 
 * A {@link ThrowAnalysis} which returns the set of runtime exceptions and errors that might be thrown by the instructions
 * represented by a unit. Note that in contrast to a UnitThrowAnalysis, this analysis can be more precise in cases where it
 * can statically determine that certain exceptions can never be thrown in practice. Note that this analysis might be
 * relatively slow due to the overhead of checking plausible exceptions.
 * 
 * @author Marc Miltenberger
 */
public class PreciseThrowAnalysis extends UnitThrowAnalysis {

  /**
   * Constructs a <code>PreciseThrowAnalysis</code> for inclusion in Soot's global variable manager, {@link G}.
   *
   * @param g
   *          guarantees that the constructor may only be called from {@link Singletons}.
   */
  public PreciseThrowAnalysis(Singletons.Global g) {
    this(false);
  }

  /**
   * A protected constructor for use by unit tests.
   */
  protected PreciseThrowAnalysis() {
    this(false);
  }

  /**
   * Returns the single instance of <code>PreciseThrowAnalysis</code>.
   *
   * @return Soot's <code>PreciseThrowAnalysis</code>.
   */
  public static PreciseThrowAnalysis v() {
    return G.v().soot_toolkits_exceptions_PreciseThrowAnalysis();
  }

  protected PreciseThrowAnalysis(boolean isInterproc) {
    super(isInterproc);
  }

  public static PreciseThrowAnalysis interproceduralAnalysis = null;

  public static PreciseThrowAnalysis interproc() {
    if (interproceduralAnalysis == null) {
      interproceduralAnalysis = new PreciseThrowAnalysis(true);
    }
    return interproceduralAnalysis;
  }

  protected class IntraproceduralAnalyses {

    private NullnessAnalysis nullnessAnalysis;
    private Body body;
    private ExceptionalUnitGraph exceptionalGraph;
    private LocalDefs defs;
    private long modCount;

    public IntraproceduralAnalyses(Body body) {
      this.body = body;

    }

    private void checkModCount() {
      if (modCount != body.getModificationCount()) {
        nullnessAnalysis = null;
        exceptionalGraph = null;
        defs = null;
      }
    }

    public NullnessAnalysis getNullness() {
      checkModCount();
      NullnessAnalysis n = nullnessAnalysis;

      if (n == null) {
        n = new NullnessAnalysis(getExceptionalCFG());
        this.nullnessAnalysis = n;
      }
      return n;

    }

    public UnitGraph getExceptionalCFG() {
      checkModCount();
      ExceptionalUnitGraph e = exceptionalGraph;
      if (e == null) {
        e = new ExceptionalUnitGraph(body,
            // Although this is more imprecise, we don't want to have a stack overflow.
            // Shouldn't matter much in practice
            new UnitThrowAnalysis(false));
        exceptionalGraph = e;
      }
      modCount = body.getModificationCount();
      ;
      return e;
    }

    public LocalDefs getDefs() {
      checkModCount();
      LocalDefs d = defs;
      if (d == null) {
        d = new SimpleLocalDefs(getExceptionalCFG());
        defs = d;
      }
      return d;
    }

    public boolean canBeOutOfBounds(Unit statement, Value base, int val) {
      if (val < 0) {
        return true;
      }
      if (base instanceof Local) {
        Local baseArrayLocal = (Local) base;
        Iterator<Unit> it = getDefs().getDefsOfAtIterator(baseArrayLocal, statement);
        while (it.hasNext()) {
          Unit unit = it.next();
          if (unit instanceof AssignStmt) {
            AssignStmt assign = (AssignStmt) unit;
            Value rop = assign.getRightOp();
            if (rop instanceof NewArrayExpr) {
              NewArrayExpr ne = (NewArrayExpr) rop;
              Value size = ne.getSize();
              if (size instanceof IntConstant) {
                IntConstant sz = (IntConstant) size;
                if (val >= sz.value) {
                  return true;
                }
              } else {
                return true;
              }
            } else {
              return true;
            }
          } else {
            return true;
          }
        }
        return false;
      }
      return true;
    }

  }

  protected final Map<Body, IntraproceduralAnalyses> analyses = new ConcurrentHashMap<>();

  @Override
  protected boolean canBeNull(Unit u, Value v) {
    if (v instanceof Local) {
      Local l = (Local) v;
      if (u instanceof Stmt) {
        Stmt s = (Stmt) u;
        IntraproceduralAnalyses analysis = getIntraproceduralAnalysis(s.getContainingBody());
        if (s.containsInvokeExpr() && s.getInvokeExpr().getMethodRef().isConstructor()) {
          return false;
        }
        return !analysis.getNullness().isAlwaysNonNullBefore(s, l);
      }
    }
    return true;
  }

  protected IntraproceduralAnalyses getIntraproceduralAnalysis(Body body) {
    return analyses.computeIfAbsent(body, (x) -> new IntraproceduralAnalyses(x));
  }

  private static Type getBaseType(Type bt) {
    if (bt instanceof ArrayType) {
      ArrayType at = (ArrayType) bt;
      return at.getBaseType();
    } else if (bt instanceof RefType || bt instanceof PrimType) {
      return bt;
    } else {
      throw new IllegalArgumentException(String.format("%s is not supported", bt));
    }
  }

  @Override
  protected boolean canThrowResolveClassError(Unit u, Type type) {
    if (!type.isAllowedInFinalCode()) {
      return true;
    }
    type = getBaseType(type);
    if (type instanceof RefType) {
      RefType rt = (RefType) type;
      if (!(u instanceof Stmt)) {
        return true;
      }
      Stmt s = (Stmt) u;
      Body body = s.getContainingBody();
      if (Scene.v().getOrMakeFastHierarchy().canStoreType(body.getMethod().getDeclaringClass().getType(), rt)) {
        // We request the same type as the class is currently in or some super class or interface.
        // Since these have to be loaded before the code in our class can run, we can never throw a class resolve error here.
        return false;
      }

      return true;

    } else {
      if (!(type instanceof PrimType)) {
        // This must never happen
        throw new IllegalArgumentException(String.format("Not supported %s", type));
      }
      return false;
    }
  }

  @Override
  protected ValueSwitch valueSwitch(Unit stmt) {
    return new ValueSwitch(stmt) {
      @Override
      protected boolean canBeOutOfBounds(ArrayRef ref) {
        Value ic = ref.getIndex();
        if (ic instanceof IntConstant) {
          int val = ((IntConstant) ic).value;
          if (val >= 0 && statement instanceof Stmt) {
            Stmt s = (Stmt) statement;
            IntraproceduralAnalyses analysis = getIntraproceduralAnalysis(s.getContainingBody());
            return analysis.canBeOutOfBounds(statement, ref.getBase(), val);
          }
        }
        return true;
      }

    };
  }
}
