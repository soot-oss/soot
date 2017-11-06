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
 * @production ArrayCreationExpr : {@link PrimaryExpr} ::= <span class="component">TypeAccess:{@link Access}</span> <span class="component">[{@link ArrayInit}]</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:133
 */
public class ArrayCreationExpr extends PrimaryExpr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
    numArrays_computed = false;
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
  public ArrayCreationExpr clone() throws CloneNotSupportedException {
    ArrayCreationExpr node = (ArrayCreationExpr)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.numArrays_computed = false;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayCreationExpr copy() {
    try {
      ArrayCreationExpr node = (ArrayCreationExpr) clone();
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
  public ArrayCreationExpr fullCopy() {
    ArrayCreationExpr tree = (ArrayCreationExpr) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:356
   */
  public void toString(StringBuffer s) {
    s.append("new ");
    getTypeAccess().toString(s);
    if(hasArrayInit()) {
      getArrayInit().toString(s);
    }
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:633
   */
  public soot.Value eval(Body b) {
    if(hasArrayInit()) {
      return getArrayInit().eval(b);
    }
    else {
      ArrayList list = new ArrayList();
      getTypeAccess().addArraySize(b, list);
      if(numArrays() == 1) {
        soot.Value size = (soot.Value)list.get(0);
        return b.newNewArrayExpr(
          type().componentType().getSootType(),
          asImmediate(b, size),
          this
        );
      }
      else {
        return b.newNewMultiArrayExpr(
          (soot.ArrayType)type().getSootType(),
          list,
          this
        );
      }
    }
  }
  /**
   * @ast method 
   * 
   */
  public ArrayCreationExpr() {
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
    children = new ASTNode[2];
    setChild(new Opt(), 1);
  }
  /**
   * @ast method 
   * 
   */
  public ArrayCreationExpr(Access p0, Opt<ArrayInit> p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 2;
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
   * Replaces the TypeAccess child.
   * @param node The new node to replace the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the TypeAccess child.
   * @return The current node used as the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Access getTypeAccess() {
    return (Access)getChild(0);
  }
  /**
   * Retrieves the TypeAccess child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the TypeAccess child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * Replaces the optional node for the ArrayInit child. This is the {@code Opt} node containing the child ArrayInit, not the actual child!
   * @param opt The new node to be used as the optional node for the ArrayInit child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setArrayInitOpt(Opt<ArrayInit> opt) {
    setChild(opt, 1);
  }
  /**
   * Check whether the optional ArrayInit child exists.
   * @return {@code true} if the optional ArrayInit child exists, {@code false} if it does not.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public boolean hasArrayInit() {
    return getArrayInitOpt().getNumChild() != 0;
  }
  /**
   * Retrieves the (optional) ArrayInit child.
   * @return The ArrayInit child, if it exists. Returns {@code null} otherwise.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayInit getArrayInit() {
    return (ArrayInit)getArrayInitOpt().getChild(0);
  }
  /**
   * Replaces the (optional) ArrayInit child.
   * @param node The new node to be used as the ArrayInit child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setArrayInit(ArrayInit node) {
    getArrayInitOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<ArrayInit> getArrayInitOpt() {
    return (Opt<ArrayInit>)getChild(1);
  }
  /**
   * Retrieves the optional node for child ArrayInit. This is the {@code Opt} node containing the child ArrayInit, not the actual child!
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The optional node for child ArrayInit.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<ArrayInit> getArrayInitOptNoTransform() {
    return (Opt<ArrayInit>)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:432
   */
  public boolean isDAafterCreation(Variable v) {
    ASTNode$State state = state();
    try {  return getTypeAccess().isDAafter(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:235
   */
  public boolean isDAafter(Variable v) {
    ASTNode$State state = state();
    try {  return hasArrayInit() ? getArrayInit().isDAafter(v) : isDAafterCreation(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:860
   */
  public boolean isDUafterCreation(Variable v) {
    ASTNode$State state = state();
    try {  return getTypeAccess().isDUafter(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:694
   */
  public boolean isDUafter(Variable v) {
    ASTNode$State state = state();
    try {  return hasArrayInit() ? getArrayInit().isDUafter(v) : isDUafterCreation(v);  }
    finally {
    }
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
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:312
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
  private TypeDecl type_compute() {  return getTypeAccess().type();  }
  /**
   * @apilevel internal
   */
  protected boolean numArrays_computed = false;
  /**
   * @apilevel internal
   */
  protected int numArrays_value;
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int numArrays() {
    if(numArrays_computed) {
      return numArrays_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    numArrays_value = numArrays_compute();
      if(isFinal && num == state().boundariesCrossed) numArrays_computed = true;
    return numArrays_value;
  }
  /**
   * @apilevel internal
   */
  private int numArrays_compute() {
    int i = type().dimension();
    Access a = getTypeAccess();
    while(a instanceof ArrayTypeAccess && !(a instanceof ArrayTypeWithSizeAccess)) {
      i--;
      a = ((ArrayTypeAccess)a).getAccess();
    }
    return i;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:434
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getArrayInitOptNoTransform()) {
      return isDAafterCreation(v);
    }
    else {      return getParent().Define_boolean_isDAbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:863
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getArrayInitOptNoTransform()) {
      return isDUafterCreation(v);
    }
    else {      return getParent().Define_boolean_isDUbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:87
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeAccessNoTransform()) {
      return NameType.TYPE_NAME;
    }
    else {      return getParent().Define_NameType_nameType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:262
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
    if(caller == getArrayInitOptNoTransform()) {
      return type();
    }
    else {      return getParent().Define_TypeDecl_declType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:66
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
    if(caller == getArrayInitOptNoTransform()) {
      return type().componentType();
    }
    else {      return getParent().Define_TypeDecl_expectedType(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
