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
 * @declaredat java.ast:23
 */
public class ArrayTypeWithSizeAccess extends ArrayTypeAccess implements Cloneable {
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
  public ArrayTypeWithSizeAccess clone() throws CloneNotSupportedException {
    ArrayTypeWithSizeAccess node = (ArrayTypeWithSizeAccess)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayTypeWithSizeAccess copy() {
      try {
        ArrayTypeWithSizeAccess node = (ArrayTypeWithSizeAccess)clone();
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
  public ArrayTypeWithSizeAccess fullCopy() {
    ArrayTypeWithSizeAccess res = (ArrayTypeWithSizeAccess)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:495
   */
  public void toString(StringBuffer s) {
    getAccess().toString(s);
    s.append("[");
    getExpr().toString(s);
    s.append("]");
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:555
   */
  public void typeCheck() {
    super.typeCheck();
    if(!getExpr().type().unaryNumericPromotion().isInt())
      error(getExpr().type().typeName() + " is not int after unary numeric promotion");
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:663
   */
  public void addArraySize(Body b, ArrayList list) {
    getAccess().addArraySize(b, list);
    list.add(asImmediate(b, getExpr().eval(b)));
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public ArrayTypeWithSizeAccess() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public ArrayTypeWithSizeAccess(Access p0, Expr p1) {
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
   * Setter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Getter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Access getAccess() {
    return (Access)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Access getAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * Setter for Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setExpr(Expr node) {
    setChild(node, 1);
  }
  /**
   * Getter for Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getExpr() {
    return (Expr)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getExprNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * Setter for lexeme Package
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setPackage(String value) {
    tokenString_Package = value;
  }
  /**
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:358
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
      ASTNode$State state = state();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {  return getExpr().isDAafter(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:837
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
      ASTNode$State state = state();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {  return getExpr().isDUafter(v);  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:36
   * @apilevel internal
   */
  public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
    if(caller == getExprNoTransform()) {
      return false;
    }
    return getParent().Define_boolean_isDest(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:37
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getExprNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isSource(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:359
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getExprNoTransform()) {
      return getAccess().isDAafter(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:838
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getExprNoTransform()) {
      return getAccess().isDUafter(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:31
   * @apilevel internal
   */
  public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
    if(caller == getExprNoTransform()) {
      return unqualifiedScope().lookupMethod(name);
    }
    return getParent().Define_Collection_lookupMethod(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:91
   * @apilevel internal
   */
  public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
    if(caller == getExprNoTransform()) {
      return unqualifiedScope().hasPackage(packageName);
    }
    return getParent().Define_boolean_hasPackage(this, caller, packageName);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:168
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getExprNoTransform()) {
      return unqualifiedScope().lookupType(name);
    }
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:136
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getExprNoTransform()) {
      return unqualifiedScope().lookupVariable(name);
    }
    return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:123
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getExprNoTransform()) {
      return NameType.EXPRESSION_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
