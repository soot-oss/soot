package soot.jimple.toolkits.pointer.kloj;
import soot.jimple.toolkits.pointer.util.*;
import soot.jimple.toolkits.pointer.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.*;

class KlojNativeHelper extends NativeHelper {
    protected NodePPG graph;
    protected SootMethod currentMethod;

    public KlojNativeHelper( NodePPG graph ) {
	this.graph = graph;
    }
    public void setCurrentMethod( SootMethod m ) {
	currentMethod = m;
    }
    protected void assignImpl(ReferenceVariable lhs, ReferenceVariable rhs) {
	if( lhs instanceof VarNode && rhs instanceof VarNode ) {
	    graph.addSimpleEdge( (VarNode) rhs, (VarNode) lhs );
	} else if( lhs instanceof VarNode && rhs instanceof FieldRefNode ) {
	    graph.addLoadEdge( (FieldRefNode) rhs, (VarNode) lhs );
	} else if( lhs instanceof FieldRefNode && rhs instanceof VarNode ) {
	    graph.addStoreEdge( (VarNode) rhs, (FieldRefNode) lhs );
	} else throw new RuntimeException( "Unrecognized node types "+lhs+rhs );
    }
    protected void assignObjectToImpl(ReferenceVariable lhs, AbstractObject obj) {
	AllocNode objNode;
        if( obj.getType().isAbstract() ) {
            objNode = AllocNode.v( new Pair( "AbstractObject", obj.getType() ),
                AnyType.v(), currentMethod );
        } else { 
            objNode = AllocNode.v( new Pair( "AbstractObject", obj.getType() ),
                obj.getType().getType(), currentMethod );
        }

	if( lhs instanceof VarNode ) {
	    graph.addNewEdge( objNode, (VarNode) lhs );
	} else if( lhs instanceof FieldRefNode ) {
	    VarNode l = VarNode.v( objNode, objNode.getType(), currentMethod );
	    graph.addNewEdge( objNode, l );
	    graph.addStoreEdge( l, (FieldRefNode) lhs );
	} else throw new RuntimeException( "Unrecognized node types "+lhs+obj );
    }
    protected ReferenceVariable arrayElementOfImpl(ReferenceVariable base) {
	if( base instanceof VarNode ) {
	    VarNode b = (VarNode) base;
	    return FieldRefNode.v( 
		    b,
		    PointerAnalysis.ARRAY_ELEMENTS_NODE,
		    ( (RefLikeType) b.getType() ).getArrayElementType(),
		    currentMethod );
	} else if( base instanceof FieldRefNode ) {
	    FieldRefNode b = (FieldRefNode) base;
	    VarNode l = VarNode.v( b, b.getType(), currentMethod );
	    graph.addLoadEdge( b, l );
	    return FieldRefNode.v( 
		    l,
		    PointerAnalysis.ARRAY_ELEMENTS_NODE,
		    ( (RefLikeType) b.getType() ).getArrayElementType(),
		    currentMethod );
	} else throw new RuntimeException( "Unrecognized node type "+base );
    }
    protected ReferenceVariable cloneObjectImpl(ReferenceVariable source) {
	return source;
    }
    protected ReferenceVariable newInstanceOfImpl(ReferenceVariable cls) {
	AllocNode site = AllocNode.v( cls, AnyType.v(), currentMethod );
	VarNode local = VarNode.v( site, RefType.v( "java.lang.Object" ),
		currentMethod );
	graph.addNewEdge( site, local );
	return local;
    }
    protected ReferenceVariable staticFieldImpl(String className, String fieldName ) {
	SootClass c = RefType.v( className ).getSootClass();
	SootField f = c.getFieldByName( fieldName );
	return VarNode.v( f, f.getType(), currentMethod );
    }
    protected ReferenceVariable tempFieldImpl(String fieldsig) {
	return VarNode.v( new Pair( "tempField", fieldsig ), AnyType.v(), currentMethod );
    }
    static int tempVar = 0;
    protected ReferenceVariable tempVariableImpl() {
	return VarNode.v( new Pair( "TempVar", new Integer( ++tempVar ) ),
		RefType.v( "java.lang.Object" ), currentMethod );
    }
}
