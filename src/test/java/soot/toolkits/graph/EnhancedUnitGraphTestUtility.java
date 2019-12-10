package soot.toolkits.graph;

import soot.Body;
import soot.BodyTransformer;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;

public class EnhancedUnitGraphTestUtility extends BodyTransformer {
    private EnhancedUnitGraph unitGraph = null;
    protected void internalTransform(Body body, String phase, java.util.Map<String,String> options)
    {
        String methodSig = body.getMethod().getSignature();
        if(methodSig.contains("soot.toolkits.graph.targets.TestException")
                && body.getMethod().getName().contains("main"))
            unitGraph = new EnhancedUnitGraph(body);
    }

    public EnhancedUnitGraph getUnitGraph() {
        return unitGraph;
    }
}
