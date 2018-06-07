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

import soot.Immediate;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;

@SuppressWarnings("serial")
public abstract class Constant implements Value, ConvertToBaf, Immediate {
  @Override
  public final List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  /** Adds a Baf instruction pushing this constant to the stack onto <code>out</code>. */
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    Unit u = Baf.v().newPushInst(this);
    u.addAllTagsOf(context.getCurrentUnit());
    out.add(u);
  }

  /** Clones the current constant. Not implemented here. */
  public Object clone() {
    throw new RuntimeException();
  }

  /**
   * Returns true if this object is structurally equivalent to c. For Constants, equality is structural equality, so we just
   * call equals().
   */
  public boolean equivTo(Object c) {
    return equals(c);
  }

  /**
   * Returns a hash code consistent with structural equality for this object. For Constants, equality is structural equality;
   * we hope that each subclass defines hashCode() correctly.
   */
  public int equivHashCode() {
    return hashCode();
  }

  public void toString(UnitPrinter up) {
    up.constant(this);
  }
}
