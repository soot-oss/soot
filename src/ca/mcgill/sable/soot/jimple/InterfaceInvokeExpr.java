/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $JimpleVersion: 0.5 $

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on September 22, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Changed the base from Immediate to Local.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;

public class InterfaceInvokeExpr extends InvokeExpr
{
    LocalBox baseBox;
    
    public InterfaceInvokeExpr(Local base, Method method, List args)
    {
        this.baseBox = new LocalBox(base);
        this.method = method;
        
        this.argBoxes = (ImmediateBox[]) new ImmediateBox[args.size()];
        
        for(int i = 0; i < args.size(); i++)
            this.argBoxes[i] = new ImmediateBox((Immediate) args.get(i));
    }
    
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("interfaceinvoke " + baseBox.getValue().toString() + 
            ".[" + method.getSignature() + "](");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");
                
            buffer.append(argBoxes[i].getValue().toString());
        }
            
        buffer.append(")");
        
        return buffer.toString();
    }
    
    public Local getBase()
    {
        return (Local) baseBox.getValue();
    }
    
    public LocalBox getBaseBox()
    {
        return baseBox;
    }
    
    public void setBase(Local base)
    {
        baseBox.setValue(base);
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();
            
        list.add(baseBox);
        
        for(int i = 0; i < argBoxes.length; i++)
            list.add(argBoxes[i]);
    
        // Add the boxes within the boxes
        {        
            list.addAll(baseBox.getValue().getUseBoxes());
            
            for(int i = 0; i < argBoxes.length; i++)
                list.addAll(argBoxes[i].getValue().getUseBoxes());
        }
        
        return list;
    }    
    
    public Type getType()
    {
        return method.getReturnType();
    }
    
    public void apply(Switch sw)
    {
        ((ValueSwitch) sw).caseInterfaceInvokeExpr(this);
    }
}


