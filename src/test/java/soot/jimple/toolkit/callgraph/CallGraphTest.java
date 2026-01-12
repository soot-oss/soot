/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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
package soot.jimple.toolkit.callgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * Test the CallGraph data structure.
 */
public class CallGraphTest {

  private SootMethod tgtMethod;
  private SootMethod srcMethod;
  private Stmt invokeStmt;

  private static class TestCallGraph extends CallGraph {

    public void assertMapsEmpty() {
      assertTrue(srcMethodToEdge.keySet().isEmpty());
      assertTrue(srcUnitToEdge.keySet().isEmpty());
      assertTrue(tgtToEdge.keySet().isEmpty());
    }

    public void assertMapsNotEmpty() {
      assertFalse(srcMethodToEdge.keySet().isEmpty());
      assertFalse(srcUnitToEdge.keySet().isEmpty());
      assertFalse(tgtToEdge.keySet().isEmpty());
    }

  }

  @Before
  public void setup() {
    SootClass sootClass = new SootClass("Dummy", Modifier.PUBLIC);
    tgtMethod = new SootMethod("tgt", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC);
    sootClass.addMethod(tgtMethod);
    srcMethod = new SootMethod("src", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC);
    sootClass.addMethod(srcMethod);
    JimpleBody body = Jimple.v().newBody(srcMethod);
    Local base = Jimple.v().newLocal("base", RefType.v(sootClass));
    body.getLocals().add(base);
    invokeStmt = Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(base, tgtMethod.makeRef()));
    body.getUnits().add(invokeStmt);
  }

  @Test
  public void testAddRemoveEdge() {
    TestCallGraph cg = new TestCallGraph();
    cg.assertMapsEmpty();
    Edge e = new Edge(srcMethod, invokeStmt, tgtMethod);
    cg.addEdge(e);
    cg.assertMapsNotEmpty();
    assertEquals(1, cg.size());
    assertTrue(cg.edgesOutOf(invokeStmt).hasNext());
    cg.removeEdge(e);
    assertEquals(0, cg.size());
    assertFalse(cg.edgesOutOf(invokeStmt).hasNext());
    cg.assertMapsEmpty();
  }

  @Test
  public void testAddRemoveEdges() {
    TestCallGraph cg = new TestCallGraph();
    Edge e = new Edge(srcMethod, invokeStmt, tgtMethod);
    cg.addEdge(e);
    boolean removedEdges = cg.removeEdges(List.of(e));
    assertTrue(removedEdges);
    assertEquals(0, cg.size());
    cg.assertMapsEmpty();
  }

  @Test
  public void testAddRemoveAllEdgesOutOf() {
    TestCallGraph cg = new TestCallGraph();
    Edge e = new Edge(srcMethod, invokeStmt, tgtMethod);
    cg.addEdge(e);
    boolean removedEdges = cg.removeAllEdgesOutOf(invokeStmt);
    assertTrue(removedEdges);
    assertEquals(0, cg.size());
    cg.assertMapsEmpty();
    assertFalse(cg.edgesOutOf(invokeStmt).hasNext());
  }

}
