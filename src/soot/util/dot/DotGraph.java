/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Sable Research Group
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


/**
 * DotGraph provides an interface to SOOT for generating DOT language
 * for graphviz from ATT research lab.
 *
 * Intended usage: virtualize CFG, graphes, etc...
 *
 * @author Feng Qian
 */

package soot.util.dot;
import soot.*;

import java.io.*;
import java.util.*;

public class DotGraph implements Renderable{
  
  /* allow a serialized drawing, following steps:
   * 1. new DotGraph
   * 2. draw(Directed)Edge / drawUndirectedEdge
   *    attachAttributes, addNode
   * 3. plot
   */
  private String  graphname;
  private boolean isSubGraph;

  private HashMap nodes;
  /* draw elements are sub graphs, edges, commands */
  private List    drawElements;
 
  private List    attributes;

  /**
   * The extension added to output files, exported so that
   * clients can search for the filenames.
   */
  public final static String DOT_EXTENSION = ".dot";

  /**
   * Creates a new graph for drawing.
   * @param graphname, the name used to identify the graph in the dot source.
   */
  public DotGraph(String graphname) {
    this.graphname   = graphname;
    this.isSubGraph = false;
    this.nodes      = new HashMap(100);
    this.drawElements = new LinkedList();
    this.attributes   = new LinkedList();
  }
  
  /**
   * Generates the drawing on canvas to the dot file.
   * @param filename the name for the output file.  By convention, it should
   * end with DOT_EXTENSION, but this is not enforced.
   */
  public void plot(String filename) {
    try {
      BufferedOutputStream out = 
	new BufferedOutputStream(new FileOutputStream(filename));
							  
      render(out, 0);
      out.close();
    } catch (IOException ioe) {
    }
  }

  /**
   * Draws a directed edge (including the source and end nodes,
   * if they have not already been drawn).
   * @param from, the source node
   * @param to, the end node
   * @return a graph edge
   */
  public DotGraphEdge drawEdge(String from, String to) {

    DotGraphNode src = drawNode(from);
    DotGraphNode dst = drawNode(to);
    DotGraphEdge edge = new DotGraphEdge(src, dst);
    
    this.drawElements.add(edge);
    
    return edge;
  }

  /**
   * Draws a node.
   * @param name, the node to draw.
   * @return the {@link DotGraphNode} corresponding to the 
   * specified name.
   */
  public DotGraphNode drawNode(String name){
      DotGraphNode node = getNode(name);

      if(node == null)
          throw new RuntimeException("Assertion failed.");

      if(!this.drawElements.contains(node))
          this.drawElements.add(node);

      return node;
  }

  /**
   * Gets the graph node by name.
   * @param name, unique name of the node.
   * @return the node with the specified name, or <code>null</code>
   * if there is no such node.
   */
  public DotGraphNode getNode(String name){
      DotGraphNode node = (DotGraphNode)nodes.get(name);
      if (node == null) {
          node = new DotGraphNode(name);
          nodes.put(name, node);
      }
      return node;
  }

  /**
   * Sets all node shapes, see the list of node shapes in DotGraphConstants.
   * @param shape, the node shape
   */
  public void setNodeShape(String shape){
    StringBuffer command = new StringBuffer("node [shape=");
    command.append(shape);
    command.append("];");
    this.drawElements.add(new DotGraphCommand(new String(command)));
  }

  /**
   * Sets all node styles
   * @param style, the node style
   */
  public void setNodeStyle(String style){
    StringBuffer command = new StringBuffer("node [style=");
    command.append(style);
    command.append("];");
    this.drawElements.add(new DotGraphCommand(new String(command)));
  }

  /**
   * sets the size of drawing area, in inches
   */
  public void setGraphSize(double width, double height){
    String size = "\""+width+","+height+"\"";
    this.setGraphAttribute("size", size);
  }

  /**
   * sets the pages size, once this is set, the generated graph
   * will be broken into several pages.
   */
  public void setPageSize(double width, double height){
    String size = "\""+width+", "+height+"\"";
    this.setGraphAttribute("page", size);
  }

  /**
   * sets the graph rotation angles
   */
  public void setOrientation(String orientation){
    this.setGraphAttribute("orientation", orientation);
  }

  /**
   * sets the graph label
   */
  public void setGraphLabel(String label){
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setGraphAttribute("label", "\""+label+"\"");
  }

  /**
   * sets any general attributes
   * @param id is the attribute name.
   * @param value is the attribute value.
   */
  public void setGraphAttribute(String id, String value){
    this.setGraphAttribute(new DotGraphAttribute(id, value));    
  }

  /**
   * sets any general attributes
   * @param attr a {@link DotGraphAttribute} specifying the
   * attribute name and value.
   */
  public void setGraphAttribute(DotGraphAttribute attr){
    this.attributes.add(attr);    
  }

  /**
   * draws an undirected edge
   * @param label1, label2
   */
  public void drawUndirectedEdge(String label1, String label2) {
  }

  /**
   * creates a sub graph.
   * @return the newly created sub graph.
   */
  public DotGraph createSubGraph(String label){
    // file name is used as label of sub graph.
    DotGraph subgraph = new DotGraph(label);
    subgraph.isSubGraph = true;

    this.drawElements.add(subgraph);

    return subgraph;
  }

  /* implements renderable interface. */
  public void render(OutputStream out, int indent) throws IOException{
    // header
    String graphname = this.graphname;

    if (!isSubGraph) {
      DotGraphUtility.renderLine(out, "digraph \""+graphname+"\" {", indent);
    } else {
      DotGraphUtility.renderLine(out, "subgraph \""+graphname+"\" {", indent);
    }

    /* render graph attributes */
    Iterator attrIt = this.attributes.iterator();
    while (attrIt.hasNext()) {
      DotGraphAttribute attr = (DotGraphAttribute)attrIt.next();
      DotGraphUtility.renderLine(out, attr.toString()+";", indent+4);
    }

    /* render elements */
    Iterator elmntsIt = this.drawElements.iterator();
    while (elmntsIt.hasNext()) {
      Renderable element = (Renderable)elmntsIt.next();
      element.render(out, indent+4);
    }

    // close the description
    DotGraphUtility.renderLine(out, "}", indent);
  }
}
