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
 * @production GenericInterfaceDecl : {@link InterfaceDecl} ::= <span class="component">{@link Modifiers}</span> <span class="component">&lt;ID:String&gt;</span> <span class="component">SuperInterfaceId:{@link Access}*</span> <span class="component">{@link BodyDecl}*</span> <span class="component">TypeParameter:{@link TypeVariable}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.ast:3
 */
public class GenericInterfaceDecl extends InterfaceDecl implements Cloneable, GenericTypeDecl {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    rawType_computed = false;
    rawType_value = null;
    lookupParTypeDecl_ArrayList_values = null;
    lookupParTypeDecl_ArrayList_list = null;    usesTypeVariable_visited = -1;
    usesTypeVariable_computed = false;
    usesTypeVariable_initialized = false;
    subtype_TypeDecl_values = null;
    instanceOf_TypeDecl_values = null;
    lookupParTypeDecl_ParTypeAccess_values = null;
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
  public GenericInterfaceDecl clone() throws CloneNotSupportedException {
    GenericInterfaceDecl node = (GenericInterfaceDecl)super.clone();
    node.rawType_computed = false;
    node.rawType_value = null;
    node.lookupParTypeDecl_ArrayList_values = null;
    node.lookupParTypeDecl_ArrayList_list = null;    node.usesTypeVariable_visited = -1;
    node.usesTypeVariable_computed = false;
    node.usesTypeVariable_initialized = false;
    node.subtype_TypeDecl_values = null;
    node.instanceOf_TypeDecl_values = null;
    node.lookupParTypeDecl_ParTypeAccess_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GenericInterfaceDecl copy() {
    try {
      GenericInterfaceDecl node = (GenericInterfaceDecl) clone();
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
  public GenericInterfaceDecl fullCopy() {
    GenericInterfaceDecl tree = (GenericInterfaceDecl) copy();
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
   * @aspect GenericsTypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:430
   */
  public void typeCheck() {
    super.typeCheck();
    if(instanceOf(typeThrowable()))
      error(" generic interface " + typeName() + " may not directly or indirectly inherit java.lang.Throwable");
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1346
   */
  public InterfaceDecl substitutedInterfaceDecl(Parameterization parTypeDecl) {
    GenericInterfaceDecl c = new GenericInterfaceDeclSubstituted(
      (Modifiers)getModifiers().fullCopy(),
      getID(),
      getSuperInterfaceIdList().substitute(parTypeDecl),
     // ES:  new List(),
      new List(), // delegates TypeParameter lookup to original
      this
    );
    return c;
  }
  /**
   * @ast method 
   * @aspect GenericsPrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsPrettyPrint.jrag:115
   */
  public void toString(StringBuffer s) {
		getModifiers().toString(s);
		s.append("interface " + getID());
		s.append('<');
    	if (getNumTypeParameter() > 0) {
    		getTypeParameter(0).toString(s);
    		for (int i = 1; i < getNumTypeParameter(); i++) {
    			s.append(", ");
    			getTypeParameter(i).toString(s);
    		}
    	}
    	s.append('>');
		if(getNumSuperInterfaceId() > 0) {
			s.append(" extends ");
			getSuperInterfaceId(0).toString(s);
      for(int i = 1; i < getNumSuperInterfaceId(); i++) {
        s.append(", ");
			  getSuperInterfaceId(i).toString(s);
      }
    }

    /*

    s.append(" instantiated with: ");
    for(int i = 0; i < getNumParTypeDecl(); i++) {
      if(i != 0) s.append(", ");
      ParTypeDecl decl = getParTypeDecl(i);
      s.append("<");
      for(int j = 0; j < decl.getNumArgument(); j++) {
        if(j != 0) s.append(", ");
        s.append(decl.getArgument(j).type().fullName());
      }
      s.append(">");
    }
    */
    
		ppBodyDecls(s);
    
    /*
    for(int i = 0; i < getNumParTypeDecl(); i++) {
      ParInterfaceDecl decl = getParTypeDecl(i);
      decl.toString(s);
    }
    */
	}
  /**
   * @ast method 
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:235
   */
  public TypeDecl makeGeneric(Signatures.ClassSignature s) {
    return (TypeDecl)this;
  }
  /**
   * @ast method 
   * @aspect GenericsNameBinding
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:489
   */
  public SimpleSet addTypeVariables(SimpleSet c, String name) {
    GenericTypeDecl original = (GenericTypeDecl)original();
    for(int i = 0; i < original.getNumTypeParameter(); i++) {
      TypeVariable p = original.getTypeParameter(i);
      if(p.name().equals(name))
        c = c.add(p);
    }
    return c;
  }
  /**
   * @ast method 
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:799
   */
  public List createArgumentList(ArrayList params) {
    GenericTypeDecl original = (GenericTypeDecl)original();
    List list = new List();
    if(params.isEmpty()) {
      // Change: Don't add any thing to the list. 
      // Concern: The previous version seem to add the erasure of the type variable for some reason,  
      // maybe this is how the raw type is represented (?), but this doesn't really comply with the 
      // claim that raw types don't have any type variables...?
      for(int i = 0; i < original.getNumTypeParameter(); i++)
        list.add(original.getTypeParameter(i).erasure().createBoundAccess());
    } else
      for(Iterator iter = params.iterator(); iter.hasNext(); )
        list.add(((TypeDecl)iter.next()).createBoundAccess());
    return list;
  }
  /**
   * @ast method 
   * 
   */
  public GenericInterfaceDecl() {
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
  public GenericInterfaceDecl(Modifiers p0, String p1, List<Access> p2, List<BodyDecl> p3, List<TypeVariable> p4) {
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
  public GenericInterfaceDecl(Modifiers p0, beaver.Symbol p1, List<Access> p2, List<BodyDecl> p3, List<TypeVariable> p4) {
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
   * Replaces the SuperInterfaceId list.
   * @param list The new list node to be used as the SuperInterfaceId list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setSuperInterfaceIdList(List<Access> list) {
    setChild(list, 1);
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
   * @return The node representing the SuperInterfaceId list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getSuperInterfaceIdList() {
    List<Access> list = (List<Access>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the SuperInterfaceId list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the SuperInterfaceId list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<Access> getSuperInterfaceIdListNoTransform() {
    return (List<Access>)getChildNoTransform(1);
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
   * Replaces the TypeParameter list.
   * @param list The new list node to be used as the TypeParameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeParameterList(List<TypeVariable> list) {
    setChild(list, 3);
  }
  /**
   * Retrieves the number of children in the TypeParameter list.
   * @return Number of children in the TypeParameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumTypeParameter() {
    return getTypeParameterList().getNumChild();
  }
  /**
   * Retrieves the number of children in the TypeParameter list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the TypeParameter list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumTypeParameterNoTransform() {
    return getTypeParameterListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the TypeParameter list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the TypeParameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeVariable getTypeParameter(int i) {
    return (TypeVariable)getTypeParameterList().getChild(i);
  }
  /**
   * Append an element to the TypeParameter list.
   * @param node The element to append to the TypeParameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addTypeParameter(TypeVariable node) {
    List<TypeVariable> list = (parent == null || state == null) ? getTypeParameterListNoTransform() : getTypeParameterList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addTypeParameterNoTransform(TypeVariable node) {
    List<TypeVariable> list = getTypeParameterListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the TypeParameter list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeParameter(TypeVariable node, int i) {
    List<TypeVariable> list = getTypeParameterList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the TypeParameter list.
   * @return The node representing the TypeParameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<TypeVariable> getTypeParameters() {
    return getTypeParameterList();
  }
  /**
   * Retrieves the TypeParameter list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the TypeParameter list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<TypeVariable> getTypeParametersNoTransform() {
    return getTypeParameterListNoTransform();
  }
  /**
   * Retrieves the TypeParameter list.
   * @return The node representing the TypeParameter list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeVariable> getTypeParameterList() {
    List<TypeVariable> list = (List<TypeVariable>)getChild(3);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the TypeParameter list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the TypeParameter list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeVariable> getTypeParameterListNoTransform() {
    return (List<TypeVariable>)getChildNoTransform(3);
  }
  /**
   * @apilevel internal
   */
  protected boolean rawType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl rawType_value;
  /**
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:177
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl rawType() {
    if(rawType_computed) {
      return rawType_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    rawType_value = rawType_compute();
      if(isFinal && num == state().boundariesCrossed) rawType_computed = true;
    return rawType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl rawType_compute() {  return lookupParTypeDecl(new ArrayList());  }
  /**
   * @apilevel internal
   */
  protected java.util.Map lookupParTypeDecl_ArrayList_values;
  /**
   * @apilevel internal
   */
  protected List lookupParTypeDecl_ArrayList_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:734
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupParTypeDecl(ArrayList list) {
    Object _parameters = list;
    if(lookupParTypeDecl_ArrayList_values == null) lookupParTypeDecl_ArrayList_values = new java.util.HashMap(4);
    if(lookupParTypeDecl_ArrayList_values.containsKey(_parameters)) {
      return (TypeDecl)lookupParTypeDecl_ArrayList_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    TypeDecl lookupParTypeDecl_ArrayList_value = lookupParTypeDecl_compute(list);
    if(lookupParTypeDecl_ArrayList_list == null) {
      lookupParTypeDecl_ArrayList_list = new List();
      lookupParTypeDecl_ArrayList_list.is$Final = true;
      lookupParTypeDecl_ArrayList_list.setParent(this);
    }
    lookupParTypeDecl_ArrayList_list.add(lookupParTypeDecl_ArrayList_value);
    if(lookupParTypeDecl_ArrayList_value != null) {
      lookupParTypeDecl_ArrayList_value.is$Final = true;
    }
      if(true) lookupParTypeDecl_ArrayList_values.put(_parameters, lookupParTypeDecl_ArrayList_value);
    return lookupParTypeDecl_ArrayList_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl lookupParTypeDecl_compute(ArrayList list) {
    /*
    int size = createParTypeDeclStub_ArrayList_list != null ? createParTypeDeclStub_ArrayList_list.numChildren : 0;
    ParInterfaceDecl typeDecl = (ParInterfaceDecl)createParTypeDeclStub(list);
    if (size < createParTypeDeclStub_ArrayList_list.numChildren) {
      createParTypeDeclBody(list, typeDecl);
    }
    return typeDecl;
    */
    
    ParInterfaceDecl typeDecl = list.size() == 0 ? new RawInterfaceDecl() : new ParInterfaceDecl();
    typeDecl.setModifiers((Modifiers)getModifiers().fullCopy());
    typeDecl.setID(getID());
    // ES: trying to only so this for ParInterfaceDecl and then later for RawInterfaceDecl
    if (!(typeDecl instanceof RawInterfaceDecl)) 
      typeDecl.setArgumentList(createArgumentList(list));
    return typeDecl;
    
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1078
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
  protected java.util.Map subtype_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:19
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
  private boolean subtype_compute(TypeDecl type) {  return type.supertypeGenericInterfaceDecl(this);  }
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:125
   */
  public boolean supertypeParClassDecl(ParClassDecl type) {
    ASTNode$State state = state();
    try {  return type.genericDecl().original().subtype(this);  }
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
    try {  return type.genericDecl().original().subtype(this);  }
    finally {
    }
  }
  protected java.util.Map instanceOf_TypeDecl_values;
  /**
   * @attribute syn
   * @aspect GenericsSubtype
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsSubtype.jrag:395
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
   * @attribute syn
   * @aspect Generics
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:158
   */
  public boolean isGenericType() {
    ASTNode$State state = state();
    try {  return true;  }
    finally {
    }
  }
  protected java.util.Map lookupParTypeDecl_ParTypeAccess_values;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:708
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupParTypeDecl(ParTypeAccess p) {
    Object _parameters = p;
    if(lookupParTypeDecl_ParTypeAccess_values == null) lookupParTypeDecl_ParTypeAccess_values = new java.util.HashMap(4);
    if(lookupParTypeDecl_ParTypeAccess_values.containsKey(_parameters)) {
      return (TypeDecl)lookupParTypeDecl_ParTypeAccess_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    TypeDecl lookupParTypeDecl_ParTypeAccess_value = lookupParTypeDecl_compute(p);
      if(isFinal && num == state().boundariesCrossed) lookupParTypeDecl_ParTypeAccess_values.put(_parameters, lookupParTypeDecl_ParTypeAccess_value);
    return lookupParTypeDecl_ParTypeAccess_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl lookupParTypeDecl_compute(ParTypeAccess p) {
    ArrayList typeArguments = new ArrayList();
    for(int i = 0; i < p.getNumTypeArgument(); i++)
      typeArguments.add(p.getTypeArgument(i).type());
    return lookupParTypeDecl(typeArguments);
  }
  /**
   * @attribute inh
   * @aspect GenericsTypeCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:436
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeThrowable() {
    ASTNode$State state = state();
    TypeDecl typeThrowable_value = getParent().Define_TypeDecl_typeThrowable(this, null);
    return typeThrowable_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:486
   * @apilevel internal
   */
  public boolean Define_boolean_isNestedType(ASTNode caller, ASTNode child) {
    if(caller == getTypeParameterListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return true;
  }
    else {      return super.Define_boolean_isNestedType(caller, child);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:487
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingType(ASTNode caller, ASTNode child) {
    if(caller == getTypeParameterListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return this;
  }
    else {      return super.Define_TypeDecl_enclosingType(caller, child);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:531
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getBodyDeclListNoTransform())  { 
    int index = caller.getIndexOfChild(child);
    {
    SimpleSet c = memberTypes(name);
    if(getBodyDecl(index).visibleTypeParameters())
      c = addTypeVariables(c, name);
    if(!c.isEmpty())
      return c;
    // 8.5.2
    if(isClassDecl() && isStatic() && !isTopLevelType()) {
      for(Iterator iter = lookupType(name).iterator(); iter.hasNext(); ) {
        TypeDecl d = (TypeDecl)iter.next();
        if(d.isStatic() || (d.enclosingType() != null && instanceOf(d.enclosingType()))) {
          c = c.add(d);
        }
      }
    }
    else
      c = lookupType(name);
    if(!c.isEmpty())
      return c;
    return topLevelType().lookupType(name); // Fix to search imports
    // include type parameters if not static
  }
  }
    else if(caller == getTypeParameterListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
    SimpleSet c = memberTypes(name);
    c = addTypeVariables(c, name);
    if(!c.isEmpty()) return c;
    // 8.5.2
    if(isClassDecl() && isStatic() && !isTopLevelType()) {
      for(Iterator iter = lookupType(name).iterator(); iter.hasNext(); ) {
        TypeDecl d = (TypeDecl)iter.next();
        if(d.isStatic() || (d.enclosingType() != null && instanceOf(d.enclosingType()))) {
          c = c.add(d);
        }
      }
    }
    else
      c = lookupType(name);
    if(!c.isEmpty())
      return c;
    return topLevelType().lookupType(name); // Fix to search imports
  }
  }
    else if(caller == getSuperInterfaceIdListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
    SimpleSet c = addTypeVariables(SimpleSet.emptySet, name);
    return !c.isEmpty() ? c : lookupType(name);
  }
  }
    else {      return super.Define_SimpleSet_lookupType(caller, child, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/GenericsParTypeDecl.jrag:51
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_genericDecl(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return this;
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
