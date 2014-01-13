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

import java.util.List;

import org.jf.dexlib2.iface.instruction.Instruction;

import soot.Local;
import soot.dexpler.DexBody;
import soot.jimple.Jimple;

public class InvokeInterfaceInstruction extends MethodInvocationInstruction {

    public InvokeInterfaceInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        // use Nop as begin marker
//        NopStmt nop = Jimple.v().newNopStmt();
//        defineBlock(nop);
//        tagWithLineNumber(nop);
//        body.add(nop);
//        beginUnit = nop;

        List<Local> parameters = buildParameters(body, false);
        invocation = Jimple.v().newInterfaceInvokeExpr(parameters.get(0),
                                                       getSootMethodRef(),
                                                       parameters.subList(1, parameters.size()));
        body.setDanglingInstruction(this);
        // setUnit() is called in MethodInvocationInstruction
    }
}
