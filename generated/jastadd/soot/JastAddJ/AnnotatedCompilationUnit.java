
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


// 7.4.1.1 Package Annotations

public class AnnotatedCompilationUnit extends CompilationUnit implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AnnotatedCompilationUnit clone() throws CloneNotSupportedException {
        AnnotatedCompilationUnit node = (AnnotatedCompilationUnit)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AnnotatedCompilationUnit copy() {
      try {
          AnnotatedCompilationUnit node = (AnnotatedCompilationUnit)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public AnnotatedCompilationUnit fullCopy() {
        AnnotatedCompilationUnit res = (AnnotatedCompilationUnit)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Annotations.jrag at line 21

  // 7.4.1.1 Package Annotations

  /* Annotations may be used on package declarations, with the restriction that
  at most one annotated package declaration is permitted for a given package.
  The manner in which this restriction is enforced must, of necessity, vary
  from implementation to implementation. The following scheme is strongly
  recommended for file-system-based implementations: The sole annotated
  package declaration, if it exists, is placed in a source file called
  package-info.java in the directory containing the source files for the
  package. */
  public void nameCheck() {
    super.nameCheck();
    if(!relativeName().endsWith("package-info.java"))
      error("package annotations should be in a file package-info.java");
  }

    // Declared in Annotations.jrag at line 553


  public void toString(StringBuffer s) {
      getModifiers().toString(s);
      super.toString(s);
  }

    // Declared in AnnotationsCodegen.jrag at line 11

  public void jimplify1phase2() {
    super.jimplify1phase2();
    ArrayList c = new ArrayList();
    getModifiers().addAllAnnotations(c);
    for(Iterator iter = c.iterator(); iter.hasNext(); ) {
      soot.tagkit.Tag tag = (soot.tagkit.Tag)iter.next();
      //host.addTag(tag);
    }
  }

    // Declared in Annotations.ast at line 3
    // Declared in Annotations.ast line 16

    public AnnotatedCompilationUnit() {
        super();

        setChild(new List(), 0);
        setChild(new List(), 1);

    }

    // Declared in Annotations.ast at line 12


    // Declared in Annotations.ast line 16
    public AnnotatedCompilationUnit(java.lang.String p0, List<ImportDecl> p1, List<TypeDecl> p2, Modifiers p3) {
        setPackageDecl(p0);
        setChild(p1, 0);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in Annotations.ast at line 20


    // Declared in Annotations.ast line 16
    public AnnotatedCompilationUnit(beaver.Symbol p0, List<ImportDecl> p1, List<TypeDecl> p2, Modifiers p3) {
        setPackageDecl(p0);
        setChild(p1, 0);
        setChild(p2, 1);
        setChild(p3, 2);
    }

    // Declared in Annotations.ast at line 27


  protected int numChildren() {
    return 3;
  }

    // Declared in Annotations.ast at line 30

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 4
    public void setPackageDecl(java.lang.String value) {
        tokenjava_lang_String_PackageDecl = value;
    }

    // Declared in java.ast at line 5

    public void setPackageDecl(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setPackageDecl is only valid for String lexemes");
        tokenjava_lang_String_PackageDecl = (String)symbol.value;
        PackageDeclstart = symbol.getStart();
        PackageDeclend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public java.lang.String getPackageDecl() {
        return tokenjava_lang_String_PackageDecl != null ? tokenjava_lang_String_PackageDecl : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 4
    public void setImportDeclList(List<ImportDecl> list) {
        setChild(list, 0);
    }

    // Declared in java.ast at line 6


    public int getNumImportDecl() {
        return getImportDeclList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public ImportDecl getImportDecl(int i) {
        return (ImportDecl)getImportDeclList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addImportDecl(ImportDecl node) {
        List<ImportDecl> list = (parent == null || state == null) ? getImportDeclListNoTransform() : getImportDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addImportDeclNoTransform(ImportDecl node) {
        List<ImportDecl> list = getImportDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setImportDecl(ImportDecl node, int i) {
        List<ImportDecl> list = getImportDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<ImportDecl> getImportDecls() {
        return getImportDeclList();
    }

    // Declared in java.ast at line 31

    public List<ImportDecl> getImportDeclsNoTransform() {
        return getImportDeclListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<ImportDecl> getImportDeclList() {
        List<ImportDecl> list = (List<ImportDecl>)getChild(0);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<ImportDecl> getImportDeclListNoTransform() {
        return (List<ImportDecl>)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 4
    public void setTypeDeclList(List<TypeDecl> list) {
        setChild(list, 1);
    }

    // Declared in java.ast at line 6


    public int getNumTypeDecl() {
        return getTypeDeclList().getNumChild();
    }

    // Declared in java.ast at line 10


     @SuppressWarnings({"unchecked", "cast"})  public TypeDecl getTypeDecl(int i) {
        return (TypeDecl)getTypeDeclList().getChild(i);
    }

    // Declared in java.ast at line 14


    public void addTypeDecl(TypeDecl node) {
        List<TypeDecl> list = (parent == null || state == null) ? getTypeDeclListNoTransform() : getTypeDeclList();
        list.addChild(node);
    }

    // Declared in java.ast at line 19


    public void addTypeDeclNoTransform(TypeDecl node) {
        List<TypeDecl> list = getTypeDeclListNoTransform();
        list.addChild(node);
    }

    // Declared in java.ast at line 24


    public void setTypeDecl(TypeDecl node, int i) {
        List<TypeDecl> list = getTypeDeclList();
        list.setChild(node, i);
    }

    // Declared in java.ast at line 28

    public List<TypeDecl> getTypeDecls() {
        return getTypeDeclList();
    }

    // Declared in java.ast at line 31

    public List<TypeDecl> getTypeDeclsNoTransform() {
        return getTypeDeclListNoTransform();
    }

    // Declared in java.ast at line 35


     @SuppressWarnings({"unchecked", "cast"})  public List<TypeDecl> getTypeDeclList() {
        List<TypeDecl> list = (List<TypeDecl>)getChild(1);
        list.getNumChild();
        return list;
    }

    // Declared in java.ast at line 41


     @SuppressWarnings({"unchecked", "cast"})  public List<TypeDecl> getTypeDeclListNoTransform() {
        return (List<TypeDecl>)getChildNoTransform(1);
    }

    // Declared in Annotations.ast at line 2
    // Declared in Annotations.ast line 16
    public void setModifiers(Modifiers node) {
        setChild(node, 2);
    }

    // Declared in Annotations.ast at line 5

    public Modifiers getModifiers() {
        return (Modifiers)getChild(2);
    }

    // Declared in Annotations.ast at line 9


    public Modifiers getModifiersNoTransform() {
        return (Modifiers)getChildNoTransform(2);
    }

    // Declared in Annotations.jrag at line 71
    public boolean Define_boolean_mayUseAnnotationTarget(ASTNode caller, ASTNode child, String name) {
        if(caller == getModifiersNoTransform()) {
            return name.equals("PACKAGE");
        }
        return getParent().Define_boolean_mayUseAnnotationTarget(this, caller, name);
    }

    // Declared in Annotations.jrag at line 548
    public String Define_String_hostPackage(ASTNode caller, ASTNode child) {
        if(caller == getModifiersNoTransform()) {
            return packageName();
        }
        return super.Define_String_hostPackage(caller, child);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
