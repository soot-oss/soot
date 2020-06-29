package soot.defaultInterface;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 Manuel Benz
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.Lists;

import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

/**
 * @author Pavan Gurkhi Bhimesh created on 29.06.20
 */

public class CallGraphTest {	
	public void SubClassTest() throws FileNotFoundException, UnsupportedEncodingException {
		Options.v().set_whole_program(true);
		Options.v().set_output_format(Options.output_format_none);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_no_bodies_for_excluded(true);
		
		String pathToJar = "./target/test-classes";
		Options.v().set_process_dir(Collections.singletonList(pathToJar));	
		PhaseOptions.v().setPhaseOption("cg.cha", "on");
		
		Scene.v().loadNecessaryClasses();
		
		PackManager.v().runPacks();
		
		ArrayList<Edge> edges = GetCallGraph();
		
		assertEquals(edges.get(0).getTgt(), Scene.v().getMethod("<com.pubbycrawl.tools.checkstyle.api.AbstractCheck: void log(java.lang.String,java.lang.String)>"));		
		
	}
	
	private static ArrayList<Edge> GetCallGraph() {			
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> mainMethodEdges = cg.edgesOutOf(Scene.v().getMethod("<com.pubbycrawl.tools.checkstyle.checks.metrics.JavaNCSSCheck: void finishTree()>"));
		ArrayList<Edge> edgeList = Lists.newArrayList(mainMethodEdges);
		return edgeList;		
	}

}
