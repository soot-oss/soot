package soot.dava;

import soot.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.toolkits.graph.*;

public class BlockTrunk extends AbstractTrunk
{

    public BlockTrunk()
    {
        contents = new HashChain();
	successors = new ArrayList();
	predecessors = new ArrayList();
	condition = null;
    }
    
    public BlockTrunk( Block b) 
    {
	Branches = false; 
	contents = new HashChain();
	successors = new ArrayList();
	predecessors = new ArrayList();
	condition = null;
	targetS = null;
	doGotoMask = false;
    }
    
    public void maskGotoStmt()
    {
	doGotoMask = true;
    }

    public Stmt getFirstStmt()
    {
	return (Stmt) contents.getFirst();
    }

    public Stmt getLastStmt()
    {
	return (Stmt) contents.getLast();
    }

    public Stmt getTarget()
    {
	return targetS;
    }
    
    public void addContents( Object o) 
    {
	contents.add( o);
    }

    public List getChildren()
    {
        ArrayList l = new ArrayList();
        l.addAll(getContents());
        return l;
    }        

    public Object clone()
    {
        BlockTrunk t = new BlockTrunk();
        Iterator it = t.getContents().iterator();
        Chain newContents = t.getContents();
        
        while(it.hasNext())
            newContents.add(((Unit) it.next()).clone());
            
        return t;
    }
    
    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        Iterator it = getContents().iterator();
        StringBuffer b = new StringBuffer();
	String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;

        while(it.hasNext()) {
	    Unit u = (Unit) it.next();
	    Stmt s = (Stmt) u;

	    if ((!(s instanceof GotoStmt)) || (!doGotoMask)) {
		b.append(((isBrief) ? u.toBriefString(stmtToName, indentation) : u.toString(stmtToName, indentation)));
		b.append( ";"+endOfLine);
	    }
        }        
        
        return b.toString();
    }
}



