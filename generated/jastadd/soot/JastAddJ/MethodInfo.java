package soot.JastAddJ;

import java.util.HashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;
/**
  * @ast class
 * 
 */
public class MethodInfo extends java.lang.Object {

    private BytecodeParser p;


    String name;


    int flags;


    private MethodDescriptor methodDescriptor;


    private Attributes.MethodAttributes attributes;



    public MethodInfo(BytecodeParser parser) {
      p = parser;
      flags = p.u2();
      if(BytecodeParser.VERBOSE)
        p.print("  Flags: " + Integer.toBinaryString(flags));
      int name_index = p.u2();
      CONSTANT_Info info = p.constantPool[name_index];
      if(info == null || !(info instanceof CONSTANT_Utf8_Info)) {
        System.err.println("Expected CONSTANT_Utf8_Info but found: " + info.getClass().getName());
        //if(info instanceof CONSTANT_Class_Info) {
        //  System.err.println(" found CONSTANT_Class_Info: " + ((CONSTANT_Class_Info)info).name());
        //  name = ((CONSTANT_Class_Info)info).name();
        //}
      } 
      name = ((CONSTANT_Utf8_Info)info).string();
      methodDescriptor = new MethodDescriptor(p, name);
      attributes = new Attributes.MethodAttributes(p);
    }


    public BodyDecl bodyDecl() {
      Signatures.MethodSignature s = attributes.methodSignature;
      Access returnType = (s != null && s.hasReturnType()) ? s.returnType() : methodDescriptor.type();
      List parameterList;
      if(isConstructor() && p.isInnerClass) {
        parameterList = methodDescriptor.parameterListSkipFirst();
        if(s != null) {
          Iterator iter = s.parameterTypes().iterator();
          if(iter.hasNext()) iter.next();
          for(int i = 0; iter.hasNext(); i++) {
            Access a = (Access)iter.next();
            ((ParameterDeclaration)parameterList.getChildNoTransform(i)).setTypeAccess(a);
          }
        }
      }
      else {
        parameterList = methodDescriptor.parameterList();
        if(s != null) {
          int i = 0;
          for(Iterator iter = s.parameterTypes().iterator(); iter.hasNext(); i++) {
            Access a = (Access)iter.next();
            ((ParameterDeclaration)parameterList.getChildNoTransform(i)).setTypeAccess(a);
          }
        }
      }
      if((flags & Flags.ACC_VARARGS) != 0) {
        int lastIndex = parameterList.getNumChildNoTransform() - 1;
        ParameterDeclaration p = (ParameterDeclaration)parameterList.getChildNoTransform(lastIndex);
        parameterList.setChild(
            new VariableArityParameterDeclaration(
              p.getModifiersNoTransform(),
              ((ArrayTypeAccess)p.getTypeAccessNoTransform()).getAccessNoTransform(),
              p.getID()
            ),
          lastIndex
        );
      }
      List exceptionList = (s != null && s.hasExceptionList()) ? s.exceptionList() : attributes.exceptionList();
  
      if(attributes.parameterAnnotations != null) {
        for(int i = 0; i < attributes.parameterAnnotations.length; i++) {
          ParameterDeclaration p = (ParameterDeclaration)parameterList.getChildNoTransform(i);
          for(Iterator iter = attributes.parameterAnnotations[i].iterator(); iter.hasNext(); ) {
            Modifier m = (Modifier)iter.next();
            p.getModifiersNoTransform().addModifier(m);
          }
        }
      }

      BodyDecl b;
      if(isConstructor()) {
        b = new ConstructorDecl(BytecodeParser.modifiers(flags), name, parameterList, 
            exceptionList, new Opt(), new Block());
      }
      else if(attributes.elementValue() != null) {
        b = new AnnotationMethodDecl(BytecodeParser.modifiers(flags), returnType, name, 
            parameterList, exceptionList, 
            new Opt(new Block()), new Opt(attributes.elementValue()));
      }
      else if(s != null && s.hasFormalTypeParameters()) {
        b = new GenericMethodDecl(BytecodeParser.modifiers(flags), returnType, name, parameterList, 
            exceptionList, new Opt(new Block()), s.typeParameters());
      }
      else {
        b = new MethodDecl(BytecodeParser.modifiers(flags), returnType, name, parameterList, 
            exceptionList, new Opt(new Block()));
      }
      if(attributes.annotations != null) {
        for(Iterator iter = attributes.annotations.iterator(); iter.hasNext(); ) {
          if(b instanceof MethodDecl) 
            ((MethodDecl)b).getModifiers().addModifier((Modifier)iter.next());
          else if(b instanceof ConstructorDecl)
            ((ConstructorDecl)b).getModifiers().addModifier((Modifier)iter.next());
        }
      }
      return b;
    }



    private boolean isConstructor() {
      return name.equals("<init>");
    }



    public boolean isSynthetic() {
      return attributes.isSynthetic() || (flags & 0x1000) != 0;
    }


}
