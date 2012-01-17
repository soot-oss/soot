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
 * @declaredat Generics.ast:30
 */
public class ParameterDeclarationSubstituted extends ParameterDeclaration implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    sourceVariableDecl_computed = false;
    sourceVariableDecl_value = null;
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
  public ParameterDeclarationSubstituted clone() throws CloneNotSupportedException {
    ParameterDeclarationSubstituted node = (ParameterDeclarationSubstituted)super.clone();
    node.sourceVariableDecl_computed = false;
    node.sourceVariableDecl_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParameterDeclarationSubstituted copy() {
      try {
        ParameterDeclarationSubstituted node = (ParameterDeclarationSubstituted)clone();
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
  public ParameterDeclarationSubstituted fullCopy() {
    ParameterDeclarationSubstituted res = (ParameterDeclarationSubstituted)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @declaredat Generics.ast:1
   */
  public ParameterDeclarationSubstituted() {
    super();


  }
  /**
   * @ast method 
   * @declaredat Generics.ast:7
   */
  public ParameterDeclarationSubstituted(Modifiers p0, Access p1, String p2, ParameterDeclaration p3) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
    setOriginal(p3);
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:13
   */
  public ParameterDeclarationSubstituted(Modifiers p0, Access p1, beaver.Symbol p2, ParameterDeclaration p3) {
    setChild(p0, 0);
    setChild(p1, 1);
    setID(p2);
    setOriginal(p3);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:22
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Generics.ast:28
   */
  public boolean mayHaveRewrite() {
    return false;
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
   * Setter for lexeme Original
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setOriginal(ParameterDeclaration value) {
    tokenParameterDeclaration_Original = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat Generics.ast:8
   */
  
  /**   * @apilevel internal   */  protected ParameterDeclaration tokenParameterDeclaration_Original;
  /**
   * Getter for lexeme Original
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:13
   */
  public ParameterDeclaration getOriginal() {
    return tokenParameterDeclaration_Original;
  }
  /**
   * @apilevel internal
   */
  protected boolean sourceVariableDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected Variable sourceVariableDecl_value;
  /**
   * @attribute syn
   * @aspect SourceDeclarations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1287
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Variable sourceVariableDecl() {
    if(sourceVariableDecl_computed) {
      return sourceVariableDecl_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sourceVariableDecl_value = sourceVariableDecl_compute();
if(isFinal && num == state().boundariesCrossed) sourceVariableDecl_computed = true;
    return sourceVariableDecl_value;
  }
  /**
   * @apilevel internal
   */
  private Variable sourceVariableDecl_compute() {  return getOriginal().sourceVariableDecl();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
