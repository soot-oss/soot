package soot.jimple.toolkits.annotation;

import java.util.ArrayList;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.tagkit.LineNumberTag;
import soot.util.Chain;

public class LineNumberAdder extends SceneTransformer {

  public LineNumberAdder(Singletons.Global g) {
  }

  public static LineNumberAdder v() {
    return G.v().soot_jimple_toolkits_annotation_LineNumberAdder();
  }

  @Override
  public void internalTransform(String phaseName, Map<String, String> opts) {
    // using a snapshot iterator because Application classes may change if LambdaMetaFactory translates
    // invokedynamic to new classes; no need to visit new classes
    for (Iterator<SootClass> it = Scene.v().getApplicationClasses().snapshotIterator(); it.hasNext();) {
      SootClass sc = it.next();
      // make map of first line to each method
      HashMap<Integer, SootMethod> lineToMeth = new HashMap<Integer, SootMethod>();
      for (SootMethod meth : new ArrayList<>(sc.getMethods())) {
        if (!meth.isConcrete()) {
          continue;
        }
        Chain<Unit> units = meth.retrieveActiveBody().getUnits();
        Unit s = units.getFirst();
        while (s instanceof IdentityStmt) {
          s = units.getSuccOf(s);
        }
        LineNumberTag tag = (LineNumberTag) s.getTag(LineNumberTag.NAME);
        if (tag != null) {
          lineToMeth.put(tag.getLineNumber(), meth);
        }
      }
      for (SootMethod meth : sc.getMethods()) {
        if (!meth.isConcrete()) {
          continue;
        }
        Chain<Unit> units = meth.retrieveActiveBody().getUnits();
        Unit s = units.getFirst();
        while (s instanceof IdentityStmt) {
          s = units.getSuccOf(s);
        }
        LineNumberTag tag = (LineNumberTag) s.getTag(LineNumberTag.NAME);
        if (tag != null) {
          int line_num = tag.getLineNumber() - 1;
          // already taken
          if (lineToMeth.containsKey(line_num)) {
            meth.addTag(new LineNumberTag(line_num + 1));
          } else {
            // still available - so use it for this meth
            meth.addTag(new LineNumberTag(line_num));
          }
        }
      }
    }
  }
}
