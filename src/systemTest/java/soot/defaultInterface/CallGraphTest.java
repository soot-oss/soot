package soot.defaultInterface;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.google.common.collect.Lists;

import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

public class CallGraphTest {
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Options.v().set_whole_program(true);
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_no_bodies_for_excluded(true);
		
		Options.v().set_process_dir(Collections.singletonList("D:\\Java_8_Programs\\Jar_Executables\\Sample.jar"));		
		PhaseOptions.v().setPhaseOption("cg.cha", "on");
		
		Scene.v().loadNecessaryClasses();
		
		PackManager.v().runPacks();
		
		WriteCallGraph();
		
	}
	
	private static void WriteCallGraph() throws FileNotFoundException, UnsupportedEncodingException {	

		System.out.println("Writing call graph to a file....!!!");		
		PrintWriter writer = new PrintWriter("D:\\JarFiles\\Develop_Edges.txt", "UTF-8");
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> mainMethodEdges = cg.edgesOutOf(Scene.v().getMethod("<com.pubbycrawl.tools.checkstyle.checks.metrics.JavaNCSSCheck: void finishTree()>"));
		
		//Iterator<Edge> edgeToSort = cg.iterator();
		ArrayList<Edge> edgeList = Lists.newArrayList(mainMethodEdges);	
		for(Edge edge: edgeList) {
			writer.println(edge);
		}
		writer.close();
		System.out.println("Finished writing to a text file!!!");
	}

}
