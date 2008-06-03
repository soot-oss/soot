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
public class MethodRWSet extends RWSet {
    public Set globals;
    public Map<Object,PointsToSet> fields;
    protected boolean callsNative = false;
    protected boolean isFull = false;
    public static final int MAX_SIZE = Integer.MAX_VALUE;

    public String toString() {
        boolean empty = true;
        StringBuffer ret = new StringBuffer();
        if( fields != null ) {
            for (Object element : fields.keySet()) {
                final Object field = element;
                ret.append( "[Field: "+field+" "+fields.get(field)+"]\n" );
                empty = false;
            }
        }
        if( globals != null ) {
            for( Iterator globalIt = globals.iterator(); globalIt.hasNext(); ) {
                final Object global = globalIt.next();
                ret.append( "[Global: "+global+"]\n" );
                empty = false;
            }
        }
        if(empty) ret.append("empty");
        return ret.toString();
    }

   	public int size()
	{
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

    //static int count = 0;
    public MethodRWSet() {
        /*
	count++;
	if( 0 == (count % 1000) ) {
	    G.v().out.println( "Created "+count+"th MethodRWSet" );
	}
        */
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
	if( isFull ) return G.v().MethodRWSet_allGlobals;
	if( globals == null ) return Collections.EMPTY_SET;
	return globals;
    }

    /** Returns an iterator over any fields read/written. */
    public Set getFields() {
	if( isFull ) return G.v().MethodRWSet_allFields;
	if( fields == null ) return Collections.EMPTY_SET;
	return fields.keySet();
    }

    /** Returns a set of base objects whose field f is read/written. */
    public PointsToSet getBaseForField( Object f ) {
	if( isFull ) return FullObjectSet.v();
	if( fields == null ) return null;
	return fields.get( f );
    }

    public boolean hasNonEmptyIntersection( RWSet oth ) {
	if( isFull ) return oth != null;
	if( !(oth instanceof MethodRWSet) ) {
	    return oth.hasNonEmptyIntersection( this );
	}
	MethodRWSet other = (MethodRWSet) oth;
	if( globals != null && other.globals != null
		&& !globals.isEmpty() && !other.globals.isEmpty() ) {
	    for( Iterator it = other.globals.iterator(); it.hasNext(); ) {
		if( globals.contains( it.next() ) ) return true;
	    }
	}
	if( fields != null && other.fields != null
		&& !fields.isEmpty() && !other.fields.isEmpty() ) {
	    for (Object element : other.fields.keySet()) {
	        final Object field = element;
		if( fields.containsKey( field ) ) {
		    if( Union.hasNonEmptyIntersection(
                                getBaseForField( field ),
				other.getBaseForField( field ) ) ) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    /** Adds the RWSet other into this set. */
    public boolean union( RWSet other ) {
	if( other == null ) return false;
	if( isFull ) return false;
	boolean ret = false;
	if( other instanceof MethodRWSet ) {
	    MethodRWSet o = (MethodRWSet) other;
	    if( o.getCallsNative() ) {
		ret = !getCallsNative() | ret;
		setCallsNative();
	    }
	    if( o.isFull ) {
		ret = !isFull | ret;
		isFull = true;
                if( true ) throw new RuntimeException( "attempt to add full set "+o+" into "+this );
		globals = null;
		fields = null;
		return ret;
	    }
	    if( o.globals != null ) {
		if( globals == null ) globals = new HashSet();
		ret = globals.addAll( o.globals ) | ret;
		if( globals.size() > MAX_SIZE ) {
		    globals = null;
		    isFull = true;
                throw new RuntimeException( "attempt to add full set "+o+" into "+this );
		}
	    }
	    if( o.fields != null ) {
		for (Object element : o.fields.keySet()) {
		    final Object field = element;
		    PointsToSet os = o.getBaseForField( field );
		    ret = addFieldRef( os, field ) | ret;
		}
	    }
	} else {
	    StmtRWSet oth = (StmtRWSet) other;
	    if( oth.base != null ) {
		ret = addFieldRef( oth.base, oth.field ) | ret;
	    } else if( oth.field != null ) {
		ret = addGlobal( (SootField) oth.field ) | ret;
	    }
	}
	if( !getCallsNative() && other.getCallsNative() ) {
	    setCallsNative();
	    return true;
	}
	return ret;
    }

    public boolean addGlobal( SootField global ) {
	if( globals == null ) globals = new HashSet();
	boolean ret = globals.add( global );
	if( globals.size() > MAX_SIZE ) {
	    globals = null;
	    isFull = true;
                throw new RuntimeException( "attempt to add more than "+MAX_SIZE+" globals into "+this );
	}
	return ret;
    }
    public boolean addFieldRef( PointsToSet otherBase, Object field ) {
	boolean ret = false;
	if( fields == null ) fields = new HashMap();
	PointsToSet base = getBaseForField( field );
	if( base instanceof FullObjectSet ) return false;
	if( otherBase instanceof FullObjectSet ) {
	    fields.put( field, otherBase );
	    return true;
	}
	if( otherBase.equals( base ) ) return false;
	Union u;
	if( base == null || !(base instanceof Union) ) {
	    u = G.v().Union_factory.newUnion();
	    if( base != null) u.addAll( base );
	    fields.put( field, u );
	    if( base == null ) addedField( fields.size() );
	    ret = true;
	    if( fields.keySet().size() > MAX_SIZE ) {
		fields = null;
		isFull = true;
            if( true )    throw new RuntimeException( "attempt to add more than "+MAX_SIZE+" fields into "+this );
		return true;
	    }
	} else {
	    u = (Union) base;
	}
	ret = u.addAll( otherBase ) | ret;
	return ret;
    }
    static void addedField( int size ) {
    }
    public boolean isEquivTo( RWSet other ) {
	return other == this;
    }
}
