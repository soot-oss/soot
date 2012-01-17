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
 * @declaredat java.ast:130
 */
public class ArrayCreationExpr extends PrimaryExpr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
    numArrays_computed = false;
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
  public ArrayCreationExpr clone() throws CloneNotSupportedException {
    ArrayCreationExpr node = (ArrayCreationExpr)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.numArrays_computed = false;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayCreationExpr copy() {
      try {
        ArrayCreationExpr node = (ArrayCreationExpr)clone();
        if(children != null) node.children = (ASTNode[])children.clone();
        return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
  }
  /**
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayCreationExpr fullCopy() {
    ArrayCreationExpr res = (ArrayCreationExpr)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:356
   */
  public void toString(StringBuffer s) {
    s.append("new ");
    getTypeAccess().toString(s);
    if(hasArrayInit()) {
      getArrayInit().toString(s);
    }
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:633
   */
  public soot.Value eval(Body b) {
    if(hasArrayInit()) {
      return getArrayInit().eval(b);
    }
    else {
      ArrayList list = new ArrayList();
      getTypeAccess().addArraySize(b, list);
      if(numArrays() == 1) {
        soot.Value size = (soot.Value)list.get(0);
        return b.newNewArrayExpr(
          type().componentType().getSootType(),
          asImmediate(b, size),
          this
        );
      }
      else {
        return b.newNewMultiArrayExpr(
          (soot.ArrayType)type().getSootType(),
          list,
          this
        );
      }
    }
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public ArrayCreationExpr() {
    super();

    setChild(new Opt(), 1);

  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public ArrayCreationExpr(Access p0, Opt<ArrayInit> p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:15
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:21
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setTypeAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Getter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Access getTypeAccess() {
    return (Access)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * Setter for ArrayInitOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setArrayInitOpt(Opt<ArrayInit> opt) {
    setChild(opt, 1);
  }
  /**
   * Does this node have a ArrayInit child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasArrayInit() {
    return getArrayInitOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child ArrayInit
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ArrayInit getArrayInit() {
    return (ArrayInit)getArrayInitOpt().getChild(0);
  }
  /**
   * Setter for optional child ArrayInit
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setArrayInit(ArrayInit node) {
    getArrayInitOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<ArrayInit> getArrayInitOpt() {
    return (Opt<ArrayInit>)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<ArrayInit> getArrayInitOptNoTransform() {
    return (Opt<ArrayInit>)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:431
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafterCreation(Variable v) {
      ASTNode$State state = state();
    boolean isDAafterCreation_Variable_value = isDAafterCreation_compute(v);
    return isDAafterCreation_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafterCreation_compute(Variable v) {  return getTypeAccess().isDAafter(v);  }
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:432
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
  private boolean isDAafter_compute(Variable v) {  return hasArrayInit() ? getArrayInit().isDAafter(v) : isDAafterCreation(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:859
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafterCreation(Variable v) {
      ASTNode$State state = state();
    boolean isDUafterCreation_Variable_value = isDUafterCreation_compute(v);
    return isDUafterCreation_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafterCreation_compute(Variable v) {  return getTypeAccess().isDUafter(v);  }
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:860
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
  private boolean isDUafter_compute(Variable v) {  return hasArrayInit() ? getArrayInit().isDUafter(v) : isDUafterCreation(v);  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:312
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
  private TypeDecl type_compute() {  return getTypeAccess().type();  }
  /**
   * @apilevel internal
   */
  protected boolean numArrays_computed = false;
  /**
   * @apilevel internal
   */
  protected int numArrays_value;
  /**
   * @attribute syn
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:69
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int numArrays() {
    if(numArrays_computed) {
      return numArrays_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    numArrays_value = numArrays_compute();
if(isFinal && num == state().boundariesCrossed) numArrays_computed = true;
    return numArrays_value;
  }
  /**
   * @apilevel internal
   */
  private int numArrays_compute() {
    int i = type().dimension();
    Access a = getTypeAccess();
    while(a instanceof ArrayTypeAccess && !(a instanceof ArrayTypeWithSizeAccess)) {
      i--;
      a = ((ArrayTypeAccess)a).getAccess();
    }
    return i;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:433
   * @apilevel internal
   */
  public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getArrayInitOptNoTransform()) {
      return isDAafterCreation(v);
    }
    return getParent().Define_boolean_isDAbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:862
   * @apilevel internal
   */
  public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
    if(caller == getArrayInitOptNoTransform()) {
      return isDUafterCreation(v);
    }
    return getParent().Define_boolean_isDUbefore(this, caller, v);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:87
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeAccessNoTransform()) {
      return NameType.TYPE_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:262
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_declType(ASTNode caller, ASTNode child) {
    if(caller == getArrayInitOptNoTransform()) {
      return type();
    }
    return getParent().Define_TypeDecl_declType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:63
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_expectedType(ASTNode caller, ASTNode child) {
    if(caller == getArrayInitOptNoTransform()) {
      return type().componentType();
    }
    return getParent().Define_TypeDecl_expectedType(this, caller);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
