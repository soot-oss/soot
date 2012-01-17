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
 * @declaredat Annotations.ast:8
 */
public class ElementValuePair extends ASTNode<ASTNode> implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
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
  public ElementValuePair clone() throws CloneNotSupportedException {
    ElementValuePair node = (ElementValuePair)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ElementValuePair copy() {
      try {
        ElementValuePair node = (ElementValuePair)clone();
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
  public ElementValuePair fullCopy() {
    ElementValuePair res = (ElementValuePair)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:502
   */
  public void typeCheck() {
    if(!type().commensurateWith(getElementValue()))
      error(type().typeName() + " is not commensurate with " + getElementValue().type().typeName());
  }
  /**
   * @ast method 
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:589
   */
  public void toString(StringBuffer s) {
    s.append(getName() + " = ");
    getElementValue().toString(s);
  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:1
   */
  public ElementValuePair() {
    super();


  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:7
   */
  public ElementValuePair(String p0, ElementValue p1) {
    setName(p0);
    setChild(p1, 0);
  }
  /**
   * @ast method 
   * @declaredat Annotations.ast:11
   */
  public ElementValuePair(beaver.Symbol p0, ElementValue p1) {
    setName(p0);
    setChild(p1, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:18
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Annotations.ast:24
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * Setter for lexeme Name
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:5
   */
  public void setName(String value) {
    tokenString_Name = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat Annotations.ast:8
   */
  
  /**   * @apilevel internal   */  protected String tokenString_Name;
  /**
   * @ast method 
   * @declaredat Annotations.ast:9
   */
  
  public int Namestart;
  /**
   * @ast method 
   * @declaredat Annotations.ast:10
   */
  
  public int Nameend;
  /**
   * @ast method 
   * @declaredat Annotations.ast:11
   */
  public void setName(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setName is only valid for String lexemes");
    tokenString_Name = (String)symbol.value;
    Namestart = symbol.getStart();
    Nameend = symbol.getEnd();
  }
  /**
   * Getter for lexeme Name
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:22
   */
  public String getName() {
    return tokenString_Name != null ? tokenString_Name : "";
  }
  /**
   * Setter for ElementValue
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:5
   */
  public void setElementValue(ElementValue node) {
    setChild(node, 0);
  }
  /**
   * Getter for ElementValue
   * @apilevel high-level
   * @ast method 
   * @declaredat Annotations.ast:12
   */
  public ElementValue getElementValue() {
    return (ElementValue)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Annotations.ast:18
   */
  public ElementValue getElementValueNoTransform() {
    return (ElementValue)getChildNoTransform(0);
  }
  /**
   * @apilevel internal
   */
  protected boolean type_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl type_value;
  /* The annotation type named by an annotation must be accessible (\u00df6.6) at the
  point where the annotation is used, or a compile-time error occurs.
  Comment: This is done by the access control framework* @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:448
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
    if(type_computed) {
      return type_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    type_value = type_compute();
if(isFinal && num == state().boundariesCrossed) type_computed = true;
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {
    Iterator iter = enclosingAnnotationDecl().memberMethods(getName()).iterator();
    if(iter.hasNext()) {
      MethodDecl m = (MethodDecl)iter.next();
      return m.type();
    }
    return unknownType();
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:456
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl unknownType() {
      ASTNode$State state = state();
    TypeDecl unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
    return unknownType_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:458
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl enclosingAnnotationDecl() {
      ASTNode$State state = state();
    TypeDecl enclosingAnnotationDecl_value = getParent().Define_TypeDecl_enclosingAnnotationDecl(this, null);
    return enclosingAnnotationDecl_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag at line 523
    if(type().isArrayDecl() && getElementValue() instanceof ElementConstantValue) {
      state().duringAnnotations++;
      ASTNode result = rewriteRule0();
      state().duringAnnotations--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:523
   * @apilevel internal
   */  private ElementValuePair rewriteRule0() {
{
      setElementValue(new ElementArrayValue(new List().add(getElementValue())));
      return this;
    }  }
}
