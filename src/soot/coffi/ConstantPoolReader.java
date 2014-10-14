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
import java.util.Map;

import java.io.DataInputStream;
import java.io.IOException;
import soot.rtaclassload.DescriptorParser;
import soot.rtaclassload.RTAClassLoader;
import soot.rtaclassload.ClassName;

import org.apache.commons.lang3.StringEscapeUtils;

public class ConstantPoolReader {

  private static ConstantPoolReader m_instance;

  public static ConstantPoolReader v(){
    if(m_instance == null){
      m_instance = new ConstantPoolReader();
    }
    return m_instance;
  }

  private ConstantPoolReader(){
  }

  public String convertClass(String class_name){
    ClassName class_parser = new ClassName();
    class_parser.constantPoolParse(class_name);

    String raw_name = class_parser.getClassName();

    //then we can reverse remap
    Map<String, String> reverse_remap = RTAClassLoader.v().getReverseClassRemappings();
    if(reverse_remap.containsKey(raw_name)){
      class_parser.setClassName(reverse_remap.get(raw_name));
    }

    return class_parser.toJavaString();
  }

  public void remapConstantPool(cp_info[] constantPool){
    Map<String, String> reverse_map = RTAClassLoader.v().getReverseClassRemappings();

    for(int i = 0; i < constantPool.length; ++i){
      cp_info entry = constantPool[i];
      if(entry instanceof CONSTANT_Class_info){
        CONSTANT_Class_info class_info = (CONSTANT_Class_info) entry;
        int name_index = class_info.name_index;

        CONSTANT_Utf8_info name_entry = (CONSTANT_Utf8_info) constantPool[name_index];
        //TODO: if this string is used for more than just the class, split
        //into two entries. One will be remmaped, the other will not.
        String class_name = name_entry.convert();

        ClassName class_parser = new ClassName();
        class_parser.constantPoolParse(class_name);

        String raw_name = class_parser.getClassName();

        if(reverse_map.containsKey(raw_name)){
          class_parser.setClassName(reverse_map.get(raw_name));
          String new_name = class_parser.toConstantPoolString();

          System.out.println("remapping: "+class_name+" to "+new_name);

          byte[] utf8 = CONSTANT_Utf8_info.toUtf8(new_name);
          constantPool[name_index] = new CONSTANT_Utf8_info(utf8);
        }
      }
    }
  }

  private String escapeQuotes(String str){
    return StringEscapeUtils.escapeJava(str);
  }

  private int count(cp_info[] constant_pool){
    int ret = 0;
    for(cp_info info : constant_pool){
      if(info == null){
        ret++;
      }
    }
    return ret;
  }
  public String getRaw(int index, cp_info[] constant_pool, boolean quoteString) {
    cp_info entry = constant_pool[index];
    if(entry instanceof CONSTANT_Class_info){
      CONSTANT_Class_info class_info = (CONSTANT_Class_info) entry;
      String class_name = getRaw(class_info.name_index, constant_pool, false);
      return class_name;
    } else if(entry instanceof CONSTANT_Methodref_info){
      CONSTANT_Methodref_info methodref_info = (CONSTANT_Methodref_info) entry;
      String class_name = getRaw(methodref_info.class_index, constant_pool, false);
      CONSTANT_NameAndType_info nameandtype_info = (CONSTANT_NameAndType_info) constant_pool[methodref_info.name_and_type_index];
      String name = getRaw(nameandtype_info.name_index, constant_pool, false);
      String descriptor = getRaw(nameandtype_info.descriptor_index, constant_pool, false);
      return class_name+"/"+name+descriptor;
    } else if(entry instanceof CONSTANT_Utf8_info){
      CONSTANT_Utf8_info utf8_info = (CONSTANT_Utf8_info) entry;
      String str = utf8_info.convert();
      if(quoteString){
        str = "\""+escapeQuotes(str)+"\"";
      }
      return str;
    } else if(entry instanceof CONSTANT_Fieldref_info){
      CONSTANT_Fieldref_info fieldref_info = (CONSTANT_Fieldref_info) entry;
      String class_name = getRaw(fieldref_info.class_index, constant_pool, false);
      CONSTANT_NameAndType_info nameandtype_info = (CONSTANT_NameAndType_info) constant_pool[fieldref_info.name_and_type_index];
      String name = getRaw(nameandtype_info.name_index, constant_pool, false);
      String descriptor = getRaw(nameandtype_info.descriptor_index, constant_pool, false);
      return class_name+"/"+name+" "+descriptor;
    } else if(entry instanceof CONSTANT_String_info){
      CONSTANT_String_info string_info = (CONSTANT_String_info) entry;
      return getRaw(string_info.string_index, constant_pool, true);
    } else if(entry instanceof CONSTANT_InterfaceMethodref_info){
      CONSTANT_InterfaceMethodref_info methodref_info = (CONSTANT_InterfaceMethodref_info) entry;
      String class_name = getRaw(methodref_info.class_index, constant_pool, false);
      CONSTANT_NameAndType_info nameandtype_info = (CONSTANT_NameAndType_info) constant_pool[methodref_info.name_and_type_index];
      String name = getRaw(nameandtype_info.name_index, constant_pool, false);
      String descriptor = getRaw(nameandtype_info.descriptor_index, constant_pool, false);
      return class_name+"/"+name+descriptor;
    } else if(entry instanceof CONSTANT_Integer_info){
      CONSTANT_Integer_info integer_info = (CONSTANT_Integer_info) entry;
      return Integer.toString((int) integer_info.bytes);
    } else if(entry instanceof CONSTANT_Long_info){
      CONSTANT_Long_info long_info = (CONSTANT_Long_info) entry;
      return Long.toString((int) long_info.convert());
    } else if(entry instanceof CONSTANT_Float_info){
      CONSTANT_Float_info float_info = (CONSTANT_Float_info) entry;
      String ret = Float.toString(float_info.convert());
      if(ret.equals("Infinity")){
        return "+FloatInfinity";
      } else if(ret.equals("-Infinity")){
        return "-FloatInfinity";
      } else if(ret.equals("NaN")){
        return "+FloatNaN";
      } else {
        return ret;
      }
    } else if(entry instanceof CONSTANT_Double_info){
      CONSTANT_Double_info double_info = (CONSTANT_Double_info) entry;
      String ret = Double.toString(double_info.convert());
      if(ret.equals("Infinity")){
        return "+DoubleInfinity";
      } else if(ret.equals("-Infinity")){
        return "-DoubleInfinity";
      } else if(ret.equals("NaN")){
        return "+DoubleNaN";
      } else {
        return ret;
      }
    } else if(entry instanceof CONSTANT_NameAndType_info){
      CONSTANT_NameAndType_info nameandtype_info = (CONSTANT_NameAndType_info) entry;
      String name = getRaw(nameandtype_info.name_index, constant_pool, false);
      String descriptor = getRaw(nameandtype_info.descriptor_index, constant_pool, false);
      return name+" "+descriptor;
    } else {
      throw new RuntimeException("unknown type: "+entry);
    }
  }


  public String get(int index, cp_info[] constant_pool){
    cp_info entry = constant_pool[index];
    if(entry instanceof CONSTANT_Class_info){
      CONSTANT_Class_info class_info = (CONSTANT_Class_info) entry;
      String class_name = get(class_info.name_index, constant_pool);
      return convertClass(class_name);
    } else if(entry instanceof CONSTANT_Methodref_info){
      CONSTANT_Methodref_info methodref_info = (CONSTANT_Methodref_info) entry;
      String class_name = get(methodref_info.class_index, constant_pool);
      class_name = convertClass(class_name);
      String subsig = getNameAndTypeInfo(methodref_info.name_and_type_index,
        constant_pool, true);
      String ret = "<"+class_name+": "+subsig+">";
      return ret;
    } else if(entry instanceof CONSTANT_Utf8_info){
      CONSTANT_Utf8_info utf8_info = (CONSTANT_Utf8_info) entry;
      String str = utf8_info.convert();
      return str;
    } else if(entry instanceof CONSTANT_Fieldref_info){
      CONSTANT_Fieldref_info fieldref_info = (CONSTANT_Fieldref_info) entry;
      String class_name = get(fieldref_info.class_index, constant_pool);
      class_name = convertClass(class_name);
      String type = getNameAndTypeInfo(fieldref_info.name_and_type_index,
        constant_pool, false);
      return "<"+class_name+": "+type+">";
    } else if(entry instanceof CONSTANT_String_info){
      CONSTANT_String_info string_info = (CONSTANT_String_info) entry;
      return get(string_info.string_index, constant_pool);
    } else if(entry instanceof CONSTANT_InterfaceMethodref_info){
      CONSTANT_InterfaceMethodref_info methodref_info = (CONSTANT_InterfaceMethodref_info) entry;
      String class_name = get(methodref_info.class_index, constant_pool);
      class_name = convertClass(class_name);
      String subsig = getNameAndTypeInfo(methodref_info.name_and_type_index,
        constant_pool, true);
      String ret = "<"+class_name+": "+subsig+">";
      return ret;
    } else if(entry instanceof CONSTANT_Integer_info){
      CONSTANT_Integer_info integer_info = (CONSTANT_Integer_info) entry;
      return Integer.toString((int) integer_info.bytes);
    } else if(entry instanceof CONSTANT_Long_info){
      CONSTANT_Long_info long_info = (CONSTANT_Long_info) entry;
      return Long.toString((int) long_info.convert());
    } else if(entry instanceof CONSTANT_Float_info){
      CONSTANT_Float_info float_info = (CONSTANT_Float_info) entry;
      return Float.toString(float_info.convert());
    } else if(entry instanceof CONSTANT_Double_info){
      CONSTANT_Double_info double_info = (CONSTANT_Double_info) entry;
      return Double.toString(double_info.convert());
    } else {
      throw new RuntimeException("unknown type: "+entry);
    }
  }

  public String getType(int index, cp_info[] constant_pool){
    cp_info entry = constant_pool[index];
    if(entry instanceof CONSTANT_Class_info){
      return "class_ref";
    } else if(entry instanceof CONSTANT_Methodref_info){
      return "method_ref";
    } else if(entry instanceof CONSTANT_Utf8_info){
      return "string";
    } else if(entry instanceof CONSTANT_Fieldref_info){
      return "field_ref";
    } else if(entry instanceof CONSTANT_String_info){
      return "string";
    } else if(entry instanceof CONSTANT_InterfaceMethodref_info){
      return "method_ref";
    } else if(entry instanceof CONSTANT_Integer_info){
      return "int";
    } else if(entry instanceof CONSTANT_Long_info){
      return "long";
    } else if(entry instanceof CONSTANT_Float_info){
      return "float";
    } else if(entry instanceof CONSTANT_Double_info){
      return "double";
    } else {
      throw new RuntimeException("unknown type: "+entry);
    }
  }

  private String getNameAndTypeInfo(int index, cp_info[] constant_pool,
    boolean method){

    CONSTANT_NameAndType_info name_and_type_info = (CONSTANT_NameAndType_info) constant_pool[index];
    String name = get(name_and_type_info.name_index, constant_pool);
    String desc = get(name_and_type_info.descriptor_index, constant_pool);

    if(method){
      String return_type = DescriptorParser.v().parseMethodDesc_return(desc);
      String[] params = DescriptorParser.v().parseMethodDesc_params(desc);
      String params_full = "";
      for(int i = 0; i < params.length; ++i){
        params_full += params[i];
        if(i < params.length - 1){
          params_full += ",";
        }
      }
      String ret = return_type+" "+name+"("+params_full+")";
      return ret;
    } else {
      return DescriptorParser.v().parseDesc(desc)+" "+name;
    }
  }

  public cp_info[] readConstantPoolUTF8s(DataInputStream d, int constant_pool_count) throws IOException {
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
}
