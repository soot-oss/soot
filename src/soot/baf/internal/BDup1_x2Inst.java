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
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class BDup1_x2Inst extends BDupInst implements Dup1_x2Inst
{
    private Type mOpType;
    private Type mUnder1Type;
    private Type mUnder2Type;

    public BDup1_x2Inst(Type aOpType, Type aUnder1Type, Type aUnder2Type)
    {
        mOpType = Baf.getDescriptorTypeOf(aOpType);
        mUnder1Type = Baf.getDescriptorTypeOf(aUnder1Type);
        mUnder2Type = Baf.getDescriptorTypeOf(aUnder2Type);
    }

    public Type getOp1Type()
    {
        return mOpType;
    }

    public Type getUnder1Type()
    {
        return mUnder1Type;
    }

    public Type getUnder2Type()
    {
        return mUnder2Type;
    }

    public List getOpTypes()
    {
        List res =  new ArrayList();
        res.add(mOpType);
        return res;
    }
    
    public List getUnderTypes()
    {
        List res =  new ArrayList();
        res.add(mUnder1Type);
        res.add(mUnder2Type);
        return res;
    }

    
    final public String getName() { return "dup1_x2"; }


    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseDup1_x2Inst(this);
    }   



    public String toString()
    {
        return "dup1_x2." +  Baf.bafDescriptorOf(mOpType) + "_" +  Baf.bafDescriptorOf(mUnder1Type) + "." + Baf.bafDescriptorOf(mUnder2Type);
    }
  
}



