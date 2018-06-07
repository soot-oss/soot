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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Unit;
import soot.UnitPrinter;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.analysis.Analysis;

/*
 Will contain the For loop Construct
 ________    _______    _______
 for ( |___A____| ;|___B___| ;|___C___| )

 * A has to be the following (look at the java grammar specs)
 --> local variable declarations (int a=0,b=3,c=10)
 OR
 --> assignment expressions
 --> increment/decrement expressions both pre and post e.g. ++bla,--bla,bla++,bla--
 --> all sorts of method invocations
 --> new class instance declaration (dont know exactly what these include)

 * B can be any ASTCondition

 * C can be
 --> assignment expressions
 --> increment/decrement expressions both pre and post e.g. ++bla,--bla,bla++,bla--
 --> all sorts of method invocations
 --> new class instance declaration (dont know exactly what these include)


 Extend the ASTControlFlowNode since there is a B (ASTCondition involved)
 and also since that extends ASTlabeledNoce and a for loop can have an associated label

 */
public class ASTForLoopNode extends ASTControlFlowNode {
  private List<AugmentedStmt> init; // list of values
  // notice B is an ASTCondition and is stored in the parent
  private List<AugmentedStmt> update; // list of values

  private List<Object> body;

  public ASTForLoopNode(SETNodeLabel label, List<AugmentedStmt> init, ASTCondition condition, List<AugmentedStmt> update,
      List<Object> body) {
    super(label, condition);
    this.body = body;
    this.init = init;
    this.update = update;

    subBodies.add(body);
  }

  public List<AugmentedStmt> getInit() {
    return init;
  }

  public List<AugmentedStmt> getUpdate() {
    return update;
  }

  public void replaceBody(List<Object> body) {
    this.body = body;
    subBodies = new ArrayList<Object>();
    subBodies.add(body);
  }

  public Object clone() {
    return new ASTForLoopNode(get_Label(), init, get_Condition(), update, body);
  }

  public void toString(UnitPrinter up) {
    label_toString(up);

    up.literal("for");
    up.literal(" ");
    up.literal("(");

    Iterator<AugmentedStmt> it = init.iterator();
    while (it.hasNext()) {
      AugmentedStmt as = it.next();
      Unit u = as.get_Stmt();
      u.toString(up);
      if (it.hasNext()) {
        up.literal(" , ");
      }
    }

    up.literal("; ");

    condition.toString(up);
    up.literal("; ");

    it = update.iterator();
    while (it.hasNext()) {
      AugmentedStmt as = it.next();
      Unit u = as.get_Stmt();
      u.toString(up);
      if (it.hasNext()) {
        up.literal(" , ");
      }
    }

    up.literal(")");
    up.newline();

    up.literal("{");
    up.newline();

    up.incIndent();
    body_toString(up, body);
    up.decIndent();

    up.literal("}");
    up.newline();

  }

  public String toString() {
    StringBuffer b = new StringBuffer();

    b.append(label_toString());
    b.append("for (");

    Iterator<AugmentedStmt> it = init.iterator();
    while (it.hasNext()) {
      b.append(it.next().get_Stmt().toString());
      if (it.hasNext()) {
        b.append(" , ");
      }
    }
    b.append("; ");

    b.append(get_Condition().toString());
    b.append("; ");

    it = update.iterator();
    while (it.hasNext()) {
      b.append(it.next().get_Stmt().toString());
      if (it.hasNext()) {
        b.append(" , ");
      }
    }

    b.append(")");
    b.append(NEWLINE);

    b.append("{");
    b.append(NEWLINE);

    b.append(body_toString(body));

    b.append("}");
    b.append(NEWLINE);

    return b.toString();
  }

  public void apply(Analysis a) {
    a.caseASTForLoopNode(this);
  }

}
