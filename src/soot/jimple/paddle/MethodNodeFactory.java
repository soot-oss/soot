/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002, 2003, 2004 Ondrej Lhotak
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
import soot.jimple.*;
import soot.*;
import soot.util.*;
import soot.toolkits.scalar.Pair;
import java.util.*;

/** Class implementing builder parameters (this decides
 * what kinds of nodes should be built for each kind of Soot value).
 * @author Ondrej Lhotak
 */
public class MethodNodeFactory extends AbstractJimpleValueSwitch {
    public MethodNodeFactory( SootMethod method ) {
        this.method = method;
    }

    private SootMethod method;
    private NodeFactory gnf = PaddleScene.v().nodeFactory();
    private NodeManager nm = PaddleScene.v().nodeManager();

    public Node getNode( Value v ) {
        v.apply( this );
        return getNode();
    }
    /** Adds the edges required for this statement to the graph. */
    final public void handleStmt( Stmt s ) {
	if( s.containsInvokeExpr() ) {
	    return;
	}
	s.apply( new AbstractStmtSwitch() {
	    final public void caseAssignStmt(AssignStmt as) {
                Value l = as.getLeftOp();
                Value r = as.getRightOp();
		if( !( l.getType() instanceof RefLikeType ) ) return;
		l.apply( MethodNodeFactory.this );
		Node dest = getNode();
		r.apply( MethodNodeFactory.this );
		Node src = getNode();
		gnf.addEdge( src, dest );
	    }
	    final public void caseReturnStmt(ReturnStmt rs) {
		if( !( rs.getOp().getType() instanceof RefLikeType ) ) return;
		rs.getOp().apply( MethodNodeFactory.this );
                Node retNode = getNode();
                gnf.addEdge( retNode, caseRet() );
	    }
	    final public void caseIdentityStmt(IdentityStmt is) {
		if( !( is.getLeftOp().getType() instanceof RefLikeType ) ) return;
		is.getLeftOp().apply( MethodNodeFactory.this );
		Node dest = getNode();
		is.getRightOp().apply( MethodNodeFactory.this );
		Node src = getNode();
		gnf.addEdge( src, dest );
	    }
	    final public void caseThrowStmt(ThrowStmt ts) {
		ts.getOp().apply( MethodNodeFactory.this );
		gnf.addEdge( getNode(), gnf.caseThrow() );
	    }
	} );
    }
    final public Node getNode() {
	return (Node) getResult();
    }
    final public Node caseThis() {
	VarNode ret = nm.makeLocalVarNode(
		    new Pair( method, PointsToAnalysis.THIS_NODE ),
		    method.getDeclaringClass().getType(), method );
        return ret;
    }

    final public Node caseParm( int index ) {
        VarNode ret = nm.makeLocalVarNode(
                    new Pair( method, new Integer( index ) ),
                    method.getParameterType( index ), method );
        return ret;
    }

    final public Node caseRet() {
        VarNode ret = nm.makeLocalVarNode(
                    Parm.v( method, PointsToAnalysis.RETURN_NODE ),
                    method.getReturnType(), method );
        return ret;
    }
    final public Node caseArray( VarNode base ) {
	return FieldRefNode.make( base, ArrayElement.v() );
    }
    /* End of public methods. */
    /* End of package methods. */

    // OK, these ones are public, but they really shouldn't be; it's just
    // that Java requires them to be, because they override those other
    // public methods.
    final public void caseArrayRef( ArrayRef ar ) {
    	caseLocal( (Local) ar.getBase() );
	setResult( caseArray( (VarNode) getNode() ) );
    }
    final public void caseCastExpr( CastExpr ce ) {
	Pair castPair = new Pair( ce, PointsToAnalysis.CAST_NODE );
	ce.getOp().apply( this );
	Node opNode = getNode();
	Node castNode = nm.makeLocalVarNode( castPair, ce.getCastType(), method );
	gnf.addEdge( opNode, castNode );
	setResult( castNode );
    }
    final public void caseCaughtExceptionRef( CaughtExceptionRef cer ) {
	setResult( gnf.caseThrow() );
    }
    final public void caseInstanceFieldRef( InstanceFieldRef ifr ) {
	if( PaddleScene.v().options().field_based() || PaddleScene.v().options().vta() || PaddleScene.v().options().rta() ) {
	    setResult( nm.makeGlobalVarNode( 
			ifr.getField(), 
			ifr.getField().getType() ) );
	} else {
	    setResult( FieldRefNode.make( nm.makeLocalVarNode( 
			ifr.getBase(), ifr.getBase().getType(), method ), ifr.getField() ) );
	}
    }
    final public void caseLocal( Local l ) {
	setResult( nm.makeLocalVarNode( l,  l.getType(), method ) );
    }
    final public void caseNewArrayExpr( NewArrayExpr nae ) {
        setResult( makeAllocNode( nae, nae.getType(), method ) );
    }
    final public void caseNewExpr( NewExpr ne ) {
        if( PaddleScene.v().options().merge_stringbuffer() 
        && ne.getType().equals( RefType.v("java.lang.StringBuffer" ) ) ) {
            setResult( nm.makeGlobalAllocNode( ne.getType(), ne.getType() ) );
        } else {
            setResult( makeAllocNode( ne, ne.getType(), method ) );
        }
    }
    private AllocNode makeAllocNode( Object ne, Type type, SootMethod method ) {
        if( PaddleScene.v().options().context_heap() ) {
            return nm.makeLocalAllocNode(ne, type, method);
        } else {
            return nm.makeGlobalAllocNode(ne, type, method);
        }
    }
    final public void caseNewMultiArrayExpr( NewMultiArrayExpr nmae ) {
        ArrayType type = (ArrayType) nmae.getType();
        AllocNode prevAn = makeAllocNode(
            new Pair( nmae, new Integer( type.numDimensions ) ), type, method );
        VarNode prevVn = nm.makeLocalVarNode( prevAn, prevAn.getType(), method );
        gnf.addEdge( prevAn, prevVn );
        setResult( prevAn );
        while( true ) {
            Type t = type.getElementType();
            if( !( t instanceof ArrayType ) ) break;
            type = (ArrayType) t;
            AllocNode an = makeAllocNode(
                new Pair( nmae, new Integer( type.numDimensions ) ), type, method );
            VarNode vn = nm.makeLocalVarNode( an, an.getType(), method );
            gnf.addEdge( an, vn );
            gnf.addEdge( vn, FieldRefNode.make( prevVn, ArrayElement.v() ) );
            prevAn = an;
            prevVn = vn;
        }
    }
    final public void caseParameterRef( ParameterRef pr ) {
	setResult( caseParm( pr.getIndex() ) );
    }
    final public void caseStaticFieldRef( StaticFieldRef sfr ) {
	setResult( nm.makeGlobalVarNode( 
		    sfr.getField(), 
		    sfr.getField().getType() ) );
    }
    final public void caseStringConstant( StringConstant sc ) {
        AllocNode stringConstant;
        if( PaddleScene.v().options().string_constants()
        || Scene.v().containsClass(sc.value) 
        || ( sc.value.length() > 0 && sc.value.charAt(0) == '[' ) ) {
            stringConstant = nm.makeStringConstantNode( sc.value );
        } else {
            stringConstant = nm.makeGlobalAllocNode(
                PointsToAnalysis.STRING_NODE,
                RefType.v( "java.lang.String" ) );
        }
        VarNode stringConstantLocal = nm.makeGlobalVarNode(
            stringConstant,
            RefType.v( "java.lang.String" ) );
        gnf.addEdge( stringConstant, stringConstantLocal );
        setResult( stringConstantLocal );
    }
    final public void caseThisRef( ThisRef tr ) {
	setResult( caseThis() );
    }
    final public void caseNullConstant( NullConstant nr ) {
	setResult( null );
    }
    final public void defaultCase( Object v ) {
	throw new RuntimeException( "failed to handle "+v );
    }
    public void addMiscEdges() {
        // Add node for parameter (String[]) in main method
        if( method.getSubSignature().equals( SootMethod.getSubSignature( "main", new SingletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v() ) ) ) {
            gnf.addEdge( gnf.caseArgv(), caseParm(0) );
        }

        if( method.getSignature().equals(
                    "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>" ) ) {
            gnf.addEdge( gnf.caseMainThread(), caseThis() );
            gnf.addEdge( gnf.caseMainThreadGroup(), caseParm( 0 ) );
        }

        if( method.getSignature().equals(
                    "<java.lang.ref.Finalizer: void <init>(java.lang.Object)>") ) {
            gnf.addEdge( caseThis(), gnf.caseFinalizeQueue() );
        }
        if( method.getSignature().equals(
                    "<java.lang.ref.Finalizer: void runFinalizer()>") ) {
            gnf.addEdge( gnf.caseFinalizeQueue(), caseThis() );
        }

        if( method.getSignature().equals(
                    "<java.lang.ref.Finalizer: void access$100(java.lang.Object)>") ) {
            gnf.addEdge( gnf.caseFinalizeQueue(), caseParm(0) );
        }

        if( method.getSignature().equals(
                    "<java.lang.ClassLoader: void <init>()>") ) {
            gnf.addEdge( gnf.caseDefaultClassLoader(), caseThis() );
        }

        if( method.getSignature().equals(
                    "<java.lang.Thread: void exit()>") ) {
            gnf.addEdge( gnf.caseMainThread(), caseThis() );
        }

        if( method.getSignature().equals(
                    "<java.security.PrivilegedActionException: void <init>(java.lang.Exception)>") ) {
            gnf.addEdge( gnf.caseThrow(), caseParm(0) );
            gnf.addEdge( gnf.casePrivilegedActionException(), caseThis() );
        }

        if( method.getNumberedSubSignature().equals( sigCanonicalize ) ) {
            SootClass cl = method.getDeclaringClass();
            while(true) {
                if( cl.equals( Scene.v().getSootClass("java.io.FileSystem") ) ) {
                    gnf.addEdge( gnf.caseCanonicalPath(), caseRet() );
                }
                if( !cl.hasSuperclass() ) break;
                cl = cl.getSuperclass();
            }
        }

        boolean isImplicit = false;
        for( Iterator implicitMethodIt = EntryPoints.v().implicit().iterator(); implicitMethodIt.hasNext(); ) {
            final SootMethod implicitMethod = (SootMethod) implicitMethodIt.next();
            if( implicitMethod.getNumberedSubSignature().equals(
                        method.getNumberedSubSignature() ) ) {
                isImplicit = true;
            }
        }
        if( isImplicit ) {
            SootClass c = method.getDeclaringClass();
outer:      do {
                while( !c.getName().equals( "java.lang.ClassLoader" ) ) {
                    if( !c.hasSuperclass() ) {
                        break outer;
                    }
                    c = c.getSuperclass();
                }
                if( method.getName().equals("<init>") ) continue;
                gnf.addEdge( gnf.caseDefaultClassLoader(),
                        caseThis() );
                gnf.addEdge( gnf.caseMainClassNameString(),
                        caseParm(0) );
            } while( false );
        }
    }
    protected final NumberedString sigCanonicalize = Scene.v().getSubSigNumberer().
        findOrAdd("java.lang.String canonicalize(java.lang.String)");
}

