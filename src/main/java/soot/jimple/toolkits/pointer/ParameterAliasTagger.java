package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefLikeType;
import soot.Scene;
import soot.Singletons;
import soot.ValueBox;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.tagkit.ColorTag;

/** Adds colour tags to indicate potential aliasing between method parameters. */
public class ParameterAliasTagger extends BodyTransformer {
  public ParameterAliasTagger(Singletons.Global g) {
  }

  public static ParameterAliasTagger v() {
    return G.v().soot_jimple_toolkits_pointer_ParameterAliasTagger();
  }

  protected void internalTransform(Body b, String phaseName, Map options) {
    PointsToAnalysis pa = Scene.v().getPointsToAnalysis();
    Set<IdentityStmt> parms = new HashSet<IdentityStmt>();

    for (Iterator sIt = b.getUnits().iterator(); sIt.hasNext();) {

      final Stmt s = (Stmt) sIt.next();
      if (!(s instanceof IdentityStmt)) {
        continue;
      }
      IdentityStmt is = (IdentityStmt) s;
      ValueBox vb = is.getRightOpBox();
      if (!(vb.getValue() instanceof ParameterRef)) {
        continue;
      }
      ParameterRef pr = (ParameterRef) vb.getValue();
      if (!(pr.getType() instanceof RefLikeType)) {
        continue;
      }
      parms.add(is);
    }

    int colour = 0;
    while (!parms.isEmpty()) {
      fill(parms, parms.iterator().next(), colour++, pa);
    }
  }

  private void fill(Set<IdentityStmt> parms, IdentityStmt parm, int colour, PointsToAnalysis pa) {
    if (!parms.contains(parm)) {
      return;
    }
    parm.getRightOpBox().addTag(new ColorTag(colour, "Parameter Alias"));
    parms.remove(parm);
    PointsToSet ps = pa.reachingObjects((Local) parm.getLeftOp());
    for (Iterator<IdentityStmt> parm2It = (new LinkedList<IdentityStmt>(parms)).iterator(); parm2It.hasNext();) {
      final IdentityStmt parm2 = parm2It.next();
      if (ps.hasNonEmptyIntersection(pa.reachingObjects((Local) parm2.getLeftOp()))) {
        fill(parms, parm2, colour, pa);
      }
    }
  }
}
