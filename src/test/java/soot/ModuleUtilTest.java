package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import org.junit.Test;
import org.junit.Assert;


public class ModuleUtilTest {


    @Test
    public void ownPackage() {
        G.reset();
        ModuleUtil moduleUtil = ModuleUtil.v();
        ModuleScene moduleScene = ModuleScene.v();

        SootModuleInfo moduleA = new SootModuleInfo(SootModuleInfo.MODULE_INFO, "moduleA");
        moduleA.addExportedPackage("de.upb");
        moduleScene.addClassSilent(moduleA);


        String foundModule = moduleUtil.declaringModule("de.upb", "moduleA");
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
        String foundModule = moduleUtil.declaringModule("de.upb.A", "moduleB");
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
        String foundModule = moduleUtil.declaringModule("de.upb.A", "moduleC");
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
        String foundModule = moduleUtil.declaringModule("de.upb.A", "moduleD");
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
        String foundModule = moduleUtil.declaringModule("de.upb.A", "moduleD");
        // output should be D, because module C, does NOT REQUIERS TRANSITIVE module B
        Assert.assertEquals("moduleD", foundModule);

    }
}
