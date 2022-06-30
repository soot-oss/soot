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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.Value;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.finders.SwitchNode;
import soot.util.IterableSet;

public class SETSwitchNode extends SETDagNode {
  private List<SwitchNode> switchNodeList;
  private Value key;

  public SETSwitchNode(AugmentedStmt characterizingStmt, Value key, IterableSet body, List<SwitchNode> switchNodeList,
      IterableSet junkBody) {
    super(characterizingStmt, body);

    this.key = key;
    this.switchNodeList = switchNodeList;
    Iterator<SwitchNode> it = switchNodeList.iterator();
    while (it.hasNext()) {
      add_SubBody(it.next().get_Body());
    }

    add_SubBody(junkBody);
  }

  public IterableSet get_NaturalExits() {
    return new IterableSet();
  }

  public ASTNode emit_AST() {
    LinkedList<Object> indexList = new LinkedList<Object>();
    Map<Object, List<Object>> index2ASTBody = new HashMap<Object, List<Object>>();

    Iterator<SwitchNode> it = switchNodeList.iterator();
    while (it.hasNext()) {
      SwitchNode sn = it.next();

      Object lastIndex = sn.get_IndexSet().last();
      Iterator iit = sn.get_IndexSet().iterator();
      while (iit.hasNext()) {
        Object index = iit.next();

        indexList.addLast(index);

        if (index != lastIndex) {
          index2ASTBody.put(index, null);
        } else {
          index2ASTBody.put(index, emit_ASTBody(get_Body2ChildChain().get(sn.get_Body())));
        }
      }
    }

    return new ASTSwitchNode(get_Label(), key, indexList, index2ASTBody);
  }

  public AugmentedStmt get_EntryStmt() {
    return get_CharacterizingStmt();
  }
}
