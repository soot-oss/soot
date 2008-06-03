import soot.Body;
import soot.Main;
import soot.Pack;
import soot.PackManager;
import soot.Transform;
import soot.Unit;

import soot.tagkit.StringTag;

import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.graph.BriefUnitGraph;

public class NullnessDriver
{
    public static void main(String[] argv)
    {
        Pack jtp = soot.G.v().PackManager().getPack("jtp");
        jtp.add(new Transform("jtp.nt", new NullTransformer()));
        jtp.add(new Transform("jtp.nac", new NullnessAnalysisColorer()));

        soot.Main.main(argv);
    }
}

class NullTransformer extends soot.BodyTransformer
{
    protected void internalTransform(Body b, String phaseName, 
                                     java.util.Map options)
    {
        NullnessAnalysis na = new NullnessAnalysis(new BriefUnitGraph(b));

        java.util.Iterator uIt = b.getUnits().iterator();
        while (uIt.hasNext())
        {
            Unit u = (Unit)uIt.next();

            StringBuffer n = new StringBuffer();
            u.addTag(new StringTag("IN: "+na.getFlowBefore(u).toString()));

            if (u.fallsThrough())
            { 
                ArraySparseSet s = (ArraySparseSet)na.getFallFlowAfter(u);
                u.addTag(new StringTag("FALL: "+s.toString())); 
            }
            if (u.branches())
            { 
                ArraySparseSet t = (ArraySparseSet)na.
                    getBranchFlowAfter(u).get(0);
                u.addTag(new StringTag("BRANCH: "+t.toString())); 
            }
        }
    }
}
