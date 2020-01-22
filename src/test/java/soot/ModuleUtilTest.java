package soot;

import org.junit.Test;
import org.junit.Assert;

public class ModuleUtilTest {


    public void buildModuleScene() {


    }


    @Test
    public void ownPackage() {
        G.reset();
        ModuleUtil moduleUtil = ModuleUtil.v();
        String foundModule = moduleUtil.findModuleThatExports("de.upb", "moduleA");
        Assert.assertEquals("moduleA", foundModule);
    }

    @Test
    public void simpleExport(){
        G.reset();
        ModuleScene moduleScene = ModuleScene.v();

        SootModuleInfo moduleA = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleA");
        moduleA.addExportedPackage("de.upb");
        moduleScene.addClassSilent(moduleA);

        SootModuleInfo moduleB = new SootModuleInfo(SootModuleInfo.MODULE_INFO,"moduleB");
        moduleB.getRequiredModules().put(moduleA,Modifier.REQUIRES_STATIC);
        moduleScene.addClassSilent(moduleB);

        ModuleUtil moduleUtil = ModuleUtil.v();
        String foundModule = moduleUtil.findModuleThatExports("de.upb.A", "moduleB");
        Assert.assertEquals("moduleA", foundModule);

    }


    @Test
    public void simpleRequiresTransitiveExport(){
        G.reset();
        ModuleScene moduleScene = ModuleScene.v();

        SootModuleInfo moduleA = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleA");
        moduleA.addExportedPackage("de.upb");
        moduleScene.addClassSilent(moduleA);

        SootModuleInfo moduleB = new SootModuleInfo(SootModuleInfo.MODULE_INFO,"moduleB");
        moduleB.getRequiredModules().put(moduleA,Modifier.REQUIRES_TRANSITIVE);
        moduleScene.addClassSilent(moduleB);


        SootModuleInfo moduleC = new SootModuleInfo(SootModuleInfo.MODULE_INFO,"moduleC");
        moduleC.getRequiredModules().put(moduleB,Modifier.REQUIRES_STATIC);
        moduleScene.addClassSilent(moduleC);

        ModuleUtil moduleUtil = ModuleUtil.v();
        String foundModule = moduleUtil.findModuleThatExports("de.upb.A", "moduleC");
        Assert.assertEquals("moduleA", foundModule);

    }
}
