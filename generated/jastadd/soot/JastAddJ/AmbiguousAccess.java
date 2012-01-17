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
 * @declaredat java.ast:32
 */
public class AmbiguousAccess extends Access implements Cloneable {
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
  public AmbiguousAccess clone() throws CloneNotSupportedException {
    AmbiguousAccess node = (AmbiguousAccess)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AmbiguousAccess copy() {
      try {
        AmbiguousAccess node = (AmbiguousAccess)clone();
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
  public AmbiguousAccess fullCopy() {
    AmbiguousAccess res = (AmbiguousAccess)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:51
   */
  public void nameCheck() {
    error("ambiguous name " + name());
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:33
   */
  public AmbiguousAccess(String name, int start, int end) {
    this(name);
    this.start = this.IDstart = start;
    this.end = this.IDend = end;
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public AmbiguousAccess() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public AmbiguousAccess(String p0) {
    setID(p0);
  }
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  public AmbiguousAccess(beaver.Symbol p0) {
    setID(p0);
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
    return true;
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
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:339
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet qualifiedLookupType(String name) {
      ASTNode$State state = state();
    SimpleSet qualifiedLookupType_String_value = qualifiedLookupType_compute(name);
    return qualifiedLookupType_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet qualifiedLookupType_compute(String name) {  return SimpleSet.emptySet;  }
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:143
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet qualifiedLookupVariable(String name) {
      ASTNode$State state = state();
    SimpleSet qualifiedLookupVariable_String_value = qualifiedLookupVariable_compute(name);
    return qualifiedLookupVariable_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet qualifiedLookupVariable_compute(String name) {  return SimpleSet.emptySet;  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:809
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
  private String dumpString_compute() {  return getClass().getName() + " [" + getID() + "]";  }
  /**
   * @attribute syn
   * @aspect Names
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:16
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
   * @attribute syn
   * @aspect SyntacticClassification
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:112
   */
  @SuppressWarnings({"unchecked", "cast"})
  public NameType predNameType() {
      ASTNode$State state = state();
    NameType predNameType_value = predNameType_compute();
    return predNameType_value;
  }
  /**
   * @apilevel internal
   */
  private NameType predNameType_compute() {  return NameType.AMBIGUOUS_NAME;  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag at line 194
    if(!duringSyntacticClassification()) {
      state().duringResolveAmbiguousNames++;
      ASTNode result = rewriteRule0();
      state().duringResolveAmbiguousNames--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:194
   * @apilevel internal
   */  private Access rewriteRule0() {
{
      if(!lookupVariable(name()).isEmpty()) {
        return new VarAccess(name(), start(), end());
      }
      else if(!lookupType(name()).isEmpty()) {
        return new TypeAccess(name(), start(), end());
      }
      else {
        return new PackageAccess(name(), start(), end());
      }
    }  }
}
