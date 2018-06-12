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
 * A Dot graph node with various attributes.
 */
public class DotGraphNode implements Renderable {
  private String name;
  private List<DotGraphAttribute> attributes;

  public DotGraphNode(String name) {
    this.name = "\"" + DotGraphUtility.replaceQuotes(name) + "\"";
  }

  // make any illegal name to be legal
  public String getName() {
    return this.name;
  }

  public void setLabel(String label) {
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", "\"" + label + "\"");
  }

  public void setHTMLLabel(String label) {
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", label);
  }

  public void setShape(String shape) {
    this.setAttribute("shape", shape);
  }

  public void setStyle(String style) {
    this.setAttribute("style", style);
  }

  public void setAttribute(String id, String value) {
    if (this.attributes == null) {
      this.attributes = new LinkedList<DotGraphAttribute>();
    }

    this.setAttribute(new DotGraphAttribute(id, value));
  }

  public void setAttribute(DotGraphAttribute attr) {
    if (this.attributes == null) {
      this.attributes = new LinkedList<DotGraphAttribute>();
    }

    this.attributes.add(attr);
  }

  public void render(OutputStream out, int indent) throws IOException {
    StringBuffer line = new StringBuffer(this.getName());
    if (this.attributes != null) {
      line.append(" [");
      for (DotGraphAttribute attr : this.attributes) {
        line.append(attr.toString());
        line.append(",");
      }
      line.append("];");
    }
    DotGraphUtility.renderLine(out, new String(line), indent);
  }
}
