package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.tagkit.Tag;

/**
 * Implements a tag that can be used to tell a VM whether a cast check can be eliminated or not.
 */
public class CastCheckTag implements Tag {
  boolean eliminateCheck;

  CastCheckTag(boolean eliminateCheck) {
    this.eliminateCheck = eliminateCheck;
  }

  public String getName() {
    return "CastCheckTag";
  }

  public byte[] getValue() {
    byte[] ret = new byte[1];
    ret[0] = (byte) (eliminateCheck ? 1 : 0);
    return ret;
  }

  public String toString() {
    if (eliminateCheck) {
      return "This cast check can be eliminated.";
    } else {
      return "This cast check should NOT be eliminated.";
    }
  }

  public boolean canEliminateCheck() {
    return eliminateCheck;
  }
}
