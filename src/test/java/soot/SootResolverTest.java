package soot;

import org.junit.Assert;
import org.junit.Test;
import soot.options.Options;

import java.io.File;

/**
 * Created by canliture on 2021/6/28 <br/>
 *
 * A minimal Test for evaluating the bug in the Method {@link soot.Scene#defaultJavaClassPath}. <br/>
 *
 * When {@link soot.SootResolver} and {@link soot.SourceLocator#getClassSource} resolve classes,
 * they will get default java classPath by call `Scene.v().getSootClassPath()`. <br/>
 *
 * <b>Test under Java 8 environment: </b> <br/>
 *
 * Before fixed the bug, we will get default java class path when <br/>
 * - we set <pre>Options.v().set_whole_program(true);</pre>: `path/to/rt.jar;path/to/jce.jar` <br/>
 * - we set <pre>Options.v().set_whole_shimple(true);</pre>: `path/to/rt.jar` <br/>
 *
 * After fixed the bug, we will get default java class path when <br/>
 * - we set <pre>Options.v().set_whole_program(true);</pre>: `path/to/rt.jar;path/to/jce.jar` <br/>
 * - we set <pre>Options.v().set_whole_shimple(true);</pre>: `path/to/rt.jar;path/to/jce.jar` <br/>
 *
 * @author canliture
 */
public class SootResolverTest {

    @Test
    public void test1() {
        G.reset();

        // setting
        Options.v().set_whole_program(true);

        // No throw. ^_^
        Scene.v().loadNecessaryClasses();

        // assert default class path
        String classPath = Scene.v().getSootClassPath();
        String[] paths = classPath.split(File.pathSeparator);
        Assert.assertEquals(2, paths.length);
    }

    @Test
    public void test2() {
        G.reset();

        // setting
        Options.v().set_whole_shimple(true);

        // throw java.lang.AssertionError !!!
        Scene.v().loadNecessaryClasses();

        // assert default class path
        String classPath = Scene.v().getSootClassPath();
        String[] paths = classPath.split(File.pathSeparator);
        Assert.assertEquals(2, paths.length);
    }
}
