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

import java.util.Iterator;

import soot.Value;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.finders.ExceptionNode;
import soot.util.IterableSet;

public class SETSynchronizedBlockNode extends SETNode {
  private Value local;

  public SETSynchronizedBlockNode(ExceptionNode en, Value local) {
    super(en.get_Body());

    add_SubBody(en.get_TryBody());
    add_SubBody(en.get_CatchBody());

    this.local = local;
  }

  public IterableSet get_NaturalExits() {
    return ((SETNode) body2childChain.get(subBodies.get(0)).getLast()).get_NaturalExits();
  }

  public ASTNode emit_AST() {
    return new ASTSynchronizedBlockNode(get_Label(), emit_ASTBody(body2childChain.get(subBodies.get(0))), local);
  }

  public AugmentedStmt get_EntryStmt() {
    return ((SETNode) body2childChain.get(subBodies.get(0)).getFirst()).get_EntryStmt();
  }

  protected boolean resolve(SETNode parent) {
    Iterator<IterableSet> sbit = parent.get_SubBodies().iterator();

    while (sbit.hasNext()) {
      IterableSet subBody = sbit.next();

      if (subBody.intersects(get_Body())) {
        return subBody.isSupersetOf(get_Body());
      }
    }

    return true;
  }
}
