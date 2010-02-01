
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class MemberClassDecl extends MemberTypeDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public MemberClassDecl clone() throws CloneNotSupportedException {
        MemberClassDecl node = (MemberClassDecl)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public MemberClassDecl copy() {
      try {
          MemberClassDecl node = (MemberClassDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public MemberClassDecl fullCopy() {
        MemberClassDecl res = (MemberClassDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in PrettyPrint.jadd at line 205


  public void toString(StringBuffer s) {
    s.append(indent());
    getClassDecl().toString(s);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 92

    public MemberClassDecl() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 92
    public MemberClassDecl(ClassDecl p0) {
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
    // Declared in java.ast line 92
    public void setClassDecl(ClassDecl node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public ClassDecl getClassDecl() {
        return (ClassDecl)getChild(0);
    }

    // Declared in java.ast at line 9


    public ClassDecl getClassDeclNoTransform() {
        return (ClassDecl)getChildNoTransform(0);
    }

    // Declared in LookupType.jrag at line 397
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeDecl() {
        ASTNode$State state = state();
        TypeDecl typeDecl_value = typeDecl_compute();
        return typeDecl_value;
    }

    private TypeDecl typeDecl_compute() {  return getClassDecl();  }

    // Declared in TypeAnalysis.jrag at line 528
    public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
        if(caller == getClassDeclNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isMemberType(this, caller);
    }

    // Declared in TypeHierarchyCheck.jrag at line 145
    public boolean Define_boolean_inStaticContext(ASTNode caller, ASTNode child) {
        if(caller == getClassDeclNoTransform()) {
            return false;
        }
        return getParent().Define_boolean_inStaticContext(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
