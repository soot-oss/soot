/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;

/**
 * Simple box class that encapsulates a reference to a PurityGraph.
 * 
 */
public class PurityGraphBox
{
    public PurityGraph g;

    PurityGraphBox()
    { g = new PurityGraph(); }

    public int hashCode()
    { return g.hashCode(); }

    public boolean equals(Object o)
    { return g.equals(((PurityGraphBox)o).g); }
}

