/* NullnessAnalysis
 * Copyright (C) 2006 Eric Bodden
 * Copyright (C) 2007 Julian Tibble
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
import java.util.Iterator;
import java.util.List;

import soot.Immediate;
import soot.Local;
import soot.RefLikeType;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.ClassConstant;
import soot.jimple.CaughtExceptionRef;
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
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JNeExpr;
import soot.shimple.PhiExpr;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardBranchedFlowAnalysis;


/**
 * An intraprocedural nullness analysis that computes for each location and each value
 * in a method if the value is (before or after that location) definitely null,
 * definitely non-null or neither.
 * This class replaces {@link BranchedRefVarsAnalysis} which is known to have bugs.
 *
 * @author Eric Bodden
 * @author Julian Tibble
 */
public class NullnessAnalysis  extends ForwardBranchedFlowAnalysis<NullnessAnalysis.AnalysisInfo>
{
	/**
	 * The analysis info is a simple mapping of type {@link Value} to
	 * any of the constants BOTTOM, NON_NULL, NULL or TOP.
	 * This class returns BOTTOM by default.
	 * 
	 * @author Julian Tibble
	 */
	protected class AnalysisInfo extends java.util.BitSet
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -9200043127757823764L;

		public AnalysisInfo() {
			super(used);
		}

		public AnalysisInfo(AnalysisInfo other) {
			super(used);
			or(other);
		}

		public int get(Value key)
		{
			if (!valueToIndex.containsKey(key))
				return BOTTOM;

			int index = valueToIndex.get(key);
			int result = get(index) ? 2 : 0;
			result += get(index + 1) ? 1 : 0;

			return result;
		}
		
		public void put(Value key, int val)
		{
			int index;
			if (!valueToIndex.containsKey(key)) {
				index = used;
				used += 2;
				valueToIndex.put(key, index);
			} else {
				index = valueToIndex.get(key);
			}
			set(index, (val & 2) == 2);
			set(index + 1, (val & 1) == 1);
		}
	}

	protected final static int BOTTOM = 0;
	protected final static int NULL = 1;
	protected final static int NON_NULL = 2;
	protected final static int TOP = 3;
	
	protected final HashMap<Value,Integer> valueToIndex = new HashMap<Value,Integer>();
	protected int used = 0;

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
	@Override
	protected void flowThrough(AnalysisInfo in, Unit u, List<AnalysisInfo> fallOut, List<AnalysisInfo> branchOuts) {
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
		
		// if we have an array ref, set the base to non-null
		if(s.containsArrayRef()) {
			ArrayRef arrayRef = s.getArrayRef();
			handleArrayRef(arrayRef,out);
		}
		// for field refs, set the receiver object to non-null, if there is one
		if(s.containsFieldRef()) {
			FieldRef fieldRef = s.getFieldRef();
			handleFieldRef(fieldRef, out);
		}
		// for invoke expr, set the receiver object to non-null, if there is one
		if(s.containsInvokeExpr()) {
			InvokeExpr invokeExpr = s.getInvokeExpr();
			handleInvokeExpr(invokeExpr, out);
		}
		
		//if we have a definition (assignment) statement to a ref-like type, handle it,
		//i.e. assign it TOP, except in the following special cases:
		// x=null,               assign NULL
		// x=@this or x= new...  assign NON_NULL
		// x=y,                  copy the info for y (for locals x,y)
		if(s instanceof DefinitionStmt) {
			DefinitionStmt defStmt = (DefinitionStmt) s;
			if(defStmt.getLeftOp().getType() instanceof RefLikeType) {
				handleRefTypeAssignment(defStmt, out);
			}
		}
		
		// now copy the computed info to all successors
		for( Iterator<AnalysisInfo> it = fallOut.iterator(); it.hasNext(); ) {
			copy( out, it.next() );
		}
		for( Iterator<AnalysisInfo> it = branchOuts.iterator(); it.hasNext(); ) {
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
		//here we know that the array must point to an object
		out.put(array, NON_NULL);
	}

	private void handleFieldRef(FieldRef fieldRef,
			AnalysisInfo out) {
		if(fieldRef instanceof InstanceFieldRef) {
			InstanceFieldRef instanceFieldRef = (InstanceFieldRef) fieldRef;
			//here we know that the receiver must point to an object
			Value base = instanceFieldRef.getBase();
			out.put(base,NON_NULL);
		}
	}

	private void handleInvokeExpr(InvokeExpr invokeExpr,AnalysisInfo out) {
		if(invokeExpr instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr instanceInvokeExpr = (InstanceInvokeExpr) invokeExpr;
			//here we know that the receiver must point to an object
			Value base = instanceInvokeExpr.getBase();
			out.put(base,NON_NULL);
		}
	}

	private void handleRefTypeAssignment(DefinitionStmt assignStmt, AnalysisInfo out) {
		Value left = assignStmt.getLeftOp();
		Value right = assignStmt.getRightOp();
		
		//unbox casted value
		if(right instanceof JCastExpr) {
			JCastExpr castExpr = (JCastExpr) right;
			right = castExpr.getOp();
		}
		
		//if we have a definition (assignment) statement to a ref-like type, handle it,
		if ( isAlwaysNonNull(right)
		|| right instanceof NewExpr || right instanceof NewArrayExpr
		|| right instanceof NewMultiArrayExpr || right instanceof ThisRef
		|| right instanceof StringConstant || right instanceof ClassConstant
		|| right instanceof CaughtExceptionRef) {
			//if we assign new... or @this, the result is non-null
			out.put(left,NON_NULL);
		} else if(right==NullConstant.v()) {
			//if we assign null, well, it's null
			out.put(left, NULL);
		} else if(left instanceof Local && right instanceof Local) {
			out.put(left, out.get(right));
		} else if(left instanceof Local && right instanceof PhiExpr) {
			handlePhiExpr(out, left, (PhiExpr)right);
		} else {
			out.put(left, TOP);
		}
	}

	private void handlePhiExpr(AnalysisInfo out, Value left, PhiExpr right) {
		int curr = BOTTOM;
		for(Value v : right.getValues()) {
			int nullness = out.get(v);
			if(nullness == BOTTOM) {
				continue;
			} else if(nullness == TOP) {
				out.put(left, TOP);
				return;
			} else if(nullness == NULL) {
				if(curr == BOTTOM) {
					curr = NULL;
				} else if(curr != NULL) {
					out.put(left, TOP);
					return;
				}
			} else if(nullness == NON_NULL) {
				if(curr == BOTTOM) {
					curr = NON_NULL;
				} else if(curr != NON_NULL) {
					out.put(left, TOP);
					return;
				}
			}
		}
		out.put(left, curr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void copy(AnalysisInfo s, AnalysisInfo d) {
		d.clear();
		d.or(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void merge(AnalysisInfo in1, AnalysisInfo in2, AnalysisInfo out) {
		out.clear();
		out.or(in1);
		out.or(in2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AnalysisInfo newInitialFlow() {
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
		return getFlowBefore(s).get(i) == NULL;
	}

	/**
	 * Returns <code>true</code> if the analysis could determine that i is always non-null
	 * before the statement s.
	 * @param s a statement of the respective body
	 * @param i a local of that body
	 * @return true if i is always non-null right before this statement
	 */
	public boolean isAlwaysNonNullBefore(Unit s, Immediate i) {
		return getFlowBefore(s).get(i) == NON_NULL;
	}
}
