/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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
import java.util.*;

/** A java.util.Map-like map with Numberable objects as the keys.
 * This one is designed for maps close to the size of the universe.
 * For smaller maps, use SmallNumberedMap.
 *
 * @author Ondrej Lhotak
 */

public final class LargeNumberedMap {
    public LargeNumberedMap( ArrayNumberer universe ) {
        this.universe = universe;
        int newsize = universe.size();
        if( newsize < 8 ) newsize = 8;
        values = new Object[newsize];
    }
    public boolean put( Numberable key, Object value ) {
        int number = key.getNumber();
        if( number == 0 ) throw new RuntimeException( "oops, forgot to initialize" );
        if( number >= values.length ) {
            Object[] oldValues = values;
            values = new Object[ universe.size()*2+5 ];
            System.arraycopy(oldValues,0,values,0,oldValues.length);
        }
        boolean ret = ( values[number] != value );
        values[number] = value;
        return ret;
    }
    public Object get( Numberable key ) {
        int i = key.getNumber();
        if( i >= values.length ) return null;
        return values[ i ];
    }

    /* Private stuff. */

    private Object[] values;
    private long[] bits;
    private int size = 0;
    private ArrayNumberer universe;
}
