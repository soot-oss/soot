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
 * A catch clause which can catch a single exception type.
 * @ast node
 * @declaredat CatchClause.ast:9
 */
public class BasicCatch extends CatchClause implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    parameterDeclaration_String_values = null;
    label_computed = false;
    label_value = null;
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
  public BasicCatch clone() throws CloneNotSupportedException {
    BasicCatch node = (BasicCatch)super.clone();
    node.parameterDeclaration_String_values = null;
    node.label_computed = false;
    node.label_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BasicCatch copy() {
      try {
        BasicCatch node = (BasicCatch)clone();
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
  public BasicCatch fullCopy() {
    BasicCatch res = (BasicCatch)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:722
   */
  public void toString(StringBuffer s) {
    s.append("catch (");
    getParameter().toString(s);
    s.append(") ");
    getBlock().toString(s);
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:368
   */
  public void typeCheck() {
    if(!getParameter().type().instanceOf(typeThrowable()))
      error("*** The catch variable must extend Throwable");
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:455
   */
  public void jimplify2(Body b) {
    b.addLabel(label());
    Local local = b.newLocal(getParameter().name(), getParameter().type().getSootType());
    b.setLine(this);
    b.add(b.newIdentityStmt(local, b.newCaughtExceptionRef(getParameter()), this));
    getParameter().local = local;
    getBlock().jimplify2(b);
  }
  /**
   * @ast method 
   * @declaredat CatchClause.ast:1
   */
  public BasicCatch() {
    super();


  }
  /**
   * @ast method 
   * @declaredat CatchClause.ast:7
   */
  public BasicCatch(ParameterDeclaration p0, Block p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat CatchClause.ast:14
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat CatchClause.ast:20
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Parameter
   * @apilevel high-level
   * @ast method 
   * @declaredat CatchClause.ast:5
   */
  public void setParameter(ParameterDeclaration node) {
    setChild(node, 0);
  }
  /**
   * Getter for Parameter
   * @apilevel high-level
   * @ast method 
   * @declaredat CatchClause.ast:12
   */
  public ParameterDeclaration getParameter() {
    return (ParameterDeclaration)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat CatchClause.ast:18
   */
  public ParameterDeclaration getParameterNoTransform() {
    return (ParameterDeclaration)getChildNoTransform(0);
  }
  /**
   * Setter for Block
   * @apilevel high-level
   * @ast method 
   * @declaredat CatchClause.ast:5
   */
  public void setBlock(Block node) {
    setChild(node, 1);
  }
  /**
   * Getter for Block
   * @apilevel high-level
   * @ast method 
   * @declaredat CatchClause.ast:12
   */
  public Block getBlock() {
    return (Block)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat CatchClause.ast:18
   */
  public Block getBlockNoTransform() {
    return (Block)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:199
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
  private boolean handles_compute(TypeDecl exceptionType) {  return !getParameter().type().isUnknown()
    && exceptionType.instanceOf(getParameter().type());  }
  protected java.util.Map parameterDeclaration_String_values;
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:113
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
  private SimpleSet parameterDeclaration_compute(String name) {  return getParameter().name().equals(name) ? getParameter() : SimpleSet.emptySet;  }
  /**
   * @apilevel internal
   */
  protected boolean label_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:454
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label() {
    if(label_computed) {
      return label_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_value = label_compute();
if(isFinal && num == state().boundariesCrossed) label_computed = true;
    return label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_compute() {  return newLabel();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:83
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getParameterNoTransform()) {
      return parameterDeclaration(name);
    }
    return super.Define_SimpleSet_lookupVariable(caller, child, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:290
   * @apilevel internal
   */
  public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return this;
    }
    return getParent().Define_VariableScope_outerScope(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:86
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return NameType.TYPE_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:122
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return reachableCatchClause(getParameter().type());
    }
    return getParent().Define_boolean_reachable(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:64
   * @apilevel internal
   */
  public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return false;
    }
    return getParent().Define_boolean_isMethodParameter(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:65
   * @apilevel internal
   */
  public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return false;
    }
    return getParent().Define_boolean_isConstructorParameter(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:66
   * @apilevel internal
   */
  public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:23
   * @apilevel internal
   */
  public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return false;
    }
    return getParent().Define_boolean_variableArityValid(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
