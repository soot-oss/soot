package soot.dava;

import soot.*;
import java.util.*;
import soot.util.*;

class BlockTrunk extends AbstractTrunk
{
    Chain contents;
        
    public BlockTrunk()
    {
        contents = new HashChain();
    }
    
    public Chain getContents()
    {
        return contents;
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

        while(it.hasNext())
        {
            Unit u = (Unit) it.next();
            b.append(((isBrief) ? u.toBriefString(stmtToName, indentation) : 
                              u.toString(stmtToName, indentation)));
        }        
        
        return b.toString();
    }
}
