/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;
import soot.*;

/**
 * Allows specifing which SootMethod you want to analyse in a
 * AbstractInterproceduralAnalysis.
 *
 * You will need a way to provide a summary for unanalysed methods that
 * are used by analysed code!
 */
public interface SootMethodFilter {

    public boolean want(SootMethod m);

}
