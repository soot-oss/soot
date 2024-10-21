package soot.jimple.toolkits.typing.fast;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2008 Ben Bellamy 
 * 
 * All rights reserved.
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
 * Whether a cast is needed or not
 */
public enum NeedCastResult {
  /**
   * The cast is needed
   */
  NEEDS_CAST,

  /**
   * No cast is needed
   */
  DOESNT_NEED_CAST,

  /**
   * Actually, no cast is needed, but it is discouraged.
   * For example, in Dex, int and float are essentially the same.
   * In some contexts however, we prefer one of the types.  
   */
  DISCOURAGED_TARGET_TYPE
}
