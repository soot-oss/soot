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



