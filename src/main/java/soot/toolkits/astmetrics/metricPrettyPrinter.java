/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot.toolkits.astmetrics;

import polyglot.visit.PrettyPrinter;
import polyglot.ast.Node;
import polyglot.util.CodeWriter;

/**
 * @author Michael Batchelder 
 * 
 * Created on 12-Apr-2006 
 */
public class metricPrettyPrinter extends PrettyPrinter {

  ASTMetric astMetric;
  /**
   * 
   */
  public metricPrettyPrinter(ASTMetric astMetric) {
    this.astMetric = astMetric;
  }
  
  public void print(Node parent, Node child, CodeWriter w) {
    astMetric.printAstMetric(child,w);
    super.print(parent,child,w);
  }
}
