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

public class DoubleConstant extends RealConstant
{
    public final double value;

    private DoubleConstant(double value)
    {
        this.value = value;
    }

    public static DoubleConstant v(double value)
    {
        return new DoubleConstant(value);
    }

    public boolean equals(Object c)
    {
        return (c instanceof DoubleConstant && ((DoubleConstant) c).value == this.value);
    }

    /** Returns a hash code for this DoubleConstant object. */
    public int hashCode()
    {
        long v = Double.doubleToLongBits(value);
        return (int)(v^(v>>>32));
    }

    // PTC 1999/06/28
    public NumericConstant add(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return DoubleConstant.v(this.value + ((DoubleConstant)c).value);
    }

    public NumericConstant subtract(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return DoubleConstant.v(this.value - ((DoubleConstant)c).value);
    }

    public NumericConstant multiply(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return DoubleConstant.v(this.value * ((DoubleConstant)c).value);
    }

    public NumericConstant divide(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return DoubleConstant.v(this.value / ((DoubleConstant)c).value);
    }

    public NumericConstant remainder(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return DoubleConstant.v(this.value % ((DoubleConstant)c).value);
    }

    public NumericConstant equalEqual(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return IntConstant.v((this.value == ((DoubleConstant)c).value) ? 1 : 0);
    }

    public NumericConstant notEqual(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return IntConstant.v((this.value != ((DoubleConstant)c).value) ? 1 : 0);
    }

    public NumericConstant lessThan(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return IntConstant.v((this.value < ((DoubleConstant)c).value) ? 1 : 0);
    }

    public NumericConstant lessThanOrEqual(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return IntConstant.v((this.value <= ((DoubleConstant)c).value) ? 1 : 0);
    }

    public NumericConstant greaterThan(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return IntConstant.v((this.value > ((DoubleConstant)c).value) ? 1 : 0);
    }

    public NumericConstant greaterThanOrEqual(NumericConstant c)
    {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        return IntConstant.v((this.value >= ((DoubleConstant)c).value) ? 1 : 0);
    }

    public IntConstant cmpg(RealConstant c) {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        double cValue = ((DoubleConstant) c).value;
        if (this.value < cValue)
            return IntConstant.v(-1);
        else if (this.value == cValue)
            return IntConstant.v(0);
        else /* this or c could be NaN */
            return IntConstant.v(1);
    }
    
    public IntConstant cmpl(RealConstant c) {
        if (!(c instanceof DoubleConstant))
            throw new IllegalArgumentException("DoubleConstant expected");
        double cValue = ((DoubleConstant) c).value;
        if (this.value > cValue)
            return IntConstant.v(1);
        else if (this.value == cValue)
            return IntConstant.v(0);
        else /* this or c could be NaN */
            return IntConstant.v(-1);
    }
    
    public NumericConstant negate()
    {
        return DoubleConstant.v(-(this.value));
    }

    public String toString()
    {
        String doubleString = new Double(value).toString();
        
        if(doubleString.equals("NaN") || 
            doubleString.equals("Infinity") ||
            doubleString.equals("-Infinity"))
            return "#" + doubleString;
        else
            return doubleString;
    }
    
    public Type getType()
    {
        return DoubleType.v();
    }

    public void apply(Switch sw)
    {
        ((ConstantSwitch) sw).caseDoubleConstant(this);
    }
}
