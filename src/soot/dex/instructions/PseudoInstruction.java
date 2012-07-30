package soot.dex.instructions;

import org.jf.dexlib.Code.Instruction;

import soot.dex.DexBody;

public abstract class PseudoInstruction extends DexlibAbstractInstruction {

  public PseudoInstruction(Instruction instruction, int codeAddress) {
    super(instruction, codeAddress);
    // TODO Auto-generated constructor stub
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
    return dataFirstByte;
  }

  protected void setDataFirstByte(int dataFirstByte) {
    this.dataFirstByte = dataFirstByte;
  }

  public int getDataLastByte() {
    return dataLastByte;
  }

  protected void setDataLastByte(int dataLastByte) {
    this.dataLastByte = dataLastByte;
  }

  public int getDataSize() {
    return dataSize;
  }

  protected void setDataSize(int dataSize) {
    this.dataSize = dataSize;
  }

  public abstract void computeDataOffsets(DexBody body);

  
  
  
  
}
