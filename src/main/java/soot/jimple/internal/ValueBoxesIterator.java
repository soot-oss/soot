package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2025 Marc Miltenberger
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

import soot.ValueBox;

final class ValueBoxesIterator implements Iterator<ValueBox> {
  Iterator<ValueBox> currentIterator;
  int i = 0;
  private ValueBox[] argBoxes;

  public ValueBoxesIterator(ValueBox[] argBoxes) {
    this.argBoxes = argBoxes;
  }

  @Override
  public boolean hasNext() {
    if (currentIterator != null && currentIterator.hasNext()) {
      return true;
    }
    while (i < argBoxes.length) {
      currentIterator = argBoxes[i].getValue().getUseBoxesIterator();
      i++;
      if (currentIterator.hasNext()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public ValueBox next() {
    if (!hasNext()) {
      throw new IllegalStateException("End of list");
    }
    return currentIterator.next();
  }
}