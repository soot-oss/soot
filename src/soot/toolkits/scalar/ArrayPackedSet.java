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

import java.util.*;


/**
 *   Reference implementation for a BoundedFlowSet. Items are stored in an Array.  
 */
public class ArrayPackedSet extends AbstractBoundedFlowSet
{
    ObjectIntMapper map;
    int[] bits;

    public ArrayPackedSet(FlowUniverse universe) {
        this(new ObjectIntMapper(universe));
    }

    ArrayPackedSet(ObjectIntMapper map)
    {
        //int size = universe.getSize();

        //int numWords = size / 32 + (((size % 32) != 0) ? 1 : 0);

        this(map, new int[map.size() / 32 + (((map.size() % 32) != 0) ? 1 : 0)]);
    }
    
    ArrayPackedSet(ObjectIntMapper map, int[] bits)
    {
        this.map = map;
        this.bits = bits.clone();
    }

    /** Returns true if flowSet is the same type of flow set as this. */
    private boolean sameType(Object flowSet)
    {
        return (flowSet instanceof ArrayPackedSet &&
                ((ArrayPackedSet)flowSet).map == map);
    }

    public ArrayPackedSet clone()
    {
        return new ArrayPackedSet(map, bits);
    }

    public Object emptySet()
    {
        return new ArrayPackedSet(map);
    }

    public int size()
    {
        int count = 0;

        for (int word : bits) {
            for(int j = 0; j < 32; j++)
                if((word & (1 << j)) != 0)
                    count++;
        }

        return count;
    }

    public boolean isEmpty()
    {
        for (int element : bits)
			if(element != 0)
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
                    elements.add(map.getObject(offset + j));
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
                            elements.add(map.getObject(offset + j));
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
                        elements.add(map.getObject(offset + j));
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
                    elements.add(map.getObject(offset + j));
        }

        return elements;
    }

    public void add(Object obj)
    {
        int bitNum = map.getInt(obj);

        bits[bitNum / 32] |= 1 << (bitNum % 32);
    }

    public void complement(FlowSet destFlow)
    {
      if (sameType(destFlow)) {
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = ~(this.bits[i]);
            
        // Clear the bits which are outside of this universe
            if(bits.length >= 1)
            {
                int lastValidBitCount = map.size() % 32;
                
                if(lastValidBitCount != 0)
                    dest.bits[bits.length - 1] &= ~(0xFFFFFFFF << lastValidBitCount);  
            }
      } else
        super.complement(destFlow);
    }

    public void remove(Object obj)
    {
        int bitNum = map.getInt(obj);

        bits[bitNum / 32] &= ~(1 << (bitNum % 32));
    }

    public void union(FlowSet otherFlow, FlowSet destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        ArrayPackedSet other = (ArrayPackedSet) otherFlow;
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        if(!(other instanceof ArrayPackedSet) || bits.length != other.bits.length)
            throw new RuntimeException("Incompatible other set for union");

        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = this.bits[i] | other.bits[i];
      } else
        super.union(otherFlow, destFlow);
    }

    public void difference(FlowSet otherFlow, FlowSet destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        ArrayPackedSet other = (ArrayPackedSet) otherFlow;
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        if(!(other instanceof ArrayPackedSet) || bits.length != other.bits.length)
            throw new RuntimeException("Incompatible other set for union");
            
        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = this.bits[i] & ~other.bits[i];
      } else
        super.difference(otherFlow, destFlow);
    }
    
    public void intersection(FlowSet otherFlow, FlowSet destFlow)
    {
      if (sameType(otherFlow) &&
          sameType(destFlow)) {
        ArrayPackedSet other = (ArrayPackedSet) otherFlow;
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        if(!(other instanceof ArrayPackedSet) || bits.length != other.bits.length)
            throw new RuntimeException("Incompatible other set for union");

        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = this.bits[i] & other.bits[i];
      } else
        super.intersection(otherFlow, destFlow);
    }

  /** Returns true, if the object is in the set.
   */
    public boolean contains(Object obj)
    {
      /* check if the object is in the map, direct call of map.getInt will
       * add the object into the map.
       */
        if (!map.contains(obj)) return false;

        int bitNum = map.getInt(obj);

        return (bits[bitNum / 32] & (1 << (bitNum % 32))) != 0;
    }

    public boolean equals(Object otherFlow)
    {
      if (sameType(otherFlow)) {
        return Arrays.equals(bits, ((ArrayPackedSet)otherFlow).bits);
      } else
        return super.equals(otherFlow);
    }

    public void copy(FlowSet destFlow)
    {
      if (sameType(destFlow)) {
        ArrayPackedSet dest = (ArrayPackedSet) destFlow;

        for(int i = 0; i < bits.length; i++)
            dest.bits[i] = this.bits[i];
      } else
        super.copy(destFlow);
    }

}

