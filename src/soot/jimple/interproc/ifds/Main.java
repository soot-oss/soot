package soot.jimple.interproc.ifds;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;
import soot.jimple.interproc.ifds.flowfunc.FlowFunctions;
import soot.jimple.interproc.ifds.flowfunc.Gen;
import soot.jimple.interproc.ifds.flowfunc.Identity;
import soot.jimple.interproc.ifds.flowfunc.SimpleFlowFunction;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
				Collection<Local> universe = new HashSet<Local>();
				
				for(SootMethod m: Scene.v().getMainClass().getMethods()) {
					if(m.hasActiveBody())
						universe.addAll(m.getActiveBody().getLocals());					
				}
				final FixedUniverse<Local> allLocals = new FixedUniverse<Local>(universe);
				
				for(SootMethod m: Scene.v().getMainClass().getMethods()) {
					if(m.hasActiveBody())
						System.err.println(m.getActiveBody());
				}

				
				Map<SootMethod, Set<Local>> initialSeeds = new HashMap<SootMethod, Set<Local>>();
				initialSeeds.put(Scene.v().getMainMethod(), Collections.singleton(Scene.v().getMainMethod().getActiveBody().getLocals().getFirst()));
				
				TabulationSolver<Unit, Local> solver = new TabulationSolver<Unit,Local>(
					new DefaultInterproceduralCFG(),
					allLocals,
					new FlowFunctions<Unit>() {

						public SimpleFlowFunction getNormalFlowFunction(Unit src, Unit dest) {
							if(src instanceof AssignStmt) {
								AssignStmt assignStmt = (AssignStmt) src;
								Value right = assignStmt.getRightOp();
								if(right instanceof Local) {
									Local rightLocal = (Local) right;
									final int rightIndex = allLocals.indexOf(rightLocal);
									final int leftIndex = allLocals.indexOf((Local)assignStmt.getLeftOp());
									return new SimpleFlowFunction() {
										
										public Set<Integer> computeTargets(int source) {
											if(source==rightIndex) {
												Set<Integer> res = new HashSet<Integer>();
												res.add(source);
												res.add(leftIndex);
												return res;
											}
											return Collections.singleton(source);
										}
										
										public Set<Integer> computeSources(int target) {
											if(target==rightIndex || target==leftIndex) {
												return Collections.singleton(rightIndex);
											} 
											return Collections.singleton(target);
										}
									};
								}
							}
							return Identity.v();
						}

						public SimpleFlowFunction getCallFlowFunction(Unit src, Unit dest) {
							return Identity.v();
						}

						public SimpleFlowFunction getReturnFlowFunction() {
							return Identity.v();
						}

						public SimpleFlowFunction getCallToReturnFlowFunction(Unit call, Unit returnSite) {
							return Identity.v();
						}
					}, initialSeeds
				);	
				solver.solve();
			}
		}));
		
		soot.Main.main(args);
	}

}
