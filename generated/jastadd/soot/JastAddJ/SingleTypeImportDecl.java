
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class SingleTypeImportDecl extends ImportDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
        importedTypes_String_values = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public SingleTypeImportDecl clone() throws CloneNotSupportedException {
        SingleTypeImportDecl node = (SingleTypeImportDecl)super.clone();
        node.importedTypes_String_values = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SingleTypeImportDecl copy() {
      try {
          SingleTypeImportDecl node = (SingleTypeImportDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SingleTypeImportDecl fullCopy() {
        SingleTypeImportDecl res = (SingleTypeImportDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in NameCheck.jrag at line 20


  public void nameCheck() {
    if(!getAccess().type().typeName().equals(typeName()) && !getAccess().type().isUnknown())
      error("Single-type import " + typeName() + " is not the canonical name of type " + getAccess().type().typeName());
    else if(allImportedTypes(getAccess().type().name()).size() > 1)
      error(getAccess().type().name() + " is imported multiple times");
  }

    // Declared in PrettyPrint.jadd at line 50


  public void toString(StringBuffer s) {
    s.append("import ");
    getAccess().toString(s);
    s.append(";\n");
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 8

    public SingleTypeImportDecl() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 8
    public SingleTypeImportDecl(Access p0) {
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

    // Declared in LookupType.jrag at line 235
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
    if(getAccess().type().name().equals(name))
      set = set.add(getAccess().type());
    return set;
  }

    // Declared in NameCheck.jrag at line 26
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet allImportedTypes(String name) {
        ASTNode$State state = state();
        SimpleSet allImportedTypes_String_value = getParent().Define_SimpleSet_allImportedTypes(this, null, name);
        return allImportedTypes_String_value;
    }

    // Declared in SyntacticClassification.jrag at line 72
    public NameType Define_NameType_nameType(ASTNode caller, ASTNode child) {
        if(caller == getAccessNoTransform()) {
            return NameType.TYPE_NAME;
        }
        return getParent().Define_NameType_nameType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
