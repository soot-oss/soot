/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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





package soot.baf.internal;

import soot.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public abstract class AbstractOpTypeInst extends AbstractInst
{
    protected Type opType;

    protected AbstractOpTypeInst(Type opType)
    {
        if(opType instanceof NullType || opType instanceof ArrayType || opType instanceof RefType)
            opType = RefType.v();
        
        this.opType = opType;
    }
    
    public Type getOpType()
    {
        return opType;
    }
    
    public void setOpType(Type t)
    {
        opType = t;
        if(opType instanceof NullType || opType instanceof ArrayType || opType instanceof RefType)
            opType = RefType.v();
    }

    private static String bafDescriptorOf(Type type)
    {
        TypeSwitch sw;

        type.apply(sw = new TypeSwitch()
        {
            public void caseBooleanType(BooleanType t)
            {
                setResult("b");
            }

            public void caseByteType(ByteType t)
            {
                setResult("b");
            }

            public void caseCharType(CharType t)
            {
                setResult("c");
            }

            public void caseDoubleType(DoubleType t)
            {
                setResult("d");
            }

            public void caseFloatType(FloatType t)
            {
                setResult("f");
            }

            public void caseIntType(IntType t)
            {
                setResult("i");
            }

            public void caseLongType(LongType t)
            {
                setResult("l");
            }

            public void caseShortType(ShortType t)
            {
                setResult("s");
            }

            
            public void defaultCase(Type t)
            {
                throw new RuntimeException("Invalid type: " + t);
            }

            public void caseRefType(RefType t)
            {
                setResult("r");
            }


        });

        return (String) sw.getResult();

    }

    /* override AbstractInst's toString with our own, including types */
    protected String toString(boolean isBrief, Map unitToName, String indentation)
    {
        return indentation + getName() + "." + 
          Baf.bafDescriptorOf(opType) + getParameters(isBrief, unitToName);
    }


  
  
  
  public int getOutMachineCount()
  {
    return JasminClass.sizeOfType(getOpType());
  } 

}
