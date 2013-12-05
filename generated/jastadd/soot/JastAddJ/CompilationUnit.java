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
 * @production CompilationUnit : {@link ASTNode} ::= <span class="component">&lt;PackageDecl:java.lang.String&gt;</span> <span class="component">{@link ImportDecl}*</span> <span class="component">{@link TypeDecl}*</span>;
 * @ast node
 * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/java.ast:4
 */
public class CompilationUnit extends ASTNode<ASTNode> implements Cloneable {
  /**
   * @apilevel low-level
   */
  public void flushCache() {
    super.flushCache();
    packageName_computed = false;
    packageName_value = null;
    lookupType_String_values = null;
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
  public CompilationUnit clone() throws CloneNotSupportedException {
    CompilationUnit node = (CompilationUnit)super.clone();
    node.packageName_computed = false;
    node.packageName_value = null;
    node.lookupType_String_values = null;
    node.in$Circle(false);
    node.is$Final(false);
    return node;
  }
  /**
   * @apilevel internal
   */
  @SuppressWarnings({"unchecked", "cast"})
  public CompilationUnit copy() {
    try {
      CompilationUnit node = (CompilationUnit) clone();
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
  public CompilationUnit fullCopy() {
    CompilationUnit tree = (CompilationUnit) copy();
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
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:159
   */
  

  private String relativeName;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:160
   */
  
  private String pathName;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:161
   */
  
  private boolean fromSource;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:163
   */
  public void setRelativeName(String name) {
    relativeName = name;
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:166
   */
  public void setPathName(String name) {
    pathName = name;
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:169
   */
  public void setFromSource(boolean value) {
    fromSource = value;
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:65
   */
  

  protected java.util.ArrayList errors = new java.util.ArrayList();
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:66
   */
  
  protected java.util.ArrayList warnings = new java.util.ArrayList();
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:68
   */
  public Collection parseErrors() { return parseErrors; }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:69
   */
  public void addParseError(Problem msg) { parseErrors.add(msg); }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:70
   */
  
  protected Collection parseErrors = new ArrayList();
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:228
   */
  public void errorCheck(Collection collection) {
    collectErrors();
    collection.addAll(errors);
  }
  /**
   * @ast method 
   * @aspect ErrorCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ErrorCheck.jrag:232
   */
  public void errorCheck(Collection err, Collection warn) {
    collectErrors();
    err.addAll(errors);
    warn.addAll(warnings);
  }
  /**
   * @ast method 
   * @aspect NameCheck
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:35
   */
  public void refined_NameCheck_CompilationUnit_nameCheck() {
    for(int i = 0; i < getNumImportDecl(); i++) {
      ImportDecl decl = getImportDecl(i);
      if(decl instanceof SingleTypeImportDecl) {
        TypeDecl importedType = decl.getAccess().type();
        Iterator iter = localLookupType(importedType.name()).iterator();
        while (iter.hasNext()) {
          TypeDecl local = (TypeDecl) iter.next();
          if (local != importedType)
            error("imported type " + decl + " is conflicting with visible type");
        }
      }
    }
  }
  /**
   * @ast method 
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:32
   */
  public void toString(StringBuffer s) {
    try {
      if(!getPackageDecl().equals("")) {
        s.append("package " + getPackageDecl() + ";\n");
      }
      for(int i = 0; i < getNumImportDecl(); i++) {
        getImportDecl(i).toString(s);
      }
      for(int i = 0; i < getNumTypeDecl(); i++) {
        getTypeDecl(i).toString(s);
        s.append("\n");
      }
    } catch (NullPointerException e) {
      System.out.print("Error in compilation unit hosting " + getTypeDecl(0).typeName());
      throw e;
    }
  }
  /**
   * @ast method 
   * @aspect Transformations
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Backend/Transformations.jrag:18
   */
  public void transformation() {
    if(fromSource()) {
      for(int i = 0; i < getNumTypeDecl(); i++) {
        getTypeDecl(i).transformation();
      }
    }
  }
  /**
   * @ast method 
   * @aspect ClassLoading
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddExtensions/SootJastAddJ/ClassLoading.jrag:12
   */
  

  public boolean isResolved = false;
  /**
   * @ast method 
   * 
   */
  public CompilationUnit() {
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
  public CompilationUnit(java.lang.String p0, List<ImportDecl> p1, List<TypeDecl> p2) {
    setPackageDecl(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * 
   */
  public CompilationUnit(beaver.Symbol p0, List<ImportDecl> p1, List<TypeDecl> p2) {
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
   * @apilevel internal
   * @ast method 
   * 
   */
  
  /**
   * @apilevel internal
   */
  protected java.lang.String tokenjava_lang_String_PackageDecl;
  /**
   * @ast method 
   * 
   */
  
  public int PackageDeclstart;
  /**
   * @ast method 
   * 
   */
  
  public int PackageDeclend;
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
   * @ast method 
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:181
   */
    public void nameCheck() {
    refined_NameCheck_CompilationUnit_nameCheck();
    for(int i = 0; i < getNumImportDecl(); i++) {
      if(getImportDecl(i) instanceof SingleStaticImportDecl) {
        SingleStaticImportDecl decl = (SingleStaticImportDecl)getImportDecl(i);
        String name = decl.name();
        if(!decl.importedTypes(name).isEmpty()) {
          TypeDecl type = (TypeDecl)decl.importedTypes(name).iterator().next();
          if(localLookupType(name).contains(type))
            decl.error(packageName() + "." + name + " is already defined in this compilation unit");
        }
      }
    }
  }
  /**
   * @ast method 
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:270
   */
  private SimpleSet refined_TypeScopePropagation_CompilationUnit_Child_lookupType_String(String name)
{
    // locally declared types in compilation unit
    SimpleSet set = localLookupType(name);
    if(!set.isEmpty()) return set;

    // imported types
    set = importedTypes(name);
    if(!set.isEmpty()) return set;

    // types in the same package
    TypeDecl result = lookupType(packageName(), name);
    if(result != null && result.accessibleFromPackage(packageName())) 
      return SimpleSet.emptySet.add(result);
    
    // types imported on demand
    set = importedTypesOnDemand(name);
    if(!set.isEmpty()) return set;
    
    // include primitive types
    result = lookupType(PRIMITIVE_PACKAGE_NAME, name);
    if(result != null) return SimpleSet.emptySet.add(result);
    
    // 7.5.5 Automatic Imports
    result = lookupType("java.lang", name);
    if(result != null && result.accessibleFromPackage(packageName()))
      return SimpleSet.emptySet.add(result);
    return lookupType(name);
  }
  /**
   * @attribute syn
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:27
   */
  public String relativeName() {
    ASTNode$State state = state();
    try {  return relativeName;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:28
   */
  public String pathName() {
    ASTNode$State state = state();
    try {  return pathName;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:29
   */
  public boolean fromSource() {
    ASTNode$State state = state();
    try {  return fromSource;  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:299
   */
  public SimpleSet localLookupType(String name) {
    ASTNode$State state = state();
    try {
    for(int i = 0; i < getNumTypeDecl(); i++)
      if(getTypeDecl(i).name().equals(name))
        return SimpleSet.emptySet.add(getTypeDecl(i));
    return SimpleSet.emptySet;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:306
   */
  public SimpleSet importedTypes(String name) {
    ASTNode$State state = state();
    try {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumImportDecl(); i++)
      if(!getImportDecl(i).isOnDemand())
        for(Iterator iter = getImportDecl(i).importedTypes(name).iterator(); iter.hasNext(); )
          set = set.add(iter.next());
    return set;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:314
   */
  public SimpleSet importedTypesOnDemand(String name) {
    ASTNode$State state = state();
    try {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumImportDecl(); i++)
      if(getImportDecl(i).isOnDemand())
        for(Iterator iter = getImportDecl(i).importedTypes(name).iterator(); iter.hasNext(); )
          set = set.add(iter.next());
    return set;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:800
   */
  public String dumpString() {
    ASTNode$State state = state();
    try {  return getClass().getName() + " [" + getPackageDecl() + "]";  }
    finally {
    }
  }
  /**
   * @apilevel internal
   */
  protected boolean packageName_computed = false;
  /**
   * @apilevel internal
   */
  protected String packageName_value;
  /**
   * @attribute syn
   * @aspect TypeName
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:92
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String packageName() {
    if(packageName_computed) {
      return packageName_value;
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    packageName_value = packageName_compute();
      if(isFinal && num == state().boundariesCrossed) packageName_computed = true;
    return packageName_value;
  }
  /**
   * @apilevel internal
   */
  private String packageName_compute() {return getPackageDecl();}
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:112
   */
  public SimpleSet importedFields(String name) {
    ASTNode$State state = state();
    try {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumImportDecl(); i++)
      if(!getImportDecl(i).isOnDemand())
        for(Iterator iter = getImportDecl(i).importedFields(name).iterator(); iter.hasNext(); )
          set = set.add(iter.next());
    return set;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:120
   */
  public SimpleSet importedFieldsOnDemand(String name) {
    ASTNode$State state = state();
    try {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumImportDecl(); i++)
      if(getImportDecl(i).isOnDemand())
        for(Iterator iter = getImportDecl(i).importedFields(name).iterator(); iter.hasNext(); )
          set = set.add(iter.next());
    return set;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:141
   */
  public Collection importedMethods(String name) {
    ASTNode$State state = state();
    try {
    Collection list = new ArrayList();
    for(int i = 0; i < getNumImportDecl(); i++)
      if(!getImportDecl(i).isOnDemand())
        list.addAll(getImportDecl(i).importedMethods(name));
    return list;
  }
    finally {
    }
  }
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:148
   */
  public Collection importedMethodsOnDemand(String name) {
    ASTNode$State state = state();
    try {
    Collection list = new ArrayList();
    for(int i = 0; i < getNumImportDecl(); i++)
      if(getImportDecl(i).isOnDemand())
        list.addAll(getImportDecl(i).importedMethods(name));
    return list;
  }
    finally {
    }
  }
  /**
   * @attribute inh
   * @aspect LookupFullyQualifiedTypes
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:99
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl lookupType(String packageName, String typeName) {
    ASTNode$State state = state();
    TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName, typeName);
    return lookupType_String_String_value;
  }
  protected java.util.Map lookupType_String_values;
  /**
   * @attribute inh
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:259
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupType(String name) {
    Object _parameters = name;
    if(lookupType_String_values == null) lookupType_String_values = new java.util.HashMap(4);
    if(lookupType_String_values.containsKey(_parameters)) {
      return (SimpleSet)lookupType_String_values.get(_parameters);
    }
    ASTNode$State state = state();
  int num = state.boundariesCrossed;
  boolean isFinal = this.is$Final();
    SimpleSet lookupType_String_value = getParent().Define_SimpleSet_lookupType(this, null, name);
      if(isFinal && num == state().boundariesCrossed) lookupType_String_values.put(_parameters, lookupType_String_value);
    return lookupType_String_value;
  }
  /**
   * @attribute inh
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:111
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet lookupVariable(String name) {
    ASTNode$State state = state();
    SimpleSet lookupVariable_String_value = getParent().Define_SimpleSet_lookupVariable(this, null, name);
    return lookupVariable_String_value;
  }
  /**
   * @attribute inh
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:140
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection lookupMethod(String name) {
    ASTNode$State state = state();
    Collection lookupMethod_String_value = getParent().Define_Collection_lookupMethod(this, null, name);
    return lookupMethod_String_value;
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:32
   * @apilevel internal
   */
  public CompilationUnit Define_CompilationUnit_compilationUnit(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return this;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:51
   * @apilevel internal
   */
  public boolean Define_boolean_isIncOrDec(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return false;
  }
    else {      return getParent().Define_boolean_isIncOrDec(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:198
   * @apilevel internal
   */
  public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    if(caller == getImportDeclListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
    return !exceptionType.isUncheckedException();
  }
  }
    else if(caller == getTypeDeclListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return !exceptionType.isUncheckedException();
  }
    else {      return getParent().Define_boolean_handlesException(this, caller, exceptionType);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:355
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getImportDeclListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return lookupType(name);
  }
    else  { 
   int childIndex = this.getIndexOfChild(caller);
{
    SimpleSet result = SimpleSet.emptySet;
    for(Iterator iter = refined_TypeScopePropagation_CompilationUnit_Child_lookupType_String(name).iterator(); iter.hasNext(); ) {
      TypeDecl typeDecl = (TypeDecl)iter.next();
      if(typeDecl instanceof ParTypeDecl)
        result = result.add(((ParTypeDecl)typeDecl).genericDecl());
      else
        result = result.add(typeDecl);
    }
    return result;
  }
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:27
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_allImportedTypes(ASTNode caller, ASTNode child, String name) {
    if(caller == getImportDeclListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return importedTypes(name);
  }
    else {      return getParent().Define_SimpleSet_allImportedTypes(this, caller, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:90
   * @apilevel internal
   */
  public String Define_String_packageName(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return packageName();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:69
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getImportDeclListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return NameType.PACKAGE_NAME;
  }
    else {      return getParent().Define_NameType_nameType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:493
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:519
   * @apilevel internal
   */
  public boolean Define_boolean_isNestedType(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:529
   * @apilevel internal
   */
  public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return false;
  }
    else {      return getParent().Define_boolean_isMemberType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:541
   * @apilevel internal
   */
  public boolean Define_boolean_isLocalClass(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:563
   * @apilevel internal
   */
  public String Define_String_hostPackage(ASTNode caller, ASTNode child) {
     {
      int childIndex = this.getIndexOfChild(caller);
      return packageName();
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:583
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
    if(caller == getImportDeclListNoTransform())  {
    int childIndex = caller.getIndexOfChild(child);
    return null;
  }
    else {      return getParent().Define_TypeDecl_hostType(this, caller);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:104
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getTypeDeclListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
    SimpleSet set = importedFields(name);
    if(!set.isEmpty()) return set;
    set = importedFieldsOnDemand(name);
    if(!set.isEmpty()) return set;
    return lookupVariable(name);
  }
  }
    else {      return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
    }
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:133
   * @apilevel internal
   */
  public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
    if(caller == getTypeDeclListNoTransform())  { 
    int childIndex = caller.getIndexOfChild(child);
    {
    Collection list = importedMethods(name);
    if(!list.isEmpty()) return list;
    list = importedMethodsOnDemand(name);
    if(!list.isEmpty()) return list;
    return lookupMethod(name);
  }
  }
    else {      return getParent().Define_Collection_lookupMethod(this, caller, name);
    }
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
