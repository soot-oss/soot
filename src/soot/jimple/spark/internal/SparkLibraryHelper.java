package soot.jimple.spark.internal;

import soot.AnySubType;
import soot.ArrayType;
import soot.RefType;
import soot.SootMethod;
import soot.Type;
import soot.TypeSwitch;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;

public class SparkLibraryHelper extends TypeSwitch {
	
	private PAG pag;
	private Node node;
	private SootMethod method;
	
	public SparkLibraryHelper(PAG pag, Node node, SootMethod method) {
		this.pag = pag;
		this.node = node;
		this.method = method;
	}
	
	@Override
	public void caseRefType(RefType t) {
		VarNode local = pag.makeLocalVarNode(new Object(), t, method);
		AllocNode alloc = pag.makeAllocNode(new Object(), AnySubType.v(t), method);
		
		pag.addAllocEdge(alloc, local);
		pag.addEdge(local, node);
	}
	
	@Override
	public void caseArrayType(ArrayType type) {
		Node array = node;
		for (Type t = type; t instanceof ArrayType; t = ((ArrayType) t).getElementType()) {
    		ArrayType at = (ArrayType) t;
			if (at.baseType instanceof RefType) {
				//TODO new Object() or Type?
				LocalVarNode localArray = pag.makeLocalVarNode(new Object(), t, method);
				pag.addEdge(localArray, array);
				
    			AllocNode newArray = pag.makeAllocNode(new Object(), at, method);
    			pag.addEdge(newArray, localArray);
    			
    			FieldRefNode arrayRef = pag.makeFieldRefNode( localArray, ArrayElement.v());
    			LocalVarNode local = pag.makeLocalVarNode(new Object(), at.getElementType(), method);
    			pag.addEdge(local, arrayRef);
    			
    			array = local;

    			if (at.numDimensions == 1) {                            				
    				AllocNode alloc = pag.makeAllocNode(new Object(), AnySubType.v((RefType)at.baseType), method);
    				pag.addEdge(alloc, local);
    			}
    		}
		}
	}

}
