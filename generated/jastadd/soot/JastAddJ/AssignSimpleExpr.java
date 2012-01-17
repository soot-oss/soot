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
 * @declaredat java.ast:105
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
        AssignSimpleExpr node = (AssignSimpleExpr)clone();
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
  public AssignSimpleExpr fullCopy() {
    AssignSimpleExpr res = (AssignSimpleExpr)copy();
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
   * @declaredat java.ast:1
   */
  public AssignSimpleExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public AssignSimpleExpr(Expr p0, Expr p1) {
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
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:248
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String printOp() {
      ASTNode$State state = state();
    String printOp_value = printOp_compute();
    return printOp_value;
  }
  /**
   * @apilevel internal
   */
  private String printOp_compute() {  return " = ";  }
  /**
   * @attribute syn
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:121
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl sourceType() {
      ASTNode$State state = state();
    TypeDecl sourceType_value = sourceType_compute();
    return sourceType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl sourceType_compute() {  return getSource().type();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:17
   * @apilevel internal
   */
  public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
    if(caller == getDestNoTransform()) {
      return true;
    }
    return super.Define_boolean_isDest(caller, child);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:27
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getDestNoTransform()) {
      return false;
    }
    return super.Define_boolean_isSource(caller, child);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:36
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
    if(caller == getSourceNoTransform()) {
      return getDest().type();
    }
    return getParent().Define_TypeDecl_assignConvertedType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
