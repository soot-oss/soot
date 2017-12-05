/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dava.toolkits.base.AST.analysis;


import soot.*;
import soot.jimple.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;

public interface Analysis{

    public void caseASTMethodNode(ASTMethodNode node);
    public void caseASTSynchronizedBlockNode(ASTSynchronizedBlockNode node);
    public void caseASTLabeledBlockNode (ASTLabeledBlockNode node);
    public void caseASTUnconditionalLoopNode (ASTUnconditionalLoopNode node);
    public void caseASTSwitchNode(ASTSwitchNode node);
    public void caseASTIfNode(ASTIfNode node);
    public void caseASTIfElseNode(ASTIfElseNode node);
    public void caseASTWhileNode(ASTWhileNode node);
    public void caseASTForLoopNode(ASTForLoopNode node);
    public void caseASTDoWhileNode(ASTDoWhileNode node);
    public void caseASTTryNode(ASTTryNode node);
    public void caseASTStatementSequenceNode(ASTStatementSequenceNode node);
    
    public void caseASTUnaryCondition(ASTUnaryCondition uc);
    public void caseASTBinaryCondition(ASTBinaryCondition bc);
    public void caseASTAndCondition(ASTAndCondition ac);
    public void caseASTOrCondition(ASTOrCondition oc);
    

    public void caseType(Type t);
    public void caseDefinitionStmt(DefinitionStmt s);
    public void caseReturnStmt(ReturnStmt s);
    public void caseInvokeStmt(InvokeStmt s);
    public void caseThrowStmt(ThrowStmt s);
    public void caseDVariableDeclarationStmt(DVariableDeclarationStmt s);
    public void caseStmt(Stmt s);
    public void caseValue(Value v);
    public void caseExpr(Expr e);
    public void caseRef(Ref r);
    public void caseBinopExpr(BinopExpr be);
    public void caseUnopExpr(UnopExpr ue);
    public void caseNewArrayExpr(NewArrayExpr nae);
    public void caseNewMultiArrayExpr(NewMultiArrayExpr nmae);
    public void caseInstanceOfExpr(InstanceOfExpr ioe);
    public void caseInvokeExpr(InvokeExpr ie);
    public void caseInstanceInvokeExpr(InstanceInvokeExpr iie);
    public void caseCastExpr(CastExpr ce);
    public void caseArrayRef(ArrayRef ar);
    public void caseInstanceFieldRef(InstanceFieldRef ifr);
    public void caseStaticFieldRef(StaticFieldRef sfr);
}
