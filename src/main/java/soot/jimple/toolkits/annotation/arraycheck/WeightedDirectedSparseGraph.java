package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class WeightedDirectedSparseGraph {
  private boolean isUnknown;

  /* The graph is in linked list structure. */
  private Hashtable<Object, Hashtable<Object, IntContainer>> sources
      = new Hashtable<Object, Hashtable<Object, IntContainer>>();

  /* vertex set, may contain superious nodes. */
  private HashSet vertexes = new HashSet();

  public WeightedDirectedSparseGraph(HashSet vertexset) {
    this(vertexset, false);
  }

  public WeightedDirectedSparseGraph(HashSet vertexset, boolean isTop) {
    this.vertexes = vertexset;
    this.isUnknown = !isTop;
  }

  public void setTop() {
    this.isUnknown = false;
    this.sources.clear();
  }

  public HashSet getVertexes() {
    return this.vertexes;
  }

  public void setVertexes(HashSet newset) {
    this.vertexes = newset;
    this.sources.clear();
  }

  /**
   * Add an edge with weight to the graph
   */
  public void addEdge(Object from, Object to, int w) {
    if (this.isUnknown) {
      throw new RuntimeException("Unknown graph can not have edges");
    }

    Hashtable<Object, IntContainer> targets = sources.get(from);

    if (targets == null) {
      /* a new node was added to the graph */
      targets = new Hashtable<Object, IntContainer>();
      sources.put(from, targets);
    }

    IntContainer weight = targets.get(to);

    if (weight == null) {
      weight = new IntContainer(w);
      targets.put(to, weight);
    } else {
      if (weight.value > w) {
        weight.value = w;
      }
    }
  }

  /**
   * addMutualEdge adds bi-direct edges between two nodes. for example, i = j + 1; generates two directed edges. one from j
   * to i with weight 1, another from i to j with weight -1
   */
  public void addMutualEdges(Object from, Object to, int weight) {
    addEdge(from, to, weight);
    addEdge(to, from, -weight);
  }

  /*
   * removeEdge removes all edges from source to target.
   */
  public void removeEdge(Object from, Object to) {
    Hashtable targets = sources.get(from);
    if (targets == null) {
      return;
    }

    targets.remove(to);

    if (targets.size() == 0) {
      sources.remove(from);
    }
  }

  public boolean hasEdge(Object from, Object to) {
    Hashtable targets = sources.get(from);

    if (targets == null) {
      return false;
    }

    return targets.containsKey(to);
  }

  /* return back the weight of the edge from source to target */
  public int edgeWeight(Object from, Object to) {
    Hashtable targets = sources.get(from);

    if (targets == null) {
      throw new RuntimeException("No such edge (" + from + " ," + to + ") exists.");
    }

    IntContainer weight = (IntContainer) targets.get(to);
    if (weight == null) {
      throw new RuntimeException("No such edge (" + from + ", " + to + ") exists.");
    }

    return weight.value;
  }

  /*
   * If other graph is unknown, keep current one. If current graph is unknown, copy the other. And if both are not unknown,
   * union each edge.
   */
  public void unionSelf(WeightedDirectedSparseGraph other) {
    if (other == null) {
      return;
    }

    WeightedDirectedSparseGraph othergraph = other;

    if (othergraph.isUnknown) {
      return;
    }

    if (this.isUnknown) {
      addAll(othergraph);
    }

    List<Object> sourceList = new ArrayList<Object>(this.sources.keySet());

    Iterator<Object> firstSrcIt = sourceList.iterator();

    while (firstSrcIt.hasNext()) {
      Object srcKey = firstSrcIt.next();
      Hashtable src1 = this.sources.get(srcKey);
      Hashtable src2 = othergraph.sources.get(srcKey);

      /* other is unbounded */
      if (src2 == null) {
        this.sources.remove(srcKey);
        continue;
      }

      List targetList = new ArrayList(src1.keySet());

      Iterator targetIt = targetList.iterator();

      while (targetIt.hasNext()) {
        Object target = targetIt.next();

        IntContainer w1 = (IntContainer) src1.get(target);
        IntContainer w2 = (IntContainer) src2.get(target);
        /* other is unbounded */
        if (w2 == null) {
          src1.remove(target);
          continue;
        }

        if (w2.value > w1.value) {
          w1.value = w2.value;
        }
      }

      if (src1.size() == 0) {
        this.sources.remove(srcKey);
      }
    }
  }

  /*
   * it was used to compare with former graph, if the edge weight is increasing, the edge is removed (unlimited distance).
   */
  public void widenEdges(WeightedDirectedSparseGraph othergraph) {
    WeightedDirectedSparseGraph other = othergraph;

    if (other.isUnknown) {
      return;
    }

    Hashtable<Object, Hashtable<Object, IntContainer>> othersources = other.sources;

    List<Object> sourceList = new ArrayList<Object>(this.sources.keySet());

    Iterator<Object> srcIt = sourceList.iterator();
    while (srcIt.hasNext()) {
      Object src = srcIt.next();
      Hashtable thistargets = this.sources.get(src);
      Hashtable othertargets = othersources.get(src);

      /* the former is unbounded */
      if (othertargets == null) {
        this.sources.remove(src);
        continue;
      }

      List targetList = new ArrayList(thistargets.keySet());

      Iterator targetIt = targetList.iterator();
      while (targetIt.hasNext()) {
        Object target = targetIt.next();
        IntContainer thisweight = (IntContainer) thistargets.get(target);
        IntContainer otherweight = (IntContainer) othertargets.get(target);

        /* the former edge is unbounded. */
        if (otherweight == null) {
          thistargets.remove(target);
          continue;
        }

        if (thisweight.value > otherweight.value) {
          thistargets.remove(target);
        }
      }

      if (thistargets.size() == 0) {
        this.sources.remove(src);
      }
    }
  }

  /* It is necessary to prune the graph to the shortest path. */

  /* it could be replaced by a ShortestPathGraph */

  /* kill a node. */

  public void killNode(Object tokill) {
    if (!this.vertexes.contains(tokill)) {
      return;
    }

    this.makeShortestPathGraph();

    List<Object> sourceList = new ArrayList<Object>(sources.keySet());

    Iterator<Object> srcIt = sourceList.iterator();

    while (srcIt.hasNext()) {
      Object src = srcIt.next();

      Hashtable targets = sources.get(src);
      /* delete the in edge */
      targets.remove(tokill);

      if (targets.size() == 0) {
        sources.remove(src);
      }
    }

    sources.remove(tokill);

    this.makeShortestPathGraph();
  }

  /* when met i=i+c, it is necessary to update the weight of in and out edges */
  public void updateWeight(Object which, int c) {
    /* for the in edge, the weight + c. for the out edge, the weight - c */
    Iterator<Object> srcIt = sources.keySet().iterator();

    while (srcIt.hasNext()) {
      Object from = srcIt.next();

      Hashtable targets = sources.get(from);

      IntContainer weight = (IntContainer) targets.get(which);

      if (weight != null) {
        weight.value += c;
      }
    }

    /* update out edges */
    Hashtable toset = sources.get(which);

    if (toset == null) {
      return;
    }

    Iterator toIt = toset.keySet().iterator();
    while (toIt.hasNext()) {
      Object to = toIt.next();

      IntContainer weight = (IntContainer) toset.get(to);

      weight.value -= c;
    }
  }

  public void clear() {
    sources.clear();
  }

  public void replaceAllEdges(WeightedDirectedSparseGraph other) {
    this.isUnknown = other.isUnknown;
    this.vertexes = other.vertexes;
    this.sources = other.sources;
  }

  /* add edges that belong to this vertex set */
  public void addBoundedAll(WeightedDirectedSparseGraph another) {
    this.isUnknown = another.isUnknown;

    Hashtable<Object, Hashtable<Object, IntContainer>> othersources = another.sources;

    Iterator thisnodeIt = this.vertexes.iterator();
    while (thisnodeIt.hasNext()) {
      Object src = thisnodeIt.next();
      Hashtable othertargets = othersources.get(src);
      if (othertargets == null) {
        continue;
      }

      Hashtable<Object, IntContainer> thistargets = new Hashtable<Object, IntContainer>();
      Iterator othertargetIt = othertargets.keySet().iterator();
      while (othertargetIt.hasNext()) {
        Object key = othertargetIt.next();
        if (this.vertexes.contains(key)) {
          IntContainer weight = (IntContainer) othertargets.get(key);
          thistargets.put(key, weight.dup());
        }
      }

      if (thistargets.size() > 0) {
        this.sources.put(src, thistargets);
      }
    }
  }

  /*
   * add another graph's edge and weight to this graph, it simply replace the edge weight. When used with clear, it can copy
   * a graph to a new graph
   */
  public void addAll(WeightedDirectedSparseGraph othergraph) {
    WeightedDirectedSparseGraph another = othergraph;

    this.isUnknown = another.isUnknown;
    this.clear();

    Hashtable<Object, Hashtable<Object, IntContainer>> othersources = another.sources;
    Iterator<Object> othersrcIt = othersources.keySet().iterator();

    while (othersrcIt.hasNext()) {
      Object src = othersrcIt.next();

      Hashtable othertargets = othersources.get(src);

      Hashtable<Object, IntContainer> thistargets = new Hashtable<Object, IntContainer>(othersources.size());
      this.sources.put(src, thistargets);

      Iterator targetIt = othertargets.keySet().iterator();
      while (targetIt.hasNext()) {
        Object target = targetIt.next();

        IntContainer otherweight = (IntContainer) othertargets.get(target);

        thistargets.put(target, otherweight.dup());
      }
    }
  }

  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }

    if (!(other instanceof WeightedDirectedSparseGraph)) {
      return false;
    }

    WeightedDirectedSparseGraph othergraph = (WeightedDirectedSparseGraph) other;

    if (this.isUnknown != othergraph.isUnknown) {
      return false;
    }

    if (this.isUnknown) {
      return true;
    }

    // compare each edges. It is not always true, only when shortest path graph can be guaranteed.
    Hashtable<Object, Hashtable<Object, IntContainer>> othersources = othergraph.sources;

    if (this.sources.size() != othersources.size()) {
      return false;
    }

    Iterator<Object> sourceIt = this.sources.keySet().iterator();
    while (sourceIt.hasNext()) {
      Object src = sourceIt.next();
      Hashtable thistarget = sources.get(src);
      Hashtable othertarget = othersources.get(src);

      if (othertarget == null) {
        return false;
      }

      if (thistarget.size() != othertarget.size()) {
        return false;
      }

      Iterator targetIt = thistarget.keySet().iterator();
      while (targetIt.hasNext()) {
        Object target = targetIt.next();
        IntContainer thisweight = (IntContainer) thistarget.get(target);
        IntContainer otherweight = (IntContainer) othertarget.get(target);

        if (otherweight == null) {
          return false;
        }

        if (thisweight.value != otherweight.value) {
          return false;
        }
      }
    }

    return true;
  }

  public String toString() {
    String graphstring = "WeightedDirectedSparseGraph:\n";

    graphstring = graphstring + this.vertexes + "\n";

    Iterator<Object> srcIt = sources.keySet().iterator();

    while (srcIt.hasNext()) {
      Object src = srcIt.next();

      graphstring = graphstring + src + " : ";

      Hashtable targets = sources.get(src);

      Iterator targetIt = targets.keySet().iterator();
      while (targetIt.hasNext()) {
        Object target = targetIt.next();
        IntContainer weight = (IntContainer) targets.get(target);
        graphstring = graphstring + target + "(" + weight.value + ")  ";
      }
      graphstring += "\n";
    }

    return graphstring;
  }

  public WeightedDirectedSparseGraph dup() {
    WeightedDirectedSparseGraph newone = new WeightedDirectedSparseGraph(this.vertexes);

    newone.addAll(this);

    return newone;
  }

  public boolean makeShortestPathGraph() {
    boolean nonegcycle = true;

    List<Object> srcList = new ArrayList<Object>(sources.keySet());

    Iterator<Object> srcIt = srcList.iterator();

    while (srcIt.hasNext()) {
      Object src = srcIt.next();

      if (!SSSPFinder(src)) {
        nonegcycle = false;
      }
    }

    return nonegcycle;
  }

  private final HashSet<Object> reachableNodes = new HashSet<Object>();
  private final HashSet<WeightedDirectedEdge> reachableEdges = new HashSet<WeightedDirectedEdge>();
  private final Hashtable<Object, IntContainer> distance = new Hashtable<Object, IntContainer>();
  private final Hashtable<Object, Object> pei = new Hashtable<Object, Object>();

  private boolean SSSPFinder(Object src) {
    Hashtable<Object, IntContainer> outedges = sources.get(src);
    if (outedges == null) {
      return true;
    }

    if (outedges.size() == 0) {
      return true;
    }

    InitializeSingleSource(src);
    getReachableNodesAndEdges(src);

    // relaxation
    int vSize = reachableNodes.size();

    for (int i = 0; i < vSize; i++) {
      Iterator<WeightedDirectedEdge> edgeIt = reachableEdges.iterator();

      while (edgeIt.hasNext()) {
        WeightedDirectedEdge edge = edgeIt.next();

        Relax(edge.from, edge.to, edge.weight);
      }
    }

    distance.remove(src);

    // check negative cycle

    {
      Iterator<WeightedDirectedEdge> edgeIt = reachableEdges.iterator();
      while (edgeIt.hasNext()) {
        WeightedDirectedEdge edge = edgeIt.next();

        IntContainer dfrom = distance.get(edge.from);

        if (dfrom == null) {
          continue;
        }

        IntContainer dto = distance.get(edge.to);
        if (dto == null) {
          continue;
        }

        if (dto.value > (dfrom.value + edge.weight)) {
          return false;
        }
      }
    }

    // update the graph
    outedges.clear();
    Iterator<Object> targetIt = distance.keySet().iterator();
    while (targetIt.hasNext()) {
      Object to = targetIt.next();
      IntContainer dist = distance.get(to);

      outedges.put(to, dist.dup());
    }

    return true;

  }

  private void InitializeSingleSource(Object src) {
    reachableNodes.clear();
    reachableEdges.clear();
    pei.clear();
    distance.clear();
    distance.put(src, new IntContainer(0));
  }

  private void getReachableNodesAndEdges(Object src) {
    LinkedList<Object> worklist = new LinkedList<Object>();

    reachableNodes.add(src);
    worklist.add(src);

    while (!worklist.isEmpty()) {
      Object from = worklist.removeFirst();

      Hashtable targets = sources.get(from);
      if (targets == null) {
        continue;
      }

      Iterator targetIt = targets.keySet().iterator();
      while (targetIt.hasNext()) {
        Object target = targetIt.next();

        if (!reachableNodes.contains(target)) {
          worklist.add(target);
          reachableNodes.add(target);
        }

        IntContainer weight = (IntContainer) targets.get(target);

        reachableEdges.add(new WeightedDirectedEdge(from, target, weight.value));
      }
    }
  }

  private void Relax(Object from, Object to, int weight) {
    IntContainer dfrom = distance.get(from);
    IntContainer dto = distance.get(to);

    if (dfrom != null) {
      int vfrom = dfrom.value;
      int vnew = vfrom + weight;
      if (dto == null) {
        distance.put(to, new IntContainer(vnew));
        pei.put(to, from);
      } else {
        int vto = dto.value;
        if (vto > vnew) {
          distance.put(to, new IntContainer(vnew));
          pei.put(to, from);
        }
      }
    }
  }

}
