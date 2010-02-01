
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class WildcardExtends extends AbstractWildcard implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardExtends clone() throws CloneNotSupportedException {
        WildcardExtends node = (WildcardExtends)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardExtends copy() {
      try {
          WildcardExtends node = (WildcardExtends)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public WildcardExtends fullCopy() {
        WildcardExtends res = (WildcardExtends)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in GenericsPrettyPrint.jrag at line 174

  public void toString(StringBuffer s) {
    s.append("? extends ");
    getAccess().toString(s);
  }

    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 19

    public WildcardExtends() {
        super();


    }

    // Declared in Generics.ast at line 10


    // Declared in Generics.ast line 19
    public WildcardExtends(Access p0) {
        setChild(p0, 0);
    }

    // Declared in Generics.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in Generics.ast at line 17

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Generics.ast at line 2
    // Declared in Generics.ast line 19
    public void setAccess(Access node) {
        setChild(node, 0);
    }

    // Declared in Generics.ast at line 5

    public Access getAccess() {
        return (Access)getChild(0);
    }

    // Declared in Generics.ast at line 9


    public Access getAccessNoTransform() {
        return (Access)getChildNoTransform(0);
    }

    // Declared in Generics.jrag at line 1125
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        if(type_computed) {
            return type_value;
        }
        ASTNode$State state = state();
        int num = state.boundariesCrossed;
        boolean isFinal = this.is$Final();
        type_value = type_compute();
        if(isFinal && num == state().boundariesCrossed)
            type_computed = true;
        return type_value;
    }

    private TypeDecl type_compute() {  return lookupWildcardExtends(getAccess().type());  }

    // Declared in Generics.jrag at line 1128
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl lookupWildcardExtends(TypeDecl typeDecl) {
        ASTNode$State state = state();
        TypeDecl lookupWildcardExtends_TypeDecl_value = getParent().Define_TypeDecl_lookupWildcardExtends(this, null, typeDecl);
        return lookupWildcardExtends_TypeDecl_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
