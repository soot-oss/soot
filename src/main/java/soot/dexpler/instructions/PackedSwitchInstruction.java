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

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.formats.PackedSwitchPayload;

import soot.IntType;
import soot.Local;
import soot.Unit;
import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;

public class PackedSwitchInstruction extends SwitchInstruction {

  public PackedSwitchInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  protected Stmt switchStatement(DexBody body, Instruction targetData, Local key) {
    PackedSwitchPayload i = (PackedSwitchPayload) targetData;
    List<? extends SwitchElement> seList = i.getSwitchElements();

    // the default target always follows the switch statement
    int defaultTargetAddress = codeAddress + instruction.getCodeUnits();
    Unit defaultTarget = body.instructionAtAddress(defaultTargetAddress).getUnit();

    List<IntConstant> lookupValues = new ArrayList<IntConstant>();
    List<Unit> targets = new ArrayList<Unit>();
    for (SwitchElement se : seList) {
      lookupValues.add(IntConstant.v(se.getKey()));
      int offset = se.getOffset();
      targets.add(body.instructionAtAddress(codeAddress + offset).getUnit());
    }
    LookupSwitchStmt switchStmt = Jimple.v().newLookupSwitchStmt(key, lookupValues, targets, defaultTarget);
    setUnit(switchStmt);

    if (IDalvikTyper.ENABLE_DVKTYPER) {
      DalvikTyper.v().setType(switchStmt.getKeyBox(), IntType.v(), true);
    }

    return switchStmt;
  }

  @Override
  public void computeDataOffsets(DexBody body) {
  }

}
