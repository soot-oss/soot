package soot.jimple.toolkits.ide;

import heros.IFDSTabulationProblem;
import heros.InterproceduralCFG;

import java.util.Map;

import soot.PackManager;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.toolkits.ide.exampleproblems.IFDSPossibleTypes;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		PackManager.v().getPack("wjtp").add(new Transform("wjtp.ifds", new SceneTransformer() {
			protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {

				IFDSTabulationProblem<Unit,?,SootMethod,InterproceduralCFG<Unit,SootMethod>> problem = new IFDSPossibleTypes(new JimpleBasedInterproceduralCFG());
				
				@SuppressWarnings({ "rawtypes", "unchecked" })
				JimpleIFDSSolver<?,InterproceduralCFG<Unit,SootMethod>> solver = new JimpleIFDSSolver(problem);
				solver.solve();
			}
		}));
		
		soot.Main.main(args);
	}

}
