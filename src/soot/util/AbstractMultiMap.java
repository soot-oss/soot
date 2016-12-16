package soot.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import heros.solver.Pair;

public abstract class AbstractMultiMap<K, V> implements MultiMap<K, V> {
	
    private class EntryIterator implements Iterator<Pair<K,V>> {

    	Iterator<K> keyIterator = keySet().iterator();
    	Iterator<V> valueIterator = null;
    	K currentKey = null;
    	
		@Override
		public boolean hasNext() {
			if (valueIterator != null && valueIterator.hasNext())
				return true;
			
			// Prepare for the next key
			valueIterator = null;
			currentKey = null;
			return keyIterator.hasNext();
		}

		@Override
		public Pair<K, V> next() {
			// Obtain the next key
			if (valueIterator == null) {
				currentKey = keyIterator.next();
				valueIterator = get(currentKey).iterator();
			}
			return new Pair<K, V>(currentKey, valueIterator.next());
		}
		
		@Override
		public void remove() {
			valueIterator.remove();

			if (get(currentKey).isEmpty()) {
				keyIterator.remove();
				keyIterator = null;
				currentKey = null;
			}
		}
    	
    }
	
    @Override
    public boolean putAll( MultiMap<K,V> m ) {
    	boolean hasNew = false;
        for (K key : m.keySet())
            if (putAll(key, m.get(key)))
            	hasNew = true;
        return hasNew;
    }
    
    @Override
    public boolean putAll( Map<K,Set<V>> m ) {
    	boolean hasNew = false;
        for (K key : m.keySet())
            if (putAll(key, m.get(key)))
            	hasNew = true;
        return hasNew;
    }
    
    @Override
    public boolean isEmpty() {
        return numKeys() == 0;
    }
    
    @Override
	public boolean contains(K key, V value) {
		Set<V> set = get(key);
		if (set == null)
			return false;
		return set.contains(value);
	}

	@Override
	public Iterator<Pair<K, V>> iterator() {
		return new EntryIterator();
	}
}
