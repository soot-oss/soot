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
 * @declaredat java.ast:155
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
        AddExpr node = (AddExpr)clone();
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
  public AddExpr fullCopy() {
    AddExpr res = (AddExpr)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
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
   * @declaredat java.ast:1
   */
  public AddExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public AddExpr(Expr p0, Expr p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:14
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:20
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for LeftOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setLeftOperand(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for LeftOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getLeftOperand() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getLeftOperandNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for RightOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setRightOperand(Expr node) {
    setChild(node, 1);
  }
  /**
   * Getter for RightOperand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getRightOperand() {
    return (Expr)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getRightOperandNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:121
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant constant() {
      ASTNode$State state = state();
    Constant constant_value = constant_compute();
    return constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant constant_compute() {  return type().add(getLeftOperand().constant(), getRightOperand().constant());  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:404
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String printOp() {
      ASTNode$State state = state();
    String printOp_value = printOp_compute();
    return printOp_value;
  }
  /**
   * @apilevel internal
   */
  private String printOp_compute() {  return " + ";  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:86
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isStringAdd() {
      ASTNode$State state = state();
    boolean isStringAdd_value = isStringAdd_compute();
    return isStringAdd_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isStringAdd_compute() {  return type().isString() && !isConstant();  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:88
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean firstStringAddPart() {
      ASTNode$State state = state();
    boolean firstStringAddPart_value = firstStringAddPart_compute();
    return firstStringAddPart_value;
  }
  /**
   * @apilevel internal
   */
  private boolean firstStringAddPart_compute() {  return type().isString() && !getLeftOperand().isStringAdd();  }
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:89
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean lastStringAddPart() {
      ASTNode$State state = state();
    boolean lastStringAddPart_value = lastStringAddPart_compute();
    return lastStringAddPart_value;
  }
  /**
   * @apilevel internal
   */
  private boolean lastStringAddPart_compute() {  return !getParent().isStringAdd();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
