/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;

/** 
 * The GBL node.
 * Each purity graph has only one of this kind. (should we make it a singleton ?)
 */
public class PurityGlobalNode implements PurityNode
{
    PurityGlobalNode() {}

    public String toString()   
    { return "GBL"; }

    public int hashCode()  
    { return 0; }
    
    public boolean equals(Object o)
    { return o instanceof PurityGlobalNode; }
    
    public boolean isInside() 
    { return false; }

    public boolean isLoad()
    { return false; }

    public boolean isParam() 
    { return false; }
}
