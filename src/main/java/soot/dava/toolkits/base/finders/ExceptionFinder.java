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

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import soot.G;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.dava.Dava;
import soot.dava.DavaBody;
import soot.dava.RetriggerAnalysisException;
import soot.dava.internal.SET.SETNode;
import soot.dava.internal.SET.SETTryNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.jimple.GotoStmt;
import soot.jimple.Stmt;
import soot.util.IterableSet;

public class ExceptionFinder implements FactFinder {
  public ExceptionFinder(Singletons.Global g) {
  }

  public static ExceptionFinder v() {
    return G.v().soot_dava_toolkits_base_finders_ExceptionFinder();
  }

  public void find(DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException {
    Dava.v().log("ExceptionFinder::find()");

    for (ExceptionNode en : body.get_ExceptionFacts()) {
      if (body.get_SynchronizedBlockFacts().contains(en)) {
        continue;
      }

      IterableSet<AugmentedStmt> fullBody = new IterableSet<AugmentedStmt>();

      for (IterableSet<AugmentedStmt> is : en.get_CatchList()) {
        fullBody.addAll(is);
      }

      fullBody.addAll(en.get_TryBody());

      if (SET.nest(new SETTryNode(fullBody, en, asg, body)) == false) {
        throw new RetriggerAnalysisException();
      }
    }
  }

  public void preprocess(DavaBody body, AugmentedStmtGraph asg) {
    Dava.v().log("ExceptionFinder::preprocess()");

    IterableSet<ExceptionNode> enlist = new IterableSet<ExceptionNode>();

    // Find the first approximation for all the try catch bodies.
    {
      for (Trap trap : body.getTraps()) {
        Unit endUnit = trap.getEndUnit();

        // get the body of the try block as a raw read of the area of
        // protection
        IterableSet<AugmentedStmt> tryBody = new IterableSet<AugmentedStmt>();

        Iterator<Unit> btit = body.getUnits().iterator(trap.getBeginUnit());
        for (Unit u = (Unit) btit.next(); u != endUnit; u = (Unit) btit.next()) {
          tryBody.add(asg.get_AugStmt((Stmt) u));
        }

        enlist.add(new ExceptionNode(tryBody, trap.getException(), asg.get_AugStmt((Stmt) trap.getHandlerUnit())));
      }
    }

    // Add in gotos that may escape the try body (created by the indirection
    // introduced in DavaBody).
    {
      for (ExceptionNode en : enlist) {
        IterableSet<AugmentedStmt> try_body = en.get_TryBody();

        for (AugmentedStmt tras : try_body) {
          for (AugmentedStmt pas : tras.cpreds) {
            Stmt ps = pas.get_Stmt();

            if ((try_body.contains(pas) == false) && (ps instanceof GotoStmt)) {
              boolean add_it = true;

              for (AugmentedStmt pred : pas.cpreds) {
                if ((add_it = try_body.contains(pred)) == false) {
                  break;
                }
              }

              if (add_it) {
                en.add_TryStmt(pas);
              }
            }
          }
        }
      }
    }

    // Split up the try blocks until they cause no nesting problems.
    splitLoop: while (true) {
      // refresh the catch bodies
      {
        for (ExceptionNode enode : enlist) {
          enode.refresh_CatchBody(this);
        }
      }

      // split for inter-exception nesting problems
      {
        ExceptionNode[] ena = new ExceptionNode[enlist.size()];
        Iterator<ExceptionNode> enlit = enlist.iterator();
        for (int i = 0; enlit.hasNext(); i++) {
          ena[i] = (ExceptionNode) enlit.next();
        }

        for (int i = 0; i < ena.length - 1; i++) {
          ExceptionNode eni = ena[i];
          for (int j = i + 1; j < ena.length; j++) {
            ExceptionNode enj = ena[j];

            IterableSet<AugmentedStmt> eniTryBody = eni.get_TryBody(), enjTryBody = enj.get_TryBody();

            if ((eniTryBody.equals(enjTryBody) == false) && (eniTryBody.intersects(enjTryBody))) {

              if ((eniTryBody.isSupersetOf(enj.get_Body())) || (enjTryBody.isSupersetOf(eni.get_Body()))) {
                continue;
              }

              IterableSet<AugmentedStmt> newTryBody = eniTryBody.intersection(enjTryBody);

              if (newTryBody.equals(enjTryBody)) {
                eni.splitOff_ExceptionNode(newTryBody, asg, enlist);
              } else {
                enj.splitOff_ExceptionNode(newTryBody, asg, enlist);
              }

              continue splitLoop;
            }
          }
        }
      }

      // split for intra-try-body issues
      {
        for (ExceptionNode en : enlist) {
          // Get the try block entry points
          IterableSet<AugmentedStmt> tryBody = en.get_TryBody();
          LinkedList<AugmentedStmt> heads = new LinkedList<AugmentedStmt>();
          for (AugmentedStmt as : tryBody) {
            if (as.cpreds.isEmpty()) {
              heads.add(as);
              continue;
            }

            for (AugmentedStmt pred : as.cpreds) {
              if (tryBody.contains(pred) == false) {
                heads.add(as);
                break;
              }
            }
          }

          HashSet<AugmentedStmt> touchSet = new HashSet<AugmentedStmt>();
          touchSet.addAll(heads);

          // Break up the try block for all the so-far detectable
          // parts.
          AugmentedStmt head = heads.removeFirst();
          IterableSet<AugmentedStmt> subTryBlock = new IterableSet<AugmentedStmt>();
          LinkedList<AugmentedStmt> worklist = new LinkedList<AugmentedStmt>();

          worklist.add(head);

          while (worklist.isEmpty() == false) {
            AugmentedStmt as = worklist.removeFirst();

            subTryBlock.add(as);
            for (AugmentedStmt sas : as.csuccs) {
              if ((tryBody.contains(sas) == false) || (touchSet.contains(sas))) {
                continue;
              }

              touchSet.add(sas);

              if (sas.get_Dominators().contains(head)) {
                worklist.add(sas);
              } else {
                heads.addLast(sas);
              }
            }
          }

          if (heads.isEmpty() == false) {
            en.splitOff_ExceptionNode(subTryBlock, asg, enlist);
            continue splitLoop;
          }
        }
      }

      break;
    }

    // Aggregate the try blocks.
    {
      LinkedList<ExceptionNode> reps = new LinkedList<ExceptionNode>();
      HashMap<Serializable, LinkedList<IterableSet<AugmentedStmt>>> hCode2bucket
          = new HashMap<Serializable, LinkedList<IterableSet<AugmentedStmt>>>();
      HashMap<Serializable, ExceptionNode> tryBody2exceptionNode = new HashMap<Serializable, ExceptionNode>();

      for (ExceptionNode en : enlist) {
        int hashCode = 0;
        IterableSet<AugmentedStmt> curTryBody = en.get_TryBody();

        for (AugmentedStmt au : curTryBody) {
          hashCode ^= au.hashCode();
        }
        Integer I = new Integer(hashCode);

        LinkedList<IterableSet<AugmentedStmt>> bucket = hCode2bucket.get(I);
        if (bucket == null) {
          bucket = new LinkedList<IterableSet<AugmentedStmt>>();
          hCode2bucket.put(I, bucket);
        }

        ExceptionNode repExceptionNode = null;

        for (IterableSet<AugmentedStmt> bucketTryBody : bucket) {
          if (bucketTryBody.equals(curTryBody)) {
            repExceptionNode = tryBody2exceptionNode.get(bucketTryBody);
            break;
          }
        }

        if (repExceptionNode == null) {
          tryBody2exceptionNode.put(curTryBody, en);
          bucket.add(curTryBody);
          reps.add(en);
        } else {
          repExceptionNode.add_CatchBody(en);
        }
      }

      enlist.clear();
      enlist.addAll(reps);
    }

    body.get_ExceptionFacts().clear();
    body.get_ExceptionFacts().addAll(enlist);

  }

  public IterableSet<AugmentedStmt> get_CatchBody(AugmentedStmt handlerAugmentedStmt) {
    IterableSet<AugmentedStmt> catchBody = new IterableSet<AugmentedStmt>();
    LinkedList<AugmentedStmt> catchQueue = new LinkedList<AugmentedStmt>();

    catchBody.add(handlerAugmentedStmt);
    catchQueue.addAll(handlerAugmentedStmt.csuccs);

    while (catchQueue.isEmpty() == false) {
      AugmentedStmt as = (AugmentedStmt) catchQueue.removeFirst();

      if (catchBody.contains(as)) {
        continue;
      }

      if (as.get_Dominators().contains(handlerAugmentedStmt)) {
        catchBody.add(as);
        catchQueue.addAll(as.csuccs);
      }
    }

    return catchBody;
  }
}
