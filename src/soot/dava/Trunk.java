package soot.dava;

import soot.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.toolkits.graph.*;

public interface Trunk extends Unit
{
    public List getChildren();
    public void addSuccessor( Trunk t);
    public void addPredecessor( Trunk t);
    public List getSuccessors();
    public List getPredecessors();
    public void setSuccessorList( List l); 
    public void setPredecessorList( List l);
    public boolean removed();
    public void setRemoved();
    public ConditionExpr getCondition();
    public void setCondition( ConditionExpr c);
    public void dump( String indentation);
    public Chain getContents();
    public void setContents( Chain c);
    public Stmt getFirstStmt();
    public Stmt getLastStmt();
    public Stmt getTarget();
    public void maskGotoStmt();
}


