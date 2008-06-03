
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;



public abstract class Access extends Expr implements Cloneable {
    public void flushCache() {
        super.flushCache();
        prevExpr_computed = false;
        prevExpr_value = null;
        hasPrevExpr_computed = false;
        type_computed = false;
        type_value = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Access clone() throws CloneNotSupportedException {
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
    // Declared in ResolveAmbiguousNames.jrag at line 144


  public Access addArrayDims(List list) {
    Access a = this;
    for(int i = 0; i < list.getNumChildNoTransform(); i++) {
      Dims dims = (Dims)list.getChildNoTransform(i);
      Opt opt = dims.getExprOpt();
      if(opt.getNumChildNoTransform() == 1)
        a = new ArrayTypeWithSizeAccess(a, (Expr)opt.getChildNoTransform(0));
      else
        a = new ArrayTypeAccess(a);
    }
    return a;
  }

    // Declared in InnerClasses.jrag at line 103


  protected TypeDecl superConstructorQualifier(TypeDecl targetEnclosingType) {
    TypeDecl enclosing = hostType();
    while(!enclosing.instanceOf(targetEnclosingType))
      enclosing = enclosing.enclosingType();
    return enclosing;
  }

    // Declared in Expressions.jrag at line 287

  public soot.Value emitLoadLocalInNestedClass(Body b, Variable v) {
    if(inExplicitConstructorInvocation() && enclosingBodyDecl() instanceof ConstructorDecl) {
      ConstructorDecl c = (ConstructorDecl)enclosingBodyDecl();
      return ((ParameterDeclaration)c.parameterDeclaration(v.name()).iterator().next()).local;
    }
    else {
      return Jimple.v().newInstanceFieldRef(
        b.emitThis(hostType()),
        Scene.v().makeFieldRef(hostType().getSootClassDecl(), "val$" + v.name(), v.type().getSootType(), false)
        //hostType().getSootClassDecl().getField("val$" + v.name(), v.type().getSootType()).makeRef()
      );
    }
  }

    // Declared in Expressions.jrag at line 422


  // load this where hostType is the target this instance 
  // supporting inner classes and in explicit contructor invocations
  public soot.Local emitThis(Body b, TypeDecl targetDecl) {
    b.setLine(this);
    if(targetDecl == hostType())
      return b.emitThis(hostType());
    else {
      TypeDecl enclosing = hostType();
      Local base;
      if(inExplicitConstructorInvocation()) {
        base = asLocal(b,
          Jimple.v().newParameterRef(enclosing.enclosingType().getSootType(), 0)
        );
        enclosing = enclosing.enclosing();
      }
      else {
        base = b.emitThis(hostType());
      }
      while(enclosing != targetDecl) {
        Local next = b.newTemp(enclosing.enclosingType().getSootType());
        b.add(
          Jimple.v().newAssignStmt(
            next,
            Jimple.v().newInstanceFieldRef(
              base,
              enclosing.getSootField("this$0", enclosing.enclosingType()).makeRef()
            )
          )
        );
        base = next;
        enclosing = enclosing.enclosingType();
      }
      return base;
    }
  }

    // Declared in Expressions.jrag at line 640


  public void addArraySize(Body b, ArrayList list) {
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 11

    public Access() {
        super();


    }

    // Declared in java.ast at line 9


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 12

  public boolean mayHaveRewrite() { return false; }

    // Declared in LookupMethod.jrag at line 17
 @SuppressWarnings({"unchecked", "cast"})     public Expr unqualifiedScope() {
        Expr unqualifiedScope_value = unqualifiedScope_compute();
        return unqualifiedScope_value;
    }

    private Expr unqualifiedScope_compute() {  return isQualified() ? nestedScope() : this;  }

    // Declared in ResolveAmbiguousNames.jrag at line 58
 @SuppressWarnings({"unchecked", "cast"})     public boolean isQualified() {
        boolean isQualified_value = isQualified_compute();
        return isQualified_value;
    }

    private boolean isQualified_compute() {  return hasPrevExpr();  }

    // Declared in ResolveAmbiguousNames.jrag at line 61
 @SuppressWarnings({"unchecked", "cast"})     public Expr qualifier() {
        Expr qualifier_value = qualifier_compute();
        return qualifier_value;
    }

    private Expr qualifier_compute() {  return prevExpr();  }

    // Declared in ResolveAmbiguousNames.jrag at line 66
 @SuppressWarnings({"unchecked", "cast"})     public Access lastAccess() {
        Access lastAccess_value = lastAccess_compute();
        return lastAccess_value;
    }

    private Access lastAccess_compute() {  return this;  }

    protected boolean prevExpr_computed = false;
    protected Expr prevExpr_value;
    // Declared in ResolveAmbiguousNames.jrag at line 78
 @SuppressWarnings({"unchecked", "cast"})     public Expr prevExpr() {
        if(prevExpr_computed)
            return prevExpr_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        prevExpr_value = prevExpr_compute();
        if(isFinal && num == boundariesCrossed)
            prevExpr_computed = true;
        return prevExpr_value;
    }

    private Expr prevExpr_compute() {
    if(isLeftChildOfDot()) {
      if(parentDot().isRightChildOfDot())
        return parentDot().parentDot().leftSide();
    }
    else if(isRightChildOfDot())
      return parentDot().leftSide();
    throw new Error(this + " does not have a previous expression");
  }

    protected boolean hasPrevExpr_computed = false;
    protected boolean hasPrevExpr_value;
    // Declared in ResolveAmbiguousNames.jrag at line 89
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasPrevExpr() {
        if(hasPrevExpr_computed)
            return hasPrevExpr_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        hasPrevExpr_value = hasPrevExpr_compute();
        if(isFinal && num == boundariesCrossed)
            hasPrevExpr_computed = true;
        return hasPrevExpr_value;
    }

    private boolean hasPrevExpr_compute() {
    if(isLeftChildOfDot()) {
      if(parentDot().isRightChildOfDot())
        return true;
    }
    else if(isRightChildOfDot())
      return true;
    return false;
  }

    // Declared in SyntacticClassification.jrag at line 56
 @SuppressWarnings({"unchecked", "cast"})     public NameType predNameType() {
        NameType predNameType_value = predNameType_compute();
        return predNameType_value;
    }

    private NameType predNameType_compute() {  return NameType.NO_NAME;  }

    protected boolean type_computed = false;
    protected TypeDecl type_value;
    // Declared in TypeAnalysis.jrag at line 278
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

    private TypeDecl type_compute() {  return unknownType();  }

    // Declared in LookupMethod.jrag at line 18
 @SuppressWarnings({"unchecked", "cast"})     public Expr nestedScope() {
        Expr nestedScope_value = getParent().Define_Expr_nestedScope(this, null);
        return nestedScope_value;
    }

    // Declared in LookupType.jrag at line 133
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl unknownType() {
        TypeDecl unknownType_value = getParent().Define_TypeDecl_unknownType(this, null);
        return unknownType_value;
    }

    // Declared in LookupVariable.jrag at line 228
 @SuppressWarnings({"unchecked", "cast"})     public Variable unknownField() {
        Variable unknownField_value = getParent().Define_Variable_unknownField(this, null);
        return unknownField_value;
    }

    // Declared in Annotations.jrag at line 268
 @SuppressWarnings({"unchecked", "cast"})     public boolean withinSuppressWarnings(String s) {
        boolean withinSuppressWarnings_String_value = getParent().Define_boolean_withinSuppressWarnings(this, null, s);
        return withinSuppressWarnings_String_value;
    }

    // Declared in Annotations.jrag at line 372
 @SuppressWarnings({"unchecked", "cast"})     public boolean withinDeprecatedAnnotation() {
        boolean withinDeprecatedAnnotation_value = getParent().Define_boolean_withinDeprecatedAnnotation(this, null);
        return withinDeprecatedAnnotation_value;
    }

    // Declared in Expressions.jrag at line 286
 @SuppressWarnings({"unchecked", "cast"})     public boolean inExplicitConstructorInvocation() {
        boolean inExplicitConstructorInvocation_value = getParent().Define_boolean_inExplicitConstructorInvocation(this, null);
        return inExplicitConstructorInvocation_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
