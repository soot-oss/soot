package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.Context;
import soot.Kind;
import soot.MethodOrMethodContext;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.util.Invalidable;

/**
 * Represents a single edge in a call graph.
 *
 * @author Ondrej Lhotak
 */
public class Edge implements Invalidable {

  /**
   * The method in which the call occurs; may be null for calls not occurring in a specific method (eg. implicit calls by the
   * VM)
   */
  private MethodOrMethodContext src;

  /**
   * The target method of the call edge.
   */
  private MethodOrMethodContext tgt;

  /**
   * The unit at which the call occurs; may be null for calls not occurring at a specific statement (eg. calls in native
   * code)
   */
  private Unit srcUnit;

  /**
   * The kind of edge. Note: kind should not be tested by other classes; instead, accessors such as isExplicit() should be
   * added.
   */
  private final Kind kind;

  private boolean invalid = false;

  public Edge(MethodOrMethodContext src, Unit srcUnit, MethodOrMethodContext tgt, Kind kind) {
    this.src = src;
    this.srcUnit = srcUnit;
    this.tgt = tgt;
    this.kind = kind;
  }

  public Edge(MethodOrMethodContext src, Stmt srcUnit, MethodOrMethodContext tgt) {
    this.kind = ieToKind(srcUnit.getInvokeExpr());
    this.src = src;
    this.srcUnit = srcUnit;
    this.tgt = tgt;
  }

  public SootMethod src() {
    return (src == null) ? null : src.method();
  }

  public Context srcCtxt() {
    return (src == null) ? null : src.context();
  }

  public MethodOrMethodContext getSrc() {
    return src;
  }

  public Unit srcUnit() {
    return srcUnit;
  }

  public Stmt srcStmt() {
    return (Stmt) srcUnit;
  }

  public SootMethod tgt() {
    return (tgt == null) ? null : tgt.method();
  }

  public Context tgtCtxt() {
    return (tgt == null) ? null : tgt.context();
  }

  public MethodOrMethodContext getTgt() {
    return tgt;
  }

  public Kind kind() {
    return kind;
  }

  public static Kind ieToKind(InvokeExpr ie) {
    if (ie instanceof VirtualInvokeExpr) {
      return Kind.VIRTUAL;
    } else if (ie instanceof SpecialInvokeExpr) {
      return Kind.SPECIAL;
    } else if (ie instanceof InterfaceInvokeExpr) {
      return Kind.INTERFACE;
    } else if (ie instanceof StaticInvokeExpr) {
      return Kind.STATIC;
    } else {
      throw new RuntimeException();
    }
  }

  /**
   * Returns true if the call is due to an explicit invoke statement.
   */
  public boolean isExplicit() {
    return Kind.isExplicit(this.kind);
  }

  /**
   * Returns true if the call is due to an explicit instance invoke statement.
   */
  public boolean isInstance() {
    return Kind.isInstance(this.kind);
  }

  public boolean isVirtual() {
    return Kind.isVirtual(this.kind);
  }

  public boolean isSpecial() {
    return Kind.isSpecial(this.kind);
  }

  /**
   * Returns true if the call is to static initializer.
   */
  public boolean isClinit() {
    return Kind.isClinit(this.kind);
  }

  /**
   * Returns true if the call is due to an explicit static invoke statement.
   */
  public boolean isStatic() {
    return Kind.isStatic(this.kind);
  }

  public boolean isThreadRunCall() {
    return Kind.isThread(this.kind);
  }

  public boolean passesParameters() {
    return Kind.passesParameters(this.kind);
  }

  @Override
  public boolean isInvalid() {
    return invalid;
  }

  @Override
  public void invalidate() {
    // Since the edge remains in the QueueReaders for a while, the GC could not claim old units.
    src = null;
    srcUnit = null;
    tgt = null;
    invalid = true;
  }

  @Override
  public int hashCode() {
    if (invalid) {
      return 0;
    }
    int ret = (tgt.hashCode() + 20) + (kind == null ? 0 : kind.getNumber());
    if (src != null) {
      ret = ret * 32 + src.hashCode();
    }
    if (srcUnit != null) {
      ret = ret * 32 + srcUnit.hashCode();
    }
    return ret;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Edge)) {
      return false;
    }
    Edge o = (Edge) other;
    return (o.src == this.src) && (o.srcUnit == srcUnit) && (o.tgt == tgt) && (o.kind == kind);
  }

  @Override
  public String toString() {
    return String.valueOf(this.kind) + " edge: " + srcUnit + " in " + src + " ==> " + tgt;
  }

  private Edge nextByUnit = this;
  private Edge prevByUnit = this;
  private Edge nextBySrc = this;
  private Edge prevBySrc = this;
  private Edge nextByTgt = this;
  private Edge prevByTgt = this;

  void insertAfterByUnit(Edge other) {
    nextByUnit = other.nextByUnit;
    nextByUnit.prevByUnit = this;
    other.nextByUnit = this;
    prevByUnit = other;
  }

  void insertAfterBySrc(Edge other) {
    nextBySrc = other.nextBySrc;
    nextBySrc.prevBySrc = this;
    other.nextBySrc = this;
    prevBySrc = other;
  }

  void insertAfterByTgt(Edge other) {
    nextByTgt = other.nextByTgt;
    nextByTgt.prevByTgt = this;
    other.nextByTgt = this;
    prevByTgt = other;
  }

  void insertBeforeByUnit(Edge other) {
    prevByUnit = other.prevByUnit;
    prevByUnit.nextByUnit = this;
    other.prevByUnit = this;
    nextByUnit = other;
  }

  void insertBeforeBySrc(Edge other) {
    prevBySrc = other.prevBySrc;
    prevBySrc.nextBySrc = this;
    other.prevBySrc = this;
    nextBySrc = other;
  }

  void insertBeforeByTgt(Edge other) {
    prevByTgt = other.prevByTgt;
    prevByTgt.nextByTgt = this;
    other.prevByTgt = this;
    nextByTgt = other;
  }

  void remove() {
    invalid = true;
    nextByUnit.prevByUnit = prevByUnit;
    prevByUnit.nextByUnit = nextByUnit;
    nextBySrc.prevBySrc = prevBySrc;
    prevBySrc.nextBySrc = nextBySrc;
    nextByTgt.prevByTgt = prevByTgt;
    prevByTgt.nextByTgt = nextByTgt;
  }

  Edge nextByUnit() {
    return nextByUnit;
  }

  Edge nextBySrc() {
    return nextBySrc;
  }

  Edge nextByTgt() {
    return nextByTgt;
  }

  Edge prevByUnit() {
    return prevByUnit;
  }

  Edge prevBySrc() {
    return prevBySrc;
  }

  Edge prevByTgt() {
    return prevByTgt;
  }
}
