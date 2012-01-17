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
 * @declaredat Generics.ast:23
 */
public class WildcardType extends AbstractWildcardType implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    subtype_TypeDecl_values = null;
    containedIn_TypeDecl_values = null;
    instanceOf_TypeDecl_values = null;
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
  public WildcardType clone() throws CloneNotSupportedException {
    WildcardType node = (WildcardType)super.clone();
    node.subtype_TypeDecl_values = null;
    node.containedIn_TypeDecl_values = null;
    node.instanceOf_TypeDecl_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public WildcardType copy() {
      try {
        WildcardType node = (WildcardType)clone();
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
  public WildcardType fullCopy() {
    WildcardType res = (WildcardType)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @declaredat Generics.ast:1
   */
  public WildcardType() {
    super();

    setChild(new List(), 1);

  }
  /**
   * @ast method 
   * @declaredat Generics.ast:8
   */
  public WildcardType(Modifiers p0, String p1, List<BodyDecl> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:13
   */
  public WildcardType(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:21
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Generics.ast:27
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
   * Setter for BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 1);
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
    List<BodyDecl> list = (List<BodyDecl>)getChild(1);
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
    return (List<BodyDecl>)getChildNoTransform(1);
  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:567
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean sameSignature(Access a) {
      ASTNode$State state = state();
    boolean sameSignature_Access_value = sameSignature_compute(a);
    return sameSignature_Access_value;
  }
  /**
   * @apilevel internal
   */
  private boolean sameSignature_compute(Access a) {
    if(a instanceof Wildcard)
      return true;
    return super.sameSignature(a);
  }
  protected java.util.Map subtype_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:45
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
  private boolean subtype_compute(TypeDecl type) {  return type.supertypeWildcard(this);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:47
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeWildcard(WildcardType type) {
      ASTNode$State state = state();
    boolean supertypeWildcard_WildcardType_value = supertypeWildcard_compute(type);
    return supertypeWildcard_WildcardType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeWildcard_compute(WildcardType type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:58
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeWildcardExtends(WildcardExtendsType type) {
      ASTNode$State state = state();
    boolean supertypeWildcardExtends_WildcardExtendsType_value = supertypeWildcardExtends_compute(type);
    return supertypeWildcardExtends_WildcardExtendsType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeWildcardExtends_compute(WildcardExtendsType type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:67
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeWildcardSuper(WildcardSuperType type) {
      ASTNode$State state = state();
    boolean supertypeWildcardSuper_WildcardSuperType_value = supertypeWildcardSuper_compute(type);
    return supertypeWildcardSuper_WildcardSuperType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeWildcardSuper_compute(WildcardSuperType type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:75
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeClassDecl(ClassDecl type) {
      ASTNode$State state = state();
    boolean supertypeClassDecl_ClassDecl_value = supertypeClassDecl_compute(type);
    return supertypeClassDecl_ClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeClassDecl_compute(ClassDecl type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:76
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeInterfaceDecl(InterfaceDecl type) {
      ASTNode$State state = state();
    boolean supertypeInterfaceDecl_InterfaceDecl_value = supertypeInterfaceDecl_compute(type);
    return supertypeInterfaceDecl_InterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:77
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeParClassDecl(ParClassDecl type) {
      ASTNode$State state = state();
    boolean supertypeParClassDecl_ParClassDecl_value = supertypeParClassDecl_compute(type);
    return supertypeParClassDecl_ParClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeParClassDecl_compute(ParClassDecl type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:78
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeParInterfaceDecl(ParInterfaceDecl type) {
      ASTNode$State state = state();
    boolean supertypeParInterfaceDecl_ParInterfaceDecl_value = supertypeParInterfaceDecl_compute(type);
    return supertypeParInterfaceDecl_ParInterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeParInterfaceDecl_compute(ParInterfaceDecl type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:79
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeRawClassDecl(RawClassDecl type) {
      ASTNode$State state = state();
    boolean supertypeRawClassDecl_RawClassDecl_value = supertypeRawClassDecl_compute(type);
    return supertypeRawClassDecl_RawClassDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeRawClassDecl_compute(RawClassDecl type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:80
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeRawInterfaceDecl(RawInterfaceDecl type) {
      ASTNode$State state = state();
    boolean supertypeRawInterfaceDecl_RawInterfaceDecl_value = supertypeRawInterfaceDecl_compute(type);
    return supertypeRawInterfaceDecl_RawInterfaceDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeRawInterfaceDecl_compute(RawInterfaceDecl type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:81
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeTypeVariable(TypeVariable type) {
      ASTNode$State state = state();
    boolean supertypeTypeVariable_TypeVariable_value = supertypeTypeVariable_compute(type);
    return supertypeTypeVariable_TypeVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeTypeVariable_compute(TypeVariable type) {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:82
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean supertypeArrayDecl(ArrayDecl type) {
      ASTNode$State state = state();
    boolean supertypeArrayDecl_ArrayDecl_value = supertypeArrayDecl_compute(type);
    return supertypeArrayDecl_ArrayDecl_value;
  }
  /**
   * @apilevel internal
   */
  private boolean supertypeArrayDecl_compute(ArrayDecl type) {  return true;  }
  protected java.util.Map containedIn_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:169
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean containedIn(TypeDecl type) {
    Object _parameters = type;
    if(containedIn_TypeDecl_values == null) containedIn_TypeDecl_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(containedIn_TypeDecl_values.containsKey(_parameters)) {
      Object _o = containedIn_TypeDecl_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return ((Boolean)_o).booleanValue();
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      containedIn_TypeDecl_values.put(_parameters, _value);
      _value.value = Boolean.valueOf(true);
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      boolean new_containedIn_TypeDecl_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_containedIn_TypeDecl_value = containedIn_compute(type);
        if (new_containedIn_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
          state.CHANGE = true;
          _value.value = Boolean.valueOf(new_containedIn_TypeDecl_value);
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
        containedIn_TypeDecl_values.put(_parameters, new_containedIn_TypeDecl_value);
      }
      else {
        containedIn_TypeDecl_values.remove(_parameters);
      state.RESET_CYCLE = true;
      containedIn_compute(type);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_containedIn_TypeDecl_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      boolean new_containedIn_TypeDecl_value = containedIn_compute(type);
      if (state.RESET_CYCLE) {
        containedIn_TypeDecl_values.remove(_parameters);
      }
      else if (new_containedIn_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
        state.CHANGE = true;
        _value.value = new_containedIn_TypeDecl_value;
      }
      return new_containedIn_TypeDecl_value;
    }
    return ((Boolean)_value.value).booleanValue();
  }
  /**
   * @apilevel internal
   */
  private boolean containedIn_compute(TypeDecl type) {
    if(type == this)
      return true;
    else if(type instanceof WildcardExtendsType)
      return typeObject().subtype(((WildcardExtendsType)type).extendsType());
    else 
      return false;
  }
  protected java.util.Map instanceOf_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:401
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
  private boolean instanceOf_compute(TypeDecl type) {  return subtype(type);  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
