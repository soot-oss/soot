package soot.util;

import static org.junit.Assert.*;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.junit.Test;

public class SmallPriorityQueueTest {

	Integer[] universe1 = new Integer[] {1,2,3,4,5,6,7,8,9,10,11,12,13,15};

	@Test
	public void testDeleteFirst() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		assertTrue(q.remove(universe1[0]));
		assertEquals(q.peek(), universe1[1]);
		assertEquals(q.poll(), universe1[1]);
	}

	@Test
	public void testNew() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		assertEquals(universe1.length, q.size());
		assertFalse(q.isEmpty());
	}
	
	@Test
	public void testInstance() {
		assertTrue(PriorityQueue.of(universe1) instanceof SmallPriorityQueue);
	}
	
	@Test
	public void testMixedAddDelete() {
		int second = 6;
		Queue<Integer> q = PriorityQueue.noneOf(universe1);
		assertTrue(q.add(universe1[0]));
		assertFalse(q.offer(universe1[0]));
		assertTrue(q.add(universe1[second]));
		assertTrue(q.remove(universe1[0]));
		assertEquals(q.peek(), universe1[second]);
		assertEquals(q.poll(), universe1[second]);
	}
	

	@Test
	public void testOfferAlreadyInQueue() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		for ( Integer i : universe1 ) {
			assertFalse(q.offer(i));
		}
	}	

	@Test
	public void testClear() {
		Queue<Integer> q = PriorityQueue.of(universe1);
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
		Queue<Integer> q = PriorityQueue.of(universe1);
		q.offer(999);
	}

	@Test(expected=NullPointerException.class)
	public void testOfferNull() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		q.offer(null);
	}

	@Test
	public void testRemoveNull() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		assertFalse(q.remove(null));
	}

	@Test
	public void testIteratorAll() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		int j = 0;
		for (Integer i : q) {
			assertEquals(universe1[j++], i);
		}
	}

	@Test(expected=ConcurrentModificationException.class)
	public void testIteratorDelete() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		int j = 0;
		for (Integer i : q) {
			assertEquals(universe1[j++], i);
			assertTrue(q.remove(universe1[universe1.length-1]));
		}
	}	
	
	@Test
	public void testOffer() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		
		int i = 0;
		assertEquals(universe1[i++], q.poll());
		assertEquals(universe1[i++], q.poll());
		assertEquals(universe1[i++], q.poll());
		assertEquals(universe1[i++], q.poll());
		q.add(universe1[i=0]);
		assertEquals(universe1[i++], q.poll());
	}	
	
	@Test
	public void testIteratorRemove() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		
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
		Queue<Integer> q = PriorityQueue.of(universe1);
		
		Iterator<Integer> it = q.iterator();
		
		while (it.hasNext()) {
			it.next();
		}
		it.next();
	}	
	
	@Test(expected=IllegalStateException.class)
	public void testIteratorDoubleRemove() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		
		Iterator<Integer> it = q.iterator();
		
		while (it.hasNext()) {
			it.next();
			it.remove();
			it.remove();
		}
	}		
	
	@Test(expected=IllegalStateException.class)
	public void testIteratorBeforeFirst() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		
		Iterator<Integer> it = q.iterator();
		it.remove();
	}	
	


	@Test()
	public void testIteratorHole1() {
		Queue<Integer> q = PriorityQueue.of(universe1);
		int hole = 7;
		assertTrue(q.remove(universe1[hole]));
		int j = 0;
		for (Integer i : q) {
			if (j==hole)j++;
			assertEquals(universe1[j++], i);
		}
	}
	

}
