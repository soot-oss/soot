/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *       updated 2002 Florian Loitsch
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.scalar;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 *   Reference implementation for a BoundedFlowSet. Items are stored in an Array.  
 */
public class ArrayPackedSet<T> extends AbstractBoundedFlowSet<T>
{
    ObjectIntMapper<T> map;
    BitSet bits;

    public ArrayPackedSet(FlowUniverse<T> universe) {
        this(new ObjectIntMapper<T>(universe));
    }

    ArrayPackedSet(ObjectIntMapper<T> map)
    {
        this(map, new BitSet());
    }
    
    ArrayPackedSet(ObjectIntMapper<T> map, BitSet bits)
    {
        this.map = map;
        this.bits = bits;
    }

    /** Returns true if flowSet is the same type of flow set as this. */
    @SuppressWarnings("rawtypes")
	private boolean sameType(Object flowSet)
    {
        return (flowSet instanceof ArrayPackedSet &&
                ((ArrayPackedSet)flowSet).map == map);
    }

    public ArrayPackedSet<T> clone()
    {
        return new ArrayPackedSet<T>(map, (BitSet) bits.clone());
    }

    public FlowSet<T> emptySet()
    {
        return new ArrayPackedSet<T>(map);
    }

    public int size()
    {
        return bits.cardinality();
    }

    public boolean isEmpty()
    {
        return bits.isEmpty();
    }


    public void clear()
    {
        bits.clear();
    }


    public List<T> toList(int lowInclusive, int highInclusive)
    {
    	int highExclusive = highInclusive + 1;

    	if (lowInclusive < 0)
    		throw new IllegalArgumentException();
    	
    	if (lowInclusive > highInclusive)
    		throw new IllegalArgumentException();
    	
    	if (lowInclusive == highInclusive)
    		return bits.get(lowInclusive) 
    			? Collections.singletonList(map.getObject(lowInclusive)) 
    			: Collections.<T>emptyList() 
    			;
    	
    	int i = bits.nextSetBit(lowInclusive);
    	if (i < 0 || i >= highExclusive)
    		return Collections.emptyList();
    	
		List<T> elements = new LinkedList<T>();                		
		for (;;) {
			int endOfRun = Math.min(highExclusive, bits.nextClearBit(i+1));
			do { elements.add(map.getObject(i++)); }
			while (i < endOfRun);
			if (i >= highExclusive)
				return elements;
			i = bits.nextSetBit(i+1);
			if (i < 0 || i >= highExclusive)
				return elements;
		}
    }
    

    public List<T> toList()
    {
    	int i = bits.nextSetBit(0);
    	if (i == -1)
    		return Collections.emptyList();
    	
	List<T> elements = new ArrayList<T>(bits.cardinality());
	for (; i >= 0; i = bits.nextSetBit(i+1)) {
		int endOfRun = bits.nextClearBit(i+1);
		do { elements.add(map.getObject(i++)); }
		while (i < endOfRun);
	}

        return elements;
    }

    public void add(T obj)
    {
        bits.set(map.getInt(obj));
    }

    public void complement(FlowSet<T> destFlow)
    {
      if (sameType(destFlow)) {
        ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;

        if (this != dest) {
	        dest.bits.clear();
	        dest.bits.or(this.bits);
        }   
        dest.bits.flip(0, dest.bits.size());
      } else
        super.complement(destFlow);
    }

    public void remove(T obj)
    {
    	bits.clear(map.getInt(obj));
    }
    
	@Override
	public boolean isSubSet(FlowSet<T> other) {
		if (other == this)
			return true;
		if (sameType(other)) {
	          ArrayPackedSet<T> o = (ArrayPackedSet<T>) other;
	          
	          BitSet tmp = (BitSet) o.bits.clone();
	          tmp.andNot(bits);
	          return tmp.isEmpty();
		}
		return super.isSubSet(other);
	}
	
	
    public void union(FlowSet<T> otherFlow, FlowSet<T> destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        ArrayPackedSet<T> other = (ArrayPackedSet<T>) otherFlow;
        ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;

        if(!(other instanceof ArrayPackedSet))
            throw new RuntimeException("Incompatible other set for union");

        if (this != dest) {
	        dest.bits.clear();
	        dest.bits.or(this.bits);
        }
        
        dest.bits.or(other.bits);
        
      } else
        super.union(otherFlow, destFlow);
    }

    public void difference(FlowSet<T> otherFlow, FlowSet<T> destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {

        if(!(otherFlow instanceof ArrayPackedSet))
            throw new RuntimeException("Incompatible other set for union");

        ArrayPackedSet<T> other = (ArrayPackedSet<T>) otherFlow;
        ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;
        

        if (this != dest) {
	        dest.bits.clear();
	        dest.bits.or(this.bits);
        }        
        dest.bits.andNot(other.bits);
        
      } else
        super.difference(otherFlow, destFlow);
    }
    
    public void intersection(FlowSet<T> otherFlow, FlowSet<T> destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        if(!(otherFlow instanceof ArrayPackedSet))
            throw new RuntimeException("Incompatible other set for union");

        ArrayPackedSet<T> other = (ArrayPackedSet<T>) otherFlow;
        ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;

        if (this != dest) {
	        dest.bits.clear();
	        dest.bits.or(this.bits);
        }        
        dest.bits.and(other.bits);
      } else
        super.intersection(otherFlow, destFlow);
    }

  /** Returns true, if the object is in the set.
   */
    public boolean contains(T obj)
    {
      /* check if the object is in the map, direct call of map.getInt will
       * add the object into the map.
       */
        if (!map.contains(obj)) return false;
        
        return bits.get(map.getInt(obj));
    }

    @SuppressWarnings("unchecked")
	public boolean equals(Object otherFlow)
    {
      if (sameType(otherFlow)) {
        return bits.equals(((ArrayPackedSet<T>)otherFlow).bits);
      } else
        return super.equals(otherFlow);
    }

    public void copy(FlowSet<T> destFlow)
    {
		if (this == destFlow)
			return;
      if (sameType(destFlow)) {
        ArrayPackedSet<T> dest = (ArrayPackedSet<T>) destFlow;
                
        dest.bits.clear();
        dest.bits.or(this.bits);
      } else
        super.copy(destFlow);
    }

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			
			int i = bits.nextSetBit(0);		
			T t;
			
			@Override
			public boolean hasNext() {
				return (i >= 0);
			}

			@Override
			public T next() {
				if (i < 0)
					throw new NoSuchElementException();
				t = map.getObject(i);				
				i = bits.nextSetBit(i+1);					
				return t;
			}

			@Override
			public void remove() {
				if (t == null)
					throw new IllegalStateException();
		        bits.clear(i);
		        t = null;
			}
			
		};
	}

}

