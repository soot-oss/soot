/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;
import soot.jimple.*;
import java.util.*;

/**
 * A node created dynamically and attached to a statement Stmt.
 * Can be either an inside or a load node.
 * Two such nodes are equal if and only if they have the same inside / load
 * flag and are attached to the same statement (we use Stmt.equal here).
 *
 */
public class PurityStmtNode implements PurityNode
{
    /** Statement that created the node */
    private Stmt id;

    /** true if an inside node, false if an load node */
    private boolean inside;

    /** gives a unique id, for pretty-printing purposes */
    private static Map nMap = new HashMap();
    private static int n = 0;

    PurityStmtNode(Stmt id, boolean inside)
    { 
	this.id = id; this.inside = inside;
	if (!nMap.containsKey(id)) { nMap.put(id,new Integer(n)); n++; }
    }

    public String toString() 
    { 
	if (inside) return "I_"+nMap.get(id); else return "L_"+nMap.get(id);
	//if (inside) return "I_"+id; else return "L_"+id; 
    }

    public int hashCode() 
    { return id.hashCode(); }
    
    public boolean equals(Object o)
    {
	if (o instanceof PurityStmtNode) {
	    PurityStmtNode oo = (PurityStmtNode)o;
	    return id.equals(oo.id) && oo.inside==inside;
	}
	else return false;
    }

    public boolean isInside() 
    { return inside; }

    public boolean isLoad()
    { return !inside; }

    public boolean isParam() 
    { return false; }
}
