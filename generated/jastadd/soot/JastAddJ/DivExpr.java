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
 * @declaredat java.ast:152
 */
public class DivExpr extends MultiplicativeExpr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isConstant_visited = -1;
    isConstant_computed = false;
    isConstant_initialized = false;
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
  public DivExpr clone() throws CloneNotSupportedException {
    DivExpr node = (DivExpr)super.clone();
    node.isConstant_visited = -1;
    node.isConstant_computed = false;
    node.isConstant_initialized = false;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public DivExpr copy() {
      try {
        DivExpr node = (DivExpr)clone();
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
  public DivExpr fullCopy() {
    DivExpr res = (DivExpr)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:814
   */
  public soot.Value emitOperation(Body b, soot.Value left, soot.Value right) {
    return asLocal(b, b.newDivExpr(asImmediate(b, left), asImmediate(b, right), this));
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public DivExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public DivExpr(Expr p0, Expr p1) {
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:118
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
  private Constant constant_compute() {  return type().div(getLeftOperand().constant(), getRightOperand().constant());  }
  /**
   * @apilevel internal
   */
  protected int isConstant_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean isConstant_value;
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:497
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isConstant() {
    if(isConstant_computed) {
      return isConstant_value;
    }
    ASTNode$State state = state();
    if (!isConstant_initialized) {
      isConstant_initialized = true;
      isConstant_value = false;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        isConstant_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_isConstant_value = isConstant_compute();
        if (new_isConstant_value!=isConstant_value)
          state.CHANGE = true;
        isConstant_value = new_isConstant_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
      isConstant_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      isConstant_compute();
      state.RESET_CYCLE = false;
        isConstant_computed = false;
        isConstant_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return isConstant_value;
    }
    if(isConstant_visited != state.CIRCLE_INDEX) {
      isConstant_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        isConstant_computed = false;
        isConstant_initialized = false;
        isConstant_visited = -1;
        return isConstant_value;
      }
      boolean new_isConstant_value = isConstant_compute();
      if (new_isConstant_value!=isConstant_value)
        state.CHANGE = true;
      isConstant_value = new_isConstant_value; 
      return isConstant_value;
    }
    return isConstant_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isConstant_compute() {  return getLeftOperand().isConstant() && getRightOperand().isConstant() && !(getRightOperand().type().isInt() && getRightOperand().constant().intValue() == 0);  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:402
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
  private String printOp_compute() {  return " / ";  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
