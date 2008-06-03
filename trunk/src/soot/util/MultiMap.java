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

/** A map with sets as values.
 *
 * @author Ondrej Lhotak
 */

public interface MultiMap {
    public boolean isEmpty();
    public int numKeys();
    public boolean containsKey( Object key );
    public boolean containsValue( Object value );
    public boolean put( Object key, Object value );
    public boolean putAll( Object key, Set values );
    public void putAll( MultiMap m );
//    public boolean putAll( Map m );
    public boolean remove( Object key, Object value );
    public boolean remove( Object key );
    public boolean removeAll( Object key, Set values );
    public Set get( Object o );
    public Set keySet();
    public Set values();
    public boolean equals( Object o );
    public int hashCode();
}
