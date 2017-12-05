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
 * @production EnumInstanceExpr : {@link ClassInstanceExpr} ::= <span class="component">{@link Access}</span> <span class="component">Arg:{@link Expr}*</span> <span class="component">[{@link TypeDecl}]</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.ast:5
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
      EnumInstanceExpr node = (EnumInstanceExpr) clone();
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
  public EnumInstanceExpr fullCopy() {
    EnumInstanceExpr tree = (EnumInstanceExpr) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
          switch (i) {
          case 1:
            tree.children[i] = null;
            continue;
          case 2:
            tree.children[i] = new List();
            continue;
          }
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
   * 
   */
  public EnumInstanceExpr() {
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
    children = new ASTNode[3];
    setChild(new Opt(), 0);
    setChild(new List(), 2);
  }
  /**
   * @ast method 
   * 
   */
  public EnumInstanceExpr(Opt<TypeDecl> p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 1;
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
   * Replaces the optional node for the TypeDecl child. This is the {@code Opt} node containing the child TypeDecl, not the actual child!
   * @param opt The new node to be used as the optional node for the TypeDecl child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setTypeDeclOpt(Opt<TypeDecl> opt) {
    setChild(opt, 0);
  }
  /**
   * Check whether the optional TypeDecl child exists.
   * @return {@code true} if the optional TypeDecl child exists, {@code false} if it does not.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public boolean hasTypeDecl() {
    return getTypeDeclOpt().getNumChild() != 0;
  }
  /**
   * Retrieves the (optional) TypeDecl child.
   * @return The TypeDecl child, if it exists. Returns {@code null} otherwise.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl getTypeDecl() {
    return (TypeDecl)getTypeDeclOpt().getChild(0);
  }
  /**
   * Replaces the (optional) TypeDecl child.
   * @param node The new node to be used as the TypeDecl child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeDecl(TypeDecl node) {
    getTypeDeclOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<TypeDecl> getTypeDeclOpt() {
    return (Opt<TypeDecl>)getChild(0);
  }
  /**
   * Retrieves the optional node for child TypeDecl. This is the {@code Opt} node containing the child TypeDecl, not the actual child!
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The optional node for child TypeDecl.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<TypeDecl> getTypeDeclOptNoTransform() {
    return (Opt<TypeDecl>)getChildNoTransform(0);
  }
  /**
   * Replaces the Access child.
   * @param node The new node to replace the Access child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setAccess(Access node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the Access child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Access child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Access getAccessNoTransform() {
    return (Access)getChildNoTransform(1);
  }
  /**
   * Retrieves the child position of the optional child Access.
   * @return The the child position of the optional child Access.
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int getAccessChildPosition() {
    return 1;
  }
  /**
   * Replaces the Arg list.
   * @param list The new list node to be used as the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setArgList(List<Expr> list) {
    setChild(list, 2);
  }
  /**
   * Retrieves the number of children in the Arg list.
   * @return Number of children in the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumArg() {
    return getArgList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Arg list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Arg list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumArgNoTransform() {
    return getArgListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Arg list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getArg(int i) {
    return (Expr)getArgList().getChild(i);
  }
  /**
   * Append an element to the Arg list.
   * @param node The element to append to the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addArg(Expr node) {
    List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addArgNoTransform(Expr node) {
    List<Expr> list = getArgListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Arg list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setArg(Expr node, int i) {
    List<Expr> list = getArgList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Arg list.
   * @return The node representing the Arg list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Expr> getArgs() {
    return getArgList();
  }
  /**
   * Retrieves the Arg list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Arg list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Expr> getArgsNoTransform() {
    return getArgListNoTransform();
  }
  /**
   * Retrieves the Arg list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Arg list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Expr> getArgListNoTransform() {
    return (List<Expr>)getChildNoTransform(2);
  }
  /**
   * Retrieves the child position of the Arg list.
   * @return The the child position of the Arg list.
   * @apilevel low-level
   * @ast method 
   * 
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
    invoked is chosen using the normal overloading rules (\ufffd\ufffd\ufffd15.12.2). If the
    arguments are omitted, an empty argument list is assumed. 
  * @attribute syn nta
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:209
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getAccess() {
    if(getAccess_computed) {
      return (Access) getChild(getAccessChildPosition());
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getAccess_value = getAccess_compute();
      setAccess(getAccess_value);
      if(isFinal && num == state().boundariesCrossed) getAccess_computed = true;
    return (Access) getChild(getAccessChildPosition());
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
      return (List<Expr>) getChild(getArgListChildPosition());
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getArgList_value = getArgList_compute();
    setArgList(getArgList_value);
      if(isFinal && num == state().boundariesCrossed) getArgList_computed = true;
    return (List<Expr>) getChild(getArgListChildPosition());
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
