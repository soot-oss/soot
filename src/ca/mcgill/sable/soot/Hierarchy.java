package ca.mcgill.sable.soot;

import java.util.*;

public class Hierarchy
{
    HashMap classToSubclasses;
    HashMap interfaceToSubinterfaces;

    // This holds the direct implementers.
    HashMap interfaceToImplementers;

    int state;
    Scene sc;

    public Hierarchy(Scene sc)
    {
        // Well, this used to be describable by 'Duh'.
        // Construct the subclasses hierarchy and the subinterfaces hierarchy.
        {
            List allClasses = sc.getClasses();

            classToSubclasses = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);
            interfaceToSubinterfaces = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);
            interfaceToImplementers = new HashMap
                (allClasses.size() * 2 + 1, 0.7f);

            Iterator classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();

                if (c.isInterface())
                {
                    interfaceToSubinterfaces.put(c, new ArrayList());
                    interfaceToImplementers.put(c, new ArrayList());
                }
                else
                    classToSubclasses.put(c, new ArrayList());
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
                            List l = (List)interfaceToSubinterfaces.get(i);
                            l.add(c);
                        }
                    }
                    else
                    {
                        List l = (List)classToSubclasses.get(c.getSuperclass());
                        l.add(c);

                    
                        Iterator subIt = c.getInterfaces().iterator();

                        while (subIt.hasNext())
                        {
                            SootClass i = (SootClass)subIt.next();
                            l = (List)interfaceToImplementers.get(i);
                            l.add(c);
                        }
                    }
                }
            }

            classesIt = allClasses.iterator();
            while (classesIt.hasNext())
            {
                SootClass c = (SootClass)classesIt.next();
                if (c.isInterface())
                {
                    interfaceToSubinterfaces.put(c, Collections.unmodifiableList
                                          ((List)interfaceToSubinterfaces.get(c)));
                    interfaceToImplementers.put(c, Collections.unmodifiableList
                                                ((List)interfaceToImplementers.get(c)));
                }
                else
                    classToSubclasses.put(c, Collections.unmodifiableList
                                          ((List)classToSubclasses.get(c)));
            }
        }

        this.sc = sc;
        state = sc.getState();
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

        checkState();

        List l = new ArrayList();
        l.addAll((List)classToSubclasses.get(c));
        l.add(c);

        return Collections.unmodifiableList(l);
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

        checkState();

        List l = new ArrayList();
        l.addAll((List)interfaceToSubinterfaces.get(c));
        l.add(c);

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
        throw new RuntimeException("Not implemented yet!");
    }
    public List getDirectSuperinterfacesOf(SootClass c)
    {
        throw new RuntimeException("Not implemented yet!");
    }
    public List getDirectSubinterfacesOf(SootClass c)
    {
        throw new RuntimeException("Not implemented yet!");
    }

    public List getDirectImplementersOf(SootClass i)
    {
        if (!c.isInterface())
            throw new RuntimeException("interface needed!");

        checkState();

        return Collections.unmodifiableList((List)interfaceToImplementers.get(i));
    }

    public List getImplementersOf(SootClass i)
    {
        if (!c.isInterface())
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
