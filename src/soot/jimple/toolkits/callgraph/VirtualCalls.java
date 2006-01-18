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
    public static VirtualCalls v() { return G.v().soot_jimple_toolkits_callgraph_VirtualCalls(); }

    private LargeNumberedMap typeToVtbl =
        new LargeNumberedMap( Scene.v().getTypeNumberer() );

    public SootMethod resolveSpecial( SpecialInvokeExpr iie, NumberedString subSig, SootMethod container ) {
        SootMethod target = iie.getMethod();
        /* cf. JVM spec, invokespecial instruction */
        if( Scene.v().getOrMakeFastHierarchy()
                .canStoreType( container.getDeclaringClass().getType(),
                    target.getDeclaringClass().getType() )
            && container.getDeclaringClass().getType() !=
                target.getDeclaringClass().getType() 
            && !target.getName().equals( "<init>" ) 
            && subSig != sigClinit ) {

            return resolveNonSpecial(
                    container.getDeclaringClass().getSuperclass().getType(),
                    subSig );
        } else {
            return target;
        }
    }

    public SootMethod resolveNonSpecial( RefType t, NumberedString subSig ) {
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
                ret = resolveNonSpecial( cls.getSuperclass().getType(), subSig );
            }
        }
        vtbl.put( subSig, ret );
        return ret;
    }

    private Map baseToSubTypes = new HashMap();

    public void resolve( Type t, Type declaredType, NumberedString subSig, SootMethod container, ChunkedQueue targets ) {
        resolve(t, declaredType, null, subSig, container, targets);
    }
    public void resolve( Type t, Type declaredType, Type sigType, NumberedString subSig, SootMethod container, ChunkedQueue targets ) {
        if( declaredType instanceof ArrayType ) declaredType = RefType.v("java.lang.Object");
        if( sigType instanceof ArrayType ) sigType = RefType.v("java.lang.Object");
        if( t instanceof ArrayType ) t = RefType.v( "java.lang.Object" );
        if( declaredType != null && !Scene.v().getOrMakeFastHierarchy()
                .canStoreType( t, declaredType ) ) {
            return;
        }
        if( sigType != null && !Scene.v().getOrMakeFastHierarchy()
                .canStoreType( t, sigType ) ) {
            return;
        }
        if( t instanceof RefType ) {
            SootMethod target = resolveNonSpecial( (RefType) t, subSig );
            if( target != null ) targets.add( target );
        } else if( t instanceof AnySubType ) {
            RefType base = ((AnySubType)t).getBase();

            List subTypes = (List) baseToSubTypes.get(base);
            if( subTypes != null ) {
                for( Iterator stIt = subTypes.iterator(); stIt.hasNext(); ) {
                    final Type st = (Type) stIt.next();
                    resolve( st, declaredType, sigType, subSig, container, targets );
                }
                return;
            }

            baseToSubTypes.put(base, subTypes = new ArrayList() );

            subTypes.add(base);

            LinkedList worklist = new LinkedList();
            HashSet workset = new HashSet();
            FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
            SootClass cl = base.getSootClass();

            if( workset.add( cl ) ) worklist.add( cl );
            while( !worklist.isEmpty() ) {
                cl = (SootClass) worklist.removeFirst();
                if( cl.isInterface() ) {
                    for( Iterator cIt = fh.getAllImplementersOfInterface(cl).iterator(); cIt.hasNext(); ) {
                        final SootClass c = (SootClass) cIt.next();
                        if( workset.add( c ) ) worklist.add( c );
                    }
                } else {
                    if( cl.isConcrete() ) {
                        resolve( cl.getType(), declaredType, sigType, subSig, container, targets );
                        subTypes.add(cl.getType());
                    }
                    for( Iterator cIt = fh.getSubclassesOf( cl ).iterator(); cIt.hasNext(); ) {
                        final SootClass c = (SootClass) cIt.next();
                        if( workset.add( c ) ) worklist.add( c );
                    }
                }
            }
        } else if( t instanceof NullType ) {
        } else {
            throw new RuntimeException( "oops "+t );
        }
    }
    
    public final NumberedString sigClinit =
        Scene.v().getSubSigNumberer().findOrAdd("void <clinit>()");
    public final NumberedString sigStart =
        Scene.v().getSubSigNumberer().findOrAdd("void start()");
    public final NumberedString sigRun =
        Scene.v().getSubSigNumberer().findOrAdd("void run()");
}


