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
 * @declaredat java.ast:169
 */
public class AndLogicalExpr extends LogicalExpr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isDAafterTrue_Variable_values = null;
    isDAafterFalse_Variable_values = null;
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
    next_test_label_computed = false;
    next_test_label_value = null;
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
  public AndLogicalExpr clone() throws CloneNotSupportedException {
    AndLogicalExpr node = (AndLogicalExpr)super.clone();
    node.isDAafterTrue_Variable_values = null;
    node.isDAafterFalse_Variable_values = null;
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.next_test_label_computed = false;
    node.next_test_label_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AndLogicalExpr copy() {
      try {
        AndLogicalExpr node = (AndLogicalExpr)clone();
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
  public AndLogicalExpr fullCopy() {
    AndLogicalExpr res = (AndLogicalExpr)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:184
   */
  public void emitEvalBranch(Body b) {
    b.setLine(this);
    getLeftOperand().emitEvalBranch(b);
    b.addLabel(next_test_label());
    if(getLeftOperand().canBeTrue()) {
      getRightOperand().emitEvalBranch(b);
      if(getRightOperand().canBeTrue())
        b.add(b.newGotoStmt(true_label(), this));
    }
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public AndLogicalExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public AndLogicalExpr(Expr p0, Expr p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:14
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:20
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for LeftOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setLeftOperand(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for LeftOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getLeftOperand() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getLeftOperandNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for RightOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setRightOperand(Expr node) {
    setChild(node, 1);
  }
  /**
   * Getter for RightOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getRightOperand() {
    return (Expr)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getRightOperandNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:537
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant constant() {
      ASTNode$State state = state();
    Constant constant_value = constant_compute();
    return constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant constant_compute() {  return Constant.create(left().constant().booleanValue() && right().constant().booleanValue());  }
  protected java.util.Map isDAafterTrue_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:362
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
  private boolean isDAafterTrue_compute(Variable v) {  return getRightOperand().isDAafterTrue(v) || isFalse();  }
  protected java.util.Map isDAafterFalse_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:364
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
  private boolean isDAafterFalse_compute(Variable v) {  return (getLeftOperand().isDAafterFalse(v) && getRightOperand().isDAafterFalse(v)) || isTrue();  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:370
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
  private boolean isDAafter_compute(Variable v) {  return isDAafterTrue(v) && isDAafterFalse(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:801
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafterTrue(Variable v) {
      ASTNode$State state = state();
    boolean isDUafterTrue_Variable_value = isDUafterTrue_compute(v);
    return isDUafterTrue_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafterTrue_compute(Variable v) {  return getRightOperand().isDUafterTrue(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:802
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafterFalse(Variable v) {
      ASTNode$State state = state();
    boolean isDUafterFalse_Variable_value = isDUafterFalse_compute(v);
    return isDUafterFalse_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafterFalse_compute(Variable v) {  return getLeftOperand().isDUafterFalse(v) && getRightOperand().isDUafterFalse(v);  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:805
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
  private boolean isDUafter_compute(Variable v) {  return isDUafterTrue(v) && isDUafterFalse(v);  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:412
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String printOp() {
      ASTNode$State state = state();
    String printOp_value = printOp_compute();
    return printOp_value;
  }
  /**
   * @apilevel internal
   */
  private String printOp_compute() {  return " && ";  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:86
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean canBeTrue() {
      ASTNode$State state = state();
    boolean canBeTrue_value = canBeTrue_compute();
    return canBeTrue_value;
  }
  /**
   * @apilevel internal
   */
  private boolean canBeTrue_compute() {  return getLeftOperand().canBeTrue() && getRightOperand().canBeTrue();  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:96
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean canBeFalse() {
      ASTNode$State state = state();
    boolean canBeFalse_value = canBeFalse_compute();
    return canBeFalse_value;
  }
  /**
   * @apilevel internal
   */
  private boolean canBeFalse_compute() {  return getLeftOperand().canBeFalse() || getRightOperand().canBeFalse();  }
  /**
   * @apilevel internal
   */
  protected boolean next_test_label_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt next_test_label_value;
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:194
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt next_test_label() {
    if(next_test_label_computed) {
      return next_test_label_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    next_test_label_value = next_test_label_compute();
if(isFinal && num == state().boundariesCrossed) next_test_label_computed = true;
    return next_test_label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt next_test_label_compute() {  return newLabel();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:368
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getRightOperandNoTransform()) {
      return getLeftOperand().isDAafterTrue(v);
    }
    if(caller == getLeftOperandNoTransform()) {
      return isDAbefore(v);
    }
    return super.Define_boolean_isDAbefore(caller, child, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:804
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getRightOperandNoTransform()) {
      return getLeftOperand().isDUafterTrue(v);
    }
    if(caller == getLeftOperandNoTransform()) {
      return isDUbefore(v);
    }
    return super.Define_boolean_isDUbefore(caller, child, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:74
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
    if(caller == getRightOperandNoTransform()) {
      return false_label();
    }
    if(caller == getLeftOperandNoTransform()) {
      return false_label();
    }
    return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:75
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
    if(caller == getRightOperandNoTransform()) {
      return true_label();
    }
    if(caller == getLeftOperandNoTransform()) {
      return next_test_label();
    }
    return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
