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

public class BDup1_x1Inst extends BDupInst implements Dup1_x1Inst
{

    private final Type mOpType;
    private final Type mUnderType;

    public BDup1_x1Inst(Type aOpType, Type aUnderType)
    {
        mOpType = Baf.getDescriptorTypeOf(aOpType);
        mUnderType = Baf.getDescriptorTypeOf(aUnderType);
    }

    public Type getOp1Type()
    {
        return mOpType;
    }

    public Type getUnder1Type()
    {
        return mUnderType;
    }


    public List<Type> getOpTypes()
    {
        List<Type> res =  new ArrayList<Type>();
        res.add(mOpType);
        return res;
    }
    
    public List<Type> getUnderTypes()
    {
        List<Type> res =  new ArrayList<Type>();
        res.add(mUnderType);
        return res;
    }

    
    final public String getName() { return "dup1_x1"; }


    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseDup1_x1Inst(this);
    }   



    public String toString()
    {
        return "dup1_x1." +  Baf.bafDescriptorOf(mOpType) + "_" +  Baf.bafDescriptorOf(mUnderType);        
    }
  
}



