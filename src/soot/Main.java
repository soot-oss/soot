/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


import soot.toolkits.astmetrics.ClassData;
import java.util.*;
import soot.options.Options;

import java.io.*;

/** Main class for Soot; provides Soot's command-line user interface. */
public class Main {
    public Main(Singletons.Global g) {
    }
    public static Main v() {
        return G.v().soot_Main();
    }
    // TODO: the following string should be updated by the source control
    // No it shouldn't. Prcs is horribly borken in this respect, and causes
    // the code to not compile all the time.
    public final String versionString = "2.2.3";

    private Date start;
    private Date finish;

    private void printVersion() {
        G.v().out.println("Soot version " + versionString);

        G.v().out.println(
            "Copyright (C) 1997-2003 Raja Vallee-Rai and others.");
        G.v().out.println("All rights reserved.");
        G.v().out.println("");
        G.v().out.println(
            "Contributions are copyright (C) 1997-2003 by their respective contributors.");
        G.v().out.println("See the file 'credits' for a list of contributors.");
        G.v().out.println("See individual source files for details.");
        G.v().out.println("");
        G.v().out.println(
            "Soot comes with ABSOLUTELY NO WARRANTY.  Soot is free software,");
        G.v().out.println(
            "and you are welcome to redistribute it under certain conditions.");
        G.v().out.println(
            "See the accompanying file 'COPYING-LESSER.txt' for details.");
        G.v().out.println();
        G.v().out.println("Visit the Soot website:");
        G.v().out.println("  http://www.sable.mcgill.ca/soot/");
        G.v().out.println();
        G.v().out.println("For a list of command line options, enter:");
        G.v().out.println("  java soot.Main --help");
    }

    private void processCmdLine(String[] args) {

        if (!Options.v().parse(args))
            throw new CompilationDeathException(
                CompilationDeathException.COMPILATION_ABORTED,
                "Option parse error");

        if( PackManager.v().onlyStandardPacks() ) {
            for( Iterator packIt = PackManager.v().allPacks().iterator(); packIt.hasNext(); ) {
                final Pack pack = (Pack) packIt.next();
                Options.v().warnForeignPhase(pack.getPhaseName());
                for( Iterator trIt = pack.iterator(); trIt.hasNext(); ) {
                    final Transform tr = (Transform) trIt.next();
                    Options.v().warnForeignPhase(tr.getPhaseName());
                }
            }
        }
        Options.v().warnNonexistentPhase();

        if (Options.v().help()) {
            G.v().out.println(Options.v().getUsage());
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        if (Options.v().phase_list()) {
            G.v().out.println(Options.v().getPhaseList());
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        if(!Options.v().phase_help().isEmpty()) {
            for( Iterator phaseIt = Options.v().phase_help().iterator(); phaseIt.hasNext(); ) {
                final String phase = (String) phaseIt.next();
                G.v().out.println(Options.v().getPhaseHelp(phase));
            }
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        if (args.length == 0 || Options.v().version()) {
            printVersion();
            throw new CompilationDeathException(CompilationDeathException.COMPILATION_SUCCEEDED);
        }

        postCmdLineCheck();
    }

    private void exitCompilation(int status) {
        exitCompilation(status, "");
    }

    private void exitCompilation(int status, String msg) {
        if(status == CompilationDeathException.COMPILATION_ABORTED) {
                G.v().out.println("compilation failed: "+msg);
        }
    }

    private void postCmdLineCheck() {
        if (Options.v().classes().isEmpty()
        && Options.v().process_dir().isEmpty()) {
            throw new CompilationDeathException(
                CompilationDeathException.COMPILATION_ABORTED,
                "No input classes specified!");
        }
    }

    public String[] cmdLineArgs = new String[0];
    /**
     *   Entry point for cmd line invocation of soot.
     */
    public static void main(String[] args) {
        try {
            Main.v().run(args);
        } catch( OutOfMemoryError e ) {
            G.v().out.println( "Soot has run out of the memory allocated to it by the Java VM." );
            G.v().out.println( "To allocate more memory to Soot, use the -Xmx switch to Java." );
            G.v().out.println( "For example (for 400MB): java -Xmx400m soot.Main ..." );
            throw e;
        }
    }

    /** 
     *  Entry point to the soot's compilation process.
     */
    public int run(String[] args) {
        cmdLineArgs = args;

        start = new Date();

        try {
            Timers.v().totalTimer.start();

            processCmdLine(cmdLineArgs);

            G.v().out.println("Soot started on " + start);

            Scene.v().loadNecessaryClasses();

            /*
             * By this all the java to jimple has occured so we just check ast-metrics flag
             * 
             * If it is set......print the astMetrics.xml file and stop executing soot
             */
            if(Options.v().ast_metrics()){
        	   	try{
            		OutputStream streamOut = new FileOutputStream("../astMetrics.xml");
            		PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
            		writerOut.println("<?xml version='1.0'?>");
            		writerOut.println("<ASTMetrics>");		 		
            		
            		Iterator it = G.v().ASTMetricsData.iterator();
            		while(it.hasNext()){
            			//each is a classData object
            			ClassData cData = (ClassData)it.next();
            			writerOut.println(cData.toString());
            		}

            		writerOut.println("</ASTMetrics>");
             		writerOut.flush();
            		streamOut.close();
            	} catch (IOException e) {
            		throw new CompilationDeathException("Cannot output file astMetrics");
            	}
                exitCompilation(CompilationDeathException.COMPILATION_SUCCEEDED);
                return CompilationDeathException.COMPILATION_SUCCEEDED;

            }
            
            
            PackManager.v().runPacks();
            PackManager.v().writeOutput();

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

        exitCompilation(CompilationDeathException.COMPILATION_SUCCEEDED);
        return CompilationDeathException.COMPILATION_SUCCEEDED;
    }
}
