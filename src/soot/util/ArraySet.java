/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * SableUtil, a clean room implementation of the Collection API.     *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca).   *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca). All   *
 * rights reserved.                                                  *
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
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $SableUtilVersion: 1.11 $

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

 - Modified on May 11, 1999 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Moved the class Array into this class.
   
 - Modified on July 23, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added toArray(Object[]).

 - Modified on June 15, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First release of this file.
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
