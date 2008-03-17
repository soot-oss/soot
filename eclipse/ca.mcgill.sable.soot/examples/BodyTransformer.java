import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PackManager;
import soot.Transform;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class MyMain {

	public static void main(String[] args) {
		PackManager.v().getPack("jtp").add(
				new Transform("jtp.myTransform", new BodyTransformer() {

					protected void internalTransform(Body body, String phase, Map options) {
						new MyAnalysis(new ExceptionalUnitGraph(body));
						// use G.v().out instead of System.out so that Soot can
						// redirect this output to the Eclipse console
						G.v().out.println(body.getMethod());
					}
					
				}));
		
		soot.Main.main(args);
	}

	public static class MyAnalysis /*extends ForwardFlowAnalysis */ {

		public MyAnalysis(ExceptionalUnitGraph exceptionalUnitGraph) {
			//doAnalysis();
		}

	}

}