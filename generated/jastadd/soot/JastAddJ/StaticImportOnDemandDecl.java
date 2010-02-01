
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;


public class StaticImportOnDemandDecl extends StaticImportDecl implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public StaticImportOnDemandDecl clone() throws CloneNotSupportedException {
        StaticImportOnDemandDecl node = (StaticImportOnDemandDecl)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
     @SuppressWarnings({"unchecked", "cast"})  public StaticImportOnDemandDecl copy() {
      try {
          StaticImportOnDemandDecl node = (StaticImportOnDemandDecl)clone();
          if(children != null) node.children = (ASTNode[])children.clone();
          return node;
      } catch (CloneNotSupportedException e) {
      }
      System.err.println("Error: Could not clone node of type " + getClass().getName() + "!");
      return null;
    }
     @SuppressWarnings({"unchecked", "cast"})  public StaticImportOnDemandDecl fullCopy() {
        StaticImportOnDemandDecl res = (StaticImportOnDemandDecl)copy();
        for(int i = 0; i < getNumChildNoTransform(); i++) {
          ASTNode node = getChildNoTransform(i);
          if(node != null) node = node.fullCopy();
          res.setChild(node, i);
        }
        return res;
    }
    // Declared in StaticImports.jrag at line 213

  public void toString(StringBuffer s) {
    s.append("import static ");
    getAccess().toString(s);
    s.append(".*;\n");
  }

    // Declared in StaticImports.ast at line 3
    // Declared in StaticImports.ast line 4

    public StaticImportOnDemandDecl() {
        super();


    }

    // Declared in StaticImports.ast at line 10


    // Declared in StaticImports.ast line 4
    public StaticImportOnDemandDecl(Access p0) {
        setChild(p0, 0);
    }

    // Declared in StaticImports.ast at line 14


  protected int numChildren() {
    return 1;
  }

    // Declared in StaticImports.ast at line 17

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

    // Declared in StaticImports.jrag at line 55
 @SuppressWarnings({"unchecked", "cast"})     public TypeDecl type() {
        ASTNode$State state = state();
        TypeDecl type_value = type_compute();
        return type_value;
    }

    private TypeDecl type_compute() {  return getAccess().type();  }

    // Declared in StaticImports.jrag at line 58
 @SuppressWarnings({"unchecked", "cast"})     public boolean isOnDemand() {
        ASTNode$State state = state();
        boolean isOnDemand_value = isOnDemand_compute();
        return isOnDemand_value;
    }

    private boolean isOnDemand_compute() {  return true;  }

    // Declared in StaticImports.jrag at line 204
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
