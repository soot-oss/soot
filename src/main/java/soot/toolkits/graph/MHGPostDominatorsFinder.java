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

package soot.toolkits.graph;


/**
 * Post-dominators finder for multi-headed graph.
 * The dominators returned by this finder are postdominators,
 * so e.g. {@link #getDominators(Object)} returns all post-dominators.
 *
 * @author Eric Bodden
 **/
public class MHGPostDominatorsFinder<N> extends MHGDominatorsFinder<N>
{

	public MHGPostDominatorsFinder(DirectedGraph<N> graph) {
		super(new InverseGraph<N>(graph));
	}
	
	
}
