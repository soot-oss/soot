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
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class BStaticPutInst extends AbstractInst implements StaticPutInst
{
    SootField field;

    public BStaticPutInst(SootField field)
    {
        this.field = field;
    }


    public int getInCount()
    {
        return 1;
    }



    public Object clone() 
    {
        return new  BStaticPutInst(getField());
    }



  public int getInMachineCount()
  {
    return JasminClass.sizeOfType(field.getType());
  }
    
    public int getOutCount()
    {
        return 0;
    }

    public int getOutMachineCount()
    {
        return 0;
    }

    final public String getName() { return "staticput"; }
    final String getParameters(boolean isBrief, Map unitToName) 
    { 
        return " " + (isBrief ? 
                      (field.getDeclaringClass().getName() + "." + 
                       field.getName()) : field.getSignature()); 
    }

    public SootField getField() { return field; }
    public void setField(SootField f) { this.field = f; }
    
    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseStaticPutInst(this);
    }   
}
