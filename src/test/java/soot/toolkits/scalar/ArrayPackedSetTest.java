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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class ArrayPackedSetTest {

	FlowUniverse<Integer> universe;
	BoundedFlowSet<Integer> a;

	@Before
	public void init() {
		Integer[] aa = {1,2,3,4,5,6,7,8,9};
		universe = new ArrayFlowUniverse<Integer>(aa);
		a = new ArrayPackedSet<Integer>(universe);
	}

	@Test
	public void testEmptySet() {
		FlowSet<Integer> e = a.emptySet();

		assertNotSame(a, e);
		assertTrue(e.isEmpty());
		for (int i : universe) {
			assertFalse(e.contains(i));
		}
	}

	@Test
	public void testAdd() {
		FlowSet<Integer> e = a.emptySet();

		for (int i : universe) {
			assertFalse(e.contains(i));
			e.add(i);
			assertTrue(e.contains(i));
		}
	}

	@Test
	public void testRemove() {
		FlowSet<Integer> e = a.topSet();

		for (int i : universe) {
			assertTrue(e.contains(i));
			e.remove(i);
			assertFalse(e.contains(i));
		}
	}

	@Test
	public void testEmptySetNewInstance() {
		assertNotSame(a.emptySet(), a.emptySet());
	}

	@Test
	public void testTopSetNewInstance() {
		assertNotSame(a.topSet(), a.topSet());
	}

	@Test
	public void testTopSet() {
		FlowSet<Integer> e = a.topSet();

		assertNotSame(a, e);
		assertFalse(e.isEmpty());
		assertEquals(universe.size(), e.size());
		for (int i : universe) {
			assertTrue(e.contains(i));
		}
	}

	@Test
	public void testIteratorFull() {
		FlowSet<Integer> e = a.topSet();

		Iterator<Integer> it = universe.iterator();
		for (int i : e) {
			assertEquals(it.next().intValue(), i);
		}
		assertFalse(it.hasNext());
	}

	@Test
	public void testToListFull() {
		FlowSet<Integer> e = a.topSet();
		assertArrayEquals(universe.toArray(), e.toList().toArray());
	}

	@Test
	public void testToListEmpty() {
		FlowSet<Integer> e = a.emptySet();
		assertArrayEquals(new Object[0], e.toList().toArray());
	}

	@Test
	public void testToList() {
		FlowSet<Integer> e = a.emptySet();
		Integer[] t = {3,7,33};

		for (int i : t)
			e.add(i);

		assertEquals(t.length, e.size());
		assertArrayEquals(t, e.toList().toArray());
	}

	@Test
	public void testIterator() {
		FlowSet<Integer> e = a.emptySet();
		Integer[] t = {3,6,7,8,12};

		for (int i : t)
			e.add(i);

		int j = 0;
		for (int i : e)
			assertEquals(t[j++].intValue(), i);
	}

	@Test
	public void testCopy() {
		FlowSet<Integer> e1 = a.emptySet();
		FlowSet<Integer> e2 = a.topSet();

		e2.copy(e1);
		assertEquals(e1, e2);
	}

	@Test
	public void testClear() {
		FlowSet<Integer> e = a.topSet();
		assertFalse(e.isEmpty());

		e.clear();

		assertTrue(e.isEmpty());
		for (int i : universe) {
			assertFalse(e.contains(i));
		}
	}

	@Test
	public void testComplement() {
		BoundedFlowSet<Integer> e1 = (BoundedFlowSet<Integer>) a.emptySet();
		FlowSet<Integer> e2 = a.topSet();

		assertTrue(e1.isEmpty());
		assertEquals(0, e1.size());
		assertEquals(universe.size(), e2.size());

		e1.complement();
		assertEquals(e1, e2);
		assertNotSame(e1, e2);
	}

	@Test
	public void testComplement2() {
		BoundedFlowSet<Integer> e = (BoundedFlowSet<Integer>) a.emptySet();

		for (int i : universe) {
			if ( (i % 3) == 0 ) {
				e.add(i);
			}
		}

		for (int i : universe) {
			if ( (i % 3) == 0 ) {
				assertTrue(e.contains(i));
			} else {
				assertFalse(e.contains(i));
			}
		}

		e.complement();

		for (int i : universe) {
			if ( (i % 3) == 0 ) {
				assertFalse(e.contains(i));
			} else {
				assertTrue(e.contains(i));
			}
		}
	}

}
