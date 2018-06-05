package soot.dava.toolkits.base.AST;

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

import soot.Value;
import soot.dava.internal.AST.ASTNode;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.Expr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.Ref;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;

public abstract class ASTAnalysis {
  public static final int ANALYSE_AST = 0, ANALYSE_STMTS = 1, ANALYSE_VALUES = 2;

  public abstract int getAnalysisDepth();

  public void analyseASTNode(ASTNode n) {
  }

  public void analyseDefinitionStmt(DefinitionStmt s) {
  }

  public void analyseReturnStmt(ReturnStmt s) {
  }

  public void analyseInvokeStmt(InvokeStmt s) {
  }

  public void analyseThrowStmt(ThrowStmt s) {
  }

  public void analyseStmt(Stmt s) {
  }

  public void analyseBinopExpr(BinopExpr v) {
  }

  public void analyseUnopExpr(UnopExpr v) {
  }

  public void analyseNewArrayExpr(NewArrayExpr v) {
  }

  public void analyseNewMultiArrayExpr(NewMultiArrayExpr v) {
  }

  public void analyseInstanceOfExpr(InstanceOfExpr v) {
  }

  public void analyseInstanceInvokeExpr(InstanceInvokeExpr v) {
  }

  public void analyseInvokeExpr(InvokeExpr v) {
  }

  public void analyseExpr(Expr v) {
  }

  public void analyseArrayRef(ArrayRef v) {
  }

  public void analyseInstanceFieldRef(InstanceFieldRef v) {
  }

  public void analyseRef(Ref v) {
  }

  public void analyseValue(Value v) {
  }
}
