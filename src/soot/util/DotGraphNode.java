package soot.util;

import java.io.*;
import java.util.*;

/* A graph node may has attributes to be set */  
public class DotGraphNode implements Renderable{
  private String name;
  private List attributes;

  public DotGraphNode(String name) {
    this.name = "\""+DotGraphUtility.replaceQuotes(name)+"\"";
  }

  // make any illegal name to be legal
  public String getName(){
    return this.name;
  }

  public void setLabel(String label) {
    label = DotGraphUtility.replaceQuotes(label);
    label = DotGraphUtility.replaceReturns(label);
    this.setAttribute("label", "\""+label+"\"");
  }

  public void setShape(String shape) {
    this.setAttribute("shape", shape);
  }

  public void setStyle(String style) {
    this.setAttribute("style", style);
  }

  public void setAttribute(String id, String value) {
    if (this.attributes == null) {
      this.attributes = new LinkedList();
    }
    
    this.attributes.add(new DotGraphAttribute(id, value));    
  }

  /* only when node's attributes are not empty, render the node */
  public void render(OutputStream out, int indent) throws IOException {
    if (this.attributes == null) {
      return;
    }

    StringBuffer line = new StringBuffer(this.getName());

    line.append(" [");
    Iterator attrIt = this.attributes.iterator();
    while (attrIt.hasNext()) {
      DotGraphAttribute attr = (DotGraphAttribute)attrIt.next();
      line.append(attr.toString());
      line.append(",");
    }
    line.append("];");

    DotGraphUtility.renderLine(out, new String(line), indent);
  }
}
