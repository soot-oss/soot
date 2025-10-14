package soot.jimple.internal;

import com.google.common.collect.Iterators;

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

import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;

@SuppressWarnings("serial")
public abstract class AbstractDefinitionStmt extends AbstractStmt implements DefinitionStmt {

  protected final ValueBox leftBox;
  protected final ValueBox rightBox;

  protected AbstractDefinitionStmt(ValueBox leftBox, ValueBox rightBox) {
    this.leftBox = leftBox;
    this.rightBox = rightBox;
  }

  @Override
  public final Value getLeftOp() {
    return leftBox.getValue();
  }

  @Override
  public final Value getRightOp() {
    return rightBox.getValue();
  }

  @Override
  public final ValueBox getLeftOpBox() {
    return leftBox;
  }

  @Override
  public final ValueBox getRightOpBox() {
    return rightBox;
  }

  @Override
  public final List<ValueBox> getDefBoxes() {
    return Collections.singletonList(leftBox);
  }

  @Override
  public final Iterator<ValueBox> getDefBoxesIterator() {
    return Iterators.singletonIterator(leftBox);
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();
    list.addAll(getLeftOp().getUseBoxes());
    list.add(rightBox);
    list.addAll(getRightOp().getUseBoxes());
    return list;
  }

  @Override
  public Iterator<ValueBox> getUseBoxesIterator() {
    return new Iterator<ValueBox>() {
      Iterator<ValueBox> lop = getLeftOp().getUseBoxesIterator();
      Iterator<ValueBox> rop = getRightOp().getUseBoxesIterator();
      // 0 = iterator 1
      // 1 = right box
      // 2 = iterator 2
      int state = 0;

      @Override
      public boolean hasNext() {
        switch (state) {
          case 0:
            boolean b = lop.hasNext();
            if (b) {
              return true;
            } else {
              state = 1;
            }
          case 1:
            return true;
          default:
            return rop.hasNext();
        }
      }

      @Override
      public ValueBox next() {
        switch (state) {
          case 0:
            if (lop.hasNext()) {
              return lop.next();
            }
          case 1:
            state = 2;
            return rightBox;
          default:
            return rop.next();
        }
      }
    };

  }

  @Override
  public boolean fallsThrough() {
    return true;
  }

  @Override
  public boolean branches() {
    return false;
  }
}
