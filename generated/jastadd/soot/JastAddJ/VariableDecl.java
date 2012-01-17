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
 * @declaredat java.ast:82
 */
public class VariableDecl extends ASTNode<ASTNode> implements Cloneable {
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
  public VariableDecl clone() throws CloneNotSupportedException {
    VariableDecl node = (VariableDecl)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VariableDecl copy() {
      try {
        VariableDecl node = (VariableDecl)clone();
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
  public VariableDecl fullCopy() {
    VariableDecl res = (VariableDecl)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:182
   */
  public VariableDeclaration createVariableDeclarationFrom(Modifiers modifiers, Access type) {
    VariableDeclaration decl = new VariableDeclaration(
      modifiers,
      type.addArrayDims(getDimsList()),
      getID(),
      getInitOpt()
    );
    decl.setStart(start); // copy location information
    decl.setEnd(end); // copy location information
    decl.IDstart = IDstart;
    decl.IDend = IDend;
    return decl;
  }
  /**
   * @ast method 
   * @aspect VariableDeclarationTransformation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:196
   */
  public FieldDeclaration createFieldDeclarationFrom(Modifiers modifiers, Access type) {
    FieldDeclaration decl = new FieldDeclaration(
      modifiers,
      type.addArrayDims(getDimsList()),
      getID(),
      getInitOpt()
    );
    decl.setStart(start); // copy location information
    decl.setEnd(end); // copy location information
    decl.IDstart = IDstart;
    decl.IDend = IDend;
    return decl;
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public VariableDecl() {
    super();

    setChild(new List(), 0);
    setChild(new Opt(), 1);

  }
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  public VariableDecl(String p0, List<Dims> p1, Opt<Expr> p2) {
    setID(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * @declaredat java.ast:14
   */
  public VariableDecl(beaver.Symbol p0, List<Dims> p1, Opt<Expr> p2) {
    setID(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:22
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:28
   */
  public boolean mayHaveRewrite() {
    return false;
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
  /**   * @apilevel internal   * @ast method 
   * @declaredat java.ast:8
   */
  
  /**   * @apilevel internal   */  protected String tokenString_ID;
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  
  public int IDstart;
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  
  public int IDend;
  /**
   * @ast method 
   * @declaredat java.ast:11
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Getter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:22
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for DimsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setDimsList(List<Dims> list) {
    setChild(list, 0);
  }
  /**
   * @return number of children in DimsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumDims() {
    return getDimsList().getNumChild();
  }
  /**
   * Getter for child in list DimsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Dims getDims(int i) {
    return (Dims)getDimsList().getChild(i);
  }
  /**
   * Add element to list DimsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addDims(Dims node) {
    List<Dims> list = (parent == null || state == null) ? getDimsListNoTransform() : getDimsList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addDimsNoTransform(Dims node) {
    List<Dims> list = getDimsListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list DimsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setDims(Dims node, int i) {
    List<Dims> list = getDimsList();
    list.setChild(node, i);
  }
  /**
   * Getter for Dims list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<Dims> getDimss() {
    return getDimsList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<Dims> getDimssNoTransform() {
    return getDimsListNoTransform();
  }
  /**
   * Getter for list DimsList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Dims> getDimsList() {
    List<Dims> list = (List<Dims>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Dims> getDimsListNoTransform() {
    return (List<Dims>)getChildNoTransform(0);
  }
  /**
   * Setter for InitOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setInitOpt(Opt<Expr> opt) {
    setChild(opt, 1);
  }
  /**
   * Does this node have a Init child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasInit() {
    return getInitOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child Init
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getInit() {
    return (Expr)getInitOpt().getChild(0);
  }
  /**
   * Setter for optional child Init
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setInit(Expr node) {
    getInitOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Expr> getInitOpt() {
    return (Opt<Expr>)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Expr> getInitOptNoTransform() {
    return (Opt<Expr>)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect Variables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/VariableDeclaration.jrag:84
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String name() {
      ASTNode$State state = state();
    String name_value = name_compute();
    return name_value;
  }
  /**
   * @apilevel internal
   */
  private String name_compute() {  return getID();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:41
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getInitOptNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isSource(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:66
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
    if(caller == getInitOptNoTransform()) {
      return null;
    }
    return getParent().Define_TypeDecl_expectedType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
