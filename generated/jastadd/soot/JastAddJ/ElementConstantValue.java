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
 * @declaredat Annotations.ast:11
 */
public class ElementConstantValue extends ElementValue implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
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
  public ElementConstantValue clone() throws CloneNotSupportedException {
    ElementConstantValue node = (ElementConstantValue)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementConstantValue copy() {
      try {
        ElementConstantValue node = (ElementConstantValue)clone();
        if(children != null) node.children = (ASTNode[])children.clone();
        return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
  }
  /**
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementConstantValue fullCopy() {
    ElementConstantValue res = (ElementConstantValue)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:169
   */
  public void nameCheck() {
    if(enclosingAnnotationDecl().fullName().equals("java.lang.annotation.Target")) {
      Variable v = getExpr().varDecl();
      if(v != null && v.hostType().fullName().equals("java.lang.annotation.ElementType"))
        if(lookupElementTypeValue(v.name()) != this)
          error("repeated annotation target");
    }
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:593
   */
  public void toString(StringBuffer s) {
    getExpr().toString(s);
  }
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:326
   */
  public void appendAsAttributeTo(Collection list, String name) {
    if(getExpr().isConstant() && !getExpr().type().isEnumDecl()) {
      char kind = getExpr().type().isString() ? 's' : getExpr().type().typeDescriptor().charAt(0);
      TypeDecl type = getExpr().type();
      if(type.isLong())
        list.add(new soot.tagkit.AnnotationLongElem(getExpr().constant().longValue(), kind, name));
      else if(type.isDouble())
        list.add(new soot.tagkit.AnnotationDoubleElem(getExpr().constant().doubleValue(), kind, name));
      else if(type.isFloat())
        list.add(new soot.tagkit.AnnotationFloatElem(getExpr().constant().floatValue(), kind, name));
      else if(type.isString())
        list.add(new soot.tagkit.AnnotationStringElem(getExpr().constant().stringValue(), kind, name));
      else if(type.isIntegralType())
        list.add(new soot.tagkit.AnnotationIntElem(getExpr().constant().intValue(), kind, name));
      else if(type().isBoolean())
       list.add(new soot.tagkit.AnnotationBooleanElem(getExpr().constant().booleanValue(), kind, name));      
      else
        throw new UnsupportedOperationException("Unsupported attribute constant type " + type.typeName());
    }
    else if(getExpr().isClassAccess()) {
      list.add(new soot.tagkit.AnnotationClassElem(getExpr().type().typeDescriptor(), 'c', name));
    }
    else {
      Variable v = getExpr().varDecl();
      if(v == null) throw new Error("Expected Enumeration constant");
      list.add(new soot.tagkit.AnnotationEnumElem(v.type().typeDescriptor(), v.name(), 'e', name));
    }
  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:1
   */
  public ElementConstantValue() {
    super();


  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:7
   */
  public ElementConstantValue(Expr p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:13
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Annotations.ast:19
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:5
   */
  public void setExpr(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:12
   */
  public Expr getExpr() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:18
   */
  public Expr getExprNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:58
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean validTarget(Annotation a) {
      ASTNode$State state = state();
    boolean validTarget_Annotation_value = validTarget_compute(a);
    return validTarget_Annotation_value;
  }
  /**
   * @apilevel internal
   */
  private boolean validTarget_compute(Annotation a) {
    Variable v = getExpr().varDecl();
    if(v == null) return true;
    return v.hostType().fullName().equals("java.lang.annotation.ElementType") && a.mayUseAnnotationTarget(v.name());
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:182
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementValue definesElementTypeValue(String name) {
      ASTNode$State state = state();
    ElementValue definesElementTypeValue_String_value = definesElementTypeValue_compute(name);
    return definesElementTypeValue_String_value;
  }
  /**
   * @apilevel internal
   */
  private ElementValue definesElementTypeValue_compute(String name) {
    Variable v = getExpr().varDecl();
    if(v != null && v.hostType().fullName().equals("java.lang.annotation.ElementType") && v.name().equals(name))
      return this;
    return null;
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:296
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasValue(String s) {
      ASTNode$State state = state();
    boolean hasValue_String_value = hasValue_compute(s);
    return hasValue_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasValue_compute(String s) {  return getExpr().type().isString() &&
    getExpr().isConstant() && 
    getExpr().constant().stringValue().equals(s);  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:474
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean commensurateWithTypeDecl(TypeDecl type) {
      ASTNode$State state = state();
    boolean commensurateWithTypeDecl_TypeDecl_value = commensurateWithTypeDecl_compute(type);
    return commensurateWithTypeDecl_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean commensurateWithTypeDecl_compute(TypeDecl type) {
    Expr v = getExpr();
    if(!v.type().assignConversionTo(type, v))
      return false;
    if((type.isPrimitive() || type.isString()) && !v.isConstant())
      return false;
    if(v.type().isNull())
      return false;
    if(type.fullName().equals("java.lang.Class") && !v.isClassAccess())
      return false;
    if(type.isEnumDecl() && (v.varDecl() == null || !(v.varDecl() instanceof EnumConstant)))
      return false;
    return true;
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:507
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
      ASTNode$State state = state();
    TypeDecl type_value = type_compute();
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return getExpr().type();  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:177
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementValue lookupElementTypeValue(String name) {
      ASTNode$State state = state();
    ElementValue lookupElementTypeValue_String_value = getParent().Define_ElementValue_lookupElementTypeValue(this, null, name);
    return lookupElementTypeValue_String_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:546
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getExprNoTransform()) {
      return NameType.AMBIGUOUS_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:551
   * @apilevel internal
   */
  public String Define_String_methodHost(ASTNode caller, ASTNode child) {
    if(caller == getExprNoTransform()) {
      return enclosingAnnotationDecl().typeName();
    }
    return getParent().Define_String_methodHost(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
