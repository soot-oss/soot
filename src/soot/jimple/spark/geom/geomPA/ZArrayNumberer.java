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
 * Similar to the ArrayNumberer class in soot. But, this class counts the objects from zero.
 * And, we permit the deletion of objects from the array container.
 * And most importantly, we permits the search for a particular object efficiently.
 * Therefore, this class supports efficiently insert, lookup, deletion and traversal queries.
 * 
 * @author xiao
 */
public class ZArrayNumberer<E extends Numberable> implements IterableNumberer<E> , Iterable<E>
{
    Numberable[] numberToObj = null;
    Map<E, E> objContainer = null;
    int lastNumber = 0;
    int filledCells = 0;
    
    public ZArrayNumberer()
    {
    	// With default initialize size
    	numberToObj = new Numberable[1023];
    	objContainer = new HashMap<E, E>(1023);
    }
    
    public ZArrayNumberer( int initSize )
    {
    	numberToObj = new Numberable[initSize];
    	objContainer = new HashMap<E, E>(initSize);
    }
    
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

    /**
     * Clear the reference to the objects to help the garbage collection
     */
    public void clear()
    {
    	
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
    
    /**
     * Return how many objects are in the container but not the capacity of the container.
     */
    public int size() 
    { 
    	return filledCells; 
    }

    /**
     * The removed objects cause some empty slots. 
     * We shift the objects to the empty slots in order to ensure ids of the objects are less than the filledCells. 
     */
    public void reassign()
    {
    	int i, j;
    	
    	for ( i = 0, j = lastNumber - 1; i < j; ++i ) {
    		if ( numberToObj[i] != null )
    			continue;
    		
    		while ( j > i ) {
    			if ( numberToObj[j] != null ) break;
    			--j;
    		}
    		
    		if ( i == j ) break;
    		
    		numberToObj[i] = numberToObj[j];
    		numberToObj[i].setNumber(i);
    		numberToObj[j] = null;
    	}
    	
    	lastNumber = i;
    }
    
    @Override
    public Iterator<E> iterator() {
        return new NumbererIterator();
    }

    final class NumbererIterator implements Iterator<E> {
        int cur = 0;
        E lastElement = null;
        
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
        	lastElement = (E)numberToObj[cur++];
        	return lastElement;
        }
        
        public final void remove() {
            ZArrayNumberer.this.remove( lastElement );
        }
    }
}

