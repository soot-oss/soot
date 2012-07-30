package soot.dex.instructions;
import org.jf.dexlib.Code.Instruction;

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
