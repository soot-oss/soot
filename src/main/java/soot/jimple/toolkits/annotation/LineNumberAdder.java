package soot.jimple.toolkits.annotation;

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

import soot.Body;
import soot.G;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.IdentityStmt;
import soot.jimple.Stmt;
import soot.tagkit.LineNumberTag;

public class LineNumberAdder extends SceneTransformer {

  public LineNumberAdder(Singletons.Global g) {
  }

  public static LineNumberAdder v() {
    return G.v().soot_jimple_toolkits_annotation_LineNumberAdder();
  }

  public void internalTransform(String phaseName, Map opts) {

    Iterator it = Scene.v().getApplicationClasses().iterator();
    while (it.hasNext()) {
      SootClass sc = (SootClass) it.next();
      // make map of first line to each method
      HashMap<Integer, SootMethod> lineToMeth = new HashMap<Integer, SootMethod>();
      Iterator methIt = sc.getMethods().iterator();
      while (methIt.hasNext()) {
        SootMethod meth = (SootMethod) methIt.next();
        if (!meth.isConcrete()) {
          continue;
        }
        Body body = meth.retrieveActiveBody();
        Stmt s = (Stmt) body.getUnits().getFirst();
        while (s instanceof IdentityStmt) {
          s = (Stmt) body.getUnits().getSuccOf(s);
        }
        if (s.hasTag("LineNumberTag")) {
          LineNumberTag tag = (LineNumberTag) s.getTag("LineNumberTag");
          lineToMeth.put(new Integer(tag.getLineNumber()), meth);
        }
      }
      Iterator methIt2 = sc.getMethods().iterator();
      while (methIt2.hasNext()) {
        SootMethod meth = (SootMethod) methIt2.next();
        if (!meth.isConcrete()) {
          continue;
        }
        Body body = meth.retrieveActiveBody();
        Stmt s = (Stmt) body.getUnits().getFirst();
        while (s instanceof IdentityStmt) {
          s = (Stmt) body.getUnits().getSuccOf(s);
        }
        if (s.hasTag("LineNumberTag")) {
          LineNumberTag tag = (LineNumberTag) s.getTag("LineNumberTag");
          int line_num = tag.getLineNumber() - 1;
          // already taken
          if (lineToMeth.containsKey(new Integer(line_num))) {
            meth.addTag(new LineNumberTag(line_num + 1));
          }
          // still available - so use it for this meth
          else {
            meth.addTag(new LineNumberTag(line_num));
          }
        }
      }

    }
  }
}
