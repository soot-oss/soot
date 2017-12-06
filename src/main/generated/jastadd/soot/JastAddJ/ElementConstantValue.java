/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
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
 * @production ElementConstantValue : {@link ElementValue} ::= <span class="component">{@link Expr}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.ast:11
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
      ElementConstantValue node = (ElementConstantValue) clone();
      node.parent = null;
      if(children != null)
        node.children = (ASTNode[]) children.clone();
      return node;
    } catch (CloneNotSupportedException e) {
      throw new Error("Error: clone not supported for " +
        getClass().getName());
    }
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementConstantValue fullCopy() {
    ElementConstantValue tree = (ElementConstantValue) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) children[i];
        if(child != null) {
          child = child.fullCopy();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:597
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
   * 
   */
  public ElementConstantValue() {
    super();


  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @ast method 
   * 
   */
  public void init$Children() {
    children = new ASTNode[1];
  }
  /**
   * @ast method 
   * 
   */
  public ElementConstantValue(Expr p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Replaces the Expr child.
   * @param node The new node to replace the Expr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setExpr(Expr node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Expr child.
   * @return The current node used as the Expr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getExpr() {
    return (Expr)getChild(0);
  }
  /**
   * Retrieves the Expr child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Expr child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getExprNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:57
   */
  public boolean validTarget(Annotation a) {
    ASTNode$State state = state();
    try {
    Variable v = getExpr().varDecl();
    if(v == null) return true;
    return v.hostType().fullName().equals("java.lang.annotation.ElementType") && a.mayUseAnnotationTarget(v.name());
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:181
   */
  public ElementValue definesElementTypeValue(String name) {
    ASTNode$State state = state();
    try {
    Variable v = getExpr().varDecl();
    if(v != null && v.hostType().fullName().equals("java.lang.annotation.ElementType") && v.name().equals(name))
      return this;
    return null;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:295
   */
  public boolean hasValue(String s) {
    ASTNode$State state = state();
    try {  return getExpr().type().isString() &&
    getExpr().isConstant() && 
    getExpr().constant().stringValue().equals(s);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:475
   */
  public boolean commensurateWithTypeDecl(TypeDecl type) {
    ASTNode$State state = state();
    try {
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
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:510
   */
  public TypeDecl type() {
    ASTNode$State state = state();
    try {  return getExpr().type();  }
    finally {
    }
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:550
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getExprNoTransform()) {
      return NameType.AMBIGUOUS_NAME;
    }
    else {      return getParent().Define_NameType_nameType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:555
   * @apilevel internal
   */
  public String Define_String_methodHost(ASTNode caller, ASTNode child) {
    if(caller == getExprNoTransform()) {
      return enclosingAnnotationDecl().typeName();
    }
    else {      return getParent().Define_String_methodHost(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
