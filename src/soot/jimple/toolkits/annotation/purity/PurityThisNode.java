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
 * (should we make it a singleton ?)
 */ 
public class PurityThisNode extends PurityParamNode
{
    private PurityThisNode() { super(-1); }
    
    public static PurityThisNode node = new PurityThisNode();

    public String toString() { return "this"; }

    public boolean isInside() 
    { return false; }

    public boolean isLoad()
    { return false; }

    public boolean isParam() 
    { return true; }
}
