package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.List;

import soot.Immediate;
import soot.IntType;
import soot.Local;
import soot.Unit;
import soot.UnitBox;
import soot.UnitBoxOwner;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.AddExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.ConvertToBaf;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.StaticFieldRef;
import soot.jimple.StmtSwitch;
import soot.jimple.SubExpr;
import soot.util.Switch;

public class JAssignStmt extends AbstractDefinitionStmt implements AssignStmt {

  @SuppressWarnings("serial")
  public static class LinkedVariableBox extends VariableBox {
    ValueBox otherBox = null;

    public LinkedVariableBox(Value v) {
      super(v);
    }

    public void setOtherBox(ValueBox otherBox) {
      this.otherBox = otherBox;
    }

    @Override
    public boolean canContainValue(Value v) {
      if (super.canContainValue(v)) {
        return (otherBox == null) || (v instanceof Immediate) || (otherBox.getValue() instanceof Immediate);
      }
      return false;
    }
  }

  @SuppressWarnings("serial")
  public static class LinkedRValueBox extends RValueBox {
    ValueBox otherBox = null;

    public LinkedRValueBox(Value v) {
      super(v);
    }

    public void setOtherBox(ValueBox otherBox) {
      this.otherBox = otherBox;
    }

    @Override
    public boolean canContainValue(Value v) {
      if (super.canContainValue(v)) {
        return (otherBox == null) || (v instanceof Immediate) || (otherBox.getValue() instanceof Immediate);
      }
      return false;
    }
  }

  public JAssignStmt(Value variable, Value rvalue) {
    this(new LinkedVariableBox(variable), new LinkedRValueBox(rvalue));

    ((LinkedVariableBox) leftBox).setOtherBox(rightBox);
    ((LinkedRValueBox) rightBox).setOtherBox(leftBox);

    if (!leftBox.canContainValue(variable) || !rightBox.canContainValue(rvalue)) {
      throw new RuntimeException(
          "Illegal assignment statement. Make sure that either left side or right hand side has a local or constant."
              + "Variable is class " + variable.getClass().getName() + "(" + leftBox.canContainValue(variable) + ")"
              + " and rvalue is class " + rvalue.getClass().getName() + "(" + rightBox.canContainValue(rvalue) + ").");
    }
  }

  protected JAssignStmt(ValueBox variableBox, ValueBox rvalueBox) {
    super(variableBox, rvalueBox);
    if (leftBox instanceof LinkedVariableBox) {
      ((LinkedVariableBox) leftBox).setOtherBox(rightBox);
    }
    if (rightBox instanceof LinkedRValueBox) {
      ((LinkedRValueBox) rightBox).setOtherBox(leftBox);
    }
  }

  @Override
  public boolean containsInvokeExpr() {
    return getRightOp() instanceof InvokeExpr;
  }

  @Override
  public InvokeExpr getInvokeExpr() {
    return (InvokeExpr) getInvokeExprBox().getValue();
  }

  @Override
  public ValueBox getInvokeExprBox() {
    if (!containsInvokeExpr()) {
      throw new RuntimeException("getInvokeExprBox() called with no invokeExpr present!");
    }
    return rightBox;
  }

  /* added by Feng */
  @Override
  public boolean containsArrayRef() {
    return ((getLeftOp() instanceof ArrayRef) || (getRightOp() instanceof ArrayRef));
  }

  @Override
  public ArrayRef getArrayRef() {
    return (ArrayRef) getArrayRefBox().getValue();
  }

  @Override
  public ValueBox getArrayRefBox() {
    if (!containsArrayRef()) {
      throw new RuntimeException("getArrayRefBox() called with no ArrayRef present!");
    }
    return (leftBox.getValue() instanceof ArrayRef) ? leftBox : rightBox;
  }

  @Override
  public boolean containsFieldRef() {
    return ((getLeftOp() instanceof FieldRef) || (getRightOp() instanceof FieldRef));
  }

  @Override
  public FieldRef getFieldRef() {
    return (FieldRef) getFieldRefBox().getValue();
  }

  @Override
  public ValueBox getFieldRefBox() {
    if (!containsFieldRef()) {
      throw new RuntimeException("getFieldRefBox() called with no FieldRef present!");
    }
    return (leftBox.getValue() instanceof FieldRef) ? leftBox : rightBox;
  }

  @Override
  public List<UnitBox> getUnitBoxes() {
    // handle possible PhiExpr's
    Value rValue = rightBox.getValue();
    if (rValue instanceof UnitBoxOwner) {
      return ((UnitBoxOwner) rValue).getUnitBoxes();
    } else {
      return super.getUnitBoxes();
    }
  }

  @Override
  public String toString() {
    return leftBox.getValue().toString() + " = " + rightBox.getValue().toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    leftBox.toString(up);
    up.literal(" = ");
    rightBox.toString(up);
  }

  @Override
  public Object clone() {
    return new JAssignStmt(Jimple.cloneIfNecessary(getLeftOp()), Jimple.cloneIfNecessary(getRightOp()));
  }

  @Override
  public void setLeftOp(Value variable) {
    getLeftOpBox().setValue(variable);
  }

  @Override
  public void setRightOp(Value rvalue) {
    getRightOpBox().setValue(rvalue);
  }

  @Override
  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseAssignStmt(this);
  }

  @Override
  public void convertToBaf(final JimpleToBafContext context, final List<Unit> out) {
    final Value lvalue = this.getLeftOp();
    final Value rvalue = this.getRightOp();

    // Handle simple subcase where you can use the efficient iinc bytecode
    if (lvalue instanceof Local && (rvalue instanceof AddExpr || rvalue instanceof SubExpr)) {
      Local l = (Local) lvalue;
      BinopExpr expr = (BinopExpr) rvalue;
      Value op1 = expr.getOp1();
      Value op2 = expr.getOp2();

      if (IntType.v().equals(l.getType())) {
        boolean isValidCase = false;
        int x = 0;

        if (op1 == l && op2 instanceof IntConstant) {
          x = ((IntConstant) op2).value;
          isValidCase = true;
        } else if (expr instanceof AddExpr && op2 == l && op1 instanceof IntConstant) {
          // Note expr can't be a SubExpr because that would be x = 3 - x
          x = ((IntConstant) op1).value;
          isValidCase = true;
        }

        if (isValidCase && x >= Short.MIN_VALUE && x <= Short.MAX_VALUE) {
          final Baf baf = Baf.v();
          Unit u = baf.newIncInst(context.getBafLocalOfJimpleLocal(l), IntConstant.v((expr instanceof AddExpr) ? x : -x));
          u.addAllTagsOf(this);
          out.add(u);

          return;
        }
      }
    }

    context.setCurrentUnit(this);

    lvalue.apply(new AbstractJimpleValueSwitch() {
      @Override
      public void caseArrayRef(ArrayRef v) {
        ((ConvertToBaf) (v.getBase())).convertToBaf(context, out);
        ((ConvertToBaf) (v.getIndex())).convertToBaf(context, out);
        ((ConvertToBaf) rvalue).convertToBaf(context, out);

        Unit u = Baf.v().newArrayWriteInst(v.getType());
        u.addAllTagsOf(JAssignStmt.this);
        out.add(u);
      }

      @Override
      public void caseInstanceFieldRef(InstanceFieldRef v) {
        ((ConvertToBaf) (v.getBase())).convertToBaf(context, out);
        ((ConvertToBaf) rvalue).convertToBaf(context, out);

        Unit u = Baf.v().newFieldPutInst(v.getFieldRef());
        u.addAllTagsOf(JAssignStmt.this);
        out.add(u);
      }

      @Override
      public void caseLocal(final Local v) {
        ((ConvertToBaf) rvalue).convertToBaf(context, out);

        /*
         * Add the tags to the statement that COMPUTES the value, NOT to the statement that stores it.
         */

        /*
         * No: the convertToBaf on the rvalue already adds them, so no need to add them here. However, with the current
         * semantics, we should add them to every statement and let the aggregator sort them out.
         */

        Unit u = Baf.v().newStoreInst(v.getType(), context.getBafLocalOfJimpleLocal(v));
        u.addAllTagsOf(JAssignStmt.this);
        out.add(u);
      }

      @Override
      public void caseStaticFieldRef(StaticFieldRef v) {
        ((ConvertToBaf) rvalue).convertToBaf(context, out);

        Unit u = Baf.v().newStaticPutInst(v.getFieldRef());
        u.addAllTagsOf(JAssignStmt.this);
        out.add(u);
      }
    });
  }
}
