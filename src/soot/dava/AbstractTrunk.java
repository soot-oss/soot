/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Jerome Miecznikowski
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

package soot.dava;

import soot.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.toolkits.graph.*;

public abstract class AbstractTrunk extends AbstractUnit implements Trunk 
{
    static final public List emptyList = Collections.unmodifiableList(new ArrayList());

    public boolean Branches;
    protected List successors, predecessors;
    protected boolean Removed;
    protected ConditionExpr condition;
    public Stmt targetS;
    public Stmt firstStmt;
    protected boolean doGotoMask;

    protected Chain contents;

    public Chain getContents()
    {
        return contents;
    }

    public boolean branches() {
	return Branches;
    }

    public boolean fallsThrough() {
	return false;
    }

    public void setContents( Chain c) {
	contents = c;
    }

    public List getChildren() 
    {
        return emptyList;
    }

    public ConditionExpr getCondition() {
	return condition;
    }

    public void setCondition( ConditionExpr c) {
	condition = c;
    }
    
 
  public void addSuccessor( Trunk t) 
  {
      if (t != null)
	  successors.add( t);
  }

  public void addPredecessor( Trunk t)
  {
      if (t != null)
	  predecessors.add( t);
  }

  public List getSuccessors()
  {
    return successors;
  }
  
  public List getPredecessors()
  {
    return predecessors;
  }

  public void setSuccessorList( List l) 
  {
    successors = l;
  }

  public void setSuccessorList( Trunk t) {
    successors = new ArrayList();
    successors.add( t);
  }

  public void setPredecessorList( List l)
  {
    predecessors = l;
  }

  public boolean removed() {
    return Removed;
  }

  public void setRemoved() {
    Removed = true;
  }

    public void dump( String indentation) {
	System.out.println( indentation + "Warning: Trunk doesn't have dump() defined.");
    }

    
}
