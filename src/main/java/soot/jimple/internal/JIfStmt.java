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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.AbstractJimpleValueSwitch;
import soot.jimple.BinopExpr;
import soot.jimple.ConvertToBaf;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.LeExpr;
import soot.jimple.LtExpr;
import soot.jimple.NeExpr;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.StmtSwitch;
import soot.util.Switch;

public class JIfStmt extends AbstractStmt implements IfStmt {
  final ValueBox conditionBox;
  final UnitBox targetBox;

  final List<UnitBox> targetBoxes;

  public JIfStmt(Value condition, Unit target) {
    this(condition, Jimple.v().newStmtBox(target));
  }

  public JIfStmt(Value condition, UnitBox target) {
    this(Jimple.v().newConditionExprBox(condition), target);
  }

  protected JIfStmt(ValueBox conditionBox, UnitBox targetBox) {
    this.conditionBox = conditionBox;
    this.targetBox = targetBox;

    targetBoxes = Collections.singletonList(targetBox);
  }

  public Object clone() {
    return new JIfStmt(Jimple.cloneIfNecessary(getCondition()), getTarget());
  }

  public String toString() {
    Unit t = getTarget();
    String target = "(branch)";
    if (!t.branches()) {
      target = t.toString();
    }
    return Jimple.IF + " " + getCondition().toString() + " " + Jimple.GOTO + " " + target;
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.IF);
    up.literal(" ");
    conditionBox.toString(up);
    up.literal(" ");
    up.literal(Jimple.GOTO);
    up.literal(" ");
    targetBox.toString(up);
  }

  public Value getCondition() {
    return conditionBox.getValue();
  }

  public void setCondition(Value condition) {
    conditionBox.setValue(condition);
  }

  public ValueBox getConditionBox() {
    return conditionBox;
  }

  public Stmt getTarget() {
    return (Stmt) targetBox.getUnit();
  }

  public void setTarget(Unit target) {
    targetBox.setUnit(target);
  }

  public UnitBox getTargetBox() {
    return targetBox;
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    List<ValueBox> useBoxes = new ArrayList<ValueBox>();

    useBoxes.addAll(conditionBox.getValue().getUseBoxes());
    useBoxes.add(conditionBox);

    return useBoxes;
  }

  @Override
  public final List<UnitBox> getUnitBoxes() {
    return targetBoxes;
  }

  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseIfStmt(this);
  }

  public void convertToBaf(final JimpleToBafContext context, final List<Unit> out) {
    Value cond = getCondition();

    final Value op1 = ((BinopExpr) cond).getOp1();
    final Value op2 = ((BinopExpr) cond).getOp2();

    context.setCurrentUnit(this);

    // Handle simple subcase where op1 is null
    if (op2 instanceof NullConstant || op1 instanceof NullConstant) {
      if (op2 instanceof NullConstant) {
        ((ConvertToBaf) op1).convertToBaf(context, out);
      } else {
        ((ConvertToBaf) op2).convertToBaf(context, out);
      }
      Unit u;

      if (cond instanceof EqExpr) {
        u = Baf.v().newIfNullInst(Baf.v().newPlaceholderInst(getTarget()));
      } else if (cond instanceof NeExpr) {
        u = Baf.v().newIfNonNullInst(Baf.v().newPlaceholderInst(getTarget()));
      } else {
        throw new RuntimeException("invalid condition");
      }

      u.addAllTagsOf(this);
      out.add(u);
      return;
    }

    // Handle simple subcase where op2 is 0
    if (op2 instanceof IntConstant && ((IntConstant) op2).value == 0) {
      ((ConvertToBaf) op1).convertToBaf(context, out);

      cond.apply(new AbstractJimpleValueSwitch() {
        private void add(Unit u) {
          u.addAllTagsOf(JIfStmt.this);
          out.add(u);
        }

        public void caseEqExpr(EqExpr expr) {
          add(Baf.v().newIfEqInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseNeExpr(NeExpr expr) {
          add(Baf.v().newIfNeInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseLtExpr(LtExpr expr) {
          add(Baf.v().newIfLtInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseLeExpr(LeExpr expr) {
          add(Baf.v().newIfLeInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseGtExpr(GtExpr expr) {
          add(Baf.v().newIfGtInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseGeExpr(GeExpr expr) {
          add(Baf.v().newIfGeInst(Baf.v().newPlaceholderInst(getTarget())));
        }
      });

      return;
    }

    // Handle simple subcase where op1 is 0 (flip directions)
    if (op1 instanceof IntConstant && ((IntConstant) op1).value == 0) {
      ((ConvertToBaf) op2).convertToBaf(context, out);

      cond.apply(new AbstractJimpleValueSwitch() {
        private void add(Unit u) {
          u.addAllTagsOf(JIfStmt.this);
          out.add(u);
        }

        public void caseEqExpr(EqExpr expr) {
          add(Baf.v().newIfEqInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseNeExpr(NeExpr expr) {
          add(Baf.v().newIfNeInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseLtExpr(LtExpr expr) {
          add(Baf.v().newIfGtInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseLeExpr(LeExpr expr) {
          add(Baf.v().newIfGeInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseGtExpr(GtExpr expr) {
          add(Baf.v().newIfLtInst(Baf.v().newPlaceholderInst(getTarget())));
        }

        public void caseGeExpr(GeExpr expr) {
          add(Baf.v().newIfLeInst(Baf.v().newPlaceholderInst(getTarget())));
        }
      });

      return;
    }

    ((ConvertToBaf) op1).convertToBaf(context, out);
    ((ConvertToBaf) op2).convertToBaf(context, out);

    cond.apply(new AbstractJimpleValueSwitch() {
      private void add(Unit u) {
        u.addAllTagsOf(JIfStmt.this);
        out.add(u);
      }

      public void caseEqExpr(EqExpr expr) {
        add(Baf.v().newIfCmpEqInst(op1.getType(), Baf.v().newPlaceholderInst(getTarget())));
      }

      public void caseNeExpr(NeExpr expr) {
        add(Baf.v().newIfCmpNeInst(op1.getType(), Baf.v().newPlaceholderInst(getTarget())));
      }

      public void caseLtExpr(LtExpr expr) {
        add(Baf.v().newIfCmpLtInst(op1.getType(), Baf.v().newPlaceholderInst(getTarget())));
      }

      public void caseLeExpr(LeExpr expr) {
        add(Baf.v().newIfCmpLeInst(op1.getType(), Baf.v().newPlaceholderInst(getTarget())));
      }

      public void caseGtExpr(GtExpr expr) {
        add(Baf.v().newIfCmpGtInst(op1.getType(), Baf.v().newPlaceholderInst(getTarget())));
      }

      public void caseGeExpr(GeExpr expr) {
        add(Baf.v().newIfCmpGeInst(op1.getType(), Baf.v().newPlaceholderInst(getTarget())));
      }
    });

  }

  public boolean fallsThrough() {
    return true;
  }

  public boolean branches() {
    return true;
  }

}
