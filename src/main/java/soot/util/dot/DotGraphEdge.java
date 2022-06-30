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
import java.util.Collection;

/**
 * Graph edges are the major elements of a graph
 * 
 * @author Feng Qian
 */
public class DotGraphEdge extends AbstractDotGraphElement implements Renderable {

  private final DotGraphNode start;
  private final DotGraphNode end;
  private final boolean isDirected;

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
   * Sets the edge style.
   * 
   * @param style,
   *          a style of edge
   * @see DotGraphConstants
   */
  public void setStyle(String style) {
    this.setAttribute("style", style);
  }

  @Override
  public void render(OutputStream out, int indent) throws IOException {
    StringBuilder line = new StringBuilder();
    line.append(this.start.getName());
    line.append(this.isDirected ? "->" : "--");
    line.append(this.end.getName());

    Collection<DotGraphAttribute> attrs = this.getAttributes();
    if (!attrs.isEmpty()) {
      line.append(" [");
      for (DotGraphAttribute attr : attrs) {
        line.append(attr.toString()).append(',');
      }
      line.append(']');
    }
    line.append(';');

    DotGraphUtility.renderLine(out, line.toString(), indent);
  }
}
