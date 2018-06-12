package soot.jimple.toolkits.scalar;

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
import soot.ConflictingFieldRefException;
import soot.G;
import soot.Singletons;
import soot.SootField;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

/**
 * Transformer that checks whether a static field is used like an instance field. If this is the case, all instance
 * references are replaced by static field references.
 *
 * @author Steven Arzt
 *
 */
public class FieldStaticnessCorrector extends AbstractStaticnessCorrector {

  public FieldStaticnessCorrector(Singletons.Global g) {
  }

  public static FieldStaticnessCorrector v() {
    return G.v().soot_jimple_toolkits_scalar_FieldStaticnessCorrector();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    // Some apps reference static fields as instance fields. We need to fix
    // this for not breaking the client analysis.
    for (Iterator<Unit> unitIt = b.getUnits().iterator(); unitIt.hasNext();) {
      Stmt s = (Stmt) unitIt.next();
      if (s.containsFieldRef() && s instanceof AssignStmt) {
        FieldRef ref = s.getFieldRef();
        // Make sure that the target class has already been loaded
        if (isTypeLoaded(ref.getFieldRef().type())) {
          try {
            if (ref instanceof InstanceFieldRef) {
              SootField fld = ref.getField();
              if (fld != null && fld.isStatic()) {
                AssignStmt assignStmt = (AssignStmt) s;
                if (assignStmt.getLeftOp() == ref) {
                  assignStmt.setLeftOp(Jimple.v().newStaticFieldRef(ref.getField().makeRef()));
                } else if (assignStmt.getRightOp() == ref) {
                  assignStmt.setRightOp(Jimple.v().newStaticFieldRef(ref.getField().makeRef()));
                }
              }
            }
          } catch (ConflictingFieldRefException ex) {
            // That field is broken, just don't touch it
          }
        }
      }
    }
  }

}
