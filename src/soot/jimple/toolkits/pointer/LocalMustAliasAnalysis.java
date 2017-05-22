/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Patrick Lam
 * Copyright (C) 2007 Eric Bodden
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
package soot.jimple.toolkits.pointer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.EquivalentValue;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.RefLikeType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityRef;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/** LocalMustAliasAnalysis attempts to determine if two local
 * variables (at two potentially different program points) must point
 * to the same object.
 *
 * The underlying abstraction is based on global value numbering.
 * 
 * See also {@link StrongLocalMustAliasAnalysis} for an analysis
 * that soundly treats redefinitions within loops.
 * 
 * See Sable TR 2007-8 for details.
 * 
 * P.S. The idea behind this analysis and the way it assigns numbers is very
 * similar to what is described in the paper:
 * Lapkowski, C. and Hendren, L. J. 1996. Extended SSA numbering: introducing SSA properties to languages with multi-level pointers.
 * In Proceedings of the 1996 Conference of the Centre For Advanced Studies on Collaborative Research (Toronto, Ontario, Canada, November 12 - 14, 1996).
 * M. Bauer, K. Bennet, M. Gentleman, H. Johnson, K. Lyons, and J. Slonim, Eds. IBM Centre for Advanced Studies Conference. IBM Press, 23. 
 * 
 * Only major differences: Here we only use primary numbers, no secondary numbers. Further, we use the call graph to determine fields
 * that are not written to in the transitive closure of this method's execution. A third difference is that we assign fixed values
 * to {@link IdentityRef}s, because those never change during one execution. 
 * 
 * @author Patrick Lam
 * @author Eric Bodden
 * @see StrongLocalMustAliasAnalysis
 * */
public class LocalMustAliasAnalysis extends ForwardFlowAnalysis<Unit,HashMap<Value,Integer>>
{
	
    /**
     * The set of all local variables and field references that we track.
     * This set contains objects of type {@link Local} and, if tryTrackFieldAssignments is
     * enabled, it may also contain {@link EquivalentValue}s of {@link FieldRef}s.
     * If so, these field references are to be tracked on the same way as {@link Local}s are.
     */
    protected Set<Value> localsAndFieldRefs;

    /** maps from right-hand side expressions (non-locals) to value numbers */
    protected transient Map<Value,Integer> rhsToNumber;
    
    /** maps from a merge point (a unit) and a value to the unique value number of that value at this point */
    protected transient Map<Unit,Map<Value,Integer>> mergePointToValueToNumber;

    /** the next value number */
    protected int nextNumber = 1;

	/** the containing method */
	protected SootMethod container;
    
    /**
     * Creates a new {@link LocalMustAliasAnalysis} tracking local variables.
     */
    public LocalMustAliasAnalysis(UnitGraph g) {
    	this(g,false);
    }
	
    /**
     * Creates a new {@link LocalMustAliasAnalysis}. If tryTrackFieldAssignments,
     * we run an interprocedural side-effects analysis to determine which fields
     * are (transitively) written to by this method. All fields which that are not written
     * to are tracked just as local variables. This semantics is sound for single-threaded programs.  
     */
	public LocalMustAliasAnalysis(UnitGraph g, boolean tryTrackFieldAssignments) {
        super(g);
        this.container = g.getBody().getMethod();
        this.localsAndFieldRefs = new HashSet<Value>(); 
        
        //add all locals
        for (Local l : (Collection<Local>) g.getBody().getLocals()) {
            if (l.getType() instanceof RefLikeType)
                this.localsAndFieldRefs.add(l);
        }
		
        if(tryTrackFieldAssignments) {
        	this.localsAndFieldRefs.addAll(trackableFields());
        }

       	this.rhsToNumber = new HashMap<Value, Integer>();
        this.mergePointToValueToNumber = new HashMap<Unit,Map<Value,Integer>>();
        
        doAnalysis();
        
        //not needed any more
        this.rhsToNumber = null;
        this.mergePointToValueToNumber = null;
    }

    /**
     * Computes the set of {@link EquivalentValue}s of all field references that are used
     * in this method but not set by the method or any method transitively called by this method.
     */
    private Set<Value> trackableFields() {
    	Set<Value> usedFieldRefs = new HashSet<Value>();
        //add all field references that are in use boxes
        for (Unit unit : this.graph) {
			Stmt s = (Stmt) unit;
			List<ValueBox> useBoxes = s.getUseBoxes();
			for (ValueBox useBox : useBoxes) {
				Value val = useBox.getValue();
				if(val instanceof FieldRef) {
					FieldRef fieldRef = (FieldRef) val;
					if(fieldRef.getType() instanceof RefLikeType)
						usedFieldRefs.add(new EquivalentValue(fieldRef));						
				}
			}
		}
        
        //prune all fields that are written to
        if(!usedFieldRefs.isEmpty()) {
    	
	    	if(!Scene.v().hasCallGraph()) {
	    		throw new IllegalStateException("No call graph found!");
	    	}
	    	    	
			CallGraph cg = Scene.v().getCallGraph();
			ReachableMethods reachableMethods = new ReachableMethods(cg,Collections.<MethodOrMethodContext>singletonList(container));
			reachableMethods.update();
			for (Iterator<MethodOrMethodContext> iterator = reachableMethods.listener(); iterator.hasNext();) {
				SootMethod m = (SootMethod) iterator.next();
				if(m.hasActiveBody() &&
				//exclude static initializer of same class (assume that it has already been executed)
				 !(m.getName().equals(SootMethod.staticInitializerName) && m.getDeclaringClass().equals(container.getDeclaringClass()))) {				
					for (Unit u : m.getActiveBody().getUnits()) {
						List<ValueBox> defBoxes = u.getDefBoxes();
						for (ValueBox defBox : defBoxes) {
							Value value = defBox.getValue();
							if(value instanceof FieldRef) {
								usedFieldRefs.remove(new EquivalentValue(value));
							}
						}
					}
				}
			}
        }
        
		return usedFieldRefs;
	}

    @Override
	protected void merge(Unit succUnit, HashMap<Value,Integer> inMap1, HashMap<Value,Integer> inMap2, HashMap<Value,Integer> outMap)
    {
        for (Value l : localsAndFieldRefs) {
            Integer i1 = inMap1.get(l), i2 = inMap2.get(l);
            if (i1 == null)
            	outMap.put(l, i2);
            else if (i2 == null)
            	outMap.put(l, i1);
            else if (i1.equals(i2)) 
                outMap.put(l, i1);
            else {
                /* Merging two different values is tricky...
                 * A naive approach would be to assign UNKNOWN. However, that would lead to imprecision in the following case:
                 * 
                 * x = null;
                 * if(p) x = new X();
                 * y = x;
                 * z = x;
                 *
                 * Even though it is obvious that after this block y and z are aliased, both would be UNKNOWN :-(
                 * Hence, when merging the numbers for the two branches (null, new X()), we assign a value number that is unique
                 * to that merge location. Consequently, both y and z is assigned that same number!
                 * In the following it is important that we use an IdentityHashSet because we want the number to be unique to the
                 * location. Using a normal HashSet would make it unique to the contents.
                 * (Eric)  
                 */
            	
            	//retrieve the unique number for l at the merge point succUnit
            	//if there is no such number yet, generate one
            	//then assign the number to l in the outMap
                Map<Value, Integer> valueToNumber = mergePointToValueToNumber.get(succUnit);
                Integer number = null;
                if(valueToNumber==null) {
                	valueToNumber = new HashMap<Value, Integer>();
                	mergePointToValueToNumber.put(succUnit, valueToNumber);
                }
                else
                	number = valueToNumber.get(l);
                
                if(number==null) {
                	number = nextNumber++;
                	valueToNumber.put(l, number);
                }
                outMap.put(l, number);
            }
        }
    }
    
	@Override
    protected void flowThrough(HashMap<Value,Integer> in, Unit u, HashMap<Value,Integer> out) {
    	Stmt s = (Stmt)u;
        out.clear();

        out.putAll(in);

        if (s instanceof DefinitionStmt) {
            DefinitionStmt ds = (DefinitionStmt) s;
            Value lhs = ds.getLeftOp();
            Value rhs = ds.getRightOp();

            if (rhs instanceof CastExpr) {
            	//un-box casted value
				CastExpr castExpr = (CastExpr) rhs;
            	rhs = castExpr.getOp();
            }
            
            if ((lhs instanceof Local
            		|| (lhs instanceof FieldRef && this.localsAndFieldRefs.contains(new EquivalentValue(lhs))))
            	&& lhs.getType() instanceof RefLikeType) {
                if (rhs instanceof Local) {
                	//local-assignment - must be aliased...
                	Integer val = in.get(rhs);
                	if (val != null)
                		out.put(lhs, val);
                } else if(rhs instanceof ThisRef) {
                	//ThisRef can never change; assign unique number
                	out.put(lhs, thisRefNumber());
                } else if(rhs instanceof ParameterRef) {
                	//ParameterRef can never change; assign unique number
                	out.put(lhs, parameterRefNumber((ParameterRef) rhs));
                } else {
                	//assign number for expression
                    out.put(lhs, numberOfRhs(rhs));
                } 
            }
        } else {
        	//which other kind of statement has def-boxes? hopefully none...
        	assert s.getDefBoxes().isEmpty();
        }
    }

    private Integer numberOfRhs(Value rhs) {
   		EquivalentValue equivValue = new EquivalentValue(rhs);
   		if(localsAndFieldRefs.contains(equivValue)){
   			rhs = equivValue;
   		}
        Integer num = rhsToNumber.get(rhs);
        if(num==null) {
            num = nextNumber++;
            rhsToNumber.put(rhs, num);
        }        
        return num;
    }

    public static int thisRefNumber() {
    	//unique number for ThisRef (must be <1)
		return 0;
	}

    public static int parameterRefNumber(ParameterRef r) {
    	//unique number for ParameterRef[i] (must be <0)
		return -1 - r.getIndex();
	}

    @Override
	protected void copy(HashMap<Value,Integer> sourceMap, HashMap<Value,Integer> destMap)
    {
        destMap.clear();
        destMap.putAll(sourceMap);
    }

    /** Initial most conservative value: We leave it away to save memory, implicitly UNKNOWN. */
    @Override
    protected HashMap<Value,Integer> entryInitialFlow()
    {
    	return new HashMap<Value,Integer>();
    }

    /** Initial bottom value: objects have no definitions. */
    @Override
    protected HashMap<Value,Integer> newInitialFlow()
    {
    	return new HashMap<Value,Integer>();
    }
    
    /**
     * Returns a string (natural number) representation of the instance key associated with l
     * at statement s or <code>null</code> if there is no such key associated.
     * @param l any local of the associated method
     * @param s the statement at which to check
     */
    public String instanceKeyString(Local l, Stmt s) {
        Object ln = getFlowBefore(s).get(l);
        if(ln==null)
        	return null;
        return ln.toString();
    }
    
    /**
     * Returns true if this analysis has any information about local l
     * at statement s.
     * In particular, it is safe to pass in locals/statements that are not
     * even part of the right method. In those cases <code>false</code>
     * will be returned.
     * Permits s to be <code>null</code>, in which case <code>false</code> will be returned.
     */
    public boolean hasInfoOn(Local l, Stmt s) {
    	HashMap<Value,Integer> flowBefore = getFlowBefore(s);
    	return flowBefore !=null;
    }
    
    /**
     * @return true if values of l1 (at s1) and l2 (at s2) have the
     * exact same object IDs, i.e. at statement s1 the variable l1 must point to the same object
     * as l2 at s2.
     */
    public boolean mustAlias(Local l1, Stmt s1, Local l2, Stmt s2) {
        Object l1n = getFlowBefore(s1).get(l1);
        Object l2n = getFlowBefore(s2).get(l2);

        if (l1n == null || l2n == null)
            return false;

        return l1n == l2n;
    }

	@Override
	protected void merge(HashMap<Value, Integer> in1,
			HashMap<Value, Integer> in2, HashMap<Value, Integer> out) {
		// Copy over in1. This will be the baseline
		out.putAll(in1);
		
		// Merge in in2. Make sure that we do not have ambiguous values.
		for (Value val : in2.keySet()) {
			Integer i1 = in1.get(val);
			Integer i2 = in2.get(val);
			if (i2.equals(i1))
				out.put(val, i2);
			else
				throw new RuntimeException("Merge of different IDs not supported");
		}
	}
}
