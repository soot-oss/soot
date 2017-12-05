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
 * @production VarDeclStmt : {@link Stmt} ::= <span class="component">{@link Modifiers}</span> <span class="component">TypeAccess:{@link Access}</span> <span class="component">{@link VariableDecl}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:82
 */
public class VarDeclStmt extends Stmt implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    canCompleteNormally_computed = false;
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
  public VarDeclStmt clone() throws CloneNotSupportedException {
    VarDeclStmt node = (VarDeclStmt)super.clone();
    node.canCompleteNormally_computed = false;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VarDeclStmt copy() {
    try {
      VarDeclStmt node = (VarDeclStmt) clone();
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
  public VarDeclStmt fullCopy() {
    VarDeclStmt tree = (VarDeclStmt) copy();
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
   * @aspect VariableDeclarationTransformation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:163
   */
  private List createVariableDeclarationList() {
    List varList = new List();
    for(int j = 0; j < getNumVariableDecl(); j++) {
      VariableDeclaration v =
        getVariableDecl(j).createVariableDeclarationFrom(
          (Modifiers)getModifiers().fullCopy(),
          (Access)getTypeAccess().fullCopy()
        );
      if(j == 0)
        v.setStart(start);
      else {
        v.getModifiersNoTransform().clearLocations();
        v.getTypeAccessNoTransform().clearLocations();
      }
      varList.add(v);
    }
    return varList;
  }
  /**
   * @ast method 
   * 
   */
  public VarDeclStmt() {
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
    setChild(new List(), 2);
  }
  /**
   * @ast method 
   * 
   */
  public VarDeclStmt(Modifiers p0, Access p1, List<VariableDecl> p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setChild(p2, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 3;
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
   * Replaces the Modifiers child.
   * @param node The new node to replace the Modifiers child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Modifiers child.
   * @return The current node used as the Modifiers child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * Retrieves the Modifiers child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Modifiers child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Replaces the TypeAccess child.
   * @param node The new node to replace the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeAccess(Access node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the TypeAccess child.
   * @return The current node used as the TypeAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Access getTypeAccess() {
    return (Access)getChild(1);
  }
  /**
   * Retrieves the TypeAccess child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the TypeAccess child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(1);
  }
  /**
   * Replaces the VariableDecl list.
   * @param list The new list node to be used as the VariableDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setVariableDeclList(List<VariableDecl> list) {
    setChild(list, 2);
  }
  /**
   * Retrieves the number of children in the VariableDecl list.
   * @return Number of children in the VariableDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumVariableDecl() {
    return getVariableDeclList().getNumChild();
  }
  /**
   * Retrieves the number of children in the VariableDecl list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the VariableDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumVariableDeclNoTransform() {
    return getVariableDeclListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the VariableDecl list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the VariableDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VariableDecl getVariableDecl(int i) {
    return (VariableDecl)getVariableDeclList().getChild(i);
  }
  /**
   * Append an element to the VariableDecl list.
   * @param node The element to append to the VariableDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addVariableDecl(VariableDecl node) {
    List<VariableDecl> list = (parent == null || state == null) ? getVariableDeclListNoTransform() : getVariableDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addVariableDeclNoTransform(VariableDecl node) {
    List<VariableDecl> list = getVariableDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the VariableDecl list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setVariableDecl(VariableDecl node, int i) {
    List<VariableDecl> list = getVariableDeclList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the VariableDecl list.
   * @return The node representing the VariableDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<VariableDecl> getVariableDecls() {
    return getVariableDeclList();
  }
  /**
   * Retrieves the VariableDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the VariableDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<VariableDecl> getVariableDeclsNoTransform() {
    return getVariableDeclListNoTransform();
  }
  /**
   * Retrieves the VariableDecl list.
   * @return The node representing the VariableDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<VariableDecl> getVariableDeclList() {
    List<VariableDecl> list = (List<VariableDecl>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the VariableDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the VariableDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<VariableDecl> getVariableDeclListNoTransform() {
    return (List<VariableDecl>)getChildNoTransform(2);
  }
  /**
   * @apilevel internal
   */
  protected boolean canCompleteNormally_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean canCompleteNormally_value;
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:42
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean canCompleteNormally() {
    if(canCompleteNormally_computed) {
      return canCompleteNormally_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    canCompleteNormally_value = canCompleteNormally_compute();
      if(isFinal && num == state().boundariesCrossed) canCompleteNormally_computed = true;
    return canCompleteNormally_value;
  }
  /**
   * @apilevel internal
   */
  private boolean canCompleteNormally_compute() {  return reachable();  }
  /**
   * @attribute syn
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:55
   */
  public boolean modifiedInScope(Variable var) {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:84
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeAccessNoTransform()) {
      return NameType.TYPE_NAME;
    }
    else {      return getParent().Define_NameType_nameType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:258
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
    if(caller == getVariableDeclListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return null;
  }
    else {      return getParent().Define_TypeDecl_declType(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag at line 144
    if(getNumVariableDecl() == 1) {
      state().duringVariableDeclarationTransformation++;
      ASTNode result = rewriteRule0();
      state().duringVariableDeclarationTransformation--;
      return result;
    }

    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag at line 155
    if(getParent().getParent() instanceof Block && 
      ((Block)getParent().getParent()).getStmtListNoTransform() == getParent() && getNumVariableDecl() > 1) {
    state().duringVariableDeclarationTransformation++;
      List list = (List)getParent();
      int i = list.getIndexOfChild(this);
      List newList = rewriteBlock_getStmt();
      for(int j = 1; j < newList.getNumChildNoTransform(); j++)
        list.insertChild(newList.getChildNoTransform(j), ++i);
        state().duringVariableDeclarationTransformation--;
      return newList.getChildNoTransform(0);
    }
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag at line 159
    if(getParent().getParent() instanceof ForStmt && 
      ((ForStmt)getParent().getParent()).getInitStmtListNoTransform() == getParent() && getNumVariableDecl() > 1) {
    state().duringVariableDeclarationTransformation++;
      List list = (List)getParent();
      int i = list.getIndexOfChild(this);
      List newList = rewriteForStmt_getInitStmt();
      for(int j = 1; j < newList.getNumChildNoTransform(); j++)
        list.insertChild(newList.getChildNoTransform(j), ++i);
        state().duringVariableDeclarationTransformation--;
      return newList.getChildNoTransform(0);
    }
    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:144
   * @apilevel internal
   */  private VariableDeclaration rewriteRule0() {
{
      VariableDeclaration decl = getVariableDecl(0).createVariableDeclarationFrom(getModifiers(), getTypeAccess());
      decl.setStart(start); // copy location information
      decl.setEnd(end); // copy location information
      return decl;
    }  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:155
   * @apilevel internal
   */  private List rewriteBlock_getStmt() {
    return createVariableDeclarationList();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:159
   * @apilevel internal
   */  private List rewriteForStmt_getInitStmt() {
    return createVariableDeclarationList();
  }
}
