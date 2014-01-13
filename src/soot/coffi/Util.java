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
import soot.jimple.*;
import java.util.*;
import java.io.*;
import soot.tagkit.*;
import soot.*;


public class Util
{
    public Util( Singletons.Global g ) {}
    public static Util v() { return G.v().soot_coffi_Util(); }

    private cp_info[] activeConstantPool = null;
    private LocalVariableTable_attribute activeVariableTable;
    private LocalVariableTypeTable_attribute activeVariableTypeTable;
    /* maps from variable names to local variable slot indexes to soot Locals*/
    private Map<String, Map<Integer, Local>> nameToIndexToLocal;
    private boolean useFaithfulNaming = false;

    /**
      Set the informations relative to the current method body.
      This method must be called before using getLocalForIndex(...)
      and getLocalForStackOp(...) each time a different current method body
      is considered.
    */
    public void bodySetup(LocalVariableTable_attribute la, 
                      LocalVariableTypeTable_attribute lt,
                      cp_info[] ca)
    {
      activeVariableTable = la;
      activeVariableTypeTable = lt;
      activeConstantPool = ca;
      nameToIndexToLocal = null;
    }
    
    public void setFaithfulNaming(boolean v)
    {
        useFaithfulNaming = v;
    }   

    public boolean isUsingFaithfulNaming() {
      return useFaithfulNaming;
    }

    public void resolveFromClassFile(SootClass aClass, InputStream is, String filePath, Collection<Type> references)
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
                    
                    CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.super_class];
    
                    String superName = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
                    superName = superName.replace('/', '.');
    
                    references.add(RefType.v(superName));
                    bclass.setSuperclass(SootResolver.v().makeClassRef(superName));
                }
        }
    
        // Add interfaces to the bclass
        {
            for(int i = 0; i < coffiClass.interfaces_count; i++)
                {
                    CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.interfaces[i]];
    
                    String interfaceName =
                        ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
    
                    interfaceName = interfaceName.replace('/', '.');
    
                    references.add(RefType.v(interfaceName));
                    SootClass interfaceClass = SootResolver.v().makeClassRef(interfaceName);
                    bclass.addInterface(interfaceClass);
                }
        }
    
        // Add every field to the bclass
        for(int i = 0; i < coffiClass.fields_count; i++){
            
            field_info fieldInfo = coffiClass.fields[i];

            String fieldName = ((CONSTANT_Utf8_info)
                                (coffiClass.constant_pool[fieldInfo.name_index])).convert();

            String fieldDescriptor = ((CONSTANT_Utf8_info)
                                      (coffiClass.constant_pool[fieldInfo.descriptor_index])).convert();

            int modifiers = fieldInfo.access_flags;
            Type fieldType = jimpleTypeOfFieldDescriptor(fieldDescriptor);
                
            SootField field = new SootField(fieldName, fieldType, modifiers);
            bclass.addField(field);
                
            references.add(fieldType);

            // add initialization constant, if any
		    for(int j = 0; j < fieldInfo.attributes_count; j++) {
                // add constant value attributes
                if (fieldInfo.attributes[j] instanceof ConstantValue_attribute){
                    
                    ConstantValue_attribute attr = (ConstantValue_attribute) fieldInfo.attributes[j];
                    cp_info cval = coffiClass.constant_pool[attr.constantvalue_index];
                    ConstantValueTag tag;
                    switch (cval.tag) {
                    case cp_info.CONSTANT_Integer:
                    tag = new IntegerConstantValueTag((int)((CONSTANT_Integer_info)cval).bytes);
                    break;
                    case cp_info.CONSTANT_Float:
                    //tag = new FloatConstantValueTag((int)((CONSTANT_Float_info)cval).bytes);
                    tag = new FloatConstantValueTag(((CONSTANT_Float_info)cval).convert());
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
                    //tag = new DoubleConstantValueTag((dcval.high << 32) + dcval.low);
                    tag = new DoubleConstantValueTag(dcval.convert());
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
                }
                // add synthetic tag
                else if (fieldInfo.attributes[j] instanceof Synthetic_attribute){
                    field.addTag(new SyntheticTag());
                }
                // add deprecated tag
                else if (fieldInfo.attributes[j] instanceof Deprecated_attribute){
                    field.addTag(new DeprecatedTag());
                }
                // add signature tag
                else if (fieldInfo.attributes[j] instanceof Signature_attribute){
                    String generic_sig = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[((Signature_attribute)fieldInfo.attributes[j]).signature_index])).convert();
                    field.addTag(new SignatureTag(generic_sig));
                }
                else if (fieldInfo.attributes[j] instanceof RuntimeVisibleAnnotations_attribute || fieldInfo.attributes[j] instanceof RuntimeInvisibleAnnotations_attribute)
                {
                    addAnnotationVisibilityAttribute(field, fieldInfo.attributes[j], coffiClass, references);
                }
                else if (fieldInfo.attributes[j] instanceof Generic_attribute)
                {        	
                	Generic_attribute attr = (Generic_attribute) fieldInfo.attributes[j];
                    String name = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[attr.attribute_name])).convert();
        			field.addTag(new GenericAttribute(name, attr.info));
                }
		    }
        }
    
        // Add every method to the bclass
        for(int i = 0; i < coffiClass.methods_count; i++){
            
            method_info methodInfo = coffiClass.methods[i];
		
		
		    if( (coffiClass.constant_pool[methodInfo.name_index]) == null) {
		        G.v().out.println("method index: " + methodInfo.toName(coffiClass.constant_pool));
		        throw new RuntimeException("method has no name");
		    }

            String methodName = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[methodInfo.name_index])).convert();
		    String methodDescriptor = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[methodInfo.descriptor_index])).convert();
    
            List parameterTypes;
            Type returnType;
    
            // Generate parameterTypes & returnType
            {
                Type[] types = jimpleTypesOfFieldOrMethodDescriptor(methodDescriptor);
    
                parameterTypes = new ArrayList();
                for(int j = 0; j < types.length - 1; j++){
                    references.add(types[j]);
                    parameterTypes.add(types[j]);
                }
                        
                returnType = types[types.length - 1];
                references.add(returnType);
            }
    
            int modifiers = methodInfo.access_flags;

            SootMethod method;

            method = new SootMethod(methodName,
                                    parameterTypes, returnType, modifiers);
            bclass.addMethod(method);

            methodInfo.jmethod = method;

            // add exceptions to method
            {
                for(int j = 0; j < methodInfo.attributes_count; j++){
                    if(methodInfo.attributes[j] instanceof Exception_attribute){
                        Exception_attribute exceptions = (Exception_attribute) methodInfo.attributes[j];

                        for(int k = 0; k < exceptions.number_of_exceptions; k++)
{
                            CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.
                            constant_pool[exceptions.exception_index_table[k]];

                            String exceptionName = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[c.name_index])).convert();

                            exceptionName = exceptionName.replace('/', '.');

                            references.add(RefType.v(exceptionName));
                            method.addExceptionIfAbsent(SootResolver.v().makeClassRef(exceptionName));
                        }
                    }
                    else if (methodInfo.attributes[j] instanceof Synthetic_attribute) {
                        method.addTag(new SyntheticTag());
                    }
                    else if (methodInfo.attributes[j] instanceof Deprecated_attribute) {
                        method.addTag(new DeprecatedTag());
                    }
                    else if (methodInfo.attributes[j] instanceof Signature_attribute){
                        String generic_sig = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[((Signature_attribute)methodInfo.attributes[j]).signature_index])).convert();
                        method.addTag(new SignatureTag(generic_sig));
                    }
                    else if (methodInfo.attributes[j] instanceof RuntimeVisibleAnnotations_attribute || methodInfo.attributes[j] instanceof RuntimeInvisibleAnnotations_attribute)
                    {
                        addAnnotationVisibilityAttribute(method, methodInfo.attributes[j], coffiClass, references);
                    }
                    else if (methodInfo.attributes[j] instanceof RuntimeVisibleParameterAnnotations_attribute || methodInfo.attributes[j] instanceof RuntimeInvisibleParameterAnnotations_attribute)
                    {
                        addAnnotationVisibilityParameterAttribute(method, methodInfo.attributes[j], coffiClass, references);
                    }
                    else if (methodInfo.attributes[j] instanceof AnnotationDefault_attribute)
                    {
                        AnnotationDefault_attribute attr = (AnnotationDefault_attribute)methodInfo.attributes[j];
                        element_value [] input = new element_value[1];
                        input[0] = attr.default_value;
                        ArrayList<AnnotationElem> list = createElementTags(1, coffiClass, input);
                        method.addTag(new AnnotationDefaultTag(list.get(0)));
                    }
                    else if (methodInfo.attributes[j] instanceof Generic_attribute)
                    {        	
                    	Generic_attribute attr = (Generic_attribute) methodInfo.attributes[j];
                        String name = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[attr.attribute_name])).convert();
            			method.addTag(new GenericAttribute(name, attr.info));
                    }
                }
            }
                    
                // Go through the constant pool, forcing all mentioned classes to be resolved. 
                {
                    for(int k = 0; k < coffiClass.constant_pool_count; k++) {
                        if(coffiClass.constant_pool[k] instanceof CONSTANT_Class_info)
                            {
                                CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[k];

                                String desc = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
                                String name = desc.replace('/', '.');

                                if(name.startsWith("["))
                                    references.add(jimpleTypeOfFieldDescriptor(desc));
                                else
                                    references.add(RefType.v(name));
                            }
                        if(coffiClass.constant_pool[k] instanceof CONSTANT_Fieldref_info
                        || coffiClass.constant_pool[k] instanceof CONSTANT_Methodref_info
                        || coffiClass.constant_pool[k] instanceof CONSTANT_InterfaceMethodref_info) {
                            Type[] types = jimpleTypesOfFieldOrMethodDescriptor(
                                cp_info.getTypeDescr(coffiClass.constant_pool,k));
                            for (Type element : types) {
                                references.add(element);
                            }
                        }

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
	for(int i = 0; i < coffiClass.attributes_count; i++){
	    
		if(coffiClass.attributes[i] instanceof SourceFile_attribute){
		    
		    SourceFile_attribute attr = (SourceFile_attribute)coffiClass.attributes[i];
            String sourceFile = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[attr.sourcefile_index])).convert();

            if( sourceFile.indexOf(' ') >= 0 ) {
                G.v().out.println( "Warning: Class "+className+" has invalid SourceFile attribute (will be ignored)." );
            } else {
                bclass.addTag(new SourceFileTag( sourceFile , filePath) );
            }
	
	    }
	    // Set "InnerClass" attribute tag
	    else if(coffiClass.attributes[i] instanceof InnerClasses_attribute){
		   
		    InnerClasses_attribute attr = (InnerClasses_attribute)coffiClass.attributes[i];
            for (int j = 0; j < attr.inner_classes_length; j++)
                {
                    inner_class_entry e = attr.inner_classes[j];
                String inner = null;
                String outer = null;
                String name = null;
                if (e.inner_class_index != 0)
                    inner = ((CONSTANT_Utf8_info)coffiClass.constant_pool[((CONSTANT_Class_info)coffiClass.constant_pool[e.inner_class_index]).name_index]).convert();
                if (e.outer_class_index != 0)
                    outer = ((CONSTANT_Utf8_info)coffiClass.constant_pool[((CONSTANT_Class_info)coffiClass.constant_pool[e.outer_class_index]).name_index]).convert();
                if (e.name_index != 0)
                    name = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[e.name_index])).convert();
                bclass.addTag(new InnerClassTag(inner, outer, name, e.access_flags));
            }
        }
        // set synthetic tags
        else if(coffiClass.attributes[i] instanceof Synthetic_attribute){
		    
		    bclass.addTag(new SyntheticTag());
        }
        // set deprectaed tags
        else if(coffiClass.attributes[i] instanceof Deprecated_attribute){
		    bclass.addTag(new DeprecatedTag());
        }
        else if (coffiClass.attributes[i] instanceof Signature_attribute){
            String generic_sig = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[((Signature_attribute)coffiClass.attributes[i]).signature_index])).convert();
            bclass.addTag(new SignatureTag(generic_sig));
        }
        else if (coffiClass.attributes[i] instanceof EnclosingMethod_attribute){           
            EnclosingMethod_attribute attr = (EnclosingMethod_attribute)coffiClass.attributes[i];
            String class_name = ((CONSTANT_Utf8_info)coffiClass.constant_pool[((CONSTANT_Class_info)coffiClass.constant_pool[ attr.class_index  ]).name_index]).convert();
            CONSTANT_NameAndType_info info = (CONSTANT_NameAndType_info)coffiClass.constant_pool[attr.method_index];

            String method_name = "";
            String method_sig = "";
            
            if (info != null){
                method_name = ((CONSTANT_Utf8_info)coffiClass.constant_pool[info.name_index]).convert();
                method_sig = ((CONSTANT_Utf8_info)coffiClass.constant_pool[info.descriptor_index]).convert();
            }
            bclass.addTag(new EnclosingMethodTag(class_name, method_name, method_sig));
        }
        else if (coffiClass.attributes[i] instanceof RuntimeVisibleAnnotations_attribute || coffiClass.attributes[i] instanceof RuntimeInvisibleAnnotations_attribute)
        {
            addAnnotationVisibilityAttribute(bclass, coffiClass.attributes[i], coffiClass, references);
        }
        else if (coffiClass.attributes[i] instanceof Generic_attribute)
        {        	
        	Generic_attribute attr = (Generic_attribute) coffiClass.attributes[i];
            String name = ((CONSTANT_Utf8_info)(coffiClass.constant_pool[attr.attribute_name])).convert();
			bclass.addTag(new GenericAttribute(name, attr.info));
        }
   
    }
    }



    Type jimpleReturnTypeOfMethodDescriptor(String descriptor)
    {
        Type[] types = jimpleTypesOfFieldOrMethodDescriptor(descriptor);

        return types[types.length - 1];
    }

    private final ArrayList<Type> conversionTypes = new ArrayList<Type>();
    
    /*
    private Map cache = new HashMap();
    public Type[] jimpleTypesOfFieldOrMethodDescriptor(String descriptor)
    {
        Type[] ret = (Type[]) cache.get(descriptor);
        if( ret != null ) return ret;
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

        ret = (Type[]) conversionTypes.toArray(new Type[0]);
        cache.put(descriptor, ret);
        return ret;
    }
*/


    private final Map<String, Type[]> cache = new HashMap<String, Type[]>();
    public Type[] jimpleTypesOfFieldOrMethodDescriptor(String descriptor)
    {
        Type[] ret = cache.get(descriptor);
        if( ret != null ) return ret;
        char[] d = descriptor.toCharArray();
        int p = 0;
        conversionTypes.clear();

outer:
        while(p<d.length)
        {
            boolean isArray = false;
            int numDimensions = 0;
            Type baseType = null;

swtch:
            while(p<d.length) {
                switch( d[p] ) {
                // Skip parenthesis
                    case '(': case ')':
                        p++;
                        continue outer;

                    case '[':
                        isArray = true;
                        numDimensions++;
                        p++;
                        continue swtch;
                    case 'B':
                        baseType = ByteType.v();
                        p++;
                        break swtch;
                    case 'C':
                        baseType = CharType.v();
                        p++;
                        break swtch;
                    case 'D':
                        baseType = DoubleType.v();
                        p++;
                        break swtch;
                    case 'F':
                        baseType = FloatType.v();
                        p++;
                        break swtch;
                    case 'I':
                        baseType = IntType.v();
                        p++;
                        break swtch;
                    case 'J':
                        baseType = LongType.v();
                        p++;
                        break swtch;
                    case 'L':
                        int index = p+1;
                        while(index < d.length && d[index] != ';') {
                            if(d[index] == '/') d[index] = '.';
                            index++;
                        }
                        if( index >= d.length )
                            throw new RuntimeException("Class reference has no ending ;");
                        String className = new String(d, p+1, index - p - 1);
                        baseType = RefType.v(className);
                        p = index+1;
                        break swtch;
                    case 'S':
                        baseType = ShortType.v();
                        p++;
                        break swtch;
                    case 'Z':
                        baseType = BooleanType.v();
                        p++;
                        break swtch;
                    case 'V':
                        baseType = VoidType.v();
                        p++;
                        break swtch;
                    default:
                        throw new RuntimeException("Unknown field type!");
                }
            }
            if( baseType == null ) continue;

            // Determine type
            Type t;
            if(isArray)
                t = ArrayType.v(baseType, numDimensions);
            else
                t = baseType;

            conversionTypes.add(t);
        }

        ret = conversionTypes.toArray(new Type[0]);
        cache.put(descriptor, ret);
        return ret;
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

    private Local getLocal(Body b, String name) 
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


    private boolean declaresLocal(Body b, String localName)
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

    Local getLocalCreatingIfNecessary(JimpleBody listBody, String name, Type type)
    {
      Local l;
      if(declaresLocal(listBody, name))
      {
        l = getLocal(listBody, name);
        if(!l.getType().equals(type)) {
          throw new RuntimeException("The body already declares this local name with a different type.");
        }
      }
      else {
        l = Jimple.v().newLocal(name, type);
        listBody.getLocals().add(l);
      }
      return l;
    }
    
    /**
      Get a Local for the parameter at the given local variable index.
      @listBody the method body.
      @index the parameter's local variable index.
      @return the Local for the given local variable index.
     */
    Local getLocalForParameter(JimpleBody listBody, int index) {
      return getLocalForIndex(listBody, index, 0, 0, false);
    }

    /**
      Get a Local for the local variable at the given index in the context of
      the given instruction.
      @listBody the method body.
      @index the local variable index.
      @context the instruction context.
      @return the Local for the given local variable index.
     */
    Local getLocalForIndex(JimpleBody listBody, int index, Instruction context) {
      return getLocalForIndex(listBody, index, context.originalIndex, 
        context.nextOffset(context.originalIndex), ByteCode.isLocalStore(context.code));
    }

    private Local getLocalForIndex(JimpleBody listBody, int index, int bcIndex, int nextBcIndex, boolean isLocalStore)
    {
      String name = null; 
      Local local;
      
      if(useFaithfulNaming && activeVariableTable != null)
      { 
        /*
          [use-original-names]
          
          Use original variables names. Generally speeking, the current 
          way of handling original variables names is sound if the local
          variable table is consistant with the source code. 
          
                      consistant(table) ==> sound(local for index)
                      
          In the table, variables are tuples ((s,l),i,N) where (s,l) 
          encodes the half-open bytecode index interval [s,s+l[, i is the 
          local variable index and N is the original variable name. 
          ((s,l),i,N) means "from bytecode index s included to bytecode index
          s+l excluded, the variable at index i is named N is the source 
          code".
          
          However the content of the table is for informational and debugging 
          purpose only, the compiler can insert whatever inconsistancies it 
          may want, meaning we cannot trust the table.
          
          The most common inconsistancy we found so far is where variables
          with different indexes and overlapping bytecode ranges are given the
          same name in the table. Although it won't happen with user-defined 
          variables since Java doesn't allow the same variable name to be 
          declared in nested scopes. 
          But this situation can arise for compiler-generated
          local variables if they are assigned the same name (`i$` is common 
          in OpenJDK). Notable example are local variables generated to 
          implement the Java for-each statement (for(Foo foo : bar){}). If 
          the source code contains nested for-each statements, then it becomes
          ugly because all the generated iterators or counters (for-each on
          arrays) are assigned the same name in the table.
          
          This inconsistancy is now handled correctly by the following code.
          The idea is based on the observation that the Local allocation works
          well if we simply allocate a different Local object for each 
          local variable index (it is the default allocation policy, when the
          original names are not kept).
          Therefore, local variables with the same name and same index should
          have the same Local object. And local variables with the same name
          but different index should have a different Local object.
          We maintain the map
            nameToIndexToLocal :: Name -> (Index -> Local Object)
          from variable names to maps from variable indexes to Local objects.
          So we allocate a new Local object for each pair of a variable name 
          and a variable index.
          
          Unfortunately, this is still unsound if the table gives several
          names for the same variable of the source code. Or if it reports
          wrong bytecode ranges, or if it doesn't report the name of a 
          variable everywhere it is used in the bytecode...
          
          In order to obtain sound Local allocation when taking original
          names into account... we should not allocate Local according to these
          names. Instead we must keep the default allocation policy (one Local
          object for each local variable index), and then annotate each 
          statement with "most probable name(s)" for each use or def Local.
          
        */
        if(bcIndex != -1)
        {
          int lookupBcIndex = bcIndex;
          /* 
            For local store bytecode, the local actually takes its new value 
            after the bytecode is executed, so we must look at the next 
            bytecode index. This is the behavior observed at least with 
            OpenJDK javac.
          */
          if(isLocalStore) lookupBcIndex = nextBcIndex;

          name = activeVariableTable.getLocalVariableName(activeConstantPool, index, lookupBcIndex);
          
          /*// for debug purpose
          String desc = activeVariableTable.getLocalVariableDescriptor(activeConstantPool, index, activeOriginalIndex);
          if (activeVariableTypeTable != null){
            String debug_type = activeVariableTypeTable.getLocalVariableType(activeConstantPool, index, activeOriginalIndex);
          }*/
        }
      }

      if(name == null) {
        name = "l" + index; // generate a default name for the local
      }

      if(nameToIndexToLocal == null) nameToIndexToLocal = new HashMap<String, Map<Integer, Local>>();
      
      Map<Integer,Local> indexToLocal;
      
      if(!nameToIndexToLocal.containsKey(name)) {
        indexToLocal = new HashMap<Integer, Local>();
        nameToIndexToLocal.put(name, indexToLocal);
      }else indexToLocal = nameToIndexToLocal.get(name);

      if(indexToLocal.containsKey(index)) {
        local = indexToLocal.get(index);
      }else{
        local = Jimple.v().newLocal(name, UnknownType.v());
        listBody.getLocals().add(local);
        indexToLocal.put(index, local);
      }
      
      return local;
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

    private void addAnnotationVisibilityAttribute(Host host, attribute_info attribute, ClassFile coffiClass, Collection<Type> references){
        VisibilityAnnotationTag tag;
        if (attribute instanceof RuntimeVisibleAnnotations_attribute){
            tag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE);         
            RuntimeVisibleAnnotations_attribute attr = (RuntimeVisibleAnnotations_attribute)attribute;
            addAnnotations(attr.number_of_annotations, attr.annotations, coffiClass, tag, references);
        }
        else {
            tag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE);
            RuntimeInvisibleAnnotations_attribute attr = (RuntimeInvisibleAnnotations_attribute)attribute;
            addAnnotations(attr.number_of_annotations, attr.annotations, coffiClass, tag, references);
        }
        host.addTag(tag); 
    }
    
    private void addAnnotationVisibilityParameterAttribute(Host host, attribute_info attribute, ClassFile coffiClass, Collection<Type> references){
        VisibilityParameterAnnotationTag tag;
        if (attribute instanceof RuntimeVisibleParameterAnnotations_attribute){
            RuntimeVisibleParameterAnnotations_attribute attr = (RuntimeVisibleParameterAnnotations_attribute)attribute;
            tag = new VisibilityParameterAnnotationTag(attr.num_parameters, AnnotationConstants.RUNTIME_VISIBLE);
            for (int i = 0; i < attr.num_parameters; i++){
                parameter_annotation pAnnot = attr.parameter_annotations[i];
                VisibilityAnnotationTag vTag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_VISIBLE);
                addAnnotations(pAnnot.num_annotations, pAnnot.annotations, coffiClass, vTag, references);
                tag.addVisibilityAnnotation(vTag);
            }
        }
        else {
            RuntimeInvisibleParameterAnnotations_attribute attr = (RuntimeInvisibleParameterAnnotations_attribute)attribute;
            tag = new VisibilityParameterAnnotationTag(attr.num_parameters, AnnotationConstants.RUNTIME_INVISIBLE);
            for (int i = 0; i < attr.num_parameters; i++){
                parameter_annotation pAnnot = attr.parameter_annotations[i];
                VisibilityAnnotationTag vTag = new VisibilityAnnotationTag(AnnotationConstants.RUNTIME_INVISIBLE);
                addAnnotations(pAnnot.num_annotations, pAnnot.annotations, coffiClass, vTag, references);
                tag.addVisibilityAnnotation(vTag);
            }
        }
        host.addTag(tag); 
    }

    private void addAnnotations(int numAnnots, annotation [] annotations, ClassFile coffiClass, VisibilityAnnotationTag tag, Collection<Type> references){
        for (int i = 0; i < numAnnots; i++){
            annotation annot = annotations[i];
            String annotType = ((CONSTANT_Utf8_info)coffiClass.constant_pool[annot.type_index]).convert();
            String ref = annotType.substring(1, annotType.length()-1);
            ref = ref.replace('/', '.');
            references.add(RefType.v(ref));
            AnnotationTag annotTag = new AnnotationTag(annotType, 
                createElementTags(annot.num_element_value_pairs, coffiClass, annot.element_value_pairs));
            tag.addAnnotation(annotTag);
        }
    }
    
    private ArrayList<AnnotationElem> createElementTags(int count, ClassFile coffiClass, element_value [] elems){
        ArrayList<AnnotationElem> list = new ArrayList<AnnotationElem>();
        for (int j = 0; j < count; j++){
            element_value ev = elems[j];
            char kind = ev.tag;
            String elemName = "default";
            if (ev.name_index != 0){
                elemName = ((CONSTANT_Utf8_info)coffiClass.constant_pool[ev.name_index]).convert();
            }
            if (kind == 'B' || kind == 'C' || kind == 'I' || kind == 'S' || kind == 'Z' || kind == 'D' || kind == 'F' || kind == 'J' || kind == 's'){
                constant_element_value cev = (constant_element_value)ev;
                if (kind == 'B' || kind == 'C' || kind == 'I' || kind == 'S' || kind == 'Z'){
                    cp_info cval = coffiClass.constant_pool[cev.constant_value_index];
                    int constant_val = (int)((CONSTANT_Integer_info)cval).bytes;
                    AnnotationIntElem elem = new AnnotationIntElem(constant_val, kind, elemName);
                    list.add(elem);
                }
                else if (kind == 'D'){
                    cp_info cval = coffiClass.constant_pool[cev.constant_value_index];
                    double constant_val = ((CONSTANT_Double_info)cval).convert();
                    AnnotationDoubleElem elem = new AnnotationDoubleElem(constant_val, kind, elemName);
                    list.add(elem);
                
                }
                else if (kind == 'F'){
                    cp_info cval = coffiClass.constant_pool[cev.constant_value_index];
                    float constant_val = ((CONSTANT_Float_info)cval).convert();
                    AnnotationFloatElem elem = new AnnotationFloatElem(constant_val, kind, elemName);
                    list.add(elem);
                
                }
                else if (kind == 'J'){
                    cp_info cval = coffiClass.constant_pool[cev.constant_value_index];
                    CONSTANT_Long_info lcval = (CONSTANT_Long_info)cval;
                    long constant_val = (lcval.high << 32) + lcval.low;
                    AnnotationLongElem elem = new AnnotationLongElem(constant_val, kind, elemName);
                    list.add(elem);
                
                }
                else if (kind == 's'){
                    cp_info cval = coffiClass.constant_pool[cev.constant_value_index];
                    String constant_val = ((CONSTANT_Utf8_info)cval).convert();
                    AnnotationStringElem elem = new AnnotationStringElem(constant_val, kind, elemName);
                    list.add(elem);
                }
            }
            else if (kind == 'e'){
                enum_constant_element_value ecev = (enum_constant_element_value)ev;
                cp_info type_val = coffiClass.constant_pool[ecev.type_name_index];
                String type_name = ((CONSTANT_Utf8_info)type_val).convert();
                cp_info name_val = coffiClass.constant_pool[ecev.constant_name_index];
                String constant_name = ((CONSTANT_Utf8_info)name_val).convert();
                AnnotationEnumElem elem = new AnnotationEnumElem(type_name, constant_name, kind, elemName);
                list.add(elem);
            }
            else if (kind == 'c'){
                class_element_value cev = (class_element_value)ev;
                cp_info cval = coffiClass.constant_pool[cev.class_info_index];
                CONSTANT_Utf8_info sval = (CONSTANT_Utf8_info)cval;
                String desc = sval.convert();
                
                AnnotationClassElem elem = new AnnotationClassElem(desc, kind, elemName);
                list.add(elem);
            }
            else if (kind == '['){
                array_element_value aev = (array_element_value)ev;
                int num_vals = aev.num_values;

                ArrayList<AnnotationElem> elemVals = createElementTags(num_vals, coffiClass, aev.values);
                AnnotationArrayElem elem = new AnnotationArrayElem(elemVals, kind, elemName);
                list.add(elem);
            }
            else if (kind == '@'){
                annotation_element_value aev = (annotation_element_value)ev;
                annotation annot = aev.annotation_value;
                String annotType = ((CONSTANT_Utf8_info)coffiClass.constant_pool[annot.type_index]).convert();
                AnnotationTag annotTag = new AnnotationTag(annotType, 
                    createElementTags(annot.num_element_value_pairs, coffiClass, annot.element_value_pairs));
                
                AnnotationAnnotationElem elem = new AnnotationAnnotationElem(annotTag, kind, elemName);
                list.add(elem);
            }
        }
   
        return list;
    }
}
