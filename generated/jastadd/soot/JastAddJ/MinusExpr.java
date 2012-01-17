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
 * @declaredat java.ast:136
 */
public class MinusExpr extends Unary implements Cloneable {
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
  public MinusExpr clone() throws CloneNotSupportedException {
    MinusExpr node = (MinusExpr)super.clone();
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
  public MinusExpr copy() {
      try {
        MinusExpr node = (MinusExpr)clone();
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
  public MinusExpr fullCopy() {
    MinusExpr res = (MinusExpr)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:275
   */
  public void typeCheck() {
    if(!getOperand().type().isNumericType())
      error("unary minus only operates on numeric types");
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:692
   */
  public soot.Value eval(Body b) { 
    return b.newNegExpr(asImmediate(b, getOperand().eval(b)), this);
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public MinusExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public MinusExpr(Expr p0) {
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
    return true;
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
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:114
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
  private Constant constant_compute() {  return type().minus(getOperand().constant());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:488
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isConstant() {
      ASTNode$State state = state();
    boolean isConstant_value = isConstant_compute();
    return isConstant_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isConstant_compute() {  return getOperand().isConstant();  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:379
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
  private String printPreOp_compute() {  return "-";  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:316
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
  private TypeDecl type_compute() {  return getOperand().type().unaryNumericPromotion();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag at line 246
    if(getOperand() instanceof IntegerLiteral && ((IntegerLiteral)getOperand()).isDecimal() && getOperand().isPositive()) {
      state().duringConstantExpression++;
      ASTNode result = rewriteRule0();
      state().duringConstantExpression--;
      return result;
    }

    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag at line 251
    if(getOperand() instanceof LongLiteral && ((LongLiteral)getOperand()).isDecimal() && getOperand().isPositive()) {
      state().duringConstantExpression++;
      ASTNode result = rewriteRule1();
      state().duringConstantExpression--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:246
   * @apilevel internal
   */  private IntegerLiteral rewriteRule0() {
    return new IntegerLiteral("-" + ((IntegerLiteral)getOperand()).getLITERAL());
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:251
   * @apilevel internal
   */  private LongLiteral rewriteRule1() {
    return new LongLiteral("-" + ((LongLiteral)getOperand()).getLITERAL());
  }
}
