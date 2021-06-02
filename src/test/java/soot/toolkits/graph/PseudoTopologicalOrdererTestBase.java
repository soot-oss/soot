package soot.toolkits.graph;

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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

public class PseudoTopologicalOrdererTestBase {

  // Use a single global instance to ensure the implementation is safe for reuse
  private final Orderer<Node> orderer;

  public PseudoTopologicalOrdererTestBase(Orderer<Node> orderer) {
    this.orderer = orderer;
  }

  @Test
  public void testOrder1() {
    // linear graph of 10 nodes
    Node n0 = new Node(0);
    Node n1 = new Node(1);
    n0.addkid(n1);
    Node n2 = new Node(2);
    n1.addkid(n2);
    Node n3 = new Node(3);
    n2.addkid(n3);
    Node n4 = new Node(4);
    n3.addkid(n4);
    Node n5 = new Node(5);
    n4.addkid(n5);
    Node n6 = new Node(6);
    n5.addkid(n6);
    Node n7 = new Node(7);
    n6.addkid(n7);
    Node n8 = new Node(8);
    n7.addkid(n8);
    Node n9 = new Node(9);
    n8.addkid(n9);
    Graph g = new Graph(n0);
    {
      Assert.assertEquals(10, g.size());
    }
    {
      List<Node> order = orderer.newList(g, false);
      Assert.assertEquals(10, order.size());
      Assert.assertEquals(Arrays.asList(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9), order);
    }
    {
      List<Node> order = orderer.newList(g, true);
      Assert.assertEquals(10, order.size());
      Assert.assertEquals(Arrays.asList(n9, n8, n7, n6, n5, n4, n3, n2, n1, n0), order);
    }
    // System.out.println("Completed testOrder1()");
  }

  @Test
  public void testOrder2() {
    // simple binary tree
    Node n0 = new Node(0);
    Node n1 = new Node(1);
    Node n2 = new Node(2);
    n0.addkid(n1);
    n0.addkid(n2);
    Graph g = new Graph(n0);

    {
      Assert.assertEquals(3, g.size());
    }
    {
      List<Node> order = orderer.newList(g, false);
      Assert.assertEquals(3, order.size());
      Assert.assertTrue(order.containsAll(Arrays.asList(n0, n1, n2)));
      Assert.assertEquals(n0, order.get(0));
      // NOTE: the other 2 could be in either order since they are siblings
    }
    {
      List<Node> order = orderer.newList(g, true);
      Assert.assertEquals(3, order.size());
      Assert.assertTrue(order.containsAll(Arrays.asList(n0, n1, n2)));
      Assert.assertEquals(n0, order.get(order.size() - 1));
      // NOTE: the other 2 could be in either order since they are siblings
    }
    // System.out.println("Completed testOrder2()");
  }

  @Test
  public void testOrder3() {
    // larger binary tree
    Node n0 = new Node(0);

    Node n1 = new Node(1);
    Node n2 = new Node(2);
    n0.addkid(n1);
    n0.addkid(n2);

    Node n3 = new Node(3);
    Node n4 = new Node(4);
    Node n5 = new Node(5);
    Node n6 = new Node(6);
    n1.addkid(n3);
    n1.addkid(n4);
    n2.addkid(n5);
    n2.addkid(n6);

    Node n7 = new Node(7);
    Node n8 = new Node(8);
    Node n9 = new Node(9);
    Node n10 = new Node(10);
    Node n11 = new Node(11);
    Node n12 = new Node(12);
    Node n13 = new Node(13);
    Node n14 = new Node(14);
    n3.addkid(n7);
    n3.addkid(n8);
    n4.addkid(n9);
    n4.addkid(n10);
    n5.addkid(n11);
    n5.addkid(n12);
    n6.addkid(n13);
    n6.addkid(n14);

    Graph g = new Graph(n0);

    {
      Assert.assertEquals(15, g.size());
    }
    {
      List<Node> order = orderer.newList(g, false);
      Assert.assertEquals(15, order.size());
      Assert.assertTrue(order.containsAll(Arrays.asList(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14)));
      // Check for topological sort property: edge (i,j) implies i is before j in the order
      // - n0 comes before all others
      Assert.assertEquals(n0, order.get(0));
      // - n1 comes before the entire left hand sub-tree
      Assert.assertTrue(order.indexOf(n1) < order.indexOf(n3));
      Assert.assertTrue(order.indexOf(n1) < order.indexOf(n4));
      Assert.assertTrue(order.indexOf(n1) < order.indexOf(n7));
      Assert.assertTrue(order.indexOf(n1) < order.indexOf(n8));
      Assert.assertTrue(order.indexOf(n1) < order.indexOf(n9));
      Assert.assertTrue(order.indexOf(n1) < order.indexOf(n10));
      // - n2 comes before the entire right hand sub-tree
      Assert.assertTrue(order.indexOf(n2) < order.indexOf(n5));
      Assert.assertTrue(order.indexOf(n2) < order.indexOf(n6));
      Assert.assertTrue(order.indexOf(n2) < order.indexOf(n11));
      Assert.assertTrue(order.indexOf(n2) < order.indexOf(n12));
      Assert.assertTrue(order.indexOf(n2) < order.indexOf(n13));
      Assert.assertTrue(order.indexOf(n2) < order.indexOf(n14));
      // - n3 comes before n7 and n8
      Assert.assertTrue(order.indexOf(n3) < order.indexOf(n7));
      Assert.assertTrue(order.indexOf(n3) < order.indexOf(n8));
      // - n4 comes before n9 and n10
      Assert.assertTrue(order.indexOf(n4) < order.indexOf(n9));
      Assert.assertTrue(order.indexOf(n4) < order.indexOf(n10));
      // - n5 comes before n11 and n12
      Assert.assertTrue(order.indexOf(n5) < order.indexOf(n11));
      Assert.assertTrue(order.indexOf(n5) < order.indexOf(n12));
      // - n6 comes before n13 and n14
      Assert.assertTrue(order.indexOf(n6) < order.indexOf(n13));
      Assert.assertTrue(order.indexOf(n6) < order.indexOf(n14));
    }
    {
      List<Node> order = orderer.newList(g, true);
      Assert.assertEquals(15, order.size());
      Assert.assertTrue(order.containsAll(Arrays.asList(n0, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14)));
      // Check for reverse topological sort property: edge (i,j) implies j is before i in the order
      // - n0 comes after all others
      Assert.assertEquals(n0, order.get(order.size() - 1));
      // - n1 comes after the entire left hand sub-tree
      Assert.assertTrue(order.indexOf(n1) > order.indexOf(n3));
      Assert.assertTrue(order.indexOf(n1) > order.indexOf(n4));
      Assert.assertTrue(order.indexOf(n1) > order.indexOf(n7));
      Assert.assertTrue(order.indexOf(n1) > order.indexOf(n8));
      Assert.assertTrue(order.indexOf(n1) > order.indexOf(n9));
      Assert.assertTrue(order.indexOf(n1) > order.indexOf(n10));
      // - n2 comes after the entire right hand sub-tree
      Assert.assertTrue(order.indexOf(n2) > order.indexOf(n5));
      Assert.assertTrue(order.indexOf(n2) > order.indexOf(n6));
      Assert.assertTrue(order.indexOf(n2) > order.indexOf(n11));
      Assert.assertTrue(order.indexOf(n2) > order.indexOf(n12));
      Assert.assertTrue(order.indexOf(n2) > order.indexOf(n13));
      Assert.assertTrue(order.indexOf(n2) > order.indexOf(n14));
      // - n3 comes after n7 and n8
      Assert.assertTrue(order.indexOf(n3) > order.indexOf(n7));
      Assert.assertTrue(order.indexOf(n3) > order.indexOf(n8));
      // - n4 comes after n9 and n10
      Assert.assertTrue(order.indexOf(n4) > order.indexOf(n9));
      Assert.assertTrue(order.indexOf(n4) > order.indexOf(n10));
      // - n5 comes after n11 and n12
      Assert.assertTrue(order.indexOf(n5) > order.indexOf(n11));
      Assert.assertTrue(order.indexOf(n5) > order.indexOf(n12));
      // - n6 comes after n13 and n14
      Assert.assertTrue(order.indexOf(n6) > order.indexOf(n13));
      Assert.assertTrue(order.indexOf(n6) > order.indexOf(n14));
    }
    // System.out.println("Completed testOrder3()");
  }

  @Test
  public void testOrder4() {
    // graph with a loop and non-tree structures
    Node n0 = new Node(0);
    Node n1 = new Node(1);
    Node n2 = new Node(2);
    Node n3 = new Node(3);
    Node n4 = new Node(4);
    Node n5 = new Node(5);
    Node n6 = new Node(6);
    n0.addkid(n1);
    n1.addkid(n2);
    n2.addkid(n3);
    n3.addkid(n4);
    n4.addkid(n5);
    n5.addkid(n6);

    n3.addkid(n1);
    n2.addkid(n6);

    Graph g = new Graph(n0);

    {
      Assert.assertEquals(7, g.size());
    }
    {
      List<Node> order = orderer.newList(g, false);
      Assert.assertEquals(7, order.size());
      Assert.assertTrue(order.containsAll(Arrays.asList(n0, n1, n2, n3, n4, n5, n6)));
      // NOTE: n0 must come first and n4, n5, n6 must be the tail but the others can be any order
      Assert.assertEquals(n0, order.get(0));
      Assert.assertEquals(n4, order.get(4));
      Assert.assertEquals(n5, order.get(5));
      Assert.assertEquals(n6, order.get(6));
    }
    {
      List<Node> order = orderer.newList(g, true);
      Assert.assertEquals(7, order.size());
      Assert.assertTrue(order.containsAll(Arrays.asList(n0, n1, n2, n3, n4, n5, n6)));
      // NOTE: n0 must come last and n6, n5, n4 must be the head but the others can be any order
      Assert.assertEquals(n0, order.get(6));
      Assert.assertEquals(n4, order.get(2));
      Assert.assertEquals(n5, order.get(1));
      Assert.assertEquals(n6, order.get(0));
    }
    // System.out.println("Completed testOrder4()");
  }

  @Test
  public void testOrder5_threadSafety() {
    final int n = Runtime.getRuntime().availableProcessors();
    ExecutorService executor = Executors.newFixedThreadPool(n);
    ArrayList<Future<?>> futures = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      futures.add(executor.submit(this::testOrder1));
      futures.add(executor.submit(this::testOrder2));
      futures.add(executor.submit(this::testOrder3));
      futures.add(executor.submit(this::testOrder4));
    }
    Assert.assertEquals(n * 4, futures.size());
    try {
      executor.shutdown();
      executor.awaitTermination(60, TimeUnit.SECONDS);
      for (Future<?> f : futures) {
        Assert.assertTrue(f.isDone());
        Assert.assertFalse(f.isCancelled());
      }
    } catch (InterruptedException e) {
      Assert.fail(e.getMessage());
    } finally {
      if (!executor.isTerminated()) {
        // Cancel non-finished tasks, clear cache, and shutdown
        for (Future<?> f : futures) {
          f.cancel(true);
        }
        executor.shutdownNow();
      }
    }
  }
}
