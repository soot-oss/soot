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
 * @declaredat Generics.ast:19
 */
public class WildcardExtends extends AbstractWildcard implements Cloneable {
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
  public WildcardExtends clone() throws CloneNotSupportedException {
    WildcardExtends node = (WildcardExtends)super.clone();
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
  public WildcardExtends copy() {
      try {
        WildcardExtends node = (WildcardExtends)clone();
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
  public WildcardExtends fullCopy() {
    WildcardExtends res = (WildcardExtends)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect GenericsPrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsPrettyPrint.jrag:164
   */
  public void toString(StringBuffer s) {
    s.append("? extends ");
    getAccess().toString(s);
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:1
   */
  public WildcardExtends() {
    super();


  }
  /**
   * @ast method 
   * @declaredat Generics.ast:7
   */
  public WildcardExtends(Access p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:13
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Generics.ast:19
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Getter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:12
   */
  public Access getAccess() {
    return (Access)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:18
   */
  public Access getAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * @apilevel internal
   */
  protected boolean type_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl type_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1133
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
  private TypeDecl type_compute() {  return lookupWildcardExtends(getAccess().type());  }
  /**
   * @attribute inh
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1136
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupWildcardExtends(TypeDecl typeDecl) {
      ASTNode$State state = state();
    TypeDecl lookupWildcardExtends_TypeDecl_value = getParent().Define_TypeDecl_lookupWildcardExtends(this, null, typeDecl);
    return lookupWildcardExtends_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
