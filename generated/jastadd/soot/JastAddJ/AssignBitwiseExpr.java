
package soot.JastAddJ;
import java.util.HashSet;import java.util.LinkedHashSet;import java.io.File;import java.util.*;import beaver.*;import java.util.ArrayList;import java.util.zip.*;import java.io.*;import java.io.FileNotFoundException;import java.util.Collection;import soot.*;import soot.util.*;import soot.jimple.*;import soot.coffi.ClassFile;import soot.coffi.method_info;import soot.coffi.CONSTANT_Utf8_info;import soot.tagkit.SourceFileTag;import soot.coffi.CoffiMethodSource;



public abstract class AssignBitwiseExpr extends AssignExpr implements Cloneable {
    public void flushCache() {
        super.flushCache();
    }
    public void flushCollectionCache() {
        super.flushCollectionCache();
    }
     @SuppressWarnings({"unchecked", "cast"})  public AssignBitwiseExpr clone() throws CloneNotSupportedException {
        AssignBitwiseExpr node = (AssignBitwiseExpr)super.clone();
        node.in$Circle(false);
        node.is$Final(false);
        return node;
    }
    // Declared in TypeCheck.jrag at line 98

  
  public void typeCheck() {
    TypeDecl source = sourceType();
    TypeDecl dest = getDest().type();
    if(source.isIntegralType() && dest.isIntegralType())
      super.typeCheck();
    else if(source.isBoolean() && dest.isBoolean())
      super.typeCheck();
    else
      error("Operator only operates on integral and boolean types");
  }

    // Declared in java.ast at line 3
    // Declared in java.ast line 117

    public AssignBitwiseExpr() {
        super();


    }

    // Declared in java.ast at line 10


    // Declared in java.ast line 117
    public AssignBitwiseExpr(Expr p0, Expr p1) {
        setChild(p0, 0);
        setChild(p1, 1);
    }

    // Declared in java.ast at line 15


  protected int numChildren() {
    return 2;
  }

    // Declared in java.ast at line 18

    public boolean mayHaveRewrite() {
        return false;
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 99
    public void setDest(Expr node) {
        setChild(node, 0);
    }

    // Declared in java.ast at line 5

    public Expr getDest() {
        return (Expr)getChild(0);
    }

    // Declared in java.ast at line 9


    public Expr getDestNoTransform() {
        return (Expr)getChildNoTransform(0);
    }

    // Declared in java.ast at line 2
    // Declared in java.ast line 99
    public void setSource(Expr node) {
        setChild(node, 1);
    }

    // Declared in java.ast at line 5

    public Expr getSource() {
        return (Expr)getChild(1);
    }

    // Declared in java.ast at line 9


    public Expr getSourceNoTransform() {
        return (Expr)getChildNoTransform(1);
    }

public ASTNode rewriteTo() {
    return super.rewriteTo();
}

}
