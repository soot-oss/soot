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
import java.util.ArrayList;
import java.util.Map;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.field_info;
import soot.coffi.cp_info;
import soot.coffi.RTAClassFactory;
import soot.coffi.ClassFile;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import soot.Modifier;

public class RTAClass {

  private enum RTAClassLevel {
    HIERARCHY,
    SIGNATURES,
    BODIES
  }

  private RTAType className;
  private boolean hasSuperClass;
  private RTAType superClassName;
  private RTAType[] interfaces;
  private RTAField[] fields;
  private RTAMethod[] methods;
  private Map<String, RTAMethod> covarientMethods;
  private int modifiers;
  private boolean isApplicationClass;
  private byte[] data;
  private cp_info[] constantPool;
  private boolean isPhantom;

  private RTAClassLevel rtaClassLevel;

  public RTAClass(RTAType className, byte[] data){
    this.className = className;
    this.data = data;

    isPhantom = false;
    rtaClassLevel = RTAClassLevel.HIERARCHY;
  }

  public RTAClass(RTAType className, boolean phantom){
    if(phantom == false){
      throw new IllegalArgumentException("phantom must be true for this constructor");
    }

    this.className = className;
    hasSuperClass = true;
    superClassName = RTAType.create("java.lang.Object");
    methods = new RTAMethod[0];
    fields = new RTAField[0];
    interfaces = new RTAType[0];
    isPhantom = true;
    rtaClassLevel = RTAClassLevel.BODIES;
  }

  public RTAClass(RTAType className, boolean hasSuperClass,
    RTAType superClassName, RTAType[] interfaces, int modifiers,
    method_info[] mi, field_info[] fi, cp_info[] cpool, byte[] data){

    this.className = className;
    this.hasSuperClass = hasSuperClass;
    this.superClassName = superClassName;
    this.interfaces = interfaces;
    this.modifiers = modifiers;
    this.data = data;
    this.constantPool = cpool;

    parseMethodInfo(mi, cpool);
    parseFieldInfo(fi, cpool);

    rtaClassLevel = RTAClassLevel.SIGNATURES;
  }

  private void parseMethodInfo(method_info[] mi, cp_info[] cpool){
    methods = new RTAMethod[mi.length];
    for(int i = 0; i < mi.length; ++i){
      method_info method = mi[i];
      RTAMethod rtaMethod = new RTAMethod(this, method, i);
      methods[i] = rtaMethod;
    }
  }

  private void parseFieldInfo(field_info[] fi, cp_info[] cpool){
    fields = new RTAField[fi.length];
    for(int i = 0; i < fi.length; ++i){
      field_info field = fi[i];
      RTAField rtaField = new RTAField(this, field, i);
      fields[i] = rtaField;
    }
  }

  private RTAMethod phantomRefMethod(String methodSubSig){
    RTAMethod phantomMethod = new RTAMethod(this, methodSubSig, true);
    RTAMethod[] methodArray = new RTAMethod[methods.length+1];
    for(int i = 0; i < methods.length; ++i){
      methodArray[i] = methods[i];
    }
    methodArray[methodArray.length-1] = phantomMethod;
    methods = methodArray;
    System.out.println("adding phantom method: "+phantomMethod.getSignature().toString());
    return phantomMethod;
  }

  private RTAMethod phantomRefMethod(MethodSignature methodSig){
    return phantomRefMethod(methodSig.getSubSignatureString());
  }

  private RTAField phantomRefField(String fieldName){
    return null;
  }

  public RTAMethod getMethod(MethodSignature methodSig){
    requireSignatures();
    for(RTAMethod method : methods){
      if(method.getSignature().subsigMatch(methodSig)){
        return method;
      }
    }
    if(hasSuperClass){
      RTAClass superClass = RTAClassLoader.v().getRTAClass(superClassName);
      return superClass.getMethod(methodSig);
    }
    if(isPhantom){
      return phantomRefMethod(methodSig);
    } else {
      return null;
    }
  }

  public RTAField findFieldByName(String fieldName){
    requireSignatures();
    for(RTAField field : fields){
      String currMethodName = StringNumbers.v().getString(field.getName());
      if(fieldName.equals(currMethodName)){
        return field;
      }
    }

    if(isPhantom){
      return phantomRefField(fieldName);
    } else {
      return null;
    }
  }

  public RTAMethod findMethodBySubSignature(String methodSubsignature){
    requireSignatures();
    for(RTAMethod method : methods){
      MethodSignature currentSig = method.getSignature();
      String currentSubSig = currentSig.getSubSignatureString();
      if(currentSubSig.equals(methodSubsignature)){
        return method;
      }
    }
    if(hasSuperClass){
      RTAClass superClass = RTAClassLoader.v().getRTAClass(superClassName);
      RTAMethod ret = superClass.findMethodBySubSignature(methodSubsignature);
      if(ret != null){
        return ret;
      }
    }
    for(RTAType iface : interfaces){
      RTAClass interfaceClass = RTAClassLoader.v().getRTAClass(iface);
      RTAMethod ret = interfaceClass.findMethodBySubSignature(methodSubsignature);
      if(ret != null){
        return ret;
      }
    }
    if(isPhantom){
      return phantomRefMethod(methodSubsignature);
    } else {
      return null;
    }
  }

  public RTAMethod[] getMethods(){
    requireSignatures();
    return methods;
  }

  public String getName(){
    return className.toString();
  }

  public RTAType getRTAType(){
    return className;
  }

  public boolean hasSuperClass(){
    return hasSuperClass;
  }

  public RTAType getSuperClass(){
    return superClassName;
  }

  public RTAType[] getInterfaces(){
    requireSignatures();
    return interfaces;
  }

  public boolean implementsInterface(RTAType iface){
    RTAType[] interfaces = getInterfaces();
    for(RTAType currentInterface : interfaces){
      if(iface == currentInterface){    //note RTAType is flyweight
        return true;
      }
    }
    return false;
  }

  public List<String> getInterfaceStrings(){
    requireSignatures();
    List<String> ret = new ArrayList<String>();
    for(RTAType type : interfaces){
      ret.add(type.toString());
    }
    return ret;
  }

  public RTAField[] getFields(){
    requireSignatures();
    return fields;
  }

  private void requireSignatures(){
    if(rtaClassLevel == RTAClassLevel.HIERARCHY){
      RTAClassFactory factory = new RTAClassFactory();
      RTAClass parsed = factory.create(data);

      this.className = parsed.className;
      this.hasSuperClass = parsed.hasSuperClass;
      this.superClassName = parsed.superClassName;
      this.interfaces = parsed.interfaces;
      this.fields = parsed.fields;
      this.methods = parsed.methods;
      this.covarientMethods = parsed.covarientMethods;
      this.modifiers = parsed.modifiers;
      this.isApplicationClass = parsed.isApplicationClass;
      this.constantPool = parsed.constantPool;
      rtaClassLevel = RTAClassLevel.SIGNATURES;
    }
  }

  public int getModifiers(){
    requireSignatures();
    return modifiers;
  }

  public cp_info[] getConstantPool(){
    requireSignatures();
    return constantPool;
  }

  public ClassFile getClassFile(){
    ByteArrayInputStream byteIn = new ByteArrayInputStream(data);
    String classNameString = getName();
    ClassFile classFile = new ClassFile(classNameString);
    if(!classFile.loadClassFile(byteIn)){
      throw new RuntimeException("cannot read class file for: "+classNameString);
    }
    return classFile;
  }

  public boolean isInterface(){
    return Modifier.isInterface(modifiers);
  }

  public boolean isConcrete(){
    return !Modifier.isInterface(modifiers) &&
      !Modifier.isAbstract(modifiers);
  }
}
