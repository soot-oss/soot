/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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






package soot.jimple.internal;

import soot.*;
import soot.tagkit.*;
import soot.jimple.*;
import soot.baf.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public class JIfStmt extends AbstractStmt implements IfStmt
{
    ValueBox conditionBox;
    UnitBox targetBox;

    List targetBoxes;

    public JIfStmt(Value condition, Unit target)
    {
        this(Jimple.v().newConditionExprBox(condition),
             Jimple.v().newStmtBox(target));
    }

    public JIfStmt(Value condition, UnitBox target)
    {
        this(Jimple.v().newConditionExprBox(condition),
             target);
    }



    protected JIfStmt(ValueBox conditionBox, UnitBox targetBox)
    {
        this.conditionBox = conditionBox;
        this.targetBox = targetBox;

        targetBoxes = new ArrayList();
        targetBoxes.add(this.targetBox);
        targetBoxes = Collections.unmodifiableList(targetBoxes);
    }
    
    public Object clone()
    {
        return new JIfStmt(Jimple.cloneIfNecessary(getCondition()), getTarget());
    }
    
    public String toString()
    {
        Unit t = getTarget();
        String target = "(branch)";
        if(!t.branches())
            target = t.toString();
        return Jimple.v().IF + " "  + getCondition().toString() + " " + Jimple.v().GOTO + " "  + target;
    }
    
    public void toString(UnitPrinter up) {
        up.literal(Jimple.v().IF);
        up.literal(" ");
        conditionBox.toString(up);
        up.literal(" ");
        up.literal(Jimple.v().GOTO);
        up.literal(" ");
        targetBox.toString(up);
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

	context.setCurrentUnit(this);

        // Handle simple subcase where op1 is null
        if(op2 instanceof NullConstant || op1 instanceof NullConstant)
          {
            if(op2 instanceof NullConstant)
              ((ConvertToBaf)op1).convertToBaf(context, out);
            else
              ((ConvertToBaf)op2).convertToBaf(context, out);
            Unit u;

            if(cond instanceof EqExpr)
              u = Baf.v().newIfNullInst
                      (Baf.v().newPlaceholderInst(getTarget()));
            else if (cond instanceof NeExpr)
              u = Baf.v().newIfNonNullInst
                      (Baf.v().newPlaceholderInst(getTarget()));
            else
              throw new RuntimeException("invalid condition");

            out.add(u);
	    Iterator it = getTags().iterator();
	    while(it.hasNext()) {
		u.addTag((Tag) it.next());
	    }
            return;
          }

        // Handle simple subcase where op2 is 0  
        if(op2 instanceof IntConstant && ((IntConstant) op2).value == 0)
          {
              ((ConvertToBaf)op1).convertToBaf(context, out);
                
              cond.apply(new AbstractJimpleValueSwitch()
              {
                    private void add(Unit u)
                    {
		        out.add(u);
			Iterator it = getTags().iterator();
			while(it.hasNext()) {
			    u.addTag((Tag) it.next());
                        }
                    }

                    public void caseEqExpr(EqExpr expr)
                    {
			add(Baf.v().newIfEqInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseNeExpr(NeExpr expr)
                    {
			add(Baf.v().newIfNeInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseLtExpr(LtExpr expr)
                    {
			add(Baf.v().newIfLtInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
                    
                    public void caseLeExpr(LeExpr expr)
                    {
			add(Baf.v().newIfLeInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseGtExpr(GtExpr expr)
                    {
			add(Baf.v().newIfGtInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseGeExpr(GeExpr expr)
                    {
			add(Baf.v().newIfGeInst(Baf.v().newPlaceholderInst(getTarget())));
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
                    private void add(Unit u)
                    {
		        out.add(u);
			Iterator it = getTags().iterator();
			while(it.hasNext()) {
			    u.addTag((Tag) it.next());
                        }
                    }

                    public void caseEqExpr(EqExpr expr)
                    { 
			add(Baf.v().newIfEqInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseNeExpr(NeExpr expr)
                    {
			add(Baf.v().newIfNeInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseLtExpr(LtExpr expr)
                    {
			add(Baf.v().newIfGtInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
                    
                    public void caseLeExpr(LeExpr expr)
                    {
			add(Baf.v().newIfGeInst (Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseGtExpr(GtExpr expr)
                    {
			add(Baf.v().newIfLtInst(Baf.v().newPlaceholderInst(getTarget())));
                    }
        
                    public void caseGeExpr(GeExpr expr)
                    {
			add(Baf.v().newIfLeInst(Baf.v().newPlaceholderInst(getTarget())));
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
	

        cond.apply(new AbstractJimpleValueSwitch() {
            private void add(Unit u)
            {
                out.add(u);
                Iterator it = getTags().iterator();
                while(it.hasNext()) {
                    u.addTag((Tag) it.next());
                }
            }

            public void caseEqExpr(EqExpr expr)
	    {
                add(Baf.v().newIfCmpEqInst(op1.getType(), 
                          Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseNeExpr(NeExpr expr)
            {
                add(Baf.v().newIfCmpNeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseLtExpr(LtExpr expr)
            {
                add(Baf.v().newIfCmpLtInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseLeExpr(LeExpr expr)
            {
                add(Baf.v().newIfCmpLeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseGtExpr(GtExpr expr)
            {
                add(Baf.v().newIfCmpGtInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }

            public void caseGeExpr(GeExpr expr)
            {
                add(Baf.v().newIfCmpGeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
            }
        });
	
    }


    public boolean fallsThrough(){return true;}        
    public boolean branches(){return true;}

}
