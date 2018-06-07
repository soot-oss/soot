package soot.toolkits.astmetrics;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import polyglot.ast.Node;
import polyglot.util.CodeWriter;
import polyglot.visit.PrettyPrinter;

/**
 * @author Michael Batchelder
 * 
 *         Created on 12-Apr-2006
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
    astMetric.printAstMetric(child, w);
    super.print(parent, child, w);
  }
}
