/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
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
 * Modified by the Sable Research Group and others 2002-2003.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.tools;

import java.lang.reflect.Method;
import java.util.*;

import soot.*;
import soot.baf.Baf;
import soot.toolkits.exceptions.*;
import soot.jimple.JimpleBody;
import soot.grimp.Grimp;
import soot.shimple.Shimple;
import soot.toolkits.graph.*;
import soot.util.dot.DotGraph;
import soot.util.cfgcmd.AltClassLoader;
import soot.util.cfgcmd.CFGGraphType;
import soot.util.cfgcmd.CFGIntermediateRep;
import soot.util.cfgcmd.CFGOptionMatcher;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.*;

/**
 * A utility class for generating dot graph file for a control flow graph
 *
 * @author Feng Qian
 */
public class CFGViewer extends BodyTransformer {

  /**
   * An enumeration type for representing the ThrowAnalysis to use. 
   */
  abstract class ThrowAnalysisOption extends CFGOptionMatcher.CFGOption {
    ThrowAnalysisOption(String name) {
      super(name);
    }
    abstract ThrowAnalysis getAnalysis();
  }

  private final ThrowAnalysisOption PEDANTIC_THROW_ANALYSIS = 
    new ThrowAnalysisOption("pedantic") {
    ThrowAnalysis getAnalysis() { 
      return PedanticThrowAnalysis.v(); 
    }
  };

  private final ThrowAnalysisOption UNIT_THROW_ANALYSIS = new ThrowAnalysisOption("unit") {
    ThrowAnalysis getAnalysis() { 
      return UnitThrowAnalysis.v(); 
    }
  };

  private final CFGOptionMatcher throwAnalysisOptions = 
    new CFGOptionMatcher(new ThrowAnalysisOption[] {    
      PEDANTIC_THROW_ANALYSIS,
      UNIT_THROW_ANALYSIS,
    });
  private ThrowAnalysis getThrowAnalysis(String option) {
    return ((ThrowAnalysisOption) 
	    throwAnalysisOptions.match(option)).getAnalysis();
  }

  private CFGGraphType graphtype = CFGGraphType.BRIEF_UNIT_GRAPH;
  private CFGIntermediateRep ir = CFGIntermediateRep.JIMPLE_IR;
  private ThrowAnalysis throwAnalysis = UNIT_THROW_ANALYSIS.getAnalysis();
  private CFGToDotGraph drawer = new CFGToDotGraph();
  private Map methodsToPrint = null; // If the user specifies particular
				     // methods to print, this is a map
				     // from method name to the class
				     // name declaring the method.


  protected void internalTransform(Body b, String phaseName, Map options) {
    SootMethod meth = b.getMethod();

    if ((methodsToPrint == null) ||
	(meth.getDeclaringClass().getName() ==
	 methodsToPrint.get(meth.getName()))) {
      Body body = ir.getBody((JimpleBody) b);
      print_cfg(body);
    }
  }


  public static void main(String[] args) {
      new CFGViewer().run( args );
  }


  public void run(String[] args) {

    /* process options */
    args = parse_options(args);
for (int i = 0; i < args.length; i++) System.err.println(args[i]);
    if (args.length == 0) {
      usage();
      return;
    }

    AltClassLoader.v().setAltClasses(new String[] {
      "soot.toolkits.graph.ArrayRefBlockGraph",
      "soot.toolkits.graph.Block",
      "soot.toolkits.graph.Block$AllMapTo",
      "soot.toolkits.graph.BlockGraph",
      "soot.toolkits.graph.BriefBlockGraph",
      "soot.toolkits.graph.BriefUnitGraph",
      "soot.toolkits.graph.CompleteBlockGraph",
      "soot.toolkits.graph.CompleteUnitGraph",
      "soot.toolkits.graph.TrapUnitGraph",
      "soot.toolkits.graph.UnitGraph",
      "soot.toolkits.graph.ZonedBlockGraph",
    });

    Pack jtp = PackManager.v().getPack("jtp");
    jtp.add(new Transform("jtp.printcfg", this));

    soot.Main.main(args);
  }


  private void usage(){
      G.v().out.println(
"Usage:\n" +
"   java soot.util.CFGViewer [soot options] [CFGViewer options] [class[:method]]...\n\n" +
"   CFGViewer options:\n" +
"      (When specifying the value for an '=' option, you only\n" +
"       need to type enough characters to specify the choice\n" +
"       unambiguously, and case is ignored.)\n" +
"\n" +
"       --alt-classpath PATH :\n" +
"                specifies the classpath from which to load classes\n" +
"                that implement graph types whose names begin with 'Alt'.\n" +
"       --graph={" +
CFGGraphType.help(0, 70, 
"                ".length()) + "} :\n" +
"                show the specified type of graph.\n" +
"                Defaults to BriefUnitGraph.\n" +
"       --ir={" +
CFGIntermediateRep.help(0, 70, 
"                ".length()) + "} :\n" +
"                create the CFG from the specified intermediate\n" +
"                representation. The default is jimple.\n" +
"       --brief :\n" +
"                label nodes with the unit or block index,\n" +
"                instead of the text of their statements.\n" +
"       --throwAnalysis={" +
throwAnalysisOptions.help(0, 70,
"              ".length()) + "} :\n" +
"                use the specified throw analysis when creating Exceptional\n" +
"                graphs (UnitThrowAnalysis is the default).\n" +
"       --showExceptions :\n" +
"                in Exceptional graphs, include edges showing the path of\n" +
"                exceptions from thrower to catcher, labeled with the\n" +
"                possible exception types.\n" +
"       --multipages :\n" +
"                produce dot file output for multiple 8.5x11\" pages.\n" +
"                By default, a single page is produced.\n" +
"       --help :\n" +
"                print this message.\n"
);
  }

  /**
   * Parse the command line arguments specific to CFGViewer,
   * @return an array of arguments to pass on to Soot.Main.main().
   */
  private String[] parse_options(String[] args){
    List sootArgs = new ArrayList(args.length);

    drawer.setBriefLabels(false);
    drawer.setOnePage(true);
    drawer.setShowExceptions(false);
    drawer.setUnexceptionalControlFlowAttr("color", "black");
    drawer.setExceptionalControlFlowAttr("color", "red");
    drawer.setExceptionEdgeAttr("color", "lightgray");

    for (int i=0, n=args.length; i<n; i++) {
      if (args[i].equals("--soot-classpath") ||
	  args[i].equals("--soot-class-path")) {
	Scene.v().setSootClassPath(args[++i]);
      } else if (args[i].equals("--alt-classpath") ||
	  args[i].equals("--alt-class-path")) {
	AltClassLoader.v().setAltClassPath(args[++i]);
      } else if (args[i].startsWith("--graph=")) {
	graphtype = 
	  CFGGraphType.getGraphType(args[i].substring("--graph=".length()));
      } else if (args[i].startsWith("--ir=")) {
	ir = 
	  CFGIntermediateRep.getIR(args[i].substring("--ir=".length()));
      } else if (args[i].equals("--brief")) {
	drawer.setBriefLabels(true);
      } else if (args[i].startsWith("--throwAnalysis=")) {
	throwAnalysis = 
	  getThrowAnalysis(args[i].substring("--throwAnalysis=".length()));
      } else if (args[i].equals("--showExceptions")) {
	drawer.setShowExceptions(true);
      } else if (args[i].equals("--multipages")) {
	drawer.setOnePage(false);
      } else if (args[i].equals("--help")) {
	return new String[0];	// This is a cheesy method to inveigle
				// our caller into printing the help
				// and exiting.
      } else if (args[i].equals("-p") ||
		 args[i].equals("--phase-option") ||
		 args[i].equals("-phase-option")) {
	// Pass the phase option right away, so the colon doesn't look
	// like a method specifier.
	sootArgs.add(args[i]);
	sootArgs.add(args[++i]);
	sootArgs.add(args[++i]);
      } else {
	int smpos = args[i].indexOf(':');
	if (smpos == -1) {
	  sootArgs.add(args[i]); 
	} else {
	  String clsname  = args[i].substring(0, smpos);
	  sootArgs.add(clsname);
	  String methname = args[i].substring(smpos+1);
	  if (methodsToPrint == null) {
	    methodsToPrint = new HashMap();
	  }
	  methodsToPrint.put(methname, clsname);
	}
      }
    }
    String[] sootArgsArray = new String[sootArgs.size()];
    return (String[]) sootArgs.toArray(sootArgsArray);
  }

  protected void print_cfg(Body body) {
    DirectedGraph graph = graphtype.buildGraph(body);
    DotGraph canvas = graphtype.drawGraph(drawer, graph, body);

    String methodname = body.getMethod().getSubSignature();
    String filename = soot.SourceLocator.v().getOutputDir();
    if (filename.length() > 0) {
	filename = filename + java.io.File.separator;
    }
    filename = filename + 
      methodname.replace(java.io.File.separatorChar, '.') + 
      DotGraph.DOT_EXTENSION;

    canvas.plot(filename);
  }
}
