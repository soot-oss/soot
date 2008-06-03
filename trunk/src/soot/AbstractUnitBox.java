/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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





package soot;

/** Reference implementation for UnitBox; just
 * add a canContainUnit method. */
public abstract class AbstractUnitBox implements UnitBox
{
    protected Unit unit;

    public abstract boolean canContainUnit(Unit u);

    public boolean isBranchTarget()
    {
        return true;
    }
    
    public void setUnit(Unit unit)
    {
        if(!canContainUnit(unit))
            throw new RuntimeException("attempting to put invalid unit in UnitBox");
            
        // Remove this from set of back pointers.
            if(this.unit != null)
            {
                this.unit.removeBoxPointingToThis(this);
            }

        // Perform link
            this.unit = unit;

        // Add this to back pointers
            if(this.unit != null)
            {
                this.unit.addBoxPointingToThis(this);
            }
    }

    public Unit getUnit()
    {
        return unit;
    }

    public void toString(UnitPrinter up) {
        up.startUnitBox(this);
        up.unitRef(unit, isBranchTarget());
        up.endUnitBox(this);
    }
}
