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
 * @production ParInterfaceDecl : {@link InterfaceDecl} ::= <span class="component">Argument:{@link Access}*</span> <span class="component">SuperInterfaceId:{@link Access}*</span> <span class="component">{@link BodyDecl}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.ast:9
 */
public class ParInterfaceDecl extends InterfaceDecl implements Cloneable, ParTypeDecl, MemberSubstitutor {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    involvesTypeParameters_visited = -1;
    involvesTypeParameters_computed = false;
    involvesTypeParameters_initialized = false;
    erasure_computed = false;
    erasure_value = null;
    getSuperInterfaceIdList_computed = false;
    getSuperInterfaceIdList_value = null;
    getBodyDeclList_computed = false;
    getBodyDeclList_value = null;
    subtype_TypeDecl_values = null;
    sameStructure_TypeDecl_values = null;
    instanceOf_TypeDecl_values = null;
    sameSignature_ArrayList_values = null;
    usesTypeVariable_visited = -1;
    usesTypeVariable_computed = false;
    usesTypeVariable_initialized = false;
    sourceTypeDecl_computed = false;
    sourceTypeDecl_value = null;
    fullName_computed = false;
    fullName_value = null;
    typeName_computed = false;
    typeName_value = null;
    unimplementedMethods_computed = false;
    unimplementedMethods_value = null;
    localMethodsSignatureMap_computed = false;
    localMethodsSignatureMap_value = null;
    localFields_String_values = null;
    localTypeDecls_String_values = null;
    constructors_computed = false;
    constructors_value = null;
    genericDecl_computed = false;
    genericDecl_value = null;
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
  public ParInterfaceDecl clone() throws CloneNotSupportedException {
    ParInterfaceDecl node = (ParInterfaceDecl)super.clone();
    node.involvesTypeParameters_visited = -1;
    node.involvesTypeParameters_computed = false;
    node.involvesTypeParameters_initialized = false;
    node.erasure_computed = false;
    node.erasure_value = null;
    node.getSuperInterfaceIdList_computed = false;
    node.getSuperInterfaceIdList_value = null;
    node.getBodyDeclList_computed = false;
    node.getBodyDeclList_value = null;
    node.subtype_TypeDecl_values = null;
    node.sameStructure_TypeDecl_values = null;
    node.instanceOf_TypeDecl_values = null;
    node.sameSignature_ArrayList_values = null;
    node.usesTypeVariable_visited = -1;
    node.usesTypeVariable_computed = false;
    node.usesTypeVariable_initialized = false;
    node.sourceTypeDecl_computed = false;
    node.sourceTypeDecl_value = null;
    node.fullName_computed = false;
    node.fullName_value = null;
    node.typeName_computed = false;
    node.typeName_value = null;
    node.unimplementedMethods_computed = false;
    node.unimplementedMethods_value = null;
    node.localMethodsSignatureMap_computed = false;
    node.localMethodsSignatureMap_value = null;
    node.localFields_String_values = null;
    node.localTypeDecls_String_values = null;
    node.constructors_computed = false;
    node.constructors_value = null;
    node.genericDecl_computed = false;
    node.genericDecl_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ParInterfaceDecl copy() {
    try {
      ParInterfaceDecl node = (ParInterfaceDecl) clone();
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
  public ParInterfaceDecl fullCopy() {
    ParInterfaceDecl tree = (ParInterfaceDecl) copy();
    if (children != null) {
      for (int i = 0; i < children.length; ++i) {
          switch (i) {
          case 3:
          case 4:
            tree.children[i] = new List();
            continue;
          }
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
   * @aspect GenericsNameBinding
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:557
   */
  public void collectErrors() {
    // Disable error check for ParInterfaceDecl which is an instanciated GenericInterfaceDecl
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:840
   */
  public TypeDecl substitute(TypeVariable typeVariable) {
    for(int i = 0; i < numTypeParameter(); i++)
      if(typeParameter(i) == typeVariable)
        return getArgument(i).type();
    return super.substitute(typeVariable);
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:853
   */
  public int numTypeParameter() {
    return ((GenericTypeDecl)original()).getNumTypeParameter(); 
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:856
   */
  public TypeVariable typeParameter(int index) {
    return ((GenericTypeDecl)original()).getTypeParameter(index);
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:888
   */
  public Access substitute(Parameterization parTypeDecl) {
    // TODO: include nesting as well....
    if(parTypeDecl.isRawType())
      return ((GenericTypeDecl)genericDecl()).rawType().createBoundAccess();
    if(!usesTypeVariable())
      return super.substitute(parTypeDecl);
    List list = new List();
    for(int i = 0; i < getNumArgument(); i++)
      list.add(getArgument(i).type().substitute(parTypeDecl));
    return new ParTypeAccess(genericDecl().createQualifiedAccess(), list);
  }
  /**
   * @ast method 
   * @aspect GenericsParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:76
   */
  public Access createQualifiedAccess() {
    List typeArgumentList = new List();
    for(int i = 0; i < getNumArgument(); i++) {
      Access a = (Access)getArgument(i);
      if(a instanceof TypeAccess)
        typeArgumentList.add(a.type().createQualifiedAccess());
      else
        typeArgumentList.add(a.fullCopy());
    }
    if(!isTopLevelType()) {
      if(isRawType())
        return enclosingType().createQualifiedAccess().qualifiesAccess(
          new TypeAccess("", getID())
        );
      else
        return enclosingType().createQualifiedAccess().qualifiesAccess(
          new ParTypeAccess(new TypeAccess("", getID()), typeArgumentList)
        );
    }
    else {
      if(isRawType())
        return new TypeAccess(packageName(), getID());
      else
        return new ParTypeAccess(new TypeAccess(packageName(), getID()), typeArgumentList);
    }
  }
  /**
   * @ast method 
   * @aspect GenericsCodegen
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/Jimple1.5Backend/GenericsCodegen.jrag:406
   */
  public void transformation() {
  }
  /**
   * @ast method 
   * 
   */
  public ParInterfaceDecl() {
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
    setChild(new List(), 1);
    setChild(new List(), 2);
    setChild(new List(), 3);
  }
  /**
   * @ast method 
   * 
   */
  public ParInterfaceDecl(Modifiers p0, String p1, List<Access> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * 
   */
  public ParInterfaceDecl(Modifiers p0, beaver.Symbol p1, List<Access> p2) {
    setChild(p0, 0);
    setID(p1);
    setChild(p2, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 2;
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
   * Replaces the Argument list.
   * @param list The new list node to be used as the Argument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setArgumentList(List<Access> list) {
    setChild(list, 1);
  }
  /**
   * Retrieves the number of children in the Argument list.
   * @return Number of children in the Argument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumArgument() {
    return getArgumentList().getNumChild();
  }
  /**
   * Retrieves the number of children in the Argument list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the Argument list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumArgumentNoTransform() {
    return getArgumentListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the Argument list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the Argument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getArgument(int i) {
    return (Access)getArgumentList().getChild(i);
  }
  /**
   * Append an element to the Argument list.
   * @param node The element to append to the Argument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addArgument(Access node) {
    List<Access> list = (parent == null || state == null) ? getArgumentListNoTransform() : getArgumentList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addArgumentNoTransform(Access node) {
    List<Access> list = getArgumentListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the Argument list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setArgument(Access node, int i) {
    List<Access> list = getArgumentList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the Argument list.
   * @return The node representing the Argument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Access> getArguments() {
    return getArgumentList();
  }
  /**
   * Retrieves the Argument list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Argument list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getArgumentsNoTransform() {
    return getArgumentListNoTransform();
  }
  /**
   * Retrieves the Argument list.
   * @return The node representing the Argument list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getArgumentList() {
    List<Access> list = (List<Access>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the Argument list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the Argument list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getArgumentListNoTransform() {
    return (List<Access>)getChildNoTransform(1);
  }
  /**
   * Replaces the SuperInterfaceId list.
   * @param list The new list node to be used as the SuperInterfaceId list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setSuperInterfaceIdList(List<Access> list) {
    setChild(list, 2);
  }
  /**
   * Retrieves the number of children in the SuperInterfaceId list.
   * @return Number of children in the SuperInterfaceId list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumSuperInterfaceId() {
    return getSuperInterfaceIdList().getNumChild();
  }
  /**
   * Retrieves the number of children in the SuperInterfaceId list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the SuperInterfaceId list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumSuperInterfaceIdNoTransform() {
    return getSuperInterfaceIdListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the SuperInterfaceId list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the SuperInterfaceId list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Access getSuperInterfaceId(int i) {
    return (Access)getSuperInterfaceIdList().getChild(i);
  }
  /**
   * Append an element to the SuperInterfaceId list.
   * @param node The element to append to the SuperInterfaceId list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addSuperInterfaceId(Access node) {
    List<Access> list = (parent == null || state == null) ? getSuperInterfaceIdListNoTransform() : getSuperInterfaceIdList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addSuperInterfaceIdNoTransform(Access node) {
    List<Access> list = getSuperInterfaceIdListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the SuperInterfaceId list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setSuperInterfaceId(Access node, int i) {
    List<Access> list = getSuperInterfaceIdList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the SuperInterfaceId list.
   * @return The node representing the SuperInterfaceId list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<Access> getSuperInterfaceIds() {
    return getSuperInterfaceIdList();
  }
  /**
   * Retrieves the SuperInterfaceId list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the SuperInterfaceId list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getSuperInterfaceIdsNoTransform() {
    return getSuperInterfaceIdListNoTransform();
  }
  /**
   * Retrieves the SuperInterfaceId list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the SuperInterfaceId list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<Access> getSuperInterfaceIdListNoTransform() {
    return (List<Access>)getChildNoTransform(2);
  }
  /**
   * Retrieves the child position of the SuperInterfaceId list.
   * @return The the child position of the SuperInterfaceId list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int getSuperInterfaceIdListChildPosition() {
    return 2;
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
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the BodyDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<BodyDecl> getBodyDeclListNoTransform() {
    return (List<BodyDecl>)getChildNoTransform(3);
  }
  /**
   * Retrieves the child position of the BodyDecl list.
   * @return The the child position of the BodyDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int getBodyDeclListChildPosition() {
    return 3;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericMethodsInference.jrag:24
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
  private boolean involvesTypeParameters_compute() {
    for(int i = 0; i < getNumArgument(); i++)
      if(getArgument(i).type().involvesTypeParameters())
        return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect NestedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:569
   */
  public TypeDecl hostType() {
    ASTNode$State state = state();
    try {  return original();  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:245
   */
  public boolean isRawType() {
    ASTNode$State state = state();
    try {  return isNestedType() && enclosingType().isRawType();  }
    finally {
    }
  }
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:351
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
  private TypeDecl erasure_compute() {  return genericDecl();  }
  /**
   * @apilevel internal
   */
  protected boolean getSuperInterfaceIdList_computed = false;
  /**
   * @apilevel internal
   */
  protected List getSuperInterfaceIdList_value;
  /**
   * @attribute syn nta
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1037
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List getSuperInterfaceIdList() {
    if(getSuperInterfaceIdList_computed) {
      return (List) getChild(getSuperInterfaceIdListChildPosition());
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getSuperInterfaceIdList_value = getSuperInterfaceIdList_compute();
    setSuperInterfaceIdList(getSuperInterfaceIdList_value);
      if(isFinal && num == state().boundariesCrossed) getSuperInterfaceIdList_computed = true;
    return (List) getChild(getSuperInterfaceIdListChildPosition());
  }
  /**
   * @apilevel internal
   */
  private List getSuperInterfaceIdList_compute() {
    GenericInterfaceDecl decl = (GenericInterfaceDecl)genericDecl();
    //System.err.println("Begin substituting implements list");
    List list = decl.getSuperInterfaceIdList().substitute(this);
    //System.err.println("End substituting implements list");
    return list;
  }
  /**
   * @apilevel internal
   */
  protected boolean getBodyDeclList_computed = false;
  /**
   * @apilevel internal
   */
  protected List getBodyDeclList_value;
  /**
   * @attribute syn nta
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1047
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List getBodyDeclList() {
    if(getBodyDeclList_computed) {
      return (List) getChild(getBodyDeclListChildPosition());
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    getBodyDeclList_value = getBodyDeclList_compute();
    setBodyDeclList(getBodyDeclList_value);
      if(isFinal && num == state().boundariesCrossed) getBodyDeclList_computed = true;
    return (List) getChild(getBodyDeclListChildPosition());
  }
  /**
   * @apilevel internal
   */
  private List getBodyDeclList_compute() {  return new BodyDeclList();  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:14
   */
  public boolean supertypeGenericClassDecl(GenericClassDecl type) {
    ASTNode$State state = state();
    try {  return type.subtype(genericDecl().original());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:20
   */
  public boolean supertypeGenericInterfaceDecl(GenericInterfaceDecl type) {
    ASTNode$State state = state();
    try {  return type.subtype(genericDecl().original());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:421
   */
  public boolean supertypeClassDecl(ClassDecl type) {
    ASTNode$State state = state();
    try {  return super.supertypeClassDecl(type);  }
    finally {
    }
  }
  protected java.util.Map subtype_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:128
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
  private boolean subtype_compute(TypeDecl type) {  return type.supertypeParInterfaceDecl(this);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:26
   */
  public boolean supertypeRawClassDecl(RawClassDecl type) {
    ASTNode$State state = state();
    try {  return type.genericDecl().original().subtype(genericDecl().original());  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:30
   */
  public boolean supertypeRawInterfaceDecl(RawInterfaceDecl type) {
    ASTNode$State state = state();
    try {  return type.genericDecl().original().subtype(genericDecl().original());  }
    finally {
    }
  }
  protected java.util.Map sameStructure_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:194
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
    if(!(t instanceof ParInterfaceDecl))
      return false;
    ParInterfaceDecl type = (ParInterfaceDecl)t;
    if(type.genericDecl().original() == genericDecl().original() &&
       type.getNumArgument() == getNumArgument()) {
      for(int i = 0; i < getNumArgument(); i++)
        if(!type.getArgument(i).type().sameStructure(getArgument(i).type()))
          return false;
      if(isNestedType() && type.isNestedType())
        return type.enclosingType().sameStructure(enclosingType());
      return true;
    }
    return false;
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:125
   */
  public boolean supertypeParClassDecl(ParClassDecl type) {
    ASTNode$State state = state();
    try {
    if(type.genericDecl().original() == genericDecl().original() &&
       type.getNumArgument() == getNumArgument()) {
      for(int i = 0; i < getNumArgument(); i++)
        if(!type.getArgument(i).type().containedIn(getArgument(i).type()))
          return false;
      if(isNestedType() && type.isNestedType())
        return type.enclosingType().subtype(enclosingType());
      return true;
    }
    return supertypeClassDecl(type);
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:129
   */
  public boolean supertypeParInterfaceDecl(ParInterfaceDecl type) {
    ASTNode$State state = state();
    try {
    if(type.genericDecl().original() == genericDecl().original() &&
       type.getNumArgument() == getNumArgument()) {
      for(int i = 0; i < getNumArgument(); i++)
        if(!type.getArgument(i).type().containedIn(getArgument(i).type()))
          return false;
      if(isNestedType() && type.isNestedType())
        return type.enclosingType().subtype(enclosingType());
      return true;
    }
    return supertypeInterfaceDecl(type);
  }
    finally {
    }
  }
  protected java.util.Map instanceOf_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:398
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
    try {
		if (isRawType())
			return true;
		for (int i = 0; i < getNumArgument(); ++i) {
			if (!getArgument(i).type().isWildcard())
				return false;
		}
		return true;
	}
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:244
   */
  public boolean isParameterizedType() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect GenericsTypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:380
   */
  public boolean sameArgument(ParTypeDecl decl) {
    ASTNode$State state = state();
    try {
    if(this == decl) return true;
    if(genericDecl() != decl.genericDecl())
      return false;
    for(int i = 0; i < getNumArgument(); i++) {
      TypeDecl t1 = getArgument(i).type();
      TypeDecl t2 = decl.getArgument(i).type();
      if(t1 instanceof ParTypeDecl && t2 instanceof ParTypeDecl) {
        if(!((ParTypeDecl)t1).sameArgument((ParTypeDecl)t2))
          return false;
      }
      else {
        if(t1 != t2)
          return false;
      }
    }
    return true;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:577
   */
  public boolean sameSignature(Access a) {
    ASTNode$State state = state();
    try {
    if(a instanceof ParTypeAccess) {
      ParTypeAccess ta = (ParTypeAccess)a;
      if(genericDecl() != ta.genericDecl())
        return false;
      if(getNumArgument() != ta.getNumTypeArgument())
        return false;
      for(int i = 0; i < getNumArgument(); i++)
        if(!getArgument(i).type().sameSignature(ta.getTypeArgument(i)))
          return false;
      return true;
    }
    else if(a instanceof TypeAccess && ((TypeAccess)a).isRaw())
      return false;
    return super.sameSignature(a);
  }
    finally {
    }
  }
  protected java.util.Map sameSignature_ArrayList_values;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:612
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean sameSignature(ArrayList list) {
    Object _parameters = list;
    if(sameSignature_ArrayList_values == null) sameSignature_ArrayList_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(sameSignature_ArrayList_values.containsKey(_parameters)) {
      Object _o = sameSignature_ArrayList_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return ((Boolean)_o).booleanValue();
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      sameSignature_ArrayList_values.put(_parameters, _value);
      _value.value = Boolean.valueOf(true);
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      boolean new_sameSignature_ArrayList_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_sameSignature_ArrayList_value = sameSignature_compute(list);
        if (new_sameSignature_ArrayList_value!=((Boolean)_value.value).booleanValue()) {
          state.CHANGE = true;
          _value.value = Boolean.valueOf(new_sameSignature_ArrayList_value);
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(isFinal && num == state().boundariesCrossed) {
        sameSignature_ArrayList_values.put(_parameters, new_sameSignature_ArrayList_value);
      }
      else {
        sameSignature_ArrayList_values.remove(_parameters);
      state.RESET_CYCLE = true;
      sameSignature_compute(list);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_sameSignature_ArrayList_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      boolean new_sameSignature_ArrayList_value = sameSignature_compute(list);
      if (state.RESET_CYCLE) {
        sameSignature_ArrayList_values.remove(_parameters);
      }
      else if (new_sameSignature_ArrayList_value!=((Boolean)_value.value).booleanValue()) {
        state.CHANGE = true;
        _value.value = new_sameSignature_ArrayList_value;
      }
      return new_sameSignature_ArrayList_value;
    }
    return ((Boolean)_value.value).booleanValue();
  }
  /**
   * @apilevel internal
   */
  private boolean sameSignature_compute(ArrayList list) {
    if(getNumArgument() != list.size())
      return false;
    for(int i = 0; i < list.size(); i++)
      if(getArgument(i).type() != list.get(i))
        return false;
    return true;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1069
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
  private boolean usesTypeVariable_compute() {
    if(super.usesTypeVariable())
      return true;
    for(int i = 0; i < getNumArgument(); i++)
      if(getArgument(i).type().usesTypeVariable())
        return true;
    return false;
  }
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1306
   */
  public TypeDecl original() {
    ASTNode$State state = state();
    try {  return genericDecl().original();  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean sourceTypeDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl sourceTypeDecl_value;
  /**
   * @attribute syn
   * @aspect SourceDeclarations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1505
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl sourceTypeDecl() {
    if(sourceTypeDecl_computed) {
      return sourceTypeDecl_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    sourceTypeDecl_value = sourceTypeDecl_compute();
      if(isFinal && num == state().boundariesCrossed) sourceTypeDecl_computed = true;
    return sourceTypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl sourceTypeDecl_compute() {  return genericDecl().original().sourceTypeDecl();  }
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
   * @aspect GenericsParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:12
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
    if(isNestedType())
      return enclosingType().fullName() + "." + nameWithArgs();
    String packageName = packageName();
    if(packageName.equals(""))
      return nameWithArgs();
    return packageName + "." + nameWithArgs();
  }
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
   * @aspect GenericsParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:21
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
  private String typeName_compute() {
    if(isNestedType())
      return enclosingType().typeName() + "." + nameWithArgs();
    String packageName = packageName();
    if(packageName.equals("") || packageName.equals(PRIMITIVE_PACKAGE_NAME))
      return nameWithArgs();
    return packageName + "." + nameWithArgs();
  }
  /**
   * @attribute syn
   * @aspect GenericsParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:30
   */
  public String nameWithArgs() {
    ASTNode$State state = state();
    try {
    StringBuffer s = new StringBuffer();
    s.append(name());
    s.append("<");
    for(int i = 0; i < getNumArgument(); i++) {
      if(i != 0)
        s.append(", ");
      s.append(getArgument(i).type().fullName());
    }
    s.append(">");
    return s.toString();
  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean unimplementedMethods_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection unimplementedMethods_value;
  /**
   * @attribute syn
   * @aspect MethodSignature15
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/MethodSignature.jrag:406
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection unimplementedMethods() {
    if(unimplementedMethods_computed) {
      return unimplementedMethods_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    unimplementedMethods_value = unimplementedMethods_compute();
      if(isFinal && num == state().boundariesCrossed) unimplementedMethods_computed = true;
    return unimplementedMethods_value;
  }
  /**
   * @apilevel internal
   */
  private Collection unimplementedMethods_compute() {
    HashSet set = new HashSet();
    HashSet result = new HashSet();
    for(Iterator iter = genericDecl().unimplementedMethods().iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      set.add(m.sourceMethodDecl());
    }
    for(Iterator iter = super.unimplementedMethods().iterator(); iter.hasNext(); ) {
      MethodDecl m = (MethodDecl)iter.next();
      if(set.contains(m.sourceMethodDecl()))
        result.add(m);
    }
    return result;
  }
  /**
   * @apilevel internal
   */
  protected boolean localMethodsSignatureMap_computed = false;
  /**
   * @apilevel internal
   */
  protected HashMap localMethodsSignatureMap_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1084
   */
  @SuppressWarnings({"unchecked", "cast"})
  public HashMap localMethodsSignatureMap() {
    if(localMethodsSignatureMap_computed) {
      return localMethodsSignatureMap_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    localMethodsSignatureMap_value = localMethodsSignatureMap_compute();
      if(true) localMethodsSignatureMap_computed = true;
    return localMethodsSignatureMap_value;
  }
  /**
   * @apilevel internal
   */
  private HashMap localMethodsSignatureMap_compute() {
    HashMap map = new HashMap();
    for(Iterator iter = original().localMethodsIterator(); iter.hasNext(); ) {
      MethodDecl decl = (MethodDecl)iter.next();

      /* ES removing this:
      if(!decl.isStatic() && (decl.usesTypeVariable() || isRawType())) {
        BodyDecl b = decl.substitutedBodyDecl(this);
        addBodyDecl(b);
      	// Here we should access b through an ordinary
      	// child accessor instead of setting is$Final directly,
      	// however doing so appears to cause unexpected behaviour!
        b.is$Final = true;
        decl = (MethodDecl) b;
      }
      map.put(decl.signature(), decl);
      * and replacing with:
      */
      if(!decl.isStatic() && (decl.usesTypeVariable() || isRawType())) {
        BodyDecl copyDecl = ((BodyDeclList)getBodyDeclList()).localMethodSignatureCopy(decl, this);
        decl = (MethodDecl) copyDecl;
      }
      map.put(decl.signature(), decl);

    }
    return map;
  }
  protected java.util.Map localFields_String_values;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1119
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localFields(String name) {
    Object _parameters = name;
    if(localFields_String_values == null) localFields_String_values = new java.util.HashMap(4);
    if(localFields_String_values.containsKey(_parameters)) {
      return (SimpleSet)localFields_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet localFields_String_value = localFields_compute(name);
      if(true) localFields_String_values.put(_parameters, localFields_String_value);
    return localFields_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localFields_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(Iterator iter = original().localFields(name).iterator(); iter.hasNext(); ) {
      FieldDeclaration f = (FieldDeclaration)iter.next();

      /* ES removing this:   
      if(!f.isStatic() && (f.usesTypeVariable() || isRawType())) {
        BodyDecl b = f.substitutedBodyDecl(this);
        addBodyDecl(b);
      	// Here we should access b through an ordinary
      	// child accessor instead of setting is$Final directly,
      	// however doing so appears to cause unexpected behaviour!
        b.is$Final = true;
        f = (FieldDeclaration) b;
      }
      set = set.add(f);
      * and replacing with:
      */
      if(!f.isStatic() && (f.usesTypeVariable() || isRawType())) {
        BodyDecl fCopy = ((BodyDeclList)getBodyDeclList()).localFieldCopy(f, this);
        f = (FieldDeclaration) fCopy;
      }
      set = set.add(f);

    }
    return set;
  }
  protected java.util.Map localTypeDecls_String_values;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1154
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localTypeDecls(String name) {
    Object _parameters = name;
    if(localTypeDecls_String_values == null) localTypeDecls_String_values = new java.util.HashMap(4);
    ASTNode$State.CircularValue _value;
    if(localTypeDecls_String_values.containsKey(_parameters)) {
      Object _o = localTypeDecls_String_values.get(_parameters);
      if(!(_o instanceof ASTNode$State.CircularValue)) {
        return (SimpleSet)_o;
      }
      else
        _value = (ASTNode$State.CircularValue)_o;
    }
    else {
      _value = new ASTNode$State.CircularValue();
      localTypeDecls_String_values.put(_parameters, _value);
      _value.value = SimpleSet.emptySet;
    }
    ASTNode$State state = state();
    if (!state.IN_CIRCLE) {
      state.IN_CIRCLE = true;
      int num = state.boundariesCrossed;
      boolean isFinal = this.is$Final();
      SimpleSet new_localTypeDecls_String_value;
      do {
        _value.visited = new Integer(state.CIRCLE_INDEX);
        state.CHANGE = false;
        new_localTypeDecls_String_value = localTypeDecls_compute(name);
        if ((new_localTypeDecls_String_value==null && (SimpleSet)_value.value!=null) || (new_localTypeDecls_String_value!=null && !new_localTypeDecls_String_value.equals((SimpleSet)_value.value))) {
          state.CHANGE = true;
          _value.value = new_localTypeDecls_String_value;
        }
        state.CIRCLE_INDEX++;
      } while (state.CHANGE);
        if(true) {
        localTypeDecls_String_values.put(_parameters, new_localTypeDecls_String_value);
      }
      else {
        localTypeDecls_String_values.remove(_parameters);
      state.RESET_CYCLE = true;
      localTypeDecls_compute(name);
      state.RESET_CYCLE = false;
      }
      state.IN_CIRCLE = false; 
      return new_localTypeDecls_String_value;
    }
    if(!new Integer(state.CIRCLE_INDEX).equals(_value.visited)) {
      _value.visited = new Integer(state.CIRCLE_INDEX);
      SimpleSet new_localTypeDecls_String_value = localTypeDecls_compute(name);
      if (state.RESET_CYCLE) {
        localTypeDecls_String_values.remove(_parameters);
      }
      else if ((new_localTypeDecls_String_value==null && (SimpleSet)_value.value!=null) || (new_localTypeDecls_String_value!=null && !new_localTypeDecls_String_value.equals((SimpleSet)_value.value))) {
        state.CHANGE = true;
        _value.value = new_localTypeDecls_String_value;
      }
      return new_localTypeDecls_String_value;
    }
    return (SimpleSet)_value.value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localTypeDecls_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(Iterator iter = original().localTypeDecls(name).iterator(); iter.hasNext(); ) {
      TypeDecl t = (TypeDecl)iter.next();

      /* ES: removing this:
      if(t.isStatic())
        set = set.add(t);
      else {
        BodyDecl b;
        TypeDecl typeDecl;
        if(t instanceof ClassDecl) {
          ClassDecl classDecl = (ClassDecl)t;
          typeDecl = classDecl.substitutedClassDecl(this);
          b = new MemberClassDecl((ClassDecl)typeDecl);
          addBodyDecl(b);
      	  // Here we should access b through an ordinary
      	  // child accessor instead of setting is$Final directly,
      	  // however doing so appears to cause unexpected behaviour!
      	  b.is$Final = true;
          set = set.add(typeDecl);
        }
        else if(t instanceof InterfaceDecl) {
          InterfaceDecl interfaceDecl = (InterfaceDecl)t;
          typeDecl = interfaceDecl.substitutedInterfaceDecl(this);
          b = new MemberInterfaceDecl((InterfaceDecl)typeDecl);
          addBodyDecl(b);
      	  // Here we should access b through an ordinary
      	  // child accessor instead of setting is$Final directly,
      	  // however doing so appears to cause unexpected behaviour!
      	  b.is$Final = true;
          set = set.add(typeDecl);
        }
      }
      * and replacing with:
      */
      if(t.isStatic()) {
        set = set.add(t);
      } else if (t instanceof ClassDecl) {
        MemberClassDecl copy = ((BodyDeclList)getBodyDeclList()).localClassDeclCopy((ClassDecl)t, this);
        set = set.add(copy.getClassDecl());
      } else if (t instanceof InterfaceDecl) {
        MemberInterfaceDecl copy = ((BodyDeclList)getBodyDeclList()).localInterfaceDeclCopy((InterfaceDecl)t, this);
        set = set.add(copy.getInterfaceDecl());
      }
    }
    return set;
  }
  /**
   * @apilevel internal
   */
  protected boolean constructors_computed = false;
  /**
   * @apilevel internal
   */
  protected Collection constructors_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1213
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection constructors() {
    if(constructors_computed) {
      return constructors_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    constructors_value = constructors_compute();
      if(isFinal && num == state().boundariesCrossed) constructors_computed = true;
    return constructors_value;
  }
  /**
   * @apilevel internal
   */
  private Collection constructors_compute() {
    Collection set = new ArrayList();
    for(Iterator iter = original().constructors().iterator(); iter.hasNext(); ) {
      ConstructorDecl c = (ConstructorDecl)iter.next();

      /* ES: removing this:
      BodyDecl b = c.substitutedBodyDecl(this);
      addBodyDecl(b);
      // Here we should access b through an ordinary
      // child accessor instead of setting is$Final directly,
      // however doing so appears to cause unexpected behaviour!
      b.is$Final = true;
      * and replacing with:
      */
      BodyDecl b = ((BodyDeclList)getBodyDeclList()).constructorCopy(c, this);
      set.add(b);
    }
    return set;
  }
  /**
   * @apilevel internal
   */
  protected boolean genericDecl_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl genericDecl_value;
  /**
   * @attribute inh
   * @aspect GenericsParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:45
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl genericDecl() {
    if(genericDecl_computed) {
      return genericDecl_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    genericDecl_value = getParent().Define_TypeDecl_genericDecl(this, null);
      if(isFinal && num == state().boundariesCrossed) genericDecl_computed = true;
    return genericDecl_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:477
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getArgumentListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return NameType.TYPE_NAME;
  }
    else {      return super.Define_NameType_nameType(caller, child);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:59
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_genericDecl(ASTNode caller, ASTNode child) {
    if(caller == getBodyDeclListNoTransform())  { 
    int index = caller.getIndexOfChild(child);
    {
    if(getBodyDecl(index) instanceof MemberTypeDecl) {
      MemberTypeDecl m = (MemberTypeDecl)getBodyDecl(index);
      return extractSingleType(genericDecl().memberTypes(m.typeDecl().name()));
    }
    return genericDecl();
  }
  }
    else {      return getParent().Define_TypeDecl_genericDecl(this, caller);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
