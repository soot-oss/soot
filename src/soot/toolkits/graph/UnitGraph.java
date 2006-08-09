/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-2003.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.graph;



import soot.*;
import soot.util.*;
import java.util.*;

import soot.options.Options;


/**
 *  <p>
 *  Represents a CFG where the nodes are {@link Unit} instances and
 *  edges represent unexceptional and (possibly) exceptional control
 *  flow between <tt>Unit</tt>s.</p>
 *
 *  <p>
 *  This is an abstract class, providing the facilities used to build
 *  CFGs for specific purposes.</p>
 */

public abstract class UnitGraph implements DirectedGraph
{
    List heads;
    List tails;

    protected Map unitToSuccs;
    protected Map unitToPreds;        
    protected SootMethod method;
    protected Body body;
    protected Chain unitChain;

    /**
     *   Performs the work that is required to construct any sort of 
     *   <tt>UnitGraph</tt>.
     *
     *   @param body The body of the method for which to construct a 
     *               control flow graph.
     */
    protected UnitGraph( Body body) {
	this.body = body;
	unitChain = body.getUnits();
        method = body.getMethod();
        if(Options.v().verbose())
	    G.v().out.println("[" + method.getName() + "]     Constructing " + 
			      this.getClass().getName() + "...");
      
    }
    

    /**
     * Utility method for <tt>UnitGraph</tt> constructors. It computes
     * the edges corresponding to unexceptional control flow.
     *
     * @param unitToSuccs A {@link Map} from {@link Unit}s to 
     *                    {@link List}s of {@link Unit}s. This is
     *	                  an ``out parameter''; callers must pass an empty
     *                    {@link Map}. <tt>buildUnexceptionalEdges</tt> will
     *                    add a mapping for every <tt>Unit</tt> in the
     *                    body to a list of its unexceptional successors.
     *
     * @param unitToPreds A {@link Map} from {@link Unit}s to 
     *                    {@link List}s of {@link Unit}s. This is an 
     *                    ``out parameter''; callers must pass an empty 
     *                    {@link Map}. <tt>buildUnexceptionalEdges</tt> will
     *                    add a mapping for every <tt>Unit</tt> in the body
     *                    to a list of its unexceptional predecessors.
     */
    protected void buildUnexceptionalEdges(Map unitToSuccs, Map unitToPreds) {

	// Initialize the predecessor sets to empty
	for (Iterator unitIt = unitChain.iterator(); unitIt.hasNext(); ) {
	    unitToPreds.put(unitIt.next(), new ArrayList());
	}
	
	Iterator unitIt = unitChain.iterator();
	Unit currentUnit, nextUnit;
                
	nextUnit = unitIt.hasNext() ? (Unit) unitIt.next(): null;
                
	while(nextUnit != null) {
	    currentUnit = nextUnit;
	    nextUnit = unitIt.hasNext() ? (Unit) unitIt.next(): null;
                    
	    List successors = new ArrayList();
                    
	    if( currentUnit.fallsThrough() ) {
		// Add the next unit as the successor
		if(nextUnit != null) {
		    successors.add(nextUnit);
		    ((List) unitToPreds.get(nextUnit)).add(currentUnit);
		}
	    }
                        
	    if( currentUnit.branches() ) {
		for (Iterator targetIt = currentUnit.getUnitBoxes().iterator();
		     targetIt.hasNext(); ) {
		    Unit target = ((UnitBox) targetIt.next()).getUnit();
		    // Arbitrary bytecode can branch to the same
		    // target it falls through to, so we screen for duplicates:
		    if (! successors.contains(target)) {
			successors.add(target);
			((List) unitToPreds.get(target)).add(currentUnit);
		    }
		}
	    }

	    // Store away successors
	    unitToSuccs.put(currentUnit, successors);
	}
    }


    /**
     * <p>Utility method used in the construction of {@link UnitGraph}s, to be
     * called only after the unitToPreds and unitToSuccs maps have
     * been built.</p>
     *
     * <p><code>UnitGraph</code> provides an implementation of
     * <code>buildHeadsAndTails()</code> which defines the graph's set
     * of heads to include the first {@link Unit} in the graph's body,
     * together with any other <tt>Unit</tt> which has no predecessors.
     * It defines the graph's set of tails to include all
     * <tt>Unit</tt>s with no successors.  Subclasses of
     * <code>UnitGraph</code> may override this method to change the
     * criteria for classifying a node as a head or tail.</p>
     */
    protected void buildHeadsAndTails() {
	List tailList = new ArrayList();
	List headList = new ArrayList();

	for (Iterator unitIt = unitChain.iterator(); unitIt.hasNext(); ) {
	    Unit s = (Unit) unitIt.next();
	    List succs = (List) unitToSuccs.get(s);
	    if(succs.size() == 0) {
		tailList.add(s);
	    }
	    List preds = (List) unitToPreds.get(s);
	    if(preds.size() == 0) {
		headList.add(s);
	    }
	}

	// Add the first Unit, even if it is the target of
	// a branch.
	Unit entryPoint = (Unit) unitChain.getFirst();
	if (! headList.contains(entryPoint)) {
	    headList.add(entryPoint);
	}

	tails = Collections.unmodifiableList(tailList);
	heads = Collections.unmodifiableList(headList);
    }


    /**
     * Utility method that replaces the values of a {@link Map}, 
     * which must be instances of {@link List}, with unmodifiable
     * equivalents.
     * 
     * @param map      The map whose values are to be made unmodifiable.
     */
    protected static void makeMappedListsUnmodifiable(Map map) {
	for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry entry = (Map.Entry) it.next();
	    List value = (List) entry.getValue();
	    if (value.size() == 0) {
		entry.setValue(Collections.EMPTY_LIST);
	    } else {
		entry.setValue(Collections.unmodifiableList(value));
	    }
	}
    }


    /**
     * Utility method that produces a new map from the {@link Unit}s
     * of this graph's body to the union of the values stored in the
     * two argument {@link Map}s, used to combine the maps of
     * exceptional and unexceptional predecessors and successors into
     * maps of all predecessors and successors. The values stored in
     * both argument maps must be {@link List}s of {@link Unit}s,
     * which are assumed not to contain any duplicate <tt>Unit</tt>s.
     * 
     * @param mapA      The first map to be combined. 
     *
     * @param mapB	The second map to be combined.
     */
    protected Map combineMapValues(Map mapA, Map mapB) {
	// The duplicate screen 
	Map result = new HashMap(mapA.size() * 2 + 1, 0.7f);
	for (Iterator chainIt = unitChain.iterator(); chainIt.hasNext(); ) {
	    Unit unit = (Unit) chainIt.next();
	    List listA = (List) mapA.get(unit);
	    if (listA == null) {
		listA = Collections.EMPTY_LIST;
	    }
	    List listB = (List) mapB.get(unit);
	    if (listB == null) {
		listB = Collections.EMPTY_LIST;
	    }

	    int resultSize = listA.size() + listB.size();
	    if (resultSize == 0) {
		result.put(unit, Collections.EMPTY_LIST);
	    } else {
		List resultList = new ArrayList(resultSize);
		Iterator listIt = null;
		// As a minor optimization of the duplicate screening, 
		// copy the longer list first.
		if (listA.size() >= listB.size()) {
		    resultList.addAll(listA);
		    listIt = listB.iterator();
		} else {
		    resultList.addAll(listB);
		    listIt = listA.iterator();
		}
		while (listIt.hasNext()) {
		    Object element = listIt.next();
		    // It is possible for there to be both an exceptional
		    // and an unexceptional edge connecting two Units
		    // (though probably not in a class generated by
		    // javac), so we need to screen for duplicates. On the
		    // other hand, we expect most of these lists to have
		    // only one or two elements, so it doesn't seem worth
		    // the cost to build a Set to do the screening.
		    if (! resultList.contains(element)) {
			resultList.add(element);
		    }
		}
		result.put(unit, Collections.unmodifiableList(resultList));
	    }
	}
	return result;
    }


    /**
     * Utility method for adding an edge to maps representing the CFG.
     * 
     * @param unitToSuccs The {@link Map} from {@link Unit}s to {@link List}s
     *                    of their successors.
     *
     * @param unitToPreds The {@link Map} from {@link Unit}s to {@link List}s
     *                    of their successors.
     *
     * @param head     The {@link Unit} from which the edge starts.
     *
     * @param tail     The {@link Unit} to which the edge flows.
     */
    protected void addEdge(Map unitToSuccs, Map unitToPreds,
			   Unit head, Unit tail) {
	List headsSuccs = (List) unitToSuccs.get(head);
	if (headsSuccs == null) {
	    headsSuccs = new ArrayList(3); // We expect this list to
					   // remain short.
	    unitToSuccs.put(head, headsSuccs);
	}
	if (! headsSuccs.contains(tail)) {
	    headsSuccs.add(tail);
	    List tailsPreds = (List) unitToPreds.get(tail);
	    if (tailsPreds == null) {
		tailsPreds = new ArrayList();
		unitToPreds.put(tail, tailsPreds);
	    }
	    tailsPreds.add(head);
	}
    }


    /**
     *   @return The body from which this UnitGraph was built.
     *
     *  @see Body
     */
    public Body getBody()
    {
        return body;
    }



  /**
   *  Look for a path in graph,  from def to use. 
   *  This path has to lie inside an extended basic block 
   *  (and this property implies uniqueness.). The path returned 
   *   includes from and to.
   *
   *  @param from start point for the path.
   *  @param to   end point for the path. 
   *  @return null if there is no such path.
   */
  public List getExtendedBasicBlockPathBetween(Unit from, Unit to)
    {
        UnitGraph g = this;
        
      // if this holds, we're doomed to failure!!!
      if (g.getPredsOf(to).size() > 1)
        return null;

      // pathStack := list of succs lists
      // pathStackIndex := last visited index in pathStack
      LinkedList pathStack = new LinkedList();
      LinkedList pathStackIndex = new LinkedList();

      pathStack.add(from);
      pathStackIndex.add(new Integer(0));

      int psiMax = (g.getSuccsOf((Unit)pathStack.get(0))).size();
      int level = 0;
      while (((Integer)pathStackIndex.get(0)).intValue() != psiMax)
        {
          int p = ((Integer)(pathStackIndex.get(level))).intValue();

          List succs = g.getSuccsOf((Unit)(pathStack.get(level)));
          if (p >= succs.size())
            {
              // no more succs - backtrack to previous level.

              pathStack.remove(level);
              pathStackIndex.remove(level);

              level--;
              int q = ((Integer)pathStackIndex.get(level)).intValue();
              pathStackIndex.set(level, new Integer(q+1));
              continue;
            }

          Unit betweenUnit = (Unit)(succs.get(p));

          // we win!
          if (betweenUnit == to)
            {
              pathStack.add(to);
              return pathStack;
            }

          // check preds of betweenUnit to see if we should visit its kids.
          if (g.getPredsOf(betweenUnit).size() > 1)
            {
              pathStackIndex.set(level, new Integer(p+1));
              continue;
            }

          // visit kids of betweenUnit.
          level++;
          pathStackIndex.add(new Integer(0));
          pathStack.add(betweenUnit);
        }
      return null;
    }       


    /* DirectedGraph implementation */
    public List getHeads()
    {
        return heads;
    }

    public List getTails()
    {
        return tails;
    }

    public List getPredsOf(Object u)
    {
        if(!unitToPreds.containsKey(u)) 
	    throw new NoSuchElementException("Invalid unit " + u);

        return (List) unitToPreds.get(u);
    }

    public List getSuccsOf(Object u)
    {
		List l = (List) unitToSuccs.get(u);
        if (l == null) throw new RuntimeException("Invalid unit " + u);
        return l;
    }

    public int size()
    {
        return unitChain.size();
    }  

    public Iterator iterator()
    {
        return unitChain.iterator();
    }

    public String toString() 
    {
        Iterator it = unitChain.iterator();
        StringBuffer buf = new StringBuffer();
        while(it.hasNext()) {
            Unit u = (Unit) it.next();
            buf.append("// preds: "+getPredsOf(u)+"\n");
            buf.append(u.toString() + '\n');
            buf.append("// succs "+getSuccsOf(u)+"\n");
        }
        return buf.toString();
    }
}
