/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

/* This program is designed by Patrice Pominville and Feng Qian
 */

package soot.tagkit;

import soot.*;


import java.util.*;
import javax.swing.*;


/** Represents an inner class attribute which can be attatched to
 * implementations of Host. It can be directly used to add
 * attributes to class files
 *
 */
public class InnerClassAttribute implements Tag
{
    private ArrayList list;
    
    public InnerClassAttribute(ArrayList list)
    {
	    this.list = list;
    }

    public String getClassSpecs(){
        StringBuffer sb = new StringBuffer();
        Iterator it = list.iterator();
        while (it.hasNext()){
            InnerClassTag ict = (InnerClassTag)it.next();
            sb.append(".inner_class_spec_attr ");
            sb.append(ict.getInnerClass());
            sb.append(" ");
            sb.append(ict.getOuterClass());
            sb.append(" ");
            sb.append(ict.getShortName());
            sb.append(" ");
            sb.append(ict.getAccessFlags());
            sb.append(" ");
            sb.append(".end .inner_class_spec_attr ");    
        }
        return sb.toString();
    }

    public String getName(){
        return "InnerClassAttribute";
    }

    public byte[] getValue() throws AttributeValueException{
        return new byte[1];
    }

    public ArrayList getSpecs(){
        return list;
    }
}
