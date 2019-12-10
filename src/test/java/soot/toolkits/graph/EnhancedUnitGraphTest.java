package soot.toolkits.graph;
import org.junit.BeforeClass;
import org.junit.Test;
import soot.*;
import soot.options.Options;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EnhancedUnitGraphTest {
    private static EnhancedUnitGraphTestUtility testUtility;

    private static String TARGET_CLASS = "soot.toolkits.graph.targets.TestException";
    @BeforeClass
    public static void setUp() throws IOException {
        G.reset();
        List<String> processDir = new ArrayList<>();
        File f = new File("./target/test-classes");
        if (f.exists()) {
            processDir.add(f.getCanonicalPath());
        }
        Options.v().set_process_dir(processDir);

        Options.v().set_src_prec(Options.src_prec_only_class);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_none);
        Scene.v().addBasicClass(TARGET_CLASS);
        Scene.v().loadNecessaryClasses();
        Options.v().set_prepend_classpath(true);
        Scene.v().forceResolve("soot.toolkits.graph.targets.TestException", SootClass.BODIES);
        testUtility = new EnhancedUnitGraphTestUtility();
        PackManager.v().getPack("jtp")
                .add(new Transform("jtp.TestEnhancedGraphUtility", testUtility));
        PackManager.v().runPacks();
    }

    @Test
    public void exceptionIsReachable(){

        EnhancedUnitGraph unitGraph = testUtility.getUnitGraph();
        UnitPatchingChain allUnits = unitGraph.body.getUnits();

        int targetUnitsFound = 0;
        for(Unit u : allUnits){
            if(u.toString().contains("@caughtexception")){
                assert(unitGraph.unitToPreds.get(u).size() > 0);
                ++targetUnitsFound;
            }
        }
        assert(targetUnitsFound == 2);
    }
}
