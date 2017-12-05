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
 * @production PrimitiveCompilationUnit : {@link CompilationUnit};
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:7
 */
public class PrimitiveCompilationUnit extends CompilationUnit implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    typeBoolean_computed = false;
    typeBoolean_value = null;
    typeByte_computed = false;
    typeByte_value = null;
    typeShort_computed = false;
    typeShort_value = null;
    typeChar_computed = false;
    typeChar_value = null;
    typeInt_computed = false;
    typeInt_value = null;
    typeLong_computed = false;
    typeLong_value = null;
    typeFloat_computed = false;
    typeFloat_value = null;
    typeDouble_computed = false;
    typeDouble_value = null;
    typeVoid_computed = false;
    typeVoid_value = null;
    typeNull_computed = false;
    typeNull_value = null;
    unknownType_computed = false;
    unknownType_value = null;
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
  public PrimitiveCompilationUnit clone() throws CloneNotSupportedException {
    PrimitiveCompilationUnit node = (PrimitiveCompilationUnit)super.clone();
    node.typeBoolean_computed = false;
    node.typeBoolean_value = null;
    node.typeByte_computed = false;
    node.typeByte_value = null;
    node.typeShort_computed = false;
    node.typeShort_value = null;
    node.typeChar_computed = false;
    node.typeChar_value = null;
    node.typeInt_computed = false;
    node.typeInt_value = null;
    node.typeLong_computed = false;
    node.typeLong_value = null;
    node.typeFloat_computed = false;
    node.typeFloat_value = null;
    node.typeDouble_computed = false;
    node.typeDouble_value = null;
    node.typeVoid_computed = false;
    node.typeVoid_value = null;
    node.typeNull_computed = false;
    node.typeNull_value = null;
    node.unknownType_computed = false;
    node.unknownType_value = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public PrimitiveCompilationUnit copy() {
    try {
      PrimitiveCompilationUnit node = (PrimitiveCompilationUnit) clone();
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
  public PrimitiveCompilationUnit fullCopy() {
    PrimitiveCompilationUnit tree = (PrimitiveCompilationUnit) copy();
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
  public PrimitiveCompilationUnit() {
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
  public PrimitiveCompilationUnit(java.lang.String p0, List<ImportDecl> p1, List<TypeDecl> p2) {
    setPackageDecl(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * 
   */
  public PrimitiveCompilationUnit(beaver.Symbol p0, List<ImportDecl> p1, List<TypeDecl> p2) {
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
  protected boolean typeBoolean_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeBoolean_value;
  /* ES: Replacing this with a create method used in type lookup the first time a primitive type is requested
  private boolean Program.initPrimTypes = false;
  public void Program.addPrimitiveTypes() {
    if(!initPrimTypes) {
      initPrimTypes = true;
    
    CompilationUnit u = new CompilationUnit();
    u.setPackageDecl(PRIMITIVE_PACKAGE_NAME);
    addCompilationUnit(u);

    TypeDecl classDecl = generateUnknownType();
    u.addTypeDecl(classDecl);
    TypeDecl unknown = classDecl;

    classDecl = generatePrimitiveType(new BooleanType(), "boolean", unknown);
    u.addTypeDecl(classDecl);
    
    classDecl = generatePrimitiveType(new DoubleType(), "double", unknown);
    u.addTypeDecl(classDecl);
    
    classDecl = generatePrimitiveType(new FloatType(), "float", classDecl);
    u.addTypeDecl(classDecl);
    
    classDecl = generatePrimitiveType(new LongType(), "long", classDecl);
    u.addTypeDecl(classDecl);
    
    classDecl = generatePrimitiveType(new IntType(), "int", classDecl);
    u.addTypeDecl(classDecl);
    TypeDecl intDecl = classDecl;
    
    classDecl = generatePrimitiveType(new ShortType(), "short", classDecl);
    u.addTypeDecl(classDecl);
    
    classDecl = generatePrimitiveType(new ByteType(), "byte", classDecl);
    u.addTypeDecl(classDecl);
    
    classDecl = generatePrimitiveType(new CharType(), "char", intDecl);
    u.addTypeDecl(classDecl);
    
    classDecl = new NullType();
    classDecl.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    classDecl.setID("null");
    u.addTypeDecl(classDecl);

    classDecl = new VoidType();
    classDecl.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    classDecl.setID("void");
    u.addTypeDecl(classDecl);

    }
  }


  public TypeDecl Program.generatePrimitiveType(PrimitiveType type, String name, TypeDecl superType) {
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID(name);
    if(superType != null)
      type.setSuperClassAccess(superType.createQualifiedAccess());
    return type;
  }

  private TypeDecl Program.generateUnknownType() {
    ClassDecl classDecl = new UnknownType();
    classDecl.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    classDecl.setID("Unknown");
    MethodDecl methodDecl = new MethodDecl(
        new Modifiers(new List().add(
          new Modifier("public")
        )),
        new PrimitiveTypeAccess("Unknown"),
        "unknown",
        new List(),
        new List(),
        new Opt()
    );
    classDecl.addBodyDecl(methodDecl);
    FieldDeclaration fieldDecl = new FieldDeclaration(
        new Modifiers(new List().add(
          new Modifier("public")
        )),
        new PrimitiveTypeAccess("Unknown"),
        "unknown",
        new Opt()
    );
    classDecl.addBodyDecl(fieldDecl);   
    ConstructorDecl constrDecl = new ConstructorDecl(
      new Modifiers(new List().add(new Modifier("public"))),
      "Unknown",
      new List(),
      new List(),
      new Opt(),
      new Block()
    );
    classDecl.addBodyDecl(constrDecl);
      
    return classDecl;
  }

* @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:113
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeBoolean() {
    if(typeBoolean_computed) {
      return typeBoolean_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeBoolean_value = typeBoolean_compute();
    typeBoolean_value.setParent(this);
    typeBoolean_value.is$Final = true;
      if(true) typeBoolean_computed = true;
    return typeBoolean_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeBoolean_compute() {
    BooleanType type = new BooleanType();
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID("boolean");
    type.setSuperClassAccess(unknownType().createQualifiedAccess());
    return type;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeByte_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeByte_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:120
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeByte() {
    if(typeByte_computed) {
      return typeByte_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeByte_value = typeByte_compute();
    typeByte_value.setParent(this);
    typeByte_value.is$Final = true;
      if(true) typeByte_computed = true;
    return typeByte_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeByte_compute() {
    ByteType type = new ByteType();
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID("byte");
    type.setSuperClassAccess(typeShort().createQualifiedAccess());
    return type;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeShort_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeShort_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:127
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeShort() {
    if(typeShort_computed) {
      return typeShort_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeShort_value = typeShort_compute();
    typeShort_value.setParent(this);
    typeShort_value.is$Final = true;
      if(true) typeShort_computed = true;
    return typeShort_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeShort_compute() {
    ShortType type = new ShortType();
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID("short");
    type.setSuperClassAccess(typeInt().createQualifiedAccess());
    return type;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeChar_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeChar_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:134
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeChar() {
    if(typeChar_computed) {
      return typeChar_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeChar_value = typeChar_compute();
    typeChar_value.setParent(this);
    typeChar_value.is$Final = true;
      if(true) typeChar_computed = true;
    return typeChar_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeChar_compute() {
    CharType type = new CharType();
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID("char");
    type.setSuperClassAccess(typeInt().createQualifiedAccess());
    return type;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeInt_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeInt_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:141
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeInt() {
    if(typeInt_computed) {
      return typeInt_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeInt_value = typeInt_compute();
    typeInt_value.setParent(this);
    typeInt_value.is$Final = true;
      if(true) typeInt_computed = true;
    return typeInt_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeInt_compute() {
    IntType type = new IntType();
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID("int");
    type.setSuperClassAccess(typeLong().createQualifiedAccess());
    return type;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeLong_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeLong_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:148
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeLong() {
    if(typeLong_computed) {
      return typeLong_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeLong_value = typeLong_compute();
    typeLong_value.setParent(this);
    typeLong_value.is$Final = true;
      if(true) typeLong_computed = true;
    return typeLong_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeLong_compute() {
    LongType type = new LongType();
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID("long");
    // Float doesn't seem right here, keeping it because the old code does this
    type.setSuperClassAccess(typeFloat().createQualifiedAccess()); 
    return type;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeFloat_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeFloat_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:156
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeFloat() {
    if(typeFloat_computed) {
      return typeFloat_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeFloat_value = typeFloat_compute();
    typeFloat_value.setParent(this);
    typeFloat_value.is$Final = true;
      if(true) typeFloat_computed = true;
    return typeFloat_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeFloat_compute() {
    FloatType type = new FloatType();
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID("float");
    type.setSuperClassAccess(typeDouble().createQualifiedAccess());
    return type;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeDouble_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeDouble_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:163
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeDouble() {
    if(typeDouble_computed) {
      return typeDouble_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeDouble_value = typeDouble_compute();
    typeDouble_value.setParent(this);
    typeDouble_value.is$Final = true;
      if(true) typeDouble_computed = true;
    return typeDouble_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeDouble_compute() {
    DoubleType type = new DoubleType();
    type.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    type.setID("double");
    type.setSuperClassAccess(unknownType().createQualifiedAccess());
    return type;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeVoid_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeVoid_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:170
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeVoid() {
    if(typeVoid_computed) {
      return typeVoid_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeVoid_value = typeVoid_compute();
    typeVoid_value.setParent(this);
    typeVoid_value.is$Final = true;
      if(true) typeVoid_computed = true;
    return typeVoid_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeVoid_compute() {
    VoidType classDecl = new VoidType();
    classDecl.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    classDecl.setID("void");
    return classDecl;
  }
  /**
   * @apilevel internal
   */
  protected boolean typeNull_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl typeNull_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:176
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl typeNull() {
    if(typeNull_computed) {
      return typeNull_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    typeNull_value = typeNull_compute();
    typeNull_value.setParent(this);
    typeNull_value.is$Final = true;
      if(true) typeNull_computed = true;
    return typeNull_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl typeNull_compute() {
    NullType classDecl = new NullType();
    classDecl.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    classDecl.setID("null");
    return classDecl;
  }
  /**
   * @apilevel internal
   */
  protected boolean unknownType_computed = false;
  /**
   * @apilevel internal
   */
  protected TypeDecl unknownType_value;
  /**
   * @attribute syn
   * @aspect PrimitiveTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrimitiveTypes.jrag:182
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl unknownType() {
    if(unknownType_computed) {
      return unknownType_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    unknownType_value = unknownType_compute();
    unknownType_value.setParent(this);
    unknownType_value.is$Final = true;
      if(true) unknownType_computed = true;
    return unknownType_value;
  }
  /**
   * @apilevel internal
   */
  private TypeDecl unknownType_compute() {
    ClassDecl classDecl = new UnknownType();
    classDecl.setModifiers(new Modifiers(new List().add(new Modifier("public"))));
    classDecl.setID("Unknown");
    MethodDecl methodDecl = new MethodDecl(
        new Modifiers(new List().add(
          new Modifier("public")
        )),
        new PrimitiveTypeAccess("Unknown"),
        "unknown",
        new List(),
        new List(),
        new Opt()
    );
    classDecl.addBodyDecl(methodDecl);
    FieldDeclaration fieldDecl = new FieldDeclaration(
        new Modifiers(new List().add(
          new Modifier("public")
        )),
        new PrimitiveTypeAccess("Unknown"),
        "unknown",
        new Opt()
    );
    classDecl.addBodyDecl(fieldDecl);   
    ConstructorDecl constrDecl = new ConstructorDecl(
      new Modifiers(new List().add(new Modifier("public"))),
      "Unknown",
      new List(),
      new List(),
      new Opt(),
      new Block()
    );
    classDecl.addBodyDecl(constrDecl);      
    return classDecl;
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
