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
import soot.gui.*;
import java.util.*;
import soot.jimple.*;
import soot.grimp.*;
import soot.baf.*;
import soot.jimple.toolkits.invoke.*;
import soot.baf.toolkits.base.*;
import soot.toolkits.scalar.*;
import soot.dava.*;

import soot.jimple.toolkits.annotation.arraycheck.*;
import soot.jimple.toolkits.annotation.nullcheck.*;
import soot.jimple.toolkits.annotation.profiling.*;
import soot.jimple.toolkits.annotation.tags.*;

import java.io.*;
import java.text.*;

import gnu.getopt.*;


/** Main class for Soot; provides Soot's command-line user interface. */
public class Main implements Runnable
{        
    public Date start;
    public Date finish;

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
    
    static List dynamicPackages = new ArrayList();

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

    static boolean doArrayBoundsCheck = false;
    static boolean doNullPointerCheck = false;
           
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
    static int finalRep = BAF;
      // The final rep to be used is Baf; conclusion of our CC2000 paper!

    private static List sTagFileList = new ArrayList(); 

    private static List getClassesUnder(String aPath) 
    {
        File file = new File(aPath);
        List fileNames = new ArrayList();

        File[] files = file.listFiles();
        if (files == null)
        {
            files = new File[1]; files[0] = file;
        }
        
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
        isOptimizing = val;
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

    public static void addDynamicPackage(String str)
         throws CompilationDeathException
    {
        if (!isApplication) {
            throw new CompilationDeathException(COMPILATION_ABORTED, "Dynamic-package flag only valid in application mode!");
        }
        
        StringTokenizer tokenizer = new StringTokenizer(str, ",");
        while(tokenizer.hasMoreTokens())
            dynamicPackages.add(tokenizer.nextToken());
    }

    /* This is called after sootClassPath has been defined. */
    public static void markPackageAsDynamic(String str)
    {
        StringTokenizer strtok = new StringTokenizer(Scene.v().getSootClassPath(), ":");
        while(strtok.hasMoreTokens()) {
            HashSet set = new HashSet(0);
            String path = strtok.nextToken();

            // For jimple files
            List l = getClassesUnder(path);
            for (Iterator it = l.iterator(); it.hasNext(); ) {
                String filename = (String)it.next();
                if (filename.startsWith(str))
                    set.add(filename);
            }

            // For class files;
            path = path + "/";
            StringTokenizer tokenizer = new StringTokenizer(str, ".");
            while(tokenizer.hasMoreTokens()) {
                path = path + tokenizer.nextToken();
                if (tokenizer.hasMoreTokens())
                    path = path + "/";
            }
            l = getClassesUnder(path);
            for (Iterator it = l.iterator(); it.hasNext(); )
                set.add(str+"."+((String)it.next()));
            dynamicClasses.addAll(set);
        }
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
            throw new CompilationDeathException(COMPILATION_ABORTED, 
					 "Illegal argument \"" + rep + "\" for final-rep option"
					 + "\nvalid args are: [baf|grimp|jimple]" );
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

    public static void setAnnotationPhases(String opt)
    {
	if (opt.equals("both"))
	{
	    doNullPointerCheck = true;
	    doArrayBoundsCheck = true;
	}
	else
        if (opt.equals("arraybounds"))
	{
	    doArrayBoundsCheck = true;
	}
	else
	if (opt.equals("nullpointer"))
	{
	    doNullPointerCheck = true;
	}
	else
	    System.out.println("Annotation phase \"" + opt + "\" is not valid.");

	// put null pointer check before bounds check for profiling purpose
	if (doNullPointerCheck)
	{
	    Scene.v().getPack("jtp").add(new Transform("jtp.npc", NullPointerChecker.v()));
	}

	if (doArrayBoundsCheck)
	{
	    Scene.v().getPack("wjtp2").add(new Transform("wjtp2.ra", RectangularArrayFinder.v()));
	    Scene.v().getPack("jtp").add(new Transform("jtp.abc", ArrayBoundsChecker.v()));
	}

	if (doNullPointerCheck || doArrayBoundsCheck)
	{
	    Scene.v().getPack("jtp").add(new Transform("jtp.profiling", ProfilingGenerator.v()));
	    // turn on the tag aggregator
	    CodeAttributeGenerator.v().registerAggregator(new ArrayNullTagAggregator(true));
	}
    }

    private static void printVersion()
    {
         // $Format: "            System.out.println(\"Soot version 1.2.2 (build $ProjectVersion$)\");"$
            System.out.println("Soot version 1.2.2 (build 1.2.2.dev.3)");
            System.out.println("Copyright (C) 1997-2001 Raja Vallee-Rai (rvalleerai@sable.mcgill.ca).");
            System.out.println("All rights reserved.");
            System.out.println("");
            System.out.println("Contributions are copyright (C) 1997-2001 by their respective contributors.");
            System.out.println("See individual source files for details.");
            System.out.println("");
            System.out.println("Soot comes with ABSOLUTELY NO WARRANTY.  Soot is free software,");
            System.out.println("and you are welcome to redistribute it under certain conditions.");
            System.out.println("See the accompanying file 'license.html' for details.");
    }

    private static void printHelp()
    {
            System.out.println("Syntax:");
            System.out.println("        (single-file mode) soot [option]* classname ...  ");
            System.out.println("        (application mode) soot --app [option]* mainClassName");
            System.out.println("");
            System.out.println("General options:");
            System.out.println("  --version                    output version information and exit");
            System.out.println("  -h, --help                   display this help and exit");
            System.out.println("");
            System.out.println("Output options:");
            System.out.println("  -b, --b                      produce .b (abbreviated .baf) files");
            System.out.println("  -B, --baf                    produce .baf code");
            System.out.println("  -j, --jimp                   produce .jimp (abbreviated .jimple) files");
            System.out.println("  -J, --jimple                 produce .jimple code");
            System.out.println("  -g, --grimp                  produce .grimp (abbreviated .grimple) files");
            System.out.println("  -G, --grimple                produce .grimple files");
            System.out.println("  -s, --jasmin                 produce .jasmin files");
            System.out.println("  -c, --class                  produce .class files");
            System.out.println("  -d PATH                      store produced files in PATH");
            System.out.println("");
            System.out.println("Application mode options:");
            System.out.println("  -x, --exclude PACKAGE        marks classfiles in PACKAGE (e.g. java.)"); 
            System.out.println("                               as context classes");
            System.out.println("  -i, --include PACKAGE        marks classfiles in PACKAGE (e.g. java.util.)");
            System.out.println("                               as application classes");
            System.out.println("  -a, --analyze-context        label context classes as library");
            System.out.println("  --dynamic-path PATH          marks all class files in PATH as ");
            System.out.println("                               potentially dynamic classes");
            System.out.println("  --dynamic-packages PACKAGES  marks classfiles in PACKAGES (separated by");
            System.out.println("                               commas) as potentially dynamic classes");
            System.out.println("");
            System.out.println("Single-file mode options:");
            System.out.println("  --process-path PATH          process all classes on the PATH");
            System.out.println("");
            System.out.println("Construction options:");
            System.out.println("  --final-rep REP              produce classfile/jasmin from REP ");
            System.out.println("                               (jimple, grimp, or baf)");
            System.out.println("");
            System.out.println("Optimization options:");
            System.out.println("  -O  --optimize               perform scalar optimizations on the classfiles");
            System.out.println("  -W  --whole-optimize         perform whole program optimizations on the ");
            System.out.println("                               classfiles");
            System.out.println("");
            System.out.println("Miscellaneous options:");
            System.out.println("  --soot-classpath PATH        uses PATH as the classpath for finding classes");
            System.out.println("  --src-prec [jimple|class]    sets the source precedence for Soot");
            System.out.println("  -t, --time                   print out time statistics about tranformations");
            System.out.println("  --subtract-gc                attempt to subtract the gc from the time stats");
            System.out.println("  -v, --verbose                verbose mode");
            System.out.println("  --debug                      avoid catching exceptions");
            System.out.println("  -p, --phase-option PHASE-NAME KEY1[:VALUE1],KEY2[:VALUE2],...,KEYn[:VALUEn]");
            System.out.println("                               set run-time option KEY to VALUE for PHASE-NAME");
            System.out.println("                               (default for VALUE is true)");
			System.out.println("  -A  --annotation [both|nullpointer|arraybounds]");
			System.out.println("                               turn on the annotation for null pointer and/or ");
			System.out.println("                               array bounds check. ");
			System.out.println("                               more options are in the document. ");
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
		// check --new-cmdline-parser option 
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("--use-CommandLine")) {
				processCmdLine_CommandLine(args);
				return;
            }
            if(arg.equals("--use-Getopt")) {
				processCmdLine_Getopt(args);
				return;
            }
		}
		processCmdLine_classic(args);
	}

    private static void processCmdLine_CommandLine(String[] args)
        throws CompilationDeathException
    {
        if(args.length == 0) {
			printHelp();
			throw new CompilationDeathException(COMPILATION_ABORTED, "don't know what to do!");
		}
		
		CommandLine cl = new CommandLine(args);

		// handle --app option first
		while (cl.contains("app")) {
			setAppMode(true);
		}
		
		// Handle all the options
		while (cl.contains("j") || cl.contains("jimp"))
			setTargetRep(JIMP);
		
	    while (cl.contains("njimple"))
		   setTargetRep(NJIMPLE);

	    while (cl.contains("s") || cl.contains("jasmin"))
		    setTargetRep(JASMIN);

		while (cl.contains("J") || cl.contains("jimple"))
		    setTargetRep(JIMPLE);

		while (cl.contains("B") || cl.contains("baf"))
			setTargetRep(BAF);
		
		while (cl.contains("b"))
		    setTargetRep(B);

		while (cl.contains("g") || cl.contains("grimp"))
		    setTargetRep(GRIMP);

		while (cl.contains("G") || cl.contains("grimple"))
		    setTargetRep(GRIMPLE);

		while (cl.contains("c") || cl.contains("class"))
		    setTargetRep(CLASS);

		while (cl.contains("dava"))
		    setTargetRep(DAVA);

		while (cl.contains("X") || cl.contains("xml"))
		    produceXmlOutput = true;

		while (cl.contains("O") || cl.contains("optimize"))
		    setOptimizing(true);

		while (cl.contains("W") || cl.contains("whole-optimize"))
		    setOptimizingWhole(true);

		while (cl.contains("t") || cl.contains("time"))
			setProfiling(true);
		
		while (cl.contains("subtract-gc"))
			setSubstractingGC(true);

		while (cl.contains("v") || cl.contains("verbose"))
		    setVerbose(true);
		
		while (cl.contains("soot-class-path") 
			   || cl.contains("soot-classpath")) {
			Scene.v().setSootClassPath(cl.getValue());
		}

		while (cl.contains("d")) {
			String s = cl.getValueOf("d");
			 if (s.equals("")) {
				 System.err.println ("Warning: -d option used without argument");
				 System.err.println ("         Using default output directory");
			 }
			 outputDir = s;
		}
		
		while (cl.contains("x") || cl.contains("exclude")) {
			String s = cl.getValue();
			if (s.equals("")) {
				System.err.println ("Warning: exclude-package option used without argument");
			} else {
				addExclude(s);
			}
		}
		
		while (cl.contains("i") || cl.contains("include")) {
			String s = cl.getValue();
			if (s.equals("")) {
				System.err.println ("Warning: include-package option used without argument");
			} else {
				addInclude(s);
			}
		}

		while (cl.contains("a") || cl.contains("analyze-context")) {
		    setAnalyzingLibraries(true);
		}

	    while (cl.contains("final-rep")) {
			String s = cl.getValueOf("final-rep");
			if (s.equals("")) {
				throw new CompilationDeathException(COMPILATION_ABORTED,
													"final-rep requires an argument\n"
													+ "valid args are: [baf|grimp|jimple]");
			} else {
				setFinalRep(s);
			}
		}

	    while (cl.contains("p") || cl.contains("phase-option")) {
			String s = cl.getValue();
			if (s.equals("")) {
				 System.err.println ("Warning: phase-option option used without argument");
			} else {
				processPhaseOptions(s);
			}
		}

		while (cl.contains("debug"))
		    setDebug(true);

		while (cl.contains("dynamic-path")) {
			addDynamicPath(cl.getValueOf("dynamic-path"));
		}

		while (cl.contains("dynamic-packages")) {
			addDynamicPackage(cl.getValueOf("dynamic-packages"));
		}
		
		while (cl.contains("process-path")) {
			addProcessPath(cl.getValueOf("process-path"));                    
		}

		while (cl.contains("src-prec")) {
			setSrcPrecedence(cl.getValueOf("src-prec"));
		}

		while (cl.contains("tag-file")) {
			sTagFileList.add(cl.getValueOf("tag-file"));
		}

		while (cl.contains("A") || cl.contains("annotation")) {
			setAnnotationPhases(cl.getValue());
		}

		while (cl.contains("version")) {
		    printVersion();
		    throw new CompilationDeathException(COMPILATION_SUCCEDED);
		}
		
		while (cl.contains("h") || cl.contains("help")) {
		    printHelp();
		    throw new CompilationDeathException(COMPILATION_SUCCEDED);
		}

		while (cl.contains("use-Getopt") || cl.contains("use-CommandLine")
			   || cl.contains("classic")) {
			// already handled: ignore
		}

		cl.completeOptionsCheck();
		
		Iterator argIt = cl.getNonOptionArguments().iterator();
		while (argIt.hasNext())
			cmdLineClasses.add((String)argIt.next());
		
        postCmdLineCheck();
    }


    private static void processCmdLine_Getopt(String[] args)
        throws CompilationDeathException
    {
        if(args.length == 0)
			{
				printHelp();
				throw new CompilationDeathException(COMPILATION_ABORTED, "don't know what to do!");
			}

		// handle --app option first
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("--app")) {
                setAppMode(true);
            }
		}

        // Initialize the options for getOpt
		addGetoptOption('j', "jimp", LongOpt.NO_ARGUMENT);
		addGetoptOption(-10, "njimple", LongOpt.NO_ARGUMENT);
		addGetoptOption('s', "jasmin", LongOpt.NO_ARGUMENT);
		addGetoptOption('J', "jimple", LongOpt.NO_ARGUMENT);
		addGetoptOption('B', "baf", LongOpt.NO_ARGUMENT);
		addGetoptOption('b', "b", LongOpt.NO_ARGUMENT);
		addGetoptOption('g', "grimp", LongOpt.NO_ARGUMENT);
		addGetoptOption('G', "grimple", LongOpt.NO_ARGUMENT);
		addGetoptOption('c', "class", LongOpt.NO_ARGUMENT);
		addGetoptOption(-11, "dava", LongOpt.NO_ARGUMENT);
		addGetoptOption('X', "xml", LongOpt.NO_ARGUMENT);
		addGetoptOption('O', "optimize", LongOpt.NO_ARGUMENT);
		addGetoptOption('W', "whole-optimize", LongOpt.NO_ARGUMENT);
		addGetoptOption('t', "time", LongOpt.NO_ARGUMENT);
		addGetoptOption(-12, "substract-gc", LongOpt.NO_ARGUMENT);
		addGetoptOption('v', "verbose", LongOpt.NO_ARGUMENT);
		addGetoptOption(-13, "soot-class-path", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption(-13, "soot-classpath", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption('d', "output-dir", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption('x', "exclude", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption('i', "include", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption('a', "analyze-context", LongOpt.NO_ARGUMENT);
		addGetoptOption(-14, "final-rep", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption('p', "phase-option", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption(-15, "debug", LongOpt.NO_ARGUMENT);
		addGetoptOption(-16, "dynamic-path", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption(-17, "dynamic-packages", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption(-18, "process-path", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption(-19, "src-prec", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption(-20, "tag-file", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption('A', "annotation", LongOpt.REQUIRED_ARGUMENT);
		addGetoptOption(-21, "version", LongOpt.NO_ARGUMENT);
		addGetoptOption('h', "help", LongOpt.NO_ARGUMENT);

		// options handled elsewhere
		addGetoptOption('-', "use-Getopt", LongOpt.NO_ARGUMENT);
		addGetoptOption('-', "use-CommandLine", LongOpt.NO_ARGUMENT);
		addGetoptOption('-', "classic", LongOpt.NO_ARGUMENT);

        // initialize getopt
		Object[] objArray = longOpts.toArray();
		LongOpt[] longOptsArray = new LongOpt[objArray.length];
		for (int i=0; i<objArray.length; i++) {
			longOptsArray[i] = (LongOpt)objArray[i];
		}
		// I sometimes have ClassCastException with the following line
		// so I use the ugly thing above, which seems to work
		// LongOpt[] longOptsArray = (LongOpt[])longOpts.toArray();
		Getopt g = new Getopt("Soot", args, shortOpts, longOptsArray);
		g.setOpterr(false); // We'll do our own error handling

		// Handle all the options
		int c;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 0:
				// should never reached that point, due to the way
				// the LongOption are created
				System.err.println("000000000000");
				break;
			case 1:
				// non-option argument
				// behaviour triggered by the - at the beginning of shortOpts
				cmdLineClasses.add(g.getOptarg());
				break;
			case 'j':
				setTargetRep(JIMP);
				break;
			case -10:
			    setTargetRep(NJIMPLE);
				break;
			case 's':
				setTargetRep(JASMIN);
				break;
			case 'J':
				setTargetRep(JIMPLE);
				break;
			case 'B':
				setTargetRep(BAF);
				break;
			case 'b':
				setTargetRep(B);
				break;
			case 'g':
				setTargetRep(GRIMP);
				break;
			case 'G':
				setTargetRep(GRIMPLE);
				break;
			case 'c':
				setTargetRep(CLASS);
				break;
			case -11:
				setTargetRep(DAVA);
				break;
			case 'X':
				produceXmlOutput = true;
				break;
			case 'O':
				setOptimizing(true);
				break;
			case 'W':
				setOptimizingWhole(true);
				break;
			case 't':
				setProfiling(true);
				break;
			case -12:
				setSubstractingGC(true);
				break;
			case 'v':
				setVerbose(true);
				break;
			case -13:
				Scene.v().setSootClassPath(g.getOptarg());
				break;
			case 'd':
				outputDir = g.getOptarg();
				break;
			case 'x':
				addExclude(g.getOptarg());
				break;
			case 'i':
				addInclude(g.getOptarg());
				break;
			case 'a':
				setAnalyzingLibraries(true);
				break;
			case -14:
				setFinalRep(g.getOptarg());
				break;
			case 'p':
				processPhaseOptions(g.getOptarg());
				break;
			case -15:
				setDebug(true);
				break;
			case -16:
				addDynamicPath(g.getOptarg());
				break;
			case -17:
				addDynamicPackage(g.getOptarg());
				break;
			case -18:
				addProcessPath(g.getOptarg());
				break;
			case -19:
				setSrcPrecedence(g.getOptarg());
				break;
			case -20:
				sTagFileList.add(g.getOptarg());
			case 'A':
				setAnnotationPhases(g.getOptarg());
				break;
			case -21:
				printVersion();
				throw new CompilationDeathException(COMPILATION_SUCCEDED);
			case 'h':
				printHelp();
				throw new CompilationDeathException(COMPILATION_SUCCEDED);
			case ':':
				// option that needs an argument and did not get one
				// this behaviour is triggered by the : in shortOpts[1]
				String optName;
				// This technic is the best to get the value of the option
				// (which is difficult when you use a long option because
				// getopt will by default give you the id of the option.
				// It fails when the user uses the option with different
				// syntaxes in succession.
				if (g.getOptopt() < 0 
					|| longOptsArray[g.getLongind()].getVal() != g.getOptopt() ) {
					// this is definitely a short option
					optName = String.valueOf((char)g.getOptopt());
				} else {
					optName = longOptsArray[g.getLongind()].getName();
				}
				throw new CompilationDeathException(COMPILATION_ABORTED,
													"Option " + optName
													+ "requires an argument!");
			case '?':
				// invalid option
				throw new 
					CompilationDeathException(COMPILATION_ABORTED,
											  "Option " 
											  + (g.getOptopt() == 0 ? 
												 args[g.getOptind()-1].substring(2)
												 : String.valueOf((char)g.getOptopt()) )
											  + " is not valid");
			default:
				// we should only get here for the unhandled options
			}
	}

		// get trailing non options ( after '--' )
		for (int i = g.getOptind(); i < args.length ; i++)
			cmdLineClasses.add(args[i]);

        postCmdLineCheck();
	}

		
	private static String shortOpts = "-:";
	private static ArrayList longOpts = new ArrayList();
	
	private static void addGetoptOption(int id, String name, int has_arg) {
		
		if (id > 0 && shortOpts.indexOf((char)id) == -1) {
			StringBuffer opts = new StringBuffer(shortOpts);
			opts.append((char)id);
			if (has_arg == LongOpt.REQUIRED_ARGUMENT)
				opts.append(':');
			// supported but discouraged
			if (has_arg == LongOpt.OPTIONAL_ARGUMENT)
				opts.append("::");
			shortOpts = opts.toString();
		}

		longOpts.add(new LongOpt(name, has_arg, null, id));
		
	}


    private static void processCmdLine_classic(String[] args)
        throws CompilationDeathException
    {
        if(args.length == 0) {
			printHelp();
			throw new CompilationDeathException(COMPILATION_ABORTED, "don't know what to do!");
	    }

		// handle --app option first
        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("--app")) {
                setAppMode(true);
            }
		}

        // Handle all the options
        for(int i = 0; i < args.length; i++) {
			String arg = args[i];
			if(arg.equals("--app"))
			    continue; // ignore
			else if(arg.equals("-j") || arg.equals("--jimp"))
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
			else if(arg.equals("--soot-class-path") 
					|| arg.equals("--soot-classpath")) {
				if(++i < args.length)
					Scene.v().setSootClassPath(args[i]);
			}
			else if(arg.equals("-d")) {
				if(++i < args.length)
					outputDir = args[i];
			}
			else if(arg.equals("-x") || arg.equals("--exclude")) {
				if(++i < args.length)
					addExclude(args[i]);
			}
			else if(arg.equals("-i") || arg.equals("--include")) {
				if(++i < args.length)
					addInclude(args[i]);
			}
			else if(arg.equals("-a") || arg.equals("--analyze-context"))
				setAnalyzingLibraries(true);
			else if(arg.equals("--final-rep")) {
				if(++i < args.length)
					setFinalRep(args[i]);
			}
			else if (arg.equals("-p") || arg.equals("--phase-option")) {
				if(i+2 < args.length)
					processPhaseOptions(args[++i], args[++i]);                
				//syntax -p phase-name:phase-options
				//if(++i < args.length) 
				//    processPhaseOption(args[i]);                
			}
			else if (arg.equals("--debug"))
				setDebug(true);
			else if (arg.equals("--dynamic-path")) {
				if(++i < args.length) 
					addDynamicPath(args[i]);
			}
			else if (arg.equals("--dynamic-packages")) {
				if(++i < args.length)
					addDynamicPackage(args[i]);
			}
			else if (arg.equals("--process-path")) {
				if(++i < args.length)
					addProcessPath(args[i]);                    
			}
			else if(arg.equals("--src-prec")) {
				if(++i < args.length)                    
					setSrcPrecedence(args[i]);
			}
			else if(arg.equals("--tag-file")) {
				if(++i < args.length)
					sTagFileList.add(args[i]);
			}
			else if(arg.equals("-A") || arg.equals("--annotation")) {
				if (++i < args.length)
					setAnnotationPhases(args[i]);
			}
			else if(arg.equals("--version")) {
				printVersion();
				throw new CompilationDeathException(COMPILATION_SUCCEDED);            
			}
			else if(arg.equals("-h") || arg.equals("--help")) {
				printHelp();
				throw new CompilationDeathException(COMPILATION_SUCCEDED);            
			}

			else if (arg.equals("--classic") || arg.equals("--use-Getop")
						 || arg.equals("--use-CommandLine")) {
				// handled elsewhere
			}
			else if(arg.startsWith("-")) {
				System.out.println("Unrecognized option: " + arg);
				printHelp();
				throw new CompilationDeathException(COMPILATION_ABORTED);
			}
			else if(arg.startsWith("@")) {
				try {
					File fn = new File(arg.substring(1));
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fn)));
					List argsList = new LinkedList();
					while (br.ready())
						argsList.add(br.readLine());
					br.close();
					processCmdLine_classic((String[])argsList.toArray(new String[argsList.size()]));
				} catch (IOException e) {
					throw new CompilationDeathException(COMPILATION_ABORTED,
														"Error reading file "+arg.substring(1));
				}
			}
			else {                    
				cmdLineClasses.add(arg);
			}
		}
        postCmdLineCheck();
    }
    
    private static void exitCompilation(int status)
    {
        exitCompilation(status, "") ;
    }

    private static void exitCompilation(int status, String msg)
    {
	Scene.v().reset();
        Iterator it = compilationListeners.iterator();
	while(it.hasNext()) 
	    ((ICompilationListener)it.next()).compilationTerminated(status, msg);
	    
	    
    }

	// called by the new command-line parser
	private static void processPhaseOptions(String phaseOptions) {
		int idx = phaseOptions.indexOf(':');
		if (idx == -1) {
			throw new CompilationDeathException(COMPILATION_ABORTED,
												"Invalid phase option: " 
												+ phaseOptions);
		}
		String phaseName = phaseOptions.substring(0, idx);		
		StringTokenizer st = new StringTokenizer(phaseOptions.substring(idx+1), ",");
		while (st.hasMoreTokens()) {
			processPhaseOption(phaseName, st.nextToken(), '=');
		}
    }
	
	// called by the "classic" command-line parser
    private static void processPhaseOptions(String phaseName, String option) {
		StringTokenizer st = new StringTokenizer(option, ",");
		while (st.hasMoreTokens()) {
			processPhaseOption(phaseName, st.nextToken(), ':');
		}
    }

    private static void processPhaseOption(String phaseName, String option,
										   char delimiter)
    {
        int delimLoc = option.indexOf(delimiter);
        String key = null, value = null;
	
        if (delimLoc == -1)
            {
                key = option;
                value = "true";
            }
        else 
            {
                key = option.substring(0, delimLoc);
                value = option.substring(delimLoc+1);
            }

        Scene.v().getPhaseOptions(phaseName).put(key, value);
    }

    private static void postCmdLineCheck()
        throws CompilationDeathException
    {
            if(cmdLineClasses.isEmpty() && processClasses.isEmpty())
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
        ConsoleCompilationListener ccl = new ConsoleCompilationListener();
        addCompilationListener(ccl);
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

    /** 
     *  Entry point to the soot's compilation process. Be sure to call
     *  setCmdLineArgs before invoking this method.
     *
     *  @see #setCmdLineArgs
     */
    public void run()
    {   

        start = new Date();

        try {
        totalTimer.start();
        cmdLineClasses = new HashChain();
        initApp();
        processCmdLine(cmdLineArgs);
        System.out.println("Soot started on "+start);

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
        
            // Dynamic packages
            it = dynamicPackages.iterator();
            while(it.hasNext())
                markPackageAsDynamic((String)it.next());
   
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
                Iterator contextClassesIt = Scene.v().getContextClasses().snapshotIterator();
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
                Iterator contextClassesIt = Scene.v().getContextClasses().snapshotIterator();
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

			
		    }
		}				
	    } catch (IOException e) {
		
	    }
	}



    // Run the whole-program packs.
        Scene.v().getPack("wjtp").apply();
        if(isOptimizingWhole)
            Scene.v().getPack("wjop").apply();

    // Give one more chance
    	Scene.v().getPack("wjtp2").apply();
	
    // System.gc();

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
		    e.printStackTrace();
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

        finish = new Date();
        System.out.println("Soot finished on "+finish);
        long runtime = finish.getTime() - start.getTime();
        System.out.println("Soot has run for "+(runtime/60000)+" min. "+((runtime%60000)/1000)+" sec.");
     
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



