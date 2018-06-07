package soot.jimple.toolkits.thread.mhp;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Sable Research Group
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.toolkits.graph.DirectedGraph;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphConstants;
import soot.util.dot.DotGraphNode;

//import soot.toolkits.mhp.*;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class PegCallGraphToDot {

  /*
   * make all control fields public, allow other soot class dump the graph in the middle
   */

  public static boolean isBrief = false;
  private static final Map<Object, String> listNodeName = new HashMap<Object, String>();

  /* in one page or several pages of 8.5x11 */
  public static boolean onepage = true;

  public PegCallGraphToDot(DirectedGraph graph, boolean onepage, String name) {

    PegCallGraphToDot.onepage = onepage;
    toDotFile(name, graph, "PegCallGraph");

  }

  /*
   * public PegToDotFile(PegGraph graph, boolean onepage, String name) { this.onepage = onepage; toDotFile(name,
   * graph,"Simple graph"); }
   */

  private static int nodecount = 0;

  /**
   * Generates a dot format file for a DirectedGraph
   *
   * @param methodname,
   *          the name of generated dot file
   * @param graph,
   *          a directed control flow graph (UnitGraph, BlockGraph ...)
   * @param graphname,
   *          the title of the graph
   */
  public static void toDotFile(String methodname, DirectedGraph graph, String graphname) {
    int sequence = 0;
    // this makes the node name unique
    nodecount = 0; // reset node counter first.
    Hashtable nodeindex = new Hashtable(graph.size());

    // file name is the method name + .dot
    DotGraph canvas = new DotGraph(methodname);
    // System.out.println("onepage is:"+onepage);
    if (!onepage) {
      canvas.setPageSize(8.5, 11.0);
    }

    canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_BOX);
    canvas.setGraphLabel(graphname);

    Iterator nodesIt = graph.iterator();

    {
      while (nodesIt.hasNext()) {
        Object node = nodesIt.next();

        if (node instanceof List) {
          String listName = "list" + (new Integer(sequence++)).toString();
          String nodeName = makeNodeName(getNodeOrder(nodeindex, listName));
          listNodeName.put(node, listName);
          // System.out.println("put node: "+node +"into listNodeName");

        }
      }
    }

    nodesIt = graph.iterator();
    while (nodesIt.hasNext()) {
      Object node = nodesIt.next();
      String nodeName = null;
      if (node instanceof List) {

        nodeName = makeNodeName(getNodeOrder(nodeindex, listNodeName.get(node)));
      } else {

        nodeName = makeNodeName(getNodeOrder(nodeindex, node));
      }
      Iterator succsIt = graph.getSuccsOf(node).iterator();

      while (succsIt.hasNext()) {
        Object s = succsIt.next();
        String succName = null;
        if (s instanceof List) {
          succName = makeNodeName(getNodeOrder(nodeindex, listNodeName.get(s)));
        } else {
          Object succ = s;
          // System.out.println("$$$$$$succ: "+succ);
          // nodeName = makeNodeName(getNodeOrder(nodeindex, tag+" "+node));
          succName = makeNodeName(getNodeOrder(nodeindex, succ));
          // System.out.println("node is :" +node);
          // System.out.println("find start node in pegtodotfile:"+node);
        }

        canvas.drawEdge(nodeName, succName);
      }

    }

    // set node label
    if (!isBrief) {
      nodesIt = nodeindex.keySet().iterator();
      while (nodesIt.hasNext()) {
        Object node = nodesIt.next();
        // System.out.println("node is:"+node);
        if (node != null) {
          // System.out.println("node: "+node);
          String nodename = makeNodeName(getNodeOrder(nodeindex, node));
          // System.out.println("nodename: "+ nodename);
          DotGraphNode dotnode = canvas.getNode(nodename);
          // System.out.println("dotnode: "+dotnode);
          if (dotnode != null) {
            dotnode.setLabel(node.toString());
          }
        }
      }
    }

    canvas.plot("pecg.dot");

    // clean up
    listNodeName.clear();
  }

  private static int getNodeOrder(Hashtable<Object, Integer> nodeindex, Object node) {

    Integer index = nodeindex.get(node);
    if (index == null) {
      index = new Integer(nodecount++);
      nodeindex.put(node, index);
    }
    // System.out.println("order is:"+index.intValue());
    return index.intValue();
  }

  private static String makeNodeName(int index) {
    return "N" + index;
  }
}
