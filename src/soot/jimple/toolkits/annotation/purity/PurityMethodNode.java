/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;
import soot.*;
import java.util.*;

/**
 * Kind of Stmt inside node, but global to the method.
 * Used for synthetic summary of unalysed methods returning a fresh object.
 */
public class PurityMethodNode implements PurityNode
{
    /** Method that created the node */
    private SootMethod id;

    /** gives a unique id, for pretty-printing purposes */
    private static Map nMap = new HashMap();
    private static int n = 0;

    PurityMethodNode(SootMethod id)
    { 
	this.id = id;
	if (!nMap.containsKey(id)) { nMap.put(id,new Integer(n)); n++; }
    }

    public String toString() 
    { 
	return "M_"+nMap.get(id);
	//return ""+id;
    }

    public int hashCode() 
    { return id.hashCode(); }
    
    public boolean equals(Object o)
    {
	if (o instanceof PurityMethodNode) {
	    PurityMethodNode oo = (PurityMethodNode)o;
	    return id.equals(oo.id);
	}
	else return false;
    }

    public boolean isInside() 
    { return true; }

    public boolean isLoad()
    { return false; }

    public boolean isParam() 
    { return false; }
}
