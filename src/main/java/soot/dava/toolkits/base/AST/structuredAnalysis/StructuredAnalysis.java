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

/**
 * Maintained by: Nomair A. Naeem
 */

/**
 * CHANGE LOG:
 *     November 21st, 2005: Reasoning about correctness of implementation.
 *     November 22nd, 2005: Found bug in process_DoWhile while implementing ReachingCopies..
 *                          see method for details
 *     January 30th, 2006:  Found bug in handling of breaks inside the ASTTryNode while implementing 
 *                          MustMayinitialize...see ASTTryNode method for details
 *     January 30th, 2006:  Found bug in handling of switchNode while implementing MustMayInitialize
 *                          NEEDS THOROUGH TESTING!!!
 *
 */

/**
 * TODO:
 * Refactor the class into a top level class and a forward analysis subclass
 * Write the backwards flow analysis
 *
 *  THOROUGH TESTING OF BUG FOUND ON 30th January
 */
package soot.dava.toolkits.base.AST.structuredAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Local;
import soot.Value;
import soot.dava.internal.AST.ASTAggregatedCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTLabeledNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.AST.ASTUnaryBinaryCondition;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;

/*
 * This class is meant to be extended to write structred analyses.
 * The analysis is invoked by invoking the process method sending it
 * the body to be analyzed and the input flowset
 * Currently support is available only for a forward flow analysis.
 * This should soon be refactored to include backwards flow analysis
 * (Nomair 16th November 2005)
 */
public abstract class StructuredAnalysis<E> {

	public static boolean DEBUG = false;
	public static boolean DEBUG_IF = false;
	public static boolean DEBUG_WHILE = false;
	public static boolean DEBUG_STATEMENTS = false;
	public static boolean DEBUG_TRY = false;
	/*
	 * public static boolean DEBUG = true; public static boolean DEBUG_IF =
	 * true; public static boolean DEBUG_WHILE = true; public static boolean
	 * DEBUG_STATEMENTS = true; public static boolean DEBUG_TRY = true; /* /**
	 * Whenever an abrupt edge is encountered the flow set is added into a the
	 * break or continue list and a NOPATH object is returned
	 */
	DavaFlowSet<E> NOPATH = emptyFlowSet();
	public int MERGETYPE; // the confluence operator

	// the three types of operators
	final int UNDEFINED = 0;
	final int UNION = 1;
	final int INTERSECTION = 2;

	// storing before and after sets for each stmt or ASTNode
	HashMap<Object, DavaFlowSet<E>> beforeSets, afterSets;

	public StructuredAnalysis() {
		beforeSets = new HashMap<Object, DavaFlowSet<E>>();
		afterSets = new HashMap<Object, DavaFlowSet<E>>();
		MERGETYPE = UNDEFINED;
		// invoke user defined function which makes sure that you have the merge
		// operator set
		setMergeType();
		// System.out.println("MergeType is"+MERGETYPE);
		if (MERGETYPE == UNDEFINED)
			throw new RuntimeException("MERGETYPE UNDEFINED");
	}

	/*
	 * This method should be used to set the variable MERGETYPE use
	 * StructuredAnalysis.UNION for union use StructuredAnalysis.INTERSECTION
	 * for intersection
	 */
	public abstract void setMergeType();

	/*
	 * Returns the flow object corresponding to the initial values for the catch
	 * statements
	 */
	public abstract DavaFlowSet<E> newInitialFlow();

	/*
	 * Returns an empty flow set object Notice this has to be a DavaFlowSet or a
	 * set extending DavaFlowSet (hopefully constantpropagationFlowSET??)
	 */
	public abstract DavaFlowSet<E> emptyFlowSet();

	/**
	 * Make a clone of the flowset The implementor should know when they want a
	 * shallow or deep clone
	 */
	public abstract DavaFlowSet<E> cloneFlowSet(DavaFlowSet<E> flowSet);

	/**
	 * Specific stmts within AST Constructs are processed through this method.
	 * It will be invoked everytime a stmt is encountered
	 */
	public abstract DavaFlowSet<E> processStatement(Stmt s, DavaFlowSet<E> input);

	/**
	 * To have maximum flexibility in analyzing conditions the analysis API
	 * breaks down the aggregated conditions to simple unary or binary
	 * conditions user defined code can then deal with each condition
	 * separately. To be able to deal with entire aggregated conditions the user
	 * should wite their own implementation of the method processCondition
	 */
	public abstract DavaFlowSet<E> processUnaryBinaryCondition(ASTUnaryBinaryCondition cond, DavaFlowSet<E> input);

	/**
	 * To deal with the local used for synch blocks
	 */
	public abstract DavaFlowSet<E> processSynchronizedLocal(Local local, DavaFlowSet<E> input);

	/**
	 * Deal with the key in the switch construct
	 */
	public abstract DavaFlowSet<E> processSwitchKey(Value key, DavaFlowSet<E> input);

	public void print(Object toPrint) {
		System.out.println(toPrint.toString());
	}

	/**
	 * This implementation breaks down the aggregated condition to the terminal
	 * conditions which all have type ASTUnaryBinaryCondition. Once these are
	 * obtained the abstract method processUnaryBinaryCondition is invoked. For
	 * aggregated conditions the merging is done in a depth first order of the
	 * condition tree.
	 */
	public DavaFlowSet<E> processCondition(ASTCondition cond, DavaFlowSet<E> input) {
		if (cond instanceof ASTUnaryBinaryCondition) {
			return processUnaryBinaryCondition((ASTUnaryBinaryCondition) cond, input);
		} else if (cond instanceof ASTAggregatedCondition) {
			ASTCondition left = ((ASTAggregatedCondition) cond).getLeftOp();
			DavaFlowSet<E> output1 = processCondition(left, input);

			ASTCondition right = ((ASTAggregatedCondition) cond).getRightOp();
			DavaFlowSet<E> output2 = processCondition(right, output1);

			return merge(output1, output2);
		} else {
			throw new RuntimeException("Unknown ASTCondition found in structred flow analysis");
		}
	}

	/*
	 * The parameter body contains the body to be analysed It can be an ASTNode,
	 * a Stmt, an augmentedStmt or a list of ASTNodes The input is any data that
	 * is gathered plus any info needed for making decisions during the analysis
	 */
	public DavaFlowSet<E> process(Object body, DavaFlowSet<E> input) {
		if (body instanceof ASTNode) {
			beforeSets.put(body, input);
			DavaFlowSet<E> temp = processASTNode((ASTNode) body, input);
			afterSets.put(body, temp);
			return temp;
		} else if (body instanceof Stmt) {
			beforeSets.put(body, input);
			DavaFlowSet<E> result = processAbruptStatements((Stmt) body, input);
			afterSets.put(body, result);
			return result;
		} else if (body instanceof AugmentedStmt) {
			AugmentedStmt as = (AugmentedStmt) body;
			Stmt s = as.get_Stmt();

			beforeSets.put(s, input);
			DavaFlowSet<E> result = processAbruptStatements(s, input);
			afterSets.put(s, result);
			return result;

		} else if (body instanceof List) {
			// this should always be a list of ASTNodes
			Iterator it = ((List) body).iterator();
			DavaFlowSet<E> result = input;
			while (it.hasNext()) {
				Object temp = it.next();
				if (!(temp instanceof ASTNode))
					throw new RuntimeException("Body sent to be processed by "
							+ "StructuredAnalysis contains a list which does not have ASTNodes");
				else {
					/*
					 * As we are simply going through a list of ASTNodes The
					 * output of the previous becomes the input of the next
					 */
					beforeSets.put(temp, result);
					result = processASTNode((ASTNode) temp, result);
					afterSets.put(temp, result);
				}
			}// end of going through list

			// at this point the result var contains the result of processing
			// the List
			return result;
		} else {
			throw new RuntimeException("Body sent to be processed by " + "StructuredAnalysis is not a valid body");
		}
	}

	/*
	 * This method internally invoked by the process method decides which
	 * ASTNode specialized method to call
	 */
	public DavaFlowSet<E> processASTNode(ASTNode node, DavaFlowSet<E> input) {
		if (node instanceof ASTDoWhileNode) {
			return processASTDoWhileNode((ASTDoWhileNode) node, input);
		} else if (node instanceof ASTForLoopNode) {
			return processASTForLoopNode((ASTForLoopNode) node, input);
		} else if (node instanceof ASTIfElseNode) {
			return processASTIfElseNode((ASTIfElseNode) node, input);
		} else if (node instanceof ASTIfNode) {
			return processASTIfNode((ASTIfNode) node, input);
		} else if (node instanceof ASTLabeledBlockNode) {
			return processASTLabeledBlockNode((ASTLabeledBlockNode) node, input);
		} else if (node instanceof ASTMethodNode) {
			return processASTMethodNode((ASTMethodNode) node, input);
		} else if (node instanceof ASTStatementSequenceNode) {
			return processASTStatementSequenceNode((ASTStatementSequenceNode) node, input);
		} else if (node instanceof ASTSwitchNode) {
			return processASTSwitchNode((ASTSwitchNode) node, input);
		} else if (node instanceof ASTSynchronizedBlockNode) {
			return processASTSynchronizedBlockNode((ASTSynchronizedBlockNode) node, input);
		} else if (node instanceof ASTTryNode) {
			return processASTTryNode((ASTTryNode) node, input);
		} else if (node instanceof ASTWhileNode) {
			return processASTWhileNode((ASTWhileNode) node, input);
		} else if (node instanceof ASTUnconditionalLoopNode) {
			return processASTUnconditionalLoopNode((ASTUnconditionalLoopNode) node, input);
		} else {
			throw new RuntimeException("processASTNode called using unknown node type");
		}
	}

	/**
	 * This method is called from the specialized ASTNodes. The purpose was to
	 * deal with different ASTNodes with similar structure in one go. The method
	 * will deal with retrieve the body of the ASTNode which are known to have
	 * only one subBody
	 */
	public final DavaFlowSet<E> processSingleSubBodyNode(ASTNode node, DavaFlowSet<E> input) {
		// get the subBodies
		List<Object> subBodies = node.get_SubBodies();
		if (subBodies.size() != 1) {
			throw new RuntimeException("processSingleSubBodyNode called with a node without one subBody");
		}
		// we know there is only one
		List subBody = (List) subBodies.get(0);
		return process(subBody, input);
	}

	/**
	 * returns label on the ASTNode null if the ASTNode cannot hold a label or
	 * if the label is null
	 */
	public String getLabel(ASTNode node) {
		if (node instanceof ASTLabeledNode) {
			Object temp = ((ASTLabeledNode) node).get_Label();
			if (temp != null)
				return temp.toString();
		}
		return null;
	}

	/**
	 * Whenever a statement has to be processed the first step is to invoke this
	 * method. This is to remove the tedious work of adding code to deal with
	 * abrupt control flow from the programmer of the analysis. The method
	 * invokes the processStatement method for all other statements
	 * 
	 * A programmer can decide to override this method if they want to do
	 * something specific
	 */
	public DavaFlowSet<E> processAbruptStatements(Stmt s, DavaFlowSet<E> input) {
		if (s instanceof ReturnStmt || s instanceof RetStmt || s instanceof ReturnVoidStmt) {
			// dont need to remember this path
			return NOPATH;
		} else if (s instanceof DAbruptStmt) {
			DAbruptStmt abStmt = (DAbruptStmt) s;

			// see if its a break or continue
			if (!(abStmt.is_Continue() || abStmt.is_Break())) {
				// DAbruptStmt is of only two kinds
				throw new RuntimeException("Found a DAbruptStmt which is neither break nor continue!!");
			}

			DavaFlowSet<E> temp = NOPATH;
			SETNodeLabel nodeLabel = abStmt.getLabel();
			// System.out.println("here");
			if (nodeLabel != null && nodeLabel.toString() != null) {
				// explicit abrupt stmt
				if (abStmt.is_Continue())
					temp.addToContinueList(nodeLabel.toString(), input);
				else if (abStmt.is_Break())
					temp.addToBreakList(nodeLabel.toString(), input);
				else
					throw new RuntimeException("Found abruptstmt which is neither break nor continue");
			} else {
				// found implicit break/continue
				if (abStmt.is_Continue())
					temp.addToImplicitContinues(abStmt, input);
				else if (abStmt.is_Break())
					temp.addToImplicitBreaks(abStmt, input);
				else
					throw new RuntimeException("Found abruptstmt which is neither break nor continue");
			}
			return temp;
		} else {
			/**************************************************************/
			/****** ALL OTHER STATEMENTS HANDLED BY PROGRAMMER **************/
			/**************************************************************/
			return processStatement(s, input);
		}
	}

	/*
	 * Notice Right now the output of the processing of method bodies is
	 * returned as the output. This only works for INTRA procedural Analysis.
	 * For accomodating INTER procedural analysis one needs to have a return
	 * list of all possible returns (stored in the flowset) And merge Returns
	 * with the output of normal execution of the body
	 */
	// reasoned about this....seems right!!
	public DavaFlowSet<E> processASTMethodNode(ASTMethodNode node, DavaFlowSet<E> input) {
		DavaFlowSet<E> temp = processSingleSubBodyNode(node, input);
		return temp;
	}

	public DavaFlowSet<E> processASTStatementSequenceNode(ASTStatementSequenceNode node,
			DavaFlowSet<E> input) {
		DavaFlowSet<E> output = cloneFlowSet(input);// needed if there are no stmts

		for (AugmentedStmt as : node.getStatements()) {
			Stmt s = as.get_Stmt();
			/*
			 * Since we are processing a list of statements the output of
			 * previous is input of next
			 */
			output = process(s, output);
			if (DEBUG_STATEMENTS) {
				System.out.println("After Processing statement " + s + output.toString());
				;
			}
		}
		return output;
	}

	// reasoned about this....seems right!!
	public DavaFlowSet<E> processASTLabeledBlockNode(ASTLabeledBlockNode node, DavaFlowSet<E> input) {
		DavaFlowSet<E> output1 = processSingleSubBodyNode(node, input);

		// handle break
		String label = getLabel(node);
		return handleBreak(label, output1, node);
	}

	public DavaFlowSet<E> processASTSynchronizedBlockNode(ASTSynchronizedBlockNode node, DavaFlowSet<E> input) {
		input = processSynchronizedLocal(node.getLocal(), input);

		DavaFlowSet<E> output = processSingleSubBodyNode(node, input);
		String label = getLabel(node);
		return handleBreak(label, output, node);
	}

	// reasoned about this....seems right!!
	public DavaFlowSet<E> processASTIfNode(ASTIfNode node, DavaFlowSet<E> input) {
		input = processCondition(node.get_Condition(), input);

		DavaFlowSet<E> output1 = processSingleSubBodyNode(node, input);

		// merge with input which tells if the cond did not evaluate to true
		DavaFlowSet<E> output2 = merge(input, output1);

		// handle break
		String label = getLabel(node);

		DavaFlowSet<E> temp = handleBreak(label, output2, node);

		if (DEBUG_IF) {
			System.out.println("Exiting if node" + temp.toString());
		}
		return temp;
	}

	public DavaFlowSet<E> processASTIfElseNode(ASTIfElseNode node, DavaFlowSet<E> input) {
		// get the subBodies
		List<Object> subBodies = node.get_SubBodies();
		if (subBodies.size() != 2) {
			throw new RuntimeException("processASTIfElseNode called with a node without two subBodies");
		}
		// we know there is only two subBodies
		List subBodyOne = (List) subBodies.get(0);
		List subBodyTwo = (List) subBodies.get(1);

		// process Condition
		input = processCondition(node.get_Condition(), input);
		// the current input flowset is sent to both branches
		DavaFlowSet<E> clonedInput = cloneFlowSet(input);
		DavaFlowSet<E> output1 = process(subBodyOne, clonedInput);

		clonedInput = cloneFlowSet(input);
		DavaFlowSet<E> output2 = process(subBodyTwo, clonedInput);

		DavaFlowSet<E> temp = merge(output1, output2);
		// notice we handle breaks only once since these are breaks to the same
		// label or same node
		String label = getLabel(node);
		output1 = handleBreak(label, temp, node);

		return output1;
	}

	public DavaFlowSet<E> processASTWhileNode(ASTWhileNode node, DavaFlowSet<E> input) {
		DavaFlowSet<E> lastin = null;
		DavaFlowSet<E> initialInput = cloneFlowSet(input);

		String label = getLabel(node);
		DavaFlowSet<E> output = null;

		input = processCondition(node.get_Condition(), input);
		if (DEBUG_WHILE)
			System.out.println("Going int while (condition processed): " + input.toString());

		do {
			lastin = cloneFlowSet(input);
			output = processSingleSubBodyNode(node, input);

			// handle continue
			output = handleContinue(label, output, node);

			// merge with the initial input
			input = merge(initialInput, output);
			input = processCondition(node.get_Condition(), input);
		} while (isDifferent(lastin, input));

		// input contains the result of the fixed point
		DavaFlowSet<E> temp = handleBreak(label, input, node);
		if (DEBUG_WHILE)
			System.out.println("Going out of while: " + temp.toString());
		return temp;
	}

	public DavaFlowSet<E> processASTDoWhileNode(ASTDoWhileNode node, DavaFlowSet<E> input) {
		DavaFlowSet<E> lastin = null, output = null;
		DavaFlowSet<E> initialInput = cloneFlowSet(input);
		String label = getLabel(node);
		if (DEBUG_WHILE)
			System.out.println("Going into do-while: " + initialInput.toString());

		do {
			lastin = cloneFlowSet(input);
			output = processSingleSubBodyNode(node, input);

			// handle continue
			output = handleContinue(label, output, node);

			output = processCondition(node.get_Condition(), output);

			// merge with the initial input
			input = merge(initialInput, output);
		} while (isDifferent(lastin, input));

		// output contains the result of the fixed point since do-while breaks
		// of at the processing of cond
		DavaFlowSet<E> temp = handleBreak(label, output, node);
		if (DEBUG_WHILE)
			System.out.println("Going out of do-while: " + temp.toString());

		return temp;

	}

	public DavaFlowSet<E> processASTUnconditionalLoopNode(ASTUnconditionalLoopNode node, DavaFlowSet<E> input) {
		// an unconditional loop behaves almost like a conditional While loop
		DavaFlowSet<E> initialInput = cloneFlowSet(input);
		DavaFlowSet<E> lastin = null;
		if (DEBUG_WHILE)
			System.out.println("Going into while(true): " + initialInput.toString());

		String label = getLabel(node);
		DavaFlowSet<E> output = null;
		do {
			lastin = cloneFlowSet(input);
			output = processSingleSubBodyNode(node, input);

			// handle continue
			output = handleContinue(label, output, node);

			// merge this with the initial input
			input = merge(initialInput, output);
		} while (isDifferent(lastin, input));

		// the output is not part of the set returned
		// it is just used to retrieve the set of breaklists stored for this
		// label
		DavaFlowSet<E> temp = getMergedBreakList(label, output, node);
		if (DEBUG_WHILE)
			System.out.println("Going out of while(true): " + temp.toString());
		return temp;

	}

	public DavaFlowSet<E> processASTForLoopNode(ASTForLoopNode node, DavaFlowSet<E> input) {
		for (AugmentedStmt as : node.getInit()) {
			Stmt s = as.get_Stmt();
			input = process(s, input);
		}

		// finished processing the init part of the for loop
		DavaFlowSet<E> initialInput = cloneFlowSet(input);

		input = processCondition(node.get_Condition(), input);
		DavaFlowSet<E> lastin = null;
		String label = getLabel(node);
		DavaFlowSet<E> output2 = null;
		do {
			lastin = cloneFlowSet(input);

			// process body
			DavaFlowSet<E> output1 = processSingleSubBodyNode(node, input);

			// handle continues (Notice this is done before update!!!)
			output1 = handleContinue(label, output1, node);

			// notice that we dont merge with the initial output1 from
			// processing singleSubBody
			// the handlecontinue function takes care of it

			// handle update
			output2 = cloneFlowSet(output1);// if there is nothing in update

			for (AugmentedStmt as : node.getUpdate()) {
				Stmt s = as.get_Stmt();
				/*
				 * Since we are just going over a list of statements the output
				 * of each statement is the input of the next
				 */
				output2 = process(s, output2);
			}

			// output2 is the final result

			// merge this with the input
			input = merge(initialInput, output2);
			input = processCondition(node.get_Condition(), input);
		} while (isDifferent(lastin, input));

		// handle break
		return handleBreak(label, input, node);
	}

	/*
	 * Notice ASTSwitch is horribly conservative....eg. if all cases break
	 * properly it will still merge with defaultOut which will be a NOPATH and
	 * bound to have empty or full sets
	 */
	public DavaFlowSet<E> processASTSwitchNode(ASTSwitchNode node, DavaFlowSet<E> input) {
		if (DEBUG)
			System.out.println("Going into switch: " + input.toString());

		List<Object> indexList = node.getIndexList();
		Map<Object, List<Object>> index2BodyList = node.getIndex2BodyList();

		Iterator<Object> it = indexList.iterator();

		input = processSwitchKey(node.get_Key(), input);
		DavaFlowSet<E> initialIn = cloneFlowSet(input);

		DavaFlowSet<E> out = null;
		DavaFlowSet<E> defaultOut = null;

		List<DavaFlowSet<E>> toMergeBreaks = new ArrayList<DavaFlowSet<E>>();

		while (it.hasNext()) {// going through all the cases of the switch
								// statement
			Object currentIndex = it.next();
			List body = index2BodyList.get(currentIndex);

			// BUG FIX if body is null (fall through we shouldnt invoke process
			// Reported by Steffen Pingel 14th Jan 2006 on the soot mailing list
			if (body != null) {
				out = process(body, input);

				// System.out.println("Breaklist for this out is"+out.getBreakList());
				toMergeBreaks.add(cloneFlowSet(out));

				if (currentIndex instanceof String) {
					// this is the default
					defaultOut = out;
				}

				// the input to the next can be a fall through or directly input
				input = merge(out, initialIn);
			}// body was non null
		}

		// have to handle the case when no case matches. The input is the output
		DavaFlowSet<E> output = null;
		if (out != null) {// just to make sure that there were some cases
							// present

			/*
			 * January 30th 2006, FOUND BUG The initialIn should only be merge
			 * with the out if there is no default in the list of switch cases
			 * If there is a default then there is no way that the initialIn is
			 * the actual result. Then its either the default or one of the
			 * outs!!!
			 */
			if (defaultOut != null) {
				// there was a default
				// System.out.println("DEFAULTSET");
				// System.out.println("defaultOut is"+defaultOut);
				// System.out.println("out is"+out);

				output = merge(defaultOut, out);
			} else {
				// there was no default so no case might match
				output = merge(initialIn, out);
			}

		} else
			output = initialIn;

		// handle break
		String label = getLabel(node);

		// have to handleBreaks for all the different cases
		List<DavaFlowSet<E>> outList = new ArrayList<DavaFlowSet<E>>();

		// handling breakLists of each of the toMergeBreaks
		for (DavaFlowSet<E> mset : toMergeBreaks)
			outList.add(handleBreak(label, mset, node));

		// merge all outList elements. since these are the outputs with breaks
		// handled
		DavaFlowSet<E> finalOut = output;
		for (DavaFlowSet<E> outIt : outList)
			finalOut = merge(finalOut, outIt);

		if (DEBUG)
			System.out.println("Going out of switch: " + finalOut.toString());

		return finalOut;
	}

	public DavaFlowSet<E> processASTTryNode(ASTTryNode node, DavaFlowSet<E> input) {
		if (DEBUG_TRY)
			System.out.println("TRY START is:" + input);

		// System.out.println("SET beginning of tryBody is:"+input);
		List<Object> tryBody = node.get_TryBody();
		DavaFlowSet<E> tryBodyOutput = process(tryBody, input);
		// System.out.println("SET end of tryBody is:"+tryBodyOutput);

		/*
		 * By default take either top or bottom as the input to the catch
		 * statements Which goes in depends on the type of analysis.
		 */
		DavaFlowSet<E> inputCatch = newInitialFlow();
		if (DEBUG_TRY)
			System.out.println("TRY initialFLOW is:" + inputCatch);

		List<Object> catchList = node.get_CatchList();
		Iterator<Object> it = catchList.iterator();
		List<DavaFlowSet<E>> catchOutput = new ArrayList<DavaFlowSet<E>>();

		while (it.hasNext()) {
			ASTTryNode.container catchBody = (ASTTryNode.container) it.next();

			List<E> body = (List<E>) catchBody.o;
			// list of ASTNodes

			// result because of going through the catchBody
			DavaFlowSet<E> tempResult = process(body, cloneFlowSet(inputCatch));
			// System.out.println("TempResult going through body"+tempResult);
			catchOutput.add(tempResult);
		}

		// handle breaks
		String label = getLabel(node);

		/*
		 * 30th Jan 2005, Found bug in handling out breaks what was being done
		 * was that handleBreak was invoked using just
		 * handleBreak(label,tryBodyoutput,node) Now what it does is that it
		 * looks for the breakList stored in the tryBodyOutput node What might
		 * happen is that there might be some breaks in the catchOutput which
		 * would have gotten stored in the breakList of the respective
		 * catchoutput
		 * 
		 * The correct way to handle this is create a list of handledBreak
		 * objects (in the outList) And then to merge them
		 */
		List<DavaFlowSet<E>> outList = new ArrayList<DavaFlowSet<E>>();

		// handle breaks out of tryBodyOutput
		outList.add(handleBreak(label, tryBodyOutput, node));
		// System.out.println("After handling break from tryBodyOutput"+outList.get(0));

		// handling breakLists of each of the catchOutputs
		for (DavaFlowSet<E> co : catchOutput) {
			DavaFlowSet<E> temp = handleBreak(label, co, node);

			if (DEBUG_TRY)
				System.out.println("TRY handling breaks is:" + temp);

			outList.add(temp);
		}

		// merge all outList elements. since these are the outputs with breaks
		// handled
		DavaFlowSet<E> out = tryBodyOutput;
		for (DavaFlowSet<E> co : outList)
			out = merge(out, co);
		
		if (DEBUG_TRY)
			System.out.println("TRY after merge outList is:" + out);

		// System.out.println("After handling break"+out);

		for (DavaFlowSet<E> co : catchOutput)
			out = merge(out, co);
		
		if (DEBUG_TRY)
			System.out.println("TRY END RESULT is:" + out);

		return out;
	}

	/*
	 * MERGETYPE var has to be set 0, means no type set 1, means union 2, means
	 * intersection
	 */
	public DavaFlowSet<E> merge(DavaFlowSet<E> in1, DavaFlowSet<E> in2) {
		if (MERGETYPE == 0)
			throw new RuntimeException("Use the setMergeType method to set the type of merge used in the analysis");
		
		DavaFlowSet<E> out;
		if (in1 == NOPATH && in2 != NOPATH) {
			out = in2.clone();
			out.copyInternalDataFrom(in1);
			return out;
		} else if (in1 != NOPATH && in2 == NOPATH) {
			out = in1.clone();
			out.copyInternalDataFrom(in2);
			return out;
		} else if (in1 == NOPATH && in2 == NOPATH) {
			out = in1.clone();
			out.copyInternalDataFrom(in2);
			return out; // meaning return NOPATH
		} else {// both are not NOPATH
			out = emptyFlowSet();
			if (MERGETYPE == 1)// union
				in1.union(in2, out);
			else if (MERGETYPE == 2)// intersection
				in1.intersection(in2, out);
			else
				throw new RuntimeException("Merge type value" + MERGETYPE + " not recognized");
			out.copyInternalDataFrom(in1);
			out.copyInternalDataFrom(in2);
			return out;
		}
	}

	public DavaFlowSet<E> mergeExplicitAndImplicit(String label, DavaFlowSet<E> output,
			List<DavaFlowSet<E>> explicitSet, List<DavaFlowSet<E>> implicitSet) {
		DavaFlowSet<E> toReturn = output.clone();

		if (label != null) {
			// use the explicit list
			/*
			 * If there is no list associated with this label or the list is
			 * empty there no explicit merging to be done
			 */
			if (explicitSet != null && explicitSet.size() != 0) {
				// explicitSet is a list of DavaFlowSets
				Iterator<DavaFlowSet<E>> it = explicitSet.iterator();

				// we know there is atleast one element
				toReturn = merge(output, it.next());

				while (it.hasNext()) {
					// merge this with toReturn
					toReturn = merge(toReturn, it.next());
				}
			}// a non empty explicitSet was found
		}// label not null could have explicit sets

		// toReturn contains result of dealing with explicit stmts

		// dealing with implicit set now
		if (implicitSet != null) {
			// implicitSet is a list of DavaFlowSets
			Iterator<DavaFlowSet<E>> it = implicitSet.iterator();
			while (it.hasNext()) {
				// merge this with toReturn
				toReturn = merge(toReturn, it.next());
			}
		}
		return toReturn;
	}

	/**
	 * Need to handleBreak stmts There can be explicit breaks (in which case
	 * label is non null) There can always be implicit breaks ASTNode is non
	 * null
	 */
	public DavaFlowSet<E> handleBreak(String label, DavaFlowSet<E> out, ASTNode node) {
		// get the explicit list with this label from the breakList
		List<DavaFlowSet<E>> explicitSet = out.getBreakSet(label);
		// System.out.println("\n\nExplicit set is"+explicitSet);
		// getting the implicit list now
		if (node == null)
			throw new RuntimeException("ASTNode sent to handleBreak was null");

		List<DavaFlowSet<E>> implicitSet = out.getImplicitlyBrokenSets(node);
		// System.out.println("\n\nImplicit set is"+implicitSet);

		// invoke mergeExplicitAndImplicit
		return mergeExplicitAndImplicit(label, out, explicitSet, implicitSet);
	}

	/**
	 * Need to handleContinue stmts There can be explicit continues (in which
	 * case label is non null) There can always be implicit continues ASTNode is
	 * non null
	 */
	public DavaFlowSet<E> handleContinue(String label, DavaFlowSet<E> out, ASTNode node) {
		// get the explicit list with this label from the continueList
		List<DavaFlowSet<E>> explicitSet = out.getContinueSet(label);

		// getting the implicit list now
		if (node == null)
			throw new RuntimeException("ASTNode sent to handleContinue was null");

		List<DavaFlowSet<E>> implicitSet = out.getImplicitlyContinuedSets(node);

		// invoke mergeExplicitAndImplicit
		return mergeExplicitAndImplicit(label, out, explicitSet, implicitSet);
	}

	/**
	 * Invoked from within the UnconditionalWhile processing method Need to
	 * handle both explicit and implicit breaks
	 */
	private DavaFlowSet<E> getMergedBreakList(String label, DavaFlowSet<E> output, ASTNode node) {
		List<DavaFlowSet<E>> breakSet = output.getBreakSet(label);
		DavaFlowSet<E> toReturn = null;

		if (breakSet == null) {
			// there is no list associated with this label hence no merging to
			// be done
			// since this is a call from unconditional this means there should
			// have been an implicit break
			toReturn = NOPATH;
		} else if (breakSet.size() == 0) {
			// list is empty for this label hence no merging to be done
			// since this is a call from unconditional this means there should
			// have been an implicit break
			toReturn = NOPATH;
		} else {
			// breakSet is a list of DavaFlowSets
			Iterator<DavaFlowSet<E>> it = breakSet.iterator();

			// we know there is atleast one element
			// making sure we dont send NOPATH
			toReturn = it.next();

			while (it.hasNext()) {
				// merge this with toReturn
				toReturn = merge(toReturn, it.next());
			}
		}// a non empty breakSet was found

		// dealing with implicit set now

		List<DavaFlowSet<E>> implicitSet = output.getImplicitlyBrokenSets(node);
		if (implicitSet != null) {
			// implicitSet is a list of DavaFlowSets
			Iterator<DavaFlowSet<E>> it = implicitSet.iterator();

			// making sure that we dont send NOPATH
			if (implicitSet.size() > 0)
				toReturn = it.next();

			while (it.hasNext()) {
				// merge this with toReturn
				toReturn = merge(toReturn, it.next());
			}
		}
		return toReturn;
	}

	public boolean isDifferent(DavaFlowSet<E> oldObj, DavaFlowSet<E> newObj) {
		if (oldObj.equals(newObj) && oldObj.internalDataMatchesTo(newObj)) {
			// set matches and breaks and continues also match
			// System.out.println("NOT DIFFERENT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			return false;
		} else {
			// System.out.println(oldObj);
			// System.out.println(newObj);
			// System.out.println("DIFFERENT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			return true;
		}
	}

	/*
	 * The before set contains the before set of an ASTNode , a Stmt , and
	 * AugmentedStmt, Notice for instance for a for loop we will get a before
	 * set before the loop and an after set after the loop
	 * 
	 * we dont have info about before set before executing a particular stmt
	 * that kind of info is available if you know which stmt u want e.g. the
	 * update stmt
	 */
	public DavaFlowSet<E> getBeforeSet(Object beforeThis) {
		return beforeSets.get(beforeThis);
	}

	public DavaFlowSet<E> getAfterSet(Object afterThis) {
		return afterSets.get(afterThis);
	}

	public void debug(String methodName, String debug) {
		if (DEBUG)
			System.out.println("Class: StructuredAnalysis MethodName: " + methodName + "    DEBUG: " + debug);
	}

	public void debug(String debug) {
		if (DEBUG)
			System.out.println("Class: StructuredAnalysis DEBUG: " + debug);
	}

}
