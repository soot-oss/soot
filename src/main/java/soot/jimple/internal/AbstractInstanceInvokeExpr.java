package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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
import java.util.Collections;
import java.util.List;

import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InstanceInvokeExpr;

@SuppressWarnings("serial")
public abstract class AbstractInstanceInvokeExpr extends AbstractInvokeExpr implements InstanceInvokeExpr {
  final protected ValueBox baseBox;

  protected AbstractInstanceInvokeExpr(SootMethodRef methodRef, ValueBox baseBox, ValueBox[] argBoxes) {
    super(methodRef, argBoxes);
    this.baseBox = baseBox;
  }

  public Value getBase() {
    return baseBox.getValue();
  }

  public ValueBox getBaseBox() {
    return baseBox;
  }

  public void setBase(Value base) {
    baseBox.setValue(base);
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();
    if (argBoxes != null) {
      Collections.addAll(list, argBoxes);
      for (ValueBox element : argBoxes) {
        list.addAll(element.getValue().getUseBoxes());
      }
    }
    list.addAll(baseBox.getValue().getUseBoxes());
    list.add(baseBox);

    return list;
  }
}
