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
