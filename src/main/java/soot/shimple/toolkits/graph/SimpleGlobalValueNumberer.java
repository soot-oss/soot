package soot.shimple.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.shimple.toolkits.graph.ValueGraph.Node;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.CompleteBlockGraph;

public class SimpleGlobalValueNumberer implements GlobalValueNumberer {
  protected BlockGraph cfg;
  protected ValueGraph vg;
  protected Set<Partition> partitions;
  protected Map<Node, Partition> nodeToPartition;

  protected int currentPartitionNumber;

  public SimpleGlobalValueNumberer(BlockGraph cfg) {
    this.cfg = cfg;
    vg = new ValueGraph(cfg);
    partitions = new HashSet<Partition>(); // not deterministic
    nodeToPartition = new HashMap<Node, Partition>();
    currentPartitionNumber = 0;

    initPartition();
    iterPartition();
  }

  // testing
  public static void main(String[] args) {
    // assumes 2 args: Class + Method

    Scene.v().loadClassAndSupport(args[0]);
    SootClass sc = Scene.v().getSootClass(args[0]);
    SootMethod sm = sc.getMethod(args[1]);
    Body b = sm.retrieveActiveBody();
    ShimpleBody sb = Shimple.v().newBody(b);
    CompleteBlockGraph cfg = new CompleteBlockGraph(sb);
    SimpleGlobalValueNumberer sgvn = new SimpleGlobalValueNumberer(cfg);
    System.out.println(sgvn);
  }

  public int getGlobalValueNumber(Local local) {
    Node node = vg.getNode(local);
    return nodeToPartition.get(node).getPartitionNumber();
  }

  public boolean areEqual(Local local1, Local local2) {
    Node node1 = vg.getNode(local1);
    Node node2 = vg.getNode(local2);

    return (nodeToPartition.get(node1) == nodeToPartition.get(node2));
  }

  protected void initPartition() {
    Map<String, Partition> labelToPartition = new HashMap<String, Partition>();

    Iterator<Node> topNodesIt = vg.getTopNodes().iterator();
    while (topNodesIt.hasNext()) {
      Node node = topNodesIt.next();
      String label = node.getLabel();

      Partition partition = labelToPartition.get(label);
      if (partition == null) {
        partition = new Partition();
        partitions.add(partition);
        labelToPartition.put(label, partition);
      }

      partition.add(node);
      nodeToPartition.put(node, partition);
    }
  }

  protected List<Partition> newPartitions;

  protected void iterPartition() {
    newPartitions = new ArrayList<Partition>();

    Iterator<Partition> partitionsIt = partitions.iterator();
    while (partitionsIt.hasNext()) {
      Partition partition = partitionsIt.next();
      processPartition(partition);
    }

    partitions.addAll(newPartitions);
  }

  protected void processPartition(Partition partition) {
    int size = partition.size();

    if (size == 0) {
      return;
    }

    Node firstNode = (Node) partition.get(0);
    Partition newPartition = new Partition();
    boolean processNewPartition = false;

    for (int i = 1; i < size; i++) {
      Node node = (Node) partition.get(i);

      if (!childrenAreInSamePartition(firstNode, node)) {
        partition.remove(i);
        size--;
        newPartition.add(node);
        newPartitions.add(newPartition);
        nodeToPartition.put(node, newPartition);
        processNewPartition = true;
      }
    }

    if (processNewPartition) {
      processPartition(newPartition);
    }
  }

  protected boolean childrenAreInSamePartition(Node node1, Node node2) {
    boolean ordered = node1.isOrdered();
    if (ordered != node2.isOrdered()) {
      throw new RuntimeException("Assertion failed.");
    }

    List<Node> children1 = node1.getChildren();
    List<Node> children2 = node2.getChildren();

    int numberOfChildren = children1.size();
    if (children2.size() != numberOfChildren) {
      return false;
    }

    if (ordered) {
      for (int i = 0; i < numberOfChildren; i++) {
        Node child1 = children1.get(i);
        Node child2 = children2.get(i);
        if (nodeToPartition.get(child1) != nodeToPartition.get(child2)) {
          return false;
        }
      }
    } else {
      for (int i = 0; i < numberOfChildren; i++) {
        Node child1 = children1.get(i);
        for (int j = i; j < numberOfChildren; j++) {
          Node child2 = children2.get(j);

          if (nodeToPartition.get(child1) == nodeToPartition.get(child2)) {
            if (j != i) {
              children2.remove(j);
              children2.add(i, child2);
            }
            break;
          }

          // last iteration, no match
          if ((j + 1) == numberOfChildren) {
            return false;
          }
        }
      }
    }

    return true;
  }

  public String toString() {
    StringBuffer tmp = new StringBuffer();

    Body b = cfg.getBody();
    Iterator localsIt = b.getLocals().iterator();

    while (localsIt.hasNext()) {
      Local local = (Local) localsIt.next();
      tmp.append(local + "\t" + getGlobalValueNumber(local) + "\n");
    }

    /*
     * Iterator partitionsIt = partitions.iterator(); while(partitionsIt.hasNext()){ Partition partition = (Partition)
     * partitionsIt.next(); int partitionNumber = partition.getPartitionNumber();
     *
     * Iterator partitionIt = partition.iterator(); while(partitionIt.hasNext()){ Local local =
     * vg.getLocal((Node)partitionIt.next()); if(local == null) continue; tmp.append(local + "\t" + partitionNumber + "\n");
     * } }
     */

    return tmp.toString();
  }

  protected class Partition extends ArrayList {
    protected int partitionNumber;

    protected Partition() {
      super();
      partitionNumber = currentPartitionNumber++;
    }

    protected int getPartitionNumber() {
      return partitionNumber;
    }
  }
}
