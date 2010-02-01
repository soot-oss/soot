
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class ForStmt extends BranchTargetStmt implements Cloneable, VariableScope {
    public void flushCache() {
        super.flushCache();
        targetOf_ContinueStmt_values = null;
        targetOf_BreakStmt_values = null;
        isDAafter_Variable_values = null;
        isDUafter_Variable_values = null;
        isDUbeforeCondition_Variable_values = null;
        localLookup_String_values = null;
        localVariableDeclaration_String_values = null;
        canCompleteNormally_computed = false;
        cond_label_computed = false;
        cond_label_value = null;
        begin_label_computed = false;
        begin_label_value = null;
        update_label_computed = false;
        update_label_value = null;
        end_label_computed = false;
        end_label_value = null;
        lookupVariable_String_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ForStmt clone() throws CloneNotSupportedException {
        ForStmt node = (ForStmt)super.clone();
        node.targetOf_ContinueStmt_values = null;
        node.targetOf_BreakStmt_values = null;
        node.isDAafter_Variable_values = null;
        node.isDUafter_Variable_values = null;
        node.isDUbeforeCondition_Variable_values = null;
        node.localLookup_String_values = null;
        node.localVariableDeclaration_String_values = null;
        node.canCompleteNormally_computed = false;
        node.cond_label_computed = false;
        node.cond_label_value = null;
        node.begin_label_computed = false;
        node.begin_label_value = null;
        node.update_label_computed = false;
        node.update_label_value = null;
        node.end_label_computed = false;
        node.end_label_value = null;
        node.lookupVariable_String_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ForStmt copy() {
      try {
          ForStmt node = (ForStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public ForStmt fullCopy() {
        ForStmt res = (ForStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 603


  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("for(");
    if(getNumInitStmt() > 0) {
      if(getInitStmt(0) instanceof VariableDeclaration) {
        int minDimension = Integer.MAX_VALUE;
        for(int i = 0; i < getNumInitStmt(); i++) {
          VariableDeclaration v = (VariableDeclaration)getInitStmt(i);
          minDimension = Math.min(minDimension, v.type().dimension());
        }
        VariableDeclaration v = (VariableDeclaration)getInitStmt(0);
        v.getModifiers().toString(s);
        s.append(v.type().elementType().typeName());
        for(int i = minDimension; i > 0; i--)
          s.append("[]");

        for(int i = 0; i < getNumInitStmt(); i++) {
          if(i != 0)
            s.append(",");
          v = (VariableDeclaration)getInitStmt(i);
          s.append(" " + v.name());
          for(int j = v.type().dimension() - minDimension; j > 0; j--)
            s.append("[]");
          if(v.hasInit()) {
            s.append(" = ");
            v.getInit().toString(s);
          }
        }
      }
      else if(getInitStmt(0) instanceof ExprStmt) {
        ExprStmt stmt = (ExprStmt)getInitStmt(0);
        stmt.getExpr().toString(s);
        for(int i = 1; i < getNumInitStmt(); i++) {
          s.append(", ");
          stmt = (ExprStmt)getInitStmt(i);
          stmt.getExpr().toString(s);
        }
      }
      else {
        throw new Error("Unexpected initializer in for loop: " + getInitStmt(0));
      }
    }
    
    s.append("; ");
    if(hasCondition()) {
      getCondition().toString(s);
    }
    s.append("; ");

    if(getNumUpdateStmt() > 0) {
      ExprStmt stmt = (ExprStmt)getUpdateStmt(0);
      stmt.getExpr().toString(s);
      for(int i = 1; i < getNumUpdateStmt(); i++) {
        s.append(", ");
        stmt = (ExprStmt)getUpdateStmt(i);
        stmt.getExpr().toString(s);
      }
    }
    
    s.append(") ");
    getStmt().toString(s);
  }

    // Declared in TypeCheck.jrag at line 334

  public void typeCheck() {
    if(hasCondition()) {
      TypeDecl cond = getCondition().type();
      if(!cond.isBoolean()) {
        error("the type of \"" + getCondition() + "\" is " + cond.name() + " which is not boolean");
      }
    }
  }

    // Declared in Statements.jrag at line 179


  public void jimplify2(Body b) {
    for (int i=0; i<getNumInitStmt(); i++) {
      getInitStmt(i).jimplify2(b);
    }
    b.addLabel(cond_label());
    getCondition().emitEvalBranch(b);
    if(getCondition().canBeTrue()) {
      b.addLabel(begin_label());
      getStmt().jimplify2(b);
      b.addLabel(update_label());	
      for (int i=0; i < getNumUpdateStmt(); i++)
        getUpdateStmt(i).jimplify2(b);
      b.setLine(this);
      b.add(b.newGotoStmt(cond_label(), this));
    }
    if(canCompleteNormally()) {
      b.addLabel(end_label());
    }
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 213

    public ForStmt() {
        super();

        setChild(new List(), 0);
        setChild(new Opt(), 1);
        setChild(new List(), 2);

    }

    // Declared in java.ast at line 13


    // Declared in java.ast line 213
    public ForStmt(List<Stmt> p0, Opt<Expr> p1, List<Stmt> p2, Stmt p3) {
        setChild(p0, 0);
        setChild(p1, 1);
        setChild(p2, 2);
        setChild(p3, 3);
    }

    // Declared in java.ast at line 20


  protected int numChildren() {
    return 4;
  }

    // Declared in java.ast at line 23

    public boolean mayHaveRewrite() {
        return true;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 213
    public void setInitStmtList(List<Stmt> list) {
        setChild(list, 0);
    }

    // Declared in java.ast at line 6


    public int getNumInitStmt() {
        return getInitStmtList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Stmt getInitStmt(int i) {
        return (Stmt)getInitStmtList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addInitStmt(Stmt node) {
        List<Stmt> list = (parent == null || state == null) ? getInitStmtListNoTransform() : getInitStmtList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addInitStmtNoTransform(Stmt node) {
        List<Stmt> list = getInitStmtListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setInitStmt(Stmt node, int i) {
        List<Stmt> list = getInitStmtList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Stmt> getInitStmts() {
        return getInitStmtList();
    }

    // Declared in java.ast at line 31

    public List<Stmt> getInitStmtsNoTransform() {
        return getInitStmtListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Stmt> getInitStmtList() {
        List<Stmt> list = (List<Stmt>)getChild(0);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Stmt> getInitStmtListNoTransform() {
        return (List<Stmt>)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 213
    public void setConditionOpt(Opt<Expr> opt) {
        setChild(opt, 1);
    }

    // Declared in java.ast at line 6


    public boolean hasCondition() {
        return getConditionOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Expr getCondition() {
        return (Expr)getConditionOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setCondition(Expr node) {
        getConditionOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getConditionOpt() {
        return (Opt<Expr>)getChild(1);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Expr> getConditionOptNoTransform() {
        return (Opt<Expr>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 213
    public void setUpdateStmtList(List<Stmt> list) {
        setChild(list, 2);
    }

    // Declared in java.ast at line 6


    public int getNumUpdateStmt() {
        return getUpdateStmtList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Stmt getUpdateStmt(int i) {
        return (Stmt)getUpdateStmtList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addUpdateStmt(Stmt node) {
        List<Stmt> list = (parent == null || state == null) ? getUpdateStmtListNoTransform() : getUpdateStmtList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addUpdateStmtNoTransform(Stmt node) {
        List<Stmt> list = getUpdateStmtListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setUpdateStmt(Stmt node, int i) {
        List<Stmt> list = getUpdateStmtList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<Stmt> getUpdateStmts() {
        return getUpdateStmtList();
    }

    // Declared in java.ast at line 31

    public List<Stmt> getUpdateStmtsNoTransform() {
        return getUpdateStmtListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<Stmt> getUpdateStmtList() {
        List<Stmt> list = (List<Stmt>)getChild(2);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<Stmt> getUpdateStmtListNoTransform() {
        return (List<Stmt>)getChildNoTransform(2);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 213
    public void setStmt(Stmt node) {
        setChild(node, 3);
    }

    // Declared in java.ast at line 5

    public Stmt getStmt() {
        return (Stmt)getChild(3);
    }

    // Declared in java.ast at line 9


    public Stmt getStmtNoTransform() {
        return (Stmt)getChildNoTransform(3);
    }

    protected java.util.Map targetOf_ContinueStmt_values;
    // Declared in BranchTarget.jrag at line 72
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

    private boolean targetOf_compute(ContinueStmt stmt) {  return !stmt.hasLabel();  }

    protected java.util.Map targetOf_BreakStmt_values;
    // Declared in BranchTarget.jrag at line 80
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

    // Declared in DefiniteAssignment.jrag at line 613
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
    if(!(!hasCondition() || getCondition().isDAafterFalse(v)))
      return false;
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(!stmt.isDAafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }

    // Declared in DefiniteAssignment.jrag at line 626
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterInitialization(Variable v) {
        ASTNode$State state = state();
        boolean isDAafterInitialization_Variable_value = isDAafterInitialization_compute(v);
        return isDAafterInitialization_Variable_value;
    }

    private boolean isDAafterInitialization_compute(Variable v) {  return getNumInitStmt() == 0 ? isDAbefore(v) : getInitStmt(getNumInitStmt()-1).isDAafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 1100
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
    if(!isDUbeforeCondition(v)) // start a circular evaluation here
      return false;
    if(!(!hasCondition() || getCondition().isDUafterFalse(v))) {
      return false;
    }
    for(Iterator iter = targetBreaks().iterator(); iter.hasNext(); ) {
      BreakStmt stmt = (BreakStmt)iter.next();
      if(!stmt.isDUafterReachedFinallyBlocks(v))
        return false;
    }
    //if(!isDUafterUpdate(v))
    //  return false;
    return true;
  }

    // Declared in DefiniteAssignment.jrag at line 1120
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterInit(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterInit_Variable_value = isDUafterInit_compute(v);
        return isDUafterInit_Variable_value;
    }

    private boolean isDUafterInit_compute(Variable v) {  return getNumInitStmt() == 0 ? isDUbefore(v) : getInitStmt(getNumInitStmt()-1).isDUafter(v);  }

    protected java.util.Map isDUbeforeCondition_Variable_values;
    // Declared in DefiniteAssignment.jrag at line 1122
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUbeforeCondition(Variable v) {
        Object _parameters = v;
if(isDUbeforeCondition_Variable_values == null) isDUbeforeCondition_Variable_values = new java.util.HashMap(4);
        ASTNode$State.CircularValue _value;
        if(isDUbeforeCondition_Variable_values.containsKey(_parameters)) {
            Object _o = isDUbeforeCondition_Variable_values.get(_parameters);
            if(!(_o instanceof ASTNode$State.CircularValue)) {
                return ((Boolean)_o).booleanValue();
            }
            else
                _value = (ASTNode$State.CircularValue)_o;
        }
        else {
            _value = new ASTNode$State.CircularValue();
            isDUbeforeCondition_Variable_values.put(_parameters, _value);
            _value.value = Boolean.valueOf(true);
        }
        ASTNode$State state = state();
        if (!state.IN_CIRCLE) {
            state.IN_CIRCLE = true;
            int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
            boolean new_isDUbeforeCondition_Variable_value;
            do {
                _value.visited = new Integer(state.CIRCLE_INDEX);
                state.CHANGE = false;
                new_isDUbeforeCondition_Variable_value = isDUbeforeCondition_compute(v);
                if (new_isDUbeforeCondition_Variable_value!=((Boolean)_value.value).booleanValue()) {
                    state.CHANGE = true;
                    _value.value = Boolean.valueOf(new_isDUbeforeCondition_Variable_value);
                }
                state.CIRCLE_INDEX++;
            } while (state.CHANGE);
            if(isFinal && num == state().boundariesCrossed)
{
                isDUbeforeCondition_Variable_values.put(_parameters, new_isDUbeforeCondition_Variable_value);
            }
            else {
                isDUbeforeCondition_Variable_values.remove(_parameters);
            state.RESET_CYCLE = true;
            isDUbeforeCondition_compute(v);
            state.RESET_CYCLE = false;
            }
            state.IN_CIRCLE = false; 
            return new_isDUbeforeCondition_Variable_value;
        }
        if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
            _value.visited = new Integer(state.CIRCLE_INDEX);
            boolean new_isDUbeforeCondition_Variable_value = isDUbeforeCondition_compute(v);
            if (state.RESET_CYCLE) {
                isDUbeforeCondition_Variable_values.remove(_parameters);
            }
            else if (new_isDUbeforeCondition_Variable_value!=((Boolean)_value.value).booleanValue()) {
                state.CHANGE = true;
                _value.value = new_isDUbeforeCondition_Variable_value;
            }
            return new_isDUbeforeCondition_Variable_value;
        }
        return ((Boolean)_value.value).booleanValue();
    }

    private boolean isDUbeforeCondition_compute(Variable v) {
    if(!isDUafterInit(v))
      return false;
    else if(!isDUafterUpdate(v))
      return false;
    return true;
  }

    // Declared in DefiniteAssignment.jrag at line 1133
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterUpdate(Variable v) {
        ASTNode$State state = state();
        boolean isDUafterUpdate_Variable_value = isDUafterUpdate_compute(v);
        return isDUafterUpdate_Variable_value;
    }

    private boolean isDUafterUpdate_compute(Variable v) {
    if(!isDUbeforeCondition(v)) // start a circular evaluation here
      return false;
    if(getNumUpdateStmt() > 0)
      return getUpdateStmt(getNumUpdateStmt()-1).isDUafter(v);
    if(!getStmt().isDUafter(v))
      return false;
    for(Iterator iter = targetContinues().iterator(); iter.hasNext(); ) {
      ContinueStmt stmt = (ContinueStmt)iter.next();
      if(!stmt.isDUafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }

    protected java.util.Map localLookup_String_values;
    // Declared in LookupVariable.jrag at line 91
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet localLookup(String name) {
        Object _parameters = name;
if(localLookup_String_values == null) localLookup_String_values = new java.util.HashMap(4);
        if(localLookup_String_values.containsKey(_parameters)) {
            return (SimpleSet)localLookup_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet localLookup_String_value = localLookup_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            localLookup_String_values.put(_parameters, localLookup_String_value);
        return localLookup_String_value;
    }

    private SimpleSet localLookup_compute(String name) {
    VariableDeclaration v = localVariableDeclaration(name);
    if(v != null) return v;
    return lookupVariable(name);
  }

    protected java.util.Map localVariableDeclaration_String_values;
    // Declared in LookupVariable.jrag at line 121
 @SuppressWarnings({"unchecked", "cast"})     public VariableDeclaration localVariableDeclaration(String name) {
        Object _parameters = name;
if(localVariableDeclaration_String_values == null) localVariableDeclaration_String_values = new java.util.HashMap(4);
        if(localVariableDeclaration_String_values.containsKey(_parameters)) {
            return (VariableDeclaration)localVariableDeclaration_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        VariableDeclaration localVariableDeclaration_String_value = localVariableDeclaration_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            localVariableDeclaration_String_values.put(_parameters, localVariableDeclaration_String_value);
        return localVariableDeclaration_String_value;
    }

    private VariableDeclaration localVariableDeclaration_compute(String name) {
    for(int i = 0; i < getNumInitStmt(); i++)
      if(getInitStmt(i).declaresVariable(name))
        return (VariableDeclaration)getInitStmt(i);
    return null;
  }

    // Declared in NameCheck.jrag at line 397
 @SuppressWarnings({"unchecked", "cast"})     public boolean continueLabel() {
        ASTNode$State state = state();
        boolean continueLabel_value = continueLabel_compute();
        return continueLabel_value;
    }

    private boolean continueLabel_compute() {  return true;  }

    // Declared in UnreachableStatements.jrag at line 102
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

    private boolean canCompleteNormally_compute() {  return reachable() && hasCondition() && (!getCondition().isConstant() || !getCondition().isTrue()) || reachableBreak();  }

    // Declared in BooleanExpressions.jrag at line 35
 @SuppressWarnings({"unchecked", "cast"})     public boolean definesLabel() {
        ASTNode$State state = state();
        boolean definesLabel_value = definesLabel_compute();
        return definesLabel_value;
    }

    private boolean definesLabel_compute() {  return true;  }

    protected boolean cond_label_computed = false;
    protected soot.jimple.Stmt cond_label_value;
    // Declared in Statements.jrag at line 174
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt cond_label() {
        if(cond_label_computed) {
            return cond_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        cond_label_value = cond_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            cond_label_computed = true;
        return cond_label_value;
    }

    private soot.jimple.Stmt cond_label_compute() {  return newLabel();  }

    protected boolean begin_label_computed = false;
    protected soot.jimple.Stmt begin_label_value;
    // Declared in Statements.jrag at line 175
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt begin_label() {
        if(begin_label_computed) {
            return begin_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        begin_label_value = begin_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            begin_label_computed = true;
        return begin_label_value;
    }

    private soot.jimple.Stmt begin_label_compute() {  return newLabel();  }

    protected boolean update_label_computed = false;
    protected soot.jimple.Stmt update_label_value;
    // Declared in Statements.jrag at line 176
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt update_label() {
        if(update_label_computed) {
            return update_label_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        update_label_value = update_label_compute();
        if(isFinal && num == state().boundariesCrossed)
            update_label_computed = true;
        return update_label_value;
    }

    private soot.jimple.Stmt update_label_compute() {  return newLabel();  }

    protected boolean end_label_computed = false;
    protected soot.jimple.Stmt end_label_value;
    // Declared in Statements.jrag at line 177
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

    // Declared in Statements.jrag at line 202
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt break_label() {
        ASTNode$State state = state();
        soot.jimple.Stmt break_label_value = break_label_compute();
        return break_label_value;
    }

    private soot.jimple.Stmt break_label_compute() {  return end_label();  }

    // Declared in Statements.jrag at line 227
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt continue_label() {
        ASTNode$State state = state();
        soot.jimple.Stmt continue_label_value = continue_label_compute();
        return continue_label_value;
    }

    private soot.jimple.Stmt continue_label_compute() {  return update_label();  }

    protected java.util.Map lookupVariable_String_values;
    // Declared in LookupVariable.jrag at line 18
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

    // Declared in DefiniteAssignment.jrag at line 637
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getUpdateStmtListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    if(!getStmt().isDAafter(v))
      return false;
    for(Iterator iter = targetContinues().iterator(); iter.hasNext(); ) {
      ContinueStmt stmt = (ContinueStmt)iter.next();
      if(!stmt.isDAafterReachedFinallyBlocks(v))
        return false;
    }
    return true;
  }
}
        if(caller == getStmtNoTransform()){
    if(hasCondition() && getCondition().isDAafterTrue(v))
      return true;
    if(!hasCondition() && isDAafterInitialization(v))
      return true;
    return false;
  }
        if(caller == getConditionOptNoTransform()) {
            return isDAafterInitialization(v);
        }
        if(caller == getInitStmtListNoTransform()) {
      int i = caller.getIndexOfChild(child);
            return i == 0 ? isDAbefore(v) : getInitStmt(i-1).isDAafter(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in DefiniteAssignment.jrag at line 1149
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getUpdateStmtListNoTransform()) { 
   int i = caller.getIndexOfChild(child);
{
    if(!isDUbeforeCondition(v)) // start a circular evaluation here
      return false;
    if(i == 0) {
      if(!getStmt().isDUafter(v))
        return false;
      for(Iterator iter = targetContinues().iterator(); iter.hasNext(); ) {
        ContinueStmt stmt = (ContinueStmt)iter.next();
        if(!stmt.isDUafterReachedFinallyBlocks(v))
          return false;
      }
      return true;
    }
    else
      return getUpdateStmt(i-1).isDUafter(v);
  }
}
        if(caller == getStmtNoTransform()) {
            return isDUbeforeCondition(v) && (hasCondition() ?
    getCondition().isDUafterTrue(v) : isDUafterInit(v));
        }
        if(caller == getConditionOptNoTransform()) {
            return isDUbeforeCondition(v);
        }
        if(caller == getInitStmtListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return childIndex == 0 ? isDUbefore(v) : getInitStmt(childIndex-1).isDUafter(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

    // Declared in LookupVariable.jrag at line 90
    public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
        if(caller == getStmtNoTransform()) {
            return localLookup(name);
        }
        if(caller == getUpdateStmtListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return localLookup(name);
        }
        if(caller == getConditionOptNoTransform()) {
            return localLookup(name);
        }
        if(caller == getInitStmtListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return localLookup(name);
        }
        return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }

    // Declared in NameCheck.jrag at line 294
    public VariableScope Define_VariableScope_outerScope(ASTNode caller, ASTNode child) {
        if(caller == getStmtNoTransform()) {
            return this;
        }
        if(caller == getInitStmtListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return this;
        }
        return getParent().Define_VariableScope_outerScope(this, caller);
    }

    // Declared in NameCheck.jrag at line 365
    public boolean Define_boolean_insideLoop(ASTNode caller, ASTNode child) {
        if(caller == getStmtNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_insideLoop(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 103
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getStmtNoTransform()) {
            return reachable() && (!hasCondition() || (!getCondition().isConstant() || !getCondition().isFalse()));
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 149
    public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
        if(caller == getStmtNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reportUnreachable(this, caller);
    }

    // Declared in BooleanExpressions.jrag at line 44
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_false_label(ASTNode caller, ASTNode child) {
        if(caller == getConditionOptNoTransform()) {
            return end_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_false_label(this, caller);
    }

    // Declared in BooleanExpressions.jrag at line 45
    public soot.jimple.Stmt Define_soot_jimple_Stmt_condition_true_label(ASTNode caller, ASTNode child) {
        if(caller == getConditionOptNoTransform()) {
            return begin_label();
        }
        return getParent().Define_soot_jimple_Stmt_condition_true_label(this, caller);
    }

public ASTNode rewriteTo() {
    // Declared in DefiniteAssignment.jrag at line 1167
    if(!hasCondition()) {
        state().duringDefiniteAssignment++;
        ASTNode result = rewriteRule0();
        state().duringDefiniteAssignment--;
        return result;
    }

    return super.rewriteTo();
}

    // Declared in DefiniteAssignment.jrag at line 1167
    private ForStmt rewriteRule0() {
{
      setCondition(new BooleanLiteral("true"));
      return this;
    }    }
}
