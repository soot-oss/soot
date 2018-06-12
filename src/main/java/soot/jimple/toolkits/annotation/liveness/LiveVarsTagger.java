package soot.jimple.toolkits.annotation.liveness;

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

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Stmt;
import soot.tagkit.ColorTag;
import soot.tagkit.StringTag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.LiveLocals;
import soot.toolkits.scalar.SimpleLiveLocals;

public class LiveVarsTagger extends BodyTransformer {

  public LiveVarsTagger(Singletons.Global g) {
  }

  public static LiveVarsTagger v() {
    return G.v().soot_jimple_toolkits_annotation_liveness_LiveVarsTagger();
  }

  protected void internalTransform(Body b, String phaseName, Map options) {

    LiveLocals sll = new SimpleLiveLocals(new ExceptionalUnitGraph(b));

    Iterator it = b.getUnits().iterator();
    while (it.hasNext()) {
      Stmt s = (Stmt) it.next();
      // System.out.println("stmt: "+s);
      Iterator liveLocalsIt = sll.getLiveLocalsAfter(s).iterator();
      while (liveLocalsIt.hasNext()) {
        Value v = (Value) liveLocalsIt.next();
        s.addTag(new StringTag("Live Variable: " + v, "Live Variable"));

        Iterator usesIt = s.getUseBoxes().iterator();
        while (usesIt.hasNext()) {
          ValueBox use = (ValueBox) usesIt.next();
          if (use.getValue().equals(v)) {
            use.addTag(new ColorTag(ColorTag.GREEN, "Live Variable"));
          }
        }
        Iterator defsIt = s.getDefBoxes().iterator();
        while (defsIt.hasNext()) {
          ValueBox def = (ValueBox) defsIt.next();
          if (def.getValue().equals(v)) {
            def.addTag(new ColorTag(ColorTag.GREEN, "Live Variable"));
          }
        }
      }
    }
  }
}
