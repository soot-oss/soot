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
 The reference version is: $SootVersion$

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

 - Modified on November 19, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Fixed the toString()
   
 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.util.*;

class ArrayPackedSet implements BoundedFlowSet
{
    FlowUniverse map;
    int[] bits;

    public ArrayPackedSet(FlowUniverse universe)
    {
        //int size = universe.getSize();

        //int numWords = size / 32 + (((size % 32) != 0) ? 1 : 0);

        this(universe, new int[universe.getSize() / 32 + (((universe.getSize() % 32) != 0) ? 1 : 0)]);        
    }
    
    ArrayPackedSet(FlowUniverse map, int[] bits)
    {
        this.map = map;
        this.bits = (int[]) bits.clone();
    }

    public Object clone()
    {
        return new ArrayPackedSet(map, bits);
    }

    public int size()
    {
        int count = 0;

        for(int i = 0; i < bits.length; i++)
        {
            int word = bits[i];

            for(int j = 0; j < 32; j++)
                if((word & (1 << j)) != 0)
                    count++;
        }

        return count;
    }

    public boolean isEmpty()
    {
        for(int i = 0; i < bits.length; i++)
            if(bits[i] != 0)
                return false;

        return true;
    }

    public void clear()
    {
        for(int i = 0; i < bits.length; i++)
            bits[i] = 0;
    }


    public List toList(int low, int high)
    {
        List elements = new ArrayList();

        int startWord = low / 32,
            startBit = low % 32;

        int endWord = high / 32,
            endBit = high % 32;

        if(low > high)
            return elements;

        // Do the first word
        {
            int word = bits[startWord];

            int offset = startWord * 32;
            int lastBit = (startWord != endWord) ? 32 : (endBit + 1);

            for(int j = startBit; j < lastBit; j++)
            {
                if((word & (1 << j)) != 0)
                    elements.add(map.getObjectOf(offset + j));
            }
        }

        // Do the in between ones
            if(startWord != endWord && startWord + 1 != endWord)
            {
                for(int i = startWord + 1; i < endWord; i++)
                {
                    int word = bits[i];
                    int offset = i * 32;

                    for(int j = 0; j < 32; j++)
                    {
                        if((word & (1 << j)) != 0)
                            elements.add(map.getObjectOf(offset + j));
                    }
                }
            }

        // Do the last one
            if(startWord != endWord)
            {
                int word = bits[endWord];
                int offset = endWord * 32;
                int lastBit = endBit + 1;

                for(int j = 0; j < lastBit; j++)
                {
                    if((word & (1 << j)) != 0)
                        elements.add(map.getObjectOf(offset + j));
                }
            }

        return elements;
    }


    public List toList()
    {
        List elements = new ArrayList();

        for(int i = 0; i < bits.length; i++)
        {
            int word = bits[i];
            int offset = i * 32;

            for(int j = 0; j < 32; j++)
                if((word & (1 << j)) != 0)
                    elements.add(map.getObjectOf(offset + j));
        }

        return elements;
    }

    public void add(Object obj, FlowSet destFlow)
    {
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        if(this != dest)
            copy(dest);

        int bitNum = map.getIndexOf(obj);

        dest.bits[bitNum / 32] |= 1 << (bitNum % 32);
    }

    public void complement(FlowSet destFlow)
    {
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = ~(this.bits[i]);
            
        // Clear the bits which are outside of this universe
            if(bits.length >= 1)
            {
                int lastValidBitCount = map.getSize() % 32;
                
                if(lastValidBitCount != 0)
                    dest.bits[bits.length - 1] &= ~(0xFFFFFFFF << lastValidBitCount);  
            }
    }

    public void remove(Object obj, FlowSet destFlow)
    {
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        if(this != dest)
            copy(dest);

        int bitNum = map.getIndexOf(obj);

        dest.bits[bitNum / 32] &= ~(1 << (bitNum % 32));
    }

    public void union(FlowSet otherFlow, FlowSet destFlow)
    {
        ArrayPackedSet other = (ArrayPackedSet) otherFlow;
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        if(!(other instanceof ArrayPackedSet) || bits.length != other.bits.length)
            throw new RuntimeException("Incompatible other set for union");

        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = this.bits[i] | other.bits[i];
    }

    public void difference(FlowSet otherFlow, FlowSet destFlow)
    {
        ArrayPackedSet other = (ArrayPackedSet) otherFlow;
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        if(!(other instanceof ArrayPackedSet) || bits.length != other.bits.length)
            throw new RuntimeException("Incompatible other set for union");
            
        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = this.bits[i] & ~other.bits[i];
    }
    
    public void intersection(FlowSet otherFlow, FlowSet destFlow)
    {
        ArrayPackedSet other = (ArrayPackedSet) otherFlow;
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        if(!(other instanceof ArrayPackedSet) || bits.length != other.bits.length)
            throw new RuntimeException("Incompatible other set for union");

        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = this.bits[i] & other.bits[i];
    }

    public boolean contains(Object obj)
    {
        int bitNum = map.getIndexOf(obj);

        return (bits[bitNum / 32] & (1 << (bitNum % 32))) != 0;
    }

    public boolean equals(Object otherFlow)
    {
        ArrayPackedSet other = (ArrayPackedSet) otherFlow;

        for(int i = 0; i < bits.length; i++)
            if(this.bits[i] != other.bits[i])
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
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = this.bits[i];
    }

}

