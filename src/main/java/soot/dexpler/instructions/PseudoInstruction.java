// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import org.jf.dexlib2.iface.instruction.Instruction;

import soot.dexpler.DexBody;

public abstract class PseudoInstruction extends DexlibAbstractInstruction {

  public PseudoInstruction(Instruction instruction, int codeAddress) {
    super(instruction, codeAddress);
  }

  protected int dataFirstByte = -1;
  protected int dataLastByte = -1;
  protected int dataSize = -1;
  protected byte[] data = null;
  protected boolean loaded = false;

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
    if (dataFirstByte == -1) {
      throw new RuntimeException("Error: dataFirstByte was not set!");
    }
    return dataFirstByte;
  }

  protected void setDataFirstByte(int dataFirstByte) {
    this.dataFirstByte = dataFirstByte;
  }

  public int getDataLastByte() {
    if (dataLastByte == -1) {
      throw new RuntimeException("Error: dataLastByte was not set!");
    }
    return dataLastByte;
  }

  protected void setDataLastByte(int dataLastByte) {
    this.dataLastByte = dataLastByte;
  }

  public int getDataSize() {
    if (dataSize == -1) {
      throw new RuntimeException("Error: dataFirstByte was not set!");
    }
    return dataSize;
  }

  protected void setDataSize(int dataSize) {
    this.dataSize = dataSize;
  }

  public abstract void computeDataOffsets(DexBody body);

}
