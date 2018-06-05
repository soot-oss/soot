package soot.jimple.toolkits.pointer.representations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Feng Qian
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
import soot.Type;

public class GeneralConstObject extends ConstantObject {

  /* what's the soot class */
  private Type type;
  private String name;
  private int id;

  public GeneralConstObject(Type t, String n) {
    this.type = t;
    this.name = n;
    this.id = G.v().GeneralConstObject_counter++;
  }

  public Type getType() {
    return type;
  }

  public String toString() {
    return name;
  }

  public int hashCode() {
    return this.id;
  }

  public boolean equals(Object other) {
    return this == other;
  }
}
