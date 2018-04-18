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
 * @production Case : {@link Stmt};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:203
 */
public abstract class Case extends Stmt implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isDAbefore_Variable_values = null;
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
    label_computed = false;
    label_value = null;
    bind_Case_values = null;
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
  public Case clone() throws CloneNotSupportedException {
    Case node = (Case)super.clone();
    node.isDAbefore_Variable_values = null;
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.label_computed = false;
    node.label_value = null;
    node.bind_Case_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:109
   */
  public void jimplify2(Body b) {
    b.addLabel(label());
  }
  /**
   * @ast method 
   * 
   */
  public Case() {
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
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 0;
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
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:431
   */
  @SuppressWarnings({"unchecked", "cast"})
  public abstract boolean constValue(Case c);
  protected java.util.Map isDAbefore_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:570
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAbefore(Variable v) {
    Object _parameters = v;
    if(isDAbefore_Variable_values == null) isDAbefore_Variable_values = new java.util.HashMap(4);
    if(isDAbefore_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAbefore_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAbefore_Variable_value = isDAbefore_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDAbefore_Variable_values.put(_parameters, Boolean.valueOf(isDAbefore_Variable_value));
    return isDAbefore_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAbefore_compute(Variable v) {  return getParent().getParent() instanceof Block && ((Block)getParent().getParent()).isDAbefore(v)
    && super.isDAbefore(v);  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:574
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
    Object _parameters = v;
    if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
    if(isDAafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {  return isDAbefore(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1025
   */
  public boolean isDUbefore(Variable v) {
    ASTNode$State state = state();
    try {  return getParent().getParent() instanceof Block && ((Block)getParent().getParent()).isDUbefore(v)
    && super.isDUbefore(v);  }
    finally {
    }
  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1029
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
    Object _parameters = v;
    if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
    if(isDUafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {  return isDUbefore(v);  }
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:83
   */
  public boolean reachable() {
    ASTNode$State state = state();
    try {  return getParent().getParent() instanceof Block && ((Block)getParent().getParent()).reachable();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean label_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:107
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label() {
    if(label_computed) {
      return label_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_value = label_compute();
      if(isFinal && num == state().boundariesCrossed) label_computed = true;
    return label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_compute() {  return newLabel();  }
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
  /* Type checking for case labels need not be changed as it
	 * already tests if the case labels have expressions which are
	 * assignable to the switch expression
	 * @attribute syn
   * @aspect StringsInSwitch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/StringsInSwitch.jrag:38
   */
  public boolean isDefaultCase() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  protected java.util.Map bind_Case_values;
  /**
   * @attribute inh
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:417
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Case bind(Case c) {
    Object _parameters = c;
    if(bind_Case_values == null) bind_Case_values = new java.util.HashMap(4);
    if(bind_Case_values.containsKey(_parameters)) {
      return (Case)bind_Case_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    Case bind_Case_value = getParent().Define_Case_bind(this, null, c);
      if(isFinal && num == state().boundariesCrossed) bind_Case_values.put(_parameters, bind_Case_value);
    return bind_Case_value;
  }
  /**
   * @attribute inh
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:358
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl switchType() {
    ASTNode$State state = state();
    TypeDecl switchType_value = getParent().Define_TypeDecl_switchType(this, null);
    return switchType_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
