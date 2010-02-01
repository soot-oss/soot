
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class SwitchStmt extends BranchTargetStmt implements Cloneable {
    public void flushCache() {
        super.flushCache();
        targetOf_ContinueStmt_values = null;
        targetOf_BreakStmt_values = null;
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        canCompleteNormally_computed = false;
        defaultCase_computed = false;
        defaultCase_value = null;
        end_label_computed = false;
        end_label_value = null;
        typeInt_computed = false;
        typeInt_value = null;
        typeLong_computed = false;
        typeLong_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public SwitchStmt clone() throws CloneNotSupportedException {
        SwitchStmt node = (SwitchStmt)super.clone();
        node.targetOf_ContinueStmt_values = null;
        node.targetOf_BreakStmt_values = null;
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.canCompleteNormally_computed = false;
        node.defaultCase_computed = false;
        node.defaultCase_value = null;
        node.end_label_computed = false;
        node.end_label_value = null;
        node.typeInt_computed = false;
        node.typeInt_value = null;
        node.typeLong_computed = false;
        node.typeLong_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SwitchStmt copy() {
      try {
          SwitchStmt node = (SwitchStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SwitchStmt fullCopy() {
        SwitchStmt res = (SwitchStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 553


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("switch (");
    getExpr().toString(s);
    s.append(")");
    getBlock().toString(s);
  }

    // Declared in Statements.jrag at line 50


  public void jimplify2(Body b) {
    soot.jimple.Stmt cond_label = newLabel();
    soot.jimple.Stmt switch_label = newLabel();

    b.setLine(this);
    b.add(b.newGotoStmt(cond_label, this));
    getBlock().jimplify2(b);
    if(canCompleteNormally()) {
      b.setLine(this);
      b.add(b.newGotoStmt(end_label(), this));
    }
    b.addLabel(cond_label);
    soot.Value expr = asImmediate(b, getExpr().eval(b));

    TreeMap map = new TreeMap();
    for(int i= 0; i < getBlock().getNumStmt(); i++) {
      if(getBlock().getStmt(i) instanceof ConstCase) {
        ConstCase ca = (ConstCase)getBlock().getStmt(i);
        map.put(new Integer(ca.getValue().constant().intValue()), ca);
      }        
    }

    long low = map.isEmpty() ? 0 : ((Integer)map.firstKey()).intValue();
    long high = map.isEmpty() ? 0 : ((Integer)map.lastKey()).intValue();

    long tableSwitchSize = 8L + (high - low + 1L) * 4L;
    long lookupSwitchSize = 4L + map.size() * 8L;

    b.addLabel(switch_label);
    soot.jimple.Stmt defaultStmt = defaultCase() != null ? defaultCase().label() : end_label();
    if(tableSwitchSize < lookupSwitchSize) {
      ArrayList targets = new ArrayList();
      for(long i = low; i <= high; i++) {
        ConstCase ca = (ConstCase)map.get(new Integer((int)i));
        if(ca != null)
          targets.add(ca.label());
        else
          targets.add(defaultStmt);
      }
      b.setLine(this);
      b.add(b.newTableSwitchStmt(expr, (int)low, (int)high, targets, defaultStmt, this));
    }
    else {
      ArrayList targets = new ArrayList();
      ArrayList values = new ArrayList();
      for(Iterator iter = map.values().iterator(); iter.hasNext(); ) {
        ConstCase ca = (ConstCase)iter.next();
        targets.add(ca.label());
        values.add(IntType.emitConstant(ca.getValue().constant().intValue()));
      }

      b.setLine(this);
      b.add(b.newLookupSwitchStmt(expr, values, targets, defaultStmt, this));
    }
    b.addLabel(end_label());
  }

    // Declared in EnumsCodegen.jrag at line 17


  // transform enum switch statements into integer indexed switch statements
  public void transformation() {
    if(getExpr().type().isEnumDecl()) {
      TypeDecl type = getExpr().type();
      hostType().createEnumArray(type);
      hostType().createEnumMethod(type);
      setExpr(
        hostType().createEnumMethod(type).createBoundAccess(new List()).qualifiesAccess(
        new ArrayAccess(
          getExpr().qualifiesAccess(new MethodAccess("ordinal", new List()))
        ))
      );
    }
    super.transformation();
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 205

    public SwitchStmt() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 205
    public SwitchStmt(Expr p0, Block p1) {
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
    // Declared in java.ast line 205
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
    // Declared in java.ast line 205
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

    // Declared in Enums.jrag at line 471


    public void typeCheck() {
     TypeDecl type = getExpr().type();
    if((!type.isIntegralType() || type.isLong()) && !type.isEnumDecl())
      error("Switch expression must be of char, byte, short, int, or enum type");
  }

    protected java.util.Map targetOf_ContinueStmt_values;
    // Declared in BranchTarget.jrag at line 73
 @SuppressWarnings({"unchecked", "cast"})     public boolean targetOf(ContinueStmt stmt) {
        Object _parameters = stmt;
if(targetOf_ContinueStmt_values == null) targetOf_ContinueStmt_values = new java.util.HashMap(4);
        if(targetOf_ContinueStmt_values.containsKey(_parameters)) {
            return ((Boolean)targetOf_ContinueStmt_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean targetOf_ContinueStmt_value = targetOf_compute(stmt);
        if(isFinal && num == state().boundariesCrossed)
            targetOf_ContinueStmt_values.put(_parameters, Boolean.valueOf(targetOf_ContinueStmt_value));
        return targetOf_ContinueStmt_value;
    }

    private boolean targetOf_compute(ContinueStmt stmt) {  return false;  }

    protected java.util.Map targetOf_BreakStmt_values;
    // Declared in BranchTarget.jrag at line 77
 @SuppressWarnings({"unchecked", "cast"})     public boolean targetOf(BreakStmt stmt) {
        Object _parameters = stmt;
if(targetOf_BreakStmt_values == null) targetOf_BreakStmt_values = new java.util.HashMap(4);
        if(targetOf_BreakStmt_values.containsKey(_parameters)) {
            return ((Boolean)targetOf_BreakStmt_values.get(_parameters)).booleanValue();
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean targetOf_BreakStmt_value = targetOf_compute(stmt);
        if(isFinal && num == state().boundariesCrossed)
            targetOf_BreakStmt_values.put(_parameters, Boolean.valueOf(targetOf_BreakStmt_value));
        return targetOf_BreakStmt_value;
    }

    private boolean targetOf_compute(BreakStmt stmt) {  return !stmt.hasLabel();  }

    // Declared in DefiniteAssignment.jrag at line 532
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

    private boolean isDAafter_compute(Variable v) {
    if(!(!noDefaultLabel() || getExpr().isDAafter(v))) {
      return false;
    }
    if(!(!switchLabelEndsBlock() || getExpr().isDAafter(v))) {
      return false;
    }
    if(!assignedAfterLastStmt(v)) {
      return false;
    }
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(!stmt.isDAafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }

    // Declared in DefiniteAssignment.jrag at line 550
 @SuppressWarnings({"unchecked", "cast"})     public boolean assignedAfterLastStmt(Variable v) {
        ASTNode$State state = state();
        boolean assignedAfterLastStmt_Variable_value = assignedAfterLastStmt_compute(v);
        return assignedAfterLastStmt_Variable_value;
    }

    private boolean assignedAfterLastStmt_compute(Variable v) {  return getBlock().isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 1004
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

    private boolean isDUafter_compute(Variable v) {
    if(!(!noDefaultLabel() || getExpr().isDUafter(v)))
      return false;
    if(!(!switchLabelEndsBlock() || getExpr().isDUafter(v)))
      return false;
    if(!unassignedAfterLastStmt(v))
      return false;
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(!stmt.isDUafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }

    // Declared in DefiniteAssignment.jrag at line 1019
 @SuppressWarnings({"unchecked", "cast"})     public boolean unassignedAfterLastStmt(Variable v) {
        ASTNode$State state = state();
        boolean unassignedAfterLastStmt_Variable_value = unassignedAfterLastStmt_compute(v);
        return unassignedAfterLastStmt_Variable_value;
    }

    private boolean unassignedAfterLastStmt_compute(Variable v) {  return getBlock().isDUafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 1022
 @SuppressWarnings({"unchecked", "cast"})     public boolean switchLabelEndsBlock() {
        ASTNode$State state = state();
        boolean switchLabelEndsBlock_value = switchLabelEndsBlock_compute();
        return switchLabelEndsBlock_value;
    }

    private boolean switchLabelEndsBlock_compute() {  return getBlock().getNumStmt() > 0 && getBlock().getStmt(getBlock().getNumStmt()-1) instanceof ConstCase;  }

    // Declared in UnreachableStatements.jrag at line 60
 @SuppressWarnings({"unchecked", "cast"})     public boolean lastStmtCanCompleteNormally() {
        ASTNode$State state = state();
        boolean lastStmtCanCompleteNormally_value = lastStmtCanCompleteNormally_compute();
        return lastStmtCanCompleteNormally_value;
    }

    private boolean lastStmtCanCompleteNormally_compute() {  return getBlock().canCompleteNormally();  }

    // Declared in UnreachableStatements.jrag at line 62
 @SuppressWarnings({"unchecked", "cast"})     public boolean noStmts() {
        ASTNode$State state = state();
        boolean noStmts_value = noStmts_compute();
        return noStmts_value;
    }

    private boolean noStmts_compute() {
    for(int i = 0; i < getBlock().getNumStmt(); i++)
      if(!(getBlock().getStmt(i) instanceof Case))
        return false;
    return true;
  }

    // Declared in UnreachableStatements.jrag at line 69
 @SuppressWarnings({"unchecked", "cast"})     public boolean noStmtsAfterLastLabel() {
        ASTNode$State state = state();
        boolean noStmtsAfterLastLabel_value = noStmtsAfterLastLabel_compute();
        return noStmtsAfterLastLabel_value;
    }

    private boolean noStmtsAfterLastLabel_compute() {  return getBlock().getNumStmt() > 0 && getBlock().getStmt(getBlock().getNumStmt()-1) instanceof Case;  }

    // Declared in UnreachableStatements.jrag at line 72
 @SuppressWarnings({"unchecked", "cast"})     public boolean noDefaultLabel() {
        ASTNode$State state = state();
        boolean noDefaultLabel_value = noDefaultLabel_compute();
        return noDefaultLabel_value;
    }

    private boolean noDefaultLabel_compute() {
    for(int i = 0; i < getBlock().getNumStmt(); i++)
      if(getBlock().getStmt(i) instanceof DefaultCase)
        return false;
    return true;
  }

    // Declared in UnreachableStatements.jrag at line 79
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

    private boolean canCompleteNormally_compute() {  return lastStmtCanCompleteNormally() || noStmts() || noStmtsAfterLastLabel() || noDefaultLabel() || reachableBreak();  }

    protected boolean defaultCase_computed = false;
    protected DefaultCase defaultCase_value;
    // Declared in Statements.jrag at line 40
 @SuppressWarnings({"unchecked", "cast"})     public DefaultCase defaultCase() {
        if(defaultCase_computed) {
            return defaultCase_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        defaultCase_value = defaultCase_compute();
        if(isFinal && num == state().boundariesCrossed)
            defaultCase_computed = true;
        return defaultCase_value;
    }

    private DefaultCase defaultCase_compute() {
    for(int i= 0; i < getBlock().getNumStmt(); i++) {
      if(getBlock().getStmt(i) instanceof DefaultCase)
        return (DefaultCase)getBlock().getStmt(i);
    }
    return null;
  }

    protected boolean end_label_computed = false;
    protected soot.jimple.Stmt end_label_value;
    // Declared in Statements.jrag at line 48
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt end_label() {
        if(end_label_computed) {
            return end_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        end_label_value = end_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            end_label_computed = true;
        return end_label_value;
    }

    private soot.jimple.Stmt end_label_compute() {  return newLabel();  }

    // Declared in Statements.jrag at line 206
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt break_label() {
        ASTNode$State state = state();
        soot.jimple.Stmt break_label_value = break_label_compute();
        return break_label_value;
    }

    private soot.jimple.Stmt break_label_compute() {  return end_label();  }

    protected boolean typeInt_computed = false;
    protected TypeDecl typeInt_value;
    // Declared in LookupType.jrag at line 61
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeInt() {
        if(typeInt_computed) {
            return typeInt_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        typeInt_value = getParent().Define_TypeDecl_typeInt(this, null);
        if(isFinal && num == state().boundariesCrossed)
            typeInt_computed = true;
        return typeInt_value;
    }

    protected boolean typeLong_computed = false;
    protected TypeDecl typeLong_value;
    // Declared in LookupType.jrag at line 63
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeLong() {
        if(typeLong_computed) {
            return typeLong_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        typeLong_value = getParent().Define_TypeDecl_typeLong(this, null);
        if(isFinal && num == state().boundariesCrossed)
            typeLong_computed = true;
        return typeLong_value;
    }

    // Declared in DefiniteAssignment.jrag at line 569
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockNoTransform()) {
            return getExpr().isDAafter(v);
        }
        if(caller == getExprNoTransform()){
    if(((ASTNode)v).isDescendantTo(this))
      return false;
    boolean result = isDAbefore(v);
    return result;
  }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 1027
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getBlockNoTransform()) {
            return getExpr().isDUafter(v);
        }
        if(caller == getExprNoTransform()) {
            return isDUbefore(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in NameCheck.jrag at line 372
    public boolean Define_boolean_insideSwitch(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_insideSwitch(this, caller);
    }

    // Declared in NameCheck.jrag at line 413
    public Case Define_Case_bind(ASTNode caller, ASTNode child, Case c) {
        if(caller == getBlockNoTransform()){
    Block b = getBlock();
    for(int i = 0; i < b.getNumStmt(); i++)
      if(b.getStmt(i) instanceof Case && ((Case)b.getStmt(i)).constValue(c))
        return (Case)b.getStmt(i);
    return null;
  }
        return getParent().Define_Case_bind(this, caller, c);
    }

    // Declared in TypeCheck.jrag at line 359
    public TypeDecl Define_TypeDecl_switchType(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return getExpr().type();
        }
        return getParent().Define_TypeDecl_switchType(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 82
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 156
    public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reportUnreachable(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
