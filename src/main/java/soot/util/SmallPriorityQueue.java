/**
 * 
 */
package soot.util;

import static java.lang.Long.numberOfTrailingZeros;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Steven Lambeth
 *
 */
class SmallPriorityQueue<E> extends PriorityQueue<E> {
	final static int MAX_CAPACITY = Long.SIZE;

	private long queue = 0;

	void addAll() {
		if (N == 0)
			return;

		queue = -1L >>> -N;
		min = 0;
	}

	SmallPriorityQueue(List<? extends E> universe, Map<E, Integer> ordinalMap) {
		super(universe, ordinalMap);
		assert universe.size() <= Long.SIZE;
	}

	@Override
	public void clear() {
		queue = 0L;
		min = Integer.MAX_VALUE;
	}

	@Override
	public Iterator<E> iterator() {
		return new Itr() {
			@Override
			long getExpected() {
				return queue;
			}
		};
	}

	@Override
	public int size() {
		return Long.bitCount(queue);
	}

	@Override
	int nextSetBit(int fromIndex) {
		assert fromIndex >= 0;

		if (fromIndex > N)
			return fromIndex;

		long m0 = -1L << fromIndex;
		long t0 = queue & m0;
		if ((t0 & -m0) != 0)
			return fromIndex;

		return numberOfTrailingZeros(t0);
	}

	@Override
	boolean add(int ordinal) {
		long old = queue;
		queue |= (1L << ordinal);
		if (old == queue)
			return false;
		min = Math.min(min, ordinal);
		return true;
	}

	@Override
	boolean contains(int ordinal) {
		assert ordinal >= 0;
		assert ordinal < N;

		return ((queue >>> ordinal) & 1L) == 1L;
	}

	@Override
	boolean remove(int index) {
		assert index >= 0;
		assert index < N;

		long old = queue;
		queue &= ~(1L << index);

		if (old == queue)
			return false;

		if (min == index)
			min = nextSetBit(min + 1);
		return true;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		long mask = 0;
		for (Object o : c) {
			mask |= (1L << getOrdinal(o));
		}
		long old = queue;
		queue &= ~mask;
		min = nextSetBit(min);
		return old != queue;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		long mask = 0;
		for (Object o : c) {
			mask |= (1L << getOrdinal(o));
		}
		long old = queue;
		queue &= mask;
		min = nextSetBit(min);
		return old != queue;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		long mask = 0;
		for (Object o : c) {
			mask |= (1L << getOrdinal(o));
		}
		return (mask & ~queue) == 0;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		long mask = 0;
		for (Object o : c) {
			mask |= (1L << getOrdinal(o));
		}
		long old = queue;
		queue |= mask;
		if (old == queue)
			return false;
		min = nextSetBit(0);
		return true;
	}

}
