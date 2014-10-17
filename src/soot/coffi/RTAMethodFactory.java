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
import java.util.Set;
import java.util.TreeSet;
import soot.rtaclassload.RTAMethod;
import soot.rtaclassload.RTAType;
import soot.rtaclassload.RTAInstruction;
import soot.rtaclassload.Operand;
import soot.rtaclassload.DescriptorParser;
import soot.rtaclassload.StringNumbers;

public class RTAMethodFactory {

  private cp_info[] m_constantPool;

  public int getMaxStack(method_info info){
    return info.code_attr.max_stack;
  }

  public int getMaxLocals(method_info info){
    return info.code_attr.max_locals;
  }

  public RTAType[] getCodeAttrExceptions(method_info methodInfo,
    cp_info[] constant_pool){

    Code_attribute code_attr = methodInfo.locate_code_attribute();
    if(code_attr == null){
      return new RTAType[0];
    }

    exception_table_entry[] ex_table = code_attr.exception_table;
    RTAType[] ret = new RTAType[ex_table.length];
    for(int i = 0; i < ex_table.length; ++i){
      int catch_type = ex_table[i].catch_type;
      if(catch_type != 0){
        String type = ConstantPoolReader.v().get(catch_type, constant_pool);
        ret[i] = RTAType.create(type);
      } else {
        ret[i] = RTAType.create("java.lang.Throwable");
      }
    }
    return ret;
  }

  private List<String> parseRawExceptionDecls(exception_table_entry[] ex_table){
    List<String> ret = new ArrayList<String>();
    for(int i = 0; i < ex_table.length; ++i){
      exception_table_entry entry = ex_table[i];
      StringBuilder builder = new StringBuilder();
      String typeString;
      if(entry.catch_type == 0){
        typeString = "all";
      } else {
        typeString = ConstantPoolReader.v().getRaw(entry.catch_type, m_constantPool, false);
      }
      builder.append(".catch ");
      builder.append(typeString);
      builder.append(" from label");
      builder.append(entry.start_pc);
      builder.append(" to label");
      builder.append(entry.end_pc);
      builder.append(" using label");
      builder.append(entry.handler_pc);
      ret.add(builder.toString());
    }
    return ret;
  }

  public List<String> parseRawInstructions(ClassFile classFile, method_info methodInfo){
    m_constantPool = classFile.constant_pool;
    List<String> instructions = new ArrayList<String>();
    Set<Integer> labels = new TreeSet<Integer>();
    Code_attribute code_attr = methodInfo.locate_code_attribute();
    exception_table_entry[] ex_table = null;
    if(code_attr != null){
      ex_table = code_attr.exception_table;
      instructions.addAll(parseRawExceptionDecls(ex_table));
      for(int i = 0; i < ex_table.length; ++i){
        exception_table_entry entry = ex_table[i];
        labels.add(entry.start_pc);
        labels.add(entry.end_pc);
        labels.add(entry.handler_pc);
      }
    }
    Instruction head = classFile.parseMethod(methodInfo);
    Instruction inst = head;
    while(inst != null){
      if(inst instanceof Instruction_intbranch){
        Instruction_intbranch inst_intbranch = (Instruction_intbranch) inst;
        labels.add(inst.originalIndex+inst_intbranch.arg_i);
      } else if(inst instanceof Instruction_longbranch){
        Instruction_longbranch inst_longbranch = (Instruction_longbranch) inst;
        labels.add(inst_longbranch.arg_i);
      } else if(inst instanceof Instruction_Tableswitch){
        Instruction_Tableswitch inst_tableswitch = (Instruction_Tableswitch) inst;
        labels.add(inst.originalIndex+inst_tableswitch.default_offset);
        for(int i = 0; i < inst_tableswitch.jump_offsets.length; ++i){
          labels.add(inst.originalIndex+inst_tableswitch.jump_offsets[i]);
        }
      } else if(inst instanceof Instruction_Lookupswitch){
        Instruction_Lookupswitch inst_lookupswitch = (Instruction_Lookupswitch) inst;
        labels.add(inst.originalIndex+inst_lookupswitch.default_offset);
        if(inst_lookupswitch.match_offsets != null){
          for(int i = 0; i < inst_lookupswitch.match_offsets.length; i += 2){
            labels.add(inst.originalIndex+inst_lookupswitch.match_offsets[i+1]);
          }
        }
      }
      inst = inst.next;
    }
    inst = head;
    while(inst != null){
      if(labels.contains(inst.originalIndex)){
        instructions.add("label"+inst.originalIndex+":");
      }
      String hinst = parseRawInstruction(inst);
      instructions.add("  "+hinst);
      inst = inst.next;
    }
    return instructions;
  }

  public List<RTAInstruction> parseInstructions(ClassFile classFile, method_info methodInfo){
    m_constantPool = classFile.constant_pool;
    List<RTAInstruction> instructions = new ArrayList<RTAInstruction>();
    Instruction inst = classFile.parseMethod(methodInfo);
    while(inst != null){
      RTAInstruction hinst = parseInstruction(inst);
      instructions.add(hinst);
      inst = inst.next;
    }
    return instructions;
  }

  public String parseRawInstruction(Instruction inst){
    String name = inst.name;
    if(inst instanceof Instruction_noargs){
      return name;
    } else if(inst instanceof Instruction_Invokeinterface){
      Instruction_Invokeinterface inst_invokeinterface = (Instruction_Invokeinterface) inst;
      String arg0 = ConstantPoolReader.v().getRaw(inst_invokeinterface.arg_i, m_constantPool, false);
      return name+" "+arg0+" "+inst_invokeinterface.nargs;
    } else if(inst instanceof Instruction_Iinc){
      Instruction_Iinc inst_iinc = (Instruction_Iinc) inst;
      return name+" "+inst_iinc.arg_b+" "+inst_iinc.arg_c;
    } else if(inst instanceof Instruction_Multianewarray){
      Instruction_Multianewarray inst_multianewarray = (Instruction_Multianewarray) inst;
      String arg0 = ConstantPoolReader.v().getRaw(inst_multianewarray.arg_i, m_constantPool, false);
      return name+" "+arg0+" "+inst_multianewarray.dims;
    } else if(inst instanceof Instruction_byte){
      Instruction_byte inst_byte = (Instruction_byte) inst;
      String arg0 = Byte.valueOf(inst_byte.arg_b).toString();
      return name+" "+arg0;
    } else if(inst instanceof Instruction_bytevar){
      Instruction_bytevar inst_bytevar = (Instruction_bytevar) inst;
      String arg0 = Integer.valueOf(inst_bytevar.arg_b).toString();
      return name+" "+arg0;
    } else if(inst instanceof Instruction_byteindex){
      if(name.equals("ldc1")){
        name = "ldc";
      }
      Instruction_byteindex inst_byteindex = (Instruction_byteindex) inst;
      String arg0 = ConstantPoolReader.v().getRaw(inst_byteindex.arg_b, m_constantPool, true);
      return name+" "+arg0;
    } else if(inst instanceof Instruction_int){
      Instruction_int inst_int = (Instruction_int) inst;
      String arg0 = Integer.valueOf(inst_int.arg_i).toString();
      return name+" "+arg0;
    } else if(inst instanceof Instruction_intvar){
      Instruction_intvar inst_intvar = (Instruction_intvar) inst;
      String arg0 = Integer.valueOf(inst_intvar.arg_i).toString();
      return name+" "+arg0;
    } else if(inst instanceof Instruction_intindex){
      if(name.equals("ldc2")){
        name = "ldc";
      } else if(name.equals("ldc2w")){
        name = "ldc2_w";
      }
      Instruction_intindex inst_intindex = (Instruction_intindex) inst;
      String arg0 = ConstantPoolReader.v().getRaw(inst_intindex.arg_i, m_constantPool, true);
      return name+" "+arg0;
    } else if(inst instanceof Instruction_intbranch){
      Instruction_intbranch inst_intbranch = (Instruction_intbranch) inst;
      String arg0 = Integer.valueOf(inst.originalIndex+inst_intbranch.arg_i).toString();
      String ret = name+" label"+arg0;
      return ret;
    } else if(inst instanceof Instruction_longbranch){
      Instruction_longbranch inst_longbranch = (Instruction_longbranch) inst;
      String arg0 = Integer.valueOf(inst_longbranch.arg_i).toString();
      String ret = name+" label"+arg0;
      return ret;
    } else if(inst instanceof Instruction_Lookupswitch){
      Instruction_Lookupswitch inst_lookupswitch = (Instruction_Lookupswitch) inst;
      String ret = name+"\n";
      for(int i = 0; i < inst_lookupswitch.npairs; ++i){
        int offset1 = inst_lookupswitch.match_offsets[i*2];
        int offset2 = inst.originalIndex+inst_lookupswitch.match_offsets[i*2+1];
        ret += offset1+": label"+offset2+"\n";
      }
      ret += "default: label"+(inst.originalIndex+inst_lookupswitch.default_offset);
      return ret;
    } else if(inst instanceof Instruction_Tableswitch){
      Instruction_Tableswitch inst_tableswitch = (Instruction_Tableswitch) inst;
      String ret = name+" "+inst_tableswitch.low+" "+inst_tableswitch.high+"\n";
      for(int i = 0; i < inst_tableswitch.jump_offsets.length; ++i){
        ret += "  label"+(inst.originalIndex+inst_tableswitch.jump_offsets[i])+"\n";
      }
      ret += "default: label"+(inst.originalIndex+inst_tableswitch.default_offset);
      return ret;
    } else if(inst instanceof Instruction_Newarray){
      Instruction_Newarray inst_newarray = (Instruction_Newarray) inst;
      String str = inst_newarray.toString(m_constantPool);
      String[] tokens = str.split(" ");
      name = tokens[0];
      return name+" "+tokens[1];
    } else {
      throw new RuntimeException("unknown instruction type: "+inst.toString());
    }
  }

  public RTAInstruction parseInstruction(Instruction inst){
    String name = inst.name;
    List<Operand> operands = new ArrayList<Operand>();
    if(inst instanceof Instruction_noargs){
      //ignore
    } else if(inst instanceof Instruction_byte){
      Instruction_byte inst_byte = (Instruction_byte) inst;
      String arg0 = Byte.valueOf(inst_byte.arg_b).toString();
      operands.add(new Operand(arg0, "byte"));
    } else if(inst instanceof Instruction_bytevar){
      Instruction_bytevar inst_bytevar = (Instruction_bytevar) inst;
      String arg0 = Integer.valueOf(inst_bytevar.arg_b).toString();
      operands.add(new Operand(arg0, "byte"));
    } else if(inst instanceof Instruction_byteindex){
      Instruction_byteindex inst_byteindex = (Instruction_byteindex) inst;
      String arg0 = ConstantPoolReader.v().get(inst_byteindex.arg_b, m_constantPool);
      String type0 = ConstantPoolReader.v().getType(inst_byteindex.arg_b, m_constantPool);
      operands.add(new Operand(arg0, type0));
    } else if(inst instanceof Instruction_int){
      Instruction_int inst_int = (Instruction_int) inst;
      String arg0 = Integer.valueOf(inst_int.arg_i).toString();
      operands.add(new Operand(arg0, "int"));
    } else if(inst instanceof Instruction_intvar){
      Instruction_intvar inst_intvar = (Instruction_intvar) inst;
      String arg0 = Integer.valueOf(inst_intvar.arg_i).toString();
      operands.add(new Operand(arg0, "int"));
    } else if(inst instanceof Instruction_intindex){
      Instruction_intindex inst_intindex = (Instruction_intindex) inst;
      String arg0 = ConstantPoolReader.v().get(inst_intindex.arg_i, m_constantPool);
      String type0 = ConstantPoolReader.v().getType(inst_intindex.arg_i, m_constantPool);
      operands.add(new Operand(arg0, type0));
    } else if(inst instanceof Instruction_intbranch){
      Instruction_intbranch inst_intbranch = (Instruction_intbranch) inst;
      String arg0 = Integer.valueOf(inst_intbranch.arg_i).toString();
      operands.add(new Operand(arg0, "int"));
    } else if(inst instanceof Instruction_longbranch){
      Instruction_longbranch inst_longbranch = (Instruction_longbranch) inst;
      String arg0 = Integer.valueOf(inst_longbranch.arg_i).toString();
      operands.add(new Operand(arg0, "long"));
    } else if(inst instanceof Instruction_Lookupswitch){
      //ignore
    } else if(inst instanceof Instruction_Tableswitch){
      //ignore
    } else if(inst instanceof Instruction_Newarray){
      Instruction_Newarray inst_newarray = (Instruction_Newarray) inst;
      String str = inst_newarray.toString(m_constantPool);
      String[] tokens = str.split(" ");
      name = tokens[0];
      operands.add(new Operand(tokens[1], "class_ref"));
    } else {
      throw new RuntimeException("unknown instruction type: "+inst.toString());
    }
    RTAInstruction ret = new RTAInstruction(name, operands);
    return ret;
  }

  public RTAType[] getExceptionTypes(method_info mi, cp_info[] cpool){
    attribute_info exceptions_attr = findExceptionsAttribute(mi, cpool);
    if(exceptions_attr != null){
      Exception_attribute ex_attribute = (Exception_attribute) exceptions_attr;
      RTAType[] ret = new RTAType[ex_attribute.exception_index_table.length];
      for(int table_index : ex_attribute.exception_index_table){
        String type = ConstantPoolReader.v().get(table_index, cpool);
        ret[table_index] = RTAType.create(type);
      }
      return ret;
    } else {
      return new RTAType[0];
    }
  }

  private attribute_info findExceptionsAttribute(method_info mi, cp_info[] cpool){
    if(mi.attributes == null){
      return null;
    }
    for(attribute_info attribute : mi.attributes){
      if(attribute == null){
        continue;
      }
      int name_index = attribute.attribute_name;
      CONSTANT_Utf8_info utf8_name = (CONSTANT_Utf8_info) cpool[name_index];
      String attribute_name = utf8_name.convert();
      if(attribute_name.equals(attribute_info.Exceptions)){
        return attribute;
      }
    }
    return null;
  }
}
