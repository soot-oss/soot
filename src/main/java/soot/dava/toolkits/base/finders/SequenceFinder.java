package soot.dava.toolkits.base.finders;

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

import soot.G;
import soot.Singletons;
import soot.dava.Dava;
import soot.dava.DavaBody;
import soot.dava.RetriggerAnalysisException;
import soot.dava.internal.SET.SETNode;
import soot.dava.internal.SET.SETStatementSequenceNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.util.IterableSet;

public class SequenceFinder implements FactFinder {
  public SequenceFinder(Singletons.Global g) {
  }

  public static SequenceFinder v() {
    return G.v().soot_dava_toolkits_base_finders_SequenceFinder();
  }

  public void find(DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException {
    Dava.v().log("SequenceFinder::find()");

    SET.find_StatementSequences(this, body);
  }

  public void find_StatementSequences(SETNode SETParent, IterableSet body, HashSet<AugmentedStmt> childUnion,
      DavaBody davaBody) {
    Iterator bit = body.iterator();
    while (bit.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) bit.next();

      if (childUnion.contains(as)) {
        continue;
      }

      IterableSet sequenceBody = new IterableSet();

      while (as.bpreds.size() == 1) {
        AugmentedStmt pas = (AugmentedStmt) as.bpreds.get(0);
        if ((body.contains(pas) == false) || (childUnion.contains(pas) == true)) {
          break;
        }

        as = pas;
      }

      while ((body.contains(as)) && (childUnion.contains(as) == false)) {

        childUnion.add(as);
        sequenceBody.addLast(as);

        if (as.bsuccs.isEmpty() == false) {
          as = (AugmentedStmt) as.bsuccs.get(0);
        }

        if (as.bpreds.size() != 1) {
          break;
        }
      }

      SETParent.add_Child(new SETStatementSequenceNode(sequenceBody, davaBody), SETParent.get_Body2ChildChain().get(body));
    }
  }
}
