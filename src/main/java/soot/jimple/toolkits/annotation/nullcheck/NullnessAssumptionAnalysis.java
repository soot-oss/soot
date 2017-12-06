/* NullnessAssumptionAnalysis
 * Copyright (C) 2006 Richard L. Halpert
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

package soot.jimple.toolkits.annotation.nullcheck;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import soot.Immediate;
import soot.Local;
import soot.RefLikeType;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.MonitorStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JCastExpr;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;


/**
 * An intraprocedural nullness assumption analysis that computes for each location and each value
 * in a method if the value (before or after that location) is treated as definitely null,
 * definitely non-null or neither.  This information could be useful in deciding whether
 * or not to insert code that accesses a potentially null object.  If the original
 * program assumes a value is non-null, then adding a use of that value 
 * will not introduce any NEW nullness errors into the program.
 * This code may be buggy, or just plain wrong.  It has not been checked.
 *
 * @author Richard L. Halpert
 * Adapted from Eric Bodden's NullnessAnalysis
 */
public class NullnessAssumptionAnalysis  extends BackwardFlowAnalysis
{
	protected final static Object BOTTOM = new Object() {
		public String toString() {return "bottom";}
	};

	protected final static Object NULL = new Object() {
		public String toString() {return "null";}
	};

	protected final static Object NON_NULL = new Object() {
		public String toString() {return "non-null";}
	};

	// TOP IS MEANINGLESS FOR THIS ANALYSIS: YOU CAN'T ASSUME A VALUE IS NULL AND NON_NULL.  BOTTOM IS USED FOR THAT CASE
	protected final static Object TOP = new Object() {
		public String toString() {return "top";}
	};
	
	/**
	 * Creates a new analysis for the given graph/
	 * @param graph any unit graph
	 */
	public NullnessAssumptionAnalysis(UnitGraph graph) {
		super(graph);
		
		doAnalysis();
	}

	/**
	 * {@inheritDoc}
	 */
	protected void flowThrough(Object inValue, Object unit, Object outValue)
//	protected void flowThrough(Object flowin, Unit u, List fallOut, List branchOuts)
	{
		AnalysisInfo in = (AnalysisInfo) inValue;
		AnalysisInfo out = new AnalysisInfo(in);
		
		Stmt s = (Stmt) unit;
		
		//in case of an if statement, we neet to compute the branch-flow;
		//e.g. for a statement "if(x!=null) goto s" we have x==null for the fallOut and
		//x!=null for the branchOut
		//or for an instanceof expression
//		if(s instanceof JIfStmt) {
//			JIfStmt ifStmt = (JIfStmt) s;
//			handleIfStmt(ifStmt, in, out, outBranch);
//		}
		//in case of a monitor statement, we know that the programmer assumes we have a non-null value
		if(s instanceof MonitorStmt) {
			MonitorStmt monitorStmt = (MonitorStmt) s;
			out.put(monitorStmt.getOp(), NON_NULL);
		}
		
		//if we have an array ref, set the info for this ref to TOP,
		//cause we need to be conservative here
		if(s.containsArrayRef()) {
			ArrayRef arrayRef = s.getArrayRef();
			handleArrayRef(arrayRef,out);
		}
		//same for field refs, but also set the receiver object to non-null, if there is one
		if(s.containsFieldRef()) {
			FieldRef fieldRef = s.getFieldRef();
			handleFieldRef(fieldRef, out);
		}
		//same for invoke expr., also set the receiver object to non-null, if there is one
		if(s.containsInvokeExpr()) {
			InvokeExpr invokeExpr = s.getInvokeExpr();
			handleInvokeExpr(invokeExpr, out);
		}
		
		//allow sublasses to define certain values as always-non-null
		for (Iterator outIter = out.entrySet().iterator(); outIter.hasNext();) {
			Entry entry = (Entry) outIter.next();
			Value v = (Value) entry.getKey();
			if(isAlwaysNonNull(v)) {
				entry.setValue(NON_NULL);
			}
		}
		
		//if we have a definition (assignment) statement to a ref-like type, handle it,
		if(s instanceof DefinitionStmt) {
			//need to copy the current out set because we need to assign under this assumption;
			//so this copy becomes the in-set to handleRefTypeAssignment
			AnalysisInfo temp = new AnalysisInfo(out);
			DefinitionStmt defStmt = (DefinitionStmt) s;
			if(defStmt.getLeftOp().getType() instanceof RefLikeType) {
				handleRefTypeAssignment(defStmt, temp, out);
			}
		}
		
		//save memory by only retaining information about locals
		for (Iterator outIter = out.keySet().iterator(); outIter.hasNext();) {
			Value v = (Value) outIter.next();
			if(!(v instanceof Local)) {
				outIter.remove();
			}
		}
//		for (Iterator outBranchIter = outBranch.keySet().iterator(); outBranchIter.hasNext();) {
//			Value v = (Value) outBranchIter.next();
//			if(!(v instanceof Local)) {
//				outBranchIter.remove();
//			}
//		}

		// now copy the computed info to out
        copy( out, outValue );
	}
	
	/**
	 * This can be overridden by sublasses to mark a certain value
	 * as constantly non-null.
	 * @param v any value
	 * @return true if it is known that this value (e.g. a method
	 * return value) is never null
	 */
	protected boolean isAlwaysNonNull(Value v) {
		return false;
	}

	private void handleArrayRef(ArrayRef arrayRef, AnalysisInfo out) {
		Value array = arrayRef.getBase();
		//here we know that the array must point to an object, but the array value might be anything
		out.put(array, NON_NULL);
//		out.put(arrayRef, TOP);
	}

	private void handleFieldRef(FieldRef fieldRef,
			AnalysisInfo out) {
		if(fieldRef instanceof InstanceFieldRef) {
			InstanceFieldRef instanceFieldRef = (InstanceFieldRef) fieldRef;
			//here we know that the receiver must point to an object
			Value base = instanceFieldRef.getBase();
			out.put(base,NON_NULL);
		}
		//but the referenced object might point to everything
//		out.put(fieldRef, TOP);
	}

	private void handleInvokeExpr(InvokeExpr invokeExpr,AnalysisInfo out) {
		if(invokeExpr instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
			//here we know that the receiver must point to an object
			Value base = instanceInvokeExpr.getBase();
			out.put(base,NON_NULL);
		}
		//but the returned object might point to everything
//		out.put(invokeExpr, TOP);
	}

	private void handleRefTypeAssignment(DefinitionStmt assignStmt,
			AnalysisInfo rhsInfo, AnalysisInfo out) {
		Value left = assignStmt.getLeftOp();
		Value right = assignStmt.getRightOp();
		
		//unbox casted value
		if(right instanceof JCastExpr) {
			JCastExpr castExpr = (JCastExpr) right;
			right = castExpr.getOp();
		}
		
		// An assignment invalidates any assumptions of null/non-null for lhs
		// We COULD be more accurate by assigning those assumptions to the rhs prior to this statement
		rhsInfo.put(right,BOTTOM);
		
		//assign from rhs to lhs
		out.put(left,rhsInfo.get(right));
	}

	/**
	 * {@inheritDoc}
	 */
	protected void copy(Object source, Object dest) {
		Map s = (Map) source;
		Map d = (Map) dest;
		d.clear();
		d.putAll(s);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object entryInitialFlow() {
		return new AnalysisInfo();
	}
	/**
	 * {@inheritDoc}
	 */
	protected void merge(Object in1, Object in2, Object out) {
		AnalysisInfo left = (AnalysisInfo) in1;
		AnalysisInfo right = (AnalysisInfo) in2;
		AnalysisInfo res = (AnalysisInfo) out;
		
		Set values = new HashSet();
		values.addAll(left.keySet());
		values.addAll(right.keySet());
		
		res.clear();
		
		for (Iterator keyIter = values.iterator(); keyIter.hasNext();) {
			Value v = (Value) keyIter.next();
			Set<Object> leftAndRight = new HashSet<Object>();
			leftAndRight.add(left.get(v));
			leftAndRight.add(right.get(v));			

			Object result;
			// This needs to be corrected for assumption *** TODO
			//TOP stays TOP
			if(leftAndRight.contains(BOTTOM)) // if on either side we know nothing... then together we know nothing for sure
			{
				result = BOTTOM;
			}
			else if(leftAndRight.contains(NON_NULL))
			{
				if(leftAndRight.contains(NULL)) 
				{
					//NULL and NON_NULL merges to BOTTOM
					result = BOTTOM;
				}
				else 
				{
					//NON_NULL and NON_NULL stays NON_NULL 
					result = NON_NULL;
				}
			}
			else if(leftAndRight.contains(NULL))
			{
				//NULL and NULL stays NULL 
				result = NULL;
			}
			else
			{
				//only BOTTOM remains 
				result = BOTTOM;
			}
			
			res.put(v, result);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object newInitialFlow() {
		return new AnalysisInfo();
	}
	
	/**
	 * Returns <code>true</code> if the analysis could determine that i is always treated as null
	 * after and including the statement s.
	 * @param s a statement of the respective body
	 * @param i a local or constant of that body
	 * @return true if i is always null right before this statement
	 */
	public boolean isAssumedNullBefore(Unit s, Immediate i) {
		AnalysisInfo ai = (AnalysisInfo) getFlowBefore(s);
		return ai.get(i)==NULL;
	}

	/**
	 * Returns <code>true</code> if the analysis could determine that i is always treated as non-null
	 * after and including the statement s.
	 * @param s a statement of the respective body
	 * @param i a local of that body
	 * @return true if i is always non-null right before this statement
	 */
	public boolean isAssumedNonNullBefore(Unit s, Immediate i) {
		AnalysisInfo ai = (AnalysisInfo) getFlowBefore(s);
		return ai.get(i)==NON_NULL;
	}

	/**
	 * The analysis info is a simple mapping of type {@link Value} to
	 * any of the constants BOTTOM, NON_NULL, NULL or TOP.
	 * This class returns BOTTOM by default.
	 * 
	 * @author Eric Bodden
	 */
	protected static class AnalysisInfo extends HashMap {
		
		public AnalysisInfo() {
			super();
		}

		public AnalysisInfo(Map m) {
			super(m);
		}

		public Object get(Object key) {
			Object object = super.get(key);
			if(object==null) {
				return BOTTOM;
			}
			return object;
		}
		
	}


}

