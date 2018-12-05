package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import soot.Local;

/**
 * Class for packing local numbers such that bit sets can easily be used to reference locals in bodies
 * 
 * @author Steven Arzt
 *
 */
public class LocalBitSetPacker {

  private final Body body;

  private Local[] locals;
  private int[] oldNumbers;

  public LocalBitSetPacker(Body body) {
    this.body = body;
  }

  /**
   * Reassigns the local numbers such that a dense bit set can be created over them
   */
  public void pack() {
    int n = body.getLocalCount();
    locals = new Local[n];
    oldNumbers = new int[n];
    n = 0;
    for (Local local : body.getLocals()) {
      locals[n] = local;
      oldNumbers[n] = local.getNumber();
      local.setNumber(n++);
    }
  }

  /**
   * Restores the original local numbering
   */
  public void unpack() {
    for (int i = 0; i < locals.length; i++) {
      locals[i].setNumber(oldNumbers[i]);
    }
    locals = null;
    oldNumbers = null;
  }

  public int getLocalCount() {
    return locals == null ? 0 : locals.length;
  }

}
