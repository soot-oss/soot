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

    private final LargeNumberedMap<Type, SmallNumberedMap<SootMethod>> typeToVtbl =
        new LargeNumberedMap<Type, SmallNumberedMap<SootMethod>>( Scene.v().getTypeNumberer() );

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
        SmallNumberedMap<SootMethod> vtbl = typeToVtbl.get( t );
        if( vtbl == null ) {
            typeToVtbl.put( t, vtbl =
                    new SmallNumberedMap<SootMethod>( Scene.v().getMethodNumberer() ) );
        }
        SootMethod ret = vtbl.get( subSig );
        if( ret != null ) return ret;
        SootClass cls = t.getSootClass();
        SootMethod m = cls.getMethodUnsafe( subSig );
        if( m != null ) {
            if( m.isConcrete() || m.isNative() || m.isPhantom() ) {
                ret = m;
            }
        } else {
            if( cls.hasSuperclass() ) {
                ret = resolveNonSpecial( cls.getSuperclass().getType(), subSig );
            }
        }
        vtbl.put( subSig, ret );
        return ret;
    }

    private final Map<Type,List<Type>> baseToSubTypes = new HashMap<Type,List<Type>>();

    public void resolve( Type t, Type declaredType, NumberedString subSig, SootMethod container, ChunkedQueue<SootMethod> targets ) {
        resolve(t, declaredType, null, subSig, container, targets);
    }
    public void resolve( Type t, Type declaredType, Type sigType, NumberedString subSig, SootMethod container, ChunkedQueue<SootMethod> targets ) {
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

            List<Type> subTypes = baseToSubTypes.get(base);
            if( subTypes != null ) {
                for( Iterator<Type> stIt = subTypes.iterator(); stIt.hasNext(); ) {
                    final Type st = stIt.next();
                    resolve( st, declaredType, sigType, subSig, container, targets );
                }
                return;
            }

            baseToSubTypes.put(base, subTypes = new ArrayList<Type>() );

            subTypes.add(base);

            LinkedList<SootClass> worklist = new LinkedList<SootClass>();
            HashSet<SootClass> workset = new HashSet<SootClass>();
            FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
            SootClass cl = base.getSootClass();

            if( workset.add( cl ) ) worklist.add( cl );
            while( !worklist.isEmpty() ) {
                cl = worklist.removeFirst();
                if( cl.isInterface() ) {
                    for( Iterator<SootClass> cIt = fh.getAllImplementersOfInterface(cl).iterator(); cIt.hasNext(); ) {
                        final SootClass c = cIt.next();
                        if( workset.add( c ) ) worklist.add( c );
                    }
                } else {
                    if( cl.isConcrete() ) {
                        resolve( cl.getType(), declaredType, sigType, subSig, container, targets );
                        subTypes.add(cl.getType());
                    }
                    for( Iterator<SootClass> cIt = fh.getSubclassesOf( cl ).iterator(); cIt.hasNext(); ) {
                        final SootClass c = cIt.next();
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


