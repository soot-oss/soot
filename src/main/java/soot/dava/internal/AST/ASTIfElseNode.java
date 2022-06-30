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

import java.util.ArrayList;
import java.util.List;

import soot.UnitPrinter;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.toolkits.base.AST.analysis.Analysis;
import soot.jimple.ConditionExpr;

public class ASTIfElseNode extends ASTControlFlowNode {
  private List<Object> ifBody, elseBody;

  public ASTIfElseNode(SETNodeLabel label, ConditionExpr condition, List<Object> ifBody, List<Object> elseBody) {
    super(label, condition);
    this.ifBody = ifBody;
    this.elseBody = elseBody;

    subBodies.add(ifBody);
    subBodies.add(elseBody);
  }

  /*
   * Nomair A. Naeem 17-FEB-05 Needed because of change of grammar of condition being stored as a ASTCondition rather than
   * the ConditionExpr which was the case before
   */
  public ASTIfElseNode(SETNodeLabel label, ASTCondition condition, List<Object> ifBody, List<Object> elseBody) {
    super(label, condition);
    this.ifBody = ifBody;
    this.elseBody = elseBody;

    subBodies.add(ifBody);
    subBodies.add(elseBody);
  }

  /*
   * Nomair A. Naeem 19-FEB-2005 Added to support aggregation of conditions
   */
  public void replace(SETNodeLabel newLabel, ASTCondition newCond, List<Object> newBody, List<Object> bodyTwo) {
    this.ifBody = newBody;
    this.elseBody = bodyTwo;
    subBodies = new ArrayList<Object>();
    subBodies.add(newBody);
    subBodies.add(bodyTwo);
    set_Condition(newCond);
    set_Label(newLabel);

  }

  /*
   * Nomair A. Naeem 21-FEB-2005 Added to support UselessLabelBlockRemover
   */
  public void replaceBody(List<Object> ifBody, List<Object> elseBody) {
    this.ifBody = ifBody;
    this.elseBody = elseBody;

    subBodies = new ArrayList<Object>();
    subBodies.add(ifBody);
    subBodies.add(elseBody);
  }

  /*
   * Nomair A. Naeem 21-FEB-2005 Added to support OrAggregatorTwo
   */
  public void replaceElseBody(List<Object> elseBody) {
    this.elseBody = elseBody;

    subBodies = new ArrayList<Object>();
    subBodies.add(ifBody);
    subBodies.add(elseBody);
  }

  /*
   * Nomair A. Naeem 21-FEB-05 Used by OrAggregatorTwo
   */
  public List<Object> getIfBody() {
    return ifBody;
  }

  public List<Object> getElseBody() {
    return elseBody;
  }

  public Object clone() {
    return new ASTIfElseNode(get_Label(), get_Condition(), ifBody, elseBody);
  }

  public void toString(UnitPrinter up) {
    label_toString(up);

    up.literal("if");
    up.literal(" ");
    up.literal("(");
    condition.toString(up);
    up.literal(")");
    up.newline();

    up.literal("{");
    up.newline();

    up.incIndent();
    body_toString(up, ifBody);
    up.decIndent();

    up.literal("}");
    up.newline();

    up.literal("else");
    up.newline();

    up.literal("{");
    up.newline();

    up.incIndent();
    body_toString(up, elseBody);
    up.decIndent();

    up.literal("}");
    up.newline();
  }

  public String toString() {
    StringBuffer b = new StringBuffer();

    b.append(label_toString());

    b.append("if (");
    b.append(get_Condition().toString());
    b.append(")");
    b.append(NEWLINE);

    b.append("{");
    b.append(NEWLINE);

    b.append(body_toString(ifBody));

    b.append("}");
    b.append(NEWLINE);

    b.append("else");
    b.append(NEWLINE);

    b.append("{");
    b.append(NEWLINE);

    b.append(body_toString(elseBody));

    b.append("}");
    b.append(NEWLINE);

    return b.toString();
  }

  /*
   * Nomair A. Naeem, 7-FEB-05 Part of Visitor Design Implementation for AST See: soot.dava.toolkits.base.AST.analysis For
   * details
   */
  public void apply(Analysis a) {
    a.caseASTIfElseNode(this);
  }

}
