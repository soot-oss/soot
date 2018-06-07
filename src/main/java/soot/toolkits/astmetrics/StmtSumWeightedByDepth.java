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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import polyglot.ast.Block;
import polyglot.ast.Branch;
import polyglot.ast.CodeDecl;
import polyglot.ast.Expr;
import polyglot.ast.Formal;
import polyglot.ast.If;
import polyglot.ast.Initializer;
import polyglot.ast.Labeled;
import polyglot.ast.LocalClassDecl;
import polyglot.ast.Loop;
import polyglot.ast.Node;
import polyglot.ast.ProcedureDecl;
import polyglot.ast.Stmt;
import polyglot.ast.Switch;
import polyglot.ast.Synchronized;
import polyglot.ast.Try;
import polyglot.util.CodeWriter;
import polyglot.visit.NodeVisitor;

public class StmtSumWeightedByDepth extends ASTMetric {

  int currentDepth;
  int sum;
  int maxDepth;
  int numNodes;

  Stack<ArrayList> labelNodesSoFar = new Stack<ArrayList>();
  ArrayList<Node> blocksWithAbruptFlow = new ArrayList<Node>();
  HashMap<Node, Integer> stmtToMetric = new HashMap<Node, Integer>();
  HashMap<Node, Integer> stmtToMetricDepth = new HashMap<Node, Integer>();

  public static boolean tmpAbruptChecker = false;

  public StmtSumWeightedByDepth(Node node) {
    super(node);
  }

  public void printAstMetric(Node n, CodeWriter w) {
    if (n instanceof Stmt) {
      if (stmtToMetric.containsKey(n)) {
        w.write(" // sum= " + stmtToMetric.get(n) + " : depth= " + stmtToMetricDepth.get(n) + "\t");
      }
    }
  }

  public void reset() {
    // if not one, then fields and method sigs don't get counted
    currentDepth = 1; // inside a class
    maxDepth = 1;
    sum = 0;
    numNodes = 0;
  }

  public void addMetrics(ClassData data) {
    // data.addMetric(new MetricData("MaxDepth",new Integer(maxDepth)));

    data.addMetric(new MetricData("D-W-Complexity", new Double(sum)));

    data.addMetric(new MetricData("AST-Node-Count", new Integer(numNodes)));
  }

  private void increaseDepth() {
    System.out.println("Increasing depth");
    currentDepth++;
    if (currentDepth > maxDepth) {
      maxDepth = currentDepth;
    }
  }

  private void decreaseDepth() {
    System.out.println("Decreasing depth");
    currentDepth--;
  }

  /*
   * List of Node types which increase depth of traversal!!! Any construct where one can have a { } increases the depth hence
   * even though if(cond) stmt doesnt expicitly use a block its depth is still +1 when executing the stmt
   *
   * If the "if" stmt has code if(cond) { stmt } OR if(cond) stmt this will only increase the depth by 1 (ignores compound
   * stmt blocks)
   *
   * If, Loop, Try, Synch, ProcDecl, Init, Switch, LocalClassDecl .... add currentDepth to sum and then increase depth by one
   * irrespective of how many stmts there are in the body
   *
   * Block ... if it is a block within a block, add currentDepth plus increment depth ONLY if it has abrupt flow out of it.
   */
  public NodeVisitor enter(Node parent, Node n) {
    numNodes++;
    if (n instanceof CodeDecl) {
      // maintain stack of label arrays (can't have label from inside method to outside)
      labelNodesSoFar.push(new ArrayList());
    } else if (n instanceof Labeled) {
      // add any labels we find to the array
      labelNodesSoFar.peek().add(((Labeled) n).label());
    }

    if (n instanceof If || n instanceof Loop || n instanceof Try || n instanceof Switch || n instanceof LocalClassDecl
        || n instanceof Synchronized || n instanceof ProcedureDecl || n instanceof Initializer) {
      sum += currentDepth * 2;
      System.out.println(n);
      increaseDepth();
    } else if (parent instanceof Block && n instanceof Block) {
      StmtSumWeightedByDepth.tmpAbruptChecker = false;
      n.visit(new NodeVisitor() {
        // extended NodeVisitor that checks for branching out of a block
        public NodeVisitor enter(Node parent, Node node) {
          if (node instanceof Branch) {
            Branch b = (Branch) node;
            // null branching out of a plain block is NOT ALLOWED!
            if (b.label() != null && labelNodesSoFar.peek().contains(b.label())) {
              StmtSumWeightedByDepth.tmpAbruptChecker = true;
            }
          }
          return enter(node);
        }

        // this method simply stops further node visiting if we found our info
        public Node override(Node parent, Node node) {
          if (StmtSumWeightedByDepth.tmpAbruptChecker) {
            return node;
          }
          return null;
        }
      });

      if (StmtSumWeightedByDepth.tmpAbruptChecker) {
        blocksWithAbruptFlow.add(n);
        sum += currentDepth * 2;
        System.out.println(n);
        increaseDepth();
      }
    }
    // switch from Stmt to Expr here, since Expr is the smallest unit
    else if (n instanceof Expr || n instanceof Formal) {
      System.out.print(sum + "  " + n + "  ");
      sum += currentDepth * 2;
      System.out.println(sum);
    }

    // carry metric cummulative for each statement for metricPrettyPrinter
    if (n instanceof Stmt) {
      stmtToMetric.put(n, new Integer(sum));
      stmtToMetricDepth.put(n, new Integer(currentDepth));
    }

    return enter(n);
  }

  public Node leave(Node old, Node n, NodeVisitor v) {

    // stack maintenance, if leaving a method
    if (n instanceof CodeDecl) {
      labelNodesSoFar.pop();
    }

    if (n instanceof If || n instanceof Loop || n instanceof Try || n instanceof Switch || n instanceof LocalClassDecl
        || n instanceof Synchronized || n instanceof ProcedureDecl || n instanceof Initializer) {
      decreaseDepth();
    } else if (n instanceof Block && blocksWithAbruptFlow.contains(n)) {
      decreaseDepth();
    }
    return n;
  }
}
