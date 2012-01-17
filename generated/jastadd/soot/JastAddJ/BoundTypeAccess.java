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
 * @declaredat BoundNames.ast:8
 */
public class BoundTypeAccess extends TypeAccess implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    decls_computed = false;
    decls_value = null;
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
  public BoundTypeAccess clone() throws CloneNotSupportedException {
    BoundTypeAccess node = (BoundTypeAccess)super.clone();
    node.decls_computed = false;
    node.decls_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BoundTypeAccess copy() {
      try {
        BoundTypeAccess node = (BoundTypeAccess)clone();
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
  public BoundTypeAccess fullCopy() {
    BoundTypeAccess res = (BoundTypeAccess)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect GenericsTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:304
   */
  public boolean isRaw() {
    return getTypeDecl().isRawType();
  }
  /**
   * @ast method 
   * @declaredat BoundNames.ast:1
   */
  public BoundTypeAccess() {
    super();


  }
  /**
   * @ast method 
   * @declaredat BoundNames.ast:7
   */
  public BoundTypeAccess(String p0, String p1, TypeDecl p2) {
    setPackage(p0);
    setID(p1);
    setTypeDecl(p2);
  }
  /**
   * @ast method 
   * @declaredat BoundNames.ast:12
   */
  public BoundTypeAccess(beaver.Symbol p0, beaver.Symbol p1, TypeDecl p2) {
    setPackage(p0);
    setID(p1);
    setTypeDecl(p2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat BoundNames.ast:20
   */
  protected int numChildren() {
    return 0;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat BoundNames.ast:26
   */
  public boolean mayHaveRewrite() {
    return false;
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
   * @ast method 
   * @declaredat java.ast:8
   */
  public void setPackage(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setPackage is only valid for String lexemes");
    tokenString_Package = (String)symbol.value;
    Packagestart = symbol.getStart();
    Packageend = symbol.getEnd();
  }
  /**
   * Getter for lexeme Package
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  public String getPackage() {
    return tokenString_Package != null ? tokenString_Package : "";
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
   * Setter for lexeme TypeDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat BoundNames.ast:5
   */
  public void setTypeDecl(TypeDecl value) {
    tokenTypeDecl_TypeDecl = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat BoundNames.ast:8
   */
  
  /**   * @apilevel internal   */  protected TypeDecl tokenTypeDecl_TypeDecl;
  /**
   * Getter for lexeme TypeDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat BoundNames.ast:13
   */
  public TypeDecl getTypeDecl() {
    return tokenTypeDecl_TypeDecl;
  }
  /**
   * @apilevel internal
   */
  protected boolean decls_computed = false;
  /**
   * @apilevel internal
   */
  protected SimpleSet decls_value;
  /**
   * @attribute syn
   * @aspect BoundNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BoundNames.jrag:93
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet decls() {
    if(decls_computed) {
      return decls_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    decls_value = decls_compute();
if(isFinal && num == state().boundariesCrossed) decls_computed = true;
    return decls_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet decls_compute() {  return SimpleSet.emptySet.add(getTypeDecl());  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:818
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String dumpString() {
      ASTNode$State state = state();
    String dumpString_value = dumpString_compute();
    return dumpString_value;
  }
  /**
   * @apilevel internal
   */
  private String dumpString_compute() {  return getClass().getName() + " [" + getTypeDecl().fullName() + "]";  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
