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

package soot.jimple.toolkits.callgraph;
import soot.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.util.queue.*;

/** Resolves virtual calls.
 * @author Ondrej Lhotak
 */
public final class VirtualCalls
{ 
    public VirtualCalls( Singletons.Global g ) {}
    public static VirtualCalls v() { return G.v().VirtualCalls(); }

    private LargeNumberedMap typeToVtbl =
        new LargeNumberedMap( Scene.v().getTypeNumberer() );

    private SootMethod resolveRefType( RefType t, InstanceInvokeExpr iie, NumberedString subSig, SootMethod container ) {
        if( iie instanceof SpecialInvokeExpr ) {
            SootMethod target = iie.getMethod();
            /* cf. JVM spec, invokespecial instruction */
            if( Scene.v().getOrMakeFastHierarchy()
                    .canStoreType( container.getDeclaringClass().getType(),
                        target.getDeclaringClass().getType() )
                && container.getDeclaringClass().getType() !=
                    target.getDeclaringClass().getType() 
                && !target.getName().equals( "<init>" ) 
                && subSig != ImplicitMethodInvocation.v().sigClinit ) {

                t = container.getDeclaringClass().getSuperclass().getType();
            } else {
                return target;
            }
        }
        return resolveNonSpecial( t, iie, container, subSig );
    }

    private SootMethod resolveNonSpecial( RefType t, InstanceInvokeExpr iie, SootMethod container, NumberedString subSig ) {
        if( !Scene.v().getOrMakeFastHierarchy()
                .canStoreType( t, iie.getBase().getType() ) ) {
            return null;
        }
        SmallNumberedMap vtbl = (SmallNumberedMap) typeToVtbl.get( t );
        if( vtbl == null ) {
            typeToVtbl.put( t, vtbl =
                    new SmallNumberedMap( Scene.v().getMethodNumberer() ) );
        }
        SootMethod ret = (SootMethod) vtbl.get( subSig );
        if( ret != null ) return ret;
        SootClass cls = ((RefType)t).getSootClass();
        if( cls.declaresMethod( subSig ) ) {
            SootMethod m = cls.getMethod( subSig );
            if( m.isConcrete() || m.isNative() ) {
                ret = cls.getMethod( subSig );
            }
        } else {
            if( cls.hasSuperclass() ) {
                ret = resolveNonSpecial( cls.getSuperclass().getType(),
                        iie, container, subSig );
            }
        }
        vtbl.put( subSig, ret );
        return ret;
    }

    private void resolve( Type t, InstanceInvokeExpr iie, NumberedString subSig, SootMethod container, ChunkedQueue targets ) {
        if( t instanceof ArrayType ) t = RefType.v( "java.lang.Object" );
        if( t instanceof RefType ) {
            SootMethod target = resolveRefType( (RefType) t, iie, subSig, container );
            if( target != null ) targets.add( target );
        } else if( t instanceof AnySubType ) {
            RefType base = ((AnySubType)t).getBase();
            resolve( base, iie, subSig, container, targets );
            if( iie instanceof SpecialInvokeExpr ) return;
            LinkedList worklist = new LinkedList();
            SootClass cl = base.getSootClass();
            worklist.add( cl );
            while( !worklist.isEmpty() ) {
                cl = (SootClass) worklist.removeFirst();
                if( cl.isInterface() ) {
                    for( Iterator cIt = Scene.v().getOrMakeFastHierarchy()
                            .getAllImplementersOfInterface(cl).iterator(); cIt.hasNext(); ) {
                        final SootClass c = (SootClass) cIt.next();
                        worklist.add( c );
                    }
                } else {
                    if( cl.declaresMethod( subSig ) ) {
                        SootMethod m = cl.getMethod( subSig );
                        if( m.isConcrete() || m.isNative() ) {
                            targets.add( m );
                        }
                    }
                }
            }
        } else if( t instanceof NullType ) {
        } else {
            throw new RuntimeException( "oops "+t );
        }
    }

    public void resolve( Type t, InstanceInvokeExpr iie, SootMethod container, ChunkedQueue targets ) {
        resolve( t, iie, iie.getMethod().getNumberedSubSignature(), container, targets );
    }

    public void resolveThread( Type t, InstanceInvokeExpr iie, SootMethod container, ChunkedQueue targets ) {
        if( iie.getMethod().getNumberedSubSignature() !=
                ImplicitMethodInvocation.v().sigStart ) return;
        if( !Scene.v().getOrMakeFastHierarchy()
                .canStoreType( t, RefType.v( "java.lang.Runnable" ) ) ) return;
        resolve( t, iie, ImplicitMethodInvocation.v().sigRun,
                container, targets );
    }
}


