/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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


package soot.jimple.internal;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import soot.baf.*;
import soot.jimple.*;
import java.util.*;

public abstract class AbstractSpecialInvokeExpr extends AbstractInstanceInvokeExpr 
           implements SpecialInvokeExpr, ConvertToBaf
{
    protected AbstractSpecialInvokeExpr(ValueBox baseBox, SootMethod method,
                                ValueBox[] argBoxes)
    {
        this.baseBox = baseBox; this.method = method;
        this.argBoxes = argBoxes;
    }

    public boolean equivTo(Object o)
    {
        if (o instanceof AbstractSpecialInvokeExpr)
        {
            AbstractSpecialInvokeExpr ie = (AbstractSpecialInvokeExpr)o;
            if (!(baseBox.getValue().equivTo(ie.baseBox.getValue()) &&
                    method.equals(ie.method) && 
                    argBoxes.length == ie.argBoxes.length))
                return false;
            for (int i = 0; i < argBoxes.length; i++)
                if (!(argBoxes[i].getValue().equivTo(ie.argBoxes[i].getValue())))
                    return false;
            return true;
        }
        return false;
    }

    /** Returns a hash code for this object, consistent with structural equality. */
    public int equivHashCode() 
    {
        return baseBox.getValue().equivHashCode() * 101 + method.equivHashCode() * 17;
    }

    public abstract Object clone();

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(Jimple.SPECIALINVOKE + " " + baseBox.getValue().toString() +
            "." + method.getSignature() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(argBoxes[i].getValue().toString());
        }

        buffer.append(")");

        return buffer.toString();
    }


    public String toBriefString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(Jimple.SPECIALINVOKE + " " + ((ToBriefString) baseBox.getValue()).toBriefString() +
            "." + method.getName() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(((ToBriefString) argBoxes[i].getValue()).toBriefString());
        }

        buffer.append(")");

        return buffer.toString();
    }


    public void apply(Switch sw)
    {
        ((ExprSwitch) sw).caseSpecialInvokeExpr(this);
    }


    public void convertToBaf(JimpleToBafContext context, List out)
    {
       ((ConvertToBaf)(getBase())).convertToBaf(context, out);

       for(int i = 0; i < argBoxes.length; i++)
        {
            ((ConvertToBaf)(argBoxes[i].getValue())).convertToBaf(context, out);
        }
       
       Unit u;
       out.add(u = Baf.v().newSpecialInvokeInst(method));

       Unit currentUnit = context.getCurrentUnit();

	Iterator it = currentUnit.getTags().iterator();	
	while(it.hasNext()) {
	    u.addTag((Tag) it.next());
	}
    }
}
