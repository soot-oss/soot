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

import soot.tagkit.*;

/** Reference implementation for ValueBox; just
 * add a canContainValue method. */
public abstract class AbstractValueBox extends AbstractHost implements ValueBox
{
    Value value;

    public void setValue(Value value)
    {
        if(canContainValue(value))
            this.value = value;
        else
            throw new RuntimeException("Box " + this + " cannot contain value: " + value + "(" + value.getClass() + ")" );
    }

    public Value getValue()
    {
        return value;
    }
    
    public void toString( UnitPrinter up ) {
        up.startValueBox(this);
        value.toString(up);
        up.endValueBox(this);
    }
    public String toString() { return "VB("+value+")"; }
}





