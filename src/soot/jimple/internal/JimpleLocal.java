/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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






package soot.jimple.internal;

import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class JimpleLocal implements Local, ConvertToBaf
{
    String name;
    Type type;

    int fixedHashCode;
    boolean isHashCodeChosen;
        
    public JimpleLocal(String name, Type t)
    {
        this.name = name;
        this.type = t;
    }

    public Object clone()
    {
        return new JimpleLocal(name, type);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int hashCode()
    {
        if(!isHashCodeChosen)
        {
            // Set the hash code for this object
            
            if(name != null & type != null)
                fixedHashCode = name.hashCode() + 19 * type.hashCode();
            else if(name != null)
                fixedHashCode = name.hashCode();
            else if(type != null)
                fixedHashCode = type.hashCode();
            else
                fixedHashCode = 1;
                
            isHashCodeChosen = true;
        }
        
        return fixedHashCode;
    }
    
    public Type getType()
    {
        return type;
    }

    public void setType(Type t)
    {
        this.type = t;
    }

    public String toString()
    {
        return getName();
    }

    public String toBriefString()
    {
        return toString();
    }
    
    public List getUseBoxes()
    {
        return AbstractUnit.emptyList;
    }

    public void apply(Switch sw)
    {
        ((JimpleValueSwitch) sw).caseLocal(this);
    }

    public void convertToBaf(JimpleToBafContext context, List out)
    {
        out.add(Baf.v().newLoadInst(getType(), 
            context.getBafLocalOfJimpleLocal(this)));
    }
}

