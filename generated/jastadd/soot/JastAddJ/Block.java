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
 * @declaredat java.ast:194
 */
public class Block extends Stmt implements Cloneable, VariableScope {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    checkReturnDA_Variable_values = null;
    isDAafter_Variable_values = null;
    checkReturnDU_Variable_values = null;
    isDUafter_Variable_values = null;
    localVariableDeclaration_String_values = null;
    canCompleteNormally_computed = false;
    lookupType_String_values = null;
    lookupVariable_String_values = null;
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
  public Block clone() throws CloneNotSupportedException {
    Block node = (Block)super.clone();
    node.checkReturnDA_Variable_values = null;
    node.isDAafter_Variable_values = null;
    node.checkReturnDU_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.localVariableDeclaration_String_values = null;
    node.canCompleteNormally_computed = false;
    node.lookupType_String_values = null;
    node.lookupVariable_String_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Block copy() {
      try {
        Block node = (Block)clone();
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
  public Block fullCopy() {
    Block res = (Block)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect DeclareBeforeUse
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DeclareBeforeUse.jrag:21
   */
  public boolean declaredBeforeUse(Variable decl, ASTNode use) {
    int indexDecl = ((ASTNode)decl).varChildIndex(this);
    int indexUse = use.varChildIndex(this);
    return indexDecl <= indexUse;
  }
  /**
   * @ast method 
   * @aspect DeclareBeforeUse
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DeclareBeforeUse.jrag:26
   */
  public boolean declaredBeforeUse(Variable decl, int indexUse) {
    int indexDecl = ((ASTNode)decl).varChildIndex(this);
    return indexDecl <= indexUse;
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:526
   */
  public void toString(StringBuffer s) {
    String indent = indent();
    s.append(shouldHaveIndent() ? indent : "");
    s.append("{");
    for(int i = 0; i < getNumStmt(); i++) {
      getStmt(i).toString(s);
    }
    s.append(shouldHaveIndent() ? indent : indent.substring(0, indent.length()-2));
    s.append("}");
  }
  /**
   * @ast method 
   * @aspect Statements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Statements.jrag:15
   */
  public void jimplify2(Body b) {
    for(int i = 0; i < getNumStmt(); i++)
      getStmt(i).jimplify2(b);
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public Block() {
    super();

    setChild(new List(), 0);

  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public Block(List<Stmt> p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:14
   */
  protected int numChildren() {
    return 1;
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
   * Setter for StmtList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setStmtList(List<Stmt> list) {
    setChild(list, 0);
  }
  /**
   * @return number of children in StmtList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumStmt() {
    return getStmtList().getNumChild();
  }
  /**
   * Getter for child in list StmtList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Stmt getStmt(int i) {
    return (Stmt)getStmtList().getChild(i);
  }
  /**
   * Add element to list StmtList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addStmt(Stmt node) {
    List<Stmt> list = (parent == null || state == null) ? getStmtListNoTransform() : getStmtList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addStmtNoTransform(Stmt node) {
    List<Stmt> list = getStmtListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list StmtList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setStmt(Stmt node, int i) {
    List<Stmt> list = getStmtList();
    list.setChild(node, i);
  }
  /**
   * Getter for Stmt list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<Stmt> getStmts() {
    return getStmtList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<Stmt> getStmtsNoTransform() {
    return getStmtListNoTransform();
  }
  /**
   * Getter for list StmtList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Stmt> getStmtList() {
    List<Stmt> list = (List<Stmt>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Stmt> getStmtListNoTransform() {
    return (List<Stmt>)getChildNoTransform(0);
  }
  protected java.util.Map checkReturnDA_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:300
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean checkReturnDA(Variable v) {
    Object _parameters = v;
    if(checkReturnDA_Variable_values == null) checkReturnDA_Variable_values = new java.util.HashMap(4);
    if(checkReturnDA_Variable_values.containsKey(_parameters)) {
      return ((Boolean)checkReturnDA_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean checkReturnDA_Variable_value = checkReturnDA_compute(v);
if(isFinal && num == state().boundariesCrossed) checkReturnDA_Variable_values.put(_parameters, Boolean.valueOf(checkReturnDA_Variable_value));
    return checkReturnDA_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean checkReturnDA_compute(Variable v) {
    HashSet set = new HashSet();
    collectBranches(set);
    for(Iterator iter = set.iterator(); iter.hasNext(); ) {
      Object o = iter.next();
      if(o instanceof ReturnStmt) {
        ReturnStmt stmt = (ReturnStmt)o;
        if(!stmt.isDAafterReachedFinallyBlocks(v))
          return false;
      }
    }
    return true;
  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:440
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
  private boolean isDAafter_compute(Variable v) {  return getNumStmt() == 0 ? isDAbefore(v) : getStmt(getNumStmt()-1).isDAafter(v);  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:446
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUeverywhere(Variable v) {
      ASTNode$State state = state();
    boolean isDUeverywhere_Variable_value = isDUeverywhere_compute(v);
    return isDUeverywhere_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUeverywhere_compute(Variable v) {  return isDUbefore(v) && checkDUeverywhere(v);  }
  protected java.util.Map checkReturnDU_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:756
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean checkReturnDU(Variable v) {
    Object _parameters = v;
    if(checkReturnDU_Variable_values == null) checkReturnDU_Variable_values = new java.util.HashMap(4);
    if(checkReturnDU_Variable_values.containsKey(_parameters)) {
      return ((Boolean)checkReturnDU_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean checkReturnDU_Variable_value = checkReturnDU_compute(v);
if(isFinal && num == state().boundariesCrossed) checkReturnDU_Variable_values.put(_parameters, Boolean.valueOf(checkReturnDU_Variable_value));
    return checkReturnDU_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean checkReturnDU_compute(Variable v) {
    HashSet set = new HashSet();
    collectBranches(set);
    for(Iterator iter = set.iterator(); iter.hasNext(); ) {
      Object o = iter.next();
      if(o instanceof ReturnStmt) {
        ReturnStmt stmt = (ReturnStmt)o;
        if(!stmt.isDUafterReachedFinallyBlocks(v))
          return false;
      }
    }
    return true;
  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:869
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
  private boolean isDUafter_compute(Variable v) {  return getNumStmt() == 0 ? isDUbefore(v) : getStmt(getNumStmt()-1).isDUafter(v);  }
  protected java.util.Map localVariableDeclaration_String_values;
  /**
   * @attribute syn
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:116
   */
  @SuppressWarnings({"unchecked", "cast"})
  public VariableDeclaration localVariableDeclaration(String name) {
    Object _parameters = name;
    if(localVariableDeclaration_String_values == null) localVariableDeclaration_String_values = new java.util.HashMap(4);
    if(localVariableDeclaration_String_values.containsKey(_parameters)) {
      return (VariableDeclaration)localVariableDeclaration_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    VariableDeclaration localVariableDeclaration_String_value = localVariableDeclaration_compute(name);
if(isFinal && num == state().boundariesCrossed) localVariableDeclaration_String_values.put(_parameters, localVariableDeclaration_String_value);
    return localVariableDeclaration_String_value;
  }
  /**
   * @apilevel internal
   */
  private VariableDeclaration localVariableDeclaration_compute(String name) {
    for(int i = 0; i < getNumStmt(); i++)
      if(getStmt(i).declaresVariable(name))
        return (VariableDeclaration)getStmt(i);
    return null;
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:763
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean addsIndentationLevel() {
      ASTNode$State state = state();
    boolean addsIndentationLevel_value = addsIndentationLevel_compute();
    return addsIndentationLevel_value;
  }
  /**
   * @apilevel internal
   */
  private boolean addsIndentationLevel_compute() {  return shouldHaveIndent();  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:765
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean shouldHaveIndent() {
      ASTNode$State state = state();
    boolean shouldHaveIndent_value = shouldHaveIndent_compute();
    return shouldHaveIndent_value;
  }
  /**
   * @apilevel internal
   */
  private boolean shouldHaveIndent_compute() {  return getParent() instanceof List && getParent().getParent() instanceof Block;  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:37
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
  private boolean canCompleteNormally_compute() {  return getNumStmt() == 0 ? reachable() : getStmt(getNumStmt() - 1).canCompleteNormally();  }
  protected java.util.Map lookupType_String_values;
  /**
   * @attribute inh
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:175
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupType(String name) {
    Object _parameters = name;
    if(lookupType_String_values == null) lookupType_String_values = new java.util.HashMap(4);
    if(lookupType_String_values.containsKey(_parameters)) {
      return (SimpleSet)lookupType_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
if(isFinal && num == state().boundariesCrossed) lookupType_String_values.put(_parameters, lookupType_String_value);
    return lookupType_String_value;
  }
  protected java.util.Map lookupVariable_String_values;
  /**
   * @attribute inh
   * @aspect VariableScope
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:17
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupVariable(String name) {
    Object _parameters = name;
    if(lookupVariable_String_values == null) lookupVariable_String_values = new java.util.HashMap(4);
    if(lookupVariable_String_values.containsKey(_parameters)) {
      return (SimpleSet)lookupVariable_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
if(isFinal && num == state().boundariesCrossed) lookupVariable_String_values.put(_parameters, lookupVariable_String_value);
    return lookupVariable_String_value;
  }
  /**
   * @attribute inh
   * @aspect UnreachableStatements
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:28
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean reachable() {
      ASTNode$State state = state();
    boolean reachable_value = getParent().Define_boolean_reachable(this, null);
    return reachable_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:52
   * @apilevel internal
   */
  public boolean Define_boolean_isIncOrDec(ASTNode caller, ASTNode child) {
    if(caller == getStmtListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isIncOrDec(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:443
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getStmtListNoTransform()) {
      int index = caller.getIndexOfChild(child);
      return index == 0 ? isDAbefore(v) : getStmt(index - 1).isDAafter(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:870
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getStmtListNoTransform()) {
      int index = caller.getIndexOfChild(child);
      return index == 0 ? isDUbefore(v) : getStmt(index - 1).isDUafter(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:292
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getStmtListNoTransform()) { 
   int index = caller.getIndexOfChild(child);
{
    SimpleSet c = SimpleSet.emptySet;
    for(int i = index; i >= 0 && !(getStmt(i) instanceof Case); i--) {
      if(getStmt(i) instanceof LocalClassDeclStmt) {
        TypeDecl t = ((LocalClassDeclStmt)getStmt(i)).getClassDecl();
        if(t.name().equals(name)) {
          c = c.add(t);
        }
      }
    }
    if(!c.isEmpty())
      return c;
    return lookupType(name);
  }
}
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:68
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getStmtListNoTransform()) { 
   int index = caller.getIndexOfChild(child);
{
    VariableDeclaration v = localVariableDeclaration(name);
    // declare before use and shadowing
    if(v != null && declaredBeforeUse(v, index))
      return v;
    return lookupVariable(name);
  }
}
    return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:291
   * @apilevel internal
   */
  public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
    if(caller == getStmtListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return this;
    }
    return getParent().Define_VariableScope_outerScope(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:116
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getStmtListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return NameType.EXPRESSION_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:38
   * @apilevel internal
   */
  public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
    if(caller == getStmtListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return childIndex == 0 ? reachable() : getStmt(childIndex-1).canCompleteNormally();
    }
    return getParent().Define_boolean_reachable(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/UnreachableStatements.jrag:148
   * @apilevel internal
   */
  public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
    if(caller == getStmtListNoTransform()) {
      int i = caller.getIndexOfChild(child);
      return i == 0 ? reachable() : getStmt(i-1).reachable();
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
