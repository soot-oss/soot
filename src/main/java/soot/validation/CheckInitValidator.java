package soot.validation;

import java.util.Iterator;

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

import java.util.List;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.exceptions.ThrowAnalysisFactory;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.InitAnalysis;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SmartLocalDefs;

public enum CheckInitValidator implements BodyValidator {
  INSTANCE;

  public static CheckInitValidator v() {
    return INSTANCE;
  }

  @Override
  public void validate(Body body, List<ValidationException> exception) {
    ExceptionalUnitGraph g
        = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body, ThrowAnalysisFactory.checkInitThrowAnalysis(), false);

    InitAnalysis analysis = new InitAnalysis(g);
    for (Unit s : body.getUnits()) {
      FlowSet<Local> init = analysis.getFlowBefore(s);
      for (Iterator<ValueBox> iterator = s.getUseBoxesIterator(); iterator.hasNext();) {
        ValueBox vBox = iterator.next();
        Value v = vBox.getValue();
        if (v instanceof Local) {
          Local l = (Local) v;
          if (!init.contains(l)) {
            SimpleLocalDefs defs = new SimpleLocalDefs(g);
            List<Unit> allDefs = defs.getDefsOfAt(l, s);
            ValidationException e = new ValidationException(s, "Local variable " + l.getName() + 
                " is not definitively defined at this point",
                "Warning: Local variable " + l + " not definitely defined at " + s + " in " + 
                body.getMethod() + "\nFound definition sites: " + allDefs);
            exception.add(
                e);
          }
        }
      }
    }
  }

  @Override
  public boolean isBasicValidator() {
    return false;
  }
}
