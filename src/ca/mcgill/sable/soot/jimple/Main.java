/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
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
 * This work is free software; you can redistribute it and/or        *
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

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.grimp.*;

import java.io.*;

public class Main
{
    static boolean naiveJimplification;
    static boolean onlyJimpleOutput;
    public static boolean isVerbose;
    static boolean onlyJasminOutput;
    static boolean isProfilingOptimization;
    static boolean oldTyping;
    static boolean isInDebugMode;
    static boolean usePackedLive;
    static boolean usePackedDefs = true;
    static boolean isTestingPerformance;

    public static String jimpleClassPath;

    static boolean produceJimpleFile,
        produceJasminFile,
        produceJimpFile = true;

    static int totalFlowNodes,
           totalFlowComputations;
           
    static Timer copiesTimer = new Timer(),
        defsTimer = new Timer(),
        usesTimer = new Timer(),
        liveTimer = new Timer(),
        splitTimer = new Timer(),
        packTimer = new Timer(),
        cleanup1Timer = new Timer(),
        cleanup2Timer = new Timer(),
        conversionTimer = new Timer(),
        cleanupAlgorithmTimer = new Timer(),
        graphTimer = new Timer(),
        assignTimer = new Timer(),
        resolveTimer = new Timer(),
        totalTimer = new Timer(),
        splitPhase1Timer = new Timer(),
        splitPhase2Timer = new Timer(),
        defsSetupTimer = new Timer(),
        defsAnalysisTimer = new Timer(),
        defsPostTimer = new Timer(),
        liveSetupTimer = new Timer(),
        liveAnalysisTimer = new Timer(),
        livePostTimer = new Timer(),
        jimpleAggregationTimer = new Timer(),
        grimpAggregationTimer = new Timer();
        

    static int conversionLocalCount,
        cleanup1LocalCount,
        splitLocalCount,
        assignLocalCount,
        packLocalCount,
        cleanup2LocalCount;

    static int conversionStmtCount,
        cleanup1StmtCount,
        splitStmtCount,
        assignStmtCount,
        packStmtCount,
        cleanup2StmtCount;


    public static void main(String[] args) throws RuntimeException
    {
        int firstNonOption = 0;
        long stmtCount = 0;
        int buildBodyOptions = 0;

        totalTimer.start();

        SootClassManager cm = new SootClassManager();

        if(args.length == 0)
        {
// $Format: "            System.out.println(\"Jimple version $ProjectVersion$\");"$
            System.out.println("Jimple version 1.beta.3.dev.16");
            System.out.println("Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca).");
            System.out.println("All rights reserved.");
            System.out.println("");
            System.out.println("Portions copyright (C) 1997 Clark Verbrugge (clump@sable.mcgill.ca).");
            System.out.println("All rights reserved.");
            System.out.println("");
            System.out.println("Modifications are copyright (C) 1997, 1998 by their respective contributors.");
            System.out.println("See individual source files for details.");
            System.out.println("");
            System.out.println("Jimple comes with ABSOLUTELY NO WARRANTY.  This is free software,");
            System.out.println("and you are welcome to redistribute it under certain conditions.");
            System.out.println("See the accompanying file 'COPYING' for details.");
            System.out.println("");
            System.out.println("Syntax: java ca.mcgill.sable.soot.jimple.Main [options] class");
            System.out.println("");
            System.out.println("Classpath Option:");
            System.out.println("    -jimpleClassPath <path>   uses <path> as classpath for finding classes");
            System.out.println("");
            System.out.println("Output Options:");
            System.out.println("    -jimple                   produce .jimple code");
            System.out.println("    -jimp                     produce .jimp (abbreviated .jimple) code [default]");
            System.out.println("    -jasmin                   produce .jasmin code");
            System.out.println("");
            System.out.println("Jimplification Options:");
            System.out.println("    -nocleanup                no constant or copy propagation is performed");
            System.out.println("    -nosplitting              no splitting of variables is performed");
            System.out.println("    -nocleanup                no constant or copy propagation is performed");
            System.out.println("    -oldtyping                use old typing algorithm");
            System.out.println("    -typeless                 do not assign types.  Cannot be used with -jasmin");
            System.out.println("                              or -nolocalpacking ");
            System.out.println("    -nolocalpacking           do not re-use locals after jimplification");
            System.out.println("    -noaggregating            do not perform any Jimple-level aggregation");
            System.out.println("");
            System.out.println("Profiling/Debugging Options:");
            System.out.println("    -timetransform            perform full transformation and print timings");
            System.out.println("    -verbose                  print out jimplification process");
            System.out.println("    -debug                    avoid catching errors during jimplification");
            System.out.println("    -testperf                 jimplify all classes & methods and gather stats");
            System.out.println("                              does not throw exception if error in typing");
            System.exit(0);
        }

        // Handle all the options
            for(int i = 0; i < args.length; i++)
            {
                if(args[i].equals("-jimple"))
                    produceJimpleFile = true;
                else if(args[i].equals("-jasmin"))
                    produceJasminFile = true;
                else if(args[i].equals("-jimp"))
                    produceJimpFile = true;
                else if(args[i].equals("-nocleanup"))
                    buildBodyOptions |= BuildJimpleBodyOption.NO_CLEANUP;
                else if(args[i].equals("-typeless"))
                    buildBodyOptions |= BuildJimpleBodyOption.NO_TYPING;
                else if(args[i].equals("-nolocalpacking"))
                    buildBodyOptions |= BuildJimpleBodyOption.NO_PACKING;
                else if(args[i].equals("-noaggregating"))
                    buildBodyOptions |= BuildJimpleBodyOption.NO_AGGREGATING;
                else if(args[i].equals("-timetransform"))
                    isProfilingOptimization = true;
                else if(args[i].equals("-verbose"))
                    isVerbose = true;
                else if(args[i].equals("-nosplitting"))
                    buildBodyOptions |= BuildJimpleBodyOption.NO_SPLITTING;
                else if(args[i].equals("-oldtyping"))
                    oldTyping = true;
                else if(args[i].equals("-usepackedlive"))
                    usePackedLive = true;
                else if(args[i].equals("-usepackeddefs"))
                    usePackedDefs = true;    
                else if(args[i].equals("-testperf"))
                {
                    isProfilingOptimization = true;
                    isTestingPerformance = true;
                }
                else if(args[i].equals("-jimpleClassPath"))
                {   if(++i < args.length)
                        jimpleClassPath = args[i];
                }
                else if(args[i].equals("-debug"))
                    isInDebugMode = true;
                else if(args[i].startsWith("-"))
                {
                    System.out.println("Unrecognized option: " + args[i]);
                    System.exit(0);
                }
                else
                    break;

                firstNonOption = i + 1;
            }

        // Handle all the classes
        {
            int numFailed = 0;
            int numSuccess = 0;

            List listBodies = new ArrayList();

            for(int i = firstNonOption; i < args.length; i++)
            {
                SootClass c = cm.getClass(args[i]);
                String postFix;
                PrintWriter writerOut = null;
                FileOutputStream streamOut = null;

                System.out.print("Jimplifying " + c.getName() + "... " );
                System.out.flush();

                // Open output file.
                {
                    if(produceJasminFile)
                        postFix = ".jasmin";
                    else if(produceJimpleFile)
                        postFix = ".jimple";
                    else
                        postFix = ".jimp";

                    try {
                        streamOut = new FileOutputStream(c.getName() + postFix);
                        writerOut = new PrintWriter(streamOut);
                    }
                    catch (IOException e)
                    {
                        System.out.println("Cannot output file " + c.getName() + postFix);
                    }
                }

                if(isTestingPerformance)
                {
                    Iterator methodIt = c.getMethods().iterator();
                    long localStmtCount = 0;

                    try {
                        while(methodIt.hasNext())
                        {
                            SootMethod m = (SootMethod) methodIt.next();
                            JimpleBody listBody = (JimpleBody) new BuildBody(Jimple.v(), new StoredBody(ClassFile.v())).resolveFor(m);
                            
                            listBodies.add(listBody);
                            localStmtCount += listBody.getStmtList().size();
                        }

                        stmtCount += localStmtCount;

                        System.out.println(localStmtCount + " stmts  ");
                        numSuccess++;
                    }
                    catch(Exception e)
                    {
                        System.out.println("failed due to: " + e);
                        numFailed++;
                    }
                }
                else
                {
                    // Produce the file
                    {
                        if(!isInDebugMode)
                        {
                            try {
                                handleClass(c, postFix, writerOut, buildBodyOptions);
                            }
                            catch(Exception e)
                            {
                                System.out.println("failed due to: " + e);
                            }
                        }
                        else {
                            handleClass(c, postFix, writerOut, buildBodyOptions);
                        }
    
                        try {
                            writerOut.flush();
                            streamOut.close();
                        }
                        catch(IOException e )
                        {
                            System.out.println("Cannot close output file " + c.getName() + postFix);
                        }
    
                        System.out.println();
                    }
                }
            }
            
            if(isProfilingOptimization)
            {
                if(isTestingPerformance)
                {
                    System.out.println("Successfully jimplified " + numSuccess + " classfiles; failed on " + numFailed + ".");
    
                    // Count number of statements stored
                    {
                        Iterator bodyIt = listBodies.iterator();
                        long storedStmtCount = 0;
    
                        while(bodyIt.hasNext())
                        {
                            JimpleBody listBody = (JimpleBody) bodyIt.next();
                            storedStmtCount += listBody.getStmtList().size();
                        }
    
                        System.out.println("Confirmed " + storedStmtCount + " stored statements.");
                        System.out.println();
                    }
                }
                
                totalTimer.end();
                    
                long totalTime = totalTimer.getTime();
                    
                System.out.println("Time measurements");
                System.out.println();
                
                System.out.println("      Building graphs: " + toTimeString(graphTimer, totalTime));
                System.out.println("  Computing LocalDefs: " + toTimeString(defsTimer, totalTime));
                System.out.println("                setup: " + toTimeString(defsSetupTimer, totalTime));
                System.out.println("             analysis: " + toTimeString(defsAnalysisTimer, totalTime));
                System.out.println("                 post: " + toTimeString(defsPostTimer, totalTime));
                System.out.println("  Computing LocalUses: " + toTimeString(usesTimer, totalTime));
                System.out.println("     Cleaning up code: " + toTimeString(cleanupAlgorithmTimer, totalTime));
                System.out.println("Computing LocalCopies: " + toTimeString(copiesTimer, totalTime));
                System.out.println(" Computing LiveLocals: " + toTimeString(liveTimer, totalTime));
                System.out.println("                setup: " + toTimeString(liveSetupTimer, totalTime));
                System.out.println("          aggregation: " + toTimeString(jimpleAggregationTimer, totalTime));
                System.out.println("             analysis: " + toTimeString(liveAnalysisTimer, totalTime));
                System.out.println("                 post: " + toTimeString(livePostTimer, totalTime));
                
                System.out.println("Coading coffi structs: " + toTimeString(resolveTimer, totalTime));

                
                System.out.println();

                // Print out time stats.
                {
                    float timeInSecs;

                    System.out.println(" Bytecode -> jimple (naive): " + toTimeString(conversionTimer, totalTime) + 
                        "\t" + conversionLocalCount + " locals  " + conversionStmtCount + " stmts");
                        
                    System.out.println("           Cleaning up code: " + toTimeString(cleanup1Timer, totalTime) +
                        "\t" + cleanup1LocalCount + " locals  " + cleanup1StmtCount + " stmts");
                        
                    System.out.println("        Splitting variables: " + toTimeString(splitTimer, totalTime) + 
                        "\t" + splitLocalCount + " locals  " + splitStmtCount + " stmts");
                        
                    System.out.println("               Split phase1: " + toTimeString(splitPhase1Timer, totalTime));
                    System.out.println("               Split phase2: " + toTimeString(splitPhase2Timer, totalTime));
                    
                    System.out.println("            Assigning types: " + toTimeString(assignTimer, totalTime) +
                        "\t" + assignLocalCount + " locals  " + assignStmtCount + " stmts");
                    System.out.println("             Packing locals: " + toTimeString(packTimer, totalTime) + 
                        "\t" + packLocalCount + " locals  " + packStmtCount + " stmts");
                
                        /*
                    System.out.println("cleanup2Timer:   " + cleanup2Time +
                        "(" + (cleanup2Time * 100 / totalTime) + "%) " +
                        cleanup2LocalCount + " locals  " + cleanup2StmtCount + " stmts");
*/

                    timeInSecs = (float) totalTime / 1000.0f;
                    float memoryUsed = (float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000.0f;

                    System.out.println("totalTime:" + toTimeString(totalTimer, totalTime));
                    System.out.println("totalMemory:" + memoryUsed + "k  ");

                    if(isTestingPerformance)
                    {
                        System.out.println("Time/Space performance");
                        System.out.println();
                        
                        System.out.println(toFormattedString(stmtCount / timeInSecs) + " stmt/s");
                        System.out.println(toFormattedString((float) memoryUsed / stmtCount) + " k/stmt");
                        
                    }
                    
                    System.out.println("totalFlowNodes: " + totalFlowNodes + 
                        " totalFlowComputations: " + totalFlowComputations + " avg: " + 
                        truncatedOf((double) totalFlowComputations / totalFlowNodes, 2));
        
                }
            }
        }
    }

    private static String toTimeString(Timer timer, long totalTime)
    {
        long time = timer.getTime();
        String timeString = paddedLeftOf(new Double(truncatedOf(time / 1000.0, 1)).toString(), 5);
        
        return (timeString + "s" + paddedLeftOf(" (" + (time * 100 / totalTime) + "%" + ")", 5));   
    }
    
    private static String toFormattedString(double value)
    {
        return paddedLeftOf(new Double(truncatedOf(value, 2)).toString(), 5);
    }
    
    private static void handleClass(SootClass c, String postFix, PrintWriter writerOut, int buildBodyOptions)
    {
        if(postFix.equals(".jasmin"))
            new JasminClass(c, new BuildBody(Grimp.v(), new StoredBody(ClassFile.v()))).print(writerOut);
        else if(postFix.equals(".jimp"))
        {
            c.printTo(new BuildBody(Jimple.v(), new StoredBody(ClassFile.v()), buildBodyOptions),
                writerOut, PrintJimpleBodyOption.USE_ABBREVIATIONS);
        }
        else
            c.printTo(new BuildBody(Jimple.v(), new StoredBody(ClassFile.v()), buildBodyOptions),
                writerOut);
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
