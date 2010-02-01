
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class IfStmt extends Stmt implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        canCompleteNormally_computed = false;
        else_branch_label_computed = false;
        else_branch_label_value = null;
        then_branch_label_computed = false;
        then_branch_label_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public IfStmt clone() throws CloneNotSupportedException {
        IfStmt node = (IfStmt)super.clone();
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.canCompleteNormally_computed = false;
        node.else_branch_label_computed = false;
        node.else_branch_label_value = null;
        node.then_branch_label_computed = false;
        node.then_branch_label_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public IfStmt copy() {
      try {
          IfStmt node = (IfStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public IfStmt fullCopy() {
        IfStmt res = (IfStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in NodeConstructors.jrag at line 66


  public IfStmt(Expr cond, Stmt thenBranch) {
    this(cond, thenBranch, new Opt());
  }

    // Declared in NodeConstructors.jrag at line 70


  public IfStmt(Expr cond, Stmt thenBranch, Stmt elseBranch) {
    this(cond, thenBranch, new Opt(elseBranch));
  }

    // Declared in PrettyPrint.jadd at line 573


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("if(");
    getCondition().toString(s);
    s.append(") ");
    getThen().toString(s);
    if(hasElse()) {
      s.append(indent());
      s.append("else ");
      getElse().toString(s);
    }
  }

    // Declared in TypeCheck.jrag at line 316


  public void typeCheck() {
    TypeDecl cond = getCondition().type();
    if(!cond.isBoolean()) {
      error("the type of \"" + getCondition() + "\" is " + cond.name() + " which is not boolean");
    }
  }

    // Declared in Statements.jrag at line 115

  public void jimplify2(Body b) {
    soot.jimple.Stmt endBranch = newLabel();
    if(getCondition().isConstant()) {
      if(getCondition().isTrue())
        getThen().jimplify2(b);
      else if(getCondition().isFalse() && hasElse())
        getElse().jimplify2(b);
    }
    else {
      soot.jimple.Stmt elseBranch = else_branch_label();
      soot.jimple.Stmt thenBranch = then_branch_label();
      getCondition().emitEvalBranch(b);
      b.addLabel(thenBranch);
      getThen().jimplify2(b);
      if(getThen().canCompleteNormally() && hasElse()) {
        b.setLine(this);
        b.add(b.newGotoStmt(endBranch, this));
      }
      b.addLabel(elseBranch);
      if(hasElse())
        getElse().jimplify2(b);
    }
    if(getThen().canCompleteNormally() && hasElse())
      b.addLabel(endBranch);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 210

    public IfStmt() {
        super();

        setChild(new Opt(), 2);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 210
    public IfStmt(Expr p0, Stmt p1, Opt<Stmt> p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setChild(p2, 2);
    }

    // Declared in java.ast at line 17


  protected int numChildren() {
    return 3;
  }

    // Declared in java.ast at line 20

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 210
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
    // Declared in java.ast line 210
    public void setThen(Stmt node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Stmt getThen() {
        return (Stmt)getChild(1);
    }

    // Declared in java.ast at line 9


    public Stmt getThenNoTransform() {
        return (Stmt)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 210
    public void setElseOpt(Opt<Stmt> opt) {
        setChild(opt, 2);
    }

    // Declared in java.ast at line 6


    public boolean hasElse() {
        return getElseOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Stmt getElse() {
        return (Stmt)getElseOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setElse(Stmt node) {
        getElseOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Stmt> getElseOpt() {
        return (Opt<Stmt>)getChild(2);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Stmt> getElseOptNoTransform() {
        return (Opt<Stmt>)getChildNoTransform(2);
    }

    // Declared in DefiniteAssignment.jrag at line 526
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

    private boolean isDAafter_compute(Variable v) {  return hasElse() ? getThen().isDAafter(v) && getElse().isDAafter(v) : getThen().isDAafter(v) && getCondition().isDAafterFalse(v);  }

    // Declared in DefiniteAssignment.jrag at line 998
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

    private boolean isDUafter_compute(Variable v) {  return hasElse() ? getThen().isDUafter(v) && getElse().isDUafter(v) : getThen().isDUafter(v) && getCondition().isDUafterFalse(v);  }

    // Declared in UnreachableStatements.jrag at line 139
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

    private boolean canCompleteNormally_compute() {  return (reachable() && !hasElse()) || (getThen().canCompleteNormally() ||
    (hasElse() && getElse().canCompleteNormally()));  }

    // Declared in BooleanExpressions.jrag at line 32
 @SuppressWarnings({"unchecked", "cast"})     public boolean definesLabel() {
        ASTNode$State state = state();
        boolean definesLabel_value = definesLabel_compute();
        return definesLabel_value;
    }

    private boolean definesLabel_compute() {  return true;  }

    protected boolean else_branch_label_computed = false;
    protected soot.jimple.Stmt else_branch_label_value;
    // Declared in Statements.jrag at line 113
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt else_branch_label() {
        if(else_branch_label_computed) {
            return else_branch_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        else_branch_label_value = else_branch_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            else_branch_label_computed = true;
        return else_branch_label_value;
    }

    private soot.jimple.Stmt else_branch_label_compute() {  return newLabel();  }

    protected boolean then_branch_label_computed = false;
    protected soot.jimple.Stmt then_branch_label_value;
    // Declared in Statements.jrag at line 114
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt then_branch_label() {
        if(then_branch_label_computed) {
            return then_branch_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        then_branch_label_value = then_branch_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            then_branch_label_computed = true;
        return then_branch_label_value;
    }

    private soot.jimple.Stmt then_branch_label_compute() {  return newLabel();  }

    // Declared in DefiniteAssignment.jrag at line 529
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getElseOptNoTransform()) {
            return getCondition().isDAafterFalse(v);
        }
        if(caller == getThenNoTransform()) {
            return getCondition().isDAafterTrue(v);
        }
        if(caller == getConditionNoTransform()) {
            return isDAbefore(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 1001
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getElseOptNoTransform()) {
            return getCondition().isDUafterFalse(v);
        }
        if(caller == getThenNoTransform()) {
            return getCondition().isDUafterTrue(v);
        }
        if(caller == getConditionNoTransform()) {
            return isDUbefore(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in UnreachableStatements.jrag at line 142
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getElseOptNoTransform()) {
            return reachable();
        }
        if(caller == getThenNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 148
    public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
        if(caller == getElseOptNoTransform()) {
            return reachable();
        }
        if(caller == getThenNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reportUnreachable(this, caller);
    }

    // Declared in BooleanExpressions.jrag at line 38
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
        if(caller == getConditionNoTransform()) {
            return else_branch_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
    }

    // Declared in BooleanExpressions.jrag at line 39
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
        if(caller == getConditionNoTransform()) {
            return then_branch_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
