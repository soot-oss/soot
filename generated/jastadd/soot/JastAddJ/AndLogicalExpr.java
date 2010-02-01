
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class AndLogicalExpr extends LogicalExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isDAafterTrue_Variable_values = null;
        isDAafterFalse_Variable_values = null;
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        next_test_label_computed = false;
        next_test_label_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AndLogicalExpr clone() throws CloneNotSupportedException {
        AndLogicalExpr node = (AndLogicalExpr)super.clone();
        node.isDAafterTrue_Variable_values = null;
        node.isDAafterFalse_Variable_values = null;
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.next_test_label_computed = false;
        node.next_test_label_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AndLogicalExpr copy() {
      try {
          AndLogicalExpr node = (AndLogicalExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AndLogicalExpr fullCopy() {
        AndLogicalExpr res = (AndLogicalExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BooleanExpressions.jrag at line 184

  
  public void emitEvalBranch(Body b) {
    b.setLine(this);
    getLeftOperand().emitEvalBranch(b);
    b.addLabel(next_test_label());
    if(getLeftOperand().canBeTrue()) {
      getRightOperand().emitEvalBranch(b);
      if(getRightOperand().canBeTrue())
        b.add(b.newGotoStmt(true_label(), this));
    }
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 175

    public AndLogicalExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 175
    public AndLogicalExpr(Expr p0, Expr p1) {
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
    // Declared in java.ast line 153
    public void setLeftOperand(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getLeftOperand() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getLeftOperandNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 153
    public void setRightOperand(Expr node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Expr getRightOperand() {
        return (Expr)getChild(1);
    }

    // Declared in java.ast at line 9


    public Expr getRightOperandNoTransform() {
        return (Expr)getChildNoTransform(1);
    }

    // Declared in ConstantExpression.jrag at line 537
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        ASTNode$State state = state();
        Constant constant_value = constant_compute();
        return constant_value;
    }

    private Constant constant_compute() {  return Constant.create(left().constant().booleanValue() && right().constant().booleanValue());  }

    // Declared in DefiniteAssignment.jrag at line 365
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterTrue(Variable v) {
        Object _parameters = v;
if(isDAafterTrue_Variable_values == null) isDAafterTrue_Variable_values = new java.util.HashMap(4);
        if(isDAafterTrue_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDAafterTrue_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDAafterTrue_Variable_value = isDAafterTrue_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDAafterTrue_Variable_values.put(_parameters, Boolean.valueOf(isDAafterTrue_Variable_value));
        return isDAafterTrue_Variable_value;
    }

    private boolean isDAafterTrue_compute(Variable v) {  return getRightOperand().isDAafterTrue(v) || isFalse();  }

    // Declared in DefiniteAssignment.jrag at line 367
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterFalse(Variable v) {
        Object _parameters = v;
if(isDAafterFalse_Variable_values == null) isDAafterFalse_Variable_values = new java.util.HashMap(4);
        if(isDAafterFalse_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDAafterFalse_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDAafterFalse_Variable_value = isDAafterFalse_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDAafterFalse_Variable_values.put(_parameters, Boolean.valueOf(isDAafterFalse_Variable_value));
        return isDAafterFalse_Variable_value;
    }

    private boolean isDAafterFalse_compute(Variable v) {  return (getLeftOperand().isDAafterFalse(v) && getRightOperand().isDAafterFalse(v)) || isTrue();  }

    // Declared in DefiniteAssignment.jrag at line 373
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

    private boolean isDAafter_compute(Variable v) {  return isDAafterTrue(v) && isDAafterFalse(v);  }

    // Declared in DefiniteAssignment.jrag at line 803
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterTrue(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterTrue_Variable_value = isDUafterTrue_compute(v);
        return isDUafterTrue_Variable_value;
    }

    private boolean isDUafterTrue_compute(Variable v) {  return getRightOperand().isDUafterTrue(v);  }

    // Declared in DefiniteAssignment.jrag at line 804
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterFalse(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterFalse_Variable_value = isDUafterFalse_compute(v);
        return isDUafterFalse_Variable_value;
    }

    private boolean isDUafterFalse_compute(Variable v) {  return getLeftOperand().isDUafterFalse(v) && getRightOperand().isDUafterFalse(v);  }

    // Declared in DefiniteAssignment.jrag at line 807
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

    private boolean isDUafter_compute(Variable v) {  return isDUafterTrue(v) && isDUafterFalse(v);  }

    // Declared in PrettyPrint.jadd at line 411
 @SuppressWarnings({"unchecked", "cast"})     public String printOp() {
        ASTNode$State state = state();
        String printOp_value = printOp_compute();
        return printOp_value;
    }

    private String printOp_compute() {  return " && ";  }

    // Declared in BooleanExpressions.jrag at line 86
 @SuppressWarnings({"unchecked", "cast"})     public boolean canBeTrue() {
        ASTNode$State state = state();
        boolean canBeTrue_value = canBeTrue_compute();
        return canBeTrue_value;
    }

    private boolean canBeTrue_compute() {  return getLeftOperand().canBeTrue() && getRightOperand().canBeTrue();  }

    // Declared in BooleanExpressions.jrag at line 96
 @SuppressWarnings({"unchecked", "cast"})     public boolean canBeFalse() {
        ASTNode$State state = state();
        boolean canBeFalse_value = canBeFalse_compute();
        return canBeFalse_value;
    }

    private boolean canBeFalse_compute() {  return getLeftOperand().canBeFalse() || getRightOperand().canBeFalse();  }

    protected boolean next_test_label_computed = false;
    protected soot.jimple.Stmt next_test_label_value;
    // Declared in BooleanExpressions.jrag at line 194
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt next_test_label() {
        if(next_test_label_computed) {
            return next_test_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        next_test_label_value = next_test_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            next_test_label_computed = true;
        return next_test_label_value;
    }

    private soot.jimple.Stmt next_test_label_compute() {  return newLabel();  }

    // Declared in DefiniteAssignment.jrag at line 371
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getRightOperandNoTransform()) {
            return getLeftOperand().isDAafterTrue(v);
        }
        if(caller == getLeftOperandNoTransform()) {
            return isDAbefore(v);
        }
        return super.Define_boolean_isDAbefore(caller, child, v);
    }

    // Declared in DefiniteAssignment.jrag at line 806
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getRightOperandNoTransform()) {
            return getLeftOperand().isDUafterTrue(v);
        }
        if(caller == getLeftOperandNoTransform()) {
            return isDUbefore(v);
        }
        return super.Define_boolean_isDUbefore(caller, child, v);
    }

    // Declared in BooleanExpressions.jrag at line 74
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
        if(caller == getRightOperandNoTransform()) {
            return false_label();
        }
        if(caller == getLeftOperandNoTransform()) {
            return false_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
    }

    // Declared in BooleanExpressions.jrag at line 75
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
        if(caller == getRightOperandNoTransform()) {
            return true_label();
        }
        if(caller == getLeftOperandNoTransform()) {
            return next_test_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
