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
 * @declaredat java.ast:201
 */
public class ConstCase extends Case implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
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
  public ConstCase clone() throws CloneNotSupportedException {
    ConstCase node = (ConstCase)super.clone();
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ConstCase copy() {
      try {
        ConstCase node = (ConstCase)clone();
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
  public ConstCase fullCopy() {
    ConstCase res = (ConstCase)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:401
   */
  public void nameCheck() {
    if(getValue().isConstant() && bind(this) != this) {
      error("constant expression " + getValue() + " is multiply declared in two case statements");
    }
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:562
   */
  public void toString(StringBuffer s) {
    s.append(indent());
    s.append("case ");
    getValue().toString(s);
    s.append(":");
  }
  /**
   * @ast method 
   * @aspect EnumsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/EnumsCodegen.jrag:32
   */
  public void transformation() {
    if(getValue() instanceof VarAccess && getValue().varDecl() instanceof EnumConstant) {
      int i = hostType().createEnumIndex((EnumConstant)getValue().varDecl());
      setValue(new IntegerLiteral(new Integer(i).toString()));
    }
    super.transformation();
  }
  /**
   * @ast method 
   * @declaredat java.ast:1
   */
  public ConstCase() {
    super();


  }
  /**
   * @ast method 
   * @declaredat java.ast:7
   */
  public ConstCase(Expr p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:13
   */
  protected int numChildren() {
    return 1;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:19
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for Value
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setValue(Expr node) {
    setChild(node, 0);
  }
  /**
   * Getter for Value
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public Expr getValue() {
    return (Expr)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:18
   */
  public Expr getValueNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:516
   */
    public void typeCheck() {
    boolean isEnumConstant = getValue().isEnumConstant();
    if(switchType().isEnumDecl() && !isEnumConstant) {
      error("Unqualified enumeration constant required");
    } else {
      TypeDecl switchType = switchType();
      TypeDecl type = getValue().type();
      if(!type.assignConversionTo(switchType, getValue()))
        error("Constant expression must be assignable to Expression");
      if(!getValue().isConstant() && !getValue().type().isUnknown() &&
          !isEnumConstant) 
        error("Switch expression must be constant");
    }
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:427
   */
  private boolean refined_NameCheck_ConstCase_constValue_Case(Case c)
{
    if(!(c instanceof ConstCase) || !getValue().isConstant())
      return false;
    if(!getValue().type().assignableToInt() || !((ConstCase)c).getValue().type().assignableToInt())
      return false;
    return getValue().constant().intValue() == ((ConstCase)c).getValue().constant().intValue();
  }
  /**
   * @attribute syn
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:530
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean constValue(Case c) {
      ASTNode$State state = state();
    boolean constValue_Case_value = constValue_compute(c);
    return constValue_Case_value;
  }
  /**
   * @apilevel internal
   */
  private boolean constValue_compute(Case c) {
    if(switchType().isEnumDecl()) {
      if(!(c instanceof ConstCase) || !getValue().isConstant())
        return false;
      return getValue().varDecl() == ((ConstCase)c).getValue().varDecl();
    }
    else
      return refined_NameCheck_ConstCase_constValue_Case(c);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:510
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getValueNoTransform()) {
      return switchType().isEnumDecl() ? switchType().memberFields(name) : lookupVariable(name);
    }
    return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
