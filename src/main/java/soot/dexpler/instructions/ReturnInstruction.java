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
import org.jf.dexlib2.iface.instruction.formats.Instruction11x;

import soot.Local;
import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.Jimple;
import soot.jimple.ReturnStmt;

public class ReturnInstruction extends DexlibAbstractInstruction {

  public ReturnInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  public void jimplify(DexBody body) {
    Instruction11x returnInstruction = (Instruction11x) this.instruction;
    Local l = body.getRegisterLocal(returnInstruction.getRegisterA());
    ReturnStmt returnStmt = Jimple.v().newReturnStmt(l);
    setUnit(returnStmt);
    addTags(returnStmt);
    body.add(returnStmt);

    if (IDalvikTyper.ENABLE_DVKTYPER) {

      // DalvikTyper.v().addConstraint(returnStmt.getOpBox(), new
      // ImmediateBox(Jimple.body.getBody().getMethod().getReturnType()));
      DalvikTyper.v().setType(returnStmt.getOpBox(), body.getBody().getMethod().getReturnType(), true);
    }
  }
}
