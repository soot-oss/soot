/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */







package soot.coffi;
import soot.options.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import java.io.*;
import soot.baf.*;
import soot.tagkit.*;
import soot.*;


public class Util
{
    public Util( Singletons.Global g ) {}
    public static Util v() { return G.v().soot_coffi_Util(); }


    Map classNameToAbbreviation;
    Set markedClasses;
    LinkedList classesToResolve;

    int activeOriginalIndex = -1;
    cp_info[] activeConstantPool = null;
    LocalVariableTable_attribute activeVariableTable;
    boolean useFaithfulNaming = false;
    boolean isLocalStore = false;  // global variable used 
    boolean isWideLocalStore = false;
    public void setFaithfulNaming(boolean v)
    {
        useFaithfulNaming = v;
    }    

    public void assertResolvedClass(String className)
    {
        if(!Scene.v().containsClass(className))
        {
            SootClass newClass = new SootClass(className);
            Scene.v().addClass(newClass);
            
            markedClasses.add(newClass);
            classesToResolve.addLast(newClass);
        }
    }

    public void assertResolvedClassForType(Type type)
    {
        if(type instanceof RefType)
            assertResolvedClass(((RefType) type).getClassName());
        else if(type instanceof ArrayType)
            assertResolvedClassForType(((ArrayType) type).baseType);
    }
    
    public SootClass getResolvedClass(String className)
    {
        if(Scene.v().containsClass(className))
            return Scene.v().getSootClass(className);
            
        SootClass newClass = new SootClass(className);
        Scene.v().addClass(newClass);
        
        markedClasses.add(newClass);
        classesToResolve.addLast(newClass);
           
        return newClass;
    }

    public SootClass getResolvedClass2(String className)
    {
        if(Scene.v().containsClass(className))
            return Scene.v().getSootClass(className);
            
        SootClass newClass = new SootClass(className);
        Scene.v().addClass(newClass);
        
        return newClass;
    }

    
    public void resolveFromClassFile(SootClass aClass, InputStream is)
    {
        SootClass bclass = aClass;                
        String className = bclass.getName();

        ClassFile coffiClass = new ClassFile(className);
        
        // Load up class file, and retrieve bclass from class manager.
        {
            boolean success = coffiClass.loadClassFile(is);

            if(!success)
                {
                    if(!Scene.v().allowsPhantomRefs())
                        throw new RuntimeException("Could not load classfile: " + bclass.getName());
                    else {                        
                        G.v().out.println("Warning: " + className + " is a phantom class!");
                        bclass.setPhantom(true);                                                                
                        return;
                    } 
                    
                }
            
            CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.this_class];
    
            String name = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
            name = name.replace('/', '.');
                	    
            if( !name.equals( bclass.getName() ) ) {
                throw new RuntimeException( 
                        "Error: class "+name+" read in from a classfile in which "+bclass.getName()+" was expected." );
            }
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
    
                    bclass.setSuperclass(SootResolver.v().getResolvedClass(superName));
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
    
                    SootClass interfaceClass = SootResolver.v().getResolvedClass(interfaceName);
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
                Type fieldType = jimpleTypeOfFieldDescriptor(fieldDescriptor);
                    
                SootField field = new SootField(fieldName, fieldType, modifiers);
                bclass.addField(field);
                    
                SootResolver.v().assertResolvedClassForType(fieldType);
    
                // add initialization constant, if any
		for(int j = 0; j < fieldInfo.attributes_count; j++) {
		    if (!(fieldInfo.attributes[j] instanceof ConstantValue_attribute))
			continue;
		    ConstantValue_attribute attr = (ConstantValue_attribute) fieldInfo.attributes[j];
		    cp_info cval = coffiClass.constant_pool[attr.constantvalue_index];
		    ConstantValueTag tag;
		    switch (cval.tag) {
		    case cp_info.CONSTANT_Integer:
			tag = new IntegerConstantValueTag((int)((CONSTANT_Integer_info)cval).bytes);
			break;
		    case cp_info.CONSTANT_Float:
			tag = new FloatConstantValueTag((int)((CONSTANT_Float_info)cval).bytes);
			break;
		    case cp_info.CONSTANT_Long:
		      {
			CONSTANT_Long_info lcval = (CONSTANT_Long_info)cval;
			tag = new LongConstantValueTag((lcval.high << 32) + lcval.low);
			break;
		      }
		    case cp_info.CONSTANT_Double:
		      {
			CONSTANT_Double_info dcval = (CONSTANT_Double_info)cval;
			tag = new DoubleConstantValueTag((dcval.high << 32) + dcval.low);
			break;
		      }
		    case cp_info.CONSTANT_String:
		      {
			CONSTANT_String_info scval = (CONSTANT_String_info)cval;
			CONSTANT_Utf8_info ucval = (CONSTANT_Utf8_info)coffiClass.constant_pool[scval.string_index];
			tag = new StringConstantValueTag(ucval.convert());
			break;
		      }
		    default:
			throw new RuntimeException("unexpected ConstantValue: " + cval);
		    }
		    field.addTag(tag);
		    break;
		}
            }
    
        // Add every method to the bclass
        for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info methodInfo = coffiClass.methods[i];
		
		
		if( (coffiClass.constant_pool[methodInfo.name_index]) == null) {
		    G.v().out.println("method index: " + methodInfo.toName(coffiClass.constant_pool));
		    throw new RuntimeException("method has no name");
		}

                String methodName = ((CONSTANT_Utf8_info)
                                     (coffiClass.constant_pool[methodInfo.name_index])).convert();
		
                String methodDescriptor = ((CONSTANT_Utf8_info)
                                           (coffiClass.constant_pool[methodInfo.descriptor_index])).convert();
    
                List parameterTypes;
                Type returnType;
    
                // Generate parameterTypes & returnType
                {
                    Type[] types = jimpleTypesOfFieldOrMethodDescriptor(methodDescriptor);
    
                    parameterTypes = new ArrayList();
    
                    for(int j = 0; j < types.length - 1; j++)
                        {
                            SootResolver.v().assertResolvedClassForType(types[j]);
                            parameterTypes.add(types[j]);
                        }
                        
                    returnType = types[types.length - 1];
                    SootResolver.v().assertResolvedClassForType(returnType);
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
    
                                        method.addExceptionIfAbsent(SootResolver.v().getResolvedClass(exceptionName));
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

                                if(name.startsWith("["))
                                    SootResolver.v().assertResolvedClassForType(jimpleTypeOfFieldDescriptor(desc));
                                else
                                    SootResolver.v().assertResolvedClass(name);
                            }
                }
            }

        // Set coffi source of method
        for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info methodInfo = coffiClass.methods[i];
                //                methodInfo.jmethod.setSource(coffiClass, methodInfo);
                methodInfo.jmethod.setSource(new CoffiMethodSource(coffiClass, methodInfo));
            }
        
	// Set "SourceFile" attribute tag
	for(int i = 0; i < coffiClass.attributes_count; i++)
	    {
		if(!(coffiClass.attributes[i] instanceof SourceFile_attribute))
		    continue;
		SourceFile_attribute attr = (SourceFile_attribute)coffiClass.attributes[i];
                String sourceFile = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[attr.sourcefile_index])).convert();

                if( sourceFile.indexOf(' ') >= 0 ) {
                    G.v().out.println( "Warning: Class "+className+" has invalid SourceFile attribute (will be ignored)." );
                } else {
                    bclass.addTag(new SourceFileTag( sourceFile ) );
                }
		break;
	    }

	// Set "InnerClass" attribute tag
	for(int i = 0; i < coffiClass.attributes_count; i++)
	    {
		if(!(coffiClass.attributes[i] instanceof InnerClasses_attribute))
		    continue;
		InnerClasses_attribute attr = (InnerClasses_attribute)coffiClass.attributes[i];
		for (int j = 0; j < attr.inner_classes_length; j++)
		    {
		    	inner_class_entry e = attr.inner_classes[j];
			String inner = null;
			String outer = null;
			String name = null;
			int class_index;

			if (e.inner_class_index != 0)
				inner = ((CONSTANT_Utf8_info)coffiClass.constant_pool[((CONSTANT_Class_info)coffiClass.constant_pool[e.inner_class_index]).name_index]).convert();
			if (e.outer_class_index != 0)
				outer = ((CONSTANT_Utf8_info)coffiClass.constant_pool[((CONSTANT_Class_info)coffiClass.constant_pool[e.outer_class_index]).name_index]).convert();
			if (e.name_index != 0)
				name = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[e.name_index])).convert();
			bclass.addTag(new InnerClassTag(inner, outer, name, e.access_flags));
		    }
		break;
	    }

    }
    





    Type jimpleReturnTypeOfMethodDescriptor(String descriptor)
    {
        Type[] types = jimpleTypesOfFieldOrMethodDescriptor(descriptor);

        return types[types.length - 1];
    }

    private ArrayList conversionTypes = new ArrayList();
    
    public Type[] jimpleTypesOfFieldOrMethodDescriptor(String descriptor)
    {
        conversionTypes.clear();

        while(descriptor.length() != 0)
        {
            boolean isArray = false;
            int numDimensions = 0;
            Type baseType;

            // Skip parenthesis
                if(descriptor.startsWith("(") || descriptor.startsWith(")"))
                {
                    descriptor = descriptor.substring(1);
                    continue;
                }

            // Handle array case
                while(descriptor.startsWith("["))
                {
                    isArray = true;
                    numDimensions++;
                    descriptor = descriptor.substring(1);
                }

            // Determine base type
                if(descriptor.startsWith("B"))
                {
                    baseType = ByteType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("C"))
                {
                    baseType = CharType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("D"))
                {
                    baseType = DoubleType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("F"))
                {
                    baseType = FloatType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("I"))
                {
                    baseType = IntType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("J"))
                {
                    baseType = LongType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("L"))
                {
                    int index = descriptor.indexOf(';');

                    if(index == -1)
                        throw new RuntimeException("Class reference has no ending ;");

                    String className = descriptor.substring(1, index);

                    baseType = RefType.v(className.replace('/', '.'));

                    descriptor = descriptor.substring(index + 1);
                }
                else if(descriptor.startsWith("S"))
                {
                    baseType = ShortType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("Z"))
                {
                    baseType = BooleanType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("V"))
                {
                    baseType = VoidType.v();
                    descriptor = descriptor.substring(1);
                }
                else
                    throw new RuntimeException("Unknown field type!");

            Type t;

            // Determine type
                if(isArray)
                    t = ArrayType.v(baseType, numDimensions);
                else
                    t = baseType;

            conversionTypes.add(t);
        }

        return (Type[]) conversionTypes.toArray(new Type[0]);
    }

    public Type jimpleTypeOfFieldDescriptor(String descriptor)
    {
        boolean isArray = false;
        int numDimensions = 0;
        Type baseType;

        // Handle array case
            while(descriptor.startsWith("["))
            {
                isArray = true;
                numDimensions++;
                descriptor = descriptor.substring(1);
            }

        // Determine base type
            if(descriptor.equals("B"))
                baseType = ByteType.v();
            else if(descriptor.equals("C"))
                baseType = CharType.v();
            else if(descriptor.equals("D"))
                baseType = DoubleType.v();
            else if(descriptor.equals("F"))
                baseType = FloatType.v();
            else if(descriptor.equals("I"))
                baseType = IntType.v();
            else if(descriptor.equals("J"))
                baseType = LongType.v();
            else if(descriptor.equals("V"))
                baseType = VoidType.v();
            else if(descriptor.startsWith("L"))
            {
                if(!descriptor.endsWith(";"))
                    throw new RuntimeException("Class reference does not end with ;");

                String className = descriptor.substring(1, descriptor.length() - 1);

                baseType = RefType.v(className.replace('/', '.'));
            }
            else if(descriptor.equals("S"))
                baseType = ShortType.v();
            else if(descriptor.equals("Z"))
                baseType = BooleanType.v();
            else
                throw new RuntimeException("Unknown field type: " + descriptor);

        // Return type
            if(isArray)
                return ArrayType.v(baseType, numDimensions);
            else
                return baseType;
    }

    int nextEasyNameIndex;

    void resetEasyNames()
    {
        nextEasyNameIndex = 0;
    }

    String getNextEasyName()
    {
        final String[] easyNames =
            {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
             "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

        int justifiedIndex = nextEasyNameIndex++;

        if(justifiedIndex >= easyNames.length)
            return "local" + (justifiedIndex - easyNames.length);
        else
            return easyNames[justifiedIndex];
    }

    void setClassNameToAbbreviation(Map map)
    {
        classNameToAbbreviation = map;
    }

    Local getLocalForStackOp(JimpleBody listBody, TypeStack typeStack,
        int index)
    {
        if(typeStack.get(index).equals(Double2ndHalfType.v()) ||
            typeStack.get(index).equals(Long2ndHalfType.v()))
        {
            index--;
        }

        return getLocalCreatingIfNecessary(listBody, "$stack" + index, UnknownType.v());
    }

    String getAbbreviationOfClassName(String className)
    {
        StringBuffer buffer = new StringBuffer(new Character(className.charAt(0)).toString());
        int periodIndex = 0;

        for(;;)
        {
            periodIndex = className.indexOf('.', periodIndex + 1);

            if(periodIndex == -1)
                break;

            buffer.append(Character.toLowerCase(className.charAt(periodIndex + 1)));
        }

        return buffer.toString();
    }

    String getNormalizedClassName(String className)
    {
        className = className.replace('/', '.');

        if(className.endsWith(";"))
            className = className.substring(0, className.length() - 1);

        // Handle array case
        {
            int numDimensions = 0;

            while(className.startsWith("["))
            {
                numDimensions++;
                className = className.substring(1, className.length());
                className = className + "[]";
            }

            if(numDimensions != 0)
            {
                if(!className.startsWith("L"))
                    throw new RuntimeException("For some reason an array reference does not start with L");

                className = className.substring(1, className.length());
            }
        }


        return className;
    }

    public Local getLocal(Body b, String name) 
        throws soot.jimple.NoSuchLocalException
    {
        Iterator localIt = b.getLocals().iterator();

        while(localIt.hasNext())
        {
            Local local = (Local) localIt.next();

            if(local.getName().equals(name))
                return local;
        }

        throw new soot.jimple.NoSuchLocalException();
    }


    public boolean declaresLocal(Body b, String localName)
    {
        Iterator localIt = b.getLocals().iterator();

        while(localIt.hasNext())
        {
            Local local = (Local) localIt.next();

            if(local.getName().equals(localName))
                return true;
        }

        return false;
    }

     Local
        getLocalCreatingIfNecessary(JimpleBody listBody, String name, Type type)
    {
        if(declaresLocal(listBody, name))
        {
            return getLocal(listBody, name);
        }
        else {
            Local l = Jimple.v().newLocal(name, type);
            listBody.getLocals().add(l);

            return l;
        }
    }

    Local getLocalForIndex(JimpleBody listBody, int index)
    {
        String name = null;
        boolean assignedName = false;
        
        if(useFaithfulNaming && activeVariableTable != null)
        {
            if(activeOriginalIndex != -1)
            {

	      // Feng asks: why this is necessary? it does wrong thing
	      //            for searching local variable names.
	      // It is going to be verified with plam.
                if(isLocalStore)
                    activeOriginalIndex++;
                if(isWideLocalStore)
                    activeOriginalIndex++;

                name = activeVariableTable.getLocalVariableName(activeConstantPool,
                    index, activeOriginalIndex);
               
                if(name != null) 
                    assignedName = true;
            }
        }  
        
        if(!assignedName)
            name = "l" + index;

        if(declaresLocal(listBody, name))
            return getLocal(listBody, name);
        else {
            Local l = Jimple.v().newLocal(name,
                UnknownType.v());

            listBody.getLocals().add(l);

            return l;
        }
    }

    /*
    void setLocalType(Local local, List locals,
        int localIndex, Type type)
    {
        if(local.getType().equals(UnknownType.v()) ||
            local.getType().equals(type))
        {
            local.setType(type);

            if(local.getType().equals(DoubleType.v()) ||
                local.getType().equals(LongType.v()))
            {
                // This means the next local becomes voided, since these types occupy two
                // words.

                Local secondHalf = (Local) locals.get(localIndex + 1);

                secondHalf.setType(VoidType.v());
            }

            return;
        }

        if(type.equals(IntType.v()))
        {
            if(local.getType().equals(BooleanType.v()) ||
               local.getType().equals(CharType.v()) ||
               local.getType().equals(ShortType.v()) ||
               local.getType().equals(ByteType.v()))
            {
                // Even though it's not the same, it's ok, because booleans, chars, shorts, and
                // bytes are all sort of treated like integers by the JVM.
                return;
            }

        }

        throw new RuntimeException("required and actual types do not match: " + type.toString() +
                " with " + local.getType().toString());
    }    */

    /** Verifies the prospective name for validity as a Jimple name.
     * In particular, first-char is alpha | _ | $, subsequent-chars 
     * are alphanum | _ | $. 
     *
     * We could use isJavaIdentifier, except that Jimple's grammar
     * doesn't support all of those, just ASCII.
     *
     * I'd put this in soot.Local, but that's an interface.
     *
     * @author Patrick Lam
     */
    boolean isValidJimpleName(String prospectiveName) {
        if(prospectiveName == null) return false;
	for (int i = 0; i < prospectiveName.length(); i++) {
	    char c = prospectiveName.charAt(i);
	    if (i == 0 && c >= '0' && c <= '9')
		return false;

	    if (!((c >= '0' && c <= '9') ||
		  (c >= 'a' && c <= 'z') ||
		  (c >= 'A' && c <= 'Z') ||
		  (c == '_' || c == '$')))
		return false;
	}
	return true;
    }
}
