package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import soot.*;
import java.util.*;
import soot.jimple.spark.PointsToSet;

public abstract class Ras implements PointsToSet
{ 
    /** Leave this null for a type-insensitive analysis. */
    static FastHierarchy fh;
    public static RasFactory factory;

    Type type;
    protected Ras( Type t ) {
	if( t != null && !( t instanceof RefLikeType ) ) {
	    throw new RuntimeException( "Attempt to create RAS of type "+t );
	}
	this.type = t;
    }
    public abstract boolean isEmpty();
    public abstract boolean contains( AllocNode n );
    public abstract void forall( RasVisitor v );
    public abstract boolean add( AllocNode n );
    public boolean fastAddAll( Ras r ) {
	return addAll( r );
    }
    public boolean addAll( Ras r ) {
	class AddAllVisitor extends RasVisitor {
	    boolean changed = false;
	    public void visit( AllocNode n ) {
		changed = add( n ) || changed;
	    }
	}
	AddAllVisitor v = new AddAllVisitor();
	r.forall( v );
	return v.changed;
    }
    public abstract int size();
    public void nextIter() {}
    public abstract boolean rasHasNonEmptyIntersection( Ras other );

    public boolean hasNonEmptyIntersection( PointsToSet other ) {
	if( other == null ) return false;
	if( other instanceof FullObjectSet ) return true;
	if( other instanceof AllocNode ) {
	    return contains( (AllocNode) other );
	} else if( other instanceof Ras ) {
	    return rasHasNonEmptyIntersection( (Ras) other );
	} else if( other instanceof Union ) {
	    return other.hasNonEmptyIntersection( this );
	} else throw new RuntimeException( "Unhandled type of PointsToSet"+other+
	" type is "+other.getClass() );
    }
    public Set possibleTypes() {
	final HashSet ret = new HashSet();
	forall( new RasVisitor() { public void visit( AllocNode n ) {
		ret.add( n.getType() );
	    } } );
	return ret;
    }
}


