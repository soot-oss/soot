package soot.jimple.interproc.ifds;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import soot.jimple.AssignStmt;
import soot.jimple.interproc.ifds.flowfunc.FlowFunctions;
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
					new FlowFunctions<Unit,Local>() {

						public SimpleFlowFunction<Local> getNormalFlowFunction(Unit src, Unit dest) {
							if(src instanceof AssignStmt) {
								AssignStmt assignStmt = (AssignStmt) src;
								Value right = assignStmt.getRightOp();
								if(right instanceof Local) {
									final Local rightLocal = (Local) right;
									final Local leftLocal = (Local) assignStmt.getLeftOp();
									return new SimpleFlowFunction<Local>() {
										
										public Set<Local> computeTargets(@Nullable Local source) {
											if(source==null) return Collections.singleton(null);
											if(source.equals(rightLocal)) {
												Set<Local> res = new HashSet<Local>();
												res.add(source);
												res.add(leftLocal);
												return res;
											}
											return Collections.singleton(source);
										}
										
										public Set<Local> computeSources(@Nullable Local target) {
											if(target==null) return Collections.singleton(null);
											if(target.equals(rightLocal) || target.equals(leftLocal)) {
												return Collections.singleton(rightLocal);
											} 
											return Collections.singleton(target);
										}
									};
								}
							}
							return Identity.v();
						}

						public SimpleFlowFunction<Local> getCallFlowFunction(Unit src, Unit dest) {
							return Identity.v();
						}

						public SimpleFlowFunction<Local> getReturnFlowFunction() {
							return Identity.v();
						}

						public SimpleFlowFunction<Local> getCallToReturnFlowFunction(Unit call, Unit returnSite) {
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
