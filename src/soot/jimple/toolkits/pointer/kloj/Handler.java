package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.*;

abstract class Handler {
    Scheduler s;
    Base b;
    abstract protected void handleSimple( VarNode from, VarNode to ); 
    abstract protected void handleLoad( FieldRefNode from, VarNode to ); 
    abstract protected void handleStore( VarNode from, FieldRefNode to ); 
    abstract protected void handleNew( AllocNode from, VarNode to ); 
    void setScheduler( Scheduler s ) {
	this.s = s;
    }
    void setBase( Base b ) {
	this.b = b;
    }
}

