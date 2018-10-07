package soot.jimple.spark.ondemand;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.jimple.spark.ondemand.genericutil.Predicate;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PagToDotDumper;
import soot.jimple.spark.pag.VarNode;

/**
 * you can just add edges and then dump them as a dot graph
 * 
 * @author Manu Sridharan
 * 
 */
public class DotPointerGraph {
  private static final Logger logger = LoggerFactory.getLogger(DotPointerGraph.class);

  private final Set<String> edges = new HashSet<String>();

  private final Set<Node> nodes = new HashSet<Node>();

  public void addAssign(VarNode from, VarNode to) {
    addEdge(to, from, "", "black");
  }

  private void addEdge(Node from, Node to, String edgeLabel, String color) {
    nodes.add(from);
    nodes.add(to);
    addEdge(PagToDotDumper.makeNodeName(from), PagToDotDumper.makeNodeName(to), edgeLabel, color);
  }

  private void addEdge(String from, String to, String edgeLabel, String color) {
    StringBuffer tmp = new StringBuffer();
    tmp.append("    ");
    tmp.append(from);
    tmp.append(" -> ");
    tmp.append(to);
    tmp.append(" [label=\"");
    tmp.append(edgeLabel);
    tmp.append("\", color=");
    tmp.append(color);
    tmp.append("];");
    edges.add(tmp.toString());
  }

  public void addNew(AllocNode from, VarNode to) {
    addEdge(to, from, "", "yellow");
  }

  public void addCall(VarNode from, VarNode to, Integer callSite) {
    addEdge(to, from, callSite.toString(), "blue");
  }

  public void addMatch(VarNode from, VarNode to) {
    addEdge(to, from, "", "brown");
  }

  public void addLoad(FieldRefNode from, VarNode to) {
    addEdge(to, from.getBase(), from.getField().toString(), "green");
  }

  public void addStore(VarNode from, FieldRefNode to) {
    addEdge(to.getBase(), from, to.getField().toString(), "red");
  }

  public int numEdges() {
    return edges.size();
  }

  public void dump(String filename) {
    PrintWriter pw = null;
    try {
      pw = new PrintWriter(new FileOutputStream(filename));
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage(), e);
    }
    // pw.println("digraph G {\n\trankdir=LR;");
    pw.println("digraph G {");
    Predicate<Node> falsePred = new Predicate<Node>() {

      @Override
      public boolean test(Node obj_) {
        return false;
      }

    };
    for (Node node : nodes) {
      pw.println(PagToDotDumper.makeDotNodeLabel(node, falsePred));
    }
    for (String edge : edges) {
      pw.println(edge);
    }
    pw.println("}");
    pw.close();

  }
}
