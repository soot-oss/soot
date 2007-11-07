/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.toolkits.pointer;

import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefLikeType;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.toolkits.pointer.LocalMustAliasAnalysis;
import soot.jimple.toolkits.pointer.LocalNotMayAliasAnalysis;

/**
 * An instance key is a static representative of a runtime object.
 * See Sable TR 2007-8 for details.
 *
 * @author Eric Bodden
 */
public class InstanceKey {

    protected final Local assignedLocal;
    protected final LocalMustAliasAnalysis lmaa;
    protected final LocalNotMayAliasAnalysis lnma;
    protected final Stmt stmtAfterAssignStmt;
    protected final SootMethod owner;
    protected final int hashCode;

    public InstanceKey(Local assignedLocal, Stmt stmtAfterAssignStmt, SootMethod owner, LocalMustAliasAnalysis lmaa, LocalNotMayAliasAnalysis lnma) {
        this.assignedLocal = assignedLocal;
        this.owner = owner;
        this.stmtAfterAssignStmt = stmtAfterAssignStmt;
        this.lmaa = lmaa;
        this.lnma = lnma;
        this.hashCode = computeHashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean mustAlias(InstanceKey otherKey) {
        if(stmtAfterAssignStmt==null || otherKey.stmtAfterAssignStmt==null) {
            //don't know
            return false;
        }
        return lmaa.mustAlias(assignedLocal,stmtAfterAssignStmt,otherKey.assignedLocal,otherKey.stmtAfterAssignStmt);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean mayNotAlias(InstanceKey otherKey) {
        if (owner.equals(otherKey.owner) && stmtAfterAssignStmt!=null && otherKey.stmtAfterAssignStmt!=null) {
            if(lnma.notMayAlias(assignedLocal, stmtAfterAssignStmt, otherKey.assignedLocal, otherKey.stmtAfterAssignStmt)) {
            	return true;
            }
        } 
        //different methods or local not-may-alias was not successful: get points-to info
        PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
        if(pta==null) return false; //no info; hence don't know for sure
        PointsToSet reachingObjects = pta.reachingObjects(assignedLocal);
        PointsToSet otherReachingObjects = pta.reachingObjects(otherKey.assignedLocal);
        //may not alias if we have an empty intersection 
        return !reachingObjects.hasNonEmptyIntersection(otherReachingObjects);
    }
    
    public PointsToSet getPointsToSet() {
        PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
        PointsToSet reachingObjects = pta.reachingObjects(assignedLocal);
        return reachingObjects;
    }
    
    public Local getLocal() {
        return assignedLocal;
    }
    
    public boolean haveLocalInformation() {
        return stmtAfterAssignStmt!=null;
    }

    public String toString() {
        String instanceKeyString = stmtAfterAssignStmt!=null ? lmaa.instanceKeyString(assignedLocal, stmtAfterAssignStmt) : "pts("+assignedLocal+")";
        return instanceKeyString+"("+assignedLocal.getName()+")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    /**
     * The hash code is pretty constant. The result of the call to {@link LocalMustAliasAnalysis#instanceKeyString(Local, Stmt)}
     * might change, but only if the instance key is invalidated and in this case it's UNKNOWN and does not equal any other key
     * any more anyway.
     */
    protected int computeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        if(stmtAfterAssignStmt!=null && (assignedLocal.getType() instanceof RefLikeType)) {
            //compute hash code based on instance key string
            result = prime * result + lmaa.instanceKeyString(assignedLocal, stmtAfterAssignStmt).hashCode();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final InstanceKey other = (InstanceKey) obj;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.equals(other.owner))
            return false;
        //two keys are equal if they must alias
        if(mustAlias(other)) {
            return true;
        }
        //or if both have no statement set but the same local
        return (stmtAfterAssignStmt==null && other.stmtAfterAssignStmt==null && assignedLocal==other.assignedLocal);
    }
}
