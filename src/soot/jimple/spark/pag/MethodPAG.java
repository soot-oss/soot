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

package soot.jimple.spark.pag;
import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.jimple.spark.*;
import soot.jimple.spark.builder.*;
import soot.jimple.spark.internal.*;
import soot.util.*;
import soot.util.queue.*;
import soot.toolkits.scalar.Pair;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;


/** Part of a pointer assignment graph for a single method.
 * @author Ondrej Lhotak
 */
public final class MethodPAG {
    private PAG pag;
    public PAG pag() { return pag; }

    protected MethodPAG( PAG pag, SootMethod m ) {
        this.pag = pag;
        this.method = m;
        this.nodeFactory = new MethodNodeFactory( pag, this );
    }

    private Set addedContexts;

    /** Adds this method to the main PAG, with all VarNodes parameterized by
     * varNodeParameter. */
    public void addToPAG( Context varNodeParameter ) {
        if( !hasBeenBuilt ) throw new RuntimeException();
        if( varNodeParameter == null ) {
            if( hasBeenAdded ) return;
            hasBeenAdded = true;
        } else {
            if( addedContexts == null ) addedContexts = new HashSet();
            if( !addedContexts.add( varNodeParameter ) ) return;
        }
        QueueReader reader = (QueueReader) internalReader.clone();
        while(reader.hasNext()) {
            Node src = (Node) reader.next();
            src = parameterize( src, varNodeParameter );
            Node dst = (Node) reader.next();
            dst = parameterize( dst, varNodeParameter );
            pag.addEdge( src, dst );
        }
        reader = (QueueReader) inReader.clone();
        while(reader.hasNext()) {
            Node src = (Node) reader.next();
            Node dst = (Node) reader.next();
            dst = parameterize( dst, varNodeParameter );
            pag.addEdge( src, dst );
        }
        reader = (QueueReader) outReader.clone();
        while(reader.hasNext()) {
            Node src = (Node) reader.next();
            src = parameterize( src, varNodeParameter );
            Node dst = (Node) reader.next();
            pag.addEdge( src, dst );
        }
    }
    public void addInternalEdge( Node src, Node dst ) {
        if( src == null ) return;
        internalEdges.add( src );
        internalEdges.add( dst );
        if (hasBeenAdded) {
            pag.addEdge(src, dst);
        }        
    }
    public void addInEdge( Node src, Node dst ) {
        if( src == null ) return;
        inEdges.add( src );
        inEdges.add( dst );
        if (hasBeenAdded) {
            pag.addEdge(src, dst);
        }        
    }
    public void addOutEdge( Node src, Node dst ) {
        if( src == null ) return;
        outEdges.add( src );
        outEdges.add( dst );
        if (hasBeenAdded) {
            pag.addEdge(src, dst);
        }        
    }
    private ChunkedQueue internalEdges = new ChunkedQueue();
    private ChunkedQueue inEdges = new ChunkedQueue();
    private ChunkedQueue outEdges = new ChunkedQueue();
    private QueueReader internalReader = internalEdges.reader();
    private QueueReader inReader = inEdges.reader();
    private QueueReader outReader = outEdges.reader();

    SootMethod method;
    public SootMethod getMethod() { return method; }
    protected MethodNodeFactory nodeFactory;
    public MethodNodeFactory nodeFactory() { return nodeFactory; }

    public static MethodPAG v( PAG pag, SootMethod m ) {
        MethodPAG ret = (MethodPAG) G.v().MethodPAG_methodToPag.get( m );
        if( ret == null ) { 
            ret = new MethodPAG( pag, m );
            G.v().MethodPAG_methodToPag.put( m, ret );
        }
        return ret;
    }

    public void build() {
        if( hasBeenBuilt ) return;
        hasBeenBuilt = true;
        if( method.isNative() ) {
            if( pag().getOpts().simulate_natives() ) {
                buildNative();
            }
        } else {
            if( method.isConcrete() && !method.isPhantom() ) {
                buildNormal();
            }
        }
        addMiscEdges();
    }

    protected VarNode parameterize( LocalVarNode vn, Context varNodeParameter ) {
        SootMethod m = vn.getMethod();
        if( m != method && m != null ) throw new RuntimeException( "VarNode "+vn+" with method "+m+" parameterized in method "+method );
        //System.out.println( "parameterizing "+vn+" with "+varNodeParameter );
        return pag().makeContextVarNode( vn, varNodeParameter );
    }
    protected FieldRefNode parameterize( FieldRefNode frn, Context varNodeParameter ) {
        return pag().makeFieldRefNode(
                (VarNode) parameterize( frn.getBase(), varNodeParameter ),
                frn.getField() );
    }
    public Node parameterize( Node n, Context varNodeParameter ) {
        if( varNodeParameter == null ) return n;
        if( n instanceof LocalVarNode ) 
            return parameterize( (LocalVarNode) n, varNodeParameter);
        if( n instanceof FieldRefNode )
            return parameterize( (FieldRefNode) n, varNodeParameter);
        return n;
    }
    protected boolean hasBeenAdded = false;
    protected boolean hasBeenBuilt = false;

    protected void buildNormal() {
        Body b = method.retrieveActiveBody();
        Iterator unitsIt = b.getUnits().iterator();
        while( unitsIt.hasNext() )
        {
            Stmt s = (Stmt) unitsIt.next();
            nodeFactory.handleStmt( s );
        }
    }
    protected void buildNative() {
        ValNode thisNode = null;
        ValNode retNode = null; 
        if( !method.isStatic() ) { 
	    thisNode = (ValNode) nodeFactory.caseThis();
        }
        if( method.getReturnType() instanceof RefLikeType ) {
	    retNode = (ValNode) nodeFactory.caseRet();
	}
        ValNode[] args = new ValNode[ method.getParameterCount() ];
        for( int i = 0; i < method.getParameterCount(); i++ ) {
            if( !( method.getParameterType(i) instanceof RefLikeType ) ) continue;
	    args[i] = (ValNode) nodeFactory.caseParm(i);
        }
        pag.nativeMethodDriver.process( method, thisNode, retNode, args );
    }

    protected void addMiscEdges() {
        // Add node for parameter (String[]) in main method
        if( method.getSubSignature().equals( SootMethod.getSubSignature( "main", new SingletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v() ) ) ) {
            addInEdge( pag().nodeFactory().caseArgv(), nodeFactory.caseParm(0) );
        }

        if( method.getSignature().equals(
                    "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>" ) ) {
            addInEdge( pag().nodeFactory().caseMainThread(), nodeFactory.caseThis() );
            addInEdge( pag().nodeFactory().caseMainThreadGroup(), nodeFactory.caseParm( 0 ) );
        }

        if (method.getSignature().equals(
                "<java.lang.ref.Finalizer: void <init>(java.lang.Object)>")) {
            addInEdge( nodeFactory.caseThis(), pag().nodeFactory().caseFinalizeQueue());
        }
        if (method.getSignature().equals(
                "<java.lang.ref.Finalizer: void runFinalizer()>")) {
            addInEdge(pag.nodeFactory().caseFinalizeQueue(), nodeFactory.caseThis());
        }

        if (method.getSignature().equals(
                "<java.lang.ref.Finalizer: void access$100(java.lang.Object)>")) {
            addInEdge(pag.nodeFactory().caseFinalizeQueue(), nodeFactory.caseParm(0));
        }

        if (method.getSignature().equals(
                "<java.lang.ClassLoader: void <init>()>")) {
            addInEdge(pag.nodeFactory().caseDefaultClassLoader(), nodeFactory.caseThis());
        }

        if (method.getSignature().equals("<java.lang.Thread: void exit()>")) {
            addInEdge(pag.nodeFactory().caseMainThread(), nodeFactory.caseThis());
        }

        if (method
                .getSignature()
                .equals(
                        "<java.security.PrivilegedActionException: void <init>(java.lang.Exception)>")) {
            addInEdge(pag.nodeFactory().caseThrow(), nodeFactory.caseParm(0));
            addInEdge(pag.nodeFactory().casePrivilegedActionException(), nodeFactory.caseThis());
        }

        if (method.getNumberedSubSignature().equals(sigCanonicalize)) {
            SootClass cl = method.getDeclaringClass();
            while (true) {
                if (cl.equals(Scene.v().getSootClass("java.io.FileSystem"))) {
                    addInEdge(pag.nodeFactory().caseCanonicalPath(), nodeFactory.caseRet());
                }
                if (!cl.hasSuperclass())
                    break;
                cl = cl.getSuperclass();
            }
        }

        boolean isImplicit = false;
        for (Iterator implicitMethodIt = EntryPoints.v().implicit().iterator(); implicitMethodIt
                .hasNext();) {
            final SootMethod implicitMethod = (SootMethod) implicitMethodIt
                    .next();
            if (implicitMethod.getNumberedSubSignature().equals(
                    method.getNumberedSubSignature())) {
                isImplicit = true;
            }
        }
        if (isImplicit) {
            SootClass c = method.getDeclaringClass();
            outer: do {
                while (!c.getName().equals("java.lang.ClassLoader")) {
                    if (!c.hasSuperclass()) {
                        break outer;
                    }
                    c = c.getSuperclass();
                }
                if (method.getName().equals("<init>"))
                    continue;
                addInEdge(pag().nodeFactory().caseDefaultClassLoader(),
                        nodeFactory.caseThis());
                addInEdge(pag().nodeFactory().caseMainClassNameString(),
                        nodeFactory.caseParm(0));
            } while (false);
        }
    }


    protected final NumberedString sigCanonicalize = Scene.v().getSubSigNumberer().
    findOrAdd("java.lang.String canonicalize(java.lang.String)");
}

