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

public class BFieldGetInst extends AbstractInst implements FieldGetInst
{
    SootField field;

    public BFieldGetInst(SootField field)
    {
        this.field = field;
    }

    public int getInCount()
    {
        return 1;
    }


    public Object clone() 
    {
        return new BFieldGetInst(getField());
    }

    public int getInMachineCount()
    {
        return 1;
    }
    
    public int getOutCount()
    {
        return 1;
    }

    public int getOutMachineCount()
    {
        return JasminClass.sizeOfType(field.getType());
    }
    

    final public String getName() { return "fieldget"; }
    final String getParameters(boolean isBrief, Map unitToName) 
    { 
        return " " + (isBrief ? 
                      (field.getDeclaringClass().getName() + "." + 
                       field.getName()) : field.getSignature()); 
    }
    protected void getParameters(UnitPrinter up) {
        up.literal(" ");
        up.fieldRef(field);
    }
    
    public SootField getField() { return field; }
    public void setField(SootField f) { this.field = f; }
    
    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseFieldGetInst(this);
    }   
    public boolean containsFieldRef() { return true; }
}
