package soot.dava.toolkits.base.AST.transformations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.G;
import soot.Local;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.dava.DecompilationException;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DArrayInitExpr;
import soot.dava.internal.javaRep.DArrayInitValueBox;
import soot.dava.internal.javaRep.DAssignStmt;
import soot.dava.internal.javaRep.DShortcutAssignStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.dava.toolkits.base.AST.traversals.InitializationDeclarationShortcut;
import soot.jimple.ArrayRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.Stmt;

public class ShortcutArrayInit extends DepthFirstAdapter {
	public static boolean DEBUG = false;
	ASTMethodNode methodNode;

	public ShortcutArrayInit() {

	}

	public ShortcutArrayInit(boolean verbose) {
		super(verbose);
	}

	public void inASTMethodNode(ASTMethodNode node) {
		methodNode = node;
	}

	public void debug(String msg) {
		if (DEBUG)
			System.out.println("[SHortcutArrayInit]  DEBUG" + msg);
	}

	public void inASTStatementSequenceNode(ASTStatementSequenceNode node) {
		debug("inASTStatementSequenceNode");
		boolean success = false;
		ArrayList<AugmentedStmt> toRemove = new ArrayList<AugmentedStmt>();
		for (AugmentedStmt as : node.getStatements()) {
			success = false;
			Stmt s = as.get_Stmt();
			if (!(s instanceof DefinitionStmt))
				continue;

			DefinitionStmt ds = (DefinitionStmt) s;
			ValueBox right = ds.getRightOpBox();
			Value rightValue = right.getValue();

			if (!(rightValue instanceof NewArrayExpr))
				continue;

			debug("Found a new ArrayExpr" + rightValue);
			debug("Type of array is:" + rightValue.getType());

			// get type out
			Type arrayType = rightValue.getType();

			// get size....need to know this statically for sure!!!
			Value size = ((NewArrayExpr) rightValue).getSize();

			if (!(size instanceof IntConstant))
				continue;

			if (((IntConstant) size).value == 0) {
				debug("Size of array is 0 dont do anything");
				continue;
			}

			if (DEBUG)
				System.out.println("Size of array is: "
						+ ((IntConstant) size).value);

			Iterator<AugmentedStmt> tempIt = node.getStatements().iterator();
			// get to the array creation stmt
			while (tempIt.hasNext()) {
				AugmentedStmt tempAs = tempIt.next();
				Stmt tempS = tempAs.get_Stmt();
				if (tempS.equals(s))
					break;
			}
			// have the size have the type, tempIt is poised at the current def
			// stmt
			ValueBox[] array = new ValueBox[((IntConstant) size).value];
			success = true;
			for (int i = 0; i < ((IntConstant) size).value; i++) {
				// check these many next stmts they better all be array
				// initializations

				if (!tempIt.hasNext()) {
					// since its end of the stmt seq node just return
					if (DEBUG)
						System.out.println("returning");
					return;
				}

				AugmentedStmt aug = tempIt.next();
				Stmt augS = aug.get_Stmt();
				if (!isInSequenceAssignment(augS, ds.getLeftOp(), i)) {
					// cant create shortcut since we dont have all necessary
					// initializations
					if (DEBUG)
						System.out
								.println("Out of order assignment aborting attempt");

					success = false;
					break;
				} else {
					if (DEBUG)
						System.out
								.println("Assignment stmt in order adding to array");
					// the augS is the next assignment in the sequence add to
					// ValueBox array
					array[i] = ((DefinitionStmt) augS).getRightOpBox();
					toRemove.add(aug);
				}
			}// end checking for 1D pattern

			if (success) {
				DArrayInitExpr tempExpr = new DArrayInitExpr(array, arrayType);
				DArrayInitValueBox tempValueBox = new DArrayInitValueBox(
						tempExpr);
				DAssignStmt newStmt = new DAssignStmt(ds.getLeftOpBox(),
						tempValueBox);
				// cant do array initialization without declaration being part
				// of the stmt!!!!!
				// have to prove that this array is never utilized before i.e.
				// from start of method to this point there is no use
				// or def of this array then only can we create this decl/init
				// stmt
				if (DEBUG)
					System.out
							.println("Created new DAssignStmt and replacing it");

				InitializationDeclarationShortcut shortcutChecker = new InitializationDeclarationShortcut(
						as);
				methodNode.apply(shortcutChecker);
				boolean possible = shortcutChecker.isShortcutPossible();

				if (possible) {
					if (DEBUG)
						System.out.println("Shortcut is possible");

					// create shortcut stmt
					DShortcutAssignStmt newShortcutStmt = new DShortcutAssignStmt(
							newStmt, arrayType);
					as.set_Stmt(newShortcutStmt);
					// make sure to mark the local in the DVariableDeclarations
					// so that its not printed
					markLocal(ds.getLeftOp());

				}
				break;
			}
		}// end going through stmt seq node
		if (success) {
			// means we did a transformation remove the stmts
			List<AugmentedStmt> newStmtList = new ArrayList<AugmentedStmt>();
			for (AugmentedStmt as : node.getStatements()) {
				if (toRemove.contains(as)) {
					toRemove.remove(as);
				} else {
					newStmtList.add(as);
				}
			}
			node.setStatements(newStmtList);

			// make sure any other possible simplifications are done
			inASTStatementSequenceNode(node);
			G.v().ASTTransformations_modified = true;
		}

		// try the second pattern also
		secondPattern(node);
	}

	/*
	 * Check that s is a definition stmt which assigns to array leftOp and index
	 * location index
	 */
	public boolean isInSequenceAssignment(Stmt s, Value leftOp, int index) {
		// DEBUG=false;
		if (!(s instanceof DefinitionStmt))
			return false;

		DefinitionStmt ds = (DefinitionStmt) s;
		Value leftValue = ds.getLeftOp();
		if (!(leftValue instanceof ArrayRef))
			return false;

		if (DEBUG) {
			System.out.println("Stmt number " + index
					+ " is an array ref assignment" + leftValue);
			System.out.println("Array is" + leftOp);
		}

		ArrayRef leftRef = (ArrayRef) leftValue;
		if (!(leftOp.equals(leftRef.getBase()))) {
			if (DEBUG)
				System.out.println("Not assigning to same array");
			return false;
		}

		if (!(leftRef.getIndex() instanceof IntConstant)) {
			if (DEBUG)
				System.out.println("Cant determine index of assignment");
			return false;
		}

		IntConstant leftIndex = (IntConstant) leftRef.getIndex();
		if (leftIndex.value != index) {
			if (DEBUG)
				System.out.println("Out of order assignment");
			return false;
		}

		return true;
	}

	/*
	 * Maybe its a multi-D array then we need to look for a DAssignStmt followed
	 * by a definition Stmt
	 */
	public void secondPattern(ASTStatementSequenceNode node) {
		boolean success = false;
		ArrayList<AugmentedStmt> toRemove = new ArrayList<AugmentedStmt>();
		for (AugmentedStmt as : node.getStatements()) {
			success = false;
			Stmt s = as.get_Stmt();
			if (!(s instanceof DefinitionStmt))
				continue;

			DefinitionStmt ds = (DefinitionStmt) s;
			ValueBox right = ds.getRightOpBox();
			Value rightValue = right.getValue();

			if (!(rightValue instanceof NewArrayExpr))
				continue;

			if (DEBUG) {
				System.out.println("Found a new ArrayExpr" + rightValue);
				System.out.println("Type of array is:" + rightValue.getType());
			}

			// get type out
			Type arrayType = rightValue.getType();

			// get size....need to know this statically for sure!!!
			Value size = ((NewArrayExpr) rightValue).getSize();

			if (!(size instanceof IntConstant))
				continue;

			if (((IntConstant) size).value == 0) {
				debug("Found value to be 0 doing nothing");
				continue;
			}
			if (DEBUG)
				System.out.println("Size of array is: "
						+ ((IntConstant) size).value);

			Iterator<AugmentedStmt> tempIt = node.getStatements().iterator();
			// get to the array creation stmt
			while (tempIt.hasNext()) {
				AugmentedStmt tempAs = (AugmentedStmt) tempIt.next();
				Stmt tempS = tempAs.get_Stmt();
				if (tempS.equals(s))
					break;
			}
			// have the size have the type, tempIt is poised at the current def
			// stmt
			ValueBox[] array = new ValueBox[((IntConstant) size).value];
			success = true;
			for (int i = 0; i < ((IntConstant) size).value; i++) {
				// check for each iteration there is one DAssignStmt followed by
				// a DefinitionStmt
				if (!tempIt.hasNext()) {
					// since its end of the stmt seq node just return
					if (DEBUG)
						System.out.println("returning");
					return;
				}

				AugmentedStmt augOne = tempIt.next();
				Stmt augSOne = augOne.get_Stmt();

				if (!tempIt.hasNext()) {
					// since its end of the stmt seq node just return
					if (DEBUG)
						System.out.println("returning");
					return;
				}

				AugmentedStmt augTwo = tempIt.next();
				Stmt augSTwo = augTwo.get_Stmt();

				if (!isInSequenceAssignmentPatternTwo(augSOne, augSTwo,
						ds.getLeftOp(), i)) {
					// cant create shortcut since we dont have all necessary
					// initializations
					if (DEBUG)
						System.out
								.println("Out of order assignment aborting attempt");
					success = false;
					break;
				} else {
					if (DEBUG)
						System.out
								.println("Assignment stmt in order adding to array");
					// the RHS of augSOne is the next assignment in the sequence
					// add to ValueBox array
					array[i] = ((DShortcutAssignStmt) augSOne).getRightOpBox();
					toRemove.add(augOne);
					toRemove.add(augTwo);
				}
			}// end checking for 1D pattern

			if (success) {
				DArrayInitExpr tempExpr = new DArrayInitExpr(array, arrayType);
				DArrayInitValueBox tempValueBox = new DArrayInitValueBox(
						tempExpr);
				DAssignStmt newStmt = new DAssignStmt(ds.getLeftOpBox(),
						tempValueBox);
				// cant do array initialization without declaration being part
				// of the stmt!!!!!
				// have to prove that this array is never utilized before i.e.
				// from start of method to this point there is no use
				// or def of this array then only can we create this decl/init
				// stmt
				if (DEBUG)
					System.out
							.println("Created new DAssignStmt and replacing it");

				InitializationDeclarationShortcut shortcutChecker = new InitializationDeclarationShortcut(
						as);
				methodNode.apply(shortcutChecker);
				boolean possible = shortcutChecker.isShortcutPossible();

				if (possible) {
					if (DEBUG)
						System.out.println("Shortcut is possible");

					// create shortcut stmt
					DShortcutAssignStmt newShortcutStmt = new DShortcutAssignStmt(
							newStmt, arrayType);
					as.set_Stmt(newShortcutStmt);
					// make sure to mark the local in the DVariableDeclarations
					// so that its not printed
					markLocal(ds.getLeftOp());
				}

				break;
			}
		}// end going through stmt seq node
		if (success) {
			// means we did a transformation remove the stmts
			List<AugmentedStmt> newStmtList = new ArrayList<AugmentedStmt>();
			for (AugmentedStmt as : node.getStatements()) {
				if (toRemove.contains(as)) {
					toRemove.remove(as);
				} else {
					newStmtList.add(as);
				}
			}
			node.setStatements(newStmtList);

			// make sure any other possible simplifications are done
			inASTStatementSequenceNode(node);
			G.v().ASTTransformations_modified = true;
		}

	}

	/*
	 * Check that one is a definition stmt which declares an array Check that
	 * two is a definition of leftOp at index location
	 */
	public boolean isInSequenceAssignmentPatternTwo(Stmt one, Stmt two,
			Value leftOp, int index) {
		if (!(two instanceof DefinitionStmt))
			return false;

		DefinitionStmt ds = (DefinitionStmt) two;
		Value leftValue = ds.getLeftOp();
		if (!(leftValue instanceof ArrayRef))
			return false;

		ArrayRef leftRef = (ArrayRef) leftValue;
		if (!(leftOp.equals(leftRef.getBase()))) {
			if (DEBUG)
				System.out.println("Not assigning to same array");
			return false;
		}

		if (!(leftRef.getIndex() instanceof IntConstant)) {
			if (DEBUG)
				System.out.println("Cant determine index of assignment");
			return false;
		}

		IntConstant leftIndex = (IntConstant) leftRef.getIndex();
		if (leftIndex.value != index) {
			if (DEBUG)
				System.out.println("Out of order assignment");
			return false;
		}

		Value rightOp = ds.getRightOp();

		if (!(one instanceof DShortcutAssignStmt))
			return false;

		DShortcutAssignStmt shortcut = (DShortcutAssignStmt) one;
		Value shortcutVar = shortcut.getLeftOp();
		if (!shortcutVar.equals(rightOp))
			return false;

		return true;
	}

	// This local is declared using a shortcut declaration hence needs to be
	// somehow removed from the declared locals list
	// actually all that is needed is that this localnot be printed there! it
	// might be a good
	// idea to keep the local stored there since other analyses use that as the
	// list of all
	// declared locals in the method
	public void markLocal(Value shortcutLocal) {
		if (!(shortcutLocal instanceof Local))
			throw new DecompilationException(
					"Found non local. report to developer.");

		methodNode.addToDontPrintLocalsList((Local) shortcutLocal);
	}
}
