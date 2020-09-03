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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.JavaClassProvider.JarException;
import soot.asm.AsmModuleClassProvider;

/**
 * Provides utility methods to retrieve an input stream for a class , given a classfile and module
 *
 * @author Andreas Dann
 */
public class ModulePathSourceLocator extends SourceLocator {
  private static final Logger logger = LoggerFactory.getLogger(ModulePathSourceLocator.class);

  public static final String DUMMY_CLASSPATH_JDK9_FS = "VIRTUAL_FS_FOR_JDK";

  private final HashMap<String, Path> moduleNameToPath = new HashMap<>();
  private Set<String> classesToLoad;
  private List<String> modulePath;
  private int next = 0;

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
  public ClassSource getClassSource(String className, Optional<String> moduleName) {
    String appendToPath = moduleName.isPresent() ? moduleName.get() + ":" : "";

    {
      Set<String> classesToLoad = this.classesToLoad;
      if (classesToLoad == null) {
        classesToLoad = new HashSet<>(ModuleScene.v().getBasicClasses());
        for (SootClass c : ModuleScene.v().getApplicationClasses()) {
          classesToLoad.add(c.getName());
        }
        this.classesToLoad = classesToLoad;
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

    for (StringTokenizer tokenizer = new StringTokenizer(classPath, File.pathSeparator); tokenizer.hasMoreTokens();) {
      String originalDir = tokenizer.nextToken();
      try {
        String canonicalDir = new File(originalDir).getCanonicalPath();
        if (DUMMY_CLASSPATH_JDK9_FS.equals(originalDir)) {
          canonicalDir = "jrt:/";
        }
        ret.add(canonicalDir);
      } catch (IOException e) {
        throw new CompilationDeathException("Couldn't resolve classpath entry " + originalDir + ": " + e);
      }
    }
    return ret;
  }

  @Override
  public void additionalClassLoader(ClassLoader c) {
    additionalClassLoaders.add(c);
  }

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
    List<String> sourcePath = this.sourcePath;
    if (sourcePath == null) {
      sourcePath = new ArrayList<>();
      for (String dir : modulePath) {
        ClassSourceType cst = getClassSourceType(dir);
        if (cst != ClassSourceType.apk && cst != ClassSourceType.jar && cst != ClassSourceType.zip) {
          sourcePath.add(dir);
        }
      }
      this.sourcePath = sourcePath;
    }
    return sourcePath;
  }

  /**
   * For backward compatibility returns classes in the form of module:classname
   *
   * @param aPath
   *          where to search for classes
   * @return a String list containing entries of the form module:classname
   */
  @Override
  public List<String> getClassesUnder(String aPath) {
    List<String> classes = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : getClassUnderModulePath(aPath).entrySet()) {
      for (String className : entry.getValue()) {
        classes.add(entry.getKey() + ':' + className);
      }
    }
    return classes;
  }

  /**
   * Scan the given module path entry. If the entry is a directory then it is a directory of modules or an exploded module.
   * If the entry is a regular file then it is assumed to be a packaged module.
   */
  public Map<String, List<String>> getClassUnderModulePath(String aPath) {
    Path path = null;
    switch (getClassSourceType(aPath)) {
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
        path = getRootModulesPathOfJDK();
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
      logger.debug(e.getMessage(), e);
    }
    assert (attrs != null);
    Map<String, List<String>> mapModuleClasses = new HashMap<>();
    if (attrs.isDirectory()) {
      if (!Files.exists(path.resolve(SootModuleInfo.MODULE_INFO_FILE))) {
        // assume a directory of modules
        mapModuleClasses.putAll(discoverModulesIn(path));
      } else {
        // found an exploded module
        mapModuleClasses.putAll(buildModuleForExplodedModule(path));
      }
    } else if (attrs.isRegularFile() && path.getFileName().toString().endsWith(".jar")) {
      // found a jar that is either a modular jar or a simple jar that must be transformed to an automatic module
      mapModuleClasses.putAll(buildModuleForJar(path));
    }
    return mapModuleClasses;
  }

  public static Path getRootModulesPathOfJDK() {
    Path p = Paths.get(URI.create("jrt:/"));
    if (p.endsWith("modules")) {
      return p;
    }
    // Due to a bug in some JDKs, p not necessarily points to modules directly:
    // https://bugs.openjdk.java.net/browse/JDK-8227076
    return p.resolve("modules");
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
          if (Files.exists(entry.resolve(SootModuleInfo.MODULE_INFO_FILE))) {
            mapModuleClasses.putAll(buildModuleForExplodedModule(entry));
          }
        } else if (attrs.isRegularFile() && entry.getFileName().toString().endsWith(".jar")) {
          mapModuleClasses.putAll(buildModuleForJar(entry));
        }

      }
    } catch (IOException e) {
      logger.debug(e.getMessage(), e);
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
            SootModuleInfo moduleInfo =
                (SootModuleInfo) SootModuleResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
            this.moduleNameToPath.put(moduleName, jar);
            List<String> classesInJar = super.getClassesUnder(jar.toAbsolutePath().toString());
            for (String foundClass : classesInJar) {
              int index = foundClass.lastIndexOf('.');
              if (index > 0) {
                moduleInfo.addModulePackage(foundClass.substring(0, index));
              }
            }
            moduleClassMap.put(moduleName, classesInJar);
          }
        }
      } else {
        // no module-info treat as automatic module
        // create module name from jar
        //
        // make module base on the filname of the jar
        SootModuleInfo moduleInfo;
        String moduleName = createModuleNameForAutomaticModule(jar.getFileName().toString());
        if (!ModuleScene.v().containsClass(SootModuleInfo.MODULE_INFO, Optional.of(moduleName))) {
          moduleInfo = new SootModuleInfo(SootModuleInfo.MODULE_INFO, moduleName, true);
          Scene.v().addClass(moduleInfo);
          moduleInfo.setApplicationClass();
        } else {
          moduleInfo = (SootModuleInfo) ModuleScene.v().getSootClass(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
          if (moduleInfo.resolvingLevel() != SootClass.DANGLING) {
            return moduleClassMap;
          }
        }

        // collect the packages in this jar and add them to the exported
        List<String> classesInJar = super.getClassesUnder(jar.toAbsolutePath().toString());
        for (String foundClass : classesInJar) {
          int index = foundClass.lastIndexOf('.');
          if (index > 0) {
            moduleInfo.addModulePackage(foundClass.substring(0, index));
          }
        }
        moduleInfo.setResolvingLevel(SootClass.BODIES);
        moduleInfo.setAutomaticModule(true);
        this.moduleNameToPath.put(moduleName, jar);
        moduleClassMap.put(moduleName, classesInJar);
      }

    } catch (IOException e) {
      logger.debug(e.getMessage(), e);
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
    int i = filename.lastIndexOf(File.separatorChar);
    if (i != -1) {
      filename = filename.substring(i + 1);
    }

    // drop the file extension .jar
    String moduleName = filename.substring(0, filename.length() - 4);

    // find first occurrence of -${NUMBER}. or -${NUMBER}$
    // according to the java 9 spec and current implementation, version numbers are ignored when naming automatic modules
    Matcher matcher = Pattern.compile("-(\\d+(\\.|$))").matcher(moduleName);
    if (matcher.find()) {
      moduleName = moduleName.substring(0, matcher.start());
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
        String moduleName = ((AsmModuleClassProvider) cp).getModuleName(new FoundFile(mi));
        SootModuleInfo moduleInfo =
            (SootModuleInfo) SootModuleResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
        this.moduleNameToPath.put(moduleName, dir);

        List<String> classes = getClassesUnderDirectory(dir);
        for (String foundClass : classes) {
          int index = foundClass.lastIndexOf('.');
          if (index > 0) {
            moduleInfo.addModulePackage(foundClass.substring(0, index));
          }
        }

        moduleClassesMap.put(moduleName, classes);
      }
    }
    return moduleClassesMap;
  }

  /* This is called after sootClassPath has been defined. */
  @Override
  public Set<String> classesInDynamicPackage(String str) {
    HashSet<String> set = new HashSet<>(0);
    StringTokenizer strtok = new StringTokenizer(ModuleScene.v().getSootModulePath(), File.pathSeparator);
    while (strtok.hasMoreTokens()) {
      String path = strtok.nextToken();

      // For jimple files
      for (String filename : super.getClassesUnder(path)) {
        if (filename.startsWith(str)) {
          set.add(filename);
        }
      }

      // For class files
      StringBuilder sb = new StringBuilder(path);
      sb.append(File.pathSeparatorChar);
      for (StringTokenizer tok = new StringTokenizer(str, "."); tok.hasMoreTokens();) {
        sb.append(tok.nextToken());
        if (tok.hasMoreTokens()) {
          sb.append(File.pathSeparatorChar);
        }
      }
      for (String string : super.getClassesUnder(sb.toString())) {
        set.add(str + '.' + string);
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
    if (path.toUri().toString().startsWith("jrt:/")) {
      return ClassSourceType.jrt;
    } else {
      return super.getClassSourceType(path.toAbsolutePath().toString());
    }
  }

  @Override
  protected ClassSourceType getClassSourceType(String path) {
    if (path.startsWith("jrt:/")) {
      return ClassSourceType.jrt;
    } else {
      return super.getClassSourceType(path);
    }
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
    if (foundModulePath == null) {
      return null;
    }
    // transform the path to a String to reuse the
    String uriString = foundModulePath.toUri().toString();
    String dir = uriString.startsWith("jrt:/") ? uriString : foundModulePath.toAbsolutePath().toString();

    ClassSourceType cst = getClassSourceType(foundModulePath);
    if (null != cst) {
      switch (cst) {
        case zip:
        case jar:
          return lookupInArchive(dir, className);
        case directory:
          return lookupInDir(dir, className);
        case jrt:
          return lookUpInVirtualFileSystem(dir, className);
      }
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
      getClassUnderModulePath(modulePath.get(next));
      next++;
      pathToModule = moduleNameToPath.get(moduleName);
      if (pathToModule != null) {
        return pathToModule;
      }
    }
    return null;
  }

  private FoundFile lookupInDir(String dir, String fileName) {
    Path foundFile = Paths.get(dir).resolve(fileName);
    if (foundFile != null && Files.isRegularFile(foundFile)) {
      return new FoundFile(foundFile);
    } else {
      return null;
    }
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
  @Override
  protected FoundFile lookupInArchive(String archivePath, String fileName) {
    Path archive = Paths.get(archivePath);
    try (FileSystem zipFileSystem = FileSystems.newFileSystem(archive, this.getClass().getClassLoader())) {
      Path entry = zipFileSystem.getPath(fileName);
      if (entry == null || !Files.isRegularFile(entry)) {
        return null;
      } else {
        return new FoundFile(archive.toAbsolutePath().toString(), fileName);
      }
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
    } else {
      return null;
    }
  }

  @Override
  protected void setupClassProviders() {
    classProviders = new LinkedList<>();
    classProviders.add(new AsmModuleClassProvider());
  }

  /**
   * Replaces super.getClassesUnder in order to deal with the virtual filesystem jrt
   *
   * @param aPath
   *          the directory
   * @return List of found classes
   */
  private List<String> getClassesUnderDirectory(Path aPath) {
    ClassSourceType cst = getClassSourceType(aPath);
    if (cst != ClassSourceType.directory && cst != ClassSourceType.jrt) {
      throw new RuntimeException("Invalid class source type");
    }

    List<String> classes = new ArrayList<>();
    FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        String fileName = aPath.relativize(file).toString().replace(File.separatorChar, '.');
        if (fileName.endsWith(".class")) {
          classes.add(fileName.substring(0, fileName.lastIndexOf(".class")));
        } else if (fileName.endsWith(".jimple")) {
          classes.add(fileName.substring(0, fileName.lastIndexOf(".jimple")));
        } else if (fileName.endsWith(".java")) {
          classes.add(fileName.substring(0, fileName.lastIndexOf(".java")));
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
      logger.debug(e.getMessage(), e);
    }

    return classes;
  }
}
