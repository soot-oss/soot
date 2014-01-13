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
import heros.flowfunc.Kill;
import heros.flowfunc.KillAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.NullType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.ide.DefaultJimpleIFDSTabulationProblem;
import soot.util.Chain;

public class IFDSUninitializedVariables extends DefaultJimpleIFDSTabulationProblem<Local,InterproceduralCFG<Unit, SootMethod>> {

	public IFDSUninitializedVariables(InterproceduralCFG<Unit, SootMethod> icfg) {
		super(icfg);		
	}

	@Override
	public FlowFunctions<Unit, Local, SootMethod> createFlowFunctionsFactory() {
		return new FlowFunctions<Unit, Local, SootMethod>() {

			@Override
			public FlowFunction<Local> getNormalFlowFunction(Unit curr, Unit succ) {
				final SootMethod m = interproceduralCFG().getMethodOf(curr);
				if(Scene.v().getEntryPoints().contains(m) && interproceduralCFG().isStartPoint(curr)) {
					return new FlowFunction<Local>() {
						
						@Override
						public Set<Local> computeTargets(Local source) {
							if (source == zeroValue()) {
								Set<Local> res = new LinkedHashSet<Local>();
								res.addAll(m.getActiveBody().getLocals());
								for(int i=0;i<m.getParameterCount();i++) 
									res.remove(m.getActiveBody().getParameterLocal(i));
								return res;
							}
							return Collections.emptySet();
						}
					};
				}
				
				if (curr instanceof DefinitionStmt) {
					final DefinitionStmt definition = (DefinitionStmt) curr;
					final Value leftOp = definition.getLeftOp();
					if(leftOp instanceof Local) {
						final Local leftOpLocal = (Local) leftOp;
						return new FlowFunction<Local>() {

							@Override
							public Set<Local> computeTargets(final Local source) {
								List<ValueBox> useBoxes = definition.getUseBoxes();
								for (ValueBox valueBox : useBoxes) {
									if (valueBox.getValue().equivTo(source)) {
										LinkedHashSet<Local> res = new LinkedHashSet<Local>();
										res.add(source);
										res.add(leftOpLocal); 
										return res;
									}
								}

								if (leftOp.equivTo(source))
									return Collections.emptySet();

								return Collections.singleton(source);
							}

						};
					}
				}

				return Identity.v();
			}

			@Override
			public FlowFunction<Local> getCallFlowFunction(Unit callStmt, final SootMethod destinationMethod) {
				Stmt stmt = (Stmt) callStmt;
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				final List<Value> args = invokeExpr.getArgs();

				final List<Local> localArguments = new ArrayList<Local>();
				for (Value value : args)
					if (value instanceof Local)
						localArguments.add((Local) value);

				return new FlowFunction<Local>() {

					@Override
					public Set<Local> computeTargets(final Local source) {
						// Do not map parameters for <clinit> edges
						if (destinationMethod.getName().equals("<clinit>"))
							return Collections.emptySet();

						for (Local localArgument : localArguments) {
							if (source.equivTo(localArgument)) {
								return Collections.<Local>singleton(destinationMethod.getActiveBody().getParameterLocal(args.indexOf(localArgument)));
							}
						}

						if (source == zeroValue()) {
							//gen all locals that are not parameter locals 
							Chain<Local> locals = destinationMethod.getActiveBody().getLocals();
							LinkedHashSet<Local> uninitializedLocals = new LinkedHashSet<Local>(locals);
							for(int i=0;i<destinationMethod.getParameterCount();i++) {								
								uninitializedLocals.remove(destinationMethod.getActiveBody().getParameterLocal(i));
							}
							return uninitializedLocals;
						}

						return Collections.emptySet();
					}

				};
			}

			@Override
			public FlowFunction<Local> getReturnFlowFunction(final Unit callSite, SootMethod calleeMethod,
					final Unit exitStmt, Unit returnSite) {
				if (callSite instanceof DefinitionStmt) {
					final DefinitionStmt definition = (DefinitionStmt) callSite;
					if(definition.getLeftOp() instanceof Local) {
						final Local leftOpLocal = (Local) definition.getLeftOp();				
						if (exitStmt instanceof ReturnStmt) {
							final ReturnStmt returnStmt = (ReturnStmt) exitStmt;
							return new FlowFunction<Local>() {
		
								@Override
								public Set<Local> computeTargets(Local source) {
									if (returnStmt.getOp().equivTo(source))
										return Collections.singleton(leftOpLocal);
									return Collections.emptySet();
								}
		
							};
						} else if (exitStmt instanceof ThrowStmt) {
							//if we throw an exception, LHS of call is undefined
							return new FlowFunction<Local>() {
		
								@Override
								public Set<Local> computeTargets(final Local source) {
									if (source == zeroValue())
										return Collections.singleton(leftOpLocal);
									else
										return Collections.emptySet();
								}
								
							};
						}
					}
				}
				
				return KillAll.v();
			}

			@Override
			public FlowFunction<Local> getCallToReturnFlowFunction(Unit callSite, Unit returnSite) {
				if (callSite instanceof DefinitionStmt) {
					DefinitionStmt definition = (DefinitionStmt) callSite;
					if(definition.getLeftOp() instanceof Local) {
						final Local leftOpLocal = (Local) definition.getLeftOp();				
						return new Kill<Local>(leftOpLocal);
					}
				}
				return Identity.v();
			}
		};
	}

	public Map<Unit, Set<Local>> initialSeeds() {
		return DefaultSeeds.make(Collections.singleton(Scene.v().getMainMethod().getActiveBody().getUnits().getFirst()), zeroValue());
	}

	@Override
	public Local createZeroValue() {
		return new JimpleLocal("<<zero>>", NullType.v());
	}

}
