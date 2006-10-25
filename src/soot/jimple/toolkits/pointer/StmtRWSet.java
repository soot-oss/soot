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

package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;

/** Represents the read or write set of a statement. */
public class StmtRWSet extends RWSet {
    protected Object field;
    protected PointsToSet base;
    protected boolean callsNative = false;

    public String toString() {
        return "[Field: "+field+base+"]\n";
    }

   	public int size()
	{
		Set globals = getGlobals();
		Set fields = getFields();
		if(globals == null)
		{
			if(fields == null)
				return 0;
			else
				return fields.size();
		}
		else
		{
			if(fields == null)
				return globals.size();
			else
				return globals.size() + fields.size();
		}
	}

    public boolean getCallsNative() {
	return callsNative;
    }

    public boolean setCallsNative() {
	boolean ret = !callsNative;
	callsNative = true;
	return ret;
    }

    /** Returns an iterator over any globals read/written. */
    public Set getGlobals() {
	if( base == null ) {
	    HashSet ret = new HashSet();
	    ret.add( field );
	    return ret;
	}
	return Collections.EMPTY_SET;
    }

    /** Returns an iterator over any fields read/written. */
    public Set getFields() {
	if( base != null ) {
	    HashSet ret = new HashSet();
	    ret.add( field );
	    return ret;
	}
	return Collections.EMPTY_SET;
    }

    /** Returns a set of base objects whose field f is read/written. */
    public PointsToSet getBaseForField( Object f ) {
	if( field.equals( f ) ) return base;
	return null;
    }

    public boolean hasNonEmptyIntersection( RWSet other ) {
	if( field == null ) return false;
	if( other instanceof StmtRWSet ) {
	    StmtRWSet o = (StmtRWSet) other;
	    if( !field.equals( o.field ) ) return false;
	    if( base == null ) return o.base == null;
	    return Union.hasNonEmptyIntersection( base, o.base );
	} else if( other instanceof MethodRWSet ) {
	    MethodRWSet o = (MethodRWSet) other;
	    if( base == null ) return other.getGlobals().contains( field );
	    return Union.hasNonEmptyIntersection( base,
                    other.getBaseForField( field ) );
	} else {
	    return other.hasNonEmptyIntersection( this );
	}
    }

    /** Adds the RWSet other into this set. */
    public boolean union( RWSet other ) {
	throw new RuntimeException( "Can't do that" );
    }

    public boolean addGlobal( SootField global ) {
	if( field != null || base != null ) 
	    throw new RuntimeException( "Can't do that" );
	field = global;
	return true;
    }
    public boolean addFieldRef( PointsToSet otherBase, Object field ) {
	if( this.field != null || base != null ) 
	    throw new RuntimeException( "Can't do that" );
	this.field = field;
	base = otherBase;
	return true;
    }
    public boolean isEquivTo( RWSet other ) {
	if( !( other instanceof StmtRWSet ) ) return false;
	StmtRWSet o = (StmtRWSet) other;
	if( callsNative != o.callsNative ) return false;
	if( !field.equals( o.field ) ) return false;
	if( base instanceof FullObjectSet && o.base instanceof FullObjectSet ) return true;
	if( base != o.base ) return false;
	return true;
    }
}
