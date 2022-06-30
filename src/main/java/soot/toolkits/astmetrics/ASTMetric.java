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

import java.util.Iterator;

import polyglot.ast.ClassDecl;
import polyglot.ast.Node;
import polyglot.util.CodeWriter;
import polyglot.visit.NodeVisitor;

import soot.G;

public abstract class ASTMetric extends NodeVisitor implements MetricInterface {
  polyglot.ast.Node astNode;
  String className = null; // name of Class being currently processed

  public ASTMetric(polyglot.ast.Node astNode) {
    this.astNode = astNode;
    reset();
  }

  /*
   * Taking care of the change in classes within a polyglot ast
   */
  public final NodeVisitor enter(Node n) {
    if (n instanceof ClassDecl) {
      className = ((ClassDecl) n).name();
      System.out.println("Starting processing: " + className);
    }
    return this;
  }

  /*
   * When we leave a classDecl all the metrics for this classDecl must be stored and the metrics reset
   *
   * This is done by invoking the addMetrics abstract method
   */

  public final Node leave(Node parent, Node old, Node n, NodeVisitor v) {
    if (n instanceof ClassDecl) {
      if (className == null) {
        throw new RuntimeException("className is null");
      }

      System.out.println("Done with class " + className);

      // get the classData object for this class
      ClassData data = getClassData();
      addMetrics(data);
      reset();
    }
    return leave(old, n, v);
  }

  public abstract void reset();

  public abstract void addMetrics(ClassData data);

  /*
   * Should be used to execute the traversal which will find the metric being calculated
   */
  public final void execute() {
    astNode.visit(this);
    // Testing testing testing
    System.out.println("\n\n\n PRETTY P{RINTING");
    if (this instanceof StmtSumWeightedByDepth) {
      metricPrettyPrinter p = new metricPrettyPrinter(this);
      p.printAst(astNode, new CodeWriter(System.out, 80));
    }
  }

  public void printAstMetric(Node n, CodeWriter w) {

  }

  /*
   * Returns the classData object if one if present in the globals Metrics List otherwise creates new adds to globals metric
   * list and returns that
   */
  public final ClassData getClassData() {
    if (className == null) {
      throw new RuntimeException("className is null");
    }

    Iterator<ClassData> it = G.v().ASTMetricsData.iterator();
    while (it.hasNext()) {
      ClassData tempData = it.next();

      if (tempData.classNameEquals(className)) {
        return tempData;
      }
    }
    ClassData data = new ClassData(className);
    G.v().ASTMetricsData.add(data);
    return data;
  }
}
