
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class MinusExpr extends Unary implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public MinusExpr clone() throws CloneNotSupportedException {
        MinusExpr node = (MinusExpr)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public MinusExpr copy() {
      try {
          MinusExpr node = (MinusExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public MinusExpr fullCopy() {
        MinusExpr res = (MinusExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in TypeCheck.jrag at line 275

  
  // 15.15.4
  public void typeCheck() {
    if(!getOperand().type().isNumericType())
      error("unary minus only operates on numeric types");
  }

    // Declared in Expressions.jrag at line 692

  public soot.Value eval(Body b) { 
    return b.newNegExpr(asImmediate(b, getOperand().eval(b)), this);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 142

    public MinusExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 142
    public MinusExpr(Expr p0) {
        setChild(p0, 0);
    }

    // Declared in java.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in java.ast at line 17

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 139
    public void setOperand(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getOperand() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getOperandNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in ConstantExpression.jrag at line 114
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        ASTNode$State state = state();
        Constant constant_value = constant_compute();
        return constant_value;
    }

    private Constant constant_compute() {  return type().minus(getOperand().constant());  }

    // Declared in ConstantExpression.jrag at line 488
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstant() {
        ASTNode$State state = state();
        boolean isConstant_value = isConstant_compute();
        return isConstant_value;
    }

    private boolean isConstant_compute() {  return getOperand().isConstant();  }

    // Declared in PrettyPrint.jadd at line 378
 @SuppressWarnings({"unchecked", "cast"})     public String printPreOp() {
        ASTNode$State state = state();
        String printPreOp_value = printPreOp_compute();
        return printPreOp_value;
    }

    private String printPreOp_compute() {  return "-";  }

    // Declared in TypeAnalysis.jrag at line 316
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        if(type_computed) {
            return type_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        type_value = type_compute();
        if(isFinal && num == state().boundariesCrossed)
            type_computed = true;
        return type_value;
    }

    private TypeDecl type_compute() {  return getOperand().type().unaryNumericPromotion();  }

public ASTNode rewriteTo() {
    // Declared in ConstantExpression.jrag at line 246
    if(getOperand() instanceof IntegerLiteral && ((IntegerLiteral)getOperand()).isDecimal() && getOperand().isPositive()) {
        state().duringConstantExpression++;
        ASTNode result = rewriteRule0();
        state().duringConstantExpression--;
        return result;
    }

    // Declared in ConstantExpression.jrag at line 251
    if(getOperand() instanceof LongLiteral && ((LongLiteral)getOperand()).isDecimal() && getOperand().isPositive()) {
        state().duringConstantExpression++;
        ASTNode result = rewriteRule1();
        state().duringConstantExpression--;
        return result;
    }

    return super.rewriteTo();
}

    // Declared in ConstantExpression.jrag at line 246
    private IntegerLiteral rewriteRule0() {
        return new IntegerLiteral("-" + ((IntegerLiteral)getOperand()).getLITERAL());
    }
    // Declared in ConstantExpression.jrag at line 251
    private LongLiteral rewriteRule1() {
        return new LongLiteral("-" + ((LongLiteral)getOperand()).getLITERAL());
    }
}
