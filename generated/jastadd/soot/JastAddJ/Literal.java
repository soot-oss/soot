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
 * The abstract base class for all literals.
 * @production Literal : {@link PrimaryExpr} ::= <span class="component">&lt;LITERAL:String&gt;</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.ast:4
 */
public abstract class Literal extends PrimaryExpr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    constant_computed = false;
    constant_value = null;
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
  public Literal clone() throws CloneNotSupportedException {
    Literal node = (Literal)super.clone();
    node.constant_computed = false;
    node.constant_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect BytecodeCONSTANT
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BytecodeCONSTANT.jrag:75
   */
  public static Literal buildBooleanLiteral(boolean value) {
    return new BooleanLiteral(value ? "true" : "false");
  }
  /**
   * @ast method 
   * @aspect BytecodeCONSTANT
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BytecodeCONSTANT.jrag:79
   */
  public static Literal buildStringLiteral(String value) {
    return new StringLiteral(value);
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:263
   */
  public void toString(StringBuffer s) {
    s.append(getLITERAL());
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:290
   */
  protected static String escape(String s) {
    StringBuffer result = new StringBuffer();
    for (int i=0; i < s.length(); i++) {
      switch(s.charAt(i)) {
        case '\b' : result.append("\\b"); break;
        case '\t' : result.append("\\t"); break;
        case '\n' : result.append("\\n"); break;
        case '\f' : result.append("\\f"); break;
        case '\r' : result.append("\\r"); break;
        case '\"' : result.append("\\\""); break;
        case '\'' : result.append("\\\'"); break;
        case '\\' : result.append("\\\\"); break;
        default:
          int value = (int)s.charAt(i);
          if(value < 0x20 || (value > 0x7e))
            result.append(asEscape(value));
          else
            result.append(s.charAt(i));
      }
    }
    return result.toString();
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:312
   */
  protected static String asEscape(int value) {
    StringBuffer s = new StringBuffer("\\u");
    String hex = Integer.toHexString(value);
    for(int i = 0; i < 4-hex.length(); i++)
      s.append("0");
    s.append(hex);
    return s.toString();
  }
  /**
   * @ast method 
   * 
   */
  public Literal() {
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
  }
  /**
   * @ast method 
   * 
   */
  public Literal(String p0) {
    setLITERAL(p0);
  }
  /**
   * @ast method 
   * 
   */
  public Literal(beaver.Symbol p0) {
    setLITERAL(p0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 0;
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
   * Replaces the lexeme LITERAL.
   * @param value The new value for the lexeme LITERAL.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setLITERAL(String value) {
    tokenString_LITERAL = value;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected String tokenString_LITERAL;
  /**
   * @ast method 
   * 
   */
  
  public int LITERALstart;
  /**
   * @ast method 
   * 
   */
  
  public int LITERALend;
  /**
   * JastAdd-internal setter for lexeme LITERAL using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setLITERAL(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setLITERAL is only valid for String lexemes");
    tokenString_LITERAL = (String)symbol.value;
    LITERALstart = symbol.getStart();
    LITERALend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme LITERAL.
   * @return The value for the lexeme LITERAL.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public String getLITERAL() {
    return tokenString_LITERAL != null ? tokenString_LITERAL : "";
  }
  /**
	 * @return a fresh double literal representing the given value
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:50
   */
    public static Literal buildDoubleLiteral(double value) {
		String digits = Double.toString(value);
		NumericLiteral lit = new DoubleLiteral(digits);
		lit.setDigits(digits);
		lit.setKind(NumericLiteral.DECIMAL);
		return lit;
	}
  /**
	 * @return a fresh float literal representing the given value
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:62
   */
    public static Literal buildFloatLiteral(float value) {
		String digits = Float.toString(value);
		NumericLiteral lit = new FloatingPointLiteral(digits);
		lit.setDigits(digits);
		lit.setKind(NumericLiteral.DECIMAL);
		return lit;
	}
  /**
	 * @return a fresh integer literal representing the given value
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:74
   */
    public static Literal buildIntegerLiteral(int value) {
		String digits = Integer.toHexString(value);
		NumericLiteral lit = new IntegerLiteral("0x"+digits);
		lit.setDigits(digits.toLowerCase());
		lit.setKind(NumericLiteral.HEXADECIMAL);
		return lit;
	}
  /**
	 * @return a fresh long literal representing the given value
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:86
   */
    public static Literal buildLongLiteral(long value) {
		String digits = Long.toHexString(value);
		NumericLiteral lit = new LongLiteral("0x"+digits);
		lit.setDigits(digits.toLowerCase());
		lit.setKind(NumericLiteral.HEXADECIMAL);
		return lit;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:96
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
    throw new UnsupportedOperationException("ConstantExpression operation constant" +
      " not supported for type " + getClass().getName()); 
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:336
   */
  public boolean isConstant() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:800
   */
  public String dumpString() {
    ASTNode$State state = state();
    try {  return getClass().getName() + " [" + getLITERAL() + "]";  }
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
