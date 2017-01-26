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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/** A map with sets as values, HashMap implementation.
 *
 * @author Ondrej Lhotak
 */

public class HashMultiMap<K,V> extends AbstractMultiMap<K, V> {
    protected final Map<K,Set<V>> m;
    
    protected Map<K, Set<V>> createMap() {
    	return createMap(0);
    }
    
    protected Map<K, Set<V>> createMap(int initialSize) {
    	return new HashMap<K,Set<V>>(initialSize);
    }

    public HashMultiMap() {
    	this.m = createMap();
    }
    
    public HashMultiMap(int initialSize) {
    	this.m = createMap(initialSize);
    }

    public HashMultiMap( MultiMap<K,V> m ) {
    	this.m = createMap();
        putAll( m );
    }
    
    public HashMultiMap( Map<K,Set<V>> m ) {
    	this.m = createMap();
        putAll( m );
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
        for (Set<V> s: m.values())
            if (s.contains(value)) return true;
        return false;
    }

    protected Set<V> newSet() {
        return new HashSet<V>(4);
    }
    
    private Set<V> findSet( K key ) {
        Set<V> s = m.get( key );
        if( s == null ) {
            s = newSet();
            m.put( key, s );
        }
        return s;
    }

    @Override
    public boolean put( K key, V value ) {
        return findSet( key ).add( value );
    }

    @Override
    public boolean putAll( K key, Set<V> values ) {
        if (values.isEmpty()) return false;
        return findSet( key ).addAll( values );
    }

    @Override
    public boolean remove( K key, V value ) {
        Set<V> s = m.get( key );
        if( s == null ) return false;
        boolean ret = s.remove( value );
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
        Set<V> s = m.get( key );
        if( s == null ) return false;
        boolean ret = s.removeAll( values );
        if( s.isEmpty() ) {
            m.remove( key );
        }
        return ret;
    }

    @Override
    public Set<V> get( K o ) {
        Set<V> ret = m.get( o );
        if( ret == null ) return Collections.emptySet();
        else return ret;
    }

    @Override
    public Set<K> keySet() {
        return m.keySet();
    }

    @Override
    public Set<V> values() {
        Set<V> ret = new HashSet<V>(m.size());
        for (Set<V> s : m.values())
            ret.addAll(s);
        return ret;
    }

    @Override
    public boolean equals( Object o ) {
        if( ! (o instanceof MultiMap) ) return false;
        @SuppressWarnings("unchecked")
		MultiMap<K,V> mm = (MultiMap<K,V>) o;
        if( !keySet().equals( mm.keySet() ) ) return false;
        Iterator<Map.Entry<K, Set<V>>> it = m.entrySet().iterator();
        while( it.hasNext() ) {
            Map.Entry<K, Set<V>> e = it.next();
            Set<V> s = e.getValue();
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

	@Override
	public void clear() {
		m.clear();
	}
	
}
