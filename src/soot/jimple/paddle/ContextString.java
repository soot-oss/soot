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

/** Represents a string of Contexts of maximum length k.
 * @author Ondrej Lhotak
 */
public class ContextString implements Context
{ 
    private Context[] string;
    public ContextString( Context[] string ) {
        this.string = string;
    }
    public ContextString( int k ) {
        string = new Context[k];
    }
    public Context get( int i ) {
        return string[i];
    }
    public int k() {
        return string.length;
    }
    public ContextString push( Context c ) {
        ContextString ret = new ContextString(string.length);
        for( int i = string.length-1; i > 0; i-- ) {
            ret.string[i] = string[i-1];
        }
        ret.string[0] = c;
        return ret;
    }
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append( "( " );
        for( int i = string.length-1; i >= 0; i-- ) {
            ret.append( string[i] );
            if( i > 0 ) ret.append(", ");
        }
        ret.append( " )" );
        return ret.toString();
    }
    public int hashCode() {
        int ret = 0;
        for( int i = 0; i < string.length; i++ ) {
            if( string[i] != null ) ret += string[i].hashCode();
        }
        return ret;
    }
    public boolean equals(Object o) {
        if( !(o instanceof ContextString) ) return false;
        ContextString other = (ContextString) o;
        if( other.string.length != string.length ) return false;
        for( int i = 0; i < string.length; i++ ) {
            if( string[i] == null ) {
                if( other.string[i] != null ) return false;
            } else {
                if( !string[i].equals( other.string[i] ) ) return false;
            }
        }
        return true;
    }
}

