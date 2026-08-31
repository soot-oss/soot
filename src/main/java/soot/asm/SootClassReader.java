/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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
