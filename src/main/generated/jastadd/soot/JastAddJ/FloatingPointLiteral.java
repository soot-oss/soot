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
 * Java floating point literal. Can store any value representable as an
 * IEEE 754 32-bit single-precision floating point number.
 * @production FloatingPointLiteral : {@link NumericLiteral};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.ast:60
 */
public class FloatingPointLiteral extends NumericLiteral implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
    isZero_computed = false;
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
  public FloatingPointLiteral clone() throws CloneNotSupportedException {
    FloatingPointLiteral node = (FloatingPointLiteral)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.isZero_computed = false;
    node.constant_computed = false;
    node.constant_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public FloatingPointLiteral copy() {
    try {
      FloatingPointLiteral node = (FloatingPointLiteral) clone();
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
  public FloatingPointLiteral fullCopy() {
    FloatingPointLiteral tree = (FloatingPointLiteral) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:581
   */
  public void typeCheck() {
   if(!isZero() && constant().floatValue() == 0.0f)
     error("It is an error for nonzero floating-point " + getLITERAL() + " to round to zero");
   if(constant().floatValue() == Float.NEGATIVE_INFINITY || constant().floatValue() == Float.POSITIVE_INFINITY)
     error("It is an error for floating-point " + getLITERAL() + " to round to an infinity");
     
 }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:26
   */
  public soot.Value eval(Body b) {
    return soot.jimple.FloatConstant.v(constant().floatValue());
  }
  /**
   * @ast method 
   * 
   */
  public FloatingPointLiteral() {
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
  public FloatingPointLiteral(String p0) {
    setLITERAL(p0);
  }
  /**
   * @ast method 
   * 
   */
  public FloatingPointLiteral(beaver.Symbol p0) {
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
    return true;
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
	 * Defer pretty printing to superclass.
	 * @ast method 
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:105
   */
    public void toString(StringBuffer s) {
		super.toString(s);
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:302
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
  private TypeDecl type_compute() {  return typeFloat();  }
  /**
   * @apilevel internal
   */
  protected boolean isZero_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean isZero_value;
  /**
	 * @return true if this floating point literal is equivalent to a zero literal
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:22
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
		for(int i = 0; i < digits.length(); i++) {
			char c = digits.charAt(i);
			if (c == 'e' || c == 'p') break;
			if (c != '0' && c != '.') {
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
	 * Parse this literal and return a fresh Constant.
	 * @return a fresh Constant representing this FloatingPointLiteral
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:175
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
			return Constant.create(Float.parseFloat(getDigits()));
		}
		catch (NumberFormatException e) {
			Constant c = Constant.create(0.0f);
			c.error = true;
			return c;
		}
	}
  /**
	 * Utility attribute for literal rewriting.
	 * Any of the NumericLiteral subclasses have already
	 * been rewritten and/or parsed, and should not be
	 * rewritten again.
	 *
	 * @return true if this literal is a "raw", not-yet-parsed NumericLiteral
	 * @attribute syn
   * @aspect Literals
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/Literals.jrag:334
   */
  public boolean needsRewrite() {
    ASTNode$State state = state();
    try {  return false;  }
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
