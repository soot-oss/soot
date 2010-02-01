
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;
// 7.5 Import Declarations

public abstract class StaticImportDecl extends ImportDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        importedTypes_String_values = null;
        importedFields_String_values = null;
        importedMethods_String_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public StaticImportDecl clone() throws CloneNotSupportedException {
        StaticImportDecl node = (StaticImportDecl)super.clone();
        node.importedTypes_String_values = null;
        node.importedFields_String_values = null;
        node.importedMethods_String_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in StaticImports.ast at line 3
    // Declared in StaticImports.ast line 2

    public StaticImportDecl() {
        super();


    }

    // Declared in StaticImports.ast at line 10


    // Declared in StaticImports.ast line 2
    public StaticImportDecl(Access p0) {
        setChild(p0, 0);
    }

    // Declared in StaticImports.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in StaticImports.ast at line 17

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

    // Declared in StaticImports.jrag at line 53
 @SuppressWarnings({"unchecked", "cast"})     public abstract TypeDecl type();
    // Declared in StaticImports.jrag at line 21
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
    for(Iterator iter = type().memberTypes(name).iterator(); iter.hasNext(); ) {
      TypeDecl decl = (TypeDecl)iter.next();
      if(decl.isStatic() && decl.accessibleFromPackage(packageName()))
        set = set.add(decl);
    }
    return set;
  }

    // Declared in StaticImports.jrag at line 31
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet importedFields(String name) {
        Object _parameters = name;
if(importedFields_String_values == null) importedFields_String_values = new java.util.HashMap(4);
        if(importedFields_String_values.containsKey(_parameters)) {
            return (SimpleSet)importedFields_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        SimpleSet importedFields_String_value = importedFields_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            importedFields_String_values.put(_parameters, importedFields_String_value);
        return importedFields_String_value;
    }

    private SimpleSet importedFields_compute(String name) {
    SimpleSet set = SimpleSet.emptySet;
    for(Iterator iter = type().memberFields(name).iterator(); iter.hasNext(); ) {
      FieldDeclaration decl = (FieldDeclaration)iter.next();
      if(decl.isStatic() &&
         (decl.isPublic() || (!decl.isPrivate() && decl.hostType().topLevelType().packageName().equals(packageName()))))
        set = set.add(decl);
    }
    return set;
  }

    // Declared in StaticImports.jrag at line 42
 @SuppressWarnings({"unchecked", "cast"})     public Collection importedMethods(String name) {
        Object _parameters = name;
if(importedMethods_String_values == null) importedMethods_String_values = new java.util.HashMap(4);
        if(importedMethods_String_values.containsKey(_parameters)) {
            return (Collection)importedMethods_String_values.get(_parameters);
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        Collection importedMethods_String_value = importedMethods_compute(name);
        if(isFinal && num == state().boundariesCrossed)
            importedMethods_String_values.put(_parameters, importedMethods_String_value);
        return importedMethods_String_value;
    }

    private Collection importedMethods_compute(String name) {
    Collection set = new HashSet();
    for(Iterator iter = type().memberMethods(name).iterator(); iter.hasNext(); ) {
      MethodDecl decl = (MethodDecl)iter.next();
      if(decl.isStatic() &&
         (decl.isPublic() || (!decl.isPrivate() && decl.hostType().topLevelType().packageName().equals(packageName()))))
        set.add(decl);
    }
    return set;
  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
