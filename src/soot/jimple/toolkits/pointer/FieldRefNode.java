package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;

public class FieldRefNode extends ValNode
{
    static Map nodeMap = new HashMap(4);
    public static FieldRefNode v( VarNode base, Object field, Type t, SootMethod m ) {
	FieldRefNode ret = (FieldRefNode) base.dot( field );
	if( ret == null ) {
	    if( !(t instanceof RefLikeType) ) throw new RuntimeException(
		    "Attempt to create FieldRefNode of type "+t+" base is "+base+
		    " and field is "+field+" and method is "+m );
	    ret = new FieldRefNode( base, field, t, m );
	} else {
	    if( !ret.getType().equals( t ) ) {
		throw new RuntimeException( "Inconsistent types: "+ret.getType()+" and "+t );
	    }
	}

	return ret;
    }


    VarNode base;
    Object field;
    public SootMethod m;
    protected FieldRefNode( VarNode base, Object field, Type t, SootMethod m ) {
	super(t);
	if( field == null ) throw new RuntimeException( "No, you can't make it null" );
	this.base = base;
	this.field = field;
	this.m = m;
	base.addField( this, field );
    }
    public VarNode getBase() {
	return base;
    }
    public Object getField() {
	return field;
    }
    public String toString() {
	return "FieldRefNode "+id+" "+base+"."+field+" "+m;
    }
    /*
    public boolean equals( Object o ) {
	if( o instanceof FieldRefNode ) {
	    return base.equals( ((FieldRefNode)o).base )
	    && field.equals( ((FieldRefNode)o).field );
	} else return false;
    }
    */
}

