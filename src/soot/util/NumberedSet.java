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
    public NumberedSet( Numberer universe ) {
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
                    bits[number/64] |= (1L)<<(number%64);
                    return true;
                }
            }
            array[pos] = o;
            return true;
        } else {
            int number = o.getNumber();
            if( number == 0 ) throw new RuntimeException( "unnumbered" );
            long bit = (1L)<<(number%64);
            boolean ret = true;
            try {
                ret = ( 0 == ( bits[number/64] & bit ) );
            } catch( ArrayIndexOutOfBoundsException e ) {
                long[] oldBits = bits;
                bits = new long[ universe.size()/64+2 ];
                System.arraycopy(oldBits,0,bits,0,oldBits.length);
            }
            bits[number/64] |= bit;
            return ret;
        }
    }
    public boolean contains( Numberable o ) {
        if( array != null ) {
            return array[ findPosition(o) ] != null;
        } else {
            int number = o.getNumber();
            if( number == 0 ) throw new RuntimeException( "unnumbered" );
            long bit = (1L)<<(number%64);
            boolean ret = false;
            try {
                ret = ( 0 != ( bits[number/64] & bit ) );
            } catch( ArrayIndexOutOfBoundsException e ) {}
            return ret;
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
            bits = new long[ uniSize/64+2 ];
        }
        Numberable[] oldArray = array;
        array = new Numberable[array.length*2];
        for( int i = 0; i < oldArray.length; i++ ) {
            Numberable element = oldArray[i];
            if( element != null ) {
                array[findPosition(element)] = element;
            }
        }
    }
    public Iterator iterator() { 
        if( array == null ) return new BitSetIterator( this );
        else return new NumberedSetIterator( this ); 
    }

    class BitSetIterator implements Iterator {
        NumberedSet set;
        int cur = 0;
        long mask = 1;
        BitSetIterator( NumberedSet set ) {
            this.set = set;
            seekNext();
        }
        protected final void seekNext() {
            try {
                while( ( set.bits[cur] & mask ) == 0 ) {
                    mask <<= 1;
                    if( mask == 0 ) {
                        cur++;
                        mask = 1;
                    }
                }
            } catch( ArrayIndexOutOfBoundsException e ) {
                cur = -1;
                mask = 0;
            }
        }
        public final boolean hasNext() { return cur != -1; }
        public void remove() {
            throw new RuntimeException( "Not implemented." );
        }
        public final Object next() {
            Numberable ret = universe.get(cur*64 + lookup[(int)(mask%67L)]);
            mask <<= 1;
            seekNext();
            return ret;
        }

    }
    /* Computes log_2(x) modulo 67. This uses the fact that 2 is a
     * primitive root modulo 67 */
    final static int[] lookup = {-1, 0, 1, 39, 2, 15, 40, 23, 3, 12, 16,
                         59, 41, 19, 24, 54,  4, -1, 13, 10, 17,
                         62, 60, 28, 42, 30, 20, 51, 25, 44, 55,
                         47,  5, 32, -1, 38, 14, 22, 11, 58, 18,
                         53, -1,  9, 61, 27, 29, 50, 43, 46, 31,
                         37, 21, 57, 52,  8, 26, 49, 45, 36, 56,
                         7, 48, 35, 6, 34, 33};

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

    private Numberable[] array = new Numberable[8];
    private long[] bits;
    private int size = 0;
    private Numberer universe;

}
