package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;

public class FieldRefNode extends ValNode
{
    static Map nodeMap = new HashMap(4);
    static HashSet allNodes = new HashSet();
    static public Collection getAll() {
        return allNodes;
    }
    public static FieldRefNode v( VarNode base, Object field, Type t, SootMethod m ) {
	FieldRefNode ret = (FieldRefNode) base.dot( field );
	if( ret == null ) {
	    ret = new FieldRefNode( base, field, null, m );
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
        allNodes.add( this );
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

