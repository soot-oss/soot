package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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
import soot.jimple.IntConstant;

public class BooleanConstant extends IntConstant {
  private static final String FALSE_CONSTANT = "false";
  private static final String TRUE_CONSTANT = "true";
  private static final long serialVersionUID = 0L;
  private static final int TRUE = 1;
  private static final int FALSE = 0;

  public static final BooleanConstant TRUE_C = new BooleanConstant(true);
  public static final BooleanConstant FALSE_C = new BooleanConstant(false);

  public BooleanConstant(boolean value) {
    super(value == true ? TRUE : FALSE);
  }

  public static BooleanConstant v(boolean b) {
    if (b) {
      return TRUE_C;
    } else {
      return FALSE_C;
    }
  }

  @Override
  public String toString() {
    if (value == TRUE) {
      return TRUE_CONSTANT;
    } else {
      return FALSE_CONSTANT;
    }
  }

  public boolean getBoolean() {
    // A value other than 0 or 1 shouldn't be possible
    return value == TRUE;
  }

  @Override
  public Type getType() {
    return BooleanType.v();
  }
}
