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

package soot.dava.toolkits.base.finders;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.dava.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.javaRep.*;

public class AbruptEdgeFinder implements FactFinder {
	public AbruptEdgeFinder(Singletons.Global g) {
	}

	public static AbruptEdgeFinder v() {
		return G.v().soot_dava_toolkits_base_finders_AbruptEdgeFinder();
	}

	public void find(DavaBody body, AugmentedStmtGraph asg, SETNode SET)
			throws RetriggerAnalysisException {
		Dava.v().log("AbruptEdgeFinder::find()");

		SET.find_AbruptEdges(this);
	}

	public void find_Continues(SETNode SETParent, IterableSet body,
			IterableSet children) {
		if ((SETParent instanceof SETCycleNode) == false)
			return;

		SETCycleNode scn = (SETCycleNode) SETParent;
		IterableSet naturalPreds = ((SETNode) children.getLast())
				.get_NaturalExits();

		Iterator pit = scn.get_CharacterizingStmt().bpreds.iterator();
		while (pit.hasNext()) {
			AugmentedStmt pas = (AugmentedStmt) pit.next();

			if ((body.contains(pas)) && (naturalPreds.contains(pas) == false))
				((SETStatementSequenceNode) pas.myNode)
						.insert_AbruptStmt(new DAbruptStmt("continue", scn
								.get_Label()));
		}
	}

	public void find_Breaks(SETNode prev, SETNode cur) {
		IterableSet naturalPreds = prev.get_NaturalExits();

		Iterator pit = cur.get_EntryStmt().bpreds.iterator();
		while (pit.hasNext()) {
			AugmentedStmt pas = (AugmentedStmt) pit.next();

			if (prev.get_Body().contains(pas) == false)
				continue;

			if (naturalPreds.contains(pas) == false) {
				Object temp = pas.myNode;
				/*
				 * Nomair debugging bug number 29
				 */
				// System.out.println();
				// ((SETNode)temp).dump();
				// System.out.println("Statement is"+pas);
				((SETStatementSequenceNode) temp)
						.insert_AbruptStmt(new DAbruptStmt("break", prev
								.get_Label()));
			}
		}
	}
}
