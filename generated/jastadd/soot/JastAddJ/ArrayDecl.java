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
 * @production ArrayDecl : {@link ClassDecl};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:68
 */
public class ArrayDecl extends ClassDecl implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    accessibleFrom_TypeDecl_values = null;
    dimension_computed = false;
    elementType_computed = false;
    elementType_value = null;
    fullName_computed = false;
    fullName_value = null;
    typeName_computed = false;
    typeName_value = null;
    castingConversionTo_TypeDecl_values = null;
    instanceOf_TypeDecl_values = null;
    involvesTypeParameters_visited = -1;
    involvesTypeParameters_computed = false;
    involvesTypeParameters_initialized = false;
    erasure_computed = false;
    erasure_value = null;
    usesTypeVariable_visited = -1;
    usesTypeVariable_computed = false;
    usesTypeVariable_initialized = false;
    subtype_TypeDecl_values = null;
    jvmName_computed = false;
    jvmName_value = null;
    getSootClassDecl_computed = false;
    getSootClassDecl_value = null;
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
  public ArrayDecl clone() throws CloneNotSupportedException {
    ArrayDecl node = (ArrayDecl)super.clone();
    node.accessibleFrom_TypeDecl_values = null;
    node.dimension_computed = false;
    node.elementType_computed = false;
    node.elementType_value = null;
    node.fullName_computed = false;
    node.fullName_value = null;
    node.typeName_computed = false;
    node.typeName_value = null;
    node.castingConversionTo_TypeDecl_values = null;
    node.instanceOf_TypeDecl_values = null;
    node.involvesTypeParameters_visited = -1;
    node.involvesTypeParameters_computed = false;
    node.involvesTypeParameters_initialized = false;
    node.erasure_computed = false;
    node.erasure_value = null;
    node.usesTypeVariable_visited = -1;
    node.usesTypeVariable_computed = false;
    node.usesTypeVariable_initialized = false;
    node.subtype_TypeDecl_values = null;
    node.jvmName_computed = false;
    node.jvmName_value = null;
    node.getSootClassDecl_computed = false;
    node.getSootClassDecl_value = null;
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
  public ArrayDecl copy() {
    try {
      ArrayDecl node = (ArrayDecl) clone();
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
  public ArrayDecl fullCopy() {
    ArrayDecl tree = (ArrayDecl) copy();
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
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:59
   */
  public Access createQualifiedAccess() {
    return new ArrayTypeAccess(componentType().createQualifiedAccess());
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:880
   */
  public Access substitute(Parameterization parTypeDecl) {
    return new ArrayTypeAccess(componentType().substitute(parTypeDecl));
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:916
   */
  public Access substituteReturnType(Parameterization parTypeDecl) {
    return new ArrayTypeAccess(componentType().substituteReturnType(parTypeDecl));
  }
  /**
   * @ast method 
   * 
   */
  public ArrayDecl() {
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
    children = new ASTNode[4];
    setChild(new Opt(), 1);
    setChild(new List(), 2);
    setChild(new List(), 3);
  }
  /**
   * @ast method 
   * 
   */
  public ArrayDecl(Modifiers p0, String p1, Opt<Access> p2, List<Access> p3, List<BodyDecl> p4) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(p4, 3);
  }
  /**
   * @ast method 
   * 
   */
  public ArrayDecl(Modifiers p0, beaver.Symbol p1, Opt<Access> p2, List<Access> p3, List<BodyDecl> p4) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
    setChild(p4, 3);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 4;
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
   * Replaces the Implements list.
   * @param list The new list node to be used as the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setImplementsList(List<Access> list) {
    setChild(list, 2);
  }
  /**
   * Retrieves the number of children in the Implements list.
   * @return Number of children in the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumImplements() {
    return getImplementsList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Implements list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Implements list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumImplementsNoTransform() {
    return getImplementsListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Implements list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getImplements(int i) {
    return (Access)getImplementsList().getChild(i);
  }
  /**
   * Append an element to the Implements list.
   * @param node The element to append to the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addImplements(Access node) {
    List<Access> list = (parent == null || state == null) ? getImplementsListNoTransform() : getImplementsList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addImplementsNoTransform(Access node) {
    List<Access> list = getImplementsListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Implements list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setImplements(Access node, int i) {
    List<Access> list = getImplementsList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Implements list.
   * @return The node representing the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Access> getImplementss() {
    return getImplementsList();
  }
  /**
   * Retrieves the Implements list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Implements list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getImplementssNoTransform() {
    return getImplementsListNoTransform();
  }
  /**
   * Retrieves the Implements list.
   * @return The node representing the Implements list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getImplementsList() {
    List<Access> list = (List<Access>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Implements list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Implements list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getImplementsListNoTransform() {
    return (List<Access>)getChildNoTransform(2);
  }
  /**
   * Replaces the BodyDecl list.
   * @param list The new list node to be used as the BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 3);
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
    List<BodyDecl> list = (List<BodyDecl>)getChild(3);
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
    return (List<BodyDecl>)getChildNoTransform(3);
  }
  /**
   * @ast method 
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:120
   */
  private boolean refined_TypeConversion_ArrayDecl_castingConversionTo_TypeDecl(TypeDecl type)
{
    if(type.isArrayDecl()) {
      TypeDecl SC = componentType();
      TypeDecl TC = type.componentType();
      if(SC.isPrimitiveType() && TC.isPrimitiveType() && SC == TC)
        return true;
      if(SC.isReferenceType() && TC.isReferenceType()) {
        return SC.castingConversionTo(TC);
      }
      return false;
    }
    else if(type.isClassDecl()) {
      return type.isObject();
    }
    else if(type.isInterfaceDecl()) {
      return type == typeSerializable() || type == typeCloneable();
    }
    else return super.castingConversionTo(type);
  }
  protected java.util.Map accessibleFrom_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect AccessControl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/AccessControl.jrag:13
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean accessibleFrom(TypeDecl type) {
    Object _parameters = type;
    if(accessibleFrom_TypeDecl_values == null) accessibleFrom_TypeDecl_values = new java.util.HashMap(4);
    if(accessibleFrom_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)accessibleFrom_TypeDecl_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean accessibleFrom_TypeDecl_value = accessibleFrom_compute(type);
      if(isFinal && num == state().boundariesCrossed) accessibleFrom_TypeDecl_values.put(_parameters, Boolean.valueOf(accessibleFrom_TypeDecl_value));
    return accessibleFrom_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean accessibleFrom_compute(TypeDecl type) {  return elementType().accessibleFrom(type);  }
  /**
   * @apilevel internal
   */
  protected boolean dimension_computed = false;
  /**
   * @apilevel internal
   */
  protected int dimension_value;
  /**
   * @attribute syn
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:12
   */
  @SuppressWarnings({"unchecked", "cast"})
  public int dimension() {
    if(dimension_computed) {
      return dimension_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    dimension_value = dimension_compute();
      if(isFinal && num == state().boundariesCrossed) dimension_computed = true;
    return dimension_value;
  }
  /**
   * @apilevel internal
   */
  private int dimension_compute() {  return componentType().dimension() + 1;  }
  /**
   * @apilevel internal
   */
  protected boolean elementType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl elementType_value;
  /**
   * @attribute syn
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:16
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl elementType() {
    if(elementType_computed) {
      return elementType_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    elementType_value = elementType_compute();
      if(isFinal && num == state().boundariesCrossed) elementType_computed = true;
    return elementType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl elementType_compute() {  return componentType().elementType();  }
  /**
   * @attribute syn
   * @aspect TypeName
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:68
   */
  public String name() {
    ASTNode$State state = state();
    try {  return fullName();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean fullName_computed = false;
  /**
   * @apilevel internal
   */
  protected String fullName_value;
  /**
   * @attribute syn
   * @aspect Arrays
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/Arrays.jrag:54
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String fullName() {
    if(fullName_computed) {
      return fullName_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    fullName_value = fullName_compute();
      if(isFinal && num == state().boundariesCrossed) fullName_computed = true;
    return fullName_value;
  }
  /**
   * @apilevel internal
   */
  private String fullName_compute() {  return getID();  }
  /**
   * @apilevel internal
   */
  protected boolean typeName_computed = false;
  /**
   * @apilevel internal
   */
  protected String typeName_value;
  /**
   * @attribute syn
   * @aspect TypeName
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:87
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String typeName() {
    if(typeName_computed) {
      return typeName_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeName_value = typeName_compute();
      if(isFinal && num == state().boundariesCrossed) typeName_computed = true;
    return typeName_value;
  }
  /**
   * @apilevel internal
   */
  private String typeName_compute() {  return componentType().typeName() + "[]";  }
  protected java.util.Map castingConversionTo_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:96
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean castingConversionTo(TypeDecl type) {
    Object _parameters = type;
    if(castingConversionTo_TypeDecl_values == null) castingConversionTo_TypeDecl_values = new java.util.HashMap(4);
    if(castingConversionTo_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)castingConversionTo_TypeDecl_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean castingConversionTo_TypeDecl_value = castingConversionTo_compute(type);
      if(isFinal && num == state().boundariesCrossed) castingConversionTo_TypeDecl_values.put(_parameters, Boolean.valueOf(castingConversionTo_TypeDecl_value));
    return castingConversionTo_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean castingConversionTo_compute(TypeDecl type) {
    TypeDecl S = this;
    TypeDecl T = type;
    if(T instanceof TypeVariable) {
      TypeVariable t = (TypeVariable)T;
      if(!type.isReferenceType())
        return false;
      if(t.getNumTypeBound() == 0) return true;
      for(int i = 0; i < t.getNumTypeBound(); i++) {
        TypeDecl bound = t.getTypeBound(i).type();
        if(bound.isObject() || bound == typeSerializable() || bound == typeCloneable())
          return true;
        if(bound.isTypeVariable() && castingConversionTo(bound))
          return true;
        if(bound.isArrayDecl() && castingConversionTo(bound))
          return true;
      }
      return false;
    }
    else
      return refined_TypeConversion_ArrayDecl_castingConversionTo_TypeDecl(type);
  }
  /**
   * @attribute syn
   * @aspect TypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:213
   */
  public boolean isArrayDecl() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  protected java.util.Map instanceOf_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:389
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean instanceOf(TypeDecl type) {
    Object _parameters = type;
    if(instanceOf_TypeDecl_values == null) instanceOf_TypeDecl_values = new java.util.HashMap(4);
    if(instanceOf_TypeDecl_values.containsKey(_parameters)) {
      return ((Boolean)instanceOf_TypeDecl_values.get(_parameters)).booleanValue();
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    boolean instanceOf_TypeDecl_value = instanceOf_compute(type);
      if(isFinal && num == state().boundariesCrossed) instanceOf_TypeDecl_values.put(_parameters, Boolean.valueOf(instanceOf_TypeDecl_value));
    return instanceOf_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean instanceOf_compute(TypeDecl type) { return subtype(type); }
  /**
   * @attribute syn
   * @aspect TypeWideningAndIdentity
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:453
   */
  public boolean isSupertypeOfArrayDecl(ArrayDecl type) {
    ASTNode$State state = state();
    try {
    if(type.elementType().isPrimitive() && elementType().isPrimitive())
      return type.dimension() == dimension() && type.elementType() == elementType();
    return type.componentType().instanceOf(componentType());
  }
    finally {
    }
  }
  /* It is a compile-time error if the return type of a method declared in an
  annotation type is any type other than one of the following: one of the
  primitive types, String, Class and any invocation of Class, an enum type
  (\ufffd8.9), an annotation type, or an array (\ufffd10) of one of the preceding types.* @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:121
   */
  public boolean isValidAnnotationMethodReturnType() {
    ASTNode$State state = state();
    try {  return componentType().isValidAnnotationMethodReturnType();  }
    finally {
    }
  }
  /* An element type T is commensurate with an element value V if and only if one of the following conditions is true:
    * T is an array type E[] and either:
          o V is an ElementValueArrayInitializer and each ElementValueInitializer (analogous to a variable initializer in an array initializer) in V is commensurate with E. Or
          o V is an ElementValue that is commensurate with T. 
    * The type of V is assignment compatible (\ufffd5.2) with T and, furthermore:
          o If T is a primitive type or String, V is a constant expression (\ufffd15.28).
          o V is not null.
          o if T is Class, or an invocation of Class, and V is a class literal (\ufffd15.8.2).
          o If T is an enum type, and V is an enum constant. * @attribute syn
   * @aspect Annotations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Annotations.jrag:474
   */
  public boolean commensurateWith(ElementValue value) {
    ASTNode$State state = state();
    try {  return value.commensurateWithArrayDecl(this);  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected int involvesTypeParameters_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean involvesTypeParameters_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean involvesTypeParameters_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean involvesTypeParameters_value;
  /**
   * @attribute syn
   * @aspect GenericMethodsInference
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:17
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean involvesTypeParameters() {
    if(involvesTypeParameters_computed) {
      return involvesTypeParameters_value;
    }
    ASTNode$State state = state();
    if (!involvesTypeParameters_initialized) {
      involvesTypeParameters_initialized = true;
      involvesTypeParameters_value = false;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        involvesTypeParameters_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
        if (new_involvesTypeParameters_value!=involvesTypeParameters_value)
          state.CHANGE = true;
        involvesTypeParameters_value = new_involvesTypeParameters_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(isFinal && num == state().boundariesCrossed) {
      involvesTypeParameters_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      involvesTypeParameters_compute();
      state.RESET_CYCLE = false;
        involvesTypeParameters_computed = false;
        involvesTypeParameters_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return involvesTypeParameters_value;
    }
    if(involvesTypeParameters_visited != state.CIRCLE_INDEX) {
      involvesTypeParameters_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        involvesTypeParameters_computed = false;
        involvesTypeParameters_initialized = false;
        involvesTypeParameters_visited = -1;
        return involvesTypeParameters_value;
      }
      boolean new_involvesTypeParameters_value = involvesTypeParameters_compute();
      if (new_involvesTypeParameters_value!=involvesTypeParameters_value)
        state.CHANGE = true;
      involvesTypeParameters_value = new_involvesTypeParameters_value; 
      return involvesTypeParameters_value;
    }
    return involvesTypeParameters_value;
  }
  /**
   * @apilevel internal
   */
  private boolean involvesTypeParameters_compute() {  return componentType().involvesTypeParameters();  }
  /**
   * @apilevel internal
   */
  protected boolean erasure_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl erasure_value;
  /**
   * @attribute syn
   * @aspect GenericsErasure
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:353
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl erasure() {
    if(erasure_computed) {
      return erasure_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    erasure_value = erasure_compute();
      if(isFinal && num == state().boundariesCrossed) erasure_computed = true;
    return erasure_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl erasure_compute() {  return componentType().erasure().arrayType();  }
  /**
   * @apilevel internal
   */
  protected int usesTypeVariable_visited = -1;
  /**
   * @apilevel internal
   */
  protected boolean usesTypeVariable_computed = false;
  /**
   * @apilevel internal
   */
  protected boolean usesTypeVariable_initialized = false;
  /**
   * @apilevel internal
   */
  protected boolean usesTypeVariable_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1082
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean usesTypeVariable() {
    if(usesTypeVariable_computed) {
      return usesTypeVariable_value;
    }
    ASTNode$State state = state();
    if (!usesTypeVariable_initialized) {
      usesTypeVariable_initialized = true;
      usesTypeVariable_value = false;
    }
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
    int num = state.boundariesCrossed;
    boolean isFinal = this.is$Final();
      do {
        usesTypeVariable_visited = state.CIRCLE_INDEX;
        state.CHANGE = false;
        boolean new_usesTypeVariable_value = usesTypeVariable_compute();
        if (new_usesTypeVariable_value!=usesTypeVariable_value)
          state.CHANGE = true;
        usesTypeVariable_value = new_usesTypeVariable_value; 
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(isFinal && num == state().boundariesCrossed) {
      usesTypeVariable_computed = true;
      }
      else {
      state.RESET_CYCLE = true;
      usesTypeVariable_compute();
      state.RESET_CYCLE = false;
        usesTypeVariable_computed = false;
        usesTypeVariable_initialized = false;
      }
      state.IN_CIRCLE = false; 
      return usesTypeVariable_value;
    }
    if(usesTypeVariable_visited != state.CIRCLE_INDEX) {
      usesTypeVariable_visited = state.CIRCLE_INDEX;
      if (state.RESET_CYCLE) {
        usesTypeVariable_computed = false;
        usesTypeVariable_initialized = false;
        usesTypeVariable_visited = -1;
        return usesTypeVariable_value;
      }
      boolean new_usesTypeVariable_value = usesTypeVariable_compute();
      if (new_usesTypeVariable_value!=usesTypeVariable_value)
        state.CHANGE = true;
      usesTypeVariable_value = new_usesTypeVariable_value; 
      return usesTypeVariable_value;
    }
    return usesTypeVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean usesTypeVariable_compute() {  return elementType().usesTypeVariable();  }
  protected java.util.Map subtype_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:409
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean subtype(TypeDecl type) {
    Object _parameters = type;
    if(subtype_TypeDecl_values == null) subtype_TypeDecl_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(subtype_TypeDecl_values.containsKey(_parameters)) {
      Object _o = subtype_TypeDecl_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return ((Boolean)_o).booleanValue();
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      subtype_TypeDecl_values.put(_parameters, _value);
      _value.value = Boolean.valueOf(true);
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      boolean new_subtype_TypeDecl_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_subtype_TypeDecl_value = subtype_compute(type);
        if (new_subtype_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
          state.CHANGE = true;
          _value.value = Boolean.valueOf(new_subtype_TypeDecl_value);
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(isFinal && num == state().boundariesCrossed) {
        subtype_TypeDecl_values.put(_parameters, new_subtype_TypeDecl_value);
      }
      else {
        subtype_TypeDecl_values.remove(_parameters);
      state.RESET_CYCLE = true;
      subtype_compute(type);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_subtype_TypeDecl_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      boolean new_subtype_TypeDecl_value = subtype_compute(type);
      if (state.RESET_CYCLE) {
        subtype_TypeDecl_values.remove(_parameters);
      }
      else if (new_subtype_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
        state.CHANGE = true;
        _value.value = new_subtype_TypeDecl_value;
      }
      return new_subtype_TypeDecl_value;
    }
    return ((Boolean)_value.value).booleanValue();
  }
  /**
   * @apilevel internal
   */
  private boolean subtype_compute(TypeDecl type) {  return type.supertypeArrayDecl(this);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:450
   */
  public boolean supertypeArrayDecl(ArrayDecl type) {
    ASTNode$State state = state();
    try {
    if(type.elementType().isPrimitive() && elementType().isPrimitive())
      return type.dimension() == dimension() && type.elementType() == elementType();
    return type.componentType().subtype(componentType());
  }
    finally {
    }
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:26
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
  private String jvmName_compute() {
    StringBuffer dim = new StringBuffer();
    for(int i = 0; i < dimension(); i++)
      dim.append("[");
    if(elementType().isReferenceType())
      return dim.toString() + "L" + elementType().jvmName() + ";";
    else
      return dim.toString() + elementType().jvmName(); 
  }
  /**
   * @attribute syn
   * @aspect Java2Rewrites
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Java2Rewrites.jrag:57
   */
  public String referenceClassFieldName() {
    ASTNode$State state = state();
    try {  return "array" + jvmName().replace('[', '$').replace('.', '$').replace(';', ' ').trim();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean getSootClassDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected SootClass getSootClassDecl_value;
  /**
   * @attribute syn
   * @aspect EmitJimple
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:44
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SootClass getSootClassDecl() {
    if(getSootClassDecl_computed) {
      return getSootClassDecl_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getSootClassDecl_value = getSootClassDecl_compute();
      if(isFinal && num == state().boundariesCrossed) getSootClassDecl_computed = true;
    return getSootClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private SootClass getSootClassDecl_compute() {  return typeObject().getSootClassDecl();  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/JimpleBackend/EmitJimple.jrag:56
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
  private Type getSootType_compute() {  return soot.ArrayType.v(elementType().getSootType(), dimension());  }
  /**
	 * A type is reifiable if it either refers to a non-parameterized type,
	 * is a raw type, is a parameterized type with only unbound wildcard
	 * parameters or is an array type with a reifiable type parameter.
	 *
	 * @see "JLSv3 &sect;4.7"
	 * @attribute syn
   * @aspect SafeVarargs
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java7Frontend/SafeVarargs.jrag:106
   */
  public boolean isReifiable() {
    ASTNode$State state = state();
    try {  return elementType().isReifiable();  }
    finally {
    }
  }
  /**
   * @attribute inh
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:140
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeSerializable() {
    ASTNode$State state = state();
    TypeDecl typeSerializable_value = getParent().Define_TypeDecl_typeSerializable(this, null);
    return typeSerializable_value;
  }
  /**
   * @attribute inh
   * @aspect TypeConversion
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:141
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeCloneable() {
    ASTNode$State state = state();
    TypeDecl typeCloneable_value = getParent().Define_TypeDecl_typeCloneable(this, null);
    return typeCloneable_value;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
