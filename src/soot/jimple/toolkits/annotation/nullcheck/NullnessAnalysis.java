/* NullnessAnalysis
 * Copyright (C) 2006 Eric Bodden
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import soot.Immediate;
import soot.Local;
import soot.RefLikeType;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.ClassConstant;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.MonitorStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JNeExpr;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;


/**
 * An intraprocedural nullness analysis that computes for each location and each value
 * in a method if the value is (before or after that location) definetely null,
 * definetely non-null or neither.
 * This class replaces {@link BranchedRefVarsAnalysis} which is known to have bugs.
 *
 * @author Eric Bodden
 */
public class NullnessAnalysis  extends ForwardBranchedFlowAnalysis
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

	protected final static Object TOP = new Object() {
		public String toString() {return "top";}
	};
	
	/**
	 * Creates a new analysis for the given graph/
	 * @param graph any unit graph
	 */
	public NullnessAnalysis(UnitGraph graph) {
		super(graph);
		
		doAnalysis();
	}

	/**
	 * {@inheritDoc}
	 */
	protected void flowThrough(Object flowin, Unit u, List fallOut, List branchOuts) {
		AnalysisInfo in = (AnalysisInfo) flowin;
		AnalysisInfo out = new AnalysisInfo(in);
		AnalysisInfo outBranch = new AnalysisInfo(in);
		
		Stmt s = (Stmt)u;
		
		//in case of an if statement, we neet to compute the branch-flow;
		//e.g. for a statement "if(x!=null) goto s" we have x==null for the fallOut and
		//x!=null for the branchOut
		//or for an instanceof expression
		if(s instanceof JIfStmt) {
			JIfStmt ifStmt = (JIfStmt) s;
			handleIfStmt(ifStmt, in, out, outBranch);
		}
		//in case of a monitor statement, we know that if it succeeds, we have a non-null value
		else if(s instanceof MonitorStmt) {
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
		//i.e. assign it the info of the rhs, except the following special cases:
		// x=null, assign NULL
		// x=@this or x= new... assign NON_NULL
		// x=@param_i, assign TOP
		if(s instanceof DefinitionStmt) {
			//need to copy the current out set because we need to assign under this assumption;
			//so this copy becomes the in-set to handleRefTypeAssignment
			AnalysisInfo temp = new AnalysisInfo(out);
			DefinitionStmt defStmt = (DefinitionStmt) s;
			if(defStmt.getLeftOp().getType() instanceof RefLikeType) {
				handleRefTypeAssignment(defStmt, temp, out);
			}
		}
		
		//safe memory by only retaining information about locals
		for (Iterator outIter = out.keySet().iterator(); outIter.hasNext();) {
			Value v = (Value) outIter.next();
			if(!(v instanceof Local)) {
				outIter.remove();
			}
		}
		for (Iterator outBranchIter = outBranch.keySet().iterator(); outBranchIter.hasNext();) {
			Value v = (Value) outBranchIter.next();
			if(!(v instanceof Local)) {
				outBranchIter.remove();
			}
		}

		// now copy the computed info to all successors
        for( Iterator it = fallOut.iterator(); it.hasNext(); ) {
            copy( out, it.next() );
        }
        for( Iterator it = branchOuts.iterator(); it.hasNext(); ) {
            copy( outBranch, it.next() );
        }
	}
	
	/**
	 * This can be overwritten by sublasses to mark a certain value
	 * as constantly non-null.
	 * @param v any value
	 * @return true if it is known that this value (e.g. a method
	 * return value) is never null
	 */
	protected boolean isAlwaysNonNull(Value v) {
		return false;
	}
	
	private void handleIfStmt(JIfStmt ifStmt, AnalysisInfo in, AnalysisInfo out, AnalysisInfo outBranch) {
		Value condition = ifStmt.getCondition();
		if(condition instanceof JInstanceOfExpr) {
			//a instanceof X ; if this succeeds, a is not null
			JInstanceOfExpr expr = (JInstanceOfExpr) condition;
			handleInstanceOfExpression(expr, in, out, outBranch);
		} else if(condition instanceof JEqExpr || condition instanceof JNeExpr) {
			//a==b or a!=b
			AbstractBinopExpr eqExpr = (AbstractBinopExpr) condition;
			handleEqualityOrNonEqualityCheck(eqExpr, in, out, outBranch);
		} 		
	}

	private void handleEqualityOrNonEqualityCheck(AbstractBinopExpr eqExpr, AnalysisInfo in,
			AnalysisInfo out, AnalysisInfo outBranch) {
		Value left = eqExpr.getOp1();
		Value right = eqExpr.getOp2();
		
		Value val=null;
		if(left==NullConstant.v()) {
			if(right!=NullConstant.v()) {
				val = right;
			}
		} else if(right==NullConstant.v()) {
			if(left!=NullConstant.v()) {
				val = left;
			}
		}
		
		//if we compare a local with null then process further...
		if(val!=null && val instanceof Local) {
			if(eqExpr instanceof JEqExpr)
				//a==null
				handleEquality(val, out, outBranch);
			else if(eqExpr instanceof JNeExpr)
				//a!=null
				handleNonEquality(val, out, outBranch);
			else
				throw new IllegalStateException("unexpected condition: "+eqExpr.getClass());
		}
	}

	private void handleNonEquality(Value val, AnalysisInfo out,
			AnalysisInfo outBranch) {
		out.put(val, NULL);
		outBranch.put(val, NON_NULL);
	}

	private void handleEquality(Value val, AnalysisInfo out,
			AnalysisInfo outBranch) {
		out.put(val, NON_NULL);
		outBranch.put(val, NULL);
	}
	
	private void handleInstanceOfExpression(JInstanceOfExpr expr,
			AnalysisInfo in, AnalysisInfo out, AnalysisInfo outBranch) {
		Value op = expr.getOp();
		//if instanceof succeeds, we have a non-null value
		outBranch.put(op,NON_NULL);
	}

	private void handleArrayRef(ArrayRef arrayRef, AnalysisInfo out) {
		Value array = arrayRef.getBase();
		//here we know that the array must point to an object, but the array value might be anything
		out.put(array, NON_NULL);
		out.put(arrayRef, TOP);
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
		out.put(fieldRef, TOP);
	}

	private void handleInvokeExpr(InvokeExpr invokeExpr,AnalysisInfo out) {
		if(invokeExpr instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
			//here we know that the receiver must point to an object
			Value base = instanceInvokeExpr.getBase();
			out.put(base,NON_NULL);
		}
		//but the returned object might point to everything
		out.put(invokeExpr, TOP);
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
		
		if(right instanceof NewExpr || right instanceof NewArrayExpr
		|| right instanceof NewMultiArrayExpr || right instanceof ThisRef
		|| right instanceof StringConstant || right instanceof ClassConstant) {
			//if we assign new... or @this, the result is non-null
			rhsInfo.put(right,NON_NULL);
		} else if(right instanceof ParameterRef) {
			//if we assign a parameter, we don't know anything
			rhsInfo.put(right,TOP);
		} else if(right==NullConstant.v()) {
			//if we assign null, well, it's null
			rhsInfo.put(right, NULL);
		}
		
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
			Set leftAndRight = new HashSet();
			leftAndRight.add(left.get(v));
			leftAndRight.add(right.get(v));			

			Object result;
			//TOP stays TOP
			if(leftAndRight.contains(TOP)) {
				result = TOP;
			} else if(leftAndRight.contains(NON_NULL)) {
				if(leftAndRight.contains(NULL)) {
					//NULL and NON_NULL merges to TOP
					result = TOP;
				} else {
					//NON_NULL and NON_NULL stays NON_NULL 
					result = NON_NULL;
				}
			} else if(leftAndRight.contains(NULL)) {
				//NULL and NULL stays NULL 
				result = NULL;
			} else {
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
	 * Returns <code>true</code> if the analysis could determine that i is always null
	 * before the statement s.
	 * @param s a statement of the respective body
	 * @param i a local or constant of that body
	 * @return true if i is always null right before this statement
	 */
	public boolean isAlwaysNullBefore(Unit s, Immediate i) {
		AnalysisInfo ai = (AnalysisInfo) getFlowBefore(s);
		return ai.get(i)==NULL;
	}

	/**
	 * Returns <code>true</code> if the analysis could determine that i is always non-null
	 * before the statement s.
	 * @param s a statement of the respective body
	 * @param i a local of that body
	 * @return true if i is always non-null right before this statement
	 */
	public boolean isAlwaysNonNullBefore(Unit s, Immediate i) {
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

