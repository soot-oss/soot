/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee
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

package soot.toolkits.scalar;

import soot.*;

/**
 * Utility class used to package a Value and a Unit together.
 * 
 * @author Navindra Umanee
 **/
public class ValueUnitPair extends AbstractValueBox implements ValueBox, UnitBox
{
    protected OurUnitBox oub;
        
    /**
     * Constructs a ValueUnitPair from a Unit object and a Value object.
     * 
     * @param value some Value
     * @param unit some Unit.
     **/
    public ValueUnitPair(Value value, Unit unit)
    {
        setValue(value);
        oub = new OurUnitBox(unit);
    }

    public boolean canContainValue(Value value)
    {
	return true;
    }

    public void setUnit(Unit u)
    {
        oub.setUnit(u);
    }

    public Unit getUnit()
    {
        return oub.getUnit();
    }
        
    public boolean canContainUnit(Unit u)
    {
        return oub.canContainUnit(u);
    }

    public boolean isBranchTarget()
    {
        return oub.isBranchTarget();
    }
    
    public void setBranchTarget(boolean branchTarget)
    {
        oub.setBranchTarget(branchTarget);
    }

    public String toString()
    {
        return "Value = " + getValue() + ", Unit = " + getUnit();
    }

    public void toString(UnitPrinter up) 
    {
        super.toString(up);
        if(isBranchTarget())
            up.literal(", ");
        else
            up.literal(" #");
        oub.toString(up);
    }
    
    public int hashCode()
    {
        return getValue().hashCode() * 101 + getUnit().hashCode() + 17;
    }

    /**
     * Two ValueUnitPairs are equal iff they hold the same
     * Unit objects and the same Value objects within them.
     *
     * @param other another ValueUnitPair
     * @return true if other contains the same objects as this.
     **/
    public boolean equals(Object other)
    {
        if(other instanceof ValueUnitPair &&
           ((ValueUnitPair) other).getValue() == this.getValue() &&
           ((ValueUnitPair) other).getUnit() == getUnit())
            return true;

            return false;
    }
}

class OurUnitBox extends AbstractUnitBox
{
    public OurUnitBox(Unit u)
    {
        setUnit(u);
    }
    
    public boolean canContainUnit(Unit u)
    {
        return true;
    }    
}
