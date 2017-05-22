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

import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;

public class SETWhileNode extends SETCycleNode {
	public SETWhileNode(AugmentedStmt characterizingStmt, IterableSet body) {
		super(characterizingStmt, body);

		IterableSet subBody = (IterableSet) body.clone();
		subBody.remove(characterizingStmt);
		add_SubBody(subBody);
	}

	public IterableSet get_NaturalExits() {
		IterableSet c = new IterableSet();

		c.add(get_CharacterizingStmt());

		return c;
	}

	public ASTNode emit_AST() {
		return new ASTWhileNode(get_Label(),
				(ConditionExpr) ((IfStmt) get_CharacterizingStmt().get_Stmt())
						.getCondition(),
				emit_ASTBody(body2childChain.get(subBodies.get(0))));
	}

	public AugmentedStmt get_EntryStmt() {
		return get_CharacterizingStmt();
	}
}
