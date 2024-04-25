package soot.dexpler.instructions;

import org.jf.dexlib2.iface.instruction.Instruction;

import soot.dexpler.DexBody;
import soot.dexpler.tags.SpecialInvokeTypeTag;
import soot.dexpler.tags.SpecialInvokeTypeTag.Type;

public class InvokeSpecialSuperInstruction extends InvokeSpecialInstruction {

  public InvokeSpecialSuperInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  public void finalize(DexBody body, DexlibAbstractInstruction successor) {
    super.finalize(body, successor);
    getUnit().addTag(new SpecialInvokeTypeTag(Type.SUPER));
  }
}
