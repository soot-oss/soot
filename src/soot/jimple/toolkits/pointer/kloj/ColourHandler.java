package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;

class ColourHandler extends Handler {
    protected void handleSimple( VarNode from, VarNode to ) {
	Ras fromSites = ( (ColourRas) b.getAllocSites( from ) ).gray;
	if( fromSites.isEmpty() ) return;
	b.addAllocSites( to, fromSites );
    }
    protected void handleLoad( final FieldRefNode from, final VarNode to ) {
	ColourRas sites = (ColourRas) b.getAllocSites( from.getBase() );
	sites.black.forall( new RasVisitor() {
	    public void visit( AllocNode a ) {
		SiteDotField aDotF = SiteDotField.v( a, from.getField() );
		if( aDotF == null ) return;
		if( b.hasAllocSites( aDotF ) ) {
		    ColourRas r = (ColourRas) b.getAllocSites( aDotF );
		    b.addAllocSites( to, r.gray );
		}
	    }
	} );
	sites.gray.forall( new RasVisitor() {
	    public void visit( AllocNode a ) {
		SiteDotField aDotF = SiteDotField.v( a, from.getField() );
		if( aDotF == null ) return;
		if( b.hasAllocSites( aDotF ) ) {
		    ColourRas r = (ColourRas) b.getAllocSites( aDotF );
		    b.addAllocSites( to, r.black );
		    b.addAllocSites( to, r.gray );
		}
	    }
	} );
    }
    protected void handleStore( final VarNode from, final FieldRefNode to ) {
	final ColourRas fromSites = (ColourRas) b.getAllocSites( from );
	if( fromSites.isEmpty() ) return;
	final Object f = to.getField();
	ColourRas sites = (ColourRas) b.getAllocSites( to.getBase() );
	sites.black.forall( new RasVisitor() {
	    public void visit( AllocNode a ) {
		SiteDotField aDotF = SiteDotField.v( a, f );
		if( aDotF == null ) return;
		b.addAllocSites( aDotF, fromSites.gray );
	    }
	} );
	sites.gray.forall( new RasVisitor() {
	    public void visit( AllocNode a ) {
		SiteDotField aDotF = SiteDotField.v( a, f );
		if( aDotF == null ) return;
		b.addAllocSites( aDotF, fromSites.black );
		b.addAllocSites( aDotF, fromSites.gray );
	    }
	} );
    }
    protected void handleNew( AllocNode from, VarNode to ) {
	b.addAllocSite( to, from );
    }
}

