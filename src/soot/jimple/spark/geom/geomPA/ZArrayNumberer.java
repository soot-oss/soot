/* Soot - a J*va Optimization Framework
 * Copyright (C) 2011 Richard Xiao
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
package soot.jimple.spark.geom.geomPA;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import soot.util.IterableNumberer;
import soot.util.Numberable;

/**
 * Similar to the ArrayNumberer in soot. But, this class counts the objects from zero.
 * And, we permit the deletion of objects from the array container.
 * And most importantly, we permits the search for a particular object efficiently.
 * Therefore, this class supports both efficiently insert, lookup, deletion and list queries.
 * 
 * @author richardxx
 *
 * @param <E>
 */
public class ZArrayNumberer<E extends Numberable > implements IterableNumberer<E> , Iterable<E>
{
    Numberable[] numberToObj = new Numberable[1024];
    Map<E, E> objContainer = new HashMap<E, E>();
    int lastNumber = 0;
    int filledCells = 0;
    
    public void add( E o ) 
    {
    	// We check if this object is already put into the set
        if( o.getNumber() != -1 &&
        		numberToObj[o.getNumber()] == o )
        	return;
        
        numberToObj[lastNumber] = o;
        o.setNumber( lastNumber );
        objContainer.put(o, o);
        
        ++lastNumber;
        ++filledCells;
        if( lastNumber >= numberToObj.length ) {
            Numberable[] newnto = new Numberable[numberToObj.length*2];
            System.arraycopy(numberToObj, 0, newnto, 0, numberToObj.length);
            numberToObj = newnto;
        }
    }

    public void clear()
    {
    	// Clear the reference for garbage collection
    	for ( int i = 0; i < lastNumber; ++i )
    		numberToObj[i] = null;
    	
    	lastNumber = 0;
    	filledCells = 0;
    	objContainer.clear();
    }
    
    /**
     * Input object o should be added to this container previously.
     */
    public long get( E o ) 
    {
        if( o == null ) return -1;
        return o.getNumber();
    }

    @SuppressWarnings("unchecked")
	public E get( long number ) {
        E ret = (E) numberToObj[(int) number];
        return ret;
    }

    /**
     * Input object o is not required to be an object added previously.
     * @param o
     * @return
     */
    public E searchFor( E o )
    {
    	return objContainer.get(o);
    }
    
    public boolean remove( E o )
    {
    	int id = o.getNumber();
    	if ( id < 0 )
    		return false;
    	if ( numberToObj[id] != o )
    		return false;
    	
    	numberToObj[id] = null;
    	o.setNumber(-1);
    	--filledCells;
    	return true;
    }
    
    public int size() { return filledCells; }

    
    public Iterator<E> iterator() {
        return new NumbererIterator();
    }

    final class NumbererIterator implements Iterator<E> {
        int cur = 0;
        
        /**
         * We locate the next non-null item.
         */
        public final boolean hasNext() {
        	while ( cur < lastNumber ) {
        		if ( numberToObj[cur] != null )
        			break;
        		++cur;
        	}
        	
            return cur < lastNumber;
        }
        
        /**
         * We move on until a none null pointer found.
         * In this way, the clients don't need to be aware of the empty slots.
         */
        @SuppressWarnings("unchecked")
		public final E next() {
        	return (E)numberToObj[cur++];
        }
        
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

