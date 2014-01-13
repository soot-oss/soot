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
 * @production Unary : {@link Expr} ::= <span class="component">Operand:{@link Expr}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:136
 */
public abstract class Unary extends Expr implements Cloneable {
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
  public Unary clone() throws CloneNotSupportedException {
    Unary node = (Unary)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:366
   */
  public void toString(StringBuffer s) {
    s.append(printPreOp());
    getOperand().toString(s);
    s.append(printPostOp());
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:689
   */
  public soot.Value eval(Body b) {
    return super.eval(b);
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:736
   */
  public soot.Value emitPostfix(Body b, int constant) {
    soot.Value lvalue = getOperand().eval(b);
    Value v = lvalue instanceof Local ? lvalue : (Value)lvalue.clone();
    TypeDecl type = getOperand().type().binaryNumericPromotion(typeInt());
    Value value = b.newTemp(getOperand().type().emitCastTo(b, v, type, getOperand()));
    Value rvalue = typeInt().emitCastTo(b, IntType.emitConstant(constant), type, this);
    Value sum = asRValue(b, type.emitCastTo(b,
      b.newAddExpr(asImmediate(b, value), asImmediate(b, rvalue), this),
      getOperand().type(),
      this
    ));
    getOperand().emitStore(b, lvalue, sum, this);
    return value;
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:754
   */
  public soot.Value emitPrefix(Body b, int constant) {
    soot.Value lvalue = getOperand().eval(b);
    Value v = lvalue instanceof Local ? lvalue : (Value)lvalue.clone();
    TypeDecl type = getOperand().type().binaryNumericPromotion(typeInt());
    Value value = getOperand().type().emitCastTo(b, v, type, getOperand());
    Value rvalue = typeInt().emitCastTo(b, IntType.emitConstant(constant), type, this);
    Value result = asLocal(b, type.emitCastTo(b,
      b.newAddExpr(asImmediate(b, value), asImmediate(b, rvalue), this),
      getOperand().type(),
      this
    ));
    getOperand().emitStore(b, lvalue, result, this);
    return result;
  }
  /**
   * @ast method 
   * 
   */
  public Unary() {
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
    children = new ASTNode[1];
  }
  /**
   * @ast method 
   * 
   */
  public Unary(Expr p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 1;
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
   * Replaces the Operand child.
   * @param node The new node to replace the Operand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setOperand(Expr node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Operand child.
   * @return The current node used as the Operand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getOperand() {
    return (Expr)getChild(0);
  }
  /**
   * Retrieves the Operand child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Operand child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getOperandNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:235
   */
  public boolean isDAafter(Variable v) {
    ASTNode$State state = state();
    try {  return getOperand().isDAafter(v);  }
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
    try {  return getOperand().isDUafter(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:372
   */
  public String printPostOp() {
    ASTNode$State state = state();
    try {  return "";  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:376
   */
  public String printPreOp() {
    ASTNode$State state = state();
    try {  return "";  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:314
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
  private TypeDecl type_compute() {  return getOperand().type();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:44
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getOperandNoTransform()) {
      return true;
    }
    else {      return getParent().Define_boolean_isSource(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
