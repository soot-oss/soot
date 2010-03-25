
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class CatchClause extends ASTNode<ASTNode> implements Cloneable, VariableScope {
    public void flushCache() {
        super.flushCache();
        parameterDeclaration_String_values = null;
        label_computed = false;
        label_value = null;
        typeThrowable_computed = false;
        typeThrowable_value = null;
        lookupVariable_String_values = null;
        reachableCatchClause_computed = false;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public CatchClause clone() throws CloneNotSupportedException {
        CatchClause node = (CatchClause)super.clone();
        node.parameterDeclaration_String_values = null;
        node.label_computed = false;
        node.label_value = null;
        node.typeThrowable_computed = false;
        node.typeThrowable_value = null;
        node.lookupVariable_String_values = null;
        node.reachableCatchClause_computed = false;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public CatchClause copy() {
      try {
          CatchClause node = (CatchClause)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public CatchClause fullCopy() {
        CatchClause res = (CatchClause)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 721


  public void toString(StringBuffer s) {
    s.append("catch (");
    getParameter().toString(s);
    s.append(") ");
    getBlock().toString(s);
  }

    // Declared in TypeCheck.jrag at line 368


  public void typeCheck() {
    if(!getParameter().type().instanceOf(typeThrowable()))
      error("*** The catch variable must extend Throwable");
  }

    // Declared in Statements.jrag at line 454

  public void jimplify2(Body b) {
    b.addLabel(label());
    Local local = b.newLocal(getParameter().name(), getParameter().type().getSootType());
    b.setLine(this);
    b.add(b.newIdentityStmt(local, b.newCaughtExceptionRef(getParameter()), this));
    getParameter().local = local;
    getBlock().jimplify2(b);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 223

    public CatchClause() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 223
    public CatchClause(ParameterDeclaration p0, Block p1) {
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
    // Declared in java.ast line 223
    public void setParameter(ParameterDeclaration node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public ParameterDeclaration getParameter() {
        return (ParameterDeclaration)getChild(0);
    }

    // Declared in java.ast at line 9


    public ParameterDeclaration getParameterNoTransform() {
        return (ParameterDeclaration)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 223
    public void setBlock(Block node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Block getBlock() {
        return (Block)getChild(1);
    }

    // Declared in java.ast at line 9


    public Block getBlockNoTransform() {
        return (Block)getChildNoTransform(1);
    }

    // Declared in ExceptionHandling.jrag at line 189
 @SuppressWarnings({"unchecked", "cast"})     public boolean handles(TypeDecl exceptionType) {
        ASTNode$State state = state();
        boolean handles_TypeDecl_value = handles_compute(exceptionType);
        return handles_TypeDecl_value;
    }

    private boolean handles_compute(TypeDecl exceptionType) {  return !getParameter().type().isUnknown()
    && exceptionType.instanceOf(getParameter().type());  }

    protected java.util.Map parameterDeclaration_String_values;
    // Declared in LookupVariable.jrag at line 111
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet parameterDeclaration(String name) {
        Object _parameters = name;
if(parameterDeclaration_String_values == null) parameterDeclaration_String_values = new java.util.HashMap(4);
        if(parameterDeclaration_String_values.containsKey(_parameters)) {
            return (SimpleSet)parameterDeclaration_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet parameterDeclaration_String_value = parameterDeclaration_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            parameterDeclaration_String_values.put(_parameters, parameterDeclaration_String_value);
        return parameterDeclaration_String_value;
    }

    private SimpleSet parameterDeclaration_compute(String name) {  return getParameter().name().equals(name) ? (ParameterDeclaration)getParameter() : SimpleSet.emptySet;  }

    protected boolean label_computed = false;
    protected soot.jimple.Stmt label_value;
    // Declared in Statements.jrag at line 453
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label() {
        if(label_computed) {
            return label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_value = label_compute();
        if(isFinal && num == state().boundariesCrossed)
            label_computed = true;
        return label_value;
    }

    private soot.jimple.Stmt label_compute() {  return newLabel();  }

    protected boolean typeThrowable_computed = false;
    protected TypeDecl typeThrowable_value;
    // Declared in LookupType.jrag at line 68
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeThrowable() {
        if(typeThrowable_computed) {
            return typeThrowable_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        typeThrowable_value = getParent().Define_TypeDecl_typeThrowable(this, null);
        if(isFinal && num == state().boundariesCrossed)
            typeThrowable_computed = true;
        return typeThrowable_value;
    }

    protected java.util.Map lookupVariable_String_values;
    // Declared in LookupVariable.jrag at line 20
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet lookupVariable(String name) {
        Object _parameters = name;
if(lookupVariable_String_values == null) lookupVariable_String_values = new java.util.HashMap(4);
        if(lookupVariable_String_values.containsKey(_parameters)) {
            return (SimpleSet)lookupVariable_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
        if(isFinal && num == state().boundariesCrossed)
            lookupVariable_String_values.put(_parameters, lookupVariable_String_value);
        return lookupVariable_String_value;
    }

    protected boolean reachableCatchClause_computed = false;
    protected boolean reachableCatchClause_value;
    // Declared in UnreachableStatements.jrag at line 124
 @SuppressWarnings({"unchecked", "cast"})     public boolean reachableCatchClause() {
        if(reachableCatchClause_computed) {
            return reachableCatchClause_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        reachableCatchClause_value = getParent().Define_boolean_reachableCatchClause(this, null);
        if(isFinal && num == state().boundariesCrossed)
            reachableCatchClause_computed = true;
        return reachableCatchClause_value;
    }

    // Declared in EmitJimple.jrag at line 885
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl hostType() {
        ASTNode$State state = state();
        TypeDecl hostType_value = getParent().Define_TypeDecl_hostType(this, null);
        return hostType_value;
    }

    // Declared in LookupVariable.jrag at line 83
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getParameterNoTransform()) {
            return parameterDeclaration(name);
        }
        if(caller == getBlockNoTransform()){
    SimpleSet set = parameterDeclaration(name);
    if(!set.isEmpty()) return set;
    return lookupVariable(name);
  }
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }

    // Declared in NameCheck.jrag at line 290
    public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
        if(caller == getParameterNoTransform()) {
            return this;
        }
        return getParent().Define_VariableScope_outerScope(this, caller);
    }

    // Declared in SyntacticClassification.jrag at line 86
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getParameterNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 122
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return reachableCatchClause();
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 83
    public boolean Define_boolean_isMethodParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterNoTransform()) {
            return false;
        }
        return getParent().Define_boolean_isMethodParameter(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 84
    public boolean Define_boolean_isConstructorParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterNoTransform()) {
            return false;
        }
        return getParent().Define_boolean_isConstructorParameter(this, caller);
    }

    // Declared in VariableDeclaration.jrag at line 85
    public boolean Define_boolean_isExceptionHandlerParameter(ASTNode caller, ASTNode child) {
        if(caller == getParameterNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isExceptionHandlerParameter(this, caller);
    }

    // Declared in VariableArityParameters.jrag at line 23
    public boolean Define_boolean_variableArityValid(ASTNode caller, ASTNode child) {
        if(caller == getParameterNoTransform()) {
            return false;
        }
        return getParent().Define_boolean_variableArityValid(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
