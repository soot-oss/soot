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
 * @production SynchronizedStmt : {@link Stmt} ::= <span class="component">{@link Expr}</span> <span class="component">{@link Block}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:217
 */
public class SynchronizedStmt extends Stmt implements Cloneable, FinallyHost {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
    canCompleteNormally_computed = false;
    monitor_Body_values = null;
    exceptionRanges_computed = false;
    exceptionRanges_value = null;
    label_begin_computed = false;
    label_begin_value = null;
    label_end_computed = false;
    label_end_value = null;
    label_finally_computed = false;
    label_finally_value = null;
    label_finally_block_computed = false;
    label_finally_block_value = null;
    label_exception_handler_computed = false;
    label_exception_handler_value = null;
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
  public SynchronizedStmt clone() throws CloneNotSupportedException {
    SynchronizedStmt node = (SynchronizedStmt)super.clone();
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.canCompleteNormally_computed = false;
    node.monitor_Body_values = null;
    node.exceptionRanges_computed = false;
    node.exceptionRanges_value = null;
    node.label_begin_computed = false;
    node.label_begin_value = null;
    node.label_end_computed = false;
    node.label_end_value = null;
    node.label_finally_computed = false;
    node.label_finally_value = null;
    node.label_finally_block_computed = false;
    node.label_finally_block_value = null;
    node.label_exception_handler_computed = false;
    node.label_exception_handler_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SynchronizedStmt copy() {
    try {
      SynchronizedStmt node = (SynchronizedStmt) clone();
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
  public SynchronizedStmt fullCopy() {
    SynchronizedStmt tree = (SynchronizedStmt) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:206
   */
  public void collectFinally(Stmt branchStmt, ArrayList list) {
    list.add(this);
    super.collectFinally(branchStmt, list);
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:699
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("synchronized(");
    getExpr().toString(s);
    s.append(") ");
    getBlock().toString(s);
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:362
   */
  public void typeCheck() {
    TypeDecl type = getExpr().type();
    if(!type.isReferenceType() || type.isNull())
      error("*** The type of the expression must be a reference");
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:334
   */
  public void emitFinallyCode(Body b) {
    b.setLine(this);
    b.add(b.newExitMonitorStmt(monitor(b), this));
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:500
   */
  public void jimplify2(Body b) {
    b.setLine(this);
    b.add(b.newEnterMonitorStmt(monitor(b), this));
    b.addLabel(label_begin());
    exceptionRanges().add(label_begin());
    getBlock().jimplify2(b);
    if(getBlock().canCompleteNormally()) {
      emitFinallyCode(b);
      b.add(b.newGotoStmt(label_end(), this));
    }
    b.addLabel(label_exception_handler());

    // emitExceptionHandler
    Local l = b.newTemp(typeThrowable().getSootType());
    b.add(b.newIdentityStmt(l, b.newCaughtExceptionRef(this), this));
    emitFinallyCode(b);
    b.addLabel(label_end());
    soot.jimple.Stmt throwStmt = b.newThrowStmt(l, this);
    throwStmt.addTag(new soot.tagkit.ThrowCreatedByCompilerTag());
    b.add(throwStmt);

    // createExceptionTable
    for(Iterator iter = exceptionRanges().iterator(); iter.hasNext(); ) {
      soot.jimple.Stmt stmtBegin = (soot.jimple.Stmt)iter.next();
      soot.jimple.Stmt stmtEnd;
      if(iter.hasNext())
        stmtEnd = (soot.jimple.Stmt)iter.next();
      else
        stmtEnd = label_end();
      if(stmtBegin != stmtEnd)
        b.addTrap(typeThrowable(), stmtBegin, stmtEnd, label_exception_handler());
    }
  }
  /**
   * @ast method 
   * 
   */
  public SynchronizedStmt() {
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
    children = new ASTNode[2];
  }
  /**
   * @ast method 
   * 
   */
  public SynchronizedStmt(Expr p0, Block p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 2;
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
   * Replaces the Expr child.
   * @param node The new node to replace the Expr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setExpr(Expr node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Expr child.
   * @return The current node used as the Expr child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getExpr() {
    return (Expr)getChild(0);
  }
  /**
   * Retrieves the Expr child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Expr child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getExprNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Replaces the Block child.
   * @param node The new node to replace the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBlock(Block node) {
    setChild(node, 1);
  }
  /**
   * Retrieves the Block child.
   * @return The current node used as the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Block getBlock() {
    return (Block)getChild(1);
  }
  /**
   * Retrieves the Block child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Block child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Block getBlockNoTransform() {
    return (Block)getChildNoTransform(1);
  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:655
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
  private boolean isDAafter_compute(Variable v) {  return getBlock().isDAafter(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:915
   */
  public boolean isDUafterFinally(Variable v) {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:918
   */
  public boolean isDAafterFinally(Variable v) {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1178
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
  private boolean isDUafter_compute(Variable v) {  return getBlock().isDUafter(v);  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:110
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
  private boolean canCompleteNormally_compute() {  return getBlock().canCompleteNormally();  }
  protected java.util.Map monitor_Body_values;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:330
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.Local monitor(Body b) {
    Object _parameters = b;
    if(monitor_Body_values == null) monitor_Body_values = new java.util.HashMap(4);
    if(monitor_Body_values.containsKey(_parameters)) {
      return (soot.Local)monitor_Body_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    soot.Local monitor_Body_value = monitor_compute(b);
      if(isFinal && num == state().boundariesCrossed) monitor_Body_values.put(_parameters, monitor_Body_value);
    return monitor_Body_value;
  }
  /**
   * @apilevel internal
   */
  private soot.Local monitor_compute(Body b) {
    return b.newTemp(getExpr().eval(b));
  }
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:355
   */
  public boolean needsFinallyTrap() {
    ASTNode$State state = state();
    try {  return enclosedByExceptionHandler();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean exceptionRanges_computed = false;
  /**
   * @apilevel internal
   */
  protected ArrayList exceptionRanges_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:467
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayList exceptionRanges() {
    if(exceptionRanges_computed) {
      return exceptionRanges_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    exceptionRanges_value = exceptionRanges_compute();
      if(isFinal && num == state().boundariesCrossed) exceptionRanges_computed = true;
    return exceptionRanges_value;
  }
  /**
   * @apilevel internal
   */
  private ArrayList exceptionRanges_compute() {  return new ArrayList();  }
  /**
   * @apilevel internal
   */
  protected boolean label_begin_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_begin_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:494
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label_begin() {
    if(label_begin_computed) {
      return label_begin_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_begin_value = label_begin_compute();
      if(isFinal && num == state().boundariesCrossed) label_begin_computed = true;
    return label_begin_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_begin_compute() {  return newLabel();  }
  /**
   * @apilevel internal
   */
  protected boolean label_end_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_end_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:495
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label_end() {
    if(label_end_computed) {
      return label_end_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_end_value = label_end_compute();
      if(isFinal && num == state().boundariesCrossed) label_end_computed = true;
    return label_end_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_end_compute() {  return newLabel();  }
  /**
   * @apilevel internal
   */
  protected boolean label_finally_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_finally_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:496
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label_finally() {
    if(label_finally_computed) {
      return label_finally_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_finally_value = label_finally_compute();
      if(isFinal && num == state().boundariesCrossed) label_finally_computed = true;
    return label_finally_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_finally_compute() {  return newLabel();  }
  /**
   * @apilevel internal
   */
  protected boolean label_finally_block_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_finally_block_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:497
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label_finally_block() {
    if(label_finally_block_computed) {
      return label_finally_block_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_finally_block_value = label_finally_block_compute();
      if(isFinal && num == state().boundariesCrossed) label_finally_block_computed = true;
    return label_finally_block_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_finally_block_compute() {  return newLabel();  }
  /**
   * @apilevel internal
   */
  protected boolean label_exception_handler_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_exception_handler_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:498
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label_exception_handler() {
    if(label_exception_handler_computed) {
      return label_exception_handler_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_exception_handler_value = label_exception_handler_compute();
      if(isFinal && num == state().boundariesCrossed) label_exception_handler_computed = true;
    return label_exception_handler_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_exception_handler_compute() {  return newLabel();  }
  /**
   * @attribute syn
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:55
   */
  public boolean modifiedInScope(Variable var) {
    ASTNode$State state = state();
    try {  return getBlock().modifiedInScope(var);  }
    finally {
    }
  }
  /**
   * @attribute inh
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:356
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean enclosedByExceptionHandler() {
    ASTNode$State state = state();
    boolean enclosedByExceptionHandler_value = getParent().Define_boolean_enclosedByExceptionHandler(this, null);
    return enclosedByExceptionHandler_value;
  }
  /**
   * @attribute inh
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:480
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeThrowable() {
    ASTNode$State state = state();
    TypeDecl typeThrowable_value = getParent().Define_TypeDecl_typeThrowable(this, null);
    return typeThrowable_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:657
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockNoTransform()) {
      return getExpr().isDAafter(v);
    }
    else if(caller == getExprNoTransform()) {
      return isDAbefore(v);
    }
    else {      return getParent().Define_boolean_isDAbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1180
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockNoTransform()) {
      return getExpr().isDUafter(v);
    }
    else if(caller == getExprNoTransform()) {
      return isDUbefore(v);
    }
    else {      return getParent().Define_boolean_isDUbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:111
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return reachable();
    }
    else {      return getParent().Define_boolean_reachable(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:157
   * @apilevel internal
   */
  public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return reachable();
    }
    else {      return getParent().Define_boolean_reportUnreachable(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:354
   * @apilevel internal
   */
  public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return true;
    }
    else {      return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:463
   * @apilevel internal
   */
  public ArrayList Define_ArrayList_exceptionRanges(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return exceptionRanges();
    }
    else {      return getParent().Define_ArrayList_exceptionRanges(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
