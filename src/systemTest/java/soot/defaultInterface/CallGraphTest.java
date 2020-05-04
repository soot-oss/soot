package soot.defaultInterface;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
		
		//String pathToJar = "D:\\Java_8_Programs\\Jar_Executables\\Sample.jar";
		Options.v().set_process_dir(Collections.singletonList("D:\\Java_8_Programs\\Jar_Executables\\Checkstyle\\checkstyle-8.27-SNAPSHOT.jar"));		
		PhaseOptions.v().setPhaseOption("cg.cha", "on");
		
		Scene.v().loadNecessaryClasses();
		
		PackManager.v().runPacks();
		
		System.out.println("Done Analyzing");
		
		//ArrayList<Edge> edge = WriteCallGraph();
		
		//assertEquals(edge.get(0).getTgt(), Scene.v().getMethod("<com.pubbycrawl.tools.checkstyle.api.AbstractCheck: void log(java.lang.String,java.lang.String)>"));		
		
	}
	
	private static ArrayList<Edge> WriteCallGraph() {
		
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> mainMethodEdges = cg.edgesOutOf(Scene.v().getMethod("<com.pubbycrawl.tools.checkstyle.checks.metrics.JavaNCSSCheck: void finishTree()>"));
		ArrayList<Edge> edgeList = Lists.newArrayList(mainMethodEdges);
		return edgeList;
		
	}

}
