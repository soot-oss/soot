package soot;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import fj.Function;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

public class CallGraphTest {
	private static final Logger logger = Logger.getLogger(CallGraphTest.class.getName());
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		Options.v().set_whole_program(true);
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_no_bodies_for_excluded(true);
		
		Options.v().set_process_dir(Collections.singletonList("D:\\Java_8_Programs\\Jar_Executables\\Checkstyle\\checkstyle-8.27-SNAPSHOT.jar"));
		//Options.v().set_process_dir(Collections.singletonList("D:\\Java_8_Programs\\Jar_Executables\\Javadoc\\javadoc-reloaded-1.0-SNAPSHOT.jar"));
		//Options.v().set_process_dir(Collections.singletonList("D:\\Java_8_Programs\\Jar_Executables\\Matrix\\github-matrix-0.0.1-SNAPSHOT.jar"));
		//Options.v().set_process_dir(Collections.singletonList("D:\\Java_8_Programs\\Jar_Executables\\Packr\\packr-2.1-SNAPSHOT-jar-with-dependencies.jar"));
		//Options.v().set_process_dir(Collections.singletonList("D:\\Java_8_Programs\\Jar_Executables\\Spoon\\spoon-core-7.6.0-SNAPSHOT-jar-with-dependencies.jar"));
		
		
		PhaseOptions.v().setPhaseOption("cg.cha", "on");
		
		//PhaseOptions.v().setPhaseOption("cg.spark", "on");
		
		Scene.v().loadNecessaryClasses();
		
		long startTime = System.currentTimeMillis();
		PackManager.v().runPacks();
		long endTime = System.currentTimeMillis();
		System.out.println("Start time is" + startTime);
		System.out.println("End time is" + endTime);
		System.out.println("Total elapsed time is" + (endTime - startTime));
		
		CallGraph chaCallGraph = Scene.v().getCallGraph();
		
		System.out.println("Main Class is" + Scene.v().getMainClass());
		
		System.out.println("Main method is" + Scene.v().getMainMethod());
		
		WriteCallGraph();
		
		System.out.println(chaCallGraph.size());
		
	}
	
	private static void WriteCallGraph() throws FileNotFoundException, UnsupportedEncodingException {	
		
		System.out.println("Writing call graph to a file....!!!");
		PrintWriter writer = new PrintWriter("D:\\Java_8_Programs\\Jar_Executables\\Differences_ReturnType_V2\\Checkstyle\\CallGraphs\\Checkstyle_CHA_Develop.txt", "UTF-8");
		//PrintWriter writer = new PrintWriter("D:\\Java_8_Programs\\Checkstyle_CHA_Develop_Test.txt", "UTF-8");
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> edgeToSort = cg.iterator();
		ArrayList<Edge> edgeList = Lists.newArrayList(edgeToSort);
		for(Edge edge: edgeList) {
			if(edge.tgt().getDeclaringClass().isInterface() || edge.src().getDeclaringClass().isInterface()) {
				StringBuilder tgtParameterBuilder = new StringBuilder();
				StringBuilder srcParameterBuilder = new StringBuilder();
				int srcParameterCount = edge.getSrc().method().getParameterCount();
				for(Type type: edge.getSrc().method().getParameterTypes()) {
					if(srcParameterCount > 1) {
						srcParameterBuilder.append(type.toString() + ", ");
						srcParameterCount --;
					}
					else {
						srcParameterBuilder.append(type.toString());
						srcParameterCount --;
					}
				}
				
				int tgtParameterCount = edge.getTgt().method().getParameterCount();
				for(Type type: edge.getTgt().method().getParameterTypes()) {
					if(tgtParameterCount > 1) {
						tgtParameterBuilder.append(type.toString() + ", ");
					}
					else {
						tgtParameterBuilder.append(type.toString());
					}
				}
				
				/*
				 * System.out.println("Source method is " + edge.getSrc().method().getName());
				 * System.out.println("Target method is " + edge.getTgt().method().getName());
				 */
				
				boolean omitSrcReturnType = getMethodList(edge.getSrc().method());
				boolean omitTgtReturnType = getMethodList(edge.getTgt().method());
				String defaultEdgeToPrint = null;
				if(!omitSrcReturnType && !omitTgtReturnType) {			
					defaultEdgeToPrint = edge.getSrc().method() + " => " + edge.getTgt().method();
				}
				else if(omitSrcReturnType && !omitTgtReturnType) {
					defaultEdgeToPrint = "<" + edge.getSrc().method().declaringClass + ": " + edge.getSrc().method().getName() + "(" + srcParameterBuilder + ")" +">" + " => " + edge.getTgt().method();
				}
				else if(!omitSrcReturnType && omitTgtReturnType) {
					defaultEdgeToPrint = edge.getSrc().method() + " => " + "<" + edge.getTgt().method().declaringClass + ": " + edge.getTgt().method().getName() + "(" + tgtParameterBuilder + ")" + ">";
				}
				else if(omitSrcReturnType && omitTgtReturnType) {
					defaultEdgeToPrint = "<" + edge.getSrc().method().declaringClass + ": " + edge.getSrc().method().getReturnType()+ " " + edge.getSrc().method().getName() +"(" + srcParameterBuilder + ")"+">" + " => " + "<" + edge.getTgt().method().declaringClass + ": " + edge.getTgt().method().getReturnType() + " " + edge.getTgt().method().getName() + "(" + tgtParameterBuilder + ")"+">";
				}
				//String defaultEdgeToPrint = edge.getSrc().method().declaringClass + " : " + edge.getSrc().method().getName() +"(" + srcParameterBuilder + ")" + " => " + edge.getTgt().method().declaringClass + " : " + edge.getTgt().method().getName() + "(" + tgtParameterBuilder + ")";
				//writer.println(defaultEdgeToPrint + " :(Default method edge)");
			}
			else {
				StringBuilder tgtParameterBuilder = new StringBuilder();
				StringBuilder srcParameterBuilder = new StringBuilder();
				int srcParameterCount = edge.getSrc().method().getParameterCount();
				for(Type type: edge.getSrc().method().getParameterTypes()) {
					if(srcParameterCount > 1) {
						srcParameterBuilder.append(type.toString() + ", ");
						srcParameterCount --;
					}
					else {
						srcParameterBuilder.append(type.toString());
						srcParameterCount --;
					}
				}
				
				int tgtParameterCount = edge.getTgt().method().getParameterCount();
				for(Type type: edge.getTgt().method().getParameterTypes()) {
					if(tgtParameterCount > 1) {
						tgtParameterBuilder.append(type.toString() + ", ");
					}
					else {
						tgtParameterBuilder.append(type.toString());
					}
				}				
				/*
				 * System.out.println("Source method is " + edge.getSrc().method().getName());
				 * System.out.println("Target method is " + edge.getTgt().method().getName());
				 */
				boolean omitSrcReturnType = getMethodList(edge.getSrc().method());
				boolean omitTgtReturnType = getMethodList(edge.getTgt().method());
				String normalEdgeToPrint = null;
				if(!omitSrcReturnType && !omitTgtReturnType) {			
					normalEdgeToPrint = edge.getSrc().method() + " => " + edge.getTgt().method();
				}
				else if(omitSrcReturnType && !omitTgtReturnType) {
					normalEdgeToPrint = "<" + edge.getSrc().method().declaringClass + ": " + edge.getSrc().method().getName() + "(" + srcParameterBuilder + ")" +">" + " => " + edge.getTgt().method();
				}
				else if(!omitSrcReturnType && omitTgtReturnType) {
					normalEdgeToPrint = edge.getSrc().method() + " => " + "<" +edge.getTgt().method().declaringClass + ": " + edge.getTgt().method().getName() + "(" + tgtParameterBuilder + ")" + ">";
				}
				else if(omitSrcReturnType && omitTgtReturnType) {
					normalEdgeToPrint = "<" + edge.getSrc().method().declaringClass + ": " + edge.getSrc().method().getReturnType()+ " " + edge.getSrc().method().getName() +"(" + srcParameterBuilder + ")"+">" + " => " + "<" + edge.getTgt().method().declaringClass + ": " + edge.getTgt().method().getReturnType() + " " + edge.getTgt().method().getName() + "(" + tgtParameterBuilder + ")"+">";
				}
				writer.println(normalEdgeToPrint + " :(Normal edge)");
			}
		}
		writer.close();
		System.out.println("Finished writing to a file....!!!");
	}
	
	private static boolean getMethodList(SootMethod methodName) {
		List<SootMethod> methodList = methodName.declaringClass.getMethods();
		List<SootMethod> similarMethods = new ArrayList<SootMethod>();
		for(SootMethod method: methodList) {
			if(method.getName().contentEquals(methodName.getName()) && method.getParameterCount() == methodName.getParameterCount()) {
				similarMethods.add(method);
			}
		}
		if(similarMethods.size() > 1) {
			boolean isSameParameters = getSameParamter(similarMethods);
			if(isSameParameters) {
				return true;
			}
			else {
				return false;
			}			
		}
		else {
			return false;
		}
	}
	
	private static boolean getSameParamter(List<SootMethod> similarMethodList) {		
		Map<Object, List<SootMethod>> map = similarMethodList.stream().collect(Collectors.groupingBy(x -> x.parameterTypes == null ? "No Pointers" : x.parameterTypes));
		int size = 0;		
		for(Object key: map.keySet()) {
			size = map.get(key).size();
			
		}
		if(size > 1) {
			return true;
		}
		else {
			return false;
		}
		
	}
}
