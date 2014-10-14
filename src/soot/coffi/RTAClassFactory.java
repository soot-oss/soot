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

package soot.coffi;

import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import soot.coffi.ConstantPoolReader;
import soot.rtaclassload.RTAClass;
import soot.rtaclassload.RTAMethod;
import soot.rtaclassload.RTAField;
import soot.rtaclassload.RTAType;
import soot.rtaclassload.RTAClassLoader;
import soot.rtaclassload.StringNumbers;

public class RTAClassFactory {

  private static final long MAGIC = 0xCAFEBABEL;

  public RTAClassFactory(){
  }

  public RTAClass create(byte[] data){
    try {
      DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(data));

      long magic = dataIn.readInt() & 0xFFFFFFFFL;
      if (magic != MAGIC) {
        System.out.println("magic wrong: "+magic+" "+MAGIC);
        return null;
      }
      int minorVersion = dataIn.readUnsignedShort();
      int majorVersion = dataIn.readUnsignedShort();
      int constantPoolCount = dataIn.readUnsignedShort();
      cp_info[] cpool = readConstantPool(dataIn, constantPoolCount);
      if(cpool == null){
        System.out.println("cpool == null");
        return null;
      }

      int accessFlags = dataIn.readUnsignedShort();
      int thisClass = dataIn.readUnsignedShort();
      int superClass = dataIn.readUnsignedShort();
      int interfacesCount = dataIn.readUnsignedShort();
      RTAType[] interfaces = new RTAType[interfacesCount];
      for(int i=0; i < interfacesCount; i++){
        int iface = dataIn.readUnsignedShort();
        String interfaceName = ConstantPoolReader.v().get(iface, cpool);
        interfaces[i] = RTAType.create(interfaceName);
      }

      int fieldCount = dataIn.readUnsignedShort();
      field_info[] fi = readFields(dataIn, fieldCount);

      int methodCount = dataIn.readUnsignedShort();
      method_info[] mi = readMethods(dataIn, methodCount, cpool);

      String classNameStr = ConstantPoolReader.v().get(thisClass, cpool);
      boolean hasSuperClass = true;
      String superClassNameStr = "";
      if(superClass == 0){
        hasSuperClass = false;
      } else {
        superClassNameStr = ConstantPoolReader.v().get(superClass, cpool);
      }

      RTAType className = RTAType.create(classNameStr);
      RTAType superClassName = RTAType.create(superClassNameStr);

      return new RTAClass(className, hasSuperClass, superClassName,
        interfaces, accessFlags, mi, fi, cpool, data);
    } catch(IOException ex){
      throw new RuntimeException(ex);
    }
  }

  public RTAClass create(InputStream is) {
    try {
      byte[] data = readData(is);
      return create(data);
    } catch(IOException ex){
      throw new RuntimeException(ex);
    }
  }

  private byte[] readData(InputStream is) throws IOException {
    List<byte[]> buffers = new ArrayList<byte[]>();
    List<Integer> buffer_lens = new ArrayList<Integer>();
    int total_len = 0;
    while(true){
      byte[] buffer = new byte[4096];
      int len = is.read(buffer);
      if(len == -1){
        break;
      }
      buffers.add(buffer);
      buffer_lens.add(len);
      total_len += len;
    }
    byte[] data = new byte[total_len];
    int offset = 0;
    for(int i = 0; i < buffers.size(); ++i){
      byte[] buffer = buffers.get(i);
      int len = buffer_lens.get(i);
      System.arraycopy(buffer, 0, data, offset, len);
      offset += len;
    }
    return data;
  }

  private cp_info[] readConstantPool(DataInputStream d, int constant_pool_count) throws IOException {
    byte tag;
    cp_info cp = null;
    int i;
    boolean skipone;   // set if next cp entry is to be skipped

    cp_info[] constant_pool = new cp_info[constant_pool_count];
    //Instruction.constant_pool = constant_pool;
    skipone = false;

    for (i=1;i<constant_pool_count;i++) {
      if (skipone) {
        skipone = false;
        continue;
      }
      tag = (byte)d.readUnsignedByte();
      switch(tag) {
        case cp_info.CONSTANT_Class:
          cp = new CONSTANT_Class_info();
          ((CONSTANT_Class_info)cp).name_index = d.readUnsignedShort();
          cp.tag = tag;
          break;
        case cp_info.CONSTANT_Fieldref:
          d.skipBytes(4);
          break;
        case cp_info.CONSTANT_Methodref:
          d.skipBytes(4);
          break;
        case cp_info.CONSTANT_InterfaceMethodref:
          d.skipBytes(4);
          break;
        case cp_info.CONSTANT_String:
          d.skipBytes(2);
          break;
        case cp_info.CONSTANT_Integer:
          d.skipBytes(4);
          break;
        case cp_info.CONSTANT_Float:
          d.skipBytes(4);
          break;
        case cp_info.CONSTANT_Long:
          d.skipBytes(8);
          skipone = true;  // next entry needs to be skipped
          break;
        case cp_info.CONSTANT_Double:
          d.skipBytes(8);
          skipone = true;  // next entry needs to be skipped
          break;
        case cp_info.CONSTANT_NameAndType:
          d.skipBytes(4);
          break;
        case cp_info.CONSTANT_Utf8:
          // If an equivalent CONSTANT_Utf8 already exists, we return
          // the pre-existing one and allow cputf8 to be GC'd.
          int len = d.readUnsignedShort();
          byte[] buffer = new byte[len];
          d.readFully(buffer);
          byte[] bytes = new byte[len+2];
          bytes[0] = (byte)(len>>8);
          bytes[1] = (byte)(len & 0xff);
          for(int j = 0; j < len; ++j){
            bytes[2+j] = buffer[j];
          }
          cp = (cp_info) new CONSTANT_Utf8_info(bytes);
          cp.tag = tag;
          break;
        case cp_info.CONSTANT_MethodHandle:
          d.skipBytes(3);
          break;
        case cp_info.CONSTANT_InvokeDynamic:
          d.skipBytes(4);
          break;
        default:
          return null;
      }
      constant_pool[i] = cp;
    }
    return constant_pool;
  }

  private field_info[] readFields(DataInputStream d, int fields_count) throws IOException {
    field_info[] ret = new field_info[fields_count];
    for (int i = 0; i < fields_count; i++) {
      field_info fi = new field_info();
      fi.access_flags = d.readUnsignedShort();
      fi.name_index = d.readUnsignedShort();
      fi.descriptor_index = d.readUnsignedShort();
      fi.attributes_count = d.readUnsignedShort();
      if(fi.attributes_count > 0){
        fi.attributes = new attribute_info[0];
        readAttributes(d,fi.attributes_count);
        fi.attributes_count = 0;
      }

      ret[i] = fi;
    }
    return ret;
  }

  private void readAttributes(DataInputStream d, int attributes_count) throws IOException {
    for (int i = 0; i < attributes_count; i++) {
      int name = d.readUnsignedShort();  // read attribute name before allocating
      long len = d.readInt() & 0xFFFFFFFFL;
      d.skipBytes((int) len);
    }
  }

  private method_info[] readMethods(DataInputStream d, int methods_count,
    cp_info[] cpool) throws IOException {

    method_info[] methods = new method_info[methods_count];

    for(int i = 0; i < methods_count; i++) {
      method_info mi = new method_info();
      mi.access_flags = d.readUnsignedShort();
      mi.name_index = d.readUnsignedShort();
      mi.descriptor_index = d.readUnsignedShort();
      mi.attributes_count = d.readUnsignedShort();
      CONSTANT_Utf8_info ci = (CONSTANT_Utf8_info) cpool[mi.name_index];
      if(mi.attributes_count > 0){
        mi.attributes = new attribute_info[mi.attributes_count];
        readAttributes(d,mi.attributes_count);
      }
      methods[i] = mi;
    }
    return methods;
  }
}
