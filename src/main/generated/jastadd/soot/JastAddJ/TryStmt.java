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
 * @production TryStmt : {@link Stmt} ::= <span class="component">{@link Block}</span> <span class="component">{@link CatchClause}*</span> <span class="component">[Finally:{@link Block}]</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:219
 */
public class TryStmt extends Stmt implements Cloneable, FinallyHost {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    branches_computed = false;
    branches_value = null;
    branchesFromFinally_computed = false;
    branchesFromFinally_value = null;
    targetBranches_computed = false;
    targetBranches_value = null;
    escapedBranches_computed = false;
    escapedBranches_value = null;
    isDAafter_Variable_values = null;
    isDUbefore_Variable_values = null;
    isDUafter_Variable_values = null;
    catchableException_TypeDecl_values = null;
    canCompleteNormally_computed = false;
    label_begin_computed = false;
    label_begin_value = null;
    label_block_end_computed = false;
    label_block_end_value = null;
    label_end_computed = false;
    label_end_value = null;
    label_finally_computed = false;
    label_finally_value = null;
    label_finally_block_computed = false;
    label_finally_block_value = null;
    label_exception_handler_computed = false;
    label_exception_handler_value = null;
    label_catch_end_computed = false;
    label_catch_end_value = null;
    exceptionRanges_computed = false;
    exceptionRanges_value = null;
    handlesException_TypeDecl_values = null;
    typeError_computed = false;
    typeError_value = null;
    typeRuntimeException_computed = false;
    typeRuntimeException_value = null;
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
  public TryStmt clone() throws CloneNotSupportedException {
    TryStmt node = (TryStmt)super.clone();
    node.branches_computed = false;
    node.branches_value = null;
    node.branchesFromFinally_computed = false;
    node.branchesFromFinally_value = null;
    node.targetBranches_computed = false;
    node.targetBranches_value = null;
    node.escapedBranches_computed = false;
    node.escapedBranches_value = null;
    node.isDAafter_Variable_values = null;
    node.isDUbefore_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.catchableException_TypeDecl_values = null;
    node.canCompleteNormally_computed = false;
    node.label_begin_computed = false;
    node.label_begin_value = null;
    node.label_block_end_computed = false;
    node.label_block_end_value = null;
    node.label_end_computed = false;
    node.label_end_value = null;
    node.label_finally_computed = false;
    node.label_finally_value = null;
    node.label_finally_block_computed = false;
    node.label_finally_block_value = null;
    node.label_exception_handler_computed = false;
    node.label_exception_handler_value = null;
    node.label_catch_end_computed = false;
    node.label_catch_end_value = null;
    node.exceptionRanges_computed = false;
    node.exceptionRanges_value = null;
    node.handlesException_TypeDecl_values = null;
    node.typeError_computed = false;
    node.typeError_value = null;
    node.typeRuntimeException_computed = false;
    node.typeRuntimeException_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TryStmt copy() {
    try {
      TryStmt node = (TryStmt) clone();
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
  public TryStmt fullCopy() {
    TryStmt tree = (TryStmt) copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:60
   */
  public void collectBranches(Collection c) {
    c.addAll(escapedBranches());
  }
  /**
   * @ast method 
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:161
   */
  public Stmt branchTarget(Stmt branchStmt) {
    if(targetBranches().contains(branchStmt))
      return this;
    return super.branchTarget(branchStmt);
  }
  /**
   * @ast method 
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:199
   */
  public void collectFinally(Stmt branchStmt, ArrayList list) {
    if(hasFinally() && !branchesFromFinally().contains(branchStmt))
      list.add(this);
    if(targetBranches().contains(branchStmt))
      return;
    super.collectFinally(branchStmt, list);
  }
  /**
   * @ast method 
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:231
   */
  protected boolean reachedException(TypeDecl type) {
    boolean found = false;
    // found is true if the exception type is caught by a catch clause
    for(int i = 0; i < getNumCatchClause() && !found; i++)
      if(getCatchClause(i).handles(type))
        found = true;
    // if an exception is thrown in the block and the exception is not caught and
    // either there is no finally block or the finally block can complete normally
    if(!found && (!hasFinally() || getFinally().canCompleteNormally()) )
      if(getBlock().reachedException(type))
        return true;
    // even if the exception is caught by the catch clauses they may 
    // throw new exceptions
    for(int i = 0; i < getNumCatchClause(); i++)
      if(getCatchClause(i).reachedException(type))
        return true;
    return hasFinally() && getFinally().reachedException(type);
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:707
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("try ");
    getBlock().toString(s);
    for(int i = 0; i < getNumCatchClause(); i++) {
      s.append(indent());
      getCatchClause(i).toString(s);
    }
    if(hasFinally()) {
      s.append(indent());
      s.append("finally ");
      getFinally().toString(s);
    }
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:322
   */
  public void emitFinallyCode(Body b) {
    if(hasFinally()) {
      // Clear cached attributes to force re-evaluation of local variables
      getFinally().flushCaches();
      getFinally().jimplify2(b);
    }
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:358
   */
  public void jimplify2(Body b) {
    ArrayList ranges = exceptionRanges();
    b.addLabel(label_begin());
    ranges.add(label_begin());
    getBlock().jimplify2(b);
    soot.jimple.Stmt label_block_end = null;
    soot.jimple.Stmt label_end = null;
    if(getBlock().canCompleteNormally()) {
      if(hasFinally() && getNumCatchClause() != 0) {
        label_block_end = label_block_end();
        b.addLabel(label_block_end);
      }
      emitFinallyCode(b);
      b.setLine(this);
      if((!hasFinally() || getFinally().canCompleteNormally()) && (getNumCatchClause() != 0 || hasFinally()/*needsFinallyTrap()*/))
        b.add(b.newGotoStmt(label_end = label_end(), this));
    }
    if(getNumCatchClause() != 0) {
      if(label_block_end == null)
        label_block_end = ((BasicCatch)getCatchClause(0)).label();
      ranges.add(label_block_end);
      ranges.add(label_block_end);
      for(int i = 0; i < getNumCatchClause(); i++) {
        //beginExceptionRange(b, ranges);
        getCatchClause(i).jimplify2(b);
        if(getCatchClause(i).getBlock().canCompleteNormally()) {
          b.setLine(getCatchClause(i));
          endExceptionRange(b, ranges);
          emitFinallyCode(b);
          if(!hasFinally() || getFinally().canCompleteNormally())
            b.add(b.newGotoStmt(label_end = label_end(), this));
          beginExceptionRange(b, ranges);
        }
        b.setLine(getCatchClause(i));
        //endExceptionRange(b, ranges);
      }
    }
    if(hasFinally() /*&& needsFinallyTrap()*/) {
      b.addLabel(label_exception_handler());
      emitExceptionHandler(b);
      b.setLine(getFinally());
      //if(getFinally().canCompleteNormally())
      //  b.add(b.newGotoStmt(label_end(), this));
    }
    if(label_end != null)
      b.addLabel(label_end);
    // createExceptionTable
    for(int i = 0; i < getNumCatchClause(); i++) {
      for(Iterator iter = ranges.iterator(); iter.hasNext(); ) {
        soot.jimple.Stmt stmtBegin = (soot.jimple.Stmt)iter.next();
        soot.jimple.Stmt stmtEnd = (soot.jimple.Stmt)iter.next();
        if(stmtBegin != stmtEnd) {
        	soot.jimple.Stmt lbl = ((BasicCatch)getCatchClause(i)).label();
			b.addTrap(
	              ((BasicCatch)getCatchClause(i)).getParameter().type(),
	              stmtBegin,
	              stmtEnd,
	              lbl
	          );
			addFallThroughLabelTag(b, lbl, label_end);
        }
        if(stmtEnd == label_block_end)
          break;
      }
    }
    if(hasFinally() /*&& needsFinallyTrap()*/) {
      for(Iterator iter = ranges.iterator(); iter.hasNext(); ) {
        soot.jimple.Stmt stmtBegin = (soot.jimple.Stmt)iter.next();
        soot.jimple.Stmt stmtEnd;
        if(iter.hasNext())
          stmtEnd = (soot.jimple.Stmt)iter.next();
        else
          stmtEnd = label_exception_handler();
        if(stmtBegin != stmtEnd) {
          soot.jimple.Stmt lbl = label_exception_handler();
		  b.addTrap(typeThrowable(), stmtBegin, stmtEnd, lbl);
  		  addFallThroughLabelTag(b, lbl, label_end);
        }
      }
      /*
      b.addTrap(
        typeThrowable(),
        label_begin(),
        label_exception_handler(),
        label_exception_handler()
      );
      */
    }
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:448
   */
  protected void addFallThroughLabelTag(Body b, soot.jimple.Stmt handler, soot.jimple.Stmt fallThrough) {
	soot.Body body = b.body;
	soot.tagkit.TryCatchTag tag = (soot.tagkit.TryCatchTag) body.getTag(soot.tagkit.TryCatchTag.NAME);
	if(tag == null) {
		tag = new soot.tagkit.TryCatchTag();
		body.addTag(tag);
	}
	tag.register(handler, fallThrough);
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:482
   */
  public void emitExceptionHandler(Body b) {
    Local l = b.newTemp(typeThrowable().getSootType());
    b.setLine(this);
    b.add(b.newIdentityStmt(l, b.newCaughtExceptionRef(this), this));
    emitFinallyCode(b);
    //if(hasFinally() && getFinally().canCompleteNormally()) {
      soot.jimple.Stmt throwStmt = b.newThrowStmt(l, this);
      throwStmt.addTag(new soot.tagkit.ThrowCreatedByCompilerTag());
      b.add(throwStmt);
    //}
  }
  /**
   * @ast method 
   * 
   */
  public TryStmt() {
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
    children = new ASTNode[3];
    setChild(new List(), 1);
    setChild(new Opt(), 2);
  }
  /**
   * @ast method 
   * 
   */
  public TryStmt(Block p0, List<CatchClause> p1, Opt<Block> p2) {
    setChild(p0, 0);
    setChild(p1, 1);
    setChild(p2, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 3;
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
   * Replaces the Block child.
   * @param node The new node to replace the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBlock(Block node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Block child.
   * @return The current node used as the Block child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Block getBlock() {
    return (Block)getChild(0);
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
    return (Block)getChildNoTransform(0);
  }
  /**
   * Replaces the CatchClause list.
   * @param list The new list node to be used as the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setCatchClauseList(List<CatchClause> list) {
    setChild(list, 1);
  }
  /**
   * Retrieves the number of children in the CatchClause list.
   * @return Number of children in the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumCatchClause() {
    return getCatchClauseList().getNumChild();
  }
  /**
   * Retrieves the number of children in the CatchClause list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the CatchClause list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumCatchClauseNoTransform() {
    return getCatchClauseListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the CatchClause list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CatchClause getCatchClause(int i) {
    return (CatchClause)getCatchClauseList().getChild(i);
  }
  /**
   * Append an element to the CatchClause list.
   * @param node The element to append to the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addCatchClause(CatchClause node) {
    List<CatchClause> list = (parent == null || state == null) ? getCatchClauseListNoTransform() : getCatchClauseList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addCatchClauseNoTransform(CatchClause node) {
    List<CatchClause> list = getCatchClauseListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the CatchClause list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setCatchClause(CatchClause node, int i) {
    List<CatchClause> list = getCatchClauseList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the CatchClause list.
   * @return The node representing the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<CatchClause> getCatchClauses() {
    return getCatchClauseList();
  }
  /**
   * Retrieves the CatchClause list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the CatchClause list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<CatchClause> getCatchClausesNoTransform() {
    return getCatchClauseListNoTransform();
  }
  /**
   * Retrieves the CatchClause list.
   * @return The node representing the CatchClause list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<CatchClause> getCatchClauseList() {
    List<CatchClause> list = (List<CatchClause>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the CatchClause list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the CatchClause list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<CatchClause> getCatchClauseListNoTransform() {
    return (List<CatchClause>)getChildNoTransform(1);
  }
  /**
   * Replaces the optional node for the Finally child. This is the {@code Opt} node containing the child Finally, not the actual child!
   * @param opt The new node to be used as the optional node for the Finally child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setFinallyOpt(Opt<Block> opt) {
    setChild(opt, 2);
  }
  /**
   * Check whether the optional Finally child exists.
   * @return {@code true} if the optional Finally child exists, {@code false} if it does not.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public boolean hasFinally() {
    return getFinallyOpt().getNumChild() != 0;
  }
  /**
   * Retrieves the (optional) Finally child.
   * @return The Finally child, if it exists. Returns {@code null} otherwise.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Block getFinally() {
    return (Block)getFinallyOpt().getChild(0);
  }
  /**
   * Replaces the (optional) Finally child.
   * @param node The new node to be used as the Finally child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setFinally(Block node) {
    getFinallyOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getFinallyOpt() {
    return (Opt<Block>)getChild(2);
  }
  /**
   * Retrieves the optional node for child Finally. This is the {@code Opt} node containing the child Finally, not the actual child!
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The optional node for child Finally.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Block> getFinallyOptNoTransform() {
    return (Opt<Block>)getChildNoTransform(2);
  }
  /**
   * @apilevel internal
   */
  protected boolean branches_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection branches_value;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:115
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection branches() {
    if(branches_computed) {
      return branches_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    branches_value = branches_compute();
      if(isFinal && num == state().boundariesCrossed) branches_computed = true;
    return branches_value;
  }
  /**
   * @apilevel internal
   */
  private Collection branches_compute() {
    HashSet set = new HashSet();
    getBlock().collectBranches(set);
    for(int i = 0; i < getNumCatchClause(); i++)
      getCatchClause(i).collectBranches(set);
    return set;
  }
  /**
   * @apilevel internal
   */
  protected boolean branchesFromFinally_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection branchesFromFinally_value;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:123
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection branchesFromFinally() {
    if(branchesFromFinally_computed) {
      return branchesFromFinally_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    branchesFromFinally_value = branchesFromFinally_compute();
      if(isFinal && num == state().boundariesCrossed) branchesFromFinally_computed = true;
    return branchesFromFinally_value;
  }
  /**
   * @apilevel internal
   */
  private Collection branchesFromFinally_compute() {
    HashSet set = new HashSet();
    if(hasFinally())
      getFinally().collectBranches(set);
    return set;
  }
  /**
   * @apilevel internal
   */
  protected boolean targetBranches_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection targetBranches_value;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:131
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection targetBranches() {
    if(targetBranches_computed) {
      return targetBranches_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    targetBranches_value = targetBranches_compute();
      if(isFinal && num == state().boundariesCrossed) targetBranches_computed = true;
    return targetBranches_value;
  }
  /**
   * @apilevel internal
   */
  private Collection targetBranches_compute() {
    HashSet set = new HashSet();
    if(hasFinally() && !getFinally().canCompleteNormally())
      set.addAll(branches());
    return set;
  }
  /**
   * @apilevel internal
   */
  protected boolean escapedBranches_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection escapedBranches_value;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:139
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection escapedBranches() {
    if(escapedBranches_computed) {
      return escapedBranches_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    escapedBranches_value = escapedBranches_compute();
      if(isFinal && num == state().boundariesCrossed) escapedBranches_computed = true;
    return escapedBranches_value;
  }
  /**
   * @apilevel internal
   */
  private Collection escapedBranches_compute() {
    HashSet set = new HashSet();
    if(hasFinally())
      set.addAll(branchesFromFinally());
    if(!hasFinally() || getFinally().canCompleteNormally())
      set.addAll(branches());
    return set;
  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:666
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
  private boolean isDAafter_compute(Variable v) {
    // 16.2.15 4th bullet
    if(!hasFinally()) {
      if(!getBlock().isDAafter(v))
        return false;
      for(int i = 0; i < getNumCatchClause(); i++)
        if(!getCatchClause(i).getBlock().isDAafter(v))
          return false;
      return true;
    }
    else {
      // 16.2.15 5th bullet
      if(getFinally().isDAafter(v))
        return true;
      if(!getBlock().isDAafter(v))
        return false;
      for(int i = 0; i < getNumCatchClause(); i++)
        if(!getCatchClause(i).getBlock().isDAafter(v))
          return false;
      return true;
    }
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:914
   */
  public boolean isDUafterFinally(Variable v) {
    ASTNode$State state = state();
    try {  return getFinally().isDUafter(v);  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:917
   */
  public boolean isDAafterFinally(Variable v) {
    ASTNode$State state = state();
    try {  return getFinally().isDAafter(v);  }
    finally {
    }
  }
  protected java.util.Map isDUbefore_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1185
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUbefore(Variable v) {
    Object _parameters = v;
    if(isDUbefore_Variable_values == null) isDUbefore_Variable_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(isDUbefore_Variable_values.containsKey(_parameters)) {
      Object _o = isDUbefore_Variable_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return ((Boolean)_o).booleanValue();
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      isDUbefore_Variable_values.put(_parameters, _value);
      _value.value = Boolean.valueOf(true);
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      boolean new_isDUbefore_Variable_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_isDUbefore_Variable_value = isDUbefore_compute(v);
        if (new_isDUbefore_Variable_value!=((Boolean)_value.value).booleanValue()) {
          state.CHANGE = true;
          _value.value = Boolean.valueOf(new_isDUbefore_Variable_value);
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(isFinal && num == state().boundariesCrossed) {
        isDUbefore_Variable_values.put(_parameters, new_isDUbefore_Variable_value);
      }
      else {
        isDUbefore_Variable_values.remove(_parameters);
      state.RESET_CYCLE = true;
      isDUbefore_compute(v);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_isDUbefore_Variable_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      boolean new_isDUbefore_Variable_value = isDUbefore_compute(v);
      if (state.RESET_CYCLE) {
        isDUbefore_Variable_values.remove(_parameters);
      }
      else if (new_isDUbefore_Variable_value!=((Boolean)_value.value).booleanValue()) {
        state.CHANGE = true;
        _value.value = new_isDUbefore_Variable_value;
      }
      return new_isDUbefore_Variable_value;
    }
    return ((Boolean)_value.value).booleanValue();
  }
  /**
   * @apilevel internal
   */
  private boolean isDUbefore_compute(Variable v) {  return super.isDUbefore(v);  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1221
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
  private boolean isDUafter_compute(Variable v) {
    // 16.2.14 4th bullet
    if(!hasFinally()) {
      if(!getBlock().isDUafter(v))
        return false;
      for(int i = 0; i < getNumCatchClause(); i++)
        if(!getCatchClause(i).getBlock().isDUafter(v))
          return false;
      return true;
    }
    else
      return getFinally().isDUafter(v);
  }
  protected java.util.Map catchableException_TypeDecl_values;
  /**
   * The block of the try statement can throw an exception of
   * a type assignable to the given type.
   * @attribute syn
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:221
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean catchableException(TypeDecl type) {
    Object _parameters = type;
    if(catchableException_TypeDecl_values == null) catchableException_TypeDecl_values = new java.util.HashMap(4);
    if(catchableException_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)catchableException_TypeDecl_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean catchableException_TypeDecl_value = catchableException_compute(type);
      if(isFinal && num == state().boundariesCrossed) catchableException_TypeDecl_values.put(_parameters, Boolean.valueOf(catchableException_TypeDecl_value));
    return catchableException_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean catchableException_compute(TypeDecl type) {  return getBlock().reachedException(type);  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:113
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
  private boolean canCompleteNormally_compute() {
     boolean anyCatchClauseCompleteNormally = false;
     for(int i = 0; i < getNumCatchClause() && !anyCatchClauseCompleteNormally; i++)
       anyCatchClauseCompleteNormally = getCatchClause(i).getBlock().canCompleteNormally();
     return (getBlock().canCompleteNormally() || anyCatchClauseCompleteNormally) &&
       (!hasFinally() || getFinally().canCompleteNormally());
  }
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:200
   */
  public soot.jimple.Stmt break_label() {
    ASTNode$State state = state();
    try {  return label_finally();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:225
   */
  public soot.jimple.Stmt continue_label() {
    ASTNode$State state = state();
    try {  return label_finally();  }
    finally {
    }
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:339
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
  protected boolean label_block_end_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_block_end_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:340
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label_block_end() {
    if(label_block_end_computed) {
      return label_block_end_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_block_end_value = label_block_end_compute();
      if(isFinal && num == state().boundariesCrossed) label_block_end_computed = true;
    return label_block_end_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_block_end_compute() {  return newLabel();  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:341
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:342
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:343
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:344
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
   * @apilevel internal
   */
  protected boolean label_catch_end_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt label_catch_end_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:345
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt label_catch_end() {
    if(label_catch_end_computed) {
      return label_catch_end_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    label_catch_end_value = label_catch_end_compute();
      if(isFinal && num == state().boundariesCrossed) label_catch_end_computed = true;
    return label_catch_end_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt label_catch_end_compute() {  return newLabel();  }
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:347
   */
  public boolean needsFinallyTrap() {
    ASTNode$State state = state();
    try {  return getNumCatchClause() != 0 || enclosedByExceptionHandler();  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:466
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
   * @attribute syn
   * @aspect PreciseRethrow
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:55
   */
  public boolean modifiedInScope(Variable var) {
    ASTNode$State state = state();
    try {
		if (getBlock().modifiedInScope(var))
			return true;
		for (CatchClause cc : getCatchClauseList())
			if (cc.modifiedInScope(var))
				return true;
		return hasFinally() && getFinally().modifiedInScope(var);
	}
    finally {
    }
  }
  protected java.util.Map handlesException_TypeDecl_values;
  /**
   * @attribute inh
   * @aspect ExceptionHandling
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:49
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean handlesException(TypeDecl exceptionType) {
    Object _parameters = exceptionType;
    if(handlesException_TypeDecl_values == null) handlesException_TypeDecl_values = new java.util.HashMap(4);
    if(handlesException_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)handlesException_TypeDecl_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
      if(isFinal && num == state().boundariesCrossed) handlesException_TypeDecl_values.put(_parameters, Boolean.valueOf(handlesException_TypeDecl_value));
    return handlesException_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeError_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeError_value;
  /**
   * @attribute inh
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:138
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeError() {
    if(typeError_computed) {
      return typeError_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeError_value = getParent().Define_TypeDecl_typeError(this, null);
      if(isFinal && num == state().boundariesCrossed) typeError_computed = true;
    return typeError_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeRuntimeException_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeRuntimeException_value;
  /**
   * @attribute inh
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:139
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeRuntimeException() {
    if(typeRuntimeException_computed) {
      return typeRuntimeException_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeRuntimeException_value = getParent().Define_TypeDecl_typeRuntimeException(this, null);
      if(isFinal && num == state().boundariesCrossed) typeRuntimeException_computed = true;
    return typeRuntimeException_value;
  }
  /**
   * @attribute inh
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:348
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:479
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeThrowable() {
    ASTNode$State state = state();
    TypeDecl typeThrowable_value = getParent().Define_TypeDecl_typeThrowable(this, null);
    return typeThrowable_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:665
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getFinallyOptNoTransform()) {
      return isDAbefore(v);
    }
    else if(caller == getCatchClauseListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return getBlock().isDAbefore(v);
  }
    else if(caller == getBlockNoTransform()) {
      return isDAbefore(v);
    }
    else {      return getParent().Define_boolean_isDAbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1212
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getFinallyOptNoTransform()){
    if(!getBlock().isDUeverywhere(v))
      return false;
    for(int i = 0; i < getNumCatchClause(); i++)
      if(!getCatchClause(i).getBlock().unassignedEverywhere(v, this))
        return false;
    return true;
  }
    else if(caller == getCatchClauseListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
    if(!getBlock().isDUafter(v))
      return false;
    if(!getBlock().isDUeverywhere(v))
      return false;
    return true;
  }
  }
    else if(caller == getBlockNoTransform()) {
      return isDUbefore(v);
    }
    else {      return getParent().Define_boolean_isDUbefore(this, caller, v);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ExceptionHandling.jrag:202
   * @apilevel internal
   */
  public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    if(caller == getBlockNoTransform()){
    for(int i = 0; i < getNumCatchClause(); i++)
      if(getCatchClause(i).handles(exceptionType))
        return true;
    if(hasFinally() && !getFinally().canCompleteNormally())
      return true;
    return handlesException(exceptionType);
  }
    else if(caller == getCatchClauseListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
    if(hasFinally() && !getFinally().canCompleteNormally())
      return true;
    return handlesException(exceptionType);
  }
  }
    else {      return getParent().Define_boolean_handlesException(this, caller, exceptionType);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:121
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getFinallyOptNoTransform()) {
      return reachable();
    }
    else if(caller == getBlockNoTransform()) {
      return reachable();
    }
    else {      return getParent().Define_boolean_reachable(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:128
   * @apilevel internal
   */
  public boolean Define_boolean_reachableCatchClause(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    if(caller == getCatchClauseListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
    for(int i = 0; i < childIndex; i++)
      if(getCatchClause(i).handles(exceptionType))
        return false;
    if(catchableException(exceptionType))
      return true;
    if(exceptionType.mayCatch(typeError()) || exceptionType.mayCatch(typeRuntimeException()))
      return true;
    return false;
  }
  }
    else {      return getParent().Define_boolean_reachableCatchClause(this, caller, exceptionType);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:156
   * @apilevel internal
   */
  public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
    if(caller == getFinallyOptNoTransform()) {
      return reachable();
    }
    else if(caller == getCatchClauseListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return reachable();
  }
    else if(caller == getBlockNoTransform()) {
      return reachable();
    }
    else {      return getParent().Define_boolean_reportUnreachable(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:353
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:462
   * @apilevel internal
   */
  public ArrayList Define_ArrayList_exceptionRanges(ASTNode caller, ASTNode child) {
    if(caller == getCatchClauseListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return exceptionRanges();
  }
    else if(caller == getBlockNoTransform()) {
      return exceptionRanges();
    }
    else {      return getParent().Define_ArrayList_exceptionRanges(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/PreciseRethrow.jrag:138
   * @apilevel internal
   */
  public Collection<TypeDecl> Define_Collection_TypeDecl__caughtExceptions(ASTNode caller, ASTNode child) {
    if(caller == getCatchClauseListNoTransform())  { 
    int index = caller.getIndexOfChild(child);
    {
		Collection<TypeDecl> excp = new HashSet<TypeDecl>();
		getBlock().collectExceptions(excp, this);
		Collection<TypeDecl> caught = new LinkedList<TypeDecl>();
		Iterator<TypeDecl> iter = excp.iterator();
		while (iter.hasNext()) {
			TypeDecl exception = iter.next();
			// this catch clause handles the exception
			if (!getCatchClause(index).handles(exception))
				continue;
			// no previous catch clause handles the exception
			boolean already = false;
			for (int i = 0; i < index; ++i) {
				if (getCatchClause(i).handles(exception)) {
					already = true;
					break;
				}
			}
			if (!already) {
				caught.add(exception);
			}
		}
		return caught;
	}
  }
    else {      return getParent().Define_Collection_TypeDecl__caughtExceptions(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
