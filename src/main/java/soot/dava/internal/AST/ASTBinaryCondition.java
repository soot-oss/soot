package soot.dava.internal.AST;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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

import soot.UnitPrinter;
import soot.dava.toolkits.base.AST.analysis.Analysis;
import soot.dava.toolkits.base.misc.ConditionFlipper;
import soot.jimple.ConditionExpr;
import soot.jimple.Jimple;

public class ASTBinaryCondition extends ASTUnaryBinaryCondition {
  ConditionExpr condition;

  public ASTBinaryCondition(ConditionExpr condition) {
    this.condition = condition;
  }

  public ConditionExpr getConditionExpr() {
    return condition;
  }

  public void apply(Analysis a) {
    a.caseASTBinaryCondition(this);
  }

  public String toString() {
    return condition.toString();
  }

  public void toString(UnitPrinter up) {
    (Jimple.v().newConditionExprBox(condition)).toString(up);
  }

  public void flip() {
    this.condition = ConditionFlipper.flip(condition);
  }

  /*
   * Since a conditionExpr can always be flipped we always return true
   * 
   */
  public boolean isNotted() {
    return true;
  }
}
