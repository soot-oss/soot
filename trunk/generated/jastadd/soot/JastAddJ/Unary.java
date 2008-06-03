
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;



public abstract class Unary extends Expr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Unary clone() throws CloneNotSupportedException {
        Unary node = (Unary)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
    // Declared in PrettyPrint.jadd at line 382


  // Pre and post operations for unary expression
  
  public void toString(StringBuffer s) {
    s.append(printPreOp());
    getOperand().toString(s);
    s.append(printPostOp());
  }

    // Declared in Expressions.jrag at line 670


  public soot.Value eval(Body b) {
    return super.eval(b);
  }

    // Declared in Expressions.jrag at line 716


  public soot.Value emitPostfix(Body b, int constant) {
    soot.Value lvalue = getOperand().eval(b);
    Value v = lvalue instanceof Local ? lvalue : (Value)lvalue.clone();
    TypeDecl type = getOperand().type().binaryNumericPromotion(typeInt());
    Value value = b.newTemp(getOperand().type().emitCastTo(b, v, type));
    Value rvalue = typeInt().emitCastTo(b, IntType.emitConstant(constant), type);
    Value sum = asRValue(b, type.emitCastTo(b,
      Jimple.v().newAddExpr(asImmediate(b, value), asImmediate(b, rvalue)),
      getOperand().type()
    ));
    getOperand().emitStore(b, lvalue, sum);
    return value;
  }

    // Declared in Expressions.jrag at line 733


  public soot.Value emitPrefix(Body b, int constant) {
    soot.Value lvalue = getOperand().eval(b);
    Value v = lvalue instanceof Local ? lvalue : (Value)lvalue.clone();
    TypeDecl type = getOperand().type().binaryNumericPromotion(typeInt());
    Value value = getOperand().type().emitCastTo(b, v, type);
    Value rvalue = typeInt().emitCastTo(b, IntType.emitConstant(constant), type);
    Value result = asLocal(b, type.emitCastTo(b,
      Jimple.v().newAddExpr(asImmediate(b, value), asImmediate(b, rvalue)),
      getOperand().type()
    ));
    getOperand().emitStore(b, lvalue, result);
    return result;
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 139

    public Unary() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 139
    public Unary(Expr p0) {
        setChild(p0, 0);
    }

    // Declared in java.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in java.ast at line 17

  public boolean mayHaveRewrite() { return false; }

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

    // Declared in DefiniteAssignment.jrag at line 402
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return getOperand().isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 846
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {  return getOperand().isDUafter(v);  }

    // Declared in PrettyPrint.jadd at line 388
 @SuppressWarnings({"unchecked", "cast"})     public String printPostOp() {
        String printPostOp_value = printPostOp_compute();
        return printPostOp_value;
    }

    private String printPostOp_compute() {  return "";  }

    // Declared in PrettyPrint.jadd at line 392
 @SuppressWarnings({"unchecked", "cast"})     public String printPreOp() {
        String printPreOp_value = printPreOp_compute();
        return printPreOp_value;
    }

    private String printPreOp_compute() {  return "";  }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 314
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

    private TypeDecl type_compute() {  return getOperand().type();  }

    // Declared in DefiniteAssignment.jrag at line 44
    public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
        if(caller == getOperandNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isSource(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
