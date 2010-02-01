
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


// Explicitly bound access that bypasses name binding

public class BoundFieldAccess extends VarAccess implements Cloneable {
    public void flushCache() {
        super.flushCache();
        decl_computed = false;
        decl_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public BoundFieldAccess clone() throws CloneNotSupportedException {
        BoundFieldAccess node = (BoundFieldAccess)super.clone();
        node.decl_computed = false;
        node.decl_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public BoundFieldAccess copy() {
      try {
          BoundFieldAccess node = (BoundFieldAccess)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public BoundFieldAccess fullCopy() {
        BoundFieldAccess res = (BoundFieldAccess)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in BoundNames.jrag at line 68


  public BoundFieldAccess(FieldDeclaration f) {
    this(f.name(), f);
  }

    // Declared in BoundNames.jrag at line 73

  public boolean isExactVarAccess() {
    return false;
  }

    // Declared in BoundNames.ast at line 3
    // Declared in BoundNames.ast line 6

    public BoundFieldAccess() {
        super();


    }

    // Declared in BoundNames.ast at line 10


    // Declared in BoundNames.ast line 6
    public BoundFieldAccess(String p0, FieldDeclaration p1) {
        setID(p0);
        setFieldDeclaration(p1);
    }

    // Declared in BoundNames.ast at line 16


    // Declared in BoundNames.ast line 6
    public BoundFieldAccess(beaver.Symbol p0, FieldDeclaration p1) {
        setID(p0);
        setFieldDeclaration(p1);
    }

    // Declared in BoundNames.ast at line 21


  protected int numChildren() {
    return 0;
  }

    // Declared in BoundNames.ast at line 24

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 16
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
    // Declared in BoundNames.ast line 6
    protected FieldDeclaration tokenFieldDeclaration_FieldDeclaration;

    // Declared in BoundNames.ast at line 3

    public void setFieldDeclaration(FieldDeclaration value) {
        tokenFieldDeclaration_FieldDeclaration = value;
    }

    // Declared in BoundNames.ast at line 6

    public FieldDeclaration getFieldDeclaration() {
        return tokenFieldDeclaration_FieldDeclaration;
    }

    // Declared in BoundNames.jrag at line 72
 @SuppressWarnings({"unchecked", "cast"})     public Variable decl() {
        if(decl_computed) {
            return decl_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        decl_value = decl_compute();
        if(isFinal && num == state().boundariesCrossed)
            decl_computed = true;
        return decl_value;
    }

    private Variable decl_compute() {  return getFieldDeclaration();  }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
