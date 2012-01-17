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
 * @declaredat java.ast:139
 */
public class LogNotExpr extends Unary implements Cloneable {
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
  public LogNotExpr clone() throws CloneNotSupportedException {
    LogNotExpr node = (LogNotExpr)super.clone();
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
  public LogNotExpr copy() {
      try {
        LogNotExpr node = (LogNotExpr)clone();
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
  public LogNotExpr fullCopy() {
    LogNotExpr res = (LogNotExpr)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:287
   */
  public void typeCheck() {
    if(!getOperand().type().isBoolean())
      error("unary ! only operates on boolean types");
  }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:103
   */
  public soot.Value eval(Body b) { return emitBooleanCondition(b); }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:182
   */
  public void emitEvalBranch(Body b)  { getOperand().emitEvalBranch(b); }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public LogNotExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public LogNotExpr(Expr p0) {
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
   * Setter for Operand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setOperand(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Operand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getOperand() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getOperandNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:490
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isConstant() {
      ASTNode$State state = state();
    boolean isConstant_value = isConstant_compute();
    return isConstant_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isConstant_compute() {  return getOperand().isConstant();  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:530
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
  private Constant constant_compute() {  return Constant.create(!getOperand().constant().booleanValue());  }
  /*eq Stmt.isDAafter(Variable v) {
    //System.out.println("### isDAafter reached in " + getClass().getName());
    //throw new NullPointerException();
    throw new Error("Can not compute isDAafter for " + getClass().getName() + " at " + errorPrefix());
  }* @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:378
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterTrue(Variable v) {
      ASTNode$State state = state();
    boolean isDAafterTrue_Variable_value = isDAafterTrue_compute(v);
    return isDAafterTrue_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterTrue_compute(Variable v) {  return getOperand().isDAafterFalse(v) || isFalse();  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:379
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterFalse(Variable v) {
      ASTNode$State state = state();
    boolean isDAafterFalse_Variable_value = isDAafterFalse_compute(v);
    return isDAafterFalse_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterFalse_compute(Variable v) {  return getOperand().isDAafterTrue(v) || isTrue();  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:381
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
      ASTNode$State state = state();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {  return isDAafterTrue(v) && isDAafterFalse(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:813
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
  private boolean isDUafterTrue_compute(Variable v) {  return getOperand().isDUafterFalse(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:814
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
  private boolean isDUafterFalse_compute(Variable v) {  return getOperand().isDUafterTrue(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:816
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
      ASTNode$State state = state();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {  return isDUafterTrue(v) && isDUafterFalse(v);  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:382
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String printPreOp() {
      ASTNode$State state = state();
    String printPreOp_value = printPreOp_compute();
    return printPreOp_value;
  }
  /**
   * @apilevel internal
   */
  private String printPreOp_compute() {  return "!";  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:318
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
  private TypeDecl type_compute() {  return typeBoolean();  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:27
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean definesLabel() {
      ASTNode$State state = state();
    boolean definesLabel_value = definesLabel_compute();
    return definesLabel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean definesLabel_compute() {  return true;  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:90
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
  private boolean canBeTrue_compute() {  return getOperand().canBeFalse();  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:100
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
  private boolean canBeFalse_compute() {  return getOperand().canBeTrue();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:380
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getOperandNoTransform()) {
      return isDAbefore(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:815
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getOperandNoTransform()) {
      return isDUbefore(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:57
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
    if(caller == getOperandNoTransform()) {
      return true_label();
    }
    return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:58
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
    if(caller == getOperandNoTransform()) {
      return false_label();
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
