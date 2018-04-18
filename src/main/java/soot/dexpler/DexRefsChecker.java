/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 * 
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 * 
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

package soot.dexpler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.Type;
import soot.Unit;
import soot.jimple.FieldRef;
import soot.jimple.Stmt;

/**

 */
public class DexRefsChecker extends DexTransformer {
	// Note: we need an instance variable for inner class access, treat this as
	// a local variable (including initialization before use)

	public static DexRefsChecker v() {
		return new DexRefsChecker();
	}

	Local l = null;

	@Override
	protected void internalTransform(final Body body, String phaseName, @SuppressWarnings("rawtypes") Map options) {
		// final ExceptionalUnitGraph g = new ExceptionalUnitGraph(body);
		// final SmartLocalDefs localDefs = new SmartLocalDefs(g, new
		// SimpleLiveLocals(g));
		// final SimpleLocalUses localUses = new SimpleLocalUses(g, localDefs);

		for (Unit u : getRefCandidates(body)) {
			Stmt s = (Stmt) u;
			boolean hasField = false;
			FieldRef fr = null;
			SootField sf = null;
			if (s.containsFieldRef()) {
				fr = s.getFieldRef();
				sf = fr.getField();
				if (sf != null) {
					hasField = true;
				}
			} else {
				throw new RuntimeException("Unit '" + u + "' does not contain array ref nor field ref.");
			}

			if (!hasField) {
				System.out.println("Warning: add missing field '" + fr + "' to class!");
				SootClass sc = null;
				String frStr = fr.toString();
				if (frStr.contains(".<")) {
					sc = Scene.v().getSootClass(frStr.split(".<")[1].split(" ")[0].split(":")[0]);
				} else {
					sc = Scene.v().getSootClass(frStr.split(":")[0].replaceAll("^<", ""));
				}
				String fname = fr.toString().split(">")[0].split(" ")[2];
				int modifiers = soot.Modifier.PUBLIC;
				Type ftype = fr.getType();
				sc.addField(Scene.v().makeSootField(fname, ftype, modifiers));
			} else {
				// System.out.println("field "+ sf.getName() +" '"+ sf +"'
				// phantom: "+ isPhantom +" declared: "+ isDeclared);
			}

		} // for if statements
	}

	/**
	 * Collect all the if statements comparing two locals with an Eq or Ne
	 * expression
	 *
	 * @param body
	 *            the body to analyze
	 */
	private Set<Unit> getRefCandidates(Body body) {
		Set<Unit> candidates = new HashSet<Unit>();
		Iterator<Unit> i = body.getUnits().iterator();
		while (i.hasNext()) {
			Unit u = i.next();
			Stmt s = (Stmt) u;
			if (s.containsFieldRef()) {
				candidates.add(u);
			}
		}
		return candidates;
	}

}
