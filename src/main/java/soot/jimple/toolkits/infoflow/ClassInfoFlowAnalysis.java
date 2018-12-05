package soot.jimple.toolkits.infoflow;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.EquivalentValue;
import soot.Local;
import soot.RefLikeType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.FieldRef;
import soot.jimple.IdentityRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.HashMutableDirectedGraph;
import soot.toolkits.graph.MemoryEfficientGraph;
import soot.toolkits.graph.MutableDirectedGraph;
import soot.toolkits.graph.UnitGraph;

// ClassInfoFlowAnalysis written by Richard L. Halpert, 2007-02-22
// Constructs data flow tables for each method of a class.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.

public class ClassInfoFlowAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(ClassInfoFlowAnalysis.class);
  SootClass sootClass;
  InfoFlowAnalysis dfa; // used to access the data flow analyses of other classes

  Map<SootMethod, SmartMethodInfoFlowAnalysis> methodToInfoFlowAnalysis;
  Map<SootMethod, HashMutableDirectedGraph<EquivalentValue>> methodToInfoFlowSummary;

  public static int methodCount = 0;

  public ClassInfoFlowAnalysis(SootClass sootClass, InfoFlowAnalysis dfa) {
    this.sootClass = sootClass;
    this.dfa = dfa;
    methodToInfoFlowAnalysis = new HashMap<SootMethod, SmartMethodInfoFlowAnalysis>();
    methodToInfoFlowSummary = new HashMap<SootMethod, HashMutableDirectedGraph<EquivalentValue>>();

    // doSimpleConservativeDataFlowAnalysis();
  }

  public SmartMethodInfoFlowAnalysis getMethodInfoFlowAnalysis(SootMethod method) {
    if (!methodToInfoFlowAnalysis.containsKey(method)) {
      methodCount++;

      // First do simple version that doesn't follow invoke expressions
      // The "smart" version will be computed later, but since it may
      // request its own DataFlowGraph, we need this simple version first.
      if (!methodToInfoFlowSummary.containsKey(method)) {
        HashMutableDirectedGraph<EquivalentValue> dataFlowGraph = simpleConservativeInfoFlowAnalysis(method);
        methodToInfoFlowSummary.put(method, dataFlowGraph);
      }

      // Then do smart version that does follow invoke expressions, if possible
      if (method.isConcrete()) {
        Body b = method.retrieveActiveBody();
        UnitGraph g = new ExceptionalUnitGraph(b);
        SmartMethodInfoFlowAnalysis smdfa = new SmartMethodInfoFlowAnalysis(g, dfa);

        methodToInfoFlowAnalysis.put(method, smdfa);
        methodToInfoFlowSummary.remove(method);
        methodToInfoFlowSummary.put(method, smdfa.getMethodInfoFlowSummary());
        return smdfa;
        // logger.debug(""+method + " has SMART infoFlowGraph: ");
        // printDataFlowGraph(mdfa.getMethodDataFlowGraph());
      }
    }

    return methodToInfoFlowAnalysis.get(method);
  }

  public MutableDirectedGraph<EquivalentValue> getMethodInfoFlowSummary(SootMethod method) {
    return getMethodInfoFlowSummary(method, true);
  }

  public HashMutableDirectedGraph<EquivalentValue> getMethodInfoFlowSummary(SootMethod method, boolean doFullAnalysis) {
    if (!methodToInfoFlowSummary.containsKey(method)) {
      methodCount++;

      // First do simple version that doesn't follow invoke expressions
      // The "smart" version will be computed later, but since it may
      // request its own DataFlowGraph, we need this simple version first.
      HashMutableDirectedGraph<EquivalentValue> dataFlowGraph = simpleConservativeInfoFlowAnalysis(method);
      methodToInfoFlowSummary.put(method, dataFlowGraph);

      // Then do smart version that does follow invoke expressions, if possible
      if (method.isConcrete() && doFullAnalysis)// && method.getDeclaringClass().isApplicationClass())
      {
        Body b = method.retrieveActiveBody();
        UnitGraph g = new ExceptionalUnitGraph(b);
        SmartMethodInfoFlowAnalysis smdfa = new SmartMethodInfoFlowAnalysis(g, dfa);

        methodToInfoFlowAnalysis.put(method, smdfa);
        methodToInfoFlowSummary.remove(method);
        methodToInfoFlowSummary.put(method, smdfa.getMethodInfoFlowSummary());

        // logger.debug(""+method + " has SMART infoFlowGraph: ");
        // printDataFlowGraph(mdfa.getMethodDataFlowGraph());
      }
    }

    return methodToInfoFlowSummary.get(method);
  }

  /*
   * public void doFixedPointDataFlowAnalysis() { Iterator it = sootClass.getMethods().iterator(); while(it.hasNext()) {
   * SootMethod method = (SootMethod) it.next();
   *
   * if(method.isConcrete()) { Body b = method.retrieveActiveBody(); UnitGraph g = new ExceptionalUnitGraph(b);
   * SmartMethodInfoFlowAnalysis smdfa = new SmartMethodInfoFlowAnalysis(g, dfa, true);
   * if(methodToInfoFlowSummary.containsKey(method)) methodToInfoFlowSummary.remove(method); else methodCount++;
   * methodToInfoFlowSummary.put(method, smdfa.getMethodDataFlowSummary());
   *
   * // logger.debug(""+method + " has FLOW SENSITIVE infoFlowGraph: "); //
   * printDataFlowGraph(mdfa.getMethodDataFlowGraph()); } else { if(methodToInfoFlowSummary.containsKey(method))
   * methodToInfoFlowSummary.remove(method); else methodCount++; methodToInfoFlowSummary.put(method,
   * triviallyConservativeDataFlowAnalysis(method));
   *
   * // logger.debug(""+method + " has TRIVIALLY CONSERVATIVE infoFlowGraph: "); // printDataFlowGraph((MutableDirectedGraph)
   * methodToInfoFlowSummary.get(method)); } } } //
   */
  /*
   * private void doSimpleConservativeDataFlowAnalysis() { Iterator it = sootClass.getMethods().iterator();
   * while(it.hasNext()) { SootMethod method = (SootMethod) it.next(); MutableDirectedGraph infoFlowGraph =
   * simpleConservativeDataFlowAnalysis(method); if(methodToInfoFlowSummary.containsKey(method))
   * methodToInfoFlowSummary.remove(method); else methodCount++; methodToInfoFlowSummary.put(method, infoFlowGraph);
   *
   * // logger.debug(""+method + " has infoFlowGraph: "); // printDataFlowGraph(infoFlowGraph); } } //
   */
  /** Does not require any fixed point calculation */
  private HashMutableDirectedGraph<EquivalentValue> simpleConservativeInfoFlowAnalysis(SootMethod sm) {
    // Constructs a graph representing the data flow between fields, parameters, and the
    // return value of this method. The graph nodes are EquivalentValue wrapped Refs.
    // This version is rather stupid... it just assumes all values flow to all others,
    // except for the return value, which is flowed to by all, but flows to none.

    // This version is also broken... it can't handle the ThisRef without
    // flow sensitivity.

    // If this method cannot have a body, then we can't analyze it...
    if (!sm.isConcrete()) {
      return triviallyConservativeInfoFlowAnalysis(sm);
    }

    Body b = sm.retrieveActiveBody();
    UnitGraph g = new ExceptionalUnitGraph(b);
    HashSet<EquivalentValue> fieldsStaticsParamsAccessed = new HashSet<EquivalentValue>();

    // Get list of fields, globals, and parameters that are accessed
    for (Unit u : g) {
      Stmt s = (Stmt) u;
      if (s instanceof IdentityStmt) {
        IdentityStmt is = (IdentityStmt) s;
        IdentityRef ir = (IdentityRef) is.getRightOp();
        if (ir instanceof ParameterRef) {
          ParameterRef pr = (ParameterRef) ir;
          fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForParameterRef(sm, pr.getIndex()));
        }
      }
      if (s.containsFieldRef()) {
        FieldRef ref = s.getFieldRef();
        if (ref instanceof StaticFieldRef) {
          // This should be added to the list of fields accessed
          // static fields "belong to everyone"
          StaticFieldRef sfr = (StaticFieldRef) ref;
          fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForFieldRef(sm, sfr.getField()));
        } else if (ref instanceof InstanceFieldRef) {
          // If this field is a field of this class,
          // then this should be added to the list of fields accessed
          InstanceFieldRef ifr = (InstanceFieldRef) ref;
          Value base = ifr.getBase();
          if (base instanceof Local) {
            if (dfa.includesInnerFields() || ((!sm.isStatic()) && base.equivTo(b.getThisLocal()))) {
              fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForFieldRef(sm, ifr.getField()));
            }
          }
        }
      }
    }

    // Each accessed field, global, and parameter becomes a node in the graph
    HashMutableDirectedGraph<EquivalentValue> dataFlowGraph = new MemoryEfficientGraph<EquivalentValue>();
    Iterator<EquivalentValue> accessedIt1 = fieldsStaticsParamsAccessed.iterator();
    while (accessedIt1.hasNext()) {
      EquivalentValue o = accessedIt1.next();
      dataFlowGraph.addNode(o);
    }

    // Add all of the nodes necessary to ensure that this is a complete data flow graph
    // Add every parameter of this method
    for (int i = 0; i < sm.getParameterCount(); i++) {
      EquivalentValue parameterRefEqVal = InfoFlowAnalysis.getNodeForParameterRef(sm, i);
      if (!dataFlowGraph.containsNode(parameterRefEqVal)) {
        dataFlowGraph.addNode(parameterRefEqVal);
      }
    }

    // Add every relevant field of this class (static methods don't get non-static fields)
    for (SootField sf : sm.getDeclaringClass().getFields()) {
      if (sf.isStatic() || !sm.isStatic()) {
        EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
        if (!dataFlowGraph.containsNode(fieldRefEqVal)) {
          dataFlowGraph.addNode(fieldRefEqVal);
        }
      }
    }

    // Add every field of this class's superclasses
    SootClass superclass = sm.getDeclaringClass();
    if (superclass.hasSuperclass()) {
      superclass = sm.getDeclaringClass().getSuperclass();
    }
    while (superclass.hasSuperclass()) // we don't want to process Object
    {
      for (SootField scField : superclass.getFields()) {
        if (scField.isStatic() || !sm.isStatic()) {
          EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField);
          if (!dataFlowGraph.containsNode(fieldRefEqVal)) {
            dataFlowGraph.addNode(fieldRefEqVal);
          }
        }
      }
      superclass = superclass.getSuperclass();
    }

    // The return value also becomes a node in the graph
    ParameterRef returnValueRef = null;
    if (sm.getReturnType() != VoidType.v()) {
      returnValueRef = new ParameterRef(sm.getReturnType(), -1);
      dataFlowGraph.addNode(InfoFlowAnalysis.getNodeForReturnRef(sm));
    }

    // ThisRef thisRef = null;
    if (!sm.isStatic()) {
      // thisRef = new ThisRef(sootClass.getType());
      dataFlowGraph.addNode(InfoFlowAnalysis.getNodeForThisRef(sm));
      fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForThisRef(sm));
    }

    // Create an edge from each node (except the return value) to every other node (including the return value)
    // non-Ref-type nodes are ignored
    accessedIt1 = fieldsStaticsParamsAccessed.iterator();
    while (accessedIt1.hasNext()) {
      EquivalentValue r = accessedIt1.next();
      Ref rRef = (Ref) r.getValue();
      if (!(rRef.getType() instanceof RefLikeType) && !dfa.includesPrimitiveInfoFlow()) {
        continue;
      }
      Iterator<EquivalentValue> accessedIt2 = fieldsStaticsParamsAccessed.iterator();
      while (accessedIt2.hasNext()) {
        EquivalentValue s = accessedIt2.next();
        Ref sRef = (Ref) s.getValue();
        if (rRef instanceof ThisRef && sRef instanceof InstanceFieldRef) {
          ; // don't add this edge
        } else if (sRef instanceof ThisRef && rRef instanceof InstanceFieldRef) {
          ; // don't add this edge
        } else if (sRef instanceof ParameterRef && dfa.includesInnerFields()) {
          ; // don't add edges to parameters if we are including inner fields
        } else if (sRef.getType() instanceof RefLikeType) {
          dataFlowGraph.addEdge(r, s);
        }
      }
      if (returnValueRef != null && (returnValueRef.getType() instanceof RefLikeType || dfa.includesPrimitiveInfoFlow())) {
        dataFlowGraph.addEdge(r, InfoFlowAnalysis.getNodeForReturnRef(sm));
      }
    }

    return dataFlowGraph;
  }

  /** Does not require the method to have a body */
  public HashMutableDirectedGraph<EquivalentValue> triviallyConservativeInfoFlowAnalysis(SootMethod sm) {
    HashSet<EquivalentValue> fieldsStaticsParamsAccessed = new HashSet<EquivalentValue>();

    // Add all of the nodes necessary to ensure that this is a complete data flow graph
    // Add every parameter of this method
    for (int i = 0; i < sm.getParameterCount(); i++) {
      EquivalentValue parameterRefEqVal = InfoFlowAnalysis.getNodeForParameterRef(sm, i);
      fieldsStaticsParamsAccessed.add(parameterRefEqVal);
    }

    // Add every relevant field of this class (static methods don't get non-static fields)
    for (Iterator<SootField> it = sm.getDeclaringClass().getFields().iterator(); it.hasNext();) {
      SootField sf = it.next();
      if (sf.isStatic() || !sm.isStatic()) {
        EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
        fieldsStaticsParamsAccessed.add(fieldRefEqVal);
      }
    }

    // Add every field of this class's superclasses
    SootClass superclass = sm.getDeclaringClass();
    if (superclass.hasSuperclass()) {
      superclass = sm.getDeclaringClass().getSuperclass();
    }
    while (superclass.hasSuperclass()) // we don't want to process Object
    {
      Iterator<SootField> scFieldsIt = superclass.getFields().iterator();
      while (scFieldsIt.hasNext()) {
        SootField scField = scFieldsIt.next();
        if (scField.isStatic() || !sm.isStatic()) {
          EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField);
          fieldsStaticsParamsAccessed.add(fieldRefEqVal);
        }
      }
      superclass = superclass.getSuperclass();
    }

    // Don't add any static fields outside of the class... unsafe???

    // Each field, global, and parameter becomes a node in the graph
    HashMutableDirectedGraph<EquivalentValue> dataFlowGraph = new MemoryEfficientGraph<EquivalentValue>();
    Iterator<EquivalentValue> accessedIt1 = fieldsStaticsParamsAccessed.iterator();
    while (accessedIt1.hasNext()) {
      EquivalentValue o = accessedIt1.next();
      dataFlowGraph.addNode(o);
    }

    // The return value also becomes a node in the graph
    ParameterRef returnValueRef = null;
    if (sm.getReturnType() != VoidType.v()) {
      returnValueRef = new ParameterRef(sm.getReturnType(), -1);
      dataFlowGraph.addNode(InfoFlowAnalysis.getNodeForReturnRef(sm));
    }

    if (!sm.isStatic()) {
      dataFlowGraph.addNode(InfoFlowAnalysis.getNodeForThisRef(sm));
      fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForThisRef(sm));
    }

    // Create an edge from each node (except the return value) to every other node (including the return value)
    // non-Ref-type nodes are ignored
    accessedIt1 = fieldsStaticsParamsAccessed.iterator();
    while (accessedIt1.hasNext()) {
      EquivalentValue r = accessedIt1.next();
      Ref rRef = (Ref) r.getValue();
      if (!(rRef.getType() instanceof RefLikeType) && !dfa.includesPrimitiveInfoFlow()) {
        continue;
      }
      Iterator<EquivalentValue> accessedIt2 = fieldsStaticsParamsAccessed.iterator();
      while (accessedIt2.hasNext()) {
        EquivalentValue s = accessedIt2.next();
        Ref sRef = (Ref) s.getValue();
        if (rRef instanceof ThisRef && sRef instanceof InstanceFieldRef) {
          ; // don't add this edge
        } else if (sRef instanceof ThisRef && rRef instanceof InstanceFieldRef) {
          ; // don't add this edge
        } else if (sRef.getType() instanceof RefLikeType) {
          dataFlowGraph.addEdge(r, s);
        }
      }
      if (returnValueRef != null && (returnValueRef.getType() instanceof RefLikeType || dfa.includesPrimitiveInfoFlow())) {
        dataFlowGraph.addEdge(r, InfoFlowAnalysis.getNodeForReturnRef(sm));
      }
    }

    return dataFlowGraph;
  }

}
