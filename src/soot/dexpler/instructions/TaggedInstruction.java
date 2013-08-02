// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler.instructions;

import org.jf.dexlib2.iface.instruction.Instruction;

import soot.tagkit.Tag;

public abstract class TaggedInstruction extends DexlibAbstractInstruction {

  private Tag instructionTag = null;

  public TaggedInstruction(Instruction instruction, int codeAddress) {
    super(instruction, codeAddress);
    // TODO Auto-generated constructor stub
  }

  public void setTag (Tag t) {
    instructionTag = t;
  }
  
  public Tag getTag () {
    if (instructionTag == null) {
      System.err.println("Must tag instruction first! (0x"+ Integer.toHexString(codeAddress) +": "+ instruction +")");
      System.exit(-1);
    }
    return instructionTag;
  }

}
