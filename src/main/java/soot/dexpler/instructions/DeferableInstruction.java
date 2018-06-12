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

import soot.dexpler.DexBody;

/**
 * Interface for instructions that can/must be defered, i.e. executed after the rest of the DexBody has been converted to
 * Jimple
 *
 * @author Michael Markert <michael.markert@googlemail.com>
 */
public interface DeferableInstruction {

  /**
   * Jimplify this instruction with the guarantee that every other (non-deferred) instruction has been jimplified.
   *
   * @param body
   *          to jimplify into
   */
  public void deferredJimplify(DexBody body);
}
