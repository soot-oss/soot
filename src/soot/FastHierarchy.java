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

package soot;

import soot.jimple.*;
import soot.util.*;
import java.util.*;

/** Represents the class hierarchy.  It is closely linked to a Scene,
 * and must be recreated if the Scene changes. 
 *
 * This version supercedes the old soot.Hierarchy class.
 *
 * @author Ondrej Lhotak
 */
public class FastHierarchy
{
    private static void put( Map m, Object key, Object value ) {
        List l = (List) m.get( key );
        if( l == null ) m.put( key, l = new ArrayList() );
        l.add( value );
    }
    
    /** This map holds all key,value pairs such that 
     * value.getSuperclass() == key. This is one of the three maps that hold
     * the inverse of the relationships given by the getSuperclass and
     * getInterfaces methods of SootClass. */
    protected Map classToSubclasses = new HashMap();

    /** This map holds all key,value pairs such that value is an interface 
     * and key is in value.getInterfaces(). This is one of the three maps 
     * that hold the inverse of the relationships given by the getSuperclass 
     * and getInterfaces methods of SootClass. */
    protected MultiMap interfaceToSubinterfaces = new HashMultiMap();

    /** This map holds all key,value pairs such that value is a class 
     * (NOT an interface) and key is in value.getInterfaces(). This is one of 
     * the three maps that hold the inverse of the relationships given by the 
     * getSuperclass and getInterfaces methods of SootClass. */
    protected MultiMap interfaceToImplementers = new HashMultiMap();

    /** This map is a transitive closure of interfaceToSubinterfaces,
     * and each set contains its superinterface itself. */
    protected MultiMap interfaceToAllSubinterfaces = new HashMultiMap();

    /** This map gives, for an interface, all concrete classes that
     * implement that interface and all its subinterfaces, but
     * NOT their subclasses. */
    protected MultiMap interfaceToAllImplementers = new HashMultiMap();

    /** For each class (NOT interface), this map contains a Interval, which is
     * a pair of numbers giving a preorder and postorder ordering of classes
     * in the inheritance tree. */
    protected Map classToInterval = new HashMap();

    /** These maps cache subtype queries, so they can be re-done quickly. */
    //protected MultiMap cacheSubtypes = new HashMultiMap();
    //protected MultiMap cacheNonSubtypes = new HashMultiMap();

    protected Scene sc;

    protected class Interval {
        int lower;
        int upper;
        boolean isSubrange( Interval potentialSubrange ) {
            if( lower > potentialSubrange.lower ) return false;
            if( upper < potentialSubrange.upper ) return false;
            return true;
        }
    }
    protected int dfsVisit( int start, SootClass c ) {
        Interval r = new Interval();
        r.lower = start++;
        Collection col = (Collection) classToSubclasses.get(c);
        if( col != null ) {
            Iterator it = col.iterator();
            while( it.hasNext() ) {
                SootClass sc = (SootClass) it.next();
                // For some awful reason, Soot thinks interface are subclasses
                // of java.lang.Object
                if( sc.isInterface() ) continue;
                start = dfsVisit( start, sc );
            }
        }
        r.upper = start++;
        if( c.isInterface() ) {
            throw new RuntimeException( "Attempt to dfs visit interface "+c );
        }
        classToInterval.put( c, r );
        return start;
    }

    /** Constructs a hierarchy from the current scene. */
    public FastHierarchy()
    {
        this.sc = Scene.v();

        /* First build the inverse maps. */
        for( Iterator clIt = sc.getClasses().iterator(); clIt.hasNext(); ) {
            final SootClass cl = (SootClass) clIt.next();
            if( cl.resolvingLevel() < SootClass.HIERARCHY ) continue;
            if( !cl.isInterface() && cl.hasSuperclass() ) {
                put( classToSubclasses, cl.getSuperclass(), cl );
            }
            for( Iterator superclIt = cl.getInterfaces().iterator(); superclIt.hasNext(); ) {
                final SootClass supercl = (SootClass) superclIt.next();
                if( cl.isInterface() ) {
                    interfaceToSubinterfaces.put( supercl, cl );
                } else {
                    interfaceToImplementers.put( supercl, cl );
                }
            }
        }

        /* Now do a dfs traversal to get the Interval numbers. */
        dfsVisit( 0, Scene.v().getSootClass( "java.lang.Object" ) );
    }

    /** Return true if class child is a subclass of class parent, neither of
     * them being allowed to be interfaces. */
    public boolean isSubclass( SootClass child, SootClass parent ) {
        child.checkLevel(SootClass.HIERARCHY);
        parent.checkLevel(SootClass.HIERARCHY);
        Interval parentInterval = (Interval) classToInterval.get( parent );
        Interval childInterval = (Interval) classToInterval.get( child );
        return parentInterval.isSubrange( childInterval );
    }

    /** For an interface parent (MUST be an interface), returns set of all
     * implementers of it but NOT their subclasses. */
    public Set getAllImplementersOfInterface( SootClass parent ) {
        parent.checkLevel(SootClass.HIERARCHY);
        if( !interfaceToAllImplementers.containsKey( parent ) ) {
            for( Iterator subinterfaceIt = getAllSubinterfaces( parent ).iterator(); subinterfaceIt.hasNext(); ) {
                final SootClass subinterface = (SootClass) subinterfaceIt.next();
                if( subinterface == parent ) continue;
                interfaceToAllImplementers.putAll(parent,
                    getAllImplementersOfInterface( subinterface ) );
            }
            interfaceToAllImplementers.putAll(parent, 
                    interfaceToImplementers.get( parent ) );
        }
        return interfaceToAllImplementers.get( parent );
    }

    /** For an interface parent (MUST be an interface), returns set of all
     * subinterfaces. */
    protected Set getAllSubinterfaces( SootClass parent ) {
        parent.checkLevel(SootClass.HIERARCHY);
        if( !interfaceToAllSubinterfaces.containsKey( parent ) ) {
            interfaceToAllSubinterfaces.put( parent, parent );
            for( Iterator it = interfaceToSubinterfaces.get( parent ).iterator(); it.hasNext(); ) {
                interfaceToAllSubinterfaces.putAll(parent, 
                    getAllSubinterfaces( (SootClass) it.next() ) );
            }
        }
        return interfaceToAllSubinterfaces.get( parent );
    }

    /** Given an object of declared type child, returns true if the object
     * can be stored in a variable of type parent. If child is an interface
     * that is not a subinterface of parent, this method will return false
     * even though some objects implementing the child interface may also
     * implement the parent interface. */
    /*
    public boolean canStoreType( Type child, Type parent ) {
        if( cacheSubtypes.get( parent ).contains( child ) ) return true;
        if( cacheNonSubtypes.get( parent ).contains( child ) ) return false;
        boolean ret = canStoreTypeInternal( child, parent );
        ( ret ? cacheSubtypes : cacheNonSubtypes ).put( parent, child );
        return ret;
    }
    */

    /** Given an object of declared type child, returns true if the object
     * can be stored in a variable of type parent. If child is an interface
     * that is not a subinterface of parent, this method will return false
     * even though some objects implementing the child interface may also
     * implement the parent interface. */
    public boolean canStoreType( Type child, Type parent ) {
        if( child.equals( parent ) ) return true;
        if( parent instanceof NullType ) {
            return false;
        }
        if( child instanceof RefType ) {
            if( parent instanceof RefType) {
                return canStoreClass( ((RefType) child).getSootClass(),
                    ((RefType) parent).getSootClass() );
            } else {
                return false;
            }
        } else if( child instanceof AnySubType ) {
            if( !(parent instanceof RefLikeType ) ) {
                throw new RuntimeException( "Unhandled type "+parent );
            } else if(parent instanceof ArrayType) {
                Type base = ((AnySubType)child).getBase();
                // From Java Language Spec 2nd ed., Chapter 10, Arrays
                return base.equals( RefType.v( "java.lang.Object" ) )
                || base.equals( RefType.v( "java.io.Serializable" ) )
                || base.equals( RefType.v( "java.lang.Cloneable" ) );
            } else {
                SootClass base = ((AnySubType)child).getBase().getSootClass();
                SootClass parentClass = ((RefType) parent).getSootClass();
                LinkedList worklist = new LinkedList();
                if( base.isInterface() ) worklist.addAll(getAllImplementersOfInterface(base));
                else worklist.add(base);
                Set workset = new HashSet();
                while(!worklist.isEmpty()) {
                    SootClass cl = (SootClass) worklist.removeFirst();
                    if( !workset.add(cl) ) continue;
                    if( cl.isConcrete() 
                    &&  canStoreClass(cl, parentClass) ) return true;
                    worklist.addAll(getSubclassesOf(cl));
                }
                return false;
            }
        } else {
            ArrayType achild = (ArrayType) child;
            if( parent instanceof RefType ) {
                // From Java Language Spec 2nd ed., Chapter 10, Arrays
                return parent.equals( RefType.v( "java.lang.Object" ) )
                || parent.equals( RefType.v( "java.io.Serializable" ) )
                || parent.equals( RefType.v( "java.lang.Cloneable" ) );
            }
            ArrayType aparent = (ArrayType) parent;
                                                
            // You can store a int[][] in a Object[]. Yuck!
            // Also, you can store a Interface[] in a Object[]
            if( achild.numDimensions == aparent.numDimensions ) {
                if( achild.baseType.equals( aparent.baseType ) ) return true;
                if( !(achild.baseType instanceof RefType ) ) return false;
                if( !(aparent.baseType instanceof RefType ) ) return false;
                return canStoreType( achild.baseType, aparent.baseType );
            } else if( achild.numDimensions > aparent.numDimensions ) {
                if( aparent.baseType.equals( RefType.v( "java.lang.Object" ) ) )
                    return true;
                if( aparent.baseType.equals( RefType.v( "java.io.Serializable" ) ) )
                    return true;
                if( aparent.baseType.equals( RefType.v( "java.lang.Cloneable" ) ) )
                    return true;
                return false;
            } else return false;
        }
    }

    /** Given an object of declared type child, returns true if the object
     * can be stored in a variable of type parent. If child is an interface
     * that is not a subinterface of parent, this method will return false
     * even though some objects implementing the child interface may also
     * implement the parent interface. */
    protected boolean canStoreClass( SootClass child, SootClass parent ) {
        parent.checkLevel(SootClass.HIERARCHY);
        child.checkLevel(SootClass.HIERARCHY);
        Interval parentInterval = (Interval) classToInterval.get( parent );
        Interval childInterval = (Interval) classToInterval.get( child );
        if( parentInterval != null && childInterval != null ) {
            return parentInterval.isSubrange( childInterval );
        }
        if( childInterval == null ) { // child is interface
            if( parentInterval != null ) { // parent is not interface
                return parent.equals( RefType.v("java.lang.Object").getSootClass() );
            } else {
                return getAllSubinterfaces( parent ).contains( child );
            }
        } else {
            Set impl = getAllImplementersOfInterface( parent );
            for( Iterator it = impl.iterator(); it.hasNext(); ) {
                parentInterval = (Interval) classToInterval.get( it.next() );
                if( parentInterval.isSubrange( childInterval ) ) {
                    return true;
                }
            }
            return false;
        }
    }

    public Collection resolveConcreteDispatchWithoutFailing(Collection concreteTypes, SootMethod m, RefType declaredTypeOfBase ) {

        Set ret = new HashSet();
        SootClass declaringClass = declaredTypeOfBase.getSootClass();
        declaringClass.checkLevel(SootClass.HIERARCHY);
        for( Iterator tIt = concreteTypes.iterator(); tIt.hasNext(); ) {
            final Type t = (Type) tIt.next();
            if( t instanceof AnySubType ) {
                String methodSig = m.getSubSignature();
                HashSet s = new HashSet();
                s.add( declaringClass );
                while( !s.isEmpty() ) {
                    SootClass c = (SootClass) s.iterator().next();
                    s.remove( c );
                    if( !c.isInterface() && !c.isAbstract()
                            && canStoreClass( c, declaringClass ) ) {
                        SootMethod concreteM = resolveConcreteDispatch( c, m );
                        if( concreteM != null )
                            ret.add( concreteM );
                    }
                    if( classToSubclasses.containsKey( c ) ) {
                        s.addAll( (Collection) classToSubclasses.get( c ) );
                    }
                    if( interfaceToSubinterfaces.containsKey( c ) ) {
                        s.addAll( interfaceToSubinterfaces.get( c ) );
                    }
                    if( interfaceToImplementers.containsKey( c ) ) {
                        s.addAll( interfaceToImplementers.get( c ) );
                    }
                }
                return ret;
            } else if( t instanceof RefType ) {
                RefType concreteType = (RefType) t;
                SootClass concreteClass = concreteType.getSootClass();
                if( !canStoreClass( concreteClass, declaringClass ) ) {
                    continue;
                }
                SootMethod concreteM = null;
                try {
                    concreteM = resolveConcreteDispatch( concreteClass, m );
                } catch( Exception e ) {
                    concreteM = null;
                }
                if( concreteM != null ) ret.add( concreteM );
            } else if( t instanceof ArrayType ) {
                SootMethod concreteM = null;
                try {
                    concreteM = resolveConcreteDispatch( 
                        RefType.v( "java.lang.Object" ).getSootClass(), m );
                } catch( Exception e ) {
                    concreteM = null;
                }
                if( concreteM != null ) ret.add( concreteM );
            } else throw new RuntimeException( "Unrecognized reaching type "+t );
        }
        return ret;
    }

    public Collection resolveConcreteDispatch(Collection concreteTypes, SootMethod m, RefType declaredTypeOfBase ) {

        Set ret = new HashSet();
        SootClass declaringClass = declaredTypeOfBase.getSootClass();
        declaringClass.checkLevel(SootClass.HIERARCHY);
        for( Iterator tIt = concreteTypes.iterator(); tIt.hasNext(); ) {
            final Type t = (Type) tIt.next();
            if( t instanceof AnySubType ) {
                String methodSig = m.getSubSignature();
                HashSet s = new HashSet();
                s.add( declaringClass );
                while( !s.isEmpty() ) {
                    SootClass c = (SootClass) s.iterator().next();
                    s.remove( c );
                    if( !c.isInterface() && !c.isAbstract()
                            && canStoreClass( c, declaringClass ) ) {
                        SootMethod concreteM = resolveConcreteDispatch( c, m );
                        if( concreteM != null )
                            ret.add( concreteM );
                    }
                    if( classToSubclasses.containsKey( c ) ) {
                        s.addAll( (Collection) classToSubclasses.get( c ) );
                    }
                    if( interfaceToSubinterfaces.containsKey( c ) ) {
                        s.addAll( interfaceToSubinterfaces.get( c ) );
                    }
                    if( interfaceToImplementers.containsKey( c ) ) {
                        s.addAll( interfaceToImplementers.get( c ) );
                    }
                }
                return ret;
            } else if( t instanceof RefType ) {
                RefType concreteType = (RefType) t;
                SootClass concreteClass = concreteType.getSootClass();
                if( !canStoreClass( concreteClass, declaringClass ) ) {
                    continue;
                }
                SootMethod concreteM = resolveConcreteDispatch( concreteClass, m );
                if( concreteM != null ) ret.add( concreteM );
            } else if( t instanceof ArrayType ) {
                SootMethod concreteM = resolveConcreteDispatch( 
                        RefType.v( "java.lang.Object" ).getSootClass(), m );
                if( concreteM != null ) ret.add( concreteM );
            } else throw new RuntimeException( "Unrecognized reaching type "+t );
        }
        return ret;
    }

    // Questions about method invocation.

    /** Returns true if the method m is visible from code in the class from. */
    private boolean isVisible( SootClass from, SootMethod m ) {
        from.checkLevel(SootClass.HIERARCHY);
        if( m.isPublic() ) return true;
        if( m.isPrivate() ) {
            return from.equals( m.getDeclaringClass() );
        }
        if( m.isProtected() ) {
            return canStoreClass( from, m.getDeclaringClass() );
        }
        // m is package
        return from.getJavaPackageName().equals(
                m.getDeclaringClass().getJavaPackageName() );
            //|| canStoreClass( from, m.getDeclaringClass() );
    }

    /** Given an object of declared type C, returns the methods which could
     * be called on an o.f() invocation. */
    public Set resolveAbstractDispatch(SootClass abstractType, SootMethod m )
    {
        String methodSig = m.getSubSignature();
        HashSet resolved = new HashSet();
        HashSet ret = new HashSet();
        LinkedList worklist = new LinkedList();
        worklist.add( abstractType );
        while( !worklist.isEmpty() ) {
            SootClass concreteType = (SootClass) worklist.removeFirst();
            SootClass savedConcreteType = concreteType;
            if( concreteType.isInterface() ) {
                worklist.addAll( getAllImplementersOfInterface( concreteType ) );
                continue;
            }
            Collection c = (Collection) classToSubclasses.get( concreteType );
            if( c != null ) worklist.addAll( c );
            if( !concreteType.isAbstract() ) {
                while( true ) {
                    if( resolved.contains( concreteType ) ) break;
                    resolved.add( concreteType );
                    if( concreteType.declaresMethod( methodSig ) ) {
                        SootMethod method = concreteType.getMethod( methodSig );
                        if( method.isAbstract() )
                            throw new RuntimeException("abstract dispatch resolved to abstract method!\nAbstract Type: "+abstractType+"\nConcrete Type: "+savedConcreteType+"\nMethod: "+m);

                        if( isVisible( concreteType, m ) ) {
                            ret.add( concreteType.getMethod( methodSig ) );
                            break;
                        }
                    }
                    if( !concreteType.hasSuperclass() ) 
                        throw new RuntimeException("could not resolve abstract dispatch!\nAbstract Type: "+abstractType+"\nConcrete Type: "+savedConcreteType+"\nMethod: "+m);
                    concreteType = concreteType.getSuperclass();
                }
            }
        }
        return ret;
    }

    /** Given an object of actual type C (o = new C()), returns the method which will be called
        on an o.f() invocation. */
    public SootMethod resolveConcreteDispatch(SootClass concreteType, SootMethod m)
    {
        concreteType.checkLevel(SootClass.HIERARCHY);
        if( concreteType.isInterface() ) {
            throw new RuntimeException(
                "A concrete type cannot be an interface: "+concreteType );
        }
        if( concreteType.isAbstract() ) {
            throw new RuntimeException(
                "A concrete type cannot be abstract: "+concreteType );
        }

        String methodSig = m.getSubSignature();
        while( true ) {
            if( concreteType.declaresMethod( methodSig ) ) {
                if( isVisible( concreteType, m ) ) {
                    return concreteType.getMethod( methodSig );
                }
            }
            if( !concreteType.hasSuperclass() ) break;
            concreteType = concreteType.getSuperclass();
        }
        throw new RuntimeException("could not resolve concrete dispatch!\nType: "+concreteType+"\nMethod: "+m);
    }

    /** Returns the target for the given SpecialInvokeExpr. */
    public SootMethod resolveSpecialDispatch(SpecialInvokeExpr ie, SootMethod container)
    {
        SootMethod target = ie.getMethod();

        /* This is a bizarre condition!  Hopefully the implementation is correct.
           See VM Spec, 2nd Edition, Chapter 6, in the definition of invokespecial. */
        if (target.getName().equals("<init>") || target.isPrivate())
            return target;
        else if (isSubclass(target.getDeclaringClass(), container.getDeclaringClass()))
            return resolveConcreteDispatch(container.getDeclaringClass(), target );
        else
            return target;
    }

    public Collection getSubclassesOf( SootClass c ) {
        c.checkLevel(SootClass.HIERARCHY);
        Collection ret = (Collection) classToSubclasses.get(c);
        if( ret == null ) return Collections.EMPTY_LIST;
        return ret;
    }
}
