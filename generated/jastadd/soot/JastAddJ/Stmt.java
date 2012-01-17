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
 * @declaredat java.ast:192
 */
public abstract class Stmt extends ASTNode<ASTNode> implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
    canCompleteNormally_computed = false;
    localNum_computed = false;
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
  public Stmt clone() throws CloneNotSupportedException {
    Stmt node = (Stmt)super.clone();
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.canCompleteNormally_computed = false;
    node.localNum_computed = false;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:14
   */
  void checkUnreachableStmt() {
    if(!reachable() && reportUnreachable())
      error("statement is unreachable");
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:12
   */
  public void jimplify2(Body b) {
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public Stmt() {
    super();


  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:10
   */
  protected int numChildren() {
    return 0;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:16
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:325
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
  private boolean isDAafter_compute(Variable v) {  return isDAbefore(v);  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:776
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
  private boolean isDUafter_compute(Variable v) {
    throw new Error("isDUafter in " + getClass().getName());
  }
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:129
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean declaresVariable(String name) {
      ASTNode$State state = state();
    boolean declaresVariable_String_value = declaresVariable_compute(name);
    return declaresVariable_String_value;
  }
  /**
   * @apilevel internal
   */
  private boolean declaresVariable_compute(String name) {  return false;  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:396
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean continueLabel() {
      ASTNode$State state = state();
    boolean continueLabel_value = continueLabel_compute();
    return continueLabel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean continueLabel_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:762
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean addsIndentationLevel() {
      ASTNode$State state = state();
    boolean addsIndentationLevel_value = addsIndentationLevel_compute();
    return addsIndentationLevel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean addsIndentationLevel_compute() {  return true;  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:29
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
  private boolean canCompleteNormally_compute() {  return true;  }
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:200
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt break_label() {
      ASTNode$State state = state();
    soot.jimple.Stmt break_label_value = break_label_compute();
    return break_label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt break_label_compute() {
    throw new UnsupportedOperationException("Can not break at this statement of type " + getClass().getName());
  }
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:225
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt continue_label() {
      ASTNode$State state = state();
    soot.jimple.Stmt continue_label_value = continue_label_compute();
    return continue_label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt continue_label_compute() {
    throw new UnsupportedOperationException("Can not continue at this statement");
  }
  /**
   * @attribute inh
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:232
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAbefore(Variable v) {
      ASTNode$State state = state();
    boolean isDAbefore_Variable_value = getParent().Define_boolean_isDAbefore(this, null, v);
    return isDAbefore_Variable_value;
  }
  /**
   * @attribute inh
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:690
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUbefore(Variable v) {
      ASTNode$State state = state();
    boolean isDUbefore_Variable_value = getParent().Define_boolean_isDUbefore(this, null, v);
    return isDUbefore_Variable_value;
  }
  /**
   * @attribute inh
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:24
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection lookupMethod(String name) {
      ASTNode$State state = state();
    Collection lookupMethod_String_value = getParent().Define_Collection_lookupMethod(this, null, name);
    return lookupMethod_String_value;
  }
  /**
   * @attribute inh
   * @aspect LookupFullyQualifiedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:96
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupType(String packageName, String typeName) {
      ASTNode$State state = state();
    TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName, typeName);
    return lookupType_String_String_value;
  }
  /**
   * @attribute inh
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:174
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupType(String name) {
      ASTNode$State state = state();
    SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
    return lookupType_String_value;
  }
  /**
   * @attribute inh
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:16
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupVariable(String name) {
      ASTNode$State state = state();
    SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
    return lookupVariable_String_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:512
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl enclosingBodyDecl() {
      ASTNode$State state = state();
    BodyDecl enclosingBodyDecl_value = getParent().Define_BodyDecl_enclosingBodyDecl(this, null);
    return enclosingBodyDecl_value;
  }
  /**
   * @attribute inh
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:584
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl hostType() {
      ASTNode$State state = state();
    TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
    return hostType_value;
  }
  /**
   * @attribute inh
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:27
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean reachable() {
      ASTNode$State state = state();
    boolean reachable_value = getParent().Define_boolean_reachable(this, null);
    return reachable_value;
  }
  /**
   * @attribute inh
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:147
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean reportUnreachable() {
      ASTNode$State state = state();
    boolean reportUnreachable_value = getParent().Define_boolean_reportUnreachable(this, null);
    return reportUnreachable_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean localNum_computed = false;
  /**
   * @apilevel internal
   */
  protected int localNum_value;
  /**
   * @attribute inh
   * @aspect LocalNum
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/LocalNum.jrag:12
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int localNum() {
    if(localNum_computed) {
      return localNum_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    localNum_value = getParent().Define_int_localNum(this, null);
if(isFinal && num == state().boundariesCrossed) localNum_computed = true;
    return localNum_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:352
   * @apilevel internal
   */
  public String Define_String_typeDeclIndent(ASTNode caller, ASTNode child) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return indent();
    }
    return getParent().Define_String_typeDeclIndent(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
