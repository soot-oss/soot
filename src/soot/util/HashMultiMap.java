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
import java.util.*;

/** A map with sets as values, HashMap implementation.
 *
 * @author Ondrej Lhotak
 */

public class HashMultiMap implements MultiMap {
    Map m = new HashMap(0);

    public HashMultiMap() {}
    public HashMultiMap( MultiMap m ) {
	putAll( m );
    }
    public void putAll( MultiMap m ) {
	Iterator it = m.keySet().iterator();
	while( it.hasNext() ) {
	    Object o = it.next();
	    putAll( o, m.get( o ) );
	}
    }
    public boolean isEmpty() {
	return numKeys() == 0;
    }
    public int numKeys() {
	return m.size();
    }
    public boolean containsKey( Object key ) {
	return m.containsKey( key );
    }
    public boolean containsValue( Object value ) {
	Iterator it = m.values().iterator();
	while( it.hasNext() ) {
	    Set s = (Set) it.next();
	    if( s.contains( value ) ) return true;
	}
	return false;
    }
    protected Set newSet() {
	return new HashSet(4);
    }
    private Set findSet( Object key ) {
	Set s = (Set) m.get( key );
	if( s == null ) {
	    s = newSet();
	    m.put( key, s );
	}
	return s;
    }
    public boolean put( Object key, Object value ) {
	return findSet( key ).add( value );
    }
    public boolean putAll( Object key, Set values ) {
	if (values.isEmpty()) return false;
	return findSet( key ).addAll( values );
    }
    public boolean remove( Object key, Object value ) {
	Set s = (Set) m.get( key );
	if( s == null ) return false;
	boolean ret = s.remove( value );
	if( s.isEmpty() ) {
	    m.remove( key );
	}
	return ret;
    }
    public boolean remove( Object key ) {
	return null != m.remove( key );
    }
    public boolean removeAll( Object key, Set values ) {
        Set s = (Set) m.get( key );
        if( s == null ) return false;
        boolean ret = s.removeAll( values );
        if( s.isEmpty() ) {
            m.remove( key );
        }
        return ret;
    }
    public Set get( Object o ) {
	Set ret = (Set) m.get( o );
	if( ret == null ) return Collections.EMPTY_SET;
	return Collections.unmodifiableSet(ret);
    }
    public Set keySet() {
	return m.keySet();
    }
    public Set values() {
	Set ret = new HashSet(0);
	Iterator it = m.values().iterator();
	while( it.hasNext() ) {
	    Set s = (Set) it.next();
	    ret.addAll( s );
	}
	return ret;
    }
    public boolean equals( Object o ) {
	if( ! (o instanceof MultiMap) ) return false;
	MultiMap mm = (MultiMap) o;
	if( !keySet().equals( mm.keySet() ) ) return false;
	Iterator it = m.entrySet().iterator();
	while( it.hasNext() ) {
	    Map.Entry e = (Map.Entry) it.next();
	    Set s = (Set) e.getValue();
	    if( !s.equals( mm.get( e.getKey() ) ) ) return false;
	}
	return true;
    }
    public int hashCode() {
	return m.hashCode();
    }
}
