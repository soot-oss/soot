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
package soot.jimple.spark.ondemand.genericutil;

import java.util.Arrays;

public final class DisjointSets {

	private int[] array;

	/**
	 * Construct a disjoint sets object.
	 * 
	 * @param numElements
	 *            the initial number of elements--also the initial number of
	 *            disjoint sets, since every element is initially in its own
	 *            set.
	 */
	public DisjointSets(int numElements) {
		array = new int[numElements];
        Arrays.fill(array, -1);
	}

	/**
	 * union() unites two disjoint sets into a single set. A union-by-size
	 * heuristic is used to choose the new root. This method will corrupt the
	 * data structure if root1 and root2 are not roots of their respective sets,
	 * or if they're identical.
	 * 
	 * @param root1
	 *            the root of the first set.
	 * @param root2
	 *            the root of the other set.
	 */
	public void union(int root1, int root2) {
		assert array[root1] < 0;
		assert array[root2] < 0;
		assert root1 != root2;
		if (array[root2] < array[root1]) { // root2 has larger tree
			array[root2] += array[root1]; // update # of items in root2's tree
			array[root1] = root2; // make root2 new root
		} else { // root1 has equal or larger tree
			array[root1] += array[root2]; // update # of items in root1's tree
			array[root2] = root1; // make root1 new root
		}
	}

	/**
	 * find() finds the (int) name of the set containing a given element.
	 * Performs path compression along the way.
	 * 
	 * @param x
	 *            the element sought.
	 * @return the set containing x.
	 */
	public int find(int x) {
		if (array[x] < 0) {
			return x; // x is the root of the tree; return it
		} else {
			// Find out who the root is; compress path by making the root x's
			// parent.
			array[x] = find(array[x]);
			return array[x]; // Return the root
		}
	}
}