package soot.baf;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import soot.Unit;
import soot.baf.internal.AbstractInst;

public class PlaceholderInst extends AbstractInst {

  private final Unit source;

  PlaceholderInst(Unit source) {
    this.source = source;
  }

  @Override
  public Object clone() {
    return new PlaceholderInst(getSource());
  }

  public Unit getSource() {
    return source;
  }

  @Override
  public final String getName() {
    return "<placeholder>";
  }

  @Override
  public String toString() {
    return "<placeholder: " + source.toString() + ">";
  }
}
