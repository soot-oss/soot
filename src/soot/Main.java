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
    
    private Date start;
    private Date finish;

    private static List compilationListeners = new ArrayList(1);
    public static void addCompilationListener(ICompilationListener l)
    {
        compilationListeners.add(l);
    }
    public static final int COMPILATION_ABORTED = 0;
    public static final int COMPILATION_SUCCEDED = 1;
    
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

	if (opts.outputDir() != null)
	    b.append(opts.outputDir());
	
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
    private static String pathSeparator = System.getProperty("path.separator");

    public static boolean isInDebugMode;
   
    static private boolean useJavaStyle = false;

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

    static private SootClass mainClass = null;        
    
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


        /* This is called after sootClassPath has been defined. */
    private static Set classesInDynamicPackage(String str)
    {
        HashSet set = new HashSet(0);
        StringTokenizer strtok = new StringTokenizer(Scene.v().getSootClassPath(), pathSeparator);
        while(strtok.hasMoreTokens()) {
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
        }
        return set;
    }


    public static void setDebug(boolean val)
    {
        isInDebugMode = val;
    }

    public static boolean isDebug()
    {
        return isInDebugMode;
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

        if( args.length == 0 || opts.help() ) {
            printHelp();
            throw new CompilationDeathException(COMPILATION_SUCCEDED);
        }

        if( opts.version() ) {
            printVersion();
            throw new CompilationDeathException(COMPILATION_SUCCEDED);
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

    public static String[] cmdLineArgs;
    /**
     *   Entry point for cmd line invocation of soot.
     */
    public static void main(String[] args)
    {
        cmdLineArgs = args;
        Main m = new Main();
        ConsoleCompilationListener ccl = new ConsoleCompilationListener();
        addCompilationListener(ccl);
        m.run();
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
      Timers.v().totalTimer.start();

      processCmdLine(cmdLineArgs);

      System.out.println("Soot started on "+start);

      if( opts.classpath() != null ) {
          Scene.v().setSootClassPath( opts.classpath() );
      }
      
      loadNecessaryClasses();

      prepareClasses();

      // Run the whole-program packs.
      Scene.v().getPack("wjtp").apply();
      if(isOptimizingWhole)
	Scene.v().getPack("wjop").apply();
				
      // Give one more chance
      Scene.v().getPack("wjtp2").apply();
				
      // System.gc();

      preProcessDAVA();

	processClasses();

      postProcessDAVA();
	    
      Timers.v().totalTimer.end();            
				
      // Print out time stats.				
      if(opts.time())
	Timers.v().printProfilingInformation();
        
    } catch (CompilationDeathException e) {
      Timers.v().totalTimer.end();            
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
  
    private static void loadNecessaryClasses() {
        Iterator it = opts.classes().iterator();

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
        dynClasses.addAll( opts.dynClasses() );

        for( Iterator pathIt = opts.dynPath().iterator(); pathIt.hasNext(); ) {

            final String path = (String) pathIt.next();
            dynClasses.addAll(getClassesUnder(path));
        }

        for( Iterator pkgIt = opts.dynPackage().iterator(); pkgIt.hasNext(); ) {

            final String pkg = (String) pkgIt.next();
            dynClasses.addAll( classesInDynamicPackage( pkg ) );
        }

        while (it.hasNext()) {
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
      
      LinkedList excludedPackages = new LinkedList();
      if( opts.excPackage() != null )
          excludedPackages.addAll( opts.excPackage() );

        excludedPackages.add("java.");
        excludedPackages.add("sun.");
        excludedPackages.add("javax.");
        excludedPackages.add("com.sun.");
        excludedPackages.add("com.ibm.");
        excludedPackages.add("org.xml.");
        excludedPackages.add("org.w3c.");
        excludedPackages.add("org.apache.");

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
	    
      for( Iterator pkgIt = excludedPackages.iterator(); pkgIt.hasNext(); ) {
	    
          final String pkg = (String) pkgIt.next();
	  if (s.isApplicationClass() 
	      && s.getPackageName().startsWith(pkg))
	    s.setContextClass();
      }
      for( Iterator pkgIt = opts.incPackage().iterator(); pkgIt.hasNext(); ) {
          final String pkg = (String) pkgIt.next();
	  if (s.isContextClass() 
	      && s.getPackageName().startsWith(pkg))
	    s.setApplicationClass();
      }
    }

    if (opts.analyzeContext()) {
      Iterator contextClassesIt = 
	Scene.v().getContextClasses().snapshotIterator();
      while (contextClassesIt.hasNext())
	((SootClass)contextClassesIt.next()).setLibraryClass();
    }
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
      c.write(opts.outputDir());
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
    
}
