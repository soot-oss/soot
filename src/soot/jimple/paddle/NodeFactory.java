/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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
import soot.*;
import soot.jimple.paddle.queue.*;
import soot.util.*;
import java.util.*;

/** Factory for nodes not specific to a given method.
 * @author Ondrej Lhotak
 */
public class NodeFactory {
    public NodeFactory( 
        Qsrc_dst simple,
        Qsrc_fld_dst load,
        Qsrc_fld_dst store,
        Qobj_var alloc ) {
        this.simple = simple;
        this.load = load;
        this.store = store;
        this.alloc = alloc;
    }

    protected NodeManager nm = PaddleScene.v().nodeManager();
    protected Qsrc_dst simple;
    protected Qsrc_fld_dst load;
    protected Qsrc_fld_dst store;
    protected Qobj_var alloc;
    
    final public Node caseDefaultClassLoader() {
	AllocNode a = nm.makeAllocNode( 
		PointsToAnalysis.DEFAULT_CLASS_LOADER,
		AnySubType.v( RefType.v( "java.lang.ClassLoader" ) ), null );
	VarNode v = nm.makeGlobalVarNode(
		PointsToAnalysis.DEFAULT_CLASS_LOADER_LOCAL,
		RefType.v( "java.lang.ClassLoader" ) );
	addEdge( a, v );
	return v;
    }
    final public Node caseMainClassNameString() {
	AllocNode a = nm.makeAllocNode( 
		PointsToAnalysis.MAIN_CLASS_NAME_STRING,
		RefType.v( "java.lang.String" ), null );
	VarNode v = nm.makeGlobalVarNode(
		PointsToAnalysis.MAIN_CLASS_NAME_STRING_LOCAL,
		RefType.v( "java.lang.String" ) );
	addEdge( a, v );
	return v;
    }
    final public Node caseMainThreadGroup() {
	AllocNode threadGroupNode = nm.makeAllocNode( 
		PointsToAnalysis.MAIN_THREAD_GROUP_NODE,
		RefType.v("java.lang.ThreadGroup"), null );
	VarNode threadGroupNodeLocal = nm.makeGlobalVarNode(
		PointsToAnalysis.MAIN_THREAD_GROUP_NODE_LOCAL,
		RefType.v("java.lang.ThreadGroup") );
	addEdge( threadGroupNode, threadGroupNodeLocal );
	return threadGroupNodeLocal;
    }
    final public Node caseMainThread() {
	AllocNode threadNode = nm.makeAllocNode( 
		PointsToAnalysis.MAIN_THREAD_NODE,
		RefType.v("java.lang.Thread"), null );
	VarNode threadNodeLocal = nm.makeGlobalVarNode(
		PointsToAnalysis.MAIN_THREAD_NODE_LOCAL,
		RefType.v("java.lang.Thread") );
	addEdge( threadNode, threadNodeLocal );
	return threadNodeLocal;
    }
    final public Node caseArgv() {
	AllocNode argv = nm.makeAllocNode( 
		PointsToAnalysis.STRING_ARRAY_NODE,
		ArrayType.v(RefType.v( "java.lang.String" ), 1), null );
        VarNode sanl = nm.makeGlobalVarNode(
                PointsToAnalysis.STRING_ARRAY_NODE_LOCAL,
                ArrayType.v(RefType.v( "java.lang.String" ), 1) );
	AllocNode stringNode = nm.makeAllocNode( 
		PointsToAnalysis.STRING_NODE,
		RefType.v( "java.lang.String" ), null );
	VarNode stringNodeLocal = nm.makeGlobalVarNode(
		PointsToAnalysis.STRING_NODE_LOCAL,
		RefType.v( "java.lang.String" ) );
	addEdge( argv, sanl );
	addEdge( stringNode, stringNodeLocal );
	addEdge( stringNodeLocal, 
                FieldRefNode.make( sanl, ArrayElement.v() ) );
	return sanl;
    }

    final public Node caseNewInstance( VarNode cls ) {
        AllocNode site = nm.makeAllocNode( cls, AnySubType.v( RefType.v( "java.lang.Object" ) ), null );
	VarNode local = nm.makeGlobalVarNode( site, RefType.v( "java.lang.Object" ) );
        addEdge( site, local );
        return local;
    }
    /* End of public methods. */
    /* End of package methods. */

    protected Node caseThrow() {
	VarNode ret = nm.makeGlobalVarNode( PointsToAnalysis.EXCEPTION_NODE,
		    RefType.v("java.lang.Throwable") );
        return ret;
    }
    private Set seenEdges = new HashSet();
    public void addEdge( Node src, Node dst ) {
        if( src == null ) return;
        if( dst == null ) return;
        if( !seenEdges.add(new Cons(src, dst)) ) return;
        if( src instanceof VarNode ) {
            if( dst instanceof VarNode ) {
                if( src instanceof LocalVarNode && dst instanceof LocalVarNode ) {
                    LocalVarNode lsrc = (LocalVarNode) src;
                    LocalVarNode ldst = (LocalVarNode) dst;
                    if( ldst.getMethod() != lsrc.getMethod() ) throw new RuntimeException( ""+lsrc+" and "+ldst+" are in different methods!" );
                }
                simple.add( (VarNode) src, (VarNode) dst );
            } else if( dst instanceof FieldRefNode ) {
                FieldRefNode fdst = (FieldRefNode) dst;
                store.add( (VarNode) src, fdst.field(), fdst.base() );
            } else throw new RuntimeException( "Bad PA edge "+src+" -> "+dst );
        } else if( src instanceof FieldRefNode ) {
            FieldRefNode fsrc = (FieldRefNode) src;
            load.add( fsrc.base(), fsrc.field(), (VarNode) dst );
        } else if( src instanceof AllocNode ) {
            alloc.add( (AllocNode) src, (VarNode) dst );
        } else throw new RuntimeException( "Bad PA edge "+src+" -> "+dst );
    }
}

