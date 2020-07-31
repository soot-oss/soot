package soot.grimp;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.Local;
import soot.PackManager;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.JimpleBody;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StmtBody;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.StmtBox;
import soot.options.Options;

/** Implementation of the Body class for the Grimp IR. */
public class GrimpBody extends StmtBody {
  private static final Logger logger = LoggerFactory.getLogger(GrimpBody.class);

  /**
   * Construct an empty GrimpBody
   **/

  GrimpBody(SootMethod m) {
    super(m);
  }

  @Override
  public Object clone() {
    Body b = Grimp.v().newBody(getMethod());
    b.importBodyContentsFrom(this);
    return b;
  }

  /**
   * Constructs a GrimpBody from the given Body.
   */
  GrimpBody(Body body) {
    super(body.getMethod());

    if (Options.v().verbose()) {
      logger.debug("[" + getMethod().getName() + "] Constructing GrimpBody...");
    }

    if (!(body instanceof JimpleBody)) {
      throw new RuntimeException("Can only construct GrimpBody's from JimpleBody's (for now)");
    }
    JimpleBody jBody = (JimpleBody) body;

    for (Local loc : jBody.getLocals()) {
      getLocals().add(loc);
      // getLocals().add((Local)loc.clone());
    }

    final HashMap<Stmt, Stmt> oldToNew = new HashMap<Stmt, Stmt>(getUnits().size() * 2 + 1, 0.7f);
    ArrayList<Unit> updates = new ArrayList<Unit>();

    /* we should Grimpify the Stmt's here... */
    for (Unit u : jBody.getUnits()) {
      Stmt oldStmt = (Stmt) u;
      final StmtBox newStmtBox = (StmtBox) Grimp.v().newStmtBox(null);
      final StmtBox updateStmtBox = (StmtBox) Grimp.v().newStmtBox(null);

      /* we can't have a general StmtSwapper on Grimp.v() */
      /* because we need to collect a list of updates */
      oldStmt.apply(new AbstractStmtSwitch() {
        @Override
        public void caseAssignStmt(AssignStmt s) {
          newStmtBox.setUnit(Grimp.v().newAssignStmt(s));
        }

        @Override
        public void caseIdentityStmt(IdentityStmt s) {
          newStmtBox.setUnit(Grimp.v().newIdentityStmt(s));
        }

        @Override
        public void caseBreakpointStmt(BreakpointStmt s) {
          newStmtBox.setUnit(Grimp.v().newBreakpointStmt(s));
        }

        @Override
        public void caseInvokeStmt(InvokeStmt s) {
          newStmtBox.setUnit(Grimp.v().newInvokeStmt(s));
        }

        @Override
        public void caseEnterMonitorStmt(EnterMonitorStmt s) {
          newStmtBox.setUnit(Grimp.v().newEnterMonitorStmt(s));
        }

        @Override
        public void caseExitMonitorStmt(ExitMonitorStmt s) {
          newStmtBox.setUnit(Grimp.v().newExitMonitorStmt(s));
        }

        @Override
        public void caseGotoStmt(GotoStmt s) {
          newStmtBox.setUnit(Grimp.v().newGotoStmt(s));
          updateStmtBox.setUnit(s);
        }

        @Override
        public void caseIfStmt(IfStmt s) {
          newStmtBox.setUnit(Grimp.v().newIfStmt(s));
          updateStmtBox.setUnit(s);
        }

        @Override
        public void caseLookupSwitchStmt(LookupSwitchStmt s) {
          newStmtBox.setUnit(Grimp.v().newLookupSwitchStmt(s));
          updateStmtBox.setUnit(s);
        }

        @Override
        public void caseNopStmt(NopStmt s) {
          newStmtBox.setUnit(Grimp.v().newNopStmt(s));
        }

        @Override
        public void caseReturnStmt(ReturnStmt s) {
          newStmtBox.setUnit(Grimp.v().newReturnStmt(s));
        }

        @Override
        public void caseReturnVoidStmt(ReturnVoidStmt s) {
          newStmtBox.setUnit(Grimp.v().newReturnVoidStmt(s));
        }

        @Override
        public void caseTableSwitchStmt(TableSwitchStmt s) {
          newStmtBox.setUnit(Grimp.v().newTableSwitchStmt(s));
          updateStmtBox.setUnit(s);
        }

        @Override
        public void caseThrowStmt(ThrowStmt s) {
          newStmtBox.setUnit(Grimp.v().newThrowStmt(s));
        }
      });

      /* map old Expr's to new Expr's. */
      Stmt newStmt = (Stmt) newStmtBox.getUnit();
      for (ValueBox box : newStmt.getUseBoxes()) {
        box.setValue(Grimp.v().newExpr(box.getValue()));
      }
      for (ValueBox box : newStmt.getDefBoxes()) {
        box.setValue(Grimp.v().newExpr(box.getValue()));
      }

      getUnits().add(newStmt);
      oldToNew.put(oldStmt, newStmt);
      if (updateStmtBox.getUnit() != null) {
        updates.add(updateStmtBox.getUnit());
      }
      if (oldStmt.hasTag("LineNumberTag")) {
        newStmt.addTag(oldStmt.getTag("LineNumberTag"));
      }
      if (oldStmt.hasTag("SourceLnPosTag")) {
        newStmt.addTag(oldStmt.getTag("SourceLnPosTag"));
      }
    }

    /* fixup stmt's which have had moved targets */
    for (Unit u : updates) {
      Stmt stmt = (Stmt) u;

      stmt.apply(new AbstractStmtSwitch() {
        @Override
        public void caseGotoStmt(GotoStmt s) {
          GotoStmt newStmt = (GotoStmt) oldToNew.get(s);
          newStmt.setTarget(oldToNew.get((Stmt) newStmt.getTarget()));
        }

        @Override
        public void caseIfStmt(IfStmt s) {
          IfStmt newStmt = (IfStmt) oldToNew.get(s);
          newStmt.setTarget(oldToNew.get(newStmt.getTarget()));
        }

        @Override
        public void caseLookupSwitchStmt(LookupSwitchStmt s) {
          LookupSwitchStmt newStmt = (LookupSwitchStmt) oldToNew.get(s);
          newStmt.setDefaultTarget(oldToNew.get((Stmt) newStmt.getDefaultTarget()));
          Unit[] newTargList = new Unit[newStmt.getTargetCount()];
          for (int i = 0; i < newTargList.length; i++) {
            newTargList[i] = oldToNew.get((Stmt) newStmt.getTarget(i));
          }
          newStmt.setTargets(newTargList);
        }

        @Override
        public void caseTableSwitchStmt(TableSwitchStmt s) {
          TableSwitchStmt newStmt = (TableSwitchStmt) oldToNew.get(s);
          newStmt.setDefaultTarget(oldToNew.get((Stmt) newStmt.getDefaultTarget()));
          int tc = newStmt.getHighIndex() - newStmt.getLowIndex() + 1;
          LinkedList<Unit> newTargList = new LinkedList<Unit>();
          for (int i = 0; i < tc; i++) {
            newTargList.add(oldToNew.get((Stmt) newStmt.getTarget(i)));
          }
          newStmt.setTargets(newTargList);
        }
      });
    }

    for (Trap oldTrap : jBody.getTraps()) {
      getTraps().add(Grimp.v().newTrap(oldTrap.getException(), oldToNew.get((Stmt) oldTrap.getBeginUnit()),
          oldToNew.get((Stmt) oldTrap.getEndUnit()), oldToNew.get((Stmt) oldTrap.getHandlerUnit())));
    }

    PackManager.v().getPack("gb").apply(this);
  }
}
