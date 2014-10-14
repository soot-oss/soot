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

import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import soot.coffi.ConstantPoolReader;
import soot.coffi.cp_info;

public class SuperClassReader {

  public static String read(byte[] classContents){
    try {
      ByteArrayInputStream byteIn = new ByteArrayInputStream(classContents);
      DataInputStream dataIn = new DataInputStream(byteIn);

      long magic = dataIn.readInt();
      int majorVersion = dataIn.readUnsignedShort();
      int minorVersion = dataIn.readUnsignedShort();
      int constantPoolCount = dataIn.readUnsignedShort();
      cp_info[] cpool = ConstantPoolReader.v().readConstantPoolUTF8s(dataIn, constantPoolCount);
      int accessFlags = dataIn.readUnsignedShort();
      int thisClass = dataIn.readUnsignedShort();
      int superClass = dataIn.readUnsignedShort();

      dataIn.close();

      if(superClass == 0){
        return "";
      } else {
        return ConstantPoolReader.v().get(superClass, cpool);
      }

    } catch(Exception ex){
      throw new RuntimeException(ex);
    }
  }
}
