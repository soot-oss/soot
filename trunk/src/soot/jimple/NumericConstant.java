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

package soot.jimple;

public abstract class NumericConstant extends Constant
{
    // PTC 1999/06/28
    public abstract NumericConstant add(NumericConstant c);

    public abstract NumericConstant subtract(NumericConstant c);

    public abstract NumericConstant multiply(NumericConstant c);

    public abstract NumericConstant divide(NumericConstant c);

    public abstract NumericConstant remainder(NumericConstant c);

    public abstract NumericConstant equalEqual(NumericConstant c);

    public abstract NumericConstant notEqual(NumericConstant c);

    public abstract NumericConstant lessThan(NumericConstant c);

    public abstract NumericConstant lessThanOrEqual(NumericConstant c);

    public abstract NumericConstant greaterThan(NumericConstant c);

    public abstract NumericConstant greaterThanOrEqual(NumericConstant c);

    public abstract NumericConstant negate();
}
