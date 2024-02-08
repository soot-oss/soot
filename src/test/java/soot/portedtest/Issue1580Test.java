package soot.portedtest;

import org.junit.Test;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.util.Chain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static soot.portedtest.LoadResource.loadClasses;

public class Issue1580Test {
    // port from https://github.com/soot-oss/SootUp/pull/405
    @Test
    public void test() {
        loadClasses("src", "test", "resources", "ported", "Issue1580", "jpush-android_v3.0.5.jar");
        Chain<SootClass> classes = Scene.v().getApplicationClasses();
        assertEquals(91, classes.size());
        String classSignature = "cn.jpush.android.data.f";
        assertTrue(Scene.v().containsClass(classSignature));
        SootClass clazz = Scene.v().forceResolve(classSignature, SootClass.BODIES);
        clazz.getMethods().forEach(SootMethod::retrieveActiveBody);
        clazz.getMethods().forEach (SootMethod::getActiveBody) ;
    }
}
