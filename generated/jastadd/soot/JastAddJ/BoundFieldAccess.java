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
 * @production BoundFieldAccess : {@link VarAccess} ::= <span class="component">&lt;FieldDeclaration:FieldDeclaration&gt;</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.ast:6
 */
public class BoundFieldAccess extends VarAccess implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    decl_computed = false;
    decl_value = null;
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
  public BoundFieldAccess clone() throws CloneNotSupportedException {
    BoundFieldAccess node = (BoundFieldAccess)super.clone();
    node.decl_computed = false;
    node.decl_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BoundFieldAccess copy() {
    try {
      BoundFieldAccess node = (BoundFieldAccess) clone();
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
  public BoundFieldAccess fullCopy() {
    BoundFieldAccess tree = (BoundFieldAccess) copy();
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
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:68
   */
  public BoundFieldAccess(FieldDeclaration f) {
    this(f.name(), f);
  }
  /**
   * @ast method 
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:73
   */
  public boolean isExactVarAccess() {
    return false;
  }
  /**
   * @ast method 
   * 
   */
  public BoundFieldAccess() {
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
  }
  /**
   * @ast method 
   * 
   */
  public BoundFieldAccess(String p0, FieldDeclaration p1) {
    setID(p0);
    setFieldDeclaration(p1);
  }
  /**
   * @ast method 
   * 
   */
  public BoundFieldAccess(beaver.Symbol p0, FieldDeclaration p1) {
    setID(p0);
    setFieldDeclaration(p1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 0;
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
   * Replaces the lexeme ID.
   * @param value The new value for the lexeme ID.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * JastAdd-internal setter for lexeme ID using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme ID.
   * @return The value for the lexeme ID.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Replaces the lexeme FieldDeclaration.
   * @param value The new value for the lexeme FieldDeclaration.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setFieldDeclaration(FieldDeclaration value) {
    tokenFieldDeclaration_FieldDeclaration = value;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected FieldDeclaration tokenFieldDeclaration_FieldDeclaration;
  /**
   * Retrieves the value for the lexeme FieldDeclaration.
   * @return The value for the lexeme FieldDeclaration.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public FieldDeclaration getFieldDeclaration() {
    return tokenFieldDeclaration_FieldDeclaration;
  }
  /**
   * @apilevel internal
   */
  protected boolean decl_computed = false;
  /**
   * @apilevel internal
   */
  protected Variable decl_value;
  /**
   * @attribute syn
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Variable decl() {
    if(decl_computed) {
      return decl_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    decl_value = decl_compute();
      if(isFinal && num == state().boundariesCrossed) decl_computed = true;
    return decl_value;
  }
  /**
   * @apilevel internal
   */
  private Variable decl_compute() {  return getFieldDeclaration();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
