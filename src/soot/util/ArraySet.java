/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */






package soot.util;

import java.util.*;

/**
 * Provides an implementation of the Set object using java.util.Array
 */

public class ArraySet extends AbstractSet
{
    private static final int DEFAULT_SIZE = 8;

    private int numElements;
    private int maxElements;
    private Object[] elements;

    public ArraySet()
    {
        maxElements = DEFAULT_SIZE;
        elements = new Object[DEFAULT_SIZE];
        numElements = 0;
    }

    /**
     * Create a set which contains the given elements.
     */

    public ArraySet(Object[] elements)
    {
        this();

        for(int i = 0; i < elements.length; i++)
            add(elements[i]);
    }

    public void clear()
    {
        numElements = 0;
    }

    public boolean contains(Object obj)
    {
        for(int i = 0; i < numElements; i++)
            if(elements[i].equals(obj))
                return true;

        return false;
    }

    public boolean add(Object e)
    {
        if(contains(e))
            return false;
        else
        {
            // Expand array if necessary
                if(numElements == maxElements)
                    doubleCapacity();

            // Add element
                elements[numElements++] = e;
                return true;
        }
    }

    public int size()
    {
        return numElements;
    }

    public Iterator iterator()
    {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator
    {
        int nextIndex;

        ArrayIterator()
        {
            nextIndex = 0;
        }

        public boolean hasNext()
        {
            return nextIndex < numElements;
        }

        public Object next() throws NoSuchElementException
        {
            if(!(nextIndex < numElements))
                throw new NoSuchElementException();

            return elements[nextIndex++];
        }

        public void remove() throws NoSuchElementException
        {
            if(nextIndex == 0)
                throw new NoSuchElementException();
            else
            {
                removeElementAt(nextIndex - 1);
                nextIndex = nextIndex - 1;
            }
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


    private void doubleCapacity()
    {
        int newSize = maxElements * 2;

        Object[] newElements = new Object[newSize];

        System.arraycopy(elements, 0, newElements, 0, numElements);
        elements = newElements;
        maxElements = newSize;
    }

    public Object[] toArray()
    {
        Object[] array = new Object[numElements];

        System.arraycopy(elements, 0, array, 0, numElements);
        return array;
    }

    class Array
    {
        private final int DEFAULT_SIZE = 8;
    
        private int numElements;
        private int maxElements;
        private Object[] elements;
    
        public void clear()
        {
            numElements = 0;
        }
    
        public Array()
        {
            elements = new Object[DEFAULT_SIZE];
            maxElements = DEFAULT_SIZE;
            numElements = 0;
        }
    
        private void doubleCapacity()
        {
            int newSize = maxElements * 2;
    
            Object[] newElements = new Object[newSize];
    
            System.arraycopy(elements, 0, newElements, 0, numElements);
            elements = newElements;
            maxElements = newSize;
        }
    
        public void addElement(Object e)
        {
            // Expand array if necessary
                if(numElements == maxElements)
                    doubleCapacity();
    
            // Add element
                elements[numElements++] = e;
        }
    
        public void insertElementAt(Object e, int index)
        {
            // Expaxpand array if necessary
                if(numElements == maxElements)
                    doubleCapacity();
    
            // Handle simple case
                if(index == numElements)
                {
                    elements[numElements++] = e;
                    return;
                }
    
            // Shift things over
                System.arraycopy(elements, index, elements, index + 1, numElements - index);
                elements[index] = e;
                numElements++;
        }
    
        public boolean contains(Object e)
        {
            for(int i = 0; i < numElements; i++)
                if(elements[i].equals(e))
                    return true;
    
            return false;
        }
    
        public int size()
        {
            return numElements;
        }
    
        public Object elementAt(int index)
        {
            return elements[index];
        }
    
        public void removeElementAt(int index)
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
    }
}
