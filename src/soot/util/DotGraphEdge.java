
package soot.util;

import java.io.*;
import java.util.*;

/* a graph edge is the major element of the graph */
public class DotGraphEdge implements Renderable {
  private boolean isDirected;
  private DotGraphNode start, end;
  private List attributes;

  public DotGraphEdge(DotGraphNode src, DotGraphNode dst){
    this.start = src;
    this.end   = dst;
    this.isDirected = true;
  }

  public DotGraphEdge(DotGraphNode src, DotGraphNode dst, boolean directed){
    this.start = src;
    this.end   = dst;
    this.isDirected = directed;
  }

  public void setLabel(String label){
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", "\""+label+"\"");
  }

  public void setStyle(String style){
    this.setAttribute("style", style);
  }

  public void setAttribute(String id, String value) {
    if (this.attributes == null) {
      this.attributes = new LinkedList();
    }
    
    this.attributes.add(new DotGraphAttribute(id, value));    
  }

  public void render(OutputStream out, int indent) throws IOException {
    this.start.render(out, indent);
    this.end.render(out, indent);

    StringBuffer line = new StringBuffer(start.getName());
    line.append((this.isDirected)?"->":"--");
    line.append(end.getName());

    if (this.attributes != null) {
      
      line.append(" [");
      Iterator attrIt = this.attributes.iterator();
      while (attrIt.hasNext()) {
	DotGraphAttribute attr = (DotGraphAttribute)attrIt.next();
	line.append(attr.toString());
	line.append(",");
      }
      line.append("]");
    }

    line.append(";");

    DotGraphUtility.renderLine(out, new String(line), indent);
  }
}

