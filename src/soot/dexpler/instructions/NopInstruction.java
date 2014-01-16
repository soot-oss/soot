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
import soot.jimple.Jimple;
import soot.jimple.NopStmt;

public class NopInstruction extends DexlibAbstractInstruction {

    public NopInstruction (Instruction instruction, int codeAddress) {
        super(instruction, codeAddress);
    }

    public void jimplify (DexBody body) {
        NopStmt nop = Jimple.v().newNopStmt();
        setUnit(nop);
        addTags(nop);
        body.add(nop);
    }
    

}

