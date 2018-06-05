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

public abstract class NumericConstant extends Constant {
  // PTC 1999/06/28
  public abstract NumericConstant add(NumericConstant c);

  public abstract NumericConstant subtract(NumericConstant c);

  public abstract NumericConstant multiply(NumericConstant c);

  public abstract NumericConstant divide(NumericConstant c);

  public abstract NumericConstant remainder(NumericConstant c);

  public abstract NumericConstant equalEqual(NumericConstant c);

  public abstract NumericConstant notEqual(NumericConstant c);

  public abstract NumericConstant lessThan(NumericConstant c);

  public abstract NumericConstant lessThanOrEqual(NumericConstant c);

  public abstract NumericConstant greaterThan(NumericConstant c);

  public abstract NumericConstant greaterThanOrEqual(NumericConstant c);

  public abstract NumericConstant negate();
}
