package soot.dava.toolkits.base.finders;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2004 - 20055 Nomair A. Naeem
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.G;
import soot.Local;
import soot.RefType;
import soot.Singletons;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.dava.Dava;
import soot.dava.DavaBody;
import soot.dava.RetriggerAnalysisException;
import soot.dava.internal.SET.SETNode;
import soot.dava.internal.SET.SETSynchronizedBlockNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.toolkits.graph.StronglyConnectedComponentsFast;
import soot.util.IterableSet;

public class SynchronizedBlockFinder implements FactFinder {
  public SynchronizedBlockFinder(Singletons.Global g) {
  }

  public static SynchronizedBlockFinder v() {
    return G.v().soot_dava_toolkits_base_finders_SynchronizedBlockFinder();
  }

  private HashMap<AugmentedStmt, Map<Value, Integer>> as2ml;
  private DavaBody davaBody;

  /*
   * Nomair A Naeem 08-FEB-2005 monitorLocalSet contains all the locals that are used in monitors monitorEnterSet contains
   * all enterMonitorStmts Statements
   */
  private IterableSet monitorLocalSet, monitorEnterSet;

  private final Integer WHITE = new Integer(0);// never visited in DFS
  private final Integer GRAY = new Integer(1);// visited but not finished
  private final Integer BLACK = new Integer(2);// finished

  private final int UNKNOWN = -100000; // Note there are at most 65536 monitor
  // exits in a method.
  private final Integer VARIABLE_INCR = new Integer(UNKNOWN);

  private final String THROWABLE = "java.lang.Throwable";

  public void find(DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException {
    davaBody = body;
    Dava.v().log("SynchronizedBlockFinder::find()");

    as2ml = new HashMap();

    IterableSet synchronizedBlockFacts = body.get_SynchronizedBlockFacts();
    synchronizedBlockFacts.clear();

    set_MonitorLevels(asg);

    Map<AugmentedStmt, IterableSet> as2synchSet = build_SynchSets();

    IterableSet usedMonitors = new IterableSet();

    AugmentedStmt previousStmt = null;

    Iterator asgit = asg.iterator();
    while (asgit.hasNext()) {
      // going through all augmentedStmts

      AugmentedStmt as = (AugmentedStmt) asgit.next();
      if (as.get_Stmt() instanceof EnterMonitorStmt) {
        // for each monitor enter stmt found

        IterableSet synchSet = as2synchSet.get(as);
        if (synchSet != null) {
          IterableSet synchBody = get_BodyApproximation(as, synchSet);
          Value local = ((EnterMonitorStmt) as.get_Stmt()).getOp();
          Value copiedLocal = null;

          /*
           * Synch bug due to copy stmts r2 = r1 enter monitor r1 synch body exit monitor r2 we need to realize that r1 and
           * r2 are the same implemented as a hack, the correct approach is reaching copies
           */
          if (previousStmt != null) {
            Stmt previousS = previousStmt.get_Stmt();
            if (previousS instanceof DefinitionStmt) {
              DefinitionStmt previousDef = (DefinitionStmt) previousS;
              Value rightPrevious = previousDef.getRightOp();
              if (rightPrevious.toString().compareTo(local.toString()) == 0) {
                copiedLocal = previousDef.getLeftOp();
              }
            }
          }

          Integer level = as2ml.get(as).get(local);
          Iterator enit = body.get_ExceptionFacts().iterator();

          // going through all exception nodes in the DavaBody
          boolean done = false;
          IterableSet origSynchBody = synchBody;
          while (enit.hasNext()) {
            ExceptionNode en = (ExceptionNode) enit.next();
            synchBody = (IterableSet) origSynchBody.clone();
            // see if the two bodies match exactly

            if (verify_CatchBody(en, synchBody, local, copiedLocal)) {
              if (SET.nest(new SETSynchronizedBlockNode(en, local))) {
                done = true;
                // System.out.println("synch block created");
                Iterator ssit = synchSet.iterator();
                while (ssit.hasNext()) {
                  AugmentedStmt ssas = (AugmentedStmt) ssit.next();
                  Stmt sss = ssas.get_Stmt();

                  /*
                   * Jeromes Implementation: changed because of copy stmt bug
                   *
                   * if ((sss instanceof MonitorStmt) && (((MonitorStmt) sss).getOp() == local) && (((Integer) ((HashMap)
                   * as2ml.get( ssas)).get( local)).equals( level)) && (usedMonitors.contains( ssas) == false)){
                   */
                  if (sss instanceof MonitorStmt) {
                    if ((((MonitorStmt) sss).getOp() == local) && ((as2ml.get(ssas).get(local)).equals(level))
                        && (usedMonitors.contains(ssas) == false)) {

                      usedMonitors.add(ssas);
                    } else {
                      if ((((MonitorStmt) sss).getOp() == copiedLocal) && (usedMonitors.contains(ssas) == false)) {
                        // note we dont check levels in
                        // this case
                        usedMonitors.add(ssas);
                      }
                    }
                  }
                }
                synchronizedBlockFacts.add(en);
              }
              break;
            } else {
              // throw new
              // RuntimeException("Could not verify approximated Synchronized body");
            }
          }
          if (!done) {
            throw new RuntimeException("Could not verify approximated Synchronized body!\n" + "Method:\n" + body.getMethod()
                + "Body:\n" + "===============================================================\n" + body.getUnits()
                + "===============================================================\n");
          }
        } // non null synch set for this enter monitor stmt
      } // if the augmented stmt was a enter monitor stmt
      previousStmt = as;
    } // going through all augmented stmts

    IterableSet<AugmentedStmt> monitorFacts = body.get_MonitorFacts();
    monitorFacts.clear();

    for (AugmentedStmt as : asg) {
      if ((as.get_Stmt() instanceof MonitorStmt) && (usedMonitors.contains(as) == false)) {
        monitorFacts.add(as);
      }
    }
  }

  /*
   * as2locals: contains all locals which have unknown level for this augmented stmt viAugStmts: contains all augmented stmts
   * which have some unknown level local in them
   */

  private void find_VariableIncreasing(AugmentedStmtGraph asg, HashMap local2level_template,
      LinkedList<AugmentedStmt> viAugStmts, HashMap<AugmentedStmt, LinkedList<Value>> as2locals) {
    StronglyConnectedComponentsFast scc = new StronglyConnectedComponentsFast(asg);
    IterableSet viSeeds = new IterableSet();
    HashMap as2color = new HashMap(), as2rml = new HashMap();

    // as2rml contains each augmented stmt as key with a local2Level mapping
    // as value
    Iterator asgit = asg.iterator();
    while (asgit.hasNext()) {
      as2rml.put(asgit.next(), local2level_template.clone());
    }

    // loop through all the strongly connected components in the graph
    Iterator<List> sccit = scc.getTrueComponents().iterator();
    while (sccit.hasNext()) {
      // componentList contains augmentedstmts belonging to a particular
      // scc
      List componentList = sccit.next();

      // component contains augmentedstmts belonging to a particular scc
      IterableSet component = new IterableSet();
      component.addAll(componentList);

      // add to as2color each augstmt belonging to the component as the
      // key and the color white as the value
      Iterator cit = component.iterator();
      while (cit.hasNext()) {
        as2color.put(cit.next(), WHITE);
      }

      // DFS and mark enough of the variable increasing points to get
      // started.
      AugmentedStmt seedStmt = (AugmentedStmt) component.getFirst();
      // seedStmt contains the first stmt of the scc

      DFS_Scc(seedStmt, component, as2rml, as2color, seedStmt, viSeeds);
      // viSeeds contain augmentedStmts which have unknown level since
      // their level was less
      // than that of their predecessors
    }

    // viSeeds contains all the augmentedStmts with unknown level in the
    // method (for all scc)
    IterableSet worklist = new IterableSet();
    worklist.addAll(viSeeds);

    // Propegate the variable increasing property.
    while (worklist.isEmpty() == false) {
      AugmentedStmt as = (AugmentedStmt) worklist.getFirst();
      worklist.removeFirst();
      HashMap local2level = (HashMap) as2rml.get(as);

      // get all sucessors of the viSeed
      Iterator sit = as.csuccs.iterator();
      while (sit.hasNext()) {
        AugmentedStmt sas = (AugmentedStmt) sit.next();
        HashMap<Value, Integer> slocal2level = (HashMap<Value, Integer>) as2rml.get(sas);

        Iterator mlsit = monitorLocalSet.iterator();
        while (mlsit.hasNext()) {
          Value local = (Value) mlsit.next();

          // if the level for a local is set to unknown and the level
          // for the same local is not
          // set to unknown in the successor set it and add it to the
          // worklist
          if ((local2level.get(local) == VARIABLE_INCR) && (slocal2level.get(local) != VARIABLE_INCR)) {
            slocal2level.put(local, VARIABLE_INCR);

            if (worklist.contains(sas) == false) {
              worklist.addLast(sas);
            }
          }
        }
      }
    }
    /*
     * At the end of this the worklist is empty the unknown level of locals of an augmentedstmt present in the viSeed has
     * been propagated through out the stmts reachable from that stmt
     */

    // Summarize the variable increasing information for the
    // set_MonitorLevels() function.
    asgit = asg.iterator();
    while (asgit.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) asgit.next();
      HashMap local2level = (HashMap) as2rml.get(as);

      Iterator mlsit = monitorLocalSet.iterator();
      while (mlsit.hasNext()) {
        // for each local involved in monitor stmts
        Value local = (Value) mlsit.next();

        if (local2level.get(local) == VARIABLE_INCR) {

          /*
           * Nomair A. Naeem 08-FEB-2005 BUG IN CODE. The getLast method returns a NoSuchElementException if list is empty.
           * Protect this by checking isEmpty
           */
          if (!viAugStmts.isEmpty()) {
            if (viAugStmts.getLast() != as) {
              viAugStmts.addLast(as);
            }
          } else {
            // System.out.println("List was empty added as");
            viAugStmts.addLast(as);
          }

          LinkedList<Value> locals = null;

          if ((locals = as2locals.get(as)) == null) {
            locals = new LinkedList<Value>();
            as2locals.put(as, locals);
          }

          locals.addLast(local);
        }
      }
    }
  }

  /*
   * Inputs: as: the seed stmt, i.e. the first statement of a strongly connected component component: the strongly connected
   * stmt being processed as2rml: a hashmap which contains an augmentedstmt as key and a hashmap with local to level mapping
   * as value as2color: maps each augmentedstmt of a scc to a color (white initially) viSeeds: variable increasing levels,
   * initially empty
   *
   * Purpose: makes a traversal of the entire scc and increases the levels of locals when used in monitor enter decreases the
   * levels of locals when leaving monitor if a successor has never been visited invoke same method on it with updated level
   * information if a sucessor has been visited before and has a lower level than a seed the level of the sucessor is marked
   * unknown and is added to the viSeeds list
   */
  private void DFS_Scc(AugmentedStmt as, IterableSet component, HashMap as2rml, HashMap as2color, AugmentedStmt seedStmt,
      IterableSet viSeeds) {
    as2color.put(as, GRAY);

    Stmt s = as.get_Stmt();
    // get local to level mapping of the augmented stmt
    HashMap<Value, Integer> local2level = (HashMap<Value, Integer>) as2rml.get(as);

    if (s instanceof MonitorStmt) {
      Value local = ((MonitorStmt) s).getOp();

      if (s instanceof EnterMonitorStmt) {
        // its an enter hence increase
        // level for this local
        local2level.put(local, new Integer(local2level.get(local).intValue() + 1));
      } else {
        // its an exit stmt hence reduce level
        local2level.put(local, new Integer(local2level.get(local).intValue() - 1));
      }
    }

    // get all successors of the augmented stmt
    Iterator sit = as.csuccs.iterator();
    // going through sucessors
    while (sit.hasNext()) {
      AugmentedStmt sas = (AugmentedStmt) sit.next();

      if (component.contains(sas) == false) {
        continue;
      }

      // get the local2Level hashmap for the successor
      HashMap<Value, Integer> slocal2level = (HashMap<Value, Integer>) as2rml.get(sas);
      // get the color for the sucessor
      Integer scolor = (Integer) as2color.get(sas);

      if (scolor.equals(WHITE)) {
        // the augmented stmt hasnt been handled yet
        Iterator mlsit = monitorLocalSet.iterator();
        while (mlsit.hasNext()) {
          // for all locals which are used in monitor stmts
          Value local = (Value) mlsit.next();
          /*
           * update the sucessors hashtable with level values from the seed stmt The loca2Level is the hashmap of
           * local-->level for the seed The sLocal2Level is the hashmap of local --> level for the successor of the seed
           */
          slocal2level.put(local, local2level.get(local));
        }

        // do recursive call on the sucessor
        DFS_Scc(sas, component, as2rml, as2color, seedStmt, viSeeds);
      }

      else {
        // if the augmented stmt has been visited before
        Iterator mlsit = monitorLocalSet.iterator();
        while (mlsit.hasNext()) {
          Value local = (Value) mlsit.next();

          if (slocal2level.get(local).intValue() < local2level.get(local).intValue()) {
            // if the sucessors value for the level of a local is
            // less than that of the level of the seed
            // make the level for this local to be unknown-->
            // VARIABLE_INCR
            slocal2level.put(local, VARIABLE_INCR);

            // add this to the viSeeds list
            if (viSeeds.contains(sas) == false) {
              viSeeds.add(sas);
            }
          }
        }
      }
    }

    // mark augmented stmt as done
    as2color.put(as, BLACK);
  }

  /*
   * Created a synch set for each enter monitor stmt. The synch set contains all sucessors of the monitor enter stmt which is
   * dominated by the enter stmt and the level is greater or equal to that of the enter stmt
   */
  private Map<AugmentedStmt, IterableSet> build_SynchSets() {
    HashMap<AugmentedStmt, IterableSet> as2synchSet = new HashMap<AugmentedStmt, IterableSet>();

    Iterator mesit = monitorEnterSet.iterator();
    monitorEnterLoop: while (mesit.hasNext()) {
      // going through monitor enter stmts
      AugmentedStmt headAs = (AugmentedStmt) mesit.next();
      Value local = ((EnterMonitorStmt) headAs.get_Stmt()).getOp();
      IterableSet synchSet = new IterableSet();

      // get the monitor level for the local uses in the enter stmt
      int monitorLevel = (as2ml.get(headAs).get(local)).intValue();
      IterableSet worklist = new IterableSet();
      worklist.add(headAs);

      while (worklist.isEmpty() == false) {
        AugmentedStmt as = (AugmentedStmt) worklist.getFirst();
        worklist.removeFirst();

        Stmt s = as.get_Stmt();
        if ((s instanceof DefinitionStmt) && (((DefinitionStmt) s).getLeftOp() == local)) {
          continue monitorEnterLoop;
        }

        synchSet.add(as);

        Iterator sit = as.csuccs.iterator();
        while (sit.hasNext()) {
          AugmentedStmt sas = (AugmentedStmt) sit.next();
          // get the sucessors monitor level
          int sml = (as2ml.get(sas).get(local)).intValue();

          /*
           * if the sucessor is dominated by the head stmt and the level is greater or equal to that of the head and is not
           * waiting to be analysed and is not in the synchSet.. then add it to worklist
           */
          if (sas.get_Dominators().contains(headAs) && (sml >= monitorLevel) && (worklist.contains(sas) == false)
              && (synchSet.contains(sas) == false)) {
            worklist.addLast(sas);
          }
        }
      }

      as2synchSet.put(headAs, synchSet);
    }
    return as2synchSet;
  }

  /*
   * MonitorLocalSet: contains all locals used in monitor enter statements MonitorEnterSet: contains all monitor enter
   * statements as2ml : key is each augmented stmt of the augmented graph, value is a hashmap which contains a local as key
   * and the level as value
   */
  private void set_MonitorLevels(AugmentedStmtGraph asg) {
    monitorLocalSet = new IterableSet();
    monitorEnterSet = new IterableSet();

    // Identify the locals that are used in monitor statements, and all the
    // monitor enters.
    Iterator<AugmentedStmt> asgit = asg.iterator();
    while (asgit.hasNext()) {
      AugmentedStmt as = asgit.next();
      Stmt s = as.get_Stmt();

      if (s instanceof MonitorStmt) {
        Value local = ((MonitorStmt) s).getOp();

        // if the monitorLocalSet does not contain this local add it
        if (monitorLocalSet.contains(local) == false) {
          monitorLocalSet.add(local);
        }

        // add the monitor enter statement to the monitorEnter Set
        if (s instanceof EnterMonitorStmt) {
          monitorEnterSet.add(as);
        }
      }
    }

    // Set up a base monitor lock level of 0 for all monitor locals.
    HashMap local2level_template = new HashMap();
    Iterator mlsit = monitorLocalSet.iterator();
    while (mlsit.hasNext()) {
      // add the local as key and value 0 as value
      local2level_template.put(mlsit.next(), new Integer(0));
    }

    // Give each statement the base monitor lock levels.
    asgit = asg.iterator();
    while (asgit.hasNext()) {
      // the augmented stmt is key and the whole hashMap with all the
      // local-->value mapping is the key
      as2ml.put(asgit.next(), (Map<Value, Integer>) local2level_template.clone());
    }

    LinkedList<AugmentedStmt> viAugStmts = new LinkedList<AugmentedStmt>();
    HashMap<AugmentedStmt, LinkedList<Value>> incrAs2locals = new HashMap<AugmentedStmt, LinkedList<Value>>();

    // setup the variable increasing monitor levels
    find_VariableIncreasing(asg, local2level_template, viAugStmts, incrAs2locals);
    /*
     * At this time the viAugStmts contains all augments Stmt which have some local that has unknown level the incrAs2locals
     * contains a map of augmented stmt to a linkedlist which contains locals with unknown level
     */

    // going through all augmented stmts with some local with unknown level
    Iterator<AugmentedStmt> viasit = viAugStmts.iterator();
    while (viasit.hasNext()) {
      AugmentedStmt vias = viasit.next();
      Map local2level = as2ml.get(vias);

      // getting the list of locals with unknown level for this augmented
      // stmt
      Iterator lit = incrAs2locals.get(vias).iterator();
      while (lit.hasNext()) {
        // marking the level for this local as unknown
        local2level.put(lit.next(), VARIABLE_INCR);
      }
    }

    IterableSet worklist = new IterableSet();
    worklist.addAll(monitorEnterSet);

    // Flow monitor lock levels.
    while (worklist.isEmpty() == false) {
      // going through all monitor enter stmts
      AugmentedStmt as = (AugmentedStmt) worklist.getFirst();
      worklist.removeFirst();

      Map<Value, Integer> cur_local2level = as2ml.get(as);

      Iterator pit = as.cpreds.iterator();
      while (pit.hasNext()) {
        // going through preds of an enter monitor stmt
        AugmentedStmt pas = (AugmentedStmt) pit.next();
        Stmt s = as.get_Stmt();

        Map pred_local2level = as2ml.get(pas);

        mlsit = monitorLocalSet.iterator();
        while (mlsit.hasNext()) {
          Value local = (Value) mlsit.next();

          int predLevel = ((Integer) pred_local2level.get(local)).intValue();
          Stmt ps = pas.get_Stmt();

          if (predLevel == UNKNOWN) {
            // increasing.
            continue;
          }

          if (ps instanceof ExitMonitorStmt) {

            ExitMonitorStmt ems = (ExitMonitorStmt) ps;
            /*
             * if the predecessor stmt is a monitor exit and the local it exits is the same as this local and the predLevel
             * is greater than 0 decrease the pred level
             */
            if ((ems.getOp() == local) && (predLevel > 0)) {
              predLevel--;
            }
          }

          if (s instanceof EnterMonitorStmt) {
            EnterMonitorStmt ems = (EnterMonitorStmt) s;
            /*
             * if the stmt is a monitor enter (which is true for the initial worklist) and the local it enters is the same as
             * this local and the predLevel is greater or equal to 0 increase the pred level
             */
            if ((ems.getOp() == local) && (predLevel >= 0)) {
              predLevel++;
            }
          }

          int curLevel = cur_local2level.get(local).intValue();

          /*
           * if the pred level is greater than the current level update current level add the sucessors of the stmt to the
           * worklist
           */
          if (predLevel > curLevel) {
            cur_local2level.put(local, new Integer(predLevel));

            Iterator sit = as.csuccs.iterator();
            while (sit.hasNext()) {
              Object so = sit.next();

              if (worklist.contains(so) == false) {
                worklist.add(so);
              }
            }
          }
        }
      }
    }
  }

  /*
   * If a stmt is removed from the synchBody then all other stmts which are dominated by this stmt should also be removed
   * from the synchBody
   */
  private void removeOtherDominatedStmts(IterableSet synchBody, AugmentedStmt sas) {
    ArrayList toRemove = new ArrayList();

    Iterator it = synchBody.iterator();
    while (it.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) it.next();
      IterableSet doms = as.get_Dominators();
      if (doms.contains(sas)) {
        // System.out.println("\n\nstmt:"+as+" is dominated by removed stmt"+sas);
        toRemove.add(as);
      }
    }

    it = toRemove.iterator();
    while (it.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) it.next();
      // System.out.println("Removed dominated stmt: "+as);
      synchBody.remove(as);
    }
  }

  private boolean verify_CatchBody(ExceptionNode en, IterableSet synchBody, Value monitorVariable, Value copiedLocal) {
    // System.out.println("starting verification");
    {
      /*
       * synchBody is a likely superset of exception Node since the synchBody contains a goto stmt to the stmt right after
       * the "to be created synch block" See if this is the case and remove the stmt
       */

      IterableSet tryBodySet = en.get_TryBody();
      Iterator tempIt = tryBodySet.iterator();
      AugmentedStmt tempas = null;

      // to compute last stmt in a tryBody one should find the stmt whose
      // sucessor is not in the tryBody
      outer: while (tempIt.hasNext()) {
        tempas = (AugmentedStmt) tempIt.next();

        // get natural sucessors
        Iterator succIt = tempas.bsuccs.iterator();
        while (succIt.hasNext()) {
          AugmentedStmt succAS = (AugmentedStmt) succIt.next();
          if (!tryBodySet.contains(succAS)) {
            // tempas has a sucessor which is not part of the
            // trybody hence this is the last stmt
            break outer;
          }
        }
        // System.out.println(tempas);
      }

      // tempas contains the last augmentedStmt
      if (tempas != null) {

        // retrieving successors
        Iterator sit = tempas.bsuccs.iterator();
        while (sit.hasNext()) {
          AugmentedStmt sas = (AugmentedStmt) sit.next();
          // System.out.println("Removing: successor of last stmt:"+sas.get_Stmt());

          // this is the stmt which is the successor of the try block
          // which shouldnt be in the synchronized block
          // remove the stmt if present in synchBody

          synchBody.remove(sas);
          // System.out.println("Here, removed: "+sas);
          // should remove all other stmts in the synchBody which are
          // dominated by this stmt
          removeOtherDominatedStmts(synchBody, sas);
        }
      }
    }

    /*
     * In the abc created code when a synch body occurs within a try block There is an error because the synchBody created
     * has addition statements regarding the catching of exception due to the outer try block
     *
     * Since these statements are not part of the synch body they should be removed
     */
    {
      Iterator tempIt = en.get_TryBody().iterator();
      while (tempIt.hasNext()) {
        AugmentedStmt as = (AugmentedStmt) tempIt.next();
        if (as.get_Stmt() instanceof ExitMonitorStmt) {
          // System.out.println("All possible sucessors of as (CSUCCS):"+as.csuccs);
          List csuccs = as.csuccs;

          // remove the natural sucessors
          csuccs.remove(as.bsuccs);
          // now csuccs have the exception sucessors
          // System.out.println("Exception sucessors of Exit (CSUCCS:"+csuccs);

          Iterator succIt = csuccs.iterator();
          while (succIt.hasNext()) {
            AugmentedStmt as1 = (AugmentedStmt) succIt.next();
            if (as1.get_Stmt() instanceof GotoStmt) {
              Unit target = ((soot.jimple.internal.JGotoStmt) as1.get_Stmt()).getTarget();
              if (target instanceof DefinitionStmt) {
                DefinitionStmt defStmt = (DefinitionStmt) target;

                /*
                 * These are the caught exceptions (can be more than once if there are multiple areas of protection Keep the
                 * one which is of type Throwable
                 */

                Value asnFrom = defStmt.getRightOp();

                // if not a caught exception of type throwable
                // remove from synch
                if (!(asnFrom instanceof CaughtExceptionRef)) {
                  // System.out.println("REMOVING:"+defStmt+" since this is not a caughtexception def");
                  synchBody.remove(as1);
                  // should remove all other stmts in the
                  // synchBody which are dominated by this
                  // stmt
                  removeOtherDominatedStmts(synchBody, as1);
                } else {
                  Value leftOp = defStmt.getLeftOp();
                  // System.out.println("the left op is:"+leftOp);

                  /*
                   * Only keep this if it is of throwable type
                   */
                  HashSet params = new HashSet();
                  params.addAll(davaBody.get_CaughtRefs());

                  Iterator localIt = davaBody.getLocals().iterator();
                  String typeName = "";
                  while (localIt.hasNext()) {
                    Local local = (Local) localIt.next();
                    if (local.toString().compareTo(leftOp.toString()) == 0) {
                      Type t = local.getType();

                      typeName = t.toString();
                      break;
                    }
                  }

                  if (!(typeName.compareTo(THROWABLE) == 0)) {
                    // System.out.println("REMOVING:"+defStmt+
                    // " since the caughtException not throwable type");
                    synchBody.remove(as1);
                    // should remove all other stmts in the
                    // synchBody which are dominated by this
                    // stmt
                    removeOtherDominatedStmts(synchBody, as1);
                  } else {
                    // System.out.println("KEEPING"+defStmt);
                    // System.out.println((RefType)((CaughtExceptionRef)
                    // asnFrom).getType());
                  }
                }
              } else {
                // System.out.println("\n\nnot definition"+target);
                // System.out.println("Removed"+as1);
                synchBody.remove(as1);
                // System.out.println(as1.bsuccs.get(0));
                synchBody.remove(as1.bsuccs.get(0));
                // should remove all other stmts in the
                // synchBody which are dominated by this stmt
                removeOtherDominatedStmts(synchBody, as1);
              }
            }
          }
        }
      }
    }

    /*
     * There might be some unwanted stmts in the synch body. These are likely to be stmts which have no predecessor in the
     * synchbody. Remove these stmts. NOTE: the entry of the synchbody is a special case since its predecessor is also not
     * going to be in the synchbody but we SHOULD not remove the entry point
     */

    // find the entry point of synchbody
    /*
     * One way of doing this is going through the synch body and finiding a stmt whose predecessor is an enter monitor stmt
     * NOT present in the synchBody
     */
    AugmentedStmt synchEnter = null;
    Iterator synchIt = synchBody.iterator();

    outerLoop: while (synchIt.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) synchIt.next();

      Iterator pit = as.cpreds.iterator();
      while (pit.hasNext()) {
        AugmentedStmt pas = (AugmentedStmt) pit.next();

        if (synchBody.contains(pas) == false) {
          // the as stmt has a predecessor which is not part of the
          // synchBody
          Stmt pasStmt = pas.get_Stmt();
          if (pasStmt instanceof EnterMonitorStmt) {
            // found the entry point to the synchBody
            synchEnter = as;
            break outerLoop;
          }
        }
      }
    }

    if (synchEnter == null) {
      throw new RuntimeException("Could not find enter stmt of the synchBody: " + davaBody.getMethod().getSignature());
    }

    // System.out.println("Enter stmt of synchBody is:"+synchEnter);

    /*
     * Now that we know the exception case we can go through the synchBody and remove all stmts whose predecessor does not
     * belong to the synchBody
     */

    boolean unChanged = false;
    while (!unChanged) {
      unChanged = true;
      List<AugmentedStmt> toRemove = new ArrayList<AugmentedStmt>();
      synchIt = synchBody.iterator();
      while (synchIt.hasNext()) {
        AugmentedStmt synchAs = (AugmentedStmt) synchIt.next();
        if (synchAs == synchEnter) {
          // entrypoint is an exception so just continue
          continue;
        }
        Iterator pit = synchAs.cpreds.iterator();
        boolean remove = true;
        while (pit.hasNext()) {
          AugmentedStmt pAs = (AugmentedStmt) pit.next();
          if (synchBody.contains(pAs)) {
            // one of the preds of this as is in the synchBody so
            // dont remove
            remove = false;
          }
        } // going through preds of the synchAs
        if (remove) {
          // all preds not present in synchBody
          toRemove.add(synchAs);
        }
      } // going through the synchBody
      if (toRemove.size() > 0) {
        // none of the preds of synchAs are in the synchBody hence this
        // stmt is unreachable
        synchBody.removeAll(toRemove);
        // System.out.println("Removing:"+toRemove+" since none of its preds are in the synchBody");
        unChanged = false;
      }
    }

    // see if the two bodies match
    if ((en.get_Body().equals(synchBody) == false)) {
      // System.out.println("returning unverified since synchBody does not match");
      // System.out.println("\n\nEN BODY:\n"+en.get_Body());
      // System.out.println("\n\nSYNCH BODY:\n"+synchBody);
      return false;
    }

    /*
     * The two bodies match check if the exception thrown is of type "throwable" and that there is only one catchlist The
     * reason for doing this is that synchronized blocks get converted to a try catch block with the catch , catching the
     * throwable exception and this is the ONLY catch
     */
    if ((en.get_Exception().getName().equals(THROWABLE) == false) || (en.get_CatchList().size() > 1)) {
      // System.out.println("returning unverified here");
      return false;
    }

    // at this point we know that the two bodies match and that the en
    // exception is a throwable exception

    /*
     * find the entry point of the catch boody The entry point of the catch body is the first stmt in the catchbody This can
     * be found by finiding the stmt whose predecessor is not part of the catch body
     */
    IterableSet catchBody = en.get_CatchBody();
    AugmentedStmt entryPoint = null;

    Iterator it = catchBody.iterator();
    catchBodyLoop: while (it.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) it.next();

      Iterator pit = as.cpreds.iterator();
      while (pit.hasNext()) {
        AugmentedStmt pas = (AugmentedStmt) pit.next();

        if (catchBody.contains(pas) == false) {
          entryPoint = as;
          break catchBodyLoop;
        }
      }
    }

    // Horror upon horrors, what follows is a hard coded state machine.

    /*
     * To verify the catchbody one has to go through the catchbody ONE stmt at a time. This is NOT a good approach as it is
     * using PATTERN MATCHING
     *
     * FIRST STEP Need to verify that the entrypoint stmt and any goto stmts following it all point to the entrypoint
     */

    Unit entryPointTarget = null;
    if (entryPoint.get_Stmt() instanceof GotoStmt) {
      // System.out.println("Entry point is a goto stmt getting the target");
      entryPointTarget = ((soot.jimple.internal.JGotoStmt) entryPoint.get_Stmt()).getTarget();
    }

    AugmentedStmt as = entryPoint;
    if (as.bsuccs.size() != 1) {
      // System.out.println("here1");
      return false;
    }

    while (as.get_Stmt() instanceof GotoStmt) {
      as = (AugmentedStmt) as.bsuccs.get(0);
      // if ((as.bsuccs.size() != 1) || ((as != entryPoint) &&
      // (as.cpreds.size() != 1))) {
      // return false;
      // }
      if (as.bsuccs.size() != 1) {
        // System.out.println("here2a");
        return false;
      }
      if (entryPointTarget != null) {
        if ((as.get_Stmt() != entryPointTarget) && (as.cpreds.size() != 1)) {
          if (as.cpreds.size() != 1) {
            // System.out.println("here2b");
            return false;
          }
        }
      } else {
        if ((as != entryPoint) && (as.cpreds.size() != 1)) {
          // System.out.println("here2c");
          return false;
        }
      }
    }

    // so now we are not at a goto stmt for sure
    // according to the creation pattern of the try catch block we should be
    // at the definition stmt
    // e.g. <var> := @caughtexception

    Stmt s = as.get_Stmt();

    if (!(s instanceof DefinitionStmt)) {
      // System.out.println("here3");
      return false;
    }

    DefinitionStmt ds = (DefinitionStmt) s;
    Value asnFrom = ds.getRightOp();

    // if not a caught exception of type throwable we have a problem
    if (!((asnFrom instanceof CaughtExceptionRef)
        && (((RefType) ((CaughtExceptionRef) asnFrom).getType()).getSootClass().getName().equals(THROWABLE)))) {
      // System.out.println("here4");
      return false;
    }

    Value throwlocal = ds.getLeftOp();
    // System.out.println("Throw local is:"+throwlocal);

    /*
     * The escuccs contains all the EXCEPTION SUCESSORS of the caughtexception stmt
     */
    IterableSet esuccs = new IterableSet();
    esuccs.addAll(as.csuccs);
    esuccs.removeAll(as.bsuccs);

    // sucessor of definition stmt
    as = (AugmentedStmt) as.bsuccs.get(0);
    s = as.get_Stmt();

    // this COULD be a copy stmt in which case update the throwlocal
    while (s instanceof DefinitionStmt
        && (((DefinitionStmt) s).getRightOp().toString().compareTo(throwlocal.toString()) == 0)) {

      // System.out.println("copy stmt using throwLocal found");
      // System.out.println("Right op is :"+((DefinitionStmt)s).getRightOp());
      // return false;
      throwlocal = ((DefinitionStmt) s).getLeftOp();

      // the sucessor of this stmt MIGHT be the exitmonitor stmt
      as = (AugmentedStmt) as.bsuccs.get(0);
      s = as.get_Stmt();
    }

    if (as.bsuccs.size() != 1) {
      // should have one true sucessor
      // System.out.println("here5a");
      return false;
    }

    if (as.cpreds.size() != 1) {
      // should have one true predecessor
      // System.out.println("here5b");
      return false;
    }

    // need to check whether this stmt is protected by the same exception
    // block as the whole catchbody
    checkProtectionArea(as, ds);

    s = as.get_Stmt();
    if (!(s instanceof ExitMonitorStmt)) {
      // System.out.println("Not an exit monitor stmt"+s);
      return false;
    }

    if ((((ExitMonitorStmt) s).getOp() != monitorVariable)) {

      if ((((ExitMonitorStmt) s).getOp() != copiedLocal)) {
        // System.out.println("exit monitor variable does not match enter monitor variable");
        return false;
      }
    }

    // next stmt should be a throw stmt
    as = (AugmentedStmt) as.bsuccs.get(0);
    if ((as.bsuccs.size() != 0) || (as.cpreds.size() != 1) || (verify_ESuccs(as, esuccs) == false)) {
      // System.out.println("here7");
      return false;
    }

    s = as.get_Stmt();

    if (!((s instanceof ThrowStmt) && (((ThrowStmt) s).getOp() == throwlocal))) {
      // System.out.println("here8"+s+" Throw local is:"+throwlocal);
      return false;
    }

    return true;
  }

  /*
   * DefinitionStmt s is the start of the area of protection The exception sucessors of as should directly or indirectly
   * point to s
   */
  private boolean checkProtectionArea(AugmentedStmt as, DefinitionStmt s) {
    IterableSet esuccs = new IterableSet();
    esuccs.addAll(as.csuccs);
    esuccs.removeAll(as.bsuccs);

    // esuccs contains the exception sucessors of as
    // System.out.println("ESUCCS are:"+esuccs);

    Iterator it = esuccs.iterator();
    while (it.hasNext()) {
      // going through each exception sucessor
      AugmentedStmt tempas = (AugmentedStmt) it.next();
      Stmt temps = tempas.get_Stmt();
      if (temps instanceof GotoStmt) {
        Unit target = ((GotoStmt) temps).getTarget();
        if (target != s) {
          // System.out.println("DID NOT Match indirectly");
          return false;
        } else {
          // System.out.println("Matched indirectly");
        }
      } else {
        if (temps != s) {
          // System.out.println("DID NOT Match directly");
          return false;
        } else {
          // System.out.println("Matched directly");
        }
      }

    }
    return true;
  }

  private boolean verify_ESuccs(AugmentedStmt as, IterableSet ref) {
    IterableSet esuccs = new IterableSet();

    esuccs.addAll(as.csuccs);
    esuccs.removeAll(as.bsuccs);

    // System.out.println("ESUCCS are:"+esuccs);
    // System.out.println("ref are:"+ref);
    return esuccs.equals(ref);
  }

  /*
   * Input: head: this is the monitor enter stmt synchSet: this contains all sucessors of head which have level greater or
   * equal to the head
   */
  private IterableSet get_BodyApproximation(AugmentedStmt head, IterableSet synchSet) {
    IterableSet body = (IterableSet) synchSet.clone();
    Value local = ((EnterMonitorStmt) head.get_Stmt()).getOp();
    Integer level = as2ml.get(head).get(local);

    // System.out.println("BODY"+body);
    body.remove(head);

    Iterator bit = body.snapshotIterator();
    while (bit.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) bit.next();
      Stmt s = as.get_Stmt();

      if ((s instanceof ExitMonitorStmt) && (((ExitMonitorStmt) s).getOp() == local)
          && ((as2ml.get(as).get(local)).equals(level))) {

        Iterator sit = as.csuccs.iterator();
        while (sit.hasNext()) {
          AugmentedStmt sas = (AugmentedStmt) sit.next();

          // if not dominated by head continue with next stmt in body
          if (sas.get_Dominators().contains(head) == false) {
            continue;
          }

          Stmt ss = sas.get_Stmt();

          if (((ss instanceof GotoStmt) || (ss instanceof ThrowStmt)) && (body.contains(sas) == false)) {
            // if (ss instanceof ThrowStmt && (body.contains( sas)
            // == false)){
            // System.out.println("adding"+sas);
            body.add(sas);
          }
        }
      }
    }

    return body;
  }
}
