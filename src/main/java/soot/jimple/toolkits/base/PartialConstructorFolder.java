package soot.jimple.toolkits.base;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

public class PartialConstructorFolder extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(PartialConstructorFolder.class);
  // public JimpleConstructorFolder( Singletons.Global g ) {}
  // public static JimpleConstructorFolder v() { return G.v().JimpleConstructorFolder(); }

  private List<Type> types;

  public void setTypes(List<Type> t) {
    types = t;
  }

  public List<Type> getTypes() {
    return types;
  }

  /**
   * This method pushes all newExpr down to be the stmt directly before every invoke of the init only if they are in the
   * types list
   */

  public void internalTransform(Body b, String phaseName, Map<String, String> options) {
    JimpleBody body = (JimpleBody) b;

    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Folding Jimple constructors...");
    }

    Chain<Unit> units = body.getUnits();
    List<Unit> stmtList = new ArrayList<Unit>();
    stmtList.addAll(units);

    Iterator<Unit> it = stmtList.iterator();
    Iterator<Unit> nextStmtIt = stmtList.iterator();
    // start ahead one
    nextStmtIt.next();

    LocalUses localUses = LocalUses.Factory.newLocalUses(body);

    /* fold in NewExpr's with specialinvoke's */
    while (it.hasNext()) {
      Stmt s = (Stmt) it.next();

      if (!(s instanceof AssignStmt)) {
        continue;
      }

      /* this should be generalized to ArrayRefs */
      // only deal with stmts that are an local = newExpr
      Value lhs = ((AssignStmt) s).getLeftOp();
      if (!(lhs instanceof Local)) {
        continue;
      }

      Value rhs = ((AssignStmt) s).getRightOp();
      if (!(rhs instanceof NewExpr)) {
        continue;
      }

      // check if very next statement is invoke -->
      // this indicates there is no control flow between
      // new and invoke and should do nothing
      if (nextStmtIt.hasNext()) {
        Stmt next = (Stmt) nextStmtIt.next();
        if (next instanceof InvokeStmt) {
          InvokeStmt invoke = (InvokeStmt) next;

          if (invoke.getInvokeExpr() instanceof SpecialInvokeExpr) {
            SpecialInvokeExpr invokeExpr = (SpecialInvokeExpr) invoke.getInvokeExpr();
            if (invokeExpr.getBase() == lhs) {
              break;
            }
          }
        }
      }

      // check if new is in the types list - only process these
      if (!types.contains(((NewExpr) rhs).getType())) {
        continue;
      }

      List<UnitValueBoxPair> lu = localUses.getUsesOf(s);
      Iterator<UnitValueBoxPair> luIter = lu.iterator();
      boolean MadeNewInvokeExpr = false;

      while (luIter.hasNext()) {
        Unit use = ((luIter.next())).unit;
        if (!(use instanceof InvokeStmt)) {
          continue;
        }
        InvokeStmt is = (InvokeStmt) use;
        if (!(is.getInvokeExpr() instanceof SpecialInvokeExpr)
            || lhs != ((SpecialInvokeExpr) is.getInvokeExpr()).getBase()) {
          continue;
        }

        // make a new one here
        AssignStmt constructStmt
            = Jimple.v().newAssignStmt(((DefinitionStmt) s).getLeftOp(), ((DefinitionStmt) s).getRightOp());
        constructStmt.setRightOp(Jimple.v().newNewExpr(((NewExpr) rhs).getBaseType()));
        MadeNewInvokeExpr = true;

        // redirect jumps
        use.redirectJumpsToThisTo(constructStmt);
        // insert new one here
        units.insertBefore(constructStmt, use);

        constructStmt.addTag(s.getTag("SourceLnPosTag"));
      }
      if (MadeNewInvokeExpr) {
        units.remove(s);
      }
    }
  }
}
