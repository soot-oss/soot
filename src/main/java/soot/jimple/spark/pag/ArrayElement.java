package soot.jimple.spark.pag;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import soot.G;
import soot.RefType;
import soot.Scene;
import soot.Singletons;
import soot.Type;

/**
 * Represents an array element.
 * 
 * @author Ondrej Lhotak
 */
public class ArrayElement implements SparkField {
  public ArrayElement(Singletons.Global g) {
  }

  public static ArrayElement v() {
    return G.v().soot_jimple_spark_pag_ArrayElement();
  }

  public ArrayElement() {
    Scene.v().getFieldNumberer().add(this);
  }

  public final int getNumber() {
    return number;
  }

  public final void setNumber(int number) {
    this.number = number;
  }

  public Type getType() {
    return RefType.v("java.lang.Object");
  }

  private int number = 0;
}
