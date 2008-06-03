
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;


// Statements


public abstract class Stmt extends ASTNode<ASTNode> implements Cloneable {
    public void flushCache() {
        super.flushCache();
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        canCompleteNormally_computed = false;
        localNum_computed = false;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Stmt clone() throws CloneNotSupportedException {
        Stmt node = (Stmt)super.clone();
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.canCompleteNormally_computed = false;
        node.localNum_computed = false;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
    // Declared in UnreachableStatements.jrag at line 14

  void checkUnreachableStmt() {
    if(!reachable() && reportUnreachable())
      error("statement is unreachable");
  }

    // Declared in Statements.jrag at line 12


  public void jimplify2(Body b) {
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 198

    public Stmt() {
        super();


    }

    // Declared in java.ast at line 9


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 12

  public boolean mayHaveRewrite() { return false; }

    protected java.util.Map isDAafter_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 327
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafter(Variable v) {
        Object _parameters = v;
if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
        if(isDAafter_Variable_values.containsKey(_parameters))
            return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDAafter_Variable_value = isDAafter_compute(v);
        if(isFinal && num == boundariesCrossed)
            isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
        return isDAafter_Variable_value;
    }

    private boolean isDAafter_compute(Variable v) {  return isDAbefore(v);  }

    protected java.util.Map isDUafter_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 778
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafter(Variable v) {
        Object _parameters = v;
if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
        if(isDUafter_Variable_values.containsKey(_parameters))
            return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDUafter_Variable_value = isDUafter_compute(v);
        if(isFinal && num == boundariesCrossed)
            isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
        return isDUafter_Variable_value;
    }

    private boolean isDUafter_compute(Variable v) {
    throw new Error("isDUafter in " + getClass().getName());
  }

    // Declared in LookupVariable.jrag at line 127
 @SuppressWarnings({"unchecked", "cast"})     public boolean declaresVariable(String name) {
        boolean declaresVariable_String_value = declaresVariable_compute(name);
        return declaresVariable_String_value;
    }

    private boolean declaresVariable_compute(String name) {  return false;  }

    // Declared in NameCheck.jrag at line 396
 @SuppressWarnings({"unchecked", "cast"})     public boolean continueLabel() {
        boolean continueLabel_value = continueLabel_compute();
        return continueLabel_value;
    }

    private boolean continueLabel_compute() {  return false;  }

    protected boolean canCompleteNormally_computed = false;
    protected boolean canCompleteNormally_value;
    // Declared in UnreachableStatements.jrag at line 29
 @SuppressWarnings({"unchecked", "cast"})     public boolean canCompleteNormally() {
        if(canCompleteNormally_computed)
            return canCompleteNormally_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        canCompleteNormally_value = canCompleteNormally_compute();
        if(isFinal && num == boundariesCrossed)
            canCompleteNormally_computed = true;
        return canCompleteNormally_value;
    }

    private boolean canCompleteNormally_compute() {  return true;  }

    // Declared in Statements.jrag at line 199
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt break_label() {
        soot.jimple.Stmt break_label_value = break_label_compute();
        return break_label_value;
    }

    private soot.jimple.Stmt break_label_compute() {
    throw new UnsupportedOperationException("Can not break at this statement of type " + getClass().getName());
  }

    // Declared in Statements.jrag at line 224
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt continue_label() {
        soot.jimple.Stmt continue_label_value = continue_label_compute();
        return continue_label_value;
    }

    private soot.jimple.Stmt continue_label_compute() {
    throw new UnsupportedOperationException("Can not continue at this statement");
  }

    // Declared in DefiniteAssignment.jrag at line 234
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAbefore(Variable v) {
        boolean isDAbefore_Variable_value = getParent().Define_boolean_isDAbefore(this, null, v);
        return isDAbefore_Variable_value;
    }

    // Declared in DefiniteAssignment.jrag at line 692
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUbefore(Variable v) {
        boolean isDUbefore_Variable_value = getParent().Define_boolean_isDUbefore(this, null, v);
        return isDUbefore_Variable_value;
    }

    // Declared in LookupMethod.jrag at line 24
 @SuppressWarnings({"unchecked", "cast"})     public Collection lookupMethod(String name) {
        Collection lookupMethod_String_value = getParent().Define_Collection_lookupMethod(this, null, name);
        return lookupMethod_String_value;
    }

    // Declared in LookupType.jrag at line 96
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupType(String packageName, String typeName) {
        TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName, typeName);
        return lookupType_String_String_value;
    }

    // Declared in LookupType.jrag at line 174
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet lookupType(String name) {
        SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
        return lookupType_String_value;
    }

    // Declared in LookupVariable.jrag at line 16
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet lookupVariable(String name) {
        SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
        return lookupVariable_String_value;
    }

    // Declared in TypeAnalysis.jrag at line 512
 @SuppressWarnings({"unchecked", "cast"})     public BodyDecl enclosingBodyDecl() {
        BodyDecl enclosingBodyDecl_value = getParent().Define_BodyDecl_enclosingBodyDecl(this, null);
        return enclosingBodyDecl_value;
    }

    // Declared in TypeAnalysis.jrag at line 584
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl hostType() {
        TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
        return hostType_value;
    }

    // Declared in UnreachableStatements.jrag at line 27
 @SuppressWarnings({"unchecked", "cast"})     public boolean reachable() {
        boolean reachable_value = getParent().Define_boolean_reachable(this, null);
        return reachable_value;
    }

    // Declared in UnreachableStatements.jrag at line 145
 @SuppressWarnings({"unchecked", "cast"})     public boolean reportUnreachable() {
        boolean reportUnreachable_value = getParent().Define_boolean_reportUnreachable(this, null);
        return reportUnreachable_value;
    }

    protected boolean localNum_computed = false;
    protected int localNum_value;
    // Declared in LocalNum.jrag at line 12
 @SuppressWarnings({"unchecked", "cast"})     public int localNum() {
        if(localNum_computed)
            return localNum_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        localNum_value = getParent().Define_int_localNum(this, null);
        if(isFinal && num == boundariesCrossed)
            localNum_computed = true;
        return localNum_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
