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
 * @declaredat java.ast:131
 */
public class Dims extends ASTNode<ASTNode> implements Cloneable {
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
  public Dims clone() throws CloneNotSupportedException {
    Dims node = (Dims)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Dims copy() {
      try {
        Dims node = (Dims)clone();
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
  public Dims fullCopy() {
    Dims res = (Dims)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public Dims() {
    super();

    setChild(new Opt(), 0);

  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public Dims(Opt<Expr> p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:14
   */
  protected int numChildren() {
    return 1;
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
   * Setter for ExprOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setExprOpt(Opt<Expr> opt) {
    setChild(opt, 0);
  }
  /**
   * Does this node have a Expr child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasExpr() {
    return getExprOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getExpr() {
    return (Expr)getExprOpt().getChild(0);
  }
  /**
   * Setter for optional child Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setExpr(Expr node) {
    getExprOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Expr> getExprOpt() {
    return (Opt<Expr>)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Expr> getExprOptNoTransform() {
    return (Opt<Expr>)getChildNoTransform(0);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
