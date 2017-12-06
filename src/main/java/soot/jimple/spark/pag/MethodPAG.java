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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.Context;
import soot.EntryPoints;
import soot.G;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.Stmt;
import soot.jimple.spark.builder.MethodNodeFactory;
import soot.jimple.spark.internal.SparkLibraryHelper;
import soot.options.CGOptions;
import soot.util.NumberedString;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;


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

    private Set<Context> addedContexts;

    /** Adds this method to the main PAG, with all VarNodes parameterized by
     * varNodeParameter. */
    public void addToPAG( Context varNodeParameter ) {
        if( !hasBeenBuilt ) throw new RuntimeException();
        if( varNodeParameter == null ) {
            if( hasBeenAdded ) return;
            hasBeenAdded = true;
        } else {
            if( addedContexts == null ) addedContexts = new HashSet<Context>();
            if( !addedContexts.add( varNodeParameter ) ) return;
        }
        QueueReader<Node> reader = internalReader.clone();
        while(reader.hasNext()) {
            Node src = (Node) reader.next();
            src = parameterize( src, varNodeParameter );
            Node dst = (Node) reader.next();
            dst = parameterize( dst, varNodeParameter );
            pag.addEdge( src, dst );
        }
        reader = inReader.clone();
        while(reader.hasNext()) {
            Node src = (Node) reader.next();
            Node dst = (Node) reader.next();
            dst = parameterize( dst, varNodeParameter );
            pag.addEdge( src, dst );
        }
        reader = outReader.clone();
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
    private final ChunkedQueue<Node> internalEdges = new ChunkedQueue<Node>();
    private final ChunkedQueue<Node> inEdges = new ChunkedQueue<Node>();
    private final ChunkedQueue<Node> outEdges = new ChunkedQueue<Node>();
    private final QueueReader<Node> internalReader = internalEdges.reader();
    private final QueueReader<Node> inReader = inEdges.reader();
    private final QueueReader<Node> outReader = outEdges.reader();
    
    SootMethod method;
    public SootMethod getMethod() { return method; }
    protected MethodNodeFactory nodeFactory;
    public MethodNodeFactory nodeFactory() { return nodeFactory; }

    public static MethodPAG v( PAG pag, SootMethod m ) {
        MethodPAG ret = G.v().MethodPAG_methodToPag.get( m );
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
        for (Unit u : b.getUnits())
            nodeFactory.handleStmt( (Stmt) u );
    }
    protected void buildNative() {
        ValNode thisNode = null;
        ValNode retNode = null;
        if( !method.isStatic() ) { 
	    thisNode = (ValNode) nodeFactory.caseThis();
        }
        if(method.getReturnType() instanceof RefLikeType ) {
        	retNode = (ValNode) nodeFactory.caseRet();
        	
        	// on library analysis we assume that the return type of an native method can 
        	// be anything matching to the declared type.
        	if (pag.getCGOpts().library() != CGOptions.library_disabled) {        		
        		Type retType = method.getReturnType();	    	
        		
        		retType.apply(new SparkLibraryHelper(pag, retNode, method));
        	}
        }
        ValNode[] args = new ValNode[ method.getParameterCount() ];
        for( int i = 0; i < method.getParameterCount(); i++ ) {
            if( !( method.getParameterType(i) instanceof RefLikeType ) ) continue;
            args[i] = (ValNode) nodeFactory.caseParm(i);
        }
        pag.nativeMethodDriver.process( method, thisNode, retNode, args );
    }
    
    private final static String mainSubSignature =
    		SootMethod.getSubSignature( "main", Collections.<Type>singletonList( ArrayType.v(RefType.v("java.lang.String"), 1) ), VoidType.v() );

    protected void addMiscEdges() {
        // Add node for parameter (String[]) in main method
        final String signature = method.getSignature(); 
        if( method.getSubSignature().equals( mainSubSignature )) {
            addInEdge( pag().nodeFactory().caseArgv(), nodeFactory.caseParm(0) );
        } else

        if(signature.equals(
                    "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>" ) ) {
            addInEdge( pag().nodeFactory().caseMainThread(), nodeFactory.caseThis() );
            addInEdge( pag().nodeFactory().caseMainThreadGroup(), nodeFactory.caseParm( 0 ) );
        } else

        if (signature.equals(
                "<java.lang.ref.Finalizer: void <init>(java.lang.Object)>")) {
            addInEdge( nodeFactory.caseThis(), pag().nodeFactory().caseFinalizeQueue());
        } else
        	
        if (signature.equals(
                "<java.lang.ref.Finalizer: void runFinalizer()>")) {
            addInEdge(pag.nodeFactory().caseFinalizeQueue(), nodeFactory.caseThis());
        } else

        if (signature.equals(
                "<java.lang.ref.Finalizer: void access$100(java.lang.Object)>")) {
            addInEdge(pag.nodeFactory().caseFinalizeQueue(), nodeFactory.caseParm(0));
        } else

        if (signature.equals(
                "<java.lang.ClassLoader: void <init>()>")) {
            addInEdge(pag.nodeFactory().caseDefaultClassLoader(), nodeFactory.caseThis());
        } else

        if (signature.equals("<java.lang.Thread: void exit()>")) {
            addInEdge(pag.nodeFactory().caseMainThread(), nodeFactory.caseThis());
        } else

        if (signature.equals(
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
        for (SootMethod implicitMethod : EntryPoints.v().implicit()) {
         if (implicitMethod.getNumberedSubSignature().equals(
		    method.getNumberedSubSignature())) {
        	 isImplicit = true;
        	 break;
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

