/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.text.DecimalFormat;

import soot.options.Options;

public class Timers
{
    private static Timers instance = new Timers();
    private Timers() {}
    public static Timers v() { return instance; }

     public int totalFlowNodes;

     public int totalFlowComputations;


     public Timer copiesTimer = new Timer("copies");

     public Timer defsTimer = new Timer("defs");

     public Timer usesTimer = new Timer("uses");

     public Timer liveTimer = new Timer("live");

     public Timer splitTimer = new Timer("split");

     public Timer packTimer = new Timer("pack");

     public Timer cleanup1Timer = new Timer("cleanup1");

     public Timer cleanup2Timer = new Timer("cleanup2");

     public Timer conversionTimer = new Timer("conversion");

     public Timer cleanupAlgorithmTimer = new Timer("cleanupAlgorithm");

     public Timer graphTimer = new Timer("graphTimer");

     public Timer assignTimer = new Timer("assignTimer");

     public Timer resolveTimer = new Timer("resolveTimer");

     public Timer totalTimer = new Timer("totalTimer");

     public Timer splitPhase1Timer = new Timer("splitPhase1");

     public Timer splitPhase2Timer = new Timer("splitPhase2");

     public Timer usePhase1Timer = new Timer("usePhase1");

     public Timer usePhase2Timer = new Timer("usePhase2");

     public Timer usePhase3Timer = new Timer("usePhase3");

     public Timer defsSetupTimer = new Timer("defsSetup");

     public Timer defsAnalysisTimer = new Timer("defsAnalysis");

     public Timer defsPostTimer = new Timer("defsPost");

     public Timer liveSetupTimer = new Timer("liveSetup");

     public Timer liveAnalysisTimer = new Timer("liveAnalysis");

     public Timer livePostTimer = new Timer("livePost");

     public Timer aggregationTimer = new Timer("aggregation");

     public Timer grimpAggregationTimer = new Timer("grimpAggregation");

     public Timer deadCodeTimer = new Timer("deadCode");

     public Timer propagatorTimer = new Timer("propagator");

     public Timer buildJasminTimer = new Timer("buildjasmin");

     public Timer assembleJasminTimer = new Timer("assembling jasmin");

     public Timer resolverTimer = new Timer("resolver");
        

     public int conversionLocalCount;

     public int cleanup1LocalCount;

     public int splitLocalCount;

     public int assignLocalCount;

     public int packLocalCount;

     public int cleanup2LocalCount;


     public int conversionStmtCount;

     public int cleanup1StmtCount;

     public int splitStmtCount;

     public int assignStmtCount;

     public int packStmtCount;

     public int cleanup2StmtCount;


     public long stmtCount;


     void printProfilingInformation()
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
            
            if(Main.opts.subGC())
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


    private  String toTimeString(Timer timer, long totalTime)
    {
        DecimalFormat format = new DecimalFormat("00.0");
        DecimalFormat percFormat = new DecimalFormat("00.0");
        
        long time = timer.getTime();
        
        String timeString = format.format(time / 1000.0); // paddedLeftOf(new Double(truncatedOf(time / 1000.0, 1)).toString(), 5);
        
        return (timeString + "s" + " (" + percFormat.format(time * 100.0 / totalTime) + "%" + ")");   
    }
    

    private  String toFormattedString(double value)
    {
        return paddedLeftOf(new Double(truncatedOf(value, 2)).toString(), 5);
    }


    public  double truncatedOf(double d, int numDigits)
    {
        double multiplier = 1;
        
        for(int i = 0; i < numDigits; i++)
            multiplier *= 10;
            
        return ((long) (d * multiplier)) / multiplier;
    }
    

    public  String paddedLeftOf(String s, int length)
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

