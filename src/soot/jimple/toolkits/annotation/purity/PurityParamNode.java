/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;

/**
 * A node representing a method parameter.
 * Each method parameter has a number, starting from 0.
 * 
 */
public class PurityParamNode implements PurityNode
{
    private int id;

    PurityParamNode(int id) { this.id = id; }

    public String toString() { return "P_"+id; }

    public int hashCode() { return id; }
    
    public boolean equals(Object o)
    {
	if (o instanceof PurityParamNode) return ((PurityParamNode)o).id==id;
	else return false;
    }

    public boolean isInside() 
    { return false; }

    public boolean isLoad()
    { return false; }

    public boolean isParam() 
    { return true; }
}

