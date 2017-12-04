package soot.java9;

import com.google.common.base.Optional;
import com.google.common.io.Files;
import org.junit.Test;
import polyglot.ast.Return;
import soot.*;
import soot.jimple.*;
import soot.options.Options;

import java.io.File;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * Created by adann on 24.11.17.
 */
public class LoadingTest {
    /**
     * load a JDK9 class by naming its module
     */
    @Test
    public void testLoadingJava9Class() {
        G.reset();
        File tempDir = Files.createTempDir();
        Options.v().set_soot_modulepath(tempDir.getAbsolutePath());
        Options.v().set_prepend_classpath(true);
        Scene.v().loadBasicClasses();
        SootClass klass = SootModuleResolver.v().resolveClass("java.lang.invoke.VarHandle", SootClass.BODIES, Optional.of("java.base"));

        assertTrue(klass.getName().equals("java.lang.invoke.VarHandle"));
        assertTrue(klass.moduleName.equals("java.base"));

    }


    /**
     * load a JDK9 class using the CI
     */
    @Test
    public void testLoadingJava9ClassFromCI() {
        G.reset();
        File tempDir = Files.createTempDir();

        Main.main(new String[]{"-soot-modulepath", tempDir.getAbsolutePath(), "-pp", "-src-prec", "only-class",
                "-allow-phantom-refs", "java.lang.invoke.VarHandle"});

        SootClass klass = Scene.v().getSootClass("java.lang.invoke.VarHandle");
        assertTrue(klass.getName().equals("java.lang.invoke.VarHandle"));
        assertTrue(klass.moduleName.equals("java.base"));

    }
}
