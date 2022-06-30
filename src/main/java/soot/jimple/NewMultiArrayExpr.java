package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.ArrayType;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.util.Switch;

public interface NewMultiArrayExpr extends Expr, AnyNewExpr {
  public ArrayType getBaseType();

  public void setBaseType(ArrayType baseType);

  public ValueBox getSizeBox(int index);

  public int getSizeCount();

  public Value getSize(int index);

  public List<Value> getSizes();

  public void setSize(int index, Value size);

  public Type getType();

  public void apply(Switch sw);
}
