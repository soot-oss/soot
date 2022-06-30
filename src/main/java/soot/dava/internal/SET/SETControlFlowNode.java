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

import java.util.HashSet;
import java.util.Iterator;

import soot.dava.internal.asg.AugmentedStmt;
import soot.jimple.GotoStmt;
import soot.util.IterableSet;

public abstract class SETControlFlowNode extends SETNode {
  private AugmentedStmt characterizingStmt;

  public SETControlFlowNode(AugmentedStmt characterizingStmt, IterableSet<AugmentedStmt> body) {
    super(body);
    this.characterizingStmt = characterizingStmt;
  }

  public AugmentedStmt get_CharacterizingStmt() {
    return characterizingStmt;
  }

  protected boolean resolve(SETNode parent) {
    for (IterableSet subBody : parent.get_SubBodies()) {
      if (subBody.contains(get_EntryStmt()) == false) {
        continue;
      }

      IterableSet<SETNode> childChain = parent.get_Body2ChildChain().get(subBody);
      HashSet childUnion = new HashSet();

      for (SETNode child : childChain) {
        IterableSet childBody = child.get_Body();
        childUnion.addAll(childBody);

        if (childBody.contains(characterizingStmt)) {

          for (Iterator<AugmentedStmt> asIt = get_Body().snapshotIterator(); asIt.hasNext();) {
            AugmentedStmt as = asIt.next();
            if (childBody.contains(as) == false) {
              remove_AugmentedStmt(as);
            } else if ((child instanceof SETControlFlowNode) && ((child instanceof SETUnconditionalWhileNode) == false)) {
              SETControlFlowNode scfn = (SETControlFlowNode) child;

              if ((scfn.get_CharacterizingStmt() == as) || ((as.cpreds.size() == 1) && (as.get_Stmt() instanceof GotoStmt)
                  && (scfn.get_CharacterizingStmt() == as.cpreds.get(0)))) {
                remove_AugmentedStmt(as);
              }
            }
          }

          return true;
        }
      }
    }

    return true;
  }
}
