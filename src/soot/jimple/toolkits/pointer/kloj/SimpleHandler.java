package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;

class SimpleHandler extends Handler {
    protected void handleSimple( VarNode from, VarNode to ) {
	Ras fromSites = b.getAllocSites( from );
	if( fromSites.isEmpty() ) return;
	b.addAllocSites( to, fromSites );
    }
    protected void handleLoad( final FieldRefNode from, final VarNode to ) {
	Ras sites = b.getAllocSites( from.getBase() );
	sites.forall( new RasVisitor() {
	    public void visit( AllocNode a ) {
		SiteDotField aDotF = SiteDotField.v( a, from.getField() );
		if( aDotF == null ) return;
		if( b.hasAllocSites( aDotF ) ) {
		    b.addAllocSites( to, b.getAllocSites( aDotF ) );
		}
	    }
	} );
    }
    protected void handleStore( final VarNode from, final FieldRefNode to ) {
	final Ras fromSites = b.getAllocSites( from );
	if( fromSites.isEmpty() ) return;
	final Object f = to.getField();
	Ras sites = b.getAllocSites( to.getBase() );
	sites.forall( new RasVisitor() {
	    public void visit( AllocNode a ) {
		SiteDotField aDotF = SiteDotField.v( a, f );
		if( aDotF == null ) return;
		b.addAllocSites( aDotF, fromSites );
	    }
	} );
    }
    protected void handleNew( AllocNode from, VarNode to ) {
	b.addAllocSite( to, from );
    }
}

