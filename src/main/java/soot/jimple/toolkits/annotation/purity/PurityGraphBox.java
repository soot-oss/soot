package soot.jimple.toolkits.annotation.purity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Antoine Mine
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

/**
 * Simple box class that encapsulates a reference to a PurityGraph.
 */
public class PurityGraphBox {

  public PurityGraph g;

  PurityGraphBox() {
    this.g = new PurityGraph();
  }

  @Override
  public int hashCode() {
    return g.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof PurityGraphBox) {
      PurityGraphBox oo = (PurityGraphBox) o;
      return this.g.equals(oo.g);
    } else {
      return false;
    }
  }
}
