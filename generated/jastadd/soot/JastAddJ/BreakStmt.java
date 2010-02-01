
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class BreakStmt extends Stmt implements Cloneable {
    public void flushCache() {
        super.flushCache();
        targetStmt_computed = false;
        targetStmt_value = null;
        finallyList_computed = false;
        finallyList_value = null;
        isDAafter_Variable_values = null;
        isDUafterReachedFinallyBlocks_Variable_values = null;
        isDAafterReachedFinallyBlocks_Variable_values = null;
        isDUafter_Variable_values = null;
        canCompleteNormally_computed = false;
        inSynchronizedBlock_computed = false;
        lookupLabel_String_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public BreakStmt clone() throws CloneNotSupportedException {
        BreakStmt node = (BreakStmt)super.clone();
        node.targetStmt_computed = false;
        node.targetStmt_value = null;
        node.finallyList_computed = false;
        node.finallyList_value = null;
        node.isDAafter_Variable_values = null;
        node.isDUafterReachedFinallyBlocks_Variable_values = null;
        node.isDAafterReachedFinallyBlocks_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.canCompleteNormally_computed = false;
        node.inSynchronizedBlock_computed = false;
        node.lookupLabel_String_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public BreakStmt copy() {
      try {
          BreakStmt node = (BreakStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public BreakStmt fullCopy() {
        BreakStmt res = (BreakStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BranchTarget.jrag at line 52

  public void collectBranches(Collection c) {
    c.add(this);
  }

    // Declared in NameCheck.jrag at line 374


  public void nameCheck() {
    if(!hasLabel() && !insideLoop() && !insideSwitch())
      error("break outside switch or loop");
    else if(hasLabel()) {
      LabeledStmt label = lookupLabel(getLabel());
      if(label == null)
        error("labeled break must have visible matching label");
    }
  }

    // Declared in PrettyPrint.jadd at line 666


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("break ");
    if(hasLabel())
      s.append(getLabel());
    s.append(";");
  }

    // Declared in Statements.jrag at line 209


  public void jimplify2(Body b) {
    ArrayList list = exceptionRanges();
    if(!inSynchronizedBlock())
      endExceptionRange(b, list);
    for(Iterator iter = finallyList().iterator(); iter.hasNext(); ) {
      FinallyHost stmt = (FinallyHost)iter.next();
      stmt.emitFinallyCode(b);
    }
    if(inSynchronizedBlock())
      endExceptionRange(b, list);
    b.setLine(this);
    b.add(b.newGotoStmt(targetStmt().break_label(), this));
    beginExceptionRange(b, list);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 215

    public BreakStmt() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 215
    public BreakStmt(String p0) {
        setLabel(p0);
    }

    // Declared in java.ast at line 15


    // Declared in java.ast line 215
    public BreakStmt(beaver.Symbol p0) {
        setLabel(p0);
    }

    // Declared in java.ast at line 19


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 22

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 215
    protected String tokenString_Label;

    // Declared in java.ast at line 3

    public void setLabel(String value) {
        tokenString_Label = value;
    }

    // Declared in java.ast at line 6

    public int Labelstart;

    // Declared in java.ast at line 7

    public int Labelend;

    // Declared in java.ast at line 8

    public void setLabel(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setLabel is only valid for String lexemes");
        tokenString_Label = (String)symbol.value;
        Labelstart = symbol.getStart();
        Labelend = symbol.getEnd();
    }

    // Declared in java.ast at line 15

    public String getLabel() {
        return tokenString_Label != null ? tokenString_Label : "";
    }

    // Declared in BranchTarget.jrag at line 66
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasLabel() {
        ASTNode$State state = state();
        boolean hasLabel_value = hasLabel_compute();
        return hasLabel_value;
    }

    private boolean hasLabel_compute() {  return !getLabel().equals("");  }

    protected boolean targetStmt_computed = false;
    protected Stmt targetStmt_value;
    // Declared in BranchTarget.jrag at line 149
 @SuppressWarnings({"unchecked", "cast"})     public Stmt targetStmt() {
        if(targetStmt_computed) {
            return targetStmt_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        targetStmt_value = targetStmt_compute();
        if(isFinal && num == state().boundariesCrossed)
            targetStmt_computed = true;
        return targetStmt_value;
    }

    private Stmt targetStmt_compute() {  return branchTarget(this);  }

    protected boolean finallyList_computed = false;
    protected ArrayList finallyList_value;
    // Declared in BranchTarget.jrag at line 176
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

    // Declared in DefiniteAssignment.jrag at line 648
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
    // Declared in DefiniteAssignment.jrag at line 925
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
    // Declared in DefiniteAssignment.jrag at line 957
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
    if(isDAbefore(v))
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

    // Declared in DefiniteAssignment.jrag at line 1174
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

    // Declared in UnreachableStatements.jrag at line 105
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
    // Declared in Statements.jrag at line 250
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

    protected java.util.Map lookupLabel_String_values;
    // Declared in BranchTarget.jrag at line 169
 @SuppressWarnings({"unchecked", "cast"})     public LabeledStmt lookupLabel(String name) {
        Object _parameters = name;
if(lookupLabel_String_values == null) lookupLabel_String_values = new java.util.HashMap(4);
        if(lookupLabel_String_values.containsKey(_parameters)) {
            return (LabeledStmt)lookupLabel_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        LabeledStmt lookupLabel_String_value = getParent().Define_LabeledStmt_lookupLabel(this, null, name);
        if(isFinal && num == state().boundariesCrossed)
            lookupLabel_String_values.put(_parameters, lookupLabel_String_value);
        return lookupLabel_String_value;
    }

    // Declared in NameCheck.jrag at line 360
 @SuppressWarnings({"unchecked", "cast"})     public boolean insideLoop() {
        ASTNode$State state = state();
        boolean insideLoop_value = getParent().Define_boolean_insideLoop(this, null);
        return insideLoop_value;
    }

    // Declared in NameCheck.jrag at line 369
 @SuppressWarnings({"unchecked", "cast"})     public boolean insideSwitch() {
        ASTNode$State state = state();
        boolean insideSwitch_value = getParent().Define_boolean_insideSwitch(this, null);
        return insideSwitch_value;
    }

    // Declared in Statements.jrag at line 444
 @SuppressWarnings({"unchecked", "cast"})     public ArrayList exceptionRanges() {
        ASTNode$State state = state();
        ArrayList exceptionRanges_value = getParent().Define_ArrayList_exceptionRanges(this, null);
        return exceptionRanges_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
