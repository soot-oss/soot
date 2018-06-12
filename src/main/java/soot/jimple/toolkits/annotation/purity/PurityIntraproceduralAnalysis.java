package soot.jimple.toolkits.annotation.purity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Antoine Mine
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Local;
import soot.RefLikeType;
import soot.SourceLocator;
import soot.Unit;
import soot.Value;
import soot.jimple.AnyNewExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.BreakpointStmt;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.Constant;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.MonitorStmt;
import soot.jimple.NopStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

/**
 * Intra-procedural purity-graph analysis.
 *
 * You must pass an {@link AbstractInterproceduralAnalysis} object so that the intraprocedural part can resolve the effect of
 * method calls.
 */
public class PurityIntraproceduralAnalysis extends ForwardFlowAnalysis<Unit, PurityGraphBox> {
  private static final Logger logger = LoggerFactory.getLogger(PurityIntraproceduralAnalysis.class);

  AbstractInterproceduralAnalysis<PurityGraphBox> inter;

  @Override
  protected PurityGraphBox newInitialFlow() {
    return new PurityGraphBox();
  }

  @Override
  protected PurityGraphBox entryInitialFlow() {
    return new PurityGraphBox();
  }

  @Override
  protected void merge(PurityGraphBox in1, PurityGraphBox in2, PurityGraphBox out) {
    if (out != in1) {
      out.g = new PurityGraph(in1.g);
    }
    out.g.union(in2.g);
  }

  @Override
  protected void copy(PurityGraphBox source, PurityGraphBox dest) {
    dest.g = new PurityGraph(source.g);
  }

  @Override
  protected void flowThrough(PurityGraphBox inValue, Unit unit, PurityGraphBox outValue) {
    Stmt stmt = (Stmt) unit;

    outValue.g = new PurityGraph(inValue.g);

    // ********************
    // BIG PATTERN MATCHING
    // ********************
    // I throw much "match failure" Errors to ease debugging...
    // => we could optimize the pattern matching a little bit

    // logger.debug(" | |- exec "+stmt);

    ///////////
    // Calls //
    ///////////
    if (stmt.containsInvokeExpr()) {
      inter.analyseCall(inValue, stmt, outValue);
    }

    /////////////
    // AssignStmt
    /////////////
    else if (stmt instanceof AssignStmt) {
      Value leftOp = ((AssignStmt) stmt).getLeftOp();
      Value rightOp = ((AssignStmt) stmt).getRightOp();

      // v = ...
      if (leftOp instanceof Local) {
        Local left = (Local) leftOp;

        // remove optional cast
        if (rightOp instanceof CastExpr) {
          rightOp = ((CastExpr) rightOp).getOp();
        }

        // ignore primitive types
        if (!(left.getType() instanceof RefLikeType)) {
        }

        // v = v
        else if (rightOp instanceof Local) {
          Local right = (Local) rightOp;
          outValue.g.assignLocalToLocal(right, left);
        }

        // v = v[i]
        else if (rightOp instanceof ArrayRef) {
          Local right = (Local) ((ArrayRef) rightOp).getBase();
          outValue.g.assignFieldToLocal(stmt, right, "[]", left);
        }

        // v = v.f
        else if (rightOp instanceof InstanceFieldRef) {
          Local right = (Local) ((InstanceFieldRef) rightOp).getBase();
          String field = ((InstanceFieldRef) rightOp).getField().getName();
          outValue.g.assignFieldToLocal(stmt, right, field, left);
        }

        // v = C.f
        else if (rightOp instanceof StaticFieldRef) {
          outValue.g.localIsUnknown(left);
        }

        // v = cst
        else if (rightOp instanceof Constant) {
          // do nothing...
        }

        // v = new / newarray / newmultiarray
        else if (rightOp instanceof AnyNewExpr) {
          outValue.g.assignNewToLocal(stmt, left);
        }

        // v = binary or unary operator
        else if (rightOp instanceof BinopExpr || rightOp instanceof UnopExpr || rightOp instanceof InstanceOfExpr) {
          // do nothing...
        } else {
          throw new Error("AssignStmt match failure (rightOp)" + stmt);
        }
      }

      // v[i] = ...
      else if (leftOp instanceof ArrayRef) {
        Local left = (Local) ((ArrayRef) leftOp).getBase();

        // v[i] = v
        if (rightOp instanceof Local) {
          Local right = (Local) rightOp;
          if (right.getType() instanceof RefLikeType) {
            outValue.g.assignLocalToField(right, left, "[]");
          } else {
            outValue.g.mutateField(left, "[]");
          }
        }

        // v[i] = cst
        else if (rightOp instanceof Constant) {
          outValue.g.mutateField(left, "[]");
        } else {
          throw new Error("AssignStmt match failure (rightOp)" + stmt);
        }
      }

      // v.f = ...
      else if (leftOp instanceof InstanceFieldRef) {
        Local left = (Local) ((InstanceFieldRef) leftOp).getBase();
        String field = ((InstanceFieldRef) leftOp).getField().getName();

        // v.f = v
        if (rightOp instanceof Local) {
          Local right = (Local) rightOp;
          // ignore primitive types
          if (right.getType() instanceof RefLikeType) {
            outValue.g.assignLocalToField(right, left, field);
          } else {
            outValue.g.mutateField(left, field);
          }
        }

        // v.f = cst
        else if (rightOp instanceof Constant) {
          outValue.g.mutateField(left, field);
        } else {
          throw new Error("AssignStmt match failure (rightOp) " + stmt);
        }
      }

      // C.f = ...
      else if (leftOp instanceof StaticFieldRef) {
        String field = ((StaticFieldRef) leftOp).getField().getName();

        // C.f = v
        if (rightOp instanceof Local) {
          Local right = (Local) rightOp;
          if (right.getType() instanceof RefLikeType) {
            outValue.g.assignLocalToStaticField(right, field);
          } else {
            outValue.g.mutateStaticField(field);
          }
        }

        // C.f = cst
        else if (rightOp instanceof Constant) {
          outValue.g.mutateStaticField(field);
        } else {
          throw new Error("AssignStmt match failure (rightOp) " + stmt);
        }
      } else {
        throw new Error("AssignStmt match failure (leftOp) " + stmt);
      }
    }

    ///////////////
    // IdentityStmt
    ///////////////
    else if (stmt instanceof IdentityStmt) {
      Local left = (Local) ((IdentityStmt) stmt).getLeftOp();
      Value rightOp = ((IdentityStmt) stmt).getRightOp();

      if (rightOp instanceof ThisRef) {
        outValue.g.assignThisToLocal(left);
      } else if (rightOp instanceof ParameterRef) {
        ParameterRef p = (ParameterRef) rightOp;
        // ignore primitive types
        if (p.getType() instanceof RefLikeType) {
          outValue.g.assignParamToLocal(p.getIndex(), left);
        }
      } else if (rightOp instanceof CaughtExceptionRef) {
        // local = exception
        outValue.g.localIsUnknown(left);
      } else {
        throw new Error("IdentityStmt match failure (rightOp) " + stmt);
      }
    }

    ////////////
    // ThrowStmt
    ////////////
    else if (stmt instanceof ThrowStmt) {
      Value op = ((ThrowStmt) stmt).getOp();

      if (op instanceof Local) {
        Local v = (Local) op;
        outValue.g.localEscapes(v);
      } else if (op instanceof Constant) {
        // do nothing...
      } else {
        throw new Error("ThrowStmt match failure " + stmt);
      }
    }

    /////////////
    // ReturnStmt
    /////////////
    else if (stmt instanceof ReturnVoidStmt) {
      // do nothing...
    } else if (stmt instanceof ReturnStmt) {
      Value v = ((ReturnStmt) stmt).getOp();

      if (v instanceof Local) {
        // ignore primitive types
        if (v.getType() instanceof RefLikeType) {
          outValue.g.returnLocal((Local) v);
        }
      } else if (v instanceof Constant) {
        // do nothing...
      } else {
        throw new Error("ReturnStmt match failure " + stmt);
      }

    }

    //////////
    // ignored
    //////////
    else if (stmt instanceof IfStmt || stmt instanceof GotoStmt || stmt instanceof LookupSwitchStmt
        || stmt instanceof TableSwitchStmt || stmt instanceof MonitorStmt || stmt instanceof BreakpointStmt
        || stmt instanceof NopStmt) {
      // do nothing...
    } else {
      throw new Error("Stmt match faliure " + stmt);
    }

    // outValue.g.updateStat();
  }

  /**
   * Draw the result of the intra-procedural analysis as one big dot file, named className.methodName.dot, containing one
   * purity graph for each statement in the method.
   *
   * @param prefix
   * @param name
   */
  public void drawAsOneDot(String prefix, String name) {
    DotGraph dot = new DotGraph(name);
    dot.setGraphLabel(name);
    dot.setGraphAttribute("compound", "true");
    dot.setGraphAttribute("rankdir", "LR");
    Map<Unit, Integer> node = new HashMap<Unit, Integer>();
    int id = 0;
    for (Unit stmt : graph) {
      PurityGraphBox ref = getFlowAfter(stmt);
      DotGraph sub = dot.createSubGraph("cluster" + id);
      DotGraphNode label = sub.drawNode("head" + id);
      String lbl = stmt.toString();
      if (lbl.startsWith("lookupswitch")) {
        lbl = "lookupswitch...";
      }
      if (lbl.startsWith("tableswitch")) {
        lbl = "tableswitch...";
      }
      sub.setGraphLabel(" ");
      label.setLabel(lbl);
      label.setAttribute("fontsize", "18");
      label.setShape("box");
      ref.g.fillDotGraph("X" + id, sub);
      node.put(stmt, id);
      id++;
    }
    for (Unit src : graph) {
      for (Unit dst : graph.getSuccsOf(src)) {
        DotGraphEdge edge = dot.drawEdge("head" + node.get(src), "head" + node.get(dst));
        edge.setAttribute("ltail", "cluster" + node.get(src));
        edge.setAttribute("lhead", "cluster" + node.get(dst));
      }
    }

    File f = new File(SourceLocator.v().getOutputDir(), prefix + name + DotGraph.DOT_EXTENSION);
    dot.plot(f.getPath());
  }

  /**
   * Put into dst the purity graph obtained by merging all purity graphs at the method return. It is a valid summary that can
   * be used in methodCall if you do interprocedural analysis.
   *
   * @param dst
   */
  public void copyResult(PurityGraphBox dst) {
    PurityGraph r = new PurityGraph();
    for (Unit u : graph.getTails()) {
      r.union(getFlowAfter(u).g);
    }
    r.removeLocals();
    // r.simplifyLoad();
    // r.simplifyInside();
    // r.updateStat();
    dst.g = r;
  }

  /**
   * Perform purity analysis on the Jimple unit graph g, as part of a larger interprocedural analysis. Once constructed, you
   * may call copyResult and drawAsOneDot to query the analysis result.
   */
  PurityIntraproceduralAnalysis(UnitGraph g, AbstractInterproceduralAnalysis<PurityGraphBox> inter) {
    super(g);
    this.inter = inter;
    doAnalysis();
  }
}
