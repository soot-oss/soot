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

package soot.shimple.internal;

import soot.*;
import soot.toolkits.scalar.ValueUnitPair;

/**
 * Extension of ValueUnitPair that implements SUnitBox.  Needed by
 * SPatchingChain.  Equality is no longer dependent on the value.
 *
 * @author Navindra Umanee
 **/
public class SValueUnitPair extends ValueUnitPair implements SUnitBox
{
    public SValueUnitPair(Value value, Unit unit)
    {
        super(value, unit);
        setUnitChanged(true);
    }

    public boolean isBranchTarget()
    {
        return false;
    }

    public void setUnit(Unit u)
    {
        super.setUnit(u);
        setUnitChanged(true);
    }

    protected boolean unitChanged = false;

    /**
     * @see SUnitBox#isUnitChanged()
     **/
    public boolean isUnitChanged()
    {
        return unitChanged;
    }
    
    /**
     * @see SUnitBox#setUnitChanged(boolean)
     **/
    public void setUnitChanged(boolean unitChanged)
    {
        this.unitChanged = unitChanged;
    }
}
