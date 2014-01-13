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
 * @production AssignSimpleExpr : {@link AssignExpr};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:108
 */
public class AssignSimpleExpr extends AssignExpr implements Cloneable {
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
  public AssignSimpleExpr clone() throws CloneNotSupportedException {
    AssignSimpleExpr node = (AssignSimpleExpr)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AssignSimpleExpr copy() {
    try {
      AssignSimpleExpr node = (AssignSimpleExpr) clone();
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
  public AssignSimpleExpr fullCopy() {
    AssignSimpleExpr tree = (AssignSimpleExpr) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:44
   */
  public void typeCheck() {
    if(!getDest().isVariable())
      error("left hand side is not a variable");
    else if(!sourceType().assignConversionTo(getDest().type(), getSource()) && !sourceType().isUnknown())
      error("can not assign " + getDest() + " of type " + getDest().type().typeName() +
            " a value of type " + sourceType().typeName());
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:50
   */
  public soot.Value eval(Body b) {
    Value lvalue = getDest().eval(b);
    Value rvalue = asRValue(b,
      getSource().type().emitCastTo(b, // Assign conversion
        getSource(),
        getDest().type()
      )
    );
    return getDest().emitStore(b, lvalue, asImmediate(b, rvalue), this);
  }
  /**
   * @ast method 
   * @aspect UncheckedConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/UncheckedConversion.jrag:30
   */
  public void checkWarnings() {
    if (!withinSuppressWarnings("unchecked"))
      checkUncheckedConversion(getSource().type(), getDest().type());
  }
  /**
   * @ast method 
   * 
   */
  public AssignSimpleExpr() {
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
  public AssignSimpleExpr(Expr p0, Expr p1) {
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
   * Replaces the Dest child.
   * @param node The new node to replace the Dest child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setDest(Expr node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Dest child.
   * @return The current node used as the Dest child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getDest() {
    return (Expr)getChild(0);
  }
  /**
   * Retrieves the Dest child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Dest child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getDestNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Replaces the Source child.
   * @param node The new node to replace the Source child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setSource(Expr node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the Source child.
   * @return The current node used as the Source child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getSource() {
    return (Expr)getChild(1);
  }
  /**
   * Retrieves the Source child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Source child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getSourceNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:247
   */
  public String printOp() {
    ASTNode$State state = state();
    try {  return " = ";  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:121
   */
  public TypeDecl sourceType() {
    ASTNode$State state = state();
    try {  return getSource().type();  }
    finally {
    }
  }
  /**
   * @attribute inh
   * @aspect SuppressWarnings
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SuppressWarnings.jrag:15
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean withinSuppressWarnings(String s) {
    ASTNode$State state = state();
    boolean withinSuppressWarnings_String_value = getParent().Define_boolean_withinSuppressWarnings(this, null, s);
    return withinSuppressWarnings_String_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:17
   * @apilevel internal
   */
  public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
    if(caller == getDestNoTransform()) {
      return true;
    }
    else {      return super.Define_boolean_isDest(caller, child);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:27
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getDestNoTransform()) {
      return false;
    }
    else {      return super.Define_boolean_isSource(caller, child);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:36
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
    if(caller == getSourceNoTransform()) {
      return getDest().type();
    }
    else {      return getParent().Define_TypeDecl_assignConvertedType(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
