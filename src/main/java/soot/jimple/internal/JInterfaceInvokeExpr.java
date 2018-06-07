package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
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

import java.util.ArrayList;
import java.util.List;

import soot.SootClass;
import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;

public class JInterfaceInvokeExpr extends AbstractInterfaceInvokeExpr {
  public JInterfaceInvokeExpr(Value base, SootMethodRef methodRef, List<? extends Value> args) {
    super(Jimple.v().newLocalBox(base), methodRef, new ValueBox[args.size()]);

    // Check that the method's class is resolved enough
    // CheckLevel returns without doing anything because we can be not 'done' resolving
    methodRef.declaringClass().checkLevelIgnoreResolving(SootClass.HIERARCHY);
    // now check if the class is valid
    if (!methodRef.declaringClass().isInterface() && !methodRef.declaringClass().isPhantom()) {
      throw new RuntimeException("Trying to create interface invoke expression for non-interface type: "
          + methodRef.declaringClass() + " Use JVirtualInvokeExpr or JSpecialInvokeExpr instead!");
    }

    for (int i = 0; i < args.size(); i++) {
      this.argBoxes[i] = Jimple.v().newImmediateBox(args.get(i));
    }
  }

  public Object clone() {
    List<Value> argList = new ArrayList<Value>(getArgCount());

    for (int i = 0; i < getArgCount(); i++) {
      argList.add(i, Jimple.cloneIfNecessary(getArg(i)));
    }

    return new JInterfaceInvokeExpr(Jimple.cloneIfNecessary(getBase()), methodRef, argList);
  }

}
