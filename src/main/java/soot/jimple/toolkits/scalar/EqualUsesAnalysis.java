package soot.jimple.toolkits.scalar;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.EquivalentValue;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

// EqualUsesAnalysis written by Richard L. Halpert, 2006-12-04
// Determines if a set of uses of locals all use the same value
// whenever they occur together.  Can accept a set of boundary
// statements which define a region which, if exited, counts
//
// The locals being used need not be the same

/**
 * @deprecated This class is buggy. Please use soot.jimple.toolkits.pointer.LocalMustAliasAnalysis instead.
 */
@Deprecated
public class EqualUsesAnalysis extends ForwardFlowAnalysis<Unit, FlowSet> {
  private static final Logger logger = LoggerFactory.getLogger(EqualUsesAnalysis.class);
  // Provided by client
  Map<Stmt, Local> stmtToLocal;
  Set<Stmt> useStmts;
  Collection<Local> useLocals;
  List boundaryStmts;

  // Calculated by flow analysis
  List<Stmt> redefStmts;
  Map<Stmt, List> firstUseToAliasSet;

  EqualLocalsAnalysis el;

  public EqualUsesAnalysis(UnitGraph g) {
    super(g);

    useStmts = null;
    useLocals = null;
    boundaryStmts = null;

    redefStmts = null;
    firstUseToAliasSet = null;

    // analysis is done on-demand, not now

    this.el = new EqualLocalsAnalysis(g); // also on-demand
  }

  public boolean areEqualUses(Stmt firstStmt, Local firstLocal, Stmt secondStmt, Local secondLocal) {
    Map<Stmt, Local> stmtToLocal = new HashMap<Stmt, Local>();
    stmtToLocal.put(firstStmt, firstLocal);
    stmtToLocal.put(secondStmt, secondLocal);
    return areEqualUses(stmtToLocal, new ArrayList());
  }

  public boolean areEqualUses(Stmt firstStmt, Local firstLocal, Stmt secondStmt, Local secondLocal, List boundaryStmts) {
    Map<Stmt, Local> stmtToLocal = new HashMap<Stmt, Local>();
    stmtToLocal.put(firstStmt, firstLocal);
    stmtToLocal.put(secondStmt, secondLocal);
    return areEqualUses(stmtToLocal, boundaryStmts);
  }

  public boolean areEqualUses(Map<Stmt, Local> stmtToLocal) {
    return areEqualUses(stmtToLocal, new ArrayList());
  }

  public boolean areEqualUses(Map<Stmt, Local> stmtToLocal, List boundaryStmts) { // You may optionally specify start and end
                                                                                  // statements... for if
                                                                                  // you're interested only in a certain part
                                                                                  // of the method
    this.stmtToLocal = stmtToLocal;
    this.useStmts = stmtToLocal.keySet();
    this.useLocals = stmtToLocal.values();
    this.boundaryStmts = boundaryStmts;
    this.redefStmts = new ArrayList<Stmt>();
    this.firstUseToAliasSet = new HashMap<Stmt, List>();

    // logger.debug("Checking for Locals " + useLocals + " in these statements: " + useStmts);

    doAnalysis();

    // If any redefinition reaches any use statement, return false
    Iterator<Stmt> useIt = useStmts.iterator();
    while (useIt.hasNext()) {
      Unit u = useIt.next();
      FlowSet fs = (FlowSet) getFlowBefore(u);
      Iterator<Stmt> redefIt = redefStmts.iterator();
      while (redefIt.hasNext()) {
        if (fs.contains(redefIt.next())) {
          // logger.debug("LIF = false ");
          return false;
        }
      }
      List aliases = null;
      Iterator fsIt = fs.iterator();
      while (fsIt.hasNext()) {
        Object o = fsIt.next();
        if (o instanceof List) {
          aliases = (List) o;
        }
      }
      if (aliases != null && !aliases.contains(new EquivalentValue(stmtToLocal.get(u)))) {
        // logger.debug("LIF = false ");
        return false;
      }
    }
    // logger.debug("LIF = true ");
    return true;
  }

  public Map<Stmt, List> getFirstUseToAliasSet() {
    return firstUseToAliasSet;
  }

  protected void merge(FlowSet inSet1, FlowSet inSet2, FlowSet outSet) {

    inSet1.union(inSet2, outSet);
    List aliases1 = null;
    List aliases2 = null;
    Iterator outIt = outSet.iterator();
    while (outIt.hasNext()) {
      Object o = outIt.next();
      if (o instanceof List) {
        if (aliases1 == null) {
          aliases1 = (List) o;
        } else {
          aliases2 = (List) o;
        }
      }
    }
    if (aliases1 != null && aliases2 != null) {
      outSet.remove(aliases2);
      Iterator aliasIt = aliases1.iterator();
      while (aliasIt.hasNext()) {
        Object o = aliasIt.next();
        if (!aliases2.contains(o)) {
          aliasIt.remove();
        }
      }
    }
  }

  protected void flowThrough(FlowSet in, Unit unit, FlowSet out) {
    Stmt stmt = (Stmt) unit;

    in.copy(out);

    // get list of definitions at this unit
    List<Value> newDefs = new ArrayList<Value>();
    Iterator<ValueBox> newDefBoxesIt = stmt.getDefBoxes().iterator();
    while (newDefBoxesIt.hasNext()) {
      newDefs.add(((ValueBox) newDefBoxesIt.next()).getValue());
    }

    // check if any locals of interest were redefined here
    Iterator<Local> useLocalsIt = useLocals.iterator();
    while (useLocalsIt.hasNext()) {
      Local useLocal = useLocalsIt.next();
      if (newDefs.contains(useLocal)) // if a relevant local was (re)def'd here
      {
        Iterator<?> outIt = out.iterator();
        while (outIt.hasNext()) {
          Object o = outIt.next();
          if (o instanceof Stmt) {
            Stmt s = (Stmt) o;
            if (stmtToLocal.get(s) == useLocal) {
              redefStmts.add(stmt); // mark this as an active redef stmt
            }
          }
        }
      }
    }

    // if this is a redefinition statement, flow it forwards
    if (redefStmts.contains(stmt)) {
      out.add(stmt);
    }

    // if this is a boundary statement, clear everything but aliases from the flow set
    if (boundaryStmts.contains(stmt)) {
      // find the alias entry in the flow set
      /*
       * List aliases = null; Iterator outIt = out.iterator(); while(outIt.hasNext()) { Object o = outIt.next(); if( o
       * instanceof List ) aliases = (List) o; }
       */
      // clear the flow set, and add aliases back in
      out.clear();
      // if(aliases != null)
      // out.add(aliases);
    }

    // if this is a use statement (of interest), flow it forward
    // if it's the first use statement, get an alias list
    if (useStmts.contains(stmt)) {
      if (out.size() == 0) {
        // Add a list of aliases to the used value
        Local l = stmtToLocal.get(stmt);
        List aliasList = el.getCopiesOfAt(l, stmt);
        if (aliasList.size() == 0) {
          aliasList.add(l); // covers the case of this or a parameter, where getCopiesOfAt doesn't seem to work right now
        }
        List newAliasList = new ArrayList();
        newAliasList.addAll(aliasList);
        firstUseToAliasSet.put(stmt, newAliasList);
        // logger.debug("Aliases of " + l + " at " + stmt + " are " + aliasList);
        out.add(aliasList);
      }
      out.add(stmt);
    }

    // update the alias list if this is a definition statement
    if (stmt instanceof DefinitionStmt) {
      List<EquivalentValue> aliases = null;
      Iterator outIt = out.iterator();
      while (outIt.hasNext()) {
        Object o = outIt.next();
        if (o instanceof List) {
          aliases = (List<EquivalentValue>) o;
        }
      }
      if (aliases != null) {
        if (aliases.contains(new EquivalentValue(((DefinitionStmt) stmt).getRightOp()))) {
          Iterator<Value> newDefsIt = newDefs.iterator();
          while (newDefsIt.hasNext()) {
            aliases.add(new EquivalentValue((Value) newDefsIt.next()));
          }
        } else {
          Iterator<Value> newDefsIt = newDefs.iterator();
          while (newDefsIt.hasNext()) {
            aliases.remove(new EquivalentValue((Value) newDefsIt.next()));
          }
        }
      }
    }

  }

  protected void copy(FlowSet source, FlowSet dest) {
    source.copy(dest);
  }

  protected FlowSet entryInitialFlow() {
    return new ArraySparseSet();
  }

  protected FlowSet newInitialFlow() {
    return new ArraySparseSet();
  }
}
