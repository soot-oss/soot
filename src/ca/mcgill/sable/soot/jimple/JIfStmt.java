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

public class JIfStmt extends AbstractStmt implements IfStmt
{
    ValueBox conditionBox;
    UnitBox targetBox;

    List targetBoxes;

    JIfStmt(Value condition, Unit target)
    {
        this(Jimple.v().newConditionExprBox(condition),
             Jimple.v().newStmtBox(target));
    }

    protected JIfStmt(ValueBox conditionBox, UnitBox targetBox)
    {
        this.conditionBox = conditionBox;
        this.targetBox = targetBox;

        targetBoxes = new ArrayList();
        targetBoxes.add(this.targetBox);
        targetBoxes = Collections.unmodifiableList(targetBoxes);
    }
    
    // xxx
    public Object clone()
    {
	return new JIfStmt(Jimple.cloneIfNecessary(getCondition()), getTarget());
    }
    
    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        if(isBrief)
            return indentation + "if " + 
                ((ToBriefString) getCondition()).toBriefString() + " goto " + (String) stmtToName.get(getTarget());
        else
            return indentation + "if " + getCondition().toString() + " goto " + (String) stmtToName.get(getTarget());
    }
    
    public String toString()
    {
        return "if " + conditionBox.getValue().toString() + " goto ?";
    }

    public Value getCondition()
    {
        return conditionBox.getValue();
    }

    public void setCondition(Value condition)
    {
        conditionBox.setValue(condition);
    }

    public ValueBox getConditionBox()
    {
        return conditionBox;
    }

    public Stmt getTarget()
    {
        return (Stmt) targetBox.getUnit();
    }

    public void setTarget(Unit target)
    {
        targetBox.setUnit(target);
    }

    public UnitBox getTargetBox()
    {
        return targetBox;
    }

    public List getUseBoxes()
    {
        List useBoxes = new ArrayList();

        useBoxes.addAll(conditionBox.getValue().getUseBoxes());
        useBoxes.add(conditionBox);

        return useBoxes;
    }

    public List getUnitBoxes()
    {
        return targetBoxes;
    }

    public void apply(Switch sw)
    {
        ((StmtSwitch) sw).caseIfStmt(this);
    }    

    public void convertToBaf(final JimpleToBafContext context, final List out)
    {
        Value cond = getCondition();

        final Value op1 = ((BinopExpr) cond).getOp1();
        final Value op2 = ((BinopExpr) cond).getOp2();

        // Handle simple subcase where op1 is null
        if(op2 instanceof NullConstant || op1 instanceof NullConstant)
          {
            if(op2 instanceof NullConstant)
              ((ConvertToBaf)op1).convertToBaf(context, out);
            else
              ((ConvertToBaf)op2).convertToBaf(context, out);
                    
            if(cond instanceof EqExpr)
              out.add(Baf.v().newIfNullInst
                      (Baf.v().newPlaceholderInst(getTarget())));
            else if (cond instanceof NeExpr)
              out.add(Baf.v().newIfNonNullInst
                      (Baf.v().newPlaceholderInst(getTarget())));
            else
              throw new RuntimeException("invalid condition");
                    
            return;
          }

        // Handle simple subcase where op2 is 0  
        if(op2 instanceof IntConstant && ((IntConstant) op2).value == 0)
          {
              ((ConvertToBaf)op1).convertToBaf(context, out);
                
              cond.apply(new AbstractJimpleValueSwitch()
              {
                    public void caseEqExpr(EqExpr expr)
                    {
                      out.add(Baf.v().newIfEqInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseNeExpr(NeExpr expr)
                    {
                      out.add(Baf.v().newIfNeInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseLtExpr(LtExpr expr)
                    {
                      out.add(Baf.v().newIfLtInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
                    
                    public void caseLeExpr(LeExpr expr)
                    {
                      out.add(Baf.v().newIfLeInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseGtExpr(GtExpr expr)
                    {
                      out.add(Baf.v().newIfGtInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseGeExpr(GeExpr expr)
                    {
                      out.add(Baf.v().newIfGeInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void defaultCase(Value v)
                    {
                        throw new RuntimeException("invalid condition " + v);
                    }
                });               
                 
                return;
            }
        
        // Handle simple subcase where op1 is 0  (flip directions)
            if(op1 instanceof IntConstant && ((IntConstant) op1).value == 0)
            {
                ((ConvertToBaf)op2).convertToBaf(context, out);
                
                cond.apply(new AbstractJimpleValueSwitch()
                {
                    public void caseEqExpr(EqExpr expr)
                    {
                      out.add(Baf.v().newIfEqInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseNeExpr(NeExpr expr)
                    {
                      out.add(Baf.v().newIfNeInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseLtExpr(LtExpr expr)
                    {
                      out.add(Baf.v().newIfGtInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
                    
                    public void caseLeExpr(LeExpr expr)
                    {
                      out.add(Baf.v().newIfGeInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseGtExpr(GtExpr expr)
                    {
                      out.add(Baf.v().newIfLtInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseGeExpr(GeExpr expr)
                    {
                      out.add(Baf.v().newIfLeInst
                              (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void defaultCase(Value v)
                    {
                        throw new RuntimeException("invalid condition " + v);
                    }
                });               
                 
                return;
            }
        
        ((ConvertToBaf)op1).convertToBaf(context, out);
        ((ConvertToBaf)op2).convertToBaf(context, out);

        cond.apply(new AbstractJimpleValueSwitch()
        {
            public void caseEqExpr(EqExpr expr)
            {
              out.add(Baf.v().newIfCmpEqInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseNeExpr(NeExpr expr)
            {
              out.add(Baf.v().newIfCmpNeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseLtExpr(LtExpr expr)
            {
              out.add(Baf.v().newIfCmpLtInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseLeExpr(LeExpr expr)
            {
              out.add(Baf.v().newIfCmpLeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseGtExpr(GtExpr expr)
            {
              out.add(Baf.v().newIfCmpGtInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseGeExpr(GeExpr expr)
            {
              out.add(Baf.v().newIfCmpGeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }
        });
    }


    public boolean fallsThrough(){return true;}	
    public boolean branches(){return true;}

}
