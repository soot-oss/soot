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
import org.jf.dexlib2.iface.instruction.formats.Instruction22t;

import soot.Local;
import soot.dexpler.Debug;
import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.BinopExpr;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;

public class IfTestInstruction extends ConditionalJumpInstruction {

    public IfTestInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    protected IfStmt ifStatement(DexBody body) {
        Instruction22t i = (Instruction22t) instruction;
        Local one = body.getRegisterLocal(i.getRegisterA());
        Local other = body.getRegisterLocal(i.getRegisterB());
        BinopExpr condition = getComparisonExpr(one, other);
        IfStmt jif = Jimple.v().newIfStmt(condition, targetInstruction.getUnit());
        // setUnit() is called in ConditionalJumpInstruction

		if (IDalvikTyper.ENABLE_DVKTYPER) {
		    Debug.printDbg(IDalvikTyper.DEBUG, "constraint if: "+ jif +" condition: "+ condition);
		    DalvikTyper.v().addConstraint(condition.getOp1Box(), condition.getOp2Box());
        }
        
        
        return jif;
        
    }
}
