package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.util.*;

public class VarNodeDFS {
    HashSet alreadyVisited = new HashSet();
    MultiMap assignments;
    int lastFinishingNumber = 0;
    public VarNodeDFS( MultiMap assignments ) {
	this.assignments = assignments;
    }
    protected void visit( VarNode v ) {
	if( alreadyVisited.contains( v ) ) return;
	alreadyVisited.add( v );
	for( Iterator it = assignments.get( v ).iterator(); it.hasNext(); ) {
	    VarNode target = (VarNode) it.next();
	    visit( target );
	}
	v.finishingNumber = ++lastFinishingNumber;
    }
    public void apply() {
	for( Iterator it = assignments.keySet().iterator(); it.hasNext(); ) {
	    VarNode v = (VarNode) it.next();
	    visit( v );
	}
    }
}

