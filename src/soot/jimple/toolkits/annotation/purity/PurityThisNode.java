/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;

/**
 * A node representing the this parameter.
 * It is simply a parameter node with special printing. 
 * (should we make it a singleton ?)
 */ 
public class PurityThisNode extends PurityParamNode
{
    PurityThisNode() { super(-1); }

    public String toString() { return "this"; }
}
