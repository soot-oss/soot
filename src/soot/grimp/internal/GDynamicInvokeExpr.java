/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
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






package soot.grimp.internal;
import java.util.ArrayList;
import java.util.List;

import soot.SootMethodRef;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.util.Switch;

@SuppressWarnings({"serial","rawtypes","unchecked"})
public class GDynamicInvokeExpr extends AbstractInvokeExpr implements DynamicInvokeExpr 
{
    private ValueBox[] argBoxes;
	private SootMethodRef methodRef;


	public GDynamicInvokeExpr(SootMethodRef methodRef, List args)
    {
		this.methodRef = methodRef; 
		this.argBoxes = new ValueBox[args.size()];
		
        for(int i = 0; i < args.size(); i++)
            this.argBoxes[i] = Grimp.v().newExprBox((Value) args.get(i));
    }
    
    
	public Object clone() 
    {
        ArrayList clonedArgs = new ArrayList(getArgCount());

        for(int i = 0; i < getArgCount(); i++) {
            clonedArgs.add(i, Grimp.cloneIfNecessary(getArg(i)));
        }
        
        return new  GDynamicInvokeExpr(methodRef, clonedArgs);
    }
	
	public List getUseBoxes() {
		List list = new ArrayList();

        for (ValueBox element : argBoxes) {
            list.addAll(element.getValue().getUseBoxes());
            list.add(element);
        }

        return list;
	}


	public void toString(UnitPrinter up) {
		up.literal(Jimple.DYNAMICINVOKE);
		up.literal(" ");
		up.methodRef(methodRef);
		up.literal("(");

		for(int i = 0; i < argBoxes.length; i++)
		{
			if(i != 0)
				up.literal(", ");

			argBoxes[i].toString(up);
		}

		up.literal(")");
	}


	public void apply(Switch sw) {
		((ExprSwitch) sw).caseDynamicInvokeExpr(this);
	}


	public boolean equivTo(Object o) {
		if (o instanceof GDynamicInvokeExpr)
        {
            GDynamicInvokeExpr ie = (GDynamicInvokeExpr)o;
            if (!(getMethod().equals(ie.getMethod()) && 
                  argBoxes.length == ie.argBoxes.length))
                return false;
            for (ValueBox element : argBoxes)
				if (!(element.getValue().equivTo(element.getValue())))
                    return false;
            return true;
        }
        return false;
	}


	public int equivHashCode() {
		return getMethod().equivHashCode();
	}
}
