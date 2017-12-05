/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

public class MapNumberer<T> implements Numberer<T> {
    Map<T, Integer> map = new HashMap<T, Integer>();
    ArrayList<T> al = new ArrayList<T>();
    int nextIndex = 1;
    public void add( T o ) {
        if( !map.containsKey(o) ) {
            map.put( o, new Integer(nextIndex) );
            al.add(o);
            nextIndex++;
        }
    }
    public T get( long number ) {
        return al.get((int) number);
    }
    public long get( Object o ) {
        if( o == null ) return 0;
        Integer i = map.get(o);
        if( i == null ) throw new RuntimeException( "couldn't find "+o );
        return i.intValue();
    }
    public int size() { return nextIndex-1; /*subtract 1 for null*/ }
    public MapNumberer() { al.add(null); }
    public boolean contains(Object o) { return map.containsKey(o); }
}
