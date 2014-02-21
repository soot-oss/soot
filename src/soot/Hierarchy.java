/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

import soot.jimple.SpecialInvokeExpr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents the class hierarchy.  It is closely linked to a Scene,
 * This is a wrapper that provides the old Hierarchy interface. Its use is discouraged
 * and may vanish in a future version.
 * This method will have performance overheads, be warned!
 */
@Deprecated
public class Hierarchy
{

    private final Hierarchy2 h = new Hierarchy2();

    public List<SootClass> getSubclassesOfIncluding(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getSubclassesOfIncluding(c)));
    }

    public List<SootClass> getDirectImplementersOf(SootClass i) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getDirectImplementersOf(i)));
    }

    public List<SootClass> getSuperclassesOfIncluding(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getSuperclassesOfIncluding(c)));
    }

    public List<SootMethod> resolveAbstractDispatch(SootClass c, SootMethod m) {
        return Collections.unmodifiableList(new ArrayList<SootMethod>(h.resolveAbstractDispatch(c, m)));
    }

    public List<SootClass> getDirectSubclassesOf(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getDirectSubclassesOf(c)));
    }

    public List<SootClass> getDirectSubclassesOfIncluding(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getDirectSubclassesOfIncluding(c)));
    }

    public boolean isInterfaceDirectSubinterfaceOf(SootClass child, SootClass possibleParent) {
        return h.isInterfaceDirectSubinterfaceOf(child, possibleParent);
    }

    public SootMethod resolveSpecialDispatch(SpecialInvokeExpr ie, SootMethod container) {
        return h.resolveSpecialDispatch(ie, container);
    }

    public List<SootClass> getSubinterfacesOfIncluding(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getSubinterfacesOfIncluding(c)));
    }

    public boolean isClassSuperclassOf(SootClass parent, SootClass possibleChild) {
        return h.isClassSuperclassOf(parent, possibleChild);
    }

    public List<SootClass> getSubclassesOf(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getSubclassesOf(c)));
    }

    public boolean isClassDirectSubclassOf(SootClass c, SootClass c2) {
        return h.isClassDirectSubclassOf(c, c2);
    }

    
    public List<SootClass> getSuperinterfacesOf(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getSuperinterfacesOf(c)));
    }

    public List<SootClass> getDirectSubinterfacesOfIncluding(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getDirectSubinterfacesOfIncluding(c)));
    }

    public boolean isInterfaceSuperinterfaceOf(SootClass parent, SootClass possibleChild) {
        return h.isInterfaceSuperinterfaceOf(parent, possibleChild);
    }

    public SootClass getLeastCommonSuperclassOf(SootClass c1, SootClass c2) {
        return h.getLeastCommonSuperclassOf(c1, c2);
    }

    public List<SootClass> getSuperinterfacesOfIncluding(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getSuperinterfacesOfIncluding(c)));
    }

    public List<SootMethod> resolveConcreteDispatch(Collection<Type> classes, SootMethod m) {
        return Collections.unmodifiableList(new ArrayList<SootMethod>(h.resolveConcreteDispatch(classes, m)));
    }

    public List<SootClass> getDirectSuperinterfacesOf(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getDirectSuperinterfacesOf(c)));
    }

    public boolean isClassSubclassOf(SootClass child, SootClass possibleParent) {
        return h.isClassSubclassOf(child, possibleParent);
    }

    public List<SootClass> getImplementersOf(SootClass i) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getImplementersOf(i)));
    }

    public boolean isClassSuperclassOfIncluding(SootClass parent, SootClass possibleChild) {
        return h.isClassSuperclassOfIncluding(parent, possibleChild);
    }

    public List<SootClass> getDirectSuperclassesOf(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getDirectSuperclassesOf(c)));
    }

    public boolean isInterfaceSubinterfaceOf(SootClass child, SootClass possibleParent) {
        return h.isInterfaceSubinterfaceOf(child, possibleParent);
    }

    public List<SootMethod> resolveAbstractDispatch(Collection<SootClass> classes, SootMethod m) {
        return Collections.unmodifiableList(new ArrayList<SootMethod>(h.resolveAbstractDispatch(classes, m)));
    }

    public boolean isClassSubclassOfIncluding(SootClass child, SootClass possibleParent) {
        return h.isClassSubclassOfIncluding(child, possibleParent);
    }

    public boolean isInterfaceDirectSuperinterfaceOf(SootClass parent, SootClass possibleChild) {
        return h.isInterfaceDirectSuperinterfaceOf(parent, possibleChild);
    }

    public boolean isVisible(SootClass from, SootMethod m) {
        return h.isVisible(from, m);
    }

    public SootMethod resolveConcreteDispatch(SootClass concreteType, SootMethod m) {
        return h.resolveConcreteDispatch(concreteType, m);
    }

    public List<SootClass> getDirectSubinterfacesOf(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getDirectSubinterfacesOf(c)));
    }

    public List<SootClass> getSubinterfacesOf(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getSubinterfacesOf(c)));
    }

    public List<SootClass> getSuperclassesOf(SootClass c) {
        return Collections.unmodifiableList(new ArrayList<SootClass>(h.getSuperclassesOf(c)));
    }

    /**
     * @return the wrapped Hierarchy2 object.
     */
    public Hierarchy2 getWrapped(){
        return h;
    }
}