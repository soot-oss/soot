/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

package soot.toolkits.scalar;

import soot.*;
import soot.jimple.Jimple;

/**
 * Utility class used to package a Value and a Unit together.
 * 
 * @author Navindra Umanee
 **/
public class ValueUnitPair extends AbstractValueBox implements UnitBox
{
    protected UnitBox oub;
        
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

    /**
     * @see soot.UnitBox#setUnit(Unit)
     **/
    public void setUnit(Unit u)
    {
        Unit oldU = getUnit();
        oub.setUnit(u);
    }

    /**
     * @see soot.UnitBox#getUnit()
     **/
    public Unit getUnit()
    {
        return oub.getUnit();
    }

    /**
     * @see soot.UnitBox#canContainUnit(Unit)
     **/
    public boolean canContainUnit(Unit u)
    {
        return oub.canContainUnit(u);
    }

    /**
     * @see soot.UnitBox#isBranchTarget()
     **/
    public boolean isBranchTarget()
    {
        return oub.isBranchTarget();
    }

    /**
     * @see soot.UnitBox#setBranchTarget(boolean)
     **/
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
        // If you need to change this implementation, please change it
        // in a subclass.  Otherwise, Shimple is likely to break in evil
        // ways.
        return super.hashCode();
    }

    public boolean equals(Object other)
    {
        // If you need to change this implementation, please change it
        // in a subclass.  Otherwise, Shimple is likely to break in evil
        // ways.
        return super.equals(other);
    }
    
    /**
     * Two ValueUnitPairs are equivTo iff they hold the same
     * Unit objects and the same Value objects within them.
     *
     * @param other another ValueUnitPair
     * @return true if other contains the same objects as this.
     **/
    public boolean equivTo(Object other)
    {
        if(other instanceof ValueUnitPair &&
           ((ValueUnitPair) other).getValue() == this.getValue() &&
           ((ValueUnitPair) other).getUnit() == getUnit())
            return true;

            return false;
    }

    public Object clone()
    {
        // Note to self: Do not try to "fix" this.  Yes, it should be
        // a shallow copy in order to conform with the rest of Soot.
        // When a body is cloned, the Values are cloned explicitly and
        // replaced, and UnitBoxes are explicitly patched.  See
        // Body.importBodyContentsFrom for details.
        Value cv = Jimple.cloneIfNecessary((Value) getValue());
        Unit cu = (Unit) getUnit();
        return new ValueUnitPair(cv, cu);
    }

    private class OurUnitBox extends AbstractUnitBox
    {
        public OurUnitBox(Unit u)
        {
            setUnit(u);
        }
        
        // extreme nastiness.  we do *not* want something to point
        // directly to oub.  unfortunately oub does some ugly stuff
        // internally, and now so do we.
        public void setUnit(Unit unit)
        {
            if(!canContainUnit(unit))
                throw new RuntimeException("Attempting to put invalid unit in UnitBox.");
            
            if(this.unit != null)
                this.unit.removeBoxPointingToThis(ValueUnitPair.this);

            this.unit = unit;

            if(this.unit != null)
                this.unit.addBoxPointingToThis(ValueUnitPair.this);
        }
        
        public boolean canContainUnit(Unit u)
        {
            return true;
        }
    }
}
