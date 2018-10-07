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

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;

import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;

public class MoveInstruction extends DexlibAbstractInstruction {

  public MoveInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  public void jimplify(DexBody body) {

    TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;

    int dest = i.getRegisterA();
    int source = i.getRegisterB();
    AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), body.getRegisterLocal(source));
    setUnit(assign);
    addTags(assign);
    body.add(assign);

    if (IDalvikTyper.ENABLE_DVKTYPER) {
      DalvikTyper.v().addConstraint(assign.getLeftOpBox(), assign.getRightOpBox());
    }
  }

  @Override
  int movesRegister(int register) {
    TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;
    int dest = i.getRegisterA();
    int source = i.getRegisterB();
    if (register == source) {
      return dest;
    }
    return -1;
  }

  @Override
  int movesToRegister(int register) {
    TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;
    int dest = i.getRegisterA();
    int source = i.getRegisterB();
    if (register == dest) {
      return source;
    }
    return -1;
  }

  @Override
  boolean overridesRegister(int register) {
    TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;
    int dest = i.getRegisterA();
    return register == dest;
  }
}
