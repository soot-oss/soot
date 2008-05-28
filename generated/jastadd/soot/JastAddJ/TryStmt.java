
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.coffi.CoffiMethodSource;



public class TryStmt extends Stmt implements Cloneable, FinallyHost {
    public void flushCache() {
        super.flushCache();
        branches_computed = false;
        branches_value = null;
        branchesFromFinally_computed = false;
        branchesFromFinally_value = null;
        targetBranches_computed = false;
        targetBranches_value = null;
        escapedBranches_computed = false;
        escapedBranches_value = null;
        isDAafter_Variable_values = null;
        isDUbefore_Variable_visited = new java.util.HashMap(4);
        isDUafter_Variable_values = null;
        reachableThrow_CatchClause_values = null;
        canCompleteNormally_computed = false;
        label_begin_computed = false;
        label_begin_value = null;
        label_block_end_computed = false;
        label_block_end_value = null;
        label_end_computed = false;
        label_end_value = null;
        label_finally_computed = false;
        label_finally_value = null;
        label_finally_block_computed = false;
        label_finally_block_value = null;
        label_exception_handler_computed = false;
        label_exception_handler_value = null;
        label_catch_end_computed = false;
        label_catch_end_value = null;
        exceptionRanges_computed = false;
        exceptionRanges_value = null;
        handlesException_TypeDecl_values = null;
        typeError_computed = false;
        typeError_value = null;
        typeRuntimeException_computed = false;
        typeRuntimeException_value = null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public TryStmt clone() throws CloneNotSupportedException {
        TryStmt node = (TryStmt)super.clone();
        node.branches_computed = false;
        node.branches_value = null;
        node.branchesFromFinally_computed = false;
        node.branchesFromFinally_value = null;
        node.targetBranches_computed = false;
        node.targetBranches_value = null;
        node.escapedBranches_computed = false;
        node.escapedBranches_value = null;
        node.isDAafter_Variable_values = null;
        node.isDUbefore_Variable_visited = new java.util.HashMap(4);
        node.isDUafter_Variable_values = null;
        node.reachableThrow_CatchClause_values = null;
        node.canCompleteNormally_computed = false;
        node.label_begin_computed = false;
        node.label_begin_value = null;
        node.label_block_end_computed = false;
        node.label_block_end_value = null;
        node.label_end_computed = false;
        node.label_end_value = null;
        node.label_finally_computed = false;
        node.label_finally_value = null;
        node.label_finally_block_computed = false;
        node.label_finally_block_value = null;
        node.label_exception_handler_computed = false;
        node.label_exception_handler_value = null;
        node.label_catch_end_computed = false;
        node.label_catch_end_value = null;
        node.exceptionRanges_computed = false;
        node.exceptionRanges_value = null;
        node.handlesException_TypeDecl_values = null;
        node.typeError_computed = false;
        node.typeError_value = null;
        node.typeRuntimeException_computed = false;
        node.typeRuntimeException_value = null;
        node.in$Circle(false);
        node.is$Final(false);
    return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public TryStmt copy() {
      try {
          TryStmt node = (TryStmt)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public TryStmt fullCopy() {
        TryStmt res = (TryStmt)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BranchTarget.jrag at line 61

  public void collectBranches(Collection c) {
    c.addAll(escapedBranches());
  }

    // Declared in BranchTarget.jrag at line 162

  public Stmt branchTarget(Stmt branchStmt) {
    if(targetBranches().contains(branchStmt))
      return this;
    return super.branchTarget(branchStmt);
  }

    // Declared in BranchTarget.jrag at line 200

  public void collectFinally(Stmt branchStmt, ArrayList list) {
    if(hasFinally() && !branchesFromFinally().contains(branchStmt))
      list.add(this);
    if(targetBranches().contains(branchStmt))
      return;
    super.collectFinally(branchStmt, list);
  }

    // Declared in ExceptionHandling.jrag at line 203


  protected boolean reachedException(TypeDecl type) {
    boolean found = false;
    // found is true if the exception type is caught by a catch clause
    for(int i = 0; i < getNumCatchClause() && !found; i++)
      if(getCatchClause(i).handles(type))
        found = true;
    // if an exception is thrown in the block and the exception is not caught and
    // either there is no finally block or the finally block can complete normally
    if(!found && (!hasFinally() || getFinally().canCompleteNormally()) )
      if(getBlock().reachedException(type))
        return true;
    // even if the exception is caught by the catch clauses they may 
    // throw new exceptions
    for(int i = 0; i < getNumCatchClause() && found; i++)
      if(getCatchClause(i).reachedException(type))
        return true;
    return hasFinally() && getFinally().reachedException(type);
  }

    // Declared in PrettyPrint.jadd at line 712


  public void toString(StringBuffer s) {
    s.append("try ");
    getBlock().toString(s);
    for(int i = 0; i < getNumCatchClause(); i++) {
      s.append(indent());
      getCatchClause(i).toString(s);
    }
    if(hasFinally()) {
      s.append(indent());
      s.append("finally ");
      getFinally().toString(s);
    }
  }

    // Declared in Statements.jrag at line 319

  public void emitFinallyCode(Body b) {
    if(hasFinally()) {
      // Clear cached attributes to force re-evaluation of local variables
      getFinally().flushCaches();
      getFinally().jimplify2(b);
    }
  }

    // Declared in Statements.jrag at line 355


  public void jimplify2(Body b) {
    ArrayList ranges = exceptionRanges();
    b.addLabel(label_begin());
    ranges.add(label_begin());
    getBlock().jimplify2(b);
    soot.jimple.Stmt label_block_end = null;
    soot.jimple.Stmt label_end = null;
    if(getBlock().canCompleteNormally()) {
      if(hasFinally() && getNumCatchClause() != 0) {
        label_block_end = label_block_end();
        b.addLabel(label_block_end);
      }
      emitFinallyCode(b);
      b.setLine(this);
      if((!hasFinally() || getFinally().canCompleteNormally()) && (getNumCatchClause() != 0 || hasFinally()/*needsFinallyTrap()*/))
        b.add(Jimple.v().newGotoStmt(label_end = label_end()));
    }
    if(getNumCatchClause() != 0) {
      if(label_block_end == null)
        label_block_end = getCatchClause(0).label();
      ranges.add(label_block_end);
      ranges.add(label_block_end);
      for(int i = 0; i < getNumCatchClause(); i++) {
        //beginExceptionRange(b, ranges);
        getCatchClause(i).jimplify2(b);
        if(getCatchClause(i).getBlock().canCompleteNormally()) {
          b.setLine(getCatchClause(i));
          endExceptionRange(b, ranges);
          emitFinallyCode(b);
          if(!hasFinally() || getFinally().canCompleteNormally())
            b.add(Jimple.v().newGotoStmt(label_end = label_end()));
          beginExceptionRange(b, ranges);
        }
        b.setLine(getCatchClause(i));
        //endExceptionRange(b, ranges);
      }
    }
    if(hasFinally() /*&& needsFinallyTrap()*/) {
      b.addLabel(label_exception_handler());
      emitExceptionHandler(b);
      b.setLine(getFinally());
      //if(getFinally().canCompleteNormally())
      //  b.add(Jimple.v().newGotoStmt(label_end()));
    }
    if(label_end != null)
      b.addLabel(label_end);
    // createExceptionTable
    for(int i = 0; i < getNumCatchClause(); i++) {
      for(Iterator iter = ranges.iterator(); iter.hasNext(); ) {
        soot.jimple.Stmt stmtBegin = (soot.jimple.Stmt)iter.next();
        soot.jimple.Stmt stmtEnd = (soot.jimple.Stmt)iter.next();
        if(stmtBegin != stmtEnd) {
          b.addTrap(
              getCatchClause(i).getParameter().type(),
              stmtBegin,
              stmtEnd,
              getCatchClause(i).label()
          );
        }
        if(stmtEnd == label_block_end)
          break;
      }
    }
    if(hasFinally() /*&& needsFinallyTrap()*/) {
      for(Iterator iter = ranges.iterator(); iter.hasNext(); ) {
        soot.jimple.Stmt stmtBegin = (soot.jimple.Stmt)iter.next();
        soot.jimple.Stmt stmtEnd;
        if(iter.hasNext())
          stmtEnd = (soot.jimple.Stmt)iter.next();
        else
          stmtEnd = label_exception_handler();
        if(stmtBegin != stmtEnd)
          b.addTrap(typeThrowable(), stmtBegin, stmtEnd, label_exception_handler());
      }
      /*
      b.addTrap(
        typeThrowable(),
        label_begin(),
        label_exception_handler(),
        label_exception_handler()
      );
      */
    }
  }

    // Declared in Statements.jrag at line 464

 
  public void emitExceptionHandler(Body b) {
    Local l = b.newTemp(typeThrowable().getSootType());
    b.setLine(this);
    b.add(Jimple.v().newIdentityStmt(l, Jimple.v().newCaughtExceptionRef()));
    emitFinallyCode(b);
    //if(hasFinally() && getFinally().canCompleteNormally()) {
      soot.jimple.Stmt throwStmt = Jimple.v().newThrowStmt(l);
      throwStmt.addTag(new soot.tagkit.ThrowCreatedByCompilerTag());
      b.add(throwStmt);
    //}
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 222

    public TryStmt() {
        super();

        setChild(new List(), 1);
        setChild(new Opt(), 2);

    }

    // Declared in java.ast at line 12


    // Declared in java.ast line 222
    public TryStmt(Block p0, List<CatchClause> p1, Opt<Block> p2) {
        setChild(p0, 0);
        setChild(p1, 1);
        setChild(p2, 2);
    }

    // Declared in java.ast at line 18


  protected int numChildren() {
    return 3;
  }

    // Declared in java.ast at line 21

  public boolean mayHaveRewrite() { return false; }

    // Declared in java.ast at line 2
    // Declared in java.ast line 222
    public void setBlock(Block node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Block getBlock() {
        return (Block)getChild(0);
    }

    // Declared in java.ast at line 9


    public Block getBlockNoTransform() {
        return (Block)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 222
    public void setCatchClauseList(List<CatchClause> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    private int getNumCatchClause = 0;

    // Declared in java.ast at line 7

    public int getNumCatchClause() {
        return getCatchClauseList().getNumChild();
    }

    // Declared in java.ast at line 11


     @SuppressWarnings({"unchecked", "cast"})  public CatchClause getCatchClause(int i) {
        return (CatchClause)getCatchClauseList().getChild(i);
    }

    // Declared in java.ast at line 15


    public void addCatchClause(CatchClause node) {
        List<CatchClause> list = getCatchClauseList();
        list.addChild(node);
    }

    // Declared in java.ast at line 20


    public void setCatchClause(CatchClause node, int i) {
        List<CatchClause> list = getCatchClauseList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 24

    public List<CatchClause> getCatchClauses() {
        return getCatchClauseList();
    }

    // Declared in java.ast at line 27

    public List<CatchClause> getCatchClausesNoTransform() {
        return getCatchClauseListNoTransform();
    }

    // Declared in java.ast at line 31


     @SuppressWarnings({"unchecked", "cast"})  public List<CatchClause> getCatchClauseList() {
        return (List<CatchClause>)getChild(1);
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<CatchClause> getCatchClauseListNoTransform() {
        return (List<CatchClause>)getChildNoTransform(1);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 222
    public void setFinallyOpt(Opt<Block> opt) {
        setChild(opt, 2);
    }

    // Declared in java.ast at line 6


    public boolean hasFinally() {
        return getFinallyOpt().getNumChild() != 0;
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public Block getFinally() {
        return (Block)getFinallyOpt().getChild(0);
    }

    // Declared in java.ast at line 14


    public void setFinally(Block node) {
        getFinallyOpt().setChild(node, 0);
    }

    // Declared in java.ast at line 17

     @SuppressWarnings({"unchecked", "cast"})  public Opt<Block> getFinallyOpt() {
        return (Opt<Block>)getChild(2);
    }

    // Declared in java.ast at line 21


     @SuppressWarnings({"unchecked", "cast"})  public Opt<Block> getFinallyOptNoTransform() {
        return (Opt<Block>)getChildNoTransform(2);
    }

    protected boolean branches_computed = false;
    protected Collection branches_value;
    // Declared in BranchTarget.jrag at line 116
 @SuppressWarnings({"unchecked", "cast"})     public Collection branches() {
        if(branches_computed)
            return branches_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        branches_value = branches_compute();
        if(isFinal && num == boundariesCrossed)
            branches_computed = true;
        return branches_value;
    }

    private Collection branches_compute() {
    HashSet set = new HashSet();
    getBlock().collectBranches(set);
    for(int i = 0; i < getNumCatchClause(); i++)
      getCatchClause(i).collectBranches(set);
    return set;
  }

    protected boolean branchesFromFinally_computed = false;
    protected Collection branchesFromFinally_value;
    // Declared in BranchTarget.jrag at line 124
 @SuppressWarnings({"unchecked", "cast"})     public Collection branchesFromFinally() {
        if(branchesFromFinally_computed)
            return branchesFromFinally_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        branchesFromFinally_value = branchesFromFinally_compute();
        if(isFinal && num == boundariesCrossed)
            branchesFromFinally_computed = true;
        return branchesFromFinally_value;
    }

    private Collection branchesFromFinally_compute() {
    HashSet set = new HashSet();
    if(hasFinally())
      getFinally().collectBranches(set);
    return set;
  }

    protected boolean targetBranches_computed = false;
    protected Collection targetBranches_value;
    // Declared in BranchTarget.jrag at line 132
 @SuppressWarnings({"unchecked", "cast"})     public Collection targetBranches() {
        if(targetBranches_computed)
            return targetBranches_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        targetBranches_value = targetBranches_compute();
        if(isFinal && num == boundariesCrossed)
            targetBranches_computed = true;
        return targetBranches_value;
    }

    private Collection targetBranches_compute() {
    HashSet set = new HashSet();
    if(hasFinally() && !getFinally().canCompleteNormally())
      set.addAll(branches());
    return set;
  }

    protected boolean escapedBranches_computed = false;
    protected Collection escapedBranches_value;
    // Declared in BranchTarget.jrag at line 140
 @SuppressWarnings({"unchecked", "cast"})     public Collection escapedBranches() {
        if(escapedBranches_computed)
            return escapedBranches_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        escapedBranches_value = escapedBranches_compute();
        if(isFinal && num == boundariesCrossed)
            escapedBranches_computed = true;
        return escapedBranches_value;
    }

    private Collection escapedBranches_compute() {
    HashSet set = new HashSet();
    if(hasFinally())
      set.addAll(branchesFromFinally());
    if(!hasFinally() || getFinally().canCompleteNormally())
      set.addAll(branches());
    return set;
  }

    // Declared in DefiniteAssignment.jrag at line 667
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

    private boolean isDAafter_compute(Variable v) {
    // 16.2.15 4th bullet
    if(!hasFinally()) {
      if(!getBlock().isDAafter(v))
        return false;
      for(int i = 0; i < getNumCatchClause(); i++)
        if(!getCatchClause(i).getBlock().isDAafter(v))
          return false;
      return true;
    }
    else {
      // 16.2.15 5th bullet
      if(getFinally().isDAafter(v))
        return true;
      if(!getBlock().isDAafter(v))
        return false;
      for(int i = 0; i < getNumCatchClause(); i++)
        if(!getCatchClause(i).getBlock().isDAafter(v))
          return false;
      return true;
    }
  }

    // Declared in DefiniteAssignment.jrag at line 918
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUafterFinally(Variable v) {
        boolean isDUafterFinally_Variable_value = isDUafterFinally_compute(v);
        return isDUafterFinally_Variable_value;
    }

    private boolean isDUafterFinally_compute(Variable v) {  return getFinally().isDUafter(v);  }

    // Declared in DefiniteAssignment.jrag at line 921
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDAafterFinally(Variable v) {
        boolean isDAafterFinally_Variable_value = isDAafterFinally_compute(v);
        return isDAafterFinally_Variable_value;
    }

    private boolean isDAafterFinally_compute(Variable v) {  return getFinally().isDAafter(v);  }

    protected java.util.Map isDUbefore_Variable_visited;
    protected java.util.Set isDUbefore_Variable_computed = new java.util.HashSet(4);
    protected java.util.Set isDUbefore_Variable_initialized = new java.util.HashSet(4);
    protected java.util.Map isDUbefore_Variable_values = new java.util.HashMap(4);
 @SuppressWarnings({"unchecked", "cast"})     public boolean isDUbefore(Variable v) {
        Object _parameters = v;
if(isDUbefore_Variable_visited == null) isDUbefore_Variable_visited = new java.util.HashMap(4);
if(isDUbefore_Variable_values == null) isDUbefore_Variable_values = new java.util.HashMap(4);
        if(isDUbefore_Variable_computed.contains(_parameters))
            return ((Boolean)isDUbefore_Variable_values.get(_parameters)).booleanValue();
        if (!isDUbefore_Variable_initialized.contains(_parameters)) {
            isDUbefore_Variable_initialized.add(_parameters);
            isDUbefore_Variable_values.put(_parameters, Boolean.valueOf(true));
        }
        if (!IN_CIRCLE) {
            IN_CIRCLE = true;
            int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
            CIRCLE_INDEX = 1;
            boolean new_isDUbefore_Variable_value;
            do {
                isDUbefore_Variable_visited.put(_parameters, new Integer(CIRCLE_INDEX));
                CHANGE = false;
                new_isDUbefore_Variable_value = isDUbefore_compute(v);
                if (new_isDUbefore_Variable_value!=((Boolean)isDUbefore_Variable_values.get(_parameters)).booleanValue())
                    CHANGE = true;
                isDUbefore_Variable_values.put(_parameters, Boolean.valueOf(new_isDUbefore_Variable_value));
                CIRCLE_INDEX++;
            } while (CHANGE);
            if(isFinal && num == boundariesCrossed)
{
            isDUbefore_Variable_computed.add(_parameters);
            }
            else {
            RESET_CYCLE = true;
            isDUbefore_compute(v);
            RESET_CYCLE = false;
            isDUbefore_Variable_computed.remove(_parameters);
            isDUbefore_Variable_initialized.remove(_parameters);
            }
            IN_CIRCLE = false; 
            return new_isDUbefore_Variable_value;
        }
        if(!new Integer(CIRCLE_INDEX).equals(isDUbefore_Variable_visited.get(_parameters))) {
            isDUbefore_Variable_visited.put(_parameters, new Integer(CIRCLE_INDEX));
            if (RESET_CYCLE) {
                isDUbefore_Variable_computed.remove(_parameters);
                isDUbefore_Variable_initialized.remove(_parameters);
                return ((Boolean)isDUbefore_Variable_values.get(_parameters)).booleanValue();
            }
            boolean new_isDUbefore_Variable_value = isDUbefore_compute(v);
            if (new_isDUbefore_Variable_value!=((Boolean)isDUbefore_Variable_values.get(_parameters)).booleanValue())
                CHANGE = true;
            isDUbefore_Variable_values.put(_parameters, Boolean.valueOf(new_isDUbefore_Variable_value));
            return new_isDUbefore_Variable_value;
        }
        return ((Boolean)isDUbefore_Variable_values.get(_parameters)).booleanValue();
    }

    private boolean isDUbefore_compute(Variable v) {  return super.isDUbefore(v);  }

    // Declared in DefiniteAssignment.jrag at line 1225
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
    // 16.2.14 4th bullet
    if(!hasFinally()) {
      if(!getBlock().isDUafter(v))
        return false;
      for(int i = 0; i < getNumCatchClause(); i++)
        if(!getCatchClause(i).getBlock().isDUafter(v))
          return false;
      return true;
    }
    else
      return getFinally().isDUafter(v);
  }

    protected java.util.Map reachableThrow_CatchClause_values;
    // Declared in ExceptionHandling.jrag at line 193
 @SuppressWarnings({"unchecked", "cast"})     public boolean reachableThrow(CatchClause c) {
        Object _parameters = c;
if(reachableThrow_CatchClause_values == null) reachableThrow_CatchClause_values = new java.util.HashMap(4);
        if(reachableThrow_CatchClause_values.containsKey(_parameters))
            return ((Boolean)reachableThrow_CatchClause_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean reachableThrow_CatchClause_value = reachableThrow_compute(c);
        if(isFinal && num == boundariesCrossed)
            reachableThrow_CatchClause_values.put(_parameters, Boolean.valueOf(reachableThrow_CatchClause_value));
        return reachableThrow_CatchClause_value;
    }

    private boolean reachableThrow_compute(CatchClause c) {  return getBlock().reachedException(c.getParameter().type());  }

    // Declared in UnreachableStatements.jrag at line 113
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

    private boolean canCompleteNormally_compute() {
     boolean anyCatchClauseCompleteNormally = false;
     for(int i = 0; i < getNumCatchClause() && !anyCatchClauseCompleteNormally; i++)
       anyCatchClauseCompleteNormally = getCatchClause(i).getBlock().canCompleteNormally();
     return (getBlock().canCompleteNormally() || anyCatchClauseCompleteNormally) &&
       (!hasFinally() || getFinally().canCompleteNormally());
  }

    // Declared in Statements.jrag at line 207
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt break_label() {
        soot.jimple.Stmt break_label_value = break_label_compute();
        return break_label_value;
    }

    private soot.jimple.Stmt break_label_compute() {  return label_finally();  }

    // Declared in Statements.jrag at line 231
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt continue_label() {
        soot.jimple.Stmt continue_label_value = continue_label_compute();
        return continue_label_value;
    }

    private soot.jimple.Stmt continue_label_compute() {  return label_finally();  }

    protected boolean label_begin_computed = false;
    protected soot.jimple.Stmt label_begin_value;
    // Declared in Statements.jrag at line 336
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_begin() {
        if(label_begin_computed)
            return label_begin_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_begin_value = label_begin_compute();
        if(isFinal && num == boundariesCrossed)
            label_begin_computed = true;
        return label_begin_value;
    }

    private soot.jimple.Stmt label_begin_compute() {  return newLabel();  }

    protected boolean label_block_end_computed = false;
    protected soot.jimple.Stmt label_block_end_value;
    // Declared in Statements.jrag at line 337
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_block_end() {
        if(label_block_end_computed)
            return label_block_end_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_block_end_value = label_block_end_compute();
        if(isFinal && num == boundariesCrossed)
            label_block_end_computed = true;
        return label_block_end_value;
    }

    private soot.jimple.Stmt label_block_end_compute() {  return newLabel();  }

    protected boolean label_end_computed = false;
    protected soot.jimple.Stmt label_end_value;
    // Declared in Statements.jrag at line 338
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_end() {
        if(label_end_computed)
            return label_end_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_end_value = label_end_compute();
        if(isFinal && num == boundariesCrossed)
            label_end_computed = true;
        return label_end_value;
    }

    private soot.jimple.Stmt label_end_compute() {  return newLabel();  }

    protected boolean label_finally_computed = false;
    protected soot.jimple.Stmt label_finally_value;
    // Declared in Statements.jrag at line 339
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_finally() {
        if(label_finally_computed)
            return label_finally_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_finally_value = label_finally_compute();
        if(isFinal && num == boundariesCrossed)
            label_finally_computed = true;
        return label_finally_value;
    }

    private soot.jimple.Stmt label_finally_compute() {  return newLabel();  }

    protected boolean label_finally_block_computed = false;
    protected soot.jimple.Stmt label_finally_block_value;
    // Declared in Statements.jrag at line 340
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_finally_block() {
        if(label_finally_block_computed)
            return label_finally_block_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_finally_block_value = label_finally_block_compute();
        if(isFinal && num == boundariesCrossed)
            label_finally_block_computed = true;
        return label_finally_block_value;
    }

    private soot.jimple.Stmt label_finally_block_compute() {  return newLabel();  }

    protected boolean label_exception_handler_computed = false;
    protected soot.jimple.Stmt label_exception_handler_value;
    // Declared in Statements.jrag at line 341
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_exception_handler() {
        if(label_exception_handler_computed)
            return label_exception_handler_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_exception_handler_value = label_exception_handler_compute();
        if(isFinal && num == boundariesCrossed)
            label_exception_handler_computed = true;
        return label_exception_handler_value;
    }

    private soot.jimple.Stmt label_exception_handler_compute() {  return newLabel();  }

    protected boolean label_catch_end_computed = false;
    protected soot.jimple.Stmt label_catch_end_value;
    // Declared in Statements.jrag at line 342
 @SuppressWarnings({"unchecked", "cast"})     public soot.jimple.Stmt label_catch_end() {
        if(label_catch_end_computed)
            return label_catch_end_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        label_catch_end_value = label_catch_end_compute();
        if(isFinal && num == boundariesCrossed)
            label_catch_end_computed = true;
        return label_catch_end_value;
    }

    private soot.jimple.Stmt label_catch_end_compute() {  return newLabel();  }

    // Declared in Statements.jrag at line 344
 @SuppressWarnings({"unchecked", "cast"})     public boolean needsFinallyTrap() {
        boolean needsFinallyTrap_value = needsFinallyTrap_compute();
        return needsFinallyTrap_value;
    }

    private boolean needsFinallyTrap_compute() {  return getNumCatchClause() != 0 || enclosedByExceptionHandler();  }

    protected boolean exceptionRanges_computed = false;
    protected ArrayList exceptionRanges_value;
    // Declared in Statements.jrag at line 448
 @SuppressWarnings({"unchecked", "cast"})     public ArrayList exceptionRanges() {
        if(exceptionRanges_computed)
            return exceptionRanges_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        exceptionRanges_value = exceptionRanges_compute();
        if(isFinal && num == boundariesCrossed)
            exceptionRanges_computed = true;
        return exceptionRanges_value;
    }

    private ArrayList exceptionRanges_compute() {  return new ArrayList();  }

    protected java.util.Map handlesException_TypeDecl_values;
    // Declared in ExceptionHandling.jrag at line 35
 @SuppressWarnings({"unchecked", "cast"})     public boolean handlesException(TypeDecl exceptionType) {
        Object _parameters = exceptionType;
if(handlesException_TypeDecl_values == null) handlesException_TypeDecl_values = new java.util.HashMap(4);
        if(handlesException_TypeDecl_values.containsKey(_parameters))
            return ((Boolean)handlesException_TypeDecl_values.get(_parameters)).booleanValue();
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        boolean handlesException_TypeDecl_value = getParent().Define_boolean_handlesException(this, null, exceptionType);
        if(isFinal && num == boundariesCrossed)
            handlesException_TypeDecl_values.put(_parameters, Boolean.valueOf(handlesException_TypeDecl_value));
        return handlesException_TypeDecl_value;
    }

    protected boolean typeError_computed = false;
    protected TypeDecl typeError_value;
    // Declared in UnreachableStatements.jrag at line 136
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeError() {
        if(typeError_computed)
            return typeError_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        typeError_value = getParent().Define_TypeDecl_typeError(this, null);
        if(isFinal && num == boundariesCrossed)
            typeError_computed = true;
        return typeError_value;
    }

    protected boolean typeRuntimeException_computed = false;
    protected TypeDecl typeRuntimeException_value;
    // Declared in UnreachableStatements.jrag at line 137
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeRuntimeException() {
        if(typeRuntimeException_computed)
            return typeRuntimeException_value;
        int num = boundariesCrossed;
        boolean isFinal = this.is$Final();
        typeRuntimeException_value = getParent().Define_TypeDecl_typeRuntimeException(this, null);
        if(isFinal && num == boundariesCrossed)
            typeRuntimeException_computed = true;
        return typeRuntimeException_value;
    }

    // Declared in Statements.jrag at line 345
 @SuppressWarnings({"unchecked", "cast"})     public boolean enclosedByExceptionHandler() {
        boolean enclosedByExceptionHandler_value = getParent().Define_boolean_enclosedByExceptionHandler(this, null);
        return enclosedByExceptionHandler_value;
    }

    // Declared in Statements.jrag at line 461
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeThrowable() {
        TypeDecl typeThrowable_value = getParent().Define_TypeDecl_typeThrowable(this, null);
        return typeThrowable_value;
    }

    // Declared in Statements.jrag at line 444
    public ArrayList Define_ArrayList_exceptionRanges(ASTNode caller, ASTNode child) {
        if(caller == getCatchClauseListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return exceptionRanges();
        }
        if(caller == getBlockNoTransform()) {
            return exceptionRanges();
        }
        return getParent().Define_ArrayList_exceptionRanges(this, caller);
    }

    // Declared in ExceptionHandling.jrag at line 179
    public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
        if(caller == getBlockNoTransform()){
    for(int i = 0; i < getNumCatchClause(); i++)
      if(getCatchClause(i).handles(exceptionType))
        return true;
    if(hasFinally() && !getFinally().canCompleteNormally())
      return true;
    return handlesException(exceptionType);
  }
        if(caller == getCatchClauseListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    if(hasFinally() && !getFinally().canCompleteNormally())
      return true;
    return handlesException(exceptionType);
  }
}
        return getParent().Define_boolean_handlesException(this, caller, exceptionType);
    }

    // Declared in UnreachableStatements.jrag at line 154
    public boolean Define_boolean_reportUnreachable(ASTNode caller, ASTNode child) {
        if(caller == getFinallyOptNoTransform()) {
            return reachable();
        }
        if(caller == getCatchClauseListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return reachable();
        }
        if(caller == getBlockNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reportUnreachable(this, caller);
    }

    // Declared in UnreachableStatements.jrag at line 121
    public boolean Define_boolean_reachable(ASTNode caller, ASTNode child) {
        if(caller == getFinallyOptNoTransform()) {
            return reachable();
        }
        if(caller == getBlockNoTransform()) {
            return reachable();
        }
        return getParent().Define_boolean_reachable(this, caller);
    }

    // Declared in Statements.jrag at line 350
    public boolean Define_boolean_enclosedByExceptionHandler(ASTNode caller, ASTNode child) {
        if(caller == getBlockNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_enclosedByExceptionHandler(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 666
    public boolean Define_boolean_isDAbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getFinallyOptNoTransform()) {
            return isDAbefore(v);
        }
        if(caller == getCatchClauseListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
            return getBlock().isDAbefore(v);
        }
        if(caller == getBlockNoTransform()) {
            return isDAbefore(v);
        }
        return getParent().Define_boolean_isDAbefore(this, caller, v);
    }

    // Declared in UnreachableStatements.jrag at line 125
    public boolean Define_boolean_reachableCatchClause(ASTNode caller, ASTNode child) {
        if(caller == getCatchClauseListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    TypeDecl type = getCatchClause(childIndex).getParameter().type();
    for(int i = 0; i < childIndex; i++)
      if(getCatchClause(i).handles(type))
        return false;
    if(reachableThrow(getCatchClause(childIndex)))
      return true;
    if(type.mayCatch(typeError()) || type.mayCatch(typeRuntimeException()))
      return true;
    return false;
  }
}
        return getParent().Define_boolean_reachableCatchClause(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 1216
    public boolean Define_boolean_isDUbefore(ASTNode caller, ASTNode child, Variable v) {
        if(caller == getFinallyOptNoTransform()){
    if(!getBlock().isDUeverywhere(v))
      return false;
    for(int i = 0; i < getNumCatchClause(); i++)
      if(!getCatchClause(i).getBlock().unassignedEverywhere(v, this))
        return false;
    return true;
  }
        if(caller == getCatchClauseListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    if(!getBlock().isDUafter(v))
      return false;
    if(!getBlock().isDUeverywhere(v))
      return false;
    return true;
  }
}
        if(caller == getBlockNoTransform()) {
            return isDUbefore(v);
        }
        return getParent().Define_boolean_isDUbefore(this, caller, v);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
