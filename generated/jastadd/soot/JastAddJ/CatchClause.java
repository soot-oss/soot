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
 * Abstract superclass for catch clauses.
 * @ast node
 * @declaredat CatchClause.ast:4
 */
public abstract class CatchClause extends ASTNode<ASTNode> implements Cloneable, VariableScope {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    parameterDeclaration_String_values = null;
    typeThrowable_computed = false;
    typeThrowable_value = null;
    lookupVariable_String_values = null;
    reachableCatchClause_TypeDecl_values = null;
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
  public CatchClause clone() throws CloneNotSupportedException {
    CatchClause node = (CatchClause)super.clone();
    node.parameterDeclaration_String_values = null;
    node.typeThrowable_computed = false;
    node.typeThrowable_value = null;
    node.lookupVariable_String_values = null;
    node.reachableCatchClause_TypeDecl_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @declaredat CatchClause.ast:1
   */
  public CatchClause() {
    super();


  }
  /**
   * @ast method 
   * @declaredat CatchClause.ast:7
   */
  public CatchClause(Block p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat CatchClause.ast:13
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat CatchClause.ast:19
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Block
   * @apilevel high-level
   * @ast method 
   * @declaredat CatchClause.ast:5
   */
  public void setBlock(Block node) {
    setChild(node, 0);
  }
  /**
   * Getter for Block
   * @apilevel high-level
   * @ast method 
   * @declaredat CatchClause.ast:12
   */
  public Block getBlock() {
    return (Block)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat CatchClause.ast:18
   */
  public Block getBlockNoTransform() {
    return (Block)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:198
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean handles(TypeDecl exceptionType) {
      ASTNode$State state = state();
    boolean handles_TypeDecl_value = handles_compute(exceptionType);
    return handles_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean handles_compute(TypeDecl exceptionType) {  return false;  }
  protected java.util.Map parameterDeclaration_String_values;
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:111
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet parameterDeclaration(String name) {
    Object _parameters = name;
    if(parameterDeclaration_String_values == null) parameterDeclaration_String_values = new java.util.HashMap(4);
    if(parameterDeclaration_String_values.containsKey(_parameters)) {
      return (SimpleSet)parameterDeclaration_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet parameterDeclaration_String_value = parameterDeclaration_compute(name);
if(isFinal && num == state().boundariesCrossed) parameterDeclaration_String_values.put(_parameters, parameterDeclaration_String_value);
    return parameterDeclaration_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet parameterDeclaration_compute(String name) {  return SimpleSet.emptySet;  }
  /**
   * @apilevel internal
   */
  protected boolean typeThrowable_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeThrowable_value;
  /**
   * @attribute inh
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:68
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeThrowable() {
    if(typeThrowable_computed) {
      return typeThrowable_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeThrowable_value = getParent().Define_TypeDecl_typeThrowable(this, null);
if(isFinal && num == state().boundariesCrossed) typeThrowable_computed = true;
    return typeThrowable_value;
  }
  protected java.util.Map lookupVariable_String_values;
  /**
   * @attribute inh
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:20
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupVariable(String name) {
    Object _parameters = name;
    if(lookupVariable_String_values == null) lookupVariable_String_values = new java.util.HashMap(4);
    if(lookupVariable_String_values.containsKey(_parameters)) {
      return (SimpleSet)lookupVariable_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
if(isFinal && num == state().boundariesCrossed) lookupVariable_String_values.put(_parameters, lookupVariable_String_value);
    return lookupVariable_String_value;
  }
  protected java.util.Map reachableCatchClause_TypeDecl_values;
  /**
   * @return true if an exception of type exceptionType is catchable by the catch clause
   * @attribute inh
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:127
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean reachableCatchClause(TypeDecl exceptionType) {
    Object _parameters = exceptionType;
    if(reachableCatchClause_TypeDecl_values == null) reachableCatchClause_TypeDecl_values = new java.util.HashMap(4);
    if(reachableCatchClause_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)reachableCatchClause_TypeDecl_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean reachableCatchClause_TypeDecl_value = getParent().Define_boolean_reachableCatchClause(this, null, exceptionType);
if(isFinal && num == state().boundariesCrossed) reachableCatchClause_TypeDecl_values.put(_parameters, Boolean.valueOf(reachableCatchClause_TypeDecl_value));
    return reachableCatchClause_TypeDecl_value;
  }
  /**
   * @attribute inh
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:885
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl hostType() {
      ASTNode$State state = state();
    TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
    return hostType_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:78
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getBlockNoTransform()){
    SimpleSet set = parameterDeclaration(name);
    if(!set.isEmpty()) return set;
    return lookupVariable(name);
  }
    return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
