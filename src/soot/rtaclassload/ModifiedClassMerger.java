/* Soot - a J*va Optimization Framework
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

package soot.rtaclassload;

import soot.SootClass;
import soot.jimple.JasminClass;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStreamWriter;

public class ModifiedClassMerger {

  public jasmin.ClassFile createClassFile(String jasminString){
    ByteArrayInputStream inputStream = new ByteArrayInputStream(jasminString.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      jasmin.ClassFile classFile = new jasmin.ClassFile();
      classFile.readJasmin(inputStream, "Jasmin", false);
      inputStream.close();
      if (classFile.errorCount() > 0) {
        throw new RuntimeException();
      }
      return classFile;
    } catch(Exception ex){
      ex.printStackTrace();
      return null;
    }
  }

  public BytecodeFile merge(String jarFilename, String className, SootClass sootClass, byte[] originalContents){

    String filename = className.replace(".", "/");
    filename += ".class";

    try {
      ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
      PrintWriter outputWriter = new PrintWriter(new OutputStreamWriter(streamOut));
      JasminClass jasminClass = new JasminClass(sootClass);
      jasminClass.print(outputWriter);
      outputWriter.flush();
      String generatedJasmin = new String(streamOut.toByteArray());
      jasmin.ClassFile generatedClassFile = createClassFile(generatedJasmin);

      if(originalContents == null){
        ByteArrayOutputStream binaryOut = new ByteArrayOutputStream();
        generatedClassFile.write(binaryOut);
        binaryOut.flush();

        return new BytecodeFile("", filename, binaryOut.toByteArray());
      } else {
        JasminEmitter emitter = new JasminEmitter();
        String originalJasmin = emitter.emitFromClassFile(originalContents);
        jasmin.ClassFile originalClassFile = createClassFile(originalJasmin);

        originalClassFile.merge(generatedClassFile);

        ByteArrayOutputStream binaryOut = new ByteArrayOutputStream();
        originalClassFile.write(binaryOut);
        binaryOut.flush();

        return new BytecodeFile(jarFilename, filename, binaryOut.toByteArray());
      }
    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
}
