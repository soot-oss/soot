
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public class BoundTypeAccess extends TypeAccess implements Cloneable {
    public void flushCache() {
        super.flushCache();
        decls_computed = false;
        decls_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public BoundTypeAccess clone() throws CloneNotSupportedException {
        BoundTypeAccess node = (BoundTypeAccess)super.clone();
        node.decls_computed = false;
        node.decls_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public BoundTypeAccess copy() {
      try {
          BoundTypeAccess node = (BoundTypeAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public BoundTypeAccess fullCopy() {
        BoundTypeAccess res = (BoundTypeAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Generics.jrag at line 300


  public boolean isRaw() {
    return getTypeDecl().isRawType();
  }

    // Declared in BoundNames.ast at line 3
    // Declared in BoundNames.ast line 8

    public BoundTypeAccess() {
        super();


    }

    // Declared in BoundNames.ast at line 10


    // Declared in BoundNames.ast line 8
    public BoundTypeAccess(String p0, String p1, TypeDecl p2) {
        setPackage(p0);
        setID(p1);
        setTypeDecl(p2);
    }

    // Declared in BoundNames.ast at line 17


    // Declared in BoundNames.ast line 8
    public BoundTypeAccess(beaver.Symbol p0, beaver.Symbol p1, TypeDecl p2) {
        setPackage(p0);
        setID(p1);
        setTypeDecl(p2);
    }

    // Declared in BoundNames.ast at line 23


  protected int numChildren() {
    return 0;
  }

    // Declared in BoundNames.ast at line 26

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 20
    public void setPackage(String value) {
        tokenString_Package = value;
    }

    // Declared in java.ast at line 5

    public void setPackage(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setPackage is only valid for String lexemes");
        tokenString_Package = (String)symbol.value;
        Packagestart = symbol.getStart();
        Packageend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getPackage() {
        return tokenString_Package != null ? tokenString_Package : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 20
    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in java.ast at line 5

    public void setID(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setID is only valid for String lexemes");
        tokenString_ID = (String)symbol.value;
        IDstart = symbol.getStart();
        IDend = symbol.getEnd();
    }

    // Declared in java.ast at line 12

    public String getID() {
        return tokenString_ID != null ? tokenString_ID : "";
    }

    // Declared in BoundNames.ast at line 2
    // Declared in BoundNames.ast line 8
    protected TypeDecl tokenTypeDecl_TypeDecl;

    // Declared in BoundNames.ast at line 3

    public void setTypeDecl(TypeDecl value) {
        tokenTypeDecl_TypeDecl = value;
    }

    // Declared in BoundNames.ast at line 6

    public TypeDecl getTypeDecl() {
        return tokenTypeDecl_TypeDecl;
    }

    // Declared in BoundNames.jrag at line 93
 @SuppressWarnings({"unchecked", "cast"})     public SimpleSet decls() {
        if(decls_computed) {
            return decls_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        decls_value = decls_compute();
        if(isFinal && num == state().boundariesCrossed)
            decls_computed = true;
        return decls_value;
    }

    private SimpleSet decls_compute() {  return SimpleSet.emptySet.add(getTypeDecl());  }

    // Declared in PrettyPrint.jadd at line 817
 @SuppressWarnings({"unchecked", "cast"})     public String dumpString() {
        ASTNode$State state = state();
        String dumpString_value = dumpString_compute();
        return dumpString_value;
    }

    private String dumpString_compute() {  return getClass().getName() + " [" + getTypeDecl().fullName() + "]";  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
