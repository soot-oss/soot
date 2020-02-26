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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.dava.toolkits.base.misc.PackageNamer;

/**
 * Represetns a Module-Info file
 * 
 * @author Andreas Dann
 */
public class SootModuleInfo extends SootClass {

  public static final String MODULE_INFO_FILE = "module-info.class";
  public static final String MODULE_INFO = "module-info";
  private HashSet<String> modulePackages = new HashSet<>();

  private static final String ALL_MODULES = "EVERYONE_MODULE";

  public boolean isAutomaticModule() {
    return isAutomaticModule;
  }

  public void setAutomaticModule(boolean automaticModule) {
    isAutomaticModule = automaticModule;
  }

  private boolean isAutomaticModule;

  private Map<SootModuleInfo, Integer> requiredModules = new HashMap<SootModuleInfo, Integer>();

  // TODO: change String to SootClassReference
  private Map<String, List<String>> exportedPackages = new HashMap<String, List<String>>();

  // TODO: change String to SootClassReference
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
      if (this.exportsPackage(packaze, ALL_MODULES)) {
        publicExportedPackages.add(packaze);
      }
    }
    return publicExportedPackages;
  }

  public Set<String> getPublicOpenedPackages() {
    Set<String> publicOpenedPackages = new HashSet<>();
    for (String packaze : modulePackages) {
      if (this.opensPackage(packaze, ALL_MODULES)) {
        publicOpenedPackages.add(packaze);
      }
    }
    return publicOpenedPackages;
  }

  public Map<SootModuleInfo, Integer> getRequiredModules() {
    return requiredModules;
  }

  public Map<SootModuleInfo, Integer> retrieveRequiredModules() {
    Map<SootModuleInfo, Integer> moduleInfos = requiredModules;

    // move into subclass
    if (this.isAutomaticModule) {
      // we can read all modules
      for (SootClass sootClass : Scene.v().getClasses()) {
        if (sootClass instanceof SootModuleInfo && sootClass.moduleName != this.moduleName) {
          moduleInfos.put((SootModuleInfo) sootClass, Modifier.REQUIRES_STATIC);
        }
      }
    }

    for (SootModuleInfo moduleInfo : moduleInfos.keySet()) {
      SootModuleResolver.v().resolveClass(SootModuleInfo.MODULE_INFO, SootClass.BODIES,
          Optional.fromNullable(moduleInfo.moduleName));
    }
    return moduleInfos;
  }

  public void addExportedPackage(String packaze, String... exportedToModules) {
    String packageName = PackageNamer.v().get_FixedPackageName(packaze).replace("/", ".");
    List<String> qualifiedExports = Collections.singletonList(SootModuleInfo.ALL_MODULES);
    if (exportedToModules != null && exportedToModules.length > 0) {
      qualifiedExports = Arrays.asList(exportedToModules);
    }
    exportedPackages.put(packageName, qualifiedExports);

  }

  public void addOpenedPackage(String packaze, String... openedToModules) {
    String packageName = PackageNamer.v().get_FixedPackageName(packaze).replace("/", ".");
    List<String> qualifiedOpens = Collections.singletonList(SootModuleInfo.ALL_MODULES);
    if (openedToModules != null && openedToModules.length > 0) {
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
    return exportsPackage(packaze, ALL_MODULES);
  }

  public boolean openPackagePublic(String packaze) {
    return opensPackage(packaze, ALL_MODULES);
  }

  public boolean opensPackage(String packaze, String toModule) {

    if (packaze.equalsIgnoreCase(SootModuleInfo.MODULE_INFO)) {
      return true;
    }

    /// all packages are exported/open to self
    if (this.getModuleName().equals(toModule)) {
      return this.modulePackages.contains(packaze);
    }

    // all packages in open and automatic modules are open
    if (this.isAutomaticModule()) {
      return this.modulePackages.contains(packaze);
    }

    List<String> qualifiedOpens = this.openedPackages.get(packaze);
    if (qualifiedOpens == null) {
      return false; // if qualifiedExport is null, the package is not exported
    }

    if (qualifiedOpens.contains(ALL_MODULES)) {
      return true;
    }
    if (toModule != ALL_MODULES && qualifiedOpens.contains(toModule)) {
      return true;
    }

    return false;
  }

  public boolean exportsPackage(String packaze, String toModule) {

    if (packaze.equalsIgnoreCase(SootModuleInfo.MODULE_INFO)) {
      return true;
    }

    /// all packages are exported/open to self
    if (this.getModuleName().equals(toModule)) {
      return this.modulePackages.contains(packaze);
    }

    // a automatic module exports all its packages
    if (this.isAutomaticModule()) {
      return this.modulePackages.contains(packaze);
    }

    List<String> qualifiedExport = this.exportedPackages.get(packaze);
    if (qualifiedExport == null) {
      return false;
    }

    if (qualifiedExport.contains(ALL_MODULES)) {
      return true;
    }
    if (toModule != ALL_MODULES && qualifiedExport.contains(toModule)) {
      return true;
    }

    return false;
  }

  public Set<SootModuleInfo> getRequiredPublicModules() {
    Set<SootModuleInfo> requiredPublic = new HashSet<>();
    // check if exported packages is "requires public"
    for (Map.Entry<SootModuleInfo, Integer> entry : this.requiredModules.entrySet()) {
      if ((entry.getValue() & Modifier.REQUIRES_TRANSITIVE) != 0) // check if module is reexported via "requires public"
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
