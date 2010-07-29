/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 - Jennifer Lhotak
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

public class ClassConstant extends Constant
{
    public final String value;

    private ClassConstant(String s)
    {
        this.value = s;
    }

    public static ClassConstant v(String value)
    {
    	if(value.contains(".")) throw new RuntimeException("ClassConstants must use class names separated by '/', not '.'!");
        return new ClassConstant(value);
    }

    // In this case, equals should be structural equality.
    public boolean equals(Object c)
    {
        return (c instanceof ClassConstant && ((ClassConstant) c).value.equals(this.value));
    }

    /** Returns a hash code for this ClassConstant object. */
    public int hashCode()
    {
        return value.hashCode();
    }

    public String toString()
    {
        return "class "+StringTools.getQuotedStringOf(value);
    }

    public String getValue(){
        return value;
    }
    
    public Type getType()
    {
        return RefType.v("java.lang.Class");
    }

    public void apply(Switch sw)
    {
        ((ConstantSwitch) sw).caseClassConstant(this);
    }
}
