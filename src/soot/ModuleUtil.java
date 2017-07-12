package soot;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import soot.options.Options;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by ralle on 18.08.16.
 */

/**
 * A utility class for dealing with java 9 modules and module dependencies
 */
public final class ModuleUtil {

    public ModuleUtil(Singletons.Global g) {
        modulePackageCache = CacheBuilder.newBuilder().initialCapacity(60).maximumSize(800).concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();
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
    public String findModuleThatExports(String className, String toModuleName) {

        if (className.equalsIgnoreCase(SootModuleInfo.MODULE_INFO)) {
            return toModuleName;
        }
        SootModuleInfo modInfo = (SootModuleInfo) SootModuleResolver.v().resolveClass(SootModuleInfo.MODULE_INFO, SootClass.BODIES, Optional.fromNullable(toModuleName));

        String packageName = getPackageName(className);

        if (modInfo == null) {
            return null;
        }
        String moduleName = modulePackageCache.getIfPresent(modInfo.getModuleName() + "/" + packageName);
        if (moduleName != null) {
            return moduleName;
        }

        if (modInfo.exportsPackage(packageName, toModuleName)) {
            return modInfo.getModuleName();
        }

        if (modInfo.isAutomaticModule()) {
            //shortcut, an automatic module is allowed to access any other class
            if (ModuleScene.v().containsClass(className)) {
                String foundModuleName = ModuleScene.v().getSootClass(className).getModuleInformation().getModuleName();
                modulePackageCache.put(modInfo.getModuleName() + "/" + packageName, foundModuleName);
                return foundModuleName;
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
    private static List<String> packagesJavaBaseModule = parseJavaBasePackage();


    private static List<String> parseJavaBasePackage() {
        List<String> packages = new ArrayList<String>();
        Path excludeFile = Paths.get("javabase.txt");
        if (!Files.exists(excludeFile)) {
            //else take the one package
            try {
                excludeFile = Paths.get(ModuleUtil.class.getResource(File.separator + "javabase.txt").toURI());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }
        //read file into stream, try-with-resources
        try (InputStream in = Files.newInputStream(excludeFile);
             BufferedReader reader =
                     new BufferedReader(new InputStreamReader(in))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                packages.add(line);
            }
        } catch (IOException x) {
            G.v().out.println("[WARN] No files for java.base packages");
        }
        return packages;
    }

    /**
     * Wrapper class for backward compatibility with existing soot code
     * In existing soot code classes are resolved based on their name without specifying a module
     * to avoid changing all occurrences of String constants in Soot this classes deals with these String constants
     */
    public static class ModuleClassNameWrapper {
        private String className;
        private String moduleName;


        //check for occurrence of full qualified class names
        private static String fullQualifiedName = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)+[a-zA-Z_$][a-zA-Z\\d_$]*";
        private static Pattern fqnClassNamePattern = Pattern.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*");

        //check for occurrence of module name
        private static String qualifiedModuleName = "([a-zA-Z_$])([a-zA-Z\\d_$\\.]*)+";
        private static Pattern moduleClassNamePattern = Pattern.compile(qualifiedModuleName + "(:)" + fullQualifiedName);


        public ModuleClassNameWrapper(String className) {

            String refinedClassName = className;
            String refinedModuleName = null;
            if (className.equals(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME)) {
                this.className = refinedClassName;
                return;
            } else if (moduleClassNamePattern.matcher(className).matches()) {
                String split[] = className.split(":");
                refinedModuleName = split[0];
                refinedClassName = split[1];
            } else if (fqnClassNamePattern.matcher(className).matches()) {
                for (String packageName : packagesJavaBaseModule) {
                    if (packageName.equals(ModuleUtil.getPackageName(className))) {
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
