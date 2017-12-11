/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.util;
import soot.*;

public final class SharedBitSetCache {
    public SharedBitSetCache( Singletons.Global g ) {}
    public static SharedBitSetCache v() { return G.v().soot_util_SharedBitSetCache(); }

    public static final int size = 32749; // a nice prime about 32k

    public BitVector[] cache = new BitVector[size];
    public BitVector[] orAndAndNotCache = new BitVector[size];

    public BitVector canonicalize( BitVector set ) {
        int hash = set.hashCode();
        if( hash < 0 ) hash = -hash;
        hash %= size;
        if( cache[hash] == null || !cache[hash].equals(set) ) {
            return cache[hash] = set;
        }
        return cache[hash];
    }
}
