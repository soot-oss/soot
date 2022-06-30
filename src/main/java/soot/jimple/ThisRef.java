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

import java.util.Collections;
import java.util.List;

import soot.RefType;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.util.Switch;

public class ThisRef implements IdentityRef {

  RefType thisType;

  public ThisRef(RefType thisType) {
    this.thisType = thisType;
  }

  @Override
  public boolean equivTo(Object o) {
    return (o instanceof ThisRef) && this.thisType.equals(((ThisRef) o).thisType);
  }

  @Override
  public int equivHashCode() {
    return thisType.hashCode();
  }

  @Override
  public String toString() {
    return "@this: " + thisType;
  }

  @Override
  public void toString(UnitPrinter up) {
    up.identityRef(this);
  }

  @Override
  public final List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  @Override
  public Type getType() {
    return thisType;
  }

  @Override
  public void apply(Switch sw) {
    ((RefSwitch) sw).caseThisRef(this);
  }

  @Override
  public Object clone() {
    return new ThisRef(thisType);
  }
}
