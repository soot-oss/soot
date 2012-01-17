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
 * @declaredat java.ast:112
 */
public abstract class AssignAdditiveExpr extends AssignExpr implements Cloneable {
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
  public AssignAdditiveExpr clone() throws CloneNotSupportedException {
    AssignAdditiveExpr node = (AssignAdditiveExpr)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public AssignAdditiveExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public AssignAdditiveExpr(Expr p0, Expr p1) {
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
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
