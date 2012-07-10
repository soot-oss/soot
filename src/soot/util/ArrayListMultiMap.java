package soot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ArrayListMultiMap<K, V> implements MultiMap<K, V> {
	
	private static final int DEFAULT_KEYS = 8;
	private static final int DEFAULT_VALUES = 8;

	private final int nrVals;
	private final Map<K, List<V>> map;
	
	/**
	 * Creates an empty array list multimap.
	 * @param nrk number of expected keys.
	 * @param nrv number of expected values per key.
	 */
	public ArrayListMultiMap(int nrk, int nrv) {
		if (nrv < 0 || nrk < 0)
			throw new IllegalArgumentException();
		this.nrVals = nrv;
		this.map = new HashMap<K, List<V>>(nrk);
	}

	/**
	 * Creates an empty array list multimap.
	 * @param nrk number of expected keys.
 	 */
	public ArrayListMultiMap(int nrk) {
		this(nrk, DEFAULT_VALUES);
	}

	/**
	 * Creates an empty array list multimap.
	 */
	public ArrayListMultiMap() {
		this(DEFAULT_KEYS, DEFAULT_VALUES);
	}
	
	private List<V> getOrCreate(K k) {
		List<V> l = map.get(k);
		if (l == null) {
			l = new ArrayList<V>(nrVals);
			map.put(k, l);
		}
		return l;
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}

	public int numKeys() {
		return map.size();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		for (List<V> l : map.values()) {
			if (l.contains(value))
				return true;
		}
		return false;
	}

	public boolean put(K key, V value) {
		return getOrCreate(key).add(value);
	}

	public boolean putAll(K key, Collection<V> values) {
		return getOrCreate(key).addAll(values);
	}

	public void putAll(MultiMap<K, V> m) {
		for (K o : m.keySet()) {
			getOrCreate(o).addAll(m.get(o));
		}
	}

	public boolean remove(Object key, Object value) {
		return get(key).remove(value);
	}

	public boolean remove(Object key) {
		return map.remove(key) != null;
	}

	public boolean removeAll(Object key, Collection<V> values) {
		return get(key).removeAll(values);
	}

	public Collection<V> get(Object o) {
		List<V> l = map.get(o);
		return l == null ? Collections.<V>emptyList() : l;
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Set<V> values() {
		Set<V> v = new HashSet<V>();
		for (List<V> l : map.values()) {
			v.addAll(l);
		}
		return v;
	}
}
