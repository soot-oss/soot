package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.util.*;

class MergingIterativeScheduler extends Scheduler {
    int changed;
    protected void wroteTo() {
	changed++;
    }
    protected void compute() {
    /*
	int iterNum = 0;
	computeAllocs();
	do {
	    System.out.println( "Iteration: "+(++iterNum) );
	    System.out.println( "There are "+CollapsibleGraphNode.reps.size()+
		" unmerged nodes" );
	    changed = 0;
	    for( Iterator fromIt = CollapsibleGraphNode.reps.iterator();
		    fromIt.hasNext(); ) {
		CollapsibleGraphNode cgn = (CollapsibleGraphNode) fromIt.next();
		if( cgn.flowToSuccs() ) changed++;
	    }
	    System.out.println( "There are "+CollapsibleGraphNode.reps.size()+
		" unmerged nodes" );
	    System.out.println( "Merging nodes" );
	    for( Iterator baseIt = VarNode.getAll().iterator(); baseIt.hasNext(); ) {
		final VarNode base = (VarNode) baseIt.next();
		Ras r = base.getRas();
		r.forall( new RasVisitor() {
		    public void visit( AllocNode an ) {
			for( Iterator frIt = base.getAllFieldRefs().iterator();
				frIt.hasNext(); ) {
			    FieldRefNode fr = (FieldRefNode) frIt.next();
			    if( fr.mergeInto(
				SiteDotField.v( an, fr.getField() ) ) ) {
				changed++;
			    }
			}
		    }
		} );
	    }
	    System.out.println( "Changes: "+changed );
	} while( changed > 0 );
	*/
    }
    MergingIterativeScheduler( ) {
	super( new MergingHandler() );
    }
}

