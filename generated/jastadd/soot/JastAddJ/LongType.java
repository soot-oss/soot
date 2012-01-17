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
 * @declaredat java.ast:56
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
        LongType node = (LongType)clone();
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
  public LongType fullCopy() {
    LongType res = (LongType)copy();
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:843
   */
  public void toString(StringBuffer s) {
		s.append("long");
	}
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public LongType() {
    super();

    setChild(new Opt(), 1);
    setChild(new List(), 2);

  }
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  public LongType(Modifiers p0, String p1, Opt<Access> p2, List<BodyDecl> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
  }
  /**
   * @ast method 
   * @declaredat java.ast:15
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
   * @declaredat java.ast:24
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:30
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Getter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * @ast method 
   * @declaredat java.ast:8
   */
  public void setID(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setID is only valid for String lexemes");
    tokenString_ID = (String)symbol.value;
    IDstart = symbol.getStart();
    IDend = symbol.getEnd();
  }
  /**
   * Getter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for SuperClassAccessOpt
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setSuperClassAccessOpt(Opt<Access> opt) {
    setChild(opt, 1);
  }
  /**
   * Does this node have a SuperClassAccess child?
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public boolean hasSuperClassAccess() {
    return getSuperClassAccessOpt().getNumChild() != 0;
  }
  /**
   * Getter for optional child SuperClassAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getSuperClassAccess() {
    return (Access)getSuperClassAccessOpt().getChild(0);
  }
  /**
   * Setter for optional child SuperClassAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void setSuperClassAccess(Access node) {
    getSuperClassAccessOpt().setChild(node, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:37
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Access> getSuperClassAccessOpt() {
    return (Opt<Access>)getChild(1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Opt<Access> getSuperClassAccessOptNoTransform() {
    return (Opt<Access>)getChildNoTransform(1);
  }
  /**
   * Setter for BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 2);
  }
  /**
   * @return number of children in BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumBodyDecl() {
    return getBodyDeclList().getNumChild();
  }
  /**
   * Getter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl getBodyDecl(int i) {
    return (BodyDecl)getBodyDeclList().getChild(i);
  }
  /**
   * Add element to list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addBodyDecl(BodyDecl node) {
    List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addBodyDeclNoTransform(BodyDecl node) {
    List<BodyDecl> list = getBodyDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setBodyDecl(BodyDecl node, int i) {
    List<BodyDecl> list = getBodyDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<BodyDecl> getBodyDecls() {
    return getBodyDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<BodyDecl> getBodyDeclsNoTransform() {
    return getBodyDeclListNoTransform();
  }
  /**
   * Getter for list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclList() {
    List<BodyDecl> list = (List<BodyDecl>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclListNoTransform() {
    return (List<BodyDecl>)getChildNoTransform(2);
  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:314
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant cast(Constant c) {
      ASTNode$State state = state();
    Constant cast_Constant_value = cast_compute(c);
    return cast_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant cast_compute(Constant c) {  return Constant.create(c.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:325
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant plus(Constant c) {
      ASTNode$State state = state();
    Constant plus_Constant_value = plus_compute(c);
    return plus_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant plus_compute(Constant c) {  return c;  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:334
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant minus(Constant c) {
      ASTNode$State state = state();
    Constant minus_Constant_value = minus_compute(c);
    return minus_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant minus_compute(Constant c) {  return Constant.create(-c.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:343
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant bitNot(Constant c) {
      ASTNode$State state = state();
    Constant bitNot_Constant_value = bitNot_compute(c);
    return bitNot_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant bitNot_compute(Constant c) {  return Constant.create(~c.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:350
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant mul(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant mul_Constant_Constant_value = mul_compute(c1, c2);
    return mul_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant mul_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() * c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:359
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant div(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant div_Constant_Constant_value = div_compute(c1, c2);
    return div_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant div_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() / c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:368
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant mod(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant mod_Constant_Constant_value = mod_compute(c1, c2);
    return mod_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant mod_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() % c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:377
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant add(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant add_Constant_Constant_value = add_compute(c1, c2);
    return add_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant add_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() + c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:387
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant sub(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant sub_Constant_Constant_value = sub_compute(c1, c2);
    return sub_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant sub_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() - c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:396
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant lshift(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant lshift_Constant_Constant_value = lshift_compute(c1, c2);
    return lshift_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant lshift_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() << c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:403
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant rshift(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant rshift_Constant_Constant_value = rshift_compute(c1, c2);
    return rshift_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant rshift_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() >> c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:410
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant urshift(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant urshift_Constant_Constant_value = urshift_compute(c1, c2);
    return urshift_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant urshift_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() >>> c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:417
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant andBitwise(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant andBitwise_Constant_Constant_value = andBitwise_compute(c1, c2);
    return andBitwise_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant andBitwise_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() & c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:425
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant xorBitwise(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant xorBitwise_Constant_Constant_value = xorBitwise_compute(c1, c2);
    return xorBitwise_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant xorBitwise_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() ^ c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:433
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant orBitwise(Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant orBitwise_Constant_Constant_value = orBitwise_compute(c1, c2);
    return orBitwise_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant orBitwise_compute(Constant c1, Constant c2) {  return Constant.create(c1.longValue() | c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:441
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Constant questionColon(Constant cond, Constant c1, Constant c2) {
      ASTNode$State state = state();
    Constant questionColon_Constant_Constant_Constant_value = questionColon_compute(cond, c1, c2);
    return questionColon_Constant_Constant_Constant_value;
  }
  /**
   * @apilevel internal
   */
  private Constant questionColon_compute(Constant cond, Constant c1, Constant c2) {  return Constant.create(cond.booleanValue() ? c1.longValue() : c2.longValue());  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:545
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean eqIsTrue(Expr left, Expr right) {
      ASTNode$State state = state();
    boolean eqIsTrue_Expr_Expr_value = eqIsTrue_compute(left, right);
    return eqIsTrue_Expr_Expr_value;
  }
  /**
   * @apilevel internal
   */
  private boolean eqIsTrue_compute(Expr left, Expr right) {  return left.constant().longValue() == right.constant().longValue();  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:553
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean ltIsTrue(Expr left, Expr right) {
      ASTNode$State state = state();
    boolean ltIsTrue_Expr_Expr_value = ltIsTrue_compute(left, right);
    return ltIsTrue_Expr_Expr_value;
  }
  /**
   * @apilevel internal
   */
  private boolean ltIsTrue_compute(Expr left, Expr right) {  return left.constant().longValue() < right.constant().longValue();  }
  /**
   * @attribute syn
   * @aspect ConstantExpression
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ConstantExpression.jrag:559
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean leIsTrue(Expr left, Expr right) {
      ASTNode$State state = state();
    boolean leIsTrue_Expr_Expr_value = leIsTrue_compute(left, right);
    return leIsTrue_Expr_Expr_value;
  }
  /**
   * @apilevel internal
   */
  private boolean leIsTrue_compute(Expr left, Expr right) {  return left.constant().longValue() <= right.constant().longValue();  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:424
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean assignableToInt() {
      ASTNode$State state = state();
    boolean assignableToInt_value = assignableToInt_compute();
    return assignableToInt_value;
  }
  /**
   * @apilevel internal
   */
  private boolean assignableToInt_compute() {  return false;  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:198
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isLong() {
      ASTNode$State state = state();
    boolean isLong_value = isLong_compute();
    return isLong_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isLong_compute() {  return true;  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:51
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String primitiveClassName() {
      ASTNode$State state = state();
    String primitiveClassName_value = primitiveClassName_compute();
    return primitiveClassName_value;
  }
  /**
   * @apilevel internal
   */
  private String primitiveClassName_compute() {  return "Long";  }
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
