/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot;

import com.google.common.base.Optional;
import soot.options.Options;
import soot.util.Chain;
import soot.util.HashChain;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;


/**
 * Manages the SootClasses of the application being analyzed for Java 9 modules
 *
 * @author adann
 */
public class ModuleScene extends Scene  //extends original Scene
{


    public ModuleScene(Singletons.Global g) {
        super(g);
        String smp = System.getProperty("soot.module.path");

        if (smp != null)
            setSootModulePath(smp);
    }


    public static ModuleScene v() {
        return G.v().soot_ModuleScene();
    }


    /* holds the references to SootClass
     * 1: String the class name
     * 2: Map<String, RefType>: The String represents the module that holds the corresponding RefType
     * since multiple modules may contain the same class this is a map (for fast look ups)
     * TODO: evaluate if Guava's multimap is faster
     */
    private final Map<String, Map<String, RefType>> nameToClass = new HashMap<String, Map<String, RefType>>();


    //instead of using a class path, java 9 uses a module path
    private String modulePath = null;


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
    public void setSootClassPath(String p) {
        this.setSootModulePath(p);
    }

    public void setSootModulePath(String p) {
        ModulePathSourceLocator.v().invalidateClassPath();
        modulePath = p;

    }

    @Override
    public String getSootClassPath() {
        return getSootModulePath();
    }

    public String getSootModulePath() {
        if (modulePath == null) {
            String optionsmp = Options.v().soot_modulepath();
            if (optionsmp != null && optionsmp.length() > 0)
                modulePath = optionsmp;

            //if no classpath is given on the command line, take the default
            if (modulePath == null) {
                modulePath = defaultJavaModulePath();
            } else {
                //if one is given...
                if (Options.v().prepend_classpath()) {
                    //if the prepend flag is set, append the default classpath
                    modulePath += File.pathSeparator + defaultJavaModulePath();
                }
                //else, leave it as it is
            }

            //add process-dirs
            List<String> process_dir = Options.v().process_dir();
            StringBuilder pds = new StringBuilder();
            for (String path : process_dir) {
                if (!modulePath.contains(path)) {
                    pds.append(path);
                    pds.append(File.pathSeparator);
                }
            }
            modulePath = pds + modulePath;
        }

        return modulePath;
    }


    private String defaultJavaModulePath() {
        StringBuilder sb = new StringBuilder();

        // test for java 9
        File rtJar = Paths.get(System.getProperty("java.home"), "lib", "jrt-fs.jar").toFile();
        if (rtJar.exists() && rtJar.isFile())
            sb.append(ModulePathSourceLocator.DUMMY_CLASSPATH_JDK9_FS);

        else {
            throw new RuntimeException("Error: cannot find rt.jar.");
        }
        return sb.toString();
    }


    @Override
    protected void addClassSilent(SootClass c) {
        if (c.isInScene())
            throw new RuntimeException("already managed: " + c.getName());

        if (containsClass(c.getName()))
            throw new RuntimeException("duplicate class: " + c.getName());

        classes.add(c);


        if (!nameToClass.containsKey(c.getName())) {
            nameToClass.put(c.getName(), new HashMap<String, RefType>());
        }
        Map<String, RefType> map = nameToClass.get(c.getName());
        map.put(c.moduleName, c.getType());


        c.getType().setSootClass(c);
        c.setInScene(true);

        // Phantom classes are not really part of the hierarchy anyway, so
        // we can keep the old one
        if (!c.isPhantom)
            modifyHierarchy();

    }


    public boolean containsClass(String className) {
        ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(className);

        return containsClass(wrapper.getClassName(), wrapper.getModuleNameOptional());
    }


    public boolean containsClass(String className, Optional<String> moduleName) {

        RefType type = null;
        Map<String, RefType> map = nameToClass.get(className);
        if (map != null && !map.isEmpty()) {
            if (moduleName.isPresent()) {
                String module = ModuleUtil.v().findModuleThatExports(className, moduleName.get());

                type = map.get(module);
            }
            //return first element
            else {
                type = map.values().iterator().next();
                if (ModuleUtil.module_mode() && Options.v().verbose()) {
                    G.v().out.println("[Warning] containsClass called with empty module for: " + className);
                }
            }
        }

        if (type == null) return false;
        if (!type.hasSootClass()) return false;
        SootClass c = type.getSootClass();
        return c.isInScene();
    }


    public boolean containsType(String className) {
        return nameToClass.containsKey(className);
    }


    /**
     * Attempts to load the given class and all of the required support classes.
     * Returns the original class if it was loaded, or null otherwise.
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
            if (source != null)
                source.close();
        }
        SootModuleResolver resolver = SootModuleResolver.v();
        SootClass toReturn = resolver.resolveClass(className, desiredLevel, moduleName);
        setPhantomRefs(false);

        return toReturn;

    }


    /**
     * Loads the given class and all of the required support classes.  Returns the first class.
     */

    public SootClass loadClassAndSupport(String className) {

        ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(className);


        return loadClassAndSupport(wrapper.getClassName(), wrapper.getModuleNameOptional());
    }

    public SootClass loadClassAndSupport(String className, Optional<String> moduleName) {
        SootClass ret = loadClass(className, SootClass.SIGNATURES, moduleName);
        if (!ret.isPhantom()) ret = loadClass(className, SootClass.BODIES, moduleName);
        return ret;
    }


    public SootClass loadClass(String className, int desiredLevel) {

        ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(className);


        return loadClass(wrapper.getClassName(), desiredLevel, wrapper.getModuleNameOptional());
    }

    public SootClass loadClass(String className, int desiredLevel, Optional<String> moduleName) {
        /*
        if(Options.v().time())
            Main.v().resolveTimer.start();
        */

        setPhantomRefs(true);
        SootModuleResolver resolver = SootModuleResolver.v();
        SootClass toReturn = resolver.resolveClass(className, desiredLevel, moduleName);
        setPhantomRefs(false);

        return toReturn;
        
        /*
        if(Options.v().time())
            Main.v().resolveTimer.end(); */
    }


    /**
     * Returns the RefType with the given class name or primitive type.
     *
     * @throws RuntimeException if the Type for this name cannot be found.
     *                          Use {@link #getRefTypeUnsafe(String, Optional)} to check if type is an registered RefType.
     */
    public Type getType(String arg, Optional<String> moduleName) {
        String type = arg.replaceAll("([^\\[\\]]*)(.*)", "$1");
        int arrayCount = arg.contains("[") ? arg.replaceAll("([^\\[\\]]*)(.*)", "$2").length() / 2 : 0;

        Type result = getRefTypeUnsafe(type, moduleName);

        if (result == null) {
            if (type.equals("long"))
                result = LongType.v();
            else if (type.equals("short"))
                result = ShortType.v();
            else if (type.equals("double"))
                result = DoubleType.v();
            else if (type.equals("int"))
                result = IntType.v();
            else if (type.equals("float"))
                result = FloatType.v();
            else if (type.equals("byte"))
                result = ByteType.v();
            else if (type.equals("char"))
                result = CharType.v();
            else if (type.equals("void"))
                result = VoidType.v();
            else if (type.equals("boolean"))
                result = BooleanType.v();
            else
                throw new RuntimeException("unknown type: '" + type + "'");
        }

        if (arrayCount != 0) {
            result = ArrayType.v(result, arrayCount);
        }
        return result;
    }

    /**
     * Returns the RefType with the given className.
     *
     * @throws IllegalStateException if the RefType for this class cannot be found.
     *                               Use {@link #containsType(String)} to check if type is registered
     */
    public RefType getRefType(String className, Optional<String> moduleName) {
        if (!moduleName.isPresent() && className.startsWith("java.lang"))
            moduleName = Optional.of("java.base");
        RefType refType = getRefTypeUnsafe(className, moduleName);
        if (refType == null) {
            throw new IllegalStateException("RefType " + className + " not loaded. " +
                    "If you tried to get the RefType of a library class, did you call loadNecessaryClasses()? " +
                    "Otherwise please check Soot's classpath.");
        }
        return refType;
    }

    @Override
    public RefType getRefType(String className) {

        ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(className);

        return getRefType(wrapper.getClassName(), wrapper.getModuleNameOptional());
    }


    @Override
    public RefType getObjectType() {
        return getRefType("java.lang.Object", Optional.of("java.base"));
    }

    /**
     * Returns the RefType with the given className. Returns null if no type
     * with the given name can be found.
     */

    public RefType getRefTypeUnsafe(String className, Optional<String> moduleName) {
        // RefType refType = nameToClass.get(className);
        RefType refType = null;
        Map<String, RefType> map = nameToClass.get(className);
        if (map != null && !map.isEmpty()) {
            if (moduleName.isPresent()) {
                refType = map.get(moduleName.get());
            }
            //return first element
            else {
                refType = map.values().iterator().next();
                if (Options.v().verbose() && ModuleUtil.module_mode()) {
                    G.v().out.println("[Warning] getRefTypeUnsafe called with empty module for: " + className);
                }
            }
        }


        return refType;
    }


    @Override
    public void addRefType(RefType type) {

        if (!nameToClass.containsKey(type.getClassName())) {
            nameToClass.put(type.getClassName(), new HashMap<String, RefType>());
        }
        Map<String, RefType> map = nameToClass.get(type.getClassName());
        map.put(((ModuleRefType) type).getModuleName(), type);

    }

    @Override
    public SootClass getSootClassUnsafe(String className) {

        ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(className);

        return getSootClassUnsafe(wrapper.getClassName(), wrapper.getModuleNameOptional());
    }


    public SootClass getSootClassUnsafe(String className, Optional<String> moduleName) {
        RefType type = null;
        Map<String, RefType> map = nameToClass.get(className);

        String module = "";
        if (map != null && !map.isEmpty()) {
            if (moduleName.isPresent()) {
                module = ModuleUtil.v().findModuleThatExports(className, moduleName.get());
                type = map.get(module);
            }
            //return first element
            else {
                type = map.values().iterator().next();
                if (Options.v().verbose() && ModuleUtil.module_mode()) {
                    G.v().out.println("[Warning] getSootClassUnsafe called with empty for: " + className);
                }
            }
        }

        if (type != null) {
            SootClass tsc = type.getSootClass();
            if (tsc != null)
                return tsc;
        }

        if (allowsPhantomRefs() ||
                className.equals(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME)) {
            SootClass c = new SootClass(className);
            addClassSilent(c);
            c.setPhantomClass();
            return c;
        }

        return null;
    }


    @Override
    public SootClass getSootClass(String className) {

        ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(className);

        return getSootClass(wrapper.getClassName(), wrapper.getModuleNameOptional());
    }


    public SootClass getSootClass(String className, Optional<String> moduleName) {
        SootClass sc = getSootClassUnsafe(className, moduleName);
        if (sc != null)
            return sc;

        throw new RuntimeException(System.getProperty("line.separator")
                + "Aborting: can't find classfile " + className);
    }


    private final Set<String>[] basicclasses = new Set[4];

    private void addSootBasicClasses() {
        basicclasses[SootClass.HIERARCHY] = new HashSet<String>();
        basicclasses[SootClass.SIGNATURES] = new HashSet<String>();
        basicclasses[SootClass.BODIES] = new HashSet<String>();

        addBasicClass("java.lang.Object");
        addBasicClass("java.lang.Class", SootClass.SIGNATURES);

        addBasicClass("java.lang.Void", SootClass.SIGNATURES);
        addBasicClass("java.lang.Boolean", SootClass.SIGNATURES);
        addBasicClass("java.lang.Byte", SootClass.SIGNATURES);
        addBasicClass("java.lang.Character", SootClass.SIGNATURES);
        addBasicClass("java.lang.Short", SootClass.SIGNATURES);
        addBasicClass("java.lang.Integer", SootClass.SIGNATURES);
        addBasicClass("java.lang.Long", SootClass.SIGNATURES);
        addBasicClass("java.lang.Float", SootClass.SIGNATURES);
        addBasicClass("java.lang.Double", SootClass.SIGNATURES);

        addBasicClass("java.lang.String");
        addBasicClass("java.lang.StringBuffer", SootClass.SIGNATURES);

        addBasicClass("java.lang.Error");
        addBasicClass("java.lang.AssertionError", SootClass.SIGNATURES);
        addBasicClass("java.lang.Throwable", SootClass.SIGNATURES);
        addBasicClass("java.lang.NoClassDefFoundError", SootClass.SIGNATURES);
        addBasicClass("java.lang.ExceptionInInitializerError");
        addBasicClass("java.lang.RuntimeException");
        addBasicClass("java.lang.ClassNotFoundException");
        addBasicClass("java.lang.ArithmeticException");
        addBasicClass("java.lang.ArrayStoreException");
        addBasicClass("java.lang.ClassCastException");
        addBasicClass("java.lang.IllegalMonitorStateException");
        addBasicClass("java.lang.IndexOutOfBoundsException");
        addBasicClass("java.lang.ArrayIndexOutOfBoundsException");
        addBasicClass("java.lang.NegativeArraySizeException");
        addBasicClass("java.lang.NullPointerException", SootClass.SIGNATURES);
        addBasicClass("java.lang.InstantiationError");
        addBasicClass("java.lang.InternalError");
        addBasicClass("java.lang.OutOfMemoryError");
        addBasicClass("java.lang.StackOverflowError");
        addBasicClass("java.lang.UnknownError");
        addBasicClass("java.lang.ThreadDeath");
        addBasicClass("java.lang.ClassCircularityError");
        addBasicClass("java.lang.ClassFormatError");
        addBasicClass("java.lang.IllegalAccessError");
        addBasicClass("java.lang.IncompatibleClassChangeError");
        addBasicClass("java.lang.LinkageError");
        addBasicClass("java.lang.VerifyError");
        addBasicClass("java.lang.NoSuchFieldError");
        addBasicClass("java.lang.AbstractMethodError");
        addBasicClass("java.lang.NoSuchMethodError");
        addBasicClass("java.lang.UnsatisfiedLinkError");

        addBasicClass("java.lang.Thread");
        addBasicClass("java.lang.Runnable");
        addBasicClass("java.lang.Cloneable");

        addBasicClass("java.io.Serializable");

        addBasicClass("java.lang.ref.Finalizer");

        addBasicClass("java.lang.invoke.LambdaMetafactory");

        // this is a new Class in JAVA 9; that shall be added to Soot Basic Classes in loadNecessaryClasses mehtod
        addBasicClass("java.lang.invoke.StringConcatFactory");
    }


    @Override
    public void loadBasicClasses() {
        super.addReflectionTraceClasses();

        for (int i = SootClass.BODIES; i >= SootClass.HIERARCHY; i--) {
            for (String name : basicclasses[i]) {
                ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(name);

                tryLoadClass(wrapper.getClassName(), i, wrapper.getModuleNameOptional());
            }
        }
    }


    //FIXME check where called
    private void loadNecessaryClass(String name) {
        SootClass c;
        c = loadClassAndSupport(name);
        c.setApplicationClass();
    }

    /**
     * Load the set of classes that soot needs, including those specified on the
     * command-line. This is the standard way of initialising the list of
     * classes soot should use.
     */
    public void loadNecessaryClasses() {
        loadBasicClasses();

        for (String name : Options.v().classes()) {
            loadNecessaryClass(name);
        }

        loadDynamicClasses();

        if (Options.v().oaat()) {
            if (Options.v().process_dir().isEmpty()) {
                throw new IllegalArgumentException("If switch -oaat is used, then also -process-dir must be given.");
            }
        } else {
            for (final String path : Options.v().process_dir()) {
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


    private List<SootClass> dynamicClasses = null;

    public void loadDynamicClasses() {
        dynamicClasses = new ArrayList<SootClass>();
        Map<String, List<String>> dynClasses = new HashMap<>();
        dynClasses.put(null, Options.v().dynamic_class());

        for (Iterator<String> pathIt = Options.v().dynamic_dir().iterator(); pathIt.hasNext(); ) {

            final String path = pathIt.next();
            dynClasses.putAll(ModulePathSourceLocator.v().getClassUnderModulePath(path));
        }

        for (Iterator<String> pkgIt = Options.v().dynamic_package().iterator(); pkgIt.hasNext(); ) {

            final String pkg = pkgIt.next();
            List<String> classPathClasses = dynClasses.get(null);
            classPathClasses.addAll(SourceLocator.v().classesInDynamicPackage(pkg));
        }

        for (Map.Entry<String, List<String>> entry : dynClasses.entrySet()) {
            for (String className : entry.getValue()) {
                dynamicClasses.add(loadClassAndSupport(className, Optional.fromNullable(entry.getKey())));
            }
        }

        //remove non-concrete classes that may accidentally have been loaded
        for (Iterator<SootClass> iterator = dynamicClasses.iterator(); iterator.hasNext(); ) {
            SootClass c = iterator.next();
            if (!c.isConcrete()) {
                if (Options.v().verbose()) {
                    G.v().out.println("Warning: dynamic class " + c.getName() + " is abstract or an interface, and it will not be considered.");
                }
                iterator.remove();
            }
        }
    }


    /* Generate classes to process, adding or removing package marked by
     * command line options. 
     */
    private void prepareClasses() {
        // Remove/add all classes from packageInclusionMask as per -i option
        Chain<SootClass> processedClasses = new HashChain<SootClass>();
        while (true) {
            Chain<SootClass> unprocessedClasses = new HashChain<SootClass>(getClasses());
            unprocessedClasses.removeAll(processedClasses);
            if (unprocessedClasses.isEmpty()) break;
            processedClasses.addAll(unprocessedClasses);
            for (SootClass s : unprocessedClasses) {
                if (s.isPhantom()) continue;
                if (Options.v().app()) {
                    s.setApplicationClass();
                }
                if (Options.v().classes().contains(s.getName())) {
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


    public void setMainClassFromOptions() {
        if (mainClass != null) return;
        if (Options.v().main_class() != null
                && Options.v().main_class().length() > 0) {
            setMainClass(getSootClass(Options.v().main_class(), null));
        } else {
            // try to infer a main class from the command line if none is given
            for (Iterator<String> classIter = Options.v().classes().iterator(); classIter.hasNext(); ) {
                SootClass c = getSootClass(classIter.next(), null);
                if (c.declaresMethod("main", Collections.singletonList(ArrayType.v(ModuleRefType.v("java.lang.String", Optional.fromNullable("java.base")), 1)), VoidType.v())) {
                    G.v().out.println("No main class given. Inferred '" + c.getName() + "' as main class.");
                    setMainClass(c);
                    return;
                }
            }

            // try to infer a main class from the usual classpath if none is given
            for (Iterator<SootClass> classIter = getApplicationClasses().iterator(); classIter.hasNext(); ) {
                SootClass c = classIter.next();
                if (c.declaresMethod("main", Collections.singletonList(ArrayType.v(ModuleRefType.v("java.lang.String", Optional.fromNullable("java.base")), 1)), VoidType.v())) {
                    G.v().out.println("No main class given. Inferred '" + c.getName() + "' as main class.");
                    setMainClass(c);
                    return;
                }
            }
        }
    }


    @Override
    public SootClass forceResolve(String className, int level) {

        ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(className);


        return forceResolve(wrapper.getClassName(), level, wrapper.getModuleNameOptional());
    }

    public SootClass forceResolve(String className, int level, Optional<String> moduleName) {
        boolean tmp = super.doneResolving();
        super.setResolving(false);
        SootClass c;
        try {
            c = SootModuleResolver.v().resolveClass(className, level, moduleName);
        } finally {
            super.setResolving(tmp);
        }
        return c;
    }

    @Override
    public SootClass makeSootClass(String className) {

        ModuleUtil.ModuleClassNameWrapper wrapper = new ModuleUtil.ModuleClassNameWrapper(className);

        return makeSootClass(wrapper.getClassName(), wrapper.getModuleName());
    }

    public SootClass makeSootClass(String name, String moduleName) {
        return new SootClass(name, moduleName);
    }

    public RefType getOrAddRefType(RefType tp) {
        RefType existing = getRefType(tp.getClassName(), Optional.fromNullable(((ModuleRefType) tp).getModuleName()));
        if (existing != null)
            return existing;
        this.addRefType(tp);
        return tp;
    }

}