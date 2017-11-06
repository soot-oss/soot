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
 * @production ArrayInit : {@link Expr} ::= <span class="component">Init:{@link Expr}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:93
 */
public class ArrayInit extends Expr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    computeDABefore_int_Variable_values = null;
    computeDUbefore_int_Variable_values = null;
    type_computed = false;
    type_value = null;
    declType_computed = false;
    declType_value = null;
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
  public ArrayInit clone() throws CloneNotSupportedException {
    ArrayInit node = (ArrayInit)super.clone();
    node.computeDABefore_int_Variable_values = null;
    node.computeDUbefore_int_Variable_values = null;
    node.type_computed = false;
    node.type_value = null;
    node.declType_computed = false;
    node.declType_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayInit copy() {
    try {
      ArrayInit node = (ArrayInit) clone();
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
  public ArrayInit fullCopy() {
    ArrayInit tree = (ArrayInit) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:221
   */
  public void toString(StringBuffer s) {
    s.append("{ ");
    if(getNumInit() > 0) {
      getInit(0).toString(s);
      for(int i = 1; i < getNumInit(); i++) {
        s.append(", ");
        getInit(i).toString(s);
      }
    }
    s.append(" } ");
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:144
   */
  public void typeCheck() {
    TypeDecl initializerType = declType().componentType();
    if(initializerType.isUnknown())
      error("the dimension of the initializer is larger than the expected dimension");
    for(int i = 0; i < getNumInit(); i++) {
      Expr e = getInit(i);
      if(!e.type().assignConversionTo(initializerType, e))
        error("the type " + e.type().name() + " of the initializer is not compatible with " + initializerType.name()); 
    }
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:668
   */
  public soot.Value eval(Body b) {
    soot.Value size = IntType.emitConstant(getNumInit());
    Local array = asLocal(b, b.newNewArrayExpr(
      type().componentType().getSootType(),
      asImmediate(b, size),
      this
    ));
    for(int i = 0; i < getNumInit(); i++) {
      Value rvalue = 
        getInit(i).type().emitCastTo(b, // Assign conversion
          getInit(i),
          expectedType()
        );
      Value index = IntType.emitConstant(i);
      Value lvalue = b.newArrayRef(array, index, getInit(i));
      b.setLine(this);
      b.add(b.newAssignStmt(lvalue, asImmediate(b, rvalue), getInit(i)));
    }
    return array;
  }
  /**
   * @ast method 
   * 
   */
  public ArrayInit() {
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
    setChild(new List(), 0);
  }
  /**
   * @ast method 
   * 
   */
  public ArrayInit(List<Expr> p0) {
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
   * Replaces the Init list.
   * @param list The new list node to be used as the Init list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setInitList(List<Expr> list) {
    setChild(list, 0);
  }
  /**
   * Retrieves the number of children in the Init list.
   * @return Number of children in the Init list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumInit() {
    return getInitList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Init list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Init list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumInitNoTransform() {
    return getInitListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Init list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Init list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getInit(int i) {
    return (Expr)getInitList().getChild(i);
  }
  /**
   * Append an element to the Init list.
   * @param node The element to append to the Init list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addInit(Expr node) {
    List<Expr> list = (parent == null || state == null) ? getInitListNoTransform() : getInitList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addInitNoTransform(Expr node) {
    List<Expr> list = getInitListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Init list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setInit(Expr node, int i) {
    List<Expr> list = getInitList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Init list.
   * @return The node representing the Init list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Expr> getInits() {
    return getInitList();
  }
  /**
   * Retrieves the Init list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Init list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Expr> getInitsNoTransform() {
    return getInitListNoTransform();
  }
  /**
   * Retrieves the Init list.
   * @return The node representing the Init list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getInitList() {
    List<Expr> list = (List<Expr>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Init list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Init list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getInitListNoTransform() {
    return (List<Expr>)getChildNoTransform(0);
  }
  /* 
   * representableIn(T) is true if and only if the the expression is a 
   * compile-time constant of type byte, char, short or int, and the value  
   * of the expression can be represented (by an expression) in the type T
   * where T must be byte, char or short.
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:308
   */
  public boolean representableIn(TypeDecl t) {
    ASTNode$State state = state();
    try {
    for(int i = 0; i < getNumInit(); i++)
      if(!getInit(i).representableIn(t))
        return false;
    return true;
  }
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
    try {  return getNumInit() == 0 ? isDAbefore(v) : getInit(getNumInit()-1).isDAafter(v);  }
    finally {
    }
  }
  protected java.util.Map computeDABefore_int_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:502
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean computeDABefore(int childIndex, Variable v) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(Integer.valueOf(childIndex));
    _parameters.add(v);
    if(computeDABefore_int_Variable_values == null) computeDABefore_int_Variable_values = new java.util.HashMap(4);
    if(computeDABefore_int_Variable_values.containsKey(_parameters)) {
      return ((Boolean)computeDABefore_int_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean computeDABefore_int_Variable_value = computeDABefore_compute(childIndex, v);
      if(isFinal && num == state().boundariesCrossed) computeDABefore_int_Variable_values.put(_parameters, Boolean.valueOf(computeDABefore_int_Variable_value));
    return computeDABefore_int_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean computeDABefore_compute(int childIndex, Variable v) {
    if(childIndex == 0) return isDAbefore(v);
    int index = childIndex-1;
    while(index > 0 && getInit(index).isConstant())
      index--;
    return getInit(childIndex-1).isDAafter(v);
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:694
   */
  public boolean isDUafter(Variable v) {
    ASTNode$State state = state();
    try {  return getNumInit() == 0 ? isDUbefore(v) : getInit(getNumInit()-1).isDUafter(v);  }
    finally {
    }
  }
  protected java.util.Map computeDUbefore_int_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:885
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean computeDUbefore(int childIndex, Variable v) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(Integer.valueOf(childIndex));
    _parameters.add(v);
    if(computeDUbefore_int_Variable_values == null) computeDUbefore_int_Variable_values = new java.util.HashMap(4);
    if(computeDUbefore_int_Variable_values.containsKey(_parameters)) {
      return ((Boolean)computeDUbefore_int_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean computeDUbefore_int_Variable_value = computeDUbefore_compute(childIndex, v);
      if(isFinal && num == state().boundariesCrossed) computeDUbefore_int_Variable_values.put(_parameters, Boolean.valueOf(computeDUbefore_int_Variable_value));
    return computeDUbefore_int_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean computeDUbefore_compute(int childIndex, Variable v) {
    if(childIndex == 0) return isDUbefore(v);
    int index = childIndex-1;
    while(index > 0 && getInit(index).isConstant())
      index--;
    return getInit(childIndex-1).isDUafter(v);
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:265
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
  private TypeDecl type_compute() {  return declType();  }
  /**
   * @apilevel internal
   */
  protected boolean declType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl declType_value;
  /**
   * @attribute inh
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:255
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl declType() {
    if(declType_computed) {
      return declType_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    declType_value = getParent().Define_TypeDecl_declType(this, null);
      if(isFinal && num == state().boundariesCrossed) declType_computed = true;
    return declType_value;
  }
  /**
   * @attribute inh
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:61
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl expectedType() {
    ASTNode$State state = state();
    TypeDecl expectedType_value = getParent().Define_TypeDecl_expectedType(this, null);
    return expectedType_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:42
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getInitListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return true;
  }
    else {      return getParent().Define_boolean_isSource(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:500
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getInitListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return computeDABefore(childIndex, v);
  }
    else {      return getParent().Define_boolean_isDAbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:883
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getInitListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return computeDUbefore(childIndex, v);
  }
    else {      return getParent().Define_boolean_isDUbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:263
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
    if(caller == getInitListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return declType().componentType();
  }
    else {      return getParent().Define_TypeDecl_declType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:37
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
    if(caller == getInitListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return declType().componentType();
  }
    else {      return getParent().Define_TypeDecl_assignConvertedType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:70
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
    if(caller == getInitListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return expectedType().componentType();
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
