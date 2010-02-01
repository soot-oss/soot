
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class TypeImportOnDemandDecl extends ImportDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        importedTypes_String_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public TypeImportOnDemandDecl clone() throws CloneNotSupportedException {
        TypeImportOnDemandDecl node = (TypeImportOnDemandDecl)super.clone();
        node.importedTypes_String_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public TypeImportOnDemandDecl copy() {
      try {
          TypeImportOnDemandDecl node = (TypeImportOnDemandDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public TypeImportOnDemandDecl fullCopy() {
        TypeImportOnDemandDecl res = (TypeImportOnDemandDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in NameCheck.jrag at line 30


  public void nameCheck() {
    if(getAccess().lastAccess().isTypeAccess() && !getAccess().type().typeName().equals(typeName()))
      error("On demand type import " + typeName() + ".* is not the canonical name of type " + getAccess().type().typeName());
  }

    // Declared in PrettyPrint.jadd at line 56


  public void toString(StringBuffer s) {
    s.append("import ");
    getAccess().toString(s);
    s.append(".*;\n");
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 9

    public TypeImportOnDemandDecl() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 9
    public TypeImportOnDemandDecl(Access p0) {
        setChild(p0, 0);
    }

    // Declared in java.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in java.ast at line 17

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 7
    public void setAccess(Access node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Access getAccess() {
        return (Access)getChild(0);
    }

    // Declared in java.ast at line 9


    public Access getAccessNoTransform() {
        return (Access)getChildNoTransform(0);
    }

    // Declared in LookupType.jrag at line 241
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet importedTypes(String name) {
        Object _parameters = name;
if(importedTypes_String_values == null) importedTypes_String_values = new java.util.HashMap(4);
        if(importedTypes_String_values.containsKey(_parameters)) {
            return (SimpleSet)importedTypes_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet importedTypes_String_value = importedTypes_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            importedTypes_String_values.put(_parameters, importedTypes_String_value);
        return importedTypes_String_value;
    }

    private SimpleSet importedTypes_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    if(getAccess() instanceof PackageAccess) {
      String packageName = ((PackageAccess)getAccess()).getPackage();
      TypeDecl typeDecl = lookupType(packageName, name);
      if(typeDecl != null && typeDecl.accessibleFromPackage(packageName()) &&
         typeDecl.typeName().equals(packageName + "." + name)) // canonical names match
        set = set.add(typeDecl);
    }
    else {
      for(Iterator iter = getAccess().type().memberTypes(name).iterator(); iter.hasNext(); ) {
        TypeDecl decl = (TypeDecl)iter.next();
        if(decl.accessibleFromPackage(packageName()) &&
           decl.typeName().equals(getAccess().typeName() + "." + name)) // canonical names match
          set = set.add(decl);
      }
    }
    return set;
  }

    // Declared in LookupType.jrag at line 264
 @SuppressWarnings({"unchecked", "cast"})     public boolean isOnDemand() {
        ASTNode$State state = state();
        boolean isOnDemand_value = isOnDemand_compute();
        return isOnDemand_value;
    }

    private boolean isOnDemand_compute() {  return true;  }

    // Declared in LookupType.jrag at line 260
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupType(String packageName, String typeName) {
        ASTNode$State state = state();
        TypeDecl lookupType_String_String_value = getParent().Define_TypeDecl_lookupType(this, null, packageName, typeName);
        return lookupType_String_String_value;
    }

    // Declared in SyntacticClassification.jrag at line 107
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getAccessNoTransform()) {
            return NameType.PACKAGE_OR_TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
