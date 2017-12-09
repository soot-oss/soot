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
 * @production BranchTargetStmt : {@link Stmt};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:196
 */
public abstract class BranchTargetStmt extends Stmt implements Cloneable, BranchPropagation {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    reachableBreak_computed = false;
    reachableContinue_computed = false;
    targetBranches_computed = false;
    targetBranches_value = null;
    escapedBranches_computed = false;
    escapedBranches_value = null;
    branches_computed = false;
    branches_value = null;
    targetContinues_computed = false;
    targetContinues_value = null;
    targetBreaks_computed = false;
    targetBreaks_value = null;
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
  public BranchTargetStmt clone() throws CloneNotSupportedException {
    BranchTargetStmt node = (BranchTargetStmt)super.clone();
    node.reachableBreak_computed = false;
    node.reachableContinue_computed = false;
    node.targetBranches_computed = false;
    node.targetBranches_value = null;
    node.escapedBranches_computed = false;
    node.escapedBranches_value = null;
    node.branches_computed = false;
    node.branches_value = null;
    node.targetContinues_computed = false;
    node.targetContinues_value = null;
    node.targetBreaks_computed = false;
    node.targetBreaks_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:57
   */
  public void collectBranches(Collection c) {
    c.addAll(escapedBranches());
  }
  /**
   * @ast method 
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:156
   */
  public Stmt branchTarget(Stmt branchStmt) {
    if(targetBranches().contains(branchStmt))
      return this;
    return super.branchTarget(branchStmt);
  }
  /**
   * @ast method 
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:194
   */
  public void collectFinally(Stmt branchStmt, ArrayList list) {
    if(targetBranches().contains(branchStmt))
      return;
    super.collectFinally(branchStmt, list);
  }
  /**
   * @ast method 
   * 
   */
  public BranchTargetStmt() {
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
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:39
   */
  @SuppressWarnings({"unchecked", "cast"})
  public abstract boolean targetOf(ContinueStmt stmt);
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:40
   */
  @SuppressWarnings({"unchecked", "cast"})
  public abstract boolean targetOf(BreakStmt stmt);
  /**
   * @apilevel internal
   */
  protected boolean reachableBreak_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean reachableBreak_value;
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:49
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean reachableBreak() {
    if(reachableBreak_computed) {
      return reachableBreak_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    reachableBreak_value = reachableBreak_compute();
      if(isFinal && num == state().boundariesCrossed) reachableBreak_computed = true;
    return reachableBreak_value;
  }
  /**
   * @apilevel internal
   */
  private boolean reachableBreak_compute() {
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(stmt.reachable())
        return true;
    }
    return false;
  }
  /**
   * @apilevel internal
   */
  protected boolean reachableContinue_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean reachableContinue_value;
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:91
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean reachableContinue() {
    if(reachableContinue_computed) {
      return reachableContinue_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    reachableContinue_value = reachableContinue_compute();
      if(isFinal && num == state().boundariesCrossed) reachableContinue_computed = true;
    return reachableContinue_value;
  }
  /**
   * @apilevel internal
   */
  private boolean reachableContinue_compute() {
    for(Iterator iter = targetContinues().iterator(); iter.hasNext(); ) {
      Stmt stmt = (Stmt)iter.next();
      if(stmt.reachable())
        return true;
    }
    return false;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:82
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
    for(Iterator iter = branches().iterator(); iter.hasNext(); ) {
      Object o = iter.next();
      if(o instanceof ContinueStmt && targetOf((ContinueStmt)o))
        set.add(o);
      if(o instanceof BreakStmt && targetOf((BreakStmt)o))
        set.add(o);
    }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:94
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
    for(Iterator iter = branches().iterator(); iter.hasNext(); ) {
      Object o = iter.next();
      if(o instanceof ContinueStmt && !targetOf((ContinueStmt)o))
        set.add(o);
      if(o instanceof BreakStmt && !targetOf((BreakStmt)o))
        set.add(o);
      if(o instanceof ReturnStmt)
        set.add(o);
    }
    return set;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:108
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
    super.collectBranches(set);
    return set;
  }
  /**
   * @apilevel internal
   */
  protected boolean targetContinues_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection targetContinues_value;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:215
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection targetContinues() {
    if(targetContinues_computed) {
      return targetContinues_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    targetContinues_value = targetContinues_compute();
      if(isFinal && num == state().boundariesCrossed) targetContinues_computed = true;
    return targetContinues_value;
  }
  /**
   * @apilevel internal
   */
  private Collection targetContinues_compute() {
    HashSet set = new HashSet();
    for(Iterator iter = targetBranches().iterator(); iter.hasNext(); ) {
      Object o = iter.next();
      if(o instanceof ContinueStmt)
        set.add(o);
    }
    if(getParent() instanceof LabeledStmt) {
      for(Iterator iter = ((LabeledStmt)getParent()).targetBranches().iterator(); iter.hasNext(); ) {
        Object o = iter.next();
        if(o instanceof ContinueStmt)
          set.add(o);
      }
    }
    return set;
  }
  /**
   * @apilevel internal
   */
  protected boolean targetBreaks_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection targetBreaks_value;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:232
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection targetBreaks() {
    if(targetBreaks_computed) {
      return targetBreaks_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    targetBreaks_value = targetBreaks_compute();
      if(isFinal && num == state().boundariesCrossed) targetBreaks_computed = true;
    return targetBreaks_value;
  }
  /**
   * @apilevel internal
   */
  private Collection targetBreaks_compute() {
    HashSet set = new HashSet();
    for(Iterator iter = targetBranches().iterator(); iter.hasNext(); ) {
      Object o = iter.next();
      if(o instanceof BreakStmt)
        set.add(o);
    }
    return set;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
