package soot;

import soot.coffi.*;
import java.util.*;

public class SootResolver 
{
    public static SootClass resolve(String className) 
    {
	/*
	  SootClass bclass = new SootClass(className);
	
	  if(soot.Main.isVerbose)
	  System.out.println("SootResolver: resolving " + className + "...");
	
	  soot.coffi.ClassFile coffiClass = new soot.coffi.ClassFile(className);
	
	
	  // Load up class file, and retrieve bclass from class manager.
	  {
	  boolean success = coffiClass.loadClassFile();
	  if(!success) {
	    
	  if(!Scene.v().allowsPhantomRefs())
	  throw new RuntimeException("Could not load classfile: " + bclass.getName());
	  else {
	  System.out.println("Warning: " + className + " is a phantom class!");
	  bclass.setPhantom(true);
	  }	    
	  }

	  Scene.v().addClass(bclass);
	    

	  CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.this_class];
	
	  String name = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
	  name = name.replace('/', '.');
		
	  bclass.setName(name);
	  // replace this classes name with its fully qualified version.
    
	  }


	
	  // Set modifier
	  bclass.setModifiers(coffiClass.access_flags & (~0x0020));
	  // don't want the ACC_SUPER flag, it is always supposed to be set anyways
    
	  // Set superclass
	  {
	  if(coffiClass.super_class != 0)
	  {
	  // This object is not java.lang.Object, so must have a super class
    
	  CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.
	  super_class];
    
	  String superName = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
	  superName = superName.replace('/', '.');
    
	  bclass.setSuperclass(Scene.v().getSootClass(superName));
	  }
	  }
    
	  // Add interfaces to the bclass
	  {
	  for(int i = 0; i < coffiClass.interfaces_count; i++)
	  {
	  CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.
	  interfaces[i]];
    
	  String interfaceName =
	  ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
    
	  interfaceName = interfaceName.replace('/', '.');
    
	  SootClass interfaceClass = Scene.v().getSootClass(interfaceName);
	  bclass.addInterface(interfaceClass);
	  }
	  }
    
	  // Add every field to the bclass
	  for(int i = 0; i < coffiClass.fields_count; i++)
	  {
	  field_info fieldInfo = coffiClass.fields[i];
    
	  String fieldName = ((CONSTANT_Utf8_info)
	  (coffiClass.constant_pool[fieldInfo.name_index])).convert();
    
	  String fieldDescriptor = ((CONSTANT_Utf8_info)
	  (coffiClass.constant_pool[fieldInfo.descriptor_index])).convert();
    
	  int modifiers = fieldInfo.access_flags;
	  Type fieldType = Util.jimpleTypeOfFieldDescriptor(Scene.v(), fieldDescriptor);
                    
	  bclass.addField(new SootField(fieldName,
	  fieldType, modifiers));
                    
	  }
    
	  // Add every method to the bclass
	  for(int i = 0; i < coffiClass.methods_count; i++)
	  {
	  method_info methodInfo = coffiClass.methods[i];

	  String methodName = ((CONSTANT_Utf8_info)
	  (coffiClass.constant_pool[methodInfo.name_index])).convert();
    
	  String methodDescriptor = ((CONSTANT_Utf8_info)
	  (coffiClass.constant_pool[methodInfo.descriptor_index])).convert();
    
	  List parameterTypes;
	  Type returnType;
    
	  // Generate parameterTypes & returnType
	  {
	  Type[] types = Util.jimpleTypesOfFieldOrMethodDescriptor(Scene.v(),
	  methodDescriptor);
    
	  parameterTypes = new ArrayList();
    
	  for(int j = 0; j < types.length - 1; j++)
	  {
	  //assertResolvedClassForType(types[j]);
	  parameterTypes.add(types[j]);
	  }
                        
	  returnType = types[types.length - 1];
	  //		    assertResolvedClassForType(returnType);
	  }
    
	  int modifiers = methodInfo.access_flags;
    
	  SootMethod method;
    
	  method = new SootMethod(methodName,
	  parameterTypes, returnType, modifiers);
	  bclass.addMethod(method);
    
	  methodInfo.jmethod = method;
    
	  // add exceptions to method
	  {
	  for(int j = 0; j < methodInfo.attributes_count; j++)
	  if(methodInfo.attributes[j] instanceof Exception_attribute)
	  {
	  Exception_attribute exceptions = (Exception_attribute) methodInfo.attributes[j];
    
	  for(int k = 0; k < exceptions.number_of_exceptions; k++)
	  {
	  CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.
	  constant_pool[exceptions.exception_index_table[k]];
    
	  String exceptionName = ((CONSTANT_Utf8_info)
	  (coffiClass.constant_pool[c.name_index])).convert();
    
	  exceptionName = exceptionName.replace('/', '.');
    
	  method.addException(Scene.v().getSootClass(exceptionName));
	  }
	  }
	  }
                    
	  // Go through the constant pool, forcing all mentioned classes to be resolved. 
	  {
	  for(int k = 0; k < coffiClass.constant_pool_count; k++)
	  if(coffiClass.constant_pool[k] instanceof CONSTANT_Class_info)
	  {
	  CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[k];

	  String desc = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
	  String name = desc.replace('/', '.');
	*/
				/* original commented area
				   if(name.startsWith("["))
				   //assertResolvedClassForType(jimpleTypeOfFieldDescriptor(cm, desc));
				   else
				   // assertResolvedClass(name);
				*/

				/*
				  }
				  }
				  }
				  
				  // Set coffi source of method
				  for(int i = 0; i < coffiClass.methods_count; i++)
				  {
				  method_info methodInfo = coffiClass.methods[i];
				  methodInfo.jmethod.setSource(coffiClass, methodInfo);
				  }
	
	

				  return bclass;
				*/
	return null;
    }
}
