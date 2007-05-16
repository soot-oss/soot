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

import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import java.util.*;

abstract public class AbstractJimpleIntLongBinopExpr extends AbstractIntLongBinopExpr implements ConvertToBaf
{
    protected AbstractJimpleIntLongBinopExpr(Value op1, Value op2)
    {
        this.op1Box = Jimple.v().newArgBox(op1);
        this.op2Box = Jimple.v().newArgBox(op2);
    }

    public void convertToBaf(JimpleToBafContext context, List<Unit> out)
    {
        ((ConvertToBaf) this.getOp1()).convertToBaf(context, out);
        ((ConvertToBaf) this.getOp2()).convertToBaf(context, out);
        Unit u = (Unit)makeBafInst(this.getOp1().getType());
        out.add(u);
	Iterator it = context.getCurrentUnit().getTags().iterator();
	while(it.hasNext()) {
	    u.addTag((Tag) it.next());
	}
    }

    abstract Object makeBafInst(Type opType);
}
