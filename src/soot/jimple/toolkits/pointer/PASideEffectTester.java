package soot.jimple.toolkits.pointer;

import soot.*;
import soot.jimple.*;
import java.util.*;
import soot.jimple.spark.PointsToSet;

//  ArrayRef, 
//  CaughtExceptionRef, 
//  FieldRef, 
//  IdentityRef, 
//  InstanceFieldRef, 
//  InstanceInvokeExpr, 
//  Local,  
//  StaticFieldRef

public class PASideEffectTester implements SideEffectTester
{
    PointerAnalysis pa = Scene.v().getActivePointerAnalysis();
    SideEffectAnalysis sea = Scene.v().getActiveSideEffectAnalysis();
    HashMap unitToRead;
    HashMap unitToWrite;
    HashMap localToReachingObjects;
    SootMethod currentMethod;

    public PASideEffectTester() {
	if( Union.factory == null ) {
	    Union.factory = new UnionFactory() {
		public Union newUnion() { return new FullObjectSet(); }
	    };
	}
    }

    /** Call this when starting to analyze a new method to setup the cache. */
    public void newMethod( SootMethod m ) {
	unitToRead = new HashMap();
	unitToWrite = new HashMap();
	localToReachingObjects = new HashMap();
	currentMethod = m;
	sea.findNTRWSets( currentMethod );
    }

    protected RWSet readSet( Unit u ) {
	RWSet ret = (RWSet) unitToRead.get( u );
	if( ret == null ) {
	    unitToRead.put( u, ret = sea.readSet( currentMethod, (Stmt) u ) );
	}
	return ret;
    }

    protected RWSet writeSet( Unit u ) {
	RWSet ret = (RWSet) unitToWrite.get( u );
	if( ret == null ) {
	    unitToWrite.put( u, ret = sea.writeSet( currentMethod, (Stmt) u ) );
	}
	return ret;
    }
    
    protected PointsToSet reachingObjects( Local l ) {
	PointsToSet ret = (PointsToSet) localToReachingObjects.get( l );
	if( ret == null ) {
	    localToReachingObjects.put( l, 
		    ret = pa.reachingObjects( currentMethod, null, l ) );
	}
	return ret;
    }

    /** Returns true if the unit can read from v.
     * Does not deal with expressions; deals with Refs. */
    public boolean unitCanReadFrom(Unit u, Value v)
    {
	return valueTouchesRWSet( readSet( u ), v, u.getUseBoxes() );
    }

    /** Returns true if the unit can read from v.
     * Does not deal with expressions; deals with Refs. */
    public boolean unitCanWriteTo(Unit u, Value v)
    {
	return valueTouchesRWSet( writeSet( u ), v, u.getDefBoxes() );
    }

    protected boolean valueTouchesRWSet(RWSet s, Value v, List boxes)
    {
        for( Iterator it = v.getUseBoxes().iterator(); it.hasNext(); ) {
            ValueBox use = (ValueBox) it.next();
            if( valueTouchesRWSet( s, use.getValue(), boxes ) ) return true;
        }
        // This doesn't really make any sense, but we need to return something.
        if (v instanceof Constant)
            return false;

        if (v instanceof Expr)
            throw new RuntimeException("can't deal with expr");

	for( Iterator it = boxes.iterator(); it.hasNext(); ) {
	    ValueBox box = (ValueBox) it.next();
	    Value boxed = box.getValue();
	    if( boxed.equivTo( v ) ) return true;
	}

	if (v instanceof Local) {
	    return false;
	}

	if( v instanceof InstanceFieldRef ) {
	    InstanceFieldRef ifr = (InstanceFieldRef) v;
	    if( s == null ) return false;
	    PointsToSet o1 = s.getBaseForField( ifr.getField() );
	    if( o1 == null ) return false;
	    PointsToSet o2 = reachingObjects( (Local) ifr.getBase() );
	    if( o2 == null ) return false;
	    return o1.hasNonEmptyIntersection( o2 );
	}

	if( v instanceof ArrayRef ) {
	    ArrayRef ar = (ArrayRef) v;
	    if( s == null ) return false;
	    PointsToSet o1 = s.getBaseForField( PointerAnalysis.ARRAY_ELEMENTS_NODE );
	    if( o1 == null ) return false;
	    PointsToSet o2 = reachingObjects( (Local) ar.getBase() );
	    if( o2 == null ) return false;
	    return o1.hasNonEmptyIntersection( o2 );
	}

	if( v instanceof StaticFieldRef ) {
	    StaticFieldRef sfr = (StaticFieldRef) v;
	    if( s == null ) return false;
	    return s.getGlobals().contains( sfr.getField() );
	}

	throw new RuntimeException( "Forgot to handle value "+v );
    }
}
