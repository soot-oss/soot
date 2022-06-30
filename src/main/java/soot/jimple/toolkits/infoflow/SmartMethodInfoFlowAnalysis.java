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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.EquivalentValue;
import soot.Local;
import soot.RefLikeType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.AnyNewExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.IdentityRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.UnopExpr;
import soot.jimple.internal.JCaughtExceptionRef;
import soot.toolkits.graph.HashMutableDirectedGraph;
import soot.toolkits.graph.MemoryEfficientGraph;
import soot.toolkits.graph.MutableDirectedGraph;
import soot.toolkits.graph.UnitGraph;

// SimpleMethodInfoFlowAnalysis written by Richard L. Halpert, 2007-02-25
// Constructs a data flow table for the given method.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.

public class SmartMethodInfoFlowAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(SmartMethodInfoFlowAnalysis.class);
  UnitGraph graph;
  SootMethod sm;
  Value thisLocal;
  InfoFlowAnalysis dfa;
  boolean refOnly; // determines if primitive type data flow is included
  boolean includeInnerFields; // determines if flow to a field of an object (other than this) is treated like flow to that
                              // object

  HashMutableDirectedGraph<EquivalentValue> abbreviatedInfoFlowGraph;
  HashMutableDirectedGraph<EquivalentValue> infoFlowSummary;
  Ref returnRef;

  boolean printMessages;

  public static int counter = 0;

  public SmartMethodInfoFlowAnalysis(UnitGraph g, InfoFlowAnalysis dfa) {
    graph = g;
    this.sm = g.getBody().getMethod();
    if (sm.isStatic()) {
      this.thisLocal = null;
    } else {
      this.thisLocal = g.getBody().getThisLocal();
    }
    this.dfa = dfa;
    this.refOnly = !dfa.includesPrimitiveInfoFlow();
    this.includeInnerFields = dfa.includesInnerFields();

    this.abbreviatedInfoFlowGraph = new MemoryEfficientGraph<EquivalentValue>();
    this.infoFlowSummary = new MemoryEfficientGraph<EquivalentValue>();

    this.returnRef = new ParameterRef(g.getBody().getMethod().getReturnType(), -1); // it's a dummy parameter ref

    // this.entrySet = new ArraySparseSet();
    // this.emptySet = new ArraySparseSet();

    printMessages = false; // dfa.printDebug();

    counter++;

    // Add all of the nodes necessary to ensure that this is a complete data flow graph

    // Add every parameter of this method
    for (int i = 0; i < sm.getParameterCount(); i++) {
      EquivalentValue parameterRefEqVal = InfoFlowAnalysis.getNodeForParameterRef(sm, i);
      if (!infoFlowSummary.containsNode(parameterRefEqVal)) {
        infoFlowSummary.addNode(parameterRefEqVal);
      }
    }

    // Add every relevant field of this class (static methods don't get non-static fields)
    for (Iterator<SootField> it = sm.getDeclaringClass().getFields().iterator(); it.hasNext();) {
      SootField sf = it.next();
      if (sf.isStatic() || !sm.isStatic()) {
        EquivalentValue fieldRefEqVal;
        if (!sm.isStatic()) {
          fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf, sm.retrieveActiveBody().getThisLocal());
        } else {
          fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
        }

        if (!infoFlowSummary.containsNode(fieldRefEqVal)) {
          infoFlowSummary.addNode(fieldRefEqVal);
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
          EquivalentValue fieldRefEqVal;
          if (!sm.isStatic()) {
            fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField, sm.retrieveActiveBody().getThisLocal());
          } else {
            fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField);
          }
          if (!infoFlowSummary.containsNode(fieldRefEqVal)) {
            infoFlowSummary.addNode(fieldRefEqVal);
          }
        }
      }
      superclass = superclass.getSuperclass();
    }

    // Add thisref of this class
    if (!sm.isStatic()) {
      EquivalentValue thisRefEqVal = InfoFlowAnalysis.getNodeForThisRef(sm);
      if (!infoFlowSummary.containsNode(thisRefEqVal)) {
        infoFlowSummary.addNode(thisRefEqVal);
      }
    }

    // Add returnref of this method
    EquivalentValue returnRefEqVal = new CachedEquivalentValue(returnRef);
    if (returnRef.getType() != VoidType.v() && !infoFlowSummary.containsNode(returnRefEqVal)) {
      infoFlowSummary.addNode(returnRefEqVal);
    }

    // Do the analysis
    Date start = new Date();
    int counterSoFar = counter;
    if (printMessages) {
      logger.debug("STARTING SMART ANALYSIS FOR " + g.getBody().getMethod() + " -----");
    }

    // S=#Statements, R=#Refs, L=#Locals, where generally (S ~= L), (L >> R)
    // Generates a data flow graph of refs and locals where "flows to data structure" is represented in a single node
    generateAbbreviatedInfoFlowGraph(); // O(S)
    // Generates a data flow graph of refs where "flows to data structure" has been resolved
    generateInfoFlowSummary(); // O( R*(L+R) )

    if (printMessages) {
      long longTime = ((new Date()).getTime() - start.getTime());
      float time = (longTime) / 1000.0f;
      logger.debug("ENDING   SMART ANALYSIS FOR " + g.getBody().getMethod() + " ----- " + (counter - counterSoFar + 1)
          + " analyses took: " + time + "s");
      logger.debug("  AbbreviatedDataFlowGraph:");
      InfoFlowAnalysis.printInfoFlowSummary(abbreviatedInfoFlowGraph);
      logger.debug("  DataFlowSummary:");
      InfoFlowAnalysis.printInfoFlowSummary(infoFlowSummary);
    }
  }

  public void generateAbbreviatedInfoFlowGraph() {
    Iterator<Unit> stmtIt = graph.iterator();
    while (stmtIt.hasNext()) {
      Stmt s = (Stmt) stmtIt.next();
      addFlowToCdfg(s);
    }
  }

  public void generateInfoFlowSummary() {
    Iterator<EquivalentValue> nodeIt = infoFlowSummary.iterator();
    while (nodeIt.hasNext()) {
      EquivalentValue node = nodeIt.next();
      List<EquivalentValue> sources = sourcesOf(node);
      Iterator<EquivalentValue> sourcesIt = sources.iterator();
      while (sourcesIt.hasNext()) {
        EquivalentValue source = sourcesIt.next();
        if (source.getValue() instanceof Ref) {
          infoFlowSummary.addEdge(source, node);
        }
      }
    }
  }

  public List<EquivalentValue> sourcesOf(EquivalentValue node) {
    return sourcesOf(node, new HashSet<EquivalentValue>(), new HashSet<EquivalentValue>());
  }

  private List<EquivalentValue> sourcesOf(EquivalentValue node, Set<EquivalentValue> visitedSources,
      Set<EquivalentValue> visitedSinks) {
    visitedSources.add(node);

    List<EquivalentValue> ret = new LinkedList<EquivalentValue>();
    if (!abbreviatedInfoFlowGraph.containsNode(node)) {
      return ret;
    }

    // get direct sources
    Set<EquivalentValue> preds = abbreviatedInfoFlowGraph.getPredsOfAsSet(node);
    Iterator<EquivalentValue> predsIt = preds.iterator();
    while (predsIt.hasNext()) {
      EquivalentValue pred = predsIt.next();
      if (!visitedSources.contains(pred)) {
        ret.add(pred);
        ret.addAll(sourcesOf(pred, visitedSources, visitedSinks));
      }
    }

    // get sources of (sources of sinks, of which we are one)
    List<EquivalentValue> sinks = sinksOf(node, visitedSources, visitedSinks);
    Iterator<EquivalentValue> sinksIt = sinks.iterator();
    while (sinksIt.hasNext()) {
      EquivalentValue sink = sinksIt.next();
      if (!visitedSources.contains(sink)) {
        EquivalentValue flowsToSourcesOf = new CachedEquivalentValue(new AbstractDataSource(sink.getValue()));

        if (abbreviatedInfoFlowGraph.getPredsOfAsSet(sink).contains(flowsToSourcesOf)) {
          ret.addAll(sourcesOf(flowsToSourcesOf, visitedSources, visitedSinks));
        }
      }
    }
    return ret;
  }

  public List<EquivalentValue> sinksOf(EquivalentValue node) {
    return sinksOf(node, new HashSet<EquivalentValue>(), new HashSet<EquivalentValue>());
  }

  private List<EquivalentValue> sinksOf(EquivalentValue node, Set<EquivalentValue> visitedSources,
      Set<EquivalentValue> visitedSinks) {
    List<EquivalentValue> ret = new LinkedList<EquivalentValue>();

    // if(visitedSinks.contains(node))
    // return ret;

    visitedSinks.add(node);

    if (!abbreviatedInfoFlowGraph.containsNode(node)) {
      return ret;
    }

    // get direct sinks
    Set<EquivalentValue> succs = abbreviatedInfoFlowGraph.getSuccsOfAsSet(node);
    Iterator<EquivalentValue> succsIt = succs.iterator();
    while (succsIt.hasNext()) {
      EquivalentValue succ = succsIt.next();
      if (!visitedSinks.contains(succ)) {
        ret.add(succ);
        ret.addAll(sinksOf(succ, visitedSources, visitedSinks));
      }
    }

    // get sources of (sources of sinks, of which we are one)
    succsIt = succs.iterator();
    while (succsIt.hasNext()) {
      EquivalentValue succ = succsIt.next();
      if (succ.getValue() instanceof AbstractDataSource) {
        // It will have ONE successor, who will be the value whose sources it represents
        Set vHolder = abbreviatedInfoFlowGraph.getSuccsOfAsSet(succ);
        EquivalentValue v = (EquivalentValue) vHolder.iterator().next(); // get the one and only
        if (!visitedSinks.contains(v)) {
          // Set<EquivalentValue>
          ret.addAll(sourcesOf(v, visitedSinks, visitedSinks)); // these nodes are really to be marked as sinks, not sources
        }
      }
    }
    return ret;
  }

  public HashMutableDirectedGraph<EquivalentValue> getMethodInfoFlowSummary() {
    return infoFlowSummary;
  }

  public HashMutableDirectedGraph<EquivalentValue> getMethodAbbreviatedInfoFlowGraph() {
    return abbreviatedInfoFlowGraph;
  }

  protected boolean isNonRefType(Type type) {
    return !(type instanceof RefLikeType);
  }

  protected boolean ignoreThisDataType(Type type) {
    return refOnly && isNonRefType(type);
  }

  // For when data flows to a local
  protected void handleFlowsToValue(Value sink, Value source) {
    EquivalentValue sinkEqVal;
    EquivalentValue sourceEqVal;

    if (sink instanceof InstanceFieldRef) {
      InstanceFieldRef ifr = (InstanceFieldRef) sink;
      sinkEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, ifr.getField(), (Local) ifr.getBase()); // deals with inner fields
    } else {
      sinkEqVal = new CachedEquivalentValue(sink);
    }

    if (source instanceof InstanceFieldRef) {
      InstanceFieldRef ifr = (InstanceFieldRef) source;
      sourceEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, ifr.getField(), (Local) ifr.getBase()); // deals with inner
                                                                                                    // fields
    } else {
      sourceEqVal = new CachedEquivalentValue(source);
    }

    if (source instanceof Ref && !infoFlowSummary.containsNode(sourceEqVal)) {
      infoFlowSummary.addNode(sourceEqVal);
    }
    if (sink instanceof Ref && !infoFlowSummary.containsNode(sinkEqVal)) {
      infoFlowSummary.addNode(sinkEqVal);
    }

    if (!abbreviatedInfoFlowGraph.containsNode(sinkEqVal)) {
      abbreviatedInfoFlowGraph.addNode(sinkEqVal);
    }
    if (!abbreviatedInfoFlowGraph.containsNode(sourceEqVal)) {
      abbreviatedInfoFlowGraph.addNode(sourceEqVal);
    }

    abbreviatedInfoFlowGraph.addEdge(sourceEqVal, sinkEqVal);
  }

  // for when data flows to the data structure pointed to by a local
  protected void handleFlowsToDataStructure(Value base, Value source) {
    EquivalentValue sourcesOfBaseEqVal = new CachedEquivalentValue(new AbstractDataSource(base));
    EquivalentValue baseEqVal = new CachedEquivalentValue(base);

    EquivalentValue sourceEqVal;
    if (source instanceof InstanceFieldRef) {
      InstanceFieldRef ifr = (InstanceFieldRef) source;
      sourceEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, ifr.getField(), (Local) ifr.getBase()); // deals with inner
                                                                                                    // fields
    } else {
      sourceEqVal = new CachedEquivalentValue(source);
    }

    if (source instanceof Ref && !infoFlowSummary.containsNode(sourceEqVal)) {
      infoFlowSummary.addNode(sourceEqVal);
    }

    if (!abbreviatedInfoFlowGraph.containsNode(baseEqVal)) {
      abbreviatedInfoFlowGraph.addNode(baseEqVal);
    }
    if (!abbreviatedInfoFlowGraph.containsNode(sourceEqVal)) {
      abbreviatedInfoFlowGraph.addNode(sourceEqVal);
    }
    if (!abbreviatedInfoFlowGraph.containsNode(sourcesOfBaseEqVal)) {
      abbreviatedInfoFlowGraph.addNode(sourcesOfBaseEqVal);
    }

    abbreviatedInfoFlowGraph.addEdge(sourceEqVal, sourcesOfBaseEqVal);
    abbreviatedInfoFlowGraph.addEdge(sourcesOfBaseEqVal, baseEqVal); // for convenience
  }

  // For inner fields... we have base flow to field as a service specifically
  // for the sake of LocalObjects... yes, this is a hack!
  protected void handleInnerField(Value innerFieldRef) {
    /*
     * InstanceFieldRef ifr = (InstanceFieldRef) innerFieldRef;
     *
     * EquivalentValue baseEqVal = new CachedEquivalentValue(ifr.getBase()); EquivalentValue fieldRefEqVal =
     * dfa.getEquivalentValueFieldRef(sm, ifr.getField()); // deals with inner fields
     *
     * if(!abbreviatedInfoFlowGraph.containsNode(baseEqVal)) abbreviatedInfoFlowGraph.addNode(baseEqVal);
     * if(!abbreviatedInfoFlowGraph.containsNode(fieldRefEqVal)) abbreviatedInfoFlowGraph.addNode(fieldRefEqVal);
     *
     * abbreviatedInfoFlowGraph.addEdge(baseEqVal, fieldRefEqVal);
     */
  }

  // handles the invoke expression AND returns a list of the return value's sources
  // for each node
  // if the node is a parameter
  // source = argument <Immediate>
  // if the node is a static field
  // source = node <StaticFieldRef>
  // if the node is a field
  // source = receiver object <Local>
  // if the node is the return value
  // continue

  // for each sink
  // if the sink is a parameter
  // handleFlowsToDataStructure(sink, source, fs)
  // if the sink is a static field
  // handleFlowsToValue(sink, source, fs)
  // if the sink is a field
  // handleFlowsToDataStructure(receiver object, source, fs)
  // if the sink is the return value
  // add node to list of return value sources

  protected List<Value> handleInvokeExpr(InvokeExpr ie, Stmt is) {
    // get the data flow graph
    HashMutableDirectedGraph<EquivalentValue> dataFlowSummary = dfa.getInvokeInfoFlowSummary(ie, is, sm); // must return a
                                                                                                          // graph whose
                                                                                                          // nodes are
                                                                                                          // Refs!!!
    if (false) // DEBUG!!!
    {
      SootMethod method = ie.getMethodRef().resolve();
      if (method.getDeclaringClass().isApplicationClass()) {
        logger.debug("Attempting to print graph (will succeed only if ./dfg/ is a valid path)");
        MutableDirectedGraph<EquivalentValue> abbreviatedDataFlowGraph = dfa.getInvokeAbbreviatedInfoFlowGraph(ie, sm);
        InfoFlowAnalysis.printGraphToDotFile(
            "dfg/" + method.getDeclaringClass().getShortName() + "_" + method.getName() + (refOnly ? "" : "_primitive"),
            abbreviatedDataFlowGraph, method.getName() + (refOnly ? "" : "_primitive"), false);
      }
    }
    // if( ie.getMethodRef().resolve().getSubSignature().equals(new String("boolean remove(java.lang.Object)")) )
    // {
    // logger.debug("*!*!*!*!*!<boolean remove(java.lang.Object)> has FLOW SENSITIVE infoFlowSummary: ");
    // ClassInfoFlowAnalysis.printDataFlowGraph(infoFlowSummary);
    // }

    List<Value> returnValueSources = new ArrayList();

    Iterator<EquivalentValue> nodeIt = dataFlowSummary.getNodes().iterator();
    while (nodeIt.hasNext()) {
      EquivalentValue nodeEqVal = nodeIt.next();

      if (!(nodeEqVal.getValue() instanceof Ref)) {
        throw new RuntimeException(
            "Illegal node type in data flow summary:" + nodeEqVal.getValue() + " should be an object of type Ref.");
      }

      Ref node = (Ref) nodeEqVal.getValue();

      List<Value> sources = new ArrayList();
      // Value source = null;

      if (node instanceof ParameterRef) {
        ParameterRef param = (ParameterRef) node;
        if (param.getIndex() == -1) {
          continue;
        }
        sources.add(ie.getArg(param.getIndex()));
        // source = ; // Immediate
      } else if (node instanceof StaticFieldRef) {
        sources.add(node);
        // source = node; // StaticFieldRef
      } else if (node instanceof InstanceFieldRef && ie instanceof InstanceInvokeExpr) {
        InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
        if (iie.getBase() == thisLocal) {
          sources.add(node);
          // source = node;
        } else if (includeInnerFields) {
          if (false) // isNonRefType(node.getType()) ) // TODO: double check this policy
          {
            // primitives flow from the parent object
            InstanceFieldRef ifr = (InstanceFieldRef) node;
            if (ifr.getBase() instanceof FakeJimpleLocal) {
              ; // sources.add(((FakeJimpleLocal) ifr.getBase()).getRealLocal());
            } else {
              sources.add(ifr.getBase());
            }
          } else {
            // objects flow from both
            InstanceFieldRef ifr = (InstanceFieldRef) node;
            if (ifr.getBase() instanceof FakeJimpleLocal) {
              ; // sources.add(((FakeJimpleLocal) ifr.getBase()).getRealLocal());
            } else {
              sources.add(ifr.getBase());
            }
            sources.add(node);
          }
          // source = node;
          // handleInnerField(source);
        } else {
          sources.add(iie.getBase());
          // source = iie.getBase(); // Local
        }
      } else if (node instanceof InstanceFieldRef && includeInnerFields) {
        if (false) // isNonRefType(node.getType()) ) // TODO: double check this policy
        {
          // primitives flow from the parent object
          InstanceFieldRef ifr = (InstanceFieldRef) node;
          if (ifr.getBase() instanceof FakeJimpleLocal) {
            ; // sources.add(((FakeJimpleLocal) ifr.getBase()).getRealLocal());
          } else {
            sources.add(ifr.getBase());
          }
        } else {
          // objects flow from both
          InstanceFieldRef ifr = (InstanceFieldRef) node;
          if (ifr.getBase() instanceof FakeJimpleLocal) {
            ; // sources.add(((FakeJimpleLocal) ifr.getBase()).getRealLocal());
          } else {
            sources.add(ifr.getBase());
          }
          sources.add(node);
        }
        // source = node;
        // handleInnerField(source);
      } else if (node instanceof ThisRef && ie instanceof InstanceInvokeExpr) {
        InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
        sources.add(iie.getBase());
        // source = iie.getBase(); // Local
      } else {
        throw new RuntimeException("Unknown Node Type in Data Flow Graph: node " + node + " in InvokeExpr " + ie);
      }

      Iterator<EquivalentValue> sinksIt = dataFlowSummary.getSuccsOfAsSet(nodeEqVal).iterator();
      while (sinksIt.hasNext()) {
        EquivalentValue sinkEqVal = sinksIt.next();
        Ref sink = (Ref) sinkEqVal.getValue();
        if (sink instanceof ParameterRef) {
          ParameterRef param = (ParameterRef) sink;
          if (param.getIndex() == -1) {
            returnValueSources.addAll(sources);
          } else {
            for (Iterator<Value> sourcesIt = sources.iterator(); sourcesIt.hasNext();) {
              Value source = sourcesIt.next();
              handleFlowsToDataStructure(ie.getArg(param.getIndex()), source);
            }
          }
        } else if (sink instanceof StaticFieldRef) {
          for (Iterator<Value> sourcesIt = sources.iterator(); sourcesIt.hasNext();) {
            Value source = sourcesIt.next();
            handleFlowsToValue(sink, source);
          }
        } else if (sink instanceof InstanceFieldRef && ie instanceof InstanceInvokeExpr) {
          InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
          if (iie.getBase() == thisLocal) {
            for (Iterator<Value> sourcesIt = sources.iterator(); sourcesIt.hasNext();) {
              Value source = sourcesIt.next();
              handleFlowsToValue(sink, source);
            }
          } else if (includeInnerFields) {
            for (Iterator<Value> sourcesIt = sources.iterator(); sourcesIt.hasNext();) {
              Value source = sourcesIt.next();

              if (false) // isNonRefType(sink.getType()) ) // TODO: double check this policy
              {
                // primitives flow to the parent object
                InstanceFieldRef ifr = (InstanceFieldRef) sink;
                if (ifr.getBase() instanceof FakeJimpleLocal) {
                  ; // handleFlowsToDataStructure(((FakeJimpleLocal) ifr.getBase()).getRealLocal(), source);
                } else {
                  handleFlowsToDataStructure(ifr.getBase(), source);
                }
              } else {
                // objects flow to the field
                handleFlowsToValue(sink, source);
              }

              handleInnerField(sink);
            }
          } else {
            for (Iterator<Value> sourcesIt = sources.iterator(); sourcesIt.hasNext();) {
              Value source = sourcesIt.next();
              handleFlowsToDataStructure(iie.getBase(), source);
            }
          }
        } else if (sink instanceof InstanceFieldRef && includeInnerFields) {
          for (Iterator<Value> sourcesIt = sources.iterator(); sourcesIt.hasNext();) {
            Value source = sourcesIt.next();
            if (false) // isNonRefType(sink.getType()) ) // TODO: double check this policy
            {
              // primitives flow to the parent object
              InstanceFieldRef ifr = (InstanceFieldRef) sink;
              if (ifr.getBase() instanceof FakeJimpleLocal) {
                ; // handleFlowsToDataStructure(((FakeJimpleLocal) ifr.getBase()).getRealLocal(), source);
              } else {
                handleFlowsToDataStructure(ifr.getBase(), source);
              }
            } else {
              handleFlowsToValue(sink, source);
            }

            handleInnerField(sink);
          }
        }
      }
    }

    // return the list of return value sources
    return returnValueSources;
  }

  protected void addFlowToCdfg(Stmt stmt) {
    if (stmt instanceof IdentityStmt) // assigns an IdentityRef to a Local
    {
      IdentityStmt is = (IdentityStmt) stmt;
      IdentityRef ir = (IdentityRef) is.getRightOp();

      if (ir instanceof JCaughtExceptionRef) {
        // TODO: What the heck do we do with this???
      } else if (ir instanceof ParameterRef) {
        if (!ignoreThisDataType(ir.getType())) {
          // <Local, ParameterRef and sources>
          handleFlowsToValue(is.getLeftOp(), ir);
        }
      } else if (ir instanceof ThisRef) {
        if (!ignoreThisDataType(ir.getType())) {
          // <Local, ThisRef and sources>
          handleFlowsToValue(is.getLeftOp(), ir);
        }
      }
    } else if (stmt instanceof ReturnStmt) // assigns an Immediate to the "returnRef"
    {
      ReturnStmt rs = (ReturnStmt) stmt;
      Value rv = rs.getOp();
      if (rv instanceof Constant) {
        // No (interesting) data flow
      } else if (rv instanceof Local) {
        if (!ignoreThisDataType(rv.getType())) {
          // <ReturnRef, sources of Local>
          handleFlowsToValue(returnRef, rv);
        }
      }
    } else if (stmt instanceof AssignStmt) // assigns a Value to a Variable
    {
      AssignStmt as = (AssignStmt) stmt;
      Value lv = as.getLeftOp();
      Value rv = as.getRightOp();

      Value sink = null;
      boolean flowsToDataStructure = false;
      if (lv instanceof Local) // data flows into the Local
      {
        sink = lv;
      } else if (lv instanceof ArrayRef) // data flows into the base's data structure
      {
        ArrayRef ar = (ArrayRef) lv;
        sink = ar.getBase();
        flowsToDataStructure = true;
      } else if (lv instanceof StaticFieldRef) // data flows into the field ref
      {
        sink = lv;
      } else if (lv instanceof InstanceFieldRef) {
        InstanceFieldRef ifr = (InstanceFieldRef) lv;
        if (ifr.getBase() == thisLocal) // data flows into the field ref
        {
          sink = lv;
        } else if (includeInnerFields) {
          if (false) // isNonRefType(lv.getType()) ) // TODO: double check this policy
          {
            // primitives flow to the parent object
            sink = ifr.getBase();
            flowsToDataStructure = true;
          } else {
            // objects flow to the field
            sink = lv;
            handleInnerField(sink);
          }
        } else // data flows into the base's data structure
        {
          sink = ifr.getBase();
          flowsToDataStructure = true;
        }
      }

      List sources = new ArrayList();
      boolean interestingFlow = true;

      if (rv instanceof Local) {
        sources.add(rv);
        interestingFlow = !ignoreThisDataType(rv.getType());
      } else if (rv instanceof Constant) {
        sources.add(rv);
        interestingFlow = !ignoreThisDataType(rv.getType());
      } else if (rv instanceof ArrayRef) // data flows from the base's data structure
      {
        ArrayRef ar = (ArrayRef) rv;
        sources.add(ar.getBase());
        interestingFlow = !ignoreThisDataType(ar.getType());
      } else if (rv instanceof StaticFieldRef) {
        sources.add(rv);
        interestingFlow = !ignoreThisDataType(rv.getType());
      } else if (rv instanceof InstanceFieldRef) {
        InstanceFieldRef ifr = (InstanceFieldRef) rv;
        if (ifr.getBase() == thisLocal) // data flows from the field ref
        {
          sources.add(rv);
          interestingFlow = !ignoreThisDataType(rv.getType());
        } else if (includeInnerFields) {
          if (false) // isNonRefType(rv.getType()) ) // TODO: double check this policy
          {
            // primitives flow from the parent object
            sources.add(ifr.getBase());
          } else {
            // objects flow from both
            sources.add(ifr.getBase());
            sources.add(rv);
            handleInnerField(rv);
          }
          interestingFlow = !ignoreThisDataType(rv.getType());
        } else // data flows from the base's data structure
        {
          sources.add(ifr.getBase());
          interestingFlow = !ignoreThisDataType(ifr.getType());
        }
      } else if (rv instanceof AnyNewExpr) {
        sources.add(rv);
        interestingFlow = !ignoreThisDataType(rv.getType());
      } else if (rv instanceof BinopExpr) // does this include compares and others??? yes
      {
        BinopExpr be = (BinopExpr) rv;
        sources.add(be.getOp1());
        sources.add(be.getOp2());
        interestingFlow = !ignoreThisDataType(be.getType());
      } else if (rv instanceof CastExpr) {
        CastExpr ce = (CastExpr) rv;
        sources.add(ce.getOp());
        interestingFlow = !ignoreThisDataType(ce.getType());
      } else if (rv instanceof InstanceOfExpr) {
        InstanceOfExpr ioe = (InstanceOfExpr) rv;
        sources.add(ioe.getOp());
        interestingFlow = !ignoreThisDataType(ioe.getType());
      } else if (rv instanceof UnopExpr) {
        UnopExpr ue = (UnopExpr) rv;
        sources.add(ue.getOp());
        interestingFlow = !ignoreThisDataType(ue.getType());
      } else if (rv instanceof InvokeExpr) {
        InvokeExpr ie = (InvokeExpr) rv;
        sources.addAll(handleInvokeExpr(ie, as));
        interestingFlow = !ignoreThisDataType(ie.getType());
      }

      if (interestingFlow) {
        if (flowsToDataStructure) {
          Iterator<Value> sourcesIt = sources.iterator();
          while (sourcesIt.hasNext()) {
            Value source = sourcesIt.next();
            handleFlowsToDataStructure(sink, source);
          }
        } else {
          Iterator<Value> sourcesIt = sources.iterator();
          while (sourcesIt.hasNext()) {
            Value source = sourcesIt.next();
            // if(flowsToBoth && sink instanceof InstanceFieldRef)
            // handleFlowsToDataStructure(((InstanceFieldRef)sink).getBase(), source);
            handleFlowsToValue(sink, source);
          }
        }
      }
    } else if (stmt.containsInvokeExpr()) // flows data between receiver object, parameters, globals, and return value
    {
      handleInvokeExpr(stmt.getInvokeExpr(), stmt);
    }
  }

  public Value getThisLocal() {
    return thisLocal;
  }
}
