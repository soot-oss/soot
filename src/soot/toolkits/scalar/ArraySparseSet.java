/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

import java.util.*;







/**
 *   Reference implementation for a FlowSet. Items are stored in an Array.  
 */
public class ArraySparseSet extends AbstractFlowSet
{
	protected static final int DEFAULT_SIZE = 8; 
    
    protected int numElements;
    protected int maxElements;
    protected Object[] elements;

    public ArraySparseSet()
    {
        maxElements = DEFAULT_SIZE;
        elements = new Object[DEFAULT_SIZE];
        numElements = 0;
    }
    
    private ArraySparseSet(ArraySparseSet other)
    {
        numElements = other.numElements;
        maxElements = other.maxElements;
        elements = other.elements.clone();
    }
    
    /** Returns true if flowSet is the same type of flow set as this. */
    private boolean sameType(Object flowSet)
    {
        return (flowSet instanceof ArraySparseSet);
    }

    public ArraySparseSet clone()
    {
        return new ArraySparseSet(this);
    }

    public Object emptySet()
    {
        return new ArraySparseSet();
    }

    public void clear()
    {
        numElements = 0;
    }
    
    public int size()
    {
        return numElements;
    }

    public boolean isEmpty()
    {
        return numElements == 0;
    }

    /** Returns a unbacked list of elements in this set. */
    public List toList()
    {
        Object[] copiedElements = new Object[numElements];
        System.arraycopy(elements, 0, copiedElements, 0, numElements);
        return Arrays.asList(copiedElements);
    }

  /* Expand array only when necessary, pointed out by Florian Loitsch
   * March 08, 2002
   */
    public void add(Object e)
    {
      /* Expand only if necessary! and removes one if too:) */
        // Add element
            if(!contains(e)) {
              // Expand array if necessary
              if(numElements == maxElements)
                doubleCapacity();
              elements[numElements++] = e;
            }
    }

    private void doubleCapacity()
    {        
        int newSize = maxElements * 2;
                    
        Object[] newElements = new Object[newSize];
                
        System.arraycopy(elements, 0, newElements, 0, numElements);
        elements = newElements;
        maxElements = newSize;
    }    

    public void remove(Object obj)
    {
        int i = 0;
        while (i < this.numElements) {
            if (elements[i].equals(obj))
            {
            	numElements--;
            	//copy last element to deleted position
                elements[i] = elements[numElements];
                //delete reference in last cell so that
                //we only retain a single reference to the
                //"old last" element, for memory safety
                elements[numElements] = null;
                return;
            } else
                i++;
        }
    }

  public void union(FlowSet otherFlow, FlowSet destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        ArraySparseSet other = (ArraySparseSet) otherFlow;
        ArraySparseSet dest = (ArraySparseSet) destFlow;

        // For the special case that dest == other
            if(dest == other)
            {
                for(int i = 0; i < this.numElements; i++)
                    dest.add(this.elements[i]);
            }
        
        // Else, force that dest starts with contents of this
        else {
            if(this != dest)
                copy(dest);

            for(int i = 0; i < other.numElements; i++)
                dest.add(other.elements[i]);
        }
      } else
        super.union(otherFlow, destFlow);
    }

    public void intersection(FlowSet otherFlow, FlowSet destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        ArraySparseSet other = (ArraySparseSet) otherFlow;
        ArraySparseSet dest = (ArraySparseSet) destFlow;
        ArraySparseSet workingSet;
        
        if(dest == other || dest == this)
            workingSet = new ArraySparseSet();
        else { 
            workingSet = dest;
            workingSet.clear();
        }
        
        for(int i = 0; i < this.numElements; i++)
        {
            if(other.contains(this.elements[i]))
                workingSet.add(this.elements[i]);
        }
        
        if(workingSet != dest)
            workingSet.copy(dest);
      } else
        super.intersection(otherFlow, destFlow);
    }

    public void difference(FlowSet otherFlow, FlowSet destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        ArraySparseSet other = (ArraySparseSet) otherFlow;
        ArraySparseSet dest = (ArraySparseSet) destFlow;
        ArraySparseSet workingSet;
        
        if(dest == other || dest == this)
            workingSet = new ArraySparseSet();
        else { 
            workingSet = dest;
            workingSet.clear();
        }
        
        for(int i = 0; i < this.numElements; i++)
        {
            if(!other.contains(this.elements[i]))
                workingSet.add(this.elements[i]);
        }
        
        if(workingSet != dest)
            workingSet.copy(dest);
      } else
        super.difference(otherFlow, destFlow);
    }
    
    /**
     * @deprecated This method uses linear-time lookup.
     * For better performance, consider using a {@link HashSet} instead, if you require this operation.
     */
    public boolean contains(Object obj)
    {
        for(int i = 0; i < numElements; i++)
            if(elements[i].equals(obj))
                return true;
                
        return false;
    }

    public boolean equals(Object otherFlow)
    {
      if (sameType(otherFlow)) {
        ArraySparseSet other = (ArraySparseSet) otherFlow;
         
        if(other.numElements != this.numElements)
            return false;
     
        int size = this.numElements;
             
        // Make sure that thisFlow is contained in otherFlow  
            for(int i = 0; i < size; i++)
                if(!other.contains(this.elements[i]))
                    return false;

            /* both arrays have the same size, no element appears twice in one
             * array, all elements of ThisFlow are in otherFlow -> they are
             * equal!  we don't need to test again!
        // Make sure that otherFlow is contained in ThisFlow        
            for(int i = 0; i < size; i++)
                if(!this.contains(other.elements[i]))
                    return false;
             */
        
        return true;
      } else
        return super.equals(otherFlow);
    }

    public void copy(FlowSet destFlow)
    {
      if (sameType(destFlow)) {
        ArraySparseSet dest = (ArraySparseSet) destFlow;

        while(dest.maxElements < this.maxElements)
            dest.doubleCapacity();
    
        dest.numElements = this.numElements;
        
        System.arraycopy(this.elements, 0,
            dest.elements, 0, this.numElements);
      } else
        super.copy(destFlow);
    }

}
