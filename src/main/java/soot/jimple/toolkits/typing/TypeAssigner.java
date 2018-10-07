package soot.jimple.toolkits.typing;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2000 Etienne Gagnon.
 * Copyright (C) 2008 Ben Bellamy
 * Copyright (C) 2008 Eric Bodden
 *
 * All rights reserved.
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.ByteType;
import soot.CharType;
import soot.ErroneousType;
import soot.G;
import soot.Local;
import soot.NullType;
import soot.PhaseOptions;
import soot.Scene;
import soot.ShortType;
import soot.Singletons;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.options.JBTROptions;
import soot.options.Options;
import soot.toolkits.scalar.UnusedLocalEliminator;

/**
 * This transformer assigns types to local variables.
 *
 * @author Etienne Gagnon
 * @author Ben Bellamy
 * @author Eric Bodden
 */
public class TypeAssigner extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(TypeAssigner.class);

  public TypeAssigner(Singletons.Global g) {
  }

  public static TypeAssigner v() {
    return G.v().soot_jimple_toolkits_typing_TypeAssigner();
  }

  /** Assign types to local variables. * */
  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    if (b == null) {
      throw new NullPointerException();
    }

    Date start = new Date();

    if (Options.v().verbose()) {
      logger.debug("[TypeAssigner] typing system started on " + start);
    }

    JBTROptions opt = new JBTROptions(options);

    /*
     * Setting this guard to true enables comparison of the original and new type assigners. This will be slow since type
     * assignment will always happen twice. The actual types used for Jimple are determined by the use-old-type-assigner
     * option.
     *
     * Each comparison is written as a separate semicolon-delimited line to the standard output, and the first field is
     * always 'cmp' for use in grep. The format is:
     *
     * cmp;Method Name;Stmt Count;Old Inference Time (ms); New Inference Time (ms);Typing Comparison
     *
     * The Typing Comparison field compares the old and new typings: -2 - Old typing contains fewer variables (BAD!) -1 - Old
     * typing is tighter (BAD!) 0 - Typings are equal 1 - New typing is tighter 2 - New typing contains fewer variables 3 -
     * Typings are incomparable (inspect manually)
     *
     * In a final release this guard, and anything in the first branch, would probably be removed.
     */
    if (opt.compare_type_assigners()) {
      compareTypeAssigners(b, opt.use_older_type_assigner());
    } else {
      if (opt.use_older_type_assigner()) {
        TypeResolver.resolve((JimpleBody) b, Scene.v());
      } else {
        (new soot.jimple.toolkits.typing.fast.TypeResolver((JimpleBody) b)).inferTypes();
      }
    }

    Date finish = new Date();
    if (Options.v().verbose()) {
      long runtime = finish.getTime() - start.getTime();
      long mins = runtime / 60000;
      long secs = (runtime % 60000) / 1000;
      logger.debug("[TypeAssigner] typing system ended. It took " + mins + " mins and " + secs + " secs.");
    }

    if (!opt.ignore_nullpointer_dereferences()) {
      replaceNullType(b);
    }

    if (typingFailed((JimpleBody) b)) {
      throw new RuntimeException("type inference failed!");
    }
  }

  /**
   * Replace statements using locals with null_type type and that would throw a NullPointerException at runtime by a set of
   * instructions throwing a NullPointerException.
   *
   * This is done to remove locals with null_type type.
   *
   * @param b
   */
  private void replaceNullType(Body b) {
    List<Local> localsToRemove = new ArrayList<Local>();
    boolean hasNullType = false;

    // check if any local has null_type
    for (Local l : b.getLocals()) {
      if (l.getType() instanceof NullType) {
        localsToRemove.add(l);
        hasNullType = true;
      }
    }

    // No local with null_type
    if (!hasNullType) {
      return;
    }

    // force to propagate null constants
    Map<String, String> opts = PhaseOptions.v().getPhaseOptions("jop.cpf");
    if (!opts.containsKey("enabled") || !opts.get("enabled").equals("true")) {
      logger.warn("Cannot run TypeAssigner.replaceNullType(Body). Try to enable jop.cfg.");
      return;
    }
    ConstantPropagatorAndFolder.v().transform(b);

    List<Unit> unitToReplaceByException = new ArrayList<Unit>();
    for (Unit u : b.getUnits()) {
      for (ValueBox vb : u.getUseBoxes()) {
        if (vb.getValue() instanceof Local && ((Local) vb.getValue()).getType() instanceof NullType) {

          Local l = (Local) vb.getValue();
          Stmt s = (Stmt) u;

          boolean replace = false;
          if (s.containsArrayRef()) {
            ArrayRef r = s.getArrayRef();
            if (r.getBase() == l) {
              replace = true;
            }
          } else if (s.containsFieldRef()) {
            FieldRef r = s.getFieldRef();
            if (r instanceof InstanceFieldRef) {
              InstanceFieldRef ir = (InstanceFieldRef) r;
              if (ir.getBase() == l) {
                replace = true;
              }
            }
          } else if (s.containsInvokeExpr()) {
            InvokeExpr ie = s.getInvokeExpr();
            if (ie instanceof InstanceInvokeExpr) {
              InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
              if (iie.getBase() == l) {
                replace = true;
              }
            }
          }

          if (replace) {
            unitToReplaceByException.add(u);
          }
        }
      }
    }

    for (Unit u : unitToReplaceByException) {
      soot.dexpler.Util.addExceptionAfterUnit(b, "java.lang.NullPointerException", u,
          "This statement would have triggered an Exception: " + u);
      b.getUnits().remove(u);
    }

    // should be done on a separate phase
    DeadAssignmentEliminator.v().transform(b);
    UnusedLocalEliminator.v().transform(b);

  }

  private void compareTypeAssigners(Body b, boolean useOlderTypeAssigner) {
    JimpleBody jb = (JimpleBody) b, oldJb, newJb;
    int size = jb.getUnits().size();
    long oldTime, newTime;
    if (useOlderTypeAssigner) {
      // Use old type assigner last
      newJb = (JimpleBody) jb.clone();
      newTime = System.currentTimeMillis();
      (new soot.jimple.toolkits.typing.fast.TypeResolver(newJb)).inferTypes();
      newTime = System.currentTimeMillis() - newTime;
      oldTime = System.currentTimeMillis();
      TypeResolver.resolve(jb, Scene.v());
      oldTime = System.currentTimeMillis() - oldTime;
      oldJb = jb;
    } else {
      // Use new type assigner last
      oldJb = (JimpleBody) jb.clone();
      oldTime = System.currentTimeMillis();
      TypeResolver.resolve(oldJb, Scene.v());
      oldTime = System.currentTimeMillis() - oldTime;
      newTime = System.currentTimeMillis();
      (new soot.jimple.toolkits.typing.fast.TypeResolver(jb)).inferTypes();
      newTime = System.currentTimeMillis() - newTime;
      newJb = jb;
    }

    int cmp;
    if (newJb.getLocals().size() < oldJb.getLocals().size()) {
      cmp = 2;
    } else if (newJb.getLocals().size() > oldJb.getLocals().size()) {
      cmp = -2;
    } else {
      cmp = compareTypings(oldJb, newJb);
    }

    logger.debug("cmp;" + jb.getMethod() + ";" + size + ";" + oldTime + ";" + newTime + ";" + cmp);
  }

  private boolean typingFailed(JimpleBody b) {
    // Check to see if any locals are untyped
    {
      Iterator<Local> localIt = b.getLocals().iterator();

      final UnknownType unknownType = UnknownType.v();
      final ErroneousType errornousType = ErroneousType.v();
      while (localIt.hasNext()) {
        Local l = localIt.next();

        if (l.getType().equals(unknownType) || l.getType().equals(errornousType)) {
          return true;
        }
      }
    }

    return false;
  }

  /* Returns -1 if a < b, +1 if b < a, 0 if a = b and 3 otherwise. */
  private static int compareTypings(JimpleBody a, JimpleBody b) {
    int r = 0;

    Iterator<Local> ib = b.getLocals().iterator();
    for (Local v : a.getLocals()) {
      Type ta = v.getType(), tb = ib.next().getType();

      if (soot.jimple.toolkits.typing.fast.TypeResolver.typesEqual(ta, tb)) {
        continue;
      } else if (true && ((ta instanceof CharType && (tb instanceof ByteType || tb instanceof ShortType))
          || (tb instanceof CharType && (ta instanceof ByteType || ta instanceof ShortType)))) {
        continue;
      } else if (soot.jimple.toolkits.typing.fast.AugHierarchy.ancestor_(ta, tb)) {
        if (r == -1) {
          return 3;
        } else {
          r = 1;
        }
      } else if (soot.jimple.toolkits.typing.fast.AugHierarchy.ancestor_(tb, ta)) {
        if (r == 1) {
          return 3;
        } else {
          r = -1;
        }
      } else {
        return 3;
      }
    }

    return r;
  }
}
