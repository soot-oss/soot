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

import soot.toDex.Register;

/**
 * Interface for instructions that need five registers.<br>
 * <br>
 * Note that the interface does not inherit from {@link ThreeRegInsn} due to the unusual register naming - the register
 * indices cannot be overwritten here.
 */
public interface FiveRegInsn extends Insn {

  static final int REG_D_IDX = 0;

  static final int REG_E_IDX = REG_D_IDX + 1;

  static final int REG_F_IDX = REG_E_IDX + 1;

  static final int REG_G_IDX = REG_F_IDX + 1;

  static final int REG_A_IDX = REG_G_IDX + 1;

  Register getRegD();

  Register getRegE();

  Register getRegF();

  Register getRegG();

  Register getRegA();
}
