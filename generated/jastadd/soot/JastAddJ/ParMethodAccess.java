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
 * @declaredat GenericMethods.ast:10
 */
public class ParMethodAccess extends MethodAccess implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    typeArguments_MethodDecl_values = null;
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
  public ParMethodAccess clone() throws CloneNotSupportedException {
    ParMethodAccess node = (ParMethodAccess)super.clone();
    node.typeArguments_MethodDecl_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParMethodAccess copy() {
      try {
        ParMethodAccess node = (ParMethodAccess)clone();
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
  public ParMethodAccess fullCopy() {
    ParMethodAccess res = (ParMethodAccess)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect GenericMethods
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:11
   */
  public void typeCheck() {
    super.typeCheck();
    if(!decl().hostType().isUnknown()) {
      if(!(decl() instanceof ParMethodDecl))
        error("can not have type parameters on a non generic method");
      else {
        ParMethodDecl m = (ParMethodDecl)decl();
        if(!(m instanceof RawMethodDecl) && m.numTypeParameter() != getNumTypeArgument())
          error("generic method " + m.signature() + " requires " + m.numTypeParameter() + " type arguments");
        else {
        }
      }
    }
  }
  /**
   * @ast method 
   * @aspect GenericMethodsPrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:133
   */
  public void toString(StringBuffer s) {
    s.append("<");
    for(int i = 0; i < getNumTypeArgument(); i++) {
      if(i != 0) s.append(", ");
      getTypeArgument(i).toString(s);
    }
    s.append(">");
    super.toString(s);
  }
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:1
   */
  public ParMethodAccess() {
    super();

    setChild(new List(), 0);
    setChild(new List(), 1);

  }
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:9
   */
  public ParMethodAccess(String p0, List<Expr> p1, List<Access> p2) {
    setID(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * @declaredat GenericMethods.ast:14
   */
  public ParMethodAccess(beaver.Symbol p0, List<Expr> p1, List<Access> p2) {
    setID(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:22
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat GenericMethods.ast:28
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
  /**
   * @ast method 
   * @declaredat java.ast:8
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
   * @declaredat java.ast:19
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setArgList(List<Expr> list) {
    setChild(list, 0);
  }
  /**
   * @return number of children in ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumArg() {
    return getArgList().getNumChild();
  }
  /**
   * Getter for child in list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getArg(int i) {
    return (Expr)getArgList().getChild(i);
  }
  /**
   * Add element to list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addArg(Expr node) {
    List<Expr> list = (parent == null || state == null) ? getArgListNoTransform() : getArgList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addArgNoTransform(Expr node) {
    List<Expr> list = getArgListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setArg(Expr node, int i) {
    List<Expr> list = getArgList();
    list.setChild(node, i);
  }
  /**
   * Getter for Arg list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<Expr> getArgs() {
    return getArgList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<Expr> getArgsNoTransform() {
    return getArgListNoTransform();
  }
  /**
   * Getter for list ArgList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getArgList() {
    List<Expr> list = (List<Expr>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Expr> getArgListNoTransform() {
    return (List<Expr>)getChildNoTransform(0);
  }
  /**
   * Setter for TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:5
   */
  public void setTypeArgumentList(List<Access> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:12
   */
  public int getNumTypeArgument() {
    return getTypeArgumentList().getNumChild();
  }
  /**
   * Getter for child in list TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getTypeArgument(int i) {
    return (Access)getTypeArgumentList().getChild(i);
  }
  /**
   * Add element to list TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:27
   */
  public void addTypeArgument(Access node) {
    List<Access> list = (parent == null || state == null) ? getTypeArgumentListNoTransform() : getTypeArgumentList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:34
   */
  public void addTypeArgumentNoTransform(Access node) {
    List<Access> list = getTypeArgumentListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:42
   */
  public void setTypeArgument(Access node, int i) {
    List<Access> list = getTypeArgumentList();
    list.setChild(node, i);
  }
  /**
   * Getter for TypeArgument list.
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:50
   */
  public List<Access> getTypeArguments() {
    return getTypeArgumentList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:56
   */
  public List<Access> getTypeArgumentsNoTransform() {
    return getTypeArgumentListNoTransform();
  }
  /**
   * Getter for list TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat GenericMethods.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeArgumentList() {
    List<Access> list = (List<Access>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat GenericMethods.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeArgumentListNoTransform() {
    return (List<Access>)getChildNoTransform(1);
  }
  protected java.util.Map typeArguments_MethodDecl_values;
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:311
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayList typeArguments(MethodDecl m) {
    Object _parameters = m;
    if(typeArguments_MethodDecl_values == null) typeArguments_MethodDecl_values = new java.util.HashMap(4);
    if(typeArguments_MethodDecl_values.containsKey(_parameters)) {
      return (ArrayList)typeArguments_MethodDecl_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    ArrayList typeArguments_MethodDecl_value = typeArguments_compute(m);
if(isFinal && num == state().boundariesCrossed) typeArguments_MethodDecl_values.put(_parameters, typeArguments_MethodDecl_value);
    return typeArguments_MethodDecl_value;
  }
  /**
   * @apilevel internal
   */
  private ArrayList typeArguments_compute(MethodDecl m) {
    ArrayList typeArguments = new ArrayList();
    for(int i = 0; i < getNumTypeArgument(); i++)
      typeArguments.add(getTypeArgument(i).type());
    return typeArguments;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:96
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeArgumentListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return NameType.TYPE_NAME;
    }
    return super.Define_NameType_nameType(caller, child);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethods.jrag:97
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getTypeArgumentListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return unqualifiedScope().lookupType(name);
    }
    return super.Define_SimpleSet_lookupType(caller, child, name);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
