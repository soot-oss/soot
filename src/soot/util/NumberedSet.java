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

/** Holds a set of Numberable objects.
 *
 * @author Ondrej Lhotak
 */

public final class NumberedSet {
    public NumberedSet( ArrayNumberer universe ) {
        this.universe = universe;
    }
    public boolean add( Numberable o ) {
        if( array != null ) {
            int pos = findPosition( o );
            if( array[pos] == o ) return false;
            size++;
            if( size*3 > array.length*2 ) {
                doubleSize();
                if( array != null ) {
                    pos = findPosition( o );
                } else {
                    int number = o.getNumber();
                    if( number == 0 ) throw new RuntimeException( "unnumbered" );
                    return bits.set( number );
                }
            }
            array[pos] = o;
            return true;
        } else {
            int number = o.getNumber();
            if( number == 0 ) throw new RuntimeException( "unnumbered" );
            if( bits.set( number ) ) {
                size++;
                return true;
            } else {
                return false;
            }
        }
    }
    public boolean contains( Numberable o ) {
        if( array != null ) {
            return array[ findPosition(o) ] != null;
        } else {
            int number = o.getNumber();
            if( number == 0 ) throw new RuntimeException( "unnumbered" );
            return bits.get( number );
        }
    }

    /* Private stuff. */

    private final int findPosition( Numberable o ) {
        int number = o.getNumber();
        if( number == 0 ) throw new RuntimeException( "unnumbered" );
        number = number & (array.length-1);
        while(true) {
            if( array[number] == o ) return number;
            if( array[number] == null ) return number;
            number = (number+1) & (array.length-1);
        }
    }
    private final void doubleSize() {
        int uniSize = universe.size();
        if( array.length*128 > uniSize ) {
            bits = new BitVector( uniSize );
            Numberable[] oldArray = array;
            array = null;
            for (Numberable element : oldArray) {
                if( element != null ) {
                    bits.set( element.getNumber() );
                }
            }
        } else {
            Numberable[] oldArray = array;
            array = new Numberable[array.length*2];
            for (Numberable element : oldArray) {
                if( element != null ) {
                    array[findPosition(element)] = element;
                }
            }
        }
    }
    public Iterator iterator() { 
        if( array == null ) return new BitSetIterator( this );
        else return new NumberedSetIterator( this ); 
    }

    class BitSetIterator implements Iterator {
        soot.util.BitSetIterator iter;
        BitSetIterator( NumberedSet set ) {
            iter = set.bits.iterator();
        }
        public final boolean hasNext() { return iter.hasNext(); }
        public void remove() {
            throw new RuntimeException( "Not implemented." );
        }
        public final Object next() {
            return universe.get( iter.next() );
        }

    }

    class NumberedSetIterator implements Iterator {
        NumberedSet set;
        int cur = 0;
        NumberedSetIterator( NumberedSet set ) {
            this.set = set;
            seekNext();
        }
        protected final void seekNext() {
            try {
                while( set.array[cur] == null ) {
                    cur++;
                }
            } catch( ArrayIndexOutOfBoundsException e ) {
                cur = -1;
            }
        }
        public final boolean hasNext() { return cur != -1; }
        public void remove() {
            throw new RuntimeException( "Not implemented." );
        }
        public final Object next() {
            Numberable ret = set.array[cur];
            cur++;
            seekNext();
            return ret;
        }
    }
    public final int size() { return size; }

    private Numberable[] array = new Numberable[8];
    private BitVector bits;
    private int size = 0;
    private ArrayNumberer universe;

}
