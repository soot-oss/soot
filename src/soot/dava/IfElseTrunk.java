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

public class IfElseTrunk extends AbstractTrunk
{
    Trunk ifTrunk;
    Trunk elseTrunk;
    ValueBox conditionBox;
    
    IfElseTrunk(ConditionExpr e, Trunk ifTrunk, Trunk elseTrunk)
    {
        conditionBox = Jimple.v().newConditionExprBox(e);
        
        this.ifTrunk = ifTrunk;
        this.elseTrunk = elseTrunk;
	Removed = false;
	successors = new ArrayList();
	predecessors = new ArrayList();
	contents = new HashChain();
	contents.add( ifTrunk);
	contents.add( elseTrunk);
	Branches = false;

	setCondition( e);
    }

    public void maskGotoStmt()
    {
	ifTrunk.maskGotoStmt();
	elseTrunk.maskGotoStmt();
    }

    public Stmt getFirstStmt() 
    {
	return firstStmt;
    }

    public Stmt getLastStmt() 
    {
	return elseTrunk.getLastStmt();
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
    
    public Trunk getElse()
    {
        return elseTrunk;
    }
    
    public void setElse(Trunk t)
    {
        elseTrunk = t;
    }
    
    public List getChildren()
    {
        List l = new ArrayList();
        
        l.add(ifTrunk);
        l.add(elseTrunk);
        
        return Collections.unmodifiableList(l);
    }
    
    public Object clone()
    {
        return new IfElseTrunk((ConditionExpr) getCondition().clone(), 
            (Trunk) getIf().clone(),
            (Trunk) getElse().clone());
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
        b.append(indentation + "else" + endOfLine + indentation + "{" + endOfLine);
        b.append(((isBrief) ? getElse().toBriefString(stmtToName, indentation + "    ") : 
                           getElse().toString(stmtToName, indentation + "    ")));
        b.append(indentation + "}" + endOfLine);

        return b.toString();
    }
}
