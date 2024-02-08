package soot.portedtest;

import org.junit.Test;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.util.Chain;

import static org.junit.Assert.assertEquals;
import static soot.portedtest.LoadResource.loadClasses;

public class Issue1577Test {
    // port from https://github.com/soot-oss/SootUp/pull/405
    @Test
    public void test() {
        loadClasses("src", "test", "resources", "ported", "Issue1577");
        Chain<SootClass> classes = Scene.v() .getApplicationClasses();
        assertEquals(1, classes.size());
        SootClass clazz = classes.getFirst();
        clazz.getMethods().forEach(SootMethod::retrieveActiveBody);
        clazz.getMethods().forEach(SootMethod::getActiveBody) ;
    }
}
