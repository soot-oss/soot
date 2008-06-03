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

/** A class that numbers objects, so they can be placed in bitsets.
 *
 * @author Ondrej Lhotak
 */

public class ArrayNumberer implements IterableNumberer {
    Numberable[] numberToObj = new Numberable[1024];
    int lastNumber = 0;

    public void add( Object oo ) {
        Numberable o = (Numberable) oo;
        if( o.getNumber() != 0 ) return;
        ++lastNumber;
        if( lastNumber >= numberToObj.length ) {
            Numberable[] newnto = new Numberable[numberToObj.length*2];
            System.arraycopy(numberToObj, 0, newnto, 0, numberToObj.length);
            numberToObj = newnto;
        }
        numberToObj[lastNumber] = o;
        o.setNumber( lastNumber );
    }

    public long get( Object oo ) {
        if( oo == null ) return 0;
        Numberable o = (Numberable) oo;
        int ret = o.getNumber();
        if( ret == 0 ) throw new RuntimeException( "unnumbered: "+o );
        return ret;
    }

    public Object get( long number ) {
        if( number == 0 ) return null;
        Object ret = numberToObj[(int) number];
        if( ret == null ) throw new RuntimeException( "no object with number "+number );
        return ret;
    }

    public int size() { return lastNumber; }

    public Iterator iterator() {
        return new NumbererIterator();
    }

    final class NumbererIterator implements Iterator {
        int cur = 1;
        public final boolean hasNext() {
            return cur < numberToObj.length && numberToObj[cur] != null;
        }
        public final Object next() { 
            if( !hasNext() ) throw new NoSuchElementException();
            return numberToObj[cur++];
        }
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
