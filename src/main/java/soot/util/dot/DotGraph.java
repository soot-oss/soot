package soot.util.dot;

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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DotGraph implements Renderable {
  private static final Logger logger = LoggerFactory.getLogger(DotGraph.class);

  /*
   * allow a serialized drawing, following steps: 1. new DotGraph 2. draw(Directed)Edge / drawUndirectedEdge
   * attachAttributes, addNode 3. plot
   */
  private String graphname;
  private boolean isSubGraph;

  private HashMap<String, DotGraphNode> nodes;
  /* draw elements are sub graphs, edges, commands */
  private List<Renderable> drawElements;

  private List<DotGraphAttribute> attributes;

  /**
   * The extension added to output files, exported so that clients can search for the filenames.
   */
  public final static String DOT_EXTENSION = ".dot";

  /**
   * Creates a new graph for drawing.
   *
   * @param graphname,
   *          the name used to identify the graph in the dot source.
   */
  public DotGraph(String graphname) {
    this.graphname = graphname;
    this.isSubGraph = false;
    this.nodes = new HashMap<String, DotGraphNode>(100);
    this.drawElements = new LinkedList<Renderable>();
    this.attributes = new LinkedList<DotGraphAttribute>();
  }

  /**
   * Generates the drawing on canvas to the dot file.
   *
   * @param filename
   *          the name for the output file. By convention, it should end with DOT_EXTENSION, but this is not enforced.
   */
  public void plot(String filename) {
    try {
      BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename));

      render(out, 0);
      out.close();
    } catch (IOException ioe) {
      logger.debug("" + ioe.getMessage());
    }
  }

  /**
   * Draws a directed edge (including the source and end nodes, if they have not already been drawn).
   *
   * @param from,
   *          the source node
   * @param to,
   *          the end node
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
   *
   * @param name,
   *          the node to draw.
   * @return the {@link DotGraphNode} corresponding to the specified name.
   */
  public DotGraphNode drawNode(String name) {
    DotGraphNode node = getNode(name);

    if (node == null) {
      throw new RuntimeException("Assertion failed.");
    }

    if (!this.drawElements.contains(node)) {
      this.drawElements.add(node);
    }

    return node;
  }

  /**
   * Gets the graph node by name.
   *
   * @param name,
   *          unique name of the node.
   * @return the node with the specified name, adding a new node to the graph if there is no such node.
   */
  public DotGraphNode getNode(String name) {
    if (name == null) {
      return null;
    }

    DotGraphNode node = nodes.get(name);
    if (node == null) {
      node = new DotGraphNode(name);
      nodes.put(name, node);
    }
    return node;
  }

  /**
   * Sets all node shapes, see the list of node shapes in DotGraphConstants.
   *
   * @param shape,
   *          the node shape
   */
  public void setNodeShape(String shape) {
    StringBuffer command = new StringBuffer("node [shape=");
    command.append(shape);
    command.append("];");
    this.drawElements.add(new DotGraphCommand(new String(command)));
  }

  /**
   * Sets all node styles
   *
   * @param style,
   *          the node style
   */
  public void setNodeStyle(String style) {
    StringBuffer command = new StringBuffer("node [style=");
    command.append(style);
    command.append("];");
    this.drawElements.add(new DotGraphCommand(new String(command)));
  }

  /**
   * sets the size of drawing area, in inches
   */
  public void setGraphSize(double width, double height) {
    String size = "\"" + width + "," + height + "\"";
    this.setGraphAttribute("size", size);
  }

  /**
   * sets the pages size, once this is set, the generated graph will be broken into several pages.
   */
  public void setPageSize(double width, double height) {
    String size = "\"" + width + ", " + height + "\"";
    this.setGraphAttribute("page", size);
  }

  /**
   * sets the graph rotation angles
   */
  public void setOrientation(String orientation) {
    this.setGraphAttribute("orientation", orientation);
  }

  /**
   * sets the graph name
   */
  public void setGraphName(String name) {
    this.graphname = name;
  }

  /**
   * sets the graph label
   */
  public void setGraphLabel(String label) {
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setGraphAttribute("label", "\"" + label + "\"");
  }

  /**
   * sets any general attributes
   *
   * @param id
   *          is the attribute name.
   * @param value
   *          is the attribute value.
   */
  public void setGraphAttribute(String id, String value) {
    this.setGraphAttribute(new DotGraphAttribute(id, value));
  }

  /**
   * sets any general attributes
   *
   * @param attr
   *          a {@link DotGraphAttribute} specifying the attribute name and value.
   */
  public void setGraphAttribute(DotGraphAttribute attr) {
    this.attributes.add(attr);
  }

  /**
   * draws an undirected edge
   *
   * @param label1,
   *          label2
   */
  public void drawUndirectedEdge(String label1, String label2) {
  }

  /**
   * creates a sub graph.
   *
   * @return the newly created sub graph.
   */
  public DotGraph createSubGraph(String label) {
    // file name is used as label of sub graph.
    DotGraph subgraph = new DotGraph(label);
    subgraph.isSubGraph = true;

    this.drawElements.add(subgraph);

    return subgraph;
  }

  /* implements renderable interface. */
  public void render(OutputStream out, int indent) throws IOException {
    // header
    String graphname = this.graphname;

    if (!isSubGraph) {
      DotGraphUtility.renderLine(out, "digraph \"" + graphname + "\" {", indent);
    } else {
      DotGraphUtility.renderLine(out, "subgraph \"" + graphname + "\" {", indent);
    }

    /* render graph attributes */
    Iterator<DotGraphAttribute> attrIt = this.attributes.iterator();
    while (attrIt.hasNext()) {
      DotGraphAttribute attr = attrIt.next();
      DotGraphUtility.renderLine(out, attr.toString() + ";", indent + 4);
    }

    /* render elements */
    Iterator<Renderable> elmntsIt = this.drawElements.iterator();
    while (elmntsIt.hasNext()) {
      Renderable element = elmntsIt.next();
      element.render(out, indent + 4);
    }

    // close the description
    DotGraphUtility.renderLine(out, "}", indent);
  }
}
