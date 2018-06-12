package soot.baf.toolkits.base;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville
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

/**
 * Interface to be implemented by peepholes acting on the Baf IR.
 *
 * @see PeepholeOptimizer
 * @see ExamplePeephole
 */

public interface Peephole {
  /**
   * Entry point for a peephole. This method is repeatedly called by the peephole driver, until a fixed-point is reached over
   * all peepholes.
   *
   * @param b
   *          Body to apply peephole to.
   * @return true if the peephole changed in any way the Body it acted on. false otherwise.
   */
  boolean apply(Body b);
}
