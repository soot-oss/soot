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

package soot.dava.toolkits.base.AST.transformations;

import java.util.*;
import soot.*;
import soot.jimple.*;
import soot.dava.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;
import soot.dava.internal.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;

/*
 * CHANGELOG: Nomair 9th Feb (For some reason only AddExpr was being checked for i++
 *                       Added SubExpr for i--
 */
public class DecrementIncrementStmtCreation extends DepthFirstAdapter {

	public DecrementIncrementStmtCreation() {
	}

	public DecrementIncrementStmtCreation(boolean verbose) {
		super(verbose);
	}

	public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
		List statements = node.getStatements();
		Iterator stmtIt = statements.iterator();
				
		while (stmtIt.hasNext()) {
			Object temp = stmtIt.next();
			//System.out.println(temp);
			AugmentedStmt as = (AugmentedStmt)temp;
			Stmt s = as.get_Stmt();
			if (!( s instanceof DefinitionStmt)) 
				continue;
			
			// check if its i= i+1
			Value left = ((DefinitionStmt) s).getLeftOp();
			Value right = ((DefinitionStmt) s).getRightOp();


			if (right instanceof SubExpr) {
				Value op1 = ((SubExpr) right).getOp1();
				Value op2 = ((SubExpr) right).getOp2();
				if (left.toString().compareTo(op1.toString()) != 0) {
					//not the same
					continue;
				}
				//if they are the same
					
				// check if op2 is a constant with value 1 or -1
				if (op2 instanceof IntConstant) {
					if (((IntConstant) op2).value == 1) {
						// this is i = i-1
						DDecrementStmt newStmt = new DDecrementStmt(left, right);
						as.set_Stmt(newStmt);
					} else if (((IntConstant) op2).value == -1) {
						// this is i = i+1
						DIncrementStmt newStmt = new DIncrementStmt(left, right);
						as.set_Stmt(newStmt);
					}
				}
				
			}
			else if (right instanceof AddExpr) {
				Value op1 = ((AddExpr) right).getOp1();
				Value op2 = ((AddExpr) right).getOp2();
				if (left.toString().compareTo(op1.toString()) != 0) {
					continue;
				}
				// check if op2 is a constant with value 1 or -1
				if (op2 instanceof IntConstant) {
					if (((IntConstant) op2).value == 1) {
						// this is i = i+1
						DIncrementStmt newStmt = new DIncrementStmt(left, right);
						as.set_Stmt(newStmt);
					} else if (((IntConstant) op2).value == -1) {
						// this is i = i-1
						DDecrementStmt newStmt = new DDecrementStmt(left, right);
						as.set_Stmt(newStmt);
					}					
				}
			}//right expr was addExpr
		}//going through statements
	}
}

