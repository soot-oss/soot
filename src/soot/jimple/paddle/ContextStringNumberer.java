/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

package soot.jimple.paddle;
import java.util.*;
import soot.*;
import soot.util.*;

/** Assigns a unique integer to a ContextString.
 * @author Ondrej Lhotak
 */
public class ContextStringNumberer implements Numberer
{ 
    public final static int SHIFT_WIDTH = 14;
    private final static long MAX_ITEM = 1L<<SHIFT_WIDTH;
    private Numberer contextNumberer;
    private final int k;
    private final long nullNum;
    public ContextStringNumberer( Numberer contextNumberer, int k ) {
        this.contextNumberer = contextNumberer;
        this.k = k;
        nullNum = 1L<<(k*SHIFT_WIDTH);
    }
    public void add( Object o ) {
    }
    public long get( Object o ) {
        if( o == null ) return nullNum;
        ContextString cs = (ContextString) o;
        int ret = 0;
        for( int i = k-1; i >= 0; i-- ) {
            long num = contextNumberer.get(cs.get(i));
            if( num >= MAX_ITEM ) throw new RuntimeException( "Need to increase SHIFT_WIDTH" );
            ret <<= SHIFT_WIDTH;
            ret += num;
        }
        return ret;
    }
    public Object get( long num ) {
        if( num == nullNum ) return null;
        Context[] ret = new Context[k];
        for( int i = 0; i < k; i++ ) {
            ret[i] = (Context) contextNumberer.get(num & MAX_ITEM-1);
            num >>>= SHIFT_WIDTH;
        }
        return new ContextString(ret);
    }
    public int size() {
        int ret = 1;
        for( int i = 0; i < k; i++ ) {
            ret *= contextNumberer.size();
        }
        return ret;
    }
}

