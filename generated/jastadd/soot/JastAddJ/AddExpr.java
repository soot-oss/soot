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
 * @production AddExpr : {@link AdditiveExpr};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:158
 */
public class AddExpr extends AdditiveExpr implements Cloneable {
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
  public AddExpr clone() throws CloneNotSupportedException {
    AddExpr node = (AddExpr)super.clone();
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
  public AddExpr copy() {
    try {
      AddExpr node = (AddExpr) clone();
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
  public AddExpr fullCopy() {
    AddExpr tree = (AddExpr) copy();
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
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:172
   */
  public void typeCheck() {
    TypeDecl left = getLeftOperand().type();
    TypeDecl right = getRightOperand().type();
    if(!left.isString() && !right.isString())
      super.typeCheck();
    else if(left.isVoid())
      error("The type void of the left hand side is not numeric");
    else if(right.isVoid())
      error("The type void of the right hand side is not numeric");
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:805
   */
  public soot.Value emitOperation(Body b, soot.Value left, soot.Value right) {
    return asLocal(b, b.newAddExpr(asImmediate(b, left), asImmediate(b, right), this));
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:839
   */
  public soot.Value eval(Body b) {
    if(type().isString() && isConstant())
      return soot.jimple.StringConstant.v(constant().stringValue());
    if(isStringAdd()) {
      Local v;
      if(firstStringAddPart()) {
        // new StringBuffer
        v = b.newTemp(b.newNewExpr(
          lookupType("java.lang", "StringBuffer").sootRef(), this));
        b.setLine(this);
        b.add(b.newInvokeStmt(
          b.newSpecialInvokeExpr(v, 
          Scene.v().getMethod("<java.lang.StringBuffer: void <init>()>").makeRef(),
          this
        ), this));
        b.setLine(this);
        b.add(b.newInvokeStmt(
          b.newVirtualInvokeExpr(v,
            lookupType("java.lang", "StringBuffer").methodWithArgs("append", new TypeDecl[] { getLeftOperand().type().stringPromotion() }).sootRef(),
            asImmediate(b, getLeftOperand().eval(b)),
            this
          ), this));
      }
      else
        v = (Local)getLeftOperand().eval(b);
      // append
      b.setLine(this);
      b.add(b.newInvokeStmt(
        b.newVirtualInvokeExpr(v,
          lookupType("java.lang", "StringBuffer").methodWithArgs("append", new TypeDecl[] { getRightOperand().type().stringPromotion() }).sootRef(),
          asImmediate(b, getRightOperand().eval(b)),
          this
        ), this));
      if(lastStringAddPart()) {
        return b.newTemp(
          b.newVirtualInvokeExpr(v,
            Scene.v().getMethod("<java.lang.StringBuffer: java.lang.String toString()>").makeRef(),
            this
        ));
      }
      else
        return v;
    }
    else 
    return b.newAddExpr(
      b.newTemp(
        getLeftOperand().type().emitCastTo(b,  // Binary numeric promotion
          getLeftOperand(),
          type()
        )
      ),
      asImmediate(b,
        getRightOperand().type().emitCastTo(b, // Binary numeric promotion
          getRightOperand(),
          type()
        )
      ),
      this
    );
  }
  /**
   * @ast method 
   * 
   */
  public AddExpr() {
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
  public AddExpr(Expr p0, Expr p1) {
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
   * Replaces the LeftOperand child.
   * @param node The new node to replace the LeftOperand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setLeftOperand(Expr node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the LeftOperand child.
   * @return The current node used as the LeftOperand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getLeftOperand() {
    return (Expr)getChild(0);
  }
  /**
   * Retrieves the LeftOperand child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the LeftOperand child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getLeftOperandNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Replaces the RightOperand child.
   * @param node The new node to replace the RightOperand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setRightOperand(Expr node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the RightOperand child.
   * @return The current node used as the RightOperand child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getRightOperand() {
    return (Expr)getChild(1);
  }
  /**
   * Retrieves the RightOperand child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the RightOperand child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getRightOperandNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:91
   */
  public Constant constant() {
    ASTNode$State state = state();
    try {  return type().add(getLeftOperand().constant(), getRightOperand().constant());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:400
   */
  public String printOp() {
    ASTNode$State state = state();
    try {  return " + ";  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:327
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
    TypeDecl left = getLeftOperand().type();
    TypeDecl right = getRightOperand().type();
    if(!left.isString() && !right.isString())
      return super.type();
    else {
      if(left.isVoid() || right.isVoid())
        return unknownType();
      // pick the string type
      return left.isString() ? left : right;
    }
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:88
   */
  public boolean isStringAdd() {
    ASTNode$State state = state();
    try {  return type().isString() && !isConstant();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:91
   */
  public boolean firstStringAddPart() {
    ASTNode$State state = state();
    try {  return type().isString() && !getLeftOperand().isStringAdd();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:92
   */
  public boolean lastStringAddPart() {
    ASTNode$State state = state();
    try {  return !getParent().isStringAdd();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
