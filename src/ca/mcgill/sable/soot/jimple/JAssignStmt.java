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

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class JAssignStmt extends AbstractDefinitionStmt
    implements AssignStmt
{
    JAssignStmt(Value variable, Value rvalue)
    {
        this(Jimple.v().newVariableBox(variable),
             Jimple.v().newRValueBox(rvalue));
    }

    protected JAssignStmt(ValueBox variableBox, ValueBox rvalueBox)
    {
        this.leftBox = variableBox;
        this.rightBox = rvalueBox;

        defBoxes = new ArrayList();
        defBoxes.add(leftBox);
        defBoxes = Collections.unmodifiableList(defBoxes);
    }

    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        if(isBrief)
        {
            return indentation + ((ToBriefString) leftBox.getValue()).toBriefString() + " = " + 
                ((ToBriefString) rightBox.getValue()).toBriefString();
        }
        else
            return indentation + leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
    }

    public Object clone() 
    {
            return new JAssignStmt(Jimple.cloneIfNecessary(getLeftOp()), Jimple.cloneIfNecessary(getRightOp()));
    }

    public void setLeftOp(Value variable)
    {
        leftBox.setValue(variable);
    }

    public void setRightOp(Value rvalue)
    {
        rightBox.setValue(rvalue);
    }

    public void apply(Switch sw)
    {
        ((StmtSwitch) sw).caseAssignStmt(this);
    }

    public void convertToBaf(final JimpleToBafContext context, final List out)
    {
        final Value lvalue = this.getLeftOp();
        final Value rvalue = this.getRightOp();

        /*
        // Handle simple subcase where you can use the efficient iinc bytecode
            if(lvalue instanceof Local && (rvalue instanceof AddExpr || rvalue instanceof SubExpr))
            {
                Local l = (Local) lvalue;
                BinopExpr expr = (BinopExpr) rvalue;
                Value op1 = expr.getOp1();
                Value op2 = expr.getOp2();
                                
                if(l.getType().equals(IntType.v()))
                {
                    boolean isValidCase = false;
                    int x = 0;
                    
                    if(op1 == l && op2 instanceof IntConstant) 
                    {
                        x = ((IntConstant) op2).value;
                        isValidCase = true;
                    }
                    else if(expr instanceof AddExpr && 
                        op2 == l && op1 instanceof IntConstant)
                    {
                        // Note expr can't be a SubExpr because that would be x = 3 - x
                        
                        x = ((IntConstant) op1).value;
                        isValidCase = true;
                    }
                    
                    if(isValidCase && x >= Short.MIN_VALUE && x <= Short.MAX_VALUE)
                    {
                        throw new RuntimeException("missing shortcut for iinc");
                        
                        emit("iinc " + ((Integer) localToSlot.get(l)).intValue() + " " +  
                            ((expr instanceof AddExpr) ? x : -x), 0);
                        return;
                    }        
                }
            }
*/

            lvalue.apply(new AbstractJimpleValueSwitch()
            {
                public void caseArrayRef(ArrayRef v)
                {
                    ((ConvertToBaf)(v.getBase())).convertToBaf(context, out);
                    ((ConvertToBaf)(v.getIndex())).convertToBaf(context, out);
                    ((ConvertToBaf) rvalue).convertToBaf(context, out);
                    
                    out.add(Baf.v().newArrayWriteInst(v.getType()));
                }
                
                public void defaultCase(Value v)
                {
                    throw new RuntimeException("Can't store in value " + v);
                }
                
                public void caseInstanceFieldRef(InstanceFieldRef v)
                {
                    ((ConvertToBaf)(v.getBase())).convertToBaf(context, out);
                    ((ConvertToBaf) rvalue).convertToBaf(context, out);

                    out.add(Baf.v().newFieldPutInst(v.getField()));
                }
                
                public void caseLocal(final Local v)
                {
                    ((ConvertToBaf) rvalue).convertToBaf(context, out);
                    
                    out.add(Baf.v().newStoreInst(v.getType(), 
                        context.getBafLocalOfJimpleLocal(v)));
                }
                
                public void caseStaticFieldRef(StaticFieldRef v)
                {
                    ((ConvertToBaf) rvalue).convertToBaf(context, out);

                    out.add(Baf.v().newStaticPutInst(v.getField()));
                }
            }); 
    }    
}




