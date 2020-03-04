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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import soot.JavaClassProvider.JarException;
import soot.asm.AsmModuleClassProvider;

/**
 * Provides utility methods to retrieve an input stream for a class , given a classfile and module
 *
 * @author Andreas Dann
 */
public class ModulePathSourceLocator extends SourceLocator {

  public static final String DUMMY_CLASSPATH_JDK9_FS = "VIRTUAL_FS_FOR_JDK";

  private List<String> sourcePath;

  private Set<String> classesToLoad;

  public ModulePathSourceLocator(Singletons.Global g) {
    super(g);
  }

  public static ModulePathSourceLocator v() {
    return G.v().soot_ModulePathSourceLocator();
  }

  @Override
  public ClassSource getClassSource(String className) {

    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);

    return getClassSource(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  /**
   * Given a class name, uses the soot-module-path to return a ClassSource for the given class.
   */
  public ClassSource getClassSource(String className, com.google.common.base.Optional<String> moduleName) {
    String appendToPath = "";
    if (moduleName.isPresent()) {
      appendToPath = moduleName.get() + ":";
    }
    if (classesToLoad == null) {
      classesToLoad = new HashSet<>();
      classesToLoad.addAll(ModuleScene.v().getBasicClasses());
      for (SootClass c : ModuleScene.v().getApplicationClasses()) {
        classesToLoad.add(c.getName());
      }
    }

    if (modulePath == null) {
      modulePath = explodeModulePath(ModuleScene.v().getSootModulePath());
    }
    if (classProviders == null) {
      setupClassProviders();
    }

    JarException ex = null;
    for (ClassProvider cp : classProviders) {
      try {
        ClassSource ret = cp.find(appendToPath + className);
        if (ret != null) {
          return ret;
        }
      } catch (JarException e) {
        ex = e;
      }
    }
    if (ex != null) {
      throw ex;
    }

    return null;
  }

  public static List<String> explodeModulePath(String classPath) {
    List<String> ret = new ArrayList<>();

    StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator);
    while (tokenizer.hasMoreTokens()) {
      String originalDir = tokenizer.nextToken();
      String canonicalDir;
      try {

        canonicalDir = new File(originalDir).getCanonicalPath();
        if (originalDir.equals(DUMMY_CLASSPATH_JDK9_FS)) {
          canonicalDir = "jrt:/";
        }
        ret.add(canonicalDir);
      } catch (IOException e) {
        throw new CompilationDeathException("Couldn't resolve classpath entry " + originalDir + ": " + e);
      }
    }
    return ret;
  }

  public void additionalClassLoader(ClassLoader c) {
    additionalClassLoaders.add(c);
  }

  private List<String> modulePath;
  private int next = 0;

  private boolean modulePathHasNextEntry() {
    return this.next < this.modulePath.size();
  }

  @Override
  public List<String> classPath() {
    return modulePath;
  }

  @Override
  public void invalidateClassPath() {
    modulePath = null;
    super.invalidateClassPath();
  }

  @Override
  public List<String> sourcePath() {
    if (sourcePath == null) {
      sourcePath = new ArrayList<>();
      for (String dir : modulePath) {
        ClassSourceType cst = getClassSourceType(dir);
        if (cst != ClassSourceType.apk && cst != ClassSourceType.jar && cst != ClassSourceType.zip) {
          sourcePath.add(dir);
        }
      }
    }
    return sourcePath;
  }

  private final HashMap<String, Path> moduleNameToPath = new HashMap<>();

  /**
   * For backward compatibility returns classes in the form of module:classname
   *
   * @param aPath
   *          where to search for classes
   * @return a String list containing entries of the form module:classname
   */
  @Override
  public List<String> getClassesUnder(String aPath) {

    Map<String, List<String>> moduleClasses = getClassUnderModulePath(aPath);
    List<String> classes = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : moduleClasses.entrySet()) {
      for (String className : entry.getValue()) {
        String moduleClassNameConcatenation = entry.getKey() + ":" + className;
        classes.add(moduleClassNameConcatenation);
      }
    }

    return classes;

  }

  /**
   * Scan the given module path entry. If the entry is a directory then it is a directory of modules or an exploded module.
   * If the entry is a regular file then it is assumed to be a packaged module.
   */
  public Map<String, List<String>> getClassUnderModulePath(String aPath) {
    Map<String, List<String>> mapModuleClasses = new HashMap<>();
    Path path = null;
    ClassSourceType type = getClassSourceType(aPath);
    switch (type) {
      case jar:
        path = Paths.get(aPath);
        break;
      case zip:
        path = Paths.get(aPath);
        break;
      case directory:
        path = Paths.get(aPath);
        break;
      case jrt:
        path = Paths.get(URI.create(aPath)).resolve("modules");
        break;
      case unknown:
        break;
      default:
        path = Paths.get(aPath);
        break;
    }
    if (classProviders == null) {
      setupClassProviders();
    }
    if (path == null) {
      throw new RuntimeException("[Error] The path " + aPath + "is not a valid path.");
    }

    BasicFileAttributes attrs = null;
    try {
      attrs = Files.readAttributes(path, BasicFileAttributes.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (attrs.isDirectory()) {
      Path mi = path.resolve(SootModuleInfo.MODULE_INFO_FILE);
      if (!Files.exists(mi)) {
        // assume a directory of modules
        mapModuleClasses.putAll(discoverModulesIn(path));
      } else {
        // found an exploded module
        mapModuleClasses.putAll(buildModuleForExplodedModule(path));
      }
    }
    // found a jar that is either a modular jar or a simple jar that must be transformed to an automatic module
    else if (attrs.isRegularFile() && path.getFileName().toString().endsWith(".jar")) {
      mapModuleClasses.putAll(buildModuleForJar(path));
    }
    return mapModuleClasses;

  }

  /**
   * Searches in a directory for module definitions currently only one level of hierarchy is traversed
   *
   * @param path
   *          the directory
   * @return the found modules and their classes
   */
  private Map<String, List<String>> discoverModulesIn(Path path) {
    Map<String, List<String>> mapModuleClasses = new HashMap<>();

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
      for (Path entry : stream) {
        BasicFileAttributes attrs;
        try {
          attrs = Files.readAttributes(entry, BasicFileAttributes.class);
        } catch (NoSuchFileException ignore) {
          continue;
        }

        if (attrs.isDirectory()) {
          Path mi = entry.resolve(SootModuleInfo.MODULE_INFO_FILE);
          if (Files.exists(mi)) {
            mapModuleClasses.putAll(buildModuleForExplodedModule(entry));
          }
        } else if (attrs.isRegularFile() && entry.getFileName().toString().endsWith(".jar")) {
          mapModuleClasses.putAll(buildModuleForJar(entry));
        }

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mapModuleClasses;
  }

  /**
   * Creates a module definition for either a modular jar or an automatic module
   *
   * @param jar
   *          the jar file
   * @return the module and its containing classes
   */
  private Map<String, List<String>> buildModuleForJar(Path jar) {
    Map<String, List<String>> moduleClassMap = new HashMap<>();

    try (FileSystem zipFileSystem = FileSystems.newFileSystem(jar, this.getClass().getClassLoader())) {
      Path mi = zipFileSystem.getPath(SootModuleInfo.MODULE_INFO_FILE);
      if (Files.exists(mi)) {
        FoundFile foundFile = new FoundFile(mi);

        for (ClassProvider cp : classProviders) {
          if (cp instanceof AsmModuleClassProvider) {
            String moduleName = ((AsmModuleClassProvider) cp).getModuleName(foundFile);
            SootModuleInfo moduleInfo
                = (SootModuleInfo) SootModuleResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
            this.moduleNameToPath.put(moduleName, jar);
            List<String> classesInJar = super.getClassesUnder(jar.toAbsolutePath().toString());
            for (String foundClass : classesInJar) {
              int index = foundClass.lastIndexOf('.');
              if (index > 0) {
                String packageName = foundClass.substring(0, index);
                moduleInfo.addModulePackage(packageName);
              }
            }
            moduleClassMap.put(moduleName, classesInJar);

          }
        }
      } else {
        // no module-info treat as automatic module
        // create module name from jar
        String filename = jar.getFileName().toString();

        // make module base on the filname of the jar
        String moduleName = createModuleNameForAutomaticModule(filename);
        boolean containsClass = ModuleScene.v().containsClass(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
        SootModuleInfo moduleInfo;
        if (!containsClass) {
          moduleInfo = new SootModuleInfo(SootModuleInfo.MODULE_INFO, moduleName, true);
          Scene.v().addClass(moduleInfo);
          moduleInfo.setApplicationClass();
        } else {
          moduleInfo = (SootModuleInfo) ModuleScene.v().getSootClass(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
          if (!(moduleInfo.resolvingLevel() == SootClass.DANGLING)) {
            return moduleClassMap;
          }
        }

        // collect the packages in this jar and add them to the exported
        List<String> classesInJar = super.getClassesUnder(jar.toAbsolutePath().toString());
        for (String foundClass : classesInJar) {
          int index = foundClass.lastIndexOf('.');
          if (index > 0) {
            String packageName = foundClass.substring(0, index);
            moduleInfo.addModulePackage(packageName);
          }
        }
        moduleInfo.setResolvingLevel(SootClass.BODIES);
        moduleInfo.setAutomaticModule(true);
        this.moduleNameToPath.put(moduleName, jar);
        moduleClassMap.put(moduleName, classesInJar);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return moduleClassMap;
  }

  /**
   * Creates a name for an automatic module based on the name of a jar file this is based on the jdk parsing of module name
   * in the JDK 9{@link ModulePathFinder} at least the patterns are the same
   *
   * @param filename
   *          the name of the jar file
   * @return the name of the automatic module
   */
  private String createModuleNameForAutomaticModule(String filename) {
    int i = filename.lastIndexOf(File.separator);
    if (i != -1) {
      filename = filename.substring(i + 1);
    }

    // drop teh file extension .jar
    String moduleName = filename.substring(0, filename.length() - 4);

    // find first occurrence of -${NUMBER}. or -${NUMBER}$
    // according to the java 9 spec and current implementation, version numbers are ignored when naming automatic modules
    Matcher matcher = Pattern.compile("-(\\d+(\\.|$))").matcher(moduleName);
    if (matcher.find()) {
      int start = matcher.start();
      moduleName = moduleName.substring(0, start);
    }
    moduleName = Pattern.compile("[^A-Za-z0-9]").matcher(moduleName).replaceAll(".");

    // remove all repeating dots
    moduleName = Pattern.compile("(\\.)(\\1)+").matcher(moduleName).replaceAll(".");

    // remove leading dots
    int len = moduleName.length();
    if (len > 0 && moduleName.charAt(0) == '.') {
      moduleName = Pattern.compile("^\\.").matcher(moduleName).replaceAll("");
    }

    // remove trailing dots
    len = moduleName.length();
    if (len > 0 && moduleName.charAt(len - 1) == '.') {
      moduleName = Pattern.compile("\\.$").matcher(moduleName).replaceAll("");
    }

    return moduleName;
  }

  /**
   * Creates/Discovers a module for an exploded module
   *
   * @param dir
   *          the path of the exploded module
   * @return the module and its classes
   */
  private Map<String, List<String>> buildModuleForExplodedModule(Path dir) {
    Map<String, List<String>> moduleClassesMap = new HashMap<>();
    Path mi = dir.resolve(SootModuleInfo.MODULE_INFO_FILE);

    for (ClassProvider cp : classProviders) {
      if (cp instanceof AsmModuleClassProvider) {

        FoundFile foundFile = new FoundFile(mi);
        // try (InputStream in = Files.newInputStream(mi)) {

        String moduleName = ((AsmModuleClassProvider) cp).getModuleName(foundFile);
        SootModuleInfo moduleInfo
            = (SootModuleInfo) SootModuleResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
        this.moduleNameToPath.put(moduleName, dir);

        List<String> classes = getClassesUnderDirectory(dir);
        for (String foundClass : classes) {
          int index = foundClass.lastIndexOf('.');
          if (index > 0) {

            String packageName = foundClass.substring(0, index);
            moduleInfo.addModulePackage(packageName);
          }
        }

        moduleClassesMap.put(moduleName, classes);

        /*
         * } catch (IOException e) { e.printStackTrace(); }
         */
      }
    }
    return moduleClassesMap;
  }

  /* This is called after sootClassPath has been defined. */
  @Override
  public Set<String> classesInDynamicPackage(String str) {
    HashSet<String> set = new HashSet<>(0);
    StringTokenizer strtok
        = new StringTokenizer(ModuleScene.v().getSootModulePath(), String.valueOf(File.pathSeparatorChar));
    while (strtok.hasMoreTokens()) {
      String path = strtok.nextToken();

      // For jimple files
      List<String> l = super.getClassesUnder(path);
      for (String filename : l) {
        if (filename.startsWith(str)) {
          set.add(filename);
        }
      }

      // For class files;
      path = path + File.pathSeparatorChar;
      StringTokenizer tokenizer = new StringTokenizer(str, ".");
      while (tokenizer.hasMoreTokens()) {
        path = path + tokenizer.nextToken();
        if (tokenizer.hasMoreTokens()) {
          path = path + File.pathSeparatorChar;
        }
      }
      l = super.getClassesUnder(path);
      for (String string : l) {
        set.add(str + "." + string);
      }
    }
    return set;
  }

  /**
   * Searches for a file with the given name in the exploded modulePath.
   */
  @Override
  public FoundFile lookupInClassPath(String fileName) {

    return lookUpInModulePath(fileName);

  }

  private ClassSourceType getClassSourceType(Path path) {
    String stringPath = path.toUri().toString();
    if (stringPath.startsWith("jrt:/")) {
      return ClassSourceType.jrt;
    }
    return super.getClassSourceType(path.toAbsolutePath().toString());
  }

  @Override
  protected ClassSourceType getClassSourceType(String path) {
    if (path.startsWith("jrt:/")) {
      return ClassSourceType.jrt;
    }
    return super.getClassSourceType(path);
  }

  public FoundFile lookUpInModulePath(String fileName) {
    String[] moduleAndClassName = fileName.split(":");
    String className = moduleAndClassName[moduleAndClassName.length - 1];
    String moduleName = moduleAndClassName[0];

    if (className.isEmpty() || moduleName.isEmpty()) {
      throw new RuntimeException("No module given!");
    }

    // look if we know where the module is
    Path foundModulePath = discoverModule(moduleName);

    FoundFile ret = null;
    if (foundModulePath == null) {
      return null;
    }
    // transform the path to a String to reuse the
    String dir = foundModulePath.toAbsolutePath().toString();
    if (foundModulePath.toUri().toString().startsWith("jrt:/")) {
      dir = foundModulePath.toUri().toString();
    }

    ClassSourceType cst = getClassSourceType(foundModulePath);
    if (cst == ClassSourceType.zip || cst == ClassSourceType.jar) {
      ret = lookupInArchive(dir, className);
    } else if (cst == ClassSourceType.directory) {
      ret = lookupInDir(dir, className);
    } else if (cst == ClassSourceType.jrt) {
      ret = lookUpInVirtualFileSystem(dir, className);
    }

    if (ret != null) {
      return ret;
    }

    return null;
  }

  /**
   * Searches in the modulepath for a certain module
   *
   * @param moduleName
   *          the name of the module
   * @return the found path or null
   */
  private Path discoverModule(String moduleName) {
    Path pathToModule = moduleNameToPath.get(moduleName);
    if (pathToModule != null) {
      return pathToModule;
    }
    while (modulePathHasNextEntry()) {
      String path = modulePath.get(next);
      getClassUnderModulePath(path);
      next++;
      pathToModule = moduleNameToPath.get(moduleName);
      if (pathToModule != null) {
        return pathToModule;
      }
    }
    return null;
  }

  private FoundFile lookupInDir(String dir, String fileName) {
    Path dirPath = Paths.get(dir);
    Path foundFile = dirPath.resolve(fileName);
    if (foundFile != null && Files.isRegularFile(foundFile)) {
      return new FoundFile(foundFile);
    }

    return null;

  }

  /**
   * Looks up classes in an archive file
   *
   * @param archivePath
   *          path to the zip/jar
   * @param fileName
   *          the filename to search
   * @return the FoundFile
   */
  protected FoundFile lookupInArchive(String archivePath, String fileName) {
    Path archive = Paths.get(archivePath);
    try (FileSystem zipFileSystem = FileSystems.newFileSystem(archive, this.getClass().getClassLoader())) {
      Path entry = zipFileSystem.getPath(fileName);
      if (entry == null || !Files.isRegularFile(entry)) {
        return null;
      }
      return new FoundFile(archive.toAbsolutePath().toString(), fileName);
    } catch (IOException e) {
      throw new RuntimeException(
          "Caught IOException " + e + " looking in archive file " + archivePath + " for file " + fileName);

    }
  }

  /**
   * Looks up classes in Java 9's virtual filesystem jrt:/
   *
   * @param archivePath
   *          path to the filesystem
   * @param fileName
   *          the file to search
   * @return the FoundFile
   */
  public FoundFile lookUpInVirtualFileSystem(String archivePath, String fileName) {
    // FileSystem fs = FileSystems.getFileSystem(URI.create(archivePath));
    Path foundFile = Paths.get(URI.create(archivePath)).resolve(fileName);
    if (foundFile != null && Files.isRegularFile(foundFile)) {
      return new FoundFile(foundFile);
    }

    return null;
  }

  @Override
  protected void setupClassProviders() {
    classProviders = new LinkedList<>();
    ClassProvider classFileClassProvider = new AsmModuleClassProvider();
    classProviders.add(classFileClassProvider);

  }

  /**
   * Replaces super.getClassesUnder in order to deal with the virtual filesystem jrt
   *
   * @param aPath
   *          the directory
   * @return List of found classes
   */
  private List<String> getClassesUnderDirectory(Path aPath) {
    List<String> classes = new ArrayList<>();
    ClassSourceType cst = getClassSourceType(aPath);

    if (cst == ClassSourceType.directory || cst == ClassSourceType.jrt) {

      FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

          String fileName = aPath.relativize(file).toString().replace(File.separatorChar, '.');

          if (fileName.endsWith(".class")) {
            int index = fileName.lastIndexOf(".class");
            classes.add(fileName.substring(0, index));
          }

          if (fileName.endsWith(".jimple")) {
            int index = fileName.lastIndexOf(".jimple");
            classes.add(fileName.substring(0, index));
          }

          if (fileName.endsWith(".java")) {
            int index = fileName.lastIndexOf(".java");
            classes.add(fileName.substring(0, index));
          }

          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          return FileVisitResult.CONTINUE;
        }

      };
      try {
        Files.walkFileTree(aPath, fileVisitor);
      } catch (IOException e) {
        e.printStackTrace();
      }

    } else {
      throw new

      RuntimeException("Invalid class source type");
    }
    return classes;
  }
}
