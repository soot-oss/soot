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
 * Maintained by Nomair A. Naeem
 */

/*
 * CHANGE LOG: * 30th Jan 2006 Class Created (used atleast by FinalFieldDefinition)
 *
 *
 */

package soot.dava.toolkits.base.AST.structuredAnalysis;

import soot.*;
import soot.dava.*;
import java.util.*;

import soot.jimple.*;
//import soot.dava.internal.javaRep.*;
import soot.dava.internal.AST.*;

/*
 * The analysis stores all defs of Locals/SootField. The user can then ask whether a local or SootField
 isMustInitialized or isMayInitialized
 *
 MustInitialize/MayInitialize
 Step 1:
 Set of initialized locals/SootField
 Step 2:
 A local or SootField is MUST initialized at a program point p if on all paths from the start to this
 point the local or SootField is assigned a value. 

 Similarly a local or SootField is MAY initialized at a program point p if there is a path from the start
 to this point on wich the local or SootField is assigned
 Step 3:
 Forward Analysis
 Step 4:
 Intersection/Union
 Step 5:
 x = expr
 kill = {}

 if x is a local or SootField, gen(x) = {x}
 Step 6:
 out(start) = {}
 newInitialFlow: No copies are available. an empty flow set
 remember new InitialFlow is ONLY used for input to catchBodies
 *
 *
 */

public class MustMayInitialize extends StructuredAnalysis {
	HashMap<Object, List> mapping;
	DavaFlowSet finalResult;

	public static final int MUST = 0;
	public static final int MAY = 1;

	int MUSTMAY;

	public MustMayInitialize(Object analyze, int MUSTorMAY) {
		super();
		mapping = new HashMap<Object, List>();
		MUSTMAY = MUSTorMAY;

		// System.out.println("MustOrMay value is"+MUSTorMAY);
		setMergeType();
		// the input to the process method is an empty DavaFlow Set meaning
		// out(start) ={} (no var initialized)
		finalResult = (DavaFlowSet) process(analyze, new DavaFlowSet());

		// finalResult contains the flowSet of having processed the whole of the
		// method
	}

	public DavaFlowSet emptyFlowSet() {
		return new DavaFlowSet();
	}

	public void setMergeType() {
		// System.out.println("here"+MUSTMAY);
		if (MUSTMAY == MUST) {
			MERGETYPE = INTERSECTION;
			// System.out.println("MERGETYPE set to intersection");
		} else if (MUSTMAY == MAY) {
			MERGETYPE = UNION;
			// System.out.println("MERGETYPE set to union");
		} else
			throw new DavaFlowAnalysisException("Only allowed 0 or 1 for MUST or MAY values");
	}

	/*
	 * newInitialFlow set is used only for start of catch bodies and here we
	 * assume that no var is ever being initialized
	 */
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
	public DavaFlowSet processStatement(Stmt s, DavaFlowSet inSet) {
		/*
		 * If this path will not be taken return no path straightaway
		 */
		if (inSet == NOPATH) {
			return inSet;
		}

		if (s instanceof DefinitionStmt) {
			DavaFlowSet toReturn = (DavaFlowSet) cloneFlowSet(inSet);
			// x = expr;

			Value leftOp = ((DefinitionStmt) s).getLeftOp();

			SootField field = null;
			;
			if (leftOp instanceof Local) {
				toReturn.add(leftOp);

				/*
				 * Gather more information just in case someone might need the
				 * def points
				 */
				Object temp = mapping.get(leftOp);
				List<Stmt> defs;

				if (temp == null) {
					// first definition
					defs = new ArrayList<Stmt>();
				} else {
					defs = (ArrayList<Stmt>) temp;
				}
				defs.add(s);
				mapping.put(leftOp, defs);

			} else if (leftOp instanceof FieldRef) {
				field = ((FieldRef) leftOp).getField();
				toReturn.add(field);

				/*
				 * Gather more information just in case someone might need the
				 * def points
				 */
				Object temp = mapping.get(field);
				List<Stmt> defs;

				if (temp == null) {
					// first definition
					defs = new ArrayList<Stmt>();
				} else {
					defs = (ArrayList<Stmt>) temp;
				}
				defs.add(s);

				mapping.put(field, defs);
			}
			return toReturn;
		}
		return inSet;
	}

	public boolean isMayInitialized(SootField field) {
		if (MUSTMAY == MAY) {
			Object temp = mapping.get(field);
			if (temp == null)
				return false;
			else {
				List list = (List) temp;
				if (list.size() == 0)
					return false;
				else
					return true;
			}
		} else
			throw new RuntimeException("Cannot invoke isMayInitialized for a MUST analysis");
	}

	public boolean isMayInitialized(Value local) {
		if (MUSTMAY == MAY) {
			Object temp = mapping.get(local);
			if (temp == null)
				return false;
			else {
				List list = (List) temp;
				if (list.size() == 0)
					return false;
				else
					return true;
			}
		} else
			throw new RuntimeException("Cannot invoke isMayInitialized for a MUST analysis");
	}

	public boolean isMustInitialized(SootField field) {
		if (MUSTMAY == MUST) {
			if (finalResult.contains(field))
				return true;
			return false;
		} else
			throw new RuntimeException("Cannot invoke isMustinitialized for a MAY analysis");
	}

	public boolean isMustInitialized(Value local) {
		if (MUSTMAY == MUST) {
			if (finalResult.contains(local))
				return true;
			return false;
		} else
			throw new RuntimeException("Cannot invoke isMustinitialized for a MAY analysis");
	}

	/*
	 * Given a local ask for all def positions Notice this could be null in the
	 * case there was no definition
	 */
	public List getDefs(Value local) {
		Object temp = mapping.get(local);
		if (temp == null)
			return null;
		else
			return (List) temp;
	}

	/*
	 * Given a field ask for all def positions Notice this could be null in the
	 * case there was no definition
	 */
	public List getDefs(SootField field) {
		Object temp = mapping.get(field);
		if (temp == null)
			return null;
		else
			return (List) temp;
	}

}