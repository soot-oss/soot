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
import java.util.Set;

import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.tagkit.Tag;
import soot.util.Chain;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphConstants;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

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

public class PegToDotFile {

  /*
   * make all control fields public, allow other soot class dump the graph in the middle
   */

  public final static int UNITGRAPH = 0;
  public final static int BLOCKGRAPH = 1;
  public final static int ARRAYBLOCK = 2;

  public static int graphtype = UNITGRAPH;

  public static boolean isBrief = false;
  private static final Map<Object, String> listNodeName = new HashMap<Object, String>();
  private static final Map<Object, String> startNodeToName = new HashMap<Object, String>();

  /* in one page or several pages of 8.5x11 */
  public static boolean onepage = true;

  public PegToDotFile(PegGraph graph, boolean onepage, String name) {
    PegToDotFile.onepage = onepage;
    toDotFile(name, graph, "PEG graph");
  }

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
  public static void toDotFile(String methodname, PegGraph graph, String graphname) {
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

        }
      }
    }

    nodesIt = graph.mainIterator();
    while (nodesIt.hasNext()) {
      Object node = nodesIt.next();
      String nodeName = null;
      if (node instanceof List) {

        nodeName = makeNodeName(getNodeOrder(nodeindex, listNodeName.get(node)));
      } else {

        Tag tag = (Tag) ((JPegStmt) node).getTags().get(0);
        nodeName = makeNodeName(getNodeOrder(nodeindex, tag + " " + node));
        if (((JPegStmt) node).getName().equals("start")) {
          startNodeToName.put(node, nodeName);
        }
      }
      Iterator succsIt = graph.getSuccsOf(node).iterator();
      // Iterator succsIt = graph.getPredsOf(node).iterator();
      while (succsIt.hasNext()) {
        Object s = succsIt.next();

        String succName = null;
        if (s instanceof List) {
          succName = makeNodeName(getNodeOrder(nodeindex, listNodeName.get(s)));
        } else {
          JPegStmt succ = (JPegStmt) s;
          Tag succTag = (Tag) succ.getTags().get(0);
          succName = makeNodeName(getNodeOrder(nodeindex, succTag + " " + succ));
        }

        canvas.drawEdge(nodeName, succName);
        // System.out.println("main: nodeName: "+nodeName);
        // System.out.println("main: succName: "+succName);
      }

    }
    System.out.println("Drew main chain");

    // graph for thread

    System.out.println("while printing, startToThread has size " + graph.getStartToThread().size());
    Set maps = graph.getStartToThread().entrySet();
    System.out.println("maps has size " + maps.size());
    for (Iterator iter = maps.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      Object startNode = entry.getKey();
      System.out.println("startNode is: " + startNode);
      String startNodeName = startNodeToName.get(startNode);
      System.out.println("startNodeName is: " + startNodeName);

      List runMethodChainList = (List) entry.getValue();
      Iterator it = runMethodChainList.iterator();
      while (it.hasNext()) {
        Chain chain = (Chain) it.next();
        Iterator subNodesIt = chain.iterator();
        boolean firstNode = false;
        while (subNodesIt.hasNext()) {
          Object node = subNodesIt.next();
          // JPegStmt node = (JPegStmt)subNodesIt.next();
          // System.out.println(node);

          String nodeName = null;
          if (node instanceof List) {

            nodeName = makeNodeName(getNodeOrder(nodeindex, listNodeName.get(node)));
            System.out.println("Didn't draw list node");
            // need to draw these nodes!!!
          } else {

            if (((JPegStmt) node).getName().equals("begin")) {
              firstNode = true;
            }
            Tag tag = (Tag) ((JPegStmt) node).getTags().get(0);
            nodeName = makeNodeName(getNodeOrder(nodeindex, tag + " " + node));
            if (((JPegStmt) node).getName().equals("start")) {
              startNodeToName.put(node, nodeName);
            }
            // draw start edge

            if (firstNode) {
              if (startNodeName == null) {
                System.out.println("00000000startNodeName is null ");
              }
              if (nodeName == null) {
                System.out.println("00000000nodeName is null ");
              }
              // DotGraphEdge startThreadEdge = canvas.drawEdge(startNodeName, threadNodeName);
              DotGraphEdge startThreadEdge = canvas.drawEdge(startNodeName, nodeName);
              startThreadEdge.setStyle("dotted");
              firstNode = false;
            }
          }

          Iterator succsIt = graph.getSuccsOf(node).iterator();
          // Iterator succsIt = graph.getPredsOf(node).iterator();
          while (succsIt.hasNext()) {
            Object succ = succsIt.next();

            String threadNodeName = null;
            if (succ instanceof List) {
              threadNodeName = makeNodeName(getNodeOrder(nodeindex, listNodeName.get(succ)));
            } else {
              JPegStmt succStmt = (JPegStmt) succ;
              Tag succTag = (Tag) succStmt.getTags().get(0);
              threadNodeName = makeNodeName(getNodeOrder(nodeindex, succTag + " " + succStmt));

            }

            // canvas.drawEdge(threadNodeName,
            // makeNodeName(getNodeOrder(nodeindex, succTag+" "+succ)));

            canvas.drawEdge(nodeName, threadNodeName);
            // System.out.println(" nodeName: "+nodeName);
            // System.out.println(" threadNdoeName: "+threadNodeName);
            // canvas.drawEdge(threadNodeName,
            // makeNodeName(getNodeOrder(nodeindex,succ)));

          }

        }
      }

    }

    // set node label
    if (!isBrief) {
      nodesIt = nodeindex.keySet().iterator();
      while (nodesIt.hasNext()) {
        Object node = nodesIt.next();
        String nodename = makeNodeName(getNodeOrder(nodeindex, node));
        DotGraphNode dotnode = canvas.getNode(nodename);
        dotnode.setLabel(node.toString());
      }
    }
    canvas.plot("peg.dot");

    // clean up
    listNodeName.clear();
    startNodeToName.clear();
  }

  private static int getNodeOrder(Hashtable<Object, Integer> nodeindex, Object node) {
    if (node == null) {
      System.out.println("----node is null-----");
      return 0;
      // System.exit(1); // RLH
    }
    // System.out.println("node is: "+node);
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
