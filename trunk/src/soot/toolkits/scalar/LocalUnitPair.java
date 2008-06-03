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






package soot.toolkits.scalar;

import soot.*;


/**
 *   Utility class used to package a Local and a Unit together.
 */
public class LocalUnitPair
{
    Local local;
    Unit unit;

    /**
     *  Constructs a LocalUnitPair from a Unit object and a Local object.
     *  @param local some Local
     *  @param unit some Unit.
     */
    public LocalUnitPair(Local local, Unit unit)
    {
        this.local = local;
        this.unit = unit;
    }

    /**
     *   Two LocalUnitPairs are equal iff they hold the same Unit objects and
     *   the same Local objects within them.
     *   @param other another LocalUnitPair
     *   @return true if other contains the same objects as this.
     */
    public boolean equals(Object other)
    {
        if(other instanceof LocalUnitPair &&
            ((LocalUnitPair) other).local == this.local &&
            ((LocalUnitPair) other).unit == this.unit)
        {
            return true;
        }
        else
            return false;
    }

    public int hashCode()
    {
        return local.hashCode() * 101 + unit.hashCode() + 17;
    }
    
    public Local getLocal()
    {
    	return local;
    }
    
    public Unit getUnit()
    {
    	return unit;
    }
}
