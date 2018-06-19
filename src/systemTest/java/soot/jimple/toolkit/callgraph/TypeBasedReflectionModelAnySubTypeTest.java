package soot.jimple.toolkit.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 John Toman
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

import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import soot.Kind;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.spark.internal.CompleteAccessibility;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

public class TypeBasedReflectionModelAnySubTypeTest extends AbstractTestingFramework {
  private static final String[] INTERFACE_INVOKEES = new String[] {
      "<soot.jimple.toolkit.callgraph.SubImplementation: void invokeTarget(java.lang.String)>",
      "<soot.jimple.toolkit.callgraph.SubClass: void invokeTarget2(java.lang.String)>"
  };
  
  private static final String[] INTERFACE_EXCLUDED_TARGETS = new String[] {
      "<soot.jimple.toolkit.callgraph.SubImplementation: void doNotCall(soot.jimple.toolkit.callgraph.Interface)>",
      "<soot.jimple.toolkit.callgraph.SubClass: void doNotCall2(soot.jimple.toolkit.callgraph.Interface)>"
  };
  
  private static final String[] CLASS_INVOKEES = new String[] {
      "<soot.jimple.toolkit.callgraph.B: void invokeTarget(java.lang.String)>",
      "<soot.jimple.toolkit.callgraph.C: void invokeTarget2(java.lang.String)>"
  };
  private static final String TEST_PACKAGE = "soot.jimple.toolkit.callgraph.*";
  private static final String TEST_PTA_ENTRY_POINT = "<soot.jimple.toolkit.callgraph.EntryPoint: void ptaResolution()>";
  private static final String TEST_TYPESTATE_ENTRY_POINT = "<soot.jimple.toolkit.callgraph.EntryPoint: void typestateResolution()>";

  @Test
  public void anySubTypePointsToResolution() {
    SootMethod entryPoint = prepareTarget(TEST_PTA_ENTRY_POINT, TEST_PACKAGE);
    commonInvokeTest(entryPoint);
  }
  
  @Test
  public void anySubTypeTypestateResolution() {
    SootMethod entryPoint = prepareTarget(TEST_TYPESTATE_ENTRY_POINT, TEST_PACKAGE);
    commonInvokeTest(entryPoint);
  }

  private void commonInvokeTest(SootMethod entryPoint) {
    Set<String> interfaceInvokeCallees = new HashSet<>();
    Set<String> classInvokeCallees = new HashSet<>();
    Scene.v().getCallGraph().edgesOutOf(entryPoint).forEachRemaining(edge -> {
      if(edge.kind() != Kind.REFL_INVOKE) {
        return;
      }
      Stmt src = edge.srcStmt();
      Assert.assertTrue(src.containsInvokeExpr());
      InvokeExpr ie = src.getInvokeExpr();
      Assert.assertEquals("Wrong signature", "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>", 
         ie.getMethodRef().getSignature());
      Assert.assertTrue(ie.getArgCount() > 0);
      Assert.assertThat(ie.getArg(0).getType(), instanceOf(RefType.class));
      RefType t = (RefType) ie.getArg(0).getType();
      if(t.getClassName().equals("soot.jimple.toolkit.callgraph.Interface")) {
        interfaceInvokeCallees.add(edge.getTgt().method().getSignature());
      } else if(t.getClassName().equals("soot.jimple.toolkit.callgraph.A")) {
        classInvokeCallees.add(edge.getTgt().method().getSignature());
      } else {
        Assert.fail("Unrecognized base type " + t);
      }
    });
    Assert.assertFalse(interfaceInvokeCallees.isEmpty());
    Assert.assertFalse(classInvokeCallees.isEmpty());
    
    Assert.assertThat(interfaceInvokeCallees, hasItems(INTERFACE_INVOKEES));
    Assert.assertThat(interfaceInvokeCallees, not(hasItems(INTERFACE_EXCLUDED_TARGETS)));
    
    Assert.assertThat(classInvokeCallees, hasItems(CLASS_INVOKEES));
    Assert.assertEquals(2, classInvokeCallees.size());
  }
  
  @Override
  protected void setupSoot() {
    super.setupSoot();
    Options.v().setPhaseOption("cg", "types-for-invoke:true");
    Options.v().setPhaseOption("cg", "library:any-subtype");
    Scene.v().addBasicClass("soot.jimple.toolkit.callgraph.EntryPoint");
    Scene.v().addBasicClass("soot.jimple.toolkit.callgraph.C");
    Scene.v().setClientAccessibilityOracle(CompleteAccessibility.v());
  }
}
