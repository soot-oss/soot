/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * SableUtil, a clean room implementation of the Collection API.     *
 * Copyright (C) 1997, 1998 Etienne Gagnon (gagnon@sable.mcgill.ca). *
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

 - Modified on October 14, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added the elementCount method.

 - Modified on July 5, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Initial version.
*/

package soot.util;

/**
A space efficient (internal int array) implementation of the java.util.BitSet functionality.
<P>
This class is useful for sparse bit sets. In addition to the methods of BitSet, this class
provides a useful elements() method.
*/

public class IntSet
{
    private int[] elements;
    private int size;

    public IntSet()
    {
        elements = new int[0];
        size = 0;
    }

    private IntSet(IntSet set)
    {
        elements = set.elements();
        size = set.size;
    }

    private void grow()
    {
        int[] old = elements;
        elements = new int[old.length * 2 + 1];
        System.arraycopy(old, 0, elements, 0, old.length);
    }

    public void and(IntSet set)
    {
        if(set == this)
        {
            return;
        }

        int new_size = 0;
        int l = 0; int r = 0;
        while((l < size) && (r < set.size))
        {
            if(elements[l] < set.elements[r])
            {
                l++;
            }
            else if(elements[l] == set.elements[r])
            {
                elements[new_size++] = elements[l++];
                r++;
            }
            else
            {
                r++;
            }
        }

        size = new_size;
    }

    public void clear(int  bit)
    {
        if(get(bit))
        {
            for(int i = 0; i < size; i++)
            {
                if(bit < elements[i])
                {
                    elements[i - 1] = elements[i];
                }
            }

            size--;
        }
    }

    public Object clone()
    {
        return new IntSet(this);
    }

    public boolean equals(Object  obj)
    {
        if(obj == null)
        {
            return false;
        }

        if(!(obj.getClass().equals(getClass())))
        {
            return false;
        }

        IntSet set = (IntSet) obj;

        if(size != set.size)
        {
            return false;
        }

        for(int i = 0; i < size; i++)
        {
            if(elements[i] != set.elements[i])
            {
                return false;
            }
        }

        return true;
    }

    public boolean get(int  bit)
    {
        int low = 0;
        int high = size - 1;

        while(low <= high)
        {
            int middle = (low + high) / 2;

            if(bit < elements[middle])
            {
                high = middle - 1;
            }
            else if(bit == elements[middle])
            {
                return true;
            }
            else
            {
                low = middle + 1;
            }
        }

        return false;
    }

    public int hashCode()
    {
        int result = 0;

        for(int i = 0; i < size; i++)
        {
            result += elements[i];
        }

        return result;
    }

    public void or(IntSet  set)
    {
        if(set == this)
        {
            return;
        }

        int[] old = elements;
        elements = new int[size + set.size];

        int new_size = 0;
        int l = 0; int r = 0;
        while((l < size) || (r < set.size))
        {
            if((r == set.size) ||
                ((l != size) && (old[l] < set.elements[r])))
            {
                elements[new_size++] = old[l++];
            }
            else if((l == size) ||
                (old[l] > set.elements[r]))
            {
                elements[new_size++] = set.elements[r++];
            }
            else
            {
                elements[new_size++] = old[l++];
                r++;
            }
        }

        size = new_size;
    }


    public void set(int bit)
    {
        if(!get(bit))
        {
            if(++size > elements.length)
            {
                grow();
            }

            for(int i = size - 1; ; i--)
            {
                if(( i == 0) || (bit > elements[i - 1]))
                {
                    elements[i] = bit;
                    break;
                }

                elements[i] = elements[i - 1];
            }
        }
    }

    /**
    Returns the size as if it was a BitSet
    */

    public int size()
    {
        if(size == 0)
        {
            return 0;
        }

        return elements[size - 1] + 1;
    }

    public String toString()
    {
        StringBuffer s = new StringBuffer();

        s.append("{");

        boolean comma = false;

        for(int i = 0; i < size; i++)
        {
            if(comma)
            {
                s.append(", ");
            }
            else
            {
                comma = true;
            }

            s.append(elements[i]);
        }
        s.append("}");

        return s.toString();
    }

    public void xor(IntSet  set)
    {
        if(set == this)
        {
            elements = new int[0];
            size = 0;
            return;
        }

        int[] old = elements;
        elements = new int[size + set.size];

        int new_size = 0;
        int l = 0; int r = 0;
        while((l < size) || (r < set.size))
        {
            if((r == set.size) ||
                ((l != size) && (old[l] < set.elements[r])))
            {
                elements[new_size++] = old[l++];
            }
            else if((l == size) ||
                (old[l] > set.elements[r]))
            {
                elements[new_size++] = set.elements[r++];
            }
            else
            {
                l++;
                r++;
            }
        }

        size = new_size;
    }

    public int elementCount()
    {
      return size;
    }

    public int[] elements()
    {
        int[] result = new int[size];
        System.arraycopy(elements, 0, result, 0, size);
        return result;
    }
}


