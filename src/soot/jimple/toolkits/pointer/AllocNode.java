package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.representations.*;

public class AllocNode extends Node
{
    static int nextAllocNodeId = -1;
    static Map nodeMap = new HashMap(4);
    public static int getNumNodes() {
	return (-nextAllocNodeId) -1;
    }
    public static AllocNode v( Object newExpr, Type t, SootMethod m ) {
	AllocNode ret = (AllocNode) nodeMap.get( newExpr );
	if( ret == null ) {
	    if( !(t instanceof RefLikeType) ) throw new RuntimeException(
		    "Attempt to create AllocNode of type "+t+" newe is "+newExpr+
		    " and method is "+m );
            if(t instanceof RefType) {
                SootClass c = ((RefType) t).getSootClass();
                if( c.isAbstract() ) {
                    throw new RuntimeException( "alloc node "+newExpr+" in "+m+
                            " creates abstract object "+t );
                }
            }
	    nodeMap.put( newExpr, ret = new AllocNode( newExpr, t, m ) );
	}
	return ret;
    }

    SootMethod m;
    Object newExpr;
    protected void assignId() {
	id = nextAllocNodeId--;
    }
    protected AllocNode( Object newExpr, Type t, SootMethod m ) {
	super( t );
	this.newExpr = newExpr;
	this.m = m;
    }
    public SootClass getSootClass() {
	Type t = getType();
	if( !( t instanceof RefType ) ) {
	    // TODO: Is this really what we should do here for non-ref-types?
	    return Scene.v().getSootClass( "java.lang.Object" );
	}
	RefType rt = (RefType) t;
	return rt.getSootClass();
    }
    public Object getNewExpr() { return newExpr; }
    public String toString() {
	return "Alloc "+id+" "+newExpr+" "+m;
    }
    /*
    public boolean equals( Object o ) {
	if( o instanceof AllocNode ) {
	    return site.equals( ((AllocNode)o).site );
	} else return false;
    }
    public int hashCode() {
	return site.hashCode();
    }
    */
}

