/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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
package soot;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import soot.options.Options;

/**
 * A utility class for dealing with java 9 modules and module dependencies
 *
 * @author Andreas Dann
 */
public final class ModuleUtil {

  public ModuleUtil(Singletons.Global g) {

  }

  public static ModuleUtil v() {

    return G.v().soot_ModuleUtil();
  }

  private final Cache<String, String> modulePackageCache = CacheBuilder.newBuilder().initialCapacity(60).maximumSize(800)
      .concurrencyLevel(Runtime.getRuntime().availableProcessors()).build();
  private final LoadingCache<String, ModuleClassNameWrapper> wrapperCache = CacheBuilder.newBuilder().initialCapacity(100)
      .maximumSize(1000).concurrencyLevel(Runtime.getRuntime().availableProcessors())
      .build(new CacheLoader<String, ModuleClassNameWrapper>() {
        @Override
        public ModuleClassNameWrapper load(String key) throws Exception {
          return new ModuleClassNameWrapper(key);
        }
      });

  public final ModuleClassNameWrapper makeWrapper(String className) {
    try {
      return wrapperCache.get(className);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Check if Soot is run with module mode enables
   *
   * @return true, if module mode is used
   */
  public static boolean module_mode() {
    return !Options.v().soot_modulepath().isEmpty();
  }

  /**
   * Finds the module that exports the given class to the given module
   *
   * @param className
   *          the requested class
   * @param toModuleName
   *          the module from which the request is made
   * @return the module's name that exports the class to the given module
   */
  public final String findModuleThatExports(String className, String toModuleName) {

    if (className.equalsIgnoreCase(SootModuleInfo.MODULE_INFO)) {
      return toModuleName;
    }
    SootModuleInfo modInfo;
    if (ModuleScene.v().containsClass(SootModuleInfo.MODULE_INFO, Optional.fromNullable(toModuleName))) {
      modInfo
          = (SootModuleInfo) ModuleScene.v().getSootClass(SootModuleInfo.MODULE_INFO, Optional.fromNullable(toModuleName));
      if (modInfo.resolvingLevel() < SootClass.BODIES) {
        modInfo = (SootModuleInfo) SootModuleResolver.v().resolveClass(SootModuleInfo.MODULE_INFO, SootClass.BODIES,
            Optional.fromNullable(toModuleName));
      }
    } else {
      modInfo = (SootModuleInfo) SootModuleResolver.v().resolveClass(SootModuleInfo.MODULE_INFO, SootClass.BODIES,
          Optional.fromNullable(toModuleName));
    }

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
      // shortcut, an automatic module is allowed to access any other class
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
    	Set<String> hasCheckedModule = new HashSet<String>();
     	String tModuleName = checkTransitiveChain(modInf, packageName, toModuleName, hasCheckedModule);
    	if (tModuleName != null) {
          modulePackageCache.put(modInfo.getModuleName() + "/" + packageName, tModuleName);
          return tModuleName;
    	} 

      }

    }
    // if the class is not exported by any package, it has to internal to this module
    return toModuleName;
  }
  /**
   * recycle check if exported packages is "requires transitive" case.
   * "requires transitive" module will transmit, need chain check until transitive finished.
   * @param modInfo moudleinfo 
   * @param packageName package name
   * @param toModuleName defined moduleName
   * 
   */
  private String checkTransitiveChain(SootModuleInfo modInfo, String packageName, String toModuleName,
          Set<String> hasCheckedModule) {
    for (Map.Entry<SootModuleInfo, Integer> entry : modInfo.retrieveRequiredModules().entrySet()) {
      if ((entry.getValue() & Modifier.REQUIRES_TRANSITIVE) != 0) { // check if module is exported via "requires public"
    	if (hasCheckedModule.contains(entry.getKey().getModuleName())) {
          continue;
    	} else {
          hasCheckedModule.add(entry.getKey().getModuleName());
    	}
        if (entry.getKey().exportsPackage(packageName, toModuleName)) {
          return entry.getKey().getModuleName();
        } else {
          return checkTransitiveChain(entry.getKey(), packageName, toModuleName, hasCheckedModule);
        }
      }
    }
    return null;
  }



  /**
   * The returns the package name of a full qualified class name
   *
   * @param className
   *          a full qualified className
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

  /*
   * In Soot are a hard coded class names as string constants that are now contained in the java.base module, this list
   * serves as a lookup for these string constant
   */
  private static final List<String> packagesJavaBaseModule = parseJavaBasePackage();
  private static final String JAVABASEFILE = "javabase.txt";

  private static List<String> parseJavaBasePackage() {
    List<String> packages = new ArrayList<>();
    InputStream in = null;
    Path excludeFile = Paths.get(JAVABASEFILE);
    try {
      if (!Files.exists(excludeFile)) {
        // else take the one package

        in = ModuleUtil.class.getResourceAsStream("/" + JAVABASEFILE);
      } else {

        in = Files.newInputStream(excludeFile);

      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    // read file into stream, try-with-resources
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in)))

    {
      String line;
      while ((line = reader.readLine()) != null) {
        packages.add(line);
      }
    } catch (IOException x)

    {
      G.v().out.println("[WARN] No files specifying the packages of module java.base");
    }
    return packages;
  }

  /**
   * Wrapper class for backward compatibility with existing soot code In existing soot code classes are resolved based on
   * their name without specifying a module to avoid changing all occurrences of String constants in Soot this classes deals
   * with these String constants
   *
   * @author Andreas Dann
   */
  public static final class ModuleClassNameWrapper {

    // check for occurrence of full qualified class names
    private static final String fullQualifiedName = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)+[a-zA-Z_$][a-zA-Z\\d_$]*";
    private static final Pattern fqnClassNamePattern = Pattern.compile(fullQualifiedName);

    // check for occurrence of module name
    private static final String qualifiedModuleName = "([a-zA-Z_$])([a-zA-Z\\d_$\\.]*)+";
    private static final Pattern qualifiedModuleNamePattern = Pattern.compile(qualifiedModuleName);

    private final String className;
    private String moduleName;

    private ModuleClassNameWrapper(String className) {

      String refinedClassName = className;
      String refinedModuleName = null;
      if (className.equals(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME)) {
        this.className = refinedClassName;
        return;
      } else if (className.contains(":")) {
        String split[] = className.split(":");
        if (split.length == 2) {
          // check if first is valid module name
          if (qualifiedModuleNamePattern.matcher(split[0]).matches()) {
            // check if second is fq classname
            if (fqnClassNamePattern.matcher(split[1]).matches()) {
              refinedModuleName = split[0];
              refinedClassName = split[1];
            }
          }
        }
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
      ModuleUtil.v().wrapperCache.put(className, this);
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
