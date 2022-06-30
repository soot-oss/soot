package soot.jimple.spark.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import soot.SootField;
import soot.SootMethod;

/**
 * The decision whether a libraries field or method is accessible for a client can be different for different analyses.
 * 
 * This interface provides methods to define how this decision will be made.
 * 
 * @author Florian Kuebler
 *
 */
public interface ClientAccessibilityOracle {

  /**
   * Determines whether the method is accessible for a potential library user.
   */
  public boolean isAccessible(SootMethod method);

  /**
   * Determines whether the field is accessible for a potential library user.
   */
  public boolean isAccessible(SootField field);

}
