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
 * A catch clause that can catch a single exception type.
 * @production BasicCatch : {@link CatchClause} ::= <span class="component">Parameter:{@link ParameterDeclaration}</span> <span class="component">{@link Block}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.ast:9
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
      BasicCatch node = (BasicCatch) clone();
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
  public BasicCatch fullCopy() {
    BasicCatch tree = (BasicCatch) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:470
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
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:198
   */
  void checkUnreachableStmt() {
		if (!getBlock().reachable() && reportUnreachable())
			error("the exception "+getParameter().type().fullName()+
			" is not thrown in the body of the try statement");
	}
  /**
   * @ast method 
   * 
   */
  public BasicCatch() {
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
  public BasicCatch(ParameterDeclaration p0, Block p1) {
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
   * Replaces the Parameter child.
   * @param node The new node to replace the Parameter child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setParameter(ParameterDeclaration node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Parameter child.
   * @return The current node used as the Parameter child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public ParameterDeclaration getParameter() {
    return (ParameterDeclaration)getChild(0);
  }
  /**
   * Retrieves the Parameter child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Parameter child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public ParameterDeclaration getParameterNoTransform() {
    return (ParameterDeclaration)getChildNoTransform(0);
  }
  /**
   * Replaces the Block child.
   * @param node The new node to replace the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBlock(Block node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the Block child.
   * @return The current node used as the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Block getBlock() {
    return (Block)getChild(1);
  }
  /**
   * Retrieves the Block child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Block child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Block getBlockNoTransform() {
    return (Block)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:212
   */
  public boolean handles(TypeDecl exceptionType) {
    ASTNode$State state = state();
    try {  return !getParameter().type().isUnknown()
    && exceptionType.instanceOf(getParameter().type());  }
    finally {
    }
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:469
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
    else {      return super.Define_SimpleSet_lookupVariable(caller, child, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:295
   * @apilevel internal
   */
  public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return this;
    }
    else {      return getParent().Define_VariableScope_outerScope(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:86
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return NameType.TYPE_NAME;
    }
    else {      return getParent().Define_NameType_nameType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:122
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return reachableCatchClause(getParameter().type());
    }
    else {      return getParent().Define_boolean_reachable(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:64
   * @apilevel internal
   */
  public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return false;
    }
    else {      return getParent().Define_boolean_isMethodParameter(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:65
   * @apilevel internal
   */
  public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return false;
    }
    else {      return getParent().Define_boolean_isConstructorParameter(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:66
   * @apilevel internal
   */
  public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return true;
    }
    else {      return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/VariableArityParameters.jrag:23
   * @apilevel internal
   */
  public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return false;
    }
    else {      return getParent().Define_boolean_variableArityValid(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:52
   * @apilevel internal
   */
  public boolean Define_boolean_inhModifiedInScope(ASTNode caller, ASTNode child, Variable var) {
    if(caller == getParameterNoTransform()) {
      return getBlock().modifiedInScope(var);
    }
    else {      return getParent().Define_boolean_inhModifiedInScope(this, caller, var);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:125
   * @apilevel internal
   */
  public boolean Define_boolean_isCatchParam(ASTNode caller, ASTNode child) {
    if(caller == getParameterNoTransform()) {
      return true;
    }
    else {      return getParent().Define_boolean_isCatchParam(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
