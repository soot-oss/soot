/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Etienne Gagnon.  All rights reserved.
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


package soot.jimple.toolkits.typing;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/** Represents a type variable. **/
class TypeVariable implements Comparable
{
  private static final boolean DEBUG = false;

  private final int id;
  private final TypeResolver resolver;

  private TypeVariable rep = this;
  private int rank = 0;

  private TypeNode approx;

  private TypeNode type;
  private TypeVariable array;
  private TypeVariable element;
  private int depth;

  private List parents = Collections.unmodifiableList(new LinkedList());
  private List children = Collections.unmodifiableList(new LinkedList());
  private BitVector ancestors;
  private BitVector indirectAncestors;

  public TypeVariable(int id, TypeResolver resolver)
  {
    this.id = id;
    this.resolver = resolver;
  }

  public TypeVariable(int id, TypeResolver resolver, TypeNode type)
  {
    this.id = id;
    this.resolver = resolver;
    this.type = type;
    approx = type;
    
    for( Iterator parentIt = type.parents().iterator(); parentIt.hasNext(); ) {
    
        final TypeNode parent = (TypeNode) parentIt.next();
	
	addParent(resolver.typeVariable(parent));
      }

    if(type.hasElement())
      {
	element = resolver.typeVariable(type.element());
	element.array = this;
      }
  }

  public int hashCode()
  {
    if(rep != this)
      {
	return ecr().hashCode();
      }
   
    return id;
  }

  public boolean equals(Object obj)
  {
    if(rep != this)
      {
	return ecr().equals(obj);
      }

    if(obj == null)
      {
	return false;
      }

    if(!obj.getClass().equals(getClass()))
      {
	return false;
      }
    
    TypeVariable ecr = ((TypeVariable) obj).ecr();

    if(ecr != this)
      {
	return false;
      }
    
    return true;
  }

  public int compareTo(Object o)
  {
    if(rep != this)
      {
	return ecr().compareTo(o);
      }

    return id - ((TypeVariable) o).ecr().id;
  }
  
  private TypeVariable ecr()
  {
    if(rep != this)
      {
	rep = rep.ecr();
      }

    return rep;
  }

  public TypeVariable union(TypeVariable var) throws TypeException
  {
    if(rep != this)
      {
	return ecr().union(var);
      }

    TypeVariable y = var.ecr();

    if(this == y)
      {
	return this;
      }
    
    if(rank > y.rank)
      {
	y.rep = this;

	merge(y);
	y.clear();

	return this;
      }

    rep = y;
    if(rank == y.rank)
      {
	y.rank++;
      }

    y.merge(this);
    clear();

    return y;
  }

  private void clear()
  {
    approx = null;
    type = null;
    element = null;
    array = null;
    parents = null;
    children = null;
    ancestors = null;
    indirectAncestors = null;
  }

  private void merge(TypeVariable var) throws TypeException
  {
    if(depth != 0 || var.depth != 0)
      {
	throw new InternalTypingException();
      }

    // Merge types
    if(type == null)
      {
	type = var.type;
      }
    else if(var.type != null)
      {
	error("Type Error(1): Attempt to merge two types.");
      }

    // Merge parents
    {
      Set set = new TreeSet(parents);
      set.addAll(var.parents);
      set.remove(this);
      parents = Collections.unmodifiableList(new LinkedList(set));
    }

    // Merge children
    {
      Set set = new TreeSet(children);
      set.addAll(var.children);
      set.remove(this);
      children = Collections.unmodifiableList(new LinkedList(set));
    }
  }

  void validate() throws TypeException
  {
    if(rep != this)
      {
	ecr().validate();
	return;
      }
    
    // Validate relations.
    if(type != null)
      {
	for(Iterator i = parents.iterator(); i.hasNext();)
	  {
	    TypeVariable parent = ((TypeVariable) i.next()).ecr();

	    if(parent.type != null)
	      {
		if(!type.hasAncestor(parent.type))
		  {
		    if(DEBUG)
		      {
			G.v().out.println(parent.type + " is not a parent of " + type);
		      }

		    error("Type Error(2): Parent type is not a valid ancestor.");
		  }
	      }
	  }

	for(Iterator i = children.iterator(); i.hasNext();)
	  {
	    TypeVariable child = ((TypeVariable) i.next()).ecr();

	    if(child.type != null)
	      {
		if(!type.hasDescendant(child.type))
		  {
		    if(DEBUG)
		      {
			G.v().out.println(child.type + "(" + child + ") is not a child of " + type + "(" + this + ")");
		      }

		    error("Type Error(3): Child type is not a valid descendant.");
		  }
	      }
	  }
      }
  }
  
  public void removeIndirectRelations()
  {
    if(rep != this)
      {
	ecr().removeIndirectRelations();
	return;
      }
    
    if(indirectAncestors == null)
      {
	fixAncestors();
      }

    List parentsToRemove = new LinkedList();

    for( Iterator parentIt = parents.iterator(); parentIt.hasNext(); ) {

        final TypeVariable parent = (TypeVariable) parentIt.next();
	if(indirectAncestors.get(parent.id()))
	  {
	    parentsToRemove.add(parent);
	  }
      }

    for( Iterator parentIt = parentsToRemove.iterator(); parentIt.hasNext(); ) {

        final TypeVariable parent = (TypeVariable) parentIt.next();

	removeParent(parent);
      }
  }
  
  private void fixAncestors()
  {
    BitVector ancestors = new BitVector(0);
    BitVector indirectAncestors = new BitVector(0);
    for(Iterator i = parents.iterator(); i.hasNext();)
      {
	TypeVariable parent = ((TypeVariable) i.next()).ecr();

	if(parent.ancestors == null)
	  {
	    parent.fixAncestors();
	  }

	ancestors.set(parent.id);
	ancestors.or(parent.ancestors);
	indirectAncestors.or(parent.ancestors);
      }

    this.ancestors = ancestors;
    this.indirectAncestors = indirectAncestors;
  }

  public int id()
  {
    if(rep != this)
      {
	return ecr().id();
      }

    return id;
  }

  public void addParent(TypeVariable variable)
  {
    if(rep != this)
      {
	ecr().addParent(variable);
	return;
      }

    TypeVariable var = variable.ecr();
 
    if(var == this)
      {
	return;
      }

    {
      Set set = new TreeSet(parents);
      set.add(var);
      parents = Collections.unmodifiableList(new LinkedList(set));
    }
    
    {
      Set set = new TreeSet(var.children);
      set.add(this);
      var.children = Collections.unmodifiableList(new LinkedList(set));
    }
  }

  public void removeParent(TypeVariable variable)
  {
    if(rep != this)
      {
	ecr().removeParent(variable);
	return;
      }

    TypeVariable var = variable.ecr();
 
    {
      Set set = new TreeSet(parents);
      set.remove(var);
      parents = Collections.unmodifiableList(new LinkedList(set));
    }

    {
      Set set = new TreeSet(var.children);
      set.remove(this);
      var.children = Collections.unmodifiableList(new LinkedList(set));
    }
  }

  public void addChild(TypeVariable variable)
  {
    if(rep != this)
      {
	ecr().addChild(variable);
	return;
      }

    TypeVariable var = variable.ecr();
 
    if(var == this)
      {
	return;
      }

    {
      Set set = new TreeSet(children);
      set.add(var);
      children = Collections.unmodifiableList(new LinkedList(set));
    }

    {
      Set set = new TreeSet(var.parents);
      set.add(this);
      var.parents = Collections.unmodifiableList(new LinkedList(set));
    }
  }

  public void removeChild(TypeVariable variable)
  {
    if(rep != this)
      {
	ecr().removeChild(variable);
	return;
      }

    TypeVariable var = variable.ecr();
 
    {
      Set set = new TreeSet(children);
      set.remove(var);
      children = Collections.unmodifiableList(new LinkedList(set));
    }

    {
      Set set = new TreeSet(var.parents);
      set.remove(this);
      var.parents = Collections.unmodifiableList(new LinkedList(set));
    }
  }

  public int depth()
  {
    if(rep != this)
      {
	return ecr().depth();
      }

    return depth;
  }

  public void makeElement()
  {
    if(rep != this)
      {
	ecr().makeElement();
	return;
      }

    if(element == null)
      {
	element = resolver.typeVariable();
	element.array = this;
      }
  }

  public TypeVariable element()
  {
    if(rep != this)
      {
	return ecr().element();
      }

    return (element == null) ? null : element.ecr();
  }

  public TypeVariable array()
  {
    if(rep != this)
      {
	return ecr().array();
      }

    return (array == null) ? null : array.ecr();
  }

  public List parents()
  {
    if(rep != this)
      {
	return ecr().parents();
      }
    
    return parents;
  }

  public List children()
  {
    if(rep != this)
      {
	return ecr().children();
      }
    
    return children;
  }

  public TypeNode approx()
  {
    if(rep != this)
      {
	return ecr().approx();
      }

    return approx;
  }

  public TypeNode type()
  {
    if(rep != this)
      {
	return ecr().type();
      }

    return type;
  }

  static void error(String message) throws TypeException
  {
    try
      {
	throw new TypeException(message);
      }
    catch(TypeException e)
      {
	if(DEBUG)
	  {
	    e.printStackTrace();
	  }
	throw e;
      }
  }

  /** Computes approximative types.  The work list must be 
   *  initialized with all constant type variables. */
  public static void computeApprox(TreeSet workList) throws TypeException
  {
    while(workList.size() > 0)
      {
	TypeVariable var = (TypeVariable) workList.first();
	workList.remove(var);

	var.fixApprox(workList);
      }
  }

  private void fixApprox(TreeSet workList) throws TypeException
  {
    if(rep != this)
      {
	ecr().fixApprox(workList);
	return;
      }

    if(type == null && approx != resolver.hierarchy().NULL)
      {
	TypeVariable element = element();
	
	if(element != null)
	  {
	    if(!approx.hasElement())
	      {
		G.v().out.println("*** " + this + " ***");
		
		error("Type Error(4)");
	      }
	    
	    TypeNode temp = approx.element();
	    
	    if(element.approx == null)
	      {
		element.approx = temp;
		workList.add(element);
	      }
	    else
	      {
		TypeNode type = element.approx.lca(temp);

		if(type != element.approx)
		  {
		    element.approx = type;
		    workList.add(element);
		  }
		else if(element.approx != resolver.hierarchy().INT)
		  {
		    type = approx.lca(element.approx.array());

		    if(type != approx)
		      {
			approx = type;
			workList.add(this);
		      }
		  }
	      }
	  }
	
	TypeVariable array = array();
	
	if(array != null &&
	   approx != resolver.hierarchy().NULL &&
	   approx != resolver.hierarchy().INT)
	  {
	    TypeNode temp = approx.array();
	    
	    if(array.approx == null)
	      {
		array.approx = temp;
		workList.add(array);
	      }
	    else
	      {
		TypeNode type = array.approx.lca(temp);

		if(type != array.approx)
		  {
		    array.approx = type;
		    workList.add(array);
		  }
		else
		  {
		    type = approx.lca(array.approx.element());

		    if(type != approx)
		      {
			approx = type;
			workList.add(this);
		      }
		  }
	      }
	  }
      }
    
    for(Iterator i = parents.iterator(); i.hasNext();)
      {
	TypeVariable parent = ((TypeVariable) i.next()).ecr();

	if(parent.approx == null)
	  {
	    parent.approx = approx;
	    workList.add(parent);
	  }
	else
	  {
	    TypeNode type = parent.approx.lca(approx);

	    if(type != parent.approx)
	      {
		parent.approx = type;
		workList.add(parent);
	      }
	  }
      }

    if(type != null)
      {
	approx = type;
      }
  }

  public void fixDepth() throws TypeException
  {
    if(rep != this)
      {
	ecr().fixDepth();
	return;
      }

    if(type != null)
      {
	if(type.type() instanceof ArrayType)
	  {
	    ArrayType at = (ArrayType) type.type();

	    depth = at.numDimensions;
	  }
	else
	  {
	    depth = 0;
	  }
      }
    else
      {
	if(approx.type() instanceof ArrayType)
	  {
	    ArrayType at = (ArrayType) approx.type();

	    depth = at.numDimensions;
	  }
	else
	  {
	    depth = 0;
	  }
      }

    // make sure array types have element type
    if(depth == 0 && element() != null)
      {
	error("Type Error(11)");
      }
    else if(depth > 0 && element() == null)
      {
	makeElement();
	TypeVariable element = element();
	element.depth = depth - 1;

	while(element.depth != 0)
	  {
	    element.makeElement();
	    element.element().depth = element.depth - 1;
	    element = element.element();
	  }
      }
  }

  public void propagate()
  {
    if(rep != this)
      {
	ecr().propagate();
      }

    if(depth == 0)
      {
	return;
      }

    for(Iterator i = parents.iterator(); i.hasNext(); )
      {
	TypeVariable var = ((TypeVariable) i.next()).ecr();

	if(var.depth() == depth)
	  {
	    element().addParent(var.element());
	  }
	else if(var.depth() == 0)
	  {
	    if(var.type() == null) {
	      // hack for J2ME library, reported by Stephen Cheng
	      if (!G.v().isJ2ME) {
		var.addChild(resolver.typeVariable(resolver.hierarchy().CLONEABLE));
		var.addChild(resolver.typeVariable(resolver.hierarchy().SERIALIZABLE));
	      }
	    }
	  }
	else
	  {
	    if(var.type() == null) {
	      // hack for J2ME library, reported by Stephen Cheng
	      if (!G.v().isJ2ME) {
		var.addChild(resolver.typeVariable(ArrayType.v(RefType.v("java.lang.Cloneable"), var.depth())));
		var.addChild(resolver.typeVariable(ArrayType.v(RefType.v("java.io.Serializable"), var.depth())));
	      }
	    }
	  }
      }

    for( Iterator varIt = parents.iterator(); varIt.hasNext(); ) {

        final TypeVariable var = (TypeVariable) varIt.next();
	removeParent(var);
      }
  }

  public String toString()
  {
    if(rep != this)
      {
	return ecr().toString();
      }
    
    StringBuffer s = new StringBuffer();
    s.append(",[parents:");

    {
      boolean comma = false;
      
      for(Iterator i = parents.iterator(); i.hasNext(); )
	{
	  if(comma)
	    {
	      s.append(",");
	    }
	  else
	    {
	      comma = true;
	    }
	  s.append(((TypeVariable) i.next()).id());
	}
    }
    
    s.append("],[children:");

    {
      boolean comma = false;
      
      for(Iterator i = children.iterator(); i.hasNext(); )
	{
	  if(comma)
	    {
	      s.append(",");
	    }
	  else
	    {
	      comma = true;
	    }
	  s.append(((TypeVariable) i.next()).id());
	}
    }
    
    s.append("]");
    return "[id:" + id + ",depth:" + depth + ((type != null) ? (",type:" + type) : "") + ",approx:" + approx + s + 
      (element == null ? "" : ",arrayof:" + element.id()) + "]";
  }

  public void fixParents()
  {
    if(rep != this)
      {
	ecr().fixParents();
	return;
      }

    {
      Set set = new TreeSet(parents);
      parents = Collections.unmodifiableList(new LinkedList(set));
    }
  }

  public void fixChildren()
  {
    if(rep != this)
      {
	ecr().fixChildren();
	return;
      }

    {
      Set set = new TreeSet(children);
      children = Collections.unmodifiableList(new LinkedList(set));
    }
  }

}
