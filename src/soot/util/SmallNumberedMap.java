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

/** A java.util.Map-like map with Numberable objects as the keys.
 *
 * @author Ondrej Lhotak
 */

public final class SmallNumberedMap {
    public SmallNumberedMap( ArrayNumberer universe ) {
        this.universe = universe;
    }
    /** Associates a value with a key. */
    public boolean put( Numberable key, Object value ) {
        int pos = findPosition( key );
        if( array[pos] == key ) {
            if( values[pos] == value ) return false;
            values[pos] = value;
            return true;
        }
        size++;
        if( size*3 > array.length*2 ) {
            doubleSize();
            pos = findPosition( key );
        }
        array[pos] = key;
        values[pos] = value;
        return true;
    }
    /** Returns the value associated with a given key. */
    public Object get( Numberable key ) {
        return values[ findPosition(key) ];
    }
    /** Returns the number of non-null values in this map. */
    public int nonNullSize() {
        int ret = 0;
        for( int i = 0; i < values.length; i++ ) {
            if( values[i] != null ) ret++;
        }
        return ret;
    }

    /** Returns an iterator over the keys with non-null values. */
    public Iterator keyIterator() {
        return new KeyIterator( this );
    }

    /** Returns an iterator over the non-null values. */
    public Iterator iterator() {
        return new ValueIterator( this );
    }

    abstract class SmallNumberedMapIterator implements Iterator {
        SmallNumberedMap map;
        int cur = 0;
        SmallNumberedMapIterator( SmallNumberedMap map ) {
            this.map = map;
            seekNext();
        }
        protected final void seekNext() {
            try {
                while( map.values[cur] == null ) {
                    cur++;
                }
            } catch( ArrayIndexOutOfBoundsException e ) {
                cur = -1;
            }
        }
        public final boolean hasNext() { return cur != -1; }
        public abstract Object next();
        public void remove() {
            throw new RuntimeException( "Not implemented." );
        }
    }

    class KeyIterator extends SmallNumberedMapIterator {
        KeyIterator( SmallNumberedMap map ) { super(map); }
        public final Object next() {
            Numberable ret = array[cur];
            cur++;
            seekNext();
            return ret;
        }
    }

    class ValueIterator extends SmallNumberedMapIterator {
        ValueIterator( SmallNumberedMap map ) { super(map); }
        public final Object next() {
            Object ret = values[cur];
            cur++;
            seekNext();
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
        Object[] oldValues = values;
        int newLength = array.length*2;
        values = new Object[newLength];
        array = new Numberable[newLength];
        for( int i = 0; i < oldArray.length; i++ ) {
            Numberable element = oldArray[i];
            if( element != null ) {
                int pos = findPosition(element);
                array[pos] = element;
                values[pos] = oldValues[i];
            }
        }
    }
    private Numberable[] array = new Numberable[8];
    private Object[] values = new Object[8];
    private long[] bits;
    private int size = 0;
    private ArrayNumberer universe;
}
