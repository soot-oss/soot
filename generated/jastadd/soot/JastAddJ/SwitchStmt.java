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
 * @declaredat java.ast:199
 */
public class SwitchStmt extends BranchTargetStmt implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    targetOf_ContinueStmt_values = null;
    targetOf_BreakStmt_values = null;
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
    canCompleteNormally_computed = false;
    defaultCase_computed = false;
    defaultCase_value = null;
    end_label_computed = false;
    end_label_value = null;
    typeInt_computed = false;
    typeInt_value = null;
    typeLong_computed = false;
    typeLong_value = null;
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
  public SwitchStmt clone() throws CloneNotSupportedException {
    SwitchStmt node = (SwitchStmt)super.clone();
    node.targetOf_ContinueStmt_values = null;
    node.targetOf_BreakStmt_values = null;
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.canCompleteNormally_computed = false;
    node.defaultCase_computed = false;
    node.defaultCase_value = null;
    node.end_label_computed = false;
    node.end_label_value = null;
    node.typeInt_computed = false;
    node.typeInt_value = null;
    node.typeLong_computed = false;
    node.typeLong_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SwitchStmt copy() {
      try {
        SwitchStmt node = (SwitchStmt)clone();
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
  public SwitchStmt fullCopy() {
    SwitchStmt res = (SwitchStmt)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:554
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("switch (");
    getExpr().toString(s);
    s.append(")");
    getBlock().toString(s);
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:50
   */
  public void jimplify2(Body b) {
    soot.jimple.Stmt cond_label = newLabel();
    soot.jimple.Stmt switch_label = newLabel();

    b.setLine(this);
    b.add(b.newGotoStmt(cond_label, this));
    getBlock().jimplify2(b);
    if(canCompleteNormally()) {
      b.setLine(this);
      b.add(b.newGotoStmt(end_label(), this));
    }
    b.addLabel(cond_label);
    soot.Value expr = asImmediate(b, getExpr().eval(b));

    TreeMap map = new TreeMap();
    for(int i= 0; i < getBlock().getNumStmt(); i++) {
      if(getBlock().getStmt(i) instanceof ConstCase) {
        ConstCase ca = (ConstCase)getBlock().getStmt(i);
        map.put(new Integer(ca.getValue().constant().intValue()), ca);
      }        
    }

    long low = map.isEmpty() ? 0 : ((Integer)map.firstKey()).intValue();
    long high = map.isEmpty() ? 0 : ((Integer)map.lastKey()).intValue();

    long tableSwitchSize = 8L + (high - low + 1L) * 4L;
    long lookupSwitchSize = 4L + map.size() * 8L;

    b.addLabel(switch_label);
    soot.jimple.Stmt defaultStmt = defaultCase() != null ? defaultCase().label() : end_label();
    if(tableSwitchSize < lookupSwitchSize) {
      ArrayList targets = new ArrayList();
      for(long i = low; i <= high; i++) {
        ConstCase ca = (ConstCase)map.get(new Integer((int)i));
        if(ca != null)
          targets.add(ca.label());
        else
          targets.add(defaultStmt);
      }
      b.setLine(this);
      b.add(b.newTableSwitchStmt(expr, (int)low, (int)high, targets, defaultStmt, this));
    }
    else {
      ArrayList targets = new ArrayList();
      ArrayList values = new ArrayList();
      for(Iterator iter = map.values().iterator(); iter.hasNext(); ) {
        ConstCase ca = (ConstCase)iter.next();
        targets.add(ca.label());
        values.add(IntType.emitConstant(ca.getValue().constant().intValue()));
      }

      b.setLine(this);
      b.add(b.newLookupSwitchStmt(expr, values, targets, defaultStmt, this));
    }
    b.addLabel(end_label());
  }
  /**
   * @ast method 
   * @aspect EnumsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:17
   */
  public void transformation() {
    if(getExpr().type().isEnumDecl()) {
      TypeDecl type = getExpr().type();
      hostType().createEnumArray(type);
      hostType().createEnumMethod(type);
      setExpr(
        hostType().createEnumMethod(type).createBoundAccess(new List()).qualifiesAccess(
        new ArrayAccess(
          getExpr().qualifiesAccess(new MethodAccess("ordinal", new List()))
        ))
      );
    }
    super.transformation();
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public SwitchStmt() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public SwitchStmt(Expr p0, Block p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:14
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:20
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setExpr(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getExpr() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getExprNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for Block
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setBlock(Block node) {
    setChild(node, 1);
  }
  /**
   * Getter for Block
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Block getBlock() {
    return (Block)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Block getBlockNoTransform() {
    return (Block)getChildNoTransform(1);
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:504
   */
    public void typeCheck() {
     TypeDecl type = getExpr().type();
    if((!type.isIntegralType() || type.isLong()) && !type.isEnumDecl())
      error("Switch expression must be of char, byte, short, int, or enum type");
  }
  protected java.util.Map targetOf_ContinueStmt_values;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:73
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean targetOf(ContinueStmt stmt) {
    Object _parameters = stmt;
    if(targetOf_ContinueStmt_values == null) targetOf_ContinueStmt_values = new java.util.HashMap(4);
    if(targetOf_ContinueStmt_values.containsKey(_parameters)) {
      return ((Boolean)targetOf_ContinueStmt_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean targetOf_ContinueStmt_value = targetOf_compute(stmt);
if(isFinal && num == state().boundariesCrossed) targetOf_ContinueStmt_values.put(_parameters, Boolean.valueOf(targetOf_ContinueStmt_value));
    return targetOf_ContinueStmt_value;
  }
  /**
   * @apilevel internal
   */
  private boolean targetOf_compute(ContinueStmt stmt) {  return false;  }
  protected java.util.Map targetOf_BreakStmt_values;
  /**
   * @attribute syn
   * @aspect BranchTarget
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/BranchTarget.jrag:77
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean targetOf(BreakStmt stmt) {
    Object _parameters = stmt;
    if(targetOf_BreakStmt_values == null) targetOf_BreakStmt_values = new java.util.HashMap(4);
    if(targetOf_BreakStmt_values.containsKey(_parameters)) {
      return ((Boolean)targetOf_BreakStmt_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean targetOf_BreakStmt_value = targetOf_compute(stmt);
if(isFinal && num == state().boundariesCrossed) targetOf_BreakStmt_values.put(_parameters, Boolean.valueOf(targetOf_BreakStmt_value));
    return targetOf_BreakStmt_value;
  }
  /**
   * @apilevel internal
   */
  private boolean targetOf_compute(BreakStmt stmt) {  return !stmt.hasLabel();  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:530
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
    if(!(!noDefaultLabel() || getExpr().isDAafter(v))) {
      return false;
    }
    if(!(!switchLabelEndsBlock() || getExpr().isDAafter(v))) {
      return false;
    }
    if(!assignedAfterLastStmt(v)) {
      return false;
    }
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(!stmt.isDAafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:548
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean assignedAfterLastStmt(Variable v) {
      ASTNode$State state = state();
    boolean assignedAfterLastStmt_Variable_value = assignedAfterLastStmt_compute(v);
    return assignedAfterLastStmt_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean assignedAfterLastStmt_compute(Variable v) {  return getBlock().isDAafter(v);  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:999
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
    if(!(!noDefaultLabel() || getExpr().isDUafter(v)))
      return false;
    if(!(!switchLabelEndsBlock() || getExpr().isDUafter(v)))
      return false;
    if(!unassignedAfterLastStmt(v))
      return false;
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(!stmt.isDUafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1014
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean unassignedAfterLastStmt(Variable v) {
      ASTNode$State state = state();
    boolean unassignedAfterLastStmt_Variable_value = unassignedAfterLastStmt_compute(v);
    return unassignedAfterLastStmt_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean unassignedAfterLastStmt_compute(Variable v) {  return getBlock().isDUafter(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1017
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean switchLabelEndsBlock() {
      ASTNode$State state = state();
    boolean switchLabelEndsBlock_value = switchLabelEndsBlock_compute();
    return switchLabelEndsBlock_value;
  }
  /**
   * @apilevel internal
   */
  private boolean switchLabelEndsBlock_compute() {  return getBlock().getNumStmt() > 0 && getBlock().getStmt(getBlock().getNumStmt()-1) instanceof ConstCase;  }
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:60
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean lastStmtCanCompleteNormally() {
      ASTNode$State state = state();
    boolean lastStmtCanCompleteNormally_value = lastStmtCanCompleteNormally_compute();
    return lastStmtCanCompleteNormally_value;
  }
  /**
   * @apilevel internal
   */
  private boolean lastStmtCanCompleteNormally_compute() {  return getBlock().canCompleteNormally();  }
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:62
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean noStmts() {
      ASTNode$State state = state();
    boolean noStmts_value = noStmts_compute();
    return noStmts_value;
  }
  /**
   * @apilevel internal
   */
  private boolean noStmts_compute() {
    for(int i = 0; i < getBlock().getNumStmt(); i++)
      if(!(getBlock().getStmt(i) instanceof Case))
        return false;
    return true;
  }
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:69
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean noStmtsAfterLastLabel() {
      ASTNode$State state = state();
    boolean noStmtsAfterLastLabel_value = noStmtsAfterLastLabel_compute();
    return noStmtsAfterLastLabel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean noStmtsAfterLastLabel_compute() {  return getBlock().getNumStmt() > 0 && getBlock().getStmt(getBlock().getNumStmt()-1) instanceof Case;  }
  /**
   * @attribute syn
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean noDefaultLabel() {
      ASTNode$State state = state();
    boolean noDefaultLabel_value = noDefaultLabel_compute();
    return noDefaultLabel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean noDefaultLabel_compute() {
    for(int i = 0; i < getBlock().getNumStmt(); i++)
      if(getBlock().getStmt(i) instanceof DefaultCase)
        return false;
    return true;
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:79
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
  private boolean canCompleteNormally_compute() {  return lastStmtCanCompleteNormally() || noStmts() || noStmtsAfterLastLabel() || noDefaultLabel() || reachableBreak();  }
  /**
   * @apilevel internal
   */
  protected boolean defaultCase_computed = false;
  /**
   * @apilevel internal
   */
  protected DefaultCase defaultCase_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:40
   */
  @SuppressWarnings({"unchecked", "cast"})
  public DefaultCase defaultCase() {
    if(defaultCase_computed) {
      return defaultCase_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    defaultCase_value = defaultCase_compute();
if(isFinal && num == state().boundariesCrossed) defaultCase_computed = true;
    return defaultCase_value;
  }
  /**
   * @apilevel internal
   */
  private DefaultCase defaultCase_compute() {
    for(int i= 0; i < getBlock().getNumStmt(); i++) {
      if(getBlock().getStmt(i) instanceof DefaultCase)
        return (DefaultCase)getBlock().getStmt(i);
    }
    return null;
  }
  /**
   * @apilevel internal
   */
  protected boolean end_label_computed = false;
  /**
   * @apilevel internal
   */
  protected soot.jimple.Stmt end_label_value;
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:48
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt end_label() {
    if(end_label_computed) {
      return end_label_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    end_label_value = end_label_compute();
if(isFinal && num == state().boundariesCrossed) end_label_computed = true;
    return end_label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt end_label_compute() {  return newLabel();  }
  /**
   * @attribute syn
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:207
   */
  @SuppressWarnings({"unchecked", "cast"})
  public soot.jimple.Stmt break_label() {
      ASTNode$State state = state();
    soot.jimple.Stmt break_label_value = break_label_compute();
    return break_label_value;
  }
  /**
   * @apilevel internal
   */
  private soot.jimple.Stmt break_label_compute() {  return end_label();  }
  /**
   * @apilevel internal
   */
  protected boolean typeInt_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeInt_value;
  /**
   * @attribute inh
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:61
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeInt() {
    if(typeInt_computed) {
      return typeInt_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeInt_value = getParent().Define_TypeDecl_typeInt(this, null);
if(isFinal && num == state().boundariesCrossed) typeInt_computed = true;
    return typeInt_value;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeLong_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeLong_value;
  /**
   * @attribute inh
   * @aspect SpecialClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeLong() {
    if(typeLong_computed) {
      return typeLong_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeLong_value = getParent().Define_TypeDecl_typeLong(this, null);
if(isFinal && num == state().boundariesCrossed) typeLong_computed = true;
    return typeLong_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:567
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockNoTransform()) {
      return getExpr().isDAafter(v);
    }
    if(caller == getExprNoTransform()){
    if(((ASTNode)v).isDescendantTo(this))
      return false;
    boolean result = isDAbefore(v);
    return result;
  }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:1022
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getBlockNoTransform()) {
      return getExpr().isDUafter(v);
    }
    if(caller == getExprNoTransform()) {
      return isDUbefore(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:372
   * @apilevel internal
   */
  public boolean Define_boolean_insideSwitch(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_insideSwitch(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:413
   * @apilevel internal
   */
  public Case Define_Case_bind(ASTNode caller, ASTNode child, Case c) {
    if(caller == getBlockNoTransform()){
    Block b = getBlock();
    for(int i = 0; i < b.getNumStmt(); i++)
      if(b.getStmt(i) instanceof Case && ((Case)b.getStmt(i)).constValue(c))
        return (Case)b.getStmt(i);
    return null;
  }
    return getParent().Define_Case_bind(this, caller, c);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:359
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_switchType(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return getExpr().type();
    }
    return getParent().Define_TypeDecl_switchType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:82
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return reachable();
    }
    return getParent().Define_boolean_reachable(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:158
   * @apilevel internal
   */
  public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
    if(caller == getBlockNoTransform()) {
      return reachable();
    }
    return getParent().Define_boolean_reportUnreachable(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
