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

package soot.jimple.spark.builder;
import soot.jimple.spark.*;
import soot.jimple.spark.pag.*;
import soot.jimple.*;
import soot.*;
import soot.util.*;

import java.util.*;
import soot.jimple.spark.internal.*;
import soot.jimple.toolkits.callgraph.Edge;

/** Class implementing builder parameters (this decides
 * what kinds of nodes should be built for each kind of Soot value).
 * @author Ondrej Lhotak
 */
public class StandardParms extends AbstractJimpleValueSwitch implements Parms {
    public StandardParms( PAG pag, MethodPAG mpag ) {
	this.pag = pag;
	this.mpag = mpag;
        setCurrentMethod( mpag==null ? null : mpag.getMethod() );
    }
    /** Sets the method for which a graph is currently being built. */
    private void setCurrentMethod( SootMethod m ) {
	currentMethod = m;
        if( m != null ) {
            if( !m.isStatic() ) {
                SootClass c = m.getDeclaringClass();
                if( c == null ) {
                    throw new RuntimeException( "Method "+m+" has no dclaring lass" );
                }
                caseThis( m );
            }
            for( int i = 0; i < m.getParameterCount(); i++ ) {
                if( m.getParameterType(i) instanceof RefLikeType ) {
                    caseParm( m, i );
                }
            }
            Type retType = m.getReturnType();
            if( retType instanceof RefLikeType ) {
                caseRet( m );
            }
        }
    }

    final public void addCallTarget( Edge e ) {
        if( !e.passesParameters() ) return;
        if( e.isExplicit() ) {
            addCallTarget( (Stmt) e.srcUnit(), e.tgt(), null );
        } else {
            switch( e.kind() ) {
                case Edge.THREAD:
                    addCallTarget( (Stmt) e.srcUnit(), e.tgt(), null );
                    break;
                case Edge.PRIVILEGED:
                    Node ret = caseRet( e.tgt() ).getReplacement();
                    SootClass accessController = RefType.v( "java.security.AccessController" ).getSootClass();
                    final String[] methods = {
                        "java.lang.Object doPrivileged(java.security.PrivilegedAction)",
                        "java.lang.Object doPrivileged(java.security.PrivilegedAction,java.security.AccessControlContext)",
                        "java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction)",
                        "java.lang.Object doPrivileged(java.security.PrivilegedExceptionAction,java.security.AccessControlContext)"
                    };
                    for( int i = 0; i < methods.length; i++ ) {
                        Node doPrivRet =
                            caseRet( accessController.getMethod( methods[i] ) )
                                .getReplacement();
                        addEdge( ret, doPrivRet );
                    }
                    // FALL THROUGH
                case Edge.EXIT:
                case Edge.FINALIZE:
                    Node srcThis = caseThis( e.src() ).getReplacement();
                    Node tgtThis = caseThis( e.tgt() ).getReplacement();
                    addEdge( srcThis, tgtThis );
                    break;
                case Edge.NEWINSTANCE:
                    Stmt s = (Stmt) e.srcUnit();
                    Node newObject;
                    if( s instanceof AssignStmt ) {
                        AssignStmt as = (AssignStmt) s;
                        caseLocal( (Local) as.getRightOp() );
                        newObject = getNode();
                    } else if( s instanceof InvokeExpr ) {
                        newObject = caseRet( e.tgt() ).getReplacement();
                    } else throw new RuntimeException();
                    Node initThis = caseThis( e.tgt() ).getReplacement();
                    addEdge( newObject, initThis );
                    break;
                default:
                    throw new RuntimeException( "Unhandled edge "+e );
            }
        }
    }

    
    /** Adds method target as a possible target of the invoke expression in s.
     * If target is null, only creates the nodes for the call site,
     * without actually connecting them to any target method.
     * TouchedNodes is an out parameter that is filled in with all the
     * nodes to which edges were added by adding the target. It may be
     * null if the caller does not need this information. */
    final public void addCallTarget( Stmt s, SootMethod target,
            Object varNodeParameter ) {
        MethodPAG mpag = null;
        if( target != null ) mpag = MethodPAG.v( pag, target );
        InvokeExpr ie = (InvokeExpr) s.getInvokeExpr();
        int numArgs = ie.getArgCount();
        for( int i = 0; i < numArgs; i++ ) {
            Value arg = ie.getArg( i );
            if( !( arg.getType() instanceof RefLikeType ) ) continue;
            arg.apply( this );
            Node argNode = getNode();
            if( argNode == null ) continue;
            argNode = argNode.getReplacement();
            if( target != null ) {
                Node parm = caseParm( target, i ).getReplacement();
                parm = mpag.parameterize( parm, varNodeParameter );
                addEdge( argNode, parm );
            }
        }
        if( ie instanceof InstanceInvokeExpr ) {
            InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
            iie.getBase().apply( this );
            Node baseNode = getNode().getReplacement();
            if( target != null ) {
                Node thisRef = caseThis( target ).getReplacement();
                thisRef = mpag.parameterize( thisRef, varNodeParameter );
                addEdge( baseNode, thisRef );
            }
        }
        if( s instanceof AssignStmt ) {
            Value dest = ( (AssignStmt) s ).getLeftOp();
            if( dest.getType() instanceof RefLikeType ) {
                dest.apply( this );
                Node destNode = getNode().getReplacement();
                if( target != null ) {
                    Node retNode = caseRet( target ).getReplacement();
                    retNode = mpag.parameterize( retNode, varNodeParameter );
                    addEdge( retNode, destNode );
                }
            }
        }
    }
    /** Adds the edges required for this statement to the graph. */
    final public void handleStmt( Stmt s ) {
	if( s.containsInvokeExpr() ) {
            addCallTarget( s, null, null );
	    return;
	}
	s.apply( new AbstractStmtSwitch() {
	    final public void caseAssignStmt(AssignStmt as) {
                Value l = as.getLeftOp();
                Value r = as.getRightOp();
		if( !( l.getType() instanceof RefLikeType ) ) return;
		l.apply( StandardParms.this );
		Node dest = getNode();
		r.apply( StandardParms.this );
		Node src = getNode();
                if( l instanceof InstanceFieldRef ) {
                    ((InstanceFieldRef) l).getBase().apply( StandardParms.this );
                    pag.addDereference( (VarNode) getNode() );
                }
                if( r instanceof InstanceFieldRef ) {
                    ((InstanceFieldRef) r).getBase().apply( StandardParms.this );
                    pag.addDereference( (VarNode) getNode() );
                }
		addEdge( src, dest );
	    }
	    final public void caseReturnStmt(ReturnStmt rs) {
		if( !( rs.getOp().getType() instanceof RefLikeType ) ) return;
		rs.getOp().apply( StandardParms.this );
                Node retNode = getNode();
                addEdge( retNode, caseRet( currentMethod ) );
	    }
	    final public void caseIdentityStmt(IdentityStmt is) {
		if( !( is.getLeftOp().getType() instanceof RefLikeType ) ) return;
		is.getLeftOp().apply( StandardParms.this );
		Node dest = getNode();
		is.getRightOp().apply( StandardParms.this );
		Node src = getNode();
		addEdge( src, dest );
	    }
	    final public void caseThrowStmt(ThrowStmt ts) {
		ts.getOp().apply( StandardParms.this );
		addEdge( getNode(), caseThrow() );
	    }
	} );
    }
    final public Node getNode() {
	return (Node) getResult();
    }
    final public void addEdge( Node from, Node to ) {
        if( from != null ) {
            if( mpag != null ) {
                mpag.addEdge( from, to );
            } else {
                pag.addEdge( from, to );
            }
        }
    }
    final public Node caseDefaultClassLoader() {
	AllocNode a = pag.makeAllocNode( 
		PointsToAnalysis.DEFAULT_CLASS_LOADER,
		AnySubType.v( RefType.v( "java.lang.ClassLoader" ) ), null );
	VarNode v = pag.makeVarNode(
		PointsToAnalysis.DEFAULT_CLASS_LOADER_LOCAL,
		RefType.v( "java.lang.ClassLoader" ), null );
	addEdge( a, v );
	return v;
    }
    final public Node caseMainClassNameString() {
	AllocNode a = pag.makeAllocNode( 
		PointsToAnalysis.MAIN_CLASS_NAME_STRING,
		RefType.v( "java.lang.String" ), null );
	VarNode v = pag.makeVarNode(
		PointsToAnalysis.MAIN_CLASS_NAME_STRING_LOCAL,
		RefType.v( "java.lang.String" ), null );
	addEdge( a, v );
	return v;
    }
    final public Node caseMainThreadGroup() {
	AllocNode threadGroupNode = pag.makeAllocNode( 
		PointsToAnalysis.MAIN_THREAD_GROUP_NODE,
		RefType.v("java.lang.ThreadGroup"), null );
	VarNode threadGroupNodeLocal = pag.makeVarNode(
		PointsToAnalysis.MAIN_THREAD_GROUP_NODE_LOCAL,
		RefType.v("java.lang.ThreadGroup"), null );
	addEdge( threadGroupNode, threadGroupNodeLocal );
	return threadGroupNodeLocal;
    }
    final public Node caseMainThread() {
	AllocNode threadNode = pag.makeAllocNode( 
		PointsToAnalysis.MAIN_THREAD_NODE,
		RefType.v("java.lang.Thread"), null );
	VarNode threadNodeLocal = pag.makeVarNode(
		PointsToAnalysis.MAIN_THREAD_NODE_LOCAL,
		RefType.v("java.lang.Thread"), null );
	addEdge( threadNode, threadNodeLocal );
	return threadNodeLocal;
    }
    final public Node caseArgv() {
	AllocNode argv = pag.makeAllocNode( 
		PointsToAnalysis.STRING_ARRAY_NODE,
		ArrayType.v(RefType.v( "java.lang.String" ), 1), null );
        VarNode sanl = pag.makeVarNode(
                PointsToAnalysis.STRING_ARRAY_NODE_LOCAL,
                ArrayType.v(RefType.v( "java.lang.String" ), 1), null );
	AllocNode stringNode = pag.makeAllocNode( 
		PointsToAnalysis.STRING_NODE,
		RefType.v( "java.lang.String" ), null );
	VarNode stringNodeLocal = pag.makeVarNode(
		PointsToAnalysis.STRING_NODE_LOCAL,
		RefType.v( "java.lang.String" ), null );
	addEdge( argv, sanl );
	addEdge( stringNode, stringNodeLocal );
	addEdge( stringNodeLocal, 
                pag.makeFieldRefNode( sanl, ArrayElement.v() ) );
	return sanl;
    }
    final public Node caseThis( SootMethod m ) {
	VarNode ret = pag.makeVarNode(
		    new Pair( m, PointsToAnalysis.THIS_NODE ),
		    m.getDeclaringClass().getType(), m );
        ret.setInterProcTarget();
        return ret;
    }

    final public Node caseParm( SootMethod m, int index ) {
	if( m.isStatic() || !pag.getOpts().parms_as_fields() ) {
	    VarNode ret = pag.makeVarNode(
			new Pair( m, new Integer( index ) ),
			m.getParameterType( index ), m );
            ret.setInterProcTarget();
            return ret;
	} else { 
	    return pag.makeFieldRefNode(
			new Pair( m, PointsToAnalysis.THIS_NODE ),
			m.getDeclaringClass().getType(),
			Parm.v( m, index ), m );
	}
    }

    final public Node caseRet( SootMethod m ) {
	if( m.isStatic() || !pag.getOpts().returns_as_fields() ) {
	    VarNode ret = pag.makeVarNode(
			Parm.v( m, PointsToAnalysis.RETURN_NODE ),
			m.getReturnType(), m );
            ret.setInterProcSource();
            return ret;
	} else { 
	    return pag.makeFieldRefNode(
			new Pair( m, PointsToAnalysis.THIS_NODE ),
			m.getDeclaringClass().getType(),
			Parm.v( m, PointsToAnalysis.RETURN_NODE ), m );
	}
    }
    final public Node caseArray( Object base, ArrayType arrayType ) {
	return pag.makeFieldRefNode( base, arrayType,
		    ArrayElement.v(), currentMethod );
    }
    /* End of public methods. */
    /* End of package methods. */

    // OK, these ones are public, but they really shouldn't be; it's just
    // that Java requires them to be, because they override those other
    // public methods.
    final public void caseArrayRef( ArrayRef ar ) {
	setResult( caseArray( ar.getBase(), (ArrayType) ar.getBase().getType() ) );
    }
    final public void caseCastExpr( CastExpr ce ) {
	Pair castPair = new Pair( ce, PointsToAnalysis.CAST_NODE );
	ce.getOp().apply( this );
	Node opNode = getNode();
	Node castNode = pag.makeVarNode( castPair, ce.getCastType(), currentMethod );
	addEdge( opNode, castNode );
	setResult( castNode );
    }
    final public void caseCaughtExceptionRef( CaughtExceptionRef cer ) {
	setResult( caseThrow() );
    }
    final public void caseInstanceFieldRef( InstanceFieldRef ifr ) {
	if( pag.getOpts().field_based() || pag.getOpts().vta() ) {
	    setResult( pag.makeVarNode( 
			ifr.getField(), 
			ifr.getField().getType(), currentMethod ) );
	} else {
	    setResult( pag.makeFieldRefNode( 
			ifr.getBase(), 
			ifr.getBase().getType(),
			ifr.getField(),
                        currentMethod ) );
	}
    }
    final public void caseLocal( Local l ) {
	setResult( pag.makeVarNode( l,  l.getType(), currentMethod ) );
    }
    final public void caseNewArrayExpr( NewArrayExpr nae ) {
        setResult( pag.makeAllocNode( nae, nae.getType(), currentMethod ) );
    }
    final public void caseNewExpr( NewExpr ne ) {
        if( pag.getOpts().merge_stringbuffer() 
        && ne.getType().equals( RefType.v("java.lang.StringBuffer" ) ) ) {
            setResult( pag.makeAllocNode( ne.getType(), ne.getType(), null ) );
        } else {
            setResult( pag.makeAllocNode( ne, ne.getType(), currentMethod ) );
        }
    }
    final public void caseNewMultiArrayExpr( NewMultiArrayExpr nmae ) {
        ArrayType type = (ArrayType) nmae.getType();
        AllocNode prevAn = pag.makeAllocNode(
            new Pair( nmae, new Integer( type.numDimensions ) ), type, currentMethod );
        VarNode prevVn = pag.makeVarNode( prevAn, prevAn.getType(), currentMethod );
        setResult( prevAn );
        while( true ) {
            Type t = type.getElementType();
            if( !( t instanceof ArrayType ) ) break;
            type = (ArrayType) t;
            AllocNode an = pag.makeAllocNode(
                new Pair( nmae, new Integer( type.numDimensions ) ), type, currentMethod );
            VarNode vn = pag.makeVarNode( an, an.getType(), currentMethod );
            addEdge( an, vn );
            addEdge( vn, pag.makeFieldRefNode( prevVn, ArrayElement.v() ) );
            prevAn = an;
            prevVn = vn;
        }
    }
    final public void caseParameterRef( ParameterRef pr ) {
	setResult( caseParm( currentMethod, pr.getIndex() ) );
    }
    final public void caseStaticFieldRef( StaticFieldRef sfr ) {
	setResult( pag.makeVarNode( 
		    sfr.getField(), 
		    sfr.getField().getType(), null ) );
    }
    final public void caseStringConstant( StringConstant sc ) {
        AllocNode stringConstant;
        if( Scene.v().containsClass(sc.value) 
        || ( sc.value.length() > 0 && sc.value.charAt(0) == '[' ) ) {
            stringConstant = pag.makeStringConstantNode( sc.value );
        } else {
            stringConstant = pag.makeAllocNode(
                PointsToAnalysis.STRING_NODE,
                RefType.v( "java.lang.String" ), null );
        }
        VarNode stringConstantLocal = pag.makeVarNode(
            stringConstant,
            RefType.v( "java.lang.String" ), null );
        addEdge( stringConstant, stringConstantLocal );
        setResult( stringConstantLocal );
    }
    final public void caseThisRef( ThisRef tr ) {
	setResult( caseThis( currentMethod ) );
    }
    final public void caseNullConstant( NullConstant nr ) {
	setResult( null );
    }
    final public void defaultCase( Object v ) {
	throw new RuntimeException( "failed to handle "+v );
    }
    protected Node caseThrow() {
	VarNode ret = pag.makeVarNode( PointsToAnalysis.EXCEPTION_NODE,
		    RefType.v("java.lang.Throwable"), null );
        ret.setInterProcTarget();
        ret.setInterProcSource();
        return ret;
    }
    protected PAG pag;
    protected MethodPAG mpag;
    protected SootMethod currentMethod;
}

