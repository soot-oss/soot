package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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
import java.util.function.BiFunction;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.internal.JimpleLocal;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;

/**
 * A BodyTransformer that tries to remove useless aliases such as:
 * 
 * $u0 = new java.lang.StringBuffer;
 * 
 * sb = $u0;
 * 
 * specialinvoke $u0.<java.lang.StringBuffer: void <init>()>();
 * 
 * @author Marc Miltenberger
 * @see BodyTransformer
 * @see Body
 * @see LocalSplitter
 */
public class RemoveUselessAliases extends BodyTransformer {
  private static final Integer ONE = 1;
  private static final Integer MANY = 2;
  private static final BiFunction<Local, Integer, Integer> COUNT = new BiFunction<Local, Integer, Integer>() {

    @Override
    public Integer apply(Local t, Integer u) {
      if (u == null) {
        return ONE;
      } else {
        return MANY;
      }
    }
  };

  public RemoveUselessAliases(Singletons.Global g) {
  }

  public static RemoveUselessAliases v() {
    return G.v().soot_toolkits_scalar_RemoveUselessAliases();
  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    Map<Local, Integer> definitionCounter = new HashMap<>();
    for (Unit s : body.getUnits()) {
      for (Iterator<ValueBox> iterator = s.getDefBoxesIterator(); iterator.hasNext();) {
        ValueBox box = iterator.next();
        Value val = box.getValue();
        if (val instanceof Local) {
          definitionCounter.compute((Local) val, COUNT);
        }
      }
    }
    Map<Local, Local> replacements = null;
    for (Unit s : body.getUnits()) {
      if (s instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) s;
        Value lop = assign.getLeftOp();
        Value rop = assign.getRightOp();
        if (lop instanceof Local && rop instanceof Local) {
          Local l = (Local) lop;
          Local r = (Local) rop;
          if (definitionCounter.get(l) == ONE && definitionCounter.get(r) == ONE) {
            // Both locals are defined only once
            Local use = l;
            Local other = r;
            if (r instanceof JimpleLocal) {
              JimpleLocal jr = (JimpleLocal) r;
              if (jr.isUserDefinedLocal()) {
                use = r;
                other = l;
                if (use instanceof JimpleLocal && ((JimpleLocal) use).isUserDefinedLocal()) {
                  // When both variables are user-defined variables, we do not
                  // want to change their semantics, even when they are useless.
                  continue;
                }
              }
            }
            if (replacements == null) {
              replacements = new HashMap<>();
            } else {
              use = replacements.getOrDefault(use, use);
            }
            if (use == other) {
              continue;
            }
            replacements.put(other, use);
          }
        }
      }
    }
    if (replacements != null) {
      for (Unit s : body.getUnits()) {
        Iterator<ValueBox> boxes = s.getUseAndDefBoxesIterator();
        while (boxes.hasNext()) {
          ValueBox b = boxes.next();
          Value val = b.getValue();
          if (val instanceof Local) {
            Local repl = replacements.get(val);
            if (repl != null) {
              b.setValue(repl);
            }

          }
        }
      }
      DeadAssignmentEliminator.v().transform(body);
    }
  }
}
