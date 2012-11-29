package soot.jimple.toolkits.ide;

import soot.SootMethod;
import soot.Unit;
import de.bodden.ide.InterproceduralCFG;
import de.bodden.ide.template.DefaultIDETabulationProblem;

/**
 *  A {@link DefaultIDETabulationProblem} with {@link Unit}s as nodes and {@link SootMethod}s as methods.
 */
public abstract class DefaultJimpleIDETabulationProblem<D,V,I extends InterproceduralCFG<Unit,SootMethod>>
  extends DefaultIDETabulationProblem<Unit,D,SootMethod,V,I> {

	public DefaultJimpleIDETabulationProblem(I icfg) {
		super(icfg);
	}
	
}
