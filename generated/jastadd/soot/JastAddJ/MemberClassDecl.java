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
 * @production MemberClassDecl : {@link MemberTypeDecl} ::= <span class="component">{@link ClassDecl}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:99
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
      MemberClassDecl node = (MemberClassDecl) clone();
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
  public MemberClassDecl fullCopy() {
    MemberClassDecl tree = (MemberClassDecl) copy();
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
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:206
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    getClassDecl().toString(s);
  }
  /**
   * @ast method 
   * 
   */
  public MemberClassDecl() {
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
  public MemberClassDecl(ClassDecl p0) {
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
   * Replaces the ClassDecl child.
   * @param node The new node to replace the ClassDecl child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setClassDecl(ClassDecl node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the ClassDecl child.
   * @return The current node used as the ClassDecl child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public ClassDecl getClassDecl() {
    return (ClassDecl)getChild(0);
  }
  /**
   * Retrieves the ClassDecl child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the ClassDecl child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public ClassDecl getClassDeclNoTransform() {
    return (ClassDecl)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:484
   */
  public TypeDecl typeDecl() {
    ASTNode$State state = state();
    try {  return getClassDecl();  }
    finally {
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:527
   * @apilevel internal
   */
  public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
    if(caller == getClassDeclNoTransform()) {
      return true;
    }
    else {      return getParent().Define_boolean_isMemberType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:145
   * @apilevel internal
   */
  public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
    if(caller == getClassDeclNoTransform()) {
      return false;
    }
    else {      return getParent().Define_boolean_inStaticContext(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
