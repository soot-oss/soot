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

import soot.tagkit.*;
import soot.*;
import soot.jimple.*;
import soot.baf.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public class JAssignStmt extends AbstractDefinitionStmt
    implements AssignStmt
{
    private class LinkedVariableBox extends VariableBox
    {
        ValueBox otherBox = null;

        private LinkedVariableBox(Value v)
        {
            super(v);
        }

        public void setOtherBox(ValueBox otherBox) { this.otherBox = otherBox; }

        public boolean canContainValue(Value v) 
        { 
            if (otherBox == null) return super.canContainValue(v);
            Value other = otherBox.getValue();
            return super.canContainValue(v) && 
                ((v instanceof Local || v instanceof Constant) || (other instanceof Local || other instanceof Constant));
        }
    }

    private class LinkedRValueBox extends RValueBox
    {
        ValueBox otherBox = null;

        private LinkedRValueBox(Value v)
        {
            super(v);
        }

        public void setOtherBox(ValueBox otherBox) { this.otherBox = otherBox; }

        public boolean canContainValue(Value v) 
        { 
            if (otherBox == null) return super.canContainValue(v);
            Value other = otherBox.getValue();
            return super.canContainValue(v) && 
                ((v instanceof Local || v instanceof Constant) || (other instanceof Local || other instanceof Constant));
        }
    }

    public JAssignStmt(Value variable, Value rvalue)
    {
        leftBox = new LinkedVariableBox(variable);
        rightBox = new LinkedRValueBox(rvalue);

        ((LinkedVariableBox)leftBox).setOtherBox(rightBox); 
        ((LinkedRValueBox)rightBox).setOtherBox(leftBox);

        if(!leftBox.canContainValue(variable) ||
            !rightBox.canContainValue(rvalue))
            throw new RuntimeException("Illegal assignment statement.  Make sure that either left side or right hand side has a local or constant.");
                    
        
        defBoxes = new SingletonList(leftBox);
    }

    protected JAssignStmt(ValueBox variableBox, ValueBox rvalueBox)
    {
        this.leftBox = variableBox;
        this.rightBox = rvalueBox;

        defBoxes = new SingletonList(leftBox);
    }

    public boolean containsInvokeExpr()
    {
        return rightBox.getValue() instanceof InvokeExpr;
    }

    public InvokeExpr getInvokeExpr()
    {
        if (!containsInvokeExpr())
            throw new RuntimeException("getInvokeExpr() called with no invokeExpr present!");

        return (InvokeExpr)rightBox.getValue();
    }

    public ValueBox getInvokeExprBox()
    {
        if (!containsInvokeExpr())
            throw new RuntimeException("getInvokeExpr() called with no invokeExpr present!");

        return rightBox;
    }

    /* added by Feng */
    public boolean containsArrayRef()
    {
	return ((leftBox.getValue() instanceof ArrayRef) || (rightBox.getValue() instanceof ArrayRef));
    }

    public ArrayRef getArrayRef()
    {
	if (!containsArrayRef())
	    throw new RuntimeException("getArrayRef() called with no ArrayRef present!");

	if (leftBox.getValue() instanceof ArrayRef)
	    return (ArrayRef) leftBox.getValue();
	else
	    return (ArrayRef) rightBox.getValue();
    }

    public ValueBox getArrayRefBox()
    {
	if (!containsArrayRef())
	    throw new RuntimeException("getArrayRefBox() called with no ArrayRef present!");
	
	if (leftBox.getValue() instanceof ArrayRef)
	    return leftBox;
	else
	    return rightBox;
    }

    public boolean containsFieldRef()
    {
	return ((leftBox.getValue() instanceof FieldRef) || (rightBox.getValue() instanceof FieldRef));
    }

    public FieldRef getFieldRef()
    {
	if (!containsFieldRef())
	    throw new RuntimeException("getFieldRef() called with no FieldRef present!");
	
	if (leftBox.getValue() instanceof FieldRef)
	    return (FieldRef) leftBox.getValue();
	else
	    return (FieldRef) rightBox.getValue();
    }

    public ValueBox getFieldRefBox()
    {
	if (!containsFieldRef())
	    throw new RuntimeException("getFieldRefBox() called with no FieldRef present!");
	
	if (leftBox.getValue() instanceof FieldRef)
	    return leftBox;
	else
	    return rightBox;
    }

    public List getUnitBoxes()
    {
        // handle possible PhiExpr's
        Value rValue = rightBox.getValue();
        if(rValue instanceof UnitBoxOwner)
            return ((UnitBoxOwner)rValue).getUnitBoxes();

        return super.getUnitBoxes();
    }
	  
    public String toString()
    {
        return leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
    }
    
    public void toString(UnitPrinter up) {
        leftBox.toString(up);
        up.literal(" = ");
        rightBox.toString(up);
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
			Unit u = Baf.v().newIncInst(context.getBafLocalOfJimpleLocal(l), 
                                                    IntConstant.v((expr instanceof AddExpr) ? x : -x));
                        out.add(u);
			Iterator it = getTags().iterator();
			while(it.hasNext()) {
			    u.addTag((Tag) it.next());
			}
			return;
                    }        
                }
            }

	    context.setCurrentUnit(this);

            lvalue.apply(new AbstractJimpleValueSwitch()
            {
                public void caseArrayRef(ArrayRef v)
                {
                    ((ConvertToBaf)(v.getBase())).convertToBaf(context, out);
                    ((ConvertToBaf)(v.getIndex())).convertToBaf(context, out);
                    ((ConvertToBaf) rvalue).convertToBaf(context, out);
                    
		    Unit u = Baf.v().newArrayWriteInst(v.getType());
		    Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }
		    
                    out.add(u);
                }
                
                public void defaultCase(Value v)
                {
                    throw new RuntimeException("Can't store in value " + v);
                }
                
                public void caseInstanceFieldRef(InstanceFieldRef v)
                {
                    ((ConvertToBaf)(v.getBase())).convertToBaf(context, out);
                    ((ConvertToBaf) rvalue).convertToBaf(context, out);


		    
		    Unit u = Baf.v().newFieldPutInst(v.getFieldRef());
		    Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }

                    out.add(u);
                }
                
                public void caseLocal(final Local v)
                {
                    ((ConvertToBaf) rvalue).convertToBaf(context, out);

                    /* Add the tags to the statement that COMPUTES the
                     * value, NOT to the statement that stores it. */
                    
                    /* No: the convertToBaf on the rvalue already adds
                     * them, so no need to add them here. However, with
                     * the current semantics, we should add them to every
                     * statement and let the aggregator sort them out.
                     */

                    Unit u = Baf.v().newStoreInst(v.getType(), 
                                        context.getBafLocalOfJimpleLocal(v));

		    Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }

                    out.add(u);

                }
                
                public void caseStaticFieldRef(StaticFieldRef v)
                {
                    ((ConvertToBaf) rvalue).convertToBaf(context, out);

		    Unit u = Baf.v().newStaticPutInst(v.getFieldRef());
		    Iterator it = getTags().iterator();
		    while(it.hasNext()) {
			u.addTag((Tag) it.next());
		    }

                    out.add(u);
                }
            }); 
    }    
}




