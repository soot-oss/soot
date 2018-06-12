package soot.toolkits.graph.pdg;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 - 2010 Hossein Sadat-Mohtasham
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

/**
 * 
 * This represents a loop in the PDG.
 *
 */
public class LoopedPDGNode extends PDGNode {

  protected PDGNode m_header = null;
  protected PDGNode m_body = null;

  public LoopedPDGNode(Region obj, Type t, PDGNode c) {
    super(obj, t);
    this.m_header = c;
  }

  public PDGNode getHeader() {
    return this.m_header;
  }

  public void setBody(PDGNode b) {
    this.m_body = b;
  }

  public PDGNode getBody() {
    return this.m_body;
  }

}
