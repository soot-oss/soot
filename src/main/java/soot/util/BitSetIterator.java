/* Soot - a J*va Optimization Framework
 * Copyright (C) 2001 Felix Kwok
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

package soot.util;

import java.util.*;


/** A fast enumerator for sparse bit sets. When the enumerator is
 *  created, it takes a snapshot of the underlying BitVector, and iterates
 *  through the set bits. Note that this class almost implements the
 *  Iterator interface, but it doesn't because the return type of next
 *  is int rather than Object. */
public class BitSetIterator {

    long[] bits;    // Bits inherited from the underlying BitVector
    int index;      // The 64-bit block currently being examined
    long save = 0;  // A copy of the 64-bit block (for fast access)

    /* Computes log_2(x) modulo 67. This uses the fact that 2 is a
     * primitive root modulo 67 */
    final static int[] lookup = {-1, 0, 1, 39, 2, 15, 40, 23, 3, 12, 16,
                                 59, 41, 19, 24, 54,  4, -1, 13, 10, 17,
                                 62, 60, 28, 42, 30, 20, 51, 25, 44, 55,
                                 47,  5, 32, -1, 38, 14, 22, 11, 58, 18,
                                 53, -1,  9, 61, 27, 29, 50, 43, 46, 31,
                                 37, 21, 57, 52,  8, 26, 49, 45, 36, 56,
                                 7, 48, 35, 6, 34, 33};

    /** Creates a new BitSetIterator */
    BitSetIterator(long[] bits) {
	//this.bits = new long[bits.length];
	//System.arraycopy(bits,0,this.bits,0,bits.length);
        this.bits = bits;
	index = 0;

        /* Zip through empty blocks */
	while (index < bits.length && bits[index]==0L)
            index++;
        if (index < bits.length)
            save = bits[index];
    }

    /** Returns true if there are more set bits in the BitVector; false otherwise. */
    public boolean hasNext() {
	return index < bits.length;
    }

    /** Returns the index of the next set bit. Note that the return type is int,
     *  and not Object. */
    public int next() {
        if (index >= bits.length)
            throw new NoSuchElementException();

        long k = (save & (save-1)); // Clears the last non-zero bit. save is guaranteed non-zero.
        long diff = save ^ k;       // Finds out which bit it is. diff has exactly one bit set.
        save = k;

        // Computes the position of the set bit.
        int result = (diff < 0) ? 64 * index + 63 : 64 * index + lookup[(int)(diff%67)];

        if (save == 0) {
            index++;
            while (index < bits.length && bits[index]==0L)
                index++;
            if (index < bits.length)
                save = bits[index];
        }
        return result;
    }
}
