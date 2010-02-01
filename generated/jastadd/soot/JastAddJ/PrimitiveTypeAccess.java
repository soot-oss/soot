
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class PrimitiveTypeAccess extends TypeAccess implements Cloneable {
    public void flushCache() {
        super.flushCache();
        decls_computed = false;
        decls_value = null;
        getPackage_computed = false;
        getPackage_value = null;
        getID_computed = false;
        getID_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public PrimitiveTypeAccess clone() throws CloneNotSupportedException {
        PrimitiveTypeAccess node = (PrimitiveTypeAccess)super.clone();
        node.decls_computed = false;
        node.decls_value = null;
        node.getPackage_computed = false;
        node.getPackage_value = null;
        node.getID_computed = false;
        node.getID_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public PrimitiveTypeAccess copy() {
      try {
          PrimitiveTypeAccess node = (PrimitiveTypeAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public PrimitiveTypeAccess fullCopy() {
        PrimitiveTypeAccess res = (PrimitiveTypeAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in java.ast at line 3
    // Declared in java.ast line 21

    public PrimitiveTypeAccess() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 21
    public PrimitiveTypeAccess(String p0) {
        setName(p0);
    }

    // Declared in java.ast at line 15


    // Declared in java.ast line 21
    public PrimitiveTypeAccess(beaver.Symbol p0) {
        setName(p0);
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
    // Declared in java.ast line 21
    protected String tokenString_Name;

    // Declared in java.ast at line 3

    public void setName(String value) {
        tokenString_Name = value;
    }

    // Declared in java.ast at line 6

    public int Namestart;

    // Declared in java.ast at line 7

    public int Nameend;

    // Declared in java.ast at line 8

    public void setName(beaver.Symbol symbol) {
        if(symbol.value != null && !(symbol.value instanceof String))
          throw new UnsupportedOperationException("setName is only valid for String lexemes");
        tokenString_Name = (String)symbol.value;
        Namestart = symbol.getStart();
        Nameend = symbol.getEnd();
    }

    // Declared in java.ast at line 15

    public String getName() {
        return tokenString_Name != null ? tokenString_Name : "";
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 21
    protected String tokenString_Package;

    // Declared in java.ast at line 3

    public void setPackage(String value) {
        tokenString_Package = value;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 21
    protected String tokenString_ID;

    // Declared in java.ast at line 3

    public void setID(String value) {
        tokenString_ID = value;
    }

    // Declared in LookupType.jrag at line 146
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

    private SimpleSet decls_compute() {  return lookupType(PRIMITIVE_PACKAGE_NAME, name());  }

    protected boolean getPackage_computed = false;
    protected String getPackage_value;
    // Declared in LookupType.jrag at line 147
 @SuppressWarnings({"unchecked", "cast"})     public String getPackage() {
        if(getPackage_computed) {
            return getPackage_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getPackage_value = getPackage_compute();
            setPackage(getPackage_value);
        if(isFinal && num == state().boundariesCrossed)
            getPackage_computed = true;
        return getPackage_value;
    }

    private String getPackage_compute() {  return PRIMITIVE_PACKAGE_NAME;  }

    protected boolean getID_computed = false;
    protected String getID_value;
    // Declared in LookupType.jrag at line 148
 @SuppressWarnings({"unchecked", "cast"})     public String getID() {
        if(getID_computed) {
            return getID_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        getID_value = getID_compute();
            setID(getID_value);
        if(isFinal && num == state().boundariesCrossed)
            getID_computed = true;
        return getID_value;
    }

    private String getID_compute() {  return getName();  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
