package ca.mcgill.sable.soot;

import java.util.*;

public class Hierarchy
{
    // These two maps are not filled in the constructor.
    HashMap classToSubclasses;
    HashMap interfaceToSubinterfaces;

    HashMap classToDirSubclasses;
    HashMap interfaceToDirSubinterfaces;

    // This holds the direct implementers.
    HashMap interfaceToDirImplementers;

    int state;
    Scene sc;

    public Hierarchy(Scene sc)
    {
        this.sc = sc;
        state = sc.getState();

        // Well, this used to be describable by 'Duh'.
        // Construct the subclasses hierarchy and the subinterfaces hierarchy.
        {
            List allClasses = sc.getClasses();

            classToSubclasses = new HashMap(allClasses.size() * 2 + 1, 0.7f);
            interfaceToSubinterfaces = new HashMap(allClasses.size() * 2 + 1, 0.7f);

            classToDirSubclasses = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);
            interfaceToDirSubinterfaces = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);
            interfaceToDirImplementers = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);

            Iterator classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();

                if (c.isInterface())
                {
                    interfaceToDirSubinterfaces.put(c, new ArrayList());
                    interfaceToDirImplementers.put(c, new ArrayList());
                }
                else
                    classToDirSubclasses.put(c, new ArrayList());
            }

            classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();
                if (c.hasSuperclass())
                {
                    if (c.isInterface())
                    {
                        Iterator subIt = c.getInterfaces().iterator();

                        while (subIt.hasNext())
                        {
                            SootClass i = (SootClass)subIt.next();
                            List l = (List)interfaceToDirSubinterfaces.get(i);
                            l.add(c);
                        }
                    }
                    else
                    {
                        List l = (List)classToDirSubclasses.get(c.getSuperclass());
                        l.add(c);

                    
                        Iterator subIt = c.getInterfaces().iterator();

                        while (subIt.hasNext())
                        {
                            SootClass i = (SootClass)subIt.next();
                            l = (List)interfaceToDirImplementers.get(i);
                            l.add(c);
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
                    if (c.isInterface())
                    {
                        List imp = (List)interfaceToDirImplementers.get(c);
                        Set s = new HashSet(imp.size() * 2 + 1, 0.7f);
                        
                        Iterator impIt = imp.iterator();
                        while (impIt.hasNext())
                        {
                            SootClass c0 = (SootClass)impIt.next();
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
                if (c.isInterface())
                {
                    interfaceToDirSubinterfaces.put(c, Collections.unmodifiableList
                                          ((List)interfaceToDirSubinterfaces.get(c)));
                    interfaceToDirImplementers.put(c, Collections.unmodifiableList
                                                ((List)interfaceToDirImplementers.get(c)));
                }
                else
                    classToDirSubclasses.put(c, Collections.unmodifiableList
                                          ((List)classToDirSubclasses.get(c)));
            }
        }
    }

    private void checkState()
    {
        if (state != sc.getState())
            throw new ConcurrentModificationException("Scene changed for Hierarchy!");
    }

    // This includes c in the list of subclasses.
    public List getSubclassesOfIncluding(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        List l = new ArrayList();
        l.addAll(getSubclassesOf(c));
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    public List getSubclassesOf(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        checkState();

        // If already cached, return the value.
        if (classToSubclasses.get(c) != null)
            return (List)classToSubclasses.get(c);

        // Otherwise, build up the hashmap.
        List l = new ArrayList();

        ListIterator it = ((List)classToDirSubclasses.get(c)).listIterator();
        while (it.hasNext())
        {
            l.addAll(getSubclassesOfIncluding((SootClass)it.next()));
        }
        
        l = Collections.unmodifiableList(l);
        classToSubclasses.put(c, l);

        return l;
    }

    // Return all strict superclasses of c.
    public List getSuperclassesOf(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        checkState();

        ArrayList l = new ArrayList();
        SootClass cl = c;

        while (cl.hasSuperclass())
        {
            l.add(cl.getSuperclass());
            cl = cl.getSuperclass();
        }

        return Collections.unmodifiableList(l);
    }

    public List getSubinterfacesOfIncluding(SootClass c)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        List l = new ArrayList();
        l.addAll(getSubinterfacesOf(c));
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    public List getSubinterfacesOf(SootClass c)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        // If already cached, return the value.
        if (interfaceToSubinterfaces.get(c) != null)
            return (List)interfaceToSubinterfaces.get(c);

        // Otherwise, build up the hashmap.
        List l = new ArrayList();

        ListIterator it = ((List)interfaceToDirSubinterfaces.get(c)).listIterator();
        while (it.hasNext())
        {
            l.addAll(getSubinterfacesOfIncluding((SootClass)it.next()));
        }
        
        interfaceToSubinterfaces.put(c, Collections.unmodifiableList(l));

        return Collections.unmodifiableList(l);
    }

    public List getSuperinterfacesOf(SootClass c)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    public List getDirectSuperclassesOf(SootClass c)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    public List getDirectSubclassesOf(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        checkState();

        return Collections.unmodifiableList((List)classToDirSubclasses.get(c));
    }

    // This includes c in the list of subclasses.
    public List getDirectSubclassesOfIncluding(SootClass c)
    {
        if (c.isInterface())
            throw new RuntimeException("class needed!");

        checkState();

        List l = new ArrayList();
        l.addAll((List)classToDirSubclasses.get(c));
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    public List getDirectSuperinterfacesOf(SootClass c)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    public List getDirectSubinterfacesOf(SootClass c)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        return (List)interfaceToDirSubinterfaces.get(c);
    }

    public List getDirectSubinterfacesOfIncluding(SootClass c)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        List l = new ArrayList();
        l.addAll((List)interfaceToDirSubinterfaces.get(c));
        l.add(c);

        return Collections.unmodifiableList(l);
    }

    public List getDirectImplementersOf(SootClass i)
    {
        if (!i.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        return Collections.unmodifiableList((List)interfaceToDirImplementers.get(i));
    }

    public List getImplementersOf(SootClass i)
    {
        if (!i.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        Iterator it = getSubinterfacesOfIncluding(i).iterator();
        HashSet set = new HashSet();

        while (it.hasNext())
        {
            SootClass c = (SootClass)it.next();

            set.addAll(getDirectImplementersOf(c));
        }

        ArrayList l = new ArrayList();
        l.addAll(set);

        return Collections.unmodifiableList(l);
    }

    public boolean isClassSubclassOf(SootClass c, SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }
    public boolean isInterfaceSubinterfaceOf(SootClass c, SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    public boolean classExtends(SootClass c, SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }
    public boolean classDirectlyExtends(SootClass c, SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }
    public boolean isClassDirectSubclassOf(SootClass c, SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }
    public boolean isInterfaceDirectSubinterfaceOf(SootClass c,
                                                   SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    public SootClass getLeastCommonSuperclassOf(SootClass c1, 
                                                SootClass c2)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    // what really gets called if you invoke method m on an object of class c
    public SootClass resolveInvoke(SootClass c, SootMethod m)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    // what can get called for c & all its subclasses
    public List getTargetsOf(SootClass c, SootMethod m) 
    {
        throw new RuntimeException("Not implemented yet!");
    }

    // what can get called if you have a set of possible receiver types
    public List getTargetsOf(List classes, SootMethod m)
    {
        throw new RuntimeException("Not implemented yet!");
    }
}
