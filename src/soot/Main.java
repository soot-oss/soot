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
import soot.jimple.toolkits.invoke.*;

import soot.tagkit.*;
import soot.dava.toolkits.base.misc.*;

import soot.options.Options;

import java.io.*;
import java.text.*;

/** Main class for Soot; provides Soot's command-line user interface. */
public class Main implements Runnable
{   
    // TODO: the following string should be updated by the source control
    // No it shouldn't. Prcs is horribly borken in this respect, and causes
    // the code to not compile all the time.
    public static final String versionString = "2.0";

    public static Options opts;
    
    public Date start;
    public Date finish;

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
    
    public static String getExtensionFor(int rep)
    {
        String str = null;

        switch(rep) {
        case Options.outputFormat_baf:
            str = ".baf";
            break;
        case Options.outputFormat_b:
            str = ".b";
            break;
            
        case Options.outputFormat_jimple:
            str = ".jimple";
            break;                        
        case Options.outputFormat_jimp:
            str = ".jimp";
            break;
        case Options.outputFormat_grimp:
            str = ".grimp";
            break;
        case Options.outputFormat_grimple:
            str = ".grimple";
            break;
        case Options.outputFormat_classFile:
            str = ".class";
            break;
        case Options.outputFormat_dava:
	    str = ".java";
            break;
        case Options.outputFormat_jasmin:
            str = ".jasmin";
            break;
             
        case Options.outputFormat_xml:
            str = ".xml";
            break;

        default:
            throw new RuntimeException();
        }
	return str;
    }

    public static String getFileNameFor( SootClass c, int rep)
    {
	// add an option for no output
	if (rep == Options.outputFormat_none) return null;

	StringBuffer b = new StringBuffer();

	if (outputDir != null)
	    b.append( outputDir);
	
	if ((b.length() > 0) && (b.charAt( b.length() - 1) != fileSeparator))
	    b.append( fileSeparator);
	
	if (rep != Options.outputFormat_dava) {
	    b.append( c.getName());
	    b.append( getExtensionFor( rep));

	    return b.toString();
	}

	b.append( "dava");
	b.append( fileSeparator);
	{
	    String classPath = b.toString() + "classes";
	    File dir = new File( classPath);
	    
	    if (!dir.exists())
		try {
		    dir.mkdirs();
		}
		catch( SecurityException se) {
		    System.err.println( "Unable to create " + classPath);
		    System.exit(0);
		}
	}

	b.append( "src");
	b.append( fileSeparator);
	
	String fixedPackageName = c.getJavaPackageName();
	if (fixedPackageName.equals( "") == false) {
	    b.append( fixedPackageName.replace( '.', fileSeparator));
	    b.append( fileSeparator);
	}

	{
	    String path = b.toString();
	    File dir = new File( path);

	    if (!dir.exists())
		try {
		    dir.mkdirs();
		}
		catch( SecurityException se) {
		    System.err.println( "Unable to create " + path);
		    System.exit(0);
		}
	}

	b.append( c.getShortJavaStyleName());
	b.append( ".java");

	return b.toString();
    }


    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    public static boolean isInDebugMode;
   
    static private boolean withCache = false;
    static private String cacheDir = null;
    static private boolean useJavaStyle = false;

    static public int totalFlowNodes, totalFlowComputations;

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
        conversionTimer = new Timer("conversion"),
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

  // hack for J2ME, patch provided by Stephen Chen
  // by default, this is set as false, to use SOOT with J2ME library
  // flag isJ2ME true. Type system works around Clonable, Serializeable.
  // see changes in: 
  //           soot/jimple/toolkits/typing/ClassHierarchy.java
  //           soot/jimple/toolkits/typing/TypeResolver.java
  //           soot/jimple/toolkits/typing/TypeVariable.java
  //           soot/jimple/toolkits/typing/TypeNode.java
  final static private boolean isJ2ME = false;

    // In application mode, we can choose lazy invocation mode
    // and also choose no output, this is only used for 
    // our point-to analysis right now.
    static private boolean isLazyInvocation = false;

    static private SootClass mainClass = null;        
    
    static public long stmtCount;

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
        if (!opts.appMode() && val){            
            throw new CompilationDeathException(COMPILATION_ABORTED, "Can only whole-program optimize in application mode!");
        }
  
        isOptimizingWhole = val;
        isOptimizing = val;
    }

    public static boolean isOptimizingWhole()
    {
        return isOptimizingWhole;
    }

    public static void setWithCache(boolean val)
    {
	withCache = val;
    }
    public static boolean getWithCache()
    {
	return withCache;
    }
    
    public static void setCacheDir( String s)
    {
	cacheDir = s;
	setWithCache( true);
    }
    public static String getCacheDir()
    {
	return cacheDir;
    }
    

  /* hack for J2ME */
  public static boolean isJ2ME(){
    return isJ2ME;
  }

    public static void setJavaStyle( boolean val)
    {
	useJavaStyle = val;
    }

    public static boolean getJavaStyle()
    {
	return useJavaStyle;
    }


    public static void addExclude(String str)
        throws CompilationDeathException
    {
        if (!opts.appMode()) {    
            throw new CompilationDeathException(COMPILATION_ABORTED, "Exclude flag only valid in application mode!");
        }
  
        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add(str);
  
    }

    public static void addInclude(String str)
        throws CompilationDeathException
    {
        if (!opts.appMode()) {
            throw new CompilationDeathException(COMPILATION_ABORTED, "Include flag only valid in application mode!");
        }
        packageInclusionFlags.add(new Boolean(true));
        packageInclusionMasks.add(str);
    }

    public static void addDynamicClasses(String path)
        throws CompilationDeathException
    {
        if (!opts.appMode())
            {
                throw new CompilationDeathException(COMPILATION_ABORTED, "Dynamic-classes flag only valid in application mode!");
            }
                     
        StringTokenizer tokenizer = new StringTokenizer(path, ":");
        while(tokenizer.hasMoreTokens())
            dynamicClasses.add(tokenizer.nextToken());
    }

    public static void addDynamicPath(String path)
        throws CompilationDeathException
    {
        if (!opts.appMode())
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
        if (!opts.appMode()) {
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
            for( Iterator filenameIt = l.iterator(); filenameIt.hasNext(); ) {
                final String filename = (String) filenameIt.next();
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
            throw new CompilationDeathException(COMPILATION_ABORTED,
						"Illegal --src-prec arg: " 
						+ prec + ". Valid args are:"
						+ " \"jimple\" or \"class\"");
        }
    }

    public static void setAnnotationPhases(String opt)
    {
	if (opt.equals("both")) 
	    {
		doNullPointerCheck = true;
		doArrayBoundsCheck = true;
	    } 
	else if (opt.equals("arraybounds"))
	    {
		doArrayBoundsCheck = true;
	    }
	else if (opt.equals("nullpointer"))
	    {
		doNullPointerCheck = true;
	    }
	else if (opt.equals("LineNumber"))
	    {
		CodeAttributeGenerator.v().registerAggregator(new LineNumberTagAggregator(true));
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
	
	if (doNullPointerCheck || doArrayBoundsCheck) {
	    Scene.v().getPack("jtp").add(new Transform("jtp.profiling", ProfilingGenerator.v()));
	    // turn on the tag aggregator
	    CodeAttributeGenerator.v().registerAggregator(new ArrayNullTagAggregator(true));
	}
    }

    private static void printVersion()
    {
	System.out.println("Soot version "+versionString);

	System.out.println("Copyright (C) 1997-2003 Raja Vallee-Rai (rvalleerai@sable.mcgill.ca).");
	System.out.println("All rights reserved.");
	System.out.println("");
	System.out.println("Contributions are copyright (C) 1997-2003 by their respective contributors.");
	System.out.println("See individual source files for details.");
	System.out.println("");
	System.out.println("Soot comes with ABSOLUTELY NO WARRANTY.  Soot is free software,");
	System.out.println("and you are welcome to redistribute it under certain conditions.");
	System.out.println("See the accompanying file 'license.html' for details.");
	System.out.println();
	System.out.println("Visit the Soot website:");
	System.out.println("  http://www.sable.mcgill.ca/soot/");
    }

    private static void printHelp()
    {
        System.out.println( opts.getUsage() );
    }


    private static void processCmdLine(String[] args)
        throws CompilationDeathException
    {
        opts = new Options( args );
        if( !opts.parse() ) 
            throw new CompilationDeathException(COMPILATION_ABORTED,
                    "Option parse error" );

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
    public static void processPhaseOptions(String phaseName, String option) {
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
	if(opts.classes().isEmpty() && opts.processPath().isEmpty())
            {
                throw new CompilationDeathException(COMPILATION_ABORTED, "Nothing to do!"); 
            }
	// Command line classes
	if (opts.appMode() && opts.classes().size() > 1)
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

        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add("com.sun.");                

        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add("com.ibm.");                

        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add("org.xml.");                

        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add("org.w3c.");                

        packageInclusionFlags.add(new Boolean(false));
        packageInclusionMasks.add("org.apache.");                
    }



    private static String[] cmdLineArgs;
    public static String[] getCmdLineArgs()
    {
	return cmdLineArgs;
    }
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
        m.run();
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
  public void run() {   
    start = new Date();

    try {
      totalTimer.start();

      initApp();

      processCmdLine(cmdLineArgs);

      System.out.println("Soot started on "+start);
      
      loadNecessaryClasses();

      prepareClasses();

      /* process all reachable methods and generate jimple body */
      if (isLazyInvocation) {
	lazyPreprocessClasses();
      }

      processTagFiles();

      // Run the whole-program packs.
      Scene.v().getPack("wjtp").apply();
      if(isOptimizingWhole)
	Scene.v().getPack("wjop").apply();
				
      // Give one more chance
      Scene.v().getPack("wjtp2").apply();
				
      // System.gc();

      preProcessDAVA();

      if (isLazyInvocation) {
	lazyProcessClasses(); 
      } else {
	processClasses();
      }

      postProcessDAVA();
	    
      totalTimer.end();            
				
      // Print out time stats.				
      if(opts.time())
	printProfilingInformation();
        
    } catch (CompilationDeathException e) {
      totalTimer.end();            
      exitCompilation(e.getStatus(), e.getMessage());
      return;
    }   
				
    finish = new Date();
    
    System.out.println("Soot finished on "+finish);
    long runtime = finish.getTime() - start.getTime();
    System.out.println("Soot has run for "
		       +(runtime/60000)+" min. "
		       +((runtime%60000)/1000)+" sec.");
     
    exitCompilation(COMPILATION_SUCCEDED);            
  }        

  /* preprocess classes for DAVA */
  private static void preProcessDAVA() {
    if (opts.outputFormat() == Options.outputFormat_dava) {
      ThrowFinder.v().find();
      PackageNamer.v().fixNames();

      System.out.println();
    }
  }

  /* lazily preprocess classes, based on invoke graph generated by CHA */
  private static void lazyPreprocessClasses() {
     
    Date start = new Date();

    System.out.println("[] Start building the invoke graph ... ");

    InvokeGraph invokeGraph = 
      ClassHierarchyAnalysis.newPreciseInvokeGraph(true);
    Scene.v().setActiveInvokeGraph(invokeGraph);

    Date finish = new Date();

    {
      System.out.println("[] Finished building the invoke graph ...");
      long runtime = finish.getTime() - start.getTime();
      System.out.println("[] Building invoke graph takes "
			 +(runtime/60000)+" min. "
			 +((runtime%60000)/1000)+" sec.");
    }	
  }

  /* lazily process classes */
  private static void lazyProcessClasses() {
    Iterator classIt = Scene.v().getApplicationClasses().iterator();

    while (classIt.hasNext()) {
      SootClass s = (SootClass)classIt.next();
      System.out.println(" Transforming " + s.getName() + "...");

      lazyHandleClass(s);
    }
  }

  /* process classes */
  private static void processClasses() {
    Iterator classIt = Scene.v().getApplicationClasses().iterator();
    
    // process each class 
    while(classIt.hasNext()) {
      SootClass s = (SootClass) classIt.next();
				
      if (opts.outputFormat() == Options.outputFormat_dava) {
	System.out.print( "Decompiling ");
      } else {
	System.out.print( "Transforming ");
      }
      System.out.print( s.getName() + "... " );
      System.out.flush();
							
      handleClass(s);
      System.out.println();
    }
  }

  /* post process for DAVA */
  private static void postProcessDAVA() {
    if (opts.outputFormat() == Options.outputFormat_dava) {

      // ThrowFinder.v().find();
      // PackageNamer.v().fixNames();

      System.out.println();

      setJavaStyle( true);

      Iterator classIt = Scene.v().getApplicationClasses().iterator();
      while (classIt.hasNext()) {
	SootClass s = (SootClass) classIt.next();
	
	FileOutputStream streamOut = null;
	PrintWriter writerOut = null;
	String fileName = getFileNameFor( s, opts.outputFormat());
		    
	try {
	  streamOut = new FileOutputStream(fileName);
	  writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
	} catch (IOException e) {
	  System.out.println("Cannot output file " + fileName);
	}

	System.out.print( "Generating " + fileName + "... ");
	System.out.flush();

        s.printTo(writerOut, PrintGrimpBodyOption.USE_ABBREVIATIONS);

	System.out.println();
	System.out.flush();

	{
	  try {
	    writerOut.flush();
	    streamOut.close();
	  } catch(IOException e) {
	    System.out.println("Cannot close output file " + fileName);
	  }
	}

	{
	  Iterator methodIt = s.methodIterator();
	  
	  while(methodIt.hasNext()) {   
	    SootMethod m = (SootMethod) methodIt.next();
				
	    if(m.hasActiveBody())
	      m.releaseActiveBody();
	  }
	}
      }
      System.out.println();

      setJavaStyle( false);
    }
  }
    /* load necessary classes
     */
    private static void loadNecessaryClasses() {
	Iterator it = opts.classes().iterator();
        
	while(it.hasNext()) {
	    String name = (String) it.next();
	    SootClass c;
                            
	    c = Scene.v().loadClassAndSupport(name);
                
	    if(mainClass == null) {
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
        {
            Object o = it.next();
	    Scene.v().loadClassAndSupport((String) o);
        }

        for( Iterator pathIt = opts.processPath().iterator(); pathIt.hasNext(); ) {

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
  private static void prepareClasses() {
      
    if(opts.appMode()) {
      Iterator contextClassesIt = 
	Scene.v().getContextClasses().snapshotIterator();
      while (contextClassesIt.hasNext())
	((SootClass)contextClassesIt.next()).setApplicationClass();
    }   
                         
    // Remove/add all classes from packageInclusionMask as per piFlag
    List applicationPlusContextClasses = new ArrayList();

    applicationPlusContextClasses.addAll(Scene.v().getApplicationClasses());
    applicationPlusContextClasses.addAll(Scene.v().getContextClasses());

    Iterator classIt = applicationPlusContextClasses.iterator();
                
    while(classIt.hasNext()) {
      SootClass s = (SootClass) classIt.next();
                    
      if(opts.classes().contains(s.getName()))
	continue;
	    
      Iterator packageCmdIt = packageInclusionFlags.iterator();
      Iterator packageMaskIt = packageInclusionMasks.iterator();
                    
      while(packageCmdIt.hasNext()) {
	boolean pkgFlag = 
	  ((Boolean) packageCmdIt.next()).booleanValue();
	String pkgMask = (String) packageMaskIt.next();
                        
	if (pkgFlag) {
	  if (s.isContextClass() 
	      && s.getPackageName().startsWith(pkgMask))
	    s.setApplicationClass();
	} else {
	  if (s.isApplicationClass() 
	      && s.getPackageName().startsWith(pkgMask))
	    s.setContextClass();
	}
      }
    }

    if (opts.analyzeContext()) {
      Iterator contextClassesIt = 
	Scene.v().getContextClasses().snapshotIterator();
      while (contextClassesIt.hasNext())
	((SootClass)contextClassesIt.next()).setLibraryClass();
    }
  }

  /* read in the tag files
   * Who created this? It calls retrieve jimple body
   */
    private static void processTagFiles() {
      Iterator it = sTagFileList.iterator();
      while(it.hasNext()) { 
	try {
	  File f = new File((String)it.next());
	  BufferedReader reader = 
	    new BufferedReader(new InputStreamReader(new FileInputStream(f)));
								
	  for(String line = reader.readLine(); 
	      line  !=  null;  
	      line = reader.readLine()) {
	    if(line.startsWith("<") ) {
	      String signature = line.substring(0,line.indexOf('+'));
	      int offset = Integer.parseInt(line.substring(line.indexOf('+') 
					   + 1, line.indexOf('/')));
	      String name = line.substring(line.indexOf('/')+1, 
					   line.lastIndexOf(':'));
	      String value = line.substring(line.lastIndexOf(':')+1);

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
            
            if(opts.subGC())
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
  private static void attachJimpleBodiesFor(SootClass c) {
    Iterator methodIt = c.methodIterator();
           
    while(methodIt.hasNext()) {   
      SootMethod m = (SootMethod) methodIt.next();
        
      if(!m.isConcrete())
	continue;
                                
      if(!m.hasActiveBody()) {
	m.setActiveBody(m.getBodyFromMethodSource("jb"));

	Scene.v().getPack("jtp").apply(m.getActiveBody());
	if(isOptimizing) 
	  Scene.v().getPack("jop").apply(m.getActiveBody());
      }            
    }
  }

  /* lazyHandleClass only processes methods reachable from the entry points
   * by the call graph, it does not have any output.
   */
  private static void lazyHandleClass(SootClass c) {
    Iterator methodIt = c.methodIterator();
    
    while(methodIt.hasNext()) {   
      SootMethod m = (SootMethod) methodIt.next();
        
      if(!m.isConcrete())
	continue;

      if (m.hasActiveBody()) {
	JimpleBody body = (JimpleBody) m.getActiveBody();
		    
	Scene.v().getPack("jtp").apply(body);
                    
	if(isOptimizing) 
	  Scene.v().getPack("jop").apply(body);

	m.releaseActiveBody();
      }
    }
  }

  /* normal approach to handle each class by analyzing every method.
   */
  private static void handleClass(SootClass c)
  {
    FileOutputStream streamOut = null;
    PrintWriter writerOut = null;
        
    boolean 
      produceBaf   = false,
      produceGrimp = false,
      produceDava  = false;
        
    switch( opts.outputFormat() ) {	
    case Options.outputFormat_none:
      break;
    case Options.outputFormat_jimple:
    case Options.outputFormat_jimp:
      break;
    case Options.outputFormat_dava:
      produceDava = true;
    case Options.outputFormat_grimp:
    case Options.outputFormat_grimple:
      produceGrimp = true;
      break;
    case Options.outputFormat_baf:
    case Options.outputFormat_b:
      produceBaf = true;
      break;
    case Options.outputFormat_xml:
      break;
    case Options.outputFormat_jasmin:
    case Options.outputFormat_classFile:
      produceGrimp = opts.viaGrimp();
      produceBaf = !produceGrimp;
      break;
    default:
      throw new RuntimeException();
    }
   
    String fileName = getFileNameFor( c, opts.outputFormat());
	
    if( opts.outputFormat() != Options.outputFormat_none
    &&  opts.outputFormat() != Options.outputFormat_classFile ) {
      try {
	streamOut = new FileOutputStream(fileName);
	writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
      } catch (IOException e) {
	System.out.println("Cannot output file " + fileName);
      }
    }

    HashChain newMethods = new HashChain();
	
    // Build all necessary bodies
    {
      Iterator methodIt = c.methodIterator();
      
      while(methodIt.hasNext()) {   
	SootMethod m = (SootMethod) methodIt.next();
        
	if(!m.isConcrete())
	  continue;
                                
	// Build Jimple body and transform it.
	{
	  boolean wasOptimizing = isOptimizing;
	  if (produceDava)
	    isOptimizing = true;

	  JimpleBody body = (JimpleBody) m.retrieveActiveBody();
		    
	  Scene.v().getPack("jtp").apply(body);
                    
	  if(isOptimizing) 
	    Scene.v().getPack("jop").apply(body);

	  isOptimizing = wasOptimizing;
	}

	if(produceGrimp) {
	  boolean wasOptimizing = isOptimizing;
	  if (produceDava)
	    isOptimizing = true;

	  if(isOptimizing)
	    m.setActiveBody(Grimp.v().newBody(m.getActiveBody(), "gb", "aggregate-all-locals"));
	  else
	    m.setActiveBody(Grimp.v().newBody(m.getActiveBody(), "gb"));
                        
	  if(isOptimizing)
	    Scene.v().getPack("gop").apply(m.getActiveBody());

	  isOptimizing = wasOptimizing;
	}
        else if(produceBaf) {   
	  m.setActiveBody(Baf.v().newBody((JimpleBody) m.getActiveBody()));

	  if(isOptimizing) 
	    Scene.v().getPack("bop").apply(m.getActiveBody());
	} 
      }

      if (produceDava) {
	methodIt = c.methodIterator();
		
	while (methodIt.hasNext()) {
	  SootMethod m = (SootMethod) methodIt.next();
		    
	  if (!m.isConcrete())
	    continue;

	  m.setActiveBody( Dava.v().newBody( m.getActiveBody(), "db"));
	}
      }
    }

    switch(opts.outputFormat()) {
    case Options.outputFormat_none:
      break;
    case Options.outputFormat_jasmin:
      if(c.containsBafBody())
	new soot.baf.JasminClass(c).print(writerOut);            
      else
	new soot.jimple.JasminClass(c).print(writerOut);
      break;
    case Options.outputFormat_jimp:            
      c.printTo(writerOut, PrintJimpleBodyOption.USE_ABBREVIATIONS);
      break;
    case Options.outputFormat_b:
      c.printTo(writerOut, soot.baf.PrintBafBodyOption.USE_ABBREVIATIONS);
      break;
    case Options.outputFormat_grimp:
      c.printTo(writerOut, PrintGrimpBodyOption.USE_ABBREVIATIONS);
      break;
    case Options.outputFormat_baf:
    case Options.outputFormat_jimple:
    case Options.outputFormat_grimple:
      writerOut = 
	new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));
      c.printJimpleStyleTo(writerOut, 0);
      break;
    case Options.outputFormat_dava:
      break;
    case Options.outputFormat_classFile:
      c.write(outputDir);
      break;    
    case Options.outputFormat_xml:
      writerOut = 
	new PrintWriter(new EscapedWriter(new OutputStreamWriter(streamOut)));
      c.printJimpleStyleTo(writerOut, PrintJimpleBodyOption.XML_OUTPUT);
      break;
    default:
      throw new RuntimeException();
    }
    
    if( opts.outputFormat() != Options.outputFormat_none
    &&  opts.outputFormat() != Options.outputFormat_classFile ) {
      try {
	writerOut.flush();
	streamOut.close();
      }	catch(IOException e) {
	System.out.println("Cannot close output file " + fileName);
      }
    }

    // Release bodies
    if (!produceDava) {
      Iterator methodIt = c.methodIterator();
                
      while(methodIt.hasNext()) {   
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
