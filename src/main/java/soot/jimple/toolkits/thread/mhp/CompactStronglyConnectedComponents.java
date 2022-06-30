package soot.jimple.toolkits.thread.mhp;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.toolkits.scalar.FlowSet;
import soot.util.Chain;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class CompactStronglyConnectedComponents {

  long compactNodes = 0;
  long add = 0;

  public CompactStronglyConnectedComponents(PegGraph pg) {
    Chain mainPegChain = pg.getMainPegChain();
    compactGraph(mainPegChain, pg);
    compactStartChain(pg);
    // PegToDotFile printer = new PegToDotFile(pg, false, "compact");
    System.err.println("compact SCC nodes: " + compactNodes);
    System.err.println(" number of compacting scc nodes: " + add);
  }

  private void compactGraph(Chain chain, PegGraph peg) {

    Set canNotBeCompacted = peg.getCanNotBeCompacted();
    // testCan(speg.getMainPegChain(), canNotBeCompacted);
    // SCC scc = new SCC(chain, peg);
    SCC scc = new SCC(chain.iterator(), peg);
    List<List<Object>> sccList = scc.getSccList();
    // testSCC(sccList);
    Iterator<List<Object>> sccListIt = sccList.iterator();
    while (sccListIt.hasNext()) {
      List s = sccListIt.next();
      if (s.size() > 1) {
        // printSCC(s);
        if (!checkIfContainsElemsCanNotBeCompacted(s, canNotBeCompacted)) {
          add++;
          compact(s, chain, peg);

        }
      }

    }
    // testListSucc(peg);

  }

  private void compactStartChain(PegGraph graph) {
    Set maps = graph.getStartToThread().entrySet();
    for (Iterator iter = maps.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      List runMethodChainList = (List) entry.getValue();
      Iterator it = runMethodChainList.iterator();
      while (it.hasNext()) {
        Chain chain = (Chain) it.next();
        compactGraph(chain, graph);
      }
    }

  }

  private boolean checkIfContainsElemsCanNotBeCompacted(List list, Set canNotBeCompacted) {
    Iterator sccIt = list.iterator();
    // System.out.println("sccList: ");
    while (sccIt.hasNext()) {
      JPegStmt node = (JPegStmt) sccIt.next();
      // System.out.println("elem of scc:");
      if (canNotBeCompacted.contains(node)) {
        // System.out.println("find a syn method!!");
        return true;
      }

    }
    return false;
  }

  private void compact(List list, Chain chain, PegGraph peg) {

    Iterator it = list.iterator();
    FlowSet allNodes = peg.getAllNodes();
    HashMap unitToSuccs = peg.getUnitToSuccs();
    HashMap unitToPreds = peg.getUnitToPreds();
    List<Object> newPreds = new ArrayList<Object>();
    List<Object> newSuccs = new ArrayList<Object>();

    while (it.hasNext()) {
      JPegStmt s = (JPegStmt) it.next();
      // Replace the SCC with a list node.
      {
        Iterator predsIt = peg.getPredsOf(s).iterator();
        while (predsIt.hasNext()) {
          Object pred = predsIt.next();
          List succsOfPred = peg.getSuccsOf(pred);
          succsOfPred.remove(s);
          if (!list.contains(pred)) {
            newPreds.add(pred);
            succsOfPred.add(list);

          }
        }
      }
      {
        Iterator succsIt = peg.getSuccsOf(s).iterator();
        while (succsIt.hasNext()) {
          Object succ = succsIt.next();
          List predsOfSucc = peg.getPredsOf(succ);
          predsOfSucc.remove(s);
          if (!list.contains(succ)) {
            newSuccs.add(succ);
            predsOfSucc.add(list);
          }
        }
      }

    }
    unitToSuccs.put(list, newSuccs);
    // System.out.println("put list"+list+"\n"+ "newSuccs: "+newSuccs);
    unitToPreds.put(list, newPreds);
    allNodes.add(list);
    chain.add(list);
    updateMonitor(peg, list);
    {
      it = list.iterator();
      while (it.hasNext()) {
        JPegStmt s = (JPegStmt) it.next();
        chain.remove(s);
        allNodes.remove(s);
        unitToSuccs.remove(s);
        unitToPreds.remove(s);
      }

    }
    // System.out.println("inside compactSCC");
    // testListSucc(peg);

    // add for get experimental results
    compactNodes += list.size();
  }

  private void updateMonitor(PegGraph pg, List list) {
    // System.out.println("=======update monitor===");
    // add list to corresponding monitor objects sets
    Set maps = pg.getMonitor().entrySet();

    // System.out.println("---test list----");
    // testList(list);

    for (Iterator iter = maps.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      FlowSet fs = (FlowSet) entry.getValue();

      Iterator it = list.iterator();
      while (it.hasNext()) {
        Object obj = it.next();
        if (fs.contains(obj)) {

          fs.add(list);
          break;

          // System.out.println("add list to monitor: "+entry.getKey());

        }

      }

    }
    // System.out.println("=======update monitor==end====");
  }
}
