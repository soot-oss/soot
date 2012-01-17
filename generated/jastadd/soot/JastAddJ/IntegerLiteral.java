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
 * Default Java integer literal. Should only be used for numbers
 * that can be stored in 32 bits binary.
 * @ast node
 * @declaredat Literals.ast:34
 */
public class IntegerLiteral extends Literal implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
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
  public IntegerLiteral clone() throws CloneNotSupportedException {
    IntegerLiteral node = (IntegerLiteral)super.clone();
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
  public IntegerLiteral copy() {
      try {
        IntegerLiteral node = (IntegerLiteral)clone();
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
  public IntegerLiteral fullCopy() {
    IntegerLiteral res = (IntegerLiteral)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:48
   */
  public IntegerLiteral(int i) {
    this(Integer.toString(i));
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:570
   */
  public void typeCheck() {
   if(constant().error)
     error("The value of an int literal must be a decimal value in the range -2147483648..2147483647 or a hexadecimal or octal literal that fits in 32 bits.");

 }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:17
   */
  public soot.Value eval(Body b) {
    return IntType.emitConstant(constant().intValue());
  }
  /**
   * @ast method 
   * @declaredat Literals.ast:1
   */
  public IntegerLiteral() {
    super();


  }
  /**
   * @ast method 
   * @declaredat Literals.ast:7
   */
  public IntegerLiteral(String p0) {
    setLITERAL(p0);
  }
  /**
   * @ast method 
   * @declaredat Literals.ast:10
   */
  public IntegerLiteral(beaver.Symbol p0) {
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
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:233
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isHex() {
      ASTNode$State state = state();
    boolean isHex_value = isHex_compute();
    return isHex_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isHex_compute() {  return getLITERAL().toLowerCase().startsWith("0x");  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:234
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isOctal() {
      ASTNode$State state = state();
    boolean isOctal_value = isOctal_compute();
    return isOctal_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isOctal_compute() {  return getLITERAL().startsWith("0");  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:235
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDecimal() {
      ASTNode$State state = state();
    boolean isDecimal_value = isDecimal_compute();
    return isDecimal_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDecimal_compute() {  return !isHex() && !isOctal();  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:242
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isPositive() {
      ASTNode$State state = state();
    boolean isPositive_value = isPositive_compute();
    return isPositive_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isPositive_compute() {  return !getLITERAL().startsWith("-");  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:255
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
    long l = 0;
    try {
      l = Literal.parseLong(getLITERAL());
    } catch (NumberFormatException e) {
      Constant c = Constant.create(0L);
      c.error = true;
      return c;
    }
    Constant c = Constant.create((int)l);
    if(isDecimal() && l != (int)l)
      c.error = true;
    if(isOctal() && l > 037777777777L)
      c.error = true;
    if(isHex() && l > 0xffffffffL)
      c.error = true;
    return c;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:300
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
  private TypeDecl type_compute() {  return typeInt();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
