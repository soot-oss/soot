/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Soot, a Java(TM) classfile optimization framework.                *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is freed software; you can redistribute it and/or       *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on April 20, 1999 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Fixed up some arguments.
   
 - Modified on March 28, 1999 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Renamed class to ca.mcgill.sable.soot.Main
   Fixed up all the command line arguments to conform to GNU standards.
   
 - Modified on February 3, 1999 by Patrick Lam (plam@sable.mcgill.ca) (*)
   Added changes in support of the Grimp intermediate
   representation (with aggregated-expressions).

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on October 4, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Added flag and option to print debugging information.

 - Modified on 12-Sep-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Changed the output options, and redirected the output to files.

 - Modified on 8-Sep-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Corrected the jimplification of several classes.

 - Modified on 1-Sep-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Allows multiple arguments on the command line.  Useful for
   timing the jimplification of several classes.

 - Modified on 28-Aug-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Changed the displayed copyright on program execution.

 - Modified on July 29, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added -nosplitting and -oldtyping parameters.

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Minor changes.

 - Modified on July 5, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added jimpleClassPath parameter.
   Updated copyright notice.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.grimp.*;
import ca.mcgill.sable.soot.baf.*;
// import ca.mcgill.sable.soot.jimple.toolkit.invoke.*;
import ca.mcgill.sable.soot.baf.toolkit.scalar.*;
import ca.mcgill.sable.soot.toolkit.scalar.*;
import java.io.*;

import java.text.*;

public class Main
{
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

    public static String sootClassPath;

    static private String targetExtension = ".class";

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

    static private int buildJimpleBodyOptions = 0;
    static private String outputDir = "";

    static private boolean isOptimizing;
    static private boolean isOptimizingWhole;
    static private boolean isUsingVTA;
    static private boolean isUsingRTA;
    
    static public long stmtCount;
    static String jasminSource = "grimp";
    
    public static void main(String[] args) throws RuntimeException
    {
        int firstNonOption = 0;
    
        boolean isRecursing = false;
        List excludingPackages = new ArrayList();
        List classesToTransform;
        totalTimer.start();

        if(args.length == 0)
        {
// $Format: "            System.out.println(\"Soot version $ProjectVersion$\");"$
            System.out.println("Soot version 1.beta.4.dev.70");
            System.out.println("Copyright (C) 1997-1999 Raja Vallee-Rai (rvalleerai@sable.mcgill.ca).");
            System.out.println("All rights reserved.");
            System.out.println("");
            System.out.println("Contributions are copyright (C) 1997-1999 by their respective contributors.");
            System.out.println("See individual source files for details.");
            System.out.println("");
            System.out.println("Soot comes with ABSOLUTELY NO WARRANTY.  Soot is freed software,");
            System.out.println("and you are welcome to redistribute it under certain conditions.");
            System.out.println("See the accompanying file 'license.html' for details.");
            System.out.println("");
            System.out.println("Syntax: soot [option]* classname ...  ");
            System.out.println("");
            System.out.println("Output options:");
            System.out.println("  -b, --b                    produce .b (abbreviated .baf) files");
            System.out.println("  -B, --baf                  produce .baf code");
            System.out.println("  -j, --jimp                 produce .jimp (abbreviated .jimple) files");
            System.out.println("  -J, --jimple               produce .jimple code");
            System.out.println("  -g, --grimp                produce .grimp (abbreviated .grimple) files");
            System.out.println("  -G, --grimple              produce .grimple files");
            System.out.println("  -a, --jasmin               produce .jasmin files");
            System.out.println("  -c, --class                produce .class files");
            System.out.println("");
            System.out.println("  -d PATH                    store produced files in PATH");
            System.out.println("  -r, --recurse              process dependent classfiles as well");
            System.out.println("  -x, --exclude PACKAGE      exclude classfiles in PACKAGE (e.g. java)"); 
            System.out.println("                             from transformation");
            System.out.println("");
            System.out.println("Construction options:");
            System.out.println("  --jasmin-source REP        produce jasmin from REP (jasmin, grimp, or baf)");
            System.out.println();
            System.out.println("Jimple construction options:");
            System.out.println("  --no-splitting             do not split local variables");
            System.out.println("  --use-packing              pack locals after conversion");
            System.out.println("  --no-typing                do not assign types to the local variables");
            System.out.println("  --no-jimple-aggregating    do not perform any Jimple-level aggregation");
//            System.out.println("  --use-original-names       retain variables name from local variable table");
            System.out.println("");
            System.out.println("Optimization options:");
            System.out.println("  -O  --optimize             perform scalar optimizations on the classfiles");
            System.out.println("  -W  --whole-optimize       perform whole program optimizations on the ");
            System.out.println("                             classfiles");
            System.out.println("");
            System.out.println("Misc. options:");
            System.out.println("  --soot-class-path PATH     uses PATH as the classpath for finding classes");
            System.out.println("  -t, --time                 print out time statistics about tranformations");
            System.out.println("  --subtract-gc              attempt to subtract the gc from the time stats");
            System.out.println("  -v, --verbose              verbose mode");
            System.out.println("  --debug                    avoid catching exceptions");
            System.out.println("");
            System.out.println("Examples:");
            System.out.println("");
            System.out.println("  soot -x java -x sun -r -d newClasses Simulator");
            System.out.println("         Transforms all classes starting with Simulator, excluding ");
            System.out.println("         those in java.*, sun.*, and stores them in newClasses. ");
               

            System.exit(0);
        }

        // Handle all the options
            for(int i = 0; i < args.length; i++)
            {
                String arg = args[i];
                
                if(arg.equals("-j") || arg.equals("--jimp"))
                    targetExtension = ".jimp";
                else if(arg.equals("-a") || arg.equals("--jasmin"))
                    targetExtension = ".jasmin";
                else if(arg.equals("-J") || arg.equals("--jimple"))
                    targetExtension = ".jimple";
                else if(arg.equals("-B") || arg.equals("--baf"))
                    targetExtension = ".baf";
                else if(arg.equals("-b") || arg.equals("--b"))
                    targetExtension = ".b";
                else if(arg.equals("-g") || arg.equals("--grimp"))
                    targetExtension = ".grimp";
                else if(arg.equals("-G") || arg.equals("--grimple"))
                    targetExtension = ".grimple";
                else if(arg.equals("-c") || arg.equals("--class"))
                    targetExtension = ".class";
                else if(arg.equals("-O") || arg.equals("--optimize"))
                    isOptimizing = true;
                else if(arg.equals("-W") || arg.equals("--whole-optimize"))
                {
                    isOptimizingWhole = true;
                    isOptimizing = true;
                }
                /*
                else if(arg.equals("--use-vta"))
                {
                    isUsingVTA = true;
                    Jimplifier.NOLIB = false;
                }
                else if(arg.equals("--use-rta"))
                {
                    isUsingRTA = true;
                    Jimplifier.NOLIB = false;
                } */
                
                else if(arg.equals("--no-typing"))
                    buildJimpleBodyOptions |= BuildJimpleBodyOption.NO_TYPING;
                else if(arg.equals("--no-jimple-aggregating"))
                    buildJimpleBodyOptions |= BuildJimpleBodyOption.NO_AGGREGATING;
                else if(arg.equals("--no-splitting"))
                    buildJimpleBodyOptions |= BuildJimpleBodyOption.NO_SPLITTING;
                else if(arg.equals("--use-packing"))
                    buildJimpleBodyOptions |= BuildJimpleBodyOption.USE_PACKING;
//                else if(arg.equals("--use-original-names"))
//                    buildJimpleBodyOptions |= BuildJimpleBodyOption.USE_ORIGINAL_NAMES;
                else if(arg.equals("-t") || arg.equals("--time"))
                    isProfilingOptimization = true;
                else if(arg.equals("--subtract-gc"))
                {
                    Timer.setSubtractingGC(true);
                    isSubtractingGC = true;
                }    
                else if(arg.equals("-v") || arg.equals("--verbose"))
                    isVerbose = true;
                else if(arg.equals("--soot-class-path"))
                {   
                    if(++i < args.length)
                        sootClassPath = args[i];
                }
                else if(arg.equals("-r") || arg.equals("--recurse"))
                    isRecursing=true;
                else if(arg.equals("-d"))
                {
                    if(++i < args.length)
                        outputDir = args[i];
                }
                else if(arg.equals("-x") || arg.equals("--exclude"))
                {
                    if(++i < args.length)
                        excludingPackages.add(args[i]);
                }
                else if(arg.equals("--jasmin-source"))
                {
                    if(++i < args.length)
                        jasminSource = args[i];
                        
                    if(!jasminSource.equals("jimple") &&
                        !jasminSource.equals("grimp") &&
                        !jasminSource.equals("baf"))
                    {
                        System.out.println("Illegal --jasmin-source arg: " + jasminSource);
                    }
                    
                }
                
                else if(arg.equals("--debug"))
                    isInDebugMode = true;
                else if(arg.startsWith("-"))
                {
                    System.out.println("Unrecognized option: " + arg);
                    System.exit(0);
                }
                else
                    break;

                firstNonOption = i + 1;
            }

        Scene cm = Scene.v();
        SootClass mainClass = null;
        
        // Generate classes to process
        {
            classesToTransform = new LinkedList();
            
            for(int i = firstNonOption; i < args.length; i++)
            {
                SootClass c = cm.loadClassAndSupport(args[i]);
                
                if(mainClass == null)
                    mainClass = c;
                    
                classesToTransform.add(c);
            }
            
            if(isRecursing)
            {
                classesToTransform = new LinkedList();
                classesToTransform.addAll(cm.getClasses());
             }   
                         
            // Remove all classes from excludingPackages
            {
                Iterator classIt = classesToTransform.iterator();
                
                while(classIt.hasNext())
                {
                    SootClass s = (SootClass) classIt.next();
                    
                    Iterator packageIt = excludingPackages.iterator();
                    
                    while(packageIt.hasNext())
                    {
                        String pkg = (String) packageIt.next();
                        
                        if(s.getPackageName().startsWith(pkg))
                            classIt.remove();
                    }
                }
            }
        }
        
        /*
        if(isOptimizingWhole)
        {
            System.out.print("Building InvokeGraph...");
            System.out.flush();
            
            InvokeGraph invokeGraph = ClassHierarchyAnalysis.newInvokeGraph(mainClass, classesToTransform); 
             
            if(isUsingVTA)
            {
                VariableTypeAnalysis.pruneInvokeGraph(invokeGraph);
                VariableTypeAnalysis.pruneInvokeGraph(invokeGraph);
            }   
            else if(isUsingRTA)
                RapidTypeAnalysis.pruneInvokeGraph(invokeGraph);
                                
            System.out.println();
            
            System.out.print("Inlining invokes...");
            System.out.flush();
            GlobalInvokeInliner.inlineInvokes(invokeGraph, classesToTransform);
            System.out.println();
        }
        */
        
        // Handle each class individually
        {
            Iterator classIt = classesToTransform.iterator();
            
            while(classIt.hasNext())
            {
                SootClass s = (SootClass) classIt.next();
                
                System.out.print("Transforming " + s.getName() + "... " );
                System.out.flush();
                
                if(!isInDebugMode)
                 {
                    try {
                        handleClass(s);
                    }
                    catch(Exception e)
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
                    
        // Print out time stats.
            if(isProfilingOptimization)
            {
                totalTimer.end();
                    
                
                long totalTime = totalTimer.getTime();
                
                System.out.println("Time measurements");
                System.out.println();
                
                System.out.println("      Building graphs: " + toTimeString(graphTimer, totalTime));
                System.out.println("  Computing UnitLocalDefs: " + toTimeString(defsTimer, totalTime));
//                System.out.println("                setup: " + toTimeString(defsSetupTimer, totalTime));
//                System.out.println("             analysis: " + toTimeString(defsAnalysisTimer, totalTime));
//                System.out.println("                 post: " + toTimeString(defsPostTimer, totalTime));
                System.out.println("  Computing UnitLocalUses: " + toTimeString(usesTimer, totalTime));
//                System.out.println("            Use phase1: " + toTimeString(usePhase1Timer, totalTime));
//                System.out.println("            Use phase2: " + toTimeString(usePhase2Timer, totalTime));
//                System.out.println("            Use phase3: " + toTimeString(usePhase3Timer, totalTime));

                System.out.println("     Cleaning up code: " + toTimeString(cleanupAlgorithmTimer, totalTime));
                System.out.println("Computing LocalCopies: " + toTimeString(copiesTimer, totalTime));
                System.out.println(" Computing UnitLiveLocals: " + toTimeString(liveTimer, totalTime));
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
    
    private static void handleClass(SootClass c)
    {
        FileOutputStream streamOut = null;
        OutputStreamWriter osw = null;
        PrintWriter writerOut = null;
        
        String fileName;
        
        if(!outputDir.equals(""))
            fileName = outputDir + fileSeparator;
        else
            fileName = "";
        
        fileName += c.getName() + targetExtension;
        
        if(!targetExtension.equals(".class"))
        {   
            try {
                streamOut = new FileOutputStream(fileName);
                writerOut = new EscapedPrintWriter(streamOut);
            }
            catch (IOException e)
            {
                System.out.println("Cannot output file " + c.getName() + targetExtension);
            }
        }

        boolean produceJimple = false;
        boolean produceBaf = false;
        boolean produceGrimp = false;
        
        // Determine paths
        
        {
            String endResult;
            
            if(targetExtension.startsWith(".jimp"))
                endResult = "jimple";
            else if(targetExtension.startsWith(".grimp"))
                endResult = "grimp";
            else if(targetExtension.startsWith(".baf"))
                endResult = "baf";
            else
                endResult = jasminSource;
        
    
            if(endResult.equals("jimple"))
                produceJimple = true;
            else if(endResult.equals("baf"))
            {
                produceBaf = true; 
                produceJimple = true;
            }
            else if(endResult.equals("grimp"))
            {
                produceJimple = true; 
                produceGrimp = true;
            }
        }
            
        // Build all necessary bodies
        {
            Iterator methodIt = c.getMethods().iterator();
            
            while(methodIt.hasNext())
            {   
                SootMethod m = (SootMethod) methodIt.next();
                   
                if(produceJimple)
                {
                    if(!m.hasActiveBody())
                        m.setActiveBody(new JimpleBody(new ClassFileBody(m), buildJimpleBodyOptions));
    

                    if(isOptimizing) {
                        BaseJimpleOptimizer.optimize((JimpleBody) m.getActiveBody());
                    }

                }
                
                if(produceGrimp)
                {
                    if(isOptimizing)
                        m.setActiveBody(new GrimpBody(m.getActiveBody(), BuildJimpleBodyOption.AGGRESSIVE_AGGREGATING));
                    else
                        m.setActiveBody(new GrimpBody(m.getActiveBody()));
                        
                    if(isOptimizing)
                    
		      BaseGrimpOptimizer.optimize((GrimpBody) m.getActiveBody());
		}
                else if(produceBaf)
                {   
                     m.setActiveBody(new BafBody((JimpleBody) m.getActiveBody()));

		       if(isOptimizing) {
			 Body b = m.getActiveBody();
			 
			 LoadStoreOptimizer.v().optimize(b);// new Body(new BafBody (new Body(m.getActiveBody()))));
			 //UnusedLocalRemover.removeUnusedLocals(b);
			 //UnitLocalPacker.packLocals(b);
			 b.printTo(new PrintWriter(System.out, true));			 
		     }

                } 
            }
        }
            
        if(targetExtension.equals(".jasmin"))
        {
            if(c.containsBafBody())
                new ca.mcgill.sable.soot.baf.JasminClass(c).print(writerOut);            
            else
                new ca.mcgill.sable.soot.jimple.JasminClass(c).print(writerOut);
        }
        else if(targetExtension.equals(".jimp"))
            c.printTo(writerOut, PrintJimpleBodyOption.USE_ABBREVIATIONS);
        else if(targetExtension.equals(".b"))
            c.printTo(writerOut, ca.mcgill.sable.soot.baf.PrintBafBodyOption.USE_ABBREVIATIONS);
        else if(targetExtension.equals(".baf") || targetExtension.equals(".jimple") || targetExtension.equals(".grimple"))
            c.printTo(writerOut);
        else if(targetExtension.equals(".grimp"))
            c.printTo(writerOut, PrintGrimpBodyOption.USE_ABBREVIATIONS);
        else if(targetExtension.equals(".class"))
            c.write(outputDir);
        
        if(!targetExtension.equals(".class"))
        {
            try {
                writerOut.flush();
                streamOut.close();
            }
            catch(IOException e )
            {
                System.out.println("Cannot close output file " + fileName);
            }
        }

        // Release bodies, if not performing whole program optimizations
            if(!isOptimizingWhole)
            {
                Iterator methodIt = c.getMethods().iterator();
                
                while(methodIt.hasNext())
                {   
                    SootMethod m = (SootMethod) methodIt.next();
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



