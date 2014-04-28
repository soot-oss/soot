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
 * @production BreakStmt : {@link Stmt} ::= <span class="component">&lt;Label:String&gt;</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:212
 */
public class BreakStmt extends Stmt implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    targetStmt_computed = false;
    targetStmt_value = null;
    finallyList_computed = false;
    finallyList_value = null;
    isDAafter_Variable_values = null;
    isDUafterReachedFinallyBlocks_Variable_values = null;
    isDAafterReachedFinallyBlocks_Variable_values = null;
    isDUafter_Variable_values = null;
    canCompleteNormally_computed = false;
    inSynchronizedBlock_computed = false;
    lookupLabel_String_values = null;
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
  public BreakStmt clone() throws CloneNotSupportedException {
    BreakStmt node = (BreakStmt)super.clone();
    node.targetStmt_computed = false;
    node.targetStmt_value = null;
    node.finallyList_computed = false;
    node.finallyList_value = null;
    node.isDAafter_Variable_values = null;
    node.isDUafterReachedFinallyBlocks_Variable_values = null;
    node.isDAafterReachedFinallyBlocks_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.canCompleteNormally_computed = false;
    node.inSynchronizedBlock_computed = false;
    node.lookupLabel_String_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BreakStmt copy() {
    try {
      BreakStmt node = (BreakStmt) clone();
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
  public BreakStmt fullCopy() {
    BreakStmt tree = (BreakStmt) copy();
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
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:51
   */
  public void collectBranches(Collection c) {
    c.add(this);
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:379
   */
  public void nameCheck() {
    if(!hasLabel() && !insideLoop() && !insideSwitch())
      error("break outside switch or loop");
    else if(hasLabel()) {
      LabeledStmt label = lookupLabel(getLabel());
      if(label == null)
        error("labeled break must have visible matching label");
    }
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:667
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("break ");
    if(hasLabel())
      s.append(getLabel());
    s.append(";");
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:210
   */
  public void jimplify2(Body b) {
    ArrayList list = exceptionRanges();
    if(!inSynchronizedBlock())
      endExceptionRange(b, list);
    for(Iterator iter = finallyList().iterator(); iter.hasNext(); ) {
      FinallyHost stmt = (FinallyHost)iter.next();
      stmt.emitFinallyCode(b);
    }
    if(inSynchronizedBlock())
      endExceptionRange(b, list);
    b.setLine(this);
    b.add(b.newGotoStmt(targetStmt().break_label(), this));
    beginExceptionRange(b, list);
  }
  /**
   * @ast method 
   * 
   */
  public BreakStmt() {
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
   * @ast method 
   * 
   */
  public BreakStmt(String p0) {
    setLabel(p0);
  }
  /**
   * @ast method 
   * 
   */
  public BreakStmt(beaver.Symbol p0) {
    setLabel(p0);
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
   * Replaces the lexeme Label.
   * @param value The new value for the lexeme Label.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setLabel(String value) {
    tokenString_Label = value;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected String tokenString_Label;
  /**
   * @ast method 
   * 
   */
  
  public int Labelstart;
  /**
   * @ast method 
   * 
   */
  
  public int Labelend;
  /**
   * JastAdd-internal setter for lexeme Label using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setLabel(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setLabel is only valid for String lexemes");
    tokenString_Label = (String)symbol.value;
    Labelstart = symbol.getStart();
    Labelend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme Label.
   * @return The value for the lexeme Label.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public String getLabel() {
    return tokenString_Label != null ? tokenString_Label : "";
  }
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:65
   */
  public boolean hasLabel() {
    ASTNode$State state = state();
    try {  return !getLabel().equals("");  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean targetStmt_computed = false;
  /**
   * @apilevel internal
   */
  protected Stmt targetStmt_value;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:148
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Stmt targetStmt() {
    if(targetStmt_computed) {
      return targetStmt_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    targetStmt_value = targetStmt_compute();
      if(isFinal && num == state().boundariesCrossed) targetStmt_computed = true;
    return targetStmt_value;
  }
  /**
   * @apilevel internal
   */
  private Stmt targetStmt_compute() {  return branchTarget(this);  }
  /**
   * @apilevel internal
   */
  protected boolean finallyList_computed = false;
  /**
   * @apilevel internal
   */
  protected ArrayList finallyList_value;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:175
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayList finallyList() {
    if(finallyList_computed) {
      return finallyList_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    finallyList_value = finallyList_compute();
      if(isFinal && num == state().boundariesCrossed) finallyList_computed = true;
    return finallyList_value;
  }
  /**
   * @apilevel internal
   */
  private ArrayList finallyList_compute() {
    ArrayList list = new ArrayList();
    collectFinally(this, list);
    return list;
  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:647
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
  private boolean isDAafter_compute(Variable v) {  return true;  }
  protected java.util.Map isDUafterReachedFinallyBlocks_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:921
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafterReachedFinallyBlocks(Variable v) {
    Object _parameters = v;
    if(isDUafterReachedFinallyBlocks_Variable_values == null) isDUafterReachedFinallyBlocks_Variable_values = new java.util.HashMap(4);
    if(isDUafterReachedFinallyBlocks_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDUafterReachedFinallyBlocks_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDUafterReachedFinallyBlocks_Variable_value = isDUafterReachedFinallyBlocks_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDUafterReachedFinallyBlocks_Variable_values.put(_parameters, Boolean.valueOf(isDUafterReachedFinallyBlocks_Variable_value));
    return isDUafterReachedFinallyBlocks_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafterReachedFinallyBlocks_compute(Variable v) {
    if(!isDUbefore(v) && finallyList().isEmpty())
      return false;
    for(Iterator iter = finallyList().iterator(); iter.hasNext(); ) {
      FinallyHost f = (FinallyHost)iter.next();
      if(!f.isDUafterFinally(v))
        return false;
    }
    return true;
  }
  protected java.util.Map isDAafterReachedFinallyBlocks_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:953
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterReachedFinallyBlocks(Variable v) {
    Object _parameters = v;
    if(isDAafterReachedFinallyBlocks_Variable_values == null) isDAafterReachedFinallyBlocks_Variable_values = new java.util.HashMap(4);
    if(isDAafterReachedFinallyBlocks_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafterReachedFinallyBlocks_Variable_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafterReachedFinallyBlocks_Variable_value = isDAafterReachedFinallyBlocks_compute(v);
      if(isFinal && num == state().boundariesCrossed) isDAafterReachedFinallyBlocks_Variable_values.put(_parameters, Boolean.valueOf(isDAafterReachedFinallyBlocks_Variable_value));
    return isDAafterReachedFinallyBlocks_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterReachedFinallyBlocks_compute(Variable v) {
    if(isDAbefore(v))
      return true;
    if(finallyList().isEmpty())
      return false;
    for(Iterator iter = finallyList().iterator(); iter.hasNext(); ) {
      FinallyHost f = (FinallyHost)iter.next();
      if(!f.isDAafterFinally(v))
        return false;
    }
    return true;
  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1170
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
  private boolean isDUafter_compute(Variable v) {  return true;  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:105
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
  private boolean canCompleteNormally_compute() {  return false;  }
  /**
   * @apilevel internal
   */
  protected boolean inSynchronizedBlock_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean inSynchronizedBlock_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:251
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean inSynchronizedBlock() {
    if(inSynchronizedBlock_computed) {
      return inSynchronizedBlock_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    inSynchronizedBlock_value = inSynchronizedBlock_compute();
      if(isFinal && num == state().boundariesCrossed) inSynchronizedBlock_computed = true;
    return inSynchronizedBlock_value;
  }
  /**
   * @apilevel internal
   */
  private boolean inSynchronizedBlock_compute() {  return !finallyList().isEmpty() && finallyList().iterator().next() instanceof SynchronizedStmt;  }
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
  protected java.util.Map lookupLabel_String_values;
  /**
   * @attribute inh
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:168
   */
  @SuppressWarnings({"unchecked", "cast"})
  public LabeledStmt lookupLabel(String name) {
    Object _parameters = name;
    if(lookupLabel_String_values == null) lookupLabel_String_values = new java.util.HashMap(4);
    if(lookupLabel_String_values.containsKey(_parameters)) {
      return (LabeledStmt)lookupLabel_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    LabeledStmt lookupLabel_String_value = getParent().Define_LabeledStmt_lookupLabel(this, null, name);
      if(isFinal && num == state().boundariesCrossed) lookupLabel_String_values.put(_parameters, lookupLabel_String_value);
    return lookupLabel_String_value;
  }
  /**
   * @attribute inh
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:365
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean insideLoop() {
    ASTNode$State state = state();
    boolean insideLoop_value = getParent().Define_boolean_insideLoop(this, null);
    return insideLoop_value;
  }
  /**
   * @attribute inh
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:374
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean insideSwitch() {
    ASTNode$State state = state();
    boolean insideSwitch_value = getParent().Define_boolean_insideSwitch(this, null);
    return insideSwitch_value;
  }
  /**
   * @attribute inh
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:460
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayList exceptionRanges() {
    ASTNode$State state = state();
    ArrayList exceptionRanges_value = getParent().Define_ArrayList_exceptionRanges(this, null);
    return exceptionRanges_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
