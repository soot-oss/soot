/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.HashSet;
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
 * @production Access : {@link Expr};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:14
 */
public abstract class Access extends Expr implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    prevExpr_computed = false;
    prevExpr_value = null;
    hasPrevExpr_computed = false;
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
  public Access clone() throws CloneNotSupportedException {
    Access node = (Access)super.clone();
    node.prevExpr_computed = false;
    node.prevExpr_value = null;
    node.hasPrevExpr_computed = false;
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @ast method 
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:148
   */
  public Access addArrayDims(List list) {
    Access a = this;
    for(int i = 0; i < list.getNumChildNoTransform(); i++) {
      Dims dims = (Dims)list.getChildNoTransform(i);
      Opt opt = dims.getExprOpt();
      if(opt.getNumChildNoTransform() == 1)
        a = new ArrayTypeWithSizeAccess(a, (Expr)opt.getChildNoTransform(0));
      else
        a = new ArrayTypeAccess(a);
      a.setStart(dims.start());
      a.setEnd(dims.end());
    }
    return a;
  }
  /**
   * @ast method 
   * @aspect InnerClasses
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/InnerClasses.jrag:106
   */
  protected TypeDecl superConstructorQualifier(TypeDecl targetEnclosingType) {
    TypeDecl enclosing = hostType();
    while(!enclosing.instanceOf(targetEnclosingType))
      enclosing = enclosing.enclosingType();
    return enclosing;
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:293
   */
  public soot.Value emitLoadLocalInNestedClass(Body b, Variable v) {
    if(inExplicitConstructorInvocation() && enclosingBodyDecl() instanceof ConstructorDecl) {
      ConstructorDecl c = (ConstructorDecl)enclosingBodyDecl();
      return ((ParameterDeclaration)c.parameterDeclaration(v.name()).iterator().next()).local;
    }
    else {
      return b.newInstanceFieldRef(
        b.emitThis(hostType()),
        Scene.v().makeFieldRef(hostType().getSootClassDecl(), "val$" + v.name(), v.type().getSootType(), false),
        this
        //hostType().getSootClassDecl().getField("val$" + v.name(), v.type().getSootType()).makeRef()
      );
    }
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:430
   */
  public soot.Local emitThis(Body b, TypeDecl targetDecl) {
    b.setLine(this);
    if(targetDecl == hostType())
      return b.emitThis(hostType());
    else {
      TypeDecl enclosing = hostType();
      Local base;
      if(inExplicitConstructorInvocation()) {
        base = asLocal(b,
          b.newParameterRef(enclosing.enclosingType().getSootType(), 0, this)
        );
        enclosing = enclosing.enclosing();
      }
      else {
        base = b.emitThis(hostType());
      }
      while(enclosing != targetDecl) {
        Local next = b.newTemp(enclosing.enclosingType().getSootType());
        b.add(
          b.newAssignStmt(
            next,
            b.newInstanceFieldRef(
              base,
              enclosing.getSootField("this$0", enclosing.enclosingType()).makeRef(),
              this
            ),
            this
          )
        );
        base = next;
        enclosing = enclosing.enclosingType();
      }
      return base;
    }
  }
  /**
   * @ast method 
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:658
   */
  public void addArraySize(Body b, ArrayList list) {
  }
  /**
   * @ast method 
   * 
   */
  public Access() {
    super();


  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @ast method 
   * 
   */
  public void init$Children() {
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 0;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * @attribute syn
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:17
   */
  public Expr unqualifiedScope() {
    ASTNode$State state = state();
    try {  return isQualified() ? nestedScope() : this;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:58
   */
  public boolean isQualified() {
    ASTNode$State state = state();
    try {  return hasPrevExpr();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:61
   */
  public Expr qualifier() {
    ASTNode$State state = state();
    try {  return prevExpr();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:66
   */
  public Access lastAccess() {
    ASTNode$State state = state();
    try {  return this;  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean prevExpr_computed = false;
  /**
   * @apilevel internal
   */
  protected Expr prevExpr_value;
  /**
   * @attribute syn
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:78
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr prevExpr() {
    if(prevExpr_computed) {
      return prevExpr_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    prevExpr_value = prevExpr_compute();
      if(isFinal && num == state().boundariesCrossed) prevExpr_computed = true;
    return prevExpr_value;
  }
  /**
   * @apilevel internal
   */
  private Expr prevExpr_compute() {
    if(isLeftChildOfDot()) {
      if(parentDot().isRightChildOfDot())
        return parentDot().parentDot().leftSide();
    }
    else if(isRightChildOfDot())
      return parentDot().leftSide();
    throw new Error(this + " does not have a previous expression");
  }
  /**
   * @apilevel internal
   */
  protected boolean hasPrevExpr_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean hasPrevExpr_value;
  /**
   * @attribute syn
   * @aspect QualifiedNames
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ResolveAmbiguousNames.jrag:89
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean hasPrevExpr() {
    if(hasPrevExpr_computed) {
      return hasPrevExpr_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    hasPrevExpr_value = hasPrevExpr_compute();
      if(isFinal && num == state().boundariesCrossed) hasPrevExpr_computed = true;
    return hasPrevExpr_value;
  }
  /**
   * @apilevel internal
   */
  private boolean hasPrevExpr_compute() {
    if(isLeftChildOfDot()) {
      if(parentDot().isRightChildOfDot())
        return true;
    }
    else if(isRightChildOfDot())
      return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect SyntacticClassification
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:56
   */
  public NameType predNameType() {
    ASTNode$State state = state();
    try {  return NameType.NO_NAME;  }
    finally {
    }
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:278
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
  private TypeDecl type_compute() {  return unknownType();  }
  /**
   * @attribute syn
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:65
   */
  public boolean isDiamond() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
	 * Builds a copy of this Access node where all occurrences
	 * of type variables in the original type parameter list have been replaced
	 * by the substitution type parameters.
	 *
	 * @return the substituted Access node
	 * @attribute syn
   * @aspect TypeInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/TypeInference.jrag:406
   */
  public Access substituted(Collection<TypeVariable> original, List<TypeVariable> substitution) {
    ASTNode$State state = state();
    try {  return (Access) cloneSubtree();  }
    finally {
    }
  }
  /**
   * @attribute inh
   * @aspect LookupMethod
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupMethod.jrag:18
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr nestedScope() {
    ASTNode$State state = state();
    Expr nestedScope_value = getParent().Define_Expr_nestedScope(this, null);
    return nestedScope_value;
  }
  /**
   * @attribute inh
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:221
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl unknownType() {
    ASTNode$State state = state();
    TypeDecl unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
    return unknownType_value;
  }
  /**
   * @attribute inh
   * @aspect VariableScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupVariable.jrag:239
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Variable unknownField() {
    ASTNode$State state = state();
    Variable unknownField_value = getParent().Define_Variable_unknownField(this, null);
    return unknownField_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:268
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean withinSuppressWarnings(String s) {
    ASTNode$State state = state();
    boolean withinSuppressWarnings_String_value = getParent().Define_boolean_withinSuppressWarnings(this, null, s);
    return withinSuppressWarnings_String_value;
  }
  /**
   * @attribute inh
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:372
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean withinDeprecatedAnnotation() {
    ASTNode$State state = state();
    boolean withinDeprecatedAnnotation_value = getParent().Define_boolean_withinDeprecatedAnnotation(this, null);
    return withinDeprecatedAnnotation_value;
  }
  /**
   * @attribute inh
   * @aspect Expressions
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/Expressions.jrag:292
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean inExplicitConstructorInvocation() {
    ASTNode$State state = state();
    boolean inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
    return inExplicitConstructorInvocation_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
