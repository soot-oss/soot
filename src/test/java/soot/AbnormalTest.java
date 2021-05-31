package soot;

import org.junit.Test;
import soot.options.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 * Tests abnormal cases.
 *
 * @author Linghui Luo
 */

public class AbnormalTest {

    public void setup() {
        Path cp = Paths.get("src", "test", "resources", "AbnormalClass");
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_process_dir(Collections.singletonList(cp.toFile().getAbsolutePath()));
        Options.v().set_src_prec(Options.src_prec_class);
    }

    @Test
    public void testMethodWithNoInstruction() {
        setup();
        Options.v().set_output_format(Options.output_format_jimple);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_grimp);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_baf);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_dava);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_shimp);
        runTest();
        setup();
        Options.v().set_output_format(Options.output_format_class);
        runTest();
    }

    public void runTest() {
        Scene.v().loadNecessaryClasses();
        PackManager.v().runBodyPacks();
        // The method test in class E has no opcode instruction at all.
        // Such method can be created by a bytecode editor.
        SootMethod method = Scene.v().getSootClass("E").getMethodByName("test");
        assertTrue(!method.hasActiveBody());
    }
}