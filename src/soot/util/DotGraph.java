/**
 * DotPainter provide an interface to soot for generating DOT language
 * for graphviz from ATT research lab.
 *
 * Intended ussage: virtualize CFG, graphes, etc...
 * @author Feng Qian
 */

package soot.util;

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
   * creates a new graph for drawing.
   * @param graphname, the file name with dot format will be generated
   */
  public DotGraph(String graphname) {
    this.graphname   = graphname;
    this.isSubGraph = false;
    this.nodes      = new HashMap(100);
    this.drawElements = new LinkedList();
    this.attributes   = new LinkedList();
  }
  
  /**
   * generates the drawing on canvas to the dot file.
   */
  public void plot() {
    try {
      BufferedOutputStream out = 
	new BufferedOutputStream(new FileOutputStream(this.graphname+".dot"));
							  
      render(out, 0);
      out.close();

      System.out.println("Generate dot file in "+this.graphname+".dot");

    } catch (IOException ioe) {
    }
  }

  /**
   * draws a directed edge
   */
  public DotGraphEdge drawEdge(String from, String to) {

    DotGraphNode src = (DotGraphNode)nodes.get(from);
    if (src == null) {
      src = new DotGraphNode(from);
      nodes.put(from, src);
    } 

    DotGraphNode dst = (DotGraphNode)nodes.get(to);
    if (dst == null) {
      dst = new DotGraphNode(to);
      nodes.put(to, dst);
    }

    DotGraphEdge edge = new DotGraphEdge(src, dst);
    
    this.drawElements.add(edge);
    
    return edge;
  }

  public DotGraphNode getNode(String name){
    DotGraphNode node = (DotGraphNode)nodes.get(name);
    if (node == null) {
      node = new DotGraphNode(name);
      nodes.put(name, node);
    }
    return node;
  }

  /**
   * sets all node shapes, see the list of node shapes in DotGraphConstants.
   */
  public void setNodeShape(String shape){
    StringBuffer command = new StringBuffer("node [shape=");
    command.append(shape);
    command.append("];");
    this.drawElements.add(new DotGraphCommand(new String(command)));
  }

  /**
   * sets all node styles
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
   */
  public void setGraphAttribute(String id, String value){
    this.attributes.add(new DotGraphAttribute(id, value));    
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
