
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class PackageAccess extends Access implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public PackageAccess clone() throws CloneNotSupportedException {
        PackageAccess node = (PackageAccess)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public PackageAccess copy() {
      try {
          PackageAccess node = (PackageAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public PackageAccess fullCopy() {
        PackageAccess res = (PackageAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in NameCheck.jrag at line 45


  public void nameCheck() {
    if(!hasPackage(packageName())) {
      error(packageName() + " not found");
    }
  }

    // Declared in NodeConstructors.jrag at line 18


  public PackageAccess(String name, int start, int end) {
    this(name);
    this.start = start;
    this.end = end;
  }

    // Declared in PrettyPrint.jadd at line 509


  public void toString(StringBuffer s) {
    s.append(getPackage());
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 26

    public PackageAccess() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 26
    public PackageAccess(String p0) {
        setPackage(p0);
    }

    // Declared in java.ast at line 15


    // Declared in java.ast line 26
    public PackageAccess(beaver.Symbol p0) {
        setPackage(p0);
    }

    // Declared in java.ast at line 19


  protected int numChildren() {
    return 0;
  }

    // Declared in java.ast at line 22

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 26
    protected String tokenString_Package;

    // Declared in java.ast at line 3

    public void setPackage(String value) {
        tokenString_Package = value;
    }

    // Declared in java.ast at line 6

    public int Packagestart;

    // Declared in java.ast at line 7

    public int Packageend;

    // Declared in java.ast at line 8

    public void setPackage(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setPackage is only valid for String lexemes");
        tokenString_Package = (String)symbol.value;
        Packagestart = symbol.getStart();
        Packageend = symbol.getEnd();
    }

    // Declared in java.ast at line 15

    public String getPackage() {
        return tokenString_Package != null ? tokenString_Package : "";
    }

    // Declared in LookupType.jrag at line 84
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasQualifiedPackage(String packageName) {
        ASTNode$State state = state();
        boolean hasQualifiedPackage_String_value = hasQualifiedPackage_compute(packageName);
        return hasQualifiedPackage_String_value;
    }

    private boolean hasQualifiedPackage_compute(String packageName) {  return hasPackage(packageName() + "." + packageName);  }

    // Declared in LookupType.jrag at line 354
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet qualifiedLookupType(String name) {
        ASTNode$State state = state();
        SimpleSet qualifiedLookupType_String_value = qualifiedLookupType_compute(name);
        return qualifiedLookupType_String_value;
    }

    private SimpleSet qualifiedLookupType_compute(String name) {
    SimpleSet c = SimpleSet.emptySet;
    TypeDecl typeDecl = lookupType(packageName(), name);
    if(nextAccess() instanceof ClassInstanceExpr) {
      if(typeDecl != null && typeDecl.accessibleFrom(hostType()))
        c = c.add(typeDecl);
      return c;
    }
    else {
      if(typeDecl != null) {
        if(hostType() != null && typeDecl.accessibleFrom(hostType()))
          c = c.add(typeDecl);
        else if(hostType() == null && typeDecl.accessibleFromPackage(hostPackage()))
          c = c.add(typeDecl);
      }
      return c;
    }
  }

    // Declared in LookupVariable.jrag at line 151
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet qualifiedLookupVariable(String name) {
        ASTNode$State state = state();
        SimpleSet qualifiedLookupVariable_String_value = qualifiedLookupVariable_compute(name);
        return qualifiedLookupVariable_String_value;
    }

    private SimpleSet qualifiedLookupVariable_compute(String name) {  return SimpleSet.emptySet;  }

    // Declared in PrettyPrint.jadd at line 805
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        ASTNode$State state = state();
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName() + " [" + getPackage() + "]";  }

    // Declared in QualifiedNames.jrag at line 23
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        ASTNode$State state = state();
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return getPackage();  }

    // Declared in QualifiedNames.jrag at line 28
 @SuppressWarnings({"unchecked", "cast"})     public String packageName() {
        ASTNode$State state = state();
        String packageName_value = packageName_compute();
        return packageName_value;
    }

    private String packageName_compute() {
    StringBuffer s = new StringBuffer();
    if(hasPrevExpr()) {
      s.append(prevExpr().packageName());
      s.append(".");
    }
    s.append(getPackage());
    return s.toString();
  }

    // Declared in ResolveAmbiguousNames.jrag at line 39
 @SuppressWarnings({"unchecked", "cast"})     public boolean isPackageAccess() {
        ASTNode$State state = state();
        boolean isPackageAccess_value = isPackageAccess_compute();
        return isPackageAccess_value;
    }

    private boolean isPackageAccess_compute() {  return true;  }

    // Declared in SyntacticClassification.jrag at line 68
 @SuppressWarnings({"unchecked", "cast"})     public NameType predNameType() {
        ASTNode$State state = state();
        NameType predNameType_value = predNameType_compute();
        return predNameType_value;
    }

    private NameType predNameType_compute() {  return NameType.PACKAGE_NAME;  }

    // Declared in TypeHierarchyCheck.jrag at line 21
 @SuppressWarnings({"unchecked", "cast"})     public boolean isUnknown() {
        ASTNode$State state = state();
        boolean isUnknown_value = isUnknown_compute();
        return isUnknown_value;
    }

    private boolean isUnknown_compute() {  return !hasPackage(packageName());  }

    // Declared in NameCheck.jrag at line 238
 @SuppressWarnings({"unchecked", "cast"})     public boolean hasPackage(String packageName) {
        ASTNode$State state = state();
        boolean hasPackage_String_value = getParent().Define_boolean_hasPackage(this, null, packageName);
        return hasPackage_String_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
