package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;
import soot.jimple.spark.PointsToSet;

/** Represents the read or write set of a statement. */
public class MethodRWSet extends RWSet {
    public Set globals;
    public Map fields;
    protected boolean callsNative = false;
    protected boolean isFull = false;
    static Set allGlobals = new HashSet();
    static Set allFields = new HashSet();
    final static PointsToSet fullObjectSet = new FullObjectSet();
    public static int MAX_SIZE = Integer.MAX_VALUE;

    static int count = 0;
    public MethodRWSet() {
	count++;
	if( 0 == (count % 1000) ) {
	    System.out.println( "Created "+count+"th MethodRWSet" );
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
	if( isFull ) return allGlobals;
	if( globals == null ) return Collections.EMPTY_SET;
	return globals;
    }

    /** Returns an iterator over any fields read/written. */
    public Set getFields() {
	if( isFull ) return allFields;
	if( fields == null ) return Collections.EMPTY_SET;
	return fields.keySet();
    }

    /** Returns a set of base objects whose field f is read/written. */
    public PointsToSet getBaseForField( Object f ) {
	if( isFull ) return fullObjectSet;
	if( fields == null ) return null;
	return (PointsToSet) fields.get( f );
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
	    for( Iterator it = other.fields.keySet().iterator(); it.hasNext(); ) {
		Object field = it.next();
		if( fields.containsKey( field ) ) {
		    if( getBaseForField( field ).hasNonEmptyIntersection(
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
		}
	    }
	    if( o.fields != null ) {
		for( Iterator it = o.fields.keySet().iterator(); it.hasNext(); ) {
		    Object field = it.next();
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
	    u = Union.factory.newUnion();
	    if( base != null) u.addAll( base );
	    fields.put( field, u );
	    if( base == null ) addedField( fields.size() );
	    ret = true;
	    if( fields.keySet().size() > MAX_SIZE ) {
		fields = null;
		isFull = true;
		return true;
	    }
	} else {
	    u = (Union) base;
	}
	ret = u.addAll( otherBase ) | ret;
	return ret;
    }
    static int fieldCount = 0;
    static void addedField( int size ) {
	fieldCount++;
	//if( 0 == ( fieldCount % 1000 ) ) System.out.println( "Added "+fieldCount+"th field" );

	if( size > 1000 && (( size & (size-1) )== 0 ) ) {
	    System.out.println( "This method has reached "+size+" fields" );
	}
    }
    public boolean isEquivTo( RWSet other ) {
	return other == this;
    }
}
