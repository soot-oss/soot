/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Felix Kwok
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.invoke;

import java.util.*;
import soot.util.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.util.BitSet;

/** A bit-vector implementation for flow sets with types as its elements. */

public class TypeSet implements Set 
{
    static HashMap typeToNumber = null;
    static RefType[] numberToType = null;
    static TypeSet allTypes = null;
    static TypeSet libraryTypes = null;
    static TypeSet benchTypes = null;

  
    private BitSet types;

    private static void initialize() 
    {
        Chain allClasses = Scene.v().getClasses();
        LinkedList l = new LinkedList();
        for (Iterator clsIt = allClasses.iterator(); clsIt.hasNext(); ) {
            SootClass cls = (SootClass)clsIt.next();
            if (!cls.isInterface())
                l.add(RefType.v(cls));
        }
        numberToType = new RefType[1];
        numberToType = (RefType[])l.toArray(numberToType);
        typeToNumber = new HashMap();
        libraryTypes = new TypeSet();
        benchTypes = new TypeSet();
        for (int i = 0; i < numberToType.length; i++) {
            RefType t = numberToType[i];
            typeToNumber.put(t, new Integer(i));
            String name = t.toString();
            if (name.startsWith("java.") || name.startsWith("sun.") || name.startsWith("sunw.") ||
                name.startsWith("javax.")|| name.startsWith("org.") || name.startsWith("com."))
                libraryTypes.types.set(i);
            else
                benchTypes.types.set(i);
        }
        allTypes = new TypeSet();
        allTypes.addAll(benchTypes);
        allTypes.addAll(libraryTypes);
    }

    /** Creates an empty TypeSet. */
    public TypeSet() {
        types = new BitSet();
        if (typeToNumber == null)
            initialize();
    }

    /** Creates a TypeSet which contains a types specified in the collection <code>c</code>. */
    public TypeSet(Collection c) {
        types = new BitSet();
        if (typeToNumber == null)
            initialize();
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            RefType t = (RefType)it.next();
            int index = ((Integer)typeToNumber.get(t)).intValue();
            types.set(index);
        }
    }

    /** Adds a type to this set. */
    public boolean add(Object o) {
        RefType t = (RefType)o;
        int index = ((Integer)typeToNumber.get(t)).intValue();
        boolean retVal = !types.get(index);
        types.set(index);
        return retVal;
    }

    /** Adds all the types in <code>c</code> to this set. */
    public boolean addAll(Collection c) {
        boolean retVal = false;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            RefType t = (RefType)it.next();
            int index = ((Integer)typeToNumber.get(t)).intValue();
            retVal = retVal || (!types.get(index));
            types.set(index);
        }
        return retVal;
    }

    /** Adds all the types in <code>s</code> to this set. */
    public boolean addAll(TypeSet s) {
        BitSet temp = types;
        types.or(s.types);
        return temp.equals(types);
    }

    /** Removes all types in <code>s</code> from this set, if present. */
    public boolean removeAll(TypeSet s) {
        BitSet temp = types;
        types.andNot(s.types);
        return temp.equals(types);
    }
    
    /** Removes all types in this set, except for those specified in <code>s</code>. */
    public boolean retainAll(TypeSet s) {
        BitSet temp = types;
        types.and(s.types);
        return temp.equals(types);
    }

    /** Removes all types from this set. */
    public void clear() {
        types = new BitSet();
    }

    /** Returns true if the type <code>o</code> is in this set, false otherwise. */
    public boolean contains(Object o) {
        RefType t = (RefType)o;
        int index = ((Integer)typeToNumber.get(t)).intValue();
        return types.get(index);
    }

    /** Returns true if all the types in <code>c</code> are in this set, false otherwise. */
    public boolean containsAll(Collection c) {
        boolean retValue = true;
        for (Iterator it = c.iterator(); it.hasNext(); ) {
            RefType t = (RefType)it.next();
            int index = ((Integer)typeToNumber.get(t)).intValue();
            retValue = retValue && types.get(index);
        }
        return retValue;
    }

    /** Returns the cardinality of this set. */
    public int size() {
        int size = 0;
        int N = types.length();
        for (int i = 0; i < N; i++)
            if (types.get(i))
                size++;
        return size;
    }

    /** Returns an iterator over this set. */
    public Iterator iterator() {
        return toList().iterator();
    }

    private List toList() { 
        LinkedList l = new LinkedList();
	for (BitSetIterator it = types.iterator(); it.hasNext(); )
	    l.add(numberToType[it.next()]);
        return l;
    }

    /** Returns a string representation of this set. */
    public String toString() {
        String str = "[";
        Iterator it = toList().iterator();
        while (it.hasNext()) { 
            str = str + it.next().toString();
            if (it.hasNext())
                str = str + ",";
        }
        str = str + "]";
        return str;
    }

    /** Tests whether this set is equal to the TypeSet <code>o</code>, in terms of set equality. */
    public boolean equals(Object o) {
        if (!(o instanceof TypeSet))
            return false;
        return types.equals(((TypeSet)o).types);
    }

    /** Returns an object array containing the elements of this set. */
    public Object[] toArray() {
        return toList().toArray();
    }

    /** Returns true if the set is empty. */
    public boolean isEmpty() {
        return (types.length()==0);
    }
    
    /** Removes the object <code>o</code> from this set. */
    public boolean remove(Object o) {
        RefType t = (RefType)o;
        int index = ((Integer)typeToNumber.get(t)).intValue();
        boolean retVal = types.get(index);
        types.clear(index);
        return retVal;
    }

    /** Removes all types in <code>s</code> from this set, if present. */
    public boolean removeAll(Collection c) {
        TypeSet t = new TypeSet(c);
        BitSet temp = types;
        types.andNot(t.types);
        return (temp.equals(types));
    }

    /** Removes all types in this set, except for those specified in <code>s</code>. */
    public boolean retainAll(Collection c) {
        TypeSet t = new TypeSet(c);
        BitSet temp = types;
        types.and(t.types);
        return (temp.equals(types));
    }

    /** Returns an array with the same type as <code>a</code>, containing the elements
     *  which are assignment-compatible with the base type of <code>a</code>.
     */
    public Object[] toArray(Object[] a) {
        return toList().toArray(a);
    }
}
