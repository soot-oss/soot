package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;
import soot.util.*;

abstract class Scheduler {
    protected Base b;
    protected Handler h;
    protected void wroteTo( VarNode n ) {
	wroteTo();
    }
    protected void wroteTo( SiteDotField n ) {
	wroteTo();
    }
    protected void wroteTo() {
	throw new RuntimeException( "Not overridden" );
    }

    abstract protected void compute();
    void setBase( Base b ) {
	this.b = b;
	h.setBase( b );
    }
    Scheduler( Handler h ) {
	this.h = h;
	h.setScheduler( this );
    }
    protected void computeAllocs() {
	for( Iterator newSiteIt = b.getNews().keySet().iterator();
		newSiteIt.hasNext(); ) {
	    AllocNode newSite = (AllocNode) newSiteIt.next();
	    for( Iterator varIt = b.getNews().get( newSite ).iterator();
		    varIt.hasNext(); ) {
		VarNode var = (VarNode) varIt.next();

		h.handleNew( newSite, var );
	    }
	}
	b.nextIter();
    }
}

