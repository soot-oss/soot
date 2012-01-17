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
 * @declaredat java.ast:14
 */
public class Dot extends AbstractDot implements Cloneable {
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
  public Dot clone() throws CloneNotSupportedException {
    Dot node = (Dot)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Dot copy() {
      try {
        Dot node = (Dot)clone();
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
  public Dot fullCopy() {
    Dot res = (Dot)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:99
   */
  public Dot lastDot() {
    Dot node = this;
    while(node.getRightNoTransform() instanceof Dot)
      node = (Dot)node.getRightNoTransform();
    return node;
  }
  /**
   * @ast method 
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:113
   */
  public Dot qualifiesAccess(Access access) {
	  Dot lastDot = lastDot();
	  Expr l = lastDot.getRightNoTransform();
	  Dot dot = new Dot(lastDot.getRightNoTransform(), access);
	  dot.setStart(l.getStart());
	  dot.setEnd(access.getEnd());
	  lastDot.setRight(dot);
	  return this;
  }
  /**
   * @ast method 
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:124
   */
  private Access qualifyTailWith(Access expr) {
    if(getRight/*NoTransform*/() instanceof AbstractDot) {
      AbstractDot dot = (AbstractDot)getRight/*NoTransform*/();
      return expr.qualifiesAccess(dot.getRight/*NoTransform*/());
    }
    return expr;
  }
  /**
   * @ast method 
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:141
   */
  public Access extractLast() {
    return lastDot().getRightNoTransform();
  }
  /**
   * @ast method 
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:144
   */
  public void replaceLast(Access access) {
    lastDot().setRight(access);
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public Dot() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public Dot(Expr p0, Access p1) {
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
    return true;
  }
  /**
   * Setter for Left
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setLeft(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Left
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getLeft() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getLeftNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for Right
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setRight(Access node) {
    setChild(node, 1);
  }
  /**
   * Getter for Right
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Access getRight() {
    return (Access)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Access getRightNoTransform() {
    return (Access)getChildNoTransform(1);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag at line 210
    if(!duringSyntacticClassification() && leftSide().isPackageAccess() && rightSide().isPackageAccess()) {
      state().duringResolveAmbiguousNames++;
      ASTNode result = rewriteRule0();
      state().duringResolveAmbiguousNames--;
      return result;
    }

    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag at line 222
    if(!duringSyntacticClassification() && leftSide().isPackageAccess() && !((Access)leftSide()).hasPrevExpr() && rightSide() instanceof TypeAccess) {
      state().duringResolveAmbiguousNames++;
      ASTNode result = rewriteRule1();
      state().duringResolveAmbiguousNames--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:210
   * @apilevel internal
   */  private Access rewriteRule0() {
{
      PackageAccess left = (PackageAccess)leftSide();
      PackageAccess right = (PackageAccess)rightSide();
      left.setPackage(left.getPackage() + "." + right.getPackage());
      left.setEnd(right.end());
      return qualifyTailWith(left);
    }  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:222
   * @apilevel internal
   */  private Access rewriteRule1() {
{
      PackageAccess left = (PackageAccess)leftSide();
      TypeAccess right = (TypeAccess)rightSide();
      right.setPackage(left.getPackage());
      right.setStart(left.start());
      return qualifyTailWith(right);
    }  }
}
