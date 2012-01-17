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
 * @declaredat Annotations.ast:12
 */
public class ElementAnnotationValue extends ElementValue implements Cloneable {
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
  public ElementAnnotationValue clone() throws CloneNotSupportedException {
    ElementAnnotationValue node = (ElementAnnotationValue)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementAnnotationValue copy() {
      try {
        ElementAnnotationValue node = (ElementAnnotationValue)clone();
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
  public ElementAnnotationValue fullCopy() {
    ElementAnnotationValue res = (ElementAnnotationValue)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:596
   */
  public void toString(StringBuffer s) {
    getAnnotation().toString(s);
  }
  /**
   * @ast method 
   * @aspect AnnotationsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/AnnotationsCodegen.jrag:354
   */
  public void appendAsAttributeTo(Collection list, String name) {
    ArrayList elemVals = new ArrayList();
    getAnnotation().appendAsAttributeTo(elemVals);
    list.add(new soot.tagkit.AnnotationAnnotationElem((soot.tagkit.AnnotationTag)elemVals.get(0), '@', name));
  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:1
   */
  public ElementAnnotationValue() {
    super();


  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:7
   */
  public ElementAnnotationValue(Annotation p0) {
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
   * Setter for Annotation
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:5
   */
  public void setAnnotation(Annotation node) {
    setChild(node, 0);
  }
  /**
   * Getter for Annotation
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:12
   */
  public Annotation getAnnotation() {
    return (Annotation)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:18
   */
  public Annotation getAnnotationNoTransform() {
    return (Annotation)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:488
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
    return type() == type;
  }
  /**
   * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:508
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
  private TypeDecl type_compute() {  return getAnnotation().type();  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:423
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Annotation lookupAnnotation(TypeDecl typeDecl) {
      ASTNode$State state = state();
    Annotation lookupAnnotation_TypeDecl_value = getParent().Define_Annotation_lookupAnnotation(this, null, typeDecl);
    return lookupAnnotation_TypeDecl_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:95
   * @apilevel internal
   */
  public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
    if(caller == getAnnotationNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:427
   * @apilevel internal
   */
  public Annotation Define_Annotation_lookupAnnotation(ASTNode caller, ASTNode child, TypeDecl typeDecl) {
    if(caller == getAnnotationNoTransform()) {
      return getAnnotation().type() == typeDecl ? getAnnotation() : lookupAnnotation(typeDecl);
    }
    return getParent().Define_Annotation_lookupAnnotation(this, caller, typeDecl);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
