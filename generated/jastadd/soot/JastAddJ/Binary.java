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
 * @production Binary : {@link Expr} ::= <span class="component">LeftOperand:{@link Expr}</span> <span class="component">RightOperand:{@link Expr}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:150
 */
public abstract class Binary extends Expr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isConstant_visited = -1;
    isConstant_computed = false;
    isConstant_initialized = false;
    isDAafterTrue_Variable_values = null;
    isDAafterFalse_Variable_values = null;
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
    isDUbefore_Variable_values = null;
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
  public Binary clone() throws CloneNotSupportedException {
    Binary node = (Binary)super.clone();
    node.isConstant_visited = -1;
    node.isConstant_computed = false;
    node.isConstant_initialized = false;
    node.isDAafterTrue_Variable_values = null;
    node.isDAafterFalse_Variable_values = null;
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.isDUbefore_Variable_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:394
   */
  public void toString(StringBuffer s) {
    getLeftOperand().toString(s);
    s.append(printOp());
    getRightOperand().toString(s);
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:772
   */
  public soot.Value eval(Body b) {
    return asLocal(b, emitOperation(b, 
      getLeftOperand().type().emitCastTo(b,  // Binary numeric promotion
        getLeftOperand(),
        type()
      ),
      getRightOperand().type().emitCastTo(b, // Binary numeric promotion
        getRightOperand(),
        type()
      )
    ));
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:785
   */
  public soot.Value emitShiftExpr(Body b) {
    return asLocal(b, emitOperation(b, 
      getLeftOperand().type().emitCastTo(b,  // Binary numeric promotion
        getLeftOperand(),
        type()
      ),
      getRightOperand().type().emitCastTo(b,
        getRightOperand(),
        typeInt()
      )
    ));
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:802
   */
  public soot.Value emitOperation(Body b, soot.Value left, soot.Value right) {
    throw new Error("emitOperation not implemented in " + getClass().getName());
  }
  /**
   * @ast method 
   * 
   */
  public Binary() {
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
  }
  /**
   * @ast method 
   * 
   */
  public Binary(Expr p0, Expr p1) {
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
   * Replaces the LeftOperand child.
   * @param node The new node to replace the LeftOperand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setLeftOperand(Expr node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the LeftOperand child.
   * @return The current node used as the LeftOperand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getLeftOperand() {
    return (Expr)getChild(0);
  }
  /**
   * Retrieves the LeftOperand child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the LeftOperand child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getLeftOperandNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Replaces the RightOperand child.
   * @param node The new node to replace the RightOperand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setRightOperand(Expr node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the RightOperand child.
   * @return The current node used as the RightOperand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getRightOperand() {
    return (Expr)getChild(1);
  }
  /**
   * Retrieves the RightOperand child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the RightOperand child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getRightOperandNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @ast method 
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:370
   */
  private TypeDecl refined_ConstantExpression_Binary_binaryNumericPromotedType()
{
    TypeDecl leftType = left().type();
    TypeDecl rightType = right().type();
    if(leftType.isString())
      return leftType;
    if(rightType.isString())
      return rightType;
    if(leftType.isNumericType() && rightType.isNumericType())
      return leftType.binaryNumericPromotion(rightType);
    if(leftType.isBoolean() && rightType.isBoolean())
      return leftType;
    return unknownType();
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:400
   */
  @SuppressWarnings({"unchecked", "cast"})
  public abstract String printOp();
  /**
   * @apilevel internal
   */
  protected int isConstant_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_value;
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:345
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isConstant() {
    if(isConstant_computed) {
      return isConstant_value;
    }
    ASTNode$State state = state();
    if (!isConstant_initialized) {
      isConstant_initialized = true;
      isConstant_value = false;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        isConstant_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_isConstant_value = isConstant_compute();
        if (new_isConstant_value!=isConstant_value)
          state.CHANGE = true;
        isConstant_value = new_isConstant_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(isFinal && num == state().boundariesCrossed) {
      isConstant_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      isConstant_compute();
      state.RESET_CYCLE = false;
        isConstant_computed = false;
        isConstant_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return isConstant_value;
    }
    if(isConstant_visited != state.CIRCLE_INDEX) {
      isConstant_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        isConstant_computed = false;
        isConstant_initialized = false;
        isConstant_visited = -1;
        return isConstant_value;
      }
      boolean new_isConstant_value = isConstant_compute();
      if (new_isConstant_value!=isConstant_value)
        state.CHANGE = true;
      isConstant_value = new_isConstant_value; 
      return isConstant_value;
    }
    return isConstant_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isConstant_compute() {  return getLeftOperand().isConstant() && getRightOperand().isConstant();  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:368
   */
  public Expr left() {
    ASTNode$State state = state();
    try {  return getLeftOperand();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:369
   */
  public Expr right() {
    ASTNode$State state = state();
    try {  return getRightOperand();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:370
   */
  public TypeDecl binaryNumericPromotedType() {
    ASTNode$State state = state();
    try {
    TypeDecl leftType = left().type();
    TypeDecl rightType = right().type();
    if(leftType.isBoolean() && rightType.isBoolean()) {
      return leftType.isReferenceType() ? leftType.unboxed() : leftType;
    }
    return refined_ConstantExpression_Binary_binaryNumericPromotedType();
  }
    finally {
    }
  }
  protected java.util.Map isDAafterTrue_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:401
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterTrue(Variable v) {
    Object _parameters = v;
    if(isDAafterTrue_Variable_values == null) isDAafterTrue_Variable_values = new java.util.HashMap(4);
    if(isDAafterTrue_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafterTrue_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafterTrue_Variable_value = isDAafterTrue_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDAafterTrue_Variable_values.put(_parameters, Boolean.valueOf(isDAafterTrue_Variable_value));
    return isDAafterTrue_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterTrue_compute(Variable v) {  return getRightOperand().isDAafter(v) || isFalse();  }
  protected java.util.Map isDAafterFalse_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:402
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterFalse(Variable v) {
    Object _parameters = v;
    if(isDAafterFalse_Variable_values == null) isDAafterFalse_Variable_values = new java.util.HashMap(4);
    if(isDAafterFalse_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafterFalse_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafterFalse_Variable_value = isDAafterFalse_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDAafterFalse_Variable_values.put(_parameters, Boolean.valueOf(isDAafterFalse_Variable_value));
    return isDAafterFalse_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterFalse_compute(Variable v) {  return getRightOperand().isDAafter(v) || isTrue();  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:404
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
    Object _parameters = v;
    if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
    if(isDAafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {  return getRightOperand().isDAafter(v);  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:845
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
    Object _parameters = v;
    if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
    if(isDUafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {  return getRightOperand().isDUafter(v);  }
  protected java.util.Map isDUbefore_Variable_values;
  /**
   * @attribute inh
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:696
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUbefore(Variable v) {
    Object _parameters = v;
    if(isDUbefore_Variable_values == null) isDUbefore_Variable_values = new java.util.HashMap(4);
    if(isDUbefore_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDUbefore_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDUbefore_Variable_value = getParent().Define_boolean_isDUbefore(this, null, v);
      if(isFinal && num == state().boundariesCrossed) isDUbefore_Variable_values.put(_parameters, Boolean.valueOf(isDUbefore_Variable_value));
    return isDUbefore_Variable_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:405
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getRightOperandNoTransform()) {
      return getLeftOperand().isDAafter(v);
    }
    else {      return getParent().Define_boolean_isDAbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:846
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getRightOperandNoTransform()) {
      return getLeftOperand().isDUafter(v);
    }
    else {      return getParent().Define_boolean_isDUbefore(this, caller, v);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
