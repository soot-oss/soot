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
 * @declaredat java.ast:135
 */
public class PreDecExpr extends Unary implements Cloneable {
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
  public PreDecExpr clone() throws CloneNotSupportedException {
    PreDecExpr node = (PreDecExpr)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public PreDecExpr copy() {
      try {
        PreDecExpr node = (PreDecExpr)clone();
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
  public PreDecExpr fullCopy() {
    PreDecExpr res = (PreDecExpr)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect DefiniteAssignment
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:81
   */
  public void definiteAssignment() {
    if(getOperand().isVariable()) {
      Variable v = getOperand().varDecl();
      if(v != null && v.isFinal()) {
        error("++ and -- can not be applied to final variable " + v);
      }
    }
  }
  /**
   * @ast method 
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:482
   */
  protected boolean checkDUeverywhere(Variable v) {
    if(getOperand().isVariable() && getOperand().varDecl() == v)
      if(!isDAbefore(v))
        return false;
    return super.checkDUeverywhere(v);
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:309
   */
  public void typeCheck() {
    if(!getOperand().isVariable())
      error("prefix decrement expression only work on variables");
    else if(!getOperand().type().isNumericType())
      error("unary decrement only operates on numeric types");
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:770
   */
  public soot.Value eval(Body b) { return emitPrefix(b, -1); }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public PreDecExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public PreDecExpr(Expr p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:13
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:19
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Operand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setOperand(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Operand
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getOperand() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getOperandNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:378
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String printPreOp() {
      ASTNode$State state = state();
    String printPreOp_value = printPreOp_compute();
    return printPreOp_value;
  }
  /**
   * @apilevel internal
   */
  private String printPreOp_compute() {  return "--";  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:47
   * @apilevel internal
   */
  public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
    if(caller == getOperandNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isDest(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:55
   * @apilevel internal
   */
  public boolean Define_boolean_isIncOrDec(ASTNode caller, ASTNode child) {
    if(caller == getOperandNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isIncOrDec(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
