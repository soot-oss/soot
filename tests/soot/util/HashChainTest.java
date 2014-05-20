package soot.util;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HashChainTest {
	Chain<Integer> chain;
	Integer[] expected;

	@Before
	public void init() {
		chain = new HashChain<Integer>();
		expected = new Integer[0];
	}

	@After
	public void postConditionToArray() {
		assertArrayEquals(expected, chain.toArray());
	}
	
	@After
	public void postConditionIteratorAll() {
		List<Integer> actual = new ArrayList<Integer>();
		actual.addAll(chain);
		assertArrayEquals(expected, actual.toArray());
	}
	
	@After
	public void postConditionSnapshotIterator() {
		if (expected.length > 0) {
			List<Integer> actual = new ArrayList<Integer>();
			Iterator<Integer> it = chain.snapshotIterator();
			while (it.hasNext())
				actual.add(it.next());
			assertArrayEquals(expected, actual.toArray());
		}
	}
	
	@After
	public void postConditionIteratorFrom() {
		if (expected.length > 0) {
			List<Integer> actual = new ArrayList<Integer>();
			Iterator<Integer> it = chain.iterator(expected[0]);
			while (it.hasNext())
				actual.add(it.next());
			assertArrayEquals(expected, actual.toArray());
		}
	}

	@After
	public void postConditionIteratorFromTo() {
		if (expected.length > 0) {
			List<Integer> actual = new ArrayList<Integer>();
			Iterator<Integer> it = chain.iterator(expected[0],
					expected[expected.length - 1]);
			while (it.hasNext())
				actual.add(it.next());
			assertArrayEquals(expected, actual.toArray());
		}
	}

	@After
	public void postConditionFirstLast() {
		if (expected.length > 0) {
			assertEquals(chain.getFirst(), expected[0]);
			assertEquals(chain.getLast(), expected[expected.length - 1]);
		}
	}

	@After
	public void postConditionSize() {
		assertEquals(chain.size(), expected.length);
	}

	@After
	public void postConditionContainsAll() {
		assertTrue(chain.containsAll(Arrays.asList(expected)));
	}

	@After
	public void postConditionSuccs() {
		for (int i = 1; i < expected.length; i++) {
			assertEquals(chain.getSuccOf(expected[i - 1]), expected[i]);
		}
		if (expected.length > 0)
			assertNull(chain.getSuccOf(expected[expected.length - 1]));
	}

	@After
	public void postConditionPreds() {
		for (int i = 1; i < expected.length; i++) {
			assertEquals(chain.getPredOf(expected[i]), expected[i - 1]);
		}
		if (expected.length > 0)
			assertNull(chain.getPredOf(expected[0]));
	}

	@After
	public void postConditionFollows() {
		for (int i = 0; i < expected.length; i++) {
			Integer ii = expected[i];
			for (int j = 0; j < i; j++) {
				Integer jj = expected[j];
				assertFalse(jj + " should not follow " + ii, chain.follows(jj, ii));
				assertTrue(ii + " should follow " + jj, chain.follows(ii, jj));
			}

			assertFalse(ii + " should not follow " + ii, chain.follows(ii, ii));			

			for (int j = i + 1; j < expected.length; j++) {
				Integer jj = expected[j];
				assertFalse(ii + " should not follow " + jj, chain.follows(ii, jj));
				assertTrue(jj + " should follow " + ii, chain.follows(jj, ii));
			}
		}
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetFirstEmpty() {
		chain.getFirst();
	}

	@Test(expected = NoSuchElementException.class)
	public void testGetLastEmpty() {
		chain.getLast();
	}

	@Test
	public void testInsertAfter1() {
		Collections.addAll(chain, new Integer[] { 1, 2, 4, 5 });
		expected = new Integer[] { 1, 2, 3, 4, 5 };
		chain.insertAfter(3, 2);
	}

	@Test(expected = NullPointerException.class)
	public void testInsertAfter1ErrorNull() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 4, 5 });
		chain.insertAfter(3, null);
	}

	@Test(expected = NoSuchElementException.class)
	public void testInsertAfter1ErrorNoSuchElement() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 4, 5 });
		chain.insertAfter(3, 999);
	}

	@Test
	public void testInsertBefore1() {
		Collections.addAll(chain, new Integer[] { 1, 2, 4, 5 });
		expected = new Integer[] { 1, 2, 3, 4, 5 };
		chain.insertBefore(3, 4);
	}

	@Test(expected = NullPointerException.class)
	public void testInsertBefore1ErrorNull() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 4, 5 });
		chain.insertBefore(3, null);
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testInsertBefore1ErrorNoSuchElement() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 4, 5 });
		chain.insertBefore(3, 999);
	}

	@Test(expected = RuntimeException.class)
	public void testAddErrorAgain() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 5 });
		chain.add(2);
	}

	@Test(expected = NullPointerException.class)
	public void testAddErrorNull() {
		chain.add(null);
	}

	@Test(expected = NullPointerException.class)
	public void testAddFirstErrorNull() {
		chain.addFirst(null);
	}

	@Test(expected = NullPointerException.class)
	public void testAddLastErrorNull() {
		chain.addLast(null);
	}

	@Test(expected = NullPointerException.class)
	public void testFollowsErrorNull1() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 5 });

		chain.follows(null, 1);
		chain.follows(6, 1);
	}

	@Test(expected = NullPointerException.class)
	public void testFollowsErrorNull2() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 5 });

		chain.follows(1, null);
		chain.follows(1, 6);
	}

	@Test(expected = NoSuchElementException.class)
	public void testFollowsErrorNoSuchElement() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 5 });

		chain.follows(1, 6);
	}

	@Test
	public void testSerializable() throws IOException, ClassNotFoundException {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 11, 5, 7, 9 });

		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bo);
		out.writeObject(chain);

		ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
		ObjectInputStream in = new ObjectInputStream(bi);

		@SuppressWarnings("unchecked")
		HashChain<Integer> deserialized = (HashChain<Integer>) in.readObject();
				
		in.close();
		out.close();
		bi.close();
		bo.close();

		assertArrayEquals(chain.toArray(), deserialized.toArray());
	}

	@Test
	public void testAddFirst() {
		expected = new Integer[] { 1, 2, 3, 4, 5 };

		for (int i : new int[] { 5, 4, 3, 2, 1 }) {
			chain.addFirst(i);
		}
	}
	
	@Test
	public void testAddAll() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 5 });
	}

	@Test
	public void testAddLast() {
		expected = new Integer[] { 1, 2, 3, 4, 5 };

		for (int i : expected) {
			chain.addLast(i);
		}
	}	
	
	@Test
	public void testInsertAfterList() {
		Collections.addAll(chain, 1, 2, 5);
		expected = new Integer[] { 1, 2, 3, 4, 5 };

		chain.insertAfter(Arrays.asList(3, 4), 2);		
	}
	
	@Test(expected=RuntimeException.class)
	public void testInsertAfterListErrorDuplicated() {
		Collections.addAll(chain, 1, 2, 5);
		expected = new Integer[] { 1, 2, 3, 5 };

		chain.insertAfter(Arrays.asList(3, 3, 4), 2);		
	}
	
	@Test(expected=RuntimeException.class)
	public void testInsertBeforeListErrorDuplicated() {
		Collections.addAll(chain, 1, 2, 5);
		expected = new Integer[] { 1, 2, 3, 5 };

		chain.insertBefore(Arrays.asList(3, 3, 4), 5);		
	}
	
	@Test
	public void testInsertBeforeList() {
		Collections.addAll(chain, 1, 2, 5);
		expected = new Integer[] { 1, 2, 3, 4, 5 };

		chain.insertBefore(Arrays.asList(3, 4), 5);		
	}
	

	@Test(expected = NoSuchElementException.class)
	public void testIteratorErrorNoSuchElement() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 5 });

		Iterator<Integer> it = chain.iterator();
		while (it.hasNext()) {
			it.next();
		}
		it.next();
	}

	@Test
	public void testIteratorRemove() {
		Collections.addAll(chain, 1, 2, 3, 4, 5);
		expected = new Integer[] { 1, 2, 4, 5 };

		Iterator<Integer> it = chain.iterator();
		while (it.hasNext()) {
			Integer i = it.next();
			if (i == 3) {
				assertTrue(chain.contains(i));
				it.remove();
				assertFalse(chain.contains(i));				
			}
		}
	}

	@Test(expected = IllegalStateException.class)
	public void testIteratorRemoveErrorIllegalState() {
		Collections.addAll(chain, 1, 2, 3, 4, 5);
		expected = new Integer[] { 1, 2, 4, 5 };

		Iterator<Integer> it = chain.iterator();
		while (it.hasNext()) {
			Integer i = it.next();
			if (i == 3) {
				it.remove();
				it.remove();
			}
		}
	}

	@Test(expected = ConcurrentModificationException.class)
	public void testIteratorRemoveErrorConcurrentModification1() {
		Collections.addAll(chain, 1, 2, 64, 4, 5);
		expected = new Integer[] { 1, 2, 3, 4, 5 };
		
		Iterator<Integer> it = chain.iterator();
		
		chain.swapWith(64, 3);		
		it.next();
	}
	
	@Test(expected = ConcurrentModificationException.class)
	public void testIteratorRemoveErrorConcurrentModification2() {
		Collections.addAll(chain, 1, 2, 64, 4, 5);
		expected = new Integer[] { 1, 2, 4, 5 };
		
		Iterator<Integer> it = chain.iterator();
		
		chain.remove(64);		
		it.next();
	}
	
	@Test(expected = ConcurrentModificationException.class)
	public void testIteratorRemoveErrorConcurrentModification3() {
		Collections.addAll(chain, 1, 2, 64, 4, 5);
		expected = new Integer[] { 1, 2, 64, 4, 5, 99 };


		Iterator<Integer> it = chain.iterator();
		
		chain.addLast(99);
		
		it.next();
	}

	@Test(expected = NoSuchElementException.class)
	public void testIteratorRemoveErrorNoSuchElement() {
		Iterator<Integer> it = chain.iterator();
		it.next();
	}	
	
	@Test
	public void testRemove() {
		Collections.addAll(chain, 1, 2, 3, 4, 5);
		expected = new Integer[] { 1, 2, 4, 5 };

		assertTrue(chain.remove(3));
		assertFalse(chain.remove(3));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testRemoveFirstErrorEmpty() {
		chain.removeFirst();
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testRemoveLastErrorEmpty() {
		chain.removeLast();
	}
	
	@Test
	public void testRemoveFirst() {
		Collections.addAll(chain, 1, 2, 3, 4, 5);
		expected = new Integer[] { 2, 3, 4, 5 };
		chain.removeFirst();
	}
	
	@Test
	public void testRemoveLast() {
		Collections.addAll(chain, 1, 2, 3, 4, 5);
		expected = new Integer[] { 1, 2, 3, 4 };
		chain.removeLast();
	}
	
	@Test(expected=NullPointerException.class)
	public void testRemoveErrorNull() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 5 });
		chain.remove(null);
	}
	
	@Test
	public void testRemoveErrorOther() {
		Collections.addAll(chain, expected = new Integer[] { 1, 2, 3, 4, 5 });
		assertFalse(chain.remove(999));
	}

	@Test
	public void testClear() {
		Collections.addAll(chain, 1, 2, 4, 5);
		chain.clear();
	}
	
	@Test
	public void testSwap() {
		Collections.addAll(chain, 1, 2, 64, 4, 5);
		expected = new Integer[] { 1, 2, 3, 4, 5 };
		chain.swapWith(64, 3);
	}

}
