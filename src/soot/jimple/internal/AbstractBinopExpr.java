/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
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

 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package soot.jimple.internal;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public abstract class AbstractBinopExpr implements Expr, ToBriefString
{
    protected ValueBox op1Box;
    protected ValueBox op2Box;

    public Value getOp1()
    {
        return op1Box.getValue();
    }

    public Value getOp2()
    {
        return op2Box.getValue();
    }

    public ValueBox getOp1Box()
    {
        return op1Box;
    }

    public ValueBox getOp2Box()
    {
        return op2Box;
    }

    public void setOp1(Value op1)
    {
        op1Box.setValue(op1);
    }

    public void setOp2(Value op2)
    {
        op2Box.setValue(op2);
    }

    public List getUseBoxes()
    {
        List list = new ArrayList();

        list.addAll(op1Box.getValue().getUseBoxes());
        list.add(op1Box);
        list.addAll(op2Box.getValue().getUseBoxes());
        list.add(op2Box);

        return list;
    }

    abstract protected String getSymbol();
    abstract public Object clone();

    public String toString()
    {
        Value op1 = op1Box.getValue(), op2 = op2Box.getValue();
        String leftOp = op1.toString(), rightOp = op2.toString();

        return leftOp + getSymbol() + rightOp;
    }

    public String toBriefString()
    {
        Value op1 = op1Box.getValue(), op2 = op2Box.getValue();
        String leftOp = ((ToBriefString)op1).toBriefString(), 
            rightOp = ((ToBriefString)op2).toBriefString();

        return leftOp + getSymbol() + rightOp;
    }
}
