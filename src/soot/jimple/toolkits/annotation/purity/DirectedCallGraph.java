/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Antoine Mine
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * Implementation of the paper "A Combined Pointer and Purity Analysis for
 * Java Programs" by Alexandru Salcianu and Martin Rinard, within the
 * Soot Optimization Framework.
 *
 * by Antoine Mine, 2005/01/24
 */

package soot.jimple.toolkits.annotation.purity;
import java.util.*;
import soot.*;
import soot.util.*;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.graph.*;

/**
 * Builds a DirectedGraph from a CallGraph and SootMethodFilter.
 *
 * This is used in AbstractInterproceduralAnalysis to construct a reverse
 * pseudo topological order on which to iterate.
 * You can specify a SootMethodFilter to trim the graph by cutting 
 * call edges strarting
 *
 * Methods filtered-out by the SootMethodFilter will not appear in the
 * DirectedGraph!
 */
public class DirectedCallGraph implements DirectedGraph {

    protected Set  nodes;
    protected Map<Object,List>  succ;
    protected Map<Object,List>  pred;
    protected List heads;
    protected List tails;
    protected int  size;

    /**
     * The constructor does all the work here.
     * After constructed, you can safely use all interface methods.
     * Moreover, these methods should perform very fastly...
     *
     * The DirectedGraph will only contain methods in call paths from a method
     * in head and comprising only methods wanted by filter.
     * Moreover, only concrete methods are put in the graph...
     *
     * @param heads is a List of SootMethod
     */
    public DirectedCallGraph(CallGraph        cg,
			     SootMethodFilter filter,
			     Iterator         heads,
			     boolean          verbose)
    {
	// filter heads by filter
	List filteredHeads = new LinkedList();
	while (heads.hasNext()) {
	    SootMethod m = (SootMethod) heads.next();
	    if (m.isConcrete() && filter.want(m)) filteredHeads.add(m);
	}

	this.nodes = new HashSet(filteredHeads);
	
	MultiMap s = new HashMultiMap();
	MultiMap p = new HashMultiMap();

	// simple breadth-first visit
	Set remain = new HashSet(filteredHeads);
	int nb = 0;
	if (verbose) G.v().out.println("[AM] dumping method dependencies");
	while (!remain.isEmpty()) {
	    Set newRemain = new HashSet();
	    Iterator it = remain.iterator();
	    while (it.hasNext()) {
		SootMethod m = (SootMethod)it.next();
		Iterator itt = cg.edgesOutOf(m);
		if (verbose) 
		    G.v().out.println(" |- "+m.toString()+" calls");
		while (itt.hasNext())  {
		    Edge edge = (Edge)itt.next();
		    SootMethod mm = edge.tgt();
		    boolean keep = mm.isConcrete() && filter.want(mm);
		    if (verbose)
			G.v().out.println(" |  |- "+mm.toString()+
					  (keep?"":" (filtered out)"));
		    if (keep) {
			if (this.nodes.add(mm)) newRemain.add(mm);
			s.put(m,mm);
			p.put(mm,m);
		    }
		}
		nb++;
	    }
	    remain = newRemain;
	}
	G.v().out.println("[AM] number of methods to be analysed: "+nb);

	// MultiMap -> Map of List
	this.succ   = new HashMap();
	this.pred   = new HashMap();
	this.tails  = new LinkedList();
	this.heads  = new LinkedList();
	Iterator it = this.nodes.iterator();
	while (it.hasNext()) {
	    Object x = it.next();
	    Set ss   = s.get(x);
	    Set pp   = p.get(x);
	    this.succ.put(x, new LinkedList(ss));
	    this.pred.put(x, new LinkedList(pp));
	    if (ss.isEmpty()) this.tails.add(x);
	    if (pp.isEmpty()) this.heads.add(x);
	}

	this.size  = this.nodes.size();
    }

    /** You get a List of SootMethod. */
    public List getHeads() { return heads; }

    /** You get a List of SootMethod. */
    public List getTails() { return tails; }

    /** You get an Iterator on SootMethod. */
    public Iterator iterator() { return nodes.iterator(); }

    public int size() { return size; }
    
    /** You get a List of SootMethod. */
    public List getSuccsOf(Object s) { return succ.get(s); }

    /** You get a List of SootMethod. */
    public List getPredsOf(Object s) { return pred.get(s); }
}
