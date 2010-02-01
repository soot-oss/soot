
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public abstract class Binary extends Expr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isConstant_visited = -1;
        isConstant_computed = false;
        isConstant_initialized = false;
        isDAafterTrue_Variable_values = null;
        isDAafterFalse_Variable_values = null;
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        isDUbefore_Variable_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public Binary clone() throws CloneNotSupportedException {
        Binary node = (Binary)super.clone();
        node.isConstant_visited = -1;
        node.isConstant_computed = false;
        node.isConstant_initialized = false;
        node.isDAafterTrue_Variable_values = null;
        node.isDAafterFalse_Variable_values = null;
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.isDUbefore_Variable_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in PrettyPrint.jadd at line 393


  // Binary Expr

  public void toString(StringBuffer s) {
    getLeftOperand().toString(s);
    s.append(printOp());
    getRightOperand().toString(s);
  }

    // Declared in Expressions.jrag at line 772


  public soot.Value eval(Body b) {
    return asLocal(b, emitOperation(b, 
      getLeftOperand().type().emitCastTo(b,  // Binary numeric promotion
        getLeftOperand(),
        type()
      ),
      getRightOperand().type().emitCastTo(b, // Binary numeric promotion
        getRightOperand(),
        type()
      )
    ));
  }

    // Declared in Expressions.jrag at line 785


  public soot.Value emitShiftExpr(Body b) {
    return asLocal(b, emitOperation(b, 
      getLeftOperand().type().emitCastTo(b,  // Binary numeric promotion
        getLeftOperand(),
        type()
      ),
      getRightOperand().type().emitCastTo(b,
        getRightOperand(),
        typeInt()
      )
    ));
  }

    // Declared in Expressions.jrag at line 802


  public soot.Value emitOperation(Body b, soot.Value left, soot.Value right) {
    throw new Error("emitOperation not implemented in " + getClass().getName());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 153

    public Binary() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 153
    public Binary(Expr p0, Expr p1) {
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

    // Declared in ConstantExpression.jrag at line 516
private TypeDecl refined_ConstantExpression_Binary_binaryNumericPromotedType()
{
    TypeDecl leftType = left().type();
    TypeDecl rightType = right().type();
    if(leftType.isString())
      return leftType;
    if(rightType.isString())
      return rightType;
    if(leftType.isNumericType() && rightType.isNumericType())
      return leftType.binaryNumericPromotion(rightType);
    if(leftType.isBoolean() && rightType.isBoolean())
      return leftType;
    return unknownType();
  }

    // Declared in PrettyPrint.jadd at line 399
 @SuppressWarnings({"unchecked", "cast"})     public abstract String printOp();
    protected int isConstant_visited = -1;
    protected boolean isConstant_computed = false;
    protected boolean isConstant_initialized = false;
    protected boolean isConstant_value;
    // Declared in ConstantExpression.jrag at line 491
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstant() {
        if(isConstant_computed) {
            return isConstant_value;
        }
        ASTNode$State state = state();
        if (!isConstant_initialized) {
            isConstant_initialized = true;
            isConstant_value = false;
        }
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            do {
                isConstant_visited = state.CIRCLE_INDEX;
                state.CHANGE = false;
                boolean new_isConstant_value = isConstant_compute();
                if (new_isConstant_value!=isConstant_value)
                    state.CHANGE = true;
                isConstant_value = new_isConstant_value; 
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
            isConstant_computed = true;
            }
            else {
            state.RESET_CYCLE = true;
            isConstant_compute();
            state.RESET_CYCLE = false;
              isConstant_computed = false;
              isConstant_initialized = false;
            }
            state.IN_CIRCLE = false; 
            return isConstant_value;
        }
        if(isConstant_visited != state.CIRCLE_INDEX) {
            isConstant_visited = state.CIRCLE_INDEX;
            if (state.RESET_CYCLE) {
                isConstant_computed = false;
                isConstant_initialized = false;
                isConstant_visited = -1;
                return isConstant_value;
            }
            boolean new_isConstant_value = isConstant_compute();
            if (new_isConstant_value!=isConstant_value)
                state.CHANGE = true;
            isConstant_value = new_isConstant_value; 
            return isConstant_value;
        }
        return isConstant_value;
    }

    private boolean isConstant_compute() {  return getLeftOperand().isConstant() && getRightOperand().isConstant();  }

    // Declared in ConstantExpression.jrag at line 514
 @SuppressWarnings({"unchecked", "cast"})     public Expr left() {
        ASTNode$State state = state();
        Expr left_value = left_compute();
        return left_value;
    }

    private Expr left_compute() {  return getLeftOperand();  }

    // Declared in ConstantExpression.jrag at line 515
 @SuppressWarnings({"unchecked", "cast"})     public Expr right() {
        ASTNode$State state = state();
        Expr right_value = right_compute();
        return right_value;
    }

    private Expr right_compute() {  return getRightOperand();  }

    // Declared in AutoBoxing.jrag at line 205
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl binaryNumericPromotedType() {
        ASTNode$State state = state();
        TypeDecl binaryNumericPromotedType_value = binaryNumericPromotedType_compute();
        return binaryNumericPromotedType_value;
    }

    private TypeDecl binaryNumericPromotedType_compute() {
    TypeDecl leftType = left().type();
    TypeDecl rightType = right().type();
    if(leftType.isBoolean() && rightType.isBoolean()) {
      return leftType.isReferenceType() ? leftType.unboxed() : leftType;
    }
    return refined_ConstantExpression_Binary_binaryNumericPromotedType();
  }

    protected java.util.Map isDAafterTrue_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 405
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

    private boolean isDAafterTrue_compute(Variable v) {  return getRightOperand().isDAafter(v) || isFalse();  }

    protected java.util.Map isDAafterFalse_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 406
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

    private boolean isDAafterFalse_compute(Variable v) {  return getRightOperand().isDAafter(v) || isTrue();  }

    protected java.util.Map isDAafter_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 408
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

    private boolean isDAafter_compute(Variable v) {  return getRightOperand().isDAafter(v);  }

    protected java.util.Map isDUafter_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 849
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

    private boolean isDUafter_compute(Variable v) {  return getRightOperand().isDUafter(v);  }

    protected java.util.Map isDUbefore_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 697
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUbefore(Variable v) {
        Object _parameters = v;
if(isDUbefore_Variable_values == null) isDUbefore_Variable_values = new java.util.HashMap(4);
        if(isDUbefore_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDUbefore_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDUbefore_Variable_value = getParent().Define_boolean_isDUbefore(this, null, v);
        if(isFinal && num == state().boundariesCrossed)
            isDUbefore_Variable_values.put(_parameters, Boolean.valueOf(isDUbefore_Variable_value));
        return isDUbefore_Variable_value;
    }

    // Declared in DefiniteAssignment.jrag at line 409
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getRightOperandNoTransform()) {
            return getLeftOperand().isDAafter(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 850
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getRightOperandNoTransform()) {
            return getLeftOperand().isDUafter(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
