package soot.jimple.toolkits.base;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Ondrej Lhotak
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
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.Chain;
import soot.util.HashMultiMap;
import soot.util.MultiMap;
import soot.util.PhaseDumper;

/**
 * This {@link BodyTransformer} pushes all {@link NewExpr} down the {@link Unit} {@link Chain} so that it is the statement
 * directly before the invoke of the {@code <init>} function.
 */
public class JimpleConstructorFolder extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(JimpleConstructorFolder.class);

  private static final boolean DEBUG_DUMP_BODIES = false;

  public JimpleConstructorFolder() {
  }

  static Value rhs(Stmt s) {
    return ((AssignStmt) s).getRightOp();
  }

  static Value lhs(Stmt s) {
    return ((AssignStmt) s).getLeftOp();
  }

  static Local rhsLocal(Stmt s) {
    return (Local) rhs(s);
  }

  static Local lhsLocal(Stmt s) {
    return (Local) lhs(s);
  }

  static boolean isNew(Stmt s) {
    return (s instanceof AssignStmt) && (rhs(s) instanceof NewExpr);
  }

  static boolean isConstructor(Stmt s) {
    if (s instanceof InvokeStmt) {
      InvokeExpr expr = ((InvokeStmt) s).getInvokeExpr();
      if (expr instanceof SpecialInvokeExpr) {
        SpecialInvokeExpr sie = (SpecialInvokeExpr) expr;
        return SootMethod.constructorName.equals(sie.getMethodRef().getName());
      }
    }
    return false;
  }

  static Local base(Stmt s) {
    InvokeStmt is = (InvokeStmt) s;
    InstanceInvokeExpr expr = (InstanceInvokeExpr) is.getInvokeExpr();
    return (Local) expr.getBase();
  }

  static void setBase(Stmt s, Local l) {
    InvokeStmt is = (InvokeStmt) s;
    InstanceInvokeExpr expr = (InstanceInvokeExpr) is.getInvokeExpr();
    expr.getBaseBox().setValue(l);
  }

  static boolean isCopy(Stmt s) {
    return (s instanceof AssignStmt) && (rhs(s) instanceof Local) && (lhs(s) instanceof Local);
  }

  private static class Fact {

    private Map<Local, Stmt> varToStmt = new HashMap<>();
    private MultiMap<Stmt, Local> stmtToVar = new HashMultiMap<>();
    private Stmt alloc = null;

    public void add(Local l, Stmt s) {
      varToStmt.put(l, s);
      stmtToVar.put(s, l);
    }

    public Stmt get(Local l) {
      return varToStmt.get(l);
    }

    public Set<Local> get(Stmt s) {
      return stmtToVar.get(s);
    }

    public void removeAll(Stmt s) {
      for (Local var : stmtToVar.get(s)) {
        varToStmt.remove(var);
      }
      stmtToVar.remove(s);
    }

    public Stmt alloc() {
      return alloc;
    }

    public void setAlloc(Stmt newAlloc) {
      alloc = newAlloc;
    }

    public void copyFrom(Fact in) {
      this.varToStmt = new HashMap<>(in.varToStmt);
      this.stmtToVar = new HashMultiMap<>(in.stmtToVar);
      this.alloc = in.alloc;
    }

    public void mergeFrom(Fact in1, Fact in2) {
      this.varToStmt = new HashMap<>();

      for (Map.Entry<Local, Stmt> e : in1.varToStmt.entrySet()) {
        Local l = e.getKey();
        Stmt newStmt = e.getValue();
        if (in2.varToStmt.containsKey(l) && !newStmt.equals(in2.varToStmt.get(l))) {
          throw new RuntimeException("Merge of different uninitialized values; are you sure this bytecode is verifiable?");
        }
        add(l, newStmt);
      }
      for (Map.Entry<Local, Stmt> e : in2.varToStmt.entrySet()) {
        add(e.getKey(), e.getValue());
      }

      Stmt alloc1 = in1.alloc;
      this.alloc = (alloc1 != null && alloc1.equals(in2.alloc)) ? alloc1 : null;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof Fact)) {
        return false;
      }
      Fact o = (Fact) other;
      if ((this.alloc == null && o.alloc != null) || (this.alloc != null && o.alloc == null)) {
        return false;
      }
      return (this.alloc == null || this.alloc.equals(o.alloc)) && this.stmtToVar.equals(o.stmtToVar);
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 89 * hash + Objects.hashCode(this.stmtToVar);
      hash = 89 * hash + Objects.hashCode(this.alloc);
      return hash;
    }
  }

  private class Analysis extends ForwardFlowAnalysis<Unit, Fact> {

    public Analysis(DirectedGraph<Unit> graph) {
      super(graph);
      doAnalysis();
    }

    @Override
    protected Fact newInitialFlow() {
      return new Fact();
    }

    @Override
    public void flowThrough(Fact in, Unit u, Fact out) {
      Stmt s = (Stmt) u;
      copy(in, out);
      out.setAlloc(null);

      if (isNew(s)) {
        out.add(lhsLocal(s), s);
      } else if (isCopy(s)) {
        Stmt newStmt = out.get(rhsLocal(s));
        if (newStmt != null) {
          out.add(lhsLocal(s), newStmt);
        }
      } else if (isConstructor(s)) {
        Stmt newStmt = out.get(base(s));
        if (newStmt != null) {
          out.removeAll(newStmt);
          out.setAlloc(newStmt);
        }
      }
    }

    @Override
    public void copy(Fact source, Fact dest) {
      dest.copyFrom(source);
    }

    @Override
    public void merge(Fact in1, Fact in2, Fact out) {
      out.mergeFrom(in1, in2);
    }
  }

  @Override
  public void internalTransform(Body b, String phaseName, Map<String, String> options) {
    final JimpleBody body = (JimpleBody) b;
    if (DEBUG_DUMP_BODIES) {
      PhaseDumper.v().dumpBody(body, "constructorfolder.in");
    }
    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Folding Jimple constructors...");
    }

    Analysis analysis = new Analysis(new BriefUnitGraph(body));

    final Chain<Unit> units = body.getUnits();
    for (Unit u : units) {
      Stmt s = (Stmt) u;
      if (isCopy(s) || isConstructor(s)) {
        continue;
      }
      Fact before = analysis.getFlowBefore(s);
      for (ValueBox usebox : s.getUseBoxes()) {
        Value value = usebox.getValue();
        if (value instanceof Local && before.get((Local) value) != null) {
          throw new RuntimeException(
              "Use of an unitialized value before constructor call; are you sure this bytecode is verifiable?\n " + s);
        }
      }
    }

    // throw out all new statements
    for (Iterator<Unit> it = units.snapshotIterator(); it.hasNext();) {
      Stmt s = (Stmt) it.next();
      if (isNew(s)) {
        units.remove(s);
      }
    }

    final Jimple jimp = Jimple.v();
    for (Iterator<Unit> it = units.snapshotIterator(); it.hasNext();) {
      Stmt s = (Stmt) it.next();

      // throw out copies of uninitialized variables
      if (isCopy(s)) {
        final Fact before = analysis.getFlowBefore(s);
        if (before.get(rhsLocal(s)) != null) {
          units.remove(s);
        }
      } else if (analysis.getFlowAfter(s).alloc() != null) {
        // insert the new just before the constructor
        final Fact before = analysis.getFlowBefore(s);
        final Local baseS = base(s);
        final Stmt newStmt = before.get(baseS);
        setBase(s, lhsLocal(newStmt));
        units.insertBefore(newStmt, s);

        // add necessary copies
        for (Local l : before.get(newStmt)) {
          if (!baseS.equals(l)) {
            units.insertAfter(jimp.newAssignStmt(l, baseS), s);
          }
        }
      }
    }

    if (DEBUG_DUMP_BODIES) {
      PhaseDumper.v().dumpBody(body, "constructorfolder.out");
    }
  }
}
