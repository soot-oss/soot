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
import soot.util.*;
import java.util.*;

public abstract class AbstractBranchInst extends AbstractInst
{
    UnitBox targetBox;

    List<UnitBox> targetBoxes;

    AbstractBranchInst(UnitBox targetBox)
    {
        this.targetBox = targetBox;

        targetBoxes = Collections.<UnitBox>singletonList(this.targetBox);
    }

    abstract public String getName();

    public String toString()
    {
		String target = ""; 
		Unit targetUnit = getTarget();
		if (this == targetUnit)
		  target = getName();
		else
		  target = getTarget().toString();
		return getName() + " " + target;	    	
    }

    public void toString( UnitPrinter up ) {
        up.literal( getName() );
        up.literal(" ");
        targetBox.toString( up );
    }
    
    public Unit getTarget()
    {
        return targetBox.getUnit();
    }

    public void setTarget(Unit target)
    {
        targetBox.setUnit(target);
    }

    public UnitBox getTargetBox()
    {
        return targetBox;
    }

    public List<UnitBox> getUnitBoxes()
    {
        return targetBoxes;
    }

    abstract public void apply(Switch sw);

    
    public boolean branches()
    {
        return true;
    }
    

}

