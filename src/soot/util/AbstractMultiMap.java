package soot.util;

import java.util.Map;
import java.util.Set;

public abstract class AbstractMultiMap<K, V> implements MultiMap<K, V> {
	
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

}
