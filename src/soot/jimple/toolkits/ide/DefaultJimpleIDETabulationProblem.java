package soot.jimple.toolkits.ide;

import heros.InterproceduralCFG;
import heros.template.DefaultIDETabulationProblem;
import soot.SootMethod;
import soot.Unit;

/**
 *  A {@link DefaultIDETabulationProblem} with {@link Unit}s as nodes and {@link SootMethod}s as methods.
 */
public abstract class DefaultJimpleIDETabulationProblem<D,V,I extends InterproceduralCFG<Unit,SootMethod>>
  extends DefaultIDETabulationProblem<Unit,D,SootMethod,V,I> {

	public DefaultJimpleIDETabulationProblem(I icfg) {
		super(icfg);
	}
	
}
