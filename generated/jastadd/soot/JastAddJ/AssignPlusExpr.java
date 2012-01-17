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
 * @declaredat java.ast:113
 */
public class AssignPlusExpr extends AssignAdditiveExpr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
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
  public AssignPlusExpr clone() throws CloneNotSupportedException {
    AssignPlusExpr node = (AssignPlusExpr)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AssignPlusExpr copy() {
      try {
        AssignPlusExpr node = (AssignPlusExpr)clone();
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
  public AssignPlusExpr fullCopy() {
    AssignPlusExpr res = (AssignPlusExpr)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:71
   */
  public void typeCheck() {
    if(!getDest().isVariable())
      error("left hand side is not a variable");
    else if(getSource().type().isUnknown() || getDest().type().isUnknown())
      return;
    else if(getDest().type().isString() && !(getSource().type().isVoid()))
      return;
    else if(getSource().type().isBoolean() || getDest().type().isBoolean())
      error("Operator + does not operate on boolean types");
    else if(getSource().type().isPrimitive() && getDest().type().isPrimitive())
      return;
    else
      error("can not assign " + getDest() + " of type " + getDest().type().typeName() +
            " a value of type " + sourceType().typeName());
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:84
   */
  public soot.Value eval(Body b) {
    TypeDecl dest = getDest().type();
    TypeDecl source = getSource().type();
    if(dest.isString()) {
      
      Value lvalue = getDest().eval(b);

      Value v = asImmediate(b, lvalue);

      // new StringBuffer(left)
      Local local = b.newTemp(b.newNewExpr(
        lookupType("java.lang", "StringBuffer").sootRef(), this));
      b.setLine(this);
      b.add(b.newInvokeStmt(
        b.newSpecialInvokeExpr(local, 
          Scene.v().getMethod("<java.lang.StringBuffer: void <init>(java.lang.String)>").makeRef(),
          v,
          this
        ), this));

      // append right
      Local rightResult = b.newTemp(
        b.newVirtualInvokeExpr(local,
          lookupType("java.lang", "StringBuffer").methodWithArgs("append", new TypeDecl[] { source.stringPromotion() }).sootRef(),
          asImmediate(b, getSource().eval(b)),
          this
        ));

      // toString
      Local result = b.newTemp(
        b.newVirtualInvokeExpr(rightResult,
          Scene.v().getMethod("<java.lang.StringBuffer: java.lang.String toString()>").makeRef(),
          this
        ));
  
      Value v2 = lvalue instanceof Local ? lvalue : (Value)lvalue.clone();
      getDest().emitStore(b, v2, result, this);
      return result;
    }
    else {
      return super.eval(b);
    }
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:165
   */
  public soot.Value createAssignOp(Body b, soot.Value fst, soot.Value snd) {
    return b.newAddExpr(asImmediate(b, fst), asImmediate(b, snd), this);
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public AssignPlusExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public AssignPlusExpr(Expr p0, Expr p1) {
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
   * Setter for Dest
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setDest(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Dest
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getDest() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getDestNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for Source
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setSource(Expr node) {
    setChild(node, 1);
  }
  /**
   * Getter for Source
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getSource() {
    return (Expr)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getSourceNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:252
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
  private String printOp_compute() {  return " += ";  }
  /**
   * @attribute syn
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:111
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl sourceType() {
      ASTNode$State state = state();
    TypeDecl sourceType_value = sourceType_compute();
    return sourceType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl sourceType_compute() {
    TypeDecl left = getDest().type();
    TypeDecl right = getSource().type();
    if(!left.isString() && !right.isString())
      return super.sourceType();
    if(left.isVoid() || right.isVoid())
      return unknownType();
    return left.isString() ? left : right;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
