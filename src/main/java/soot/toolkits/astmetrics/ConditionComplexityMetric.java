package soot.toolkits.astmetrics;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import polyglot.ast.Binary;
import polyglot.ast.Expr;
import polyglot.ast.If;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.Unary;
import polyglot.visit.NodeVisitor;

/*
 * TODO compute avarge complexity weighted by depth similar to expression complexity
 * 
 * 
 * A unary boolean condition should have the complexity (BooleanLit) 1
 * A noted condition (!)  +0.5 
 * A binary relational operation ( < > <= >= == !=) +0.5
 * A boolean logical operator ( AND and OR) +1.0
 */
public class ConditionComplexityMetric extends ASTMetric {
  int loopComplexity;
  int ifComplexity;

  public ConditionComplexityMetric(polyglot.ast.Node node) {
    super(node);
  }

  public void reset() {
    loopComplexity = ifComplexity = 0;
  }

  public void addMetrics(ClassData data) {
    data.addMetric(new MetricData("Loop-Cond-Complexity", new Integer(loopComplexity)));
    data.addMetric(new MetricData("If-Cond-Complexity", new Integer(ifComplexity)));
    data.addMetric(new MetricData("Total-Cond-Complexity", new Integer(loopComplexity + ifComplexity)));
  }

  public NodeVisitor enter(Node parent, Node n) {
    if (n instanceof Loop) {
      Expr expr = ((Loop) n).cond();
      loopComplexity += condComplexity(expr);
    } else if (n instanceof If) {
      Expr expr = ((If) n).cond();
      ifComplexity += condComplexity(expr);
    }

    return enter(n);
  }

  private double condComplexity(Expr expr) {

    // boolean literal
    // binary check for AND and OR ... else its relational!!
    // unary (Check for NOT)

    if (expr instanceof Binary) {
      Binary b = (Binary) expr;
      if (b.operator() == Binary.COND_AND || b.operator() == Binary.COND_OR) {
        // System.out.println(">>>>>>>> Binary (AND or OR) "+expr);
        return 1.0 + condComplexity(b.left()) + condComplexity(b.right());
      } else {
        // System.out.println(">>>>>>>> Binary (relatinal) "+expr);
        return 0.5 + condComplexity(b.left()) + condComplexity(b.right());
      }
    } else if (expr instanceof Unary) {
      if (((Unary) expr).operator() == Unary.NOT) {
        // System.out.println(">>>>>>>>>>>>>>Unary: !"+expr);
        return 0.5 + condComplexity(((Unary) expr).expr());
      } else {
        // System.out.println(">>>>>>>>>>>>>>unary but Not ! "+expr);
        return condComplexity(((Unary) expr).expr());
      }
    } else {
      return 1;// should return something as it is a condition after all
    }
  }

}
