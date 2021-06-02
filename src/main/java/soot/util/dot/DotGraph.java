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
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allow a serialized drawing, following steps:
 * <ol>
 * <li>new DotGraph</li>
 * <li>draw(Directed/Undirected)Edge, attachAttributes, addNode</li>
 * <li>plot</li>
 * </ol>
 */
public class DotGraph extends AbstractDotGraphElement implements Renderable {
  private static final Logger logger = LoggerFactory.getLogger(DotGraph.class);

  /**
   * The extension added to output files, exported so that clients can search for the filenames.
   */
  public static final String DOT_EXTENSION = ".dot";

  private final HashMap<String, DotGraphNode> nodes;
  /* draw elements are sub graphs, edges, commands */
  private final List<Renderable> drawElements;

  private final boolean isSubGraph;

  private boolean dontQuoteNodeNames;

  private String graphname;

  private DotGraph(String graphname, boolean isSubGraph) {
    this.graphname = graphname;
    this.isSubGraph = isSubGraph;
    this.nodes = new HashMap<String, DotGraphNode>(100);
    this.drawElements = new LinkedList<Renderable>();
    this.dontQuoteNodeNames = false;
  }

  /**
   * Creates a new graph for drawing.
   *
   * @param graphname,
   *          the name used to identify the graph in the dot source.
   */
  public DotGraph(String graphname) {
    this(graphname, false);
  }

  /**
   * Generates the drawing on canvas to the dot file.
   *
   * @param filename
   *          the name for the output file. By convention, it should end with DOT_EXTENSION, but this is not enforced.
   */
  public void plot(String filename) {
    try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename))) {
      render(out, 0);
    } catch (IOException ioe) {
      logger.debug(ioe.getMessage());
    }
  }

  /**
   * Draws a directed edge (including the source and end nodes, if they have not already been drawn).
   *
   * @param from,
   *          the source node
   * @param to,
   *          the end node
   * @return a directed graph edge connecting {@code from} with {@code to}
   */
  public DotGraphEdge drawEdge(String from, String to) {
    return drawEdge(from, to, true);
  }

  /**
   * Draws a undirected edge (including the nodes, if they have not already been drawn).
   *
   * @param node1
   * @param node2
   *
   * @return an undirected graph edge connecting {@code node1} with {@code node2}
   */
  public DotGraphEdge drawUndirectedEdge(String node1, String node2) {
    return drawEdge(node1, node2, false);
  }

  private DotGraphEdge drawEdge(String from, String to, boolean directed) {
    DotGraphNode src = drawNode(from);
    DotGraphNode dst = drawNode(to);
    DotGraphEdge edge = new DotGraphEdge(src, dst, directed);

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
      node = new DotGraphNode(name, dontQuoteNodeNames);
      nodes.put(name, node);
    }
    return node;
  }

  /**
   * Checks if the graph already contains the node with the specified name.
   *
   * @param name,
   *          unique name of the node.
   *
   * @return true if the node with the specified name is already in the graph, false otherwise
   */
  public boolean containsNode(String name) {
    return name != null && nodes.containsKey(name);
  }

  /**
   * Checks if the graph already contains the given {@link DotGraphNode}.
   *
   * @param node
   *
   * @return true if the node is already in the graph, false otherwise
   */
  public boolean containsNode(DotGraphNode node) {
    return this.drawElements.contains(node);
  }

  /**
   * NOTE: default is true
   *
   * @param value
   */
  public void quoteNodeNames(boolean value) {
    this.dontQuoteNodeNames = !value;
  }

  /**
   * Sets all node shapes, see the list of node shapes in DotGraphConstants.
   *
   * @param shape,
   *          the node shape
   */
  public void setNodeShape(String shape) {
    String command = "node [shape=" + shape + "];";
    this.drawElements.add(new DotGraphCommand(command));
  }

  /**
   * Sets all node styles
   *
   * @param style,
   *          the node style
   */
  public void setNodeStyle(String style) {
    String command = "node [style=" + style + "];";
    this.drawElements.add(new DotGraphCommand(command));
  }

  /**
   * sets the size of drawing area, in inches
   */
  public void setGraphSize(double width, double height) {
    String size = "\"" + width + "," + height + "\"";
    this.setAttribute("size", size);
  }

  /**
   * sets the pages size, once this is set, the generated graph will be broken into several pages.
   */
  public void setPageSize(double width, double height) {
    String size = "\"" + width + ", " + height + "\"";
    this.setAttribute("page", size);
  }

  /**
   * sets the graph rotation angles
   */
  public void setOrientation(String orientation) {
    this.setAttribute("orientation", orientation);
  }

  /**
   * sets the graph name
   */
  public void setGraphName(String name) {
    this.graphname = name;
  }

  /**
   * NOTE: Alias for {@link #setLabel(java.lang.String)}.
   */
  public void setGraphLabel(String label) {
    this.setLabel(label);
  }

  /**
   * sets any general attributes
   * 
   * NOTE: Alias for {@link #setAttribute(java.lang.String, java.lang.String)}.
   *
   * @param id
   *          is the attribute name.
   * @param value
   *          is the attribute value.
   */
  public void setGraphAttribute(String id, String value) {
    this.setAttribute(id, value);
  }

  /**
   * sets any general attributes
   * 
   * NOTE: Alias for {@link #setAttribute(soot.util.dot.DotGraphAttribute)}.
   *
   * @param attr
   *          a {@link DotGraphAttribute} specifying the attribute name and value.
   */
  public void setGraphAttribute(DotGraphAttribute attr) {
    this.setAttribute(attr);
  }

  /**
   * creates a sub graph.
   * 
   * NOTE: Some renderers require subgraph labels to start with "cluster".
   *
   * @return the newly created sub graph.
   */
  public DotGraph createSubGraph(String label) {
    // file name is used as label of sub graph.
    DotGraph subgraph = new DotGraph(label, true);

    this.drawElements.add(subgraph);

    return subgraph;
  }

  /* implements renderable interface. */
  @Override
  public void render(OutputStream out, int indent) throws IOException {
    // header
    if (!isSubGraph) {
      DotGraphUtility.renderLine(out, "digraph \"" + this.graphname + "\" {", indent);
    } else {
      DotGraphUtility.renderLine(out, "subgraph \"" + this.graphname + "\" {", indent);
    }

    /* render graph attributes */
    for (DotGraphAttribute attr : this.getAttributes()) {
      DotGraphUtility.renderLine(out, attr.toString() + ';', indent + 4);
    }

    /* render elements */
    for (Renderable element : this.drawElements) {
      element.render(out, indent + 4);
    }

    // close the description
    DotGraphUtility.renderLine(out, "}", indent);
  }
}
