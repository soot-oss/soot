/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1998, 1999 Etienne Gagnon (gagnon@sable.mcgill.ca). *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Raja Vallee-Rai (rvallerai@sable.mcgill.ca) are  *
 * Copyright (C) 1998 Raja Vallee-Rai.  All rights reserved.         *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:
 - Modified on January 20, 1999 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Fixed a fixed a basic type array typing problem.

 - Modified on January 15, 1999 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Fixed typing bug in null assignment to array variables.

 - Modified on November 13, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Added type information for @caughtexception

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on October 14, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Implemented fast typing algorithm for arrays.

 - Modified on October 1, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Fixed the inference of <<, >> and >>>.

 - Modified on 2-Sep-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   getBaseType() on NewArrayExpr does not always yield a BaseType.

 - Modified on 2-Sep-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Applied Etienne's patch.

 - Modified on July 29, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Initial version.

*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;

/**
 * This class resolves the type of local variables.
 **/
class TypeResolver
{
    static boolean firstTime = true;

    private static final boolean DEBUG = false;

    /** Reference to the class hierarchy **/
    ClassHierarchy classHierarchy;

    /** Reference to the current method **/
    SootMethod currentMethod;

    static String lastClass;
    /** All type variable instances **/
    Vector typeVariableInstances = new Vector();

    /** Hashtable: [TypeNode or Local] -> TypeVariable **/
    private Hashtable typeVariableHashtable = new Hashtable();

    /** Hashtable: TypeVariable -> String **/
    private Hashtable typeVariableStringHashtable = new Hashtable();

    /** Used to collect type constraints **/
    private ConstraintCollector constraintCollector = new ConstraintCollector();

    /** Type variables left to resolve **/
    IntSet unresolvedTypeVariables = new IntSet();

    private JimpleBody stmtBody;

    private void debug_locals()
    {
      for(Iterator i = stmtBody.getLocals().iterator(); i.hasNext(); )
      {
        Local local = (Local) i.next();

        System.out.print(local + ": ");

        TypeVariable var = getTypeVariable(local).ecr();
        if(var == null)
        {
          System.out.println("null");
        }
        else
        {
          System.out.println(var.getEcrId());
        }
      }
    }

    private void debug()
    {
      System.out.println("*** DEBUG ***");
      debug_locals();
      int size = typeVariableInstances.size();
      for(int i = 0; i < size; i++)
      {
        TypeVariable a = (TypeVariable) typeVariableInstances.elementAt(i);

        if(a == a.ecr())
        {
          System.out.print(i + ":");

          ClassHierarchy.TypeNode node = a.getEcrTypeNode();
          if(node != null)
          {
            System.out.print(" " + node.getType());
          }
          System.out.println();

          TypeVariable[] parents = a.getEcrParents();
          if(parents.length != 0)
          {
            System.out.print("  Parents:");

            for(int j = 0; j < parents.length; j++)
            {
              System.out.print(" " + parents[j].getEcrId());
            }

            System.out.println();
          }

          TypeVariable[] children = a.getEcrChildren();
          if(children.length != 0)
          {
            System.out.print("  Children:");

            for(int j = 0; j < children.length; j++)
            {
              System.out.print(" " + children[j].getEcrId());
            }

            System.out.println();
          }

          if(a.isArrayOf != null)
          {
            System.out.println("  Array of: " + a.isArrayOf.getEcrId());
          }

          System.out.println("  Array depth: " + a.arrayDepth);
        }
      }
    }

    /** This constructor triggers the type resolution of
        local variables of the given statement list body. **/
    private TypeResolver(JimpleBody stmtBody)
    {
        try 
        {
            this.stmtBody = stmtBody;

            currentMethod = stmtBody.getMethod();

            classHierarchy = ClassHierarchy.getClassHierarchy(
                currentMethod.getDeclaringClass().getManager());

            // Collect constraints
            for(Iterator i = stmtBody.getStmtList().iterator(); i.hasNext();)
            {
                Stmt stmt = (Stmt) i.next();
                stmt.apply(constraintCollector);
            }

            // Let's make sure a type variable is created
            // for each of these.
            getTypeVariable(RefType.v("java.lang.Object"));
            getTypeVariable(RefType.v("java.lang.Cloneable"));
            getTypeVariable(RefType.v("java.io.Serializable"));
            getTypeVariable(NullType.v());
            
            // Compute array depths
            computeArrayDepths();
            
            // Propagate array contraints to depth 0
            propagateArrayConstraints();
            
            // From now on, we ignore depth >= 1
            
            // Add relations between hard nodes
            addRelationsBetweenHardNodes();
            
            // Merge basic types
            mergeAll(getTypeVariable(IntType.v()));
            mergeAll(getTypeVariable(LongType.v()));
            mergeAll(getTypeVariable(FloatType.v()));
            mergeAll(getTypeVariable(DoubleType.v()));
            mergeAll(getTypeVariable(StmtAddressType.v()));
            
            // Merge strongly connected components
            mergeStronglyConnectedComponents();
            
            // Remove transitive constraints.
            removeTransitiveRelations();
            
            // Merge single constraints
            resolveSingleRelations();
            
            // Do the exponential exhaustive search
            resolveComplexRelations();            
            
            // Propagate basic array types
            propagateBasicArrayTypes();
        } 
        catch(Exception e)
        {
            if(!firstTime)
            {
                System.out.println();
                System.out.println("-*- Type Error in method " +
                    currentMethod.getName() + " of class " +
                    currentMethod.getDeclaringClass().getName() +
                    " -*-");
                System.out.println();
            }
        }


        for(Iterator i = stmtBody.getLocals().iterator(); i.hasNext(); )
        {
            Local local = (Local) i.next();

            TypeVariable var = getTypeVariable(local).ecr();
            if(var == null)
            {
                local.setType(UnknownType.v());
            }
            else if (var.arrayDepth == 0)
            {
                if(var.getEcrTypeNode() == null)
                {
                    local.setType(UnknownType.v());
                }
                else
                {
                    local.setType(var.getEcrTypeNode().getType());
                }
            }
            else
            {
                if/*(*/(var.getEcrTypeNode() != null) /*&&
                    (!(var.base.getEcrTypeNode().getType() instanceof RefType)))*/
                {
                    local.setType(var.getEcrTypeNode().getType());
                }
                else if(var.base.getEcrTypeNode() == null)
                {
                    local.setType(UnknownType.v());
                }
                else if(var.base.getEcrTypeNode().getType() instanceof ErroneousType)
                {
                    local.setType(ErroneousType.v());
                }
                else if(var.base.getEcrTypeNode().getType() instanceof NullType)
                {
                    local.setType(NullType.v());
                }
                else
                {
                    local.setType(ArrayType.v(
                        (BaseType) var.base.getEcrTypeNode().getType(),
                        var.arrayDepth));
                }
            }
        }
    }

    private void removeRelationsBetweenNonEcrs()
    {
        for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
        {
            TypeVariable var = (TypeVariable) e.nextElement();

            if((var != var.ecr()) ||
                (var.arrayDepth != 0))
            {
                var.parents = new IntSet();
                var.children = new IntSet();
            }
        }
    }

    private void addRelationsBetweenHardNodes()
    {
        LinkedList workList = new LinkedList();

        for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
        {
            TypeVariable var = (TypeVariable) e.nextElement();

            if((var == var.ecr()) &&
                (var.getEcrTypeNode() != null) &&
                (!(var.getEcrTypeNode().getType() instanceof ArrayType)))
            {
                workList.add(var);
            }
        }

        while(workList.size() > 0)
        {
            TypeVariable var = (TypeVariable) workList.removeFirst();

            TypeVariable[] elements = new TypeVariable[workList.size()];
            workList.toArray(elements);

            for(int i = 0; i < elements.length; i++)
            {
                TypeVariable other = elements[i];

                if(var.getEcrTypeNode().hasAncestor(other.getEcrTypeNode()))
                {
                    var.ecrAddParent(other);
                }
                else if(var.getEcrTypeNode().hasDescendant(other.getEcrTypeNode()))
                {
                    var.ecrAddChild(other);
                }
            }

        }
    }

    /** Assign types to local variables. **/
    public static void assignTypesToLocals(JimpleBody stmtBody)
    {
        new TypeResolver(stmtBody);
    }

    /** Get type variable for the given local. **/
    TypeVariable getTypeVariable(Local local)
    {
        TypeVariable result = (TypeVariable) typeVariableHashtable.get(local);

        if(result == null)
        {
            result = new TypeVariable(local);
//            System.out.println(local + ": " + result.getEcrId());
        }

        return result;
    }

    /** Get type variable for the given type node. **/
    TypeVariable getTypeVariable(ClassHierarchy.TypeNode typeNode)
    {
        TypeVariable result = (TypeVariable) typeVariableHashtable.get(typeNode);

        if(result == null)
        {
            result = new TypeVariable(typeNode);
        }

        return result;
    }

    /** Get type variable for the given  SootClass. **/
    TypeVariable getTypeVariable(SootClass sClass)
    {
        return getTypeVariable(classHierarchy.getTypeNode(RefType.v(sClass.getName())));
    }

    /** Get type variable for the given type. **/
    TypeVariable getTypeVariable(Type type)
    {
        return getTypeVariable(classHierarchy.getTypeNode(type));
    }

    /** Merge together variables involved in a dependence cycle.
        Use the set of all type variables **/
    private void mergeStronglyConnectedComponents()
    {
        new SCC(typeVariableInstances);
    }

    /** Merge the given type variable with all its ancestors and descentants. **/
    private boolean mergeAll(TypeVariable var)
    {
        TypeVariable[] parents = var.getEcrParents();
        TypeVariable[] children = var.getEcrChildren();
        boolean changed = true;
        boolean modif = false;

        while(changed)
        {
            changed = false;

            for(int i = 0; i < parents.length; i++)
            {
                if(parents[i].ecr().arrayDepth == var.ecr().arrayDepth)
                {
                    modif = true;
                    changed = true;
                    var.ecrUnion(parents[i]);
                }
            }

            for(int i = 0; i < children.length; i++)
            {
                if(children[i].ecr().arrayDepth == var.ecr().arrayDepth)
                {
                    modif = true;
                    changed = true;
                    var.ecrUnion(children[i]);
                }
            }

            parents = var.getEcrParents();
            children = var.getEcrChildren();
        }

        return modif;
    }

    private void propagateBasicArrayTypes()
    {
        boolean changed;

        do
        {
            changed = false;

            for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
            {
                TypeVariable var = (TypeVariable) e.nextElement();

                if((var == var.ecr()) &&
                    (var.getEcrTypeNode() != null) &&
                    (var.getEcrTypeNode().getType() instanceof ArrayType))
                {
                    ArrayType type = (ArrayType) var.getEcrTypeNode().getType();

                    if(!(type.baseType instanceof RefType))
                    {
                        ClassHierarchy.TypeNode node = 
                            classHierarchy.getTypeNode(
                            ArrayType.v(type.baseType, type.numDimensions - 1));
                        
                        if((var.isArrayOf != null) &&
                            (var.isArrayOf.arrayDepth != 0) &&
                            (var.isArrayOf.getEcrTypeNode() == null))
                        {
                            changed = true;
                            var.isArrayOf.ecr().typeNode = node;
                        }
                    
                        TypeVariable[] parents = var.getEcrParents();
                        TypeVariable[] children = var.getEcrChildren();
                    
                        for(int i = 0; i < parents.length; i++)
                        {
                            if((parents[i].arrayDepth == var.arrayDepth) &&
                                (parents[i].getEcrTypeNode() == null))
                            {
                                changed = true;
                                parents[i].typeNode = var.getEcrTypeNode();
                            }
                        }

                        for(int i = 0; i < children.length; i++)
                        {
                            if((children[i].arrayDepth == var.arrayDepth) &&
                                (children[i].getEcrTypeNode() == null))
                            {
                                changed = true;
                                children[i].typeNode = var.getEcrTypeNode();
                            }
                        }
                    }
                }
            }
        }
        while(changed);
    }

    private void propagateArrayConstraints()
    {
        LinkedList workList = new LinkedList();
        
        // Initialize work list
        for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
        {
            TypeVariable var = (TypeVariable) e.nextElement();

            if(var.arrayDepth == 0) // already base
            {
                var.base = var;
            }
            else if(var.isArrayOf == null) // create new base
            {
                var.base = new TypeVariable();
                var.base.base = var.base;
            }
            else if(var.arrayDepth != -1) // add to work list
            {
                workList.add(var);
            }
            else // infinite depth => Null
            {
                var.ecrUnion(getTypeVariable(NullType.v()));
            }
        }
        
        // Propagate "base" (depth 0) node
        while(workList.size() != 0)
        {
            TypeVariable var = (TypeVariable) workList.removeFirst();
            
            if(var.isArrayOf.base != null)
            {
                var.base = var.isArrayOf.base;
            }
            else
            {
                workList.add(var);
            }
        }

        // Propagate constraints
        for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
        {
            TypeVariable var = (TypeVariable) e.nextElement();

            if(var == var.ecr())
            {
                int[] elements = var.parents.elements();
                for(int i = 0; i < elements.length; i++)
                {
                    TypeVariable parent =
                        (TypeVariable) typeVariableInstances.elementAt(elements[i]);

                    if(var.arrayDepth == -1)
                    {
                        continue;
                    }

                    if(parent.arrayDepth == var.arrayDepth)
                    {
                        var.base.ecrAddParent(parent.base);
                    }
                    else
                    {
                        parent.base.ecrAddChild(getTypeVariable(
                            RefType.v("java.lang.Cloneable")));
                        parent.base.ecrAddChild(getTypeVariable(
                            RefType.v("java.io.Serializable")));
                    }
                }

                if(var.arrayDepth != 0)
                {
                    unresolvedTypeVariables.clear(var.id);
                }
            }
        }
    }

    private void computeArrayDepths()
    {
        // Initialize array depths
        for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
        {
            TypeVariable var = (TypeVariable) e.nextElement();

            if(var.typeNode != null)
            {
                if(var.typeNode.getType() instanceof ArrayType)
                {
                    ArrayType type = (ArrayType) var.typeNode.getType();
                    
                    var.arrayDepth = type.numDimensions;
                }
                else
                {
                    var.arrayDepth = 0;
                }
            }
            else
            {
                var.arrayDepth = -1;  // -1 stands for "infinite"
            }
        }

        // Propagate minimum-value depth up, left, and right (until fixed point)
        boolean changed;
        do
        {
            changed = false;
            
            for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
            {
                TypeVariable var = (TypeVariable) e.nextElement();
                
                // Left and right propagation
                if((var.typeNode == null) && (var.isArrayOf != null))
                {
                    if(var.arrayDepth > 0)
                    {
                        if((var.isArrayOf.arrayDepth == -1) ||
                            (var.isArrayOf.arrayDepth >= var.arrayDepth))
                        {
                            changed = true;
                            var.isArrayOf.arrayDepth = var.arrayDepth - 1;
                        }
                        else if(var.isArrayOf.arrayDepth + 1 < var.arrayDepth)
                        {
                            changed = true;
                            var.arrayDepth = var.isArrayOf.arrayDepth + 1;
                        }
                    }
                    else if((var.arrayDepth == -1) && 
                        (var.isArrayOf.arrayDepth != -1))
                    {
                        changed = true;
                        var.arrayDepth = var.isArrayOf.arrayDepth + 1;
                    }
                }
                
                if(var.arrayDepth == -1)
                {
                    continue;
                }
                
                // Up propagation
                int[] elements = var.parents.elements();
                for(int i = 0; i < elements.length; i++)
                {
                    TypeVariable parent =
                        (TypeVariable) typeVariableInstances.elementAt(elements[i]);
                        
                    if((parent.typeNode == null) && ((parent.arrayDepth == -1) || 
                        (parent.arrayDepth > var.arrayDepth)))
                    {
                        changed = true;
                        parent.arrayDepth = var.arrayDepth;
                    }
                }
            }
        }
        while(changed);
    }

    /** Resolve single relations.
        <P> <UL> <LI> No Parent -> "java.lang.Object"
        <LI> No Child -> "*null*"
        <LI> One parent -> merge with parent
        <LI> One child -> merge with child
        <UL> **/
    private void resolveSingleRelations()
    {
        boolean changed;

        // Merge down 
        do
        {
            changed = false;

            int[] elements = unresolvedTypeVariables.elements();
            for(int i = 0; i < elements.length; i++)
            {
                TypeVariable var = 
                    (TypeVariable) typeVariableInstances.elementAt(elements[i]);

                if((var == var.ecr()) && (var.getEcrTypeNode() == null))
                {
                    TypeVariable[] children = var.getEcrChildren();

                    if(children.length == 1)
                    {
                        changed = true;
                        var.ecrUnion(children[0]);
                    }
                    else if(children.length == 0)
                    {
                        changed = true;
                        var.ecrUnion(getTypeVariable(NullType.v()));
                    }
                }
            }
        }
        while(changed);

        // Merge soft nodes up 
        do
        {
            changed = false;

            int[] elements = unresolvedTypeVariables.elements();
            for(int i = 0; i < elements.length; i++)
            {
                TypeVariable var = 
                    (TypeVariable) typeVariableInstances.elementAt(elements[i]);

                if((var == var.ecr()) && (var.getEcrTypeNode() == null))
                {
                    TypeVariable[] parents = var.getEcrParents();

                    if((parents.length == 1) && 
                        (parents[0].getEcrTypeNode() == null))
                    {
                        changed = true;
                        var.ecrUnion(parents[0]);
                    }
                }
            }
        }
        while(changed);

        // Merge nodes up 
        do
        {
            changed = false;

            int[] elements = unresolvedTypeVariables.elements();
            for(int i = 0; i < elements.length; i++)
            {
                TypeVariable var = 
                    (TypeVariable) typeVariableInstances.elementAt(elements[i]);

                if((var == var.ecr()) && (var.getEcrTypeNode() == null))
                {
                    TypeVariable[] parents = var.getEcrParents();
                    TypeVariable[] children = var.getEcrChildren();

                    if((parents.length < 2) &&
                       ((parents.length != 1) || isClass(parents[0])))
                    {
                        TypeVariable lca = null;
                        for(int j = 0; j < children.length; j++)
                        {
                            if(isClass(children[j]))
                            {
                                if(lca == null)
                                {
                                    lca = children[j];
                                }
                                else
                                {
                                    lca = getLCA(lca, children[j]);
                                }
                            }
                            else
                            {
                                lca = null;
                                break;
                            }
                        }
                        
                        if(lca != null)
                        {
                            changed = true;
                            var.ecrUnion(lca);
                        }
                        else if(parents.length == 1)
                        {
                            changed = true;
                            var.ecrUnion(parents[0]);
                        }
                        else
                        {
                            changed = true;
                            var.ecrUnion(
                                getTypeVariable(RefType.v("java.lang.Object")));
                        }
                    }
                }
            }
        }
        while(changed);
    }

    private boolean isClass(TypeVariable var)
    {
        // Get the assigned type node
        ClassHierarchy.TypeNode node = var.getEcrTypeNode();
        if(node == null)
        {
            return false;
        }
        
        // get the type
        Type type = node.getType();
        if(!(type instanceof RefType))
        {
            return false;
        }
        
        SootClass sClass = classHierarchy.classManager.getClass(
            ((RefType) type).className);
            
        if(Modifier.isInterface(sClass.getModifiers()))
        {
            return false;
        }
        
        return true;
    }
    
    private TypeVariable superClass(TypeVariable var)
    {
        // Get the assigned type node
        ClassHierarchy.TypeNode node = var.getEcrTypeNode();
        Type type = node.getType();
        SootClass sClass = classHierarchy.classManager.getClass(
            ((RefType) type).className);
        if(sClass.hasSuperClass())
        {
            return getTypeVariable(sClass.getSuperClass()).ecr();
        }
        
        return null;    
    }
 
    private TypeVariable getLCA(TypeVariable var1, TypeVariable var2)
    {
        var1 = var1.ecr();
        var2 = var2.ecr();

        if(!(isClass(var1) && isClass(var2)))
        {
            throw new RuntimeException("BUG: LCA expects 2 classes.");
        }
        
        Stack s1 = new Stack();
        Stack s2 = new Stack();
        
        s1.push(var1.ecr());
        while((var1 = superClass(var1)) != null)
        {
            s1.push(var1.ecr());
        }
        
        s2.push(var2.ecr());
        while((var2 = superClass(var2)) != null)
        {
            s2.push(var2.ecr());
        }
        
        TypeVariable result = null;
        while((!s1.empty()) && (!s2.empty()) &&
            (s1.peek() == s2.peek()))
        {
            result = (TypeVariable) s1.pop();
            s2.pop();
        }
        
        if(result == null)
        {
            throw new RuntimeException("BUG: Class Hierarchy error!");
        }

        return result;
    }
    
    /** Remove a parent if it is an ancestor of another parent. **/
    private void removeTransitiveRelations()
    {
        LinkedList workList = new LinkedList();
        workList.addLast(getTypeVariable(RefType.v("java.lang.Object")));
        IntSet processed = new IntSet();

        while(workList.size() != 0)
        {
            TypeVariable var = (TypeVariable) workList.removeFirst();

            var.removeEcrIndirectRelations();
            processed.set(var.getEcrId());

            TypeVariable[] children = var.getEcrChildren();
            for(int i = 0; i < children.length; i++)
            {
                IntSet temp = children[i].getEcrParentIds();
                temp.and(processed);
                
                // Don't process a child before all its parents are processed
                if(temp.equals(children[i].getEcrParentIds()))
                {
                    workList.addLast(children[i]);
                }
            }
        }
    }

    /** Do the exponential search of a solution. This is an NP-Complete problem. **/
    private boolean resolveComplexRelations()
    {
        if(unresolvedTypeVariables.size() == 0)
        {
            return true;
        }

        final TypeVariable[] ecrInstances;
        {
            Vector ecrs = new Vector();
            TypeVariable nullVar = null;
            for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
            {
                TypeVariable var = (TypeVariable) e.nextElement();
                if((var.ecr() == var) && (var.getEcrTypeNode() != null))
                {
                    if(var.getEcrTypeNode().getType().equals(NullType.v()))
                    {
                        nullVar = var;
                    }
                    else
                    {
                        ecrs.addElement(var);
                    }
                }
            }

            // Make sure that *null* is tried last.
            if(nullVar != null)
            {
                ecrs.addElement(nullVar);
            }

            ecrInstances = new TypeVariable[ecrs.size()];
            int index = 0;
            for(Enumeration e = ecrs.elements(); e.hasMoreElements();)
            {
                TypeVariable var = (TypeVariable) e.nextElement();
                ecrInstances[index++] = var;
            }
        }

        final int[] elements;
        {
            IntSet processed = new IntSet();
            LinkedList processedList = new LinkedList();

            while(!processed.equals(unresolvedTypeVariables))
            {
                IntSet unprocessed = (IntSet) unresolvedTypeVariables.clone();
                unprocessed.xor(processed);
                int[] unprocessedElements = unprocessed.elements();

                for(int i = 0; i < unprocessedElements.length; i++)
                {
                    TypeVariable var =
                        (TypeVariable) typeVariableInstances.elementAt(unprocessedElements[i]);

                    if((!var.isEcrArray()) ||
                        processed.get(var.getEcrIsArrayOf().getEcrId()) ||
                        (!unresolvedTypeVariables.get(var.getEcrIsArrayOf().getEcrId())))
                    {
                        processed.set(var.getEcrId());
                        processedList.add(var);
                    }
                }
            }

            elements = new int[processedList.size()];
            int index = 0;
            for(Iterator i = processedList.iterator(); i.hasNext();)
            {
                elements[index++] = ((TypeVariable) i.next()).getEcrId();
            }
        }

        class RecursiveFunction
        {
            int index = 0;

            public boolean resolve()
            {
                if(index == elements.length)
                {
                    return true;
                }

                TypeVariable var =
                    (TypeVariable) typeVariableInstances.elementAt(elements[index++]);

                for(int i = 0; i < ecrInstances.length; i++)
                {
                    if(var.setEcrTypeNode(ecrInstances[i].getEcrTypeNode()))
                    {
                        if(resolve())
                        {
                            return true;
                        }
                        var.unsetEcrTypeNode();
                    }
                }
                index--;

                return false;
            }
        }

        return new RecursiveFunction().resolve();
    }

    /** Represents a type variable. **/
    class TypeVariable
    {
        /** Unique id **/
        private int id;

        IntSet parents = new IntSet();
        IntSet children = new IntSet();

        private ClassHierarchy.TypeNode typeNode;

        private boolean cannotBeInt;
        private boolean cannotBeLong;
        private boolean cannotBeFloat;
        private boolean cannotBeDouble;
        private boolean cannotBeAddress;
        private boolean cannotBeRef;

        TypeVariable isArrayOf;
        int arrayDepth;
        TypeVariable base;
        int count;

        private TypeVariable rep = this;
        private int rank = 0;

        private BitSet ancestors = new BitSet();

        TypeVariable()
        {
            id = typeVariableInstances.size();
            typeVariableInstances.addElement(this);
            unresolvedTypeVariables.set(id);
        }

        TypeVariable(Local local)
        {
            this();
            typeVariableHashtable.put(local, this);
            typeVariableStringHashtable.put(this, local.toString());
        }

        TypeVariable(ClassHierarchy.TypeNode typeNode)
        {
            this();
            typeVariableHashtable.put(typeNode, this);
            typeVariableStringHashtable.put(this, typeNode.toString());
            this.typeNode = typeNode;
            unresolvedTypeVariables.clear(id);

            if(typeNode.getType() instanceof ArrayType)
            {
                ArrayType type = (ArrayType) typeNode.getType();

                if(type.numDimensions > 1)
                {
                    isArrayOf = getTypeVariable(
                        ArrayType.v(type.baseType, type.numDimensions - 1));
                }
                else
                {
                    isArrayOf = getTypeVariable(type.baseType);
                }
            }
        }

        TypeVariable ecr()
        {
            if(rep != this)
            {
                rep = rep.ecr();
            }

            return rep;
        }

        TypeVariable ecrUnion(TypeVariable var)
        {
            TypeVariable x = ecr();
            TypeVariable y = var.ecr();

            if(x == y)
            {
                return x;
            }

            if(x.rank > y.rank)
            {
                x.merge(y);

                y.rep = x;
                return x;
            }

            y.merge(x);

            x.rep = y;

            if(y.rank == x.rank)
            {
                y.rank++;
            }

            return y;
        }

        private void merge(TypeVariable var)
        {
            // Merge types
            if(typeNode == null)
            {
                typeNode = var.typeNode;
            }
            else if(var.typeNode != null)
            {
                var.typeNode = typeNode = classHierarchy.getTypeNode(ErroneousType.v());
                error("Type Error(1): Attempt to merge incompatible types.");
            }

            unresolvedTypeVariables.clear(var.id);

            if(typeNode != null)
            {
                unresolvedTypeVariables.clear(id);
            }

            // Merge properties
            cannotBeInt |= var.cannotBeInt;
            cannotBeLong |= var.cannotBeLong;
            cannotBeFloat |= var.cannotBeFloat;
            cannotBeDouble |= var.cannotBeDouble;
            cannotBeAddress |= var.cannotBeAddress;
            cannotBeRef |= var.cannotBeRef;

            // Merge parents
            parents.or(var.parents);
            int[] elements = var.parents.elements();
            for(int i = 0; i < elements.length; i++)
            {
                TypeVariable parent =
                    (TypeVariable) typeVariableInstances.elementAt(elements[i]);
                parent.children.clear(var.id);
                parent.children.set(id);
            }

            // Merge children
            children.or(var.children);
            elements = var.children.elements();
            for(int i = 0; i < elements.length; i++)
            {
                TypeVariable child =
                    (TypeVariable) typeVariableInstances.elementAt(elements[i]);
                child.parents.clear(var.id);
                child.parents.set(id);
            }
            parents.clear(id);
            children.clear(id);
            parents.clear(var.id);
            children.clear(var.id);

            // Verify "concrete" relations.
            if(typeNode != null)
            {
                if(cannotBeInt && (typeNode.getType() instanceof IntType))
                {
                    typeNode = classHierarchy.getTypeNode(ErroneousType.v());
                    error("Type Error(2): Should not be an IntType.");
                }

                if(cannotBeLong && (typeNode.getType() instanceof LongType))
                {
                    typeNode = classHierarchy.getTypeNode(ErroneousType.v());
                    error("Type Error(3): Should not be a LongType.");
                }

                if(cannotBeFloat && (typeNode.getType() instanceof FloatType))
                {
                    typeNode = classHierarchy.getTypeNode(ErroneousType.v());
                    error("Type Error(4): Should not be a FloatType.");
                }

                if(cannotBeDouble && (typeNode.getType() instanceof DoubleType))
                {
                    typeNode = classHierarchy.getTypeNode(ErroneousType.v());
                    error("Type Error(5): Should not be a DoubleType.");
                }

                if(cannotBeAddress && (typeNode.getType() instanceof StmtAddressType))
                {
                    typeNode = classHierarchy.getTypeNode(ErroneousType.v());
                    error("Type Error(6): Should not be a StmtAddressType.");
                }

                if(cannotBeRef &&
                    ((typeNode.getType() instanceof RefType) ||
                    (typeNode.getType() instanceof ArrayType)))
                {
                    typeNode = classHierarchy.getTypeNode(ErroneousType.v());
                    error("Type Error(7): Should not be a RefType nor an ArrayType.");
                }

                elements = parents.elements();
                for(int i = 0; i < elements.length; i++)
                {
                    TypeVariable parent =
                        (TypeVariable) typeVariableInstances.elementAt(elements[i]);
                    if(parent.typeNode != null)
                    {
                        if(!typeNode.hasAncestor(parent.typeNode))
                        {
//                            System.out.println(typeNode.getType() + 
//                                " can't have " + parent.typeNode.getType() +
//                                " as parent");
                            parent.typeNode = typeNode =
                                classHierarchy.getTypeNode(ErroneousType.v());
                            error("Type Error(8): Parent type is not a valid ancestor.");
                        }
                    }
                }

                elements = children.elements();
                for(int i = 0; i < elements.length; i++)
                {
                    TypeVariable child =
                        (TypeVariable) typeVariableInstances.elementAt(elements[i]);
                    if(child.typeNode != null)
                    {
                        if(!typeNode.hasDescendant(child.typeNode))
                        {
//                            System.out.println(typeNode.getType() + 
//                                " can't have " + child.typeNode.getType() +
//                                " as child");
                            child.typeNode = typeNode =
                                classHierarchy.getTypeNode(ErroneousType.v());
                            error("Type Error(9): Child type is not a valid descendant.");
                        }
                    }
                }
            }

            var.parents = new IntSet();
            var.children = new IntSet();
            var.isArrayOf = null;
        }

        void removeEcrIndirectRelations()
        {
            TypeVariable x = ecr();
            x.ancestors = new BitSet();

            TypeVariable[] parents = getEcrParents();
            for(int i = 0; i < parents.length; i++)
            {
                x.ancestors.or(parents[i].ancestors);
            }
            for(int i = 0; i < parents.length; i++)
            {
                if(x.ancestors.get(parents[i].id))
                {
                    x.parents.clear(parents[i].id);
                    parents[i].children.clear(x.id);
                }
                else
                {
                    x.ancestors.set(parents[i].id);
                }
            }
        }

        void ecrAddParent(TypeVariable variable)
        {
            if(ecr() != variable.ecr())
            {
                if(DEBUG)
                {
                    System.out.println(typeVariableStringHashtable.get(variable.ecr()) + " < " +
                        typeVariableStringHashtable.get(ecr()));
                }

                ecr().parents.set(variable.ecr().id);
                variable.ecr().children.set(ecr().id);
            }
        }

        void ecrAddChild(TypeVariable variable)
        {
            if(ecr() != variable.ecr())
            {
                if(DEBUG)
                {
                    System.out.println(typeVariableStringHashtable.get(ecr()) + " < " +
                        typeVariableStringHashtable.get(variable.ecr()));
                }

                ecr().children.set(variable.ecr().id);
                variable.ecr().parents.set(ecr().id);
            }
        }

        void ecrCannotBeInt()
        {
            ecr().cannotBeInt = true;
        }

        void ecrCannotBeLong()
        {
            ecr().cannotBeLong = true;
        }

        void ecrCannotBeFloat()
        {
            ecr().cannotBeFloat = true;
        }

        void ecrCannotBeDouble()
        {
            ecr().cannotBeDouble = true;
        }

        void ecrCannotBeAddress()
        {
            ecr().cannotBeAddress = true;
        }

        void ecrCannotBeRef()
        {
            ecr().cannotBeRef = true;
        }

        int getEcrId()
        {
            return ecr().id;
        }

        boolean isEcrArray()
        {
            return ecr().isArrayOf != null;
        }

        int ecrArrayDepth()
        {
            return ecr().arrayDepth;
        }

        TypeVariable getEcrIsArrayOf()
        {
            TypeVariable x = ecr();

            if(x.isArrayOf == null)
            {
                x.isArrayOf = new TypeVariable();
            }

            return x.isArrayOf;
        }

        IntSet getEcrParentIds()
        {
            return (IntSet) ecr().parents.clone();
        }

        TypeVariable[] getEcrParents()
        {
            TypeVariable x = ecr();
            int[] elements = x.parents.elements();
            TypeVariable[] result = new TypeVariable[elements.length];

            for(int i = 0; i < elements.length; i++)
            {
                result[i] = (TypeVariable) typeVariableInstances.elementAt(elements[i]);
            }

            return result;
        }

        TypeVariable[] getEcrChildren()
        {
            TypeVariable x = ecr();
            int[] elements = x.children.elements();
            TypeVariable[] result = new TypeVariable[elements.length];

            for(int i = 0; i < elements.length; i++)
            {
                result[i] = (TypeVariable) typeVariableInstances.elementAt(elements[i]);
            }

            return result;
        }

        ClassHierarchy.TypeNode getEcrTypeNode()
        {
            return ecr().typeNode;
        }

        boolean setEcrTypeNode(ClassHierarchy.TypeNode typeNode)
        {
            TypeVariable[] elements = getEcrParents();
            for(int i = 0; i < elements.length; i++)
            {
                if(elements[i].typeNode != null)
                {
                    if((!typeNode.hasAncestor(elements[i].typeNode)) &&
                        (typeNode != elements[i].typeNode))
                    {
                        return false;
                    }
                }
            }

            elements = getEcrChildren();
            for(int i = 0; i < elements.length; i++)
            {
                if(elements[i].typeNode != null)
                {
                    if((!typeNode.hasDescendant(elements[i].typeNode)) &&
                        (typeNode != elements[i].typeNode))
                    {
                        return false;
                    }
                }
            }

            if(isEcrArray())
            {
                if(!(typeNode.getType() instanceof ArrayType))
                {
                    return false;
                }
                else
                {
                    ArrayType at = (ArrayType) typeNode.getType();
                    ClassHierarchy.TypeNode tn = (at.numDimensions > 1) ?
                        classHierarchy.getTypeNode(
                        ArrayType.v(at.baseType, at.numDimensions - 1)) :
                        classHierarchy.getTypeNode(at.baseType);

                    if(!tn.hasDescendant(getEcrIsArrayOf().getEcrTypeNode()))
                    {
                        return false;
                    }
                }
            }

            ecr().typeNode = typeNode;
            return true;
        }

        void unsetEcrTypeNode()
        {
           ecr().typeNode = null;
        }
    }

    private class ConstraintCollector extends AbstractStmtSwitch
    {
        private void handleInvokeExpr(InvokeExpr ie)
        {
            if(ie instanceof InterfaceInvokeExpr)
            {
                InterfaceInvokeExpr invoke = (InterfaceInvokeExpr) ie;

                SootMethod method = invoke.getMethod();
                Value base = invoke.getBase();

                if(base instanceof Local)
                {
                    Local local = (Local) base;

                    TypeVariable localType = getTypeVariable(local);

                    localType.ecrAddParent(getTypeVariable(method.getDeclaringClass()));
                }

                int count = invoke.getArgCount();

                for(int i = 0; i < count; i++)
                {
                    if(invoke.getArg(i) instanceof Local)
                    {
                        Local local = (Local) invoke.getArg(i);

                        TypeVariable localType = getTypeVariable(local);

                        localType.ecrAddParent(getTypeVariable(method.getParameterType(i)));
                    }
                }
            }
            else if(ie instanceof SpecialInvokeExpr)
            {
                SpecialInvokeExpr invoke = (SpecialInvokeExpr) ie;

                SootMethod method = invoke.getMethod();
                Value base = invoke.getBase();

                if(base instanceof Local)
                {
                    Local local = (Local) base;

                    TypeVariable localType = getTypeVariable(local);

                    localType.ecrAddParent(getTypeVariable(method.getDeclaringClass()));
                }

                int count = invoke.getArgCount();

                for(int i = 0; i < count; i++)
                {
                    if(invoke.getArg(i) instanceof Local)
                    {
                        Local local = (Local) invoke.getArg(i);

                        TypeVariable localType = getTypeVariable(local);

                        localType.ecrAddParent(getTypeVariable(method.getParameterType(i)));
                    }
                }
            }
            else if(ie instanceof VirtualInvokeExpr)
            {
                VirtualInvokeExpr invoke = (VirtualInvokeExpr) ie;

                SootMethod method = invoke.getMethod();
                Value base = invoke.getBase();

                if(base instanceof Local)
                {
                    Local local = (Local) base;

                    TypeVariable localType = getTypeVariable(local);

                    localType.ecrAddParent(getTypeVariable(method.getDeclaringClass()));
                }

                int count = invoke.getArgCount();

                for(int i = 0; i < count; i++)
                {
                    if(invoke.getArg(i) instanceof Local)
                    {
                        Local local = (Local) invoke.getArg(i);

                        TypeVariable localType = getTypeVariable(local);

                        localType.ecrAddParent(getTypeVariable(method.getParameterType(i)));
                    }
                }
            }
            else if(ie instanceof StaticInvokeExpr)
            {
                StaticInvokeExpr invoke = (StaticInvokeExpr) ie;

                SootMethod method = invoke.getMethod();

                int count = invoke.getArgCount();

                for(int i = 0; i < count; i++)
                {
                    if(invoke.getArg(i) instanceof Local)
                    {
                        Local local = (Local) invoke.getArg(i);

                        TypeVariable localType = getTypeVariable(local);

                        localType.ecrAddParent(getTypeVariable(method.getParameterType(i)));
                    }
                }
            }
            else
            {
                throw new RuntimeException("Unhandled invoke expression type: " + ie.getClass());
            }
        }

        public void caseBreakpointStmt(BreakpointStmt stmt)
        {
            // Do nothing
        }

        public void caseInvokeStmt(InvokeStmt stmt)
        {
            handleInvokeExpr((InvokeExpr) stmt.getInvokeExpr());
        }

        public void caseAssignStmt(AssignStmt stmt)
        {
            Value l = stmt.getLeftOp();
            Value r = stmt.getRightOp();

//            System.out.println(l + " = " + r);

            TypeVariable left = null;
            TypeVariable right = null;

            if(l instanceof ArrayRef)
            {
                ArrayRef ref = (ArrayRef) l;
                Value base = ref.getBase();
                Value index = ref.getIndex();

                TypeVariable baseType = getTypeVariable((Local) base);
                left = baseType.getEcrIsArrayOf();

                if(index instanceof Local)
                {
                    getTypeVariable((Local) index).ecrCannotBeLong();
                    getTypeVariable((Local) index).ecrCannotBeFloat();
                    getTypeVariable((Local) index).ecrCannotBeDouble();
                    getTypeVariable((Local) index).ecrCannotBeAddress();
                    getTypeVariable((Local) index).ecrCannotBeRef();
                }
            }
            else if(l instanceof Local)
            {
                left = getTypeVariable((Local) l);
            }
            else if(l instanceof InstanceFieldRef)
            {
                InstanceFieldRef ref = (InstanceFieldRef) l;

                TypeVariable baseType = getTypeVariable((Local) ref.getBase());
                baseType.ecrAddParent(getTypeVariable(ref.getField().getDeclaringClass()));
                left = getTypeVariable(ref.getField().getType());
            }
            else if(l instanceof StaticFieldRef)
            {
                StaticFieldRef ref = (StaticFieldRef) l;

                left = getTypeVariable(ref.getField().getType());
            }
            else
            {
                throw new RuntimeException("Unhandled variable type: " + l.getClass());
            }

            if(r instanceof ArrayRef)
            {
                ArrayRef ref = (ArrayRef) r;
                Value base = ref.getBase();
                Value index = ref.getIndex();

                TypeVariable baseType = getTypeVariable((Local) base);
                right = baseType.getEcrIsArrayOf();

                if(index instanceof Local)
                {
                    getTypeVariable((Local) index).ecrCannotBeLong();
                    getTypeVariable((Local) index).ecrCannotBeFloat();
                    getTypeVariable((Local) index).ecrCannotBeDouble();
                    getTypeVariable((Local) index).ecrCannotBeAddress();
                    getTypeVariable((Local) index).ecrCannotBeRef();
                }
            }
            else if(r instanceof DoubleConstant)
            {
                right = getTypeVariable(DoubleType.v());
            }
            else if(r instanceof FloatConstant)
            {
                right = getTypeVariable(FloatType.v());
            }
            else if(r instanceof IntConstant)
            {
                right = getTypeVariable(IntType.v());
            }
            else if(r instanceof LongConstant)
            {
                right = getTypeVariable(LongType.v());
            }
            else if(r instanceof NullConstant)
            {
                right = null; // getTypeVariable(NullType.v());
            }
            else if(r instanceof StringConstant)
            {
                right = getTypeVariable(RefType.v("java.lang.String"));
            }
            else if(r instanceof BinopExpr)
            {
                BinopExpr be = (BinopExpr) r;

                if(be.getOp1() instanceof Local)
                {
                    TypeVariable var  = getTypeVariable((Local) be.getOp1());

                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr))
                    {
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                        var.ecrAddParent(left);
                    }
                    else if((r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr) ||
                        (r instanceof ShlExpr) ||
                        (r instanceof ShrExpr) ||
                        (r instanceof UshrExpr))
                    {
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                        var.ecrAddParent(left);
                        right = var;
                    }
                    else if(r instanceof CmpExpr)
                    {
                        var.ecrCannotBeInt();
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                    }
                    else if((r instanceof CmpgExpr) ||
                        (r instanceof CmplExpr))
                    {
                        var.ecrCannotBeInt();
                        var.ecrCannotBeLong();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                    }
                    else if((r instanceof EqExpr) ||
                        (r instanceof NeExpr))
                    {
                        var.ecrCannotBeLong();
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                    }
                    else if((r instanceof GeExpr) ||
                        (r instanceof GtExpr) ||
                        (r instanceof LeExpr) ||
                        (r instanceof LtExpr))
                    {
                        var.ecrCannotBeLong();
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                    }
                }
                else if(be.getOp1() instanceof DoubleConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr) ||
                        (r instanceof ShlExpr) ||
                        (r instanceof ShrExpr) ||
                        (r instanceof UshrExpr))
                    {
                        getTypeVariable(DoubleType.v()).ecrAddParent(left);
                        right = getTypeVariable(DoubleType.v());
                    }
                }
                else if(be.getOp1() instanceof FloatConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr) ||
                        (r instanceof ShlExpr) ||
                        (r instanceof ShrExpr) ||
                        (r instanceof UshrExpr))
                    {
                        getTypeVariable(FloatType.v()).ecrAddParent(left);
                        right = getTypeVariable(FloatType.v());
                    }
                }
                else if(be.getOp1() instanceof IntConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr) ||
                        (r instanceof ShlExpr) ||
                        (r instanceof ShrExpr) ||
                        (r instanceof UshrExpr))
                    {
                        getTypeVariable(IntType.v()).ecrAddParent(left);
                        right = getTypeVariable(IntType.v());
                    }
                }
                else if(be.getOp1() instanceof LongConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr) ||
                        (r instanceof ShlExpr) ||
                        (r instanceof ShrExpr) ||
                        (r instanceof UshrExpr))
                    {
                        getTypeVariable(LongType.v()).ecrAddParent(left);
                        right = getTypeVariable(LongType.v());
                    }
                }
                else if(be.getOp1() instanceof NullConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr) ||
                        (r instanceof ShlExpr) ||
                        (r instanceof ShrExpr) ||
                        (r instanceof UshrExpr))
                    {
                        // getTypeVariable(NullType.v()).ecrAddParent(left);
                        right = null; // getTypeVariable(NullType.v());
                    }
                }
                else if(be.getOp1() instanceof StringConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr) ||
                        (r instanceof ShlExpr) ||
                        (r instanceof ShrExpr) ||
                        (r instanceof UshrExpr))
                    {
                        getTypeVariable(RefType.v("java.lang.String")).ecrAddParent(left);
                        right = getTypeVariable(RefType.v("java.lang.String"));
                    }
                }

                if(be.getOp2() instanceof Local)
                {
                    TypeVariable var  = getTypeVariable((Local) be.getOp2());

                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr))
                    {
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                        right = var;
                    }
                    else if((r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr))
                    {
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                        right = var;
                    }
                    else if((r instanceof ShlExpr) ||
                        (r instanceof ShrExpr) ||
                        (r instanceof UshrExpr))
                    {
                        var.ecrCannotBeLong();
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                    }
                    else if(r instanceof CmpExpr)
                    {
                        var.ecrCannotBeInt();
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                        right = getTypeVariable(IntType.v());
                    }
                    else if((r instanceof CmpgExpr) ||
                        (r instanceof CmplExpr))
                    {
                        var.ecrCannotBeInt();
                        var.ecrCannotBeLong();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                        right = getTypeVariable(IntType.v());
                    }
                    else if((r instanceof EqExpr) ||
                        (r instanceof NeExpr))
                    {
                        var.ecrCannotBeLong();
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        right = getTypeVariable(IntType.v());
                    }
                    else if((r instanceof GeExpr) ||
                        (r instanceof GtExpr) ||
                        (r instanceof LeExpr) ||
                        (r instanceof LtExpr))
                    {
                        var.ecrCannotBeLong();
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                        right = getTypeVariable(IntType.v());
                    }
                }
                else if(be.getOp2() instanceof DoubleConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr))
                    {
                        right = getTypeVariable(DoubleType.v());
                    }
                    else
                    {
                        right = getTypeVariable(IntType.v());
                    }
                }
                else if(be.getOp2() instanceof FloatConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr))
                    {
                        right = getTypeVariable(FloatType.v());
                    }
                    else
                    {
                        right = getTypeVariable(IntType.v());
                    }
                }
                else if(be.getOp2() instanceof IntConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr))
                    {
                        right = getTypeVariable(IntType.v());
                    }
                    else if(!((r instanceof ShlExpr) ||
                        (r instanceof UshrExpr) ||
                        (r instanceof ShrExpr)))
                    {
                        right = getTypeVariable(IntType.v());
                    }
                }
                else if(be.getOp2() instanceof LongConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr))
                    {
                        right = getTypeVariable(LongType.v());
                    }
                    else
                    {
                        right = getTypeVariable(IntType.v());
                    }
                }
                else if(be.getOp2() instanceof NullConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr))
                    {
                        right = null; // getTypeVariable(NullType.v());
                    }
                    else
                    {
                        right = getTypeVariable(IntType.v());
                    }
                }
                else if(be.getOp2() instanceof StringConstant)
                {
                    if((r instanceof AddExpr) ||
                        (r instanceof SubExpr) ||
                        (r instanceof MulExpr) ||
                        (r instanceof DivExpr) ||
                        (r instanceof RemExpr) ||
                        (r instanceof AndExpr) ||
                        (r instanceof OrExpr) ||
                        (r instanceof XorExpr))
                    {
                        right = getTypeVariable(RefType.v("java.lang.String"));
                    }
                    else
                    {
                        right = getTypeVariable(IntType.v());
                    }
                }
            }
            else if(r instanceof CastExpr)
            {
                CastExpr ce = (CastExpr) r;

                right = getTypeVariable(ce.getCastType());
            }
            else if(r instanceof InstanceOfExpr)
            {
                InstanceOfExpr ioe = (InstanceOfExpr) r;

                TypeVariable var = getTypeVariable((Local) ioe.getOp());

                var.ecrCannotBeInt();
                var.ecrCannotBeLong();
                var.ecrCannotBeFloat();
                var.ecrCannotBeDouble();
                var.ecrCannotBeAddress();

                right = getTypeVariable(IntType.v());
            }
            else if(r instanceof InvokeExpr)
            {
                InvokeExpr ie = (InvokeExpr) r;

                handleInvokeExpr(ie);

                right = getTypeVariable(ie.getMethod().getReturnType());
            }
            else if(r instanceof NewArrayExpr)
            {
                NewArrayExpr nae = (NewArrayExpr) r;

                Type baseType = nae.getBaseType();

                if(baseType instanceof ArrayType)
                {
                    right = getTypeVariable(ArrayType.v(((ArrayType) baseType).
                        baseType, ((ArrayType) baseType).numDimensions + 1));
                }
                else
                    right = getTypeVariable(ArrayType.v((BaseType) baseType, 1));

                Value size = nae.getSize();
                if(size instanceof Local)
                {
                    TypeVariable var = getTypeVariable((Local) size);
                    var.ecrCannotBeLong();
                    var.ecrCannotBeFloat();
                    var.ecrCannotBeDouble();
                    var.ecrCannotBeAddress();
                    var.ecrCannotBeRef();
                }
            }
            else if(r instanceof NewExpr)
            {
                NewExpr na = (NewExpr) r;

                right = getTypeVariable(na.getBaseType());
            }
            else if(r instanceof NewMultiArrayExpr)
            {
                NewMultiArrayExpr nmae = (NewMultiArrayExpr) r;

                right = getTypeVariable(nmae.getBaseType());

                for(int i = 0; i < nmae.getSizeCount(); i++)
                {
                    Value size = nmae.getSize(i);
                    if(size instanceof Local)
                    {
                        TypeVariable var = getTypeVariable((Local) size);
                        var.ecrCannotBeLong();
                        var.ecrCannotBeFloat();
                        var.ecrCannotBeDouble();
                        var.ecrCannotBeAddress();
                        var.ecrCannotBeRef();
                    }
                }
            }
            else if(r instanceof LengthExpr)
            {
                LengthExpr le = (LengthExpr) r;

                if(le.getOp() instanceof Local)
                {
                    getTypeVariable((Local) le.getOp()).getEcrIsArrayOf();
                }

                right = getTypeVariable(IntType.v());
            }
            else if(r instanceof NegExpr)
            {
                NegExpr ne = (NegExpr) r;

                if(ne.getOp() instanceof Local)
                {
                    right = getTypeVariable((Local) ne.getOp());

                    right.ecrCannotBeAddress();
                    right.ecrCannotBeRef();
                }
                else if(ne.getOp() instanceof DoubleConstant)
                {
                    right = getTypeVariable(DoubleType.v());
                }
                else if(ne.getOp() instanceof FloatConstant)
                {
                    right = getTypeVariable(FloatType.v());
                }
                else if(ne.getOp() instanceof IntConstant)
                {
                    right = getTypeVariable(IntType.v());
                }
                else if(ne.getOp() instanceof LongConstant)
                {
                    right = getTypeVariable(LongType.v());
                }
            }
            else if(r instanceof Local)
            {
                right = getTypeVariable((Local) r);
            }
            else if(r instanceof InstanceFieldRef)
            {
                InstanceFieldRef ref = (InstanceFieldRef) r;

                TypeVariable baseType = getTypeVariable((Local) ref.getBase());
                baseType.ecrAddParent(getTypeVariable(ref.getField().getDeclaringClass()));
                right = getTypeVariable(ref.getField().getType());
            }
            else if(r instanceof StaticFieldRef)
            {
                StaticFieldRef ref = (StaticFieldRef) r;

                right = getTypeVariable(ref.getField().getType());
            }
            else if(r instanceof NextNextStmtRef)
            {
                right = getTypeVariable(StmtAddressType.v());
            }
            else
            {
                throw new RuntimeException("Unhandled variable type: " + r.getClass());
            }

            if(right != null)
            {
                right.ecrAddParent(left);
            }
        }

        public void caseIdentityStmt(IdentityStmt stmt)
        {
            Value l = stmt.getLeftOp();
            Value r = stmt.getRightOp();

            if(l instanceof Local)
            {
                TypeVariable left = getTypeVariable((Local) l);

                if(!(r instanceof CaughtExceptionRef))
                {
                    TypeVariable right = getTypeVariable(r.getType());
                    right.ecrAddParent(left);
                }
                else
                {
                    List exceptionTypes = ((CaughtExceptionRef) r).getExceptionTypes();
                    Iterator typeIt = exceptionTypes.iterator();

                    while(typeIt.hasNext())
                    {
                        Type t = (Type) typeIt.next();

                        left.ecrAddChild(getTypeVariable(t));
                    }

                    left.ecrAddParent(getTypeVariable(RefType.v("java.lang.Throwable")));
                }
            }
        }

        public void caseEnterMonitorStmt(EnterMonitorStmt stmt)
        {
            if(stmt.getOp() instanceof Local)
            {
                TypeVariable op = getTypeVariable((Local) stmt.getOp());

                op.ecrAddParent(getTypeVariable(RefType.v("java.lang.Object")));
            }
        }

        public void caseExitMonitorStmt(ExitMonitorStmt stmt)
        {
            if(stmt.getOp() instanceof Local)
            {
                TypeVariable op = getTypeVariable((Local) stmt.getOp());

                op.ecrAddParent(getTypeVariable(RefType.v("java.lang.Object")));
            }
        }

        public void caseGotoStmt(GotoStmt stmt)
        {
        }

        public void caseIfStmt(IfStmt stmt)
        {
            ConditionExpr cond = (ConditionExpr) stmt.getCondition();

            BinopExpr expr = (BinopExpr) cond;
            Value l = expr.getOp1();
            Value r = expr.getOp2();

            if(l instanceof Local)
            {
                TypeVariable var = getTypeVariable((Local) l);

                if((cond instanceof EqExpr) ||
                    (cond instanceof NeExpr))
                {
                    var.ecrCannotBeLong();
                    var.ecrCannotBeFloat();
                    var.ecrCannotBeDouble();
                    var.ecrCannotBeAddress();
                }
                else if((cond instanceof GeExpr) ||
                    (cond instanceof GtExpr) ||
                    (cond instanceof LeExpr) ||
                    (cond instanceof LtExpr))
                {
                    var.ecrCannotBeLong();
                    var.ecrCannotBeFloat();
                    var.ecrCannotBeDouble();
                    var.ecrCannotBeAddress();
                    var.ecrCannotBeRef();
                }
            }

            if(r instanceof Local)
            {
                TypeVariable var = getTypeVariable((Local) r);

                if((cond instanceof EqExpr) ||
                    (cond instanceof NeExpr))
                {
                    var.ecrCannotBeLong();
                    var.ecrCannotBeFloat();
                    var.ecrCannotBeDouble();
                    var.ecrCannotBeAddress();
                }
                else if((cond instanceof GeExpr) ||
                    (cond instanceof GtExpr) ||
                    (cond instanceof LeExpr) ||
                    (cond instanceof LtExpr))
                {
                    var.ecrCannotBeLong();
                    var.ecrCannotBeFloat();
                    var.ecrCannotBeDouble();
                    var.ecrCannotBeAddress();
                    var.ecrCannotBeRef();
                }
            }
        }

        public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
        {
            Value key = stmt.getKey();

            if(key instanceof Local)
            {
                getTypeVariable((Local) key).ecrCannotBeLong();
                getTypeVariable((Local) key).ecrCannotBeFloat();
                getTypeVariable((Local) key).ecrCannotBeDouble();
                getTypeVariable((Local) key).ecrCannotBeAddress();
                getTypeVariable((Local) key).ecrCannotBeRef();
            }
        }

        public void caseNopStmt(NopStmt stmt)
        {
        }

        public void caseRetStmt(RetStmt stmt)
        {
            getTypeVariable((Local) stmt.getStmtAddress()).
                ecrAddParent(getTypeVariable(StmtAddressType.v()));
        }

        public void caseReturnStmt(ReturnStmt stmt)
        {
            if(stmt.getReturnValue() instanceof Local)
            {
                getTypeVariable((Local) stmt.getReturnValue()).ecrAddParent(
                    getTypeVariable(currentMethod.getReturnType()));
            }
        }

        public void caseReturnVoidStmt(ReturnVoidStmt stmt)
        {
        }

        public void caseTableSwitchStmt(TableSwitchStmt stmt)
        {
            Value key = stmt.getKey();

            if(key instanceof Local)
            {
                getTypeVariable((Local) key).ecrCannotBeLong();
                getTypeVariable((Local) key).ecrCannotBeFloat();
                getTypeVariable((Local) key).ecrCannotBeDouble();
                getTypeVariable((Local) key).ecrCannotBeAddress();
                getTypeVariable((Local) key).ecrCannotBeRef();
            }
        }

        public void caseThrowStmt(ThrowStmt stmt)
        {
            if(stmt.getOp() instanceof Local)
            {
                TypeVariable op = getTypeVariable((Local) stmt.getOp());

                op.ecrAddParent(getTypeVariable(RefType.v("java.lang.Throwable")));
            }
        }

        public void defaultCase(Stmt stmt)
        {
            throw new RuntimeException("Unhandled statement type: " + stmt.getClass());
        }
    }

    private static class SCC
    {
        TypeVariable[] variables;
        boolean[] black;
        TypeVariable[] finished;
        int time;

        LinkedList forest = new LinkedList();
        LinkedList current_tree;

        SCC(Vector typeVariableInstances)
        {
            variables = new TypeVariable[typeVariableInstances.size()];

            int counter = 0;
            for(Enumeration e = typeVariableInstances.elements(); e.hasMoreElements();)
            {
                variables[counter++] = (TypeVariable) e.nextElement();
            }

            black = new boolean[variables.length];
            finished = new TypeVariable[variables.length];

            for(int i = 0; i < variables.length; i++)
            {
                if((variables[i] == variables[i].ecr()) &&
                    (variables[i].arrayDepth == 0))
                {
                    if(!black[variables[i].id])
                    {
                        black[variables[i].id] = true;
                        dfsg_visit(variables[i]);
                    }
                }
            }

            black = new boolean[variables.length];

            for(int i = variables.length - 1; i >= 0; i--)
            {
                if(finished[i] == null)
                {
                    continue;
                }

                if((finished[i] == finished[i].ecr()) &&
                    (finished[i].arrayDepth == 0))
                {
                    if(!black[finished[i].id])
                    {
                        current_tree = new LinkedList();
                        forest.add(current_tree);

                        black[finished[i].id] = true;
                        dfsgt_visit(finished[i]);
                    }
                }
            }

            for(Iterator i = forest.iterator(); i.hasNext();)
            {
                LinkedList list = (LinkedList) i.next();
                TypeVariable previous = null;

                for(Iterator j = list.iterator(); j.hasNext();)
                {
                    TypeVariable current = (TypeVariable) j.next();

                    if(previous == null)
                    {
                        previous = current;
                    }
                    else
                    {
                        previous.ecrUnion(current);
                    }
                }
            }
        }

        void dfsg_visit(TypeVariable var)
        {
            TypeVariable[] parents = var.getEcrParents();

            for(int i = 0; i < parents.length; i++)
            {
                if(!black[parents[i].id])
                {
                    black[parents[i].id] = true;
                    dfsg_visit(parents[i]);
                }
            }

            finished[time++] = var;
        }

        void dfsgt_visit(TypeVariable var)
        {
            current_tree.add(var);

            TypeVariable[] children = var.getEcrChildren();

            for(int i = 0; i < children.length; i++)
            {
                if(!black[children[i].id])
                {
                    if(children[i].arrayDepth == 0)
                    {
                        black[children[i].id] = true;
                        dfsgt_visit(children[i]);
                    }
                }
            }
        }
    }

    private static class TypeException extends RuntimeException
    {
    }

    private static void error(String message)
    {
        if(DEBUG)
        {
          System.out.println(message);
        }

        throw new TypeException();
    }
}

