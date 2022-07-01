package soot;

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

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.BufferedReader;
import java.io.IOException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.options.Options;

/**
 * A utility class for dealing with java 9 modules and module dependencies.
 *
 * @author Andreas Dann
 */
public class ModuleUtil {
  private static final Logger logger = LoggerFactory.getLogger(ModuleUtil.class);

  /*
   * Soot has hard coded class names as string constants that are now contained in the java.base module, this list serves as
   * a lookup for these string constants.
   */
  private static final List<String> packagesJavaBaseModule = parseJavaBasePackage();
  private static final String JAVABASEFILE = "javabase.txt";

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

  public ModuleUtil(Singletons.Global g) {
  }

  public static ModuleUtil v() {
    return G.v().soot_ModuleUtil();
  }

  public final ModuleClassNameWrapper makeWrapper(String className) {
    try {
      return wrapperCache.get(className);
    } catch (ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Check if Soot is run with module mode enabled.
   *
   * @return true, if module mode is used
   */
  public static boolean module_mode() {
    return !Options.v().soot_modulepath().isEmpty();
  }

  /**
   * Finds the module that exports the given class to the given module.
   *
   * @param className
   *          the requested class
   * @param toModuleName
   *          the module from which the request is made
   * @return the module's name that exports the class to the given module
   */
  public final String declaringModule(String className, String toModuleName) {
    if (SootModuleInfo.MODULE_INFO.equalsIgnoreCase(className)) {
      return toModuleName;
    }

    final ModuleScene modSc = ModuleScene.v();
    final SootModuleInfo modInfo;
    if (modSc.containsClass(SootModuleInfo.MODULE_INFO, Optional.fromNullable(toModuleName))) {
      SootClass temp = modSc.getSootClass(SootModuleInfo.MODULE_INFO, Optional.fromNullable(toModuleName));
      if (temp.resolvingLevel() < SootClass.BODIES) {
        modInfo = (SootModuleInfo) SootModuleResolver.v().resolveClass(SootModuleInfo.MODULE_INFO, SootClass.BODIES,
            Optional.fromNullable(toModuleName));
      } else {
        modInfo = (SootModuleInfo) temp;
      }
    } else {
      modInfo = (SootModuleInfo) SootModuleResolver.v().resolveClass(SootModuleInfo.MODULE_INFO, SootClass.BODIES,
          Optional.fromNullable(toModuleName));
    }
    if (modInfo == null) {
      return null;
    }

    final String packageName = getPackageName(className);
    final String chacheKey = modInfo.getModuleName() + '/' + packageName;
    String moduleName = modulePackageCache.getIfPresent(chacheKey);
    if (moduleName != null) {
      return moduleName;
    }

    if (modInfo.exportsPackage(packageName, toModuleName)) {
      return modInfo.getModuleName();
    }

    if (modInfo.isAutomaticModule()) {
      // shortcut, an automatic module is allowed to access any other class
      if (modSc.containsClass(className)) {
        String foundModuleName = modSc.getSootClass(className).getModuleInformation().getModuleName();
        modulePackageCache.put(chacheKey, foundModuleName);
        return foundModuleName;
      }
    }

    for (SootModuleInfo modInf : modInfo.retrieveRequiredModules().keySet()) {
      if (modInf.exportsPackage(packageName, toModuleName)) {
        modulePackageCache.put(chacheKey, modInf.getModuleName());
        return modInf.getModuleName();
      } else {
        String tModuleName = checkTransitiveChain(modInf, packageName, toModuleName, new HashSet<>());
        if (tModuleName != null) {
          modulePackageCache.put(chacheKey, tModuleName);
          return tModuleName;
        }
      }
    }
    // if the class is not exported by any package, it has to internal to this module
    return toModuleName;
  }

  /**
   * recycle check if exported packages is "requires transitive" case. "requires transitive" module will transmit, need chain
   * check until transitive finished.
   * 
   * @param modInfo
   *          moudleinfo
   * @param packageName
   *          package name
   * @param toModuleName
   *          defined moduleName
   */
  private String checkTransitiveChain(SootModuleInfo modInfo, String packageName, String toModuleName,
      Set<String> hasCheckedModule) {
    for (Map.Entry<SootModuleInfo, Integer> entry : modInfo.retrieveRequiredModules().entrySet()) {
      if ((entry.getValue() & Modifier.REQUIRES_TRANSITIVE) != 0) { // check if module is exported via "requires public"
        final SootModuleInfo key = entry.getKey();
        final String moduleName = key.getModuleName();
        if (!hasCheckedModule.contains(moduleName)) {
          hasCheckedModule.add(moduleName);
          if (key.exportsPackage(packageName, toModuleName)) {
            return moduleName;
          } else {
            return checkTransitiveChain(key, packageName, toModuleName, hasCheckedModule);
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns the package name of a full qualified class name.
   *
   * @param className
   *          a full qualified className
   * @return the package name
   */
  private static String getPackageName(String className) {
    int index = className.lastIndexOf('.');
    return (index > 0) ? className.substring(0, index) : "";
  }

  private static List<String> parseJavaBasePackage() {
    List<String> packages = new ArrayList<>();
    Path excludeFile = Paths.get(JAVABASEFILE);
    try (BufferedReader reader
        = new BufferedReader(new InputStreamReader(Files.exists(excludeFile) ? Files.newInputStream(excludeFile)
            : ModuleUtil.class.getResourceAsStream('/' + JAVABASEFILE)))) {
      for (String line; (line = reader.readLine()) != null;) {
        packages.add(line);
      }
    } catch (IOException x) {
      logger.warn("Cannot open file specifying the packages of module 'java.base'", x);
    }
    return packages;
  }

  /**
   * Wrapper class for backward compatibility with existing soot code In existing soot code classes are resolved based on
   * their name without specifying a module to avoid changing all occurrences of String constants in Soot this classes deals
   * with these String constants.
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
    private final String moduleName;

    private ModuleClassNameWrapper(String className) {
      if (SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME.equals(className)) {
        this.className = className;
        this.moduleName = null;
        return;
      }

      String refinedClassName = className;
      String refinedModuleName = null;
      if (className.contains(":")) {
        String[] split = className.split(":");
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
        if (packagesJavaBaseModule.contains(ModuleUtil.getPackageName(className))) {
          refinedModuleName = "java.base";
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
