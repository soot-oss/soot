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

import java.util.Iterator;
import java.util.List;

/**
 * An inverted graph of a directed graph.
 *
 * @author Eric Bodden
 */
public class InverseGraph<N> implements DirectedGraph<N> {
	
	protected final DirectedGraph<N> g;

	public InverseGraph(DirectedGraph<N> g) {
		this.g = g;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<N> getHeads() {
		return g.getTails();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<N> getPredsOf(N s) {
		return g.getSuccsOf(s);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<N> getSuccsOf(N s) {
		return g.getPredsOf(s);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<N> getTails() {
		return g.getHeads();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<N> iterator() {
		return g.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return g.size();
	}

}
