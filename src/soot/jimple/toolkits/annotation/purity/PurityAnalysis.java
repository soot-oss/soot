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

	// Filter: for now, keep everything
	class Filter implements SootMethodFilter {
	    public boolean want(SootMethod m) { 
		String s = m.toString();
		if (s.indexOf("<sun.")!=-1 ||
		    s.indexOf("<com.")!=-1 ||
		    s.indexOf("<org.")!=-1 ||
		    s.indexOf("<javax.")!=-1)
		    return false;
		return true; 
	    }
	}

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
	    new PurityInterproceduralAnalysis(cg, 
					      new Filter(), 
					      heads.iterator(),
					      opts); 
    }
}
