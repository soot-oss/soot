/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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





package soot.baf.internal;

import soot.*;

public abstract class AbstractRefTypeInst extends AbstractInst
{
    Type opType;

    protected AbstractRefTypeInst(RefType opType)
    {
        this.opType = opType;
    }
    
    public Type getOpType()
    {
        return opType;
    }
    
    public void setOpType(Type t)
    {
        opType = t;
    }

    public RefType getBaseType() { return (RefType)opType; }
    public void setBaseType(RefType type) { this.opType = type; }

    String getParameters() { return " "+opType.toString(); }
    protected void getParameters( UnitPrinter up ) {
        up.literal(" ");
        up.type(opType);
    }
}
