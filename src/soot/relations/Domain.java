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
import soot.util.*;

public class Domain
{ 
    public Domain( Numberer numberer ) {
        this.numberer = numberer;
    }
    public Domain( Numberer numberer, String name ) {
        this(numberer);
        this.name = name;
    }
    private Numberer numberer;
    public Numberer numberer() { return numberer; }
    private String name;
    public String toString() { 
        if( name == null ) return super.toString();
        return name;
    }

    public static String toString( Object[] domains ) {
        StringBuffer b = new StringBuffer();
        b.append( "[ ");
        for( int k = 0; k < domains.length; k++ ) {
            if( k != 0 ) b.append( ", " );
            b.append( domains[k].toString() );
        }
        b.append( " ]");
        return b.toString();
    }

    public static Domain[] box( Domain d1 ) {
        Domain[] ret = { d1 };
        return ret;
    }
    public static Domain[] box( Domain d1, Domain d2 ) {
        Domain[] ret = { d1, d2 };
        return ret;
    }
    public static Domain[] box( Domain d1, Domain d2, Domain d3 ) {
        Domain[] ret = { d1, d2, d3 };
        return ret;
    }
}
