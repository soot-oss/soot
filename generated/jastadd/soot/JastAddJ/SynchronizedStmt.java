
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class SynchronizedStmt extends Stmt implements Cloneable, FinallyHost {
    public void flushCache() {
        super.flushCache();
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        canCompleteNormally_computed = false;
        monitor_Body_values = null;
        exceptionRanges_computed = false;
        exceptionRanges_value = null;
        label_begin_computed = false;
        label_begin_value = null;
        label_end_computed = false;
        label_end_value = null;
        label_finally_computed = false;
        label_finally_value = null;
        label_finally_block_computed = false;
        label_finally_block_value = null;
        label_exception_handler_computed = false;
        label_exception_handler_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public SynchronizedStmt clone() throws CloneNotSupportedException {
        SynchronizedStmt node = (SynchronizedStmt)super.clone();
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.canCompleteNormally_computed = false;
        node.monitor_Body_values = null;
        node.exceptionRanges_computed = false;
        node.exceptionRanges_value = null;
        node.label_begin_computed = false;
        node.label_begin_value = null;
        node.label_end_computed = false;
        node.label_end_value = null;
        node.label_finally_computed = false;
        node.label_finally_value = null;
        node.label_finally_block_computed = false;
        node.label_finally_block_value = null;
        node.label_exception_handler_computed = false;
        node.label_exception_handler_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SynchronizedStmt copy() {
      try {
          SynchronizedStmt node = (SynchronizedStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SynchronizedStmt fullCopy() {
        SynchronizedStmt res = (SynchronizedStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BranchTarget.jrag at line 207

  public void collectFinally(Stmt branchStmt, ArrayList list) {
    list.add(this);
    super.collectFinally(branchStmt, list);
  }

    // Declared in PrettyPrint.jadd at line 698


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("synchronized(");
    getExpr().toString(s);
    s.append(") ");
    getBlock().toString(s);
  }

    // Declared in TypeCheck.jrag at line 362


  public void typeCheck() {
    TypeDecl type = getExpr().type();
    if(!type.isReferenceType() || type.isNull())
      error("*** The type of the expression must be a reference");
  }

    // Declared in Statements.jrag at line 333


  public void emitFinallyCode(Body b) {
    b.setLine(this);
    b.add(b.newExitMonitorStmt(monitor(b), this));
  }

    // Declared in Statements.jrag at line 484


  public void jimplify2(Body b) {
    b.setLine(this);
    b.add(b.newEnterMonitorStmt(monitor(b), this));
    b.addLabel(label_begin());
    exceptionRanges().add(label_begin());
    getBlock().jimplify2(b);
    if(getBlock().canCompleteNormally()) {
      emitFinallyCode(b);
      b.add(b.newGotoStmt(label_end(), this));
    }
    b.addLabel(label_exception_handler());

    // emitExceptionHandler
    Local l = b.newTemp(typeThrowable().getSootType());
    b.add(b.newIdentityStmt(l, b.newCaughtExceptionRef(this), this));
    emitFinallyCode(b);
    soot.jimple.Stmt throwStmt = b.newThrowStmt(l, this);
    throwStmt.addTag(new soot.tagkit.ThrowCreatedByCompilerTag());
    b.add(throwStmt);
    b.addLabel(label_end());

    // createExceptionTable
    for(Iterator iter = exceptionRanges().iterator(); iter.hasNext(); ) {
      soot.jimple.Stmt stmtBegin = (soot.jimple.Stmt)iter.next();
      soot.jimple.Stmt stmtEnd;
      if(iter.hasNext())
        stmtEnd = (soot.jimple.Stmt)iter.next();
      else
        stmtEnd = label_end();
      if(stmtBegin != stmtEnd)
        b.addTrap(typeThrowable(), stmtBegin, stmtEnd, label_exception_handler());
    }
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 220

    public SynchronizedStmt() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 220
    public SynchronizedStmt(Expr p0, Block p1) {
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
    // Declared in java.ast line 220
    public void setExpr(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getExpr() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getExprNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 220
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

    // Declared in DefiniteAssignment.jrag at line 656
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

    private boolean isDAafter_compute(Variable v) {  return getBlock().isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 919
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterFinally(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterFinally_Variable_value = isDUafterFinally_compute(v);
        return isDUafterFinally_Variable_value;
    }

    private boolean isDUafterFinally_compute(Variable v) {  return true;  }

    // Declared in DefiniteAssignment.jrag at line 922
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterFinally(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterFinally_Variable_value = isDAafterFinally_compute(v);
        return isDAafterFinally_Variable_value;
    }

    private boolean isDAafterFinally_compute(Variable v) {  return false;  }

    // Declared in DefiniteAssignment.jrag at line 1182
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

    private boolean isDUafter_compute(Variable v) {  return getBlock().isDUafter(v);  }

    // Declared in UnreachableStatements.jrag at line 110
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

    private boolean canCompleteNormally_compute() {  return getBlock().canCompleteNormally();  }

    protected java.util.Map monitor_Body_values;
    // Declared in Statements.jrag at line 329
 @SuppressWarnings({"unchecked", "cast"})     public soot.Local monitor(Body b) {
        Object _parameters = b;
if(monitor_Body_values == null) monitor_Body_values = new java.util.HashMap(4);
        if(monitor_Body_values.containsKey(_parameters)) {
            return (soot.Local)monitor_Body_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        soot.Local monitor_Body_value = monitor_compute(b);
        if(isFinal && num == state().boundariesCrossed)
            monitor_Body_values.put(_parameters, monitor_Body_value);
        return monitor_Body_value;
    }

    private soot.Local monitor_compute(Body b) {
    return b.newTemp(getExpr().eval(b));
  }

    // Declared in Statements.jrag at line 354
 @SuppressWarnings({"unchecked", "cast"})     public boolean needsFinallyTrap() {
        ASTNode$State state = state();
        boolean needsFinallyTrap_value = needsFinallyTrap_compute();
        return needsFinallyTrap_value;
    }

    private boolean needsFinallyTrap_compute() {  return enclosedByExceptionHandler();  }

    protected boolean exceptionRanges_computed = false;
    protected ArrayList exceptionRanges_value;
    // Declared in Statements.jrag at line 451
 @SuppressWarnings({"unchecked", "cast"})     public ArrayList exceptionRanges() {
        if(exceptionRanges_computed) {
            return exceptionRanges_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        exceptionRanges_value = exceptionRanges_compute();
        if(isFinal && num == state().boundariesCrossed)
            exceptionRanges_computed = true;
        return exceptionRanges_value;
    }

    private ArrayList exceptionRanges_compute() {  return new ArrayList();  }

    protected boolean label_begin_computed = false;
    protected soot.jimple.Stmt label_begin_value;
    // Declared in Statements.jrag at line 478
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_begin() {
        if(label_begin_computed) {
            return label_begin_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_begin_value = label_begin_compute();
        if(isFinal && num == state().boundariesCrossed)
            label_begin_computed = true;
        return label_begin_value;
    }

    private soot.jimple.Stmt label_begin_compute() {  return newLabel();  }

    protected boolean label_end_computed = false;
    protected soot.jimple.Stmt label_end_value;
    // Declared in Statements.jrag at line 479
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_end() {
        if(label_end_computed) {
            return label_end_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_end_value = label_end_compute();
        if(isFinal && num == state().boundariesCrossed)
            label_end_computed = true;
        return label_end_value;
    }

    private soot.jimple.Stmt label_end_compute() {  return newLabel();  }

    protected boolean label_finally_computed = false;
    protected soot.jimple.Stmt label_finally_value;
    // Declared in Statements.jrag at line 480
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_finally() {
        if(label_finally_computed) {
            return label_finally_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_finally_value = label_finally_compute();
        if(isFinal && num == state().boundariesCrossed)
            label_finally_computed = true;
        return label_finally_value;
    }

    private soot.jimple.Stmt label_finally_compute() {  return newLabel();  }

    protected boolean label_finally_block_computed = false;
    protected soot.jimple.Stmt label_finally_block_value;
    // Declared in Statements.jrag at line 481
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_finally_block() {
        if(label_finally_block_computed) {
            return label_finally_block_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_finally_block_value = label_finally_block_compute();
        if(isFinal && num == state().boundariesCrossed)
            label_finally_block_computed = true;
        return label_finally_block_value;
    }

    private soot.jimple.Stmt label_finally_block_compute() {  return newLabel();  }

    protected boolean label_exception_handler_computed = false;
    protected soot.jimple.Stmt label_exception_handler_value;
    // Declared in Statements.jrag at line 482
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_exception_handler() {
        if(label_exception_handler_computed) {
            return label_exception_handler_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_exception_handler_value = label_exception_handler_compute();
        if(isFinal && num == state().boundariesCrossed)
            label_exception_handler_computed = true;
        return label_exception_handler_value;
    }

    private soot.jimple.Stmt label_exception_handler_compute() {  return newLabel();  }

    // Declared in Statements.jrag at line 355
 @SuppressWarnings({"unchecked", "cast"})     public boolean enclosedByExceptionHandler() {
        ASTNode$State state = state();
        boolean enclosedByExceptionHandler_value = getParent().Define_boolean_enclosedByExceptionHandler(this, null);
        return enclosedByExceptionHandler_value;
    }

    // Declared in Statements.jrag at line 464
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeThrowable() {
        ASTNode$State state = state();
        TypeDecl typeThrowable_value = getParent().Define_TypeDecl_typeThrowable(this, null);
        return typeThrowable_value;
    }

    // Declared in DefiniteAssignment.jrag at line 658
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockNoTransform()) {
            return getExpr().isDAafter(v);
        }
        if(caller == getExprNoTransform()) {
            return isDAbefore(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 1184
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockNoTransform()) {
            return getExpr().isDUafter(v);
        }
        if(caller == getExprNoTransform()) {
            return isDUbefore(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in UnreachableStatements.jrag at line 111
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 155
    public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reportUnreachable(this, caller);
    }

    // Declared in Statements.jrag at line 353
    public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
    }

    // Declared in Statements.jrag at line 447
    public ArrayList Define_ArrayList_exceptionRanges(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return exceptionRanges();
        }
        return getParent().Define_ArrayList_exceptionRanges(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
