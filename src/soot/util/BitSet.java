/*
 * Java core library component.
 *
 * Copyright (c) 1997, 1998
 *      Transvirtual Technologies, Inc.  All rights reserved.
 *
 * See the file "license.terms" for information on usage and redistribution
 * of this file.
 *
 * @author Edouard G. Parmelan <edouard.parmelan@quadratec.fr>
 */

/* Modified by Felix Kwok in 2001. This implementation is based on
 * the one in Kaffe 1.0.6, except I have removed synchronization and
 * implemented an efficient enumerator for sparse bit sets.
 */

package soot.util;

import java.lang.String;
import java.lang.System;
import java.io.Serializable;

/** 
 * A BitSet implementation based on Kaffe 1.0.6. The API is identical except
 * for the addition of an efficient iterator (see also BitSetIterator). 
 * Also, unlike the Kaffe BitSet, this BitSet is unsynchronized (for efficiency
 * purposes.
 */ 
public final class BitSet
    implements Cloneable, Serializable
{
    private static final long serialVersionUID = 7997698588986878753L;

    /**
     * The bits in this BitSet.
     * @serial The ith bit is stored in bits[i/64] (aka bits[i>>6])
     * at bit position (i%64) (aka i & 0x3F).
     */
    private long bits[];

    /** Returns an iterator over this BitSet. */
    public BitSetIterator iterator() {
        return new BitSetIterator(bits);
    }

    /**
     * return the offset in bits[].
     */
    private static int bitOffset(int bit) {
	return bit >> 6;
    }

    /**
     * return the mask of the bit in bits[bitOffset(bit)].
     */
    private static long bitMask(int bit) {
	return 1L << (bit & 0x3F);
    }

    /**
     * ensure bits can hold nr bit.
     */
    private void ensureSize(int nr) {
	int len = bitOffset(nr + 0x3F);
	if (len > bits.length) {
	    if (len < 2 * bits.length) {
		// at least double it to avoid frequent allocations
		len = 2 * bits.length;
	    }
	    long b[] = new long[len];
	    System.arraycopy(bits, 0, b, 0, bits.length);
	    bits = b;
	}
    }


    /**
     * Create a new BitSet.
     */
    public BitSet() {
	this(64);
    }


    /**
     * Create a new BitSet that can hold nr bits.
     * All bits are initialized to false.
     *
     * @param nr the inital size of the set.
     */
    public BitSet(int nr) {
	if (nr < 0)
	    throw new NegativeArraySizeException();
	bits = new long[bitOffset(nr + 0x3F)];
    }


    /**
     * Set a bit in the set.
     * @param bit the bit to set.
     */
    public boolean set(int bit) {
	if (bit < 0)
	    throw new IndexOutOfBoundsException();
	ensureSize(bit+1);
        int offset = bitOffset( bit );
	long l = bits[offset];
        long mask = bitMask(bit);
        bits[ offset ] = l | mask;
        return (l & mask) == 0;
    }

    /**
     * Clear a bit in the set.
     * @param bit the bit to clear.
     */
    public void clear(int bit) {
	if (bit < 0)
	    throw new IndexOutOfBoundsException();
	ensureSize(bit+1);
	bits[bitOffset(bit)] &= ~bitMask(bit);
    }

    /**
     * Returns the value of the bit in the set.
     * @param bit the bit to check.
     * @return the value of the bit.
     */
    public boolean get(int bit) {
	if (bit < 0)
	    throw new IndexOutOfBoundsException();
	int k = bitOffset(bit);
	if (k < bits.length)
	    return ((bits[k] & bitMask(bit)) != 0);
	else
	    return false;
    }


    /**
     * Returns the index of the highest set bit in the set plus one.
     *
     * @return the logical size of this set.
     * @since JDK1.2
     */
    public int length() {
	long b[] = bits;
	int n = b.length - 1;
	while ((n >= 0) && (b[n] == 0L))
	    n--;
	if (n < 0)
	    return 0;

	long m = b[n];
	long k = 1L << 63;
	n = (n + 1) << 6;

	// k never 0 because m != 0
	while ((m & k) == 0L) {
	    n--;
	    k >>= 1;
	}
	return n;
    }


    /**
     * @return the number of bits in the set.
     */
    public int size() {
	return bits.length << 6;
    }


    /**
     * Compares this object againts the specified object.
     * The result is true if and only if the argument is not null and is
     * a BitSet object that has exactly the same set of bits set to true.
     * The current sizes of the two sets are not compared.
     * @return true if the objects are the same; false otherwise.
     */
    public boolean equals(Object obj) {
	if (obj == this)
	    return true;
	if (!(obj instanceof BitSet))
	    return false;

        long a[] = bits;
        long b[] = ((BitSet)obj).bits;
        
        // Check commun bits
        int nr = Math.min(a.length, b.length);
        for (int i = nr; i-- > 0; ) {
            if (a[i] != b[i])
                return false;
        }

        // Check upper bits
        if (nr != b.length)
            a = b;
        else if (nr == a.length)
            return true;
        for(int i = a.length; i-- > nr; ) {
            if (a[i] != 0L)
                return false;
        }
        return true;
    }


    /**
     * Performs a logical AND of this set and the argument bit set.
     * Result of the mathematical intersection of two sets.
     * @param set a bit set.
     */
    public void and(BitSet bitset) {
	if (this == bitset)
	    return;

        // a = a and b
        long a[] = bits;
        long b[] = bitset.bits;
        
        int nr = Math.min(a.length, b.length);
        for (int i = nr; i-- > 0; )
            a[i] &= b[i];
        // clear upper bits
        for (int i = a.length; i-- > nr; )
            a[i] = 0;
    }


    /**
     * Performs a logical AND of this set and the COMPLEMENT of the argument
     * bit set.
     * Result of the mathematical subtraction of two sets.
     * @param set a bit set.
     * @since JDK1.2
     */
    public void andNot(BitSet bitset) {

	if(this == bitset) {
            long a[] = bits;
            for (int i = a.length; i-- > 0; )
                a[i] = 0;
	    return;
        }

        // a = a and (not b)
        long a[] = bits;
        long b[] = bitset.bits;
        
        int nr = Math.min(a.length, b.length);
        for (int i = nr; i-- > 0; )
            a[i] &= ~b[i];
        // don't nead to check upper bits
    }




    /**
     * Performs a logical OR of this set and the argument bit set.
     * Result of the mathematical union of two sets.
     * @param set a bit set.
     */
    public void or(BitSet bitset) {
	if (this == bitset)
	    return;

        // a = a or b
        long a[] = bits;
        long b[] = bitset.bits;
        
        //System.out.println(a.length+" - "+b.length);
        
        int nr = Math.min(a.length, b.length);
        for (int i = nr; i-- > 0; ) {
            a[i] |= b[i];
        }
        // Set upper bits
        if (a.length < b.length) {
            // grow only for set bits
            int blength = bitset.length();
            //System.out.println(blength);
            ensureSize(blength);
            //System.out.println(blength >> 6);
            a = bits;
            //System.out.println(a.length);
            for (int i = ((blength-1) >> 6); i >= nr; i--) {
                //System.out.println(i);
                a[i] = b[i];
            }
        }
    }


    /**
     * Computes this = this OR ((orset AND andset ) AND (NOT andnotset))
     * Returns true iff this is modified.
     * Result of the mathematical union of two sets.
     * @param set a bit set.
     */
    public boolean orAndAndNot(BitSet orset, BitSet andset, BitSet andnotset) {
        boolean ret = false;
        long[] a = null, b = null, c = null, d = null, e = null;
        int al, bl, cl, dl, el;
        a = this.bits;
        al = a.length;
        if( orset == null ) {
            bl = 0;
        } else {
            b = orset.bits;
            bl = b.length;
        }
        if( andset == null ) {
            cl = 0;
        } else {
            c = andset.bits;
            cl = c.length;
        }
        if( andnotset == null ) {
            dl = 0;
        } else {
            d = andnotset.bits;
            dl = d.length;
        }

        if( al < bl ) {
            e = new long[bl];
            System.arraycopy( a, 0, e, 0, al );
            this.bits = e;
        } else {
            e = a;
        }
        el = e.length;

        // INV: el >= bl

        int i = 0;
        long l;

        if( bl <= cl && bl <= dl ) {
            while( i < bl ) {
                l = b[i] & c[i] & ~d[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
        } else if( cl <= dl && dl <= bl ) {
            while( i < cl ) {
                l = b[i] & c[i] & ~d[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
            while( i < dl ) {
                l = b[i] & ~d[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
            while( i < bl ) {
                l = b[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
        } else if( dl <= cl && cl <= bl ) {
            while( i < dl ) {
                l = b[i] & c[i] & ~d[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
            while( i < cl ) {
                l = b[i] & c[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
            while( i < bl ) {
                l = b[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
        } else if( dl <= bl && bl <= cl ) {
            while( i < dl ) {
                l = b[i] & c[i] & ~d[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
            while( i < bl ) {
                l = b[i] & c[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
        } else if( cl <= bl && bl <= dl ) {
            while( i < cl ) {
                l = b[i] & c[i] & ~d[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
            while( i < bl ) {
                l = b[i] & ~d[i];
                if( (l & ~e[i]) != 0 ) ret = true;
                e[i] |= l;
                i++;
            }
        } else throw new RuntimeException( "oops bl="+bl+" cl="+cl+" dl="+dl );

        return ret;
    }


    /**
     * Performs a logical EXCLUSIVE OR of this set and the argument bit set.
     *
     * @param set a bit set.
     */
    public void xor(BitSet bitset) {
	if(this == bitset) {
            long a[] = bits;
            for (int i = a.length; i-- > 0; )
                a[i] = 0;
	    return;
        }

        // a = a xor b
        long a[] = bits;
        long b[] = bitset.bits;
        
        int nr = Math.min(a.length, b.length);
        for (int i = nr; i-- > 0; )
            a[i] ^= b[i];
        // Set upper bits,  X ^ 0 == 0 ^ X == X
        if (a.length < b.length) {
            // grow only for set bits
            ensureSize(bitset.length());
            a = bits;
            for (int i = bits.length; i-- > nr; )
                a[i] = b[i];
        }
    }


    /**
     * Returns a hash code value for this bit set.
     */
    public int hashCode() {
	long h = 1234L;
	for (int i = bits.length; --i >= 0; ) {
	    h ^= bits[i] * (i + 1);
	}
	return (int)((h >> 32) ^ h);
    }


    /**
     * Cloning this BitSet produces a new BitSet that is equal to it.
     * The clone of the bit set is another bit set that has exactly the same
     * bits set to true as this bit set and the same current size.
     */
    public Object clone() {
	BitSet bitset;
	try {
	    bitset = (BitSet)super.clone();
	}
	catch (CloneNotSupportedException _) {
	    return null;
	}
	bitset.bits = new long[bits.length];
	// if clone could reduce the size: copy up to last set bit
	// bitset.bits = new long[bitOffset(length() + 0x3F)];
	System.arraycopy(bits, 0, bitset.bits, 0, bitset.bits.length);
	return bitset;
    }


    /**
     * Returns a string representation of this bit set.
     * For every index for which this BitSet contains a bit in the set state,
     * the decimal representation of that index is included in the result.
     * Such indeces are listed in order from lowest to highest, separated by
     * ",&nbsp;" (a comma and a space) and surrounded by braces, resulting in
     * the usual mathematical notation for a set of integers.
     */
    public String toString() {
	int bit;
	int nbits = length();
	boolean first = true;
	StringBuffer sb = new StringBuffer();
	sb.append('{');
	for (bit = 0; bit < nbits; bit++) {
	    if (get(bit)) {
		if (!first)
		    sb.append(", ");
		else
		    first = false;
		sb.append(bit);
	    }
	}
	sb.append('}');
	return sb.toString();
    }
}
