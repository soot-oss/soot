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

/** <code>ParameterRef</code> objects are used by <code>Body</code>
 * objects to refer to the parameter slots on method entry. <br>
 *
 * For instance, in an instance method, the first statement will
 * often be <code> this := @parameter0; </code> */
public class ParameterRef implements IdentityRef
{
    int n;
    Type paramType;

    /** Constructs a ParameterRef object of the specified type, representing the specified parameter number. */
    public ParameterRef(Type paramType, int number)
    {
        this.n = number;
        this.paramType = paramType;
    }

    public boolean equivTo(Object o)
    {
        if (o instanceof ParameterRef)
        {
            return n == ((ParameterRef)o).n &&
                paramType.equals(((ParameterRef)o).paramType);
        }
        return false;
    }

    public int equivHashCode()
    {
        return n * 101 + paramType.hashCode() * 17;
    }
    
    /** Create a new ParameterRef object with the same paramType and number. */
    public Object clone() 
    {
        return new ParameterRef(paramType, n);
    }
    
    /** Converts the given ParameterRef into a String i.e. <code>@parameter0: .int</code>. */
    public String toString()
    {
        return "@parameter" + n + ": " + paramType;                                                   
    }
    
    public void toString( UnitPrinter up )
    {
        up.identityRef(this);
    }

    /** Returns the index of this ParameterRef. */
    public int getIndex()
    {
        return n;
    }

    /** Sets the index of this ParameterRef. */
    public void setIndex(int index)
    {
        n = index;
    }

    public List getUseBoxes()
    {
        return AbstractUnit.emptyList;
    }

    /** Returns the type of this ParameterRef. */
    public Type getType()
    {
        return paramType;
    }

    /** Used with RefSwitch. */
    public void apply(Switch sw)
    {
        ((RefSwitch) sw).caseParameterRef(this);
    }
}
