/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */






package soot.jimple.internal;

import soot.*;
import soot.tagkit.*;

import soot.jimple.*;
import soot.baf.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public class JReturnStmt extends AbstractStmt implements ReturnStmt
{
    ValueBox returnValueBox;

    public JReturnStmt(Value returnValue)
    {
        this(Jimple.v().newImmediateBox(returnValue));
    }

    protected JReturnStmt(ValueBox returnValueBox)
    {
        this.returnValueBox = returnValueBox;
    }

    public Object clone() 
    {
        return new JReturnStmt(Jimple.cloneIfNecessary(getOp()));
    }

    public String toString()
    {
        return Jimple.v().RETURN + " "  + returnValueBox.getValue().toString();
    }
    
    public void toString( UnitPrinter up) {
        up.literal(Jimple.v().RETURN);
        up.literal(" ");
        returnValueBox.toString(up);
    }
    
    public ValueBox getOpBox()
    {
        return returnValueBox;
    }

    public void setOp(Value returnValue)
    {
        returnValueBox.setValue(returnValue);
    }

    public Value getOp()
    {
        return returnValueBox.getValue();
    }

    public List getUseBoxes()
    {
        List useBoxes = new ArrayList();

        useBoxes.addAll(returnValueBox.getValue().getUseBoxes());
        useBoxes.add(returnValueBox);

        return useBoxes;
    }

    public void apply(Switch sw)
    {
        ((StmtSwitch) sw).caseReturnStmt(this);
    }

    public void convertToBaf(JimpleToBafContext context, List out)
    {
       ((ConvertToBaf)(getOp())).convertToBaf(context, out);
       
       
       Unit u;
       out.add(u = Baf.v().newReturnInst(getOp().getType()));
     
       Unit currentUnit = this;

	Iterator it = currentUnit.getTags().iterator();	
	while(it.hasNext()) {
	    u.addTag((Tag) it.next());
	}
    }

     
    public boolean fallsThrough(){return false;}        
    public boolean branches(){return false;}


}

