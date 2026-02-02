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
import soot.Unit;
import soot.Value;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.tagkit.ColorTag;

/** Adds colour tags to indicate potential aliasing between method parameters. */
public class ParameterAliasTagger extends BodyTransformer {

  public ParameterAliasTagger(Singletons.Global g) {
  }

  public static ParameterAliasTagger v() {
    return G.v().soot_jimple_toolkits_pointer_ParameterAliasTagger();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    Set<IdentityStmt> parms = new HashSet<IdentityStmt>();
    for (Unit u : b.getUnits()) {
      if (u instanceof IdentityStmt) {
        IdentityStmt is = (IdentityStmt) u;
        Value value = is.getRightOpBox().getValue();
        if (value instanceof ParameterRef) {
          if (((ParameterRef) value).getType() instanceof RefLikeType) {
            parms.add(is);
          }
        }
      }
    }

    int colour = 0;
    PointsToAnalysis pa = Scene.v().getPointsToAnalysis();
    while (!parms.isEmpty()) {
      fill(parms, parms.iterator().next(), colour++, pa);
    }
  }

  private void fill(Set<IdentityStmt> parms, IdentityStmt parm, int colour, PointsToAnalysis pa) {
    if (parms.contains(parm)) {
      parm.getRightOpBox().addTag(new ColorTag(colour, "Parameter Alias"));
      parms.remove(parm);
      PointsToSet ps = pa.reachingObjects((Local) parm.getLeftOp());
      for (IdentityStmt is : new LinkedList<IdentityStmt>(parms)) {
        if (ps.hasNonEmptyIntersection(pa.reachingObjects((Local) is.getLeftOp()))) {
          fill(parms, is, colour, pa);
        }
      }
    }
  }
}
