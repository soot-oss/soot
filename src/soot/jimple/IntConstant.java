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

public class IntConstant extends ArithmeticConstant
{
    public final int value;

    protected IntConstant(int value)
    {
        this.value = value;
    }

    public static IntConstant v(int value)
    {
        return new IntConstant(value);
    }

    public boolean equals(Object c)
    {
        return c instanceof IntConstant && ((IntConstant) c).value == value;
    }

    public int hashCode()
    {
        return value;
    }

    // PTC 1999/06/28
    public NumericConstant add(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value + ((IntConstant)c).value);
    }

    public NumericConstant subtract(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value - ((IntConstant)c).value);
    }

    public NumericConstant multiply(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value * ((IntConstant)c).value);
    }

    public NumericConstant divide(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value / ((IntConstant)c).value);
    }

    public NumericConstant remainder(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value % ((IntConstant)c).value);
    }

    public NumericConstant equalEqual(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v((this.value == ((IntConstant)c).value) ? 1 : 0);
    }

    public NumericConstant notEqual(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v((this.value != ((IntConstant)c).value) ? 1 : 0);
    }

    public NumericConstant lessThan(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v((this.value < ((IntConstant)c).value) ? 1 : 0);
    }

    public NumericConstant lessThanOrEqual(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v((this.value <= ((IntConstant)c).value) ? 1 : 0);
    }

    public NumericConstant greaterThan(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v((this.value > ((IntConstant)c).value) ? 1 : 0);
    }

    public NumericConstant greaterThanOrEqual(NumericConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v((this.value >= ((IntConstant)c).value) ? 1 : 0);
    }

    public NumericConstant negate()
    {
        return IntConstant.v(-(this.value));
    }

    public ArithmeticConstant and(ArithmeticConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value & ((IntConstant)c).value);
    }

    public ArithmeticConstant or(ArithmeticConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value | ((IntConstant)c).value);
    }

    public ArithmeticConstant xor(ArithmeticConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value ^ ((IntConstant)c).value);
    }

    public ArithmeticConstant shiftLeft(ArithmeticConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value << ((IntConstant)c).value);
    }

    public ArithmeticConstant shiftRight(ArithmeticConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value >> ((IntConstant)c).value);
    }

    public ArithmeticConstant unsignedShiftRight(ArithmeticConstant c)
    {
        if (!(c instanceof IntConstant))
            throw new IllegalArgumentException("IntConstant expected");
        return IntConstant.v(this.value >>> ((IntConstant)c).value);
    }

    public String toString()
    {
        return new Integer(value).toString();
    }

    public Type getType()
    {
        return IntType.v();
    }

    public void apply(Switch sw)
    {
        ((ConstantSwitch) sw).caseIntConstant(this);
    }
    
}
