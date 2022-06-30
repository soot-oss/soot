package soot.dava.toolkits.base.AST.traversals;

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

import java.util.List;

import soot.Local;
import soot.Value;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;

/*
 * Given a statement of interest this traversal checks whether
 * a, the defined variable is a local 
 * b, there is any def of the defined local of interest before
 * the given statement
 * 
 * if no then possible is set to true
 * else false
 */
public class InitializationDeclarationShortcut extends DepthFirstAdapter {
  AugmentedStmt ofInterest;
  boolean possible = false;
  Local definedLocal = null;
  int seenBefore = 0;// if there is a definition of local which is not by stmt of interest increment this

  public InitializationDeclarationShortcut(AugmentedStmt ofInterest) {
    this.ofInterest = ofInterest;
  }

  public InitializationDeclarationShortcut(boolean verbose, AugmentedStmt ofInterest) {
    super(verbose);
    this.ofInterest = ofInterest;
  }

  public boolean isShortcutPossible() {
    return possible;
  }

  /*
   * Check that the stmt of interest defines a local in the DVariableDeclarationNode of the method else set to false and stop
   */
  public void inASTMethodNode(ASTMethodNode node) {
    Stmt s = ofInterest.get_Stmt();
    // check this is a definition
    if (!(s instanceof DefinitionStmt)) {
      possible = false;
      return;
    }

    Value defined = ((DefinitionStmt) s).getLeftOp();
    if (!(defined instanceof Local)) {
      possible = false;
      return;
    }

    // check that this is a local defined in this method
    // its a sanity check
    List declaredLocals = node.getDeclaredLocals();
    if (!declaredLocals.contains(defined)) {
      possible = false;
      return;
    }
    definedLocal = (Local) defined;
  }

  public void inDefinitionStmt(DefinitionStmt s) {
    if (definedLocal == null) {
      return;
    }

    Value defined = (s).getLeftOp();
    if (!(defined instanceof Local)) {
      return;
    }

    if (defined.equals(definedLocal)) {
      // the local of interest is being defined

      // if this is the augmentedStmt of interest set possible to true if not already seen
      if (s.equals(ofInterest.get_Stmt())) {
        // it is the stmt of interest
        if (seenBefore == 0) {
          possible = true;
        } else {
          possible = false;
        }
      } else {
        // its a definition of the local of interest but not by the stmt of interest
        seenBefore++;
      }

    }
  }

}
