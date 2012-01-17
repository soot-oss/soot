package soot.JastAddJ;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.io.File;
import java.util.*;
import beaver.*;
import java.util.ArrayList;
import java.util.zip.*;
import java.io.*;
import java.io.FileNotFoundException;
import java.util.Collection;
import soot.*;
import soot.util.*;
import soot.jimple.*;
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;

/**
 * @ast node
 * @declaredat java.ast:218
 */
public class AssertStmt extends Stmt implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    isDAafter_Variable_values = null;
    isDUafter_Variable_values = null;
  }
  /**
   * @apilevel internal
   */
  public void flushCollectionCache() {
    super.flushCollectionCache();
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AssertStmt clone() throws CloneNotSupportedException {
    AssertStmt node = (AssertStmt)super.clone();
    node.isDAafter_Variable_values = null;
    node.isDUafter_Variable_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AssertStmt copy() {
      try {
        AssertStmt node = (AssertStmt)clone();
        if(children != null) node.children = (ASTNode[])children.clone();
        return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
  }
  /**
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public AssertStmt fullCopy() {
    AssertStmt res = (AssertStmt)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:729
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("assert ");
    getfirst().toString(s);
    if(hasExpr()) {
      s.append(" : ");
      getExpr().toString(s);
    }
    s.append(";");
  }
  /**
   * @ast method 
   * @aspect TypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeCheck.jrag:378
   */
  public void typeCheck() {
    // 14.10
    if(!getfirst().type().isBoolean())
      error("Assert requires boolean condition");
    if(hasExpr() && getExpr().type().isVoid())
      error("The second part of an assert statement may not be void");
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:170
   */
  public void transformation() {
    super.transformation();
    // add field to hold cached result as a side-effect
    FieldDeclaration f = hostType().topLevelType().createStaticClassField(hostType().topLevelType().referenceClassFieldName());
    FieldDeclaration assertionsDisabled = hostType().createAssertionsDisabled();
    Expr condition = (Expr)getfirst().fullCopy();
    List args = new List();
    if(hasExpr())
      if(getExpr().type().isString())
        args.add(new CastExpr(new TypeAccess("java.lang", "Object"), (Expr)getExpr().fullCopy()));
      else
        args.add(getExpr().fullCopy());
    Stmt stmt = 
      new IfStmt(
        new LogNotExpr(
          new ParExpr(
            new OrLogicalExpr(
              new BoundFieldAccess(assertionsDisabled),
              condition
            )
          )
        ),
        new ThrowStmt(
          new ClassInstanceExpr(
            lookupType("java.lang", "AssertionError").createQualifiedAccess(),
            args,
            new Opt()
          )
        ),
        new Opt()
      );
     
    replace(this).with(stmt);
    stmt.transformation();
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public AssertStmt() {
    super();

    setChild(new Opt(), 1);

  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public AssertStmt(Expr p0, Opt<Expr> p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:15
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:21
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for first
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setfirst(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for first
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getfirst() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getfirstNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * Setter for ExprOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setExprOpt(Opt<Expr> opt) {
    setChild(opt, 1);
  }
  /**
   * Does this node have a Expr child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasExpr() {
    return getExprOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr getExpr() {
    return (Expr)getExprOpt().getChild(0);
  }
  /**
   * Setter for optional child Expr
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setExpr(Expr node) {
    getExprOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Expr> getExprOpt() {
    return (Opt<Expr>)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Expr> getExprOptNoTransform() {
    return (Opt<Expr>)getChildNoTransform(1);
  }
  protected java.util.Map isDAafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DA
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:418
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDAafter(Variable v) {
    Object _parameters = v;
    if(isDAafter_Variable_values == null) isDAafter_Variable_values = new java.util.HashMap(4);
    if(isDAafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDAafter_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDAafter_Variable_value = isDAafter_compute(v);
if(isFinal && num == state().boundariesCrossed) isDAafter_Variable_values.put(_parameters, Boolean.valueOf(isDAafter_Variable_value));
    return isDAafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDAafter_compute(Variable v) {  return getfirst().isDAafter(v);  }
  protected java.util.Map isDUafter_Variable_values;
  /**
   * @attribute syn
   * @aspect DU
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:865
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isDUafter(Variable v) {
    Object _parameters = v;
    if(isDUafter_Variable_values == null) isDUafter_Variable_values = new java.util.HashMap(4);
    if(isDUafter_Variable_values.containsKey(_parameters)) {
      return ((Boolean)isDUafter_Variable_values.get(_parameters)).booleanValue();
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean isDUafter_Variable_value = isDUafter_compute(v);
if(isFinal && num == state().boundariesCrossed) isDUafter_Variable_values.put(_parameters, Boolean.valueOf(isDUafter_Variable_value));
    return isDUafter_Variable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isDUafter_compute(Variable v) {  return getfirst().isDUafter(v);  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
