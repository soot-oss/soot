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
import java.util.*;
import soot.jimple.*;
import soot.util.*;

public class WhileTrunk extends AbstractTrunk
{
    Trunk wTrunk;
    ValueBox conditionBox;
    
    WhileTrunk(ConditionExpr e, Trunk wTrunk)
    {
        conditionBox = Jimple.v().newConditionExprBox(e);
        
        this.wTrunk = wTrunk;
	Removed = false;
	successors = new ArrayList();
	predecessors = new ArrayList();
	contents = new HashChain();
	contents.add( wTrunk);
	Branches = false;

	setCondition( e);
    }

    public void maskGotoStmt()
    {
	wTrunk.maskGotoStmt();
    }

    public Stmt getFirstStmt() 
    {
	return firstStmt;
    }

    public Stmt getLastStmt() 
    {
	return wTrunk.getLastStmt();
    }

    public Stmt getTarget()
    {
	return wTrunk.getTarget();
    }

    public Trunk getWhileBody()
    {
        return wTrunk;
    }
        
    public List getChildren()
    {
        List l = new ArrayList();
        
        l.add( wTrunk);
        
        return Collections.unmodifiableList(l);
    }
    
    public Object clone()
    {
        return new WhileTrunk((ConditionExpr) getCondition().clone(), 
            (Trunk) getWhileBody().clone());
    }
    
    
    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        String endOfLine = (indentation.equals("")) ? " " : StringTools.lineSeparator;
        StringBuffer b = new StringBuffer();
        
	if (getCondition() == null) {
	    System.err.println("Error:  \"while\" has no condition.");
	    System.exit(0);
	}


        b.append(indentation + "while (" + 
            ((isBrief) ? ((ToBriefString) getCondition()).toBriefString() : getCondition().toString()) + ")" + endOfLine);
            
        b.append(indentation + "{" + endOfLine);
        b.append(((isBrief) ? getWhileBody().toBriefString(stmtToName, indentation + "    ") : 
                           getWhileBody().toString(stmtToName, indentation + "    ")));
        b.append(indentation + "}" + endOfLine);

        return b.toString();
    }
}
