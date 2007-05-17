/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.jimple.spark.ondemand.genericutil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

abstract class AbstractMultiMap<K, V> implements MultiMap<K, V> {

	protected final Map<K, Set<V>> map = new HashMap<K, Set<V>>();
	
	protected final boolean create;
	
	protected AbstractMultiMap(boolean create) {
		this.create = create;
	}
	
	protected abstract Set<V> createSet();
	
	protected Set<V> emptySet() {
		return Collections.<V>emptySet();
	}
    /* (non-Javadoc)
	 * @see AAA.util.MultiMap#get(K)
	 */
    public Set<V> get(K key) {
		Set<V> ret = map.get(key);
		if (ret == null) {
		    if (create) {
		        ret = createSet();
		        map.put(key, ret);
		    } else {
		        ret = emptySet();
		    }
		}
		return ret;
    }
    
    /* (non-Javadoc)
	 * @see AAA.util.MultiMap#put(K, V)
	 */
    public boolean put(K key, V val) {
		Set<V> vals = map.get(key);
		if (vals == null) {
			vals = createSet();
			map.put(key, vals);
		}		
		return vals.add(val);        
    }
    
    /* (non-Javadoc)
	 * @see AAA.util.MultiMap#remove(K, V)
	 */
    public boolean remove(K key, V val) {
	    Set<V> elems = map.get(key);
	    if (elems == null) return false;
	    boolean ret = elems.remove(val);
	    if (elems.isEmpty()) {
	        map.remove(key);
	    }
        return ret;
    }
    
    public Set<V> removeAll(K key) {
      return map.remove(key);
    }
    /* (non-Javadoc)
	 * @see AAA.util.MultiMap#keys()
	 */
    public Set<K> keySet() {
    	return map.keySet();
    }
    
    /* (non-Javadoc)
	 * @see AAA.util.MultiMap#containsKey(java.lang.Object)
	 */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
    
    /* (non-Javadoc)
	 * @see AAA.util.MultiMap#size()
	 */
    public int size() {
        int ret = 0;
        for (K key : keySet()) {
			ret += get(key).size();
		}
        return ret;
    }
    
    /* (non-Javadoc)
	 * @see AAA.util.MultiMap#toString()
	 */
    public String toString() {
        return map.toString();
    }

	/* (non-Javadoc)
	 * @see AAA.util.MultiMap#putAll(K, java.util.Set)
	 */
	public boolean putAll(K key, Collection<? extends V> vals) {
		Set<V> edges = map.get(key);
		if (edges == null) {
			edges = createSet();
			map.put(key, edges);
		}		
		return edges.addAll(vals);        
	}

	public void clear() {
		map.clear();
	}
    
    public boolean isEmpty() {
      return map.isEmpty();
    }
}
