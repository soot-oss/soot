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

public class PhysicalDomain
{ 
    static {
        System.loadLibrary("jbuddy");
        JBuddy.bdd_init( 10000, 10000 );
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
}
