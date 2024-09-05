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

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dotnet.types.DotNetBasicTypes;
import soot.options.Options;
import soot.util.Chain;
import soot.util.HashChain;

/**
 * Manages the SootClasses of the application being analyzed for Java 9 modules.
 *
 * @author Andreas Dann
 */
public class ModuleScene extends Scene {
  private static final Logger logger = LoggerFactory.getLogger(Scene.class);

  /*
   * holds the references to SootClass 1: String the class name 2: Map<String, RefType>: The String represents the module
   * that holds the corresponding RefType since multiple modules may contain the same class this is a map (for fast look ups)
   * TODO: evaluate if Guava's multimap is faster
   */
  private final Map<String, Map<String, RefType>> nameToClass = new HashMap<>();

  // instead of using a class path, java 9 uses a module path
  private String modulePath = null;

  public ModuleScene(Singletons.Global g) {
    super(g);

    String smp = System.getProperty("soot.module.path");
    if (smp != null) {
      setSootModulePath(smp);
    }

    // this is a new Class in JAVA 9; that is added to Soot Basic Classes in loadNecessaryClasses method
    addBasicClass("java.lang.invoke.StringConcatFactory");
  }

  public static ModuleScene v() {
    return G.v().soot_ModuleScene();
  }

  @Override
  public SootMethod getMainMethod() {
    if (!hasMainClass()) {
      throw new RuntimeException("There is no main class set!");
    }

    SootMethod mainMethod = mainClass.getMethodUnsafe("main",
        Collections.singletonList(ArrayType.v(ModuleRefType.v("java.lang.String", Optional.of("java.base")), 1)),
        VoidType.v());
    if (mainMethod == null) {
      throw new RuntimeException("Main class declares no main method!");
    }
    return mainMethod;
  }

  @Override
  public void extendSootClassPath(String newPathElement) {
    this.extendSootModulePath(newPathElement);
  }

  public void extendSootModulePath(String newPathElement) {
    modulePath += File.pathSeparatorChar + newPathElement;
    ModulePathSourceLocator.v().extendClassPath(newPathElement);
  }

  @Override
  public String getSootClassPath() {
    return getSootModulePath();
  }

  @Override
  public void setSootClassPath(String p) {
    this.setSootModulePath(p);
  }

  public String getSootModulePath() {
    if (modulePath == null) {
      // First, check Options for a module path
      String cp = Options.v().soot_modulepath();
      // If no module path is given via Options, just use the default.
      // Otherwise, if the prepend flag is set, append the default.
      if (cp == null || cp.isEmpty()) {
        cp = defaultJavaModulePath();
      } else if (Options.v().prepend_classpath()) {
        cp += File.pathSeparatorChar + defaultJavaModulePath();
      }

      // add process-dirs (if applicable)
      List<String> dirs = Options.v().process_dir();
      if (!dirs.isEmpty()) {
        StringBuilder pds = new StringBuilder();
        for (String path : dirs) {
          if (!cp.contains(path)) {
            pds.append(path).append(File.pathSeparatorChar);
          }
        }
        cp = pds.append(cp).toString();
      }

      // Set the new module path
      modulePath = cp;
    }

    return modulePath;
  }

  public void setSootModulePath(String p) {
    ModulePathSourceLocator.v().invalidateClassPath();
    modulePath = p;

  }

  private String defaultJavaModulePath() {
    StringBuilder sb = new StringBuilder();

    // test for java 9
    File rtJar = Paths.get(System.getProperty("java.home"), "lib", "jrt-fs.jar").toFile();
    if ((rtJar.exists() && rtJar.isFile()) || !Options.v().soot_modulepath().isEmpty()) {
      sb.append(ModulePathSourceLocator.DUMMY_CLASSPATH_JDK9_FS);
    } else {
      throw new RuntimeException("Error: cannot find jrt-fs.jar.");
    }
    return sb.toString();
  }

  @Override
  protected void addClassSilent(SootClass c) {
    if (c.isInScene()) {
      throw new RuntimeException("already managed: " + c.getName());
    }

    final String className = c.getName();
    if (containsClass(className, Optional.fromNullable(c.moduleName))) {
      throw new RuntimeException("duplicate class: " + className);
    }

    classes.add(c);

    Map<String, RefType> map = nameToClass.get(className);
    if (map == null) {
      nameToClass.put(className, map = new HashMap<>());
    }
    map.put(c.moduleName, c.getType());

    c.getType().setSootClass(c);
    c.setInScene(true);

    // Phantom classes are not really part of the hierarchy anyway, so
    // we can keep the old one
    if (!c.isPhantom) {
      modifyHierarchy();
    }
  }

  @Override
  public boolean containsClass(String className) {
    // TODO: since this code is called from MethodNodeFactory.caseStringConstants
    // check if the wrapper is actually required
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return containsClass(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  public boolean containsClass(String className, Optional<String> moduleName) {
    RefType type = null;
    Map<String, RefType> map = nameToClass.get(className);
    if (map != null && !map.isEmpty()) {
      if (moduleName.isPresent()) {
        type = map.get(ModuleUtil.v().declaringModule(className, moduleName.get()));
      } else {
        // return first element
        type = map.values().iterator().next();
        if (Options.v().verbose() && ModuleUtil.module_mode()) {
          logger.warn("containsClass called with empty module for: " + className);
        }
      }
    }

    return type != null && type.hasSootClass() && type.getSootClass().isInScene();
  }

  @Override
  public boolean containsType(String className) {
    return nameToClass.containsKey(className);
  }

  /**
   * Attempts to load the given class and all of the required support classes. Returns the original class if it was loaded,
   * or null otherwise.
   */
  public SootClass tryLoadClass(String className, int desiredLevel, Optional<String> moduleName) {
    setPhantomRefs(true);
    ClassSource source = ModulePathSourceLocator.v().getClassSource(className, moduleName);
    try {
      if (!getPhantomRefs() && source == null) {
        setPhantomRefs(false);
        return null;
      }
    } finally {
      if (source != null) {
        source.close();
      }
    }
    SootClass toReturn = SootModuleResolver.v().resolveClass(className, desiredLevel, moduleName);
    setPhantomRefs(false);
    return toReturn;
  }

  /**
   * Loads the given class and all of the required support classes. Returns the first class.
   */
  @Override
  public SootClass loadClassAndSupport(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return loadClassAndSupport(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  public SootClass loadClassAndSupport(String className, Optional<String> moduleName) {
    SootClass ret = loadClass(className, SootClass.SIGNATURES, moduleName);
    if (!ret.isPhantom()) {
      ret = loadClass(className, SootClass.BODIES, moduleName);
    }
    return ret;
  }

  @Override
  public SootClass loadClass(String className, int desiredLevel) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return loadClass(wrapper.getClassName(), desiredLevel, wrapper.getModuleNameOptional());
  }

  public SootClass loadClass(String className, int desiredLevel, Optional<String> moduleName) {
    /*
     * if(Options.v().time()) Main.v().resolveTimer.start();
     */
    setPhantomRefs(true);
    SootClass toReturn = SootModuleResolver.v().resolveClass(className, desiredLevel, moduleName);
    setPhantomRefs(false);
    return toReturn;
    /*
     * if(Options.v().time()) Main.v().resolveTimer.end();
     */
  }

  /**
   * Returns the RefType with the given class name or primitive type.
   *
   * @throws RuntimeException
   *           if the Type for this name cannot be found. Use {@link #getRefTypeUnsafe(String, Optional)} to check if type is
   *           an registered RefType.
   */
  public Type getType(String arg, Optional<String> moduleName) {
    String type = arg.replaceAll("([^\\[\\]]*)(.*)", "$1");
    Type result = getRefTypeUnsafe(type, moduleName);
    if (result == null) {
      switch (type) {
        case "long":
          result = LongType.v();
          break;
        case "short":
          result = ShortType.v();
          break;
        case "double":
          result = DoubleType.v();
          break;
        case "int":
          result = IntType.v();
          break;
        case "float":
          result = FloatType.v();
          break;
        case "byte":
          result = ByteType.v();
          break;
        case "char":
          result = CharType.v();
          break;
        case "void":
          result = VoidType.v();
          break;
        case "boolean":
          result = BooleanType.v();
          break;
        default:
          throw new RuntimeException("unknown type: '" + type + "'");
      }
    }

    int arrayCount = arg.contains("[") ? arg.replaceAll("([^\\[\\]]*)(.*)", "$2").length() / 2 : 0;
    return (arrayCount == 0) ? result : ArrayType.v(result, arrayCount);
  }

  /**
   * Returns the RefType with the given className.
   *
   * @throws IllegalStateException
   *           if the RefType for this class cannot be found. Use {@link #containsType(String)} to check if type is
   *           registered
   */
  public RefType getRefType(String className, Optional<String> moduleName) {
    RefType refType = getRefTypeUnsafe(className, moduleName);
    if (refType == null) {
      throw new IllegalStateException("RefType " + className + " not loaded. "
          + "If you tried to get the RefType of a library class, did you call loadNecessaryClasses()? "
          + "Otherwise please check Soot's classpath.");
    }
    return refType;
  }

  @Override
  public RefType getRefType(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return getRefType(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  @Override
  public RefType getObjectType() {
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      return getRefType(DotNetBasicTypes.SYSTEM_OBJECT);
    }
    return getRefType("java.lang.Object", Optional.of("java.base"));
  }

  /**
   * Returns the RefType with the given className. Returns null if no type with the given name can be found.
   */
  public RefType getRefTypeUnsafe(String className, Optional<String> moduleName) {
    // RefType refType = nameToClass.get(className);
    RefType refType = null;
    Map<String, RefType> map = nameToClass.get(className);
    if (map != null && !map.isEmpty()) {
      if (moduleName.isPresent()) {
        refType = map.get(moduleName.get());
      } else {
        // return first element
        refType = map.values().iterator().next();
        if (Options.v().verbose() && ModuleUtil.module_mode()) {
          logger.warn("getRefTypeUnsafe called with empty module for: " + className);
        }
      }
    }

    return refType;
  }

  @Override
  public RefType getRefTypeUnsafe(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return getRefTypeUnsafe(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  public void addRefType(RefType type) {
    final String className = type.getClassName();
    Map<String, RefType> map = nameToClass.get(className);
    if (map == null) {
      nameToClass.put(className, map = new HashMap<>());
    }
    map.put(((ModuleRefType) type).getModuleName(), type);
  }

  @Override
  public SootClass getSootClassUnsafe(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return getSootClassUnsafe(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  public SootClass getSootClassUnsafe(String className, Optional<String> moduleName) {
    RefType type = null;
    Map<String, RefType> map = nameToClass.get(className);
    if (map != null && !map.isEmpty()) {
      if (moduleName.isPresent()) {
        type = map.get(ModuleUtil.v().declaringModule(className, moduleName.get()));
      } else {
        // return first element
        type = map.values().iterator().next();
        if (Options.v().verbose() && ModuleUtil.module_mode()) {
          logger.warn("getSootClassUnsafe called with empty for: " + className);
        }
      }
    }

    if (type != null) {
      SootClass tsc = type.getSootClass();
      if (tsc != null) {
        return tsc;
      }
    }

    if (allowsPhantomRefs() || SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME.equals(className)) {
      SootClass c = new SootClass(className);
      addClassSilent(c);
      c.setPhantomClass();
      return c;
    }

    return null;
  }

  @Override
  public SootClass getSootClass(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return getSootClass(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  public SootClass getSootClass(String className, Optional<String> moduleName) {
    SootClass sc = getSootClassUnsafe(className, moduleName);
    if (sc != null) {
      return sc;
    }
    throw new RuntimeException(System.lineSeparator() + "Aborting: can't find classfile " + className);
  }

  @Override
  public void loadBasicClasses() {
    addReflectionTraceClasses();

    final ModuleUtil modU = ModuleUtil.v();
    int loadedClasses = 0;
    for (int i = SootClass.BODIES; i >= SootClass.HIERARCHY; i--) {
      for (String name : basicclasses[i]) {
        ModuleUtil.ModuleClassNameWrapper wrapper = modU.makeWrapper(name);
        SootClass sootClass = tryLoadClass(wrapper.getClassName(), i, wrapper.getModuleNameOptional());
        if (sootClass != null && !sootClass.isPhantom()) {
          loadedClasses++;
        }
      }
    }
    if (loadedClasses == 0) {
      // Missing basic classes means no Exceptions could be loaded and no Exception hierarchy can
      // lead to non-deterministic Jimple code generation: catch blocks may be removed because of
      // non-existing Exception hierarchy.
      throw new RuntimeException("None of the basic classes could be loaded! Check your Soot class path!");
    }
  }

  /**
   * Load the set of classes that soot needs, including those specified on the command-line. This is the standard way of
   * initialising the list of classes soot should use.
   */
  @Override
  public void loadNecessaryClasses() {
    loadBasicClasses();

    final Options opts = Options.v();
    for (String name : opts.classes()) {
      loadNecessaryClass(name);
    }

    loadDynamicClasses();

    if (opts.oaat()) {
      if (opts.process_dir().isEmpty()) {
        throw new IllegalArgumentException("If switch -oaat is used, then also -process-dir must be given.");
      }
    } else {
      for (String path : opts.process_dir()) {
        for (Map.Entry<String, List<String>> entry : ModulePathSourceLocator.v().getClassUnderModulePath(path).entrySet()) {
          for (String cl : entry.getValue()) {
            SootClass theClass = loadClassAndSupport(cl, Optional.fromNullable(entry.getKey()));
            theClass.setApplicationClass();
          }
        }
      }
    }

    prepareClasses();
    setDoneResolving();
  }

  @Override
  public void loadDynamicClasses() {
    final ArrayList<SootClass> dynamicClasses = new ArrayList<>();
    final Options opts = Options.v();

    final Map<String, List<String>> temp = new HashMap<>();
    temp.put(null, opts.dynamic_class());

    final ModulePathSourceLocator msloc = ModulePathSourceLocator.v();
    for (String path : opts.dynamic_dir()) {
      temp.putAll(msloc.getClassUnderModulePath(path));
    }

    final SourceLocator sloc = SourceLocator.v();
    for (String pkg : opts.dynamic_package()) {
      temp.get(null).addAll(sloc.classesInDynamicPackage(pkg));
    }

    for (Map.Entry<String, List<String>> entry : temp.entrySet()) {
      for (String className : entry.getValue()) {
        dynamicClasses.add(loadClassAndSupport(className, Optional.fromNullable(entry.getKey())));
      }
    }

    // remove non-concrete classes that may accidentally have been loaded
    for (Iterator<SootClass> iterator = dynamicClasses.iterator(); iterator.hasNext();) {
      SootClass c = iterator.next();
      if (!c.isConcrete()) {
        if (opts.verbose()) {
          logger.warn("dynamic class " + c.getName() + " is abstract or an interface, and it will not be considered.");
        }
        iterator.remove();
      }
    }
    this.dynamicClasses = dynamicClasses;
  }

  /**
   * Generate classes to process, adding or removing package marked by command line options.
   */
  @Override
  protected void prepareClasses() {
    final List<String> optionsClasses = Options.v().classes();
    // Remove/add all classes from packageInclusionMask as per -i option
    Chain<SootClass> processedClasses = new HashChain<>();
    while (true) {
      Chain<SootClass> unprocessedClasses = new HashChain<>(getClasses());
      unprocessedClasses.removeAll(processedClasses);
      if (unprocessedClasses.isEmpty()) {
        break;
      }
      processedClasses.addAll(unprocessedClasses);
      for (SootClass s : unprocessedClasses) {
        if (s.isPhantom()) {
          continue;
        }
        if (Options.v().app()) {
          s.setApplicationClass();
        }
        if (optionsClasses.contains(s.getName())) {
          s.setApplicationClass();
          continue;
        }
        if (s.isApplicationClass() && isExcluded(s)) {
          s.setLibraryClass();
        }
        if (isIncluded(s)) {
          s.setApplicationClass();
        }
        if (s.isApplicationClass()) {
          // make sure we have the support
          loadClassAndSupport(s.getName(), Optional.fromNullable(s.moduleName));
        }
      }
    }
  }

  @Override
  public void setMainClassFromOptions() {
    if (mainClass == null) {
      String optsMain = Options.v().main_class();
      if (optsMain != null && !optsMain.isEmpty()) {
        setMainClass(getSootClass(optsMain, null));
      } else {
        final List<Type> mainArgs
            = Collections.singletonList(ArrayType.v(ModuleRefType.v("java.lang.String", Optional.of("java.base")), 1));
        // try to infer a main class from the command line if none is given
        for (String s : Options.v().classes()) {
          SootClass c = getSootClass(s, null);
          if (c.declaresMethod("main", mainArgs, VoidType.v())) {
            logger.debug("No main class given. Inferred '" + c.getName() + "' as main class.");
            setMainClass(c);
            return;
          }
        }

        // try to infer a main class from the usual classpath if none is given
        for (SootClass c : getApplicationClasses()) {
          if (c.declaresMethod("main", mainArgs, VoidType.v())) {
            logger.debug("No main class given. Inferred '" + c.getName() + "' as main class.");
            setMainClass(c);
            return;
          }
        }
      }
    }
  }

  @Override
  public SootClass forceResolve(String className, int level) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return forceResolve(wrapper.getClassName(), level, wrapper.getModuleNameOptional());
  }

  public SootClass forceResolve(String className, int level, Optional<String> moduleName) {
    boolean tmp = doneResolving;
    doneResolving = false;
    SootClass c;
    try {
      c = SootModuleResolver.v().resolveClass(className, level, moduleName);
    } finally {
      doneResolving = tmp;
    }
    return c;
  }

  @Override
  public SootClass makeSootClass(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return makeSootClass(wrapper.getClassName(), wrapper.getModuleName());
  }

  public SootClass makeSootClass(String name, String moduleName) {
    return new SootClass(name, moduleName);
  }

  public RefType getOrAddRefType(RefType tp) {
    RefType existing = getRefType(tp.getClassName(), Optional.fromNullable(((ModuleRefType) tp).getModuleName()));
    if (existing != null) {
      return existing;
    }
    this.addRefType(tp);
    return tp;
  }

  public RefType getOrAddRefType(String className, Optional<String> moduleName) {
    RefType existing = getRefType(className, moduleName);
    if (existing != null) {
      return existing;
    }
    RefType tp = ModuleRefType.v(className, moduleName);
    this.addRefType(tp);
    return tp;
  }
}