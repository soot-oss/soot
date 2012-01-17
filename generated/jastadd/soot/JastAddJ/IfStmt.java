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
 * @declaredat java.ast:204
 */
public class IfStmt extends Stmt implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
    canCompleteNormally_computed = false;
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
  public IfStmt clone() throws CloneNotSupportedException {
    IfStmt node = (IfStmt)super.clone();
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.canCompleteNormally_computed = false;
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
  public IfStmt copy() {
      try {
        IfStmt node = (IfStmt)clone();
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
  public IfStmt fullCopy() {
    IfStmt res = (IfStmt)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:66
   */
  public IfStmt(Expr cond, Stmt thenBranch) {
    this(cond, thenBranch, new Opt());
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:70
   */
  public IfStmt(Expr cond, Stmt thenBranch, Stmt elseBranch) {
    this(cond, thenBranch, new Opt(elseBranch));
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:574
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("if(");
    getCondition().toString(s);
    s.append(") ");
    getThen().toString(s);
    if(hasElse()) {
      s.append(indent());
      s.append("else ");
      getElse().toString(s);
    }
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:316
   */
  public void typeCheck() {
    TypeDecl cond = getCondition().type();
    if(!cond.isBoolean()) {
      error("the type of \"" + getCondition() + "\" is " + cond.name() + " which is not boolean");
    }
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:115
   */
  public void jimplify2(Body b) {
    soot.jimple.Stmt endBranch = newLabel();
    if(getCondition().isConstant()) {
      if(getCondition().isTrue())
        getThen().jimplify2(b);
      else if(getCondition().isFalse() && hasElse())
        getElse().jimplify2(b);
    }
    else {
      soot.jimple.Stmt elseBranch = else_branch_label();
      soot.jimple.Stmt thenBranch = then_branch_label();
      getCondition().emitEvalBranch(b);
      b.addLabel(thenBranch);
      getThen().jimplify2(b);
      if(getThen().canCompleteNormally() && hasElse()) {
        b.setLine(this);
        b.add(b.newGotoStmt(endBranch, this));
      }
      b.addLabel(elseBranch);
      if(hasElse())
        getElse().jimplify2(b);
    }
    if(getThen().canCompleteNormally() && hasElse())
      b.addLabel(endBranch);
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public IfStmt() {
    super();

    setChild(new Opt(), 2);

  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public IfStmt(Expr p0, Stmt p1, Opt<Stmt> p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setChild(p2, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:16
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:22
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
   * Setter for Then
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setThen(Stmt node) {
    setChild(node, 1);
  }
  /**
   * Getter for Then
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Stmt getThen() {
    return (Stmt)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Stmt getThenNoTransform() {
    return (Stmt)getChildNoTransform(1);
  }
  /**
   * Setter for ElseOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setElseOpt(Opt<Stmt> opt) {
    setChild(opt, 2);
  }
  /**
   * Does this node have a Else child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasElse() {
    return getElseOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child Else
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Stmt getElse() {
    return (Stmt)getElseOpt().getChild(0);
  }
  /**
   * Setter for optional child Else
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setElse(Stmt node) {
    getElseOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Stmt> getElseOpt() {
    return (Opt<Stmt>)getChild(2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Stmt> getElseOptNoTransform() {
    return (Opt<Stmt>)getChildNoTransform(2);
  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:524
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
  private boolean isDAafter_compute(Variable v) {  return hasElse() ? getThen().isDAafter(v) && getElse().isDAafter(v) : getThen().isDAafter(v) && getCondition().isDAafterFalse(v);  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:993
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
  private boolean isDUafter_compute(Variable v) {  return hasElse() ? getThen().isDUafter(v) && getElse().isDUafter(v) : getThen().isDUafter(v) && getCondition().isDUafterFalse(v);  }
  /**
   * @apilevel internal
   */
  protected boolean canCompleteNormally_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean canCompleteNormally_value;
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:141
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean canCompleteNormally() {
    if(canCompleteNormally_computed) {
      return canCompleteNormally_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    canCompleteNormally_value = canCompleteNormally_compute();
if(isFinal && num == state().boundariesCrossed) canCompleteNormally_computed = true;
    return canCompleteNormally_value;
  }
  /**
   * @apilevel internal
   */
  private boolean canCompleteNormally_compute() {  return (reachable() && !hasElse()) || (getThen().canCompleteNormally() ||
    (hasElse() && getElse().canCompleteNormally()));  }
  /**
   * @attribute syn
   * @aspect BooleanExpressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:32
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
   * @apilevel internal
   */
  protected boolean else_branch_label_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt else_branch_label_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:113
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
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:114
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:527
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getElseOptNoTransform()) {
      return getCondition().isDAafterFalse(v);
    }
    if(caller == getThenNoTransform()) {
      return getCondition().isDAafterTrue(v);
    }
    if(caller == getConditionNoTransform()) {
      return isDAbefore(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:996
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getElseOptNoTransform()) {
      return getCondition().isDUafterFalse(v);
    }
    if(caller == getThenNoTransform()) {
      return getCondition().isDUafterTrue(v);
    }
    if(caller == getConditionNoTransform()) {
      return isDUbefore(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:144
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getElseOptNoTransform()) {
      return reachable();
    }
    if(caller == getThenNoTransform()) {
      return reachable();
    }
    return getParent().Define_boolean_reachable(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:150
   * @apilevel internal
   */
  public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
    if(caller == getElseOptNoTransform()) {
      return reachable();
    }
    if(caller == getThenNoTransform()) {
      return reachable();
    }
    return getParent().Define_boolean_reportUnreachable(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:38
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
    if(caller == getConditionNoTransform()) {
      return else_branch_label();
    }
    return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/BooleanExpressions.jrag:39
   * @apilevel internal
   */
  public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
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
