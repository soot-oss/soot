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





package soot.jimple;

import soot.*;
import soot.util.*;
import java.util.*;

public class ParameterRef implements IdentityRef, Switchable, ToBriefString
{
    int n;
    Type paramType;

    public ParameterRef(Type paramType, int number)
    {
        this.n = number;
        this.paramType = paramType;
    }
    
    public Object clone() 
    {
        return new ParameterRef(paramType, n);
    }
    
    public String toString()
    {
      if(Jimple.isJavaKeywordType(paramType))
        return "@parameter" + n + ": " + "." + paramType;
      else
	return "@parameter" + n + ": " + paramType;
							   
    }

    public String toBriefString()
    {
        return "@parameter" + n;
    }
    
    public int getIndex()
    {
        return n;
    }

    public void setIndex(int index)
    {
        n = index;
    }

    public List getUseBoxes()
    {
        return AbstractUnit.emptyList;
    }

    public Type getType()
    {
        return paramType;
    }

    public void apply(Switch sw)
    {
        ((RefSwitch) sw).caseParameterRef(this);
    }
}
