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

import java.util.ArrayList;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.OffsetInstruction;
import org.jf.dexlib.Code.Format.SparseSwitchDataPseudoInstruction;
import org.jf.dexlib.Util.ByteArrayAnnotatedOutput;

import soot.Immediate;
import soot.IntType;
import soot.Local;
import soot.Unit;
import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;

public class SparseSwitchInstruction extends SwitchInstruction {

    LookupSwitchStmt switchStmt = null;
  
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

        switchStmt = Jimple.v().newLookupSwitchStmt(key, lookupValues, targets, defaultTarget);
        setUnit(switchStmt);
        return switchStmt;
    }
    
    @Override
    public void computeDataOffsets(DexBody body) {
      int offset = ((OffsetInstruction) instruction).getTargetAddressOffset();
      int targetAddress = codeAddress + offset;
      Instruction targetData = body.instructionAtAddress(targetAddress).instruction;
      SparseSwitchDataPseudoInstruction ssInst = (SparseSwitchDataPseudoInstruction) targetData;
      int[] targetAddresses = ssInst.getTargets();
      int size = targetAddresses.length * 2; // @ are on 32bits
      
      // From org.jf.dexlib.Code.Format.SparseSwitchDataPseudoInstruction we learn
      // that there are 2 bytes after the magic number that we have to jump.
      // 2 bytes to jump = address + 1
      //
      //      out.writeByte(0x00); // magic
      //      out.writeByte(0x02); // number
      //      out.writeShort(targets.length); // 2 bytes
      //      out.writeInt(firstKey);
      
      setDataFirstByte (targetAddress + 1);
      setDataLastByte (targetAddress + 1 + size - 1);
      setDataSize (size);
      
      ByteArrayAnnotatedOutput out = new ByteArrayAnnotatedOutput();
      ssInst.write(out, targetAddress);

      byte[] outa = out.getArray();
      byte[] data = new byte[outa.length-2];
      for (int i=2; i<outa.length; i++) {
        data[i-2] = outa[i];
      }
      setData (data);
    }
    
    public void getConstraint(IDalvikTyper dalvikTyper) {
      dalvikTyper.setType(switchStmt.getKeyBox(), IntType.v());
    }
    
}
