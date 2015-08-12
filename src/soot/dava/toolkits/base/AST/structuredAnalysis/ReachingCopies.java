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

/*
 * Maintained by Nomair A. Naeem
 */

/*
 * CHANGE LOG: * November 22nd: Removed check of DAbruptStmt from analysis since
 *               this is now handled by the structredAnalysis framework
 *             
 *             * November 22nd: Inlined the LocalPair class
 *               Tested Extensively: found bug in implementation of process_doWhile in structuredAnalysis :)
 */

package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.AST.*;

/*
 ReachingCopies
 Step 1:
 Set of pairs where each pair has the form (a,b) indicating a statement a=b
 Step 2:
 A copy statement (a=b) reaches a statement s if all paths leading to s have a copy
 statemet a=b and the values of a and b are not changed between the copy statement and the statement s.
 Step 3:
 Forward Analysis
 Step 4:
 Intersection
 Step 5:
 x = expr
 kill = { all pairs containing x in left or right position}

 if expr is a local , y
 gen = (x,y) 
 Step 6:
 out(start) = {}
 newInitialFlow: No copies are available. an empty flow set
 remember new InitialFlow is ONLY used for input to catchBodies

 In ordinary flow analyses one has to assume that out(Si) is the universal set
 for reaching copies. However the way structured flow analysis works
 there is no need for such an assumption since it is never used in the structured flow analysis code
 */

public class ReachingCopies extends StructuredAnalysis {

	/***************** DEFINIING LOCAL PAIR CLASS ************************/
	public class LocalPair {
		private final Local leftLocal;
		private final Local rightLocal;

		public LocalPair(Local left, Local right) {
			leftLocal = left;
			rightLocal = right;
		}

		public Local getLeftLocal() {
			return leftLocal;
		}

		public Local getRightLocal() {
			return rightLocal;
		}

		public boolean equals(Object other) {
			if (other instanceof LocalPair) {
				if (this.leftLocal.toString().equals(((LocalPair) other).getLeftLocal().toString())) {
					if (this.rightLocal.toString().equals(((LocalPair) other).getRightLocal().toString())) {
						return true;
					}
				}
			}
			return false;
		}

		/**
		 * Method checks whether local occurs in the left or right side of the
		 * localpair different semantics than the usual contains method which
		 * checks something in a list
		 */
		public boolean contains(Local local) {
			if (leftLocal.toString().equals(local.toString()) || rightLocal.toString().equals(local.toString())) {
				return true;
			}
			return false;
		}

		public String toString() {
			StringBuffer b = new StringBuffer();
			b.append("<" + leftLocal.toString() + "," + rightLocal.toString() + ">");
			return b.toString();
		}

	}

	/****************************** END OF LOCAL PAIR CLASS ***********************/

	public ReachingCopies(Object analyze) {
		super();
		// the input to the process method is an empty DavaFlow Set meaning
		// out(start) ={}
		DavaFlowSet temp = (DavaFlowSet) process(analyze, new DavaFlowSet());
	}

	public DavaFlowSet emptyFlowSet() {
		return new DavaFlowSet();
	}

	public void setMergeType() {
		MERGETYPE = INTERSECTION;
	}
	
	@Override
	public DavaFlowSet newInitialFlow() {
		return new DavaFlowSet();
	}

	@Override
	public DavaFlowSet cloneFlowSet(DavaFlowSet flowSet) {
		return ((DavaFlowSet) flowSet).clone();
	}

	/*
	 * By construction conditions never have assignment statements. Hence
	 * processing a condition has no effect on this analysis
	 */
	@Override
	public DavaFlowSet processUnaryBinaryCondition(ASTUnaryBinaryCondition cond, DavaFlowSet input) {
		return input;
	}

	/*
	 * By construction the synchronized Local is a Value and can definetly not
	 * have an assignment stmt Processing a synch local has no effect on this
	 * analysis
	 */
	@Override
	public DavaFlowSet processSynchronizedLocal(Local local, DavaFlowSet input) {
		return input;
	}

	/*
	 * The switch key is stored as a value and hence can never have an
	 * assignment stmt Processing the switch key has no effect on the analysis
	 */
	@Override
	public DavaFlowSet processSwitchKey(Value key, DavaFlowSet input) {
		return input;
	}

	/*
	 * This method internally invoked by the process method decides which
	 * Statement specialized method to call
	 */
	@Override
	public DavaFlowSet processStatement(Stmt s, DavaFlowSet input) {
		DavaFlowSet inSet = (DavaFlowSet) input;

		/*
		 * If this path will not be taken return no path straightaway
		 */
		if (inSet == NOPATH) {
			return inSet;
		}

		if (s instanceof DefinitionStmt) {
			DavaFlowSet toReturn = (DavaFlowSet) cloneFlowSet(inSet);
			// x = expr;
			// check if expr is a local in which case this is a copy
			Value leftOp = ((DefinitionStmt) s).getLeftOp();
			Value rightOp = ((DefinitionStmt) s).getRightOp();

			if (leftOp instanceof Local) {
				// KILL any available copy with local since it has been
				// redefined
				kill(toReturn, (Local) leftOp);
			}// leftop is a local

			if (leftOp instanceof Local && rightOp instanceof Local) {
				// this is a copy statement
				// GEN
				gen(toReturn, (Local) leftOp, (Local) rightOp);
			}
			return toReturn;
		} else {
			return input;
		}
	}

	public void gen(DavaFlowSet in, Local left, Local right) {
		// adding localpair
		// no need to check for duplicates as the DavaFlowSet checks that
		LocalPair localp = new LocalPair(left, right);
		in.add(localp);
	}

	public void kill(DavaFlowSet<LocalPair> in, Local redefined) {
		// kill any previous localpairs which have the redefined Local in the
		// left OR right position
		for (Iterator<LocalPair> listIt = in.iterator(); listIt.hasNext(); ) {
			LocalPair tempPair = listIt.next();
			if (tempPair.contains(redefined)) {
				// need to kill this from the list
				listIt.remove();
			}
		}
	}

	/*
	 * Wrapper method to get before set of an ASTNode or Statement which gives
	 * us the reaching copies at this point
	 */
	public DavaFlowSet getReachingCopies(Object node) {
		// get the before set for this node
		DavaFlowSet beforeSet = getBeforeSet(node);

		if (beforeSet == null) {
			throw new RuntimeException("Could not get reaching copies of node/stmt");
		}
		// Get all reachingCopies

		/*
		 * the list that toList of this object contains elements of type
		 * LocalPair (a,b) which means this is a copy stmt of the form a=b
		 */

		return beforeSet;
	}

}