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

package soot.relations;
import soot.jbuddy.*;
import java.util.*;

public class PhysicalDomain
{ 
    static {
        System.loadLibrary("jbuddy");
        JBuddy.bdd_init( 1000000, 100000 );
    }
    public PhysicalDomain( int bits ) {
        this.bits = bits;
        int[] sizes = { 1<<bits };
        var = JBuddy.fdd_extdomain( sizes, 1 );
    }
    public PhysicalDomain( int bits, String name ) {
        this(bits);
        this.name = name;
    }
    private int bits;
    private int var;
    private String name;
    public String toString() {
        if( name == null ) return super.toString();
        return name;
    }
    public int var() { return var; }
    public int ithvar( int value ) {
        return JBuddy.fdd_ithvar( var, value );
    }

    private int[] getvars() {
        int[] ret = new int[JBuddy.fdd_varnum(var())];
        JBuddy.fdd_getvars( ret, var() );
        return ret;
    }
    private static void reverse( int[] a ) {
        int i = a.length-1;
        int j = 0;
        while( j < i ) {
            int t = a[i];
            a[i] = a[j];
            a[j] = t;
            j++;
            i--;
        }
    }
    public static void setOrder( Object[] order, boolean msbAtTop ) {
        List newOrder = new ArrayList();

        for( int i = 0; i < order.length; i++ ) {
            Object o = order[i];
            if( o instanceof PhysicalDomain ) {
                PhysicalDomain pd = (PhysicalDomain) o;
                int[] vars = pd.getvars();
                if( msbAtTop ) reverse( vars );
                for( int k = 0; k < vars.length; k++ ) {
                    newOrder.add( new Integer( vars[k] ) );
                }
            } else if( o instanceof Object[] ) {
                PhysicalDomain[] domains = (PhysicalDomain[]) o;
                int[][] vars = new int[domains.length][];
                for( int j = 0; j < domains.length; j++ ) {
                    vars[j] = domains[j].getvars();
                    if( msbAtTop ) reverse( vars[j] );
                }
                boolean change = true;
                for( int j = 0; change; j++ ) {
                    change = false;
                    for( int k = 0; k < vars.length; k++ ) {
                        if( j < vars[k].length ) {
                            newOrder.add( new Integer( vars[k][j] ) );
                            change = true;
                        }
                    }
                }
            } else throw new RuntimeException();
        }
        int[] buddyOrder = new int[newOrder.size()];
        int j = 0;
        for( Iterator iIt = newOrder.iterator(); iIt.hasNext(); ) {
            final Integer i = (Integer) iIt.next();
            buddyOrder[j++] = i.intValue();
        }
        JBuddy.bdd_setvarorder(buddyOrder);
    }
}
