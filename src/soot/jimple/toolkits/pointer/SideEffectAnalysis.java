package soot.jimple.toolkits.pointer;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.invoke.*;
import java.util.*;
import soot.util.*;
import soot.jimple.spark.*;

/** Generates side-effect information from a PointsToAnalysis. */
public class SideEffectAnalysis {
    PointsToAnalysis pa;
    InvokeGraph ig;
    Map methodToNTReadSet = new HashMap();
    Map methodToNTWriteSet = new HashMap();
    int rwsetcount = 0;

    public void findNTRWSets( SootMethod method ) {
	if( methodToNTReadSet.containsKey( method )
	    && methodToNTWriteSet.containsKey( method ) ) return;
	
	MethodRWSet read = new MethodRWSet();
	MethodRWSet write = new MethodRWSet();
	for( Iterator sIt = method.retrieveActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
	    final Stmt s = (Stmt) sIt.next();
	    if( !s.containsInvokeExpr() ) {
		read.union( readSet( method, s ) );
		write.union( writeSet( method, s ) );
	    }
	}
	methodToNTReadSet.put( method, read );
	methodToNTWriteSet.put( method, write );
	SootClass c = method.getDeclaringClass();
	if( !c.isApplicationClass() ) {
	    method.releaseActiveBody();
	}
    }

    public RWSet nonTransitiveReadSet( SootMethod method ) {
	findNTRWSets( method );
	return (RWSet) methodToNTReadSet.get( method );
    }

    public RWSet nonTransitiveWriteSet( SootMethod method ) {
	findNTRWSets( method );
	return (RWSet) methodToNTWriteSet.get( method );
    }

    public SideEffectAnalysis( PointsToAnalysis pa, InvokeGraph ig ) {
	this.pa = pa;
	this.ig = ig;
    }

    public RWSet readSet( SootMethod method, Stmt stmt ) {
	RWSet ret = null;
	if( stmt.containsInvokeExpr() ) {
	    if( ig.containsSite( stmt ) ) {
		for( Iterator targetIt = ig.mcg.getMethodsReachableFrom(
			ig.getTargetsOf( stmt ) ).iterator(); targetIt.hasNext(); ) {
		    final SootMethod target = (SootMethod) targetIt.next();
		    if( target.isNative() ) {
			if( ret == null ) ret = new SiteRWSet();
			ret.setCallsNative();
		    } else if( target.isConcrete() ) {
			if( ret == null ) ret = new SiteRWSet();
			ret.union( nonTransitiveReadSet( target ) );
		    }
		}
	    }
	} else if( stmt instanceof AssignStmt ) {
	    AssignStmt a = (AssignStmt) stmt;
	    Value r = a.getRightOp();
	    ret = addValue( r, method, stmt );
	}
	return ret;
    }

    public RWSet writeSet( SootMethod method, Stmt stmt ) {
	RWSet ret = null;
	if( stmt.containsInvokeExpr() ) {
	    if( ig.containsSite( stmt ) ) {
		for( Iterator targetIt = ig.mcg.getMethodsReachableFrom(
			ig.getTargetsOf( stmt ) ).iterator(); targetIt.hasNext(); ) {
		    final SootMethod target = (SootMethod) targetIt.next();
		    if( target.isNative() ) {
			if( ret == null ) ret = new SiteRWSet();
			ret.setCallsNative();
		    } else if( target.isConcrete() ) {
			if( ret == null ) ret = new SiteRWSet();
			ret.union( nonTransitiveWriteSet( target ) );
		    }
		}
	    }
	} else if( stmt instanceof AssignStmt ) {
	    AssignStmt a = (AssignStmt) stmt;
	    Value l = a.getLeftOp();
	    ret = addValue( l, method, stmt );
	}
	return ret;
    }

    protected RWSet addValue( Value v, SootMethod m, Stmt s ) {
	RWSet ret = null;
	if( v instanceof InstanceFieldRef ) {
	    InstanceFieldRef ifr = (InstanceFieldRef) v;
	    PointsToSet base = pa.reachingObjects( m, s, (Local) ifr.getBase() );
	    ret = new StmtRWSet();
	    ret.addFieldRef( base, ifr.getField() );
	} else if( v instanceof StaticFieldRef ) {
	    StaticFieldRef sfr = (StaticFieldRef) v;
	    ret = new StmtRWSet();
	    ret.addGlobal( sfr.getField() );
	} else if( v instanceof ArrayRef ) {
	    ArrayRef ar = (ArrayRef) v;
	    PointsToSet base = pa.reachingObjects( m, s, (Local) ar.getBase() );
	    ret = new StmtRWSet();
	    ret.addFieldRef( base, PointsToAnalysis.ARRAY_ELEMENTS_NODE );
	}
	return ret;
    }

    public String toString() {
        return "SideEffectAnalysis: PA="+pa+" IG="+ig;
    }
}

