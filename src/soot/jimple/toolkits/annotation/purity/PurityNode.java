/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;

/**
 * Interface shared by all kinds of nodes in a PurityGraph.
 * Such nodes are immuables. They are hashable and two nodes are equal
 * only if they have the same kind and were constructed using the same
 * arguments (structural equality).
 *
 */
public interface PurityNode { 

    /** Is it an inside node ? */
    public boolean isInside();

    /** Is it a load node ? */
    public boolean isLoad();

    /** Is it a parameter or this node ? */
    public boolean isParam();
} 
