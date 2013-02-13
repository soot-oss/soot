/* Soot - a J*va Optimization Framework
 * Copyright (C) 2008 Eric Bodden
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

package soot.toolkits.graph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 *  Identifies and provides an interface to query the strongly-connected
 *  components of DirectedGraph instances.
 *  
 *  Uses Tarjan's algorithm.
 *  
 *  @see DirectedGraph
 *  @author Eric Bodden
 */

public class StronglyConnectedComponentsFast<N>
{
  protected final List<List<N>> componentList = new ArrayList<List<N>>();
  protected final List<List<N>> trueComponentList = new ArrayList<List<N>>();

  protected int index = 0;

  protected Map<N,Integer> indexForNode, lowlinkForNode;

  protected Stack<N> s;

  protected DirectedGraph<N> g;
    
  /**
   *  @param g a graph for which we want to compute the strongly
   *           connected components. 
   *  @see DirectedGraph
   */
  public StronglyConnectedComponentsFast(DirectedGraph<N> g)
  {
    this.g = g;
    s = new Stack<N>();
    List<N> heads = g.getHeads();

    indexForNode = new HashMap<N, Integer>();
    lowlinkForNode = new HashMap<N, Integer>();

    for(Iterator<N> headsIt = heads.iterator(); headsIt.hasNext(); ) {
      N head = headsIt.next();
      if(!indexForNode.containsKey(head)) {
        recurse(head);
      }
    }

    //free memory
    indexForNode = null;
    lowlinkForNode = null;
    s = null;
    g = null;
  }

  protected void recurse(N v) {
    indexForNode.put(v, index);
    lowlinkForNode.put(v, index);
    index++;
    s.push(v);
    for(N succ: g.getSuccsOf(v)) {
      if(!indexForNode.containsKey(succ)) {
        recurse(succ);
        lowlinkForNode.put(v, Math.min(lowlinkForNode.get(v), lowlinkForNode.get(succ)));
      } else if(s.contains(succ)) {
        lowlinkForNode.put(v, Math.min(lowlinkForNode.get(v), indexForNode.get(succ)));
      }			
    }
    if(lowlinkForNode.get(v).intValue() == indexForNode.get(v).intValue()) {
      List<N> scc = new ArrayList<N>();
      N v2;
      do {
        v2 = s.pop();
        scc.add(v2);
      }while(v!=v2);			
      componentList.add(scc);
      if(scc.size()>1) {
        trueComponentList.add(scc);
      } else {
        N n = scc.get(0);
        if(g.getSuccsOf(n).contains(n))
          trueComponentList.add(scc);
      }
    }
  }

  /**
   *   @return the list of the strongly-connected components
   */
  public List<List<N>> getComponents()
  {
    return componentList;
  }

  /**
   *   @return the list of the strongly-connected components, but only those
   *   that are true components, i.e. components which have more than one element
   *   or consists of one node that has itself as a successor 
   */
  public List<List<N>> getTrueComponents()
  {
    return trueComponentList;
  }
}
