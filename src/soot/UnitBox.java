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

import soot.util.*;
import java.util.*;
import java.io.*;

/** A box which can contain units. 
 *
 * @see Unit
 */
public interface UnitBox extends Serializable
{
    /** Sets this box to contain the given unit.  Subject to canContainValue() checks. */
    public void setUnit(Unit u);

    /** Returns the unit contained within this box. */
    public Unit getUnit();

    /** Returns true if this box can contain the given Unit. */
    public boolean canContainUnit(Unit u);   
    
    /**
     * Returns true if the UnitBox is holding a Unit that is the
     * target of a branch (ie a Unit at the beginning of a CFG block).
     * This is the default case.
     *
     * <p> Returns false if the UnitBox is holding a Unit that
     * indicates the end of a CFG block and may require specialised
     * processing for SSA.
     **/
    public boolean isBranchTarget();

    public void toString(UnitPrinter up); 
}
