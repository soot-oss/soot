package soot.dava;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.util.*;

public class TrunkTrunk extends AbstractTrunk
{
    private Trunk t1, t2;
    ValueBox conditionBox;
    
    TrunkTrunk(Trunk t1, Trunk t2)
    {
        this.t1 = t1;
        this.t2 = t2;
	Removed = false;
	contents = new HashChain();
	contents.add( t1);
	contents.add( t2);
	Branches = false;

    }
    
    public void maskGotoStmt()
    {
	t1.maskGotoStmt();
	t2.maskGotoStmt();
    }

    public Stmt getFirstStmt() 
    {
	return t1.getFirstStmt();
    }

    public Stmt getLastStmt() 
    {
	return t2.getLastStmt();
    }

    public Stmt getTarget()
    {
	return t1.getTarget();
    }

    public Trunk getFirstTrunk()
    {
        return t1;
    }
    
    public void setFirstTrunk(Trunk t)
    {
        t1 = t;
    }
    
    public Trunk getSecondTrunk()
    {
        return t2;
    }
    
    public void setSecondTrunk(Trunk t)
    {
        t2 = t;
    }
    
    public List getChildren()
    {
        List l = new ArrayList();
        
        l.add(t1);
        l.add(t2);
        
        return Collections.unmodifiableList(l);
    }
    
    public Object clone()
    {
        return new TrunkTrunk( (Trunk) t1.clone(), (Trunk) t2.clone());
    }
        
    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
	StringBuffer b = new StringBuffer();
//          String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;

//  	b.append( indentation);
	b.append( (isBrief) ? t1.toBriefString( stmtToName, indentation) : t1.toString( stmtToName, indentation));
//  	b.append( endOfLine + indentation);
	b.append( (isBrief) ? t2.toBriefString( stmtToName, indentation) : t2.toString( stmtToName, indentation));
//  	b.append( endOfLine);

	return b.toString();
    }

}
