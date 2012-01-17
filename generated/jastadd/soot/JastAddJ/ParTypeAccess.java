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
 * @declaredat Generics.ast:13
 */
public class ParTypeAccess extends Access implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    type_computed = false;
    type_value = null;
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
  public ParTypeAccess clone() throws CloneNotSupportedException {
    ParTypeAccess node = (ParTypeAccess)super.clone();
    node.type_computed = false;
    node.type_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParTypeAccess copy() {
      try {
        ParTypeAccess node = (ParTypeAccess)clone();
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
  public ParTypeAccess fullCopy() {
    ParTypeAccess res = (ParTypeAccess)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect GenericsTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:307
   */
  public boolean isRaw() {
    return false;
  }
  /**
   * @ast method 
   * @aspect GenericsTypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:415
   */
  public void typeCheck() {
    super.typeCheck();
    if(!genericDecl().isUnknown()) {
      TypeDecl type = type();
      if(!genericDecl().isGenericType()) {
        error(genericDecl().typeName() + " is not a generic type but used as one in " + this);
      }
      else if(!type.isRawType() && type.isNestedType() && type.enclosingType().isRawType())
        error("Can not access a member type of a raw type as a parameterized type");
      else {
        GenericTypeDecl decl = (GenericTypeDecl)genericDecl();
        GenericTypeDecl original = (GenericTypeDecl)decl.original();
        if(original.getNumTypeParameter() != getNumTypeArgument()) {
          error(decl.typeName() + " takes " + original.getNumTypeParameter() + " type parameters, not " + getNumTypeArgument() + " as used in " + this);
        }
        else {
          ParTypeDecl typeDecl = (ParTypeDecl)type();
          for(int i = 0; i < getNumTypeArgument(); i++) {
            if(!getTypeArgument(i).type().instanceOf(original.getTypeParameter(i))) {
              error("type argument " + i + " is of type " + getTypeArgument(i).type().typeName() 
                  + " which is not a subtype of " + original.getTypeParameter(i).typeName());
            }
          }
        }
      }
    }
  }
  /**
   * @ast method 
   * @aspect GenericsPrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsPrettyPrint.jrag:23
   */
  public void toString(StringBuffer s) {
    getTypeAccess().toString(s);
    s.append("<");
    for(int i = 0; i < getNumTypeArgument(); i++) {
      if(i != 0)
        s.append(", ");
      getTypeArgument(i).toString(s);
    }
    s.append(">");
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:1
   */
  public ParTypeAccess() {
    super();

    setChild(new List(), 1);

  }
  /**
   * @ast method 
   * @declaredat Generics.ast:8
   */
  public ParTypeAccess(Access p0, List<Access> p1) {
    setChild(p0, 0);
    setChild(p1, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:15
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Generics.ast:21
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setTypeAccess(Access node) {
    setChild(node, 0);
  }
  /**
   * Getter for TypeAccess
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:12
   */
  public Access getTypeAccess() {
    return (Access)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:18
   */
  public Access getTypeAccessNoTransform() {
    return (Access)getChildNoTransform(0);
  }
  /**
   * Setter for TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setTypeArgumentList(List<Access> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:12
   */
  public int getNumTypeArgument() {
    return getTypeArgumentList().getNumChild();
  }
  /**
   * Getter for child in list TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getTypeArgument(int i) {
    return (Access)getTypeArgumentList().getChild(i);
  }
  /**
   * Add element to list TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:27
   */
  public void addTypeArgument(Access node) {
    List<Access> list = (parent == null || state == null) ? getTypeArgumentListNoTransform() : getTypeArgumentList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:34
   */
  public void addTypeArgumentNoTransform(Access node) {
    List<Access> list = getTypeArgumentListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:42
   */
  public void setTypeArgument(Access node, int i) {
    List<Access> list = getTypeArgumentList();
    list.setChild(node, i);
  }
  /**
   * Getter for TypeArgument list.
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:50
   */
  public List<Access> getTypeArguments() {
    return getTypeArgumentList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:56
   */
  public List<Access> getTypeArgumentsNoTransform() {
    return getTypeArgumentListNoTransform();
  }
  /**
   * Getter for list TypeArgumentList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeArgumentList() {
    List<Access> list = (List<Access>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeArgumentListNoTransform() {
    return (List<Access>)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect GenericsTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:242
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Expr unqualifiedScope() {
      ASTNode$State state = state();
    Expr unqualifiedScope_value = unqualifiedScope_compute();
    return unqualifiedScope_value;
  }
  /**
   * @apilevel internal
   */
  private Expr unqualifiedScope_compute() {  return getParent() instanceof Access ? ((Access)getParent()).unqualifiedScope() : super.unqualifiedScope();  }
  /**
   * @apilevel internal
   */
  protected boolean type_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl type_value;
  /**
   * @attribute syn
   * @aspect GenericsTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:245
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl type() {
    if(type_computed) {
      return type_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    type_value = type_compute();
if(isFinal && num == state().boundariesCrossed) type_computed = true;
    return type_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl type_compute() {
    TypeDecl typeDecl = genericDecl();
    if(typeDecl instanceof GenericTypeDecl) {
      // use signature in lookup for types that are used in extends and implements clauses
      if(unqualifiedScope().getParent().getParent() instanceof TypeDecl)
        return ((GenericTypeDecl)typeDecl).lookupParTypeDecl(this);
      ArrayList args = new ArrayList();
      for(int i = 0; i < getNumTypeArgument(); i++)
        args.add(getTypeArgument(i).type());
      return ((GenericTypeDecl)typeDecl).lookupParTypeDecl(args);
    }
    return typeDecl;
  }
  /**
   * @attribute syn
   * @aspect GenericsTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:258
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl genericDecl() {
      ASTNode$State state = state();
    TypeDecl genericDecl_value = genericDecl_compute();
    return genericDecl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl genericDecl_compute() {  return getTypeAccess().type();  }
  /**
   * @attribute syn
   * @aspect GenericsTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:259
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isTypeAccess() {
      ASTNode$State state = state();
    boolean isTypeAccess_value = isTypeAccess_compute();
    return isTypeAccess_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isTypeAccess_compute() {  return true;  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:243
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getTypeArgumentListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return unqualifiedScope().lookupType(name);
    }
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
