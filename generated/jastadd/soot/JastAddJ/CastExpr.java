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
 * @production CastExpr : {@link Expr} ::= <span class="component">TypeAccess:{@link Access}</span> <span class="component">{@link Expr}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:144
 */
public class CastExpr extends Expr implements Cloneable {
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
  public CastExpr clone() throws CloneNotSupportedException {
    CastExpr node = (CastExpr)super.clone();
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
  public CastExpr copy() {
    try {
      CastExpr node = (CastExpr) clone();
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
  public CastExpr fullCopy() {
    CastExpr tree = (CastExpr) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:385
   */
  public void toString(StringBuffer s) {
    s.append("(");
    getTypeAccess().toString(s);
    s.append(")");
    getExpr().toString(s);
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:252
   */
  public void typeCheck() {
    TypeDecl expr = getExpr().type();
    TypeDecl type = getTypeAccess().type();
    if(!expr.isUnknown()) {
      if(!expr.castingConversionTo(type))
        error(expr.typeName() + " can not be cast into " + type.typeName());
      if(!getTypeAccess().isTypeAccess())
        error("" + getTypeAccess() + " is not a type access in cast expression");
    }
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:709
   */
  public soot.Value eval(Body b) {
    if(isConstant())
      return emitConstant(constant());
    soot.Value operand = getExpr().eval(b);
    if (operand == NullConstant.v())
        return getExpr().type().emitCastTo(b, operand, type(), this);
    return getExpr().type().emitCastTo(b, asLocal(b, operand), type(), this);
  }
  /**
   * @ast method 
   * @aspect UncheckedConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/UncheckedConversion.jrag:35
   */
  public void checkWarnings() {
    if (!withinSuppressWarnings("unchecked"))
      checkUncheckedConversion(getExpr().type(), getTypeAccess().type());
  }
  /**
   * @ast method 
   * 
   */
  public CastExpr() {
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
  public CastExpr(Access p0, Expr p1) {
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
   * Replaces the TypeAccess child.
   * @param node The new node to replace the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the TypeAccess child.
   * @return The current node used as the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Access getTypeAccess() {
    return (Access)getChild(0);
  }
  /**
   * Retrieves the TypeAccess child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the TypeAccess child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * Replaces the Expr child.
   * @param node The new node to replace the Expr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setExpr(Expr node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the Expr child.
   * @return The current node used as the Expr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getExpr() {
    return (Expr)getChild(1);
  }
  /**
   * Retrieves the Expr child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Expr child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getExprNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:91
   */
  public Constant constant() {
    ASTNode$State state = state();
    try {  return type().cast(getExpr().constant());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:336
   */
  public boolean isConstant() {
    ASTNode$State state = state();
    try {  return getExpr().isConstant() &&
    (getTypeAccess().type().isPrimitive() || getTypeAccess().type().isString());  }
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
    try {  return getExpr().isDAafter(v);  }
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
    try {  return getExpr().isDUafter(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect AccessTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:25
   */
  public boolean isSuperAccess() {
    ASTNode$State state = state();
    try {  return getExpr().isSuperAccess();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect AccessTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:31
   */
  public boolean isThisAccess() {
    ASTNode$State state = state();
    try {  return getExpr().isThisAccess();  }
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
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:320
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
  private TypeDecl type_compute() {  return getTypeAccess().type();  }
  /**
   * @attribute syn
   * @aspect TypeHierarchyCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeHierarchyCheck.jrag:150
   */
  public boolean staticContextQualifier() {
    ASTNode$State state = state();
    try {  return getExpr().staticContextQualifier();  }
    finally {
    }
  }
  /**
   * @attribute inh
   * @aspect SuppressWarnings
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SuppressWarnings.jrag:16
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean withinSuppressWarnings(String s) {
    ASTNode$State state = state();
    boolean withinSuppressWarnings_String_value = getParent().Define_boolean_withinSuppressWarnings(this, null, s);
    return withinSuppressWarnings_String_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:88
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeAccessNoTransform()) {
      return NameType.TYPE_NAME;
    }
    else {      return getParent().Define_NameType_nameType(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
