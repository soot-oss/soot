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
import soot.coffi.ClassFile;
import soot.coffi.method_info;
import soot.coffi.CONSTANT_Utf8_info;
import soot.tagkit.SourceFileTag;
import soot.coffi.CoffiMethodSource;
/**
 * @production ConstCase : {@link Case} ::= <span class="component">Value:{@link Expr}</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:204
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
      ConstCase node = (ConstCase) clone();
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
  public ConstCase fullCopy() {
    ConstCase tree = (ConstCase) copy();
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
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:406
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
   * 
   */
  public ConstCase() {
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
    children = new ASTNode[1];
  }
  /**
   * @ast method 
   * 
   */
  public ConstCase(Expr p0) {
    setChild(p0, 0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 1;
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
   * Replaces the Value child.
   * @param node The new node to replace the Value child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setValue(Expr node) {
    setChild(node, 0);
  }
  /**
   * Retrieves the Value child.
   * @return The current node used as the Value child.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public Expr getValue() {
    return (Expr)getChild(0);
  }
  /**
   * Retrieves the Value child.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The current node used as the Value child.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public Expr getValueNoTransform() {
    return (Expr)getChildNoTransform(0);
  }
  /**
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:503
   */
    public void refined_Enums_ConstCase_typeCheck() {
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
	 * <p>Improve the type checking error messages given for case labels.
	 * @ast method 
   * @aspect StringsInSwitch
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/StringsInSwitch.jrag:68
   */
    public void typeCheck() {
		boolean isEnumConstant = getValue().isEnumConstant();
		TypeDecl switchType = switchType();
		TypeDecl type = getValue().type();
		if (switchType.isEnumDecl() && !isEnumConstant)
			error("Unqualified enumeration constant required");
		if (!type.assignConversionTo(switchType, getValue()))
			error("Case label has incompatible type "+switchType.name()+
					", expected type compatible with "+type.name());
		if (!getValue().isConstant() && !getValue().type().isUnknown() &&
				!isEnumConstant) 
			error("Case label must have constant expression");
	}
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:432
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
   * @ast method 
   * @aspect Enums
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:517
   */
  private boolean refined_Enums_ConstCase_constValue_Case(Case c)
{
    if(switchType().isEnumDecl()) {
      if(!(c instanceof ConstCase) || !getValue().isConstant())
        return false;
      return getValue().varDecl() == ((ConstCase)c).getValue().varDecl();
    }
    else
      return refined_NameCheck_ConstCase_constValue_Case(c);
  }
  /**
   * @attribute syn
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:431
   */
  public boolean constValue(Case c) {
    ASTNode$State state = state();
    try {
		if (isDefaultCase() || c.isDefaultCase())
			return isDefaultCase() && c.isDefaultCase();

		Expr myValue = getValue();
		Expr otherValue = ((ConstCase) c).getValue();
		TypeDecl myType = myValue.type();
		TypeDecl otherType = otherValue.type();
		if (myType.isString() || otherType.isString()) {
			if (!myType.isString() || !otherType.isString())
				return false;
			if (!myValue.isConstant() || !otherValue.isConstant())
				return false;
			return myValue.constant().stringValue().equals(
					otherValue.constant().stringValue());
		}

		return refined_Enums_ConstCase_constValue_Case(c);
	}
    finally {
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Enums.jrag:497
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getValueNoTransform()) {
      return switchType().isEnumDecl() ? switchType().memberFields(name) : lookupVariable(name);
    }
    else {      return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
