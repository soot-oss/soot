
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class AssignSimpleExpr extends AssignExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AssignSimpleExpr clone() throws CloneNotSupportedException {
        AssignSimpleExpr node = (AssignSimpleExpr)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AssignSimpleExpr copy() {
      try {
          AssignSimpleExpr node = (AssignSimpleExpr)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AssignSimpleExpr fullCopy() {
        AssignSimpleExpr res = (AssignSimpleExpr)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in TypeCheck.jrag at line 44


  // 5.2 Assignment Conversion
  public void typeCheck() {
    if(!getDest().isVariable())
      error("left hand side is not a variable");
    else if(!sourceType().assignConversionTo(getDest().type(), getSource()) && !sourceType().isUnknown())
      error("can not assign " + getDest() + " of type " + getDest().type().typeName() +
            " a value of type " + sourceType().typeName());
  }

    // Declared in Expressions.jrag at line 50


  // simple assign expression
  public soot.Value eval(Body b) {
    Value lvalue = getDest().eval(b);
    Value rvalue = asRValue(b,
      getSource().type().emitCastTo(b, // Assign conversion
        getSource(),
        getDest().type()
      )
    );
    return getDest().emitStore(b, lvalue, asImmediate(b, rvalue), this);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 101

    public AssignSimpleExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 101
    public AssignSimpleExpr(Expr p0, Expr p1) {
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

    // Declared in PrettyPrint.jadd at line 247
 @SuppressWarnings({"unchecked", "cast"})     public String printOp() {
        ASTNode$State state = state();
        String printOp_value = printOp_compute();
        return printOp_value;
    }

    private String printOp_compute() {  return " = ";  }

    // Declared in TypeCheck.jrag at line 121
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl sourceType() {
        ASTNode$State state = state();
        TypeDecl sourceType_value = sourceType_compute();
        return sourceType_value;
    }

    private TypeDecl sourceType_compute() {  return getSource().type();  }

    // Declared in DefiniteAssignment.jrag at line 17
    public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
        if(caller == getDestNoTransform()) {
            return true;
        }
        return super.Define_boolean_isDest(caller, child);
    }

    // Declared in DefiniteAssignment.jrag at line 27
    public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
        if(caller == getDestNoTransform()) {
            return false;
        }
        return super.Define_boolean_isSource(caller, child);
    }

    // Declared in GenericMethodsInference.jrag at line 36
    public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
        if(caller == getSourceNoTransform()) {
            return getDest().type();
        }
        return getParent().Define_TypeDecl_assignConvertedType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
