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


/* @author Feng Qian */

package soot.util.dot;

import java.io.*;
import java.util.*;

/**
 * A Dot graph node with various attributes.
 */  
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

  public void setHTMLLabel(String label){
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
      this.attributes = new LinkedList();
    }
    
    this.setAttribute(new DotGraphAttribute(id, value));    
  }

  public void setAttribute(DotGraphAttribute attr) {
    if (this.attributes == null) {
      this.attributes = new LinkedList();
    }
    
    this.attributes.add(attr);    
  }

  public void render(OutputStream out, int indent) throws IOException {
    StringBuffer line = new StringBuffer(this.getName());
    if (this.attributes != null) {
      line.append(" [");
      for (Iterator attrIt = this.attributes.iterator(); attrIt.hasNext(); ) {
	DotGraphAttribute attr = (DotGraphAttribute)attrIt.next();
	line.append(attr.toString());
	line.append(",");
      }
      line.append("];");
    }
    DotGraphUtility.renderLine(out, new String(line), indent);
  }
}
