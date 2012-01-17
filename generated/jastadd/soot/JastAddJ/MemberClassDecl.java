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
 * @declaredat java.ast:96
 */
public class MemberClassDecl extends MemberTypeDecl implements Cloneable {
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
  public MemberClassDecl clone() throws CloneNotSupportedException {
    MemberClassDecl node = (MemberClassDecl)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MemberClassDecl copy() {
      try {
        MemberClassDecl node = (MemberClassDecl)clone();
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
  public MemberClassDecl fullCopy() {
    MemberClassDecl res = (MemberClassDecl)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:206
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    getClassDecl().toString(s);
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public MemberClassDecl() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public MemberClassDecl(ClassDecl p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:13
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:19
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for ClassDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setClassDecl(ClassDecl node) {
    setChild(node, 0);
  }
  /**
   * Getter for ClassDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public ClassDecl getClassDecl() {
    return (ClassDecl)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public ClassDecl getClassDeclNoTransform() {
    return (ClassDecl)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:397
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeDecl() {
      ASTNode$State state = state();
    TypeDecl typeDecl_value = typeDecl_compute();
    return typeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeDecl_compute() {  return getClassDecl();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:528
   * @apilevel internal
   */
  public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
    if(caller == getClassDeclNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isMemberType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:145
   * @apilevel internal
   */
  public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
    if(caller == getClassDeclNoTransform()) {
      return false;
    }
    return getParent().Define_boolean_inStaticContext(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
