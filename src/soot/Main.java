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
 *

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot;



import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.grimp.*;
import soot.baf.*;
import soot.jimple.toolkits.invoke.*;
import soot.baf.toolkits.base.*;
import soot.toolkits.scalar.*;
import soot.dava.*;

import java.io.*;

import java.text.*;

/** Main class for Soot; provides Soot's command-line user interface. */
public class Main implements Runnable, ICompilationListener
{        
     //------> this used to be in Main
     // DEBUG
    static boolean isAnalyzingLibraries = false;


    private static List compilationListeners = new ArrayList(1);
    public static void addCompilationListener(ICompilationListener l)
    {
        compilationListeners.add(l);
    }
    public static final int COMPILATION_ABORTED = 0;
    public static final int COMPILATION_SUCCEDED = 1;
    


    // The following lists are paired.  false is exclude in the first list.
    static List packageInclusionFlags = new ArrayList();
    static List packageInclusionMasks = new ArrayList();

    static List dynamicClasses = new ArrayList();
    static List processClasses = new ArrayList();
    
    static Chain cmdLineClasses = new HashChain();
    // <-------------

    public static final int BAF = 0;
    public static final int B = 1;

    public static final int JIMPLE = 2;
    public static final int JIMP = 3;
    
    public static final int NJIMPLE = 4;
    public static final int GRIMP = 5;

    public static final int GRIMPLE = 6;
    public static final int CLASS = 7;
    
    public static final int DAVA = 8;
    public static final int JASMIN = 9;
    




    public static String getExtensionFor(int rep)
    {
        String str = null;

        switch(rep) {
        case BAF:
            str = ".baf";
            break;
        case B:
            str = ".b";
            break;
            
        case JIMPLE: 
            str = ".jimple";
            break;                        
        case JIMP:    
            str = ".jimp";
            break;
        case NJIMPLE:
            str = ".njimple";
            break;
       
        case GRIMP:
            str = ".grimp";
            break;
        case GRIMPLE:
            str = ".grimple";
            break;
            
        case CLASS:
            str = ".class";
            break;
        case DAVA:
            str = ".dava";
            break;
        case JASMIN:
            str = ".jasmin";
            break;
        default:
            throw new RuntimeException();
        }
         return str;
    }


    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    static boolean naiveJimplification;
    static boolean onlyJimpleOutput;
    public static boolean isVerbose;
    static boolean onlyJasminOutput;
    static public boolean isProfilingOptimization;
    static boolean isSubtractingGC;
    static public boolean oldTyping;
    static public boolean isInDebugMode;
    static public boolean usePackedLive;
    static public boolean usePackedDefs = true;
    static boolean isTestingPerformance;
    
    static private int targetExtension = CLASS;
    static private String xmlInputFile = null;
    static private boolean produceXmlOutput = false;

    static public int totalFlowNodes,
           totalFlowComputations;
           
    static public Timer copiesTimer = new Timer("copies"),
        defsTimer = new Timer("defs"),
        usesTimer = new Timer("uses"),
        liveTimer = new Timer("live"),
        splitTimer = new Timer("split"),
        packTimer = new Timer("pack"),
        cleanup1Timer = new Timer("cleanup1"),
        cleanup2Timer = new Timer("cleanup2"),
        conversionTimer = new Timer("conversionm"),
        cleanupAlgorithmTimer = new Timer("cleanupAlgorithm"),
        graphTimer = new Timer("graphTimer"),
        assignTimer = new Timer("assignTimer"),
        resolveTimer = new Timer("resolveTimer"),
        totalTimer = new Timer("totalTimer"),
        splitPhase1Timer = new Timer("splitPhase1"),
        splitPhase2Timer = new Timer("splitPhase2"),
        usePhase1Timer = new Timer("usePhase1"),
        usePhase2Timer = new Timer("usePhase2"),
        usePhase3Timer = new Timer("usePhase3"),
        defsSetupTimer = new Timer("defsSetup"),
        defsAnalysisTimer = new Timer("defsAnalysis"),
        defsPostTimer = new Timer("defsPost"),
        liveSetupTimer = new Timer("liveSetup"),
        liveAnalysisTimer = new Timer("liveAnalysis"),
        livePostTimer = new Timer("livePost"),
        aggregationTimer = new Timer("aggregation"),
        grimpAggregationTimer = new Timer("grimpAggregation"),
        deadCodeTimer = new Timer("deadCode"),
        propagatorTimer = new Timer("propagator"),
        buildJasminTimer = new Timer("buildjasmin"),
        assembleJasminTimer = new Timer("assembling jasmin");
        
    static public Timer
        resolverTimer = new Timer("resolver");
        
    static public int conversionLocalCount,
        cleanup1LocalCount,
        splitLocalCount,
        assignLocalCount,
        packLocalCount,
        cleanup2LocalCount;

    static public int conversionStmtCount,
        cleanup1StmtCount,
        splitStmtCount,
        assignStmtCount,
        packStmtCount,
        cleanup2StmtCount;

    static private String outputDir = "";

    static private boolean isOptimizing;
    static private boolean isOptimizingWhole;
    static private boolean isUsingVTA;
    static private boolean isUsingRTA;
    static private boolean isApplication = false;
    static private SootClass mainClass = null;        
    
    static public long stmtCount;
    static int finalRep = GRIMP;


    private static List sTagFileList = new ArrayList();
 

    private static List getClassesUnder(String aPath) 
    {
        File file = new File(aPath);
        List fileNames = new ArrayList();

        File[] files = file.listFiles();
        

        for(int i = 0; i < files.length; i++) {            
            if(files[i].isDirectory()) {               
                List l  = getClassesUnder( aPath + File.separator + files[i].getName());
                Iterator it = l.iterator();
                while(it.hasNext()) {
                    String s = (String) it.next();
                    fileNames.add(files[i].getName() +  "." + s);
                }                    
            } else {                
                String fileName = files[i].getName();        
                
                if (fileName.endsWith(".class"))
                {
                    int index = fileName.lastIndexOf(".class");
                    fileNames.add(fileName.substring(0, index));
                }
                
                if (fileName.endsWith(".jimple"))
                {
                    int index = fileName.lastIndexOf(".jimple");
                    fileNames.add(fileName.substring(0, index));
                }
            }
        }
        return fileNames;
    }

    public static void setTargetRep(int rep)
    {
        targetExtension = rep;
    }

    public static int getTargetRep()
    {
        return targetExtension;
    }


    public static void setOptimizing(boolean val)
    {
        isOptimizing = val;
    }
    public static  boolean isOptimizing()
    {
        return isOptimizing;
    }


    public static void setOptimizingWhole(boolean val)
    throws CompilationDeathException
    {
        if (!isApplication && val){            
            throw new CompilationDeathException(COMPILATION_ABORTED, "Can only whole-program optimize in application mode!");
        }
  
        isOptimizingWhole = val;
    }

    public static boolean isOptimizingWhole()
    {
        return isOptimizingWhole;
    }

    public static void setProfiling(boolean val)
    {
        isProfilingOptimization = val;
    }

    public static boolean isProfiling()
    {
        return isProfilingOptimization;
    }


    public static void setVerbose(boolean val)
    {
        isVerbose = val;
    }
    public static boolean isVerbose()
    {
        return isVerbose;
    }

    public static void setAppMode(boolean val)
    {
        isApplication = val;
    }
    public static boolean isAppMode()
    {
        return isApplication;
    }


    public static void addExclude(String str)
        throws CompilationDeathException
    {
        if (!isApplication) {    
            throw new CompilationDeathException(COMPILATION_ABORTED, "Exclude flag only valid in application mode!");
        }
  
        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add(str);
  
    }

    public static void addInclude(String str)
        throws CompilationDeathException
    {
        if (!isApplication) {
            throw new CompilationDeathException(COMPILATION_ABORTED, "Include flag only valid in application mode!");
        }
        packageInclusionFlags.add(new Boolean(true));
        packageInclusionMasks.add(str);
    }

    public static void addDynamicPath(String path)
        throws CompilationDeathException
    {
        if (!isApplication)
            {
                throw new CompilationDeathException(COMPILATION_ABORTED, "Dynamic-path flag only valid in application mode!");
            }
                     
        StringTokenizer tokenizer = new StringTokenizer(path, ":");
        while(tokenizer.hasMoreTokens())
            dynamicClasses.addAll(getClassesUnder(tokenizer.nextToken()));
    }


    public static void addProcessPath(String path)
        throws CompilationDeathException
    {
        if (isApplication)
            {
                throw new CompilationDeathException(COMPILATION_ABORTED, "Process-path flag only valid in single-file mode!");
            }

        StringTokenizer tokenizer = new StringTokenizer(path, ":");
        while(tokenizer.hasMoreTokens())
            processClasses.addAll(getClassesUnder(tokenizer.nextToken()));
    }

    public static void setDebug(boolean val)
    {
        isInDebugMode = val;
    }

    public static boolean isDebug()
    {
        return isInDebugMode;
    }

    public static void setOutputDir(String dir)
    {
        outputDir = dir;
    }

    public static String getOutputDir()
    {
        return outputDir;
    }

    public static void setSrcPrecedence(String prec)
        throws CompilationDeathException
    {
        if(prec.equals("jimple"))
            SourceLocator.setSrcPrecedence(SourceLocator.PRECEDENCE_JIMPLE);
        else if(prec.equals("class"))
            SourceLocator.setSrcPrecedence(SourceLocator.PRECEDENCE_CLASS);
        else {                    
            
            throw new CompilationDeathException(COMPILATION_ABORTED,"Illegal --src-prec arg: " + prec +
                            ". Valid args are: \"jimple\" or \"class\"");           
        }
    }

    public static void setFinalRep(String rep)
        throws CompilationDeathException
    {
        if(rep.equals("jimple"))
            finalRep = JIMPLE;
        else if(rep.equals("grimp"))
            finalRep = GRIMP;
        else if(rep.equals("baf"))
            finalRep = BAF;
        else {                    
            throw new CompilationDeathException(COMPILATION_ABORTED, "Illegal --final-rep arg: " + rep +           
                            "\nvalid args are: [baf|grimp|jimple]" );
        }
    }

    public static int getFinalRep() 
    {
        return finalRep;
    }



    public static void setAnalyzingLibraries(boolean val)
    {
        isAnalyzingLibraries = val;
    }
    public static boolean isAnalyzingLibraries()
    {
        return isAnalyzingLibraries;
    }



    public static void setSubstractingGC(boolean val)
    {
        isSubtractingGC = val;
    }

    public static boolean isSubstractingGC()
    {
        return isSubtractingGC;
    }

    private static void printHelp()
    {
         // $Format: "            System.out.println(\"Soot version $ProjectVersion$\");"$
            System.out.println("Soot version 1.beta.6.dev.55");
            System.out.println("Copyright (C) 1997-1999 Raja Vallee-Rai (rvalleerai@sable.mcgill.ca).");
            System.out.println("All rights reserved.");
            System.out.println("");
            System.out.println("Contributions are copyright (C) 1997-1999 by their respective contributors.");
            System.out.println("See individual source files for details.");
            System.out.println("");
            System.out.println("Soot comes with ABSOLUTELY NO WARRANTY.  Soot is free software,");
            System.out.println("and you are welcome to redistribute it under certain conditions.");
            System.out.println("See the accompanying file 'license.html' for details.");
            System.out.println("");
            System.out.println("Syntax: (single-file mode) soot [option]* classname ...  ");
            System.out.println("        (application mode) soot --app [option]* mainClassName");
            System.out.println("");
            System.out.println("Output options:");
            System.out.println("  -b, --b                    produce .b (abbreviated .baf) files");
            System.out.println("  -B, --baf                  produce .baf code");
            System.out.println("  -j, --jimp                 produce .jimp (abbreviated .jimple) files");
            System.out.println("  -J, --jimple               produce .jimple code");
            System.out.println("  -g, --grimp                produce .grimp (abbreviated .grimple) files");
            System.out.println("  -G, --grimple              produce .grimple files");
            System.out.println("  -s, --jasmin               produce .jasmin files");
            System.out.println("  -c, --class                produce .class files");
            System.out.println("  -d PATH                    store produced files in PATH");
            System.out.println("");
            System.out.println("Application mode options:");
            System.out.println("  -x, --exclude PACKAGE      marks classfiles in PACKAGE (e.g. java.)"); 
            System.out.println("                             as context classes");
            System.out.println("  -i, --include PACKAGE      marks classfiles in PACKAGE (e.g. java.util.)");
            System.out.println("                             as application classes");
            System.out.println("  -a, --analyze-context      label context classes as library");
            System.out.println("  --dynamic-path PATH        mark all class files in PATH as ");
            System.out.println("                             potentially dynamic classes");
            System.out.println("");
            System.out.println("Single-file mode options:");
            System.out.println("  --process-path PATH        process all classes on the PATH");
            System.out.println("");
            System.out.println("Construction options:");
            System.out.println("  --final-rep REP            produce classfile/jasmin from REP ");
            System.out.println("                                  (jimple, grimp, or baf)");
            System.out.println();
            System.out.println("");
            System.out.println("Optimization options:");
            System.out.println("  -O  --optimize             perform scalar optimizations on the classfiles");
            System.out.println("  -W  --whole-optimize       perform whole program optimizations on the ");
            System.out.println("                             classfiles");
            System.out.println("");
            System.out.println("Miscellaneous options:");
            System.out.println("  --soot-class-path PATH     uses PATH as the classpath for finding classes");
            System.out.println("  -t, --time                 print out time statistics about tranformations");
            System.out.println("  --subtract-gc              attempt to subtract the gc from the time stats");
            System.out.println("  -v, --verbose              verbose mode");
            System.out.println("  --debug                    avoid catching exceptions");
            System.out.println("  -p, --phase-option PHASE-NAME KEY[:VALUE]");
            System.out.println("                             set run-time option KEY to VALUE for PHASE-NAME");
            System.out.println("                             (default for VALUE is true)");
            System.out.println("");
            System.out.println("Examples:");
            System.out.println("");
            System.out.println("  soot --app -d newClasses Simulator");
            System.out.println("         Transforms all classes starting with Simulator, ");
            System.out.println("         and stores them in newClasses. ");                           
    }



    private static void processCmdLine(String[] args)
        throws CompilationDeathException
    {
        if(args.length == 0)
        {
            printHelp();
            throw new CompilationDeathException(COMPILATION_ABORTED);            
        }

        // Handle all the options
        for(int i = 0; i < args.length; i++)
        {
            String arg = args[i];
             if(arg.equals("-j") || arg.equals("--jimp"))
                setTargetRep(JIMP);
            else if(arg.equals("--njimple"))
                setTargetRep(NJIMPLE);
            else if(arg.equals("-s") || arg.equals("--jasmin"))
                setTargetRep(JASMIN);
            else if(arg.equals("-J") || arg.equals("--jimple"))
                setTargetRep(JIMPLE);
            else if(arg.equals("-B") || arg.equals("--baf"))
                setTargetRep(BAF);
            else if(arg.equals("-b") || arg.equals("--b"))
                setTargetRep(B);
            else if(arg.equals("-g") || arg.equals("--grimp"))
                setTargetRep(GRIMP);
            else if(arg.equals("-G") || arg.equals("--grimple"))
                setTargetRep(GRIMPLE);
            else if(arg.equals("-c") || arg.equals("--class"))
                setTargetRep(CLASS);
            else if(arg.equals("--dava"))
                setTargetRep(DAVA);

            else if(arg.equals("-X") || arg.equals("--xml"))
                produceXmlOutput = true;
            else if(arg.equals("-O") || arg.equals("--optimize"))
                setOptimizing(true);
            else if(arg.equals("-W") || arg.equals("--whole-optimize"))            
                setOptimizingWhole(true);
           
             else if(arg.equals("-t") || arg.equals("--time"))
                setProfiling(true);
            else if(arg.equals("--subtract-gc"))
                setSubstractingGC(true);
            else if(arg.equals("-v") || arg.equals("--verbose"))
                setVerbose(true);
             else if(arg.equals("--soot-class-path"))
            {
                if(++i < args.length)
                    Scene.v().setSootClassPath(args[i]);
            }
            else if(arg.equals("--app"))
            {
                if (i != 0)
                {
                    throw new CompilationDeathException(COMPILATION_ABORTED, 
                                    "Application mode (--app) must be set as first argument to Soot!" +
                                    "\neg. java soot.Main --app Simulator");           
                }
                setAppMode(true);
            }
            else if(arg.equals("-d"))
            {
                if(++i < args.length)
                    outputDir = args[i];
            }
             else if(arg.equals("-x") || arg.equals("--exclude"))
            {            
                if(++i < args.length)
                    addExclude(args[i]);
            }
            else if(arg.equals("-i") || arg.equals("--include"))
            {                 
                if(++i < args.length)
                    addInclude(args[i]);
            }
            else if(arg.equals("-A") || arg.equals("--analyze-context"))
                setAnalyzingLibraries(true);
            else if(arg.equals("--final-rep"))
            {
                if(++i < args.length)
                    setFinalRep(args[i]);
            }
            else if (arg.equals("-p") || arg.equals("--phase-option"))
            {
                if(i+2 < args.length)
                    processPhaseOption(args[++i], args[++i]);                
            }
            else if (arg.equals("--debug"))
                setDebug(true);
               
            else if (arg.equals("--dynamic-path"))
            {
                if(++i < args.length) 
                    addDynamicPath(args[i]);
            }
            else if (arg.equals("--process-path")) 
            {
                if(++i < args.length)
                    addProcessPath(args[i]);                    
            }                    
            else if(arg.equals("--src-prec"))
            {                    
                if(++i < args.length)                    
                    setSrcPrecedence(args[i]);
            } 
	     else if(arg.equals("--tag-file")) {
		if(++i < args.length)
		    sTagFileList.add(args[i]);
	    }
             else if(arg.startsWith("-"))
             {
                 System.out.println("Unrecognized option: " + arg);
                 printHelp();
                 throw new CompilationDeathException(COMPILATION_ABORTED);            
             }  
             else if(arg.startsWith("@"))
             {
                    try
                    {
                        File fn = new File(arg.substring(1));
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fn)));
                        List argsList = new LinkedList();
                        while (br.ready())
                            argsList.add(br.readLine());
                        br.close();
                        processCmdLine((String[])argsList.toArray(new String[argsList.size()]));
                    }
                    catch (IOException e)
                        {
                            throw new CompilationDeathException(COMPILATION_ABORTED,
                                            "Error reaing file "+arg.substring(1));
                        }
                }
                else
                {                    
                    cmdLineClasses.add(arg);
                }
        }
        postCmdLineCheck();           
    }
    
    private static void exitCompilation(int status)
    {
        exitCompilation(status, null) ;
    }

    private static void exitCompilation(int status, String msg)
    {
        Iterator it = compilationListeners.iterator();

        if(msg != null) {
            while(it.hasNext()) {
                ((ICompilationListener)it.next()).compilationTerminated(status, msg);
            }
        } 
    }

    private static void processPhaseOption(String phaseName, String option)
    {
        int colonLoc = option.indexOf(':');
        String key = null, value = null;

        if (colonLoc == -1)
            {
                key = option;
                value = "true";
            }
        else 
            {
                key = option.substring(0, option.indexOf(':'));
                value = option.substring(option.indexOf(':')+1);
            }

        Scene.v().getPhaseOptions(phaseName).put(key, value);
    }

    private static void postCmdLineCheck()
        throws CompilationDeathException
    {
            if(cmdLineClasses.isEmpty())
            {
                throw new CompilationDeathException(COMPILATION_ABORTED, "Nothing to do!"); 
            }
            // Command line classes
            if (isApplication && cmdLineClasses.size() > 1)
            {

                throw new CompilationDeathException(COMPILATION_ABORTED,
                                "Can only specify one class in application mode!\n" +
                                "The transitive closure of the specified class gets loaded.\n" +
                                "(Did you mean to use single-file mode?)");
            }
    }

    /** Initializes various Soot data and calls the PackAdjuster.
     * Must be called! */
    public static void initApp()
    { 
        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add("java.");

        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add("sun.");

        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add("javax.");                
    }



    private static String[] cmdLineArgs;
    public static void setCmdLineArgs(String[] args)
    {
        cmdLineArgs = args;
    }
    
    /**
     *   Entry point for cmd line invocation of soot.
     */
    public static void main(String[] args)
    {
        setReservedNames();
        setCmdLineArgs(args);
        Main m = new Main();
        addCompilationListener(m);
        (new Thread(m)).start();
    }

    public static void setReservedNames()
    {
        Set rn = Scene.v().getReservedNames();        
        rn.add("newarray");
        rn.add("newmultiarray");
        rn.add("nop");
        rn.add("ret");
        rn.add("specialinvoke");
        rn.add("staticinvoke");
        rn.add("tableswitch");
        rn.add("virtualinvoke");
        rn.add("null_type");
        rn.add("unknown");
        rn.add("cmp");
        rn.add("cmpg");
        rn.add("cmpl");
        rn.add("entermonitor");
        rn.add("exitmonitor");
        rn.add("interfaceinvoke");
        rn.add("lengthof");
        rn.add("lookupswitch");
        rn.add("neg");
        rn.add("if");
        rn.add("abstract");
        rn.add("boolean");
        rn.add("break");
        rn.add("byte");
        rn.add("case");
        rn.add("catch");
        rn.add("char");
        rn.add("class");
        rn.add("final");
        rn.add("native");
        rn.add("public");
        rn.add("protected");
        rn.add("private");
        rn.add("static");
        rn.add("synchronized");
        rn.add("transient");
        rn.add("volatile");
	rn.add("interface");
        rn.add("void");
        rn.add("short");
        rn.add("int");
        rn.add("long");
        rn.add("float");
        rn.add("double");
        rn.add("extends");
        rn.add("implements");
        rn.add("breakpoint");
        rn.add("default");
        rn.add("goto");
        rn.add("instanceof");
        rn.add("new");
        rn.add("return");
        rn.add("throw");
        rn.add("throws");
        rn.add("null");
        rn.add("from");
	    rn.add("to");
    }

    /** Implementation of ICompilationListener */
    public  void compilationTerminated(int status, String msg) 
    {
        if(msg != null)
            System.out.println(msg);

        if(status == COMPILATION_ABORTED) { 
            System.exit(1);
        }
        else if(status == COMPILATION_SUCCEDED) {
            System.exit(0);        
        }
    }


    /** 
     *  Entry point to the soot's compilation process. Be sure to call
     *  setCmdLineArgs before invoking this method.
     *
     *  @see #setCmdLineArgs
     */
    public void run()
    {   
        try {
        totalTimer.start();
        cmdLineClasses = new HashChain();
        initApp();
        processCmdLine(cmdLineArgs);

        
        // Load necessary classes.
        {            
            Iterator it = cmdLineClasses.iterator();
        
            while(it.hasNext())
            {
                String name = (String) it.next();
                SootClass c;
                            
                c = Scene.v().loadClassAndSupport(name);                                                  
                
                if(mainClass == null)
                {
                    mainClass = c;
                    Scene.v().setMainClass(c);
                }   
                c.setApplicationClass();
            }
        
               
            // Dynamic & process classes
            it = dynamicClasses.iterator();
                
            while(it.hasNext())
                Scene.v().loadClassAndSupport((String) it.next());                 

            it = processClasses.iterator();
            
            while(it.hasNext())
            {
                String s = (String)it.next();
                Scene.v().loadClassAndSupport(s);
                Scene.v().getSootClass(s).setApplicationClass();
            }
        }

        // Generate classes to process
        { 
            if(isApplication)
            {
                List cc = new ArrayList(); cc.addAll(Scene.v().getContextClasses());
                Iterator contextClassesIt = cc.iterator();
                while (contextClassesIt.hasNext())
                    ((SootClass)contextClassesIt.next()).setApplicationClass();
            }   
                         
            // Remove/add all classes from packageInclusionMask as per piFlag
            {
                List applicationPlusContextClasses = new ArrayList();
                applicationPlusContextClasses.addAll(Scene.v().getApplicationClasses());
                applicationPlusContextClasses.addAll(Scene.v().getContextClasses());

                Iterator classIt = applicationPlusContextClasses.iterator();
                
                while(classIt.hasNext())
                {
                    SootClass s = (SootClass) classIt.next();
                    
                    if(cmdLineClasses.contains(s.getName()))
                        continue;
                        
                    Iterator packageCmdIt = packageInclusionFlags.iterator();
                    Iterator packageMaskIt = packageInclusionMasks.iterator();
                    
                    while(packageCmdIt.hasNext())
                    {
                        boolean pkgFlag = ((Boolean) packageCmdIt.next()).booleanValue();
                        String pkgMask = (String) packageMaskIt.next();
                        
                        if (pkgFlag)
                        {
                            if (s.isContextClass() && s.getPackageName().startsWith(pkgMask))
                                s.setApplicationClass();
                        }
                        else
                        {
                            if (s.isApplicationClass() && s.getPackageName().startsWith(pkgMask))
                                s.setContextClass();
                        }
                    }
                }
            }

            if (isAnalyzingLibraries)
            {
                Iterator contextClassesIt = Scene.v().getContextClasses().iterator();
                while (contextClassesIt.hasNext())
                    ((SootClass)contextClassesIt.next()).setLibraryClass();
            }
        }



	// read in the tag files
	Iterator it = sTagFileList.iterator();
	while(it.hasNext()) { 
	    try {
		File f = new File((String)it.next());
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
		
		for(String line = reader.readLine(); line  !=  null;  line = reader.readLine()) {
		    if(line.startsWith("<") ) {
			String signature = line.substring(0,line.indexOf('+'));
			int offset = Integer.parseInt(line.substring(line.indexOf('+') + 1, line.indexOf('/')));
		
			String name = line.substring(line.indexOf('/')+1, line.lastIndexOf(':'));
			String value = line.substring(line.lastIndexOf(':')+1);
	       
			//System.out.println(signature + "+" + offset + "/" + name + ":" + value);		       
			SootMethod m = Scene.v().getMethod(signature);
			
			JimpleBody body = (JimpleBody) m.retrieveActiveBody();
			List unitList = new ArrayList(body.getUnits());
			Unit u = (Unit) unitList.get(offset);

			
			if(Long.valueOf(value) == null)
			    System.out.println(value);
			((Host) u).newTag(name, Long.valueOf(value));
			
		    }
		}				
	    } catch (IOException e) {
		
	    }
	}



    // Run the whole-program packs.
        Scene.v().getPack("wjtp").apply();
        if(isOptimizingWhole)
            Scene.v().getPack("wjop").apply();


    // Handle each class individually
    {
        Iterator classIt = Scene.v().getApplicationClasses().iterator();

        while(classIt.hasNext())
            {
            SootClass s = (SootClass) classIt.next();
                
            System.out.print("Transforming " + s.getName() + "... " );
            System.out.flush();
            
            if(!isInDebugMode)
                 {
                    try 
                    {
                        handleClass(s);
                    }
                    catch(RuntimeException e)
                    {
                        System.out.println("failed due to: " + e);
                    }
                }
                else {
                    handleClass(s);
                }
                
                System.out.println();
            }
    }
        totalTimer.end();            

        // Print out time stats.

       
        if(isProfilingOptimization)
            printProfilingInformation();
   
        
        } catch (CompilationDeathException e) {
            totalTimer.end();            
            exitCompilation(e.getStatus(), e.getMessage());
            return;
        }        

        exitCompilation(COMPILATION_SUCCEDED);            
    }        

    private static void printProfilingInformation()
    {                                                   
        long totalTime = totalTimer.getTime();
                
        System.out.println("Time measurements");
        System.out.println();
                
        System.out.println("      Building graphs: " + toTimeString(graphTimer, totalTime));
        System.out.println("  Computing LocalDefs: " + toTimeString(defsTimer, totalTime));
//                System.out.println("                setup: " + toTimeString(defsSetupTimer, totalTime));
//                System.out.println("             analysis: " + toTimeString(defsAnalysisTimer, totalTime));
//                System.out.println("                 post: " + toTimeString(defsPostTimer, totalTime));
        System.out.println("  Computing LocalUses: " + toTimeString(usesTimer, totalTime));
//                System.out.println("            Use phase1: " + toTimeString(usePhase1Timer, totalTime));
//                System.out.println("            Use phase2: " + toTimeString(usePhase2Timer, totalTime));
//                System.out.println("            Use phase3: " + toTimeString(usePhase3Timer, totalTime));

        System.out.println("     Cleaning up code: " + toTimeString(cleanupAlgorithmTimer, totalTime));
        System.out.println("Computing LocalCopies: " + toTimeString(copiesTimer, totalTime));
        System.out.println(" Computing LiveLocals: " + toTimeString(liveTimer, totalTime));
//                System.out.println("                setup: " + toTimeString(liveSetupTimer, totalTime));
//                System.out.println("             analysis: " + toTimeString(liveAnalysisTimer, totalTime));
//                System.out.println("                 post: " + toTimeString(livePostTimer, totalTime));
                
        System.out.println("Coading coffi structs: " + toTimeString(resolveTimer, totalTime));

                
        System.out.println();

        // Print out time stats.
        {
            float timeInSecs;

            System.out.println("       Resolving classfiles: " + toTimeString(resolverTimer, totalTime)); 
            System.out.println(" Bytecode -> jimple (naive): " + toTimeString(conversionTimer, totalTime)); 
            System.out.println("        Splitting variables: " + toTimeString(splitTimer, totalTime));
            System.out.println("            Assigning types: " + toTimeString(assignTimer, totalTime));
            System.out.println("  Propagating copies & csts: " + toTimeString(propagatorTimer, totalTime));
            System.out.println("      Eliminating dead code: " + toTimeString(deadCodeTimer, totalTime));
            System.out.println("                Aggregation: " + toTimeString(aggregationTimer, totalTime));
            System.out.println("            Coloring locals: " + toTimeString(packTimer, totalTime));
            System.out.println("     Generating jasmin code: " + toTimeString(buildJasminTimer, totalTime));
            System.out.println("          .jasmin -> .class: " + toTimeString(assembleJasminTimer, totalTime));
            
                                            
//                    System.out.println("           Cleaning up code: " + toTimeString(cleanup1Timer, totalTime) +
//                        "\t" + cleanup1LocalCount + " locals  " + cleanup1StmtCount + " stmts");
                    
//                    System.out.println("               Split phase1: " + toTimeString(splitPhase1Timer, totalTime));
//                    System.out.println("               Split phase2: " + toTimeString(splitPhase2Timer, totalTime));
                
                        /*
                    System.out.println("cleanup2Timer:   " + cleanup2Time +
                        "(" + (cleanup2Time * 100 / totalTime) + "%) " +
                        cleanup2LocalCount + " locals  " + cleanup2StmtCount + " stmts");
*/

            timeInSecs = (float) totalTime / 1000.0f;
            float memoryUsed = (float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000.0f;
            
            System.out.println("totalTime:" + toTimeString(totalTimer, totalTime));
            
            if(isSubtractingGC)
            {
                System.out.println("Garbage collection was subtracted from these numbers.");
                System.out.println("           forcedGC:" + 
                                   toTimeString(Timer.forcedGarbageCollectionTimer, totalTime));
            }

            System.out.println("stmtCount: " + stmtCount + "(" + toFormattedString(stmtCount / timeInSecs) + " stmt/s)");
                    
            System.out.println("totalFlowNodes: " + totalFlowNodes + 
                               " totalFlowComputations: " + totalFlowComputations + " avg: " + 
                               truncatedOf((double) totalFlowComputations / totalFlowNodes, 2));
        }
    }

    private static String toTimeString(Timer timer, long totalTime)
    {
        DecimalFormat format = new DecimalFormat("00.0");
        DecimalFormat percFormat = new DecimalFormat("00.0");
        
        long time = timer.getTime();
        
        String timeString = format.format(time / 1000.0); // paddedLeftOf(new Double(truncatedOf(time / 1000.0, 1)).toString(), 5);
        
        return (timeString + "s" + " (" + percFormat.format(time * 100.0 / totalTime) + "%" + ")");   
    }
    
    private static String toFormattedString(double value)
    {
        return paddedLeftOf(new Double(truncatedOf(value, 2)).toString(), 5);
    }

    /** Attach JimpleBodies to the methods of c. */
    private static void attachJimpleBodiesFor(SootClass c)
    {
        Iterator methodIt = c.getMethods().iterator();
           
        while(methodIt.hasNext())
        {   
            SootMethod m = (SootMethod) methodIt.next();
        
            if(!m.isConcrete())
                continue;
                                
            if(!m.hasActiveBody()) 
            {
                m.setActiveBody(m.getBodyFromMethodSource("jb"));

                Scene.v().getPack("jtp").apply(m.getActiveBody());
                if(isOptimizing) 
                    Scene.v().getPack("jop").apply(m.getActiveBody());
            }            
        }
    }

    private static void handleClass(SootClass c)
    {
        FileOutputStream streamOut = null;
        PrintWriter writerOut = null;
        
        String fileName;
        
        if(!outputDir.equals(""))
            fileName = outputDir + fileSeparator;
        else
            fileName = "";
        
        fileName += c.getName() + getExtensionFor(targetExtension);
        
      
        if(targetExtension != CLASS)
        {   
            try {
                streamOut = new FileOutputStream(fileName);
                writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
            }
            catch (IOException e)
            {
                System.out.println("Cannot output file " + c.getName() + getExtensionFor(targetExtension));
            }
        }

        boolean produceBaf = false;
        boolean produceGrimp = false;
        boolean produceDava = false;
        
        // Determine paths
        
        {
            String endResult;
                               
            switch(targetExtension) {
            case JIMPLE:
            case NJIMPLE:
            case JIMP:                   
                endResult = "jimple";
                break;
            case GRIMP:
            case GRIMPLE:
                endResult = "grimp";
                break;
            case DAVA:
                endResult = "dava";
                break;
            case BAF:
            case B:
                endResult = "baf";
                break;
            default:
                endResult = getExtensionFor(finalRep).substring(1);
            }
        
            if(endResult.equals("baf"))
            {
                produceBaf = true; 
            }
            else if(endResult.equals("grimp"))
            {
                produceGrimp = true;
            }
            else if(endResult.equals("dava"))
            {
                produceGrimp = true;
                produceDava = true;
            }
        }

        // Build all necessary bodies
        {
            Iterator methodIt = c.getMethods().iterator();
           
            while(methodIt.hasNext())
            {   
                SootMethod m = (SootMethod) methodIt.next();
        
                if(!m.isConcrete())
                    continue;
                                
                // Build Jimple body and transform it.
                {
                    JimpleBody body = (JimpleBody) m.retrieveActiveBody();
              		    
		    
                    Scene.v().getPack("jtp").apply(body);
                    
                    if(isOptimizing) 
                        Scene.v().getPack("jop").apply(body);
                }
                
                if(produceGrimp)
                {
                    if(isOptimizing)
                        m.setActiveBody(Grimp.v().newBody(m.getActiveBody(), "gb", "aggregate-all-locals"));
                    else
                        m.setActiveBody(Grimp.v().newBody(m.getActiveBody(), "gb"));
                        
                    if(isOptimizing)
                        Scene.v().getPack("gop").apply(m.getActiveBody());
                        
                }
                else if(produceBaf)
                {   
                     m.setActiveBody(Baf.v().newBody((JimpleBody) m.getActiveBody()));

                     if(isOptimizing) 
                        Scene.v().getPack("bop").apply(m.getActiveBody());
                } 
                
                if(produceDava)
                {
                    m.setActiveBody(Dava.v().newBody(m.getActiveBody(), "db"));
                }    
            }
            
        }

        switch(targetExtension) {
        case JASMIN:
            if(c.containsBafBody())
                new soot.baf.JasminClass(c).print(writerOut);            
            else
                new soot.jimple.JasminClass(c).print(writerOut);
            break;
        case JIMP:            
            c.printTo(writerOut, PrintJimpleBodyOption.USE_ABBREVIATIONS);
            break;
        case NJIMPLE:
            c.printTo(writerOut, PrintJimpleBodyOption.NUMBERED);
            break;
        case B:
            c.printTo(writerOut, soot.baf.PrintBafBodyOption.USE_ABBREVIATIONS);
            break;
        case BAF:
        case JIMPLE:
        case GRIMPLE:
            writerOut = new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));
            c.printJimpleStyleTo(writerOut, 0);
            break;
        case DAVA:
            c.printTo(writerOut, PrintGrimpBodyOption.USE_ABBREVIATIONS);
            break;
        case GRIMP:
            c.printTo(writerOut, PrintGrimpBodyOption.USE_ABBREVIATIONS);
            break;
        case CLASS:
            c.write(outputDir);
            break;
        default:
            throw new RuntimeException();
        }
        
        if(targetExtension != CLASS)
        {
            try {
                writerOut.flush();
                streamOut.close();
            }
            catch(IOException e)
            {
                System.out.println("Cannot close output file " + fileName);
            }
        }

        // Release bodies
        {
            Iterator methodIt = c.getMethods().iterator();
                
            while(methodIt.hasNext())
            {   
                SootMethod m = (SootMethod) methodIt.next();
                
                if(m.hasActiveBody())
                    m.releaseActiveBody();
            }
        }
    }
    
    public static double truncatedOf(double d, int numDigits)
    {
        double multiplier = 1;
        
        for(int i = 0; i < numDigits; i++)
            multiplier *= 10;
            
        return ((long) (d * multiplier)) / multiplier;
    }
    
     public static String paddedLeftOf(String s, int length)
      {
        if(s.length() >= length)
            return s;
        else {
            int diff = length - s.length();
            char[] padding = new char[diff];
            
            for(int i = 0; i < diff; i++)
                padding[i] = ' ';
            
            return new String(padding) + s;
        }    
    }
}




class CompilationDeathException extends RuntimeException
{
    private String mMsg;
    private int mStatus;

    CompilationDeathException(int status, String msg)
    {
        mMsg = msg;
        mStatus = status;
    }
        
    CompilationDeathException(int status)
    {
        mStatus = status;
    }

    public int getStatus()
    {
        return mStatus;
    }
    public String getMsg()
    {
        return mMsg;
    }
}





