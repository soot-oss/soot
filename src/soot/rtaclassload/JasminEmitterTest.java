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

import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class JasminEmitterTest {

  private List<String> findBootJars(){
    List<String> ret = new ArrayList<String>();
    String bootPath = System.getProperty("sun.boot.class.path");
    String[] tokens = bootPath.split(File.pathSeparator);
    for(String token : tokens){
      if(token.endsWith(".jar")){
        File file = new File(token);
        if(file.exists()){
          ret.add(token);
        }
      }
    }
    return ret;
  }

  private boolean testItem(byte[] classContents, String jasminString){
    ByteArrayInputStream inputStream = new ByteArrayInputStream(jasminString.getBytes());
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      jasmin.ClassFile classFile = new jasmin.ClassFile();
      classFile.readJasmin(inputStream, "Jasmin", false);
      inputStream.close();
      if (classFile.errorCount() > 0) {
        throw new RuntimeException();
      }

      classFile.write(outputStream);
      outputStream.flush();
      return true;
    } catch(Exception ex){
      ex.printStackTrace();
      return false;
    }
  }

  private void test(String bootJar){
    System.out.println("testing: "+bootJar+"...");
    try {
      JarInputStream inputStream = new JarInputStream(new FileInputStream(bootJar));
      while(true){
        JarEntry entry = inputStream.getNextJarEntry();
        if(entry == null){
          System.out.println("passed");
          inputStream.close();
          return;
        }
        String filename = entry.getName();
        if(filename.endsWith(".class")){
          byte[] classContents = readFully(inputStream);
          System.out.println(filename);

          JasminEmitter emitter = new JasminEmitter();
          String jasminString = emitter.emitFromClassFile(classContents);
          boolean passed = testItem(classContents, jasminString);
          if(passed == false){
            System.out.println(jasminString);
            System.out.println("failed");
            inputStream.close();
            System.exit(0);
            return;
          }
        }
      }
    } catch(Exception ex){
      ex.printStackTrace();
      System.out.println("failed");
      System.exit(0);
    }
  }

  private byte[] readFully(JarInputStream jin) throws IOException {
    List<byte[]> returnList = new ArrayList<byte[]>();
    int returnCount = 0;
    int bufferSize = 4096;
    while(true){
      byte[] currArray = new byte[bufferSize];
      int readLength = jin.read(currArray, 0, bufferSize);
      if(readLength == -1){
        break;
      }
      byte[] sizedArray = new byte[readLength];
      System.arraycopy(currArray, 0, sizedArray, 0, readLength);
      returnList.add(sizedArray);
      returnCount += readLength;
    }
    byte[] ret = new byte[returnCount];
    int offset = 0;
    for(byte[] array : returnList){
      System.arraycopy(array, 0, ret, offset, array.length);
      offset += array.length;
    }
    return ret;
  }

  public void run(){
    List<String> bootJars = findBootJars();
    for(String bootJar : bootJars){
      test(bootJar);
    }
  }

  public static void main(String[] args){
    JasminEmitterTest tester = new JasminEmitterTest();
    tester.run();
  }
}
