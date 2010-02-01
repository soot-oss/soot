
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class AbstractDot extends Access implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        type_computed = false;
        type_value = null;
        isDUbefore_Variable_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AbstractDot clone() throws CloneNotSupportedException {
        AbstractDot node = (AbstractDot)super.clone();
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.type_computed = false;
        node.type_value = null;
        node.isDUbefore_Variable_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AbstractDot copy() {
      try {
          AbstractDot node = (AbstractDot)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AbstractDot fullCopy() {
        AbstractDot res = (AbstractDot)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 445


  public void toString(StringBuffer s) {
    getLeft().toString(s);
    if(!nextAccess().isArrayAccess())
      s.append(".");
    getRight().toString(s);
  }

    // Declared in ResolveAmbiguousNames.jrag at line 130



  // These are used by the parser to extract the last name which
  // will be replaced by a method name
  public Access extractLast() {
    return getRightNoTransform();
 }

    // Declared in ResolveAmbiguousNames.jrag at line 133

  public void replaceLast(Access access) {
    setRight(access);
  }

    // Declared in BooleanExpressions.jrag at line 181

  public void emitEvalBranch(Body b) { lastAccess().emitEvalBranch(b); }

    // Declared in Expressions.jrag at line 190


  public soot.Value eval(Body b) {
    return lastAccess().eval(b);
  }

    // Declared in Expressions.jrag at line 256


  public soot.Value emitStore(Body b, soot.Value lvalue, soot.Value rvalue, ASTNode location) {
    return lastAccess().emitStore(b, lvalue, rvalue, location);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 13

    public AbstractDot() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 13
    public AbstractDot(Expr p0, Access p1) {
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
    // Declared in java.ast line 13
    public void setLeft(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getLeft() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getLeftNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 13
    public void setRight(Access node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Access getRight() {
        return (Access)getChild(1);
    }

    // Declared in java.ast at line 9


    public Access getRightNoTransform() {
        return (Access)getChildNoTransform(1);
    }

    // Declared in ConstantExpression.jrag at line 109
 @SuppressWarnings({"unchecked", "cast"})     public Constant constant() {
        ASTNode$State state = state();
        Constant constant_value = constant_compute();
        return constant_value;
    }

    private Constant constant_compute() {  return lastAccess().constant();  }

    // Declared in ConstantExpression.jrag at line 495
 @SuppressWarnings({"unchecked", "cast"})     public boolean isConstant() {
        ASTNode$State state = state();
        boolean isConstant_value = isConstant_compute();
        return isConstant_value;
    }

    private boolean isConstant_compute() {  return lastAccess().isConstant();  }

    // Declared in DefiniteAssignment.jrag at line 59
 @SuppressWarnings({"unchecked", "cast"})     public Variable varDecl() {
        ASTNode$State state = state();
        Variable varDecl_value = varDecl_compute();
        return varDecl_value;
    }

    private Variable varDecl_compute() {  return lastAccess().varDecl();  }

    // Declared in DefiniteAssignment.jrag at line 337
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterTrue(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterTrue_Variable_value = isDAafterTrue_compute(v);
        return isDAafterTrue_Variable_value;
    }

    private boolean isDAafterTrue_compute(Variable v) {  return isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 338
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterFalse(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterFalse_Variable_value = isDAafterFalse_compute(v);
        return isDAafterFalse_Variable_value;
    }

    private boolean isDAafterFalse_compute(Variable v) {  return isDAafter(v);  }

    protected java.util.Map isDAafter_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 357
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

    private boolean isDAafter_compute(Variable v) {  return lastAccess().isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 794
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterTrue(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterTrue_Variable_value = isDUafterTrue_compute(v);
        return isDUafterTrue_Variable_value;
    }

    private boolean isDUafterTrue_compute(Variable v) {  return isDUafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 795
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterFalse(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterFalse_Variable_value = isDUafterFalse_compute(v);
        return isDUafterFalse_Variable_value;
    }

    private boolean isDUafterFalse_compute(Variable v) {  return isDUafter(v);  }

    protected java.util.Map isDUafter_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 839
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

    private boolean isDUafter_compute(Variable v) {  return lastAccess().isDUafter(v);  }

    // Declared in QualifiedNames.jrag at line 63
 @SuppressWarnings({"unchecked", "cast"})     public String typeName() {
        ASTNode$State state = state();
        String typeName_value = typeName_compute();
        return typeName_value;
    }

    private String typeName_compute() {  return lastAccess().typeName();  }

    // Declared in ResolveAmbiguousNames.jrag at line 15
 @SuppressWarnings({"unchecked", "cast"})     public boolean isTypeAccess() {
        ASTNode$State state = state();
        boolean isTypeAccess_value = isTypeAccess_compute();
        return isTypeAccess_value;
    }

    private boolean isTypeAccess_compute() {  return getRight().isTypeAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 18
 @SuppressWarnings({"unchecked", "cast"})     public boolean isMethodAccess() {
        ASTNode$State state = state();
        boolean isMethodAccess_value = isMethodAccess_compute();
        return isMethodAccess_value;
    }

    private boolean isMethodAccess_compute() {  return getRight().isMethodAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 22
 @SuppressWarnings({"unchecked", "cast"})     public boolean isFieldAccess() {
        ASTNode$State state = state();
        boolean isFieldAccess_value = isFieldAccess_compute();
        return isFieldAccess_value;
    }

    private boolean isFieldAccess_compute() {  return getRight().isFieldAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 26
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSuperAccess() {
        ASTNode$State state = state();
        boolean isSuperAccess_value = isSuperAccess_compute();
        return isSuperAccess_value;
    }

    private boolean isSuperAccess_compute() {  return getRight().isSuperAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 32
 @SuppressWarnings({"unchecked", "cast"})     public boolean isThisAccess() {
        ASTNode$State state = state();
        boolean isThisAccess_value = isThisAccess_compute();
        return isThisAccess_value;
    }

    private boolean isThisAccess_compute() {  return getRight().isThisAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 38
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPackageAccess() {
        ASTNode$State state = state();
        boolean isPackageAccess_value = isPackageAccess_compute();
        return isPackageAccess_value;
    }

    private boolean isPackageAccess_compute() {  return getRight().isPackageAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 42
 @SuppressWarnings({"unchecked", "cast"})     public boolean isArrayAccess() {
        ASTNode$State state = state();
        boolean isArrayAccess_value = isArrayAccess_compute();
        return isArrayAccess_value;
    }

    private boolean isArrayAccess_compute() {  return getRight().isArrayAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 46
 @SuppressWarnings({"unchecked", "cast"})     public boolean isClassAccess() {
        ASTNode$State state = state();
        boolean isClassAccess_value = isClassAccess_compute();
        return isClassAccess_value;
    }

    private boolean isClassAccess_compute() {  return getRight().isClassAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 50
 @SuppressWarnings({"unchecked", "cast"})     public boolean isSuperConstructorAccess() {
        ASTNode$State state = state();
        boolean isSuperConstructorAccess_value = isSuperConstructorAccess_compute();
        return isSuperConstructorAccess_value;
    }

    private boolean isSuperConstructorAccess_compute() {  return getRight().isSuperConstructorAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 59
 @SuppressWarnings({"unchecked", "cast"})     public boolean isQualified() {
        ASTNode$State state = state();
        boolean isQualified_value = isQualified_compute();
        return isQualified_value;
    }

    private boolean isQualified_compute() {  return hasParentDot();  }

    // Declared in ResolveAmbiguousNames.jrag at line 63
 @SuppressWarnings({"unchecked", "cast"})     public Expr leftSide() {
        ASTNode$State state = state();
        Expr leftSide_value = leftSide_compute();
        return leftSide_value;
    }

    private Expr leftSide_compute() {  return getLeft();  }

    // Declared in ResolveAmbiguousNames.jrag at line 64
 @SuppressWarnings({"unchecked", "cast"})     public Access rightSide() {
        ASTNode$State state = state();
        Access rightSide_value = rightSide_compute();
        return rightSide_value;
    }

    private Access rightSide_compute() {  return getRight/*NoTransform*/() instanceof AbstractDot ? (Access)((AbstractDot)getRight/*NoTransform*/()).getLeft() : (Access)getRight();  }

    // Declared in ResolveAmbiguousNames.jrag at line 67
 @SuppressWarnings({"unchecked", "cast"})     public Access lastAccess() {
        ASTNode$State state = state();
        Access lastAccess_value = lastAccess_compute();
        return lastAccess_value;
    }

    private Access lastAccess_compute() {  return getRight().lastAccess();  }

    // Declared in ResolveAmbiguousNames.jrag at line 75
 @SuppressWarnings({"unchecked", "cast"})     public Access nextAccess() {
        ASTNode$State state = state();
        Access nextAccess_value = nextAccess_compute();
        return nextAccess_value;
    }

    private Access nextAccess_compute() {  return rightSide();  }

    // Declared in ResolveAmbiguousNames.jrag at line 77
 @SuppressWarnings({"unchecked", "cast"})     public Expr prevExpr() {
        ASTNode$State state = state();
        Expr prevExpr_value = prevExpr_compute();
        return prevExpr_value;
    }

    private Expr prevExpr_compute() {  return leftSide();  }

    // Declared in ResolveAmbiguousNames.jrag at line 88
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasPrevExpr() {
        ASTNode$State state = state();
        boolean hasPrevExpr_value = hasPrevExpr_compute();
        return hasPrevExpr_value;
    }

    private boolean hasPrevExpr_compute() {  return true;  }

    // Declared in SyntacticClassification.jrag at line 60
 @SuppressWarnings({"unchecked", "cast"})     public NameType predNameType() {
        ASTNode$State state = state();
        NameType predNameType_value = predNameType_compute();
        return predNameType_value;
    }

    private NameType predNameType_compute() {  return getLeft() instanceof Access ? ((Access)getLeft()).predNameType() : NameType.NO_NAME;  }

    // Declared in TypeAnalysis.jrag at line 249
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

    private TypeDecl type_compute() {  return lastAccess().type();  }

    // Declared in TypeCheck.jrag at line 16
 @SuppressWarnings({"unchecked", "cast"})     public boolean isVariable() {
        ASTNode$State state = state();
        boolean isVariable_value = isVariable_compute();
        return isVariable_value;
    }

    private boolean isVariable_compute() {  return lastAccess().isVariable();  }

    // Declared in TypeHierarchyCheck.jrag at line 153
 @SuppressWarnings({"unchecked", "cast"})     public boolean staticContextQualifier() {
        ASTNode$State state = state();
        boolean staticContextQualifier_value = staticContextQualifier_compute();
        return staticContextQualifier_value;
    }

    private boolean staticContextQualifier_compute() {  return lastAccess().staticContextQualifier();  }

    // Declared in BooleanExpressions.jrag at line 24
 @SuppressWarnings({"unchecked", "cast"})     public boolean definesLabel() {
        ASTNode$State state = state();
        boolean definesLabel_value = definesLabel_compute();
        return definesLabel_value;
    }

    private boolean definesLabel_compute() {  return getParent().definesLabel();  }

    // Declared in BooleanExpressions.jrag at line 84
 @SuppressWarnings({"unchecked", "cast"})     public boolean canBeTrue() {
        ASTNode$State state = state();
        boolean canBeTrue_value = canBeTrue_compute();
        return canBeTrue_value;
    }

    private boolean canBeTrue_compute() {  return lastAccess().canBeTrue();  }

    // Declared in BooleanExpressions.jrag at line 94
 @SuppressWarnings({"unchecked", "cast"})     public boolean canBeFalse() {
        ASTNode$State state = state();
        boolean canBeFalse_value = canBeFalse_compute();
        return canBeFalse_value;
    }

    private boolean canBeFalse_compute() {  return lastAccess().canBeFalse();  }

    protected java.util.Map isDUbefore_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 698
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

    // Declared in DefiniteAssignment.jrag at line 21
    public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
        if(caller == getLeftNoTransform()) {
            return false;
        }
        return getParent().Define_boolean_isDest(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 31
    public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
        if(caller == getLeftNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isSource(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 356
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getRightNoTransform()) {
            return getLeft().isDAafter(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 838
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getRightNoTransform()) {
            return getLeft().isDUafter(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in LookupConstructor.jrag at line 17
    public Collection Define_Collection_lookupConstructor(ASTNode caller, ASTNode child) {
        if(caller == getRightNoTransform()) {
            return getLeft().type().constructors();
        }
        return getParent().Define_Collection_lookupConstructor(this, caller);
    }

    // Declared in LookupConstructor.jrag at line 25
    public Collection Define_Collection_lookupSuperConstructor(ASTNode caller, ASTNode child) {
        if(caller == getRightNoTransform()) {
            return getLeft().type().lookupSuperConstructor();
        }
        return getParent().Define_Collection_lookupSuperConstructor(this, caller);
    }

    // Declared in LookupMethod.jrag at line 20
    public Expr Define_Expr_nestedScope(ASTNode caller, ASTNode child) {
        if(caller == getLeftNoTransform()) {
            return isQualified() ? nestedScope() : this;
        }
        if(caller == getRightNoTransform()) {
            return isQualified() ? nestedScope() : this;
        }
        return getParent().Define_Expr_nestedScope(this, caller);
    }

    // Declared in LookupMethod.jrag at line 64
    public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
        if(caller == getRightNoTransform()) {
            return getLeft().type().memberMethods(name);
        }
        return getParent().Define_Collection_lookupMethod(this, caller, name);
    }

    // Declared in LookupType.jrag at line 82
    public boolean Define_boolean_hasPackage(ASTNode caller, ASTNode child, String packageName) {
        if(caller == getRightNoTransform()) {
            return getLeft().hasQualifiedPackage(packageName);
        }
        return getParent().Define_boolean_hasPackage(this, caller, packageName);
    }

    // Declared in LookupType.jrag at line 341
    public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
        if(caller == getRightNoTransform()) {
            return getLeft().qualifiedLookupType(name);
        }
        return getParent().Define_SimpleSet_lookupType(this, caller, name);
    }

    // Declared in LookupVariable.jrag at line 137
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getRightNoTransform()) {
            return getLeft().qualifiedLookupVariable(name);
        }
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }

    // Declared in SyntacticClassification.jrag at line 59
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getLeftNoTransform()) {
            return getRight().predNameType();
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

    // Declared in TypeCheck.jrag at line 516
    public TypeDecl Define_TypeDecl_enclosingInstance(ASTNode caller, ASTNode child) {
        if(caller == getRightNoTransform()) {
            return getLeft().type();
        }
        return getParent().Define_TypeDecl_enclosingInstance(this, caller);
    }

    // Declared in TypeHierarchyCheck.jrag at line 13
    public String Define_String_methodHost(ASTNode caller, ASTNode child) {
        if(caller == getRightNoTransform()) {
            return getLeft().type().typeName();
        }
        return getParent().Define_String_methodHost(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
