/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
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

package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.toolkits.base.misc.*;

public class SETIfElseNode extends SETDagNode {
	private IterableSet ifBody, elseBody;

	public SETIfElseNode(AugmentedStmt characterizingStmt, IterableSet body,
			IterableSet ifBody, IterableSet elseBody) {
		super(characterizingStmt, body);

		this.ifBody = ifBody;
		this.elseBody = elseBody;

		add_SubBody(ifBody);
		add_SubBody(elseBody);
	}

	public IterableSet get_NaturalExits() {
		IterableSet c = new IterableSet();

		IterableSet ifChain = body2childChain.get(ifBody);
		if (ifChain.isEmpty() == false)
			c.addAll(((SETNode) ifChain.getLast()).get_NaturalExits());

		IterableSet elseChain = body2childChain.get(elseBody);
		if (elseChain.isEmpty() == false)
			c.addAll(((SETNode) elseChain.getLast()).get_NaturalExits());

		return c;
	}

	public ASTNode emit_AST() {
		List<Object> astBody0 = emit_ASTBody(body2childChain.get(ifBody)), astBody1 = emit_ASTBody(body2childChain
				.get(elseBody));

		ConditionExpr ce = (ConditionExpr) ((IfStmt) get_CharacterizingStmt()
				.get_Stmt()).getCondition();

		if (astBody0.isEmpty()) {
			List<Object> tbody = astBody0;
			astBody0 = astBody1;
			astBody1 = tbody;

			ce = ConditionFlipper.flip(ce);
		}

		if (astBody1.isEmpty())
			return new ASTIfNode(get_Label(), ce, astBody0);
		else
			return new ASTIfElseNode(get_Label(), ce, astBody0, astBody1);
	}
}
