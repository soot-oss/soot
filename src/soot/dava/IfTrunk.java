package soot.dava;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.util.*;

public class IfTrunk extends AbstractTrunk
{
    Trunk ifTrunk;
    ValueBox conditionBox;
    
    IfTrunk(ConditionExpr e, Trunk ifTrunk)
    {
        conditionBox = Jimple.v().newConditionExprBox(e);
        
        this.ifTrunk = ifTrunk;
	Removed = false;
	successors = new ArrayList();
	predecessors = new ArrayList();
	contents = new ArrayChain();
	contents.add( ifTrunk);
	Branches = false;

	setCondition( e);
    }

    public void maskGotoStmt()
    {
	ifTrunk.maskGotoStmt();
    }

    public Stmt getFirstStmt() 
    {
	return firstStmt;
    }

    public Stmt getLastStmt() 
    {
	return ifTrunk.getLastStmt();
    }

    public Stmt getTarget()
    {
	return ifTrunk.getTarget();
    }

    public Trunk getIf()
    {
        return ifTrunk;
    }
    
    public void setIf(Trunk t)
    {
        ifTrunk = t;
    }
    
    public List getChildren()
    {
        List l = new ArrayList();
        
        l.add(ifTrunk);
        
        return Collections.unmodifiableList(l);
    }
    
    public Object clone()
    {
        return new IfTrunk((ConditionExpr) getCondition().clone(), 
            (Trunk) getIf().clone());
    }
    
    
    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;
        StringBuffer b = new StringBuffer();
        
	if (getCondition() == null) {
	    System.err.println("Error:  \"if\" has no condition.");
	    System.exit(0);
	}


        b.append(indentation + "if (" + 
            ((isBrief) ? ((ToBriefString) getCondition()).toBriefString() : getCondition().toString()) + ")" + endOfLine);
            
        b.append(indentation + "{" + endOfLine);
        b.append(((isBrief) ? getIf().toBriefString(stmtToName, indentation + "    ") : 
                           getIf().toString(stmtToName, indentation + "    ")));
        b.append(indentation + "}" + endOfLine);

        return b.toString();
    }
}
