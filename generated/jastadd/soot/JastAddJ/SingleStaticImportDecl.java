
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class SingleStaticImportDecl extends StaticImportDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public SingleStaticImportDecl clone() throws CloneNotSupportedException {
        SingleStaticImportDecl node = (SingleStaticImportDecl)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SingleStaticImportDecl copy() {
      try {
          SingleStaticImportDecl node = (SingleStaticImportDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public SingleStaticImportDecl fullCopy() {
        SingleStaticImportDecl res = (SingleStaticImportDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in StaticImports.jrag at line 61


  /* The TypeName must be the canonical name of a class or interface type*/
  public void typeCheck() { 
    if(!getAccess().type().typeName().equals(typeName()) && !getAccess().type().isUnknown())
      error("Single-type import " + typeName() + " is not the canonical name of type " + getAccess().type().typeName());
  }

    // Declared in StaticImports.jrag at line 93


  /* 7.5.3 A compile-time error occurs if the named type does not exist. The named type must
  be accessible (\ufffd6.6) or a compile-time error occurs.
  Comment: Taken care of by name and type analysis */

  /* 7.5.4 It is a compile-time error for a static-import-on-demand declaration to name a
  type that does not exist or a type that is not accessible. Two or more
  static-import-on-demand declarations in the same compilation unit may name the
  same type or package; the effect is as if there was exactly one such
  declaration. Two or more static-import-on-demand declarations in the same
  compilation unit may name the same member; the effect is as if the member was
  imported exactly once.

  Note that it is permissable for one static-import-on-demand declaration to
  import several fields or types with the same name, or several methods with the
  same name and signature.

  If a compilation unit contains both a static-import-on-demand declaration and a
  type-import-on-demand (\ufffd7.5.2) declaration that name the same type, the effect
  is as if the static member types of that type were imported only once.

  A static-import-on-demand declaration never causes any other declaration to be shadowed.

  Comment: Taken care of by the name and type analysis operating on sets */
  
  /* 7.5.3 The Identifier must name at least one static member of the named type; a 
  compile-time error occurs if there is no member of that name or if all of the named
  members are not accessible.*/
  public void nameCheck() {
    if(importedFields(name()).isEmpty() && importedMethods(name()).isEmpty() && importedTypes(name()).isEmpty() &&
       !getAccess().type().isUnknown()) {
      error("Semantic Error: At least one static member named " + name() + " must be available in static imported type " + type().fullName());
    }
  }

    // Declared in StaticImports.jrag at line 207


  // PrettyPrinting
  public void toString(StringBuffer s) {
    s.append("import static ");
    getAccess().toString(s);
    s.append("." + getID());
    s.append(";\n");
  }

    // Declared in StaticImports.ast at line 3
    // Declared in StaticImports.ast line 3

    public SingleStaticImportDecl() {
        super();


    }

    // Declared in StaticImports.ast at line 10


    // Declared in StaticImports.ast line 3
    public SingleStaticImportDecl(Access p0, String p1) {
        setChild(p0, 0);
        setID(p1);
    }

    // Declared in StaticImports.ast at line 16


    // Declared in StaticImports.ast line 3
    public SingleStaticImportDecl(Access p0, beaver.Symbol p1) {
        setChild(p0, 0);
        setID(p1);
    }

    // Declared in StaticImports.ast at line 21


  protected int numChildren() {
    return 1;
  }

    // Declared in StaticImports.ast at line 24

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

    // Declared in StaticImports.ast at line 2
    // Declared in StaticImports.ast line 3
    protected String tokenString_ID;

    // Declared in StaticImports.ast at line 3

    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in StaticImports.ast at line 6

    public int IDstart;

    // Declared in StaticImports.ast at line 7

    public int IDend;

    // Declared in StaticImports.ast at line 8

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in StaticImports.ast at line 15

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in StaticImports.jrag at line 54
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        ASTNode$State state = state();
        TypeDecl type_value = type_compute();
        return type_value;
    }

    private TypeDecl type_compute() {  return getAccess().type();  }

    // Declared in StaticImports.jrag at line 99
 @SuppressWarnings({"unchecked", "cast"})     public String name() {
        ASTNode$State state = state();
        String name_value = name_compute();
        return name_value;
    }

    private String name_compute() {  return getID();  }

    // Declared in StaticImports.jrag at line 203
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
