/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.util;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A concurrent version of the {@link HashMultiMap}
 *
 * @author Steven Arzt
 * 
 */
public class ConcurrentHashMultiMap<K,V> implements MultiMap<K,V> {
    Map<K,ConcurrentMap<V, V>> m = new ConcurrentHashMap<K,ConcurrentMap<V, V>>(0);

    public ConcurrentHashMultiMap() {}
    
    public ConcurrentHashMultiMap( MultiMap<K,V> m ) {
        putAll( m );
    }

    @Override
    public void putAll( MultiMap<K,V> m ) {
        for (K key : m.keySet())
            putAll(key, m.get(key));
    }

    @Override
    public boolean isEmpty() {
        return numKeys() == 0;
    }

    @Override
    public int numKeys() {
        return m.size();
    }

    @Override
    public boolean containsKey( Object key ) {
        return m.containsKey( key );
    }

    @Override
    public boolean containsValue( V value ) {
        for (Map<V, V> s: m.values())
            if (s.containsKey(value)) return true;
        return false;
    }

    protected ConcurrentMap<V,V> newSet() {
        return new ConcurrentHashMap<V, V>();
    }
    
    private ConcurrentMap<V, V> findSet( K key ) {
    	ConcurrentMap<V, V> s = m.get( key );
        if( s == null ) {
        	synchronized (this) {
        		// Better check twice, another thread may have created a set in
        		// the meantime
        		s = m.get( key );
                if( s == null ) {
                	s = newSet();
                	m.put( key, s );
                }
			}
        }
        return s;
    }

    @Override
    public boolean put( K key, V value ) {
        return findSet( key ).put( value, value ) == null;
    }
    
    public V putIfAbsent( K key, V value ) {
        return findSet( key ).putIfAbsent( value, value );
    }
    
    @Override
    public boolean putAll( K key, Set<V> values ) {
        if (values.isEmpty()) return false;
        
        Map<V,V> set = findSet( key );
        boolean ok = false;
        for (V v : values)
        	if (set.put(v, v) == null)
        		ok = true;
        
        return ok;
    }

    @Override
    public boolean remove( K key, V value ) {
        Map<V,V> s = m.get( key );
        if( s == null ) return false;
        boolean ret = s.remove( value ) != null;
        if( s.isEmpty() ) {
            m.remove( key );
        }
        return ret;
    }

    @Override
    public boolean remove( K key ) {
        return null != m.remove( key );
    }

    @Override
    public boolean removeAll( K key, Set<V> values ) {
        Map<V,V> s = m.get( key );
        if( s == null ) return false;
        boolean ret = false;
        for (V v : values)
        	if (s.remove( v ) != null)
        		ret = true;
        if( s.isEmpty() ) {
            m.remove( key );
        }
        return ret;
    }

    @Override
    public Set<V> get( K o ) {
        Map<V,V> ret = m.get( o );
        if( ret == null ) return Collections.emptySet();
        return Collections.unmodifiableSet(ret.keySet());
    }

    @Override
    public Set<K> keySet() {
        return m.keySet();
    }

    @Override
    public Set<V> values() {
        Set<V> ret = new HashSet<V>(m.size());
        for (Map<V,V> s : m.values())
            ret.addAll(s.keySet());
        return ret;
    }

    @Override
    public boolean equals( Object o ) {
        if( ! (o instanceof MultiMap) ) return false;
        @SuppressWarnings("unchecked")
		MultiMap<K,V> mm = (MultiMap<K,V>) o;
        if( !keySet().equals( mm.keySet() ) ) return false;
        for (Map.Entry<K, ConcurrentMap<V,V>> e : m.entrySet()) {
            Map<V, V> s = e.getValue();
            if( !s.equals( mm.get( e.getKey() ) ) ) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return m.hashCode();
    }

	@Override
	public int size() {
		return m.size();
	}
}
