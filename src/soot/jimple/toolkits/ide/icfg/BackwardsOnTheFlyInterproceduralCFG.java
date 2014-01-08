/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2013 Eric Bodden and others
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
package soot.jimple.toolkits.ide.icfg;

import static soot.jimple.toolkits.ide.icfg.BackwardsInterproceduralCFG.BACKWARDS_UNIT_GRAPH_CREATOR;

import java.util.Collection;

import soot.SootMethod;

/**
 * Same as {@link JimpleBasedInterproceduralCFG} but based on inverted unit graphs.
 * This should be used for backward analyses.
 */
public class BackwardsOnTheFlyInterproceduralCFG extends OnTheFlyJimpleBasedICFG {

	public BackwardsOnTheFlyInterproceduralCFG(SootMethod... entryPoints) {
		super(BACKWARDS_UNIT_GRAPH_CREATOR, entryPoints);
	}
	
	public BackwardsOnTheFlyInterproceduralCFG(Collection<SootMethod> entryPoints) {
		super(BACKWARDS_UNIT_GRAPH_CREATOR, entryPoints);
	}
}
