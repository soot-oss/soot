/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

/** A list containing exactly one object, immutable.
 *
 * @author Ondrej Lhotak
 */

public class SingletonList extends java.util.AbstractList {
    private Object o;
    public SingletonList( Object o ) { this.o = o; }
    public int size() { return 1; }
    public boolean contains( Object other ) { return other.equals(o); }
    public Object get( int index ) {
        if( index != 0 ) {
            throw new IndexOutOfBoundsException( "Singleton list; index = "+index );
        }
        return o;
    }
}
