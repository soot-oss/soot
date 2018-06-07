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

import java.util.Iterator;

import soot.G;
import soot.Singletons;
import soot.dava.Dava;
import soot.dava.DavaBody;
import soot.dava.RetriggerAnalysisException;
import soot.dava.internal.SET.SETCycleNode;
import soot.dava.internal.SET.SETNode;
import soot.dava.internal.SET.SETStatementSequenceNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.util.IterableSet;

public class AbruptEdgeFinder implements FactFinder {
  public AbruptEdgeFinder(Singletons.Global g) {
  }

  public static AbruptEdgeFinder v() {
    return G.v().soot_dava_toolkits_base_finders_AbruptEdgeFinder();
  }

  public void find(DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException {
    Dava.v().log("AbruptEdgeFinder::find()");

    SET.find_AbruptEdges(this);
  }

  public void find_Continues(SETNode SETParent, IterableSet body, IterableSet children) {
    if ((SETParent instanceof SETCycleNode) == false) {
      return;
    }

    SETCycleNode scn = (SETCycleNode) SETParent;
    IterableSet naturalPreds = ((SETNode) children.getLast()).get_NaturalExits();

    Iterator pit = scn.get_CharacterizingStmt().bpreds.iterator();
    while (pit.hasNext()) {
      AugmentedStmt pas = (AugmentedStmt) pit.next();

      if ((body.contains(pas)) && (naturalPreds.contains(pas) == false)) {
        ((SETStatementSequenceNode) pas.myNode).insert_AbruptStmt(new DAbruptStmt("continue", scn.get_Label()));
      }
    }
  }

  public void find_Breaks(SETNode prev, SETNode cur) {
    IterableSet naturalPreds = prev.get_NaturalExits();

    Iterator pit = cur.get_EntryStmt().bpreds.iterator();
    while (pit.hasNext()) {
      AugmentedStmt pas = (AugmentedStmt) pit.next();

      if (prev.get_Body().contains(pas) == false) {
        continue;
      }

      if (naturalPreds.contains(pas) == false) {
        Object temp = pas.myNode;
        /*
         * Nomair debugging bug number 29
         */
        // System.out.println();
        // ((SETNode)temp).dump();
        // System.out.println("Statement is"+pas);
        ((SETStatementSequenceNode) temp).insert_AbruptStmt(new DAbruptStmt("break", prev.get_Label()));
      }
    }
  }
}
