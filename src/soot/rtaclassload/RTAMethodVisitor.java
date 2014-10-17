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

import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.LinkedList;

public class RTAMethodVisitor {

  private Set<RTAType> newInvokes;
  private Set<RTAType> allTypes;
  private Set<RTAType> refTypes;
  private Set<RTAType> arrayTypes;
  private Set<MethodSignature> methodRefs;
  private Set<FieldSignature> fieldRefs;
  private Set<RTAType> instanceOfs;
  private Set<RTAType> hierarchyVisited;

  public RTAMethodVisitor(RTAMethod rtaMethod){
    newInvokes = new TreeSet<RTAType>();
    allTypes = new TreeSet<RTAType>();
    refTypes = new TreeSet<RTAType>();
    arrayTypes = new TreeSet<RTAType>();
    methodRefs = new TreeSet<MethodSignature>();
    fieldRefs = new TreeSet<FieldSignature>();
    instanceOfs = new TreeSet<RTAType>();
    hierarchyVisited = new TreeSet<RTAType>();

    if(rtaMethod.isPhantom() == false){
      parseMethod(rtaMethod);
    }
  }

  public Set<RTAType> getNewInvokes(){
    return newInvokes;
  }

  public Set<RTAType> getAllTypes(){
    return allTypes;
  }

  public Set<MethodSignature> getMethodRefs(){
    return methodRefs;
  }

  public Set<FieldSignature> getFieldRefs(){
    return fieldRefs;
  }

  public Set<RTAType> getInstanceOfs(){
    return instanceOfs;
  }

  private void parseMethod(RTAMethod method){
    if(method.isConcrete() == false){
      return;
    }

    MethodSignature signature = method.getSignature();
    addHierarchy(signature.getClassName());
    addSignature(method);

    List<RTAInstruction> instructions = method.getInstructions();
    for(RTAInstruction inst : instructions){
      addInstruction(inst);
    }

    RTAType[] ex_types = method.getCodeAttrExTypes();
    for(RTAType ex_type : ex_types){
      addHierarchy(ex_type);
    }
  }

  /**
   * type can be ArrayType, RefType or PrimType
   */
  private void addHierarchy(RTAType type){
    if(hierarchyVisited.contains(type)){
      return;
    }
    hierarchyVisited.add(type);

    LinkedList<RTAType> hierarchyQueue = new LinkedList<RTAType>();
    hierarchyQueue.add(type);
    while(hierarchyQueue.isEmpty() == false){
      RTAType classNumber = hierarchyQueue.removeFirst();
      RTAType orgClassNumber = classNumber;
      addRefType(classNumber);

      if(classNumber.isArray()){
        addArrayType(classNumber);
        classNumber = classNumber.getNonArray();
      }
      if(classNumber.isRefType() == false){
        continue;
      }

      RTAClass rtaClass = RTAClassLoader.v().getRTAClass(classNumber);
      if(rtaClass == null){
        continue;
      }
      if(rtaClass.hasSuperClass()){
        hierarchyQueue.add(rtaClass.getSuperClass());
      }
      RTAType[] interfaces = rtaClass.getInterfaces();
      for(RTAType iface : interfaces){
        hierarchyQueue.add(iface);
      }
    }
  }

  private void addRefType(RTAType type){
    refTypes.add(type);
    allTypes.add(type);
  }

  private void addArrayType(RTAType type){
    arrayTypes.add(type);
    allTypes.add(type);
  }

  private void addSignature(RTAMethod method){
    addHierarchy(method.getSignature().getReturnType());
    for(RTAType param : method.getSignature().getParameterTypes()){
      addHierarchy(param);
    }
    for(RTAType except : method.getExceptionTypes()){
      addHierarchy(except);
    }
  }

  private void addInstruction(RTAInstruction inst){
    addInstructionName(inst);
    addInstructionOperands(inst);
  }

  private void addInstructionName(RTAInstruction inst){
    String name = inst.getName();
    if(name.equals("anewarray")){
      addNewInvoke(inst);
    } else if(name.equals("instanceof")){
      addInstanceOf(inst);
    } else if(name.equals("multianewarray")){
      addNewInvoke(inst);
    } else if(name.equals("newarray")){
      addNewInvoke(inst);
    } else if(name.equals("new")){
      addNewInvoke(inst);
    }
  }

  private void addInstructionOperands(RTAInstruction inst){
    List<Operand> operands = inst.getOperands();
    for(Operand operand : operands){
      String value = operand.getValue();
      String type = operand.getType();

      if(type.equals("class_ref")){
        addHierarchy(RTAType.create(value));
      } else if(type.equals("method_ref")){
        MethodSignature methodRef = new MethodSignature(value);
        methodRefs.add(methodRef);
        addHierarchy(methodRef.getClassName());
        addHierarchy(methodRef.getReturnType());
        for(RTAType param : methodRef.getParameterTypes()){
          addHierarchy(param);
        }
      } else if(type.equals("field_ref")){
        FieldSignature fieldRef = new FieldSignature(value);
        fieldRefs.add(fieldRef);
        addHierarchy(fieldRef.getDeclaringClass());
        addHierarchy(fieldRef.getType());
      }
    }
  }

  private void addNewInvoke(RTAInstruction inst){
    List<Operand> operands = inst.getOperands();
    for(Operand operand : operands){
      String value = operand.getValue();
      String type = operand.getType();

      RTAType rtaType = RTAType.create(value);
      if(rtaType.isRefType()){
        newInvokes.add(rtaType);
      }
    }
  }

  private void addInstanceOf(RTAInstruction inst){
    List<Operand> operands = inst.getOperands();
    for(Operand operand : operands){
      String value = operand.getValue();
      String type = operand.getType();

      if(type.equals("class_ref")){
        RTAType rtaType = RTAType.create(value);
        instanceOfs.add(rtaType);
        addHierarchy(rtaType);
      }
    }
  }
}
