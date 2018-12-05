package soot.toolkits.astmetrics;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Nomair A. Naeem
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

import java.util.ArrayList;
import java.util.Iterator;

import polyglot.ast.Node;

import soot.options.Options;

/*
 * Add all metrics to be computed here.
 */
public class ComputeASTMetrics {

  ArrayList<ASTMetric> metrics;

  /*
   * New metrics should be added into the metrics linked list
   */
  public ComputeASTMetrics(Node astNode) {
    metrics = new ArrayList<ASTMetric>();
    // add new metrics below this line
    // REMEMBER ALL METRICS NEED TO implement MetricInterface

    // abrupt edges metric calculator
    metrics.add(new AbruptEdgesMetric(astNode));
    metrics.add(new NumLocalsMetric(astNode));
    metrics.add(new ConstructNumbersMetric(astNode));
    metrics.add(new StmtSumWeightedByDepth(astNode));
    metrics.add(new ConditionComplexityMetric(astNode));
    metrics.add(new ExpressionComplexityMetric(astNode));
    metrics.add(new IdentifiersMetric(astNode));
  }

  public void apply() {
    if (!Options.v().ast_metrics()) {
      return;
    }

    Iterator<ASTMetric> metricIt = metrics.iterator();
    while (metricIt.hasNext()) {
      metricIt.next().execute();
    }

  }
}
