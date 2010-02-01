
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class MemberInterfaceDecl extends MemberTypeDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public MemberInterfaceDecl clone() throws CloneNotSupportedException {
        MemberInterfaceDecl node = (MemberInterfaceDecl)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public MemberInterfaceDecl copy() {
      try {
          MemberInterfaceDecl node = (MemberInterfaceDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public MemberInterfaceDecl fullCopy() {
        MemberInterfaceDecl res = (MemberInterfaceDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in Modifiers.jrag at line 184

 
  // 8.1.2
  public void checkModifiers() {
    super.checkModifiers();
    if(hostType().isInnerClass())
      error("*** Inner classes may not declare member interfaces");
  }

    // Declared in PrettyPrint.jadd at line 210


  public void toString(StringBuffer s) {
    s.append(indent());
    getInterfaceDecl().toString(s);
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 93

    public MemberInterfaceDecl() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 93
    public MemberInterfaceDecl(InterfaceDecl p0) {
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
    // Declared in java.ast line 93
    public void setInterfaceDecl(InterfaceDecl node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public InterfaceDecl getInterfaceDecl() {
        return (InterfaceDecl)getChild(0);
    }

    // Declared in java.ast at line 9


    public InterfaceDecl getInterfaceDeclNoTransform() {
        return (InterfaceDecl)getChildNoTransform(0);
    }

    // Declared in LookupType.jrag at line 398
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl typeDecl() {
        ASTNode$State state = state();
        TypeDecl typeDecl_value = typeDecl_compute();
        return typeDecl_value;
    }

    private TypeDecl typeDecl_compute() {  return getInterfaceDecl();  }

    // Declared in TypeAnalysis.jrag at line 529
    public boolean Define_boolean_isMemberType(ASTNode caller, ASTNode child) {
        if(caller == getInterfaceDeclNoTransform()) {
            return true;
        }
        return getParent().Define_boolean_isMemberType(this, caller);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
