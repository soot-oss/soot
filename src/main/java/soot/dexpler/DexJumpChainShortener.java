package soot.dexpler;

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

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;

/**
 * Transformer for reducing goto chains. If there is a chain of jumps in the code before the final target is reached, we
 * collapse this chain into a direct jump to the target location.
 *
 * @author Steven Arzt
 *
 */
public class DexJumpChainShortener extends BodyTransformer {

  public static DexJumpChainShortener v() {
    return new DexJumpChainShortener();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {

    for (Iterator<Unit> unitIt = b.getUnits().snapshotIterator(); unitIt.hasNext();) {
      Unit u = unitIt.next();
      if (u instanceof GotoStmt) {
        GotoStmt stmt = (GotoStmt) u;
        while (stmt.getTarget() instanceof GotoStmt) {
          GotoStmt nextTarget = (GotoStmt) stmt.getTarget();
          stmt.setTarget(nextTarget.getTarget());
        }
      } else if (u instanceof IfStmt) {
        IfStmt stmt = (IfStmt) u;
        while (stmt.getTarget() instanceof GotoStmt) {
          GotoStmt nextTarget = (GotoStmt) stmt.getTarget();
          stmt.setTarget(nextTarget.getTarget());
        }
      }
    }
  }

}
