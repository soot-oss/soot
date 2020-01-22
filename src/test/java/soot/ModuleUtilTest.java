package soot;

import org.junit.Test;
import org.junit.Assert;


/**
 * Tests traversing the module graph.
 *
 * @author Andreas Dann
 */

public class ModuleUtilTest {


    @Test
    public void ownPackage() {
        G.reset();
        ModuleUtil moduleUtil = ModuleUtil.v();
        String foundModule = moduleUtil.findModuleThatExports("de.upb", "moduleA");
        Assert.assertEquals("moduleA", foundModule);
    }

    @Test
    public void simpleExport() {
        G.reset();
        ModuleScene moduleScene = ModuleScene.v();

        SootModuleInfo moduleA = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleA");
        moduleA.addExportedPackage("de.upb");
        moduleScene.addClassSilent(moduleA);

        SootModuleInfo moduleB = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleB");
        moduleB.getRequiredModules().put(moduleA, Modifier.REQUIRES_STATIC);
        moduleScene.addClassSilent(moduleB);

        ModuleUtil moduleUtil = ModuleUtil.v();
        String foundModule = moduleUtil.findModuleThatExports("de.upb.A", "moduleB");
        Assert.assertEquals("moduleA", foundModule);

    }


    @Test
    public void simpleRequiresTransitiveExport() {
        G.reset();
        ModuleScene moduleScene = ModuleScene.v();

        SootModuleInfo moduleA = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleA");
        moduleA.addExportedPackage("de.upb");
        moduleScene.addClassSilent(moduleA);

        SootModuleInfo moduleB = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleB");
        moduleB.getRequiredModules().put(moduleA, Modifier.REQUIRES_TRANSITIVE);
        moduleScene.addClassSilent(moduleB);


        SootModuleInfo moduleC = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleC");
        moduleC.getRequiredModules().put(moduleB, Modifier.REQUIRES_STATIC);
        moduleScene.addClassSilent(moduleC);

        ModuleUtil moduleUtil = ModuleUtil.v();
        String foundModule = moduleUtil.findModuleThatExports("de.upb.A", "moduleC");
        Assert.assertEquals("moduleA", foundModule);

    }


    @Test
    public void TwoLevelRequiresTransitiveExport() {
        G.reset();
        ModuleScene moduleScene = ModuleScene.v();

        SootModuleInfo moduleA = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleA");
        moduleA.addExportedPackage("de.upb");
        moduleScene.addClassSilent(moduleA);

        SootModuleInfo moduleB = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleB");
        moduleB.getRequiredModules().put(moduleA, Modifier.REQUIRES_TRANSITIVE);
        moduleScene.addClassSilent(moduleB);


        SootModuleInfo moduleC = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleC");
        moduleC.getRequiredModules().put(moduleB, Modifier.REQUIRES_TRANSITIVE);
        moduleScene.addClassSilent(moduleC);


        SootModuleInfo moduleD = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleD");
        moduleD.getRequiredModules().put(moduleC, Modifier.REQUIRES_STATIC);
        moduleScene.addClassSilent(moduleD);

        ModuleUtil moduleUtil = ModuleUtil.v();
        String foundModule = moduleUtil.findModuleThatExports("de.upb.A", "moduleD");
        // output should be D, because module C, does NOT REQUIERS TRANSITIVE module B
        Assert.assertEquals("moduleA", foundModule);

    }

    //the test should fail, as the requires transitive relations are not set in the module graph
    @Test
    public void TwoLevelRequiresTransitiveExportFailing() {
        G.reset();
        ModuleScene moduleScene = ModuleScene.v();

        SootModuleInfo moduleA = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleA");
        moduleA.addExportedPackage("de.upb");
        moduleScene.addClassSilent(moduleA);

        SootModuleInfo moduleB = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleB");
        moduleB.getRequiredModules().put(moduleA, Modifier.REQUIRES_TRANSITIVE);
        moduleScene.addClassSilent(moduleB);


        SootModuleInfo moduleC = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleC");
        moduleC.getRequiredModules().put(moduleB, Modifier.REQUIRES_STATIC);
        moduleScene.addClassSilent(moduleC);


        SootModuleInfo moduleD = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleD");
        moduleD.getRequiredModules().put(moduleC, Modifier.REQUIRES_STATIC);
        moduleScene.addClassSilent(moduleD);

        ModuleUtil moduleUtil = ModuleUtil.v();
        String foundModule = moduleUtil.findModuleThatExports("de.upb.A", "moduleD");
        // output should be D, because module C, does NOT REQUIERS TRANSITIVE module B
        Assert.assertEquals("moduleD", foundModule);

    }
}
