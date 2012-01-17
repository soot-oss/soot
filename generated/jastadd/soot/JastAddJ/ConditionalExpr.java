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
 * @declaredat java.ast:185
 */
public class ConditionalExpr extends Expr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    constant_computed = false;
    constant_value = null;
    isConstant_computed = false;
    booleanOperator_computed = false;
    type_computed = false;
    type_value = null;
    else_branch_label_computed = false;
    else_branch_label_value = null;
    then_branch_label_computed = false;
    then_branch_label_value = null;
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
  public ConditionalExpr clone() throws CloneNotSupportedException {
    ConditionalExpr node = (ConditionalExpr)super.clone();
    node.constant_computed = false;
    node.constant_value = null;
    node.isConstant_computed = false;
    node.booleanOperator_computed = false;
    node.type_computed = false;
    node.type_value = null;
    node.else_branch_label_computed = false;
    node.else_branch_label_value = null;
    node.then_branch_label_computed = false;
    node.then_branch_label_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConditionalExpr copy() {
      try {
        ConditionalExpr node = (ConditionalExpr)clone();
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
  public ConditionalExpr fullCopy() {
    ConditionalExpr res = (ConditionalExpr)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:427
   */
  public void toString(StringBuffer s) {
    getCondition().toString(s);
    s.append(" ? ");
    getTrueExpr().toString(s);
    s.append(" : ");
    getFalseExpr().toString(s);
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:562
   */
  public void typeCheck() {
    if(!getCondition().type().isBoolean())
      error("*** First expression must be a boolean in conditional operator");
    if(type().isUnknown() && !getTrueExpr().type().isUnknown() && !getFalseExpr().type().isUnknown()) {
      error("*** Operands in conditional operator does not match"); 
    }
  }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:119
   */
  public soot.Value eval(Body b) {
    b.setLine(this);
    if(type().isBoolean())
      return emitBooleanCondition(b);
    else {
      Local result = b.newTemp(type().getSootType());
      soot.jimple.Stmt endBranch = newLabel();
      getCondition().emitEvalBranch(b);
      if(getCondition().canBeTrue()) {
        b.addLabel(then_branch_label());
        b.add(b.newAssignStmt(result,
          getTrueExpr().type().emitCastTo(b,
            getTrueExpr(),
            type()
          ),
          this
        ));
        if(getCondition().canBeFalse()) {
          b.add(b.newGotoStmt(endBranch, this));
        }
      }
      if(getCondition().canBeFalse()) {
        b.addLabel(else_branch_label());
        b.add(b.newAssignStmt(result,
          getFalseExpr().type().emitCastTo(b,
            getFalseExpr(),
            type()
          ),
          this
        ));
      }
      b.addLabel(endBranch);
      return result;
    }
  }
  /**
   * @ast method 
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:209
   */
  public void emitEvalBranch(Body b) {
    b.setLine(this);
    soot.jimple.Stmt endBranch = newLabel();
    getCondition().emitEvalBranch(b);
    b.addLabel(then_branch_label());
    if(getCondition().canBeTrue()) {
      getTrueExpr().emitEvalBranch(b);
      b.add(b.newGotoStmt(true_label(), this));
    }  
    b.addLabel(else_branch_label());
    if(getCondition().canBeFalse()) {
      getFalseExpr().emitEvalBranch(b);
      b.add(b.newGotoStmt(true_label(), this));
    }
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public ConditionalExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public ConditionalExpr(Expr p0, Expr p1, Expr p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setChild(p2, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:15
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:21
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Condition
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setCondition(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Condition
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getCondition() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getConditionNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for TrueExpr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setTrueExpr(Expr node) {
    setChild(node, 1);
  }
  /**
   * Getter for TrueExpr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getTrueExpr() {
    return (Expr)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getTrueExprNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * Setter for FalseExpr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setFalseExpr(Expr node) {
    setChild(node, 2);
  }
  /**
   * Getter for FalseExpr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getFalseExpr() {
    return (Expr)getChild(2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getFalseExprNoTransform() {
    return (Expr)getChildNoTransform(2);
  }
  /**
   * @ast method 
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:364
   */
  private TypeDecl refined_TypeAnalysis_ConditionalExpr_type()
{
    TypeDecl trueType = getTrueExpr().type();
    TypeDecl falseType = getFalseExpr().type();
    
    if(trueType == falseType) return trueType;
    
    if(trueType.isNumericType() && falseType.isNumericType()) {
      if(trueType.isByte() && falseType.isShort()) return falseType;
      if(trueType.isShort() && falseType.isByte()) return trueType;
      if((trueType.isByte() || trueType.isShort() || trueType.isChar()) && 
         falseType.isInt() && getFalseExpr().isConstant() && getFalseExpr().representableIn(trueType))
        return trueType;
      if((falseType.isByte() || falseType.isShort() || falseType.isChar()) && 
         trueType.isInt() && getTrueExpr().isConstant() && getTrueExpr().representableIn(falseType))
        return falseType;
      return trueType.binaryNumericPromotion(falseType);
    }
    else if(trueType.isBoolean() && falseType.isBoolean()) {
      return trueType;
    }
    else if(trueType.isReferenceType() && falseType.isNull()) {
      return trueType;
    }
    else if(trueType.isNull() && falseType.isReferenceType()) {
      return falseType;
    }
    else if(trueType.isReferenceType() && falseType.isReferenceType()) {
      if(trueType.assignConversionTo(falseType, null))
        return falseType;
      if(falseType.assignConversionTo(trueType, null))
        return trueType;
      return unknownType();
    }
    else
      return unknownType();
  }
  /**
   * @ast method 
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:181
   */
  private TypeDecl refined_AutoBoxing_ConditionalExpr_type()
{
    TypeDecl trueType = getTrueExpr().type();
    TypeDecl falseType = getFalseExpr().type();
    if(trueType.isBoolean() && falseType.isBoolean()) {
      if(trueType == falseType)
        return trueType;
      if(trueType.isReferenceType())
        return trueType.unboxed();
      return trueType;
    }
    return refined_TypeAnalysis_ConditionalExpr_type();
  }
  /**
   * @apilevel internal
   */
  protected boolean constant_computed = false;
  /**
   * @apilevel internal
   */
  protected Constant constant_value;
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:132
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant constant() {
    if(constant_computed) {
      return constant_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    constant_value = constant_compute();
if(isFinal && num == state().boundariesCrossed) constant_computed = true;
    return constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant constant_compute() {  return type().questionColon(getCondition().constant(), getTrueExpr().constant(),getFalseExpr().constant());  }
  /**
   * @apilevel internal
   */
  protected boolean isConstant_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_value;
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:493
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isConstant() {
    if(isConstant_computed) {
      return isConstant_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isConstant_value = isConstant_compute();
if(isFinal && num == state().boundariesCrossed) isConstant_computed = true;
    return isConstant_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isConstant_compute() {  return getCondition().isConstant() && getTrueExpr().isConstant() && getFalseExpr().isConstant();  }
  /**
   * @apilevel internal
   */
  protected boolean booleanOperator_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean booleanOperator_value;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:230
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean booleanOperator() {
    if(booleanOperator_computed) {
      return booleanOperator_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    booleanOperator_value = booleanOperator_compute();
if(isFinal && num == state().boundariesCrossed) booleanOperator_computed = true;
    return booleanOperator_value;
  }
  /**
   * @apilevel internal
   */
  private boolean booleanOperator_compute() {  return getTrueExpr().type().isBoolean() && getFalseExpr().type().isBoolean();  }
  /*eq Stmt.isDAafter(Variable v) {
    //System.out.println("### isDAafter reached in " + getClass().getName());
    //throw new NullPointerException();
    throw new Error("Can not compute isDAafter for " + getClass().getName() + " at " + errorPrefix());
  }* @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:383
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
  private boolean isDAafterTrue_compute(Variable v) {  return (getTrueExpr().isDAafterTrue(v) && getFalseExpr().isDAafterTrue(v)) || isFalse();  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:384
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
  private boolean isDAafterFalse_compute(Variable v) {  return (getTrueExpr().isDAafterFalse(v) && getFalseExpr().isDAafterFalse(v)) || isTrue();  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:388
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
  private boolean isDAafter_compute(Variable v) {  return booleanOperator() ? isDAafterTrue(v) && isDAafterFalse(v) : getTrueExpr().isDAafter(v) && getFalseExpr().isDAafter(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:818
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
  private boolean isDUafterTrue_compute(Variable v) {  return getTrueExpr().isDUafterTrue(v) && getFalseExpr().isDUafterTrue(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:819
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
  private boolean isDUafterFalse_compute(Variable v) {  return getTrueExpr().isDUafterFalse(v) && getFalseExpr().isDUafterFalse(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:823
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
  private boolean isDUafter_compute(Variable v) {  return booleanOperator() ? isDUafterTrue(v) && isDUafterFalse(v) : getTrueExpr().isDUafter(v) && getFalseExpr().isDUafter(v);  }
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
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:109
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
    TypeDecl type = refined_AutoBoxing_ConditionalExpr_type();
    TypeDecl trueType = getTrueExpr().type();
    TypeDecl falseType = getFalseExpr().type();

    if(type.isUnknown() && (trueType.isReferenceType() || falseType.isReferenceType())) {
      if(!trueType.isReferenceType() && !trueType.boxed().isUnknown())
        trueType = trueType.boxed();
      if(!falseType.isReferenceType() && !falseType.boxed().isUnknown())
        falseType = falseType.boxed();
      if(trueType.isReferenceType() && falseType.isReferenceType()) {
        ArrayList list = new ArrayList();
        list.add(trueType);
        list.add(falseType);
        return type.lookupLUBType(list);
      }
    }
    return type;
  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:28
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:87
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
  private boolean canBeTrue_compute() {  return type().isBoolean() && (getTrueExpr().canBeTrue() && getFalseExpr().canBeTrue() 
    || getCondition().isTrue() && getTrueExpr().canBeTrue()
    || getCondition().isFalse() && getFalseExpr().canBeTrue());  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:97
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
  private boolean canBeFalse_compute() {  return type().isBoolean() && (getTrueExpr().canBeFalse() && getFalseExpr().canBeFalse() 
    || getCondition().isTrue() && getTrueExpr().canBeFalse()
    || getCondition().isFalse() && getFalseExpr().canBeFalse());  }
  /**
   * @apilevel internal
   */
  protected boolean else_branch_label_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt else_branch_label_value;
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:155
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt else_branch_label() {
    if(else_branch_label_computed) {
      return else_branch_label_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    else_branch_label_value = else_branch_label_compute();
if(isFinal && num == state().boundariesCrossed) else_branch_label_computed = true;
    return else_branch_label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt else_branch_label_compute() {  return newLabel();  }
  /**
   * @apilevel internal
   */
  protected boolean then_branch_label_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt then_branch_label_value;
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:156
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt then_branch_label() {
    if(then_branch_label_computed) {
      return then_branch_label_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    then_branch_label_value = then_branch_label_compute();
if(isFinal && num == state().boundariesCrossed) then_branch_label_computed = true;
    return then_branch_label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt then_branch_label_compute() {  return newLabel();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:387
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getFalseExprNoTransform()) {
      return getCondition().isDAafterFalse(v);
    }
    if(caller == getTrueExprNoTransform()) {
      return getCondition().isDAafterTrue(v);
    }
    if(caller == getConditionNoTransform()) {
      return isDAbefore(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:822
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getFalseExprNoTransform()) {
      return getCondition().isDUafterFalse(v);
    }
    if(caller == getTrueExprNoTransform()) {
      return getCondition().isDUafterTrue(v);
    }
    if(caller == getConditionNoTransform()) {
      return isDUbefore(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:64
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
    if(caller == getFalseExprNoTransform()) {
      return false_label();
    }
    if(caller == getTrueExprNoTransform()) {
      return false_label();
    }
    if(caller == getConditionNoTransform()) {
      return else_branch_label();
    }
    return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:65
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
    if(caller == getFalseExprNoTransform()) {
      return true_label();
    }
    if(caller == getTrueExprNoTransform()) {
      return true_label();
    }
    if(caller == getConditionNoTransform()) {
      return then_branch_label();
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
