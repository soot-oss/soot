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


package soot.jimple.toolkits.typing.integer;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
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
  private TypeNode inv_approx;
  
  private TypeNode type;

  private List parents = Collections.unmodifiableList(new LinkedList());
  private List children = Collections.unmodifiableList(new LinkedList());

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
    inv_approx = type;
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
    inv_approx = null;
    approx = null;
    type = null;
    parents = null;
    children = null;
  }

  private void merge(TypeVariable var) throws TypeException
  {
    // Merge types
    if(type == null)
      {
	type = var.type;
      }
    else if(var.type != null)
      {
	error("Type Error(22): Attempt to merge two types.");
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

  public TypeNode inv_approx()
  {
    if(rep != this)
      {
	return ecr().inv_approx();
      }

    return inv_approx;
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

  public static void computeInvApprox(TreeSet workList) throws TypeException
  {
    while(workList.size() > 0)
      {
	TypeVariable var = (TypeVariable) workList.first();
	workList.remove(var);

	var.fixInvApprox(workList);
      }
  }

  private void fixApprox(TreeSet workList) throws TypeException
  {
    if(rep != this)
      {
	ecr().fixApprox(workList);
	return;
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
	    TypeNode type = parent.approx.lca_2(approx);

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

  private void fixInvApprox(TreeSet workList) throws TypeException
  {
    if(rep != this)
      {
	ecr().fixInvApprox(workList);
	return;
      }

    for(Iterator i = children.iterator(); i.hasNext();)
      {
	TypeVariable child = ((TypeVariable) i.next()).ecr();

	if(child.inv_approx == null)
	  {
	    child.inv_approx = inv_approx;
	    workList.add(child);
	  }
	else
	  {
	    TypeNode type = child.inv_approx.gcd_2(inv_approx);

	    if(type != child.inv_approx)
	      {
		child.inv_approx = type;
		workList.add(child);
	      }
	  }
      }

    if(type != null)
      {
	inv_approx = type;
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
    return "[id:" + id + ((type != null) ? (",type:" + type) : "") + ",approx:" + approx + ",inv_approx:" + inv_approx + s + "]";
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
