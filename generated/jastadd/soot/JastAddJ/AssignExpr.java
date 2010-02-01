
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public abstract class AssignExpr extends Expr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AssignExpr clone() throws CloneNotSupportedException {
        AssignExpr node = (AssignExpr)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in DefiniteAssignment.jrag at line 464

  // 16.2.2 9th bullet
  protected boolean checkDUeverywhere(Variable v) {
    if(getDest().isVariable() && getDest().varDecl() == v)
      if(!getSource().isDAafter(v))
        return false;
    return super.checkDUeverywhere(v);
  }

    // Declared in NodeConstructors.jrag at line 94


  public static Stmt asStmt(Expr left, Expr right) {
    return new ExprStmt(new AssignSimpleExpr(left, right));
  }

    // Declared in PrettyPrint.jadd at line 240


  // Assign Expression

  public void toString(StringBuffer s) {
    getDest().toString(s);
    s.append(printOp());
    getSource().toString(s);
  }

    // Declared in TypeCheck.jrag at line 52

  
  public void typeCheck() {
    if(!getDest().isVariable())
      error("left hand side is not a variable");
    else {
      TypeDecl source = sourceType();
      TypeDecl dest = getDest().type();
      if(getSource().type().isPrimitive() && getDest().type().isPrimitive())
        return;
      error("can not assign " + getDest() + " of type " + getDest().type().typeName() +
            " a value of type " + sourceType().typeName());
    }
  }

    // Declared in Expressions.jrag at line 62


  // compound assign expression
  public soot.Value eval(Body b) {
    TypeDecl dest = getDest().type();
    TypeDecl source = getSource().type();
    TypeDecl type;
    if(dest.isNumericType() && source.isNumericType())
      type = dest.binaryNumericPromotion(source);
    else 
      type = dest;
    Value lvalue = getDest().eval(b);
    Value v = lvalue instanceof Local ? lvalue : (Value)lvalue.clone();
    Value value = b.newTemp(dest.emitCastTo(b, v, type, this));
    Value rvalue = source.emitCastTo(b, getSource(), type);
    Value result = asImmediate(b, type.emitCastTo(b,
      createAssignOp(b, value, rvalue),
      dest,
      getDest()
    ));
    getDest().emitStore(b, lvalue, result, this);
    return result;
  }

    // Declared in Expressions.jrag at line 129


  // shift assign expression
  public soot.Value emitShiftExpr(Body b) {
    TypeDecl dest = getDest().type();
    TypeDecl source = getSource().type();
    TypeDecl type = dest.unaryNumericPromotion();

    Value lvalue = getDest().eval(b);
    Value v = lvalue instanceof Local ? lvalue : (Value)lvalue.clone();
    Value value = b.newTemp(dest.emitCastTo(b, v, type, getDest()));
    Value rvalue = source.emitCastTo(b, getSource(), typeInt());
    Value result = asImmediate(b, type.emitCastTo(b,
      createAssignOp(b, value, rvalue),
      dest,
      getDest()
    ));
    getDest().emitStore(b, lvalue, result, this);
    return result;
  }

    // Declared in Expressions.jrag at line 153



  // create the operation for a compound assign expression
  public soot.Value createAssignOp(Body b, soot.Value fst, soot.Value snd) {
    throw new Error("Operation createAssignOp is not implemented for " + getClass().getName());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 99

    public AssignExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 99
    public AssignExpr(Expr p0, Expr p1) {
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

    // Declared in DefiniteAssignment.jrag at line 394
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        ASTNode$State state = state();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return getSource().isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 398
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterTrue(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterTrue_Variable_value = isDAafterTrue_compute(v);
        return isDAafterTrue_Variable_value;
    }

    private boolean isDAafterTrue_compute(Variable v) {  return isDAafter(v) || isFalse();  }

    // Declared in DefiniteAssignment.jrag at line 399
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterFalse(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterFalse_Variable_value = isDAafterFalse_compute(v);
        return isDAafterFalse_Variable_value;
    }

    private boolean isDAafterFalse_compute(Variable v) {  return isDAafter(v) || isTrue();  }

    // Declared in DefiniteAssignment.jrag at line 827
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        ASTNode$State state = state();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {  return getSource().isDUafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 830
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterTrue(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterTrue_Variable_value = isDUafterTrue_compute(v);
        return isDUafterTrue_Variable_value;
    }

    private boolean isDUafterTrue_compute(Variable v) {  return isDUafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 831
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterFalse(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterFalse_Variable_value = isDUafterFalse_compute(v);
        return isDUafterFalse_Variable_value;
    }

    private boolean isDUafterFalse_compute(Variable v) {  return isDUafter(v);  }

    // Declared in PrettyPrint.jadd at line 246
 @SuppressWarnings({"unchecked", "cast"})     public String printOp() {
        ASTNode$State state = state();
        String printOp_value = printOp_compute();
        return printOp_value;
    }

    private String printOp_compute() {  return " = ";  }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 298
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

    private TypeDecl type_compute() {  return getDest().type();  }

    // Declared in TypeCheck.jrag at line 109
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl sourceType() {
        ASTNode$State state = state();
        TypeDecl sourceType_value = sourceType_compute();
        return sourceType_value;
    }

    private TypeDecl sourceType_compute() {  return getSource().type().isPrimitive() ? getSource().type() : unknownType();  }

    // Declared in DefiniteAssignment.jrag at line 19
    public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
        if(caller == getSourceNoTransform()) {
            return false;
        }
        if(caller == getDestNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isDest(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 29
    public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
        if(caller == getSourceNoTransform()) {
            return true;
        }
        if(caller == getDestNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isSource(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 396
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getDestNoTransform()) {
            return isDAbefore(v);
        }
        if(caller == getSourceNoTransform()) {
            return getDest().isDAafter(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 829
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getDestNoTransform()) {
            return isDUbefore(v);
        }
        if(caller == getSourceNoTransform()) {
            return getDest().isDUafter(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in SyntacticClassification.jrag at line 99
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getDestNoTransform()) {
            return NameType.EXPRESSION_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
