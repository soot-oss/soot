package soot.dava.internal.SET;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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

import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.misc.ConditionFlipper;
import soot.jimple.ConditionExpr;
import soot.jimple.IfStmt;
import soot.util.IterableSet;

public class SETIfElseNode extends SETDagNode {
  private IterableSet ifBody, elseBody;

  public SETIfElseNode(AugmentedStmt characterizingStmt, IterableSet body, IterableSet ifBody, IterableSet elseBody) {
    super(characterizingStmt, body);

    this.ifBody = ifBody;
    this.elseBody = elseBody;

    add_SubBody(ifBody);
    add_SubBody(elseBody);
  }

  public IterableSet get_NaturalExits() {
    IterableSet c = new IterableSet();

    IterableSet ifChain = body2childChain.get(ifBody);
    if (ifChain.isEmpty() == false) {
      c.addAll(((SETNode) ifChain.getLast()).get_NaturalExits());
    }

    IterableSet elseChain = body2childChain.get(elseBody);
    if (elseChain.isEmpty() == false) {
      c.addAll(((SETNode) elseChain.getLast()).get_NaturalExits());
    }

    return c;
  }

  public ASTNode emit_AST() {
    List<Object> astBody0 = emit_ASTBody(body2childChain.get(ifBody)),
        astBody1 = emit_ASTBody(body2childChain.get(elseBody));

    ConditionExpr ce = (ConditionExpr) ((IfStmt) get_CharacterizingStmt().get_Stmt()).getCondition();

    if (astBody0.isEmpty()) {
      List<Object> tbody = astBody0;
      astBody0 = astBody1;
      astBody1 = tbody;

      ce = ConditionFlipper.flip(ce);
    }

    if (astBody1.isEmpty()) {
      return new ASTIfNode(get_Label(), ce, astBody0);
    } else {
      return new ASTIfElseNode(get_Label(), ce, astBody0, astBody1);
    }
  }
}
