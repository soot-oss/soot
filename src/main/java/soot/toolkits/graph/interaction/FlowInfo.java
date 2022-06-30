package soot.toolkits.graph.interaction;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

public class FlowInfo<I, U> {

  private I info;
  private U unit;
  private boolean before;

  public FlowInfo(I info, U unit, boolean b) {
    info(info);
    unit(unit);
    setBefore(b);
  }

  public U unit() {
    return unit;
  }

  public void unit(U u) {
    unit = u;
  }

  public I info() {
    return info;
  }

  public void info(I i) {
    info = i;
  }

  public boolean isBefore() {
    return before;
  }

  public void setBefore(boolean b) {
    before = b;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("unit: " + unit);
    sb.append(" info: " + info);
    sb.append(" before: " + before);
    return sb.toString();
  }
}
