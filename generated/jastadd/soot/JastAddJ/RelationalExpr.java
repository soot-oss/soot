
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;



public abstract class RelationalExpr extends Binary implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public RelationalExpr clone() throws CloneNotSupportedException {
        RelationalExpr node = (RelationalExpr)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
    // Declared in TypeCheck.jrag at line 204


  // 15.20
  public void typeCheck() {
    if(!getLeftOperand().type().isNumericType())
      error(getLeftOperand().type().typeName() + " is not numeric");
    if(!getRightOperand().type().isNumericType())
      error(getRightOperand().type().typeName() + " is not numeric");
  }

    // Declared in BooleanExpressions.jrag at line 102

  
  public soot.Value eval(Body b) { return emitBooleanCondition(b); }

    // Declared in BooleanExpressions.jrag at line 221

  
  public void emitEvalBranch(Body b) {
    b.setLine(this);
    if(isTrue())
      b.add(Jimple.v().newGotoStmt(true_label()));
    else if(isFalse())
      b.add(Jimple.v().newGotoStmt(false_label()));
    else {
      soot.Value left;
      soot.Value right;
      TypeDecl type = getLeftOperand().type();
      if(type.isNumericType()) {
        type = binaryNumericPromotedType();
        left = getLeftOperand().type().emitCastTo(b, // Binary numeric promotion
          getLeftOperand(),
          type
        );
        right = getRightOperand().type().emitCastTo(b, // Binary numeric promotion
          getRightOperand(),
          type
        );
        if(type.isDouble() || type.isFloat() || type.isLong()) {
          Local l;
          if(type.isDouble() || type.isFloat()) {
            if(this instanceof GEExpr || this instanceof GTExpr) {
              l = asLocal(b, soot.jimple.Jimple.v().newCmplExpr(asImmediate(b, left), asImmediate(b, right)));
            }
            else {
              l = asLocal(b, soot.jimple.Jimple.v().newCmpgExpr(asImmediate(b, left), asImmediate(b, right)));
            }
          }
          else {
            l = asLocal(b, soot.jimple.Jimple.v().newCmpExpr(asImmediate(b, left), asImmediate(b, right)));
          }
          b.add(Jimple.v().newIfStmt(comparisonInv(b, l, BooleanType.emitConstant(false)), false_label()));
          b.add(Jimple.v().newGotoStmt(true_label()));
        }
        else {
          b.add(Jimple.v().newIfStmt(comparison(b, left, right), true_label()));
          b.add(Jimple.v().newGotoStmt(false_label()));
          //b.add(Jimple.v().newIfStmt(comparisonInv(b, left, right), false_label()));
          //b.add(Jimple.v().newGotoStmt(true_label()));
        }
      }
      else {
        left = getLeftOperand().eval(b);
        right = getRightOperand().eval(b);
        b.add(Jimple.v().newIfStmt(comparison(b, left, right), true_label()));
        b.add(Jimple.v().newGotoStmt(false_label()));
        //b.add(Jimple.v().newIfStmt(comparisonInv(b, left, right), false_label()));
        //b.add(Jimple.v().newGotoStmt(true_label()));
      }
    }
  }

    // Declared in BooleanExpressions.jrag at line 275


  public soot.Value comparison(Body b, soot.Value left, soot.Value right) {
    throw new Error("comparison not supported for " + getClass().getName());
  }

    // Declared in BooleanExpressions.jrag at line 297


  public soot.Value comparisonInv(Body b, soot.Value left, soot.Value right) {
    throw new Error("comparisonInv not supported for " + getClass().getName());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 178

    public RelationalExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 178
    public RelationalExpr(Expr p0, Expr p1) {
        setChild(p0, 0);
        setChild(p1, 1);
    }

    // Declared in java.ast at line 15


  protected int numChildren() {
    return 2;
  }

    // Declared in java.ast at line 18

  public boolean mayHaveRewrite() { return false; }

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

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 344
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        if(type_computed)
            return type_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        type_value = type_compute();
        if(isFinal && num == boundariesCrossed)
            type_computed = true;
        return type_value;
    }

    private TypeDecl type_compute() {  return typeBoolean();  }

    // Declared in BooleanExpressions.jrag at line 29
 @SuppressWarnings({"unchecked", "cast"})     public boolean definesLabel() {
        boolean definesLabel_value = definesLabel_compute();
        return definesLabel_value;
    }

    private boolean definesLabel_compute() {  return false;  }

    // Declared in BooleanExpressions.jrag at line 69
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
        if(caller == getRightOperandNoTransform()) {
            return false_label();
        }
        if(caller == getLeftOperandNoTransform()) {
            return false_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
    }

    // Declared in BooleanExpressions.jrag at line 70
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
        if(caller == getRightOperandNoTransform()) {
            return true_label();
        }
        if(caller == getLeftOperandNoTransform()) {
            return true_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
