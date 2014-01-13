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
 * @production EqualityExpr : {@link RelationalExpr};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:181
 */
public abstract class EqualityExpr extends RelationalExpr implements Cloneable {
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
  public EqualityExpr clone() throws CloneNotSupportedException {
    EqualityExpr node = (EqualityExpr)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:220
   */
  public void typeCheck() {
    TypeDecl left = getLeftOperand().type();
    TypeDecl right = getRightOperand().type();
    if(left.isNumericType() && right.isNumericType())
      return;
    else if(left.isBoolean() && right.isBoolean())
      return;
    else if((left.isReferenceType() || left.isNull()) && (right.isReferenceType() || right.isNull())) {
      if(left.castingConversionTo(right) || right.castingConversionTo(left))
        return;
    }
    error(left.typeName() + " can not be compared to " + right.typeName());
  }
  /**
   * @ast method 
   * 
   */
  public EqualityExpr() {
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
  public EqualityExpr(Expr p0, Expr p1) {
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
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
