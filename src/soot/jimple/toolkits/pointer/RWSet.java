package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;
import soot.jimple.spark.PointsToSet;

/** Represents the read or write set of a statement. */
public abstract class RWSet {
    public abstract boolean getCallsNative();
    public abstract boolean setCallsNative();

    /** Returns an iterator over any globals read/written. */
    public abstract Set getGlobals();
    public abstract Set getFields();
    public abstract PointsToSet getBaseForField( Object f );
    public abstract boolean hasNonEmptyIntersection( RWSet other );
    /** Adds the RWSet other into this set. */
    public abstract boolean union( RWSet other );
    public abstract boolean addGlobal( SootField global );
    public abstract boolean addFieldRef( PointsToSet otherBase, Object field );
    public abstract boolean isEquivTo( RWSet other );
}
