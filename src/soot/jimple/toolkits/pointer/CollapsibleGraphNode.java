package soot.jimple.toolkits.pointer;
import soot.jimple.toolkits.pointer.kloj.*;
import java.util.*;

public class CollapsibleGraphNode {
    protected HashSet preds;
    protected HashSet succs;
    protected Ras ras;
    protected CollapsibleGraphNode rep = this;
    public static List reps = new ArrayList();

    public CollapsibleGraphNode() {
	reps.add( this );
    }
    public CollapsibleGraphNode getRep() {
	CollapsibleGraphNode ret = this;
	for(;;) {
	    CollapsibleGraphNode n = ret.rep;
	    if( n == ret ) break;
	    ret = n;
	}
	rep = ret;
	return ret;
    }

    public void killGraph() {
	preds = null;
	succs = null;
    }
    public void killRas() {
	ras = null;
    }
    public void initPredsSuccs() {
	if( rep != this )
	    throw new RuntimeException( "attempt to init preds/succs of merged node" );
	if( succs == null ) succs = new HashSet();
	if( preds == null ) preds = new HashSet();
    }
    public void setRas( Ras r ) {
	if( rep != this )
	    throw new RuntimeException( "attempt to set Ras of merged node" );
	ras = r;
    }
    public Ras getRas() {
	return getRep().ras;
    }
    public Set getSuccs() {
	return getRep().succs;
    }
    public Set getPreds() {
	return getRep().preds;
    }

    public boolean mergeInto( CollapsibleGraphNode other ) {
	CollapsibleGraphNode myRep = getRep();
	CollapsibleGraphNode otherRep = other.getRep();
	if( myRep == otherRep ) return false;
	// System.out.println( "merging "+System.identityHashCode(myRep)+" into "
	    //+System.identityHashCode(otherRep) );
	/*
	if( otherRep.ras == null ) {
	    otherRep.ras = myRep.ras;
	} else if( myRep.ras != null ) {
	    otherRep.ras.fastAddAll( myRep.ras );
	}
	*/
	if( myRep.succs != null ) {
	    otherRep.succs.addAll( myRep.succs );
	}
	if( myRep.preds != null ) {
	    otherRep.preds.addAll( myRep.preds );
	}
	myRep.rep = otherRep;
	myRep.ras = null;
	myRep.succs = null;
	myRep.preds = null;
	reps.remove( myRep );
	return true;
    }
    public boolean flowToSuccs() {
	boolean ret = false;
	if( ras == null ) return ret;
	if( succs != null ) {
	    for( Iterator it = new LinkedList( succs ).iterator();
		    it.hasNext(); ) {
		CollapsibleGraphNode succ = (CollapsibleGraphNode) it.next();
		CollapsibleGraphNode succRep = succ.getRep();
		if( succRep != succ ) {
		    succs.remove( succ );
		    succs.add( succRep );
		}
	    }
	    for( Iterator it = succs.iterator();
		    it.hasNext(); ) {
		CollapsibleGraphNode succ = (CollapsibleGraphNode) it.next();
		if( succ.ras != null )
		    ret = succ.ras.addAll( ras ) | ret;
	    }
	}
	return ret;
    }
}

