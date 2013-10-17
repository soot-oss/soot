/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2013 Eric Bodden and others
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
package soot.jimple.toolkits.ide.exampleproblems;

import heros.DefaultSeeds;
import heros.FlowFunction;
import heros.FlowFunctions;
import heros.InterproceduralCFG;
import heros.flowfunc.Identity;
import heros.flowfunc.KillAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.PrimType;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;
import soot.jimple.Ref;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;
import soot.toolkits.scalar.Pair;

@SuppressWarnings("serial")
public class IFDSPossibleTypes extends DefaultJimpleIFDSTabulationProblem<Pair<Value,Type>,InterproceduralCFG<Unit, SootMethod>> {

	public IFDSPossibleTypes(InterproceduralCFG<Unit,SootMethod> icfg) {
		super(icfg);
	}

	public FlowFunctions<Unit, Pair<Value,Type>, SootMethod> createFlowFunctionsFactory() {
		return new FlowFunctions<Unit,Pair<Value,Type>,SootMethod>() {

			public FlowFunction<Pair<Value,Type>> getNormalFlowFunction(Unit src, Unit dest) {
				if(src instanceof DefinitionStmt) {
					DefinitionStmt defnStmt = (DefinitionStmt) src;
					if(defnStmt.containsInvokeExpr()) return Identity.v();
					final Value right = defnStmt.getRightOp();
					final Value left = defnStmt.getLeftOp();
					//won't track primitive-typed variables
					if(right.getType() instanceof PrimType) return Identity.v();
										
					if(right instanceof Constant || right instanceof NewExpr) {
						return new FlowFunction<Pair<Value,Type>>() {
							public Set<Pair<Value, Type>> computeTargets(Pair<Value, Type> source) {
								if(source==zeroValue()) {
									Set<Pair<Value, Type>> res = new LinkedHashSet<Pair<Value,Type>>();
									res.add(new Pair<Value,Type>(left,right.getType()));
									res.add(zeroValue());
									return res;
								} else if(source.getO1() instanceof Local && source.getO1().equivTo(left)) {
									//strong update for local variables
									return Collections.emptySet();
								} else {
									return Collections.singleton(source);
								}
							}
						};
					} else if(right instanceof Ref || right instanceof Local) {
						return new FlowFunction<Pair<Value,Type>>() {
							public Set<Pair<Value, Type>> computeTargets(final Pair<Value, Type> source) {
								Value value = source.getO1();
								if(source.getO1() instanceof Local && source.getO1().equivTo(left)) {
									//strong update for local variables
									return Collections.emptySet();
								} else if(maybeSameLocation(value,right)) {
									return new LinkedHashSet<Pair<Value,Type>>() {{
										add(new Pair<Value,Type>(left,source.getO2())); 
										add(source); 
									}};
								} else {
									return Collections.singleton(source);
								}
							}

							private boolean maybeSameLocation(Value v1, Value v2) {
								if(!(v1 instanceof InstanceFieldRef && v2 instanceof InstanceFieldRef) &&
								   !(v1 instanceof ArrayRef && v2 instanceof ArrayRef)) {
									return v1.equivTo(v2);
								}
								if(v1 instanceof InstanceFieldRef && v2 instanceof InstanceFieldRef) {
									InstanceFieldRef ifr1 = (InstanceFieldRef) v1;
									InstanceFieldRef ifr2 = (InstanceFieldRef) v2;
									if(!ifr1.getField().getName().equals(ifr2.getField().getName())) return false;
									
									Local base1 = (Local) ifr1.getBase();
									Local base2 = (Local) ifr2.getBase();
									PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
									PointsToSet pts1 = pta.reachingObjects(base1);
									PointsToSet pts2 = pta.reachingObjects(base2);								
									return pts1.hasNonEmptyIntersection(pts2);
								} else { //v1 instanceof ArrayRef && v2 instanceof ArrayRef
									ArrayRef ar1 = (ArrayRef) v1;
									ArrayRef ar2 = (ArrayRef) v2;

									Local base1 = (Local) ar1.getBase();
									Local base2 = (Local) ar2.getBase();
									PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
									PointsToSet pts1 = pta.reachingObjects(base1);
									PointsToSet pts2 = pta.reachingObjects(base2);								
									return pts1.hasNonEmptyIntersection(pts2);
								}
							}
						};
					} 
				}
				return Identity.v();
			}

			public FlowFunction<Pair<Value,Type>> getCallFlowFunction(final Unit src, final SootMethod dest) {
				Stmt stmt = (Stmt) src;
				InvokeExpr ie = stmt.getInvokeExpr();
				final List<Value> callArgs = ie.getArgs();
				final List<Local> paramLocals = new ArrayList<Local>();
				for(int i=0;i<dest.getParameterCount();i++) {
					paramLocals.add(dest.getActiveBody().getParameterLocal(i));
				}				
				return new FlowFunction<Pair<Value,Type>>() {
					public Set<Pair<Value,Type>> computeTargets(Pair<Value,Type> source) {
						if (!dest.getName().equals("<clinit>")) {
							Value value = source.getO1();
							int argIndex = callArgs.indexOf(value);
							if(argIndex>-1) {
								return Collections.singleton(new Pair<Value,Type>(paramLocals.get(argIndex), source.getO2()));
							}
						}
						return Collections.emptySet();
					}
				};
			}

			public FlowFunction<Pair<Value,Type>> getReturnFlowFunction(Unit callSite, SootMethod callee, Unit exitStmt, Unit retSite) {
				if (exitStmt instanceof ReturnStmt) {								
					ReturnStmt returnStmt = (ReturnStmt) exitStmt;
					Value op = returnStmt.getOp();
					if(op instanceof Local) {
						if(callSite instanceof DefinitionStmt) {
							DefinitionStmt defnStmt = (DefinitionStmt) callSite;
							Value leftOp = defnStmt.getLeftOp();
							if(leftOp instanceof Local) {
								final Local tgtLocal = (Local) leftOp;
								final Local retLocal = (Local) op;
								return new FlowFunction<Pair<Value,Type>>() {

									public Set<Pair<Value,Type>> computeTargets(Pair<Value,Type> source) {
										if(source==retLocal)
											return Collections.singleton(new Pair<Value,Type>(tgtLocal, source.getO2()));
										return Collections.emptySet();
									}
									
								};
							}
						}
					}
				}
				return KillAll.v();
			}

			public FlowFunction<Pair<Value,Type>> getCallToReturnFlowFunction(Unit call, Unit returnSite) {
				return Identity.v();
			}
		};
	}

	public Map<Unit, Set<Pair<Value,Type>>> initialSeeds() {
		return DefaultSeeds.make(Collections.singleton(Scene.v().getMainMethod().getActiveBody().getUnits().getFirst()), zeroValue());
	}

	public Pair<Value,Type> createZeroValue() {
		return new Pair<Value, Type>(Jimple.v().newLocal("<dummy>", UnknownType.v()), UnknownType.v());
	}
}