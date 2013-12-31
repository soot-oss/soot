/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 * Copyright (C) 2013 Tata Consultancy Services & Ecole Polytechnique de Montreal
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

/*
 * Modified by the Sable Research Group and others 1997-1999.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;

import com.google.common.collect.Sets;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import soot.jimple.*;
import soot.options.Options;
import soot.util.*;
import java.util.*;

public class HierarchyTests {
    private static class OldHierarchy
    {
        // These two maps are not filled in the constructor.
        HashMap<SootClass, List<SootClass>> classToSubclasses;
        HashMap<SootClass, List<SootClass>> interfaceToSubinterfaces;
        HashMap<SootClass, List<SootClass>> interfaceToSuperinterfaces;
        HashMap<SootClass, List<SootClass>> classToDirSubclasses;
        HashMap<SootClass, List<SootClass>> interfaceToDirSubinterfaces;
        HashMap<SootClass, List<SootClass>> interfaceToDirSuperinterfaces;
        // This holds the direct implementers.
        HashMap<SootClass, List<SootClass>> interfaceToDirImplementers;
        int state;
        Scene sc;

        /** Constructs a hierarchy from the current scene. */
        public OldHierarchy()
        {
            this.sc = Scene.v();
            state = sc.getState();

            // Well, this used to be describable by 'Duh'.
            // Construct the subclasses hierarchy and the subinterfaces hierarchy.
            {
                Chain<SootClass> allClasses = sc.getClasses();

                classToSubclasses = new HashMap<SootClass, List<SootClass>>(allClasses.size() * 2 + 1, 0.7f);
                interfaceToSubinterfaces = new HashMap<SootClass, List<SootClass>>(allClasses.size() * 2 + 1, 0.7f);
                interfaceToSuperinterfaces = new HashMap<SootClass, List<SootClass>>(allClasses.size() * 2 + 1, 0.7f);

                classToDirSubclasses = new HashMap<SootClass, List<SootClass>>
                        (allClasses.size() * 2 + 1, 0.7f);
                interfaceToDirSubinterfaces = new HashMap<SootClass, List<SootClass>>
                        (allClasses.size() * 2 + 1, 0.7f);
                interfaceToDirSuperinterfaces = new HashMap<SootClass, List<SootClass>>
                        (allClasses.size() * 2 + 1, 0.7f);
                interfaceToDirImplementers = new HashMap<SootClass, List<SootClass>>
                        (allClasses.size() * 2 + 1, 0.7f);

                Iterator<SootClass> classesIt = allClasses.iterator();
                while (classesIt.hasNext())
                {
                    SootClass c = classesIt.next();
                    if( c.resolvingLevel() < SootClass.HIERARCHY ) continue;

                    if (c.isInterface())
                    {
                        interfaceToDirSubinterfaces.put(c, new ArrayList<SootClass>());
                        interfaceToDirSuperinterfaces.put(c, new ArrayList<SootClass>());
                        interfaceToDirImplementers.put(c, new ArrayList<SootClass>());
                    }
                    else
                        classToDirSubclasses.put(c, new ArrayList<SootClass>());
                }

                classesIt = allClasses.iterator();
                while (classesIt.hasNext())
                {
                    SootClass c = classesIt.next();
                    if( c.resolvingLevel() < SootClass.HIERARCHY ) continue;

                    List<SootClass> l2 = interfaceToDirSuperinterfaces.get(c);

                    if (c.hasSuperclass())
                    {
                        if (c.isInterface())
                        {
                            for (SootClass i : c.getInterfaces()) {
                                if( c.resolvingLevel() < SootClass.HIERARCHY ) continue;
                                List<SootClass> l = interfaceToDirSubinterfaces.get(i);
                                if (l != null) l.add(c);
                                if (l2 != null) l2.add(i);
                            }
                        }
                        else
                        {
                            List<SootClass> l = classToDirSubclasses.get(c.getSuperclass());
                            l.add(c);


                            Iterator<SootClass> subIt = c.getInterfaces().iterator();

                            while (subIt.hasNext())
                            {
                                SootClass i = subIt.next();
                                if( c.resolvingLevel() < SootClass.HIERARCHY ) continue;
                                l = interfaceToDirImplementers.get(i);
                                if (l != null) l.add(c);
                            }
                        }
                    }
                }

                // Fill the directImplementers lists with subclasses.
                {
                    classesIt = allClasses.iterator();
                    while (classesIt.hasNext())
                    {
                        SootClass c = (SootClass)classesIt.next();
                        if( c.resolvingLevel() < SootClass.HIERARCHY ) continue;
                        if (c.isInterface())
                        {
                            List<SootClass> imp = interfaceToDirImplementers.get(c);
                            Set<SootClass> s = new ArraySet<SootClass>();

                            Iterator<SootClass> impIt = imp.iterator();
                            while (impIt.hasNext())
                            {
                                SootClass c0 = impIt.next();
                                if( c.resolvingLevel() < SootClass.HIERARCHY ) continue;
                                s.addAll(getSubclassesOfIncluding(c0));
                            }

                            imp.clear(); imp.addAll(s);
                        }
                    }
                }

                classesIt = allClasses.iterator();
                while (classesIt.hasNext())
                {
                    SootClass c = (SootClass)classesIt.next();
                    if( c.resolvingLevel() < SootClass.HIERARCHY ) continue;

                    if (c.isInterface())
                    {
                        interfaceToDirSubinterfaces.put(c, Collections.unmodifiableList
                                (interfaceToDirSubinterfaces.get(c)));
                        interfaceToDirSuperinterfaces.put(c, Collections.unmodifiableList
                                (interfaceToDirSuperinterfaces.get(c)));
                        interfaceToDirImplementers.put(c, Collections.unmodifiableList
                                (interfaceToDirImplementers.get(c)));
                    }
                    else
                        classToDirSubclasses.put(c, Collections.unmodifiableList
                                (classToDirSubclasses.get(c)));
                }
            }
        }

        private void checkState()
        {
            if (state != sc.getState())
                throw new ConcurrentModificationException("Scene changed for Hierarchy!");
        }

        // This includes c in the list of subclasses.

        /** Returns a list of subclasses of c, including itself. */
        public List<SootClass> getSubclassesOfIncluding(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (c.isInterface())
                throw new RuntimeException("class needed!");

            List<SootClass> l = new ArrayList<SootClass>();
            l.addAll(getSubclassesOf(c));
            l.add(c);

            return Collections.unmodifiableList(l);
        }

        /** Returns a list of subclasses of c, excluding itself. */
        public List<SootClass> getSubclassesOf(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (c.isInterface())
                throw new RuntimeException("class needed!");

            checkState();

            // If already cached, return the value.
            if (classToSubclasses.get(c) != null)
                return classToSubclasses.get(c);

            // Otherwise, build up the hashmap.
            List<SootClass> l = new ArrayList<SootClass>();

            ListIterator<SootClass> it = classToDirSubclasses.get(c).listIterator();
            while (it.hasNext())
            {
                SootClass cls = it.next();
                if( cls.resolvingLevel() < SootClass.HIERARCHY ) continue;
                l.addAll(getSubclassesOfIncluding(cls));
            }

            l = Collections.unmodifiableList(l);
            classToSubclasses.put(c, l);

            return l;
        }

        /** Returns a list of superclasses of c, including itself. */
        public List<SootClass> getSuperclassesOfIncluding(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            List<SootClass> l = getSuperclassesOf(c);
            ArrayList<SootClass> al = new ArrayList<SootClass>(); al.add(c); al.addAll(l);
            return Collections.unmodifiableList(al);
        }

        /** Returns a list of strict superclasses of c, starting with c's parent. */
        public List<SootClass> getSuperclassesOf(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (c.isInterface())
                throw new RuntimeException("class needed!");

            checkState();

            ArrayList<SootClass> l = new ArrayList<SootClass>();
            SootClass cl = c;

            while (cl.hasSuperclass())
            {
                l.add(cl.getSuperclass());
                cl = cl.getSuperclass();
            }

            return Collections.unmodifiableList(l);
        }

        /** Returns a list of subinterfaces of c, including itself. */
        public List<SootClass> getSubinterfacesOfIncluding(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (!c.isInterface())
                throw new RuntimeException("interface needed!");

            List<SootClass> l = new ArrayList<SootClass>();
            l.addAll(getSubinterfacesOf(c));
            l.add(c);

            return Collections.unmodifiableList(l);
        }

        /** Returns a list of subinterfaces of c, excluding itself. */
        public List<SootClass> getSubinterfacesOf(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (!c.isInterface())
                throw new RuntimeException("interface needed!");

            checkState();

            // If already cached, return the value.
            if (interfaceToSubinterfaces.get(c) != null)
                return interfaceToSubinterfaces.get(c);

            // Otherwise, build up the hashmap.
            List<SootClass> l = new ArrayList<SootClass>();

            ListIterator<SootClass> it = interfaceToDirSubinterfaces.get(c).listIterator();
            while (it.hasNext())
            {
                l.addAll(getSubinterfacesOfIncluding((SootClass)it.next()));
            }

            interfaceToSubinterfaces.put(c, Collections.unmodifiableList(l));

            return Collections.unmodifiableList(l);
        }

        /** Returns a list of superinterfaces of c, including itself. */
        public List<SootClass> getSuperinterfacesOfIncluding(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (!c.isInterface())
                throw new RuntimeException("interface needed!");

            List<SootClass> l = new ArrayList<SootClass>();
            l.addAll(getSuperinterfacesOf(c));
            l.add(c);

            return Collections.unmodifiableList(l);
        }

        /** Returns a list of superinterfaces of c, excluding itself. */
        public List<SootClass> getSuperinterfacesOf(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (!c.isInterface())
                throw new RuntimeException("interface needed!");

            checkState();

            // If already cached, return the value.
            if (interfaceToSuperinterfaces.get(c) != null)
                return interfaceToSuperinterfaces.get(c);

            // Otherwise, build up the hashmap.
            List<SootClass> l = new ArrayList<SootClass>();

            ListIterator<SootClass> it = interfaceToDirSuperinterfaces.get(c).listIterator();
            while (it.hasNext())
            {
                l.addAll(getSuperinterfacesOfIncluding((SootClass)it.next()));
            }

            interfaceToSuperinterfaces.put(c, Collections.unmodifiableList(l));

            return Collections.unmodifiableList(l);
        }

        /** Returns a list of direct superclasses of c, excluding c. */
        public List<SootClass> getDirectSuperclassesOf(SootClass c)
        {
            throw new RuntimeException("Not implemented yet!");
        }

        /** Returns a list of direct subclasses of c, excluding c. */
        public List<SootClass> getDirectSubclassesOf(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (c.isInterface())
                throw new RuntimeException("class needed!");

            checkState();

            return Collections.unmodifiableList(classToDirSubclasses.get(c));
        }

        // This includes c in the list of subclasses.

        /** Returns a list of direct subclasses of c, including c. */
        public List<SootClass> getDirectSubclassesOfIncluding(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (c.isInterface())
                throw new RuntimeException("class needed!");

            checkState();

            List<SootClass> l = new ArrayList<SootClass>();
            l.addAll(classToDirSubclasses.get(c));
            l.add(c);

            return Collections.unmodifiableList(l);
        }

        /** Returns a list of direct superinterfaces of c. */
        public List<SootClass> getDirectSuperinterfacesOf(SootClass c)
        {
            throw new RuntimeException("Not implemented yet!");
        }

        /** Returns a list of direct subinterfaces of c. */
        public List<SootClass> getDirectSubinterfacesOf(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (!c.isInterface())
                throw new RuntimeException("interface needed!");

            checkState();

            return interfaceToDirSubinterfaces.get(c);
        }

        /** Returns a list of direct subinterfaces of c, including itself. */
        public List<SootClass> getDirectSubinterfacesOfIncluding(SootClass c)
        {
            c.checkLevel(SootClass.HIERARCHY);
            if (!c.isInterface())
                throw new RuntimeException("interface needed!");

            checkState();

            List<SootClass> l = new ArrayList<SootClass>();
            l.addAll(interfaceToDirSubinterfaces.get(c));
            l.add(c);

            return Collections.unmodifiableList(l);
        }

        /** Returns a list of direct implementers of c, excluding itself. */
        public List<SootClass> getDirectImplementersOf(SootClass i)
        {
            i.checkLevel(SootClass.HIERARCHY);
            if (!i.isInterface())
                throw new RuntimeException("interface needed; got "+i);

            checkState();

            return Collections.unmodifiableList(interfaceToDirImplementers.get(i));
        }

        /** Returns a list of implementers of c, excluding itself. */
        public List<SootClass> getImplementersOf(SootClass i)
        {
            i.checkLevel(SootClass.HIERARCHY);
            if (!i.isInterface())
                throw new RuntimeException("interface needed; got "+i);

            checkState();

            Iterator<SootClass> it = getSubinterfacesOfIncluding(i).iterator();
            ArraySet<SootClass> set = new ArraySet<SootClass>();

            while (it.hasNext())
            {
                SootClass c = it.next();

                set.addAll(getDirectImplementersOf(c));
            }

            ArrayList<SootClass> l = new ArrayList<SootClass>();
            l.addAll(set);

            return Collections.unmodifiableList(l);
        }

        /** Returns true if child is a subclass of possibleParent. */
        public boolean isClassSubclassOf(SootClass child, SootClass possibleParent)
        {
            child.checkLevel(SootClass.HIERARCHY);
            possibleParent.checkLevel(SootClass.HIERARCHY);
            return getSuperclassesOf(child).contains(possibleParent);
        }

        /** Returns true if child is, or is a subclass of, possibleParent. */
        public boolean isClassSubclassOfIncluding(SootClass child, SootClass possibleParent)
        {
            child.checkLevel(SootClass.HIERARCHY);
            possibleParent.checkLevel(SootClass.HIERARCHY);
            return getSuperclassesOfIncluding(child).contains(possibleParent);
        }

        /** Returns true if child is a direct subclass of possibleParent. */
        public boolean isClassDirectSubclassOf(SootClass c, SootClass c2)
        {
            throw new RuntimeException("Not implemented yet!");
        }

        /** Returns true if child is a superclass of possibleParent. */
        public boolean isClassSuperclassOf(SootClass parent, SootClass possibleChild)
        {
            parent.checkLevel(SootClass.HIERARCHY);
            possibleChild.checkLevel(SootClass.HIERARCHY);
            return getSubclassesOf(parent).contains(possibleChild);
        }

        /** Returns true if parent is, or is a superclass of, possibleChild. */
        public boolean isClassSuperclassOfIncluding(SootClass parent, SootClass possibleChild)
        {
            parent.checkLevel(SootClass.HIERARCHY);
            possibleChild.checkLevel(SootClass.HIERARCHY);
            return getSubclassesOfIncluding(parent).contains(possibleChild);
        }

        /** Returns true if child is a subinterface of possibleParent. */
        public boolean isInterfaceSubinterfaceOf(SootClass child, SootClass possibleParent)
        {
            child.checkLevel(SootClass.HIERARCHY);
            possibleParent.checkLevel(SootClass.HIERARCHY);
            return getSubinterfacesOf(possibleParent).contains(child);
        }

        /** Returns true if child is a direct subinterface of possibleParent. */
        public boolean isInterfaceDirectSubinterfaceOf(SootClass child,
                                                       SootClass possibleParent)
        {
            child.checkLevel(SootClass.HIERARCHY);
            possibleParent.checkLevel(SootClass.HIERARCHY);
            return getDirectSubinterfacesOf(possibleParent).contains(child);
        }

        /** Returns true if parent is a superinterface of possibleChild. */
        public boolean isInterfaceSuperinterfaceOf(SootClass parent, SootClass possibleChild)
        {
            parent.checkLevel(SootClass.HIERARCHY);
            possibleChild.checkLevel(SootClass.HIERARCHY);
            return getSuperinterfacesOf(possibleChild).contains(parent);
        }

        /** Returns true if parent is a direct superinterface of possibleChild. */
        public boolean isInterfaceDirectSuperinterfaceOf(SootClass parent,
                                                         SootClass possibleChild)
        {
            parent.checkLevel(SootClass.HIERARCHY);
            possibleChild.checkLevel(SootClass.HIERARCHY);
            return getDirectSuperinterfacesOf(possibleChild).contains(parent);
        }

        /** Returns the most specific type which is an ancestor of both c1 and c2. */
        public SootClass getLeastCommonSuperclassOf(SootClass c1,
                                                    SootClass c2)
        {
            c1.checkLevel(SootClass.HIERARCHY);
            c2.checkLevel(SootClass.HIERARCHY);
            throw new RuntimeException("Not implemented yet!");
        }

        // Questions about method invocation.

        /** Returns true if the method m is visible from code in the class from. */
        public boolean isVisible( SootClass from, SootMethod m ) {
            from.checkLevel(SootClass.HIERARCHY);
            m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
            if( m.isPublic() ) return true;
            if( m.isPrivate() ) {
                return from.equals( m.getDeclaringClass() );
            }
            if( m.isProtected() ) {
                return isClassSubclassOfIncluding( from, m.getDeclaringClass() );
            }
            // m is package
            return from.getJavaPackageName().equals(
                    m.getDeclaringClass().getJavaPackageName() );
            //|| isClassSubclassOfIncluding( from, m.getDeclaringClass() );
        }

        /** Given an object of actual type C (o = new C()), returns the method which will be called
         on an o.f() invocation. */
        public SootMethod resolveConcreteDispatch(SootClass concreteType, SootMethod m)
        {
            concreteType.checkLevel(SootClass.HIERARCHY);
            m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
            checkState();

            if (concreteType.isInterface())
                throw new RuntimeException("class needed!");

            Iterator<SootClass> it = getSuperclassesOfIncluding(concreteType).iterator();
            String methodSig = m.getSubSignature();

            while (it.hasNext())
            {
                SootClass c = it.next();
                if (c.declaresMethod(methodSig)
                        && isVisible( c, m )
                        ) {
                    return c.getMethod(methodSig);
                }
            }
            throw new RuntimeException("could not resolve concrete dispatch!\nType: "+concreteType+"\nMethod: "+m);
        }

        /** Given a set of definite receiver types, returns a list of possible targets. */
        public List resolveConcreteDispatch(List classes, SootMethod m)
        {
            m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
            checkState();

            ArraySet s = new ArraySet();
            Iterator classesIt = classes.iterator();

            while (classesIt.hasNext()) {
                Object cls = classesIt.next();
                if (cls instanceof RefType)
                    s.add(resolveConcreteDispatch(((RefType)cls).getSootClass(), m));
                else if (cls instanceof ArrayType) {
                    s.add(resolveConcreteDispatch((RefType.v("java.lang.Object")).getSootClass(), m));
                }
                else throw new RuntimeException("Unable to resolve concrete dispatch of type "+ cls);
            }

            List l = new ArrayList(); l.addAll(s);
            return Collections.unmodifiableList(l);
        }

        // what can get called for c & all its subclasses

        /** Given an abstract dispatch to an object of type c and a method m, gives
         * a list of possible receiver methods. */
        public List resolveAbstractDispatch(SootClass c, SootMethod m)
        {
            c.checkLevel(SootClass.HIERARCHY);
            m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
            checkState();

            Iterator<SootClass> classesIt = null;

            if (c.isInterface()) {
                classesIt = getImplementersOf(c).iterator();
                HashSet<SootClass> classes = new HashSet<SootClass>();
                while (classesIt.hasNext())
                    classes.addAll(getSubclassesOfIncluding(classesIt.next()));
                classesIt = classes.iterator();
            }

            else
                classesIt = getSubclassesOfIncluding(c).iterator();

            ArraySet s = new ArraySet();

            while (classesIt.hasNext()) {
                SootClass cl = classesIt.next();
                if( Modifier.isAbstract( cl.getModifiers() ) ) continue;
                s.add(resolveConcreteDispatch(cl, m));
            }

            List l = new ArrayList(); l.addAll(s);
            return Collections.unmodifiableList(l);
        }

        // what can get called if you have a set of possible receiver types

        /** Returns a list of possible targets for the given method and set of receiver types. */
        public List resolveAbstractDispatch(List classes, SootMethod m)
        {
            m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
            ArraySet s = new ArraySet();
            Iterator classesIt = classes.iterator();

            while (classesIt.hasNext())
                s.addAll(resolveAbstractDispatch((SootClass)classesIt.next(), m));

            List l = new ArrayList(); l.addAll(s);
            return Collections.unmodifiableList(l);
        }

        /** Returns the target for the given SpecialInvokeExpr. */
        public SootMethod resolveSpecialDispatch(SpecialInvokeExpr ie, SootMethod container)
        {
            container.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
            SootMethod target = ie.getMethod();
            target.getDeclaringClass().checkLevel(SootClass.HIERARCHY);

        /* This is a bizarre condition!  Hopefully the implementation is correct.
           See VM Spec, 2nd Edition, Chapter 6, in the definition of invokespecial. */
            if (target.getName().equals("<init>") || target.isPrivate())
                return target;
            else if (isClassSubclassOf(target.getDeclaringClass(), container.getDeclaringClass()))
                return resolveConcreteDispatch(container.getDeclaringClass(), target);
            else
                return target;
        }
    }

    /**
     * Test for Hierarchy that involve only one SootClass.
     * Note that the following are Skipped because not implemented in the legacy Hierarchy:
     * <ul>
     *     <li>getDirectSuperinterfacesOf</li>
     *     <li>getLeastCommonSuperclassOf</li>
     *     <li>getDirectSuperinterfacesOf</li>
     * </ul>
     *
     * The following is skipped because the condition is really messed up:
     * <ul>
     *     <li>resolveSpecialDispatch</li>
     * </ul>
     */
    @RunWith(Parameterized.class)
    public static class HierarchyRegressionTestSingleArg {


        static Hierarchy hierarchy;
        static OldHierarchy oldHierarchy;
        final SootClass classUnderTest;

        public HierarchyRegressionTestSingleArg(SootClass sc){
            classUnderTest = sc;
        }

        static public void init(){
            G.reset();
            Options.v().set_prepend_classpath(true);
            Options.v().set_time(false);

            Scene.v().loadNecessaryClasses();
            //the array list wrapping is to avoid a concurrent modification exception
            int numClasses;
            do{ //load to SIGNATURES until a fixed point is reached
                numClasses = Scene.v().getClasses().size();
                for (SootClass sc : new ArrayList<SootClass>(Scene.v().getClasses())){
                    if (sc.resolvingLevel() < SootClass.SIGNATURES)
                        Scene.v().forceResolve(sc.getName(), SootClass.SIGNATURES);
                }
            } while (numClasses < Scene.v().getClasses().size());
            hierarchy = new Hierarchy();
            oldHierarchy = new OldHierarchy();
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data(){
            init();
            Chain<SootClass> classes = Scene.v().getClasses();
            List<Object[]> list = new ArrayList<Object[]>(classes.size());
            SootClass object = Scene.v().getObjectType().getSootClass();
            for (SootClass sc : classes){
                if (sc != object)
                    list.add(new Object[]{sc});
            }
            return list;
        }

        @Test
        public void testDirectImplementers(){
            if (classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getDirectImplementersOf(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getDirectImplementersOf(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testDirectSubclassesOf(){
            if (!classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getDirectSubclassesOf(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getDirectSubclassesOf(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testDirectSubclassesOfIncluding(){
            if (!classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getDirectSubclassesOfIncluding(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getDirectSubclassesOfIncluding(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testDirectSubinterfacesOf(){
            if (classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getDirectSubinterfacesOf(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getDirectSubinterfacesOf(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testDirectSubinterfacesOfIncluding(){
            if (classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getDirectSubinterfacesOfIncluding(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getDirectSubinterfacesOfIncluding(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testImplementersOf(){
            if (classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getImplementersOf(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getImplementersOf(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testSubclassesOf(){
            if (!classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getSubclassesOf(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getSubclassesOf(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testSubclassesOfIncluding(){
            if (!classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getSubclassesOfIncluding(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getSubclassesOfIncluding(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testSubinterfacesOf(){
            if (classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getSubinterfacesOf(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getSubinterfacesOf(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testSubinterfacesOfIncluding(){
            if (classUnderTest.isInterface()){
                Collection<SootClass> newResult = hierarchy.getSubinterfacesOfIncluding(classUnderTest);
                Collection<SootClass> oldResult = oldHierarchy.getSubinterfacesOfIncluding(classUnderTest);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testIsVisible(){
            for (SootMethod sm : classUnderTest.getMethods()){
                boolean newResult = hierarchy.isVisible(classUnderTest, sm);
                boolean oldResult = oldHierarchy.isVisible(classUnderTest,sm);
                assertEquals(oldResult,newResult);
                Collection<SootClass> possibleTargets = classUnderTest.isInterface()?
                        hierarchy.getImplementersOf(classUnderTest):hierarchy.getSubclassesOf(classUnderTest);
                for (SootClass sc : possibleTargets){
                    newResult = hierarchy.isVisible(sc, sm);
                    oldResult = oldHierarchy.isVisible(sc,sm);
                    assertEquals(oldResult,newResult);
                }
            }
        }

        @Test
        public void testConcreteDispatch1(){
            if (!classUnderTest.isInterface()){
                for (SootMethod sm : classUnderTest.getMethods()){
                    SootMethod newResult = hierarchy.resolveConcreteDispatch(classUnderTest, sm);
                    SootMethod oldResult = oldHierarchy.resolveConcreteDispatch(classUnderTest, sm);
                    assertEquals(oldResult,newResult);
                }
            }
        }

        @Test
        public void testConcreteDispatch2(){
            if (!classUnderTest.isInterface()){

                List<Type> typeUnderTest = Arrays.asList((Type)classUnderTest.getType());
                for (SootMethod sm : classUnderTest.getMethods()){
                    Collection<SootMethod> newResult = hierarchy.resolveConcreteDispatch(typeUnderTest, sm);
                    Collection<SootMethod> oldResult = hierarchy.resolveConcreteDispatch(typeUnderTest, sm);
                    performTest(newResult,oldResult);
                }
            }
        }

        @Test
        public void testAbstractDispatch1(){
            for (SootMethod sm : classUnderTest.getMethods()){
                Collection<SootMethod> newResult = hierarchy.resolveAbstractDispatch(classUnderTest, sm);
                Collection<SootMethod> oldResult = oldHierarchy.resolveAbstractDispatch(classUnderTest, sm);
                performTest(newResult, oldResult);
            }
        }

        @Test
        public void testAbstractDispatch2(){
            for (SootMethod sm : classUnderTest.getMethods()){
                Collection<SootMethod> newResult = hierarchy.resolveAbstractDispatch(Arrays.asList(classUnderTest), sm);
                Collection<SootMethod> oldResult = oldHierarchy.resolveAbstractDispatch(Arrays.asList(classUnderTest), sm);
                performTest(newResult, oldResult);
            }
        }

        /**
         * Performs a test against two collections of results.
         * The test is unordered collection equality
         * @param newResult the 'new' result
         * @param oldResult the 'old' result, which we are comparing against
         * @param <T> the type of the collection
         */
        private <T> void performTest(Collection<T> newResult, Collection<T> oldResult){
            //Convert to sets to make sure that order doesn't matter in the check
            Set<T> newAsSet = new HashSet<T>(newResult); //this is redundant, but paranoia is OK here
            Set<T> oldAsSet = new HashSet<T>(oldResult);

            assertEquals("Failed test for "+ classUnderTest.getName(), oldAsSet,newAsSet);

        }

    }

    /**
     * Test for Hierarchy that involve two SootClass objects.
     *
     * The test data is generated by creating the cross product of the set of classes.
     *
     * Note that the following are Skipped because not implemented in the legacy Hierarchy:
     * <ul>
     *     <li>isClassDirectSubclassOf</li>
     *     <li>isInterfaceDirectSuperinterfaceOf</li>
     * </ul>
     *
     */
    @RunWith(Parameterized.class)
    public static class HierarchyRegressionTestDoubleArg {


        static Hierarchy hierarchy;
        static OldHierarchy oldHierarchy;
        final SootClass classUnderTest1;
        final SootClass classUnderTest2;

        public HierarchyRegressionTestDoubleArg(SootClass sc, SootClass sc2){
            classUnderTest1 = sc;
            classUnderTest2 = sc2;
        }

        static public void init(){
            G.reset();
            Options.v().set_prepend_classpath(true);
            Options.v().set_time(false);

            Scene.v().loadNecessaryClasses();
            //the array list wrapping is to avoid a concurrent modification exception
            for (SootClass sc : new ArrayList<SootClass>(Scene.v().getClasses())){
                if (sc.resolvingLevel() < SootClass.HIERARCHY)
                    Scene.v().forceResolve(sc.getName(), SootClass.HIERARCHY);
            }
            hierarchy = new Hierarchy();
            oldHierarchy = new OldHierarchy();
        }

        @Parameterized.Parameters
        public static Collection<Object[]> data(){
            init();
            Chain<SootClass> classes = Scene.v().getClasses();
            List<Object[]> list = new ArrayList<Object[]>(classes.size());
            SootClass object = Scene.v().getObjectType().getSootClass();
            Set<SootClass> almostAllClasses = new HashSet<SootClass>(Scene.v().getClasses());
            almostAllClasses.remove(object);


            Set<List<SootClass>> allPairs = Sets.cartesianProduct(almostAllClasses,almostAllClasses);

            for (List<SootClass> tuple : allPairs){
                list.add(tuple.toArray());
            }
            return list;
        }

        @Test
        public void testIsSubclassOf(){
            if (!classUnderTest1.isInterface()){
                boolean newResult = hierarchy.isClassSubclassOf(classUnderTest1, classUnderTest2);
                boolean oldResult = oldHierarchy.isClassSubclassOf(classUnderTest1, classUnderTest2);
                assertEquals(oldResult,newResult);
            }
        }

        @Test
        public void testIsSubclassOfIncluding(){
            if (!classUnderTest1.isInterface()){
                boolean newResult = hierarchy.isClassSubclassOfIncluding(classUnderTest1, classUnderTest2);
                boolean oldResult = oldHierarchy.isClassSubclassOfIncluding(classUnderTest1, classUnderTest2);
                assertEquals(oldResult,newResult);
            }
        }

        @Test
        public void testIsSuperclassOf(){
            if (!classUnderTest1.isInterface()){
                boolean newResult = hierarchy.isClassSuperclassOf(classUnderTest1, classUnderTest2);
                boolean oldResult = oldHierarchy.isClassSuperclassOf(classUnderTest1, classUnderTest2);
                assertEquals(oldResult,newResult);
            }
        }

        @Test
        public void testIsSuperclassOfIncluding(){
                if (!classUnderTest1.isInterface()){
                boolean newResult = hierarchy.isClassSuperclassOfIncluding(classUnderTest1, classUnderTest2);
                boolean oldResult = oldHierarchy.isClassSuperclassOfIncluding(classUnderTest1, classUnderTest2);
                assertEquals(oldResult,newResult);
            }
        }

        @Test
        public void testIsDirectSubinterfaceOf(){
            if (classUnderTest1.isInterface() && classUnderTest2.isInterface()){
                boolean newResult = hierarchy.isInterfaceDirectSubinterfaceOf(classUnderTest1, classUnderTest2);
                boolean oldResult = oldHierarchy.isInterfaceDirectSubinterfaceOf(classUnderTest1, classUnderTest2);
                assertEquals(oldResult,newResult);
            }
        }

        @Test
        public void testIsSubinterfaceOf(){
            if (classUnderTest1.isInterface() && classUnderTest2.isInterface()){
                boolean newResult = hierarchy.isInterfaceSubinterfaceOf(classUnderTest1, classUnderTest2);
                boolean oldResult = oldHierarchy.isInterfaceSubinterfaceOf(classUnderTest1, classUnderTest2);
                assertEquals(oldResult,newResult);
            }
        }

        @Test
        public void testIsSuperinterfaceOf(){
            if (classUnderTest1.isInterface() && classUnderTest2.isInterface()){
                boolean newResult = hierarchy.isInterfaceSuperinterfaceOf(classUnderTest1, classUnderTest2);
                boolean oldResult = oldHierarchy.isInterfaceSuperinterfaceOf(classUnderTest1, classUnderTest2);
                assertEquals(oldResult,newResult);
            }
        }

    }

    public static class GeneralTests {

        private static Hierarchy hierarchy;

        @BeforeClass
        static public void init(){
            G.reset();
            Options.v().set_prepend_classpath(true);
            Options.v().set_time(false);

            Scene.v().loadNecessaryClasses();
            //the array list wrapping is to avoid a concurrent modification exception
            for (SootClass sc : new ArrayList<SootClass>(Scene.v().getClasses())){
                if (sc.resolvingLevel() < SootClass.HIERARCHY)
                    Scene.v().forceResolve(sc.getName(), SootClass.HIERARCHY);
            }
            hierarchy = new Hierarchy();
        }

        @Test
        public void testObjectDescendents1(){
            SootClass object = Scene.v().getObjectType().getSootClass();
            Collection<SootClass> subClasses = hierarchy.getSubclassesOfIncluding(object);
            performTest(subClasses,Scene.v().getClasses());
        }

        @Test
        public void testObjectDescendents2(){
            SootClass object = Scene.v().getObjectType().getSootClass();
            Collection<SootClass> subClasses = hierarchy.getSubclassesOf(object);
            Set<SootClass> almostAll = new HashSet<SootClass>(Scene.v().getClasses());
            almostAll.remove(object);
            performTest(subClasses,almostAll);
        }

        @Test
        public void testObjectDescendentsHasAllInterfaces(){
            SootClass object = Scene.v().getObjectType().getSootClass();
            Collection<SootClass> subClasses = hierarchy.getSubclassesOf(object);
            Set<SootClass> allInterfaces = new HashSet<SootClass>();
            for (SootClass sc : Scene.v().getClasses())
                if (sc.isInterface())
                    allInterfaces.add(sc);

            assertTrue(subClasses.containsAll(allInterfaces));
        }

        @Test
        public void testObjectDirectDescendentsHasAllInterfaces(){
            //This is a debatable interpretation of "all interfaces are subclasses of Object",
            //Here, we consider that the keyword 'extends' is cheating the hierarchy information, so we check that they are
            //directly descending from Object.
            SootClass object = Scene.v().getObjectType().getSootClass();
            Collection<SootClass> subClasses = hierarchy.getDirectSubclassesOf(object);
            Set<SootClass> allInterfaces = new HashSet<SootClass>();
            for (SootClass sc : Scene.v().getClasses())
                if (sc.isInterface())
                    allInterfaces.add(sc);

            assertTrue(subClasses.containsAll(allInterfaces));
        }

        /**
         * Performs a test against two collections of results.
         * The test is unordered collection equality
         * @param result the result to check
         * @param expected the result which we are comparing against
         * @param <T> the type of the collection
         */
        private <T> void performTest(Collection<T> result, Collection<T> expected){
            //Convert to sets to make sure that order doesn't matter in the check
            Set<T> resultAsSet = new HashSet<T>(result); //this is redundant, but paranoia is OK here
            Set<T> expectedAsSet = new HashSet<T>(expected);

            assertEquals(expectedAsSet,resultAsSet);

        }

    }

}