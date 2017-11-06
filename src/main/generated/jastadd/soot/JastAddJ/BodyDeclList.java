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
 * @production BodyDeclList : {@link List};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.ast:13
 */
public class BodyDeclList extends List implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    localMethodSignatureCopy_MethodDecl_MemberSubstitutor_values = null;
    localMethodSignatureCopy_MethodDecl_MemberSubstitutor_list = null;    localFieldCopy_FieldDeclaration_MemberSubstitutor_values = null;
    localFieldCopy_FieldDeclaration_MemberSubstitutor_list = null;    localClassDeclCopy_ClassDecl_MemberSubstitutor_values = null;
    localClassDeclCopy_ClassDecl_MemberSubstitutor_list = null;    localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_values = null;
    localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_list = null;    constructorCopy_ConstructorDecl_MemberSubstitutor_values = null;
    constructorCopy_ConstructorDecl_MemberSubstitutor_list = null;  }
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
  public BodyDeclList clone() throws CloneNotSupportedException {
    BodyDeclList node = (BodyDeclList)super.clone();
    node.localMethodSignatureCopy_MethodDecl_MemberSubstitutor_values = null;
    node.localMethodSignatureCopy_MethodDecl_MemberSubstitutor_list = null;    node.localFieldCopy_FieldDeclaration_MemberSubstitutor_values = null;
    node.localFieldCopy_FieldDeclaration_MemberSubstitutor_list = null;    node.localClassDeclCopy_ClassDecl_MemberSubstitutor_values = null;
    node.localClassDeclCopy_ClassDecl_MemberSubstitutor_list = null;    node.localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_values = null;
    node.localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_list = null;    node.constructorCopy_ConstructorDecl_MemberSubstitutor_values = null;
    node.constructorCopy_ConstructorDecl_MemberSubstitutor_list = null;    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDeclList copy() {
    try {
      BodyDeclList node = (BodyDeclList) clone();
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
  public BodyDeclList fullCopy() {
    BodyDeclList tree = (BodyDeclList) copy();
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
   * 
   */
  public BodyDeclList() {
    super();

    is$Final(true);

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
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  protected int numChildren() {
    return 0;
  }
  /**
   * @apilevel internal
   * @ast method 
   * 
   */
  public boolean mayHaveRewrite() {
    return true;
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map localMethodSignatureCopy_MethodDecl_MemberSubstitutor_values;
  /**
   * @apilevel internal
   */
  protected List localMethodSignatureCopy_MethodDecl_MemberSubstitutor_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1114
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl localMethodSignatureCopy(MethodDecl originalMethod, MemberSubstitutor m) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(originalMethod);
    _parameters.add(m);
    if(localMethodSignatureCopy_MethodDecl_MemberSubstitutor_values == null) localMethodSignatureCopy_MethodDecl_MemberSubstitutor_values = new java.util.HashMap(4);
    if(localMethodSignatureCopy_MethodDecl_MemberSubstitutor_values.containsKey(_parameters)) {
      return (BodyDecl)localMethodSignatureCopy_MethodDecl_MemberSubstitutor_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    BodyDecl localMethodSignatureCopy_MethodDecl_MemberSubstitutor_value = localMethodSignatureCopy_compute(originalMethod, m);
    if(localMethodSignatureCopy_MethodDecl_MemberSubstitutor_list == null) {
      localMethodSignatureCopy_MethodDecl_MemberSubstitutor_list = new List();
      localMethodSignatureCopy_MethodDecl_MemberSubstitutor_list.is$Final = true;
      localMethodSignatureCopy_MethodDecl_MemberSubstitutor_list.setParent(this);
    }
    localMethodSignatureCopy_MethodDecl_MemberSubstitutor_list.add(localMethodSignatureCopy_MethodDecl_MemberSubstitutor_value);
    if(localMethodSignatureCopy_MethodDecl_MemberSubstitutor_value != null) {
      localMethodSignatureCopy_MethodDecl_MemberSubstitutor_value.is$Final = true;
    }
      if(true) localMethodSignatureCopy_MethodDecl_MemberSubstitutor_values.put(_parameters, localMethodSignatureCopy_MethodDecl_MemberSubstitutor_value);
    return localMethodSignatureCopy_MethodDecl_MemberSubstitutor_value;
  }
  /**
   * @apilevel internal
   */
  private BodyDecl localMethodSignatureCopy_compute(MethodDecl originalMethod, MemberSubstitutor m) {
     return originalMethod.substitutedBodyDecl(m);
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map localFieldCopy_FieldDeclaration_MemberSubstitutor_values;
  /**
   * @apilevel internal
   */
  protected List localFieldCopy_FieldDeclaration_MemberSubstitutor_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1148
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl localFieldCopy(FieldDeclaration originalDecl, MemberSubstitutor m) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(originalDecl);
    _parameters.add(m);
    if(localFieldCopy_FieldDeclaration_MemberSubstitutor_values == null) localFieldCopy_FieldDeclaration_MemberSubstitutor_values = new java.util.HashMap(4);
    if(localFieldCopy_FieldDeclaration_MemberSubstitutor_values.containsKey(_parameters)) {
      return (BodyDecl)localFieldCopy_FieldDeclaration_MemberSubstitutor_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    BodyDecl localFieldCopy_FieldDeclaration_MemberSubstitutor_value = localFieldCopy_compute(originalDecl, m);
    if(localFieldCopy_FieldDeclaration_MemberSubstitutor_list == null) {
      localFieldCopy_FieldDeclaration_MemberSubstitutor_list = new List();
      localFieldCopy_FieldDeclaration_MemberSubstitutor_list.is$Final = true;
      localFieldCopy_FieldDeclaration_MemberSubstitutor_list.setParent(this);
    }
    localFieldCopy_FieldDeclaration_MemberSubstitutor_list.add(localFieldCopy_FieldDeclaration_MemberSubstitutor_value);
    if(localFieldCopy_FieldDeclaration_MemberSubstitutor_value != null) {
      localFieldCopy_FieldDeclaration_MemberSubstitutor_value.is$Final = true;
    }
      if(true) localFieldCopy_FieldDeclaration_MemberSubstitutor_values.put(_parameters, localFieldCopy_FieldDeclaration_MemberSubstitutor_value);
    return localFieldCopy_FieldDeclaration_MemberSubstitutor_value;
  }
  /**
   * @apilevel internal
   */
  private BodyDecl localFieldCopy_compute(FieldDeclaration originalDecl, MemberSubstitutor m) {
    return originalDecl.substitutedBodyDecl(m);
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map localClassDeclCopy_ClassDecl_MemberSubstitutor_values;
  /**
   * @apilevel internal
   */
  protected List localClassDeclCopy_ClassDecl_MemberSubstitutor_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1204
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MemberClassDecl localClassDeclCopy(ClassDecl originalDecl, MemberSubstitutor m) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(originalDecl);
    _parameters.add(m);
    if(localClassDeclCopy_ClassDecl_MemberSubstitutor_values == null) localClassDeclCopy_ClassDecl_MemberSubstitutor_values = new java.util.HashMap(4);
    if(localClassDeclCopy_ClassDecl_MemberSubstitutor_values.containsKey(_parameters)) {
      return (MemberClassDecl)localClassDeclCopy_ClassDecl_MemberSubstitutor_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    MemberClassDecl localClassDeclCopy_ClassDecl_MemberSubstitutor_value = localClassDeclCopy_compute(originalDecl, m);
    if(localClassDeclCopy_ClassDecl_MemberSubstitutor_list == null) {
      localClassDeclCopy_ClassDecl_MemberSubstitutor_list = new List();
      localClassDeclCopy_ClassDecl_MemberSubstitutor_list.is$Final = true;
      localClassDeclCopy_ClassDecl_MemberSubstitutor_list.setParent(this);
    }
    localClassDeclCopy_ClassDecl_MemberSubstitutor_list.add(localClassDeclCopy_ClassDecl_MemberSubstitutor_value);
    if(localClassDeclCopy_ClassDecl_MemberSubstitutor_value != null) {
      localClassDeclCopy_ClassDecl_MemberSubstitutor_value.is$Final = true;
    }
      if(true) localClassDeclCopy_ClassDecl_MemberSubstitutor_values.put(_parameters, localClassDeclCopy_ClassDecl_MemberSubstitutor_value);
    return localClassDeclCopy_ClassDecl_MemberSubstitutor_value;
  }
  /**
   * @apilevel internal
   */
  private MemberClassDecl localClassDeclCopy_compute(ClassDecl originalDecl, MemberSubstitutor m) {
    ClassDecl copy = originalDecl.substitutedClassDecl(m);
    return new MemberClassDecl(copy);
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_values;
  /**
   * @apilevel internal
   */
  protected List localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1208
   */
  @SuppressWarnings({"unchecked", "cast"})
  public MemberInterfaceDecl localInterfaceDeclCopy(InterfaceDecl originalDecl, MemberSubstitutor m) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(originalDecl);
    _parameters.add(m);
    if(localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_values == null) localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_values = new java.util.HashMap(4);
    if(localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_values.containsKey(_parameters)) {
      return (MemberInterfaceDecl)localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    MemberInterfaceDecl localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_value = localInterfaceDeclCopy_compute(originalDecl, m);
    if(localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_list == null) {
      localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_list = new List();
      localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_list.is$Final = true;
      localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_list.setParent(this);
    }
    localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_list.add(localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_value);
    if(localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_value != null) {
      localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_value.is$Final = true;
    }
      if(true) localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_values.put(_parameters, localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_value);
    return localInterfaceDeclCopy_InterfaceDecl_MemberSubstitutor_value;
  }
  /**
   * @apilevel internal
   */
  private MemberInterfaceDecl localInterfaceDeclCopy_compute(InterfaceDecl originalDecl, MemberSubstitutor m) {
    InterfaceDecl copy = originalDecl.substitutedInterfaceDecl(m);
    return new MemberInterfaceDecl(copy);
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map constructorCopy_ConstructorDecl_MemberSubstitutor_values;
  /**
   * @apilevel internal
   */
  protected List constructorCopy_ConstructorDecl_MemberSubstitutor_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1234
   */
  @SuppressWarnings({"unchecked", "cast"})
  public BodyDecl constructorCopy(ConstructorDecl originalDecl, MemberSubstitutor m) {
    java.util.List _parameters = new java.util.ArrayList(2);
    _parameters.add(originalDecl);
    _parameters.add(m);
    if(constructorCopy_ConstructorDecl_MemberSubstitutor_values == null) constructorCopy_ConstructorDecl_MemberSubstitutor_values = new java.util.HashMap(4);
    if(constructorCopy_ConstructorDecl_MemberSubstitutor_values.containsKey(_parameters)) {
      return (BodyDecl)constructorCopy_ConstructorDecl_MemberSubstitutor_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    BodyDecl constructorCopy_ConstructorDecl_MemberSubstitutor_value = constructorCopy_compute(originalDecl, m);
    if(constructorCopy_ConstructorDecl_MemberSubstitutor_list == null) {
      constructorCopy_ConstructorDecl_MemberSubstitutor_list = new List();
      constructorCopy_ConstructorDecl_MemberSubstitutor_list.is$Final = true;
      constructorCopy_ConstructorDecl_MemberSubstitutor_list.setParent(this);
    }
    constructorCopy_ConstructorDecl_MemberSubstitutor_list.add(constructorCopy_ConstructorDecl_MemberSubstitutor_value);
    if(constructorCopy_ConstructorDecl_MemberSubstitutor_value != null) {
      constructorCopy_ConstructorDecl_MemberSubstitutor_value.is$Final = true;
    }
      if(true) constructorCopy_ConstructorDecl_MemberSubstitutor_values.put(_parameters, constructorCopy_ConstructorDecl_MemberSubstitutor_value);
    return constructorCopy_ConstructorDecl_MemberSubstitutor_value;
  }
  /**
   * @apilevel internal
   */
  private BodyDecl constructorCopy_compute(ConstructorDecl originalDecl, MemberSubstitutor m) {
    return originalDecl.substitutedBodyDecl(m);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
