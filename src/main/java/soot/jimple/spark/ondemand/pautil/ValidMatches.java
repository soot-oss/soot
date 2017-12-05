/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
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
package soot.jimple.spark.ondemand.pautil;

import java.util.Iterator;
import java.util.Set;

import soot.jimple.spark.ondemand.genericutil.ArraySet;
import soot.jimple.spark.ondemand.genericutil.HashSetMultiMap;
import soot.jimple.spark.ondemand.genericutil.MultiMap;
import soot.jimple.spark.ondemand.pautil.SootUtil.FieldToEdgesMap;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.SparkField;
import soot.jimple.spark.pag.VarNode;
import soot.toolkits.scalar.Pair;

public class ValidMatches {

	// edges are in same direction as PAG, in the direction of value flow
	private final MultiMap<VarNode, VarNode> vMatchEdges = new HashSetMultiMap<VarNode, VarNode>();
	
	private final MultiMap<VarNode, VarNode> vMatchBarEdges = new HashSetMultiMap<VarNode, VarNode>();
	
	public ValidMatches(PAG pag, FieldToEdgesMap fieldToStores) {
		for (Iterator iter = pag.loadSources().iterator(); iter.hasNext();) {
			FieldRefNode loadSource = (FieldRefNode) iter.next();
			SparkField field = loadSource.getField();
			VarNode loadBase = loadSource.getBase();
			ArraySet<Pair<VarNode, VarNode>> storesOnField = fieldToStores.get(field);
			for (Pair<VarNode, VarNode> store : storesOnField) {
				VarNode storeBase = store.getO2();				
				if (loadBase.getP2Set().hasNonEmptyIntersection(storeBase.getP2Set())) {
					VarNode matchSrc = store.getO1();
					Node[] loadTargets = pag.loadLookup(loadSource);
					for (int i = 0; i < loadTargets.length; i++) {
						VarNode matchTgt = (VarNode) loadTargets[i];
						vMatchEdges.put(matchSrc, matchTgt);
						vMatchBarEdges.put(matchTgt, matchSrc);
					}
				}				
			}
		}
	}
	
	public Set<VarNode> vMatchLookup(VarNode src) {
		return vMatchEdges.get(src);
	}

	public Set<VarNode> vMatchInvLookup(VarNode src) {
		return vMatchBarEdges.get(src);
	}
}
