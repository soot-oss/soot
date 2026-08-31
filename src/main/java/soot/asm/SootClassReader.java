package soot.asm;

import java.io.IOException;
import java.io.InputStream;

import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

public class SootClassReader extends ClassReader {

  private BytecodeOffsetReceiver bytecodeOffsetReceiver;

  public SootClassReader(InputStream inputStream) throws IOException {
    super(inputStream);
  }
  
  @Override
  protected void readBytecodeInstructionOffset(int bytecodeOffset) {
    super.readBytecodeInstructionOffset(bytecodeOffset);
    if (bytecodeOffsetReceiver != null) {
      bytecodeOffsetReceiver.bytecodeOffsetChanged(bytecodeOffset);
    }
    
  }
  
  @Override
  public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
    if (classVisitor instanceof BytecodeOffsetReceiver) {
      this.bytecodeOffsetReceiver = (BytecodeOffsetReceiver) classVisitor;
    }
    super.accept(classVisitor, attributePrototypes, parsingOptions);
  }

}
