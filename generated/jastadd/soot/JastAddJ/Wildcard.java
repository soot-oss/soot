
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class Wildcard extends AbstractWildcard implements Cloneable {
    public void flushCache() {
        super.flushCache();
        type_computed = false;
        type_value = null;
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public Wildcard clone() throws CloneNotSupportedException {
        Wildcard node = (Wildcard)super.clone();
        node.type_computed = false;
        node.type_value = null;
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Wildcard copy() {
      try {
          Wildcard node = (Wildcard)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public Wildcard fullCopy() {
        Wildcard res = (Wildcard)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in GenericsPrettyPrint.jrag at line 171


	public void toString(StringBuffer s) {
    s.append("?");
  }

    // Declared in Generics.ast at line 3
    // Declared in Generics.ast line 18

    public Wildcard() {
        super();


    }

    // Declared in Generics.ast at line 9


  protected int numChildren() {
    return 0;
  }

    // Declared in Generics.ast at line 12

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in Generics.jrag at line 1124
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

    private TypeDecl type_compute() {  return typeWildcard();  }

    // Declared in Generics.jrag at line 1129
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeWildcard() {
        ASTNode$State state = state();
        TypeDecl typeWildcard_value = getParent().Define_TypeDecl_typeWildcard(this, null);
        return typeWildcard_value;
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
