package soot.dava.internal.AST;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2005 Nomair A. Naeem
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

import soot.Unit;
import soot.UnitPrinter;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.ASTAnalysis;
import soot.dava.toolkits.base.AST.ASTWalker;
import soot.dava.toolkits.base.AST.TryContentsFinder;
import soot.dava.toolkits.base.AST.analysis.Analysis;

public class ASTStatementSequenceNode extends ASTNode {
  private List<AugmentedStmt> statementSequence;

  public ASTStatementSequenceNode(List<AugmentedStmt> statementSequence) {
    super();

    this.statementSequence = statementSequence;
  }

  public Object clone() {
    return new ASTStatementSequenceNode(statementSequence);
  }

  public void perform_Analysis(ASTAnalysis a) {
    if (a.getAnalysisDepth() > ASTAnalysis.ANALYSE_AST) {

      for (AugmentedStmt as : statementSequence) {
        ASTWalker.v().walk_stmt(a, as.get_Stmt());
      }
    }

    if (a instanceof TryContentsFinder) {
      TryContentsFinder.v().add_ExceptionSet(this, TryContentsFinder.v().remove_CurExceptionSet());
    }
  }

  public void toString(UnitPrinter up) {
    for (AugmentedStmt as : statementSequence) {
      // System.out.println("Stmt is:"+as.get_Stmt());
      Unit u = as.get_Stmt();
      up.startUnit(u);
      u.toString(up);
      up.literal(";");
      up.endUnit(u);
      up.newline();
    }
  }

  public String toString() {
    StringBuffer b = new StringBuffer();

    for (AugmentedStmt as : statementSequence) {
      b.append(as.get_Stmt().toString());
      b.append(";");
      b.append(NEWLINE);
    }

    return b.toString();
  }

  /*
   * Nomair A. Naeem, 7-FEB-05 Part of Visitor Design Implementation for AST See: soot.dava.toolkits.base.AST.analysis For
   * details
   */
  public List<AugmentedStmt> getStatements() {
    return statementSequence;
  }

  public void apply(Analysis a) {
    a.caseASTStatementSequenceNode(this);
  }

  /*
   * Nomair A. Naeem added 3-MAY-05
   */
  public void setStatements(List<AugmentedStmt> statementSequence) {
    this.statementSequence = statementSequence;
  }
}
