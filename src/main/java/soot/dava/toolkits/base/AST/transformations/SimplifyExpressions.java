package soot.dava.toolkits.base.AST.transformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.Value;
import soot.ValueBox;
import soot.dava.internal.javaRep.DCmpExpr;
import soot.dava.internal.javaRep.DCmpgExpr;
import soot.dava.internal.javaRep.DCmplExpr;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.AddExpr;
import soot.jimple.BinopExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.MulExpr;
import soot.jimple.NumericConstant;
import soot.jimple.SubExpr;

/*
 * x = 2+3  should be simplified to x =5
 * 4l -3l should be 1l DONE

 * Unary Condition:DONT NEED TO HANDLE IT since what would simplify
 * in a boolean flag which is what unary conditions are
 *
 * Binary Codition: has a ConditionExpr stored in it not a valuebox???
 * all other expression to be handled by caseExprOrRefValueBox
 */

public class SimplifyExpressions extends DepthFirstAdapter {
  public static boolean DEBUG = false;

  public SimplifyExpressions() {
    super();
  }

  public SimplifyExpressions(boolean verbose) {
    super(verbose);
  }

  /*
   * public void inASTBinaryCondition(ASTBinaryCondition cond){ ConditionExpr condExpr = cond.getConditionExpr();
   *
   * ValueBox op1Box = condExpr.getOp1Box();
   *
   * ValueBox op2Box = condExpr.getOp2Box(); }
   */

  public void outExprOrRefValueBox(ValueBox vb) {
    // System.out.println("here"+vb);
    Value v = vb.getValue();
    if (!(v instanceof BinopExpr)) {
      return;
    }

    BinopExpr binop = (BinopExpr) v;
    if (DEBUG) {
      System.out.println("calling getResult");
    }
    NumericConstant constant = getResult(binop);

    if (constant == null) {
      return;
    }
    if (DEBUG) {
      System.out.println("Changin" + vb + " to...." + constant);
    }
    vb.setValue(constant);
  }

  public NumericConstant getResult(BinopExpr binop) {
    if (DEBUG) {
      System.out.println("Binop expr" + binop);
    }
    Value leftOp = binop.getOp1();
    Value rightOp = binop.getOp2();

    int op = 0;
    if (binop instanceof AddExpr) {
      op = 1;
    } else if (binop instanceof SubExpr || binop instanceof DCmpExpr || binop instanceof DCmpgExpr
        || binop instanceof DCmplExpr) {
      op = 2;
    } else if (binop instanceof MulExpr) {
      op = 3;
    }

    if (op == 0) {
      if (DEBUG) {
        System.out.println("not add sub or mult");
        System.out.println(binop.getClass().getName());
      }
      return null;
    }
    NumericConstant constant = null;
    if (leftOp instanceof LongConstant && rightOp instanceof LongConstant) {
      if (DEBUG) {
        System.out.println("long constants!!");
      }
      if (op == 1) {
        constant = ((LongConstant) leftOp).add((LongConstant) rightOp);
      } else if (op == 2) {
        constant = ((LongConstant) leftOp).subtract((LongConstant) rightOp);
      } else if (op == 3) {
        constant = ((LongConstant) leftOp).multiply((LongConstant) rightOp);
      }
    } else if (leftOp instanceof DoubleConstant && rightOp instanceof DoubleConstant) {
      if (DEBUG) {
        System.out.println("double constants!!");
      }
      if (op == 1) {
        constant = ((DoubleConstant) leftOp).add((DoubleConstant) rightOp);
      } else if (op == 2) {
        constant = ((DoubleConstant) leftOp).subtract((DoubleConstant) rightOp);
      } else if (op == 3) {
        constant = ((DoubleConstant) leftOp).multiply((DoubleConstant) rightOp);
      }

    } else if (leftOp instanceof FloatConstant && rightOp instanceof FloatConstant) {
      if (DEBUG) {
        System.out.println("Float constants!!");
      }
      if (op == 1) {
        constant = ((FloatConstant) leftOp).add((FloatConstant) rightOp);
      } else if (op == 2) {
        constant = ((FloatConstant) leftOp).subtract((FloatConstant) rightOp);
      } else if (op == 3) {
        constant = ((FloatConstant) leftOp).multiply((FloatConstant) rightOp);
      }
    } else if (leftOp instanceof IntConstant && rightOp instanceof IntConstant) {
      if (DEBUG) {
        System.out.println("Integer constants!!");
      }
      if (op == 1) {
        constant = ((IntConstant) leftOp).add((IntConstant) rightOp);
      } else if (op == 2) {
        constant = ((IntConstant) leftOp).subtract((IntConstant) rightOp);
      } else if (op == 3) {
        constant = ((IntConstant) leftOp).multiply((IntConstant) rightOp);
      }
    }

    return constant;
  }

}
