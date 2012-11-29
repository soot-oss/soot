package soot.jimple.toolkits.ide;

import heros.InterproceduralCFG;
import heros.template.DefaultIDETabulationProblem;
import heros.template.DefaultIFDSTabulationProblem;
import soot.SootMethod;
import soot.Unit;

/**
 *  A {@link DefaultIDETabulationProblem} with {@link Unit}s as nodes and {@link SootMethod}s as methods.
 */
public abstract class DefaultJimpleIFDSTabulationProblem<D,I extends InterproceduralCFG<Unit,SootMethod>>
  extends DefaultIFDSTabulationProblem<Unit,D,SootMethod,I> {

	public DefaultJimpleIFDSTabulationProblem(I icfg) {
		super(icfg);
	}
	
}
