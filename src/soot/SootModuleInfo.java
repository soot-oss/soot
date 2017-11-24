package soot;

import com.google.common.base.Optional;
import soot.dava.toolkits.base.misc.PackageNamer;
import soot.util.Chain;

import java.util.*;

/**
 * Created by ralle on 14.08.16.
 */
public class SootModuleInfo extends SootClass {

    public static final String MODULE_INFO_FILE = "module-info.class";
    public static final String MODULE_INFO = "module-info";
    private HashSet<String> modulePackages = new HashSet<>();

    private static final String EVERYONE_MODULE = "EVERYONE_MODULE";

    public boolean isAutomaticModule() {
        return isAutomaticModule;
    }

    public void setAutomaticModule(boolean automaticModule) {
        isAutomaticModule = automaticModule;
    }

    private boolean isAutomaticModule;

    private Map<SootModuleInfo, Integer> requiredModules = new HashMap<SootModuleInfo, Integer>();

    //TODO: change String to SootClassReference
    private Map<String, List<String>> exportedPackages = new HashMap<String, List<String>>();

    //TODO: change String to SootClassReference
    private Map<String, List<String>> openedPackages = new HashMap<String, List<String>>();


    public SootModuleInfo(String name, int modifiers, String moduleName) {
        super(name, modifiers, moduleName);
    }

    public SootModuleInfo(String name, String moduleName) {
        this(name, moduleName, false);
    }

    public SootModuleInfo(String name, String moduleName, boolean isAutomatic) {
        super(name, moduleName);
        this.isAutomaticModule = isAutomatic;
    }

    private Map<String, List<String>> getExportedPackages() {
        return exportedPackages;
    }

    private Map<String, List<String>> getOpenedPackages() {
        return openedPackages;
    }

    public Set<String> getPublicExportedPackages() {
        Set<String> publicExportedPackages = new HashSet<>();
        for (String packaze : modulePackages) {
            if (this.exportsPackage(packaze, EVERYONE_MODULE))
                publicExportedPackages.add(packaze);
        }
        return publicExportedPackages;
    }

    public Set<String> getPublicOpenedPackages() {
        Set<String> publicOpenedPackages = new HashSet<>();
        for (String packaze : modulePackages) {
            if (this.opensPackage(packaze, EVERYONE_MODULE))
                publicOpenedPackages.add(packaze);
        }
        return publicOpenedPackages;
    }


    public Map<SootModuleInfo, Integer> getRequiredModules() {
        return requiredModules;
    }

    public Map<SootModuleInfo, Integer> retrieveRequiredModules() {
        Map<SootModuleInfo, Integer> moduleInfos = requiredModules;

        //move into subclass
        if (this.isAutomaticModule) {
            //we can read all modules
            for (SootClass sootClass : Scene.v().getClasses()) {
                if (sootClass instanceof SootModuleInfo && sootClass.moduleName != this.moduleName) {
                    moduleInfos.put((SootModuleInfo) sootClass, Modifier.REQUIRES_STATIC);
                }
            }
        }

        for (SootModuleInfo moduleInfo : moduleInfos.keySet()) {
            SootModuleResolver.v().resolveClass(SootModuleInfo.MODULE_INFO, SootClass.BODIES, Optional.fromNullable(moduleInfo.moduleName));
        }
        return moduleInfos;
    }


    public void addExportedPackage(String packaze, String... exportedToModules) {
        String packageName = PackageNamer.v().get_FixedPackageName(packaze).replace("/", ".");
        List<String> qualifiedExports = Collections.singletonList(SootModuleInfo.EVERYONE_MODULE);
        if (exportedToModules != null) {
            qualifiedExports = Arrays.asList(exportedToModules);
        }
        exportedPackages.put(packageName, qualifiedExports);

    }


    public void addOpenedPackage(String packaze, String... openedToModules) {
        String packageName = PackageNamer.v().get_FixedPackageName(packaze).replace("/", ".");
        List<String> qualifiedOpens = Collections.singletonList(SootModuleInfo.EVERYONE_MODULE);
        if (openedToModules != null) {
            qualifiedOpens = Arrays.asList(openedToModules);
        }
        openedPackages.put(packageName, qualifiedOpens);

    }


    public String getModuleName() {
        return this.moduleName;
    }


    @Override
    public boolean isConcrete() {
        return false;
    }


    @Override
    public boolean isExportedByModule() {
        return true;
    }


    @Override
    public boolean isExportedByModule(String toModule) {
        return true;
    }


    @Override
    public boolean isOpenedByModule() {
        return true;
    }

    public boolean exportsPackagePublic(String packaze) {
        return exportsPackage(packaze, EVERYONE_MODULE);
    }


    public boolean openPackagePublic(String packaze) {
        return opensPackage(packaze, EVERYONE_MODULE);
    }

    public boolean opensPackage(String packaze, String toModule) {

        if (packaze.equalsIgnoreCase(SootModuleInfo.MODULE_INFO))
            return true;

        /// all packages are exported/open to self
        if (this.getModuleName().equals(toModule))
            return this.modulePackages.contains(packaze);

        // all packages in open and automatic modules are open
        if (this.isAutomaticModule())
            return this.modulePackages.contains(packaze);

        List<String> qualifiedOpens = this.openedPackages.get(packaze);
        if (qualifiedOpens == null) {
            return false; //if qualifiedExport is null, the package is not exported
        }

        if (qualifiedOpens.contains(EVERYONE_MODULE))
            return true;
        if (toModule != EVERYONE_MODULE && qualifiedOpens.contains(toModule))
            return true;

        return false;
    }

    public boolean exportsPackage(String packaze, String toModule) {

        if (packaze.equalsIgnoreCase(SootModuleInfo.MODULE_INFO))
            return true;

        /// all packages are exported/open to self
        if (this.getModuleName().equals(toModule))
            return this.modulePackages.contains(packaze);

        //a automatic module exports all its packages
        if (this.isAutomaticModule())
            return this.modulePackages.contains(packaze);


        List<String> qualifiedExport = this.exportedPackages.get(packaze);
        if (qualifiedExport == null) {
            return false;
        }

        if (qualifiedExport.contains(EVERYONE_MODULE))
            return true;
        if (toModule != EVERYONE_MODULE && qualifiedExport.contains(toModule))
            return true;

        return false;
    }

    public Set<SootModuleInfo> getRequiredPublicModules() {
        Set<SootModuleInfo> requiredPublic = new HashSet<>();
        //check if exported packages is "requires public"
        for (Map.Entry<SootModuleInfo, Integer> entry : this.requiredModules.entrySet()) {
            if ((entry.getValue() & Modifier.REQUIRES_TRANSITIVE) != 0) //check if module is reexported via "requires public"
            {
                requiredPublic.add(entry.getKey());
            }

        }

        return requiredPublic;
    }

    public void addModulePackage(String packageName) {
        this.modulePackages.add(packageName);
    }

    public boolean moduleContainsPackage(String packageName) {
        return this.modulePackages.contains(packageName);
    }

}
