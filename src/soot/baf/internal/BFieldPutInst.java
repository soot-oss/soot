/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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





package soot.baf.internal;

import soot.*;
import soot.baf.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class BFieldPutInst extends AbstractInst implements FieldPutInst
{
    SootFieldRef fieldRef;

    public BFieldPutInst(SootFieldRef fieldRef)
    {
        if( fieldRef.isStatic() ) throw new RuntimeException("wrong static-ness");
        this.fieldRef = fieldRef;
    }
    
    public int getInCount()
    {
        return 2;
    }
    
    public int getOutCount()
    {
        return 0;
    }
    


    public Object clone() 
    {
        return new BFieldPutInst(fieldRef);
    }

    public int getInMachineCount()
    {
        return JasminClass.sizeOfType(fieldRef.type()) + 1;
    }

    public int getOutMachineCount()
    {
        return 0;
    }
        

    final public String getName() { return "fieldput"; }
    final String getParameters()
    { 
        return " " + fieldRef.getSignature(); 
    }
    protected void getParameters( UnitPrinter up ) {
        up.literal(" ");
        up.fieldRef(fieldRef);
    }
    
    public SootFieldRef getFieldRef() { return fieldRef; }
    public SootField getField() { return fieldRef.resolve(); }
    
    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseFieldPutInst(this);
    }   
    public boolean containsFieldRef() { return true; }
}
