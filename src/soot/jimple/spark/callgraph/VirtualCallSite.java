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

package soot.jimple.spark.callgraph;
import soot.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.util.queue.*;

/** Resolves a virtual call site.
 * @author Ondrej Lhotak
 */
public class VirtualCallSite
{ 
    private NumberedSet seenTypes = new NumberedSet( Scene.v().getTypeNumberer() );
    private NumberedSet seenAllSubtypes = new NumberedSet( Scene.v().getTypeNumberer() );
    private InstanceInvokeExpr iie;
    private Stmt stmt;
    private SootMethod container;
    private static FastHierarchy fh;
    private NumberedString subSig;
    private boolean seenInvokeSpecial = false;
    private static final NumberedString sigRun = Scene.v().getSubSigNumberer().
        findOrAdd( "void run()" );
    private static final NumberedString sigStart = Scene.v().getSubSigNumberer().
        findOrAdd( "void start()" );
    private static final NumberedString sigClinit = Scene.v().getSubSigNumberer().
        findOrAdd( "void <clinit>()" );

    public VirtualCallSite( Stmt stmt, SootMethod container ) {
        this.stmt = stmt;
        this.iie = (InstanceInvokeExpr) stmt.getInvokeExpr();
        this.container = container;
        this.subSig = iie.getMethod().getNumberedSubSignature();
        fh = Scene.v().getOrMakeFastHierarchy();
    }

    public void addType( Type t, ChunkedQueue targets ) {
        if( t instanceof RefType ) {
            if( iie instanceof SpecialInvokeExpr ) {
                SootMethod target = iie.getMethod();
                /* cf. JVM spec, invokespecial instruction */
                if( fh.canStoreType( container.getDeclaringClass().getType(),
                        target.getDeclaringClass().getType() )
                    && container.getDeclaringClass().getType() !=
                        target.getDeclaringClass().getType() 
                    && !target.getName().equals( "<init>" ) 
                    && subSig != sigClinit ) {

                    t = container.getDeclaringClass().getSuperclass().getType();
                } else {
                    if( !seenInvokeSpecial ) {
                        seenInvokeSpecial = true;
                        //target.addTag( new soot.tagkit.StringTag( this.toString() ) );
                        targets.add( target );
                    }
                    return;
                }
            } else if( !fh.canStoreType( t, iie.getBase().getType() ) ) {
                return;
            }
            SootClass cls = ((RefType)t).getSootClass();
            while(true) {
                if( !seenTypes.add( cls.getType() ) ) break;
                if( cls.declaresMethod( subSig ) ) {
                    SootMethod m = cls.getMethod( subSig );
                    if( m.isConcrete() || m.isNative() ) {
                        SootMethod target = cls.getMethod( subSig );
                        targets.add( target );
                    }
                    break;
                }
                if( subSig == sigStart && cls.declaresMethod( sigRun ) 
                && fh.canStoreType( t, RefType.v( "java.lang.Runnable" ) ) ) {
                    SootMethod m = cls.getMethod( sigRun );
                    if( m.isConcrete() || m.isNative() ) {
                        SootMethod target = cls.getMethod( sigRun );
                        targets.add( target );
                    }
                    break;
                }
                if( !cls.hasSuperclass() ) break;
                cls = cls.getSuperclass();
            }
        } else if( t instanceof ArrayType ) {
            addType( RefType.v( "java.lang.Object" ), targets );
        } else if( t instanceof NullType ) {
        } else if( t instanceof AnySubType ) {
            RefType base = ((AnySubType)t).getBase();
            if( seenAllSubtypes.add( base ) ) {
                SootClass cl = base.getSootClass();
                if( cl.isInterface() ) {
                    for( Iterator cIt = fh.getAllImplementersOfInterface(cl).iterator(); cIt.hasNext(); ) {
                        final SootClass c = (SootClass) cIt.next();
                        addType( AnySubType.v( c.getType() ), targets );
                    }
                }
                addType( base, targets );
                for( Iterator cIt = fh.getSubclassesOf( cl ).iterator(); cIt.hasNext(); ) {
                    final SootClass c = (SootClass) cIt.next();
                    addType( AnySubType.v( c.getType() ), targets );
                }
            }
        } else {
            throw new RuntimeException( "oops "+t );
        }
    }
    public void noMoreTypes() {
        seenTypes = null;
        seenAllSubtypes = null;
    }
    public String toString() {
        return "VCS: "+iie+" in "+container;
    }
    public Stmt getStmt() { return stmt; }
    public SootMethod getContainer() { return container; }
    public InstanceInvokeExpr getInstanceInvokeExpr() { return iie; }
}


