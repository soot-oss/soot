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
 * @declaredat java.ast:21
 */
public class PrimitiveTypeAccess extends TypeAccess implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    decls_computed = false;
    decls_value = null;
    getPackage_computed = false;
    getPackage_value = null;
    getID_computed = false;
    getID_value = null;
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
  public PrimitiveTypeAccess clone() throws CloneNotSupportedException {
    PrimitiveTypeAccess node = (PrimitiveTypeAccess)super.clone();
    node.decls_computed = false;
    node.decls_value = null;
    node.getPackage_computed = false;
    node.getPackage_value = null;
    node.getID_computed = false;
    node.getID_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public PrimitiveTypeAccess copy() {
      try {
        PrimitiveTypeAccess node = (PrimitiveTypeAccess)clone();
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
  public PrimitiveTypeAccess fullCopy() {
    PrimitiveTypeAccess res = (PrimitiveTypeAccess)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public PrimitiveTypeAccess() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public PrimitiveTypeAccess(String p0) {
    setName(p0);
  }
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  public PrimitiveTypeAccess(beaver.Symbol p0) {
    setName(p0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:16
   */
  protected int numChildren() {
    return 0;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:22
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for lexeme Name
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setName(String value) {
    tokenString_Name = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat java.ast:8
   */
  
  /**   * @apilevel internal   */  protected String tokenString_Name;
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  
  public int Namestart;
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  
  public int Nameend;
  /**
   * @ast method 
   * @declaredat java.ast:11
   */
  public void setName(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setName is only valid for String lexemes");
    tokenString_Name = (String)symbol.value;
    Namestart = symbol.getStart();
    Nameend = symbol.getEnd();
  }
  /**
   * Getter for lexeme Name
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:22
   */
  public String getName() {
    return tokenString_Name != null ? tokenString_Name : "";
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
  /**   * @apilevel internal   * @ast method 
   * @declaredat java.ast:8
   */
  
  /**   * @apilevel internal   */  protected String tokenString_Package;
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
   * @apilevel internal
   */
  protected boolean decls_computed = false;
  /**
   * @apilevel internal
   */
  protected SimpleSet decls_value;
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:146
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
  private SimpleSet decls_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME, name());  }
  /**
   * @apilevel internal
   */
  protected boolean getPackage_computed = false;
  /**
   * @apilevel internal
   */
  protected String getPackage_value;
  /**
   * @attribute syn nta
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:147
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String getPackage() {
    if(getPackage_computed) {
      return getPackage_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getPackage_value = getPackage_compute();
      setPackage(getPackage_value);
if(isFinal && num == state().boundariesCrossed) getPackage_computed = true;
    return getPackage_value;
  }
  /**
   * @apilevel internal
   */
  private String getPackage_compute() {  return PRIMITIVE_PACKAGE_NAME;  }
  /**
   * @apilevel internal
   */
  protected boolean getID_computed = false;
  /**
   * @apilevel internal
   */
  protected String getID_value;
  /**
   * @attribute syn nta
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:148
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String getID() {
    if(getID_computed) {
      return getID_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getID_value = getID_compute();
      setID(getID_value);
if(isFinal && num == state().boundariesCrossed) getID_computed = true;
    return getID_value;
  }
  /**
   * @apilevel internal
   */
  private String getID_compute() {  return getName();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
