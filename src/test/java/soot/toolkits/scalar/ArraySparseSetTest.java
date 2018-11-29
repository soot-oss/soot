package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;


public class ArraySparseSetTest {
	
	@Test
	public void testBug11() {
		ArraySparseSet<String> ars1 = new ArraySparseSet<String>();
		ars1.add("a");
		ars1.add("b");

		ArraySparseSet<String> ars2 = new ArraySparseSet<String>();
		ars2.add("b");
		ars2.add("a");
		
		Assert.assertEquals(ars1, ars2);
		Assert.assertEquals(ars1.hashCode(), ars2.hashCode());
	}
	
	@Test
	public void testBug12() {
		ArraySparseSet<String> ars1 = new ArraySparseSet<String>();
		ars1.add("a");
		ars1.add("b");

		ArrayPackedSet<String> aps = new ArrayPackedSet<String>(
				new CollectionFlowUniverse<String>(Arrays.asList("a","b")));
		aps.add("b");
		aps.add("a");
		
		Assert.assertEquals(ars1, aps);
		Assert.assertEquals(ars1.hashCode(), aps.hashCode());
	}

	@Test
	public void testIterator() {
		ArraySparseSet<String> ars1 = new ArraySparseSet<String>();
		ars1.add("a");
		ars1.add("b");
		ars1.add("c");

		// remove element c
		Iterator<String> it = ars1.iterator();
		while (it.hasNext()) {
			String element = it.next();
			if (element.equals("c"))
				it.remove();
		}

		// check size
		Assert.assertEquals(2, ars1.size());

		// check remaining elements
		boolean aFound = false;
		boolean bFound = false;
		for (String element : ars1) {
			if (element.equals("a")) {
				Assert.assertFalse(aFound);
				aFound = true;
			}
			if (element.equals("b")) {
				Assert.assertFalse(bFound);
				bFound = true;
			}
		}
		Assert.assertTrue(aFound);
		Assert.assertTrue(bFound);
	}
	
}
