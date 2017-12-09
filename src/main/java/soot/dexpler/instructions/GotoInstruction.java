/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 * 
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 * 
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

package soot.dexpler.instructions;

import org.jf.dexlib2.iface.instruction.Instruction;

import soot.dexpler.DexBody;
import soot.jimple.GotoStmt;
import soot.jimple.Jimple;

public class GotoInstruction extends JumpInstruction implements DeferableInstruction {
    public GotoInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        // check if target instruction has been jimplified
        if (getTargetInstruction(body).getUnit() != null) {
            body.add(gotoStatement());
            return;
        }
        // set marker unit to swap real gotostmt with otherwise
        body.addDeferredJimplification(this);
        markerUnit = Jimple.v().newNopStmt();
        unit = markerUnit;
        body.add(markerUnit);
    }

    public void deferredJimplify(DexBody body) {
        body.getBody().getUnits().insertAfter(gotoStatement(), markerUnit);
    }

    private GotoStmt gotoStatement() {
        GotoStmt go = Jimple.v().newGotoStmt(targetInstruction.getUnit());
        setUnit(go);
        addTags(go);
        return go;
    }


}
