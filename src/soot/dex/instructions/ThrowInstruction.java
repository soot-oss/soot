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

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction11x;

import soot.dex.DexBody;
import soot.dex.IDalvikTyper;
import soot.jimple.Jimple;
import soot.jimple.ThrowStmt;

public class ThrowInstruction extends DexlibAbstractInstruction {

    public ThrowInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        Instruction11x throwInstruction = (Instruction11x)instruction;
        ThrowStmt throwStmt = Jimple.v().newThrowStmt(body.getRegisterLocal(throwInstruction.getRegisterA()));
        defineBlock(throwStmt);
        tagWithLineNumber(throwStmt);
        body.add(throwStmt);
        if (IDalvikTyper.ENABLE_DVKTYPER) {
          body.dalvikTyper.setObjectType(throwStmt.getOpBox());
        }
    }
}
