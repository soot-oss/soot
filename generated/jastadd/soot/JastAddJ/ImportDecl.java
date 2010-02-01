
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


// 7.5 Import Declarations

public abstract class ImportDecl extends ASTNode<ASTNode> implements Cloneable {
    public void flushCache() {
        super.flushCache();
        importedTypes_String_values = null;
        importedFields_String_values = null;
        importedMethods_String_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public ImportDecl clone() throws CloneNotSupportedException {
        ImportDecl node = (ImportDecl)super.clone();
        node.importedTypes_String_values = null;
        node.importedFields_String_values = null;
        node.importedMethods_String_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in java.ast at line 3
    // Declared in java.ast line 7

    public ImportDecl() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 7
    public ImportDecl(Access p0) {
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

    protected java.util.Map importedTypes_String_values;
    // Declared in LookupType.jrag at line 234
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

    private SimpleSet importedTypes_compute(String name) {  return SimpleSet.emptySet;  }

    // Declared in LookupType.jrag at line 263
 @SuppressWarnings({"unchecked", "cast"})     public boolean isOnDemand() {
        ASTNode$State state = state();
        boolean isOnDemand_value = isOnDemand_compute();
        return isOnDemand_value;
    }

    private boolean isOnDemand_compute() {  return false;  }

    // Declared in QualifiedNames.jrag at line 51
 @SuppressWarnings({"unchecked", "cast"})     public String typeName() {
        ASTNode$State state = state();
        String typeName_value = typeName_compute();
        return typeName_value;
    }

    private String typeName_compute() {
    Access a = getAccess().lastAccess();
    String name = a.isTypeAccess() ? ((TypeAccess)a).nameWithPackage() : "";
    while(a.hasPrevExpr() && a.prevExpr() instanceof Access) {
      Access pred = (Access)a.prevExpr();
      if(pred.isTypeAccess())
        name = ((TypeAccess)pred).nameWithPackage() + "." + name;
      a = pred;
    }
    return name;
  }

    protected java.util.Map importedFields_String_values;
    // Declared in StaticImports.jrag at line 30
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

    private SimpleSet importedFields_compute(String name) {  return SimpleSet.emptySet;  }

    protected java.util.Map importedMethods_String_values;
    // Declared in StaticImports.jrag at line 41
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

    private Collection importedMethods_compute(String name) {  return Collections.EMPTY_LIST;  }

    // Declared in LookupType.jrag at line 261
 @SuppressWarnings({"unchecked", "cast"})     public String packageName() {
        ASTNode$State state = state();
        String packageName_value = getParent().Define_String_packageName(this, null);
        return packageName_value;
    }

    // Declared in DefiniteAssignment.jrag at line 23
    public boolean Define_boolean_isDest(ASTNode caller, ASTNode child) {
        if(caller == getAccessNoTransform()) {
            return false;
        }
        return getParent().Define_boolean_isDest(this, caller);
    }

    // Declared in DefiniteAssignment.jrag at line 32
    public boolean Define_boolean_isSource(ASTNode caller, ASTNode child) {
        if(caller == getAccessNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isSource(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
