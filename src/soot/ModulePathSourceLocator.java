/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot;

import com.google.common.base.Optional;
import soot.JavaClassProvider.JarException;
import soot.asm.AsmClassProvider;
import soot.asm.AsmModuleClassProvider;
import soot.options.Options;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utility methods to retrieve an input stream for a class name, given
 * a classfile, or jimple or baf output files.
 */
public class ModulePathSourceLocator extends SourceLocator {

    public static final String DUMMY_CLASSPATH_JDK9_FS = "VIRTUAL_FS_FOR_JDK9";

    protected List<String> sourcePath;
    /**
     * The index that maps classes to the files they are defined in.
     * This is necessary because a dex file can hold multiple classes.
     */
    private Map<String, File> dexClassIndex;
    protected Set<String> classesToLoad;


    public ModulePathSourceLocator(Singletons.Global g) {
        super(g);
    }


    public static ModulePathSourceLocator v() {
        return G.v().soot_ModulePathSourceLocator();
    }


    @Override
    public ClassSource getClassSource(String className) {
        return getClassSource(className, Optional.fromNullable(null));
    }

    /**
     * Given a class name, uses the soot-module-path to return a ClassSource for the given class.
     */
    public ClassSource getClassSource(String className, Optional<String> moduleName) {
        String appendToPath = "";
        if (moduleName.isPresent()) {
            appendToPath = moduleName.get() + ":";
        }
        if (classesToLoad == null) {
            classesToLoad = new HashSet<String>();
            classesToLoad.addAll(ModuleScene.v().getBasicClasses());
            for (SootClass c : ModuleScene.v().getApplicationClasses()) {
                classesToLoad.add(c.getName());
            }
        }

        if (modulePath == null) {
            modulePath = explodeClassPath(ModuleScene.v().getSootModulePath());
        }

        JarException ex = null;
        for (ClassProvider cp : classProviders) {
            try {
                ClassSource ret = cp.find(appendToPath + className);
                if (ret != null) return ret;
            } catch (JarException e) {
                ex = e;
            }
        }
        if (ex != null) throw ex;

        return null;
    }

    public void additionalClassLoader(ClassLoader c) {
        additionalClassLoaders.add(c);
    }

    private List<String> modulePath;

    @Override
    public List<String> classPath() {
        return modulePath;
    }

    @Override
    public void invalidateClassPath() {
        modulePath = null;
        dexClassIndex = null;
    }

    @Override
    public List<String> sourcePath() {
        if (sourcePath == null) {
            sourcePath = new ArrayList<String>();
            for (String dir : modulePath) {
                ClassSourceType cst = getClassSourceType(dir);
                if (cst != ClassSourceType.apk
                        && cst != ClassSourceType.jar
                        && cst != ClassSourceType.zip)
                    sourcePath.add(dir);
            }
        }
        return sourcePath;
    }


    private HashMap<String, Path> moduleNameToPath = new HashMap<>();

    @Override
    /**
     * for backward compability returns classes in the form of
     * module:classname
     */
    //FIXME: check for caller, to handle appropiate when this name style is loaded
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
     * Scan the given module path entry. If the entry is a directory then it is
     * a directory of modules or an exploded module. If the entry is a regular
     * file then it is assumed to be a packaged module.
     */
    public Map<String, List<String>> getClassUnderModulePath(String aPath) {
        Map<String, List<String>> mapModuleClasses = new HashMap<>();
        Path path = null;
        ClassSourceType type = super.getClassSourceType(aPath);
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
                mapModuleClasses.putAll(findModulesInDir(path));
            } else {
                //we have an exploded module
                mapModuleClasses.putAll(buildModuleForExplodedModule(path));
            }
        }
        //we found a jar (either it is a modular jar must be transformed to an automatic module
        else if (attrs.isRegularFile() && path.getFileName().toString().endsWith(".jar")) {
            buildModuleForJar(path);
        }
        return mapModuleClasses;

    }

    /**
     * currently only one level of hierarchy is traversed
     *
     * @param path
     * @return
     */
    private Map<String, List<String>> findModulesInDir(Path path) {
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


    private Map<String, List<String>> buildModuleForJar(Path jar) {
        Map<String, List<String>> moduleClassMape = new HashMap<>();

        try (FileSystem zipFileSystem = FileSystems.newFileSystem(jar, null);) {
            Path mi = zipFileSystem.getPath(SootModuleInfo.MODULE_INFO_FILE);
            if (Files.exists(mi)) {
                //we hava a modular jar
                try (InputStream in = Files.newInputStream(mi)) {
                    for (ClassProvider cp : classProviders) {
                        if (cp instanceof AsmModuleClassProvider) {
                            String moduleName = ((AsmModuleClassProvider) cp).getModuleName(in);
                            SootModuleInfo moduleInfo = (SootModuleInfo) SootModuleResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
                            this.moduleNameToPath.put(moduleName, jar);
                            List<String> classesInJar = super.getClassesUnder(jar.toAbsolutePath().toString());
                            for (String foundClass : classesInJar) {
                                int index = foundClass.lastIndexOf('.');
                                if (index > 0) {
                                    String packageName = foundClass.substring(0, index);
                                    moduleInfo.addModulePackage(packageName);
                                }
                            }
                            moduleClassMape.put(moduleName, classesInJar);

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //no module-info treat as automatic module
                //create module name from jar
                String filename = jar.getFileName().toString();

                //cut of the file extension
                String moduleName = createModuleNameForAutomaticModule(filename);
                if (!ModuleScene.v().containsClass(SootModuleInfo.MODULE_INFO, Optional.of(moduleName))) {
                    SootModuleInfo moduleInfo = (SootModuleInfo)
                            new SootModuleInfo(SootModuleInfo.MODULE_INFO, moduleName, true);
                    Scene.v().addClass(moduleInfo);
                    moduleInfo.setApplicationClass();

                    //collect the packages in this jar and add them to the exported
                    List<String> classesInJar = super.getClassesUnder(jar.toAbsolutePath().toString());
                    for (String foundClass : classesInJar) {
                        int index = foundClass.lastIndexOf('.');
                        if (index > 0) {
                            String packageName = foundClass.substring(0, index);
                            moduleInfo.addModulePackage(packageName);
                        }
                    }
                    //moduleInfo.setResolvingLevel(SootClass.BODIES);
                    //moduleInfo.setPhantom(true);
                    //moduleInfo.setPhantomClass();
                    this.moduleNameToPath.put(moduleName, jar);
                    moduleClassMape.put(moduleName, classesInJar);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return moduleClassMape;
    }

    //this is similar to the jdk parsing of module name
    private String createModuleNameForAutomaticModule(String filename) {
        int i = filename.lastIndexOf(File.separator);
        if (i != -1)
            filename = filename.substring(i + 1);

        // drop .jar
        String mn = filename.substring(0, filename.length() - 4);
        String vs = null;

        // find first occurrence of -${NUMBER}. or -${NUMBER}$
        Matcher matcher = Pattern.compile("-(\\d+(\\.|$))").matcher(mn);
        if (matcher.find()) {
            int start = matcher.start();


            mn = mn.substring(0, start);
        }
        mn = Pattern.compile("[^A-Za-z0-9]").matcher(mn).replaceAll(".");

        // collapse repeating dots
        mn = Pattern.compile("(\\.)(\\1)+").matcher(mn).replaceAll(".");

        // drop leading dots
        if (mn.length() > 0 && mn.charAt(0) == '.')
            mn = Pattern.compile("^\\.").matcher(mn).replaceAll("");

        // drop trailing dots
        int len = mn.length();
        if (len > 0 && mn.charAt(len - 1) == '.')
            mn = Pattern.compile("\\.$").matcher(mn).replaceAll("");


        return mn;
    }

    private Map<String, List<String>> buildModuleForExplodedModule(Path dir) {
        Map<String, List<String>> moduleClassesMap = new HashMap<>();
        Path mi = dir.resolve(SootModuleInfo.MODULE_INFO_FILE);
        try (InputStream in = Files.newInputStream(mi)) {
            for (ClassProvider cp : classProviders) {
                if (cp instanceof AsmModuleClassProvider) {
                    String moduleName = ((AsmModuleClassProvider) cp).getModuleName(in);
                    SootModuleInfo moduleInfo = (SootModuleInfo) SootModuleResolver.v().makeClassRef(SootModuleInfo.MODULE_INFO, Optional.of(moduleName));
                    this.moduleNameToPath.put(moduleName, dir);
                    //FIXME make this nice later
                 /*   String path = dir.toAbsolutePath().toString();
                    if (dir.toUri().toString().startsWith("jrt:/")) {
                        path = dir.toUri().toString();
                    } */
                    //FIXME: does not work, with virtual fileSystem
                    List<String> classes = getClassesUnderDirectory(dir);
                    for (String foundClass : classes) {
                        int index = foundClass.lastIndexOf('.');
                        if (index > 0) {

                            String packageName = foundClass.substring(0, index);
                            moduleInfo.addModulePackage(packageName);
                        }
                    }

                    moduleClassesMap.put(moduleName, classes);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return moduleClassesMap;
    }


    /* This is called after sootClassPath has been defined. */
    @Override
    public Set<String> classesInDynamicPackage(String str) {
        HashSet<String> set = new HashSet<String>(0);
        StringTokenizer strtok = new StringTokenizer(
                ModuleScene.v().getSootModulePath(), String.valueOf(File.pathSeparatorChar));
        while (strtok.hasMoreTokens()) {
            String path = strtok.nextToken();

            // For jimple files
            List<String> l = super.getClassesUnder(path);
            for (String filename : l) {
                if (filename.startsWith(str))
                    set.add(filename);
            }

            // For class files;
            path = path + File.pathSeparatorChar;
            StringTokenizer tokenizer = new StringTokenizer(str, ".");
            while (tokenizer.hasMoreTokens()) {
                path = path + tokenizer.nextToken();
                if (tokenizer.hasMoreTokens())
                    path = path + File.pathSeparatorChar;
            }
            l = super.getClassesUnder(path);
            for (String string : l)
                set.add(str + "." + string);
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

    @Override
    protected ClassSourceType getClassSourceType(String path) {
        if (path.startsWith("jrt:/"))
            return ClassSourceType.jrt;
        return super.getClassSourceType(path);
    }

    public FoundFile lookUpInModulePath(String fileName) {
        String[] moduleAndClassName = fileName.split(":");
        String className = moduleAndClassName[moduleAndClassName.length - 1];
        String moduleName = moduleAndClassName[0];

        if (className.isEmpty() || moduleName.isEmpty()) {
            throw new RuntimeException("No module given!");
        }

        //look if we know where the module is
        Path foundModulePath = findModule(moduleName, modulePath);


        FoundFile ret = null;
        //FIXME make this nice later
        String dir = foundModulePath.toAbsolutePath().toString();
        if (foundModulePath.toUri().toString().startsWith("jrt:/")) {
            dir = foundModulePath.toUri().toString();
        }


        ClassSourceType cst = getClassSourceType(dir);
        if (cst == ClassSourceType.zip || cst == ClassSourceType.jar) {
            ret = lookupInArchive(dir, className);
        } else if (cst == ClassSourceType.directory) {
            ret = lookupInDir(dir, className);
        } else if (cst == ClassSourceType.jrt) {
            ret = lookUpInFS(dir, className);
        }

        if (ret != null)
            return ret;

        return null;
    }


    private Path findModule(String moduleName, List<String> paths) {
        Path modulePath = moduleNameToPath.get(moduleName);
        if (modulePath != null)
            return modulePath;
        //FIXME: do while has nex tlikne modulpath finder in jd
        for (String path : paths) {
            lookUpInModulePath(path);
            modulePath = moduleNameToPath.get(moduleName);
            if (modulePath != null)
                return modulePath;
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

    private FoundFile lookupInArchive(String archivePath, String fileName) {
        Path archive = Paths.get(archivePath);
        try (FileSystem zipFileSystem = FileSystems.newFileSystem(archive, null);) {
            Path entry = zipFileSystem.getPath(fileName);
            if (entry == null || !Files.isRegularFile(entry)) {
                return null;
            }
            return new FoundFile(archive.toAbsolutePath().toString(), fileName);
        } catch (IOException e) {
            throw new RuntimeException("Caught IOException " + e + " looking in archive file " + archivePath + " for file " + fileName);

        }
    }

    private FoundFile lookUpInFS(String archivePath, String fileName) {
        //  FileSystem fs = FileSystems.getFileSystem(URI.create(archivePath));
        Path foundFile = Paths.get(URI.create(archivePath)).resolve(fileName);
        if (foundFile != null && Files.isRegularFile(foundFile)) {
            return new FoundFile(foundFile);
        }

        return null;
    }

    @Override
    public Map<String, File> dexClassIndex() {
        return dexClassIndex;
    }

    @Override
    public void setDexClassIndex(Map<String, File> index) {
        dexClassIndex = index;
    }

    @Override
    protected void setupClassProviders() {
        classProviders = new LinkedList<ClassProvider>();
        ClassProvider classFileClassProvider = new AsmModuleClassProvider();
        classProviders.add(classFileClassProvider);

    }

    /**
     * Replaces super.getClassesUnder in order to deal with the virtual filesystem jrt
     *
     * @param aPath
     * @return
     */
    private List<String> getClassesUnderDirectory(Path aPath) {
        List<String> classes = new ArrayList<String>();
        ClassSourceType cst = getClassSourceType(aPath.toUri().toString());

        if (cst == ClassSourceType.directory || cst == ClassSourceType.jrt) {
            Path path = aPath;
            /*if (cst == ClassSourceType.directory) {
                path = Paths.get(aPath);

            } else if (cst == ClassSourceType.jrt) {
                path = Paths.get(URI.create(aPath));
            }*/

            Path finalPath = path;
            FileVisitor<Path> fileVisitor = new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    String fileName = finalPath.relativize(file).toString().replace(File.separatorChar, '.');

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
                Files.walkFileTree(path, fileVisitor);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else
            throw new

                    RuntimeException("Invalid class source type");
        return classes;
    }
}

