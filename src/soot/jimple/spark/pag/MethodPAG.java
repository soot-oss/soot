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
import soot.util.*;
import soot.util.queue.*;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;


/** Part of a pointer assignment graph for a single method.
 * @author Ondrej Lhotak
 */
public final class MethodPAG {
    SootMethod method;
    PAG pag;
    Parms parms;
    static HashMap methodToPag = new HashMap();
    public static MethodPAG v( PAG pag, SootMethod m ) {
        MethodPAG ret = (MethodPAG) methodToPag.get( m );
        if( ret == null ) { 
            ret = new MethodPAG( pag, m );
            methodToPag.put( m, ret );
        }
        return ret;
    }
    protected MethodPAG( PAG pag, SootMethod m ) {
        this.pag = pag;
        this.method = m;
        this.parms = new StandardParms( pag, this );
        edgeQueue = new ChunkedQueue();
        edgeReader = edgeQueue.reader();
    }
    public void build() {
        if( hasBeenBuilt ) return;
        hasBeenBuilt = true;
        if( method.isNative() ) {
            if( pag.getOpts().simulateNatives() ) {
                buildNative();
            }
        } else {
            if( method.isConcrete() ) {
                buildNormal();
            }
        }
        addMiscEdges();
    }
    public SootMethod getMethod() { return method; }
    public void addToPAG() {
        if( hasBeenAdded ) return;
        hasBeenAdded = true;
        while(true) {
            Node src = (Node) edgeReader.next();
            if( src == null ) break;
            Node dst = (Node) edgeReader.next();
            pag.addEdge( src, dst );
        }
    }
    public void addEdge( Node src, Node dst ) {
        edgeQueue.add( src );
        edgeQueue.add( dst );
    }
    private boolean hasBeenAdded = false;
    private boolean hasBeenBuilt = false;
    private ChunkedQueue edgeQueue;
    private QueueReader edgeReader;

    protected void buildNormal() {
        Body b = method.retrieveActiveBody();
        Iterator unitsIt = b.getUnits().iterator();
        while( unitsIt.hasNext() )
        {
            parms.handleStmt( (Stmt) unitsIt.next() );
        }
    }
    protected void buildNative() {
        ValNode thisNode = null;
        ValNode retNode = null; 
        if( !method.isStatic() ) { 
	    thisNode = (ValNode) parms.caseThis( method );
        }
        if( method.getReturnType() instanceof RefLikeType ) {
	    retNode = (ValNode) parms.caseRet( method );
	}
        ValNode[] args = new ValNode[ method.getParameterCount() ];
        for( int i = 0; i < method.getParameterCount(); i++ ) {
            if( !( method.getParameterType(i) instanceof RefLikeType ) ) continue;
	    args[i] = (ValNode) parms.caseParm( method, i );
        }
        NativeMethodDriver.process( method, thisNode, retNode, args );
    }

    private static final RefType string = RefType.v("java.lang.String");
    private static final ArrayType strAr = ArrayType.v(string, 1);
    private static final List strArL = Collections.singletonList( strAr );
    private static final String init =
	SootMethod.getSubSignature( "<init>", Collections.EMPTY_LIST, VoidType.v() );
    private static final String main =
	SootMethod.getSubSignature( "main", strArL, VoidType.v() );
    private static final String finalize =
	SootMethod.getSubSignature( "finalize", Collections.EMPTY_LIST, VoidType.v() );
    protected void addMiscEdges() {
        // Add node for parameter (String[]) in main method
        if( method.getSubSignature().equals( main ) ) {
            parms.addEdge( parms.caseArgv(), parms.caseParm( method, 0 ) );
        }

        // Add objects reaching this of finalize() methods
        if( method.getName().equals( "<init>" ) ) {
            SootClass c = method.getDeclaringClass();
outer:      do {
                while( !c.declaresMethod( finalize ) ) {
                    if( !c.hasSuperclass() ) {
                        break outer;
                    }
                    c = c.getSuperclass();
                }
                parms.addEdge( parms.caseThis( method ),
                        parms.caseThis( c.getMethod( finalize ) ) );
            } while( false );
        }

        if( method.getSignature().equals(
                    "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>" ) ) {
            parms.addEdge( parms.caseMainThread(), parms.caseThis( method ) );
            parms.addEdge( parms.caseMainThreadGroup(), parms.caseParm( method, 0 ) );
        }

        if( method.getSubSignature().equals(
            "java.lang.Class loadClass(java.lang.String)" ) ) {
            SootClass c = method.getDeclaringClass();
outer:      do {
                while( !c.getName().equals( "java.lang.ClassLoader" ) ) {
                    if( !c.hasSuperclass() ) {
                        break outer;
                    }
                    c = c.getSuperclass();
                }
                parms.addEdge( parms.caseDefaultClassLoader(),
                        parms.caseThis( method ) );
                parms.addEdge( parms.caseMainClassNameString(),
                        parms.caseParm( method, 0 ) );
            } while( false );
        }

        if( method.getSubSignature().equals(
            "java.lang.Object run()" ) ) {
            SootClass c = method.getDeclaringClass();
outer:      do {
                while( !c.implementsInterface( "java.security.PrivilegedAction" )
                && !c.implementsInterface( "java.security.PrivilegedExceptionAction" ) ) {

                    if( !c.hasSuperclass() ) {
                        break outer;
                    }
                    c = c.getSuperclass();
                }
                SootClass controller = RefType.v("java.security.AccessController").getSootClass();
                for( Iterator it = controller.methodIterator(); it.hasNext(); ) {
                    SootMethod m = (SootMethod) it.next();
                    if( !m.getName().equals( "doPrivileged" ) ) continue;
                    parms.addEdge( parms.caseParm( m, 0 ),
                            parms.caseThis( method ) );
                    parms.addEdge( parms.caseRet( method ),
                            parms.caseRet( m ) );
                }
            } while( false );
        }
    }
}

