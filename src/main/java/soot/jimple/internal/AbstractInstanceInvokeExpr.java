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
import java.util.Iterator;
import java.util.List;

import soot.SootMethodRef;
import soot.Value;
import soot.ValueBox;
import soot.jimple.InstanceInvokeExpr;
import soot.util.IteratorConcatElement;

@SuppressWarnings("serial")
public abstract class AbstractInstanceInvokeExpr extends AbstractInvokeExpr implements InstanceInvokeExpr {

  protected final ValueBox baseBox;

  protected AbstractInstanceInvokeExpr(SootMethodRef methodRef, ValueBox baseBox, ValueBox[] argBoxes) {
    super(methodRef, argBoxes);
    this.baseBox = baseBox;
  }

  @Override
  public Value getBase() {
    return baseBox.getValue();
  }

  @Override
  public ValueBox getBaseBox() {
    return baseBox;
  }

  @Override
  public void setBase(Value base) {
    baseBox.setValue(base);
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>(baseBox.getValue().getUseBoxes());
    list.add(baseBox);

    if (argBoxes != null) {
      Collections.addAll(list, argBoxes);
      for (ValueBox element : argBoxes) {
        list.addAll(element.getValue().getUseBoxes());
      }
    }

    return list;
  }

  @Override
  public Iterator<ValueBox> getUseBoxesIterator() {
    Iterator<ValueBox> binner = baseBox.getValue().getUseBoxesIterator();
    if (argBoxes == null) {
      return IteratorConcatElement.v(binner, baseBox);
    } else {
      return new Iterator<ValueBox>() {

        Iterator<ValueBox> binnerIt = binner;
        int argBoxesIterator;
        ValueBoxesUseBoxIterator op2 = new ValueBoxesUseBoxIterator(argBoxes);
        // 0 = base inner
        // 1 = base box
        // 2 = argboxes boxes
        // 3 = argboxes inner
        int state = 0;

        @Override
        public boolean hasNext() {
          switch (state) {
            case 0:
              boolean b = binnerIt.hasNext();
              if (b) {
                return true;
              } else {
                state = 1;
              }
            case 1:
              return true;
            case 2:
              if (argBoxesIterator < argBoxes.length) {
                return true;
              } else {
                state = 3;
              }
            default:
              return op2.hasNext();
          }
        }

        @Override
        public ValueBox next() {
          switch (state) {
            case 0:
              if (binnerIt.hasNext()) {
                return binnerIt.next();
              }
            case 1:
              state = 2;
              return baseBox;
            case 2:
              ValueBox p = argBoxes[argBoxesIterator];
              if (++argBoxesIterator >= argBoxes.length) {
                state = 3;
              }
              return p;
            default:
              return op2.next();
          }
        }
      };
    }
  }
}
