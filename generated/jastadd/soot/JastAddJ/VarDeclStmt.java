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
 * @declaredat java.ast:79
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
        VarDeclStmt node = (VarDeclStmt)clone();
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
  public VarDeclStmt fullCopy() {
    VarDeclStmt res = (VarDeclStmt)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
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
   * @declaredat java.ast:1
   */
  public VarDeclStmt() {
    super();

    setChild(new List(), 2);

  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public VarDeclStmt(Modifiers p0, Access p1, List<VariableDecl> p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setChild(p2, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:16
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:22
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * Setter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Getter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Setter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setTypeAccess(Access node) {
    setChild(node, 1);
  }
  /**
   * Getter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Access getTypeAccess() {
    return (Access)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(1);
  }
  /**
   * Setter for VariableDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setVariableDeclList(List<VariableDecl> list) {
    setChild(list, 2);
  }
  /**
   * @return number of children in VariableDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumVariableDecl() {
    return getVariableDeclList().getNumChild();
  }
  /**
   * Getter for child in list VariableDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VariableDecl getVariableDecl(int i) {
    return (VariableDecl)getVariableDeclList().getChild(i);
  }
  /**
   * Add element to list VariableDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addVariableDecl(VariableDecl node) {
    List<VariableDecl> list = (parent == null || state == null) ? getVariableDeclListNoTransform() : getVariableDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addVariableDeclNoTransform(VariableDecl node) {
    List<VariableDecl> list = getVariableDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list VariableDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setVariableDecl(VariableDecl node, int i) {
    List<VariableDecl> list = getVariableDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for VariableDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<VariableDecl> getVariableDecls() {
    return getVariableDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<VariableDecl> getVariableDeclsNoTransform() {
    return getVariableDeclListNoTransform();
  }
  /**
   * Getter for list VariableDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<VariableDecl> getVariableDeclList() {
    List<VariableDecl> list = (List<VariableDecl>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:84
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeAccessNoTransform()) {
      return NameType.TYPE_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:258
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
    if(caller == getVariableDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return null;
    }
    return getParent().Define_TypeDecl_declType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag at line 144
    if(getNumVariableDecl() == 1) {
      state().duringVariableDeclaration++;
      ASTNode result = rewriteRule0();
      state().duringVariableDeclaration--;
      return result;
    }

    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag at line 155
    if(getParent().getParent() instanceof Block && 
      ((Block)getParent().getParent()).getStmtListNoTransform() == getParent() && getNumVariableDecl() > 1) {
    state().duringVariableDeclaration++;
      List list = (List)getParent();
      int i = list.getIndexOfChild(this);
      List newList = rewriteBlock_getStmt();
      for(int j = 1; j < newList.getNumChildNoTransform(); j++)
        list.insertChild(newList.getChildNoTransform(j), ++i);
        state().duringVariableDeclaration--;
      return newList.getChildNoTransform(0);
    }
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag at line 159
    if(getParent().getParent() instanceof ForStmt && 
      ((ForStmt)getParent().getParent()).getInitStmtListNoTransform() == getParent() && getNumVariableDecl() > 1) {
    state().duringVariableDeclaration++;
      List list = (List)getParent();
      int i = list.getIndexOfChild(this);
      List newList = rewriteForStmt_getInitStmt();
      for(int j = 1; j < newList.getNumChildNoTransform(); j++)
        list.insertChild(newList.getChildNoTransform(j), ++i);
        state().duringVariableDeclaration--;
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
