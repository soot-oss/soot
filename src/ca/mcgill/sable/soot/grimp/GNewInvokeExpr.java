/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Grimp, an aggregated-expression Java(TM) bytecode representation. *
 * Copyright (C) 1998 Patrick Lam (plam@sable.mcgill.ca)             *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Raja Vallee-Rai (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Raja Vallee-Rai.  All rights reserved.             *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
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

 - Modified on March 1, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Added methods to manipulate the base type.
   Changed class to inherit from AbstractStaticInvokeExpr.
   
   
 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca). (*)
   First release of Grimp.
*/

package ca.mcgill.sable.soot.grimp;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;

class GNewInvokeExpr extends AbstractStaticInvokeExpr
    implements NewInvokeExpr, Precedence
{
    RefType type;

    GNewInvokeExpr(RefType type, SootMethod method, List args)
    {
            super(method, new ExprBox[args.size()]);

        this.type = type;
        
        for(int i = 0; i < args.size(); i++)
            this.argBoxes[i] = Grimp.v().newExprBox((Value) args.get(i));
    }

    /*
    protected GNewInvokeExpr(RefType type, ExprBox[] argBoxes)
    {
        this.type = type;
        this.argBoxes = argBoxes;
    }
    */
    
    public RefType getBaseType()
    {
        return type;
    }
    
    public void setBaseType(RefType type)
    {
        this.type = type;
    }

    public Type getType()
    {
        return type;
    }    
    
    public int getPrecedence() { return 850; }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("new " + type.toString() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(argBoxes[i].getValue().toString());
        }

        buffer.append(")");

        return buffer.toString();
    }


    public String toBriefString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("new " + 
                      ((ToBriefString) type).toBriefString() + "(");

        for(int i = 0; i < argBoxes.length; i++)
        {
            if(i != 0)
                buffer.append(", ");

            buffer.append(((ToBriefString) argBoxes[i].getValue()).toBriefString());
        }

        buffer.append(")");

        return buffer.toString();
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        for(int i = 0; i < argBoxes.length; i++)
        {
            list.addAll(argBoxes[i].getValue().getUseBoxes());
            list.add(argBoxes[i]);
        }
        
        return list;
    }

    public void apply(Switch sw)
    {
        ((ExprSwitch) sw).caseNewInvokeExpr(this);
    }
}
