package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;

/**
 * @ast node
 * @declaredat java.ast:103
 */
public abstract class AssignExpr extends Expr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AssignExpr clone() throws CloneNotSupportedException {
    AssignExpr node = (AssignExpr)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:462
   */
  protected boolean checkDUeverywhere(Variable v) {
    if(getDest().isVariable() && getDest().varDecl() == v)
      if(!getSource().isDAafter(v))
        return false;
    return super.checkDUeverywhere(v);
  }
  /**
   * @ast method 
   * @aspect NodeConstructors
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NodeConstructors.jrag:94
   */
  public static Stmt asStmt(Expr left, Expr right) {
    return new ExprStmt(new AssignSimpleExpr(left, right));
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:241
   */
  public void toString(StringBuffer s) {
    getDest().toString(s);
    s.append(printOp());
    getSource().toString(s);
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:52
   */
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
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:62
   */
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
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:129
   */
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
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:153
   */
  public soot.Value createAssignOp(Body b, soot.Value fst, soot.Value snd) {
    throw new Error("Operation createAssignOp is not implemented for " + getClass().getName());
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public AssignExpr() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public AssignExpr(Expr p0, Expr p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:14
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:20
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Dest
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setDest(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Dest
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getDest() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getDestNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for Source
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setSource(Expr node) {
    setChild(node, 1);
  }
  /**
   * Getter for Source
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getSource() {
    return (Expr)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getSourceNoTransform() {
    return (Expr)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:390
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
      ASTNode$State state = state();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {  return (getDest().isVariable() && getDest().varDecl() == v) || getSource().isDAafter(v);  }
  /*eq Stmt.isDAafter(Variable v) {
    //System.out.println("### isDAafter reached in " + getClass().getName());
    //throw new NullPointerException();
    throw new Error("Can not compute isDAafter for " + getClass().getName() + " at " + errorPrefix());
  }* @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:394
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterTrue(Variable v) {
      ASTNode$State state = state();
    boolean isDAafterTrue_Variable_value = isDAafterTrue_compute(v);
    return isDAafterTrue_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterTrue_compute(Variable v) {  return isDAafter(v) || isFalse();  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:395
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterFalse(Variable v) {
      ASTNode$State state = state();
    boolean isDAafterFalse_Variable_value = isDAafterFalse_compute(v);
    return isDAafterFalse_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterFalse_compute(Variable v) {  return isDAafter(v) || isTrue();  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:825
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
      ASTNode$State state = state();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {  return getSource().isDUafter(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:828
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafterTrue(Variable v) {
      ASTNode$State state = state();
    boolean isDUafterTrue_Variable_value = isDUafterTrue_compute(v);
    return isDUafterTrue_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafterTrue_compute(Variable v) {  return isDUafter(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:829
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafterFalse(Variable v) {
      ASTNode$State state = state();
    boolean isDUafterFalse_Variable_value = isDUafterFalse_compute(v);
    return isDUafterFalse_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafterFalse_compute(Variable v) {  return isDUafter(v);  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:247
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String printOp() {
      ASTNode$State state = state();
    String printOp_value = printOp_compute();
    return printOp_value;
  }
  /**
   * @apilevel internal
   */
  private String printOp_compute() {  return " = ";  }
  /**
   * @apilevel internal
   */
  protected boolean type_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl type_value;
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:298
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
    if(type_computed) {
      return type_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    type_value = type_compute();
if(isFinal && num == state().boundariesCrossed) type_computed = true;
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {  return getDest().type();  }
  /**
   * @attribute syn
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:109
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl sourceType() {
      ASTNode$State state = state();
    TypeDecl sourceType_value = sourceType_compute();
    return sourceType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl sourceType_compute() {  return getSource().type().isPrimitive() ? getSource().type() : unknownType();  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:19
   * @apilevel internal
   */
  public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
    if(caller == getSourceNoTransform()) {
      return false;
    }
    if(caller == getDestNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isDest(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:29
   * @apilevel internal
   */
  public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
    if(caller == getSourceNoTransform()) {
      return true;
    }
    if(caller == getDestNoTransform()) {
      return true;
    }
    return getParent().Define_boolean_isSource(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:392
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getDestNoTransform()) {
      return isDAbefore(v);
    }
    if(caller == getSourceNoTransform()) {
      return getDest().isDAafter(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:827
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getDestNoTransform()) {
      return isDUbefore(v);
    }
    if(caller == getSourceNoTransform()) {
      return getDest().isDUafter(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:99
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getDestNoTransform()) {
      return NameType.EXPRESSION_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
