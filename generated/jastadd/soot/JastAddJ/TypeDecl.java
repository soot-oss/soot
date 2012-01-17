package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
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
 * @ast node
 * @declaredat java.ast:38
 */
public abstract class TypeDecl extends ASTNode<ASTNode> implements Cloneable, SimpleSet, Iterator, VariableScope {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    accessibleFromPackage_String_values = null;
    accessibleFromExtend_TypeDecl_values = null;
    accessibleFrom_TypeDecl_values = null;
    dimension_computed = false;
    elementType_computed = false;
    elementType_value = null;
    arrayType_computed = false;
    arrayType_value = null;
    isException_computed = false;
    isCheckedException_computed = false;
    isUncheckedException_computed = false;
    mayCatch_TypeDecl_values = null;
    constructors_computed = false;
    constructors_value = null;
    unqualifiedLookupMethod_String_values = null;
    methodsNameMap_computed = false;
    methodsNameMap_value = null;
    localMethodsSignatureMap_computed = false;
    localMethodsSignatureMap_value = null;
    methodsSignatureMap_computed = false;
    methodsSignatureMap_value = null;
    ancestorMethods_String_values = null;
    localTypeDecls_String_values = null;
    memberTypes_String_values = null;
    localFields_String_values = null;
    localFieldsMap_computed = false;
    localFieldsMap_value = null;
    memberFieldsMap_computed = false;
    memberFieldsMap_value = null;
    memberFields_String_values = null;
    hasAbstract_computed = false;
    unimplementedMethods_computed = false;
    unimplementedMethods_value = null;
    isPublic_computed = false;
    isStatic_computed = false;
    fullName_computed = false;
    fullName_value = null;
    typeName_computed = false;
    typeName_value = null;
    narrowingConversionTo_TypeDecl_values = null;
    methodInvocationConversionTo_TypeDecl_values = null;
    castingConversionTo_TypeDecl_values = null;
    isString_computed = false;
    isObject_computed = false;
    instanceOf_TypeDecl_values = null;
    isCircular_visited = -1;
    isCircular_computed = false;
    isCircular_initialized = false;
    boxed_computed = false;
    boxed_value = null;
    unboxed_computed = false;
    unboxed_value = null;
    isIterable_computed = false;
    involvesTypeParameters_visited = -1;
    involvesTypeParameters_computed = false;
    involvesTypeParameters_initialized = false;
    erasure_computed = false;
    erasure_value = null;
    implementedInterfaces_computed = false;
    implementedInterfaces_value = null;
    usesTypeVariable_visited = -1;
    usesTypeVariable_computed = false;
    usesTypeVariable_initialized = false;
    sourceTypeDecl_computed = false;
    sourceTypeDecl_value = null;
    containedIn_TypeDecl_values = null;
    sameStructure_TypeDecl_values = null;
    subtype_TypeDecl_values = null;
    enclosingVariables_computed = false;
    enclosingVariables_value = null;
    uniqueIndex_computed = false;
    jvmName_computed = false;
    jvmName_value = null;
    getSootClassDecl_computed = false;
    getSootClassDecl_value = null;
    getSootType_computed = false;
    getSootType_value = null;
    sootClass_computed = false;
    sootClass_value = null;
    needsClinit_computed = false;
    innerClassesAttributeEntries_computed = false;
    innerClassesAttributeEntries_value = null;
    getSootField_String_TypeDecl_values = null;
    createEnumMethod_TypeDecl_values = null;
    createEnumIndex_EnumConstant_values = null;
    createEnumArray_TypeDecl_values = null;
    componentType_computed = false;
    componentType_value = null;
    isDAbefore_Variable_values = null;
    isDUbefore_Variable_values = null;
    typeException_computed = false;
    typeException_value = null;
    typeRuntimeException_computed = false;
    typeRuntimeException_value = null;
    typeError_computed = false;
    typeError_value = null;
    lookupMethod_String_values = null;
    typeObject_computed = false;
    typeObject_value = null;
    lookupType_String_values = null;
    lookupVariable_String_values = null;
    packageName_computed = false;
    packageName_value = null;
    isAnonymous_computed = false;
    unknownType_computed = false;
    unknownType_value = null;
    inExplicitConstructorInvocation_computed = false;
    inStaticContext_computed = false;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl clone() throws CloneNotSupportedException {
    TypeDecl node = (TypeDecl)super.clone();
    node.accessibleFromPackage_String_values = null;
    node.accessibleFromExtend_TypeDecl_values = null;
    node.accessibleFrom_TypeDecl_values = null;
    node.dimension_computed = false;
    node.elementType_computed = false;
    node.elementType_value = null;
    node.arrayType_computed = false;
    node.arrayType_value = null;
    node.isException_computed = false;
    node.isCheckedException_computed = false;
    node.isUncheckedException_computed = false;
    node.mayCatch_TypeDecl_values = null;
    node.constructors_computed = false;
    node.constructors_value = null;
    node.unqualifiedLookupMethod_String_values = null;
    node.methodsNameMap_computed = false;
    node.methodsNameMap_value = null;
    node.localMethodsSignatureMap_computed = false;
    node.localMethodsSignatureMap_value = null;
    node.methodsSignatureMap_computed = false;
    node.methodsSignatureMap_value = null;
    node.ancestorMethods_String_values = null;
    node.localTypeDecls_String_values = null;
    node.memberTypes_String_values = null;
    node.localFields_String_values = null;
    node.localFieldsMap_computed = false;
    node.localFieldsMap_value = null;
    node.memberFieldsMap_computed = false;
    node.memberFieldsMap_value = null;
    node.memberFields_String_values = null;
    node.hasAbstract_computed = false;
    node.unimplementedMethods_computed = false;
    node.unimplementedMethods_value = null;
    node.isPublic_computed = false;
    node.isStatic_computed = false;
    node.fullName_computed = false;
    node.fullName_value = null;
    node.typeName_computed = false;
    node.typeName_value = null;
    node.narrowingConversionTo_TypeDecl_values = null;
    node.methodInvocationConversionTo_TypeDecl_values = null;
    node.castingConversionTo_TypeDecl_values = null;
    node.isString_computed = false;
    node.isObject_computed = false;
    node.instanceOf_TypeDecl_values = null;
    node.isCircular_visited = -1;
    node.isCircular_computed = false;
    node.isCircular_initialized = false;
    node.boxed_computed = false;
    node.boxed_value = null;
    node.unboxed_computed = false;
    node.unboxed_value = null;
    node.isIterable_computed = false;
    node.involvesTypeParameters_visited = -1;
    node.involvesTypeParameters_computed = false;
    node.involvesTypeParameters_initialized = false;
    node.erasure_computed = false;
    node.erasure_value = null;
    node.implementedInterfaces_computed = false;
    node.implementedInterfaces_value = null;
    node.usesTypeVariable_visited = -1;
    node.usesTypeVariable_computed = false;
    node.usesTypeVariable_initialized = false;
    node.sourceTypeDecl_computed = false;
    node.sourceTypeDecl_value = null;
    node.containedIn_TypeDecl_values = null;
    node.sameStructure_TypeDecl_values = null;
    node.subtype_TypeDecl_values = null;
    node.enclosingVariables_computed = false;
    node.enclosingVariables_value = null;
    node.uniqueIndex_computed = false;
    node.jvmName_computed = false;
    node.jvmName_value = null;
    node.getSootClassDecl_computed = false;
    node.getSootClassDecl_value = null;
    node.getSootType_computed = false;
    node.getSootType_value = null;
    node.sootClass_computed = false;
    node.sootClass_value = null;
    node.needsClinit_computed = false;
    node.innerClassesAttributeEntries_computed = false;
    node.innerClassesAttributeEntries_value = null;
    node.getSootField_String_TypeDecl_values = null;
    node.createEnumMethod_TypeDecl_values = null;
    node.createEnumIndex_EnumConstant_values = null;
    node.createEnumArray_TypeDecl_values = null;
    node.componentType_computed = false;
    node.componentType_value = null;
    node.isDAbefore_Variable_values = null;
    node.isDUbefore_Variable_values = null;
    node.typeException_computed = false;
    node.typeException_value = null;
    node.typeRuntimeException_computed = false;
    node.typeRuntimeException_value = null;
    node.typeError_computed = false;
    node.typeError_value = null;
    node.lookupMethod_String_values = null;
    node.typeObject_computed = false;
    node.typeObject_value = null;
    node.lookupType_String_values = null;
    node.lookupVariable_String_values = null;
    node.packageName_computed = false;
    node.packageName_value = null;
    node.isAnonymous_computed = false;
    node.unknownType_computed = false;
    node.unknownType_value = null;
    node.inExplicitConstructorInvocation_computed = false;
    node.inStaticContext_computed = false;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:28
   */
  
  
  public int anonymousIndex = 0;
  /**
   * @ast method 
   * @aspect AnonymousClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AnonymousClasses.jrag:45
   */
  public int nextAnonymousIndex() {
    if(isNestedType())
      return enclosingType().nextAnonymousIndex();
    return anonymousIndex++;
  }
  /**
   * @ast method 
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:24
   */
  public MethodDecl addMemberMethod(MethodDecl m) {
    addBodyDecl(m);
    return (MethodDecl)getBodyDecl(getNumBodyDecl()-1);
    /*
    HashMap map = methodsNameMap();
    ArrayList list = (ArrayList)map.get(m.name());
    if(list == null) {
      list = new ArrayList(4);
      map.put(m.name(), list);
    }
    list.add(m);
    if(!memberMethods(m.name()).contains(m))
      throw new Error("The method " + m.signature() + " added to " + typeName() + " can not be found using lookupMemberMethod");
    */
  }
  /**
   * @ast method 
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:40
   */
  public ConstructorDecl addConstructor(ConstructorDecl c) {
    addBodyDecl(c);
    return (ConstructorDecl)getBodyDecl(getNumBodyDecl()-1);
  }
  /**
   * @ast method 
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:45
   */
  public ClassDecl addMemberClass(ClassDecl c) {
    addBodyDecl(new MemberClassDecl(c));
    return ((MemberClassDecl)getBodyDecl(getNumBodyDecl()-1)).getClassDecl();
  }
  /**
   * @ast method 
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:52
   */
  public FieldDeclaration addMemberField(FieldDeclaration f) {
    addBodyDecl(f);
    return (FieldDeclaration)getBodyDecl(getNumBodyDecl()-1);
    //if(!memberFields(f.name()).contains(f))
    //  throw new Error("The field " + f.name() + " added to " + typeName() + " can not be found using lookupMemberField");
  }
  /**
   * @ast method 
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:90
   */
  public TypeAccess createBoundAccess() {
    return new BoundTypeAccess("", name(), this);
  }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:136
   */
  public SimpleSet add(Object o) {
    return new SimpleSetImpl().add(this).add(o);
  }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:140
   */
  public boolean isSingleton() { return true; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:141
   */
  public boolean isSingleton(Object o) { return contains(o); }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:144
   */
  
  private TypeDecl iterElem;
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:145
   */
  public Iterator iterator() { iterElem = this; return this; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:146
   */
  public boolean hasNext() { return iterElem != null; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:147
   */
  public Object next() { Object o = iterElem; iterElem = null; return o; }
  /**
   * @ast method 
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:148
   */
  public void remove() { throw new UnsupportedOperationException(); }
  /**
   * @ast method 
   * @aspect DeclareBeforeUse
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DeclareBeforeUse.jrag:41
   */
  public boolean declaredBeforeUse(Variable decl, ASTNode use) {
    int indexDecl = ((ASTNode)decl).varChildIndex(this);
    int indexUse = use.varChildIndex(this);
    return indexDecl < indexUse;
  }
  /**
   * @ast method 
   * @aspect DeclareBeforeUse
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DeclareBeforeUse.jrag:46
   */
  public boolean declaredBeforeUse(Variable decl, int indexUse) {
    int indexDecl = ((ASTNode)decl).varChildIndex(this);
    return indexDecl < indexUse;
  }
  /**
   * @ast method 
   * @aspect ConstructorLookup
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:88
   */
  public ConstructorDecl lookupConstructor(ConstructorDecl signature) {
    for(Iterator iter = constructors().iterator(); iter.hasNext(); ) {
      ConstructorDecl decl = (ConstructorDecl)iter.next();
      if(decl.sameSignature(signature)) {
        return decl;
      }
    }
    return null;
  }
  /**
   * @ast method 
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:220
   */
  public Iterator localMethodsIterator() {
    return new Iterator() {
      private Iterator outer = localMethodsSignatureMap().values().iterator();
      private Iterator inner = null;
      public boolean hasNext() {
        if((inner == null || !inner.hasNext()) && outer.hasNext())
          inner = ((SimpleSet)outer.next()).iterator();
        return inner == null ? false : inner.hasNext();
      }
      public Object next() {
        return inner.next();
      }
      public void remove() { throw new UnsupportedOperationException(); }
    };
    //return localMethodsSignatureMap().values().iterator();
  }
  /**
   * @ast method 
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:288
   */
  public Iterator methodsIterator() {
    return new Iterator() {
      private Iterator outer = methodsSignatureMap().values().iterator();
      private Iterator inner = null;
      public boolean hasNext() {
        if((inner == null || !inner.hasNext()) && outer.hasNext())
          inner = ((SimpleSet)outer.next()).iterator();
        return inner != null ? inner.hasNext() : false;
      }
      public Object next() {
        return inner.next();
      }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  /**
   * @ast method 
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:353
   */
  protected boolean allMethodsAbstract(SimpleSet set) {
    if(set == null) return true;
    for(Iterator iter = set.iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(!m.isAbstract())
        return false;
    }
    return true;
  }
  /**
   * @ast method 
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:210
   */
  public TypeDecl subclassWithinBody(TypeDecl typeDecl) {
    if(instanceOf(typeDecl))
      return this;
    if(isNestedType()) {
      return enclosingType().subclassWithinBody(typeDecl);
    }
    return null;
  }
  /**
   * @ast method 
   * @aspect Fields
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:306
   */
  public Iterator fieldsIterator() {
    return new Iterator() {
      private Iterator outer = memberFieldsMap().values().iterator();
      private Iterator inner = null;
      public boolean hasNext() {
        if((inner == null || !inner.hasNext()) && outer.hasNext())
          inner = ((SimpleSet)outer.next()).iterator();
        return inner != null ? inner.hasNext() : false;
      }
      public Object next() {
        return inner.next();
      }
      public void remove() { throw new UnsupportedOperationException(); }
    };
  }
  /**
   * @ast method 
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:68
   */
  public void checkModifiers() {
    super.checkModifiers();
    // 8.1.1
    if(isPublic() && !isTopLevelType() && !isMemberType())
      error("public pertains only to top level types and member types");

    // 8.1.1
    if((isProtected() || isPrivate()) && !(isMemberType() && enclosingType().isClassDecl()))
      error("protected and private may only be used on member types within a directly enclosing class declaration");

    // 8.1.1
    if(isStatic() && !isMemberType())
      error("static pertains only to member types");
    
    
    // 8.4.3.1
    // 8.1.1.1
    if(!isAbstract() && hasAbstract()) {
      StringBuffer s = new StringBuffer();
      s.append("" + name() + " is not declared abstract but contains abstract members: \n");
      for(Iterator iter = unimplementedMethods().iterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        s.append("  " + m.signature() + " in " + m.hostType().typeName() + "\n");
      }
      error(s.toString());
    }
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:246
   */
  public void nameCheck() {
    if(isTopLevelType() && lookupType(packageName(), name()) != this)
      error("duplicate member " + name() + " in compilation unit");
  
    if(!isTopLevelType() && !isAnonymous() && !isLocalClass() && extractSingleType(enclosingType().memberTypes(name())) != this)
      error("duplicate member type " + name() + " in type " + enclosingType().typeName());

    // 14.3
    if(isLocalClass()) {
      TypeDecl typeDecl = extractSingleType(lookupType(name()));
      if(typeDecl != null && typeDecl != this && typeDecl.isLocalClass() && enclosingBlock() == typeDecl.enclosingBlock())
        error("local class named " + name() + " may not be redeclared as a local class in the same block");
    }

    if(!packageName().equals("") && hasPackage(fullName()))
      error("duplicate member class and package " + name());
    
    // 8.1 & 9.1
    if(hasEnclosingTypeDecl(name())) {
      error("type may not have the same simple name as an enclosing type declaration");
    }
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:62
   */
  protected void ppBodyDecls(StringBuffer s) {
    s.append(" {");
    for(int i=0; i < getNumBodyDecl(); i++) {
      getBodyDecl(i).toString(s);
    }
    s.append(indent() + "}");
  }
  /**
   * @ast method 
   * @aspect CreateQualifiedAccesses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:96
   */
  public Access createQualifiedAccess() {
    if(isLocalClass() || isAnonymous()) {
      return new TypeAccess(name());
    }
    else if(!isTopLevelType()) {
      return enclosingType().createQualifiedAccess().qualifiesAccess(new TypeAccess(name()));
    }
    else {
      return new TypeAccess(packageName(), name());
    }
  }
  /**
   * @ast method 
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:234
   */
  public FieldDeclaration findSingleVariable(String name) {
    return (FieldDeclaration)memberFields(name).iterator().next();
  }
  /**
   * @ast method 
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:157
   */
  public void refined_TypeHierarchyCheck_TypeDecl_typeCheck() {
    // 8.4.6.4 & 9.4.1
    for(Iterator iter1 = localMethodsIterator(); iter1.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter1.next();
      ASTNode target = m.hostType() == this ? (ASTNode)m : (ASTNode)this;
      
      //for(Iterator i2 = overrides(m).iterator(); i2.hasNext(); ) {
      for(Iterator i2 = ancestorMethods(m.signature()).iterator(); i2.hasNext(); ) {
        MethodDecl decl = (MethodDecl)i2.next();
        if(m.overrides(decl)) {
          // 8.4.6.1
          if(!m.isStatic() && decl.isStatic())
            target.error("an instance method may not override a static method");
 
          // regardless of overriding
          // 8.4.6.3
          if(!m.mayOverrideReturn(decl))
            target.error("the return type of method " + m.signature() + " in " + m.hostType().typeName() + " does not match the return type of method " + decl.signature() + " in " + decl.hostType().typeName() + " and may thus not be overriden");
 
          // regardless of overriding
          // 8.4.4
          for(int i = 0; i < m.getNumException(); i++) {
            Access e = m.getException(i);
            boolean found = false;
            for(int j = 0; !found && j < decl.getNumException(); j++) {
              if(e.type().instanceOf(decl.getException(j).type()))
                found = true;
            }
            if(!found && e.type().isUncheckedException())
              target.error(m.signature() + " in " + m.hostType().typeName() + " may not throw more checked exceptions than overridden method " +
               decl.signature() + " in " + decl.hostType().typeName());
          }
          // 8.4.6.3
          if(decl.isPublic() && !m.isPublic())
            target.error("overriding access modifier error");
          // 8.4.6.3
          if(decl.isProtected() && !(m.isPublic() || m.isProtected()))
            target.error("overriding access modifier error");
          // 8.4.6.3
          if((!decl.isPrivate() && !decl.isProtected() && !decl.isPublic()) && m.isPrivate())
            target.error("overriding access modifier error");
 
          // regardless of overriding
          if(decl.isFinal())
            target.error("method " + m.signature() + " in " + hostType().typeName() + " can not override final method " + decl.signature() + " in " + decl.hostType().typeName());
        }
        if(m.hides(decl)) {
          // 8.4.6.2
          if(m.isStatic() && !decl.isStatic())
            target.error("a static method may not hide an instance method");
          // 8.4.6.3
          if(!m.mayOverrideReturn(decl))
            target.error("can not hide a method with a different return type");
          // 8.4.4
          for(int i = 0; i < m.getNumException(); i++) {
            Access e = m.getException(i);
            boolean found = false;
            for(int j = 0; !found && j < decl.getNumException(); j++) {
              if(e.type().instanceOf(decl.getException(j).type()))
                found = true;
            }
            if(!found)
              target.error("may not throw more checked exceptions than hidden method");
          }
          // 8.4.6.3
          if(decl.isPublic() && !m.isPublic())
            target.error("hiding access modifier error: public method " + decl.signature() + " in " + decl.hostType().typeName() + " is hidden by non public method " + m.signature() + " in " + m.hostType().typeName());
          // 8.4.6.3
          if(decl.isProtected() && !(m.isPublic() || m.isProtected()))
            target.error("hiding access modifier error: protected method " + decl.signature() + " in " + decl.hostType().typeName() + " is hidden by non (public|protected) method " + m.signature() + " in " + m.hostType().typeName());
          // 8.4.6.3
          if((!decl.isPrivate() && !decl.isProtected() && !decl.isPublic()) && m.isPrivate())
            target.error("hiding access modifier error: default method " + decl.signature() + " in " + decl.hostType().typeName() + " is hidden by private method " + m.signature() + " in " + m.hostType().typeName());
          if(decl.isFinal())
            target.error("method " + m.signature() + " in " + hostType().typeName() + " can not hide final method " + decl.signature() + " in " + decl.hostType().typeName());
        }
      }
    }
  }
  /**
   * @ast method 
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:162
   */
  public TypeDecl makeGeneric(Signatures.ClassSignature s) {
    return this;
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:692
   */
  public TypeDecl substitute(TypeVariable typeVariable) {
    if(isTopLevelType())
      return typeVariable;
    return enclosingType().substitute(typeVariable);
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:730
   */
  public Access substitute(Parameterization parTypeDecl) {
    if(parTypeDecl instanceof ParTypeDecl && ((ParTypeDecl)parTypeDecl).genericDecl() == this)
		  return ((TypeDecl)parTypeDecl).createBoundAccess();
	  if(isTopLevelType())
		  return createBoundAccess();
	  return enclosingType().substitute(parTypeDecl).qualifiesAccess(new TypeAccess(name()));
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:770
   */
  public Access substituteReturnType(Parameterization parTypeDecl) {
    return substitute(parTypeDecl);
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:814
   */
  public Access substituteParameterType(Parameterization parTypeDecl) {
    return substitute(parTypeDecl);
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:12
   */
  public boolean hasField(String name) {
    if(!memberFields(name).isEmpty())
      return true;
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof FieldDeclaration) {
        FieldDeclaration decl = (FieldDeclaration)getBodyDecl(i);
        if(decl.name().equals(name))
          return true;
      }
    }
    return false;
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:36
   */
  public boolean hasMethod(String id) {
    if(!memberMethods(id).isEmpty()) return true;
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof MethodDecl) {
        MethodDecl decl = (MethodDecl)getBodyDecl(i);
        if(decl.name().equals(id))
          return true;
      }
    }
    return false;
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:121
   */
  

  // The set of TypeDecls that has this TypeDecl as their directly enclosing TypeDecl.
  // I.e., NestedTypes, InnerTypes, AnonymousClasses, LocalClasses.
  private Collection nestedTypes;
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:122
   */
  public Collection nestedTypes() {
    return nestedTypes != null ? nestedTypes : new HashSet();
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:125
   */
  public void addNestedType(TypeDecl typeDecl) {
    if(nestedTypes == null) nestedTypes = new HashSet();
    if(typeDecl != this)
      nestedTypes.add(typeDecl);
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:132
   */
  

  // The set of nested TypeDecls that are accessed in this TypeDecl
  private Collection usedNestedTypes;
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:133
   */
  public Collection usedNestedTypes() {
    return usedNestedTypes != null ? usedNestedTypes : new HashSet();
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:136
   */
  public void addUsedNestedType(TypeDecl typeDecl) {
    if(usedNestedTypes == null) usedNestedTypes = new HashSet();
    usedNestedTypes.add(typeDecl);
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:167
   */
  


  public int accessorCounter = 0;
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:169
   */
  

  private HashMap accessorMap = null;
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:170
   */
  public ASTNode getAccessor(ASTNode source, String name) {
    ArrayList key = new ArrayList(2);
    key.add(source);
    key.add(name);
    if(accessorMap == null || !accessorMap.containsKey(key)) return null;
    return (ASTNode)accessorMap.get(key);
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:178
   */
  public void addAccessor(ASTNode source, String name, ASTNode accessor) {
    ArrayList key = new ArrayList(2);
    key.add(source);
    key.add(name);
    if(accessorMap == null) accessorMap = new HashMap();
    accessorMap.put(key, accessor);
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:186
   */
  public ASTNode getAccessorSource(ASTNode accessor) {
    Iterator i = accessorMap.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry entry = (Map.Entry) i.next();
      if (entry.getValue() == accessor)
        return (ASTNode) ((ArrayList) entry.getKey()).get(0);
    }
    return null;
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:430
   */
  



  // add val$name as fields to the class
  private boolean addEnclosingVariables = true;
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:431
   */
  public void addEnclosingVariables() {
    if(!addEnclosingVariables) return;
    addEnclosingVariables = false;
    for(Iterator iter = enclosingVariables().iterator(); iter.hasNext(); ) {
      Variable v = (Variable)iter.next();
      Modifiers m = new Modifiers();
      m.addModifier(new Modifier("public"));
      m.addModifier(new Modifier("synthetic"));
      m.addModifier(new Modifier("final"));
      addMemberField(new FieldDeclaration(m, v.type().createQualifiedAccess(), "val$" + v.name(), new Opt()));
    }
  }
  /**
   * @ast method 
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:11
   */
  
  int uniqueIndexCounter = 1;
  /**
   * @ast method 
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:65
   */
  
  

  // lazily build a static field for assertionsDisabled 
  private FieldDeclaration createAssertionsDisabled = null;
  /**
   * @ast method 
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:66
   */
  public FieldDeclaration createAssertionsDisabled() {
    if(createAssertionsDisabled != null)
      return createAssertionsDisabled;
    // static final boolean $assertionsDisabled = !TypeName.class.desiredAssertionStatus();
    createAssertionsDisabled = new FieldDeclaration(
      new Modifiers(new List().add(new Modifier("public")).add(new Modifier("static")).add(new Modifier("final"))),
      new PrimitiveTypeAccess("boolean"),
      "$assertionsDisabled",
      new Opt(
          new LogNotExpr(
            topLevelType().createQualifiedAccess().qualifiesAccess(
              new ClassAccess().qualifiesAccess(
                new MethodAccess(
                  "desiredAssertionStatus",
                  new List()
                )
              )
            )
          )
      )
    );
    getBodyDeclList().insertChild(createAssertionsDisabled, 0);
    // explicit read to trigger possible rewrites
    createAssertionsDisabled = (FieldDeclaration)getBodyDeclList().getChild(0);
    // transform the generated initalization, e.g., the ClassAccess construct
    createAssertionsDisabled.transformation();
    return createAssertionsDisabled;
  }
  /**
   * @ast method 
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:119
   */
  

  // lazily build a static field for each typename used in a .class expression
  private HashMap createStaticClassField = null;
  /**
   * @ast method 
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:120
   */
  public FieldDeclaration createStaticClassField(String name) {
    if(createStaticClassField == null)
      createStaticClassField = new HashMap();
    if(createStaticClassField.containsKey(name))
      return (FieldDeclaration)createStaticClassField.get(name);
    // static synthetic Class class$java$lang$String;
    FieldDeclaration f = new FieldDeclaration(
      new Modifiers(new List().add(new Modifier("public")).add(new Modifier("static"))),
      lookupType("java.lang", "Class").createQualifiedAccess(),
      name,
      new Opt()
    ) {
      public boolean isConstant() {
        return true;
      }
    };
    createStaticClassField.put(name, f);
    return addMemberField(f);
  }
  /**
   * @ast method 
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:141
   */
  

  // lazily build a static class$ method in this type declaration
  private MethodDecl createStaticClassMethod = null;
  /**
   * @ast method 
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:142
   */
  public MethodDecl createStaticClassMethod() {
    if(createStaticClassMethod != null)
      return createStaticClassMethod;
    // static synthetic Class class$(String name) {
    //   try {
    //     return java.lang.Class.forName(name);
    //   } catch(java.lang.ClassNotFoundException e) {
    //     throw new java.lang.NoClassDefFoundError(e.getMessage());
    //   }
    // }
    createStaticClassMethod = new MethodDecl(
      new Modifiers(new List().add(new Modifier("public")).add(new Modifier("static"))),
      lookupType("java.lang", "Class").createQualifiedAccess(),
      "class$",
      new List().add(
        new ParameterDeclaration(
          new Modifiers(new List()),
          lookupType("java.lang", "String").createQualifiedAccess(),
          "name"
        )
      ),
      new List(),
      new Opt(
        new Block(
          new List().add(
            new TryStmt(
              new Block(
                new List().add(
                  new ReturnStmt(
                    new Opt(
                      lookupType("java.lang", "Class").createQualifiedAccess().qualifiesAccess(
                        new MethodAccess(
                          "forName",
                          new List().add(
                            new VarAccess("name")
                          )
                        )
                      )
                    )
                  )
                )
              ),
              new List().add(
                new BasicCatch(
                  new ParameterDeclaration(
                    new Modifiers(new List()),
                    lookupType("java.lang", "ClassNotFoundException").createQualifiedAccess(),
                    "e"
                  ),
                  new Block(
                    new List().add(
                      new ThrowStmt(
                        new ClassInstanceExpr(
                          lookupType("java.lang", "NoClassDefFoundError").createQualifiedAccess(),
                          new List().add(
                            new VarAccess("e").qualifiesAccess(
                              new MethodAccess(
                                "getMessage",
                                new List()
                              )
                            )
                          ),
                          new Opt()
                        )
                      )
                    )
                  )
                )
              ),
              new Opt()
            )
          )
        )
      )
    ) {
      public boolean isConstant() {
        return true;
      }
    };
    return addMemberMethod(createStaticClassMethod);
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:27
   */
  public void transformation() {
    addEnclosingVariables();
    super.transformation();
    if(isNestedType())
      enclosingType().addNestedType(this);
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:143
   */
  


  public SootMethod clinit = null;
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:145
   */
  public void jimplify1phase2() {
    if(needsClinit() && !getSootClassDecl().declaresMethod("<clinit>", new ArrayList())) {
      clinit = new SootMethod("<clinit>", new ArrayList(), soot.VoidType.v(), soot.Modifier.STATIC, new ArrayList());
      getSootClassDecl().addMethod(clinit);
    }

    for(Iterator iter = nestedTypes().iterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      typeDecl.jimplify1phase2();
    }
    for(int i = 0; i < getNumBodyDecl(); i++)
      if(getBodyDecl(i).generate())
        getBodyDecl(i).jimplify1phase2();
    addAttributes();
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:434
   */
  public soot.Value emitCastTo(Body b, soot.Value v, TypeDecl type, ASTNode location) {
    if(this == type)
      return v;
    if(isReferenceType() && type.isReferenceType() && instanceOf(type))
      return v;
    if((isLong() || this instanceof FloatingPointType) && type.isIntegralType()) {
      v = b.newCastExpr(
        asImmediate(b, v), typeInt().getSootType(), location);
      return typeInt().emitCastTo(b, v, type, location);
    }

    return b.newCastExpr(
      asImmediate(b, v),
      type.getSootType(),
      location
    );
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:903
   */
  public void jimplify2clinit() {
      SootMethod m = clinit;
      JimpleBody body = Jimple.v().newBody(m);
      m.setActiveBody(body);
      Body b = new Body(this, body, this);
      for(int i = 0; i < getNumBodyDecl(); i++) {
        BodyDecl bodyDecl = getBodyDecl(i);
        if(bodyDecl instanceof FieldDeclaration && bodyDecl.generate()) {
          FieldDeclaration f = (FieldDeclaration)bodyDecl;
          if(f.isStatic() && f.hasInit()) {
            Local l = asLocal(b, 
              f.getInit().type().emitCastTo(b, f.getInit(), f.type()), // AssignConversion
              f.type().getSootType()
            );
            b.setLine(f);
            b.add(b.newAssignStmt(
              b.newStaticFieldRef(f.sootRef(), f),
              l,
              f
            ));
          }
        }
        else if(bodyDecl instanceof StaticInitializer && bodyDecl.generate()) {
          bodyDecl.jimplify2(b);
        }
      }
      b.add(b.newReturnVoidStmt(null));
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:932
   */
  public void jimplify2() {
    super.jimplify2();
    if(clinit != null)
      jimplify2clinit();
    for(Iterator iter = nestedTypes().iterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      typeDecl.jimplify2();
    }
    // add inner class attribute
    ArrayList tags = new ArrayList();
    for(Iterator iter = innerClassesAttributeEntries().iterator(); iter.hasNext(); ) {
      TypeDecl type = (TypeDecl)iter.next();
      tags.add(
        new soot.tagkit.InnerClassTag(
          type.jvmName().replace('.', '/'),
          type.isMemberType() ? type.enclosingType().jvmName().replace('.', '/') : null,
          type.isAnonymous() ? null : type.name(),
          type.sootTypeModifiers()
        )
      );
    }
    if(!tags.isEmpty())
      getSootClassDecl().addTag(new soot.tagkit.InnerClassAttribute(tags));
    addAttributes();
    getSootClassDecl().setResolvingLevel(SootClass.BODIES);
  }
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:20
   */
  public void addAttributes() {
    super.addAttributes();
    ArrayList c = new ArrayList();
    getModifiers().addRuntimeVisibleAnnotationsAttribute(c);
    getModifiers().addRuntimeInvisibleAnnotationsAttribute(c);
    getModifiers().addSourceOnlyAnnotations(c);
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      soot.tagkit.Tag tag = (soot.tagkit.Tag)iter.next();
      getSootClassDecl().addTag(tag);
    }
  }
  /**
   * @ast method 
   * @aspect AutoBoxingCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AutoBoxingCodegen.jrag:57
   */
  protected soot.Value emitBoxingOperation(Body b, soot.Value v, ASTNode location) {
    // Box the value on the stack into this Reference type
    ArrayList parameters = new ArrayList();
    parameters.add(unboxed().getSootType());
    SootMethodRef ref = Scene.v().makeMethodRef(
      getSootClassDecl(),
      "valueOf",
      parameters,
      getSootType(),
      true
    );
    ArrayList args = new ArrayList();
    args.add(asLocal(b, v));
    return b.newStaticInvokeExpr(ref, args, location);
  }
  /**
   * @ast method 
   * @aspect AutoBoxingCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AutoBoxingCodegen.jrag:83
   */
  protected soot.Value emitUnboxingOperation(Body b, soot.Value v, ASTNode location) {
    // Unbox the value on the stack from this Reference type
    SootMethodRef ref = Scene.v().makeMethodRef(
      getSootClassDecl(),
      unboxed().name() + "Value",
      new ArrayList(),
      unboxed().getSootType(),
      false
    );
    return b.newVirtualInvokeExpr(asLocal(b, v), ref, new ArrayList(), location);
  }
  /**
   * @ast method 
   * @aspect EnumsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:85
   */
  
  // compute index of enum constants
  private HashMap createEnumIndexMap = null;
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public TypeDecl() {
    super();

    setChild(new List(), 1);

  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public TypeDecl(Modifiers p0, String p1, List<BodyDecl> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * @declaredat java.ast:13
   */
  public TypeDecl(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:21
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:27
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Getter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat java.ast:8
   */
  
  /**   * @apilevel internal   */  protected String tokenString_ID;
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  
  public int IDstart;
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  
  public int IDend;
  /**
   * @ast method 
   * @declaredat java.ast:11
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Getter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:22
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumBodyDecl() {
    return getBodyDeclList().getNumChild();
  }
  /**
   * Getter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl getBodyDecl(int i) {
    return (BodyDecl)getBodyDeclList().getChild(i);
  }
  /**
   * Add element to list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addBodyDecl(BodyDecl node) {
    List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addBodyDeclNoTransform(BodyDecl node) {
    List<BodyDecl> list = getBodyDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setBodyDecl(BodyDecl node, int i) {
    List<BodyDecl> list = getBodyDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<BodyDecl> getBodyDecls() {
    return getBodyDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<BodyDecl> getBodyDeclsNoTransform() {
    return getBodyDeclListNoTransform();
  }
  /**
   * Getter for list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclList() {
    List<BodyDecl> list = (List<BodyDecl>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclListNoTransform() {
    return (List<BodyDecl>)getChildNoTransform(1);
  }
  /**
   * @ast method 
   * @aspect GenericsTypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:330
   */
    public void typeCheck() {
    refined_TypeHierarchyCheck_TypeDecl_typeCheck();
    ArrayList list = new ArrayList();
    list.addAll(implementedInterfaces());
    for(int i = 0; i < list.size(); i++) {
      InterfaceDecl decl = (InterfaceDecl)list.get(i);
      if(decl instanceof ParInterfaceDecl) {
        ParInterfaceDecl p = (ParInterfaceDecl)decl;
        for(Iterator i2 = list.listIterator(i); i2.hasNext(); ) {
          InterfaceDecl decl2 = (InterfaceDecl)i2.next();
          if(decl2 instanceof ParInterfaceDecl) {
            ParInterfaceDecl q = (ParInterfaceDecl)decl2;
            if(p != q && p.genericDecl() == q.genericDecl() && !p.sameArgument(q))
              error(p.genericDecl().name() + " cannot be inherited with different arguments: " +
                p.typeName() + " and " + q.typeName());
          }
        }
      }
    }
  }
  /**
   * @ast method 
   * @aspect AutoBoxingCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AutoBoxingCodegen.jrag:42
   */
    public soot.Value emitCastTo(Body b, Expr expr, TypeDecl type) {
    if(type instanceof LUBType || type instanceof GLBType || type instanceof AbstractWildcardType)
      type = typeObject();
    else if(expr.isConstant() && isPrimitive() && type.isReferenceType())
    	  return boxed().emitBoxingOperation(b, emitConstant(cast(expr.constant())), expr);
    else if(expr.isConstant() && !expr.type().isEnumDecl()) {
      if(type.isPrimitive())
        return emitConstant(type.cast(expr.constant()));
      else
        return emitConstant(expr.constant());
    }
    return emitCastTo(b, expr.eval(b), type, expr);
  }
  /**
   * @ast method 
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:59
   */
  private boolean refined_TypeConversion_TypeDecl_assignConversionTo_TypeDecl_Expr(TypeDecl type, Expr expr)
{
    //System.out.println("@@@ " + fullName() + " assign conversion to " + type.fullName() + ", expr: " + expr);
    boolean sourceIsConstant = expr != null ? expr.isConstant() : false;
    //System.out.println("@@@ sourceIsConstant: " + sourceIsConstant);
    if(identityConversionTo(type) || wideningConversionTo(type))
      return true;
    //System.out.println("@@@ narrowing conversion needed");
    //System.out.println("@@@ value: " + expr.value());
    if(sourceIsConstant && (isInt() || isChar() || isShort() || isByte()) &&
        (type.isByte() || type.isShort() || type.isChar()) &&
        narrowingConversionTo(type) && expr.representableIn(type))
      return true;
    //System.out.println("@@@ false");
    return false;
  }
  /**
   * @ast method 
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:76
   */
  private boolean refined_TypeConversion_TypeDecl_methodInvocationConversionTo_TypeDecl(TypeDecl type)
{
    return identityConversionTo(type) || wideningConversionTo(type);
  }
  /**
   * @ast method 
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:81
   */
  private boolean refined_TypeConversion_TypeDecl_castingConversionTo_TypeDecl(TypeDecl type)
{ return identityConversionTo(type) ||
    wideningConversionTo(type) || narrowingConversionTo(type); }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:32
   */
  private SootClass refined_EmitJimple_TypeDecl_getSootClassDecl()
{
    if(compilationUnit().fromSource()) {
      return sootClass();
    }
    else {
      if(options().verbose())
        System.out.println("Loading .class file " + jvmName());
      SootClass sc = Scene.v().loadClass(jvmName(), SootClass.SIGNATURES);
      sc.setLibraryClass();
      return sc;
    }
  }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:46
   */
  private Type refined_EmitJimple_TypeDecl_getSootType()
{ return getSootClassDecl().getType(); }
  /**
   * @ast method 
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:65
   */
  private SootClass refined_EmitJimple_TypeDecl_sootClass()
{ return null; }
  protected java.util.Map accessibleFromPackage_String_values;
  /**
   * @attribute syn
   * @aspect AccessControl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean accessibleFromPackage(String packageName) {
    Object _parameters = packageName;
    if(accessibleFromPackage_String_values == null) accessibleFromPackage_String_values = new java.util.HashMap(4);
    if(accessibleFromPackage_String_values.containsKey(_parameters)) {
      return ((Boolean)accessibleFromPackage_String_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean accessibleFromPackage_String_value = accessibleFromPackage_compute(packageName);
if(isFinal && num == state().boundariesCrossed) accessibleFromPackage_String_values.put(_parameters, Boolean.valueOf(accessibleFromPackage_String_value));
    return accessibleFromPackage_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean accessibleFromPackage_compute(String packageName) {  return !isPrivate() && (isPublic() || hostPackage().equals(packageName));  }
  protected java.util.Map accessibleFromExtend_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect AccessControl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:18
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean accessibleFromExtend(TypeDecl type) {
    Object _parameters = type;
    if(accessibleFromExtend_TypeDecl_values == null) accessibleFromExtend_TypeDecl_values = new java.util.HashMap(4);
    if(accessibleFromExtend_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)accessibleFromExtend_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean accessibleFromExtend_TypeDecl_value = accessibleFromExtend_compute(type);
if(isFinal && num == state().boundariesCrossed) accessibleFromExtend_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFromExtend_TypeDecl_value));
    return accessibleFromExtend_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean accessibleFromExtend_compute(TypeDecl type) {
    if(type == this)
      return true;
    if(isInnerType()) { 
      if(!enclosingType().accessibleFrom(type)) {
        return false;
      }
    }
    if(isPublic()) 
      return true;
    else if(isProtected()) {
      // isProtected implies a nested type
      if(hostPackage().equals(type.hostPackage())) {
        return true;
      }
      if(type.isNestedType() && type.enclosingType().withinBodyThatSubclasses(enclosingType()) != null)
        return true;
      return false;
    }
    else if(isPrivate()) {
      return topLevelType() == type.topLevelType();
    }
    else
      return hostPackage().equals(type.hostPackage());
  }
  protected java.util.Map accessibleFrom_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect AccessControl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean accessibleFrom(TypeDecl type) {
    Object _parameters = type;
    if(accessibleFrom_TypeDecl_values == null) accessibleFrom_TypeDecl_values = new java.util.HashMap(4);
    if(accessibleFrom_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)accessibleFrom_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean accessibleFrom_TypeDecl_value = accessibleFrom_compute(type);
if(isFinal && num == state().boundariesCrossed) accessibleFrom_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFrom_TypeDecl_value));
    return accessibleFrom_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean accessibleFrom_compute(TypeDecl type) {
    if(type == this)
      return true;
    if(isInnerType()) { 
      if(!enclosingType().accessibleFrom(type)) {
        return false;
      }
    }
    if(isPublic()) {  
      return true;
    }
    else if(isProtected()) {
      if(hostPackage().equals(type.hostPackage())) {
        return true;
      }
      if(isMemberType()) {
        TypeDecl typeDecl = type;
        while(typeDecl != null && !typeDecl.instanceOf(enclosingType()))
          typeDecl = typeDecl.enclosingType();
        if(typeDecl != null) {
          return true;
        }
      }
      return false;
    }
    else if(isPrivate()) {
      return topLevelType() == type.topLevelType();
    }
    else {
      return hostPackage().equals(type.hostPackage());
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean dimension_computed = false;
  /**
   * @apilevel internal
   */
  protected int dimension_value;
  /**
   * @attribute syn
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:11
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int dimension() {
    if(dimension_computed) {
      return dimension_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    dimension_value = dimension_compute();
if(isFinal && num == state().boundariesCrossed) dimension_computed = true;
    return dimension_value;
  }
  /**
   * @apilevel internal
   */
  private int dimension_compute() {  return 0;  }
  /**
   * @apilevel internal
   */
  protected boolean elementType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl elementType_value;
  /**
   * @attribute syn
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl elementType() {
    if(elementType_computed) {
      return elementType_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    elementType_value = elementType_compute();
if(isFinal && num == state().boundariesCrossed) elementType_computed = true;
    return elementType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl elementType_compute() {  return this;  }
  /**
   * @apilevel internal
   */
  protected boolean arrayType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl arrayType_value;
  /**
   * @attribute syn
   * @aspect GenericsArrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsArrays.jrag:11
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl arrayType() {
    if(arrayType_computed) {
      return arrayType_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    arrayType_value = arrayType_compute();
    arrayType_value.setParent(this);
    arrayType_value.is$Final = true;
if(true) arrayType_computed = true;
    return arrayType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl arrayType_compute() {
    String name = name() + "[]";

    List body = new List();
    body.add(
      new FieldDeclaration(
        new Modifiers(new List().add(new Modifier("public")).add(new Modifier("final"))),
        new PrimitiveTypeAccess("int"),
        "length",
        new Opt() // [Init:Expr]
      )
    );
    MethodDecl clone = null;
    TypeDecl typeObject = typeObject();
    for(int i = 0; clone == null && i < typeObject.getNumBodyDecl(); i++) {
      if(typeObject.getBodyDecl(i) instanceof MethodDecl) {
        MethodDecl m = (MethodDecl)typeObject.getBodyDecl(i);
        if(m.name().equals("clone"))
          clone = m;
      }
    }
    if(clone != null) {
      body.add(
          // we create a substituted method that substitutes the clone method in object
          // this has the following two consequences: the return value will be cast to the
          // expected return type rather than object, and the invoked method will be the
          // method in object rather in the array
          new MethodDeclSubstituted(
            new Modifiers(new List().add(new Modifier("public"))),
            new ArrayTypeAccess(createQualifiedAccess()),
            "clone",
            new List(),
            new List(),
            new Opt(new Block()),
            (MethodDecl)typeObject().memberMethods("clone").iterator().next()
          )
      );
    }
    TypeDecl typeDecl =
      new ArrayDecl(
        new Modifiers(new List().add(new Modifier("public"))),
        name,
        new Opt(typeObject().createQualifiedAccess()), // [SuperClassAccess]
        new List().add(typeCloneable().createQualifiedAccess()).add(typeSerializable().createQualifiedAccess()), // Implements*
        body // BodyDecl*
      );
    return typeDecl;
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:306
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant cast(Constant c) {
      ASTNode$State state = state();
    Constant cast_Constant_value = cast_compute(c);
    return cast_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant cast_compute(Constant c) {
    throw new UnsupportedOperationException("ConstantExpression operation cast" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:320
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant plus(Constant c) {
      ASTNode$State state = state();
    Constant plus_Constant_value = plus_compute(c);
    return plus_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant plus_compute(Constant c) {
    throw new UnsupportedOperationException("ConstantExpression operation plus" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:329
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant minus(Constant c) {
      ASTNode$State state = state();
    Constant minus_Constant_value = minus_compute(c);
    return minus_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant minus_compute(Constant c) {
    throw new UnsupportedOperationException("ConstantExpression operation minus" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:338
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant bitNot(Constant c) {
      ASTNode$State state = state();
    Constant bitNot_Constant_value = bitNot_compute(c);
    return bitNot_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant bitNot_compute(Constant c) {
    throw new UnsupportedOperationException("ConstantExpression operation bitNot" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:345
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant mul(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant mul_Constant_Constant_value = mul_compute(c1, c2);
    return mul_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant mul_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation mul" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:354
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant div(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant div_Constant_Constant_value = div_compute(c1, c2);
    return div_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant div_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation div" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:363
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant mod(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant mod_Constant_Constant_value = mod_compute(c1, c2);
    return mod_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant mod_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation mod" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:372
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant add(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant add_Constant_Constant_value = add_compute(c1, c2);
    return add_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant add_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation add" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:382
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant sub(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant sub_Constant_Constant_value = sub_compute(c1, c2);
    return sub_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant sub_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation sub" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:391
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant lshift(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant lshift_Constant_Constant_value = lshift_compute(c1, c2);
    return lshift_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant lshift_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation lshift" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:398
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant rshift(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant rshift_Constant_Constant_value = rshift_compute(c1, c2);
    return rshift_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant rshift_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation rshift" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:405
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant urshift(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant urshift_Constant_Constant_value = urshift_compute(c1, c2);
    return urshift_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant urshift_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation urshift" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:412
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant andBitwise(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant andBitwise_Constant_Constant_value = andBitwise_compute(c1, c2);
    return andBitwise_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant andBitwise_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation andBitwise" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:420
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant xorBitwise(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant xorBitwise_Constant_Constant_value = xorBitwise_compute(c1, c2);
    return xorBitwise_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant xorBitwise_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation xorBitwise" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:428
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant orBitwise(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant orBitwise_Constant_Constant_value = orBitwise_compute(c1, c2);
    return orBitwise_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant orBitwise_compute(Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation orBitwise" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:436
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant questionColon(Constant cond, Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant questionColon_Constant_Constant_Constant_value = questionColon_compute(cond, c1, c2);
    return questionColon_Constant_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant questionColon_compute(Constant cond, Constant c1, Constant c2) {
    throw new UnsupportedOperationException("ConstantExpression operation questionColon" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:540
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean eqIsTrue(Expr left, Expr right) {
      ASTNode$State state = state();
    boolean eqIsTrue_Expr_Expr_value = eqIsTrue_compute(left, right);
    return eqIsTrue_Expr_Expr_value;
  }
  /**
   * @apilevel internal
   */
  private boolean eqIsTrue_compute(Expr left, Expr right) {
    System.err.println("Evaluation eqIsTrue for unknown type: " + getClass().getName());
    return false;
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:551
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean ltIsTrue(Expr left, Expr right) {
      ASTNode$State state = state();
    boolean ltIsTrue_Expr_Expr_value = ltIsTrue_compute(left, right);
    return ltIsTrue_Expr_Expr_value;
  }
  /**
   * @apilevel internal
   */
  private boolean ltIsTrue_compute(Expr left, Expr right) {  return false;  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:557
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean leIsTrue(Expr left, Expr right) {
      ASTNode$State state = state();
    boolean leIsTrue_Expr_Expr_value = leIsTrue_compute(left, right);
    return leIsTrue_Expr_Expr_value;
  }
  /**
   * @apilevel internal
   */
  private boolean leIsTrue_compute(Expr left, Expr right) {  return false;  }
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:134
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int size() {
      ASTNode$State state = state();
    int size_value = size_compute();
    return size_value;
  }
  /**
   * @apilevel internal
   */
  private int size_compute() {  return 1;  }
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:135
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isEmpty() {
      ASTNode$State state = state();
    boolean isEmpty_value = isEmpty_compute();
    return isEmpty_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isEmpty_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect DataStructures
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DataStructures.jrag:139
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean contains(Object o) {
      ASTNode$State state = state();
    boolean contains_Object_value = contains_compute(o);
    return contains_Object_value;
  }
  /**
   * @apilevel internal
   */
  private boolean contains_compute(Object o) {  return this == o;  }
  /**
   * @apilevel internal
   */
  protected boolean isException_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isException_value;
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:24
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isException() {
    if(isException_computed) {
      return isException_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isException_value = isException_compute();
if(isFinal && num == state().boundariesCrossed) isException_computed = true;
    return isException_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isException_compute() {  return instanceOf(typeException());  }
  /**
   * @apilevel internal
   */
  protected boolean isCheckedException_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isCheckedException_value;
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:25
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isCheckedException() {
    if(isCheckedException_computed) {
      return isCheckedException_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isCheckedException_value = isCheckedException_compute();
if(isFinal && num == state().boundariesCrossed) isCheckedException_computed = true;
    return isCheckedException_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isCheckedException_compute() {  return isException() &&
    (instanceOf(typeRuntimeException()) || instanceOf(typeError()));  }
  /**
   * @apilevel internal
   */
  protected boolean isUncheckedException_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isUncheckedException_value;
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:27
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isUncheckedException() {
    if(isUncheckedException_computed) {
      return isUncheckedException_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isUncheckedException_value = isUncheckedException_compute();
if(isFinal && num == state().boundariesCrossed) isUncheckedException_computed = true;
    return isUncheckedException_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isUncheckedException_compute() {  return isException() && !isCheckedException();  }
  protected java.util.Map mayCatch_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:236
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean mayCatch(TypeDecl thrownType) {
    Object _parameters = thrownType;
    if(mayCatch_TypeDecl_values == null) mayCatch_TypeDecl_values = new java.util.HashMap(4);
    if(mayCatch_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)mayCatch_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean mayCatch_TypeDecl_value = mayCatch_compute(thrownType);
if(isFinal && num == state().boundariesCrossed) mayCatch_TypeDecl_values.put(_parameters, Boolean.valueOf(mayCatch_TypeDecl_value));
    return mayCatch_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean mayCatch_compute(TypeDecl thrownType) {  return thrownType.instanceOf(this) || this.instanceOf(thrownType);  }
  /**
   * @attribute syn
   * @aspect ConstructScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:21
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection lookupSuperConstructor() {
      ASTNode$State state = state();
    Collection lookupSuperConstructor_value = lookupSuperConstructor_compute();
    return lookupSuperConstructor_value;
  }
  /**
   * @apilevel internal
   */
  private Collection lookupSuperConstructor_compute() {  return Collections.EMPTY_LIST;  }
  /**
   * @apilevel internal
   */
  protected boolean constructors_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection constructors_value;
  /**
   * @attribute syn
   * @aspect ConstructorLookup
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:98
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection constructors() {
    if(constructors_computed) {
      return constructors_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    constructors_value = constructors_compute();
if(isFinal && num == state().boundariesCrossed) constructors_computed = true;
    return constructors_value;
  }
  /**
   * @apilevel internal
   */
  private Collection constructors_compute() {
    Collection c = new ArrayList();
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof ConstructorDecl) {
        c.add(getBodyDecl(i));
      }
    }
    return c;
  }
  protected java.util.Map unqualifiedLookupMethod_String_values;
  /**
   * @attribute syn
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:36
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection unqualifiedLookupMethod(String name) {
    Object _parameters = name;
    if(unqualifiedLookupMethod_String_values == null) unqualifiedLookupMethod_String_values = new java.util.HashMap(4);
    if(unqualifiedLookupMethod_String_values.containsKey(_parameters)) {
      return (Collection)unqualifiedLookupMethod_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    Collection unqualifiedLookupMethod_String_value = unqualifiedLookupMethod_compute(name);
if(isFinal && num == state().boundariesCrossed) unqualifiedLookupMethod_String_values.put(_parameters, unqualifiedLookupMethod_String_value);
    return unqualifiedLookupMethod_String_value;
  }
  /**
   * @apilevel internal
   */
  private Collection unqualifiedLookupMethod_compute(String name) {
    Collection c = memberMethods(name);
    if(!c.isEmpty()) return c;
    if(isInnerType())
      return lookupMethod(name);
    return removeInstanceMethods(lookupMethod(name));
  }
  /**
   * @attribute syn
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:199
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection memberMethods(String name) {
      ASTNode$State state = state();
    Collection memberMethods_String_value = memberMethods_compute(name);
    return memberMethods_String_value;
  }
  /**
   * @apilevel internal
   */
  private Collection memberMethods_compute(String name) {
    Collection c = (Collection)methodsNameMap().get(name);
    if(c != null) return c;
    return Collections.EMPTY_LIST;
  }
  /**
   * @apilevel internal
   */
  protected boolean methodsNameMap_computed = false;
  /**
   * @apilevel internal
   */
  protected HashMap methodsNameMap_value;
  /**
   * @attribute syn
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:205
   */
  @SuppressWarnings({"unchecked", "cast"})
  public HashMap methodsNameMap() {
    if(methodsNameMap_computed) {
      return methodsNameMap_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    methodsNameMap_value = methodsNameMap_compute();
if(isFinal && num == state().boundariesCrossed) methodsNameMap_computed = true;
    return methodsNameMap_value;
  }
  /**
   * @apilevel internal
   */
  private HashMap methodsNameMap_compute() {
    HashMap map = new HashMap();
    for(Iterator iter = methodsIterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      ArrayList list = (ArrayList)map.get(m.name());
      if(list == null) {
        list = new ArrayList(4);
        map.put(m.name(), list);
      }
      list.add(m);
    }
    return map;
  }
  /**
   * @attribute syn
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:236
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localMethodsSignature(String signature) {
      ASTNode$State state = state();
    SimpleSet localMethodsSignature_String_value = localMethodsSignature_compute(signature);
    return localMethodsSignature_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localMethodsSignature_compute(String signature) {
    SimpleSet set = (SimpleSet)localMethodsSignatureMap().get(signature);
    if(set != null) return set;
    return SimpleSet.emptySet;
  }
  /**
   * @apilevel internal
   */
  protected boolean localMethodsSignatureMap_computed = false;
  /**
   * @apilevel internal
   */
  protected HashMap localMethodsSignatureMap_value;
  /**
   * @attribute syn
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:242
   */
  @SuppressWarnings({"unchecked", "cast"})
  public HashMap localMethodsSignatureMap() {
    if(localMethodsSignatureMap_computed) {
      return localMethodsSignatureMap_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    localMethodsSignatureMap_value = localMethodsSignatureMap_compute();
if(isFinal && num == state().boundariesCrossed) localMethodsSignatureMap_computed = true;
    return localMethodsSignatureMap_value;
  }
  /**
   * @apilevel internal
   */
  private HashMap localMethodsSignatureMap_compute() {
    HashMap map = new HashMap(getNumBodyDecl());
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof MethodDecl) {
        MethodDecl decl = (MethodDecl)getBodyDecl(i);
        map.put(decl.signature(), decl);
      }
    }
    return map;
  }
  /**
   * @attribute syn
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:304
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet methodsSignature(String signature) {
      ASTNode$State state = state();
    SimpleSet methodsSignature_String_value = methodsSignature_compute(signature);
    return methodsSignature_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet methodsSignature_compute(String signature) {
    SimpleSet set = (SimpleSet)methodsSignatureMap().get(signature);
    if(set != null) return set;
    return SimpleSet.emptySet;
  }
  /**
   * @apilevel internal
   */
  protected boolean methodsSignatureMap_computed = false;
  /**
   * @apilevel internal
   */
  protected HashMap methodsSignatureMap_value;
  /**
   * @attribute syn
   * @aspect MemberMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:310
   */
  @SuppressWarnings({"unchecked", "cast"})
  public HashMap methodsSignatureMap() {
    if(methodsSignatureMap_computed) {
      return methodsSignatureMap_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    methodsSignatureMap_value = methodsSignatureMap_compute();
if(isFinal && num == state().boundariesCrossed) methodsSignatureMap_computed = true;
    return methodsSignatureMap_value;
  }
  /**
   * @apilevel internal
   */
  private HashMap methodsSignatureMap_compute() {  return localMethodsSignatureMap();  }
  protected java.util.Map ancestorMethods_String_values;
  /**
   * @attribute syn
   * @aspect AncestorMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:367
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet ancestorMethods(String signature) {
    Object _parameters = signature;
    if(ancestorMethods_String_values == null) ancestorMethods_String_values = new java.util.HashMap(4);
    if(ancestorMethods_String_values.containsKey(_parameters)) {
      return (SimpleSet)ancestorMethods_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet ancestorMethods_String_value = ancestorMethods_compute(signature);
if(isFinal && num == state().boundariesCrossed) ancestorMethods_String_values.put(_parameters, ancestorMethods_String_value);
    return ancestorMethods_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet ancestorMethods_compute(String signature) {  return SimpleSet.emptySet;  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:390
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasType(String name) {
      ASTNode$State state = state();
    boolean hasType_String_value = hasType_compute(name);
    return hasType_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasType_compute(String name) {  return !memberTypes(name).isEmpty();  }
  protected java.util.Map localTypeDecls_String_values;
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:401
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localTypeDecls(String name) {
    Object _parameters = name;
    if(localTypeDecls_String_values == null) localTypeDecls_String_values = new java.util.HashMap(4);
    if(localTypeDecls_String_values.containsKey(_parameters)) {
      return (SimpleSet)localTypeDecls_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet localTypeDecls_String_value = localTypeDecls_compute(name);
if(isFinal && num == state().boundariesCrossed) localTypeDecls_String_values.put(_parameters, localTypeDecls_String_value);
    return localTypeDecls_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localTypeDecls_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumBodyDecl(); i++)
      if(getBodyDecl(i).declaresType(name))
        set = set.add(getBodyDecl(i).type(name));
    return set;
  }
  protected java.util.Map memberTypes_String_values;
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:409
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet memberTypes(String name) {
    Object _parameters = name;
    if(memberTypes_String_values == null) memberTypes_String_values = new java.util.HashMap(4);
    if(memberTypes_String_values.containsKey(_parameters)) {
      return (SimpleSet)memberTypes_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet memberTypes_String_value = memberTypes_compute(name);
if(isFinal && num == state().boundariesCrossed) memberTypes_String_values.put(_parameters, memberTypes_String_value);
    return memberTypes_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet memberTypes_compute(String name) {  return SimpleSet.emptySet;  }
  protected java.util.Map localFields_String_values;
  /**
   * @attribute syn
   * @aspect Fields
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:257
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localFields(String name) {
    Object _parameters = name;
    if(localFields_String_values == null) localFields_String_values = new java.util.HashMap(4);
    if(localFields_String_values.containsKey(_parameters)) {
      return (SimpleSet)localFields_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet localFields_String_value = localFields_compute(name);
if(isFinal && num == state().boundariesCrossed) localFields_String_values.put(_parameters, localFields_String_value);
    return localFields_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localFields_compute(String name) {  return localFieldsMap().containsKey(name) ? (SimpleSet)localFieldsMap().get(name) : SimpleSet.emptySet;  }
  /**
   * @apilevel internal
   */
  protected boolean localFieldsMap_computed = false;
  /**
   * @apilevel internal
   */
  protected HashMap localFieldsMap_value;
  /**
   * @attribute syn
   * @aspect Fields
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:260
   */
  @SuppressWarnings({"unchecked", "cast"})
  public HashMap localFieldsMap() {
    if(localFieldsMap_computed) {
      return localFieldsMap_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    localFieldsMap_value = localFieldsMap_compute();
if(isFinal && num == state().boundariesCrossed) localFieldsMap_computed = true;
    return localFieldsMap_value;
  }
  /**
   * @apilevel internal
   */
  private HashMap localFieldsMap_compute() {
    HashMap map = new HashMap();
    for(int i = 0; i < getNumBodyDecl(); i++) {
      if(getBodyDecl(i) instanceof FieldDeclaration) {
        FieldDeclaration decl = (FieldDeclaration)getBodyDecl(i);
        SimpleSet fields = (SimpleSet)map.get(decl.name());
        if(fields == null) fields = SimpleSet.emptySet;
        fields = fields.add(decl);
        map.put(decl.name(), fields);
      }
    }
    return map;
  }
  /**
   * @apilevel internal
   */
  protected boolean memberFieldsMap_computed = false;
  /**
   * @apilevel internal
   */
  protected HashMap memberFieldsMap_value;
  /**
   * @attribute syn
   * @aspect Fields
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:273
   */
  @SuppressWarnings({"unchecked", "cast"})
  public HashMap memberFieldsMap() {
    if(memberFieldsMap_computed) {
      return memberFieldsMap_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    memberFieldsMap_value = memberFieldsMap_compute();
if(isFinal && num == state().boundariesCrossed) memberFieldsMap_computed = true;
    return memberFieldsMap_value;
  }
  /**
   * @apilevel internal
   */
  private HashMap memberFieldsMap_compute() {  return localFieldsMap();  }
  protected java.util.Map memberFields_String_values;
  /**
   * @attribute syn
   * @aspect Fields
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:322
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet memberFields(String name) {
    Object _parameters = name;
    if(memberFields_String_values == null) memberFields_String_values = new java.util.HashMap(4);
    if(memberFields_String_values.containsKey(_parameters)) {
      return (SimpleSet)memberFields_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet memberFields_String_value = memberFields_compute(name);
if(isFinal && num == state().boundariesCrossed) memberFields_String_values.put(_parameters, memberFields_String_value);
    return memberFields_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet memberFields_compute(String name) {  return localFields(name);  }
  /**
   * @apilevel internal
   */
  protected boolean hasAbstract_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean hasAbstract_value;
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:14
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasAbstract() {
    if(hasAbstract_computed) {
      return hasAbstract_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    hasAbstract_value = hasAbstract_compute();
if(isFinal && num == state().boundariesCrossed) hasAbstract_computed = true;
    return hasAbstract_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasAbstract_compute() {  return false;  }
  /**
   * @apilevel internal
   */
  protected boolean unimplementedMethods_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection unimplementedMethods_value;
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:16
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection unimplementedMethods() {
    if(unimplementedMethods_computed) {
      return unimplementedMethods_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    unimplementedMethods_value = unimplementedMethods_compute();
if(isFinal && num == state().boundariesCrossed) unimplementedMethods_computed = true;
    return unimplementedMethods_value;
  }
  /**
   * @apilevel internal
   */
  private Collection unimplementedMethods_compute() {  return Collections.EMPTY_LIST;  }
  /**
   * @apilevel internal
   */
  protected boolean isPublic_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isPublic_value;
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:200
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isPublic() {
    if(isPublic_computed) {
      return isPublic_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isPublic_value = isPublic_compute();
if(isFinal && num == state().boundariesCrossed) isPublic_computed = true;
    return isPublic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isPublic_compute() {  return getModifiers().isPublic() || isMemberType() && enclosingType().isInterfaceDecl();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:202
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isPrivate() {
      ASTNode$State state = state();
    boolean isPrivate_value = isPrivate_compute();
    return isPrivate_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isPrivate_compute() {  return getModifiers().isPrivate();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:203
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isProtected() {
      ASTNode$State state = state();
    boolean isProtected_value = isProtected_compute();
    return isProtected_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isProtected_compute() {  return getModifiers().isProtected();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:204
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isAbstract() {
      ASTNode$State state = state();
    boolean isAbstract_value = isAbstract_compute();
    return isAbstract_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isAbstract_compute() {  return getModifiers().isAbstract();  }
  /**
   * @apilevel internal
   */
  protected boolean isStatic_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isStatic_value;
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:206
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isStatic() {
    if(isStatic_computed) {
      return isStatic_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isStatic_value = isStatic_compute();
if(isFinal && num == state().boundariesCrossed) isStatic_computed = true;
    return isStatic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isStatic_compute() {  return getModifiers().isStatic() || isMemberType() && enclosingType().isInterfaceDecl();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:209
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isFinal() {
      ASTNode$State state = state();
    boolean isFinal_value = isFinal_compute();
    return isFinal_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isFinal_compute() {  return getModifiers().isFinal();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:210
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isStrictfp() {
      ASTNode$State state = state();
    boolean isStrictfp_value = isStrictfp_compute();
    return isStrictfp_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isStrictfp_compute() {  return getModifiers().isStrictfp();  }
  /**
   * @attribute syn
   * @aspect Modifiers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:212
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSynthetic() {
      ASTNode$State state = state();
    boolean isSynthetic_value = isSynthetic_compute();
    return isSynthetic_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSynthetic_compute() {  return getModifiers().isSynthetic();  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:269
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasEnclosingTypeDecl(String name) {
      ASTNode$State state = state();
    boolean hasEnclosingTypeDecl_String_value = hasEnclosingTypeDecl_compute(name);
    return hasEnclosingTypeDecl_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasEnclosingTypeDecl_compute(String name) {
    TypeDecl enclosingType = enclosingType();
    if(enclosingType != null) {
      return enclosingType.name().equals(name) || enclosingType.hasEnclosingTypeDecl(name);
    }
    return false;
  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:422
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean assignableToInt() {
      ASTNode$State state = state();
    boolean assignableToInt_value = assignableToInt_compute();
    return assignableToInt_value;
  }
  /**
   * @apilevel internal
   */
  private boolean assignableToInt_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:759
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean addsIndentationLevel() {
      ASTNode$State state = state();
    boolean addsIndentationLevel_value = addsIndentationLevel_compute();
    return addsIndentationLevel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean addsIndentationLevel_compute() {  return true;  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:810
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String dumpString() {
      ASTNode$State state = state();
    String dumpString_value = dumpString_compute();
    return dumpString_value;
  }
  /**
   * @apilevel internal
   */
  private String dumpString_compute() {  return getClass().getName() + " [" + getID() + "]";  }
  /**
   * @attribute syn
   * @aspect TypeName
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:68
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String name() {
      ASTNode$State state = state();
    String name_value = name_compute();
    return name_value;
  }
  /**
   * @apilevel internal
   */
  private String name_compute() {  return getID();  }
  /**
   * @apilevel internal
   */
  protected boolean fullName_computed = false;
  /**
   * @apilevel internal
   */
  protected String fullName_value;
  /**
   * @attribute syn
   * @aspect TypeName
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:70
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String fullName() {
    if(fullName_computed) {
      return fullName_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    fullName_value = fullName_compute();
if(isFinal && num == state().boundariesCrossed) fullName_computed = true;
    return fullName_value;
  }
  /**
   * @apilevel internal
   */
  private String fullName_compute() {
    if(isNestedType())
      return enclosingType().fullName() + "." + name();
    String packageName = packageName();
    if(packageName.equals(""))
      return name();
    return packageName + "." + name();
  }
  /**
   * @apilevel internal
   */
  protected boolean typeName_computed = false;
  /**
   * @apilevel internal
   */
  protected String typeName_value;
  /**
   * @attribute syn
   * @aspect TypeName
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:79
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String typeName() {
    if(typeName_computed) {
      return typeName_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeName_value = typeName_compute();
if(isFinal && num == state().boundariesCrossed) typeName_computed = true;
    return typeName_value;
  }
  /**
   * @apilevel internal
   */
  private String typeName_compute() {
    if(isNestedType())
      return enclosingType().typeName() + "." + name();
    String packageName = packageName();
    if(packageName.equals("") || packageName.equals(PRIMITIVE_PACKAGE_NAME))
      return name();
    return packageName + "." + name();
  }
  /**
   * @attribute syn
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean identityConversionTo(TypeDecl type) {
      ASTNode$State state = state();
    boolean identityConversionTo_TypeDecl_value = identityConversionTo_compute(type);
    return identityConversionTo_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean identityConversionTo_compute(TypeDecl type) {  return this == type;  }
  /**
   * @attribute syn
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:17
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean wideningConversionTo(TypeDecl type) {
      ASTNode$State state = state();
    boolean wideningConversionTo_TypeDecl_value = wideningConversionTo_compute(type);
    return wideningConversionTo_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean wideningConversionTo_compute(TypeDecl type) {  return instanceOf(type);  }
  protected java.util.Map narrowingConversionTo_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:18
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean narrowingConversionTo(TypeDecl type) {
    Object _parameters = type;
    if(narrowingConversionTo_TypeDecl_values == null) narrowingConversionTo_TypeDecl_values = new java.util.HashMap(4);
    if(narrowingConversionTo_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)narrowingConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean narrowingConversionTo_TypeDecl_value = narrowingConversionTo_compute(type);
if(isFinal && num == state().boundariesCrossed) narrowingConversionTo_TypeDecl_values.put(_parameters, Boolean.valueOf(narrowingConversionTo_TypeDecl_value));
    return narrowingConversionTo_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean narrowingConversionTo_compute(TypeDecl type) {  return instanceOf(type);  }
  /**
   * @attribute syn
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:55
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean stringConversion() {
      ASTNode$State state = state();
    boolean stringConversion_value = stringConversion_compute();
    return stringConversion_value;
  }
  /**
   * @apilevel internal
   */
  private boolean stringConversion_compute() {  return true;  }
  /**
   * @attribute syn
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:77
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean assignConversionTo(TypeDecl type, Expr expr) {
      ASTNode$State state = state();
    boolean assignConversionTo_TypeDecl_Expr_value = assignConversionTo_compute(type, expr);
    return assignConversionTo_TypeDecl_Expr_value;
  }
  /**
   * @apilevel internal
   */
  private boolean assignConversionTo_compute(TypeDecl type, Expr expr) {
    if(refined_TypeConversion_TypeDecl_assignConversionTo_TypeDecl_Expr(type, expr))
      return true;
    boolean canBoxThis = this instanceof PrimitiveType;
    boolean canBoxType = type instanceof PrimitiveType;
    boolean canUnboxThis = !unboxed().isUnknown();
    boolean canUnboxType = !type.unboxed().isUnknown();
    TypeDecl t = !canUnboxThis && canUnboxType ? type.unboxed() : type;
    boolean sourceIsConstant = expr != null ? expr.isConstant() : false;
    if(sourceIsConstant && (isInt() || isChar() || isShort() || isByte()) &&
        (t.isByte() || t.isShort() || t.isChar()) &&
        narrowingConversionTo(t) && expr.representableIn(t))
      return true;
    if(canBoxThis && !canBoxType && boxed().wideningConversionTo(type))
      return true;
    else if(canUnboxThis && !canUnboxType && unboxed().wideningConversionTo(type))
      return true;

    return false;
  }
  protected java.util.Map methodInvocationConversionTo_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:99
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean methodInvocationConversionTo(TypeDecl type) {
    Object _parameters = type;
    if(methodInvocationConversionTo_TypeDecl_values == null) methodInvocationConversionTo_TypeDecl_values = new java.util.HashMap(4);
    if(methodInvocationConversionTo_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)methodInvocationConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean methodInvocationConversionTo_TypeDecl_value = methodInvocationConversionTo_compute(type);
if(isFinal && num == state().boundariesCrossed) methodInvocationConversionTo_TypeDecl_values.put(_parameters, Boolean.valueOf(methodInvocationConversionTo_TypeDecl_value));
    return methodInvocationConversionTo_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean methodInvocationConversionTo_compute(TypeDecl type) {
    if(refined_TypeConversion_TypeDecl_methodInvocationConversionTo_TypeDecl(type))
      return true;
    boolean canBoxThis = this instanceof PrimitiveType;
    boolean canBoxType = type instanceof PrimitiveType;
    boolean canUnboxThis = !unboxed().isUnknown();
    boolean canUnboxType = !type.unboxed().isUnknown();
    if(canBoxThis && !canBoxType)
      return boxed().wideningConversionTo(type);
    else if(canUnboxThis && !canUnboxType)
      return unboxed().wideningConversionTo(type);
    return false;
  }
  protected java.util.Map castingConversionTo_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:114
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean castingConversionTo(TypeDecl type) {
    Object _parameters = type;
    if(castingConversionTo_TypeDecl_values == null) castingConversionTo_TypeDecl_values = new java.util.HashMap(4);
    if(castingConversionTo_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)castingConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean castingConversionTo_TypeDecl_value = castingConversionTo_compute(type);
if(isFinal && num == state().boundariesCrossed) castingConversionTo_TypeDecl_values.put(_parameters, Boolean.valueOf(castingConversionTo_TypeDecl_value));
    return castingConversionTo_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean castingConversionTo_compute(TypeDecl type) {
    if(refined_TypeConversion_TypeDecl_castingConversionTo_TypeDecl(type))
      return true;
    boolean canBoxThis = this instanceof PrimitiveType;
    boolean canBoxType = type instanceof PrimitiveType;
    boolean canUnboxThis = !unboxed().isUnknown();
    boolean canUnboxType = !type.unboxed().isUnknown();
    if(canBoxThis && !canBoxType)
      return boxed().wideningConversionTo(type);
    else if(canUnboxThis && !canUnboxType)
      return unboxed().wideningConversionTo(type);
    return false;
    /*
    else if(boxingConversionTo(type))
      return true;
    else if(unboxingConversionTo(type))
      return true;
    return false;
    */
  }
  /**
   * @attribute syn
   * @aspect NumericPromotion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:146
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl unaryNumericPromotion() {
      ASTNode$State state = state();
    TypeDecl unaryNumericPromotion_value = unaryNumericPromotion_compute();
    return unaryNumericPromotion_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl unaryNumericPromotion_compute() {  return this;  }
  /**
   * @attribute syn
   * @aspect NumericPromotion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:154
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl binaryNumericPromotion(TypeDecl type) {
      ASTNode$State state = state();
    TypeDecl binaryNumericPromotion_TypeDecl_value = binaryNumericPromotion_compute(type);
    return binaryNumericPromotion_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl binaryNumericPromotion_compute(TypeDecl type) {  return unknownType();  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:165
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isReferenceType() {
      ASTNode$State state = state();
    boolean isReferenceType_value = isReferenceType_compute();
    return isReferenceType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isReferenceType_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:168
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isPrimitiveType() {
      ASTNode$State state = state();
    boolean isPrimitiveType_value = isPrimitiveType_compute();
    return isPrimitiveType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isPrimitiveType_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:173
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isNumericType() {
      ASTNode$State state = state();
    boolean isNumericType_value = isNumericType_compute();
    return isNumericType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isNumericType_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:177
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isIntegralType() {
      ASTNode$State state = state();
    boolean isIntegralType_value = isIntegralType_compute();
    return isIntegralType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isIntegralType_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:181
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isBoolean() {
      ASTNode$State state = state();
    boolean isBoolean_value = isBoolean_compute();
    return isBoolean_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isBoolean_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:185
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isByte() {
      ASTNode$State state = state();
    boolean isByte_value = isByte_compute();
    return isByte_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isByte_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:187
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isChar() {
      ASTNode$State state = state();
    boolean isChar_value = isChar_compute();
    return isChar_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isChar_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:189
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isShort() {
      ASTNode$State state = state();
    boolean isShort_value = isShort_compute();
    return isShort_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isShort_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:191
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isInt() {
      ASTNode$State state = state();
    boolean isInt_value = isInt_compute();
    return isInt_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isInt_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:195
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isFloat() {
      ASTNode$State state = state();
    boolean isFloat_value = isFloat_compute();
    return isFloat_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isFloat_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:197
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isLong() {
      ASTNode$State state = state();
    boolean isLong_value = isLong_compute();
    return isLong_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isLong_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:199
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDouble() {
      ASTNode$State state = state();
    boolean isDouble_value = isDouble_compute();
    return isDouble_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDouble_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:202
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isVoid() {
      ASTNode$State state = state();
    boolean isVoid_value = isVoid_compute();
    return isVoid_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isVoid_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:205
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isNull() {
      ASTNode$State state = state();
    boolean isNull_value = isNull_compute();
    return isNull_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isNull_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:209
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isClassDecl() {
      ASTNode$State state = state();
    boolean isClassDecl_value = isClassDecl_compute();
    return isClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isClassDecl_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:211
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isInterfaceDecl() {
      ASTNode$State state = state();
    boolean isInterfaceDecl_value = isInterfaceDecl_compute();
    return isInterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isInterfaceDecl_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:213
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isArrayDecl() {
      ASTNode$State state = state();
    boolean isArrayDecl_value = isArrayDecl_compute();
    return isArrayDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isArrayDecl_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:221
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isPrimitive() {
      ASTNode$State state = state();
    boolean isPrimitive_value = isPrimitive_compute();
    return isPrimitive_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isPrimitive_compute() {  return false;  }
  /**
   * @apilevel internal
   */
  protected boolean isString_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isString_value;
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:224
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isString() {
    if(isString_computed) {
      return isString_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isString_value = isString_compute();
if(isFinal && num == state().boundariesCrossed) isString_computed = true;
    return isString_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isString_compute() {  return false;  }
  /**
   * @apilevel internal
   */
  protected boolean isObject_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isObject_value;
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:227
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isObject() {
    if(isObject_computed) {
      return isObject_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isObject_value = isObject_compute();
if(isFinal && num == state().boundariesCrossed) isObject_computed = true;
    return isObject_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isObject_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:230
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isUnknown() {
      ASTNode$State state = state();
    boolean isUnknown_value = isUnknown_compute();
    return isUnknown_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isUnknown_compute() {  return false;  }
  protected java.util.Map instanceOf_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:386
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean instanceOf(TypeDecl type) {
    Object _parameters = type;
    if(instanceOf_TypeDecl_values == null) instanceOf_TypeDecl_values = new java.util.HashMap(4);
    if(instanceOf_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)instanceOf_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean instanceOf_TypeDecl_value = instanceOf_compute(type);
if(isFinal && num == state().boundariesCrossed) instanceOf_TypeDecl_values.put(_parameters, Boolean.valueOf(instanceOf_TypeDecl_value));
    return instanceOf_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean instanceOf_compute(TypeDecl type) { return subtype(type); }
  /**
   * @attribute syn
   * @aspect TypeWideningAndIdentity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:423
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSupertypeOfClassDecl(ClassDecl type) {
      ASTNode$State state = state();
    boolean isSupertypeOfClassDecl_ClassDecl_value = isSupertypeOfClassDecl_compute(type);
    return isSupertypeOfClassDecl_ClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSupertypeOfClassDecl_compute(ClassDecl type) {  return type == this;  }
  /**
   * @attribute syn
   * @aspect TypeWideningAndIdentity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:440
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSupertypeOfInterfaceDecl(InterfaceDecl type) {
      ASTNode$State state = state();
    boolean isSupertypeOfInterfaceDecl_InterfaceDecl_value = isSupertypeOfInterfaceDecl_compute(type);
    return isSupertypeOfInterfaceDecl_InterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSupertypeOfInterfaceDecl_compute(InterfaceDecl type) {  return type == this;  }
  /**
   * @attribute syn
   * @aspect TypeWideningAndIdentity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:453
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSupertypeOfArrayDecl(ArrayDecl type) {
      ASTNode$State state = state();
    boolean isSupertypeOfArrayDecl_ArrayDecl_value = isSupertypeOfArrayDecl_compute(type);
    return isSupertypeOfArrayDecl_ArrayDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSupertypeOfArrayDecl_compute(ArrayDecl type) {  return this == type;  }
  /**
   * @attribute syn
   * @aspect TypeWideningAndIdentity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:475
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSupertypeOfPrimitiveType(PrimitiveType type) {
      ASTNode$State state = state();
    boolean isSupertypeOfPrimitiveType_PrimitiveType_value = isSupertypeOfPrimitiveType_compute(type);
    return isSupertypeOfPrimitiveType_PrimitiveType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSupertypeOfPrimitiveType_compute(PrimitiveType type) {  return type == this;  }
  /**
   * @attribute syn
   * @aspect TypeWideningAndIdentity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:482
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSupertypeOfNullType(NullType type) {
      ASTNode$State state = state();
    boolean isSupertypeOfNullType_NullType_value = isSupertypeOfNullType_compute(type);
    return isSupertypeOfNullType_NullType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSupertypeOfNullType_compute(NullType type) {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeWideningAndIdentity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:486
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isSupertypeOfVoidType(VoidType type) {
      ASTNode$State state = state();
    boolean isSupertypeOfVoidType_VoidType_value = isSupertypeOfVoidType_compute(type);
    return isSupertypeOfVoidType_VoidType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isSupertypeOfVoidType_compute(VoidType type) {  return false;  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:498
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl topLevelType() {
      ASTNode$State state = state();
    TypeDecl topLevelType_value = topLevelType_compute();
    return topLevelType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl topLevelType_compute() {
    if(isTopLevelType())
      return this;
    return enclosingType().topLevelType();
  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:524
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isTopLevelType() {
      ASTNode$State state = state();
    boolean isTopLevelType_value = isTopLevelType_compute();
    return isTopLevelType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isTopLevelType_compute() {  return !isNestedType();  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:535
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isInnerClass() {
      ASTNode$State state = state();
    boolean isInnerClass_value = isInnerClass_compute();
    return isInnerClass_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isInnerClass_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:537
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isInnerType() {
      ASTNode$State state = state();
    boolean isInnerType_value = isInnerType_compute();
    return isInnerType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isInnerType_compute() {  return (isLocalClass() || isAnonymous() || (isMemberType() && !isStatic())) && !inStaticContext();  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:539
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isInnerTypeOf(TypeDecl typeDecl) {
      ASTNode$State state = state();
    boolean isInnerTypeOf_TypeDecl_value = isInnerTypeOf_compute(typeDecl);
    return isInnerTypeOf_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isInnerTypeOf_compute(TypeDecl typeDecl) {  return typeDecl == this || (isInnerType() && enclosingType().isInnerTypeOf(typeDecl));  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:546
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl withinBodyThatSubclasses(TypeDecl type) {
      ASTNode$State state = state();
    TypeDecl withinBodyThatSubclasses_TypeDecl_value = withinBodyThatSubclasses_compute(type);
    return withinBodyThatSubclasses_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl withinBodyThatSubclasses_compute(TypeDecl type) {
    if(instanceOf(type))
      return this;
    if(!isTopLevelType())
      return enclosingType().withinBodyThatSubclasses(type);
    return null;
  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:554
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean encloses(TypeDecl type) {
      ASTNode$State state = state();
    boolean encloses_TypeDecl_value = encloses_compute(type);
    return encloses_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean encloses_compute(TypeDecl type) {  return type.enclosedBy(this);  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:556
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean enclosedBy(TypeDecl type) {
      ASTNode$State state = state();
    boolean enclosedBy_TypeDecl_value = enclosedBy_compute(type);
    return enclosedBy_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean enclosedBy_compute(TypeDecl type) {
    if(this == type)
      return true;
    if(isTopLevelType())
      return false;
    return enclosingType().enclosedBy(type);
  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:570
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl hostType() {
      ASTNode$State state = state();
    TypeDecl hostType_value = hostType_compute();
    return hostType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl hostType_compute() {  return this;  }
  /**
   * @apilevel internal
   */
  protected int isCircular_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean isCircular_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isCircular_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean isCircular_value;
  /**
   * @attribute syn
   * @aspect Circularity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:673
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isCircular() {
    if(isCircular_computed) {
      return isCircular_value;
    }
    ASTNode$State state = state();
    if (!isCircular_initialized) {
      isCircular_initialized = true;
      isCircular_value = true;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        isCircular_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_isCircular_value = isCircular_compute();
        if (new_isCircular_value!=isCircular_value)
          state.CHANGE = true;
        isCircular_value = new_isCircular_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
      isCircular_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      isCircular_compute();
      state.RESET_CYCLE = false;
        isCircular_computed = false;
        isCircular_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return isCircular_value;
    }
    if(isCircular_visited != state.CIRCLE_INDEX) {
      isCircular_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        isCircular_computed = false;
        isCircular_initialized = false;
        isCircular_visited = -1;
        return isCircular_value;
      }
      boolean new_isCircular_value = isCircular_compute();
      if (new_isCircular_value!=isCircular_value)
        state.CHANGE = true;
      isCircular_value = new_isCircular_value; 
      return isCircular_value;
    }
    return isCircular_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isCircular_compute() {  return false;  }
  /* It is a compile-time error if the return type of a method declared in an
  annotation type is any type other than one of the following: one of the
  primitive types, String, Class and any invocation of Class, an enum type
  (\u00df8.9), an annotation type, or an array (\u00df10) of one of the preceding types.* @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:121
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isValidAnnotationMethodReturnType() {
      ASTNode$State state = state();
    boolean isValidAnnotationMethodReturnType_value = isValidAnnotationMethodReturnType_compute();
    return isValidAnnotationMethodReturnType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isValidAnnotationMethodReturnType_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:225
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Annotation annotation(TypeDecl typeDecl) {
      ASTNode$State state = state();
    Annotation annotation_TypeDecl_value = annotation_compute(typeDecl);
    return annotation_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private Annotation annotation_compute(TypeDecl typeDecl) {  return getModifiers().annotation(typeDecl);  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:282
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasAnnotationSuppressWarnings(String s) {
      ASTNode$State state = state();
    boolean hasAnnotationSuppressWarnings_String_value = hasAnnotationSuppressWarnings_compute(s);
    return hasAnnotationSuppressWarnings_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasAnnotationSuppressWarnings_compute(String s) {  return getModifiers().hasAnnotationSuppressWarnings(s);  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:321
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDeprecated() {
      ASTNode$State state = state();
    boolean isDeprecated_value = isDeprecated_compute();
    return isDeprecated_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDeprecated_compute() {  return getModifiers().hasDeprecatedAnnotation();  }
  /* An element type T is commensurate with an element value V if and only if one of the following conditions is true:
    * T is an array type E[] and either:
          o V is an ElementValueArrayInitializer and each ElementValueInitializer (analogous to a variable initializer in an array initializer) in V is commensurate with E. Or
          o V is an ElementValue that is commensurate with T. 
    * The type of V is assignment compatible (\u00df5.2) with T and, furthermore:
          o If T is a primitive type or String, V is a constant expression (\u00df15.28).
          o V is not null.
          o if T is Class, or an invocation of Class, and V is a class literal (\u00df15.8.2).
          o If T is an enum type, and V is an enum constant. * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:472
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean commensurateWith(ElementValue value) {
      ASTNode$State state = state();
    boolean commensurateWith_ElementValue_value = commensurateWith_compute(value);
    return commensurateWith_ElementValue_value;
  }
  /**
   * @apilevel internal
   */
  private boolean commensurateWith_compute(ElementValue value) {  return value.commensurateWithTypeDecl(this);  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:541
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isAnnotationDecl() {
      ASTNode$State state = state();
    boolean isAnnotationDecl_value = isAnnotationDecl_compute();
    return isAnnotationDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isAnnotationDecl_compute() {  return false;  }
  /* NumericTypes, BooleanTypes
     TypeChecking (ensure that an expression of a certain type is valid in a particular context)
     TypeComputation (compute the type of an expression)
     CodeGeneration (output code including implicit type conversions and promotions)

     NumericTypes:
       binaryNumericPromotion, unaryNumericPromotion, assignmentConversion, methodInvocationConversion, castingConversion
       numeric operations that do not use these kinds of conversions and promotions explicitly need to be refined
     BooleanTypes:
       assignmentConversion, methodInvocationConversion, castingConversion
       
  * @attribute syn
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:31
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean boxingConversionTo(TypeDecl typeDecl) {
      ASTNode$State state = state();
    boolean boxingConversionTo_TypeDecl_value = boxingConversionTo_compute(typeDecl);
    return boxingConversionTo_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean boxingConversionTo_compute(TypeDecl typeDecl) {  return false;  }
  /**
   * @apilevel internal
   */
  protected boolean boxed_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl boxed_value;
  /**
   * @attribute syn
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:35
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl boxed() {
    if(boxed_computed) {
      return boxed_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boxed_value = boxed_compute();
if(isFinal && num == state().boundariesCrossed) boxed_computed = true;
    return boxed_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl boxed_compute() {  return unknownType();  }
  /**
   * @attribute syn
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:47
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean unboxingConversionTo(TypeDecl typeDecl) {
      ASTNode$State state = state();
    boolean unboxingConversionTo_TypeDecl_value = unboxingConversionTo_compute(typeDecl);
    return unboxingConversionTo_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean unboxingConversionTo_compute(TypeDecl typeDecl) {  return false;  }
  /**
   * @apilevel internal
   */
  protected boolean unboxed_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl unboxed_value;
  /**
   * @attribute syn
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:51
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl unboxed() {
    if(unboxed_computed) {
      return unboxed_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    unboxed_value = unboxed_compute();
if(isFinal && num == state().boundariesCrossed) unboxed_computed = true;
    return unboxed_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl unboxed_compute() {  return unknownType();  }
  /**
   * @apilevel internal
   */
  protected boolean isIterable_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isIterable_value;
  /**
	 * True if type is java.lang.Iterable or subtype
	   As long as we use the 1.4 API we check for java.util.Collection instead.
	 * @attribute syn
   * @aspect EnhancedFor
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/EnhancedFor.jrag:35
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isIterable() {
    if(isIterable_computed) {
      return isIterable_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isIterable_value = isIterable_compute();
if(isFinal && num == state().boundariesCrossed) isIterable_computed = true;
    return isIterable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isIterable_compute() {  return instanceOf(lookupType("java.lang", "Iterable"));  }
  /* 
     1) It is a compile-time error to attempt to explicitly instantiate an enum type
     (\u00d4\u00f8\u03a915.9.1).
  * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:16
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isEnumDecl() {
      ASTNode$State state = state();
    boolean isEnumDecl_value = isEnumDecl_compute();
    return isEnumDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isEnumDecl_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect GenericMethodsInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:13
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isUnboxedPrimitive() {
      ASTNode$State state = state();
    boolean isUnboxedPrimitive_value = isUnboxedPrimitive_compute();
    return isUnboxedPrimitive_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isUnboxedPrimitive_compute() {  return this instanceof PrimitiveType && isPrimitive();  }
  /**
   * @apilevel internal
   */
  protected int involvesTypeParameters_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean involvesTypeParameters_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean involvesTypeParameters_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean involvesTypeParameters_value;
  /**
   * @attribute syn
   * @aspect GenericMethodsInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean involvesTypeParameters() {
    if(involvesTypeParameters_computed) {
      return involvesTypeParameters_value;
    }
    ASTNode$State state = state();
    if (!involvesTypeParameters_initialized) {
      involvesTypeParameters_initialized = true;
      involvesTypeParameters_value = false;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        involvesTypeParameters_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
        if (new_involvesTypeParameters_value!=involvesTypeParameters_value)
          state.CHANGE = true;
        involvesTypeParameters_value = new_involvesTypeParameters_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
      involvesTypeParameters_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      involvesTypeParameters_compute();
      state.RESET_CYCLE = false;
        involvesTypeParameters_computed = false;
        involvesTypeParameters_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return involvesTypeParameters_value;
    }
    if(involvesTypeParameters_visited != state.CIRCLE_INDEX) {
      involvesTypeParameters_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        involvesTypeParameters_computed = false;
        involvesTypeParameters_initialized = false;
        involvesTypeParameters_visited = -1;
        return involvesTypeParameters_value;
      }
      boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
      if (new_involvesTypeParameters_value!=involvesTypeParameters_value)
        state.CHANGE = true;
      involvesTypeParameters_value = new_involvesTypeParameters_value; 
      return involvesTypeParameters_value;
    }
    return involvesTypeParameters_value;
  }
  /**
   * @apilevel internal
   */
  private boolean involvesTypeParameters_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:157
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isGenericType() {
      ASTNode$State state = state();
    boolean isGenericType_value = isGenericType_compute();
    return isGenericType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isGenericType_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:231
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isParameterizedType() {
      ASTNode$State state = state();
    boolean isParameterizedType_value = isParameterizedType_compute();
    return isParameterizedType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isParameterizedType_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:234
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isRawType() {
      ASTNode$State state = state();
    boolean isRawType_value = isRawType_compute();
    return isRawType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isRawType_compute() {  return isNestedType() && enclosingType().isRawType();  }
  /**
   * @apilevel internal
   */
  protected boolean erasure_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl erasure_value;
  /**
   * @attribute syn
   * @aspect GenericsErasure
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:314
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl erasure() {
    if(erasure_computed) {
      return erasure_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    erasure_value = erasure_compute();
if(isFinal && num == state().boundariesCrossed) erasure_computed = true;
    return erasure_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl erasure_compute() {
    if(isAnonymous() || isLocalClass())
      return this;
    if(!isNestedType())
      return this;
    return extractSingleType(enclosingType().erasure().memberTypes(name()));
  }
  /**
   * @apilevel internal
   */
  protected boolean implementedInterfaces_computed = false;
  /**
   * @apilevel internal
   */
  protected HashSet implementedInterfaces_value;
  /**
   * @attribute syn
   * @aspect GenericsTypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:370
   */
  @SuppressWarnings({"unchecked", "cast"})
  public HashSet implementedInterfaces() {
    if(implementedInterfaces_computed) {
      return implementedInterfaces_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    implementedInterfaces_value = implementedInterfaces_compute();
if(isFinal && num == state().boundariesCrossed) implementedInterfaces_computed = true;
    return implementedInterfaces_value;
  }
  /**
   * @apilevel internal
   */
  private HashSet implementedInterfaces_compute() {  return new HashSet();  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:542
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean sameSignature(Access a) {
      ASTNode$State state = state();
    boolean sameSignature_Access_value = sameSignature_compute(a);
    return sameSignature_Access_value;
  }
  /**
   * @apilevel internal
   */
  private boolean sameSignature_compute(Access a) {
    if(a instanceof ParTypeAccess) return false;
    if(a instanceof AbstractWildcard) return false;
    return this == a.type();
  }
  /**
   * @apilevel internal
   */
  protected int usesTypeVariable_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean usesTypeVariable_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean usesTypeVariable_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean usesTypeVariable_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:913
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean usesTypeVariable() {
    if(usesTypeVariable_computed) {
      return usesTypeVariable_value;
    }
    ASTNode$State state = state();
    if (!usesTypeVariable_initialized) {
      usesTypeVariable_initialized = true;
      usesTypeVariable_value = false;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        usesTypeVariable_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_usesTypeVariable_value = usesTypeVariable_compute();
        if (new_usesTypeVariable_value!=usesTypeVariable_value)
          state.CHANGE = true;
        usesTypeVariable_value = new_usesTypeVariable_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
      usesTypeVariable_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      usesTypeVariable_compute();
      state.RESET_CYCLE = false;
        usesTypeVariable_computed = false;
        usesTypeVariable_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return usesTypeVariable_value;
    }
    if(usesTypeVariable_visited != state.CIRCLE_INDEX) {
      usesTypeVariable_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        usesTypeVariable_computed = false;
        usesTypeVariable_initialized = false;
        usesTypeVariable_visited = -1;
        return usesTypeVariable_value;
      }
      boolean new_usesTypeVariable_value = usesTypeVariable_compute();
      if (new_usesTypeVariable_value!=usesTypeVariable_value)
        state.CHANGE = true;
      usesTypeVariable_value = new_usesTypeVariable_value; 
      return usesTypeVariable_value;
    }
    return usesTypeVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean usesTypeVariable_compute() {  return isNestedType() && enclosingType().usesTypeVariable();  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1069
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl original() {
      ASTNode$State state = state();
    TypeDecl original_value = original_compute();
    return original_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl original_compute() {  return this;  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1161
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl asWildcardExtends() {
      ASTNode$State state = state();
    TypeDecl asWildcardExtends_value = asWildcardExtends_compute();
    return asWildcardExtends_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl asWildcardExtends_compute() {  return lookupWildcardExtends(this);  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1174
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl asWildcardSuper() {
      ASTNode$State state = state();
    TypeDecl asWildcardSuper_value = asWildcardSuper_compute();
    return asWildcardSuper_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl asWildcardSuper_compute() {  return lookupWildcardSuper(this);  }
  /**
   * @apilevel internal
   */
  protected boolean sourceTypeDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl sourceTypeDecl_value;
  /**
   * @attribute syn
   * @aspect SourceDeclarations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1267
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl sourceTypeDecl() {
    if(sourceTypeDecl_computed) {
      return sourceTypeDecl_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sourceTypeDecl_value = sourceTypeDecl_compute();
if(isFinal && num == state().boundariesCrossed) sourceTypeDecl_computed = true;
    return sourceTypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl sourceTypeDecl_compute() {  return this;  }
  /**
   * @attribute syn
   * @aspect GenericsParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:70
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isTypeVariable() {
      ASTNode$State state = state();
    boolean isTypeVariable_value = isTypeVariable_compute();
    return isTypeVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isTypeVariable_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:14
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeGenericClassDecl(GenericClassDecl type) {
      ASTNode$State state = state();
    boolean supertypeGenericClassDecl_GenericClassDecl_value = supertypeGenericClassDecl_compute(type);
    return supertypeGenericClassDecl_GenericClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeGenericClassDecl_compute(GenericClassDecl type) {  return supertypeClassDecl(type);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:20
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeGenericInterfaceDecl(GenericInterfaceDecl type) {
      ASTNode$State state = state();
    boolean supertypeGenericInterfaceDecl_GenericInterfaceDecl_value = supertypeGenericInterfaceDecl_compute(type);
    return supertypeGenericInterfaceDecl_GenericInterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeGenericInterfaceDecl_compute(GenericInterfaceDecl type) {  return this == type || supertypeInterfaceDecl(type);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:26
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeRawClassDecl(RawClassDecl type) {
      ASTNode$State state = state();
    boolean supertypeRawClassDecl_RawClassDecl_value = supertypeRawClassDecl_compute(type);
    return supertypeRawClassDecl_RawClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeRawClassDecl_compute(RawClassDecl type) {  return supertypeParClassDecl(type);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:30
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeRawInterfaceDecl(RawInterfaceDecl type) {
      ASTNode$State state = state();
    boolean supertypeRawInterfaceDecl_RawInterfaceDecl_value = supertypeRawInterfaceDecl_compute(type);
    return supertypeRawInterfaceDecl_RawInterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeRawInterfaceDecl_compute(RawInterfaceDecl type) {  return supertypeParInterfaceDecl(type);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:46
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeWildcard(WildcardType type) {
      ASTNode$State state = state();
    boolean supertypeWildcard_WildcardType_value = supertypeWildcard_compute(type);
    return supertypeWildcard_WildcardType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeWildcard_compute(WildcardType type) {  return false;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:57
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeWildcardExtends(WildcardExtendsType type) {
      ASTNode$State state = state();
    boolean supertypeWildcardExtends_WildcardExtendsType_value = supertypeWildcardExtends_compute(type);
    return supertypeWildcardExtends_WildcardExtendsType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeWildcardExtends_compute(WildcardExtendsType type) {  return false;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:66
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeWildcardSuper(WildcardSuperType type) {
      ASTNode$State state = state();
    boolean supertypeWildcardSuper_WildcardSuperType_value = supertypeWildcardSuper_compute(type);
    return supertypeWildcardSuper_WildcardSuperType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeWildcardSuper_compute(WildcardSuperType type) {  return false;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:102
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isWildcard() {
      ASTNode$State state = state();
    boolean isWildcard_value = isWildcard_compute();
    return isWildcard_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isWildcard_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:125
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeParClassDecl(ParClassDecl type) {
      ASTNode$State state = state();
    boolean supertypeParClassDecl_ParClassDecl_value = supertypeParClassDecl_compute(type);
    return supertypeParClassDecl_ParClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeParClassDecl_compute(ParClassDecl type) {  return supertypeClassDecl(type);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:129
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeParInterfaceDecl(ParInterfaceDecl type) {
      ASTNode$State state = state();
    boolean supertypeParInterfaceDecl_ParInterfaceDecl_value = supertypeParInterfaceDecl_compute(type);
    return supertypeParInterfaceDecl_ParInterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeParInterfaceDecl_compute(ParInterfaceDecl type) {  return supertypeInterfaceDecl(type);  }
  protected java.util.Map containedIn_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:141
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean containedIn(TypeDecl type) {
    Object _parameters = type;
    if(containedIn_TypeDecl_values == null) containedIn_TypeDecl_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(containedIn_TypeDecl_values.containsKey(_parameters)) {
      Object _o = containedIn_TypeDecl_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return ((Boolean)_o).booleanValue();
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      containedIn_TypeDecl_values.put(_parameters, _value);
      _value.value = Boolean.valueOf(true);
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      boolean new_containedIn_TypeDecl_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_containedIn_TypeDecl_value = containedIn_compute(type);
        if (new_containedIn_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
          state.CHANGE = true;
          _value.value = Boolean.valueOf(new_containedIn_TypeDecl_value);
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
        containedIn_TypeDecl_values.put(_parameters, new_containedIn_TypeDecl_value);
      }
      else {
        containedIn_TypeDecl_values.remove(_parameters);
      state.RESET_CYCLE = true;
      containedIn_compute(type);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_containedIn_TypeDecl_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      boolean new_containedIn_TypeDecl_value = containedIn_compute(type);
      if (state.RESET_CYCLE) {
        containedIn_TypeDecl_values.remove(_parameters);
      }
      else if (new_containedIn_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
        state.CHANGE = true;
        _value.value = new_containedIn_TypeDecl_value;
      }
      return new_containedIn_TypeDecl_value;
    }
    return ((Boolean)_value.value).booleanValue();
  }
  /**
   * @apilevel internal
   */
  private boolean containedIn_compute(TypeDecl type) {
    if(type == this || type instanceof WildcardType) 
      return true;
    else if(type instanceof WildcardExtendsType)
      return this.subtype(((WildcardExtendsType)type).extendsType());
    else if(type instanceof WildcardSuperType)
      return ((WildcardSuperType)type).superType().subtype(this);
    else if(type instanceof TypeVariable)
      return subtype(type);
    return sameStructure(type);
    //return false;
  }
  protected java.util.Map sameStructure_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:178
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean sameStructure(TypeDecl t) {
    Object _parameters = t;
    if(sameStructure_TypeDecl_values == null) sameStructure_TypeDecl_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(sameStructure_TypeDecl_values.containsKey(_parameters)) {
      Object _o = sameStructure_TypeDecl_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return ((Boolean)_o).booleanValue();
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      sameStructure_TypeDecl_values.put(_parameters, _value);
      _value.value = Boolean.valueOf(true);
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      boolean new_sameStructure_TypeDecl_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_sameStructure_TypeDecl_value = sameStructure_compute(t);
        if (new_sameStructure_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
          state.CHANGE = true;
          _value.value = Boolean.valueOf(new_sameStructure_TypeDecl_value);
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
        sameStructure_TypeDecl_values.put(_parameters, new_sameStructure_TypeDecl_value);
      }
      else {
        sameStructure_TypeDecl_values.remove(_parameters);
      state.RESET_CYCLE = true;
      sameStructure_compute(t);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_sameStructure_TypeDecl_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      boolean new_sameStructure_TypeDecl_value = sameStructure_compute(t);
      if (state.RESET_CYCLE) {
        sameStructure_TypeDecl_values.remove(_parameters);
      }
      else if (new_sameStructure_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
        state.CHANGE = true;
        _value.value = new_sameStructure_TypeDecl_value;
      }
      return new_sameStructure_TypeDecl_value;
    }
    return ((Boolean)_value.value).booleanValue();
  }
  /**
   * @apilevel internal
   */
  private boolean sameStructure_compute(TypeDecl t) {  return t == this;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:291
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeTypeVariable(TypeVariable type) {
      ASTNode$State state = state();
    boolean supertypeTypeVariable_TypeVariable_value = supertypeTypeVariable_compute(type);
    return supertypeTypeVariable_TypeVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeTypeVariable_compute(TypeVariable type) {
    if(type == this)
      return true;
    for(int i = 0; i < type.getNumTypeBound(); i++)
      if(type.getTypeBound(i).type().subtype(this))
        return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:347
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeLUBType(LUBType type) {
      ASTNode$State state = state();
    boolean supertypeLUBType_LUBType_value = supertypeLUBType_compute(type);
    return supertypeLUBType_LUBType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeLUBType_compute(LUBType type) {
    for(int i = 0; i < type.getNumTypeBound(); i++)
      if(!type.getTypeBound(i).type().subtype(this))
        return false;
    return true;
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:366
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeGLBType(GLBType type) {
      ASTNode$State state = state();
    boolean supertypeGLBType_GLBType_value = supertypeGLBType_compute(type);
    return supertypeGLBType_GLBType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeGLBType_compute(GLBType type) {
    // T1 && .. && Tn <: this, if exists  0 < i <= n Ti <: this 
    for(int i = 0; i < type.getNumTypeBound(); i++)
      if(type.getTypeBound(i).type().subtype(this))
        return true;
    return false;
  }
  protected java.util.Map subtype_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:405
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean subtype(TypeDecl type) {
    Object _parameters = type;
    if(subtype_TypeDecl_values == null) subtype_TypeDecl_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(subtype_TypeDecl_values.containsKey(_parameters)) {
      Object _o = subtype_TypeDecl_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return ((Boolean)_o).booleanValue();
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      subtype_TypeDecl_values.put(_parameters, _value);
      _value.value = Boolean.valueOf(true);
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      boolean new_subtype_TypeDecl_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_subtype_TypeDecl_value = subtype_compute(type);
        if (new_subtype_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
          state.CHANGE = true;
          _value.value = Boolean.valueOf(new_subtype_TypeDecl_value);
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
        subtype_TypeDecl_values.put(_parameters, new_subtype_TypeDecl_value);
      }
      else {
        subtype_TypeDecl_values.remove(_parameters);
      state.RESET_CYCLE = true;
      subtype_compute(type);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_subtype_TypeDecl_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      boolean new_subtype_TypeDecl_value = subtype_compute(type);
      if (state.RESET_CYCLE) {
        subtype_TypeDecl_values.remove(_parameters);
      }
      else if (new_subtype_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
        state.CHANGE = true;
        _value.value = new_subtype_TypeDecl_value;
      }
      return new_subtype_TypeDecl_value;
    }
    return ((Boolean)_value.value).booleanValue();
  }
  /**
   * @apilevel internal
   */
  private boolean subtype_compute(TypeDecl type) {  return type == this;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:421
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeClassDecl(ClassDecl type) {
      ASTNode$State state = state();
    boolean supertypeClassDecl_ClassDecl_value = supertypeClassDecl_compute(type);
    return supertypeClassDecl_ClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeClassDecl_compute(ClassDecl type) {  return type == this;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:437
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeInterfaceDecl(InterfaceDecl type) {
      ASTNode$State state = state();
    boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
    return supertypeInterfaceDecl_InterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {  return type == this;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:450
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeArrayDecl(ArrayDecl type) {
      ASTNode$State state = state();
    boolean supertypeArrayDecl_ArrayDecl_value = supertypeArrayDecl_compute(type);
    return supertypeArrayDecl_ArrayDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeArrayDecl_compute(ArrayDecl type) {  return this == type;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:472
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypePrimitiveType(PrimitiveType type) {
      ASTNode$State state = state();
    boolean supertypePrimitiveType_PrimitiveType_value = supertypePrimitiveType_compute(type);
    return supertypePrimitiveType_PrimitiveType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypePrimitiveType_compute(PrimitiveType type) {  return type == this;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:479
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeNullType(NullType type) {
      ASTNode$State state = state();
    boolean supertypeNullType_NullType_value = supertypeNullType_compute(type);
    return supertypeNullType_NullType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeNullType_compute(NullType type) {  return false;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:483
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeVoidType(VoidType type) {
      ASTNode$State state = state();
    boolean supertypeVoidType_VoidType_value = supertypeVoidType_compute(type);
    return supertypeVoidType_VoidType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeVoidType_compute(VoidType type) {  return false;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:493
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeClassDeclSubstituted(ClassDeclSubstituted type) {
      ASTNode$State state = state();
    boolean supertypeClassDeclSubstituted_ClassDeclSubstituted_value = supertypeClassDeclSubstituted_compute(type);
    return supertypeClassDeclSubstituted_ClassDeclSubstituted_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeClassDeclSubstituted_compute(ClassDeclSubstituted type) {  return type.original() == this || supertypeClassDecl(type);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:503
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeInterfaceDeclSubstituted(InterfaceDeclSubstituted type) {
      ASTNode$State state = state();
    boolean supertypeInterfaceDeclSubstituted_InterfaceDeclSubstituted_value = supertypeInterfaceDeclSubstituted_compute(type);
    return supertypeInterfaceDeclSubstituted_InterfaceDeclSubstituted_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeInterfaceDeclSubstituted_compute(InterfaceDeclSubstituted type) {  return type.original() == this || supertypeInterfaceDecl(type);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:513
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeGenericClassDeclSubstituted(GenericClassDeclSubstituted type) {
      ASTNode$State state = state();
    boolean supertypeGenericClassDeclSubstituted_GenericClassDeclSubstituted_value = supertypeGenericClassDeclSubstituted_compute(type);
    return supertypeGenericClassDeclSubstituted_GenericClassDeclSubstituted_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeGenericClassDeclSubstituted_compute(GenericClassDeclSubstituted type) {  return type.original() == this || supertypeGenericClassDecl(type);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:523
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeGenericInterfaceDeclSubstituted(GenericInterfaceDeclSubstituted type) {
      ASTNode$State state = state();
    boolean supertypeGenericInterfaceDeclSubstituted_GenericInterfaceDeclSubstituted_value = supertypeGenericInterfaceDeclSubstituted_compute(type);
    return supertypeGenericInterfaceDeclSubstituted_GenericInterfaceDeclSubstituted_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeGenericInterfaceDeclSubstituted_compute(GenericInterfaceDeclSubstituted type) {  return type.original() == this || supertypeGenericInterfaceDecl(type);  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:79
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl stringPromotion() {
      ASTNode$State state = state();
    TypeDecl stringPromotion_value = stringPromotion_compute();
    return stringPromotion_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl stringPromotion_compute() {  return this;  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:91
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl methodWithArgs(String name, TypeDecl[] args) {
      ASTNode$State state = state();
    MethodDecl methodWithArgs_String_TypeDecl_a_value = methodWithArgs_compute(name, args);
    return methodWithArgs_String_TypeDecl_a_value;
  }
  /**
   * @apilevel internal
   */
  private MethodDecl methodWithArgs_compute(String name, TypeDecl[] args) {
    for(Iterator iter = memberMethods(name).iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(m.getNumParameter() == args.length) {
        for(int i = 0; i < args.length; i++)
          if(m.getParameter(i).type() == args[i])
            return m;
      }
    }
    return null;
  }
  /**
   * @apilevel internal
   */
  protected boolean enclosingVariables_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection enclosingVariables_value;
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:142
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection enclosingVariables() {
    if(enclosingVariables_computed) {
      return enclosingVariables_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    enclosingVariables_value = enclosingVariables_compute();
if(isFinal && num == state().boundariesCrossed) enclosingVariables_computed = true;
    return enclosingVariables_value;
  }
  /**
   * @apilevel internal
   */
  private Collection enclosingVariables_compute() {
    HashSet set = new HashSet();
    for(TypeDecl e = this; e != null; e = e.enclosingType())
      if(e.isLocalClass() || e.isAnonymous())
        collectEnclosingVariables(set, e.enclosingType());
    if(isClassDecl()) {
      ClassDecl classDecl = (ClassDecl)this;
      if(classDecl.isNestedType() && classDecl.hasSuperclass())
        set.addAll(classDecl.superclass().enclosingVariables());
    }
    return set;
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:382
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isAnonymousInNonStaticContext() {
      ASTNode$State state = state();
    boolean isAnonymousInNonStaticContext_value = isAnonymousInNonStaticContext_compute();
    return isAnonymousInNonStaticContext_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isAnonymousInNonStaticContext_compute() {
    return isAnonymous() && 
           !((ClassInstanceExpr)getParent().getParent()).unqualifiedScope().inStaticContext()
           && (!inExplicitConstructorInvocation() || enclosingBodyDecl().hostType().isInnerType());
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:388
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean needsEnclosing() {
      ASTNode$State state = state();
    boolean needsEnclosing_value = needsEnclosing_compute();
    return needsEnclosing_value;
  }
  /**
   * @apilevel internal
   */
  private boolean needsEnclosing_compute() {
    if(isAnonymous())
      return isAnonymousInNonStaticContext();
    else if(isLocalClass())
      return !inStaticContext();
    else if(isInnerType())
      return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:398
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean needsSuperEnclosing() {
      ASTNode$State state = state();
    boolean needsSuperEnclosing_value = needsSuperEnclosing_compute();
    return needsSuperEnclosing_value;
  }
  /**
   * @apilevel internal
   */
  private boolean needsSuperEnclosing_compute() {
    if(!isAnonymous())
      return false;
    TypeDecl superClass = ((ClassDecl)this).superclass();
    if(superClass.isLocalClass())
      return !superClass.inStaticContext();
    else if(superClass.isInnerType())
      return true;
    if(needsEnclosing() && enclosing() == superEnclosing())
      return false;
    return false;
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:410
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl enclosing() {
      ASTNode$State state = state();
    TypeDecl enclosing_value = enclosing_compute();
    return enclosing_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl enclosing_compute() {
    if(!needsEnclosing())
      return null;
    TypeDecl typeDecl = enclosingType();
    if(isAnonymous() && inExplicitConstructorInvocation())
      typeDecl = typeDecl.enclosingType();
    return typeDecl;
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:418
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl superEnclosing() {
      ASTNode$State state = state();
    TypeDecl superEnclosing_value = superEnclosing_compute();
    return superEnclosing_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl superEnclosing_compute() {  return null;  }
  /**
   * @apilevel internal
   */
  protected boolean uniqueIndex_computed = false;
  /**
   * @apilevel internal
   */
  protected int uniqueIndex_value;
  /**
   * @attribute syn
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:12
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int uniqueIndex() {
    if(uniqueIndex_computed) {
      return uniqueIndex_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    uniqueIndex_value = uniqueIndex_compute();
if(isFinal && num == state().boundariesCrossed) uniqueIndex_computed = true;
    return uniqueIndex_value;
  }
  /**
   * @apilevel internal
   */
  private int uniqueIndex_compute() {  return topLevelType().uniqueIndexCounter++;  }
  /**
   * @apilevel internal
   */
  protected boolean jvmName_computed = false;
  /**
   * @apilevel internal
   */
  protected String jvmName_value;
  /**
   * @attribute syn
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String jvmName() {
    if(jvmName_computed) {
      return jvmName_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    jvmName_value = jvmName_compute();
if(isFinal && num == state().boundariesCrossed) jvmName_computed = true;
    return jvmName_value;
  }
  /**
   * @apilevel internal
   */
  private String jvmName_compute() {
    throw new Error("Jvm name only supported for reference types and not " + getClass().getName());
  }
  /**
   * @attribute syn
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String primitiveClassName() {
      ASTNode$State state = state();
    String primitiveClassName_value = primitiveClassName_compute();
    return primitiveClassName_value;
  }
  /**
   * @apilevel internal
   */
  private String primitiveClassName_compute() {
    throw new Error("primitiveClassName not supported for " + name() + " of type " + getClass().getName());
  }
  /**
   * @attribute syn
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:57
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String referenceClassFieldName() {
      ASTNode$State state = state();
    String referenceClassFieldName_value = referenceClassFieldName_compute();
    return referenceClassFieldName_value;
  }
  /**
   * @apilevel internal
   */
  private String referenceClassFieldName_compute() {
    throw new Error("referenceClassFieldName not supported for " + name() + " of type " + getClass().getName());
  }
  /**
   * @apilevel internal
   */
  protected boolean getSootClassDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected SootClass getSootClassDecl_value;
  /**
   * @attribute syn
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SootClass getSootClassDecl() {
    if(getSootClassDecl_computed) {
      return getSootClassDecl_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getSootClassDecl_value = getSootClassDecl_compute();
if(isFinal && num == state().boundariesCrossed) getSootClassDecl_computed = true;
    return getSootClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private SootClass getSootClassDecl_compute() {
    if(erasure() != this)
      return erasure().getSootClassDecl();
    if(compilationUnit().fromSource()) {
      return sootClass();
    }
    else {
      if(options().verbose())
        System.out.println("Loading .class file " + jvmName());
      return SootResolver.v().makeClassRef(jvmName());
      /*

      RefType type = (RefType) Scene.v().getRefType(jvmName());
      SootClass toReturn = null;
      if( type != null ) toReturn = type.getSootClass();
      if(toReturn != null) {
        return toReturn;
      } 
      SootClass c = new SootClass(jvmName());
      c.setPhantom(true);
      Scene.v().addClass(c);
      return c;
      */


      //  return Scene.v().getSootClass(jvmName());
      /*
         SootClass sc = Scene.v().loadClass(jvmName(), SootClass.SIGNATURES);
         sc.setLibraryClass();
         return sc;
       */
    }

  }
  /**
   * @apilevel internal
   */
  protected boolean getSootType_computed = false;
  /**
   * @apilevel internal
   */
  protected Type getSootType_value;
  /**
   * @attribute syn
   * @aspect EmitJimpleRefinements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/EmitJimpleRefinements.jrag:20
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Type getSootType() {
    if(getSootType_computed) {
      return getSootType_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getSootType_value = getSootType_compute();
if(isFinal && num == state().boundariesCrossed) getSootType_computed = true;
    return getSootType_value;
  }
  /**
   * @apilevel internal
   */
  private Type getSootType_compute() {
    return RefType.v(erasure().jvmName());
  }
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:58
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.RefType sootRef() {
      ASTNode$State state = state();
    soot.RefType sootRef_value = sootRef_compute();
    return sootRef_value;
  }
  /**
   * @apilevel internal
   */
  private soot.RefType sootRef_compute() {  return (soot.RefType)getSootType();  }
  /**
   * @apilevel internal
   */
  protected boolean sootClass_computed = false;
  /**
   * @apilevel internal
   */
  protected SootClass sootClass_value;
  /**
   * @attribute syn
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:413
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SootClass sootClass() {
    if(sootClass_computed) {
      return sootClass_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sootClass_value = sootClass_compute();
if(isFinal && num == state().boundariesCrossed) sootClass_computed = true;
    return sootClass_value;
  }
  /**
   * @apilevel internal
   */
  private SootClass sootClass_compute() {
    return erasure() != this ?
    erasure().sootClass() : refined_EmitJimple_TypeDecl_sootClass();
  }
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:85
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String sourceNameWithoutPath() {
      ASTNode$State state = state();
    String sourceNameWithoutPath_value = sourceNameWithoutPath_compute();
    return sourceNameWithoutPath_value;
  }
  /**
   * @apilevel internal
   */
  private String sourceNameWithoutPath_compute() {
    String s = sourceFile();
    return s != null ? s.substring(s.lastIndexOf(java.io.File.separatorChar)+1) : "Unknown";
  }
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:90
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int sootTypeModifiers() {
      ASTNode$State state = state();
    int sootTypeModifiers_value = sootTypeModifiers_compute();
    return sootTypeModifiers_value;
  }
  /**
   * @apilevel internal
   */
  private int sootTypeModifiers_compute() {
    int result = 0;
    if(isNestedType()) {
      result |= soot.Modifier.PUBLIC;
    }
    else {
      if(isPublic()) result |= soot.Modifier.PUBLIC;
      if(isProtected()) result |= soot.Modifier.PROTECTED;
      if(isPrivate()) result |= soot.Modifier.PRIVATE;
    }
    if(isFinal()) result |= soot.Modifier.FINAL;
    if(isStatic()) result |= soot.Modifier.STATIC;
    if(isAbstract()) result |= soot.Modifier.ABSTRACT;
    return result;
  }
  /**
   * @apilevel internal
   */
  protected boolean needsClinit_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean needsClinit_value;
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:887
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean needsClinit() {
    if(needsClinit_computed) {
      return needsClinit_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    needsClinit_value = needsClinit_compute();
if(isFinal && num == state().boundariesCrossed) needsClinit_computed = true;
    return needsClinit_value;
  }
  /**
   * @apilevel internal
   */
  private boolean needsClinit_compute() {
    for(int i = 0; i < getNumBodyDecl(); i++) {
      BodyDecl b = getBodyDecl(i);
      if(b instanceof FieldDeclaration) {
        FieldDeclaration f = (FieldDeclaration)b;
        if(f.isStatic() && f.hasInit() && f.generate()) {
          return true;
        }
      }
      else if(b instanceof StaticInitializer && b.generate()) {
        return true;
      }
    }
    return false;
  }
  /**
   * @apilevel internal
   */
  protected boolean innerClassesAttributeEntries_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection innerClassesAttributeEntries_value;
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:962
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection innerClassesAttributeEntries() {
    if(innerClassesAttributeEntries_computed) {
      return innerClassesAttributeEntries_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    innerClassesAttributeEntries_value = innerClassesAttributeEntries_compute();
if(isFinal && num == state().boundariesCrossed) innerClassesAttributeEntries_computed = true;
    return innerClassesAttributeEntries_value;
  }
  /**
   * @apilevel internal
   */
  private Collection innerClassesAttributeEntries_compute() {
    HashSet list = new HashSet();
    if(isNestedType())
      list.add(this);
    for(Iterator iter = nestedTypes().iterator(); iter.hasNext(); )
      list.add(iter.next());
    for(Iterator iter = usedNestedTypes().iterator(); iter.hasNext(); )
      list.add(iter.next());
    return list;
  }
  protected java.util.Map getSootField_String_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:996
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SootField getSootField(String name, TypeDecl type) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(name);
    _parameters.add(type);
    if(getSootField_String_TypeDecl_values == null) getSootField_String_TypeDecl_values = new java.util.HashMap(4);
    if(getSootField_String_TypeDecl_values.containsKey(_parameters)) {
      return (SootField)getSootField_String_TypeDecl_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SootField getSootField_String_TypeDecl_value = getSootField_compute(name, type);
if(isFinal && num == state().boundariesCrossed) getSootField_String_TypeDecl_values.put(_parameters, getSootField_String_TypeDecl_value);
    return getSootField_String_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private SootField getSootField_compute(String name, TypeDecl type) {
    SootField f = new SootField(name, type.getSootType(), 0);
    getSootClassDecl().addField(f);
    return f;
  }
  /**
   * @attribute syn
   * @aspect LocalNum
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:52
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int variableSize() {
      ASTNode$State state = state();
    int variableSize_value = variableSize_compute();
    return variableSize_value;
  }
  /**
   * @apilevel internal
   */
  private int variableSize_compute() {  return 1;  }
  /**
   * @attribute syn
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:322
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String typeDescriptor() {
      ASTNode$State state = state();
    String typeDescriptor_value = typeDescriptor_compute();
    return typeDescriptor_value;
  }
  /**
   * @apilevel internal
   */
  private String typeDescriptor_compute() {  return jvmName();  }
  protected java.util.Map createEnumMethod_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect EnumsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:42
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MethodDecl createEnumMethod(TypeDecl enumDecl) {
    Object _parameters = enumDecl;
    if(createEnumMethod_TypeDecl_values == null) createEnumMethod_TypeDecl_values = new java.util.HashMap(4);
    if(createEnumMethod_TypeDecl_values.containsKey(_parameters)) {
      return (MethodDecl)createEnumMethod_TypeDecl_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    MethodDecl createEnumMethod_TypeDecl_value = createEnumMethod_compute(enumDecl);
if(isFinal && num == state().boundariesCrossed) createEnumMethod_TypeDecl_values.put(_parameters, createEnumMethod_TypeDecl_value);
    return createEnumMethod_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private MethodDecl createEnumMethod_compute(TypeDecl enumDecl) {
    MethodDecl m = new MethodDecl(
      new Modifiers(new List().add(new Modifier("static")).add(new Modifier("final")).add(new Modifier("private"))),
      typeInt().arrayType().createQualifiedAccess(),
      "$SwitchMap$" + enumDecl.fullName().replace('.', '$'),
      new List(),
      new List(),
      new Opt(
        new Block(
          new List().add(
            new IfStmt(
              new EQExpr(
                createEnumArray(enumDecl).createBoundFieldAccess(),
                new NullLiteral("null")
              ),
              AssignExpr.asStmt(
                createEnumArray(enumDecl).createBoundFieldAccess(),
                new ArrayCreationExpr(
                  new ArrayTypeWithSizeAccess(
                    typeInt().createQualifiedAccess(),
                    enumDecl.createQualifiedAccess().qualifiesAccess(
                        new MethodAccess("values", new List())).qualifiesAccess(
                        new VarAccess("length"))
                  ),
                  new Opt()
                )
              ),
              new Opt()
            )
          ).add(
            new ReturnStmt(
              createEnumArray(enumDecl).createBoundFieldAccess()
            )
          )
        )
      )
    );
    // add method declaration as a body declaration
    getBodyDeclList().insertChild(m, 1);
    // trigger possible rewrites
    return (MethodDecl)getBodyDeclList().getChild(1);
  }
  protected java.util.Map createEnumIndex_EnumConstant_values;
  /**
   * @attribute syn
   * @aspect EnumsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:86
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int createEnumIndex(EnumConstant e) {
    Object _parameters = e;
    if(createEnumIndex_EnumConstant_values == null) createEnumIndex_EnumConstant_values = new java.util.HashMap(4);
    if(createEnumIndex_EnumConstant_values.containsKey(_parameters)) {
      return ((Integer)createEnumIndex_EnumConstant_values.get(_parameters)).intValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    int createEnumIndex_EnumConstant_value = createEnumIndex_compute(e);
if(isFinal && num == state().boundariesCrossed) createEnumIndex_EnumConstant_values.put(_parameters, Integer.valueOf(createEnumIndex_EnumConstant_value));
    return createEnumIndex_EnumConstant_value;
  }
  /**
   * @apilevel internal
   */
  private int createEnumIndex_compute(EnumConstant e) {
    if(createEnumIndexMap == null)
      createEnumIndexMap = new HashMap();
    if(!createEnumIndexMap.containsKey(e.hostType()))
      createEnumIndexMap.put(e.hostType(), new Integer(0));
    Integer i = (Integer)createEnumIndexMap.get(e.hostType());
    i = new Integer(i.intValue() + 1);
    createEnumIndexMap.put(e.hostType(), i);

    MethodDecl m = createEnumMethod(e.hostType());
    List list = m.getBlock().getStmtList();
    list.insertChild(
      new TryStmt(
        new Block(
          new List().add(
            AssignExpr.asStmt(
              createEnumArray(e.hostType()).createBoundFieldAccess().qualifiesAccess(
                new ArrayAccess(
                  e.createBoundFieldAccess().qualifiesAccess(new MethodAccess("ordinal", new List()))
                )
              ),
              new IntegerLiteral(i.toString())
            )
          )
        ),
        new List().add(
          new BasicCatch(
            new ParameterDeclaration(
              lookupType("java.lang", "NoSuchFieldError").createQualifiedAccess(),
              "e"
            ),
            new Block(
              new List()
            )
          )
        ),
        new Opt()
      ),
      list.getNumChild()-1
    );
    return i.intValue();
  }
  protected java.util.Map createEnumArray_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect EnumsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:129
   */
  @SuppressWarnings({"unchecked", "cast"})
  public FieldDeclaration createEnumArray(TypeDecl enumDecl) {
    Object _parameters = enumDecl;
    if(createEnumArray_TypeDecl_values == null) createEnumArray_TypeDecl_values = new java.util.HashMap(4);
    if(createEnumArray_TypeDecl_values.containsKey(_parameters)) {
      return (FieldDeclaration)createEnumArray_TypeDecl_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    FieldDeclaration createEnumArray_TypeDecl_value = createEnumArray_compute(enumDecl);
if(isFinal && num == state().boundariesCrossed) createEnumArray_TypeDecl_values.put(_parameters, createEnumArray_TypeDecl_value);
    return createEnumArray_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private FieldDeclaration createEnumArray_compute(TypeDecl enumDecl) {
    FieldDeclaration f = new FieldDeclaration(
      new Modifiers(new List().add(new Modifier("static")).add(new Modifier("final")).add(new Modifier("private"))),
      typeInt().arrayType().createQualifiedAccess(),
      "$SwitchMap$" + enumDecl.fullName().replace('.', '$'),
      new Opt()
    );
    // add field declaration as a body declaration
    getBodyDeclList().insertChild(f, 0);
    // trigger possible rewrites
    return (FieldDeclaration)getBodyDeclList().getChild(0);
  }
  /**
   * @attribute syn
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:333
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet bridgeCandidates(String signature) {
      ASTNode$State state = state();
    SimpleSet bridgeCandidates_String_value = bridgeCandidates_compute(signature);
    return bridgeCandidates_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet bridgeCandidates_compute(String signature) {  return SimpleSet.emptySet;  }
  /**
   * @apilevel internal
   */
  protected boolean componentType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl componentType_value;
  /**
   * @attribute inh
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:21
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl componentType() {
    if(componentType_computed) {
      return componentType_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    componentType_value = getParent().Define_TypeDecl_componentType(this, null);
if(isFinal && num == state().boundariesCrossed) componentType_computed = true;
    return componentType_value;
  }
  /**
   * @attribute inh
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:50
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeCloneable() {
      ASTNode$State state = state();
    TypeDecl typeCloneable_value = getParent().Define_TypeDecl_typeCloneable(this, null);
    return typeCloneable_value;
  }
  /**
   * @attribute inh
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:51
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeSerializable() {
      ASTNode$State state = state();
    TypeDecl typeSerializable_value = getParent().Define_TypeDecl_typeSerializable(this, null);
    return typeSerializable_value;
  }
  /**
   * @attribute inh
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:31
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CompilationUnit compilationUnit() {
      ASTNode$State state = state();
    CompilationUnit compilationUnit_value = getParent().Define_CompilationUnit_compilationUnit(this, null);
    return compilationUnit_value;
  }
  protected java.util.Map isDAbefore_Variable_values;
  /**
   * @attribute inh
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:240
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAbefore(Variable v) {
    Object _parameters = v;
    if(isDAbefore_Variable_values == null) isDAbefore_Variable_values = new java.util.HashMap(4);
    if(isDAbefore_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAbefore_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAbefore_Variable_value = getParent().Define_boolean_isDAbefore(this, null, v);
if(isFinal && num == state().boundariesCrossed) isDAbefore_Variable_values.put(_parameters, Boolean.valueOf(isDAbefore_Variable_value));
    return isDAbefore_Variable_value;
  }
  protected java.util.Map isDUbefore_Variable_values;
  /**
   * @attribute inh
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:704
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUbefore(Variable v) {
    Object _parameters = v;
    if(isDUbefore_Variable_values == null) isDUbefore_Variable_values = new java.util.HashMap(4);
    if(isDUbefore_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDUbefore_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDUbefore_Variable_value = getParent().Define_boolean_isDUbefore(this, null, v);
if(isFinal && num == state().boundariesCrossed) isDUbefore_Variable_values.put(_parameters, Boolean.valueOf(isDUbefore_Variable_value));
    return isDUbefore_Variable_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeException_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeException_value;
  /**
   * @attribute inh
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:14
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeException() {
    if(typeException_computed) {
      return typeException_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeException_value = getParent().Define_TypeDecl_typeException(this, null);
if(isFinal && num == state().boundariesCrossed) typeException_computed = true;
    return typeException_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeRuntimeException_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeRuntimeException_value;
  /**
   * @attribute inh
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:16
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeRuntimeException() {
    if(typeRuntimeException_computed) {
      return typeRuntimeException_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeRuntimeException_value = getParent().Define_TypeDecl_typeRuntimeException(this, null);
if(isFinal && num == state().boundariesCrossed) typeRuntimeException_computed = true;
    return typeRuntimeException_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeError_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeError_value;
  /**
   * @attribute inh
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:18
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeError() {
    if(typeError_computed) {
      return typeError_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeError_value = getParent().Define_TypeDecl_typeError(this, null);
if(isFinal && num == state().boundariesCrossed) typeError_computed = true;
    return typeError_value;
  }
  protected java.util.Map lookupMethod_String_values;
  /**
   * @attribute inh
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:26
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection lookupMethod(String name) {
    Object _parameters = name;
    if(lookupMethod_String_values == null) lookupMethod_String_values = new java.util.HashMap(4);
    if(lookupMethod_String_values.containsKey(_parameters)) {
      return (Collection)lookupMethod_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    Collection lookupMethod_String_value = getParent().Define_Collection_lookupMethod(this, null, name);
if(isFinal && num == state().boundariesCrossed) lookupMethod_String_values.put(_parameters, lookupMethod_String_value);
    return lookupMethod_String_value;
  }
  /**
   * @attribute inh
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:62
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeInt() {
      ASTNode$State state = state();
    TypeDecl typeInt_value = getParent().Define_TypeDecl_typeInt(this, null);
    return typeInt_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeObject_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeObject_value;
  /**
   * @attribute inh
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:65
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeObject() {
    if(typeObject_computed) {
      return typeObject_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
if(isFinal && num == state().boundariesCrossed) typeObject_computed = true;
    return typeObject_value;
  }
  /**
   * @attribute inh
   * @aspect LookupFullyQualifiedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:98
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupType(String packageName, String typeName) {
      ASTNode$State state = state();
    TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName, typeName);
    return lookupType_String_String_value;
  }
  protected java.util.Map lookupType_String_values;
  /**
   * @attribute inh
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:172
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupType(String name) {
    Object _parameters = name;
    if(lookupType_String_values == null) lookupType_String_values = new java.util.HashMap(4);
    if(lookupType_String_values.containsKey(_parameters)) {
      return (SimpleSet)lookupType_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
if(isFinal && num == state().boundariesCrossed) lookupType_String_values.put(_parameters, lookupType_String_value);
    return lookupType_String_value;
  }
  protected java.util.Map lookupVariable_String_values;
  /**
   * @attribute inh
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:14
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupVariable(String name) {
    Object _parameters = name;
    if(lookupVariable_String_values == null) lookupVariable_String_values = new java.util.HashMap(4);
    if(lookupVariable_String_values.containsKey(_parameters)) {
      return (SimpleSet)lookupVariable_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
if(isFinal && num == state().boundariesCrossed) lookupVariable_String_values.put(_parameters, lookupVariable_String_value);
    return lookupVariable_String_value;
  }
  /**
   * @attribute inh
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:237
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasPackage(String packageName) {
      ASTNode$State state = state();
    boolean hasPackage_String_value = getParent().Define_boolean_hasPackage(this, null, packageName);
    return hasPackage_String_value;
  }
  /**
   * @attribute inh
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:240
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ASTNode enclosingBlock() {
      ASTNode$State state = state();
    ASTNode enclosingBlock_value = getParent().Define_ASTNode_enclosingBlock(this, null);
    return enclosingBlock_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean packageName_computed = false;
  /**
   * @apilevel internal
   */
  protected String packageName_value;
  /**
   * @attribute inh
   * @aspect TypeName
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:89
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String packageName() {
    if(packageName_computed) {
      return packageName_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    packageName_value = getParent().Define_String_packageName(this, null);
if(isFinal && num == state().boundariesCrossed) packageName_computed = true;
    return packageName_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean isAnonymous_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isAnonymous_value;
  /**
   * @attribute inh
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:216
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isAnonymous() {
    if(isAnonymous_computed) {
      return isAnonymous_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isAnonymous_value = getParent().Define_boolean_isAnonymous(this, null);
if(isFinal && num == state().boundariesCrossed) isAnonymous_computed = true;
    return isAnonymous_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:497
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl enclosingType() {
      ASTNode$State state = state();
    TypeDecl enclosingType_value = getParent().Define_TypeDecl_enclosingType(this, null);
    return enclosingType_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:513
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl enclosingBodyDecl() {
      ASTNode$State state = state();
    BodyDecl enclosingBodyDecl_value = getParent().Define_BodyDecl_enclosingBodyDecl(this, null);
    return enclosingBodyDecl_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:519
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isNestedType() {
      ASTNode$State state = state();
    boolean isNestedType_value = getParent().Define_boolean_isNestedType(this, null);
    return isNestedType_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:527
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isMemberType() {
      ASTNode$State state = state();
    boolean isMemberType_value = getParent().Define_boolean_isMemberType(this, null);
    return isMemberType_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:541
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isLocalClass() {
      ASTNode$State state = state();
    boolean isLocalClass_value = getParent().Define_boolean_isLocalClass(this, null);
    return isLocalClass_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:566
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String hostPackage() {
      ASTNode$State state = state();
    String hostPackage_value = getParent().Define_String_hostPackage(this, null);
    return hostPackage_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean unknownType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl unknownType_value;
  /**
   * @attribute inh
   * @aspect Circularity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:672
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl unknownType() {
    if(unknownType_computed) {
      return unknownType_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
if(isFinal && num == state().boundariesCrossed) unknownType_computed = true;
    return unknownType_value;
  }
  /**
   * @attribute inh
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:402
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeVoid() {
      ASTNode$State state = state();
    TypeDecl typeVoid_value = getParent().Define_TypeDecl_typeVoid(this, null);
    return typeVoid_value;
  }
  /**
   * @attribute inh
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:505
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl enclosingInstance() {
      ASTNode$State state = state();
    TypeDecl enclosingInstance_value = getParent().Define_TypeDecl_enclosingInstance(this, null);
    return enclosingInstance_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean inExplicitConstructorInvocation_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean inExplicitConstructorInvocation_value;
  /**
   * @attribute inh
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:127
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean inExplicitConstructorInvocation() {
    if(inExplicitConstructorInvocation_computed) {
      return inExplicitConstructorInvocation_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
if(isFinal && num == state().boundariesCrossed) inExplicitConstructorInvocation_computed = true;
    return inExplicitConstructorInvocation_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean inStaticContext_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean inStaticContext_value;
  /**
   * @attribute inh
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:135
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean inStaticContext() {
    if(inStaticContext_computed) {
      return inStaticContext_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    inStaticContext_value = getParent().Define_boolean_inStaticContext(this, null);
if(isFinal && num == state().boundariesCrossed) inStaticContext_computed = true;
    return inStaticContext_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:280
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean withinSuppressWarnings(String s) {
      ASTNode$State state = state();
    boolean withinSuppressWarnings_String_value = getParent().Define_boolean_withinSuppressWarnings(this, null, s);
    return withinSuppressWarnings_String_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:379
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean withinDeprecatedAnnotation() {
      ASTNode$State state = state();
    boolean withinDeprecatedAnnotation_value = getParent().Define_boolean_withinDeprecatedAnnotation(this, null);
    return withinDeprecatedAnnotation_value;
  }
  /**
   * @attribute inh
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1147
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeWildcard() {
      ASTNode$State state = state();
    TypeDecl typeWildcard_value = getParent().Define_TypeDecl_typeWildcard(this, null);
    return typeWildcard_value;
  }
  /**
   * @attribute inh
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1160
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupWildcardExtends(TypeDecl typeDecl) {
      ASTNode$State state = state();
    TypeDecl lookupWildcardExtends_TypeDecl_value = getParent().Define_TypeDecl_lookupWildcardExtends(this, null, typeDecl);
    return lookupWildcardExtends_TypeDecl_value;
  }
  /**
   * @attribute inh
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1173
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupWildcardSuper(TypeDecl typeDecl) {
      ASTNode$State state = state();
    TypeDecl lookupWildcardSuper_TypeDecl_value = getParent().Define_TypeDecl_lookupWildcardSuper(this, null, typeDecl);
    return lookupWildcardSuper_TypeDecl_value;
  }
  /**
   * @attribute inh
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1193
   */
  @SuppressWarnings({"unchecked", "cast"})
  public LUBType lookupLUBType(Collection bounds) {
      ASTNode$State state = state();
    LUBType lookupLUBType_Collection_value = getParent().Define_LUBType_lookupLUBType(this, null, bounds);
    return lookupLUBType_Collection_value;
  }
  /**
   * @attribute inh
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1231
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GLBType lookupGLBType(ArrayList bounds) {
      ASTNode$State state = state();
    GLBType lookupGLBType_ArrayList_value = getParent().Define_GLBType_lookupGLBType(this, null, bounds);
    return lookupGLBType_ArrayList_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:20
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_componentType(ASTNode caller, ASTNode child) {
    if(caller == arrayType_value){
      return this;
    }
    return getParent().Define_TypeDecl_componentType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:20
   * @apilevel internal
   */
  public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isDest(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:30
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return true;
    }
    return getParent().Define_boolean_isSource(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:245
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBodyDeclListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    BodyDecl b = getBodyDecl(childIndex);
    //if(b instanceof MethodDecl || b instanceof MemberTypeDecl) {
    if(!v.isInstanceVariable() && !v.isClassVariable()) {
      if(v.hostType() != this)
        return isDAbefore(v);
      return false;
    }
    if(b instanceof FieldDeclaration && !((FieldDeclaration)b).isStatic() && v.isClassVariable())
      return true;

    if(b instanceof MethodDecl) {
      return true;
    }
    if(b instanceof MemberTypeDecl && v.isBlank() && v.isFinal() && v.hostType() == this)
      return true;
    if(v.isClassVariable() || v.isInstanceVariable()) {
      if(v.isFinal() &&  v.hostType() != this && instanceOf(v.hostType()))
        return true;
      int index = childIndex - 1;
      if(b instanceof ConstructorDecl)
        index = getNumBodyDecl() - 1;
        
      for(int i = index; i >= 0; i--) {
        b = getBodyDecl(i);
        if(b instanceof FieldDeclaration) {
          FieldDeclaration f = (FieldDeclaration)b;
          if((v.isClassVariable() && f.isStatic()) || (v.isInstanceVariable() && !f.isStatic())) {
            boolean c = f.isDAafter(v);
            //System.err.println("DefiniteAssignment: is " + v.name() + " DA after index " + i + ", " + f + ": " + c);
            return c;
            //return f.isDAafter(v);
          }
        }
        else if(b instanceof StaticInitializer && v.isClassVariable()) {
          StaticInitializer si = (StaticInitializer)b;
          return si.isDAafter(v);
        }
        else if(b instanceof InstanceInitializer && v.isInstanceVariable()) {
          InstanceInitializer ii = (InstanceInitializer)b;
          return ii.isDAafter(v);
        }
      }
    }
    return isDAbefore(v);
  }
}
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:711
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBodyDeclListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    BodyDecl b = getBodyDecl(childIndex);
    if(b instanceof MethodDecl || b instanceof MemberTypeDecl) {
      return false;
    }
    if(v.isClassVariable() || v.isInstanceVariable()) {
      int index = childIndex - 1;
      if(b instanceof ConstructorDecl)
        index = getNumBodyDecl() - 1;
        
      for(int i = index; i >= 0; i--) {
        b = getBodyDecl(i);
        if(b instanceof FieldDeclaration) {
          FieldDeclaration f = (FieldDeclaration)b;
          //System.err.println("  working on field " + f.name() + " which is child " + i);
          if(f == v)
            return !f.hasInit();
          if((v.isClassVariable() && f.isStatic()) || (v.isInstanceVariable() && !f.isStatic()))
            return f.isDUafter(v);
          //System.err.println("  field " + f.name() + " can not affect " + v.name());
        }
        else if(b instanceof StaticInitializer && v.isClassVariable()) {
          StaticInitializer si = (StaticInitializer)b;
          //System.err.println("  working on static initializer which is child " + i);
          return si.isDUafter(v);
        }
        else if(b instanceof InstanceInitializer && v.isInstanceVariable()) {
          InstanceInitializer ii = (InstanceInitializer)b;
          //System.err.println("  working on instance initializer which is child " + i);
          return ii.isDUafter(v);
        }
      }
    }
    //System.err.println("Reached TypeDecl when searching for DU for variable");
    return isDUbefore(v);
  }
}
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:16
   * @apilevel internal
   */
  public Collection Define_Collection_lookupConstructor(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return constructors();
    }
    return getParent().Define_Collection_lookupConstructor(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupConstructor.jrag:20
   * @apilevel internal
   */
  public Collection Define_Collection_lookupSuperConstructor(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return lookupSuperConstructor();
    }
    return getParent().Define_Collection_lookupSuperConstructor(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:34
   * @apilevel internal
   */
  public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
    if(caller == getBodyDeclListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return unqualifiedLookupMethod(name);
    }
    return getParent().Define_Collection_lookupMethod(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:270
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getBodyDeclListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    SimpleSet c = memberTypes(name);
    if(!c.isEmpty()) 
      return c;
    if(name().equals(name))
      return SimpleSet.emptySet.add(this);

    c = lookupType(name);
    // 8.5.2
    if(isClassDecl() && isStatic() && !isTopLevelType()) {
      SimpleSet newSet = SimpleSet.emptySet;
      for(Iterator iter = c.iterator(); iter.hasNext(); ) {
        TypeDecl d = (TypeDecl)iter.next();
        //if(d.isStatic() || d.isTopLevelType() || this.instanceOf(d.enclosingType())) {
          newSet = newSet.add(d);
        //}
      }
      c = newSet;
    }
    return c;
  }
}
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:27
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getBodyDeclListNoTransform()) { 
   int i = caller.getIndexOfChild(child);
{
    SimpleSet list = memberFields(name);
    if(!list.isEmpty()) return list;
    list = lookupVariable(name);
    if(inStaticContext() || isStatic())
      list = removeInstanceVariables(list);
    return list;
  }
}
    return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:301
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePublic(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBePublic(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:302
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeProtected(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeProtected(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:303
   * @apilevel internal
   */
  public boolean Define_boolean_mayBePrivate(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBePrivate(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:306
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeAbstract(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeAbstract(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:304
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStatic(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeStatic(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:309
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeStrictfp(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    if(caller == getModifiersNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayBeStrictfp(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:305
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeFinal(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_mayBeFinal(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:307
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeVolatile(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_mayBeVolatile(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:308
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeTransient(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_mayBeTransient(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:310
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeSynchronized(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_mayBeSynchronized(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Modifiers.jrag:311
   * @apilevel internal
   */
  public boolean Define_boolean_mayBeNative(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_mayBeNative(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:292
   * @apilevel internal
   */
  public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return this;
    }
    return getParent().Define_VariableScope_outerScope(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:364
   * @apilevel internal
   */
  public boolean Define_boolean_insideLoop(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_insideLoop(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:371
   * @apilevel internal
   */
  public boolean Define_boolean_insideSwitch(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_insideSwitch(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:118
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return NameType.EXPRESSION_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:218
   * @apilevel internal
   */
  public boolean Define_boolean_isAnonymous(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isAnonymous(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:495
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingType(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return this;
    }
    return getParent().Define_TypeDecl_enclosingType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:521
   * @apilevel internal
   */
  public boolean Define_boolean_isNestedType(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return true;
    }
    return getParent().Define_boolean_isNestedType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:543
   * @apilevel internal
   */
  public boolean Define_boolean_isLocalClass(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isLocalClass(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:572
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
    if(caller == getModifiersNoTransform()) {
      return hostType();
    }
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return hostType();
    }
    return getParent().Define_TypeDecl_hostType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:404
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_returnType(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return typeVoid();
    }
    return getParent().Define_TypeDecl_returnType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:509
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingInstance(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    if(getBodyDecl(childIndex) instanceof MemberTypeDecl && !((MemberTypeDecl)getBodyDecl(childIndex)).typeDecl().isInnerType())
      return null;
    if(getBodyDecl(childIndex) instanceof ConstructorDecl)
      return enclosingInstance();
    return this;
  }
}
    return getParent().Define_TypeDecl_enclosingInstance(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:12
   * @apilevel internal
   */
  public String Define_String_methodHost(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return typeName();
    }
    return getParent().Define_String_methodHost(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:138
   * @apilevel internal
   */
  public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return isStatic() || inStaticContext();
    }
    return getParent().Define_boolean_inStaticContext(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:159
   * @apilevel internal
   */
  public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return true;
    }
    return getParent().Define_boolean_reportUnreachable(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:74
   * @apilevel internal
   */
  public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
    if(caller == getModifiersNoTransform()) {
      return name.equals("TYPE");
    }
    return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:271
   * @apilevel internal
   */
  public boolean Define_boolean_withinSuppressWarnings(ASTNode caller, ASTNode child, String s) {
    if(caller == getBodyDeclListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return getBodyDecl(i).hasAnnotationSuppressWarnings(s) || hasAnnotationSuppressWarnings(s) ||
    withinSuppressWarnings(s);
    }
    return getParent().Define_boolean_withinSuppressWarnings(this, caller, s);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:374
   * @apilevel internal
   */
  public boolean Define_boolean_withinDeprecatedAnnotation(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return getBodyDecl(i).isDeprecated() || isDeprecated() || withinDeprecatedAnnotation();
    }
    return getParent().Define_boolean_withinDeprecatedAnnotation(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:350
   * @apilevel internal
   */
  public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
