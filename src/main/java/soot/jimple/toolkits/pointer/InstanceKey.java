package soot.jimple.toolkits.pointer;

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

import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefLikeType;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.spark.sets.EqualsSupportingPointsToSet;
import soot.jimple.spark.sets.PointsToSetEqualsWrapper;

/**
 * An instance key is a static representative of a runtime object. An instance key, if based on a
 * {@link StrongLocalMustAliasAnalysis}, is guaranteed to represent a single runtime object within a its declared method. If
 * based on a (non-strong) {@link LocalMustAliasAnalysis}, it represents the value of a variable at a single location, which
 * itself can represent multiple runtime objects, if the location is contained in a loop.
 *
 * See Sable TR 2007-8 for details.
 *
 * @author Eric Bodden
 */
public class InstanceKey {

  protected final Local assignedLocal;
  protected final LocalMustAliasAnalysis lmaa;
  protected final LocalMustNotAliasAnalysis lnma;
  protected final Stmt stmtAfterAssignStmt;
  protected final SootMethod owner;
  protected final int hashCode;
  protected final PointsToSet pts;

  /**
   * Creates a new instance key representing the value stored in local, just before stmt. The identity of the key is defined
   * via lmaa, and its must-not-alias relationship to other keys via lmna.
   *
   * @param local
   *          the local variable whose value this key represents
   * @param stmt
   *          the statement at which this key represents the value
   * @param owner
   *          the method containing local
   * @param lmaa
   *          a {@link LocalMustAliasAnalysis}
   * @param lmna
   *          a {@link LocalMustNotAliasAnalysis}
   */
  public InstanceKey(Local local, Stmt stmt, SootMethod owner, LocalMustAliasAnalysis lmaa, LocalMustNotAliasAnalysis lmna) {
    this.assignedLocal = local;
    this.owner = owner;
    this.stmtAfterAssignStmt = stmt;
    this.lmaa = lmaa;
    this.lnma = lmna;
    PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
    this.pts = new PointsToSetEqualsWrapper((EqualsSupportingPointsToSet) pta.reachingObjects(local));
    this.hashCode = computeHashCode();
  }

  /**
   * {@inheritDoc}
   */
  public boolean mustAlias(InstanceKey otherKey) {
    if (stmtAfterAssignStmt == null || otherKey.stmtAfterAssignStmt == null) {
      // don't know
      return false;
    }
    return lmaa.mustAlias(assignedLocal, stmtAfterAssignStmt, otherKey.assignedLocal, otherKey.stmtAfterAssignStmt);
  }

  /**
   * {@inheritDoc}
   */
  public boolean mayNotAlias(InstanceKey otherKey) {
    if (owner.equals(otherKey.owner) && stmtAfterAssignStmt != null && otherKey.stmtAfterAssignStmt != null) {
      if (lnma.notMayAlias(assignedLocal, stmtAfterAssignStmt, otherKey.assignedLocal, otherKey.stmtAfterAssignStmt)) {
        return true;
      }
    }
    // different methods or local not-may-alias was not successful: get points-to info
    PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
    if (pta == null) {
      return false; // no info; hence don't know for sure
    }
    // may not alias if we have an empty intersection
    return !pts.hasNonEmptyIntersection(otherKey.pts);
  }

  public PointsToSet getPointsToSet() {
    return pts;
  }

  public Local getLocal() {
    return assignedLocal;
  }

  public boolean haveLocalInformation() {
    return stmtAfterAssignStmt != null;
  }

  public String toString() {
    String instanceKeyString
        = stmtAfterAssignStmt != null ? lmaa.instanceKeyString(assignedLocal, stmtAfterAssignStmt) : "pts(" + hashCode + ")";
    return instanceKeyString + "(" + assignedLocal.getName() + ")";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return hashCode;
  }

  /**
   * (Pre)computes the hash code.
   */
  protected int computeHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((owner == null) ? 0 : owner.hashCode());
    if (stmtAfterAssignStmt != null && (assignedLocal.getType() instanceof RefLikeType)) {
      // compute hash code based on instance key string
      result = prime * result + lmaa.instanceKeyString(assignedLocal, stmtAfterAssignStmt).hashCode();
    } else if (stmtAfterAssignStmt == null) {
      result = prime * result + pts.hashCode();
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final InstanceKey other = (InstanceKey) obj;
    if (owner == null) {
      if (other.owner != null) {
        return false;
      }
    } else if (!owner.equals(other.owner)) {
      return false;
    }
    // two keys are equal if they must alias
    if (mustAlias(other)) {
      return true;
    }
    // or if both have no statement set but the same local
    return (stmtAfterAssignStmt == null && other.stmtAfterAssignStmt == null && pts.equals(other.pts));
  }

  public boolean isOfReferenceType() {
    assert assignedLocal.getType() instanceof RefLikeType;
    return true;
  }

  public SootMethod getOwner() {
    return owner;
  }

  public Stmt getStmt() {
    return stmtAfterAssignStmt;
  }
}
