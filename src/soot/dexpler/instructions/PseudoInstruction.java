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

import soot.dexpler.DexBody;

public abstract class PseudoInstruction extends DexlibAbstractInstruction {

  public PseudoInstruction(Instruction instruction, int codeAddress) {
    super(instruction, codeAddress);
  }

  int dataFirstByte = -1;
  int dataLastByte = -1;
  int dataSize = -1;
  byte[] data = null;
  boolean loaded = false;

  public boolean isLoaded() {
    return loaded;
  }

  public void setLoaded(boolean loaded) {
    this.loaded = loaded;
  }

  public byte[] getData() {
    return data;
  }

  protected void setData(byte[] data) {
    this.data = data;
  }

  public int getDataFirstByte() {
    if (dataFirstByte == -1)
      throw new RuntimeException("Error: dataFirstByte was not set!");
    return dataFirstByte;
  }

  protected void setDataFirstByte(int dataFirstByte) {
    this.dataFirstByte = dataFirstByte;
  }

  public int getDataLastByte() {
    if (dataLastByte == -1)
      throw new RuntimeException("Error: dataLastByte was not set!");
    return dataLastByte;
  }

  protected void setDataLastByte(int dataLastByte) {
    this.dataLastByte = dataLastByte;
  }

  public int getDataSize() {
    if (dataSize == -1)
      throw new RuntimeException("Error: dataFirstByte was not set!");
    return dataSize;
  }

  protected void setDataSize(int dataSize) {
    this.dataSize = dataSize;
  }

  public abstract void computeDataOffsets(DexBody body);

  
  
  
  
}
