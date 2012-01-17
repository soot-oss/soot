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
 * @declaredat Generics.ast:15
 */
public class TypeVariable extends ReferenceType implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    toInterface_computed = false;
    toInterface_value = null;
    involvesTypeParameters_visited = -1;
    involvesTypeParameters_computed = false;
    involvesTypeParameters_initialized = false;
    memberFields_String_values = null;
    castingConversionTo_TypeDecl_values = null;
    erasure_computed = false;
    erasure_value = null;
    fullName_computed = false;
    fullName_value = null;
    lubType_computed = false;
    lubType_value = null;
    usesTypeVariable_visited = -1;
    usesTypeVariable_computed = false;
    usesTypeVariable_initialized = false;
    accessibleFrom_TypeDecl_values = null;
    typeName_computed = false;
    typeName_value = null;
    sameStructure_TypeDecl_values = null;
    subtype_TypeDecl_values = null;
    getSubstitutedTypeBound_int_TypeDecl_values = null;
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
  public TypeVariable clone() throws CloneNotSupportedException {
    TypeVariable node = (TypeVariable)super.clone();
    node.toInterface_computed = false;
    node.toInterface_value = null;
    node.involvesTypeParameters_visited = -1;
    node.involvesTypeParameters_computed = false;
    node.involvesTypeParameters_initialized = false;
    node.memberFields_String_values = null;
    node.castingConversionTo_TypeDecl_values = null;
    node.erasure_computed = false;
    node.erasure_value = null;
    node.fullName_computed = false;
    node.fullName_value = null;
    node.lubType_computed = false;
    node.lubType_value = null;
    node.usesTypeVariable_visited = -1;
    node.usesTypeVariable_computed = false;
    node.usesTypeVariable_initialized = false;
    node.accessibleFrom_TypeDecl_values = null;
    node.typeName_computed = false;
    node.typeName_value = null;
    node.sameStructure_TypeDecl_values = null;
    node.subtype_TypeDecl_values = null;
    node.getSubstitutedTypeBound_int_TypeDecl_values = null;
    node.instanceOf_TypeDecl_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeVariable copy() {
      try {
        TypeVariable node = (TypeVariable)clone();
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
  public TypeVariable fullCopy() {
    TypeVariable res = (TypeVariable)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect GenericTypeVariables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericTypeVariables.jrag:28
   */
  public void nameCheck() {
    if(extractSingleType(lookupType(name())) != this)
      error("*** Semantic Error: type variable " + name() + " is multiply declared");
  }
  /**
   * @ast method 
   * @aspect GenricTypeVariablesTypeAnalysis
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericTypeVariables.jrag:66
   */
  public void typeCheck() {
    if(!getTypeBound(0).type().isTypeVariable() && !getTypeBound(0).type().isClassDecl() && !getTypeBound(0).type().isInterfaceDecl()) {
      error("the first type bound must be either a type variable, or a class or interface type which " + 
        getTypeBound(0).type().fullName() + " is not");
    }
    for(int i = 1; i < getNumTypeBound(); i++) {
      if(!getTypeBound(i).type().isInterfaceDecl()) {
        error("type bound " + i + " must be an interface type which " + 
          getTypeBound(i).type().fullName() + " is not");
      }
    }
    HashSet typeSet = new HashSet();
    for(int i = 0; i < getNumTypeBound(); i++) {
      TypeDecl type = getTypeBound(i).type();
      TypeDecl erasure = type.erasure();
      if(typeSet.contains(erasure)) {
        if(type != erasure) {
          error("the erasure " + erasure.fullName() + " of typebound " + getTypeBound(i) + " is multiply declared in " + this);
        }
        else {
          error(type.fullName() + " is multiply declared");
        }
      }
      typeSet.add(erasure);
    }

    for(int i = 0; i < getNumTypeBound(); i++) {
      TypeDecl type = getTypeBound(i).type();
      for(Iterator iter = type.methodsIterator(); iter.hasNext(); ) {
        MethodDecl m = (MethodDecl)iter.next();
        for(int j = i+1; j < getNumTypeBound(); j++) {
          TypeDecl destType = getTypeBound(j).type();
          for(Iterator destIter = destType.memberMethods(m.name()).iterator(); destIter.hasNext(); ) {
            MethodDecl n = (MethodDecl)destIter.next();
            if(m.sameSignature(n) && m.type() != n.type()) {
              error("the two bounds, " + type.name() + " and " + destType.name() + ", in type variable " + name() + 
                " have a method " + m.signature() + " with conflicting return types " + m.type().name() + " and " + n.type().name());
            }
          }
        }
      }
    }

    
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:740
   */
  public Access substitute(Parameterization parTypeDecl) {
    if(parTypeDecl.isRawType())
      return erasure().createBoundAccess();
    return parTypeDecl.substitute(this).createBoundAccess();
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:785
   */
  public Access substituteReturnType(Parameterization parTypeDecl) {
    if(parTypeDecl.isRawType())
      return erasure().createBoundAccess();
    TypeDecl typeDecl = parTypeDecl.substitute(this);
    if(typeDecl instanceof WildcardType) {
      // the bound of this type variable
      return createBoundAccess();
      //return lubType().createBoundAccess();
      //return typeObject().createBoundAccess();
    }
    else if(typeDecl instanceof WildcardExtendsType) {
      if(typeDecl.instanceOf(this))
        return ((WildcardExtendsType)typeDecl).extendsType().createBoundAccess();
      else 
        return createBoundAccess();

      // the bound of this type variable of the bound of the wild card if it is more specific
      //return ((WildcardExtendsType)typeDecl).extendsType().createBoundAccess();
    }
    else if(typeDecl instanceof WildcardSuperType) {
      // the bound of this type variable 
      return createBoundAccess();
      //return typeObject().createBoundAccess();
    }
    return typeDecl.createBoundAccess();
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:819
   */
  public Access substituteParameterType(Parameterization parTypeDecl) {
    if(parTypeDecl.isRawType())
      return erasure().createBoundAccess();
    TypeDecl typeDecl = parTypeDecl.substitute(this);
    if(typeDecl instanceof WildcardType)
      return typeNull().createQualifiedAccess();
    else if(typeDecl instanceof WildcardExtendsType)
      return typeNull().createQualifiedAccess();
    else if(typeDecl instanceof WildcardSuperType)
      return ((WildcardSuperType)typeDecl).superType().createBoundAccess();
    return typeDecl.createBoundAccess();
  }
  /**
   * @ast method 
   * @aspect NewGenerics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1258
   */
  public Access createQualifiedAccess() {
    return createBoundAccess();
  }
  /**
   * @ast method 
   * @aspect GenericsPrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsPrettyPrint.jrag:11
   */
  public void toString(StringBuffer s) {
    s.append(name());
    if(getNumTypeBound() > 0) {
      s.append(" extends ");
      s.append(getTypeBound(0).type().fullName());
      for(int i = 1; i < getNumTypeBound(); i++) {
        s.append(" & ");
        s.append(getTypeBound(i).type().fullName());
      }
    }
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:1
   */
  public TypeVariable() {
    super();

    setChild(new List(), 1);
    setChild(new List(), 2);

  }
  /**
   * @ast method 
   * @declaredat Generics.ast:9
   */
  public TypeVariable(Modifiers p0, String p1, List<BodyDecl> p2, List<Access> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:15
   */
  public TypeVariable(Modifiers p0, beaver.Symbol p1, List<BodyDecl> p2, List<Access> p3) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
    setChild(p3, 2);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:24
   */
  protected int numChildren() {
    return 3;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat Generics.ast:30
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * Setter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setModifiers(Modifiers node) {
    setChild(node, 0);
  }
  /**
   * Getter for Modifiers
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:12
   */
  public Modifiers getModifiers() {
    return (Modifiers)getChild(0);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:18
   */
  public Modifiers getModifiersNoTransform() {
    return (Modifiers)getChildNoTransform(0);
  }
  /**
   * Setter for lexeme ID
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setID(String value) {
    tokenString_ID = value;
  }
  /**
   * @ast method 
   * @declaredat Generics.ast:8
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
   * @declaredat Generics.ast:19
   */
  public String getID() {
    return tokenString_ID != null ? tokenString_ID : "";
  }
  /**
   * Setter for BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setBodyDeclList(List<BodyDecl> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:12
   */
  public int getNumBodyDecl() {
    return getBodyDeclList().getNumChild();
  }
  /**
   * Getter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl getBodyDecl(int i) {
    return (BodyDecl)getBodyDeclList().getChild(i);
  }
  /**
   * Add element to list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:27
   */
  public void addBodyDecl(BodyDecl node) {
    List<BodyDecl> list = (parent == null || state == null) ? getBodyDeclListNoTransform() : getBodyDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:34
   */
  public void addBodyDeclNoTransform(BodyDecl node) {
    List<BodyDecl> list = getBodyDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:42
   */
  public void setBodyDecl(BodyDecl node, int i) {
    List<BodyDecl> list = getBodyDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for BodyDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:50
   */
  public List<BodyDecl> getBodyDecls() {
    return getBodyDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:56
   */
  public List<BodyDecl> getBodyDeclsNoTransform() {
    return getBodyDeclListNoTransform();
  }
  /**
   * Getter for list BodyDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:63
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
   * @declaredat Generics.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<BodyDecl> getBodyDeclListNoTransform() {
    return (List<BodyDecl>)getChildNoTransform(1);
  }
  /**
   * Setter for TypeBoundList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:5
   */
  public void setTypeBoundList(List<Access> list) {
    setChild(list, 2);
  }
  /**
   * @return number of children in TypeBoundList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:12
   */
  public int getNumTypeBound() {
    return getTypeBoundList().getNumChild();
  }
  /**
   * Getter for child in list TypeBoundList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getTypeBound(int i) {
    return (Access)getTypeBoundList().getChild(i);
  }
  /**
   * Add element to list TypeBoundList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:27
   */
  public void addTypeBound(Access node) {
    List<Access> list = (parent == null || state == null) ? getTypeBoundListNoTransform() : getTypeBoundList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:34
   */
  public void addTypeBoundNoTransform(Access node) {
    List<Access> list = getTypeBoundListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list TypeBoundList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:42
   */
  public void setTypeBound(Access node, int i) {
    List<Access> list = getTypeBoundList();
    list.setChild(node, i);
  }
  /**
   * Getter for TypeBound list.
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:50
   */
  public List<Access> getTypeBounds() {
    return getTypeBoundList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:56
   */
  public List<Access> getTypeBoundsNoTransform() {
    return getTypeBoundListNoTransform();
  }
  /**
   * Getter for list TypeBoundList
   * @apilevel high-level
   * @ast method 
   * @declaredat Generics.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeBoundList() {
    List<Access> list = (List<Access>)getChild(2);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat Generics.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getTypeBoundListNoTransform() {
    return (List<Access>)getChildNoTransform(2);
  }
  /**
   * @apilevel internal
   */
  protected boolean toInterface_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl toInterface_value;
  /**
   * @attribute syn
   * @aspect GreatestLowerBoundFactory
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GLBTypeFactory.jadd:12
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl toInterface() {
    if(toInterface_computed) {
      return toInterface_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    toInterface_value = toInterface_compute();
    toInterface_value.setParent(this);
    toInterface_value.is$Final = true;
if(true) toInterface_computed = true;
    return toInterface_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl toInterface_compute() {
    // convert var to interface
    InterfaceDecl ITj = new InterfaceDecl();
    ITj.setID("ITj_" + hashCode());
    // I'm assuming that TypeVariable has no members of it's own.
    // TODO: would it be enough to add only public members of a bound
    // that is TypeVariable or ClassDecl and add other (interface)
    // bounds as superinterfaces to ITj
    // TODO: Is it really necessary to add public members to the new
    // interface? Or is an empty interface more than enough since java
    // has a nominal type system.
    for (int i = 0; i < getNumTypeBound(); i++) {
      TypeDecl bound = getTypeBound(i).type();
      for (int j = 0; j < bound.getNumBodyDecl(); j++) {
        BodyDecl bd = bound.getBodyDecl(j);
        if (bd instanceof FieldDeclaration) {
          FieldDeclaration fd = (FieldDeclaration) bd.fullCopy();
          if (fd.isPublic())
            ITj.addBodyDecl(fd);
        } 
        else if (bd instanceof MethodDecl) {
          MethodDecl md = (MethodDecl) bd;
          if (md.isPublic())
            ITj.addBodyDecl((BodyDecl)md.fullCopy());
        }
      }
    }
    return ITj;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:16
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
  private boolean involvesTypeParameters_compute() {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericTypeVariables
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericTypeVariables.jrag:33
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lowerBound() {
      ASTNode$State state = state();
    TypeDecl lowerBound_value = lowerBound_compute();
    return lowerBound_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl lowerBound_compute() {  return getTypeBound(0).type();  }
  /**
   * @attribute syn
   * @aspect GenericTypeVariablesMembers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericTypeVariables.jrag:38
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection memberMethods(String name) {
      ASTNode$State state = state();
    Collection memberMethods_String_value = memberMethods_compute(name);
    return memberMethods_String_value;
  }
  /**
   * @apilevel internal
   */
  private Collection memberMethods_compute(String name) {
    Collection list = new HashSet();
    for(int i = 0; i < getNumTypeBound(); i++) {
      for(Iterator iter = getTypeBound(i).type().memberMethods(name).iterator(); iter.hasNext(); ) {
        MethodDecl decl = (MethodDecl)iter.next();
        //if(decl.accessibleFrom(hostType()))
          list.add(decl);
      }
    }
    return list;
  }
  protected java.util.Map memberFields_String_values;
  /**
   * @attribute syn
   * @aspect GenericTypeVariablesMembers
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericTypeVariables.jrag:50
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet memberFields(String name) {
    Object _parameters = name;
    if(memberFields_String_values == null) memberFields_String_values = new java.util.HashMap(4);
    if(memberFields_String_values.containsKey(_parameters)) {
      return (SimpleSet)memberFields_String_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet memberFields_String_value = memberFields_compute(name);
if(isFinal && num == state().boundariesCrossed) memberFields_String_values.put(_parameters, memberFields_String_value);
    return memberFields_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet memberFields_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumTypeBound(); i++) {
      for(Iterator iter = getTypeBound(i).type().memberFields(name).iterator(); iter.hasNext(); ) {
        FieldDeclaration decl = (FieldDeclaration)iter.next();
        //if(decl.accessibleFrom(hostType()))
          set = set.add(decl);
      }
    }
    return set;
  }
  protected java.util.Map castingConversionTo_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:66
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
    if(!type.isReferenceType())
      return false;
    if(getNumTypeBound() == 0) return true;
    for(int i = 0; i < getNumTypeBound(); i++)
      if(getTypeBound(i).type().castingConversionTo(type))
        return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:129
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isNestedType() {
      ASTNode$State state = state();
    boolean isNestedType_value = isNestedType_compute();
    return isNestedType_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isNestedType_compute() {  return false;  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:323
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
  private TypeDecl erasure_compute() {  return getTypeBound(0).type().erasure();  }
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
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:534
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
  private String fullName_compute() {
    if(getParent().getParent() instanceof TypeDecl) {
      TypeDecl typeDecl = (TypeDecl)getParent().getParent();
      return typeDecl.fullName() + "@" + name();
    }
    return super.fullName();
  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:547
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
  private boolean sameSignature_compute(Access a) {  return a.type() == this;  }
  /**
   * @apilevel internal
   */
  protected boolean lubType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl lubType_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:778
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lubType() {
    if(lubType_computed) {
      return lubType_value;
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    lubType_value = lubType_compute();
if(isFinal && num == state().boundariesCrossed) lubType_computed = true;
    return lubType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl lubType_compute() {
    ArrayList list = new ArrayList();
    for(int i = 0; i < getNumTypeBound(); i++)
      list.add(getTypeBound(i).type());
    return lookupLUBType(list);
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:924
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
  private boolean usesTypeVariable_compute() {  return true;  }
  protected java.util.Map accessibleFrom_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect NewGenerics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1262
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
  private boolean accessibleFrom_compute(TypeDecl type) {  return true;  }
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
   * @aspect NewGenerics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1264
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
  private String typeName_compute() {  return name();  }
  /**
   * @attribute syn
   * @aspect GenericsParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:71
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean isTypeVariable() {
      ASTNode$State state = state();
    boolean isTypeVariable_value = isTypeVariable_compute();
    return isTypeVariable_value;
  }
  /**
   * @apilevel internal
   */
  private boolean isTypeVariable_compute() {  return true;  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:49
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:60
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
  private boolean supertypeWildcardExtends_compute(WildcardExtendsType type) {  return type.extendsType().subtype(this);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:69
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
  private boolean supertypeWildcardSuper_compute(WildcardSuperType type) {  return type.superType().subtype(this);  }
  protected java.util.Map sameStructure_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:215
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean sameStructure(TypeDecl t) {
    Object _parameters = t;
    if(sameStructure_TypeDecl_values == null) sameStructure_TypeDecl_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(sameStructure_TypeDecl_values.containsKey(_parameters)) {
      Object _o = sameStructure_TypeDecl_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return ((Boolean)_o).booleanValue();
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      sameStructure_TypeDecl_values.put(_parameters, _value);
      _value.value = Boolean.valueOf(true);
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      boolean new_sameStructure_TypeDecl_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_sameStructure_TypeDecl_value = sameStructure_compute(t);
        if (new_sameStructure_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
          state.CHANGE = true;
          _value.value = Boolean.valueOf(new_sameStructure_TypeDecl_value);
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
      if(isFinal && num == state().boundariesCrossed) {
        sameStructure_TypeDecl_values.put(_parameters, new_sameStructure_TypeDecl_value);
      }
      else {
        sameStructure_TypeDecl_values.remove(_parameters);
      state.RESET_CYCLE = true;
      sameStructure_compute(t);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_sameStructure_TypeDecl_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      boolean new_sameStructure_TypeDecl_value = sameStructure_compute(t);
      if (state.RESET_CYCLE) {
        sameStructure_TypeDecl_values.remove(_parameters);
      }
      else if (new_sameStructure_TypeDecl_value!=((Boolean)_value.value).booleanValue()) {
        state.CHANGE = true;
        _value.value = new_sameStructure_TypeDecl_value;
      }
      return new_sameStructure_TypeDecl_value;
    }
    return ((Boolean)_value.value).booleanValue();
  }
  /**
   * @apilevel internal
   */
  private boolean sameStructure_compute(TypeDecl t) {
    if(!(t instanceof TypeVariable))
      return false;
    if(t == this)
      return true;
    TypeVariable type = (TypeVariable)t;
    if(type.getNumTypeBound() != getNumTypeBound())
      return false;
    for(int i = 0; i < getNumTypeBound(); i++) {
      boolean found = false;
      for(int j = i; !found && j < getNumTypeBound(); j++)
        if(getTypeBound(i).type().sameStructure(type.getTypeBound(j).type()))
          found = true;
      if(!found)
        return false;
    }
    return true;
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:282
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
  private boolean supertypeArrayDecl_compute(ArrayDecl type) {
    for(int i = 0; i < getNumTypeBound(); i++)
      if(type.subtype(getTypeBound(i).type())) {
        return true;
      }
    return false;
  }
  protected java.util.Map subtype_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:290
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
  private boolean subtype_compute(TypeDecl type) {  return type.supertypeTypeVariable(this);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:299
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
  private boolean supertypeTypeVariable_compute(TypeVariable type) {
    if(type == this)
      return true;
    for(int i = 0; i < getNumTypeBound(); i++) {
      boolean found = false;
      for(int j = 0; !found && j < type.getNumTypeBound(); j++) {
        if(type.getSubstitutedTypeBound(j, this).type().subtype(getTypeBound(i).type()))
          found = true;
      }
      if(!found)
        return false;
    }
    return true;
  }
  protected java.util.Map getSubstitutedTypeBound_int_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:314
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getSubstitutedTypeBound(int i, TypeDecl type) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(Integer.valueOf(i));
    _parameters.add(type);
    if(getSubstitutedTypeBound_int_TypeDecl_values == null) getSubstitutedTypeBound_int_TypeDecl_values = new java.util.HashMap(4);
    if(getSubstitutedTypeBound_int_TypeDecl_values.containsKey(_parameters)) {
      return (Access)getSubstitutedTypeBound_int_TypeDecl_values.get(_parameters);
    }
      ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    Access getSubstitutedTypeBound_int_TypeDecl_value = getSubstitutedTypeBound_compute(i, type);
if(isFinal && num == state().boundariesCrossed) getSubstitutedTypeBound_int_TypeDecl_values.put(_parameters, getSubstitutedTypeBound_int_TypeDecl_value);
    return getSubstitutedTypeBound_int_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private Access getSubstitutedTypeBound_compute(int i, TypeDecl type) {
    Access bound = getTypeBound(i);
    if(!bound.type().usesTypeVariable())
      return bound;
    final TypeDecl typeDecl = type;
    Access access = bound.type().substitute(
      new Parameterization() {
    		public boolean isRawType() { 
    			return false; 
    		}
    		public TypeDecl substitute(TypeVariable typeVariable) {
    			return typeVariable == TypeVariable.this ? typeDecl : typeVariable;
    		} 
    	}
    );
    access.setParent(this);
    return access;
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:333
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
  private boolean supertypeClassDecl_compute(ClassDecl type) {
    for(int i = 0; i < getNumTypeBound(); i++)
      if(!type.subtype(getSubstitutedTypeBound(i, type).type()))
        return false;
    return true;
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:339
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
  private boolean supertypeInterfaceDecl_compute(InterfaceDecl type) {
    for(int i = 0; i < getNumTypeBound(); i++)
      if(!type.subtype(getSubstitutedTypeBound(i, type).type()))
        return false;
    return true;
  }
  protected java.util.Map instanceOf_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:400
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
   * @attribute inh
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:776
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeObject() {
      ASTNode$State state = state();
    TypeDecl typeObject_value = getParent().Define_TypeDecl_typeObject(this, null);
    return typeObject_value;
  }
  /**
   * @attribute inh
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:818
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeNull() {
      ASTNode$State state = state();
    TypeDecl typeNull_value = getParent().Define_TypeDecl_typeNull(this, null);
    return typeNull_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericTypeVariables.jrag:13
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getTypeBoundListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return NameType.TYPE_NAME;
    }
    return super.Define_NameType_nameType(caller, child);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    // Declared in /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericTypeVariables.jrag at line 16
    if(getNumTypeBound() == 0) {
      state().duringGenericTypeVariables++;
      ASTNode result = rewriteRule0();
      state().duringGenericTypeVariables--;
      return result;
    }

    return super.rewriteTo();
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericTypeVariables.jrag:16
   * @apilevel internal
   */  private TypeVariable rewriteRule0() {
{
      addTypeBound(
        new TypeAccess(
          "java.lang",
          "Object"
        )
      );
      return this;
    }  }
}
