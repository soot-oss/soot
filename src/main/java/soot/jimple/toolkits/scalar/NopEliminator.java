package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.jimple.NopStmt;
import soot.options.Options;
import soot.util.Chain;

public class NopEliminator extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(NopEliminator.class);

  public NopEliminator(Singletons.Global g) {
  }

  public static NopEliminator v() {
    return G.v().soot_jimple_toolkits_scalar_NopEliminator();
  }

  /**
   * Removes {@link NopStmt}s from the passed body . Complexity is linear with respect to the statements.
   */
  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "] Removing nops...");
    }

    Chain<Unit> units = b.getUnits();

    // Just do one trivial pass.
    Iterator<Unit> stmtIt = units.snapshotIterator();
    while (stmtIt.hasNext()) {
      Unit u = stmtIt.next();
      if (u instanceof NopStmt) {
        // Hack: do not remove nop, if is is used for a Trap which
        // is at the very end of the code.
        boolean keepNop = false;
        if (b.getUnits().getLast() == u) {
          for (Trap t : b.getTraps()) {
            if (t.getEndUnit() == u) {
              keepNop = true;
            }
          }
        }
        if (!keepNop) {
          units.remove(u);
        }
      }
    }
  }
}
