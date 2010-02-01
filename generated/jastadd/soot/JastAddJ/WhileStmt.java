
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class WhileStmt extends BranchTargetStmt implements Cloneable {
    public void flushCache() {
        super.flushCache();
        targetOf_ContinueStmt_values = null;
        targetOf_BreakStmt_values = null;
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        isDUbeforeCondition_Variable_values = null;
        canCompleteNormally_computed = false;
        cond_label_computed = false;
        cond_label_value = null;
        end_label_computed = false;
        end_label_value = null;
        stmt_label_computed = false;
        stmt_label_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public WhileStmt clone() throws CloneNotSupportedException {
        WhileStmt node = (WhileStmt)super.clone();
        node.targetOf_ContinueStmt_values = null;
        node.targetOf_BreakStmt_values = null;
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.isDUbeforeCondition_Variable_values = null;
        node.canCompleteNormally_computed = false;
        node.cond_label_computed = false;
        node.cond_label_value = null;
        node.end_label_computed = false;
        node.end_label_value = null;
        node.stmt_label_computed = false;
        node.stmt_label_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WhileStmt copy() {
      try {
          WhileStmt node = (WhileStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WhileStmt fullCopy() {
        WhileStmt res = (WhileStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 586


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("while(");
    getCondition().toString(s);
    s.append(")");
    getStmt().toString(s);
  }

    // Declared in TypeCheck.jrag at line 322

  public void typeCheck() {
    TypeDecl cond = getCondition().type();
    if(!cond.isBoolean()) {
      error("the type of \"" + getCondition() + "\" is " + cond.name() + " which is not boolean");
    }
  }

    // Declared in Statements.jrag at line 146


  public void jimplify2(Body b) {
    b.addLabel(cond_label());
    getCondition().emitEvalBranch(b);
    b.addLabel(stmt_label());
    if(getCondition().canBeTrue()) {
      getStmt().jimplify2(b);
      if(getStmt().canCompleteNormally()) {
        b.setLine(this);
        b.add(b.newGotoStmt(cond_label(), this));
      }
    }
    if(canCompleteNormally())
      b.addLabel(end_label());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 211

    public WhileStmt() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 211
    public WhileStmt(Expr p0, Stmt p1) {
        setChild(p0, 0);
        setChild(p1, 1);
    }

    // Declared in java.ast at line 15


  protected int numChildren() {
    return 2;
  }

    // Declared in java.ast at line 18

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 211
    public void setCondition(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getCondition() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getConditionNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 211
    public void setStmt(Stmt node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Stmt getStmt() {
        return (Stmt)getChild(1);
    }

    // Declared in java.ast at line 9


    public Stmt getStmtNoTransform() {
        return (Stmt)getChildNoTransform(1);
    }

    protected java.util.Map targetOf_ContinueStmt_values;
    // Declared in BranchTarget.jrag at line 70
 @SuppressWarnings({"unchecked", "cast"})     public boolean targetOf(ContinueStmt stmt) {
        Object _parameters = stmt;
if(targetOf_ContinueStmt_values == null) targetOf_ContinueStmt_values = new java.util.HashMap(4);
        if(targetOf_ContinueStmt_values.containsKey(_parameters)) {
            return ((Boolean)targetOf_ContinueStmt_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean targetOf_ContinueStmt_value = targetOf_compute(stmt);
        if(isFinal && num == state().boundariesCrossed)
            targetOf_ContinueStmt_values.put(_parameters, Boolean.valueOf(targetOf_ContinueStmt_value));
        return targetOf_ContinueStmt_value;
    }

    private boolean targetOf_compute(ContinueStmt stmt) {  return !stmt.hasLabel();  }

    protected java.util.Map targetOf_BreakStmt_values;
    // Declared in BranchTarget.jrag at line 78
 @SuppressWarnings({"unchecked", "cast"})     public boolean targetOf(BreakStmt stmt) {
        Object _parameters = stmt;
if(targetOf_BreakStmt_values == null) targetOf_BreakStmt_values = new java.util.HashMap(4);
        if(targetOf_BreakStmt_values.containsKey(_parameters)) {
            return ((Boolean)targetOf_BreakStmt_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean targetOf_BreakStmt_value = targetOf_compute(stmt);
        if(isFinal && num == state().boundariesCrossed)
            targetOf_BreakStmt_values.put(_parameters, Boolean.valueOf(targetOf_BreakStmt_value));
        return targetOf_BreakStmt_value;
    }

    private boolean targetOf_compute(BreakStmt stmt) {  return !stmt.hasLabel();  }

    // Declared in DefiniteAssignment.jrag at line 577
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        Object _parameters = v;
if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
        if(isDAafter_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {
    if(!getCondition().isDAafterFalse(v))
      return false;
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(!stmt.isDAafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }

    // Declared in DefiniteAssignment.jrag at line 1036
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        Object _parameters = v;
if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
        if(isDUafter_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {
    if(!isDUbeforeCondition(v)) // start a circular evaluation here
      return false;
    if(!getCondition().isDUafterFalse(v))
      return false;
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(!stmt.isDUafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }

    protected java.util.Map isDUbeforeCondition_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 1053
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUbeforeCondition(Variable v) {
        Object _parameters = v;
if(isDUbeforeCondition_Variable_values == null) isDUbeforeCondition_Variable_values = new java.util.HashMap(4);
        ASTNode$State.CircularValue _value;
        if(isDUbeforeCondition_Variable_values.containsKey(_parameters)) {
            Object _o = isDUbeforeCondition_Variable_values.get(_parameters);
            if(!(_o instanceof ASTNode$State.CircularValue)) {
                return ((Boolean)_o).booleanValue();
            }
            else
                _value = (ASTNode$State.CircularValue)_o;
        }
        else {
            _value = new ASTNode$State.CircularValue();
            isDUbeforeCondition_Variable_values.put(_parameters, _value);
            _value.value = Boolean.valueOf(true);
        }
        ASTNode$State state = state();
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            boolean new_isDUbeforeCondition_Variable_value;
            do {
                _value.visited = new Integer(state.CIRCLE_INDEX);
                state.CHANGE = false;
                new_isDUbeforeCondition_Variable_value = isDUbeforeCondition_compute(v);
                if (new_isDUbeforeCondition_Variable_value!=((Boolean)_value.value).booleanValue()) {
                    state.CHANGE = true;
                    _value.value = Boolean.valueOf(new_isDUbeforeCondition_Variable_value);
                }
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
                isDUbeforeCondition_Variable_values.put(_parameters, new_isDUbeforeCondition_Variable_value);
            }
            else {
                isDUbeforeCondition_Variable_values.remove(_parameters);
            state.RESET_CYCLE = true;
            isDUbeforeCondition_compute(v);
            state.RESET_CYCLE = false;
            }
            state.IN_CIRCLE = false; 
            return new_isDUbeforeCondition_Variable_value;
        }
        if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
            _value.visited = new Integer(state.CIRCLE_INDEX);
            boolean new_isDUbeforeCondition_Variable_value = isDUbeforeCondition_compute(v);
            if (state.RESET_CYCLE) {
                isDUbeforeCondition_Variable_values.remove(_parameters);
            }
            else if (new_isDUbeforeCondition_Variable_value!=((Boolean)_value.value).booleanValue()) {
                state.CHANGE = true;
                _value.value = new_isDUbeforeCondition_Variable_value;
            }
            return new_isDUbeforeCondition_Variable_value;
        }
        return ((Boolean)_value.value).booleanValue();
    }

    private boolean isDUbeforeCondition_compute(Variable v) {
    // 1st
    if(!isDUbefore(v))
      return false;
    else if(!getStmt().isDUafter(v))
      return false;
    else {
      for(Iterator iter = targetContinues().iterator(); iter.hasNext(); ) {
        ContinueStmt stmt = (ContinueStmt)iter.next();
        if(!stmt.isDUafterReachedFinallyBlocks(v))
          return false;
      }
    }
    return true;
  }

    // Declared in NameCheck.jrag at line 398
 @SuppressWarnings({"unchecked", "cast"})     public boolean continueLabel() {
        ASTNode$State state = state();
        boolean continueLabel_value = continueLabel_compute();
        return continueLabel_value;
    }

    private boolean continueLabel_compute() {  return true;  }

    // Declared in UnreachableStatements.jrag at line 85
 @SuppressWarnings({"unchecked", "cast"})     public boolean canCompleteNormally() {
        if(canCompleteNormally_computed) {
            return canCompleteNormally_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        canCompleteNormally_value = canCompleteNormally_compute();
        if(isFinal && num == state().boundariesCrossed)
            canCompleteNormally_computed = true;
        return canCompleteNormally_value;
    }

    private boolean canCompleteNormally_compute() {  return reachable() && (!getCondition().isConstant() || !getCondition().isTrue()) || reachableBreak();  }

    // Declared in BooleanExpressions.jrag at line 33
 @SuppressWarnings({"unchecked", "cast"})     public boolean definesLabel() {
        ASTNode$State state = state();
        boolean definesLabel_value = definesLabel_compute();
        return definesLabel_value;
    }

    private boolean definesLabel_compute() {  return true;  }

    protected boolean cond_label_computed = false;
    protected soot.jimple.Stmt cond_label_value;
    // Declared in Statements.jrag at line 142
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt cond_label() {
        if(cond_label_computed) {
            return cond_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        cond_label_value = cond_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            cond_label_computed = true;
        return cond_label_value;
    }

    private soot.jimple.Stmt cond_label_compute() {  return newLabel();  }

    protected boolean end_label_computed = false;
    protected soot.jimple.Stmt end_label_value;
    // Declared in Statements.jrag at line 143
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt end_label() {
        if(end_label_computed) {
            return end_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        end_label_value = end_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            end_label_computed = true;
        return end_label_value;
    }

    private soot.jimple.Stmt end_label_compute() {  return newLabel();  }

    protected boolean stmt_label_computed = false;
    protected soot.jimple.Stmt stmt_label_value;
    // Declared in Statements.jrag at line 144
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt stmt_label() {
        if(stmt_label_computed) {
            return stmt_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        stmt_label_value = stmt_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            stmt_label_computed = true;
        return stmt_label_value;
    }

    private soot.jimple.Stmt stmt_label_compute() {  return newLabel();  }

    // Declared in Statements.jrag at line 203
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt break_label() {
        ASTNode$State state = state();
        soot.jimple.Stmt break_label_value = break_label_compute();
        return break_label_value;
    }

    private soot.jimple.Stmt break_label_compute() {  return end_label();  }

    // Declared in Statements.jrag at line 228
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt continue_label() {
        ASTNode$State state = state();
        soot.jimple.Stmt continue_label_value = continue_label_compute();
        return continue_label_value;
    }

    private soot.jimple.Stmt continue_label_compute() {  return cond_label();  }

    // Declared in DefiniteAssignment.jrag at line 588
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getStmtNoTransform()) {
            return getCondition().isDAafterTrue(v);
        }
        if(caller == getConditionNoTransform()) {
            return isDAbefore(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 1070
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getStmtNoTransform()) {
            return getCondition().isDUafterTrue(v);
        }
        if(caller == getConditionNoTransform()) {
            return isDUbeforeCondition(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in NameCheck.jrag at line 366
    public boolean Define_boolean_insideLoop(ASTNode caller, ASTNode child) {
        if(caller == getStmtNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_insideLoop(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 86
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getStmtNoTransform()) {
            return reachable() && !getCondition().isFalse();
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 151
    public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
        if(caller == getStmtNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reportUnreachable(this, caller);
    }

    // Declared in BooleanExpressions.jrag at line 40
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
        if(caller == getConditionNoTransform()) {
            return end_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
    }

    // Declared in BooleanExpressions.jrag at line 41
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
        if(caller == getConditionNoTransform()) {
            return stmt_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
