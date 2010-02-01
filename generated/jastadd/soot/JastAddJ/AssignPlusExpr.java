
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class AssignPlusExpr extends AssignAdditiveExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AssignPlusExpr clone() throws CloneNotSupportedException {
        AssignPlusExpr node = (AssignPlusExpr)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AssignPlusExpr copy() {
      try {
          AssignPlusExpr node = (AssignPlusExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AssignPlusExpr fullCopy() {
        AssignPlusExpr res = (AssignPlusExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in TypeCheck.jrag at line 71

  
  public void typeCheck() {
    if(!getDest().isVariable())
      error("left hand side is not a variable");
    else if(getSource().type().isUnknown() || getDest().type().isUnknown())
      return;
    else if(getDest().type().isString() && !(getSource().type().isVoid()))
      return;
    else if(getSource().type().isBoolean() || getDest().type().isBoolean())
      error("Operator + does not operate on boolean types");
    else if(getSource().type().isPrimitive() && getDest().type().isPrimitive())
      return;
    else
      error("can not assign " + getDest() + " of type " + getDest().type().typeName() +
            " a value of type " + sourceType().typeName());
  }

    // Declared in Expressions.jrag at line 84


  // string addition assign expression
  public soot.Value eval(Body b) {
    TypeDecl dest = getDest().type();
    TypeDecl source = getSource().type();
    if(dest.isString()) {
      
      Value lvalue = getDest().eval(b);

      Value v = asImmediate(b, lvalue);

      // new StringBuffer(left)
      Local local = b.newTemp(b.newNewExpr(
        lookupType("java.lang", "StringBuffer").sootRef(), this));
      b.setLine(this);
      b.add(b.newInvokeStmt(
        b.newSpecialInvokeExpr(local, 
          Scene.v().getMethod("<java.lang.StringBuffer: void <init>(java.lang.String)>").makeRef(),
          v,
          this
        ), this));

      // append right
      Local rightResult = b.newTemp(
        b.newVirtualInvokeExpr(local,
          lookupType("java.lang", "StringBuffer").methodWithArgs("append", new TypeDecl[] { source.stringPromotion() }).sootRef(),
          asImmediate(b, getSource().eval(b)),
          this
        ));

      // toString
      Local result = b.newTemp(
        b.newVirtualInvokeExpr(rightResult,
          Scene.v().getMethod("<java.lang.StringBuffer: java.lang.String toString()>").makeRef(),
          this
        ));
  
      Value v2 = lvalue instanceof Local ? lvalue : (Value)lvalue.clone();
      getDest().emitStore(b, v2, result, this);
      return result;
    }
    else {
      return super.eval(b);
    }
  }

    // Declared in Expressions.jrag at line 165

  public soot.Value createAssignOp(Body b, soot.Value fst, soot.Value snd) {
    return b.newAddExpr(asImmediate(b, fst), asImmediate(b, snd), this);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 109

    public AssignPlusExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 109
    public AssignPlusExpr(Expr p0, Expr p1) {
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
    // Declared in java.ast line 99
    public void setDest(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getDest() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getDestNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 99
    public void setSource(Expr node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Expr getSource() {
        return (Expr)getChild(1);
    }

    // Declared in java.ast at line 9


    public Expr getSourceNoTransform() {
        return (Expr)getChildNoTransform(1);
    }

    // Declared in PrettyPrint.jadd at line 251
 @SuppressWarnings({"unchecked", "cast"})     public String printOp() {
        ASTNode$State state = state();
        String printOp_value = printOp_compute();
        return printOp_value;
    }

    private String printOp_compute() {  return " += ";  }

    // Declared in TypeCheck.jrag at line 111
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl sourceType() {
        ASTNode$State state = state();
        TypeDecl sourceType_value = sourceType_compute();
        return sourceType_value;
    }

    private TypeDecl sourceType_compute() {
    TypeDecl left = getDest().type();
    TypeDecl right = getSource().type();
    if(!left.isString() && !right.isString())
      return super.sourceType();
    if(left.isVoid() || right.isVoid())
      return unknownType();
    return left.isString() ? left : right;
  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
