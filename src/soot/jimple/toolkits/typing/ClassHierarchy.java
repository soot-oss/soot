/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Etienne Gagnon
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






package soot.jimple.toolkits.typing;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/**
 * This class encapsulates the typing class hierarchy, as well as non-reference types.
 *
 * <P> This class is primarily used by the TypeResolver class, to optimize its computation.
 **/
class ClassHierarchy
{
    /** Hashtable: Scene -> ClassHierarchy **/
    private static Hashtable classHierarchyHashtable = new Hashtable();

    /** The class manager **/
    Scene scene;

    /** All type node instances **/
    private Vector typeNodeInstances = new Vector();

    /** Hashtable: Type -> TypeNode **/
    private Hashtable typeNodeHashtable = new Hashtable();

    /** Used to transform boolean, byte, short and char to int **/
    private ToInt transform = new ToInt();

    /** Used to create TypeNode instances **/
    private ConstructorChooser make = new ConstructorChooser();

    private ClassHierarchy(Scene scene)
    {
        if(scene == null)
        {
            throw new NullPointerException();
        }

        this.scene = scene;
        classHierarchyHashtable.put(scene, this);
    }

    /** Get the class hierarchy for the given class manager. **/
    public static ClassHierarchy getClassHierarchy(Scene scene)
    {
        ClassHierarchy classHierarchy =
            (ClassHierarchy) classHierarchyHashtable.get(scene);

        if(classHierarchy == null)
        {
            classHierarchy = new ClassHierarchy(scene);
        }

        return classHierarchy;
    }

    /** Get the type node for the given type. **/
    public TypeNode getTypeNode(Type type)
    {
        type = transform.toInt(type);
        TypeNode typeNode = (TypeNode) typeNodeHashtable.get(type);

        if(typeNode == null)
        {
            typeNode = make.typeNode(type);
        }

        return typeNode;
    }

    /** Returns a string representation of this object **/
    public String toString ()
    {
        StringBuffer s = new StringBuffer();
        boolean colon = false;

        s.append("ClassHierarchy:{");
        for(Enumeration e = typeNodeInstances.elements(); e.hasMoreElements();)
        {
            if(colon)
            {
                s.append(",");
            }
            else
            {
                colon = true;
            }

            s.append(e.nextElement());
        }
        s.append("}");

        return s.toString();
    }

    /**
     * Transforms boolean, byte, short and char into int.
     **/
    private class ToInt extends TypeSwitch
    {
        private Type result;
        private Type intType = IntType.v();

        ToInt()
        {
        }

        /** Transform boolean, byte, short and char into int. **/
        Type toInt(Type type)
        {
            type.apply(this);
            return result;
        }

        public void caseBooleanType(BooleanType type)
        {
            result = intType;
        }

        public void caseByteType(ByteType type)
        {
            result = intType;
        }

        public void caseShortType(ShortType type)
        {
            result = intType;
        }

        public void caseCharType(CharType type)
        {
            result = intType;
        }

        public void defaultCase(Type type)
        {
            result = type;
        }
    }

    /**
     * Creates new TypeNode instances usign the appropriate constructor.
     **/
    private class ConstructorChooser extends TypeSwitch
    {
        private TypeNode result;

        ConstructorChooser()
        {
        }

        /** Create a new TypeNode instance for the type parameter. **/
        TypeNode typeNode(Type type)
        {
            type.apply(this);
            return result;
        }

        public void caseRefType(RefType type)
        {
            result = new TypeNode(type);
        }

        public void caseArrayType(ArrayType type)
        {
            result = new TypeNode(type);
        }

        public void defaultCase(Type type)
        {
            result = new TypeNode(type);
        }
    }

    /**
     * Each instance of this class represents one type in the class hierarchy (or basic types).
     **/
    class TypeNode
    {
        private int id;
        private Type type;

        private BitSet parents = new BitSet();
        private BitSet ancestors = new BitSet();
        private BitSet descendants = new BitSet();

        TypeNode(Type type)
        {
            this.type = type;
            id = typeNodeInstances.size();
            typeNodeInstances.addElement(this);
            typeNodeHashtable.put(type, this);
        }

        TypeNode(RefType type)
        {
            this((Type) type);

            SootClass sClass = scene.getSootClass(type.className);
            if(sClass.hasSuperclass())
            {
                parents.set(getTypeNode(RefType.v(sClass.getSuperclass().getName())).id);
            }
            for(Iterator i = sClass.getInterfaces().iterator(); i.hasNext(); )
            {
                parents.set(getTypeNode(RefType.v(((SootClass) i.next()).getName())).id);
            }

            int size = parents.size();
            for(int i = 0; i < size; i++)
            {
                if(parents.get(i))
                {
                    TypeNode parent = (TypeNode) typeNodeInstances.elementAt(i);
                    ancestors.or(parent.ancestors);
                }
            }
            ancestors.or(parents);

            TypeNode nullNode = getTypeNode(NullType.v());
            descendants.set(nullNode.id);
            nullNode.ancestors.set(id);

            for(int i = 0; i < size; i++)
            {
                if(parents.get(i))
                {
                    TypeNode parent = (TypeNode) typeNodeInstances.elementAt(i);
                    parent.fixDescendants(id);
                }
            }
        }

        TypeNode(ArrayType type)
        {
            this((Type) type);

            if(type.baseType instanceof RefType)
            {
                RefType baseType = (RefType) type.baseType;

                SootClass sClass = scene.getSootClass(baseType.className);
                if(sClass.hasSuperclass())
                {
                    parents.set(getTypeNode(ArrayType.v(RefType.v(
                        sClass.getSuperclass().getName()),
                        type.numDimensions)).id);
                }
                for(Iterator i = sClass.getInterfaces().iterator(); i.hasNext(); )
                {
                    parents.set(getTypeNode(ArrayType.v(RefType.v(
                        ((SootClass) i.next()).getName()),
                        type.numDimensions)).id);
                }
                parents.set(getTypeNode(RefType.v("java.lang.Object")).id);
                parents.set(getTypeNode(RefType.v("java.lang.Cloneable")).id);

                int size = parents.size();
                for(int i = 0; i < size; i++)
                {
                    if(parents.get(i))
                    {
                        TypeNode parent = (TypeNode) typeNodeInstances.elementAt(i);
                        ancestors.or(parent.ancestors);
                    }
                }
                ancestors.or(parents);

                TypeNode nullNode = getTypeNode(NullType.v());
                descendants.set(nullNode.id);
                nullNode.ancestors.set(id);

                for(int i = 0; i < size; i++)
                {
                    if(parents.get(i))
                    {
                        TypeNode parent = (TypeNode) typeNodeInstances.elementAt(i);
                        parent.fixDescendants(id);
                    }
                }
            }
            else
            {
                parents.set(getTypeNode(RefType.v("java.lang.Object")).id);
                parents.set(getTypeNode(RefType.v("java.lang.Cloneable")).id);

                int size = parents.size();
                for(int i = 0; i < size; i++)
                {
                    if(parents.get(i))
                    {
                        TypeNode parent = (TypeNode) typeNodeInstances.elementAt(i);
                        ancestors.or(parent.ancestors);
                    }
                }
                ancestors.or(parents);

                TypeNode nullNode = getTypeNode(NullType.v());
                descendants.set(nullNode.id);
                nullNode.ancestors.set(id);

                for(int i = 0; i < size; i++)
                {
                    if(parents.get(i))
                    {
                        TypeNode parent = (TypeNode) typeNodeInstances.elementAt(i);
                        parent.fixDescendants(id);
                    }
                }
            }
        }

        /** Adds the given node to the list of descendants of this node and its ancestors. **/
        private void fixDescendants(int id)
        {
            if(descendants.get(id))
            {
                return;
            }

            int size = parents.size();
            for(int i = 0; i < size; i++)
            {
                if(parents.get(i))
                {
                    TypeNode parent = (TypeNode) typeNodeInstances.elementAt(i);
                    parent.fixDescendants(id);
                }
            }

            descendants.set(id);
        }

        /** Returns the unique id of this type node. **/
        public int getId()
        {
            return id;
        }

        /** Returns the type represented by this type node. **/
        public Type getType()
        {
            return type;
        }

        /** Returns the list of parents of this type node. **/
/*        public List getParents()
        {
            LinkedList result = new LinkedList();
            int[] elements  = parents.elements();

            for(int i = 0; i < elements.length; i++)
            {
                result.add(typeNodeInstances.elementAt(elements[i]));
            }

            return result;
        } */

       public boolean hasAncestor(TypeNode typeNode)
       {
           return ancestors.get(typeNode.id);
       }

       public boolean hasDescendant(TypeNode typeNode)
       {
           return descendants.get(typeNode.id);
       }

        /** Returns the ids of the ancestors of this type node. **/
/*        public BitSet getAncestors()
        {
            return (BitSet) ancestors.clone();
        } */

        /** Returns the ids of the descendants of this type node. **/
/*        public BitSet getDescendants()
        {
            return (BitSet) descendants.clone();
        } */

        /** Returns a string representation of this object **/
/*        public String toString ()
        {
            return type.toString();
        } */
    }
}

