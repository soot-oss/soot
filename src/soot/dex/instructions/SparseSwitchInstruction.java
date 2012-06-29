/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dex.instructions;

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.SparseSwitchDataPseudoInstruction;

import soot.Immediate;
import soot.Local;
import soot.Unit;
import soot.dex.DexBody;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

public class SparseSwitchInstruction extends SwitchInstruction {

    public SparseSwitchInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    protected Stmt switchStatement(DexBody body, Instruction targetData, Local key) {
        SparseSwitchDataPseudoInstruction i = (SparseSwitchDataPseudoInstruction) targetData;
        int[] targetAddresses = i.getTargets();
        List<Immediate> lookupValues = new ArrayList<Immediate>();
        for(int k : i.getKeys())
            lookupValues.add(IntConstant.v(k));

        // the default target always follows the switch statement
        int defaultTargetAddress = codeAddress + instruction.getSize(codeAddress);
        Unit defaultTarget = body.instructionAtAddress(defaultTargetAddress).getUnit();
        List<Unit> targets = new ArrayList<Unit>();
        for(int address : targetAddresses)
            targets.add(body.instructionAtAddress(codeAddress + address).getUnit());

        return Jimple.v().newLookupSwitchStmt(key, lookupValues, targets, defaultTarget);
    }
}
