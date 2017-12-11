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

public class BIdentityInst extends AbstractInst 
    implements IdentityInst
{
    ValueBox leftBox;
    ValueBox rightBox;

    List defBoxes;
    
   
    

    public Value getLeftOp()
    {
        return leftBox.getValue();
    }
  
  public int getInCount()
  {
        return 0;
    }

  public int getInMachineCount()
  {
        return 0;
    }
    
    public int getOutCount()
    {
        return 0;
    }

    public int getOutMachineCount()
    {
        return 0;
    }

    public Value getRightOp()
    {
        return rightBox.getValue();
    }

    public ValueBox getLeftOpBox()
    {
        return leftBox;
    }

    public ValueBox getRightOpBox()
    {
        return rightBox;
    }

    public List getDefBoxes()
    {
        return defBoxes;
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        list.addAll(rightBox.getValue().getUseBoxes());
        list.add(rightBox);
        list.addAll(leftBox.getValue().getUseBoxes());

        return list;
    }

    public BIdentityInst(Value local, Value identityValue)
    {
        this(Baf.v().newLocalBox(local),
             Baf.v().newIdentityRefBox(identityValue));
    }

    protected BIdentityInst(ValueBox localBox, ValueBox identityValueBox)
    {
        this.leftBox = localBox; this.rightBox = identityValueBox;

        defBoxes = Collections.singletonList(leftBox);
    }


    public Object clone() 
    {
            return new BIdentityInst(getLeftOp(), getRightOp());
    }

    public String toString()
    {
        return leftBox.getValue().toString() + " := " + rightBox.getValue().toString();
    }

    public void toString( UnitPrinter up ) {
        leftBox.toString(up);
        up.literal(" := ");
        rightBox.toString(up);
    }

    final public String getName() { return ":="; }

    public void setLeftOp(Value local)
    {
        leftBox.setValue(local);
    }

    public void setRightOp(Value identityRef)
    {
        rightBox.setValue(identityRef);
    }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseIdentityInst(this);
    }    
}



