
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


// 4.1 The Kinds of Types and Values

public abstract class ReferenceType extends TypeDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        narrowingConversionTo_TypeDecl_values = null;
        unboxed_computed = false;
        unboxed_value = null;
        jvmName_computed = false;
        jvmName_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ReferenceType clone() throws CloneNotSupportedException {
        ReferenceType node = (ReferenceType)super.clone();
        node.narrowingConversionTo_TypeDecl_values = null;
        node.unboxed_computed = false;
        node.unboxed_value = null;
        node.jvmName_computed = false;
        node.jvmName_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in AutoBoxingCodegen.jrag at line 74


  // Code generation for Unboxing Conversion
  public soot.Value emitCastTo(Body b, soot.Value v, TypeDecl type, ASTNode location) {
    if(this == type)
      return v;
    else if(type instanceof PrimitiveType)
      return type.boxed().emitUnboxingOperation(b, emitCastTo(b, v, type.boxed(), location), location);
    else 
      return super.emitCastTo(b, v, type, location);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 41

    public ReferenceType() {
        super();

        setChild(new List(), 1);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 41
    public ReferenceType(Modifiers p0, String p1, List<BodyDecl> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
    }

    // Declared in java.ast at line 18


    // Declared in java.ast line 41
    public ReferenceType(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
        setChild(p0, 0);
        setID(p1);
        setChild(p2, 1);
    }

    // Declared in java.ast at line 24


  protected int numChildren() {
    return 2;
  }

    // Declared in java.ast at line 27

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 38
    public void setModifiers(Modifiers node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(0);
    }

    // Declared in java.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 38
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 38
    public void setBodyDeclList(List<BodyDecl> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    public int getNumBodyDecl() {
        return getBodyDeclList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public BodyDecl getBodyDecl(int i) {
        return (BodyDecl)getBodyDeclList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addBodyDecl(BodyDecl node) {
        List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addBodyDeclNoTransform(BodyDecl node) {
        List<BodyDecl> list = getBodyDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setBodyDecl(BodyDecl node, int i) {
        List<BodyDecl> list = getBodyDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<BodyDecl> getBodyDecls() {
        return getBodyDeclList();
    }

    // Declared in java.ast at line 31

    public List<BodyDecl> getBodyDeclsNoTransform() {
        return getBodyDeclListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclList() {
        List<BodyDecl> list = (List<BodyDecl>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<BodyDecl> getBodyDeclListNoTransform() {
        return (List<BodyDecl>)getChildNoTransform(1);
    }

    // Declared in TypeAnalysis.jrag at line 33
 @SuppressWarnings({"unchecked", "cast"})     public boolean wideningConversionTo(TypeDecl type) {
        ASTNode$State state = state();
        boolean wideningConversionTo_TypeDecl_value = wideningConversionTo_compute(type);
        return wideningConversionTo_TypeDecl_value;
    }

    private boolean wideningConversionTo_compute(TypeDecl type) {  return instanceOf(type);  }

    // Declared in TypeAnalysis.jrag at line 36
 @SuppressWarnings({"unchecked", "cast"})     public boolean narrowingConversionTo(TypeDecl type) {
        Object _parameters = type;
if(narrowingConversionTo_TypeDecl_values == null) narrowingConversionTo_TypeDecl_values = new java.util.HashMap(4);
        if(narrowingConversionTo_TypeDecl_values.containsKey(_parameters)) {
            return ((Boolean)narrowingConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean narrowingConversionTo_TypeDecl_value = narrowingConversionTo_compute(type);
        if(isFinal && num == state().boundariesCrossed)
            narrowingConversionTo_TypeDecl_values.put(_parameters, Boolean.valueOf(narrowingConversionTo_TypeDecl_value));
        return narrowingConversionTo_TypeDecl_value;
    }

    private boolean narrowingConversionTo_compute(TypeDecl type) {
    if(type.instanceOf(this))
      return true;
    if(isClassDecl() && !getModifiers().isFinal() && type.isInterfaceDecl())
      return true;
    if(isInterfaceDecl() && type.isClassDecl() && !type.getModifiers().isFinal())
      return true;
    if(isInterfaceDecl() && type.instanceOf(this))
      return true;
    if(fullName().equals("java.lang.Object") && type.isInterfaceDecl())
      return true;
    // Dragons
    // TODO: Check if both are interfaces with compatible methods
    if(isArrayDecl() && type.isArrayDecl() && elementType().instanceOf(type.elementType()))
      return true;
    return false;
  }

    // Declared in TypeAnalysis.jrag at line 166
 @SuppressWarnings({"unchecked", "cast"})     public boolean isReferenceType() {
        ASTNode$State state = state();
        boolean isReferenceType_value = isReferenceType_compute();
        return isReferenceType_value;
    }

    private boolean isReferenceType_compute() {  return true;  }

    // Declared in TypeAnalysis.jrag at line 483
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSupertypeOfNullType(NullType type) {
        ASTNode$State state = state();
        boolean isSupertypeOfNullType_NullType_value = isSupertypeOfNullType_compute(type);
        return isSupertypeOfNullType_NullType_value;
    }

    private boolean isSupertypeOfNullType_compute(NullType type) {  return true;  }

    // Declared in Annotations.jrag at line 123
 @SuppressWarnings({"unchecked", "cast"})     public boolean isValidAnnotationMethodReturnType() {
        ASTNode$State state = state();
        boolean isValidAnnotationMethodReturnType_value = isValidAnnotationMethodReturnType_compute();
        return isValidAnnotationMethodReturnType_value;
    }

    private boolean isValidAnnotationMethodReturnType_compute() {
    if(isString()) return true;
    if(fullName().equals("java.lang.Class"))
      return true;
    // include generic versions of Class
    if(erasure().fullName().equals("java.lang.Class"))
      return true;
    return false;
  }

    // Declared in AutoBoxing.jrag at line 48
 @SuppressWarnings({"unchecked", "cast"})     public boolean unboxingConversionTo(TypeDecl typeDecl) {
        ASTNode$State state = state();
        boolean unboxingConversionTo_TypeDecl_value = unboxingConversionTo_compute(typeDecl);
        return unboxingConversionTo_TypeDecl_value;
    }

    private boolean unboxingConversionTo_compute(TypeDecl typeDecl) {  return unboxed() == typeDecl;  }

    // Declared in AutoBoxing.jrag at line 52
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl unboxed() {
        if(unboxed_computed) {
            return unboxed_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        unboxed_value = unboxed_compute();
        if(isFinal && num == state().boundariesCrossed)
            unboxed_computed = true;
        return unboxed_value;
    }

    private TypeDecl unboxed_compute() {
    if(packageName().equals("java.lang") && isTopLevelType()) {
      String n = name();
      if(n.equals("Boolean")) return typeBoolean();
      if(n.equals("Byte")) return typeByte();
      if(n.equals("Character")) return typeChar();
      if(n.equals("Short")) return typeShort();
      if(n.equals("Integer")) return typeInt();
      if(n.equals("Long")) return typeLong();
      if(n.equals("Float")) return typeFloat();
      if(n.equals("Double")) return typeDouble();
    }
    return unknownType();
  }

    // Declared in AutoBoxing.jrag at line 170
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl unaryNumericPromotion() {
        ASTNode$State state = state();
        TypeDecl unaryNumericPromotion_value = unaryNumericPromotion_compute();
        return unaryNumericPromotion_value;
    }

    private TypeDecl unaryNumericPromotion_compute() {  return isNumericType() && !isUnknown() ? unboxed().unaryNumericPromotion() : this;  }

    // Declared in AutoBoxing.jrag at line 174
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl binaryNumericPromotion(TypeDecl type) {
        ASTNode$State state = state();
        TypeDecl binaryNumericPromotion_TypeDecl_value = binaryNumericPromotion_compute(type);
        return binaryNumericPromotion_TypeDecl_value;
    }

    private TypeDecl binaryNumericPromotion_compute(TypeDecl type) {  return unboxed().binaryNumericPromotion(type);  }

    // Declared in AutoBoxing.jrag at line 196
 @SuppressWarnings({"unchecked", "cast"})     public boolean isNumericType() {
        ASTNode$State state = state();
        boolean isNumericType_value = isNumericType_compute();
        return isNumericType_value;
    }

    private boolean isNumericType_compute() {  return !unboxed().isUnknown() && unboxed().isNumericType();  }

    // Declared in AutoBoxing.jrag at line 199
 @SuppressWarnings({"unchecked", "cast"})     public boolean isIntegralType() {
        ASTNode$State state = state();
        boolean isIntegralType_value = isIntegralType_compute();
        return isIntegralType_value;
    }

    private boolean isIntegralType_compute() {  return !unboxed().isUnknown() && unboxed().isIntegralType();  }

    // Declared in AutoBoxing.jrag at line 202
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPrimitive() {
        ASTNode$State state = state();
        boolean isPrimitive_value = isPrimitive_compute();
        return isPrimitive_value;
    }

    private boolean isPrimitive_compute() {  return !unboxed().isUnknown() && unboxed().isPrimitive();  }

    // Declared in AutoBoxing.jrag at line 215
 @SuppressWarnings({"unchecked", "cast"})     public boolean isBoolean() {
        ASTNode$State state = state();
        boolean isBoolean_value = isBoolean_compute();
        return isBoolean_value;
    }

    private boolean isBoolean_compute() {  return fullName().equals("java.lang.Boolean") && unboxed().isBoolean();  }

    // Declared in GenericsSubtype.jrag at line 480
 @SuppressWarnings({"unchecked", "cast"})     public boolean supertypeNullType(NullType type) {
        ASTNode$State state = state();
        boolean supertypeNullType_NullType_value = supertypeNullType_compute(type);
        return supertypeNullType_NullType_value;
    }

    private boolean supertypeNullType_compute(NullType type) {  return true;  }

    // Declared in InnerClasses.jrag at line 80
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl stringPromotion() {
        ASTNode$State state = state();
        TypeDecl stringPromotion_value = stringPromotion_compute();
        return stringPromotion_value;
    }

    private TypeDecl stringPromotion_compute() {  return typeObject();  }

    // Declared in Java2Rewrites.jrag at line 18
 @SuppressWarnings({"unchecked", "cast"})     public String jvmName() {
        if(jvmName_computed) {
            return jvmName_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        jvmName_value = jvmName_compute();
        if(isFinal && num == state().boundariesCrossed)
            jvmName_computed = true;
        return jvmName_value;
    }

    private String jvmName_compute() {
    if(!isNestedType())
      return fullName();
    else if(isAnonymous() || isLocalClass())
      return enclosingType().jvmName() + "$" + uniqueIndex() + name();
    else
      return enclosingType().jvmName() + "$" + name();
  }

    // Declared in Java2Rewrites.jrag at line 60
 @SuppressWarnings({"unchecked", "cast"})     public String referenceClassFieldName() {
        ASTNode$State state = state();
        String referenceClassFieldName_value = referenceClassFieldName_compute();
        return referenceClassFieldName_value;
    }

    private String referenceClassFieldName_compute() {  return "class$" + jvmName().replace('[', '$').replace('.', '$').replace(';', ' ').trim();  }

    // Declared in AutoBoxing.jrag at line 66
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeBoolean() {
        ASTNode$State state = state();
        TypeDecl typeBoolean_value = getParent().Define_TypeDecl_typeBoolean(this, null);
        return typeBoolean_value;
    }

    // Declared in AutoBoxing.jrag at line 67
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeByte() {
        ASTNode$State state = state();
        TypeDecl typeByte_value = getParent().Define_TypeDecl_typeByte(this, null);
        return typeByte_value;
    }

    // Declared in AutoBoxing.jrag at line 68
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeChar() {
        ASTNode$State state = state();
        TypeDecl typeChar_value = getParent().Define_TypeDecl_typeChar(this, null);
        return typeChar_value;
    }

    // Declared in AutoBoxing.jrag at line 69
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeShort() {
        ASTNode$State state = state();
        TypeDecl typeShort_value = getParent().Define_TypeDecl_typeShort(this, null);
        return typeShort_value;
    }

    // Declared in AutoBoxing.jrag at line 70
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeInt() {
        ASTNode$State state = state();
        TypeDecl typeInt_value = getParent().Define_TypeDecl_typeInt(this, null);
        return typeInt_value;
    }

    // Declared in AutoBoxing.jrag at line 71
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeLong() {
        ASTNode$State state = state();
        TypeDecl typeLong_value = getParent().Define_TypeDecl_typeLong(this, null);
        return typeLong_value;
    }

    // Declared in AutoBoxing.jrag at line 72
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeFloat() {
        ASTNode$State state = state();
        TypeDecl typeFloat_value = getParent().Define_TypeDecl_typeFloat(this, null);
        return typeFloat_value;
    }

    // Declared in AutoBoxing.jrag at line 73
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeDouble() {
        ASTNode$State state = state();
        TypeDecl typeDouble_value = getParent().Define_TypeDecl_typeDouble(this, null);
        return typeDouble_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
