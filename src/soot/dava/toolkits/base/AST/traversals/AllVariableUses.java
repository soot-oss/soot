/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Nomair A. Naeem (nomair.naeem@mail.mcgill.ca)
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

/*
 * Maintained by: Nomair A. Naeem
 */

/*
 * CHANGE LOG:  * 30th January 2006, Class created since FinalFieldDefinition wants 
 *                info about uses of a particular field in a method. Writing a general 
 *                analysis which finds all uses of Locals and SootFields
 *
 */

/*
 Need to be very clear when a local can be used
 It can be used in the following places:
 a, a conditional in if, ifelse, while , do while, for condition     TICK
 b, in the for init or update    TICK
 c, in a switch choice           TICK
 d, in a syncrhnoized block       TICK
 d, in a statement                TICK


 Need to be very clear when a SootField can be used
 It can be used in the following places:

 a, NOT used inside a Synchronized Block ........ HOWEVER ADD IT SINCE I DONT SEE WHY THIS RESTRICTION EXISTS!!!  TICK
 b, CAN BE USED in a condition            TICK
 c, CAN BE USED in the for init for update   TICK
 d, CAN BE USED in a switch             TICK
 e, CAN BE USED in a stmt    TICK
 */

package soot.dava.toolkits.base.AST.traversals;

import soot.*;

import java.util.*;

import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;

/*
 * Creates a mapping of locals and all places where they might be used
 * creates a mapping of fields and all places where they might be used
 *    Notice that the mapping is for SootField to uses not for FieldRef to uses
 */
public class AllVariableUses extends DepthFirstAdapter {
	ASTMethodNode methodNode;

	HashMap<Local, List> localsToUses;
	HashMap<SootField, List> fieldsToUses;

	public AllVariableUses(ASTMethodNode node) {
		super();
		this.methodNode = node;
		init();
	}

	public AllVariableUses(boolean verbose, ASTMethodNode node) {
		super(verbose);
		this.methodNode = node;
		init();
	}

	public void init() {
		localsToUses = new HashMap<Local, List>();
		fieldsToUses = new HashMap<SootField, List>();
	}

	/*
	 * Notice as things stand synchblocks cant have the use of a SootField
	 */
	public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node) {
		Local local = node.getLocal();
		addLocalUse(local, node);
	}

	/*
	 * The key in a switch stmt can be a local or a SootField or a value which
	 * can contain Locals or SootFields
	 * 
	 * Hence the some what indirect approach
	 */
	public void inASTSwitchNode(ASTSwitchNode node) {
		Value val = node.get_Key();
		List<Value> localUses = new ArrayList<Value>();
		List<Value> fieldUses = new ArrayList<Value>();

		if (val instanceof Local) {
			localUses.add(val);
			System.out.println("Added " + val + " to local uses for switch");
		} else if (val instanceof FieldRef) {
			fieldUses.add(val);
			System.out.println("Added " + val + " to field uses for switch");
		} else {
			List useBoxes = val.getUseBoxes();

			List<Value> localsOrFieldRefs = getUsesFromBoxes(useBoxes);
			Iterator<Value> it = localsOrFieldRefs.iterator();

			while (it.hasNext()) {
				Value temp = it.next();
				if (temp instanceof Local) {
					localUses.add(temp);
					System.out.println("Added " + temp
							+ " to local uses for switch");
				} else if (temp instanceof FieldRef) {
					fieldUses.add(temp);
					System.out.println("Added " + temp
							+ " to field uses for switch");
				}
			}
		}

		// localuses stores Locals used
		Iterator<Value> it = localUses.iterator();
		while (it.hasNext()) {
			Local local = (Local) it.next();

			addLocalUse(local, node);
		}// end of going through all locals uses in switch key

		// fieldUses stores FieldRef
		it = fieldUses.iterator();
		while (it.hasNext()) {
			FieldRef field = (FieldRef) it.next();
			SootField sootField = field.getField();

			addFieldUse(sootField, node);
		}// end of going through all FieldRef uses in switch key
	}

	public void inASTStatementSequenceNode(ASTStatementSequenceNode node) {
		for (AugmentedStmt as : node.getStatements()) {
			Stmt s = as.get_Stmt();
			// in the case of stmtts in a stmtt sequence each stmt is considered
			// an entity
			// compared to the case where these stmts occur within other
			// constructs
			// where the node is the entity
			checkStatementUses(s, s);
		}
	}

	/*
	 * The init of a for loop can use a local/Sootfield The condition of a for
	 * node can use a local/SootField The update in a for loop can use a
	 * local/SootField
	 */
	public void inASTForLoopNode(ASTForLoopNode node) {

		// checking uses in init
		for (AugmentedStmt as : node.getInit()) {
			Stmt s = as.get_Stmt();
			checkStatementUses(s, node);
		}

		// checking uses in condition
		ASTCondition cond = node.get_Condition();
		checkConditionalUses(cond, node);

		// checking uses in update
		for (AugmentedStmt as : node.getUpdate()) {
			Stmt s = as.get_Stmt();
			checkStatementUses(s, node);
		}
	}

	public void checkStatementUses(Stmt s, Object useNodeOrStatement) {
		List useBoxes = s.getUseBoxes();

		// remeber getUsesFromBoxes returns both Locals and FieldRefs
		List<Value> uses = getUsesFromBoxes(useBoxes);

		Iterator<Value> it = uses.iterator();
		while (it.hasNext()) {
			Value temp = it.next();
			if (temp instanceof Local) {
				addLocalUse((Local) temp, useNodeOrStatement);
			} else if (temp instanceof FieldRef) {
				FieldRef field = (FieldRef) temp;
				SootField sootField = field.getField();
				addFieldUse(sootField, useNodeOrStatement);
			}
		}
	}

	/*
	 * This method gets a list of all uses of locals/Sootfield in the condition
	 * and stores a use by this node
	 */
	public void checkConditionalUses(ASTCondition cond, ASTNode node) {
		List<Value> useList = getUseList(cond);

		// System.out.println("FOR NODE with condition:"+cond+"USE list is:"+useList);

		// FOR EACH USE
		Iterator<Value> it = useList.iterator();
		while (it.hasNext()) {
			Value temp = it.next();
			if (temp instanceof Local) {
				addLocalUse((Local) temp, node);
			} else if (temp instanceof FieldRef) {
				FieldRef field = (FieldRef) temp;
				SootField sootField = field.getField();
				addFieldUse(sootField, node);
			}
		}// end of going through all locals uses in condition
	}

	/*
	 * The condition of an if node can use a local
	 */
	public void inASTIfNode(ASTIfNode node) {
		ASTCondition cond = node.get_Condition();
		checkConditionalUses(cond, node);
	}

	/*
	 * The condition of an ifElse node can use a local
	 */
	public void inASTIfElseNode(ASTIfElseNode node) {
		ASTCondition cond = node.get_Condition();
		checkConditionalUses(cond, node);
	}

	/*
	 * The condition of a while node can use a local
	 */
	public void inASTWhileNode(ASTWhileNode node) {
		ASTCondition cond = node.get_Condition();
		checkConditionalUses(cond, node);
	}

	/*
	 * The condition of a doWhile node can use a local
	 */
	public void inASTDoWhileNode(ASTDoWhileNode node) {
		ASTCondition cond = node.get_Condition();
		checkConditionalUses(cond, node);
	}

	/*
	 * Given a unary/binary or aggregated condition this method is used to find
	 * the locals/SootFields used in the condition
	 * 
	 * @param The condition to be checked for Local or FieldRef uses
	 * 
	 * @return a list containing all Locals and FieldRefs used in this condition
	 */
	public List<Value> getUseList(ASTCondition cond) {
		ArrayList<Value> useList = new ArrayList<Value>();
		if (cond instanceof ASTAggregatedCondition) {
			useList.addAll(getUseList(((ASTAggregatedCondition) cond)
					.getLeftOp()));
			useList.addAll(getUseList(((ASTAggregatedCondition) cond)
					.getRightOp()));
			return useList;
		} else if (cond instanceof ASTUnaryCondition) {
			// get uses from unary condition
			List<Value> uses = new ArrayList<Value>();

			Value val = ((ASTUnaryCondition) cond).getValue();
			if (val instanceof Local || val instanceof FieldRef) {
				uses.add(val);
			} else {
				List useBoxes = val.getUseBoxes();
				uses = getUsesFromBoxes(useBoxes);
			}
			return uses;
		} else if (cond instanceof ASTBinaryCondition) {
			// get uses from binaryCondition
			Value val = ((ASTBinaryCondition) cond).getConditionExpr();
			List useBoxes = val.getUseBoxes();
			return getUsesFromBoxes(useBoxes);
		} else {
			throw new RuntimeException(
					"Method getUseList in ASTUsesAndDefs encountered unknown condition type");
		}
	}

	private void addLocalUse(Local local, Object obj) {
		Object temp = localsToUses.get(local);
		List<Object> uses;
		if (temp == null)
			uses = new ArrayList<Object>();
		else
			uses = (ArrayList<Object>) temp;

		// add local to useList
		uses.add(obj);

		// update mapping
		localsToUses.put(local, uses);
	}

	private void addFieldUse(SootField field, Object obj) {

		Object temp = fieldsToUses.get(field);
		List<Object> uses;
		if (temp == null)
			uses = new ArrayList<Object>();
		else
			uses = (ArrayList<Object>) temp;

		// add field to useList
		uses.add(obj);

		// update mapping
		fieldsToUses.put(field, uses);
	}

	/*
	 * Method is used to strip away boxes from the actual values only those are
	 * returned which are locals or FieldRefs
	 */
	private List<Value> getUsesFromBoxes(List useBoxes) {
		ArrayList<Value> toReturn = new ArrayList<Value>();
		Iterator it = useBoxes.iterator();
		while (it.hasNext()) {
			Value val = ((ValueBox) it.next()).getValue();
			if (val instanceof Local || val instanceof FieldRef)
				toReturn.add(val);
		}
		// System.out.println("VALUES:"+toReturn);
		return toReturn;
	}

	public List getUsesForField(SootField field) {
		Object temp = fieldsToUses.get(field);
		if (temp == null)
			return null;
		else
			return (List) temp;
	}

	public List getUsesForLocal(Local local) {
		Object temp = localsToUses.get(local);
		if (temp == null)
			return null;
		else
			return (List) temp;
	}

}