package soot.dava.toolkits.base.AST.analysis;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
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

import soot.Type;
import soot.Value;
import soot.dava.internal.AST.ASTAndCondition;
import soot.dava.internal.AST.ASTBinaryCondition;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTOrCondition;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.AST.ASTUnaryCondition;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.javaRep.DVariableDeclarationStmt;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
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
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;

public class AnalysisAdapter implements Analysis {

  public void defaultCase(Object o) {
    // do nothing
  }

  @Override
  public void caseASTMethodNode(ASTMethodNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTLabeledBlockNode(ASTLabeledBlockNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTSwitchNode(ASTSwitchNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTIfNode(ASTIfNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTIfElseNode(ASTIfElseNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTWhileNode(ASTWhileNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTForLoopNode(ASTForLoopNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTDoWhileNode(ASTDoWhileNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTTryNode(ASTTryNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
    defaultCase(node);
  }

  @Override
  public void caseASTUnaryCondition(ASTUnaryCondition uc) {
    defaultCase(uc);
  }

  @Override
  public void caseASTBinaryCondition(ASTBinaryCondition bc) {
    defaultCase(bc);
  }

  @Override
  public void caseASTAndCondition(ASTAndCondition ac) {
    defaultCase(ac);
  }

  @Override
  public void caseASTOrCondition(ASTOrCondition oc) {
    defaultCase(oc);
  }

  @Override
  public void caseType(Type t) {
    defaultCase(t);
  }

  @Override
  public void caseDefinitionStmt(DefinitionStmt s) {
    defaultCase(s);
  }

  @Override
  public void caseReturnStmt(ReturnStmt s) {
    defaultCase(s);
  }

  @Override
  public void caseInvokeStmt(InvokeStmt s) {
    defaultCase(s);
  }

  @Override
  public void caseThrowStmt(ThrowStmt s) {
    defaultCase(s);
  }

  @Override
  public void caseDVariableDeclarationStmt(DVariableDeclarationStmt s) {
    defaultCase(s);
  }

  @Override
  public void caseStmt(Stmt s) {
    defaultCase(s);
  }

  @Override
  public void caseValue(Value v) {
    defaultCase(v);
  }

  @Override
  public void caseExpr(Expr e) {
    defaultCase(e);
  }

  @Override
  public void caseRef(Ref r) {
    defaultCase(r);
  }

  @Override
  public void caseBinopExpr(BinopExpr be) {
    defaultCase(be);
  }

  @Override
  public void caseUnopExpr(UnopExpr ue) {
    defaultCase(ue);
  }

  @Override
  public void caseNewArrayExpr(NewArrayExpr nae) {
    defaultCase(nae);
  }

  @Override
  public void caseNewMultiArrayExpr(NewMultiArrayExpr nmae) {
    defaultCase(nmae);
  }

  @Override
  public void caseInstanceOfExpr(InstanceOfExpr ioe) {
    defaultCase(ioe);
  }

  @Override
  public void caseInvokeExpr(InvokeExpr ie) {
    defaultCase(ie);
  }

  @Override
  public void caseInstanceInvokeExpr(InstanceInvokeExpr iie) {
    defaultCase(iie);
  }

  @Override
  public void caseCastExpr(CastExpr ce) {
    defaultCase(ce);
  }

  @Override
  public void caseArrayRef(ArrayRef ar) {
    defaultCase(ar);
  }

  @Override
  public void caseInstanceFieldRef(InstanceFieldRef ifr) {
    defaultCase(ifr);
  }

  @Override
  public void caseStaticFieldRef(StaticFieldRef sfr) {
    defaultCase(sfr);
  }
}
