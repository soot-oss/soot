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
 * @declaredat Enums.ast:5
 */
public class EnumInstanceExpr extends ClassInstanceExpr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    getAccess_computed = false;
    getAccess_value = null;
    getArgList_computed = false;
    getArgList_value = null;
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
  public EnumInstanceExpr clone() throws CloneNotSupportedException {
    EnumInstanceExpr node = (EnumInstanceExpr)super.clone();
    node.getAccess_computed = false;
    node.getAccess_value = null;
    node.getArgList_computed = false;
    node.getArgList_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public EnumInstanceExpr copy() {
      try {
        EnumInstanceExpr node = (EnumInstanceExpr)clone();
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
  public EnumInstanceExpr fullCopy() {
    EnumInstanceExpr res = (EnumInstanceExpr)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @declaredat Enums.ast:1
   */
  public EnumInstanceExpr() {
    super();

    setChild(new Opt(), 0);
    setChild(new List(), 2);

  }
  /**
   * @ast method 
   * @declaredat Enums.ast:9
   */
  public EnumInstanceExpr(Opt<TypeDecl> p0) {
    setChild(p0, 0);
    setChild(null, 1);
    setChild(new List(), 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:17
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Enums.ast:23
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for TypeDeclOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:5
   */
  public void setTypeDeclOpt(Opt<TypeDecl> opt) {
    setChild(opt, 0);
  }
  /**
   * Does this node have a TypeDecl child?
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:12
   */
  public boolean hasTypeDecl() {
    return getTypeDeclOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child TypeDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl getTypeDecl() {
    return (TypeDecl)getTypeDeclOpt().getChild(0);
  }
  /**
   * Setter for optional child TypeDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:27
   */
  public void setTypeDecl(TypeDecl node) {
    getTypeDeclOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<TypeDecl> getTypeDeclOpt() {
    return (Opt<TypeDecl>)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<TypeDecl> getTypeDeclOptNoTransform() {
    return (Opt<TypeDecl>)getChildNoTransform(0);
  }
  /**
   * Setter for Access
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:5
   */
  public void setAccess(Access node) {
    setChild(node, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:11
   */
  public Access getAccessNoTransform() {
    return (Access)getChildNoTransform(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:17
   */
  protected int getAccessChildPosition() {
    return 1;
  }
  /**
   * Setter for ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:5
   */
  public void setArgList(List<Expr> list) {
    setChild(list, 2);
  }
  /**
   * @return number of children in ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:12
   */
  public int getNumArg() {
    return getArgList().getNumChild();
  }
  /**
   * Getter for child in list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getArg(int i) {
    return (Expr)getArgList().getChild(i);
  }
  /**
   * Add element to list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:27
   */
  public void addArg(Expr node) {
    List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:34
   */
  public void addArgNoTransform(Expr node) {
    List<Expr> list = getArgListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:42
   */
  public void setArg(Expr node, int i) {
    List<Expr> list = getArgList();
    list.setChild(node, i);
  }
  /**
   * Getter for Arg list.
   * @apilevel high-level
   * @ast method 
   * @declaredat Enums.ast:50
   */
  public List<Expr> getArgs() {
    return getArgList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:56
   */
  public List<Expr> getArgsNoTransform() {
    return getArgListNoTransform();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:62
   */
  public List<Expr> getArgListNoTransform() {
    return (List<Expr>)getChildNoTransform(2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Enums.ast:68
   */
  protected int getArgListChildPosition() {
    return 2;
  }
  /**
   * @apilevel internal
   */
  protected boolean getAccess_computed = false;
  /**
   * @apilevel internal
   */
  protected Access getAccess_value;
  /*
    3) An enum constant may be followed by arguments, which are passed to the
    constructor of the enum type when the constant is created during class
    initialization as described later in this section. The constructor to be
    invoked is chosen using the normal overloading rules (\u00d4\u00f8\u03a915.12.2). If the
    arguments are omitted, an empty argument list is assumed. 
  * @attribute syn nta
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:209
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getAccess() {
    if(getAccess_computed) {
      return (Access)ASTNode.getChild(this, getAccessChildPosition());
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getAccess_value = getAccess_compute();
      setAccess(getAccess_value);
if(isFinal && num == state().boundariesCrossed) getAccess_computed = true;
    return (Access)ASTNode.getChild(this, getAccessChildPosition());
  }
  /**
   * @apilevel internal
   */
  private Access getAccess_compute() {
    return hostType().createQualifiedAccess();
  }
  /**
   * @apilevel internal
   */
  protected boolean getArgList_computed = false;
  /**
   * @apilevel internal
   */
  protected List<Expr> getArgList_value;
  /**
   * @attribute syn nta
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:213
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getArgList() {
    if(getArgList_computed) {
      return (List<Expr>)ASTNode.getChild(this, getArgListChildPosition());
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getArgList_value = getArgList_compute();
    setArgList(getArgList_value);
if(isFinal && num == state().boundariesCrossed) getArgList_computed = true;
    return (List<Expr>)ASTNode.getChild(this, getArgListChildPosition());
  }
  /**
   * @apilevel internal
   */
  private List<Expr> getArgList_compute() {
    EnumConstant ec = (EnumConstant)getParent().getParent();
    List<EnumConstant> ecs = (List<EnumConstant>)ec.getParent();
    int idx = ecs.getIndexOfChild(ec);
    if(idx == -1)
      throw new Error("internal: cannot determine numeric value of enum constant");
    List<Expr> argList = new List<Expr>();
    argList.add(Literal.buildStringLiteral(ec.name()));
    argList.add(Literal.buildIntegerLiteral(idx));
    for(Expr arg : ec.getArgs())
      argList.add((Expr)arg.fullCopy());
    return argList;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
