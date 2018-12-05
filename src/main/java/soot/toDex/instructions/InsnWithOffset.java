package soot.toDex.instructions;

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

import org.jf.dexlib2.Opcode;

import soot.jimple.Stmt;

/**
 * An abstract implementation for instructions that have a jump label.
 */
public abstract class InsnWithOffset extends AbstractInsn {

  protected Stmt target;

  public InsnWithOffset(Opcode opc) {
    super(opc);
  }

  public void setTarget(Stmt target) {
    if (target == null) {
      throw new RuntimeException("Cannot jump to a NULL target");
    }
    this.target = target;
  }

  public Stmt getTarget() {
    return this.target;
  }

  /**
   * Gets the maximum number of words available for the jump offset
   * 
   * @return The maximum number of words available for the jump offset
   */
  public abstract int getMaxJumpOffset();

}
