package soot.dava.internal.AST;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004 - 2005 Nomair A. Naeem
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

import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.toolkits.base.AST.ASTAnalysis;
import soot.dava.toolkits.base.AST.ASTWalker;
import soot.dava.toolkits.base.AST.TryContentsFinder;
import soot.jimple.ConditionExpr;

public abstract class ASTControlFlowNode extends ASTLabeledNode {
  // protected ValueBox conditionBox;
  ASTCondition condition;

  public ASTControlFlowNode(SETNodeLabel label, ConditionExpr condition) {
    super(label);
    // this.conditionBox = Jimple.v().newConditionExprBox(condition);
    this.condition = new ASTBinaryCondition(condition);
  }

  /*
   * Nomair A. Naeem 17-FEB-05 Needed because of change of grammar of condition being stored as a ASTCondition rather than
   * the ConditionExpr which was the case before
   */
  public ASTControlFlowNode(SETNodeLabel label, ASTCondition condition) {
    super(label);
    this.condition = condition;
  }

  public ASTCondition get_Condition() {
    return condition;
  }

  public void set_Condition(ASTCondition condition) {
    this.condition = condition;
  }

  public void perform_Analysis(ASTAnalysis a) {
    /*
     * Nomair A Naeem 17-FEB-05 Changed because the ASTControlFlowNode does not have a ConditionBox anymore
     *
     * The if check is not an ideal way of implementation What should be done is to do a DepthFirst of the Complete Condition
     * hierarcy and walk all values that are found
     *
     * Notice this condition will always return true UNLESS transformations aggregating the control flow have been performed.
     *
     * This method is deprecated do not use it. Use the DepthFirstAdapter class in dava.toolkits.base.AST.analysis.
     */
    if (condition instanceof ASTBinaryCondition) {
      ConditionExpr condExpr = ((ASTBinaryCondition) condition).getConditionExpr();
      ASTWalker.v().walk_value(a, condExpr);
    }

    if (a instanceof TryContentsFinder) {
      TryContentsFinder.v().add_ExceptionSet(this, TryContentsFinder.v().remove_CurExceptionSet());
    }

    perform_AnalysisOnSubBodies(a);
  }
}
