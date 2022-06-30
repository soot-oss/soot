package soot.dexpler.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

import soot.Body;
import soot.Type;
import soot.dexpler.DexBody;

/**
 * Interface for instructions that can/must be retyped, i.e. instructions that assign to a local and have to retype it after
 * local splitting.
 *
 * @author Michael Markert <michael.markert@googlemail.com>
 */
public interface RetypeableInstruction {
  /**
   * Swap generic exception type with the given one.
   *
   * @param body
   *          the body that contains the instruction
   * @param t
   *          the real type.
   */
  public void setRealType(DexBody body, Type t);

  /**
   * Do actual retype.
   *
   * Retyping is separated from setting the type, to make it possible to retype after local splitting.
   *
   * @param body
   *          The body containing the processed statement
   */
  public void retype(Body body);
}
