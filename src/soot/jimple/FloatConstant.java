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

import soot.*;
import soot.util.*;
import java.util.*;

public class FloatConstant extends RealConstant
{
    public final float value;

    private FloatConstant(float value)
    {
        this.value = value;
    }

    public static FloatConstant v(float value)
    {
        return new FloatConstant(value);
    }

    public boolean equals(Object c)
    {
        return c instanceof FloatConstant && ((FloatConstant) c).value == value;
    }

    /** Returns a hash code for this FloatConstant object. */
    public int hashCode()
    {
        return Float.floatToIntBits(value);
    }

    // PTC 1999/06/28
     public NumericConstant add(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return FloatConstant.v(this.value + ((FloatConstant)c).value);
    }

    public NumericConstant subtract(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return FloatConstant.v(this.value - ((FloatConstant)c).value);
    }

    public NumericConstant multiply(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return FloatConstant.v(this.value * ((FloatConstant)c).value);
    }

    public NumericConstant divide(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return FloatConstant.v(this.value / ((FloatConstant)c).value);
    }

    public NumericConstant remainder(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return FloatConstant.v(this.value % ((FloatConstant)c).value);
    }

    public NumericConstant equalEqual(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return IntConstant.v((this.value == ((FloatConstant)c).value) ? 1 : 0);
    }

    public NumericConstant notEqual(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return IntConstant.v((this.value != ((FloatConstant)c).value) ? 1 : 0);
    }

    public NumericConstant lessThan(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return IntConstant.v((this.value < ((FloatConstant)c).value) ? 1 : 0);
    }

    public NumericConstant lessThanOrEqual(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return IntConstant.v((this.value <= ((FloatConstant)c).value) ? 1 : 0);
    }

    public NumericConstant greaterThan(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return IntConstant.v((this.value > ((FloatConstant)c).value) ? 1 : 0);
    }

    public NumericConstant greaterThanOrEqual(NumericConstant c)
    {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        return IntConstant.v((this.value >= ((FloatConstant)c).value) ? 1 : 0);
    }

    public IntConstant cmpg(RealConstant c) {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        float cValue = ((FloatConstant) c).value;
        if (this.value < cValue)
            return IntConstant.v(-1);
        else if (this.value == cValue)
            return IntConstant.v(0);
        else /* this or c could be NaN */
            return IntConstant.v(1);
    }
    
    public IntConstant cmpl(RealConstant c) {
        if (!(c instanceof FloatConstant))
            throw new IllegalArgumentException("FloatConstant expected");
        float cValue = ((FloatConstant) c).value;
        if (this.value > cValue)
            return IntConstant.v(1);
        else if (this.value == cValue)
            return IntConstant.v(0);
        else /* this or c could be NaN */
            return IntConstant.v(-1);
    }
    
    public NumericConstant negate()
    {
        return FloatConstant.v(-(this.value));
    }

    public String toString()
    {
        String floatString = new Float(value).toString();
        
        if(floatString.equals("NaN") || 
            floatString.equals("Infinity") ||
            floatString.equals("-Infinity"))
            return "#" + floatString + "F";
        else
            return floatString + "F";
    }

    public Type getType()
    {
        return FloatType.v();
    }

    public void apply(Switch sw)
    {
        ((ConstantSwitch) sw).caseFloatConstant(this);
    }
}
