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

  public abstract void computeDataOffsets(DexBody body) throws Exception;

  
  
  
  
}
