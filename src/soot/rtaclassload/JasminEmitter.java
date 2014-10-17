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

import soot.coffi.ClassFile;
import soot.coffi.field_info;
import soot.coffi.method_info;
import soot.coffi.RTAMethodFactory;
import soot.coffi.ConstantPoolReader;
import java.io.ByteArrayInputStream;
import soot.Modifier;
import java.util.List;

public class JasminEmitter {

  private StringBuilder builder;
  private ClassFile classFile;

  public String emitFromClassFile(byte[] byteClassFile){
    builder = new StringBuilder();

    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteClassFile);
    classFile = new ClassFile("");
    classFile.loadClassFile(byteIn);

    emitClass();
    emitSuperClass();
    emitInterfaces();
    emitFields();
    emitMethods();

    return builder.toString();
  }

  private void emit(String str){
    builder.append(str);
  }

  private void emitln(String str){
    builder.append(str);
    builder.append("\n");
  }

  private void emitln(){
    builder.append("\n");
  }

  private void emitClass(){
    emit(".class ");
    emit(protectionLevel(classFile.access_flags));
    emit(" ");
    emit(isStatic(classFile.access_flags));
    emit(" ");
    emit(cpoolString(classFile.this_class));
    emitln();
  }

  private String protectionLevel(int modifiers){
    if(Modifier.isPublic(modifiers)){
      return "public";
    } else if(Modifier.isPrivate(modifiers)){
      return "private";
    } else if(Modifier.isProtected(modifiers)){
      return "protected";
    } else {
      return "";
    }
  }

  private String cpoolString(int index){
    return ConstantPoolReader.v().getRaw(index, classFile.constant_pool, false);
  }

  private String isStatic(int modifiers){
    if(Modifier.isStatic(modifiers)){
      return "static";
    } else {
      return "";
    }
  }

  private void emitSuperClass(){
    if(classFile.super_class != 0){
      emit(".super ");
      emit(cpoolString(classFile.super_class));
      emitln();
    } else {
      emit(".no_super");
      emitln();
    }
  }

  private void emitInterfaces(){
    for(int i = 0; i < classFile.interfaces_count; ++i){
      int iface = classFile.interfaces[i];
      emit(".implements ");
      emit(cpoolString(iface));
      emitln();
    }
  }

  private void emitFields(){
    for(int i = 0; i < classFile.fields_count; ++i){
      field_info info = classFile.fields[i];
      emit(".field ");
      emit(protectionLevel(info.access_flags));
      emit(" ");
      emit(isStatic(info.access_flags));
      emit(" ");
      emit("\""+cpoolString(info.name_index)+"\"");
      emit(" ");
      emit(cpoolString(info.descriptor_index));
      emitln();
    }
    emitln();
  }

  private void emitMethods(){
    for(int i = 0; i < classFile.methods_count; ++i){
      method_info info = classFile.methods[i];
      emit(".method ");
      emit(protectionLevel(info.access_flags));
      emit(" ");
      emit(isStatic(info.access_flags));
      emit(" ");
      emit(cpoolString(info.name_index));
      emit(cpoolString(info.descriptor_index));
      emitln();

      RTAMethodFactory factory = new RTAMethodFactory();
      if(info.code_attr != null){
        emitln("  .limit stack "+factory.getMaxStack(info));
        emitln("  .limit locals "+factory.getMaxLocals(info));
        emitln();
      }

      List<String> instructions = factory.parseRawInstructions(classFile, info);
      for(String inst : instructions){
        emit(inst);
        emitln();
      }

      emit(".end method");
      emitln();
      emitln();
    }
    emitln();
  }
}
