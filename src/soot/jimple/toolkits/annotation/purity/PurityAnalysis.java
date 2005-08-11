/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Antoine Mine
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

/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */


package soot.jimple.toolkits.annotation.purity;
import java.util.*;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.options.PurityOptions;

/**
 * Purity analysis phase.
 */

/**
 * TODO:
 *  - test, test, and test (and correct the potentially infinite bugs)
 *  - optimise PurityGraph, especially methodCall)
 *  - find a better abstraction for exceptions (throw & catch)
 *  - output nicer graphs (especially clusters!)
 */

public class PurityAnalysis extends SceneTransformer
{
    Singletons.Global g;

    public PurityAnalysis(Singletons.Global g ) { this.g = g; }

    public static PurityAnalysis v() 
    {
	return G.v().soot_jimple_toolkits_annotation_purity_PurityAnalysis(); 
    }

    protected void internalTransform(String phaseName, Map options)
    {
	PurityOptions opts = new PurityOptions(options);

	G.v().out.println("[AM] Analysing purity");

	CallGraph cg = Scene.v().getCallGraph();

	// Find main methods
	List heads = new LinkedList();
	Iterator getClassesIt = Scene.v().getApplicationClasses().iterator();
	while (getClassesIt.hasNext()) {
	    SootClass appClass = (SootClass)getClassesIt.next();
	    Iterator getMethodsIt = appClass.getMethods().iterator();
	    while (getMethodsIt.hasNext()) {
		SootMethod method = (SootMethod) getMethodsIt.next();
		if (method.getName().equals("main")) {
		    heads.add(method);
		    G.v().out.println("  |- will treat "+appClass.getName()+
				      "."+method.getName());
		}
	    }
	}

	// launch the analysis
	PurityInterproceduralAnalysis p =
	    new PurityInterproceduralAnalysis(cg, heads.iterator(), opts); 
    }
}
