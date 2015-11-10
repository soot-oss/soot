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

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;

/*
 Nomair A. Naeem 18-FEB-2005

 The class is responsible to do the following transformation on the AST

 label_1:{
 ___________________                      ____________________
 |                   |                    |                    |
 |  no break label_1 |                    | no break label_1   |
 |___________________|                    |____________________|
 |                   |                    |                    |
 |ASTLabeledNode     |                    | label_1:           |
 |while(cond){       |        ---->       |   while(cond){     |
 |   use of label_1  |                    |     use of label_1 |
 |                   |                    |   }                |
 |}                  |                    |____________________|
 |___________________|
 |                   |
 |  Nothing here     |
 |___________________|
 }//end of label_1  


 The important thing to note in this case is that the node which uses breaks 
 extends ASTLabeledNode so that we can move the label down. Obviously there shouldnt be
 an already existing label on this node. Once the node containing the break is detected
 there should be no node following this in the body of the labeled block. This is so because
 if the break is moved down that code will get executed and thats a wrong transformation.


 label_1:{
 ___________________                      _____________________
 |                   |                    |                     |
 |label_2            |                    | label_2:            |
 |while(cond){       |        ---->       |   while(cond){      |
 |   use of label_1  |                    |     label_1 replaced|
 |                   |                    |     by label_2      |
 |}                  |                    |___}_________________|
 |___________________|
 |                   |
 |  Nothing here     |
 |___________________|
 }//end of label_1  

 If the previous pattern did not match because the node on which we wanted to push the label 
 inwards already had a label we can try to use the label on the inner node. This is only possible
 if there is only a single node in the labeledBlocks subBody, to make sure that we break and jump
 to exactly the same point as we were jumping before.


 TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
 this is done by overriding the caseASTStatementSequenceNode
 */

public class PushLabeledBlockIn extends DepthFirstAdapter {

	public PushLabeledBlockIn() {
	}

	public PushLabeledBlockIn(boolean verbose) {
		super(verbose);
	}

	public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
	}

	public void outASTLabeledBlockNode(ASTLabeledBlockNode node) {
		String label = node.get_Label().toString();
		List<Object> subBodies = node.get_SubBodies();
		if (subBodies.size() != 1) {
			return;
		}
		List subBody = (List) subBodies.get(0);
		int nodeNumber = checkForBreak(subBody, label);
		if (nodeNumber > -1) {
			// found some break for this label
			// retrieve element at this nodeNumber
			if (subBody.size() < nodeNumber) {
				// something is wrong
				throw new RuntimeException(
						"Please submit this benchmark as a bug");
			}

			// check that this is the last node in the list
			// since otherwise we cant change anything
			if (nodeNumber + 1 != subBody.size()) {
				// it is not the last
				return;
			}

			// safe to access the list
			ASTNode temp = (ASTNode) subBody.get(nodeNumber);
			if (!(temp instanceof ASTLabeledNode)) {
				// does not extend labeledNode hence cannot give it a label
				return;
			}

			ASTLabeledNode tempNode = (ASTLabeledNode) temp;
			// shouldnt already have a label
			String innerLabel = tempNode.get_Label().toString();
			if (innerLabel != null) {
				// already has a label
				// we could still do something if this is the ONLY node in the
				// body

				if (subBody.size() == 1) {
					// there is only one node
					/*
					 * The situation is that there is a labeled block whic has
					 * only one node that also has a label on it.
					 * 
					 * There is some statement deep down which breaks the outer
					 * label
					 * 
					 * No reason why it cant break the inner label since there
					 * is nothing after that!!!
					 * 
					 * label has the outer label whose break we found innerLabel
					 * has the label of the inner node which contains the break
					 * 
					 * replace all occurances of break of outer label with that
					 * of break of inner label
					 */

					// we know that the breaks occur within the subtree rooted
					// at temp
					boolean done = replaceBreakLabels(temp, label, innerLabel);
					if (done) {
						// System.out.println("REMOVED LABELED BLOCK-replaced label names");
						node.set_Label(new SETNodeLabel());
						G.v().ASTTransformations_modified = true;
					}
				}

				return;
			} else {
				// doesnt have a label
				// System.out.println("PUSHED LABEL DOWN");
				SETNodeLabel newLabel = new SETNodeLabel();
				newLabel.set_Name(label);
				tempNode.set_Label(newLabel);
				node.set_Label(new SETNodeLabel());
				G.v().ASTTransformations_modified = true;
			}

		}
	}

	private boolean replaceBreakLabels(ASTNode node, String toReplace,
			String replaceWith) {
		boolean toReturn = false;
		List<Object> subBodies = node.get_SubBodies();
		Iterator<Object> subIt = subBodies.iterator();
		while (subIt.hasNext()) {
			List subBody = null;
			if (node instanceof ASTTryNode) {
				ASTTryNode.container subBodyContainer = (ASTTryNode.container) subIt
						.next();
				subBody = (List) subBodyContainer.o;
			} else
				subBody = (List) subIt.next();

			Iterator it = subBody.iterator();
			while (it.hasNext()) {
				ASTNode temp = (ASTNode) it.next();
				// check if this is ASTStatementSequenceNode
				if (temp instanceof ASTStatementSequenceNode) {
					ASTStatementSequenceNode stmtSeq = (ASTStatementSequenceNode) temp;
					for (AugmentedStmt as : stmtSeq.getStatements()) {
						Stmt s = as.get_Stmt();
						String labelBroken = isAbrupt(s);
						if (labelBroken != null) {// stmt breaks some label
							if (labelBroken.compareTo(toReplace) == 0) {
								// we have found a break breaking this label
								// replace the label with "replaceWith"
								replaceLabel(s, replaceWith);
								toReturn = true;
							}
						}
					}
				}// if it was a StmtSeq node
				else {
					// otherwise recursion
					boolean returnVal = replaceBreakLabels(temp, toReplace,
							replaceWith);
					if (returnVal)
						toReturn = true;
				}
			}// end of while
		}
		return toReturn;
	}

	private int checkForBreak(List ASTNodeBody, String outerLabel) {
		Iterator it = ASTNodeBody.iterator();
		int nodeNumber = 0;
		while (it.hasNext()) {
			ASTNode temp = (ASTNode) it.next();
			// check if this is ASTStatementSequenceNode
			if (temp instanceof ASTStatementSequenceNode) {
				ASTStatementSequenceNode stmtSeq = (ASTStatementSequenceNode) temp;
				for (AugmentedStmt as : stmtSeq.getStatements()) {
					Stmt s = as.get_Stmt();
					String labelBroken = breaksLabel(s);
					if (labelBroken != null && outerLabel != null) {// stmt
																	// breaks
																	// some
																	// label
						if (labelBroken.compareTo(outerLabel) == 0) {
							// we have found a break breaking this label
							return nodeNumber;
						}
					}
				}
			}// if it was a StmtSeq node
			else {
				// otherwise recursion
				// getSubBodies
				List<Object> subBodies = temp.get_SubBodies();
				Iterator<Object> subIt = subBodies.iterator();
				while (subIt.hasNext()) {

					if (temp instanceof ASTTryNode) {
						ASTTryNode.container subBody = (ASTTryNode.container) subIt
								.next();
						if (checkForBreak((List) subBody.o, outerLabel) > (-1)) {
							// if this is true there was a break found
							return nodeNumber;
						}
					} else {
						if (checkForBreak((List) subIt.next(), outerLabel) > (-1)) {
							// if this is true there was a break found
							return nodeNumber;
						}
					}
				}
			}
			nodeNumber++;
		}// end of while

		return -1;
	}

	/*
	 * If the stmt is a break stmt then this method returns the labels name else
	 * returns null
	 */
	private String breaksLabel(Stmt stmt) {
		if (!(stmt instanceof DAbruptStmt)) {
			// this is not a break stmt
			return null;
		}
		DAbruptStmt abStmt = (DAbruptStmt) stmt;
		if (!abStmt.is_Break()) {
			// not a break stmt
			return null;
		}
		SETNodeLabel label = abStmt.getLabel();
		return label.toString();
	}

	/*
	 * If the stmt is a abruptstmt either break or continue returns the labels
	 * name else returns null
	 */
	private String isAbrupt(Stmt stmt) {
		if (!(stmt instanceof DAbruptStmt)) {
			// this is not a break stmt
			return null;
		}
		DAbruptStmt abStmt = (DAbruptStmt) stmt;
		if (abStmt.is_Break() || abStmt.is_Continue()) {
			SETNodeLabel label = abStmt.getLabel();
			return label.toString();
		} else
			return null;
	}

	private void replaceLabel(Stmt s, String replaceWith) {
		// we know its an AbruptStmt
		DAbruptStmt abStmt = (DAbruptStmt) s;
		SETNodeLabel label = abStmt.getLabel();
		label.set_Name(replaceWith);
	}
}
