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
 * Java double-precision floating point literal.
 * Can store any value representable as an
 * IEEE 754 64-bit double-precision floating point number.
 * @ast node
 * @declaredat Literals.ast:53
 */
public class DoubleLiteral extends Literal implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isZero_computed = false;
    constant_computed = false;
    constant_value = null;
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
  public DoubleLiteral clone() throws CloneNotSupportedException {
    DoubleLiteral node = (DoubleLiteral)super.clone();
    node.isZero_computed = false;
    node.constant_computed = false;
    node.constant_value = null;
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
  public DoubleLiteral copy() {
      try {
        DoubleLiteral node = (DoubleLiteral)clone();
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
  public DoubleLiteral fullCopy() {
    DoubleLiteral res = (DoubleLiteral)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:285
   */
  public void toString(StringBuffer s) {
    s.append(getLITERAL());
    s.append("D");
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:588
   */
  public void typeCheck() {
   if(!isZero() && constant().doubleValue() == 0.0f)
     error("It is an error for nonzero floating-point " + getLITERAL() + " to round to zero");
   if(constant().doubleValue() == Double.NEGATIVE_INFINITY || constant().doubleValue() == Double.POSITIVE_INFINITY)
     error("It is an error for floating-point " + getLITERAL() + " to round to an infinity");
 }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:29
   */
  public soot.Value eval(Body b) {
    return soot.jimple.DoubleConstant.v(constant().doubleValue());
  }
  /**
   * @ast method 
   * @declaredat Literals.ast:1
   */
  public DoubleLiteral() {
    super();


  }
  /**
   * @ast method 
   * @declaredat Literals.ast:7
   */
  public DoubleLiteral(String p0) {
    setLITERAL(p0);
  }
  /**
   * @ast method 
   * @declaredat Literals.ast:10
   */
  public DoubleLiteral(beaver.Symbol p0) {
    setLITERAL(p0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Literals.ast:16
   */
  protected int numChildren() {
    return 0;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Literals.ast:22
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for lexeme LITERAL
   * @apilevel high-level
   * @ast method 
   * @declaredat Literals.ast:5
   */
  public void setLITERAL(String value) {
    tokenString_LITERAL = value;
  }
  /**
   * @ast method 
   * @declaredat Literals.ast:8
   */
  public void setLITERAL(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setLITERAL is only valid for String lexemes");
    tokenString_LITERAL = (String)symbol.value;
    LITERALstart = symbol.getStart();
    LITERALend = symbol.getEnd();
  }
  /**
   * Getter for lexeme LITERAL
   * @apilevel high-level
   * @ast method 
   * @declaredat Literals.ast:19
   */
  public String getLITERAL() {
    return tokenString_LITERAL != null ? tokenString_LITERAL : "";
  }
  /**
   * @apilevel internal
   */
  protected boolean isZero_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isZero_value;
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:147
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isZero() {
    if(isZero_computed) {
      return isZero_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    isZero_value = isZero_compute();
if(isFinal && num == state().boundariesCrossed) isZero_computed = true;
    return isZero_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isZero_compute() {
    String s = getLITERAL();
    for(int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if(c == 'E'  || c == 'e')
        break;
      if(Character.isDigit(c) && c != '0') {
        return false;
      }
    }
    return true;
  }
  /**
   * @apilevel internal
   */
  protected boolean constant_computed = false;
  /**
   * @apilevel internal
   */
  protected Constant constant_value;
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:292
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant constant() {
    if(constant_computed) {
      return constant_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    constant_value = constant_compute();
if(isFinal && num == state().boundariesCrossed) constant_computed = true;
    return constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant constant_compute() {
    try {
      return Constant.create(Double.parseDouble(getLITERAL()));
    }
    catch (NumberFormatException e) {
      Constant c = Constant.create(0.0d);
      c.error = true;
      return c;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:303
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
  private TypeDecl type_compute() {  return typeDouble();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
