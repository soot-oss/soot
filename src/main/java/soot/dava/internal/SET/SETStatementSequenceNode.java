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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import soot.SootMethod;
import soot.Value;
import soot.dava.DavaBody;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.util.IterableSet;

public class SETStatementSequenceNode extends SETNode {
  private DavaBody davaBody;
  private boolean hasContinue;

  public SETStatementSequenceNode(IterableSet body, DavaBody davaBody) {
    super(body);
    add_SubBody(body);

    this.davaBody = davaBody;

    hasContinue = false;
  }

  public SETStatementSequenceNode(IterableSet body) {
    this(body, null);
  }

  public boolean has_Continue() {
    return hasContinue;
  }

  public IterableSet get_NaturalExits() {
    IterableSet c = new IterableSet();
    AugmentedStmt last = (AugmentedStmt) get_Body().getLast();

    if ((last.csuccs != null) && (last.csuccs.isEmpty() == false)) {
      c.add(last);
    }

    return c;
  }

  public ASTNode emit_AST() {
    List<AugmentedStmt> l = new LinkedList<AugmentedStmt>();

    boolean isStaticInitializer = davaBody.getMethod().getName().equals(SootMethod.staticInitializerName);

    Iterator it = get_Body().iterator();
    while (it.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) it.next();
      Stmt s = as.get_Stmt();

      if (davaBody != null) {

        if ((s instanceof ReturnVoidStmt) && (isStaticInitializer)) {
          continue;
        }

        if (s instanceof GotoStmt) {
          continue;
        }

        if (s instanceof MonitorStmt) {
          continue;
        }

        /*
         * January 12th 2006 Trying to fix the super problem we need to not ignore constructor unit i.e. this or super
         */
        if (s == davaBody.get_ConstructorUnit()) {
          // System.out.println("ALLOWING this.init STMT TO GET ADDED..............SETStatementSequenceNode");
          // continue;
        }

        if (s instanceof IdentityStmt) {
          IdentityStmt ids = (IdentityStmt) s;

          Value rightOp = ids.getRightOp(), leftOp = ids.getLeftOp();

          if (davaBody.get_ThisLocals().contains(leftOp)) {
            continue;
          }

          if (rightOp instanceof ParameterRef) {
            continue;
          }

          if (rightOp instanceof CaughtExceptionRef) {
            continue;
          }
        }
      }

      l.add(as);
    }

    if (l.isEmpty()) {
      return null;
    } else {
      return new ASTStatementSequenceNode(l);
    }
  }

  public AugmentedStmt get_EntryStmt() {
    return (AugmentedStmt) get_Body().getFirst();
  }

  public void insert_AbruptStmt(DAbruptStmt stmt) {
    if (hasContinue) {
      return;
    }

    get_Body().addLast(new AugmentedStmt(stmt));
    hasContinue = stmt.is_Continue();
  }

  protected boolean resolve(SETNode parent) {
    throw new RuntimeException("Attempting auto-nest a SETStatementSequenceNode.");
  }
}
