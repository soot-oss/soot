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
 *   Utility class used to package a Value and a Unit together.
 */
public class ValueUnitPair extends AbstractUnitBox implements ValueBox, UnitBox
{
    protected Value value;

    /**
     *  Constructs a ValueUnitPair from a Unit object and a Value object.
     *  @param value some Value
     *  @param unit some Unit.
     */
    public ValueUnitPair(Value value, Unit unit)
    {
        this.value = value;
        this.unit = unit;
    }

    /**
     *   Two ValueUnitPairs are equal iff they hold the same
     *   Unit objects and the same Value objects within them.
     *
     *   @param other another ValueUnitPair
     *   @return true if other contains the same objects as this.
     */
    public boolean equals(Object other)
    {
        if(other instanceof ValueUnitPair &&
            ((ValueUnitPair) other).value == this.value &&
            ((ValueUnitPair) other).unit == this.unit)
            return true;
        else
            return false;
    }

    public void setValue(Value value)
    {
        this.value = value;
    }
        
    public Value getValue()
    {
        return value;
    }

    public boolean canContainValue(Value value)
    {
	return true;
    }

    public boolean canContainUnit(Unit u)
    {
        return true;
    }
    
    public String toString()
    {
        return "Value = " + value + ", Unit = " + unit;
    }
    
    public int hashCode()
    {
        return value.hashCode() * 101 + unit.hashCode() + 17;
    }
}
