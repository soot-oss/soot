package soot.jimple.toolkits.ide;

import soot.SootMethod;
import soot.Unit;
import de.bodden.ide.InterproceduralCFG;
import de.bodden.ide.template.DefaultIDETabulationProblem;
import de.bodden.ide.template.DefaultIFDSTabulationProblem;

/**
 *  A {@link DefaultIDETabulationProblem} with {@link Unit}s as nodes and {@link SootMethod}s as methods.
 */
public abstract class DefaultJimpleIFDSTabulationProblem<D,I extends InterproceduralCFG<Unit,SootMethod>>
  extends DefaultIFDSTabulationProblem<Unit,D,SootMethod,I> {

	public DefaultJimpleIFDSTabulationProblem(I icfg) {
		super(icfg);
	}
	
}
