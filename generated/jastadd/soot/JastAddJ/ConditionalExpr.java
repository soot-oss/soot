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
 * @production ConditionalExpr : {@link Expr} ::= <span class="component">Condition:{@link Expr}</span> <span class="component">TrueExpr:{@link Expr}</span> <span class="component">FalseExpr:{@link Expr}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:188
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
      ConditionalExpr node = (ConditionalExpr) clone();
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
  public ConditionalExpr fullCopy() {
    ConditionalExpr tree = (ConditionalExpr) copy();
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
      error("The first operand of a conditional expression must be a boolean");
    if(type().isUnknown() && !getTrueExpr().type().isUnknown() && !getFalseExpr().type().isUnknown()) {
      error("The types of the second and third operand in this conditional expression do not match"); 
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
   * 
   */
  public ConditionalExpr() {
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
    children = new ASTNode[3];
  }
  /**
   * @ast method 
   * 
   */
  public ConditionalExpr(Expr p0, Expr p1, Expr p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setChild(p2, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 3;
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
   * Replaces the Condition child.
   * @param node The new node to replace the Condition child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setCondition(Expr node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Condition child.
   * @return The current node used as the Condition child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getCondition() {
    return (Expr)getChild(0);
  }
  /**
   * Retrieves the Condition child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Condition child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getConditionNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Replaces the TrueExpr child.
   * @param node The new node to replace the TrueExpr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTrueExpr(Expr node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the TrueExpr child.
   * @return The current node used as the TrueExpr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getTrueExpr() {
    return (Expr)getChild(1);
  }
  /**
   * Retrieves the TrueExpr child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the TrueExpr child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getTrueExprNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * Replaces the FalseExpr child.
   * @param node The new node to replace the FalseExpr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setFalseExpr(Expr node) {
    setChild(node, 2);
  }
  /**
   * Retrieves the FalseExpr child.
   * @return The current node used as the FalseExpr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getFalseExpr() {
    return (Expr)getChild(2);
  }
  /**
   * Retrieves the FalseExpr child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the FalseExpr child.
   * @apilevel low-level
   * @ast method 
   * 
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:125
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:347
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:332
   */
  public boolean isDAafterTrue(Variable v) {
    ASTNode$State state = state();
    try {  return (getTrueExpr().isDAafterTrue(v) && getFalseExpr().isDAafterTrue(v)) || isFalse();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:333
   */
  public boolean isDAafterFalse(Variable v) {
    ASTNode$State state = state();
    try {  return (getTrueExpr().isDAafterFalse(v) && getFalseExpr().isDAafterFalse(v)) || isTrue();  }
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
    try {  return booleanOperator() ? isDAafterTrue(v) && isDAafterFalse(v) : getTrueExpr().isDAafter(v) && getFalseExpr().isDAafter(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:702
   */
  public boolean isDUafterTrue(Variable v) {
    ASTNode$State state = state();
    try {  return getTrueExpr().isDUafterTrue(v) && getFalseExpr().isDUafterTrue(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:703
   */
  public boolean isDUafterFalse(Variable v) {
    ASTNode$State state = state();
    try {  return getTrueExpr().isDUafterFalse(v) && getFalseExpr().isDUafterFalse(v);  }
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
    try {  return booleanOperator() ? isDUafterTrue(v) && isDUafterFalse(v) : getTrueExpr().isDUafter(v) && getFalseExpr().isDUafter(v);  }
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
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:129
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

    if(type.isUnknown()) {
      if(!trueType.isReferenceType() && !trueType.boxed().isUnknown())
        trueType = trueType.boxed();
      if(!falseType.isReferenceType() && !falseType.boxed().isUnknown())
        falseType = falseType.boxed();

      ArrayList list = new ArrayList();
      list.add(trueType);
      list.add(falseType);
      return type.lookupLUBType(list);
    }
    return type;
  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:21
   */
  public boolean definesLabel() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:82
   */
  public boolean canBeTrue() {
    ASTNode$State state = state();
    try {  return type().isBoolean() && (getTrueExpr().canBeTrue() && getFalseExpr().canBeTrue() 
    || getCondition().isTrue() && getTrueExpr().canBeTrue()
    || getCondition().isFalse() && getFalseExpr().canBeTrue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:92
   */
  public boolean canBeFalse() {
    ASTNode$State state = state();
    try {  return type().isBoolean() && (getTrueExpr().canBeFalse() && getFalseExpr().canBeFalse() 
    || getCondition().isTrue() && getTrueExpr().canBeFalse()
    || getCondition().isFalse() && getFalseExpr().canBeFalse());  }
    finally {
    }
  }
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
    else if(caller == getTrueExprNoTransform()) {
      return getCondition().isDAafterTrue(v);
    }
    else if(caller == getConditionNoTransform()) {
      return isDAbefore(v);
    }
    else {      return getParent().Define_boolean_isDAbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:823
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getFalseExprNoTransform()) {
      return getCondition().isDUafterFalse(v);
    }
    else if(caller == getTrueExprNoTransform()) {
      return getCondition().isDUafterTrue(v);
    }
    else if(caller == getConditionNoTransform()) {
      return isDUbefore(v);
    }
    else {      return getParent().Define_boolean_isDUbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:64
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
    if(caller == getFalseExprNoTransform()) {
      return false_label();
    }
    else if(caller == getTrueExprNoTransform()) {
      return false_label();
    }
    else if(caller == getConditionNoTransform()) {
      return else_branch_label();
    }
    else {      return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:65
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
    if(caller == getFalseExprNoTransform()) {
      return true_label();
    }
    else if(caller == getTrueExprNoTransform()) {
      return true_label();
    }
    else if(caller == getConditionNoTransform()) {
      return then_branch_label();
    }
    else {      return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
