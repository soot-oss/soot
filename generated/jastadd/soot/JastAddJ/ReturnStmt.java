
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ReturnStmt extends Stmt implements Cloneable {
    public void flushCache() {
        super.flushCache();
        finallyList_computed = false;
        finallyList_value = null;
        isDAafter_Variable_values = null;
        isDUafterReachedFinallyBlocks_Variable_values = null;
        isDAafterReachedFinallyBlocks_Variable_values = null;
        isDUafter_Variable_values = null;
        canCompleteNormally_computed = false;
        inSynchronizedBlock_computed = false;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ReturnStmt clone() throws CloneNotSupportedException {
        ReturnStmt node = (ReturnStmt)super.clone();
        node.finallyList_computed = false;
        node.finallyList_value = null;
        node.isDAafter_Variable_values = null;
        node.isDUafterReachedFinallyBlocks_Variable_values = null;
        node.isDAafterReachedFinallyBlocks_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.canCompleteNormally_computed = false;
        node.inSynchronizedBlock_computed = false;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ReturnStmt copy() {
      try {
          ReturnStmt node = (ReturnStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ReturnStmt fullCopy() {
        ReturnStmt res = (ReturnStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BranchTarget.jrag at line 55

  public void collectBranches(Collection c) {
    c.add(this);
  }

    // Declared in NodeConstructors.jrag at line 62


  public ReturnStmt(Expr expr) {
    this(new Opt(expr));
  }

    // Declared in PrettyPrint.jadd at line 682


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("return ");
    if(hasResult()) {
      getResult().toString(s);
    }
    s.append(";");
  }

    // Declared in TypeCheck.jrag at line 408


  public void typeCheck() {
    if(hasResult() && !returnType().isVoid()) {
      if(!getResult().type().assignConversionTo(returnType(), getResult()))
        error("return value must be an instance of " + returnType().typeName() + " which " + getResult().type().typeName() + " is not");
    }
    // 8.4.5 8.8.5
    if(returnType().isVoid() && hasResult())
      error("return stmt may not have an expression in void methods");
    // 8.4.5
    if(!returnType().isVoid() && !hasResult())
      error("return stmt must have an expression in non void methods");
    if(enclosingBodyDecl() instanceof InstanceInitializer || enclosingBodyDecl() instanceof StaticInitializer)
      error("Initializers may not return");

  }

    // Declared in Statements.jrag at line 268


  public void jimplify2(Body b) {
    if(hasResult()) {
      TypeDecl type = returnType();
      if(type.isVoid()) {
        throw new Error("Can not return a value from a void body");
      }
      Local local = asLocal(b,
        getResult().type().emitCastTo(b,
          getResult().eval(b),
          type,
          getResult()
        ),
        type.getSootType()
      );
      ArrayList list = exceptionRanges();
      if(!inSynchronizedBlock())
        endExceptionRange(b, list);
      for(Iterator iter = finallyList().iterator(); iter.hasNext(); ) {
        FinallyHost stmt = (FinallyHost)iter.next();
        stmt.emitFinallyCode(b);
      }
      b.setLine(this);
      if(inSynchronizedBlock())
        endExceptionRange(b, list);
      b.add(b.newReturnStmt(local, this));
      beginExceptionRange(b, list);
    }
    else {
      ArrayList list = exceptionRanges();
      if(!inSynchronizedBlock())
        endExceptionRange(b, list);
      for(Iterator iter = finallyList().iterator(); iter.hasNext(); ) {
        FinallyHost stmt = (FinallyHost)iter.next();
        stmt.emitFinallyCode(b);
      }
      b.setLine(this);
      if(inSynchronizedBlock())
        endExceptionRange(b, list);
      b.add(b.newReturnVoidStmt(this));
      beginExceptionRange(b, list);
    }
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 217

    public ReturnStmt() {
        super();

        setChild(new Opt(), 0);

    }

    // Declared in java.ast at line 11


    // Declared in java.ast line 217
    public ReturnStmt(Opt<Expr> p0) {
        setChild(p0, 0);
    }

    // Declared in java.ast at line 15


  protected int numChildren() {
    return 1;
  }

    // Declared in java.ast at line 18

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 217
    public void setResultOpt(Opt<Expr> opt) {
        setChild(opt, 0);
    }

    // Declared in java.ast at line 6


    public boolean hasResult() {
        return getResultOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getResult() {
        return (Expr)getResultOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setResult(Expr node) {
        getResultOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getResultOpt() {
        return (Opt<Expr>)getChild(0);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getResultOptNoTransform() {
        return (Opt<Expr>)getChildNoTransform(0);
    }

    protected boolean finallyList_computed = false;
    protected ArrayList finallyList_value;
    // Declared in BranchTarget.jrag at line 186
 @SuppressWarnings({"unchecked", "cast"})     public ArrayList finallyList() {
        if(finallyList_computed) {
            return finallyList_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        finallyList_value = finallyList_compute();
        if(isFinal && num == state().boundariesCrossed)
            finallyList_computed = true;
        return finallyList_value;
    }

    private ArrayList finallyList_compute() {
    ArrayList list = new ArrayList();
    collectFinally(this, list);
    return list;
  }

    // Declared in DefiniteAssignment.jrag at line 650
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

    private boolean isDAafter_compute(Variable v) {  return true;  }

    protected java.util.Map isDUafterReachedFinallyBlocks_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 946
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterReachedFinallyBlocks(Variable v) {
        Object _parameters = v;
if(isDUafterReachedFinallyBlocks_Variable_values == null) isDUafterReachedFinallyBlocks_Variable_values = new java.util.HashMap(4);
        if(isDUafterReachedFinallyBlocks_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDUafterReachedFinallyBlocks_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDUafterReachedFinallyBlocks_Variable_value = isDUafterReachedFinallyBlocks_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDUafterReachedFinallyBlocks_Variable_values.put(_parameters, Boolean.valueOf(isDUafterReachedFinallyBlocks_Variable_value));
        return isDUafterReachedFinallyBlocks_Variable_value;
    }

    private boolean isDUafterReachedFinallyBlocks_compute(Variable v) {
    if(!isDUbefore(v) && finallyList().isEmpty())
      return false;
    for(Iterator iter = finallyList().iterator(); iter.hasNext(); ) {
      FinallyHost f = (FinallyHost)iter.next();
      if(!f.isDUafterFinally(v))
        return false;
    }
    return true;
  }

    protected java.util.Map isDAafterReachedFinallyBlocks_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 982
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterReachedFinallyBlocks(Variable v) {
        Object _parameters = v;
if(isDAafterReachedFinallyBlocks_Variable_values == null) isDAafterReachedFinallyBlocks_Variable_values = new java.util.HashMap(4);
        if(isDAafterReachedFinallyBlocks_Variable_values.containsKey(_parameters)) {
            return ((Boolean)isDAafterReachedFinallyBlocks_Variable_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean isDAafterReachedFinallyBlocks_Variable_value = isDAafterReachedFinallyBlocks_compute(v);
        if(isFinal && num == state().boundariesCrossed)
            isDAafterReachedFinallyBlocks_Variable_values.put(_parameters, Boolean.valueOf(isDAafterReachedFinallyBlocks_Variable_value));
        return isDAafterReachedFinallyBlocks_Variable_value;
    }

    private boolean isDAafterReachedFinallyBlocks_compute(Variable v) {
    if(hasResult() ? getResult().isDAafter(v) : isDAbefore(v))
      return true;
    if(finallyList().isEmpty())
      return false;
    for(Iterator iter = finallyList().iterator(); iter.hasNext(); ) {
      FinallyHost f = (FinallyHost)iter.next();
      if(!f.isDAafterFinally(v))
        return false;
    }
    return true;
  }

    // Declared in DefiniteAssignment.jrag at line 1176
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

    private boolean isDUafter_compute(Variable v) {  return true;  }

    // Declared in UnreachableStatements.jrag at line 107
 @SuppressWarnings({"unchecked", "cast"})     public boolean canCompleteNormally() {
        if(canCompleteNormally_computed) {
            return canCompleteNormally_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        canCompleteNormally_value = canCompleteNormally_compute();
        if(isFinal && num == state().boundariesCrossed)
            canCompleteNormally_computed = true;
        return canCompleteNormally_value;
    }

    private boolean canCompleteNormally_compute() {  return false;  }

    protected boolean inSynchronizedBlock_computed = false;
    protected boolean inSynchronizedBlock_value;
    // Declared in Statements.jrag at line 248
 @SuppressWarnings({"unchecked", "cast"})     public boolean inSynchronizedBlock() {
        if(inSynchronizedBlock_computed) {
            return inSynchronizedBlock_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        inSynchronizedBlock_value = inSynchronizedBlock_compute();
        if(isFinal && num == state().boundariesCrossed)
            inSynchronizedBlock_computed = true;
        return inSynchronizedBlock_value;
    }

    private boolean inSynchronizedBlock_compute() {  return !finallyList().isEmpty() && finallyList().iterator().next() instanceof SynchronizedStmt;  }

    // Declared in TypeCheck.jrag at line 403
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl returnType() {
        ASTNode$State state = state();
        TypeDecl returnType_value = getParent().Define_TypeDecl_returnType(this, null);
        return returnType_value;
    }

    // Declared in Statements.jrag at line 442
 @SuppressWarnings({"unchecked", "cast"})     public ArrayList exceptionRanges() {
        ASTNode$State state = state();
        ArrayList exceptionRanges_value = getParent().Define_ArrayList_exceptionRanges(this, null);
        return exceptionRanges_value;
    }

    // Declared in DefiniteAssignment.jrag at line 653
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getResultOptNoTransform()) {
            return isDAbefore(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 1179
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getResultOptNoTransform()) {
            return isDUbefore(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in GenericMethodsInference.jrag at line 38
    public TypeDecl Define_TypeDecl_assignConvertedType(ASTNode caller, ASTNode child) {
        if(caller == getResultOptNoTransform()) {
            return returnType();
        }
        return getParent().Define_TypeDecl_assignConvertedType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
