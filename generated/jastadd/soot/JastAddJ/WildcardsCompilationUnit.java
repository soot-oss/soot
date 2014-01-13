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
 * @production WildcardsCompilationUnit : {@link CompilationUnit};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.ast:49
 */
public class WildcardsCompilationUnit extends CompilationUnit implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    typeWildcard_computed = false;
    typeWildcard_value = null;
    lookupWildcardExtends_TypeDecl_values = null;
    lookupWildcardExtends_TypeDecl_list = null;    lookupWildcardSuper_TypeDecl_values = null;
    lookupWildcardSuper_TypeDecl_list = null;    lookupLUBType_Collection_values = null;
    lookupLUBType_Collection_list = null;    lookupGLBType_ArrayList_values = null;
    lookupGLBType_ArrayList_list = null;  }
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
  public WildcardsCompilationUnit clone() throws CloneNotSupportedException {
    WildcardsCompilationUnit node = (WildcardsCompilationUnit)super.clone();
    node.typeWildcard_computed = false;
    node.typeWildcard_value = null;
    node.lookupWildcardExtends_TypeDecl_values = null;
    node.lookupWildcardExtends_TypeDecl_list = null;    node.lookupWildcardSuper_TypeDecl_values = null;
    node.lookupWildcardSuper_TypeDecl_list = null;    node.lookupLUBType_Collection_values = null;
    node.lookupLUBType_Collection_list = null;    node.lookupGLBType_ArrayList_values = null;
    node.lookupGLBType_ArrayList_list = null;    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public WildcardsCompilationUnit copy() {
    try {
      WildcardsCompilationUnit node = (WildcardsCompilationUnit) clone();
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
  public WildcardsCompilationUnit fullCopy() {
    WildcardsCompilationUnit tree = (WildcardsCompilationUnit) copy();
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
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1414
   */
  public static LUBType createLUBType(Collection bounds) {
    List boundList = new List();
    StringBuffer name = new StringBuffer();
    for(Iterator iter = bounds.iterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      boundList.add(typeDecl.createBoundAccess());
      name.append("& " + typeDecl.typeName());
    }
    LUBType decl = new LUBType(
      new Modifiers(new List().add(new Modifier("public"))),
      name.toString(),
      new List(),
      boundList
    );
    return decl;
  }
  /**
   * @ast method 
   * 
   */
  public WildcardsCompilationUnit() {
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
    children = new ASTNode[2];
    setChild(new List(), 0);
    setChild(new List(), 1);
  }
  /**
   * @ast method 
   * 
   */
  public WildcardsCompilationUnit(java.lang.String p0, List<ImportDecl> p1, List<TypeDecl> p2) {
    setPackageDecl(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * 
   */
  public WildcardsCompilationUnit(beaver.Symbol p0, List<ImportDecl> p1, List<TypeDecl> p2) {
    setPackageDecl(p0);
    setChild(p1, 0);
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
   * Replaces the lexeme PackageDecl.
   * @param value The new value for the lexeme PackageDecl.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setPackageDecl(java.lang.String value) {
    tokenjava_lang_String_PackageDecl = value;
  }
  /**
   * JastAdd-internal setter for lexeme PackageDecl using the Beaver parser.
   * @apilevel internal
   * @ast method 
   * 
   */
  public void setPackageDecl(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setPackageDecl is only valid for String lexemes");
    tokenjava_lang_String_PackageDecl = (String)symbol.value;
    PackageDeclstart = symbol.getStart();
    PackageDeclend = symbol.getEnd();
  }
  /**
   * Retrieves the value for the lexeme PackageDecl.
   * @return The value for the lexeme PackageDecl.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public java.lang.String getPackageDecl() {
    return tokenjava_lang_String_PackageDecl != null ? tokenjava_lang_String_PackageDecl : "";
  }
  /**
   * Replaces the ImportDecl list.
   * @param list The new list node to be used as the ImportDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setImportDeclList(List<ImportDecl> list) {
    setChild(list, 0);
  }
  /**
   * Retrieves the number of children in the ImportDecl list.
   * @return Number of children in the ImportDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumImportDecl() {
    return getImportDeclList().getNumChild();
  }
  /**
   * Retrieves the number of children in the ImportDecl list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the ImportDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumImportDeclNoTransform() {
    return getImportDeclListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the ImportDecl list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the ImportDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ImportDecl getImportDecl(int i) {
    return (ImportDecl)getImportDeclList().getChild(i);
  }
  /**
   * Append an element to the ImportDecl list.
   * @param node The element to append to the ImportDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addImportDecl(ImportDecl node) {
    List<ImportDecl> list = (parent == null || state == null) ? getImportDeclListNoTransform() : getImportDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addImportDeclNoTransform(ImportDecl node) {
    List<ImportDecl> list = getImportDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the ImportDecl list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setImportDecl(ImportDecl node, int i) {
    List<ImportDecl> list = getImportDeclList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the ImportDecl list.
   * @return The node representing the ImportDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<ImportDecl> getImportDecls() {
    return getImportDeclList();
  }
  /**
   * Retrieves the ImportDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the ImportDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<ImportDecl> getImportDeclsNoTransform() {
    return getImportDeclListNoTransform();
  }
  /**
   * Retrieves the ImportDecl list.
   * @return The node representing the ImportDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ImportDecl> getImportDeclList() {
    List<ImportDecl> list = (List<ImportDecl>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the ImportDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the ImportDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ImportDecl> getImportDeclListNoTransform() {
    return (List<ImportDecl>)getChildNoTransform(0);
  }
  /**
   * Replaces the TypeDecl list.
   * @param list The new list node to be used as the TypeDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeDeclList(List<TypeDecl> list) {
    setChild(list, 1);
  }
  /**
   * Retrieves the number of children in the TypeDecl list.
   * @return Number of children in the TypeDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public int getNumTypeDecl() {
    return getTypeDeclList().getNumChild();
  }
  /**
   * Retrieves the number of children in the TypeDecl list.
   * Calling this method will not trigger rewrites..
   * @return Number of children in the TypeDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public int getNumTypeDeclNoTransform() {
    return getTypeDeclListNoTransform().getNumChildNoTransform();
  }
  /**
   * Retrieves the element at index {@code i} in the TypeDecl list..
   * @param i Index of the element to return.
   * @return The element at position {@code i} in the TypeDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl getTypeDecl(int i) {
    return (TypeDecl)getTypeDeclList().getChild(i);
  }
  /**
   * Append an element to the TypeDecl list.
   * @param node The element to append to the TypeDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void addTypeDecl(TypeDecl node) {
    List<TypeDecl> list = (parent == null || state == null) ? getTypeDeclListNoTransform() : getTypeDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * 
   */
  public void addTypeDeclNoTransform(TypeDecl node) {
    List<TypeDecl> list = getTypeDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Replaces the TypeDecl list element at index {@code i} with the new node {@code node}.
   * @param node The new node to replace the old list element.
   * @param i The list index of the node to be replaced.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public void setTypeDecl(TypeDecl node, int i) {
    List<TypeDecl> list = getTypeDeclList();
    list.setChild(node, i);
  }
  /**
   * Retrieves the TypeDecl list.
   * @return The node representing the TypeDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  public List<TypeDecl> getTypeDecls() {
    return getTypeDeclList();
  }
  /**
   * Retrieves the TypeDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the TypeDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  public List<TypeDecl> getTypeDeclsNoTransform() {
    return getTypeDeclListNoTransform();
  }
  /**
   * Retrieves the TypeDecl list.
   * @return The node representing the TypeDecl list.
   * @apilevel high-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeDecl> getTypeDeclList() {
    List<TypeDecl> list = (List<TypeDecl>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * Retrieves the TypeDecl list.
   * <p><em>This method does not invoke AST transformations.</em></p>
   * @return The node representing the TypeDecl list.
   * @apilevel low-level
   * @ast method 
   * 
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeDecl> getTypeDeclListNoTransform() {
    return (List<TypeDecl>)getChildNoTransform(1);
  }
  /**
   * @apilevel internal
   */
  protected boolean typeWildcard_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeWildcard_value;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1376
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeWildcard() {
    if(typeWildcard_computed) {
      return typeWildcard_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeWildcard_value = typeWildcard_compute();
    typeWildcard_value.setParent(this);
    typeWildcard_value.is$Final = true;
      if(true) typeWildcard_computed = true;
    return typeWildcard_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeWildcard_compute() {
    TypeDecl decl = new WildcardType(
      new Modifiers(new List().add(new Modifier("public"))),
      "?",
      new List()
    );
    return decl;
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map lookupWildcardExtends_TypeDecl_values;
  /**
   * @apilevel internal
   */
  protected List lookupWildcardExtends_TypeDecl_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1387
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupWildcardExtends(TypeDecl bound) {
    Object _parameters = bound;
    if(lookupWildcardExtends_TypeDecl_values == null) lookupWildcardExtends_TypeDecl_values = new java.util.HashMap(4);
    if(lookupWildcardExtends_TypeDecl_values.containsKey(_parameters)) {
      return (TypeDecl)lookupWildcardExtends_TypeDecl_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    TypeDecl lookupWildcardExtends_TypeDecl_value = lookupWildcardExtends_compute(bound);
    if(lookupWildcardExtends_TypeDecl_list == null) {
      lookupWildcardExtends_TypeDecl_list = new List();
      lookupWildcardExtends_TypeDecl_list.is$Final = true;
      lookupWildcardExtends_TypeDecl_list.setParent(this);
    }
    lookupWildcardExtends_TypeDecl_list.add(lookupWildcardExtends_TypeDecl_value);
    if(lookupWildcardExtends_TypeDecl_value != null) {
      lookupWildcardExtends_TypeDecl_value.is$Final = true;
    }
      if(true) lookupWildcardExtends_TypeDecl_values.put(_parameters, lookupWildcardExtends_TypeDecl_value);
    return lookupWildcardExtends_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl lookupWildcardExtends_compute(TypeDecl bound) {
    TypeDecl decl = new WildcardExtendsType(
      new Modifiers(new List().add(new Modifier("public"))),
      "? extends " + bound.fullName(),
      new List(),
      bound.createBoundAccess()
    );
    return decl;
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map lookupWildcardSuper_TypeDecl_values;
  /**
   * @apilevel internal
   */
  protected List lookupWildcardSuper_TypeDecl_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1400
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupWildcardSuper(TypeDecl bound) {
    Object _parameters = bound;
    if(lookupWildcardSuper_TypeDecl_values == null) lookupWildcardSuper_TypeDecl_values = new java.util.HashMap(4);
    if(lookupWildcardSuper_TypeDecl_values.containsKey(_parameters)) {
      return (TypeDecl)lookupWildcardSuper_TypeDecl_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    TypeDecl lookupWildcardSuper_TypeDecl_value = lookupWildcardSuper_compute(bound);
    if(lookupWildcardSuper_TypeDecl_list == null) {
      lookupWildcardSuper_TypeDecl_list = new List();
      lookupWildcardSuper_TypeDecl_list.is$Final = true;
      lookupWildcardSuper_TypeDecl_list.setParent(this);
    }
    lookupWildcardSuper_TypeDecl_list.add(lookupWildcardSuper_TypeDecl_value);
    if(lookupWildcardSuper_TypeDecl_value != null) {
      lookupWildcardSuper_TypeDecl_value.is$Final = true;
    }
      if(true) lookupWildcardSuper_TypeDecl_values.put(_parameters, lookupWildcardSuper_TypeDecl_value);
    return lookupWildcardSuper_TypeDecl_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl lookupWildcardSuper_compute(TypeDecl bound) {
    TypeDecl decl = new WildcardSuperType(
      new Modifiers(new List().add(new Modifier("public"))),
      "? super " + bound.fullName(),
      new List(),
      bound.createBoundAccess()
    );
    return decl;
  }
  /**
   * @apilevel internal
   */
  protected java.util.Map lookupLUBType_Collection_values;
  /**
   * @apilevel internal
   */
  protected List lookupLUBType_Collection_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1413
   */
  @SuppressWarnings({"unchecked", "cast"})
  public LUBType lookupLUBType(Collection bounds) {
    Object _parameters = bounds;
    if(lookupLUBType_Collection_values == null) lookupLUBType_Collection_values = new java.util.HashMap(4);
    if(lookupLUBType_Collection_values.containsKey(_parameters)) {
      return (LUBType)lookupLUBType_Collection_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    LUBType lookupLUBType_Collection_value = lookupLUBType_compute(bounds);
    if(lookupLUBType_Collection_list == null) {
      lookupLUBType_Collection_list = new List();
      lookupLUBType_Collection_list.is$Final = true;
      lookupLUBType_Collection_list.setParent(this);
    }
    lookupLUBType_Collection_list.add(lookupLUBType_Collection_value);
    if(lookupLUBType_Collection_value != null) {
      lookupLUBType_Collection_value.is$Final = true;
    }
      if(true) lookupLUBType_Collection_values.put(_parameters, lookupLUBType_Collection_value);
    return lookupLUBType_Collection_value;
  }
  /**
   * @apilevel internal
   */
  private LUBType lookupLUBType_compute(Collection bounds) {  return createLUBType(bounds);  }
  /**
   * @apilevel internal
   */
  protected java.util.Map lookupGLBType_ArrayList_values;
  /**
   * @apilevel internal
   */
  protected List lookupGLBType_ArrayList_list;
  /**
   * @attribute syn
   * @aspect LookupParTypeDecl
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/Generics.jrag:1452
   */
  @SuppressWarnings({"unchecked", "cast"})
  public GLBType lookupGLBType(ArrayList bounds) {
    Object _parameters = bounds;
    if(lookupGLBType_ArrayList_values == null) lookupGLBType_ArrayList_values = new java.util.HashMap(4);
    if(lookupGLBType_ArrayList_values.containsKey(_parameters)) {
      return (GLBType)lookupGLBType_ArrayList_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    GLBType lookupGLBType_ArrayList_value = lookupGLBType_compute(bounds);
    if(lookupGLBType_ArrayList_list == null) {
      lookupGLBType_ArrayList_list = new List();
      lookupGLBType_ArrayList_list.is$Final = true;
      lookupGLBType_ArrayList_list.setParent(this);
    }
    lookupGLBType_ArrayList_list.add(lookupGLBType_ArrayList_value);
    if(lookupGLBType_ArrayList_value != null) {
      lookupGLBType_ArrayList_value.is$Final = true;
    }
      if(true) lookupGLBType_ArrayList_values.put(_parameters, lookupGLBType_ArrayList_value);
    return lookupGLBType_ArrayList_value;
  }
  /**
   * @apilevel internal
   */
  private GLBType lookupGLBType_compute(ArrayList bounds) {
    List boundList = new List();
    StringBuffer name = new StringBuffer();
    for(Iterator iter = bounds.iterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      boundList.add(typeDecl.createBoundAccess());
      name.append("& " + typeDecl.typeName());
    }
    GLBType decl = new GLBType(
      new Modifiers(new List().add(new Modifier("public"))),
      name.toString(),
      new List(),
      boundList
    );
    return decl;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
