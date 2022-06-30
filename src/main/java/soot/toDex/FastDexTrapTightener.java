package soot.toDex;

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
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;

/**
 * Tries may not start or end at units which have no corresponding Dalvik instructions such as IdentityStmts. We reduce the
 * traps to start at the first "real" instruction. We could also use a TrapTigthener, but that would be too expensive for
 * just producing working Dex code.
 *
 * @author Steven Arzt
 */
public class FastDexTrapTightener extends BodyTransformer {

  public FastDexTrapTightener(Singletons.Global g) {
  }

  public static FastDexTrapTightener v() {
    return soot.G.v().soot_toDex_FastDexTrapTightener();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    for (Iterator<Trap> trapIt = b.getTraps().snapshotIterator(); trapIt.hasNext();) {
      Trap t = trapIt.next();

      Unit beginUnit;
      while (!isDexInstruction(beginUnit = t.getBeginUnit()) && t.getBeginUnit() != t.getEndUnit()) {
        t.setBeginUnit(b.getUnits().getSuccOf(beginUnit));
      }

      // If the trap is empty, we remove it
      if (t.getBeginUnit() == t.getEndUnit()) {
        trapIt.remove();
      }
    }
  }

  private boolean isDexInstruction(Unit unit) {
    if (unit instanceof IdentityStmt) {
      IdentityStmt is = (IdentityStmt) unit;
      return !(is.getRightOp() instanceof ThisRef || is.getRightOp() instanceof ParameterRef);
    }
    return true;
  }

}
