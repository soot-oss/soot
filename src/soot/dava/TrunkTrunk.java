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
