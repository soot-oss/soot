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
 * @declaredat java.ast:4
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
        CompilationUnit node = (CompilationUnit)clone();
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
  public CompilationUnit fullCopy() {
    CompilationUnit res = (CompilationUnit)copy();
    for(int i = 0; i < getNumChildNoTransform(); i++) {
      ASTNode node = getChildNoTransform(i);
      if(node != null) node = node.fullCopy();
      res.setChild(node, i);
    }
    return res;
    }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:153
   */
  

  private String relativeName;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:154
   */
  
  private String pathName;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:155
   */
  
  private boolean fromSource;
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:157
   */
  public void setRelativeName(String name) {
    relativeName = name;
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:160
   */
  public void setPathName(String name) {
    pathName = name;
  }
  /**
   * @ast method 
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:163
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
        if(localLookupType(decl.getAccess().type().name()).contains(decl.getAccess().type()))
          error("" + decl + " is conflicting with visible type");
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
   * @declaredat java.ast:1
   */
  public CompilationUnit() {
    super();

    setChild(new List(), 0);
    setChild(new List(), 1);

  }
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  public CompilationUnit(java.lang.String p0, List<ImportDecl> p1, List<TypeDecl> p2) {
    setPackageDecl(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @ast method 
   * @declaredat java.ast:14
   */
  public CompilationUnit(beaver.Symbol p0, List<ImportDecl> p1, List<TypeDecl> p2) {
    setPackageDecl(p0);
    setChild(p1, 0);
    setChild(p2, 1);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:22
   */
  protected int numChildren() {
    return 2;
  }
  /**
   * @apilevel internal
   * @ast method 
   * @declaredat java.ast:28
   */
  public boolean mayHaveRewrite() {
    return false;
  }
  /**
   * Setter for lexeme PackageDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setPackageDecl(java.lang.String value) {
    tokenjava_lang_String_PackageDecl = value;
  }
  /**   * @apilevel internal   * @ast method 
   * @declaredat java.ast:8
   */
  
  /**   * @apilevel internal   */  protected java.lang.String tokenjava_lang_String_PackageDecl;
  /**
   * @ast method 
   * @declaredat java.ast:9
   */
  
  public int PackageDeclstart;
  /**
   * @ast method 
   * @declaredat java.ast:10
   */
  
  public int PackageDeclend;
  /**
   * @ast method 
   * @declaredat java.ast:11
   */
  public void setPackageDecl(beaver.Symbol symbol) {
    if(symbol.value != null && !(symbol.value instanceof String))
      throw new UnsupportedOperationException("setPackageDecl is only valid for String lexemes");
    tokenjava_lang_String_PackageDecl = (String)symbol.value;
    PackageDeclstart = symbol.getStart();
    PackageDeclend = symbol.getEnd();
  }
  /**
   * Getter for lexeme PackageDecl
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:22
   */
  public java.lang.String getPackageDecl() {
    return tokenjava_lang_String_PackageDecl != null ? tokenjava_lang_String_PackageDecl : "";
  }
  /**
   * Setter for ImportDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setImportDeclList(List<ImportDecl> list) {
    setChild(list, 0);
  }
  /**
   * @return number of children in ImportDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumImportDecl() {
    return getImportDeclList().getNumChild();
  }
  /**
   * Getter for child in list ImportDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public ImportDecl getImportDecl(int i) {
    return (ImportDecl)getImportDeclList().getChild(i);
  }
  /**
   * Add element to list ImportDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addImportDecl(ImportDecl node) {
    List<ImportDecl> list = (parent == null || state == null) ? getImportDeclListNoTransform() : getImportDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addImportDeclNoTransform(ImportDecl node) {
    List<ImportDecl> list = getImportDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list ImportDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setImportDecl(ImportDecl node, int i) {
    List<ImportDecl> list = getImportDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for ImportDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<ImportDecl> getImportDecls() {
    return getImportDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<ImportDecl> getImportDeclsNoTransform() {
    return getImportDeclListNoTransform();
  }
  /**
   * Getter for list ImportDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ImportDecl> getImportDeclList() {
    List<ImportDecl> list = (List<ImportDecl>)getChild(0);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<ImportDecl> getImportDeclListNoTransform() {
    return (List<ImportDecl>)getChildNoTransform(0);
  }
  /**
   * Setter for TypeDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:5
   */
  public void setTypeDeclList(List<TypeDecl> list) {
    setChild(list, 1);
  }
  /**
   * @return number of children in TypeDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:12
   */
  public int getNumTypeDecl() {
    return getTypeDeclList().getNumChild();
  }
  /**
   * Getter for child in list TypeDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:19
   */
  @SuppressWarnings({"unchecked", "cast"})
  public TypeDecl getTypeDecl(int i) {
    return (TypeDecl)getTypeDeclList().getChild(i);
  }
  /**
   * Add element to list TypeDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:27
   */
  public void addTypeDecl(TypeDecl node) {
    List<TypeDecl> list = (parent == null || state == null) ? getTypeDeclListNoTransform() : getTypeDeclList();
    list.addChild(node);
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:34
   */
  public void addTypeDeclNoTransform(TypeDecl node) {
    List<TypeDecl> list = getTypeDeclListNoTransform();
    list.addChild(node);
  }
  /**
   * Setter for child in list TypeDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:42
   */
  public void setTypeDecl(TypeDecl node, int i) {
    List<TypeDecl> list = getTypeDeclList();
    list.setChild(node, i);
  }
  /**
   * Getter for TypeDecl list.
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:50
   */
  public List<TypeDecl> getTypeDecls() {
    return getTypeDeclList();
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:56
   */
  public List<TypeDecl> getTypeDeclsNoTransform() {
    return getTypeDeclListNoTransform();
  }
  /**
   * Getter for list TypeDeclList
   * @apilevel high-level
   * @ast method 
   * @declaredat java.ast:63
   */
  @SuppressWarnings({"unchecked", "cast"})
  public List<TypeDecl> getTypeDeclList() {
    List<TypeDecl> list = (List<TypeDecl>)getChild(1);
    list.getNumChild();
    return list;
  }
  /**
   * @apilevel low-level
   * @ast method 
   * @declaredat java.ast:72
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:182
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
  @SuppressWarnings({"unchecked", "cast"})
  public String relativeName() {
      ASTNode$State state = state();
    String relativeName_value = relativeName_compute();
    return relativeName_value;
  }
  /**
   * @apilevel internal
   */
  private String relativeName_compute() {  return relativeName;  }
  /**
   * @attribute syn
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:28
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String pathName() {
      ASTNode$State state = state();
    String pathName_value = pathName_compute();
    return pathName_value;
  }
  /**
   * @apilevel internal
   */
  private String pathName_compute() {  return pathName;  }
  /**
   * @attribute syn
   * @aspect ClassPath
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/ClassPath.jrag:29
   */
  @SuppressWarnings({"unchecked", "cast"})
  public boolean fromSource() {
      ASTNode$State state = state();
    boolean fromSource_value = fromSource_compute();
    return fromSource_value;
  }
  /**
   * @apilevel internal
   */
  private boolean fromSource_compute() {  return fromSource;  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:211
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet localLookupType(String name) {
      ASTNode$State state = state();
    SimpleSet localLookupType_String_value = localLookupType_compute(name);
    return localLookupType_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet localLookupType_compute(String name) {
    for(int i = 0; i < getNumTypeDecl(); i++)
      if(getTypeDecl(i).name().equals(name))
        return SimpleSet.emptySet.add(getTypeDecl(i));
    return SimpleSet.emptySet;
  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:218
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet importedTypes(String name) {
      ASTNode$State state = state();
    SimpleSet importedTypes_String_value = importedTypes_compute(name);
    return importedTypes_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet importedTypes_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumImportDecl(); i++)
      if(!getImportDecl(i).isOnDemand())
        for(Iterator iter = getImportDecl(i).importedTypes(name).iterator(); iter.hasNext(); )
          set = set.add(iter.next());
    return set;
  }
  /**
   * @attribute syn
   * @aspect TypeScopePropagation
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:226
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet importedTypesOnDemand(String name) {
      ASTNode$State state = state();
    SimpleSet importedTypesOnDemand_String_value = importedTypesOnDemand_compute(name);
    return importedTypesOnDemand_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet importedTypesOnDemand_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumImportDecl(); i++)
      if(getImportDecl(i).isOnDemand())
        for(Iterator iter = getImportDecl(i).importedTypes(name).iterator(); iter.hasNext(); )
          set = set.add(iter.next());
    return set;
  }
  /**
   * @attribute syn
   * @aspect PrettyPrint
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/PrettyPrint.jadd:801
   */
  @SuppressWarnings({"unchecked", "cast"})
  public String dumpString() {
      ASTNode$State state = state();
    String dumpString_value = dumpString_compute();
    return dumpString_value;
  }
  /**
   * @apilevel internal
   */
  private String dumpString_compute() {  return getClass().getName() + " [" + getPackageDecl() + "]";  }
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
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet importedFields(String name) {
      ASTNode$State state = state();
    SimpleSet importedFields_String_value = importedFields_compute(name);
    return importedFields_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet importedFields_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumImportDecl(); i++)
      if(!getImportDecl(i).isOnDemand())
        for(Iterator iter = getImportDecl(i).importedFields(name).iterator(); iter.hasNext(); )
          set = set.add(iter.next());
    return set;
  }
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:120
   */
  @SuppressWarnings({"unchecked", "cast"})
  public SimpleSet importedFieldsOnDemand(String name) {
      ASTNode$State state = state();
    SimpleSet importedFieldsOnDemand_String_value = importedFieldsOnDemand_compute(name);
    return importedFieldsOnDemand_String_value;
  }
  /**
   * @apilevel internal
   */
  private SimpleSet importedFieldsOnDemand_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(int i = 0; i < getNumImportDecl(); i++)
      if(getImportDecl(i).isOnDemand())
        for(Iterator iter = getImportDecl(i).importedFields(name).iterator(); iter.hasNext(); )
          set = set.add(iter.next());
    return set;
  }
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:141
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection importedMethods(String name) {
      ASTNode$State state = state();
    Collection importedMethods_String_value = importedMethods_compute(name);
    return importedMethods_String_value;
  }
  /**
   * @apilevel internal
   */
  private Collection importedMethods_compute(String name) {
    Collection list = new ArrayList();
    for(int i = 0; i < getNumImportDecl(); i++)
      if(!getImportDecl(i).isOnDemand())
        list.addAll(getImportDecl(i).importedMethods(name));
    return list;
  }
  /**
   * @attribute syn
   * @aspect StaticImports
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:148
   */
  @SuppressWarnings({"unchecked", "cast"})
  public Collection importedMethodsOnDemand(String name) {
      ASTNode$State state = state();
    Collection importedMethodsOnDemand_String_value = importedMethodsOnDemand_compute(name);
    return importedMethodsOnDemand_String_value;
  }
  /**
   * @apilevel internal
   */
  private Collection importedMethodsOnDemand_compute(String name) {
    Collection list = new ArrayList();
    for(int i = 0; i < getNumImportDecl(); i++)
      if(getImportDecl(i).isOnDemand())
        list.addAll(getImportDecl(i).importedMethods(name));
    return list;
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
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:171
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
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return this;
    }
    return getParent().Define_CompilationUnit_compilationUnit(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/DefiniteAssignment.jrag:51
   * @apilevel internal
   */
  public boolean Define_boolean_isIncOrDec(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isIncOrDec(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:198
   * @apilevel internal
   */
  public boolean Define_boolean_handlesException(ASTNode caller, ASTNode child, TypeDecl exceptionType) {
    if(caller == getImportDeclListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    return !exceptionType.isUncheckedException();
  }
}
    if(caller == getTypeDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return !exceptionType.isUncheckedException();
    }
    return getParent().Define_boolean_handlesException(this, caller, exceptionType);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/LookupType.jrag:267
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupType(ASTNode caller, ASTNode child, String name) {
    if(caller == getImportDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return lookupType(name);
    }
    if(true) { 
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
    return getParent().Define_SimpleSet_lookupType(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/NameCheck.jrag:27
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_allImportedTypes(ASTNode caller, ASTNode child, String name) {
    if(caller == getImportDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return importedTypes(name);
    }
    return getParent().Define_SimpleSet_allImportedTypes(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/QualifiedNames.jrag:90
   * @apilevel internal
   */
  public String Define_String_packageName(ASTNode caller, ASTNode child) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return packageName();
    }
    return getParent().Define_String_packageName(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/SyntacticClassification.jrag:69
   * @apilevel internal
   */
  public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
    if(caller == getImportDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return NameType.PACKAGE_NAME;
    }
    return getParent().Define_NameType_nameType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:493
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_enclosingType(ASTNode caller, ASTNode child) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return null;
    }
    return getParent().Define_TypeDecl_enclosingType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:520
   * @apilevel internal
   */
  public boolean Define_boolean_isNestedType(ASTNode caller, ASTNode child) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
    return getParent().Define_boolean_isNestedType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:530
   * @apilevel internal
   */
  public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
    if(caller == getTypeDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return false;
    }
    return getParent().Define_boolean_isMemberType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:542
   * @apilevel internal
   */
  public boolean Define_boolean_isLocalClass(ASTNode caller, ASTNode child) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return false;
    }
    return getParent().Define_boolean_isLocalClass(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:564
   * @apilevel internal
   */
  public String Define_String_hostPackage(ASTNode caller, ASTNode child) {
    if(true) {
      int childIndex = this.getIndexOfChild(caller);
      return packageName();
    }
    return getParent().Define_String_hostPackage(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.4Frontend/TypeAnalysis.jrag:580
   * @apilevel internal
   */
  public TypeDecl Define_TypeDecl_hostType(ASTNode caller, ASTNode child) {
    if(caller == getImportDeclListNoTransform()) {
      int childIndex = caller.getIndexOfChild(child);
      return null;
    }
    return getParent().Define_TypeDecl_hostType(this, caller);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:104
   * @apilevel internal
   */
  public SimpleSet Define_SimpleSet_lookupVariable(ASTNode caller, ASTNode child, String name) {
    if(caller == getTypeDeclListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    SimpleSet set = importedFields(name);
    if(!set.isEmpty()) return set;
    set = importedFieldsOnDemand(name);
    if(!set.isEmpty()) return set;
    return lookupVariable(name);
  }
}
    return getParent().Define_SimpleSet_lookupVariable(this, caller, name);
  }
  /**
   * @declaredat /Users/eric/Documents/workspaces/clara-soot/JastAddJ/Java1.5Frontend/StaticImports.jrag:133
   * @apilevel internal
   */
  public Collection Define_Collection_lookupMethod(ASTNode caller, ASTNode child, String name) {
    if(caller == getTypeDeclListNoTransform()) { 
   int childIndex = caller.getIndexOfChild(child);
{
    Collection list = importedMethods(name);
    if(!list.isEmpty()) return list;
    list = importedMethodsOnDemand(name);
    if(!list.isEmpty()) return list;
    return lookupMethod(name);
  }
}
    return getParent().Define_Collection_lookupMethod(this, caller, name);
  }
  /**
   * @apilevel internal
   */
  public ASTNode rewriteTo() {
    return super.rewriteTo();
  }
}
