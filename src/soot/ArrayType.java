/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot;

import soot.util.*;
import java.util.*;

public class ArrayType extends Type
{
    /**
     * baseType can be any type except for an array type and void
     */

    public final BaseType baseType;
    public final int numDimensions;

    private ArrayType(BaseType baseType, int numDimensions)
    {
        this.baseType = baseType;
        this.numDimensions = numDimensions;
    }

    public static ArrayType v(BaseType baseType, int numDimensions)
    {
        return new ArrayType(baseType, numDimensions);
    }

    public boolean equals(Object t)
    {
        if(t instanceof ArrayType)
        {
            ArrayType arrayType = (ArrayType) t;

            return this.numDimensions == arrayType.numDimensions &&
                this.baseType.equals(arrayType.baseType);
        }
        else
            return false;
    }

    public String toBriefString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(baseType.toBriefString());

        for(int i = 0; i < numDimensions; i++)
            buffer.append("[]");

        return buffer.toString();
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append(baseType.toString());

        for(int i = 0; i < numDimensions; i++)
            buffer.append("[]");

        return buffer.toString();
    }

    public int hashCode()
    {
        return baseType.hashCode() + 0x432E0341 * numDimensions;
    }

    public void apply(Switch sw)
    {
        ((TypeSwitch) sw).caseArrayType(this);
    }
}

