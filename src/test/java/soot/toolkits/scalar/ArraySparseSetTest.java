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
	
}
