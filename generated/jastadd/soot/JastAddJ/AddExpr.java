
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


public class AddExpr extends AdditiveExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AddExpr clone() throws CloneNotSupportedException {
        AddExpr node = (AddExpr)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AddExpr copy() {
      try {
          AddExpr node = (AddExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AddExpr fullCopy() {
        AddExpr res = (AddExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in TypeCheck.jrag at line 172

  
  // 15.18
  public void typeCheck() {
    TypeDecl left = getLeftOperand().type();
    TypeDecl right = getRightOperand().type();
    if(!left.isString() && !right.isString())
      super.typeCheck();
    else if(left.isVoid())
      error("The type void of the left hand side is not numeric");
    else if(right.isVoid())
      error("The type void of the right hand side is not numeric");
  }

    // Declared in Expressions.jrag at line 783

  public soot.Value emitOperation(Body b, soot.Value left, soot.Value right) {
    return asLocal(b, Jimple.v().newAddExpr(asImmediate(b, left), asImmediate(b, right)));
  }

    // Declared in Expressions.jrag at line 817


  public soot.Value eval(Body b) {
    if(type().isString() && isConstant())
      return soot.jimple.StringConstant.v(constant().stringValue());
    if(isStringAdd()) {
      Local v;
      if(firstStringAddPart()) {
        // new StringBuffer
        v = b.newTemp(Jimple.v().newNewExpr(
          lookupType("java.lang", "StringBuffer").sootRef()));
        b.setLine(this);
        b.add(Jimple.v().newInvokeStmt(
          Jimple.v().newSpecialInvokeExpr(v, 
          Scene.v().getMethod("<java.lang.StringBuffer: void <init>()>").makeRef()
        )));
        b.setLine(this);
        b.add(Jimple.v().newInvokeStmt(
          Jimple.v().newVirtualInvokeExpr(v,
            lookupType("java.lang", "StringBuffer").methodWithArgs("append", new TypeDecl[] { getLeftOperand().type().stringPromotion() }).sootRef(),
            asImmediate(b, getLeftOperand().eval(b))
          )));
      }
      else
        v = (Local)getLeftOperand().eval(b);
      // append
      b.setLine(this);
      b.add(Jimple.v().newInvokeStmt(
        Jimple.v().newVirtualInvokeExpr(v,
          lookupType("java.lang", "StringBuffer").methodWithArgs("append", new TypeDecl[] { getRightOperand().type().stringPromotion() }).sootRef(),
          asImmediate(b, getRightOperand().eval(b))
        )));
      if(lastStringAddPart()) {
        return b.newTemp(
          Jimple.v().newVirtualInvokeExpr(v,
            Scene.v().getMethod("<java.lang.StringBuffer: java.lang.String toString()>").makeRef()
        ));
      }
      else
        return v;
    }
    else 
    return soot.jimple.Jimple.v().newAddExpr(
      b.newTemp(
        getLeftOperand().type().emitCastTo(b,  // Binary numeric promotion
          getLeftOperand(),
          type()
        )
      ),
      asImmediate(b,
        getRightOperand().type().emitCastTo(b, // Binary numeric promotion
          getRightOperand(),
          type()
        )
      )
    );
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 161

    public AddExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 161
    public AddExpr(Expr p0, Expr p1) {
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

    // Declared in ConstantExpression.jrag at line 121
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        Constant constant_value = constant_compute();
        return constant_value;
    }

    private Constant constant_compute() {  return type().add(getLeftOperand().constant(), getRightOperand().constant());  }

    // Declared in PrettyPrint.jadd at line 420
 @SuppressWarnings({"unchecked", "cast"})     public String printOp() {
        String printOp_value = printOp_compute();
        return printOp_value;
    }

    private String printOp_compute() {  return " + ";  }

    // Declared in TypeAnalysis.jrag at line 327
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

    private TypeDecl type_compute() {
    TypeDecl left = getLeftOperand().type();
    TypeDecl right = getRightOperand().type();
    if(!left.isString() && !right.isString())
      return super.type();
    else {
      if(left.isVoid() || right.isVoid())
        return unknownType();
      // pick the string type
      return left.isString() ? left : right;
    }
  }

    // Declared in InnerClasses.jrag at line 86
 @SuppressWarnings({"unchecked", "cast"})     public boolean isStringAdd() {
        boolean isStringAdd_value = isStringAdd_compute();
        return isStringAdd_value;
    }

    private boolean isStringAdd_compute() {  return type().isString() && !isConstant();  }

    // Declared in InnerClasses.jrag at line 88
 @SuppressWarnings({"unchecked", "cast"})     public boolean firstStringAddPart() {
        boolean firstStringAddPart_value = firstStringAddPart_compute();
        return firstStringAddPart_value;
    }

    private boolean firstStringAddPart_compute() {  return type().isString() && !getLeftOperand().isStringAdd();  }

    // Declared in InnerClasses.jrag at line 89
 @SuppressWarnings({"unchecked", "cast"})     public boolean lastStringAddPart() {
        boolean lastStringAddPart_value = lastStringAddPart_compute();
        return lastStringAddPart_value;
    }

    private boolean lastStringAddPart_compute() {  return !getParent().isStringAdd();  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
