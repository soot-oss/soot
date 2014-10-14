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

import java.util.List;
import soot.coffi.method_info;
import soot.coffi.cp_info;
import soot.coffi.ConstantPoolReader;
import soot.coffi.ClassFile;
import soot.coffi.RTAMethodFactory;
import soot.Modifier;
import soot.coffi.CoffiMethodSource;

public class RTAMethod {

  private enum RTAMethodLevel {
    HIERARCHY,
    SIGNATURES,
    BODIES
  }

  private RTAClass clazz;
  private int index;
  private method_info methodInfo;
  private RTAMethodLevel rtaMethodLevel;
  private MethodSignature signature;
  private int modifiers;
  private List<RTAInstruction> instructions;
  private RTAType[] codeAttrExTypes;
  private RTAType[] exceptionTypes;
  private boolean isPhantom;

  public RTAMethod(RTAClass clazz, method_info methodInfo, int index){
    this.clazz = clazz;
    this.index = index;
    this.methodInfo = methodInfo;

    isPhantom = false;
    rtaMethodLevel = RTAMethodLevel.HIERARCHY;
  }

  public RTAMethod(RTAClass clazz, String subsignature, boolean phantom){
    if(phantom == false){
      throw new IllegalArgumentException("phantom must be true for this constructor");
    }

    this.clazz = clazz;
    isPhantom = true;
    MethodSignatureUtil util = new MethodSignatureUtil();
    util.parseSubSignature(subsignature);
    List<String> parameterTypes = util.getParameterTypes();
    String[] parameterTypesArray = new String[parameterTypes.size()];
    for(int i = 0; i < parameterTypes.size(); ++i){
      parameterTypesArray[i] = parameterTypes.get(i);
    }
    signature = new MethodSignature(clazz.getRTAType().toString(),
      util.getMethodName(), util.getReturnType(), parameterTypesArray);
    codeAttrExTypes = new RTAType[0];
    exceptionTypes = new RTAType[0];

    modifiers = Modifier.PUBLIC;
    rtaMethodLevel = RTAMethodLevel.BODIES;
  }

  public boolean isPhantom(){
    return isPhantom;
  }

  public MethodSignature getSignature(){
    requireSignatures();
    return signature;
  }

  public RTAClass getRTAClass(){
    return clazz;
  }

  public List<RTAInstruction> getInstructions(){
    requireBodies();
    return instructions;
  }

  private void requireSignatures(){
    if(rtaMethodLevel == RTAMethodLevel.HIERARCHY){
      String nameStr = ConstantPoolReader.v().get(methodInfo.name_index, clazz.getConstantPool());
      String descString = ConstantPoolReader.v().get(methodInfo.descriptor_index, clazz.getConstantPool());
      String returnStr = DescriptorParser.v().parseMethodDesc_return(descString);
      String[] paramStr = DescriptorParser.v().parseMethodDesc_params(descString);

      signature = new MethodSignature(clazz.getName(), nameStr, returnStr, paramStr);
      modifiers = methodInfo.access_flags;
      rtaMethodLevel = RTAMethodLevel.SIGNATURES;
    }
  }

  public int getModifiers(){
    requireSignatures();
    return modifiers;
  }

  private void requireBodies(){
    requireSignatures();
    if(rtaMethodLevel == RTAMethodLevel.SIGNATURES){
      RTAMethodFactory factory = new RTAMethodFactory();

      ClassFile classFile = getClassFile();
      instructions = factory.parseInstructions(classFile,
        classFile.methods[index]);
      rtaMethodLevel = RTAMethodLevel.BODIES;
    }
  }

  private ClassFile getClassFile(){
    return clazz.getClassFile();
  }

  public boolean isConcrete(){
    requireSignatures();
    return !Modifier.isInterface(modifiers) &&
      !Modifier.isAbstract(modifiers) &&
      !Modifier.isNative(modifiers);
  }

  public boolean isStatic(){
    requireSignatures();
    return Modifier.isStatic(modifiers);
  }

  public RTAType[] getCodeAttrExTypes(){
    if(codeAttrExTypes == null){
      readExtraMethodInfo();
    }
    return codeAttrExTypes;
  }

  public RTAType[] getExceptionTypes(){
    if(exceptionTypes == null){
      readExtraMethodInfo();
    }
    return exceptionTypes;
  }

  private void readExtraMethodInfo(){
    RTAMethodFactory factory = new RTAMethodFactory();
    cp_info[] constantPool = clazz.getConstantPool();
    exceptionTypes = factory.getExceptionTypes(methodInfo, constantPool);
    codeAttrExTypes = factory.getCodeAttrExceptions(methodInfo, constantPool);
  }

  public CoffiMethodSource getMethodSource(){
    ClassFile classFile = getClassFile();
    return new CoffiMethodSource(classFile, classFile.methods[index]);
  }
}
