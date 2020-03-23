package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Ondrej Lhotak
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

/**
 * A class implementing this interface can be invalidated. The invalidation state can be retrieved by other classes.
 * 
 * @author Marc Miltenberger
 */
public interface Invalidable {
  /**
   * Return true if the object is invalid.
   * 
   * @return true if the object is invalid.
   */
  public boolean isInvalid();

  /**
   * Invalidates the object. Does nothing if the object is already invalid.
   */
  public void invalidate();
}
