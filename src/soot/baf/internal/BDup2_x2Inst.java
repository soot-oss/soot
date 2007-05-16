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

public class BDup2_x2Inst extends BDupInst implements Dup2_x2Inst
{
    private final Type mOp1Type;
    private final Type mOp2Type;
    private final Type mUnder1Type;
    private final Type mUnder2Type;

    public BDup2_x2Inst(Type aOp1Type, Type aOp2Type,
                        Type aUnder1Type, Type aUnder2Type)
    {
        mOp1Type = Baf.getDescriptorTypeOf(aOp1Type);
        mOp2Type = Baf.getDescriptorTypeOf(aOp2Type);
        mUnder1Type = Baf.getDescriptorTypeOf(aUnder1Type);
        mUnder2Type = Baf.getDescriptorTypeOf(aUnder2Type);
    }

    public Type getOp1Type()
    {
        return mOp1Type;
    }

    public Type getOp2Type()
    {
        return mOp2Type;
    }

    public Type getUnder1Type()
    {
        return mUnder1Type;
    }

    public Type getUnder2Type()
    {
        return mUnder2Type;
    }

    public List<Type> getOpTypes()
    {
        List<Type> res =  new ArrayList<Type>();
        res.add(mOp1Type);
        
        // 07-20-2006 Michael Batchelder
        // previously did not handle all types of dup2_x2   Now, will take null as mOp2Type, so don't add to overtypes if it is null
        if (mOp2Type != null)
          res.add(mOp2Type);
        return res;
    }
    
    public List<Type> getUnderTypes()
    {
        List<Type> res =  new ArrayList<Type>();
        res.add(mUnder1Type);
        
        // 07-20-2006 Michael Batchelder
        // previously did not handle all types of dup2_x2   Now, will take null as mUnder2Type, so don't add to undertypes if it is null
        if (mUnder2Type != null)
          res.add(mUnder2Type);
        return res;
    }

    
    final public String getName() { return "dup2_x2"; }


    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseDup2_x2Inst(this);
    }   

    public String toString()
    {
      // 07-20-2006 Michael Batchelder
      // previously did not handle all types of dup2_x2   Now, will take null as either mOp2Type or null as mUnder2Type to handle ALL types of dup2_x2
      
      // old code:
      //return "dup2_x2." + Baf.bafDescriptorOf(mOp1Type) + "." + Baf.bafDescriptorOf(mOp2Type) + "_" + Baf.bafDescriptorOf(mUnder1Type) + "." + Baf.bafDescriptorOf(mUnder2Type);
      
      String optypes = Baf.bafDescriptorOf(mOp1Type);
      if (mOp2Type != null)
        optypes += "." + Baf.bafDescriptorOf(mOp2Type);
      
      String undertypes = Baf.bafDescriptorOf(mUnder1Type);
      if (mUnder2Type != null)
        optypes += "." + Baf.bafDescriptorOf(mUnder2Type);
      
      return "dup2_x2." + optypes + "_" + undertypes;
      // END 07-20-2006 Michael Batchelder
    }
}



