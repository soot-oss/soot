/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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

import soot.util.*;
import soot.xml.*;

import java.util.*;
import soot.jimple.*;
import soot.grimp.*;
import soot.baf.*;
import soot.dava.*;
import soot.dava.toolkits.base.misc.*;

import soot.options.Options;

import java.io.*;

/** Main class for Soot; provides Soot's command-line user interface. */
public class Main {
    public Main(Singletons.Global g) {
    }
    public static Main v() {
        return G.v().Main();
    }
    // TODO: the following string should be updated by the source control
    // No it shouldn't. Prcs is horribly borken in this respect, and causes
    // the code to not compile all the time.
    public final String versionString = "2.0";

    private Date start;
    private Date finish;

    public static final int COMPILATION_ABORTED = 0;
    public static final int COMPILATION_SUCCEEDED = 1;

    public String getExtensionFor(int rep) {
        String str = null;

        switch (rep) {
            case Options.output_format_baf :
                str = ".baf";
                break;
            case Options.output_format_b :
                str = ".b";
                break;

            case Options.output_format_jimple :
                str = ".jimple";
                break;
            case Options.output_format_jimp :
                str = ".jimp";
                break;
            case Options.output_format_grimp :
                str = ".grimp";
                break;
            case Options.output_format_grimple :
                str = ".grimple";
                break;
            case Options.output_format_class :
                str = ".class";
                break;
            case Options.output_format_dava :
                str = ".java";
                break;
            case Options.output_format_jasmin :
                str = ".jasmin";
                break;

            case Options.output_format_xml :
                str = ".xml";
                break;

            default :
                throw new RuntimeException();
        }
        return str;
    }

    public String getOutputDir() {
        String ret = Options.v().output_dir();
        if( ret.length() == 0 ) ret = "sootOutput";
        File dir = new File(ret);

        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (SecurityException se) {
                G.v().out.println("Unable to create " + ret);
                throw new CompilationDeathException(COMPILATION_ABORTED);
            }
        }
        return ret;
    }

    public String getFileNameFor(SootClass c, int rep) {
        // add an option for no output
        if (rep == Options.output_format_none)
            return null;

        StringBuffer b = new StringBuffer();

        b.append(getOutputDir());

        if ((b.length() > 0) && (b.charAt(b.length() - 1) != fileSeparator))
            b.append(fileSeparator);

        if (rep != Options.output_format_dava) {
            b.append(c.getName());
            b.append(getExtensionFor(rep));

            return b.toString();
        }

        b.append("dava");
        b.append(fileSeparator);
        {
            String classPath = b.toString() + "classes";
            File dir = new File(classPath);

            if (!dir.exists())
                try {
                    dir.mkdirs();
                } catch (SecurityException se) {
                    G.v().out.println("Unable to create " + classPath);
                    throw new CompilationDeathException(COMPILATION_ABORTED);
                }
        }

        b.append("src");
        b.append(fileSeparator);

        String fixedPackageName = c.getJavaPackageName();
        if (fixedPackageName.equals("") == false) {
            b.append(fixedPackageName.replace('.', fileSeparator));
            b.append(fileSeparator);
        }

        {
            String path = b.toString();
            File dir = new File(path);

            if (!dir.exists())
                try {
                    dir.mkdirs();
                } catch (SecurityException se) {
                    G.v().out.println("Unable to create " + path);
                    throw new CompilationDeathException(COMPILATION_ABORTED);
                }
        }

        b.append(c.getShortJavaStyleName());
        b.append(".java");

        return b.toString();
    }

    private final char fileSeparator =
        System.getProperty("file.separator").charAt(0);
    private final String pathSeparator = System.getProperty("path.separator");

    private boolean useJavaStyle = false;

    // hack for J2ME, patch provided by Stephen Chen
    // by default, this is set as false, to use SOOT with J2ME library
    // flag isJ2ME true. Type system works around Clonable, Serializeable.
    // see changes in: 
    //           soot/jimple/toolkits/typing/ClassHierarchy.java
    //           soot/jimple/toolkits/typing/TypeResolver.java
    //           soot/jimple/toolkits/typing/TypeVariable.java
    //           soot/jimple/toolkits/typing/TypeNode.java
    final public boolean isJ2ME = false;

    private SootClass mainClass = null;

    private List getClassesUnder(String aPath) {
        File file = new File(aPath);
        List fileNames = new ArrayList();

        File[] files = file.listFiles();
        if (files == null) {
            files = new File[1];
            files[0] = file;
        }

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                List l =
                    getClassesUnder(
                        aPath + File.separator + files[i].getName());
                Iterator it = l.iterator();
                while (it.hasNext()) {
                    String s = (String) it.next();
                    fileNames.add(files[i].getName() + "." + s);
                }
            } else {
                String fileName = files[i].getName();

                if (fileName.endsWith(".class")) {
                    int index = fileName.lastIndexOf(".class");
                    fileNames.add(fileName.substring(0, index));
                }

                if (fileName.endsWith(".jimple")) {
                    int index = fileName.lastIndexOf(".jimple");
                    fileNames.add(fileName.substring(0, index));
                }
            }
        }
        return fileNames;
    }

    /* hack for J2ME */
    public boolean isJ2ME() {
        return isJ2ME;
    }

    public void setJavaStyle(boolean val) {
        useJavaStyle = val;
    }

    public boolean getJavaStyle() {
        return useJavaStyle;
    }

    /* This is called after sootClassPath has been defined. */
    private Set classesInDynamicPackage(String str) {
        HashSet set = new HashSet(0);
        StringTokenizer strtok =
            new StringTokenizer(Scene.v().getSootClassPath(), pathSeparator);
        while (strtok.hasMoreTokens()) {
            String path = strtok.nextToken();

            // For jimple files
            List l = getClassesUnder(path);
            for( Iterator filenameIt = l.iterator(); filenameIt.hasNext(); ) {
                final String filename = (String) filenameIt.next();
                if (filename.startsWith(str))
                    set.add(filename);
            }

            // For class files;
            path = path + "/";
            StringTokenizer tokenizer = new StringTokenizer(str, ".");
            while (tokenizer.hasMoreTokens()) {
                path = path + tokenizer.nextToken();
                if (tokenizer.hasMoreTokens())
                    path = path + "/";
            }
            l = getClassesUnder(path);
            for (Iterator it = l.iterator(); it.hasNext();)
                set.add(str + "." + ((String) it.next()));
        }
        return set;
    }

    private void printVersion() {
        G.v().out.println("Soot version " + versionString);

        G.v().out.println(
            "Copyright (C) 1997-2003 Raja Vallee-Rai (rvalleerai@sable.mcgill.ca).");
        G.v().out.println("All rights reserved.");
        G.v().out.println("");
        G.v().out.println(
            "Contributions are copyright (C) 1997-2003 by their respective contributors.");
        G.v().out.println("See individual source files for details.");
        G.v().out.println("");
        G.v().out.println(
            "Soot comes with ABSOLUTELY NO WARRANTY.  Soot is free software,");
        G.v().out.println(
            "and you are welcome to redistribute it under certain conditions.");
        G.v().out.println(
            "See the accompanying file 'license.html' for details.");
        G.v().out.println();
        G.v().out.println("Visit the Soot website:");
        G.v().out.println("  http://www.sable.mcgill.ca/soot/");
        G.v().out.println();
        G.v().out.println("For a list of command line options, enter:");
        G.v().out.println("  java soot.Main --help");
    }

    private void printHelp() {
        G.v().out.println(Options.v().getUsage());
    }

    private void processCmdLine(String[] args)
        throws CompilationDeathException {

        if (!Options.v().parse(args))
            throw new CompilationDeathException(
                COMPILATION_ABORTED,
                "Option parse error");

        for( Iterator packIt = PackManager.v().allPacks().iterator(); packIt.hasNext(); ) {

            final Pack pack = (Pack) packIt.next();
            Options.v().warnForeignPhase(pack.getPhaseName());
            for( Iterator trIt = pack.iterator(); trIt.hasNext(); ) {
                final Transform tr = (Transform) trIt.next();
                Options.v().warnForeignPhase(tr.getPhaseName());
            }
        }
        Options.v().warnNonexistentPhase();

        if (Options.v().help()) {
            printHelp();
            throw new CompilationDeathException(COMPILATION_SUCCEEDED);
        }

        if (args.length == 0 || Options.v().version()) {
            printVersion();
            throw new CompilationDeathException(COMPILATION_SUCCEEDED);
        }

        postCmdLineCheck();
    }

    private void exitCompilation(int status) {
        exitCompilation(status, "");
    }

    private void exitCompilation(int status, String msg) {
        if(status == Main.COMPILATION_ABORTED) {
                System.out.println("compilation failed: "+msg);
        }
        G.v().reset();
    }

    private void postCmdLineCheck() throws CompilationDeathException {
        if (Options.v().classes().isEmpty() && Options.v().process_path().isEmpty()) {
            throw new CompilationDeathException(
                COMPILATION_ABORTED,
                "Nothing to do!");
        }
        // Command line classes
        if (Options.v().app() && Options.v().classes().size() > 1) {

            throw new CompilationDeathException(
                COMPILATION_ABORTED,
                "Can only specify one class in application mode!\n"
                    + "The transitive closure of the specified class gets loaded.\n"
                    + "(Did you mean to use single-file mode?)");
        }
    }

    public String[] cmdLineArgs;
    /**
     *   Entry point for cmd line invocation of soot.
     */
    public static void main(String[] args) {
        Main.v().go(args);
    }
    public int go(String[] args) {
        cmdLineArgs = args;
        return run();
    }
    /**
     *   Entry point for Eclipse line invocation of soot.
     */
    public static int main(String[] args, PrintStream out) {
        G.v().reset();
        G.v().out = out;
        return Main.v().go(args);
    }

    /** 
     *  Entry point to the soot's compilation process. Be sure to call
     *  setCmdLineArgs before invoking this method.
     *
     *  @see #setCmdLineArgs
     */
    public int run() {
        start = new Date();

        try {
            Timers.v().totalTimer.start();

            processCmdLine(cmdLineArgs);

            G.v().out.println("Soot started on " + start);

            if (Options.v().soot_classpath().length() > 0) {
                Scene.v().setSootClassPath(Options.v().soot_classpath());
            }

            loadNecessaryClasses();
            prepareClasses();

            runPacks();

            Timers.v().totalTimer.end();

            // Print out time stats.				
            if (Options.v().time())
                Timers.v().printProfilingInformation();

        } catch (CompilationDeathException e) {
            Timers.v().totalTimer.end();
            exitCompilation(e.getStatus(), e.getMessage());
            return e.getStatus();
        }

        finish = new Date();

        G.v().out.println("Soot finished on " + finish);
        long runtime = finish.getTime() - start.getTime();
        G.v().out.println(
            "Soot has run for "
                + (runtime / 60000)
                + " min. "
                + ((runtime % 60000) / 1000)
                + " sec.");

        exitCompilation(COMPILATION_SUCCEEDED);
        return COMPILATION_SUCCEEDED;
    }

    private void runPacks() {
        if (Options.v().whole_program()) {
            // Run the whole-program packs.
            PackManager.v().getPack("cg").apply();
            if (Options.v().via_shimple()) {
                PackManager.v().getPack("wstp").apply();
                PackManager.v().getPack("wsop").apply();
            } else {
                PackManager.v().getPack("wjtp").apply();
                PackManager.v().getPack("wjop").apply();
                PackManager.v().getPack("wjap").apply();
            }
            preProcessDAVA();
            wholeProcessClasses();
            postProcessDAVA();
        } else {
            preProcessDAVA();
            processClasses();
            postProcessDAVA();
        }
    }

    /* preprocess classes for DAVA */
    private void preProcessDAVA() {
        if (Options.v().output_format() == Options.output_format_dava) {
            ThrowFinder.v().find();
            PackageNamer.v().fixNames();

            G.v().out.println();
        }
    }

    /* process classes in whole-program mode */
    private void wholeProcessClasses() {
        Iterator classIt =
            Scene.v().getActiveInvokeGraph().getReachableClasses().iterator();

        // process each class 
        while (classIt.hasNext()) {
            SootClass s = (SootClass) classIt.next();
            handleClass(s);

        }
    }

    /* process classes */
    private void processClasses() {
        Iterator classIt = Scene.v().getApplicationClasses().iterator();

        // process each class 
        while (classIt.hasNext()) {
            SootClass s = (SootClass) classIt.next();

            handleClass(s);
        }
    }

    /* post process for DAVA */
    private void postProcessDAVA() {
        if (Options.v().output_format() == Options.output_format_dava) {

            // ThrowFinder.v().find();
            // PackageNamer.v().fixNames();

            G.v().out.println();

            setJavaStyle(true);

            Iterator classIt = Scene.v().getApplicationClasses().iterator();
            while (classIt.hasNext()) {
                SootClass s = (SootClass) classIt.next();

                FileOutputStream streamOut = null;
                PrintWriter writerOut = null;
                String fileName = getFileNameFor(s, Options.v().output_format());

                try {
                    streamOut = new FileOutputStream(fileName);
                    writerOut =
                        new PrintWriter(new OutputStreamWriter(streamOut));
                } catch (IOException e) {
                    G.v().out.println("Cannot output file " + fileName);
                }

                G.v().out.print("Generating " + fileName + "... ");
                G.v().out.flush();

                DavaPrinter.v().printTo(s, writerOut);

                G.v().out.println();
                G.v().out.flush();

                {
                    try {
                        writerOut.flush();
                        streamOut.close();
                    } catch (IOException e) {
                        G.v().out.println(
                            "Cannot close output file " + fileName);
                    }
                }

                {
                    Iterator methodIt = s.methodIterator();

                    while (methodIt.hasNext()) {
                        SootMethod m = (SootMethod) methodIt.next();

                        if (m.hasActiveBody())
                            m.releaseActiveBody();
                    }
                }
            }
            G.v().out.println();

            setJavaStyle(false);
        }
    }

    private void loadNecessaryClasses() {
        Iterator it = Options.v().classes().iterator();

        while (it.hasNext()) {
            String name = (String) it.next();
            SootClass c;

            c = Scene.v().loadClassAndSupport(name);

            if (mainClass == null) {
                mainClass = c;
                Scene.v().setMainClass(c);
            }
            c.setApplicationClass();
        }

        HashSet dynClasses = new HashSet();
        dynClasses.addAll(Options.v().dynamic_classes());

        for( Iterator pathIt = Options.v().dynamic_path().iterator(); pathIt.hasNext(); ) {

            final String path = (String) pathIt.next();
            dynClasses.addAll(getClassesUnder(path));
        }

        for( Iterator pkgIt = Options.v().dynamic_package().iterator(); pkgIt.hasNext(); ) {

            final String pkg = (String) pkgIt.next();
            dynClasses.addAll(classesInDynamicPackage(pkg));
        }

        while (it.hasNext()) {
            Object o = it.next();
            Scene.v().loadClassAndSupport((String) o);
        }

        for( Iterator pathIt = Options.v().process_path().iterator(); pathIt.hasNext(); ) {

            final String path = (String) pathIt.next();
            for( Iterator clIt = getClassesUnder(path).iterator(); clIt.hasNext(); ) {
                final String cl = (String) clIt.next();
                Scene.v().loadClassAndSupport(cl).setApplicationClass();
            }
        }
    }

    /* Generate classes to process, adding or removing package marked by
     * command line options.
     */
    private void prepareClasses() {

        LinkedList excludedPackages = new LinkedList();
        if (Options.v().exclude() != null)
            excludedPackages.addAll(Options.v().exclude());

        excludedPackages.add("java.");
        excludedPackages.add("sun.");
        excludedPackages.add("javax.");
        excludedPackages.add("com.sun.");
        excludedPackages.add("com.ibm.");
        excludedPackages.add("org.xml.");
        excludedPackages.add("org.w3c.");
        excludedPackages.add("org.apache.");

        // Remove/add all classes from packageInclusionMask as per -i option
        for( Iterator sIt = Scene.v().getClasses().iterator(); sIt.hasNext(); ) {
            final SootClass s = (SootClass) sIt.next();
            if( s.isPhantom() ) continue;
            if (Options.v().classes().contains(s.getName())) {
                s.setApplicationClass();
                continue;
            }

            for( Iterator pkgIt = excludedPackages.iterator(); pkgIt.hasNext(); ) {

                final String pkg = (String) pkgIt.next();
                if (s.isApplicationClass()
                && s.getPackageName().startsWith(pkg)) {
                        s.setLibraryClass();
                }
            }
            for( Iterator pkgIt = Options.v().include().iterator(); pkgIt.hasNext(); ) {
                final String pkg = (String) pkgIt.next();
                if (s.getPackageName().startsWith(pkg))
                    s.setApplicationClass();
            }
        }
    }

    /* normal approach to handle each class by analyzing every method.
     */
    private void handleClass(SootClass c) {
        FileOutputStream streamOut = null;
        PrintWriter writerOut = null;

        if (Options.v().output_format() == Options.output_format_dava) {
            G.v().out.print("Decompiling ");
        } else {
            G.v().out.print("Transforming ");
        }
        G.v().out.print(c.getName() + "... ");
        G.v().out.flush();

        boolean produceBaf = false, produceGrimp = false, produceDava = false;

        switch (Options.v().output_format()) {
            case Options.output_format_none :
                break;
            case Options.output_format_jimple :
            case Options.output_format_jimp :
                break;
            case Options.output_format_dava :
                produceDava = true;
            case Options.output_format_grimp :
            case Options.output_format_grimple :
                produceGrimp = true;
                break;
            case Options.output_format_baf :
            case Options.output_format_b :
                produceBaf = true;
                break;
            case Options.output_format_xml :
                break;
            case Options.output_format_jasmin :
            case Options.output_format_class :
                produceGrimp = Options.v().via_grimp();
                produceBaf = !produceGrimp;
                break;
            default :
                throw new RuntimeException();
        }

        String fileName = getFileNameFor(c, Options.v().output_format());

        if (Options.v().output_format() != Options.output_format_none
            && Options.v().output_format() != Options.output_format_class) {
            try {
                streamOut = new FileOutputStream(fileName);
                writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
            } catch (IOException e) {
                G.v().out.println("Cannot output file " + fileName);
            }
        }


        // Build all necessary bodies
        {
            Iterator methodIt = c.methodIterator();

            while (methodIt.hasNext()) {
                SootMethod m = (SootMethod) methodIt.next();

                if (!m.isConcrete())
                    continue;

                // Build Jimple body and transform it.
                {
                    JimpleBody body = (JimpleBody) m.retrieveActiveBody();

                    if (Options.v().via_shimple()) {
                        PackManager.v().getPack("stp").apply(body);
                        PackManager.v().getPack("sop").apply(body);
                    }
                    PackManager.v().getPack("jtp").apply(body);
                    PackManager.v().getPack("jop").apply(body);
                    PackManager.v().getPack("jap").apply(body);
                }

                if (produceGrimp) {
                    m.setActiveBody(Grimp.v().newBody(m.getActiveBody(), "gb"));
                    PackManager.v().getPack("gop").apply(m.getActiveBody());
                } else if (produceBaf) {
                    m.setActiveBody(
                        Baf.v().newBody((JimpleBody) m.getActiveBody()));
                    PackManager.v().getPack("bop").apply(m.getActiveBody());
                    PackManager.v().getPack("tag").apply(m.getActiveBody());
                }
            }

            if (produceDava) {
                methodIt = c.methodIterator();

                while (methodIt.hasNext()) {
                    SootMethod m = (SootMethod) methodIt.next();

                    if (!m.isConcrete())
                        continue;

                    m.setActiveBody(Dava.v().newBody(m.getActiveBody()));
                }
            }
        }

        if (Options.v().xml_attributes()) {
            Printer.v().setOption(Printer.ADD_JIMPLE_LN);
        }
        switch (Options.v().output_format()) {
            case Options.output_format_none :
                break;
            case Options.output_format_jasmin :
                if (c.containsBafBody())
                    new soot.baf.JasminClass(c).print(writerOut);
                else
                    new soot.jimple.JasminClass(c).print(writerOut);
                break;
            case Options.output_format_jimp :
            case Options.output_format_b :
            case Options.output_format_grimp :
                Printer.v().setOption(Printer.USE_ABBREVIATIONS);
                Printer.v().printTo(c, writerOut);
                break;
            case Options.output_format_baf :
            case Options.output_format_jimple :
            case Options.output_format_grimple :
                writerOut =
                    new PrintWriter(
                        new EscapedWriter(new OutputStreamWriter(streamOut)));
                Printer.v().printTo(c, writerOut);
                break;
            case Options.output_format_dava :
                break;
            case Options.output_format_class :
                Printer.v().write(c, getOutputDir());
                break;
            case Options.output_format_xml :
                writerOut =
                    new PrintWriter(
                        new EscapedWriter(new OutputStreamWriter(streamOut)));
                XMLPrinter.v().printJimpleStyleTo(c, writerOut);
                break;
            default :
                throw new RuntimeException();
        }

        if (Options.v().output_format() != Options.output_format_none
            && Options.v().output_format() != Options.output_format_class) {
            try {
                writerOut.flush();
                streamOut.close();
            } catch (IOException e) {
                G.v().out.println("Cannot close output file " + fileName);
            }
        }

        if (Options.v().xml_attributes()) {
            XMLAttributesPrinter xap = new XMLAttributesPrinter(fileName);
            xap.printAttrs(c);
        }

        // Release bodies
        if (!produceDava) {
            Iterator methodIt = c.methodIterator();

            while (methodIt.hasNext()) {
                SootMethod m = (SootMethod) methodIt.next();

                if (m.hasActiveBody())
                    m.releaseActiveBody();
            }
        }
        G.v().out.println();
    }

}
