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
 * @author xiao, generalize it.
 */

public class ArrayNumberer<E extends Numberable> implements IterableNumberer<E> {
    @SuppressWarnings("unchecked")
    protected E[] numberToObj = (E[]) new Numberable[1024];
    protected int lastNumber = 0;
    
    private void resize(int n) {
    	E[] old = numberToObj;    	
    	numberToObj = Arrays.copyOf(numberToObj, n);
    	Arrays.fill(old, null);
    }
    
    public void add( E o ) {
        if( o.getNumber() != 0 ) return;
        
        ++lastNumber;
        if( lastNumber >= numberToObj.length ) {
        	resize(numberToObj.length*2);
        }
        numberToObj[lastNumber] = o;
        o.setNumber( lastNumber );
    }

    public long get( E o ) {
        if( o == null ) return 0;
        int ret = o.getNumber();
        if( ret == 0 ) throw new RuntimeException( "unnumbered: "+o );
        return ret;
    }

	public E get( long number ) {
        if( number == 0 ) return null;
		E ret = numberToObj[(int) number];
        if( ret == null ) throw new RuntimeException( "no object with number "+number );
        return ret;
    }

    public int size() { 
    	return lastNumber; 
    }

    public Iterator<E> iterator() {
        return new Iterator<E>() {
            int cur = 1;
            public final boolean hasNext() {
                return cur <= lastNumber && cur < numberToObj.length && numberToObj[cur] != null;
            }

    		public final E next() { 
                if ( hasNext() ) {
                	return numberToObj[cur++];
                }
                throw new NoSuchElementException();
            }
    		
            public final void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
