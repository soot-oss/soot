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
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Body;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.IntConstant;
import soot.jimple.Stmt;
import soot.jimple.toolkits.annotation.tags.ArrayCheckTag;
import soot.options.ABCOptions;
import soot.options.Options;
import soot.tagkit.Tag;
import soot.util.Chain;

public class ArrayBoundsChecker extends SceneTransformer {
  private static final Logger logger = LoggerFactory.getLogger(ArrayBoundsChecker.class);
  private static final int UNSAFE_LOWER_UNSAFE_UPPER = 0;
  private static final int UNSAFE_LOWER_SAFE_UPPER = 1;
  private static final int SAFE_LOWER_UNSAFE_UPPER = 2;
  private static final int SAFE_LOWER_SAFE_UPPER = 3;

  public ArrayBoundsChecker(Singletons.Global g) {
  }

  public static ArrayBoundsChecker v() {
    return G.v().soot_jimple_toolkits_annotation_arraycheck_ArrayBoundsChecker();
  }

  protected boolean takeClassField = false;
  protected boolean takeFieldRef = false;
  protected boolean takeArrayRef = false;
  protected boolean takeCSE = false;
  protected boolean takeRectArray = false;
  private final boolean verbose = Options.v().verbose();

  public void transformBody(Body body, ABCOptions options) {
    long start = 0;
    final SootMethod m = body.getMethod();
    if (verbose) {
      start = System.currentTimeMillis();
      logger.debug("[abc] Analyzing array bounds information for " + m.getName());
    }

    {

      ArrayBoundsCheckerAnalysis analysis = null;

      if (hasArrayLocals(body)) {
        analysis = new ArrayBoundsCheckerAnalysis(body, takeClassField, takeFieldRef, takeArrayRef, takeCSE, takeRectArray);
      }

      Chain<Unit> units = body.getUnits();

      IntContainer zero = new IntContainer(0);

      Iterator<Unit> unitIt = units.snapshotIterator();

      while (unitIt.hasNext()) {
        Stmt stmt = (Stmt) unitIt.next();

        if (stmt.containsArrayRef()) {
          ArrayRef aref = stmt.getArrayRef();

          {
            WeightedDirectedSparseGraph vgraph = (WeightedDirectedSparseGraph) analysis.getFlowBefore(stmt);

            int res = interpretGraph(vgraph, aref, stmt, zero);

            boolean lowercheck = true;
            boolean uppercheck = true;

            if (res == UNSAFE_LOWER_UNSAFE_UPPER) {
              lowercheck = true;
              uppercheck = true;
            } else if (res == UNSAFE_LOWER_SAFE_UPPER) {
              lowercheck = true;
              uppercheck = false;
            } else if (res == SAFE_LOWER_UNSAFE_UPPER) {
              lowercheck = false;
              uppercheck = true;
            } else if (res == SAFE_LOWER_SAFE_UPPER) {
              lowercheck = false;
              uppercheck = false;
            }
            Tag checkTag = new ArrayCheckTag(lowercheck, uppercheck);
            stmt.addTag(checkTag);

          }

        }
      }
    }

    if (takeRectArray) {
      RectangularArrayFinder raf = RectangularArrayFinder.v();
      for (Iterator<ValueBox> vbIt = body.getUseAndDefBoxesIterator(); vbIt.hasNext();) {
        final ValueBox vb = vbIt.next();
        Value v = vb.getValue();
        if (!(v instanceof Local)) {
          continue;
        }
        Type t = v.getType();
        if (!(t instanceof ArrayType)) {
          continue;
        }
        ArrayType at = (ArrayType) t;
        if (at.numDimensions <= 1) {
          continue;
        }
        vb.addTag(IsRectangularTag.v(raf.isRectangular(new MethodLocal(m, (Local) v))));
      }
    }

    if (verbose) {
      long runtime = System.currentTimeMillis() - start;
      logger.debug("[abc] took " + (runtime / 60000) + " min. " + ((runtime % 60000) / 1000) + " sec.");
    }
  }

  private boolean hasArrayLocals(Body body) {
    Iterator<Local> localIt = body.getLocals().iterator();

    while (localIt.hasNext()) {
      Local local = localIt.next();
      if (local.getType() instanceof ArrayType) {
        return true;
      }
    }

    return false;
  }

  protected int interpretGraph(WeightedDirectedSparseGraph vgraph, ArrayRef aref, Stmt stmt, IntContainer zero) {

    boolean lowercheck = true;
    boolean uppercheck = true;

    {
      if (Options.v().debug()) {
        if (!vgraph.makeShortestPathGraph()) {
          logger.debug(stmt + " :");
          logger.debug(vgraph.toString());
        }
      }

      Value base = aref.getBase();
      Value index = aref.getIndex();

      if (index instanceof IntConstant) {
        int indexv = ((IntConstant) index).value;

        if (vgraph.hasEdge(base, zero)) {
          int alength = vgraph.edgeWeight(base, zero);

          if (-alength > indexv) {
            uppercheck = false;
          }
        }

        if (indexv >= 0) {
          lowercheck = false;
        }
      } else {
        if (vgraph.hasEdge(base, index)) {
          int upperdistance = vgraph.edgeWeight(base, index);
          if (upperdistance < 0) {
            uppercheck = false;
          }
        }

        if (vgraph.hasEdge(index, zero)) {
          int lowerdistance = vgraph.edgeWeight(index, zero);

          if (lowerdistance <= 0) {
            lowercheck = false;
          }
        }
      }
    }

    if (lowercheck && uppercheck) {
      return UNSAFE_LOWER_UNSAFE_UPPER;
    } else if (lowercheck && !uppercheck) {
      return UNSAFE_LOWER_SAFE_UPPER;
    } else if (!lowercheck && uppercheck) {
      return SAFE_LOWER_UNSAFE_UPPER;
    } else {
      return SAFE_LOWER_SAFE_UPPER;
    }
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> opts) {
    ABCOptions options = new ABCOptions(opts);
    if (options.with_all()) {
      takeClassField = true;
      takeFieldRef = true;
      takeArrayRef = true;
      takeCSE = true;
      takeRectArray = true;
    } else {
      takeClassField = options.with_classfield();
      takeFieldRef = options.with_fieldref();
      takeArrayRef = options.with_arrayref();
      takeCSE = options.with_cse();
      takeRectArray = options.with_rectarray();
    }

    for (SootClass sc : Scene.v().getClasses()) {
      for (SootMethod m : sc.getMethods()) {
        if (m.hasActiveBody()) {
          transformBody(m.getActiveBody(), options);
        }
      }
    }
  }
}
