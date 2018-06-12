package soot.javaToJimple.toolkits;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Jennifer Lhotak
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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.EqExpr;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

public class CondTransformer extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(CondTransformer.class);

  public CondTransformer(Singletons.Global g) {
  }

  public static CondTransformer v() {
    return G.v().soot_javaToJimple_toolkits_CondTransformer();
  }

  private static final int SEQ_LENGTH = 6;
  private Stmt[] stmtSeq = new Stmt[SEQ_LENGTH];
  private boolean sameGoto = true;

  protected void internalTransform(Body b, String phaseName, Map options) {

    // logger.debug("running cond and/or transformer");
    boolean change = true;
    /*
     * the idea is to look for groups of statements of the form if cond goto L0 if cond goto L0 z0 = 1 goto L1 L0: z0 = 0 L1:
     * if z0 == 0 goto L2 conseq L2: altern
     *
     * and transform to if cond goto L0 if cond goto L0 conseq L0: altern
     *
     */

    while (change) {
      Iterator it = b.getUnits().iterator();
      int pos = 0;
      while (it.hasNext()) {
        change = false;
        Stmt s = (Stmt) it.next();
        if (testStmtSeq(s, pos)) {
          pos++;
        }
        if (pos == 6) {
          // found seq now transform then continue
          // logger.debug("found sequence will transform");
          change = true;
          break;
        }
      }
      if (change) {
        transformBody(b, (Stmt) it.next());
        pos = 0;
        stmtSeq = new Stmt[SEQ_LENGTH];
      }
    }
  }

  private void transformBody(Body b, Stmt next) {
    // change target of stmts 0 and 1 to target of stmt 5
    // remove stmts 2, 3, 4, 5
    Stmt newTarget = null;
    Stmt oldTarget = null;
    if (sameGoto) {
      newTarget = ((IfStmt) stmtSeq[5]).getTarget();
    } else {
      newTarget = next;
      oldTarget = ((IfStmt) stmtSeq[5]).getTarget();
    }
    ((IfStmt) stmtSeq[0]).setTarget(newTarget);
    ((IfStmt) stmtSeq[1]).setTarget(newTarget);

    for (int i = 2; i <= 5; i++) {
      b.getUnits().remove(stmtSeq[i]);
    }
    if (!sameGoto) {
      b.getUnits().insertAfter(Jimple.v().newGotoStmt(oldTarget), stmtSeq[1]);
    }
  }

  private boolean testStmtSeq(Stmt s, int pos) {
    switch (pos) {
      case 0: {
        if (s instanceof IfStmt) {
          stmtSeq[pos] = s;
          return true;
        }
        break;
      }
      case 1: {
        if (s instanceof IfStmt) {
          if (sameTarget(stmtSeq[pos - 1], s)) {
            stmtSeq[pos] = s;
            return true;
          }
        }
        break;
      }
      case 2: {
        if (s instanceof AssignStmt) {
          stmtSeq[pos] = s;
          if ((((AssignStmt) s).getRightOp() instanceof IntConstant)
              && (((IntConstant) ((AssignStmt) s).getRightOp())).value == 0) {
            sameGoto = false;
          }
          return true;
        }
        break;
      }
      case 3: {
        if (s instanceof GotoStmt) {
          stmtSeq[pos] = s;
          return true;
        }
        break;
      }
      case 4: {
        if (s instanceof AssignStmt) {
          if (isTarget(((IfStmt) stmtSeq[0]).getTarget(), s) && sameLocal(stmtSeq[2], s)) {
            stmtSeq[pos] = s;
            return true;
          }
        }
        break;
      }
      case 5: {
        if (s instanceof IfStmt) {
          if (isTarget((Stmt) ((GotoStmt) stmtSeq[3]).getTarget(), s) && sameCondLocal(stmtSeq[4], s)
              && (((IfStmt) s).getCondition() instanceof EqExpr)) {
            stmtSeq[pos] = s;
            return true;
          } else if (isTarget((Stmt) ((GotoStmt) stmtSeq[3]).getTarget(), s) && sameCondLocal(stmtSeq[4], s)) {
            stmtSeq[pos] = s;
            sameGoto = false;
            return true;
          }
        }
        break;
      }

      default: {
        break;
      }
    }
    return false;
  }

  private boolean sameTarget(Stmt s1, Stmt s2) {
    IfStmt is1 = (IfStmt) s1;
    IfStmt is2 = (IfStmt) s2;
    if (is1.getTarget().equals(is2.getTarget())) {
      return true;
    }
    return false;
  }

  private boolean isTarget(Stmt s1, Stmt s) {
    if (s1.equals(s)) {
      return true;
    }
    return false;
  }

  private boolean sameLocal(Stmt s1, Stmt s2) {
    AssignStmt as1 = (AssignStmt) s1;
    AssignStmt as2 = (AssignStmt) s2;
    if (as1.getLeftOp().equals(as2.getLeftOp())) {
      return true;
    }
    return false;
  }

  private boolean sameCondLocal(Stmt s1, Stmt s2) {
    AssignStmt as1 = (AssignStmt) s1;
    IfStmt is2 = (IfStmt) s2;
    if (is2.getCondition() instanceof BinopExpr) {
      BinopExpr bs2 = (BinopExpr) is2.getCondition();
      if (as1.getLeftOp().equals(bs2.getOp1())) {
        return true;
      }
    }
    return false;
  }
}
