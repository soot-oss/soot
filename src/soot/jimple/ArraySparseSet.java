/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on January 23, 1999 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   First release.
*/

package soot.jimple;

import soot.util.*;
import java.util.*;

public class ArraySparseSet implements FlowSet
{
    static final int DEFAULT_SIZE = 8; 
    
    int numElements;
    int maxElements;
    Object[] elements;

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
        elements = (Object[]) other.elements.clone();
    }
    
    public Object clone()
    {
        return new ArraySparseSet(this);
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

    public List toList()
    {
        return new SparseArrayList(elements, numElements);
    }

    public void add(Object e)
    {
        // Expand array if necessary
            if(numElements == maxElements)
                doubleCapacity();
            
        // Add element
            if(!contains(e))
                elements[numElements++] = e;
    }

    public void add(Object obj, FlowSet destFlow)
    {
        ArraySparseSet dest = (ArraySparseSet) destFlow;

        if(this != dest)
            copy(dest);

        dest.add(obj);
    }

    private void doubleCapacity()
    {        
        int newSize = maxElements * 2;
                    
        Object[] newElements = new Object[newSize];
                
        System.arraycopy(elements, 0, newElements, 0, numElements);
        elements = newElements;
        maxElements = newSize;
    }    

    public void remove(Object obj, FlowSet destFlow)
    {
        ArraySparseSet dest = (ArraySparseSet) destFlow;

        if(this != dest)
            copy(dest);

        for(int i = 0; i < this.numElements; i++)
            if(dest.elements[i].equals(obj))
            {
                dest.removeElementAt(i);
                break;
            }
    }

    private void removeElementAt(int index)
    {
        // Handle simple case
            if(index  == numElements - 1)
            {
                numElements--;
                return;
            }
            
        // Else, shift over elements
            System.arraycopy(elements, index + 1, elements, index, numElements - (index + 1));
            numElements--;
    }
    
    public void union(FlowSet otherFlow, FlowSet destFlow)
    {
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
    }

    public void intersection(FlowSet otherFlow, FlowSet destFlow)
    {
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
    }

    public void difference(FlowSet otherFlow, FlowSet destFlow)
    {
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
    }
    
    public boolean contains(Object obj)
    {
        for(int i = 0; i < numElements; i++)
            if(elements[i].equals(obj))
                return true;
                
        return false;
    }

    public boolean equals(Object otherFlow)
    {       
        ArraySparseSet other = (ArraySparseSet) otherFlow;
         
        if(other.numElements != this.numElements)
            return false;
     
        int size = this.numElements;
             
        // Make sure that thisFlow is contained in otherFlow  
            for(int i = 0; i < size; i++)
                if(!other.contains(this.elements[i]))
                    return false;

        // Make sure that otherFlow is contained in ThisFlow        
            for(int i = 0; i < size; i++)
                if(!this.contains(other.elements[i]))
                    return false;
        
        return true;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer("{");
        Iterator it = toList().iterator();

        if(it.hasNext())
        {
            buffer.append(it.next());

            while(it.hasNext())
            {
                buffer.append(", " + it.next());
            }
        }

        buffer.append("}");

        return buffer.toString();
    }

    public void copy(FlowSet destFlow)
    {
        ArraySparseSet dest = (ArraySparseSet) destFlow;

        while(dest.maxElements < this.maxElements)
            dest.doubleCapacity();
    
        dest.numElements = this.numElements;
        
        System.arraycopy(this.elements, 0,
            dest.elements, 0, this.numElements);
    }

    private static class SparseArrayList extends AbstractList 
    {
        private Object[] array;
        private int realSize;
        
        public SparseArrayList(Object[] array, int realSize)
        {
            this.array = array;
            this.realSize = realSize;
        }   
        
        public Object get(int index)
        {
            return array[index];
        }
        
        public Object set(int index, Object element)
        {
            throw new UnsupportedOperationException();
        }
        
        public int size()
        {
            return realSize;
        }
        
        public Object clone()
        {
            return array.clone();
        }
        
    }

}
