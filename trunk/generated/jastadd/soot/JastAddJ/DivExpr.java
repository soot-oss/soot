
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


public class DivExpr extends MultiplicativeExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isConstant_visited = 0;
        isConstant_computed = false;
        isConstant_initialized = false;
    }
     @SuppressWarnings({"unchecked", "cast"})  public DivExpr clone() throws CloneNotSupportedException {
        DivExpr node = (DivExpr)super.clone();
        node.isConstant_visited = 0;
        node.isConstant_computed = false;
        node.isConstant_initialized = false;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public DivExpr copy() {
      try {
          DivExpr node = (DivExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public DivExpr fullCopy() {
        DivExpr res = (DivExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Expressions.jrag at line 792

  public soot.Value emitOperation(Body b, soot.Value left, soot.Value right) {
    return asLocal(b, Jimple.v().newDivExpr(asImmediate(b, left), asImmediate(b, right)));
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 158

    public DivExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 158
    public DivExpr(Expr p0, Expr p1) {
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

    // Declared in ConstantExpression.jrag at line 118
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        Constant constant_value = constant_compute();
        return constant_value;
    }

    private Constant constant_compute() {  return type().div(getLeftOperand().constant(), getRightOperand().constant());  }

    protected int isConstant_visited;
    protected boolean isConstant_computed = false;
    protected boolean isConstant_initialized = false;
    protected boolean isConstant_value;
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstant() {
        if(isConstant_computed)
            return isConstant_value;
        if (!isConstant_initialized) {
            isConstant_initialized = true;
            isConstant_value = false;
        }
        if (!IN_CIRCLE) {
            IN_CIRCLE = true;
            int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
            CIRCLE_INDEX = 1;
            do {
                isConstant_visited = CIRCLE_INDEX;
                CHANGE = false;
                boolean new_isConstant_value = isConstant_compute();
                if (new_isConstant_value!=isConstant_value)
                    CHANGE = true;
                isConstant_value = new_isConstant_value; 
                CIRCLE_INDEX++;
            } while (CHANGE);
            if(isFinal && num == boundariesCrossed)
{
            isConstant_computed = true;
            }
            else {
            RESET_CYCLE = true;
            isConstant_compute();
            RESET_CYCLE = false;
              isConstant_computed = false;
              isConstant_initialized = false;
            }
            IN_CIRCLE = false; 
            return isConstant_value;
        }
        if(isConstant_visited != CIRCLE_INDEX) {
            isConstant_visited = CIRCLE_INDEX;
            if (RESET_CYCLE) {
                isConstant_computed = false;
                isConstant_initialized = false;
                return isConstant_value;
            }
            boolean new_isConstant_value = isConstant_compute();
            if (new_isConstant_value!=isConstant_value)
                CHANGE = true;
            isConstant_value = new_isConstant_value; 
            return isConstant_value;
        }
        return isConstant_value;
    }

    private boolean isConstant_compute() {  return getLeftOperand().isConstant() && getRightOperand().isConstant() && !(getRightOperand().type().isInt() && getRightOperand().constant().intValue() == 0);  }

    // Declared in PrettyPrint.jadd at line 418
 @SuppressWarnings({"unchecked", "cast"})     public String printOp() {
        String printOp_value = printOp_compute();
        return printOp_value;
    }

    private String printOp_compute() {  return " / ";  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
