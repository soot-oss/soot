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
import java.util.List;

import soot.ArrayType;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;

public class JNewMultiArrayExpr extends AbstractNewMultiArrayExpr {
  public JNewMultiArrayExpr(ArrayType type, List<? extends Value> sizes) {
    super(type, new ValueBox[sizes.size()]);

    for (int i = 0; i < sizes.size(); i++) {
      sizeBoxes[i] = Jimple.v().newImmediateBox(sizes.get(i));
    }
  }

  public Object clone() {
    List<Value> clonedSizes = new ArrayList<Value>(getSizeCount());

    for (int i = 0; i < getSizeCount(); i++) {
      clonedSizes.add(i, Jimple.cloneIfNecessary(getSize(i)));
    }

    return new JNewMultiArrayExpr(baseType, clonedSizes);
  }

}
