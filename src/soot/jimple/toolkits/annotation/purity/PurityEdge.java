/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;

/** 
 * An edge in a purity graph.
 * Each edge has a soruce PurityNode, a taget PurityNode, and a field label
 * (we use a String here).
 * To represent an array element, the convention is to use the [] field label.
 * Edges are mmuable and hashable. They compare equal only if they link
 * equal nodes and have equal labels.
 *
 */
public class PurityEdge
{
    private String     field; 
    private PurityNode source, target;
    private boolean    inside;

    PurityEdge(PurityNode source, String field, PurityNode target, boolean inside)
    {
	this.source = source;
	this.field  = field;
	this.target = target;
	this.inside = inside;
    }

    public String     getField()  { return field; }
    public PurityNode getTarget() { return target; }
    public PurityNode getSource() { return source; }
    public boolean    isInside()  { return inside; }

    public int hashCode() 
    { return field.hashCode()+target.hashCode()+source.hashCode()+(inside?69:0); }

    public boolean equals(Object o)
    {
	if (!(o instanceof PurityEdge)) return false;
	PurityEdge e = (PurityEdge)o;
	return source.equals(e.source) && field.equals(e.field) 
	    && target.equals(e.target) && inside==e.inside;
    }

    public String toString()
    {
	if (inside)
	    return source.toString()+" = "+field+" => "+target.toString(); 
	else
	    return source.toString()+" - "+field+" -> "+target.toString(); 
	
    }
}

