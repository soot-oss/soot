package soot.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MediumPriorityQueueTest {

	Integer[] universe1;
	Integer[] clone;
	Queue<Integer> q;
	
	@Before
	public void initUniverse() {
		universe1 = new Integer[MediumPriorityQueue.MAX_CAPACITY-63];
		for (int j = 0; j < universe1.length; j++)
			universe1[j] = j;
		
		clone = Arrays.copyOf(universe1, universe1.length);
	}

	@After
	public void postconditionUniverseNotModified() {
		assertArrayEquals(clone, universe1);
	}
	
	@After
	public void postconditionInstanceOfMediumPriorityQueue() {
		assertTrue(q instanceof MediumPriorityQueue);
	}
	
	@Test
	public void testDeleteFirst() {
		q = PriorityQueue.of(universe1);
		assertTrue(q.remove(universe1[0]));
		assertEquals(q.peek(), universe1[1]);
		assertEquals(q.poll(), universe1[1]);
	}

	@Test
	public void testNew() {
		q = PriorityQueue.of(universe1);
		assertEquals(universe1.length, q.size());
		assertFalse(q.isEmpty());
	}
	
	@Test
	public void testPollAll() {
		q = PriorityQueue.of(universe1);
		
		int i = 0;
		while (!q.isEmpty())
			assertEquals(universe1[i++], q.poll());
	}	

	@Test
	public void testPoll2() {
		q = PriorityQueue.noneOf(universe1);
		for (int i=0;i<universe1.length;i+=3)
			q.add(universe1[i]);

		int i = -3;
		while (!q.isEmpty()) {
			Object e = universe1[i+=3];
			assertEquals(e, q.peek());
			assertEquals(e, q.poll());
		}
	}	

	@Test
	public void testPeekPollAll() {
		q = PriorityQueue.of(universe1);
		
		while (!q.isEmpty())
			assertEquals(q.peek(), q.poll());
	}	
	
	
	@Test
	public void testOffer() {
		q = PriorityQueue.of(universe1);
		
		int i = 0;
		assertEquals(universe1[i++], q.poll());
		assertEquals(universe1[i++], q.poll());
		assertEquals(universe1[i++], q.poll());
		assertEquals(universe1[i++], q.poll());
		q.add(universe1[i=0]);
		assertEquals(universe1[i++], q.poll());
	}	
	
	@Test
	public void testMixedAddDelete() {
		q = PriorityQueue.noneOf(universe1);

		Integer z = universe1[0];
		Integer x = universe1[666];
		
		assertTrue(q.add(z));
		assertFalse(q.offer(z));
		assertTrue(q.contains(z));
		assertTrue(q.add(x));
		
		for (Integer i : universe1)
			assertEquals((i == z || i == x), q.contains(i));
		
		assertTrue(q.remove(z));		
		
		for (Integer i : universe1)
			assertEquals(i == x, q.contains(i));
		
		assertEquals(x, q.peek());
		assertEquals(x, q.poll());
	}
	

	@Test
	public void testOfferAlreadyInQueue() {
		q = PriorityQueue.of(universe1);
		for ( Integer i : universe1 ) {
			assertFalse(q.offer(i));
		}
	}	
	

	@Test
	public void testClear() {
		q = PriorityQueue.of(universe1);
		q.clear();
		assertEquals(q.size(), 0);
		assertTrue(q.isEmpty());
		assertNull(q.peek());
		assertNull(q.poll());
		for (Integer i : universe1) 
			assertFalse(q.contains(i));
	}	

	@Test(expected=NoSuchElementException.class)
	public void testOfferNotInUniverse() {
		q = PriorityQueue.of(universe1);
		q.offer(-999);
	}

	@Test(expected=NullPointerException.class)
	public void testOfferNull() {
		q = PriorityQueue.of(universe1);
		q.offer(null);
	}

	@Test
	public void testRemoveNull() {
		q = PriorityQueue.of(universe1);
		assertFalse(q.remove(null));
	}

	@Test
	public void testIteratorAll() {
		q = PriorityQueue.of(universe1);
		int j = 0;
		for (Integer i : q) {
			assertEquals(universe1[j++], i);
		}
	}

	@Test(expected=ConcurrentModificationException.class)
	public void testIteratorDelete() {
		q = PriorityQueue.of(universe1);
		int j = 0;
		for (Integer i : q) {
			assertEquals(universe1[j++], i);
			assertTrue(q.remove(universe1[universe1.length-1]));
		}
	}	

	@Test
	public void testIteratorRemove() {
		q = PriorityQueue.of(universe1);
		
		Iterator<Integer> it = q.iterator();
		
		while (it.hasNext()) {
			Integer i = it.next();
			assertTrue(q.contains(i));
			it.remove();
			assertFalse(q.contains(i));
		}
	}	
	
	@Test(expected=NoSuchElementException.class)
	public void testIteratorOutOfBounds() {
		q = PriorityQueue.of(universe1);
		
		Iterator<Integer> it = q.iterator();
		
		while (it.hasNext()) {
			it.next();
		}
		it.next();
	}	
	
	@Test(expected=IllegalStateException.class)
	public void testIteratorDoubleRemove() {
		q = PriorityQueue.of(universe1);
		
		Iterator<Integer> it = q.iterator();
		
		while (it.hasNext()) {
			it.next();
			it.remove();
			it.remove();
		}
	}		
	
	@Test(expected=IllegalStateException.class)
	public void testIteratorBeforeFirst() {
		q = PriorityQueue.of(universe1);
		
		Iterator<Integer> it = q.iterator();
		it.remove();
	}	
	


	@Test()
	public void testIteratorHole1() {
		q = PriorityQueue.of(universe1);
		int hole = 7;
		assertTrue(q.remove(universe1[hole]));
		assertFalse(q.contains(universe1[hole]));
		int j = 0;
		for (Integer i : q) {
			if (j==hole)j++;
			assertEquals(universe1[j++], i);
		}
	}
	

}
