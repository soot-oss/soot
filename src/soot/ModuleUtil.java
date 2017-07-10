package soot;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import soot.options.Options;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ralle on 18.08.16.
 */

/**
 * A utility class for dealing with java 9 modules and module dependencies
 */
public final class ModuleUtil {

    public ModuleUtil(Singletons.Global g) {
        modulePackageCache =
                modulePackageCache = CacheBuilder.newBuilder().initialCapacity(60).maximumSize(500).concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();
    }

    public static ModuleUtil v() {

        return G.v().soot_ModuleUtil();
    }

    private Cache<String, String> modulePackageCache;


    /**
     * Finds the module that exports the given class to the given module
     *
     * @param className    the requested class
     * @param toModuleName the module from which the request is made
     * @return the module's name that exports the class to the given module
     */
    public  String findModuleThatExports(String className, String toModuleName) {

        if (className.equalsIgnoreCase(SootModuleInfo.MODULE_INFO)) {
            return toModuleName;
        }
        SootModuleInfo modInfo = (SootModuleInfo) SootModuleResolver.v().resolveClass(SootModuleInfo.MODULE_INFO, SootClass.BODIES, Optional.fromNullable(toModuleName));

        String packageName = getPackageName(className);

        if (modInfo == null) {
            return null;
        }
        String moduleName = modulePackageCache.getIfPresent(modInfo.getModuleName() + "/" + packageName);
        if (moduleName==null) {
            return moduleName;
        }

        if (modInfo.exportsPackage(packageName, toModuleName)) {
            return modInfo.getModuleName();
        }

        if (modInfo.isAutomaticModule()) {
            //shortcut, an automatic module is allowed to access any other class
            if (ModuleScene.v().containsClass(className)) {
                return ModuleScene.v().getSootClass(className).getModuleInformation().getModuleName();
            }
        }


        for (SootModuleInfo modInf : modInfo.retrieveRequiredModules().keySet()) {
            if (modInf.exportsPackage(packageName, toModuleName)) {
                modulePackageCache.put(modInfo.getModuleName() + "/" + packageName, modInf.getModuleName());
                return modInf.getModuleName();
            } else {
                //check if exported packages is "requires public"
                for (Map.Entry<SootModuleInfo, Integer> entry : modInf.retrieveRequiredModules().entrySet()) {
                    if ((entry.getValue() & Modifier.REQUIRES_TRANSITIVE) != 0) //check if module is reexported via "requires public"
                    {

                        if (entry.getKey().exportsPackage(packageName, toModuleName)) {
                            modulePackageCache.put(modInfo.getModuleName() + "/" + packageName, entry.getKey().getModuleName());
                            return entry.getKey().getModuleName();
                        }

                    }

                }


            }

        }
        //if the class is not exported by any package, it has to internal to this module
        return toModuleName;
    }

    /**
     * The returns the package name of a full qualified class name
     *
     * @param className a full qualified className
     * @return the package name
     */
    private static String getPackageName(String className) {
        String packageName = "";
        int index = className.lastIndexOf('.');
        if (index > 0) {
            packageName = className.substring(0, index);
        }
        return packageName;
    }

    /**
     * Check if Soot is run with module mode enables
     *
     * @return true, if module mode is used
     */
    public static boolean module_mode() {
        return !Options.v().soot_modulepath().isEmpty();
    }

    /* In Soot are a hard coded class names as string contanstants that are now contained in the java.base module, this list serves as a lookup for these string constant    */
    private static List<String> packagesJavaBaseModule = Arrays.asList("java.lang", "java.io", "java.math", "java.net", "java.nio", "java.security", "java.text", "java.time", "java.util");

    /**
     * Wrapper class for backward compatibility with existing soot code
     * In existing soot code classes are resolved based on their name without specifying a module
     * to avoid changing all occurrences of String constants in Soot this classes deals with these String constants
     */
    public static class ModuleClassNameWrapper {
        private String className;
        private String moduleName;


        public ModuleClassNameWrapper(String className) {
            String refinedClassName = className;
            String refinedModuleName = null;
            if (className.contains(":")) {
                String split[] = className.split(":");
                refinedModuleName = split[0];
                refinedClassName = split[1];
            } else {
                for (String packageName : packagesJavaBaseModule) {
                    if (className.startsWith(packageName)) {
                        refinedModuleName = "java.base";
                        break;
                    }
                }
            }
            this.className = refinedClassName;
            this.moduleName = refinedModuleName;
        }

        public String getClassName() {
            return this.className;
        }

        public String getModuleName() {
            return this.moduleName;
        }

        public Optional<String> getModuleNameOptional() {
            return Optional.fromNullable(this.moduleName);
        }

    }


}
