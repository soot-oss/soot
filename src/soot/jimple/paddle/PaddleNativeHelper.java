/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.paddle;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;
import soot.toolkits.scalar.Pair;
import soot.*;

public class PaddleNativeHelper extends NativeHelper {
    protected void assignImpl(ReferenceVariable lhs, ReferenceVariable rhs) {
        mpag.addEdge( (Node) rhs, (Node) lhs );
    }
    protected void assignObjectToImpl(ReferenceVariable lhs, AbstractObject obj) {
	AllocNode objNode = PaddleScene.v().nodeManager().makeAllocNode( 
		new Pair( "AbstractObject", obj.getType() ),
		 obj.getType(), null );

        VarNode var;
        if( lhs instanceof FieldRefNode ) {
	    var = PaddleScene.v().nodeManager().makeGlobalVarNode( objNode, objNode.getType() );
            mpag.addEdge( (Node) lhs, var );
        } else {
            var = (VarNode) lhs;
        }
        mpag.addEdge( objNode, var );
    }
    protected ReferenceVariable arrayElementOfImpl(ReferenceVariable base) {
        Node n = (Node) base;
        VarNode l;
	if( base instanceof VarNode ) {
	    l = (VarNode) base;
	} else {
	    FieldRefNode b = (FieldRefNode) base;
	    l = PaddleScene.v().nodeManager().makeGlobalVarNode( b, b.getType() );
	    mpag.addEdge( b, l );
	}
        return PaddleScene.v().nodeManager().makeFieldRefNode( l, ArrayElement.v() );
    }
    protected ReferenceVariable cloneObjectImpl(ReferenceVariable source) {
	return source;
    }
    protected ReferenceVariable newInstanceOfImpl(ReferenceVariable cls) {
        return PaddleScene.v().nodeFactory().caseNewInstance( (VarNode) cls );
    }
    protected ReferenceVariable staticFieldImpl(String className, String fieldName ) {
	SootClass c = RefType.v( className ).getSootClass();
	SootField f = c.getFieldByName( fieldName );
	return PaddleScene.v().nodeManager().makeGlobalVarNode( f, f.getType() );
    }
    protected ReferenceVariable tempFieldImpl(String fieldsig) {
	return PaddleScene.v().nodeManager().makeGlobalVarNode( new Pair( "tempField", fieldsig ),
            RefType.v( "java.lang.Object" ) );
    }
    protected ReferenceVariable tempVariableImpl() {
	return PaddleScene.v().nodeManager().makeGlobalVarNode( new Pair( "TempVar", new Integer( ++G.v().PaddleNativeHelper_tempVar ) ),
		RefType.v( "java.lang.Object" ) );
    }
    private MethodPAG mpag;
    public void setMPAG(MethodPAG mpag) {
        this.mpag = mpag;
    }
}
