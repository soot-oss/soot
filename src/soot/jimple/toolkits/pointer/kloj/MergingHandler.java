package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;
import java.util.*;

class MergingHandler extends Handler {
    protected void handleSimple( VarNode from, VarNode to ) {
	throw new RuntimeException( "shouldn't be called" );
    }
    protected void handleLoad( final FieldRefNode from, final VarNode to ) {
	throw new RuntimeException( "shouldn't be called" );
    }
    protected void handleStore( final VarNode from, final FieldRefNode to ) {
	throw new RuntimeException( "shouldn't be called" );
    }
    protected void handleNew( AllocNode from, VarNode to ) {
	b.addAllocSite( to, from );
    }
}

