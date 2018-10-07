package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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

public class Array2ndDimensionSymbol {
  private Object var;

  public static Array2ndDimensionSymbol v(Object which) {
    Array2ndDimensionSymbol tdal = G.v().Array2ndDimensionSymbol_pool.get(which);
    if (tdal == null) {
      tdal = new Array2ndDimensionSymbol(which);
      G.v().Array2ndDimensionSymbol_pool.put(which, tdal);
    }

    return tdal;
  }

  private Array2ndDimensionSymbol(Object which) {
    this.var = which;
  }

  public Object getVar() {
    return this.var;
  }

  public int hashCode() {
    return var.hashCode() + 1;
  }

  public boolean equals(Object other) {
    if (other instanceof Array2ndDimensionSymbol) {
      Array2ndDimensionSymbol another = (Array2ndDimensionSymbol) other;

      return (this.var == another.var);
    } else {
      return false;
    }
  }

  public String toString() {
    return var + "[";
  }
}
