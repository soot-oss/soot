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
    
    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        if(isBrief)
            return indentation + Jimple.v().IF + " "  + 
                ((ToBriefString) getCondition()).toBriefString() + " " + Jimple.v().GOTO + " "  + (String) stmtToName.get(getTarget());
        else
            return indentation + Jimple.v().IF + " "  + getCondition().toString() + " " + Jimple.v().GOTO + " "  + (String) stmtToName.get(getTarget());
    }
    
    public String toString()
    {
        return Jimple.v().IF + " "  + conditionBox.getValue().toString() + " " + Jimple.v().GOTO + " ?";
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
		   Unit u;
		    out.add( u = Baf.v().newIfCmpEqInst(op1.getType(), 
							Baf.v().newPlaceholderInst(getTarget())));
		    
		    Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }

                    

		    
            }

            public void caseNeExpr(NeExpr expr)
            {
		Unit u;
              out.add(u = Baf.v().newIfCmpNeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
	      Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }

                    
	      
            }

            public void caseLtExpr(LtExpr expr)
            {
		Unit u;
              out.add(u = Baf.v().newIfCmpLtInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
	      Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }

                    

            }

            public void caseLeExpr(LeExpr expr)
            {
		Unit u;
              out.add(u = Baf.v().newIfCmpLeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
	      Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }

                    
            }

            public void caseGtExpr(GtExpr expr)
            {
		Unit u;
              out.add(u = Baf.v().newIfCmpGtInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
	      Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }

                    

            }

            public void caseGeExpr(GeExpr expr)
            {
		Unit u;
              out.add(u = Baf.v().newIfCmpGeInst(op1.getType(), 
                       Baf.v().newPlaceholderInst(getTarget())));
	      Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }



            }
        });
	
    }


    public boolean fallsThrough(){return true;}        
    public boolean branches(){return true;}

}
