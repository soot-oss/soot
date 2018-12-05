package soot.jimple.spark.geom.utils;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 - 2014 Richard Xiao
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

import soot.Scene;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

/**
 * It implements missing features in Soot components. All functions should be static.
 * 
 * @author xiao
 *
 */
public class SootInfo {

  public static int countCallEdgesForCallsite(Stmt callsite, boolean stopForMutiple) {
    CallGraph cg = Scene.v().getCallGraph();
    int count = 0;

    for (Iterator<Edge> it = cg.edgesOutOf(callsite); it.hasNext();) {
      it.next();
      ++count;
      if (stopForMutiple && count > 1) {
        break;
      }
    }

    return count;
  }

}
