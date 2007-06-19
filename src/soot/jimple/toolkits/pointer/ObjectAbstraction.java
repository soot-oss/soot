/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Eric Bodden
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
import soot.Scene;
import soot.jimple.Stmt;

/**
 * This is an abstraction of a runtime object, modeled by a local definition.
 * It combines local flow-sensitive must-alias information with local flow-sensitive may-alias information
 * and global (possibly flow-insensitive) may-alias information based on points-to sets.  
 *
 * @author Eric Bodden
 */
public class ObjectAbstraction {

	protected final Local l;
	protected final Stmt s;
	protected final LocalMustAliasAnalysis lmaa;
	protected final LocalNotMayAliasAnalysis lnma;
	
	/**
	 * Creates a new abstraction of a runtimee objects, modeled by a local variable l and a definition of that variable.
	 * @param l a local variable
	 * @param stmtAfterDefStatement a statement after the definition statements of l that models this object
	 * @param lmaa a local must-alias analysis of the method owning the local
	 * @param lnma a local not-may-alias analysis of the method owning the local
	 */
	public ObjectAbstraction(Local l, Stmt stmtAfterDefStatement, LocalMustAliasAnalysis lmaa, LocalNotMayAliasAnalysis lnma) {
		this.l = l;
		this.s = stmtAfterDefStatement;
		this.lmaa = lmaa;
		this.lnma = lnma;
	}

	/**
	 * Creates a new abstraction of a runtimee objects, modeled by a local variable l.
	 * In this case, since the definition statement is unknown, one cannot use any flow-sensitive information and
	 * {@link #mustAlias(ObjectAbstraction)} will always return <code>false</code>, while {@link #mayAlias(ObjectAbstraction)} will
	 * always resort to global points-to sets.
	 * @param l a local variable
	 * @param lmaa a local must-alias analysis of the method owning the local
	 * @param lnma a local not-may-alias analysis of the method owning the local
	 */
	public ObjectAbstraction(Local l, LocalMustAliasAnalysis lmaa, LocalNotMayAliasAnalysis lnma) {
		this(l,null,lmaa,lnma);
	}

	/**
	 * Returns <code>true</code> iff this abstraction must alias the other.
	 */
	public boolean mustAlias(ObjectAbstraction other) {
		if(lmaa.hasInfoOn(l, s) && lmaa.hasInfoOn(other.l, other.s)) {
			return lmaa.mustAlias(l, s, other.l, other.s);
		} else {
			return false;
		}
	}

	/**
	 * Returns <code>true</code> iff this abstraction may alias the other.
	 */
	public boolean mayAlias(ObjectAbstraction other) {
		if(lnma.hasInfoOn(l,s) && lnma.hasInfoOn(other.l, other.s)) {
			return !lnma.notMayAlias(l, s, other.l, other.s);
		} else {
			//have no local info, hence resort to global points-to information
			PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
			PointsToSet pts1 = pta.reachingObjects(l);
			PointsToSet pts2 = pta.reachingObjects(other.l);
			return pts1.hasNonEmptyIntersection(pts2);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return l.toString();
	}

}
