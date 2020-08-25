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

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Graph edges are the major elements of a graph
 * 
 * @author Feng Qian
 */
public class DotGraphEdge implements Renderable {

  private final DotGraphNode start;
  private final DotGraphNode end;
  private final boolean isDirected;
  private List<DotGraphAttribute> attributes;

  /**
   * Draws a directed edge.
   * 
   * @param src,
   *          the source node
   * @param dst,
   *          the end node
   */
  public DotGraphEdge(DotGraphNode src, DotGraphNode dst) {
    this.start = src;
    this.end = dst;
    this.isDirected = true;
  }

  /**
   * Draws a graph edge by specifying directed or undirected.
   * 
   * @param src,
   *          the source node
   * @param dst,
   *          the end node
   * @param directed,
   *          the edge is directed or not
   */
  public DotGraphEdge(DotGraphNode src, DotGraphNode dst, boolean directed) {
    this.start = src;
    this.end = dst;
    this.isDirected = directed;
  }

  /**
   * Sets the label for the edge.
   * 
   * @param label,
   *          a label string
   */
  public void setLabel(String label) {
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", "\"" + label + "\"");
  }

  /**
   * Sets the edge style.
   * 
   * @param style,
   *          a style of edge
   * @see DotGraphConstants
   */
  public void setStyle(String style) {
    this.setAttribute("style", style);
  }

  /**
   * Sets an edge attribute.
   * 
   * @param id,
   *          the attribute id to be set
   * @param value,
   *          the attribute value
   */
  public void setAttribute(String id, String value) {
    this.setAttribute(new DotGraphAttribute(id, value));
  }

  /**
   * Sets an edge attribute.
   * 
   * @param attr,
   *          a {@link DotGraphAttribute} specifying the attribute name and value.
   */
  public void setAttribute(DotGraphAttribute attr) {
    List<DotGraphAttribute> attrs = this.attributes;
    if (attrs == null) {
      this.attributes = attrs = new LinkedList<DotGraphAttribute>();
    }
    attrs.add(attr);
  }

  @Override
  public void render(OutputStream out, int indent) throws IOException {
    StringBuilder line = new StringBuilder();
    line.append(this.start.getName());
    line.append(this.isDirected ? "->" : "--");
    line.append(this.end.getName());

    if (this.attributes != null) {
      line.append(" [");
      for (DotGraphAttribute attr : this.attributes) {
        line.append(attr.toString()).append(',');
      }
      line.append(']');
    }
    line.append(';');

    DotGraphUtility.renderLine(out, line.toString(), indent);
  }
}
