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
 * @declaredat StaticImports.ast:3
 */
public class SingleStaticImportDecl extends StaticImportDecl implements Cloneable {
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
  public SingleStaticImportDecl clone() throws CloneNotSupportedException {
    SingleStaticImportDecl node = (SingleStaticImportDecl)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SingleStaticImportDecl copy() {
      try {
        SingleStaticImportDecl node = (SingleStaticImportDecl)clone();
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
  public SingleStaticImportDecl fullCopy() {
    SingleStaticImportDecl res = (SingleStaticImportDecl)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:61
   */
  public void typeCheck() { 
    if(!getAccess().type().typeName().equals(typeName()) && !getAccess().type().isUnknown())
      error("Single-type import " + typeName() + " is not the canonical name of type " + getAccess().type().typeName());
  }
  /**
   * @ast method 
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:93
   */
  public void nameCheck() {
    if(importedFields(name()).isEmpty() && importedMethods(name()).isEmpty() && importedTypes(name()).isEmpty() &&
       !getAccess().type().isUnknown()) {
      error("Semantic Error: At least one static member named " + name() + " must be available in static imported type " + type().fullName());
    }
  }
  /**
   * @ast method 
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:207
   */
  public void toString(StringBuffer s) {
    s.append("import static ");
    getAccess().toString(s);
    s.append("." + getID());
    s.append(";\n");
  }
  /**
   * @ast method 
   * @declaredat StaticImports.ast:1
   */
  public SingleStaticImportDecl() {
    super();


  }
  /**
   * @ast method 
   * @declaredat StaticImports.ast:7
   */
  public SingleStaticImportDecl(Access p0, String p1) {
    setChild(p0, 0);
    setID(p1);
  }
  /**
   * @ast method 
   * @declaredat StaticImports.ast:11
   */
  public SingleStaticImportDecl(Access p0, beaver.Symbol p1) {
    setChild(p0, 0);
    setID(p1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat StaticImports.ast:18
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat StaticImports.ast:24
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
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat StaticImports.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat StaticImports.ast:8
   */
  
  /**   * @apilevel internal   */  protected String tokenString_ID;
  /**
   * @ast method 
   * @declaredat StaticImports.ast:9
   */
  
  public int IDstart;
  /**
   * @ast method 
   * @declaredat StaticImports.ast:10
   */
  
  public int IDend;
  /**
   * @ast method 
   * @declaredat StaticImports.ast:11
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
   * @declaredat StaticImports.ast:22
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:54
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
      ASTNode$State state = state();
    TypeDecl type_value = type_compute();
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return getAccess().type();  }
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:99
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:203
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getAccessNoTransform()) {
      return NameType.TYPE_NAME;
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
