/* This file was generated with JastAdd2 (http://jastadd.org) version R20130212 (r1031) */
package soot.JastAddJ;

import java.util.HashSet;
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
import soot.tagkit.SourceFileTag;
/**
 * @production LongType : {@link IntegralType};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:59
 */
public class LongType extends IntegralType implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    boxed_computed = false;
    boxed_value = null;
    jvmName_computed = false;
    jvmName_value = null;
    getSootType_computed = false;
    getSootType_value = null;
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
  public LongType clone() throws CloneNotSupportedException {
    LongType node = (LongType)super.clone();
    node.boxed_computed = false;
    node.boxed_value = null;
    node.jvmName_computed = false;
    node.jvmName_value = null;
    node.getSootType_computed = false;
    node.getSootType_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public LongType copy() {
    try {
      LongType node = (LongType) clone();
      node.parent = null;
      if(children != null)
        node.children = (ASTNode[]) children.clone();
      return node;
    } catch (CloneNotSupportedException e) {
      throw new Error("Error: clone not supported for " +
        getClass().getName());
    }
  }
  /**
   * Create a deep copy of the AST subtree at this node.
   * The copy is dangling, i.e. has no parent.
   * @return dangling copy of the subtree at this node
   * @apilevel low-level
   */
  @SuppressWarnings({"unchecked", "cast"})
  public LongType fullCopy() {
    LongType tree = (LongType) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
        ASTNode child = (ASTNode) children[i];
        if(child != null) {
          child = child.fullCopy();
          tree.setChild(child, i);
        }
      }
    }
    return tree;
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:844
   */
  public void toString(StringBuffer s) {
		s.append("long");
	}
  /**
   * @ast method 
   * 
   */
  public LongType() {
    super();


  }
  /**
   * Initializes the child array to the correct size.
   * Initializes List and Opt nta children.
   * @apilevel internal
   * @ast method
   * @ast method 
   * 
   */
  public void init$Children() {
    children = new ASTNode[3];
    setChild(new Opt(), 1);
    setChild(new List(), 2);
  }
  /**
   * @ast method 
   * 
   */
  public LongType(Modifiers p0, String p1, Opt<Access> p2, List<BodyDecl> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
  }
  /**
   * @ast method 
   * 
   */
  public LongType(Modifiers p0, beaver.Symbol p1, Opt<Access> p2, List<BodyDecl> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Replaces the Modifiers child.
   * @param node The new node to replace the Modifiers child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Modifiers child.
   * @return The current node used as the Modifiers child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * Retrieves the Modifiers child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Modifiers child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Replaces the lexeme ID.
   * @param value The new value for the lexeme ID.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * JastAdd-internal setter for lexeme ID using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme ID.
   * @return The value for the lexeme ID.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Replaces the optional node for the SuperClassAccess child. This is the {@code Opt} node containing the child SuperClassAccess, not the actual child!
   * @param opt The new node to be used as the optional node for the SuperClassAccess child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void setSuperClassAccessOpt(Opt<Access> opt) {
    setChild(opt, 1);
  }
  /**
   * Check whether the optional SuperClassAccess child exists.
   * @return {@code true} if the optional SuperClassAccess child exists, {@code false} if it does not.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public boolean hasSuperClassAccess() {
    return getSuperClassAccessOpt().getNumChild() != 0;
  }
  /**
   * Retrieves the (optional) SuperClassAccess child.
   * @return The SuperClassAccess child, if it exists. Returns {@code null} otherwise.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getSuperClassAccess() {
    return (Access)getSuperClassAccessOpt().getChild(0);
  }
  /**
   * Replaces the (optional) SuperClassAccess child.
   * @param node The new node to be used as the SuperClassAccess child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setSuperClassAccess(Access node) {
    getSuperClassAccessOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Access> getSuperClassAccessOpt() {
    return (Opt<Access>)getChild(1);
  }
  /**
   * Retrieves the optional node for child SuperClassAccess. This is the {@code Opt} node containing the child SuperClassAccess, not the actual child!
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The optional node for child SuperClassAccess.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Access> getSuperClassAccessOptNoTransform() {
    return (Opt<Access>)getChildNoTransform(1);
  }
  /**
   * Replaces the BodyDecl list.
   * @param list The new list node to be used as the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 2);
  }
  /**
   * Retrieves the number of children in the BodyDecl list.
   * @return Number of children in the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumBodyDecl() {
    return getBodyDeclList().getNumChild();
  }
  /**
   * Retrieves the number of children in the BodyDecl list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the BodyDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumBodyDeclNoTransform() {
    return getBodyDeclListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the BodyDecl list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl getBodyDecl(int i) {
    return (BodyDecl)getBodyDeclList().getChild(i);
  }
  /**
   * Append an element to the BodyDecl list.
   * @param node The element to append to the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addBodyDecl(BodyDecl node) {
    List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addBodyDeclNoTransform(BodyDecl node) {
    List<BodyDecl> list = getBodyDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the BodyDecl list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBodyDecl(BodyDecl node, int i) {
    List<BodyDecl> list = getBodyDeclList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the BodyDecl list.
   * @return The node representing the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<BodyDecl> getBodyDecls() {
    return getBodyDeclList();
  }
  /**
   * Retrieves the BodyDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the BodyDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<BodyDecl> getBodyDeclsNoTransform() {
    return getBodyDeclListNoTransform();
  }
  /**
   * Retrieves the BodyDecl list.
   * @return The node representing the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclList() {
    List<BodyDecl> list = (List<BodyDecl>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the BodyDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the BodyDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclListNoTransform() {
    return (List<BodyDecl>)getChildNoTransform(2);
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:160
   */
  public Constant cast(Constant c) {
    ASTNode$State state = state();
    try {  return Constant.create(c.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:174
   */
  public Constant plus(Constant c) {
    ASTNode$State state = state();
    try {  return c;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:183
   */
  public Constant minus(Constant c) {
    ASTNode$State state = state();
    try {  return Constant.create(-c.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:192
   */
  public Constant bitNot(Constant c) {
    ASTNode$State state = state();
    try {  return Constant.create(~c.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:199
   */
  public Constant mul(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() * c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:208
   */
  public Constant div(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() / c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:217
   */
  public Constant mod(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() % c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:226
   */
  public Constant add(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() + c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:236
   */
  public Constant sub(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() - c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:245
   */
  public Constant lshift(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() << c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:252
   */
  public Constant rshift(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() >> c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:259
   */
  public Constant urshift(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() >>> c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:266
   */
  public Constant andBitwise(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() & c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:274
   */
  public Constant xorBitwise(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() ^ c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:282
   */
  public Constant orBitwise(Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(c1.longValue() | c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:290
   */
  public Constant questionColon(Constant cond, Constant c1, Constant c2) {
    ASTNode$State state = state();
    try {  return Constant.create(cond.booleanValue() ? c1.longValue() : c2.longValue());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:394
   */
  public boolean eqIsTrue(Expr left, Expr right) {
    ASTNode$State state = state();
    try {  return left.constant().longValue() == right.constant().longValue();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:405
   */
  public boolean ltIsTrue(Expr left, Expr right) {
    ASTNode$State state = state();
    try {  return left.constant().longValue() < right.constant().longValue();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/ConstantExpression.jrag:411
   */
  public boolean leIsTrue(Expr left, Expr right) {
    ASTNode$State state = state();
    try {  return left.constant().longValue() <= right.constant().longValue();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:427
   */
  public boolean assignableToInt() {
    ASTNode$State state = state();
    try {  return false;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:197
   */
  public boolean isLong() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean boxed_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl boxed_value;
  /**
   * @attribute syn
   * @aspect AutoBoxing
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/AutoBoxing.jrag:41
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl boxed() {
    if(boxed_computed) {
      return boxed_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boxed_value = boxed_compute();
      if(isFinal && num == state().boundariesCrossed) boxed_computed = true;
    return boxed_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl boxed_compute() {  return lookupType("java.lang", "Long");  }
  /**
   * @apilevel internal
   */
  protected boolean jvmName_computed = false;
  /**
   * @apilevel internal
   */
  protected String jvmName_value;
  /**
   * @attribute syn
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:39
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String jvmName() {
    if(jvmName_computed) {
      return jvmName_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    jvmName_value = jvmName_compute();
      if(isFinal && num == state().boundariesCrossed) jvmName_computed = true;
    return jvmName_value;
  }
  /**
   * @apilevel internal
   */
  private String jvmName_compute() {  return "J";  }
  /**
   * @attribute syn
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:44
   */
  public String primitiveClassName() {
    ASTNode$State state = state();
    try {  return "Long";  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean getSootType_computed = false;
  /**
   * @apilevel internal
   */
  protected Type getSootType_value;
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:51
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Type getSootType() {
    if(getSootType_computed) {
      return getSootType_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getSootType_value = getSootType_compute();
      if(isFinal && num == state().boundariesCrossed) getSootType_computed = true;
    return getSootType_value;
  }
  /**
   * @apilevel internal
   */
  private Type getSootType_compute() {  return soot.LongType.v();  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
