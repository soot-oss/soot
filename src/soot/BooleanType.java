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


/**
 *   Soot representation of the Java built-in type 'boolean'. Implemented as
 *   a singleton.
 */
public class BooleanType extends BaseType implements IntegerType
{
    private static final BooleanType constant = new BooleanType();

    private BooleanType()
    {
    }

    /** @return this class's singleton object */
    public static BooleanType v()
    {
        return constant;
    }

    public boolean equals(Object t)
    {
        return this == t;
    }

    public int hashCode()
    {
        return 0x1C4585DA;
    }
    
    public String toString()
    {
        return "boolean";
    }

    public void apply(Switch sw)
    {
        ((TypeSwitch) sw).caseBooleanType(this);
    }
}
