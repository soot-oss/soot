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

import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.util.IterableSet;

public class SETLabeledBlockNode extends SETNode {
  public SETLabeledBlockNode(IterableSet body) {
    super(body);
    add_SubBody(body);
  }

  public IterableSet get_NaturalExits() {
    return ((SETNode) body2childChain.get(subBodies.get(0)).getLast()).get_NaturalExits();
  }

  public ASTNode emit_AST() {
    return new ASTLabeledBlockNode(get_Label(), emit_ASTBody(body2childChain.get(subBodies.get(0))));
  }

  public AugmentedStmt get_EntryStmt() {
    return ((SETNode) body2childChain.get(subBodies.get(0)).getFirst()).get_EntryStmt();
  }

  protected boolean resolve(SETNode parent) {
    throw new RuntimeException("Attempting auto-nest a SETLabeledBlockNode.");
  }
}
