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
 * A catch clause that can catch a multiple exception types.
 * @production MultiCatch : {@link CatchClause} ::= <span class="component">Parameter:{@link CatchParameterDeclaration}</span> <span class="component">{@link Block}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.ast:14
 */
public class MultiCatch extends CatchClause implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    parameterDeclaration_String_values = null;
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
  public MultiCatch clone() throws CloneNotSupportedException {
    MultiCatch node = (MultiCatch)super.clone();
    node.parameterDeclaration_String_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MultiCatch copy() {
    try {
      MultiCatch node = (MultiCatch) clone();
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
  public MultiCatch fullCopy() {
    MultiCatch tree = (MultiCatch) copy();
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
	 * Pretty printing of multi-catch clause.
	 * @ast method 
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:146
   */
  public void toString(StringBuffer sb) {
		sb.append("catch (");
		getParameter().toString(sb);
		sb.append(") ");
		getBlock().toString(sb);
	}
  /**
   * @ast method 
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:204
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
  public MultiCatch() {
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
  public MultiCatch(CatchParameterDeclaration p0, Block p1) {
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
  public void setParameter(CatchParameterDeclaration node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Parameter child.
   * @return The current node used as the Parameter child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public CatchParameterDeclaration getParameter() {
    return (CatchParameterDeclaration)getChild(0);
  }
  /**
   * Retrieves the Parameter child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Parameter child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public CatchParameterDeclaration getParameterNoTransform() {
    return (CatchParameterDeclaration)getChildNoTransform(0);
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
  protected java.util.Map parameterDeclaration_String_values;
  /**
	 * Variable lookup in catch parameter scope.
	 * @attribute syn
   * @aspect MultiCatch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:86
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
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:212
   */
  public boolean handles(TypeDecl exceptionType) {
    ASTNode$State state = state();
    try {
		CatchParameterDeclaration param = getParameter();
		for (int i = 0; i < param.getNumTypeAccess(); ++i) {
			TypeDecl type = param.getTypeAccess(i).type();
			if (!type.isUnknown() && exceptionType.instanceOf(type))
				return true;
		}
		return false;
	}
    finally {
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:27
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:28
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:29
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:80
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/MultiCatch.jrag:128
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()){
		boolean anyReachable = false;
		CatchParameterDeclaration param = getParameter();
		for (int i = 0; i < param.getNumTypeAccess(); ++i) {
			TypeDecl type = param.getTypeAccess(i).type();
			if (!reachableCatchClause(type))
				error("The exception type "+type.fullName()+
						" can not be caught "+
						"by this multi-catch clause");
			else
				anyReachable = true;
		}
		return anyReachable;
	}
    else {      return getParent().Define_boolean_reachable(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
