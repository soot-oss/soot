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
 * A Dot graph node with various attributes.
 */
public class DotGraphNode extends AbstractDotGraphElement implements Renderable {

  private final String name;

  public DotGraphNode(String name) {
    // make any illegal name to be legal
    this.name = '"' + DotGraphUtility.replaceQuotes(name) + '"';
  }

  public DotGraphNode(String name, boolean dontQuoteName) {
    this.name = dontQuoteName ? DotGraphUtility.replaceQuotes(name) : '"' + DotGraphUtility.replaceQuotes(name) + '"';
  }

  public String getName() {
    return this.name;
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

  @Override
  public void render(OutputStream out, int indent) throws IOException {
    StringBuilder line = new StringBuilder();
    line.append(this.getName());

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
