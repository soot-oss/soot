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

import java.util.List;

import soot.Unit;

/**
 * The payload for switch instructions, usually placed at the end of a method. This is where the jump targets are stored.<br>
 * <br>
 * Note that this is an {@link InsnWithOffset} with multiple offsets.
 */
public abstract class SwitchPayload extends AbstractPayload {

  protected Insn31t switchInsn;

  protected List<Unit> targets;

  public SwitchPayload(List<Unit> targets) {
    super();
    this.targets = targets;
  }

  public void setSwitchInsn(Insn31t switchInsn) {
    this.switchInsn = switchInsn;
  }

  @Override
  public int getMaxJumpOffset() {
    return Short.MAX_VALUE;
  }

}
