package soot.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A hash based table.
 */
public class HashTable<C, R, V> implements Table<C, R, V> {

	private int size;
	private final int nrVals;
	private final Map<C, Map<R, V>> mmap;

	/**
	 * Constructs a new hash table.
	 * 
	 * @param nrVals
	 *            estimated number of rows per column.
	 */
	public HashTable(int nrVals) {
		this.nrVals = nrVals;
		this.mmap = new HashMap<C, Map<R,V>>();
	}

	public void clear() {
		mmap.clear();
		size = 0;
	}
	public int size() {
		return size;
	}

	public V put(C column, R row, V value) {
		Map<R, V> rows = mmap.get(column);
		if (rows == null) {
			rows = new HashMap<R, V>(nrVals);
			mmap.put(column, rows);
		}
		V old = rows.put(row, value);
		if (old != value)
			++size;
		return old;
	}

	public V get(Object column, Object row) {
		Map<?, V> rows = mmap.get(column);
		return rows == null ? null : rows.get(row);
	}

	public V remove(Object column, Object row) {
		Map<?, V> rows = mmap.get(column);
		if (rows != null) {
			V old = rows.remove(row);
			if (old != null) {
				--size;
				return old;
			}
		}
		return null;
	}

	public boolean contains(Object column, Object row) {
		Map<?, V> rows = mmap.get(column);
		return rows != null && rows.containsKey(row);
	}
}