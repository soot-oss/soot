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

class FlowGraphEdge {
  Object from;
  Object to;

  public FlowGraphEdge() {
    this.from = null;
    this.to = null;
  }

  public FlowGraphEdge(Object from, Object to) {
    this.from = from;
    this.to = to;
  }

  public int hashCode() {
    return this.from.hashCode() ^ this.to.hashCode();
  }

  public Object getStartUnit() {
    return this.from;
  }

  public Object getTargetUnit() {
    return this.to;
  }

  public void changeEndUnits(Object from, Object to) {
    this.from = from;
    this.to = to;
  }

  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }

    if (other instanceof FlowGraphEdge) {
      Object otherstart = ((FlowGraphEdge) other).getStartUnit();
      Object othertarget = ((FlowGraphEdge) other).getTargetUnit();

      return (this.from.equals(otherstart) && this.to.equals(othertarget));
    } else {
      return false;
    }
  }
}
