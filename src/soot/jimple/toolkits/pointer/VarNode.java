package soot.jimple.toolkits.pointer;
import java.util.*;
import soot.*;

public class VarNode extends ValNode implements Comparable
{
    static Map nodeMap = new HashMap(4);
    public static VarNode v( Object val ) {
	VarNode ret = (VarNode) nodeMap.get( val );
	return ret;
    }

    public static VarNode v( Object val, Type t, SootMethod m ) {
	VarNode ret = (VarNode) nodeMap.get( val );
	if( ret == null ) {
	    if( !(t instanceof RefLikeType) ) throw new RuntimeException(
		    "Attempt to create VarNode of type "+t+" val is "+val+
		    " and method is "+m );
	    if( val instanceof Pair ) {
		Pair p = (Pair) val;
		if( p.o1 instanceof SootMethod && p.o2 instanceof Integer ) {
		    SootMethod meth = (SootMethod) p.o1;
		    if( meth.isStatic() && p.o2.equals( PointerAnalysis.THIS_NODE ) ) {
			throw new RuntimeException( "Attempt to create this node for static method "+meth );
		    }
		}
	    }
	    nodeMap.put( val, ret = new VarNode( val, t, m ) );
	} else {
	    if( !ret.getType().equals( t ) ) {
		throw new RuntimeException( "Inconsistent types: "+ret.getType()+" and "+t );
	    }
	}
	return ret;
    }
    public static Collection getAll() {
	return nodeMap.values();
    }

    int edgesIn;
    Object val;
    public SootMethod m;
    Map fields = new HashMap(4);
    public Collection getAllFieldRefs() {
	return fields.values();
    }
    public FieldRefNode dot( Object field ) {
	return (FieldRefNode) fields.get( field );
    }
    protected VarNode( Object val, Type t, SootMethod m ) {
	super(t);
	this.val = val;
	this.m = m;
    }
    void addField( FieldRefNode frn, Object field ) {
        Object old = fields.get( field );
        if( old != null && old != frn ) {
            throw new RuntimeException( "attempt to add two copies of field" );
        }
	fields.put( field, frn );
    }
    public Object getVal() {
	return val;
    }
    public String toString() {
	return "VarNode "+id+" "+val+" "+m;
    }
    public int compareTo( Object o ) {
	VarNode other = (VarNode) o;
	return finishingNumber - other.finishingNumber;
    }
    public int refCount = 0;
    public void incRefCount() {
        refCount++;
    }
    public int finishingNumber = 0;
    /*
    public int hashCode() {
	return val.hashCode();
    }
    public boolean equals( Object o ) {
	if( o instanceof VarNode ) {
	    return val.equals( ((VarNode) o).val );
	} else return false;
    }
    */
}

